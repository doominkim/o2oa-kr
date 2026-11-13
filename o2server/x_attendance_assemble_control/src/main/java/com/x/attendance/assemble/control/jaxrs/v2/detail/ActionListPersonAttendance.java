package com.x.attendance.assemble.control.jaxrs.v2.detail;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.detail.model.RecordWo;
import com.x.attendance.entity.v2.AttendanceV2AppealInfo;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Detail;
import com.x.attendance.entity.v2.AttendanceV2LeaveData;
import com.x.attendance.entity.v2.AttendanceV2LeaveRequest;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ActionListPersonAttendance extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListPersonAttendance.class);

    ActionResult<List<Wo>> execute(JsonElement jsonElement) throws Exception {
        ActionResult<List<Wo>> result = new ActionResult<>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (wi == null || wi.getFilterList() == null || wi.getFilterList().isEmpty()) {
                throw new ExceptionEmptyParameter("필터 인원 또는 조직");
            }
            Business business = new Business(emc);
            List<String> userList = new ArrayList<>();
            for (String f : wi.getFilterList()) {
                analysisPerson(userList, f, business);
            }
            if (userList.isEmpty()) {
                throw new ExceptionWithMessage("현재 조회 조건으로 인원 정보를 찾을 수 없습니다!");
            }
            String queryDate = wi.getQueryDate();
            if (StringUtils.isEmpty(queryDate)) {
                queryDate = DateTools.format(new Date(), DateTools.format_yyyyMMdd);
            } else {
                DateTools.parse(queryDate, DateTools.format_yyyyMMdd);
            }
            List<Wo> wos = new ArrayList<>();
            Set<String> userSet = new LinkedHashSet<>(userList);
            for (String person : userSet) {
                Wo wo = new Wo();
                wo.setPerson(person);
                List<AttendanceV2Detail> detailList = business.getAttendanceV2ManagerFactory()
                        .listDetailWithPersonAndDate(person, queryDate);
                if (detailList != null && !detailList.isEmpty()) {
                    wo.setDetail(detailList.get(0));
                }
                List<AttendanceV2CheckInRecord> recordList = business.getAttendanceV2ManagerFactory()
                        .listRecordWithPersonAndDate(person, queryDate);
                List<RecordWo> recordWos = RecordWo.copier.copy(recordList);
                fillRecordData(emc, recordWos);
                wo.setRecordList(recordWos);
                wos.add(wo);
            }
            result.setData(wos);
            result.setCount((long) wos.size());
        }

        return result;
    }

    private void analysisPerson(List<String> userList, String filter, Business business) throws Exception {
        if (StringUtils.isEmpty(filter)) {
            return;
        }
        if (filter.endsWith("@U")) { // 조직을 사용자 목록으로 변환(비재귀)
            List<String> users = business.organization().person().listWithUnitSubDirect(filter);
            if (users != null && !users.isEmpty()) {
                userList.addAll(users);
            }
        } else if (filter.endsWith("@P")) {
            userList.add(filter);
        }
    }

    private void fillRecordData(EntityManagerContainer emc, List<RecordWo> recordWos) {
        if (recordWos == null || recordWos.isEmpty()) {
            return;
        }
        for (RecordWo recordWo : recordWos) {
            try {
                if (StringUtils.isNotEmpty(recordWo.getLeaveDataId())) {
                    AttendanceV2LeaveData leaveData = emc.find(recordWo.getLeaveDataId(), AttendanceV2LeaveData.class);
                    if (leaveData != null) {
                        recordWo.setLeaveData(leaveData);
                    }
                }
                if (StringUtils.isNotEmpty(recordWo.getAppealId())) {
                    AttendanceV2AppealInfo appealData = emc.find(recordWo.getAppealId(), AttendanceV2AppealInfo.class);
                    if (appealData != null) {
                        recordWo.setAppealData(appealData);
                    }
                }
                if (StringUtils.isNotEmpty(recordWo.getRequestDataId())) {
                    AttendanceV2LeaveRequest leaveRequest = emc.find(recordWo.getRequestDataId(),
                            AttendanceV2LeaveRequest.class);
                    if (leaveRequest != null) {
                        recordWo.setLeaveRequest(leaveRequest);
                    }
                }
            } catch (Exception e) {
                LOGGER.warn("fill attendance record extend data error, record id:{}.", recordWo.getId(), e);
            }
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe("인원 식별자")
        private String person;

        @FieldDescribe("근태 통계")
        private AttendanceV2Detail detail;

        @FieldDescribe("체크 기록")
        private List<RecordWo> recordList;

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public AttendanceV2Detail getDetail() {
            return detail;
        }

        public void setDetail(AttendanceV2Detail detail) {
            this.detail = detail;
        }

        public List<RecordWo> getRecordList() {
            return recordList;
        }

        public void setRecordList(
                List<RecordWo> recordList) {
            this.recordList = recordList;
        }
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("사용자 또는 조직 필터, 조직은 단일 계층만 지원: 사용자 또는 조직의 DN, 예: xxx@xxx@P, xxx@xxx@U")
        private List<String> filterList;

        @FieldDescribe("조회 날짜 yyyy-MM-dd")
        private String queryDate;

        public List<String> getFilterList() {
            return filterList;
        }

        public void setFilterList(List<String> filterList) {
            this.filterList = filterList;
        }

        public String getQueryDate() {
            return queryDate;
        }

        public void setQueryDate(String queryDate) {
            this.queryDate = queryDate;
        }
    }

}
