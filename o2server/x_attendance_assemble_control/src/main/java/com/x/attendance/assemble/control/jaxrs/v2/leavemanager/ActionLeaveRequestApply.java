package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.detail.ExceptionDateEndBeforeStartError;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveRequestEnums.LeaveRequestStatusEnum;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveTypeEnums.QuotaTypeEnum;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.attendance.entity.v2.AttendanceV2LeaveTransaction;
import com.x.attendance.entity.v2.AttendanceV2LeaveType;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;

public class ActionLeaveRequestApply extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionLeaveRequestApply.class);

    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getPerson())) {
                throw new ExceptionEmptyParameter("인원 식별자");
            }
            if (StringUtils.isEmpty(wi.getLeaveTypeId())) {
                throw new ExceptionEmptyParameter("휴가 유형 ID");
            }
            if (null == wi.getStartTime()) {
                throw new ExceptionEmptyParameter("시작 시간");
            }
            if (null == wi.getEndTime()) {
                throw new ExceptionEmptyParameter("종료 시간");
            }
            if (wi.getStartTime() != null && wi.getEndTime() != null && wi.getStartTime().after(wi.getEndTime())) {
                throw new ExceptionDateEndBeforeStartError();
            }
            // 사용자 조회 후 DN으로 변환
            Business business = new Business(emc);
            Person person = business.organization().person().getObject(wi.getPerson(), true);
            if (person == null) {
                throw new ExceptionNotExistObject("인원 " + wi.getPerson());
            }
            wi.setPerson(person.getDistinguishedName()); // DN으로 변환해 저장
            AttendanceV2LeaveType leaveType = emc.find(wi.getLeaveTypeId(), AttendanceV2LeaveType.class);
            if (leaveType == null) {
                throw new ExceptionNotExistObject("휴가 유형 " + wi.getLeaveTypeId());
            }

            if (wi.getDuration() == null || wi.getDuration() <= 0.0) { // 전달된 데이터가 있으면 계산하지 않고 전달된 값 사용.
                // 날짜 간격 계산
                long interval = wi.getEndTime().getTime() - wi.getStartTime().getTime();
                double days = interval / (1000.0 * 3600 * 24);
                // 소수점 1자리 유지
                BigDecimal b = new BigDecimal(days);
                days = b.setScale(1, RoundingMode.HALF_UP).doubleValue();
                wi.setDuration(days);
            }
            String id;
            if (QuotaTypeEnum.QUOTA.getValue().equals(leaveType.getQuotaType())) {
                emc.beginTransaction(AttendanceV2LeaveRequest.class);
                emc.beginTransaction(AttendanceV2LeaveLedger.class);
                emc.beginTransaction(AttendanceV2LeaveTransaction.class);
                id = saveLeaveRequest(emc, wi);
                deductingLeaveBalance(emc, leaveType, person.getDistinguishedName(), wi.getDuration(), id);
                emc.commit();
                AttendanceV2LeaveManager.asyncUpdateLeaveAccount(person.getDistinguishedName(), leaveType.getId());
            } else {
                emc.beginTransaction(AttendanceV2LeaveRequest.class);
                id = saveLeaveRequest(emc, wi);
                emc.commit();
            }
            Wo wo = new Wo();
            wo.setId(id);
            result.setData(wo);
            return result;
        }
    }

    /**
     * 휴가 신청 데이터 저장
     * 
     * @param emc
     * @param wi
     * @return
     * @throws Exception
     */
    private String saveLeaveRequest(EntityManagerContainer emc, Wi wi) throws Exception {
        AttendanceV2LeaveRequest leaveRequest = new AttendanceV2LeaveRequest();
        leaveRequest.setPerson(wi.getPerson());
        leaveRequest.setLeaveTypeId(wi.getLeaveTypeId());
        leaveRequest.setStartTime(wi.getStartTime());
        leaveRequest.setEndTime(wi.getEndTime());
        leaveRequest.setDuration(wi.getDuration());
        leaveRequest.setDescription(wi.getDescription());
        leaveRequest.setStatus(LeaveRequestStatusEnum.APPLYING.getValue());
        emc.persist(leaveRequest, CheckPersistType.all);
        return leaveRequest.getId();
    }

    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = 6434642802459794425L;

        @FieldDescribe("사용자")
        private String person;

        @FieldDescribe("휴가 유형 ID")
        private String leaveTypeId;

        @FieldDescribe("시작 시간: yyyy-MM-dd HH:mm:ss")
        private Date startTime;

        @FieldDescribe("종료 시간: yyyy-MM-dd HH:mm:ss")
        private Date endTime;

        @FieldDescribe("휴가 일수")
        private Double duration;

        @FieldDescribe("휴가 설명")
        private String description;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getLeaveTypeId() {
            return leaveTypeId;
        }

        public void setLeaveTypeId(String leaveTypeId) {
            this.leaveTypeId = leaveTypeId;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public Double getDuration() {
            return duration;
        }

        public void setDuration(Double duration) {
            this.duration = duration;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    public static class Wo extends WoId {

        private static final long serialVersionUID = -2826350900812782444L;

    }
}
