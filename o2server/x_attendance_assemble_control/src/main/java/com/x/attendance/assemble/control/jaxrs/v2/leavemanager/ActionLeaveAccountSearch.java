package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount;
import com.x.attendance.entity.v2.AttendanceV2LeaveAccount_;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionLeaveAccountSearch extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionLeaveAccountSearch.class);

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if ((wi.getFilterList() == null || wi.getFilterList().isEmpty())) {
                logger.warn("조회 조건이 불완전합니다. 조회 조건: {0}", wi);
                return result;
            }
            List<String> userList = new ArrayList<>();
            Business business = new Business(emc);
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                for (String f : wi.getFilterList()) {
                    AttendanceV2Helper.analysisFilterToPersonList(userList, f, business, wi.getRecursive());
                }
            }
            if (userList.isEmpty()) {
                logger.warn("사용자 정보를 찾을 수 없습니다. 조회 조건: {0}", "userList=[]");
                return result;
            }
            // userList 중복 제거
            java.util.Set<String> set = new java.util.HashSet<>(userList);
            List<String> distinctUserList = new ArrayList<>(set);
            List<AttendanceV2LeaveAccount> accounts = findAccountsWithPersonAndType(distinctUserList,
                    emc);

            List<AttendanceV2LeaveType> leaveTypeList = getLeaveTypeList(null);
            Wo wo = new Wo();
            wo.setAccountList(accounts);
            wo.setPersonList(distinctUserList);
            wo.setLeaveTypeList(leaveTypeList);
            result.setCount((long) accounts.size());
            result.setData(wo);
            return result;
        }
    }

    // 사용자와 휴가 유형으로 잔여 계정 페이지 조회
    private List<AttendanceV2LeaveAccount> findAccountsWithPersonAndType(List<String> userList,
            EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveAccount.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveAccount> cq = cb.createQuery(AttendanceV2LeaveAccount.class);
        Root<AttendanceV2LeaveAccount> root = cq.from(AttendanceV2LeaveAccount.class);
        Predicate p = root.get(AttendanceV2LeaveAccount_.person).in(userList);
        cq.select(root).where(p).orderBy(cb.desc(root.get(AttendanceV2LeaveAccount_.updateTime)));
        return em.createQuery(cq).getResultList();
    }

    // 사용자와 휴가 유형으로 잔여 계정 수 조회
    private Long countAccountsWithPersonAndType(List<String> userList, List<String> leaveTypeList,
            EntityManagerContainer emc) throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveAccount.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<AttendanceV2LeaveAccount> root = cq.from(AttendanceV2LeaveAccount.class);
        Predicate p = root.get(AttendanceV2LeaveAccount_.person).in(userList);
        p = cb.and(p, root.get(AttendanceV2LeaveAccount_.leaveTypeId).in(leaveTypeList));
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    // 휴가 유형 ID 목록으로 휴가 유형 목록 조회
    private List<AttendanceV2LeaveType> getLeaveTypeList(List<String> idList,
            EntityManagerContainer emc)
            throws Exception {
        EntityManager em = emc.get(AttendanceV2LeaveType.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<AttendanceV2LeaveType> cq = cb.createQuery(AttendanceV2LeaveType.class);
        Root<AttendanceV2LeaveType> root = cq.from(AttendanceV2LeaveType.class);
        Predicate p = root.get(JpaObject.id_FIELDNAME).in(idList);
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 5802792521103850683L;

        @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("필터 조직의 하위 조직 사용자 재귀 조회 여부, 기본 true, false면 현재 조직 직속 사용자만 조회")
        private Boolean recursive;

//        @FieldDescribe("필터링할 휴가 유형 ID 목록")
//        private List<String> leaveTypeList;

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
//
//        public List<String> getLeaveTypeList() {
//            return leaveTypeList;
//        }
//
//        public void setLeaveTypeList(List<String> leaveTypeList) {
//            this.leaveTypeList = leaveTypeList;
//        }

    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -987683927054761311L;

        @FieldDescribe("잔여 계정 목록")
        private List<AttendanceV2LeaveAccount> accountList;
        @FieldDescribe("사용자 목록")
        private List<String> personList;
        @FieldDescribe("휴가 유형 목록")
        private List<AttendanceV2LeaveType> leaveTypeList;

        public List<AttendanceV2LeaveAccount> getAccountList() {
            return accountList;
        }

        public void setAccountList(List<AttendanceV2LeaveAccount> accountList) {
            this.accountList = accountList;
        }

        public List<String> getPersonList() {
            return personList;
        }

        public void setPersonList(List<String> personList) {
            this.personList = personList;
        }

        public List<AttendanceV2LeaveType> getLeaveTypeList() {
            return leaveTypeList;
        }

        public void setLeaveTypeList(List<AttendanceV2LeaveType> leaveTypeList) {
            this.leaveTypeList = leaveTypeList;
        }

    }
}
