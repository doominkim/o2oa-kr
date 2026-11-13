package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.WoGroupShift;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.attendance.entity.v2.AttendanceV2WorkPlace;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 현재는 체크 데이터 조회 API
 * 체크 전에 반드시 이 API를 먼저 조회하고, 반환된 체크 결과 목록 데이터로 체크
 * 체크 결과 checkInResult 중 PreCheckIn은 체크가 필요한 사전 저장 데이터이고 나머지는 이미 체크된 결과
 * Created by fancyLou on 2023/2/21.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionPreCheck extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionPreCheck.class);

    ActionResult<Wo> execute(String person) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        if (StringUtils.isEmpty(person)) {
            throw new ExceptionEmptyParameter("현재 사용자 정보");
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            // 현재 사용자의 근태 그룹 조회
            Business business = new Business(emc);
            Date nowDate = new Date();
            String today = DateTools.format(nowDate, DateTools.format_yyyyMMdd);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("날짜: {}", today);
            }
            WoGroupShift woGroupShift = business.getAttendanceV2ManagerFactory().getGroupShiftByPersonDate(person, today);
            if (woGroupShift == null || woGroupShift.getGroup() == null) {
                result.setData(cannotCheckIn("해당 근태 그룹이 없습니다"));
                return result;
            }
            AttendanceV2Group group = woGroupShift.getGroup();
            // 근무지가 설정되지 않아 체크할 수 없음
            if (group.getWorkPlaceIdList() == null || group.getWorkPlaceIdList().isEmpty()) {
                result.setData(cannotCheckIn("근무지가 설정되지 않았습니다"));
                return result;
            }
            // 같은 사용자·같은 날의 사전 체크 생성을 직렬화하여 피크 시간에 전역 단일 스레드가 모든 사용자를 막지 않도록 함.
            String lockKey = "pre:" + person + ":" + today;
            List<AttendanceV2CheckInRecord> recordList = executeWithCheckLock(lockKey,
                    new CallableImpl(person, group, woGroupShift.getShift(), nowDate));
            if (recordList == null || recordList.isEmpty()) {
                result.setData(cannotCheckIn("해당 출퇴근 체크 시간이 없습니다"));
                return result;
            }
            // 정렬
            recordList = new ArrayList<>(recordList);
            recordList.sort(
                    Comparator.comparing(AttendanceV2CheckInRecord::getRecordDate, Comparator.nullsFirst(
                            Comparator.naturalOrder()))
            );
            Wo wo = new Wo();
            wo.setCanCheckIn(true);
            wo.setAllowFieldWork(group.getAllowFieldWork());
            wo.setRequiredFieldWorkRemarks(group.getRequiredFieldWorkRemarks());
            wo.setRequiredFieldWorkPhoto(group.getRequiredFieldWorkPhoto());
            wo.setCheckItemList(recordList);
            if (group.getWorkPlaceIdList() != null && !group.getWorkPlaceIdList().isEmpty()) {
                List<AttendanceV2WorkPlace> workPlaceList = new ArrayList<>();
                for (String id : group.getWorkPlaceIdList()) {
                    AttendanceV2WorkPlace workPlace = emc.find(id, AttendanceV2WorkPlace.class);
                    workPlaceList.add(workPlace);
                }
                wo.setWorkPlaceList(workPlaceList);
            }
            result.setData(wo);
        }
        return result;
    }





    private Wo cannotCheckIn(String reason) {
        Wo wo = new Wo();
        wo.setCanCheckIn(false);
        wo.setCannotCheckInReason(reason);
        return wo;
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = -2916750848315546343L;

        @FieldDescribe("여부")
        private Boolean free;

        @FieldDescribe("외근 체크 허용 여부")
        private Boolean allowFieldWork;
        @FieldDescribe("외근 체크 비고 필수 여부")
        private Boolean requiredFieldWorkRemarks;
        @FieldDescribe("외근 체크 사진 필수 여부")
        private Boolean requiredFieldWorkPhoto;
        @FieldDescribe("현재 시간 체크 가능 여부")
        private Boolean canCheckIn;
        @FieldDescribe("체크할 수 없는 사유")
        private String cannotCheckInReason;
        @FieldDescribe("체크 목록")
        private List<AttendanceV2CheckInRecord> checkItemList;
        @FieldDescribe("근무지 목록")
        private List<AttendanceV2WorkPlace> workPlaceList;

        public Boolean getAllowFieldWork() {
            return allowFieldWork;
        }

        public void setAllowFieldWork(Boolean allowFieldWork) {
            this.allowFieldWork = allowFieldWork;
        }

        public Boolean getRequiredFieldWorkRemarks() {
            return requiredFieldWorkRemarks;
        }

        public void setRequiredFieldWorkRemarks(Boolean requiredFieldWorkRemarks) {
            this.requiredFieldWorkRemarks = requiredFieldWorkRemarks;
        }

        public Boolean getRequiredFieldWorkPhoto() {
            return requiredFieldWorkPhoto;
        }

        public void setRequiredFieldWorkPhoto(Boolean requiredFieldWorkPhoto) {
            this.requiredFieldWorkPhoto = requiredFieldWorkPhoto;
        }

        public Boolean getCanCheckIn() {
            return canCheckIn;
        }

        public void setCanCheckIn(Boolean canCheckIn) {
            this.canCheckIn = canCheckIn;
        }

        public String getCannotCheckInReason() {
            return cannotCheckInReason;
        }

        public void setCannotCheckInReason(String cannotCheckInReason) {
            this.cannotCheckInReason = cannotCheckInReason;
        }

        public List<AttendanceV2CheckInRecord> getCheckItemList() {
            return checkItemList;
        }

        public void setCheckItemList(List<AttendanceV2CheckInRecord> checkItemList) {
            this.checkItemList = checkItemList;
        }

        public List<AttendanceV2WorkPlace> getWorkPlaceList() {
            return workPlaceList;
        }

        public void setWorkPlaceList(List<AttendanceV2WorkPlace> workPlaceList) {
            this.workPlaceList = workPlaceList;
        }
    }

    public static class WoCheckInAndRecordItem extends GsonPropertyObject {
        private static final long serialVersionUID = 9162618804645395372L;
        @FieldDescribe("근태 유형, OnDuty: 출근 OffDuty: 퇴근")
        private String checkInType;
        @FieldDescribe("체크 시간")
        private String dutyTime;
        @FieldDescribe("체크 전 제한")
        private String dutyTimeBeforeLimit;
        @FieldDescribe("체크 후 제한")
        private String dutyTimeAfterLimit;

        /// 체크 완료 필드
        @FieldDescribe("체크 결과 id")
        private String checkInRecordId;
        @FieldDescribe("체크 결과")
        private String checkInResult;
        @FieldDescribe("체크 기록 날짜")
        private Date recordDate;

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getDutyTime() {
            return dutyTime;
        }

        public void setDutyTime(String dutyTime) {
            this.dutyTime = dutyTime;
        }

        public String getDutyTimeBeforeLimit() {
            return dutyTimeBeforeLimit;
        }

        public void setDutyTimeBeforeLimit(String dutyTimeBeforeLimit) {
            this.dutyTimeBeforeLimit = dutyTimeBeforeLimit;
        }

        public String getDutyTimeAfterLimit() {
            return dutyTimeAfterLimit;
        }

        public void setDutyTimeAfterLimit(String dutyTimeAfterLimit) {
            this.dutyTimeAfterLimit = dutyTimeAfterLimit;
        }

        public String getCheckInRecordId() {
            return checkInRecordId;
        }

        public void setCheckInRecordId(String checkInRecordId) {
            this.checkInRecordId = checkInRecordId;
        }

        public String getCheckInResult() {
            return checkInResult;
        }

        public void setCheckInResult(String checkInResult) {
            this.checkInResult = checkInResult;
        }

        public Date getRecordDate() {
            return recordDate;
        }

        public void setRecordDate(Date recordDate) {
            this.recordDate = recordDate;
        }
    }
}
