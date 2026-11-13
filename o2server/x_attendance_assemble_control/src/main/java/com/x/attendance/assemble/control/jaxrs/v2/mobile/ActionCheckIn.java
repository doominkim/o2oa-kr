package com.x.attendance.assemble.control.jaxrs.v2.mobile;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionNotExistObject;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.entity.v2.AttendanceV2CheckInRecord;
import com.x.attendance.entity.v2.AttendanceV2Group;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2023/2/22.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ActionCheckIn extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckIn.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        if (effectivePerson == null || StringUtils.isEmpty(effectivePerson.getDistinguishedName())) {
            throw new ExceptionEmptyParameter("현재 사용자 정보");
        }
        ActionResult<Wo> result = new ActionResult<>();
        String detailDate = null;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            if (StringUtils.isEmpty(wi.getRecordId())) {
                throw new ExceptionEmptyParameter("체크 대상 id");
            }
            if (StringUtils.isEmpty(wi.getCheckInType()) || (!wi.getCheckInType().equals(AttendanceV2CheckInRecord.OnDuty) && !wi.getCheckInType().equals(AttendanceV2CheckInRecord.OffDuty))) {
                throw new ExceptionEmptyParameter("체크 유형");
            }
            if (StringUtils.isEmpty(wi.getLatitude()) || StringUtils.isEmpty(wi.getLongitude())) {
                throw new ExceptionEmptyParameter("위경도");
            }
            if ((BooleanUtils.isFalse(wi.getFieldWork())) && StringUtils.isEmpty(wi.getWorkPlaceId())) {
                throw new ExceptionEmptyParameter("체크 근무 장소 id");
            }
            // 현재 사용자의 근태 그룹 조회
            Business business = new Business(emc);
            List<AttendanceV2Group> groups = business.getAttendanceV2ManagerFactory().listGroupWithPerson(effectivePerson.getDistinguishedName());
            if (groups == null || groups.isEmpty()) {
                throw new ExceptionNotExistObject("근태 그룹 정보");
            }

            AttendanceV2CheckInRecord record = emc.find(wi.getRecordId(), AttendanceV2CheckInRecord.class);
            if (record == null) {
                throw new ExceptionNotExistObject("체크 기록");
            }
            if (!effectivePerson.getDistinguishedName().equals(record.getUserId())) {
                throw new ExceptionWithMessage("사용자가 일치하지 않아 체크할 수 없습니다!");
            }
            // 빠른 체크는 이미 체크된 데이터를 업데이트할 수 없습니다
            if (AttendanceV2CheckInRecord.SOURCE_TYPE_FAST_CHECK.equals(wi.getSourceType()) && !AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn.equals(record.getCheckInResult())) {
                throw new ExceptionCannotFastCheckIn();
            }
            // 체크 시간
            Date nowDate = new Date();
            AttendanceV2CheckInRecord back = executeWithCheckLock("check:" + record.getId(),
                    new CheckInCallableImpl(nowDate, record.getId(), CheckInWi.fromApp(wi)));
            if (back != null) {
                // 이상 데이터
                generateAppealInfo(back, groups.get(0).getFieldWorkMarkError(), emc, business);
                Wo wo = new Wo();
                wo.setCheckInResult(back.getCheckInResult());
                wo.setRecordDate(nowDate);
                wo.setCheckInRecordId(back.getId());
                result.setData(wo);
                detailDate = back.getRecordDateString();
            }
        }
        if (result.getData() == null) {
            throw new ExceptionNoCheckInResult();
        }
        checkAndSendV2DetailIfAllCheckInCompletedAsync(effectivePerson.getDistinguishedName(),
                detailDate);
        return result;
    }


    public static class Wo extends GsonPropertyObject {
        @FieldDescribe("체크 결과 id")
        private String checkInRecordId;
        @FieldDescribe("체크 결과")
        private String checkInResult;
        @FieldDescribe("체크 기록 날짜")
        private Date recordDate;

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

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("체크 대상 id")
        private String recordId;

        @FieldDescribe("출퇴근 체크 유형, OnDuty OffDuty")
        private String checkInType;

        @FieldDescribe("체크 근무지 id, 범위 내 체크 시 전달 필요")
        private String workPlaceId;

        @FieldDescribe("외근 체크 여부.")
        private Boolean fieldWork;

        @FieldDescribe("외근 체크 설명")
        private String signDescription;

        @FieldDescribe("외근 체크 사진 첨부파일 ID 목록")
        private List<String> fieldWorkPhotoFileIdList;

        @FieldDescribe("출처 기기: Mac|Windows|IOS|Android|기타")
        private String sourceDevice;

        @FieldDescribe("기타 설명 비고")
        private String description;

        @FieldDescribe("현재 위치 경도")
        private String longitude;

        @FieldDescribe("현재 위치 위도")
        private String latitude;

        @FieldDescribe("현재 위치 장소 설명")
        private String recordAddress;

        @FieldDescribe("체크 데이터 출처: USER_CHECK(사용자 체크) FAST_CHECK(빠른 체크) ")
        private String sourceType;

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public String getRecordId() {
            return recordId;
        }

        public void setRecordId(String recordId) {
            this.recordId = recordId;
        }

        public String getCheckInType() {
            return checkInType;
        }

        public void setCheckInType(String checkInType) {
            this.checkInType = checkInType;
        }

        public String getWorkPlaceId() {
            return workPlaceId;
        }

        public void setWorkPlaceId(String workPlaceId) {
            this.workPlaceId = workPlaceId;
        }

        public Boolean getFieldWork() {
            return fieldWork;
        }

        public void setFieldWork(Boolean fieldWork) {
            this.fieldWork = fieldWork;
        }

        public String getSignDescription() {
            return signDescription;
        }

        public void setSignDescription(String signDescription) {
            this.signDescription = signDescription;
        }

        public List<String> getFieldWorkPhotoFileIdList() {
            return fieldWorkPhotoFileIdList;
        }

        public void setFieldWorkPhotoFileIdList(List<String> fieldWorkPhotoFileIdList) {
            this.fieldWorkPhotoFileIdList = fieldWorkPhotoFileIdList;
        }

        public String getSourceDevice() {
            return sourceDevice;
        }

        public void setSourceDevice(String sourceDevice) {
            this.sourceDevice = sourceDevice;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getRecordAddress() {
            return recordAddress;
        }

        public void setRecordAddress(String recordAddress) {
            this.recordAddress = recordAddress;
        }
    }
}
