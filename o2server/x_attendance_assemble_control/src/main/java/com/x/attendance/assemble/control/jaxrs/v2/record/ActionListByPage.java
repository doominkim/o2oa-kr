package com.x.attendance.assemble.control.jaxrs.v2.record;

import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;

/**
 * Created by fancyLou on 2023/2/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionListByPage extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListByPage.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, Integer page, Integer size, JsonElement jsonElement)
            throws Exception {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("execute:{}, page:{}, size:{}.", effectivePerson.getDistinguishedName(), page, size);
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<List<Wo>> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null) {
                wi = new Wi();
            }
            Integer adjustPage = this.adjustPage(page);
            Integer adjustPageSize = this.adjustSize(size);
            Date start = null;
            Date end = null;
            if (StringUtils.isNotEmpty(wi.getStartDate()) && StringUtils.isNotEmpty(wi.getEndDate())) {
                start = DateTools.parseDateTime(wi.getStartDate() + " 00:00:00");
                end = DateTools.parseDateTime(wi.getEndDate() + " 23:59:59");
            }
            List<String> userList = null;
            if (wi.getFilterList() != null && !wi.getFilterList().isEmpty()) {
                userList = new ArrayList<>();
                for (String f : wi.getFilterList()) {
                    AttendanceV2Helper.analysisFilterToPersonList(userList, f, business, wi.getRecursive());
                }
                userList = new ArrayList<>(new LinkedHashSet<>(userList));
                if (userList.isEmpty()) {
                    throw new ExceptionWithMessage("현재 조회 조건으로 인원 정보를 찾을 수 없습니다!");
                }
            }
            List<AttendanceV2CheckInRecord> list = business.getAttendanceV2ManagerFactory().listRecordByPage(adjustPage,
                    adjustPageSize, wi.getUserId(), userList, start, end, wi.getSourceType(), wi.getCheckInResult(),
                    wi.getCheckInType(), wi.getFieldWork());
            List<Wo> wos = Wo.copier.copy(list);
            for (Wo wo : wos) {
                try {
                    if (StringUtils.isNotEmpty(wo.getLeaveDataId())) {
                        AttendanceV2LeaveData leaveData = emc.find(wo.getLeaveDataId(), AttendanceV2LeaveData.class);
                        if (leaveData != null) {
                            wo.setLeaveData(leaveData);
                        }
                    }
                    if (StringUtils.isNotEmpty(wo.getRequestDataId())) {
                        AttendanceV2LeaveRequest leaveRequest = emc.find(wo.getRequestDataId(), AttendanceV2LeaveRequest.class);
                        if (leaveRequest != null) {
                            wo.setLeaveRequest(leaveRequest);
                        }
                    }
                } catch (Exception ignore) {}
            }
            result.setData(wos);
            result.setCount(business.getAttendanceV2ManagerFactory().recordCount(wi.getUserId(), userList, start, end,
                    wi.getSourceType(), wi.getCheckInResult(), wi.getCheckInType(), wi.getFieldWork()));
            return result;
        }
    }

    public static class Wi extends GsonPropertyObject {


        private static final long serialVersionUID = 4227642755086093795L;
        @FieldDescribe("체크 사용자 식별자")
        private String userId;
        @FieldDescribe("사용자 또는 조직 필터, 조직은 기본 재귀: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
        private List<String> filterList;
        @FieldDescribe("필터 조직의 하위 조직 사용자 재귀 조회 여부, 기본 true, false면 현재 조직 직속 사용자만 조회")
        private Boolean recursive;
        @FieldDescribe("체크 기록 시작 날짜: YYYY-MM-dd")
        private String startDate;
        @FieldDescribe("체크 기록 종료 날짜: YYYY-MM-dd")
        private String endDate;
        @FieldDescribe("체크 데이터 출처: USER_CHECK|AUTO_CHECK|FAST_CHECK|SYSTEM_IMPORT")
        private String sourceType;
        @FieldDescribe("체크 결과: Normal|Early|Late|SeriousLate|NotSigned")
        private String checkInResult;
        @FieldDescribe("근태 유형: OnDuty|OffDuty")
        private String checkInType;
        @FieldDescribe("외근 체크 여부.")
        private Boolean fieldWork;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getCheckInResult() {
            return checkInResult;
        }

        public void setCheckInResult(String checkInResult) {
            this.checkInResult = checkInResult;
        }

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public Boolean getFieldWork() {
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

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


    public static class Wo extends AttendanceV2CheckInRecord {

        @FieldDescribe("외출 기록")
        private AttendanceV2LeaveData leaveData;
        @FieldDescribe("휴가 기록")
        private AttendanceV2LeaveRequest leaveRequest;
        public AttendanceV2LeaveData getLeaveData() {
            return leaveData;
        }

        public void setLeaveData(AttendanceV2LeaveData leaveData) {
            this.leaveData = leaveData;
        }

        public AttendanceV2LeaveRequest getLeaveRequest() {
            return leaveRequest;
        }

        public void setLeaveRequest(AttendanceV2LeaveRequest leaveRequest) {
            this.leaveRequest = leaveRequest;
        }

        static WrapCopier<AttendanceV2CheckInRecord, Wo> copier = WrapCopierFactory.wo(AttendanceV2CheckInRecord.class, Wo.class, null,
                JpaObject.FieldsInvisible);
    }
}
