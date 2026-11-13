package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

public class ActionLeaveRequestSearch extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeaveRequestSearch.class);

    ActionResult<List<Wo>> execute(Integer page, Integer size, JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String f : wi.getFilterList()) {
                    AttendanceV2Helper.analysisFilterToPersonList(userList, f, business, wi.getRecursive());
                }
            }
            if (userList.isEmpty()) {
                logger.warn("사용자 정보를 찾을 수 없습니다. 조회 조건: {0}", wi.getFilterList());
                result.setData(new ArrayList<>());
                return result;
            }
            Date startDate = null;
            if (wi.getStartDate() != null && !wi.getStartDate().isEmpty()) {
                startDate = DateTools.parse(wi.getStartDate(), DateTools.format_yyyyMMdd); // 형식 검사
            }
            Date endDate = null;
            if (wi.getEndDate() != null && !wi.getEndDate().isEmpty()) {
                endDate = DateTools.parse(wi.getEndDate(), DateTools.format_yyyyMMdd); // 형식 검사
            }
            if (startDate != null && endDate != null && startDate.after(endDate)) {
                throw new ExceptionDateEndBeforeStartError();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            List<AttendanceV2LeaveRequest> list = findRequestWithPersonAndTime(userList, startDate, endDate, emc, adjustPage, adjustPageSize);
            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            List<Wo> wos = list.stream().map(request -> {
                Wo wo = Wo.copier.copy(request);
                // 휴가 유형 설정
                for (AttendanceV2LeaveType type : leaveTypeList) {
                    if (type.getId().equals(request.getLeaveTypeId())) {
                        wo.setLeaveType(type);
                        break;
                    }
                }
                return wo;
            }).collect(Collectors.toList());
            result.setData(wos);
            result.setCount(countRequestWithPersonAndTime(userList, startDate, endDate, emc));
            return result;
        }
    }

    // 사용자·시간으로 휴가 신청 페이지 조회
    private List<AttendanceV2LeaveRequest> findRequestWithPersonAndTime(List<String> userList, Date startDate,
            Date endDate, EntityManagerContainer emc, Integer adjustPage, Integer adjustPageSize) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveRequest> cq = cb.createQuery(AttendanceV2LeaveRequest.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = root.get(AttendanceV2LeaveRequest_.person).in(userList);
        if (endDate != null) {
           p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        }
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveRequest_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize).setMaxResults(adjustPageSize).getResultList();
    }
    // 사용자·시간으로 휴가 신청 수 조회
    private Long countRequestWithPersonAndTime(List<String> userList, Date startDate,
            Date endDate, EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveRequest.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveRequest> root = cq.from(AttendanceV2LeaveRequest.class);
        Predicate p = root.get(AttendanceV2LeaveRequest_.person).in(userList);
        if (endDate != null) {
            p = cb.and(p, cb.lessThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.startTime), endDate));
        }
        if (startDate != null) {
            p = cb.and(p, cb.greaterThanOrEqualTo(root.get(AttendanceV2LeaveRequest_.endTime), startDate));
        }
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    public static class Wi extends GsonPropertyObject {
        private static final long serialVersionUID = 8433642169523374771L;

        @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("필터 조직의 하위 조직 사용자 재귀 조회 여부, 기본 true, false면 현재 조직 직속 사용자만 조회")
        private Boolean recursive;

        @FieldDescribe("시작 날짜")
        private String startDate;

        @FieldDescribe("종료 날짜")
        private String endDate;

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }

        public Boolean getRecursive() {
            return recursive;
        }

        public void setRecursive(Boolean recursive) {
            this.recursive = recursive;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

    }

    public static class Wo extends AttendanceV2LeaveRequest {

        private static final long serialVersionUID = 5750870182729872170L;

        static WrapCopier<AttendanceV2LeaveRequest, Wo> copier = WrapCopierFactory.wo(AttendanceV2LeaveRequest.class,
                Wo.class, null,
                JpaObject.FieldsInvisible);

        private AttendanceV2LeaveType leaveType;

        public AttendanceV2LeaveType getLeaveType() {
            return leaveType;
        }

        public void setLeaveType(AttendanceV2LeaveType leaveType) {
            this.leaveType = leaveType;
        }

    }

}
