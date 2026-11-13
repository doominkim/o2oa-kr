package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Strategy;

/**
 * 근태 설정
 * Created by fancyLou on 2023/2/28.
 * Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2Config", description = "근태 설정 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Config.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2Config.table
                                                                                                           + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Config extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Config.table;
    private static final long serialVersionUID = 3024779660233909771L;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("데이터베이스 기본 키, 자동 생성.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
        if(this.properties == null){
            this.properties = new AttendanceV2ConfigProperties();
        }
    }
    /*
     * =============================================================================
     * ===== 위는 JpaObject 기본 필드
     * =============================================================================
     * =====
     */

    /*
     * =============================================================================
     * ===== 아래는 업무별 데이터 테이블 필드 요구사항
     * =============================================================================
     * =====
     */


    public static final String appealEnable_FIELDNAME = "appealEnable";
    @FieldDescribe("보정 체크 이의 신청 기능 사용 여부")
    @Column(name = ColumnNamePrefix + appealEnable_FIELDNAME)
    private Boolean appealEnable = false;

    public static final String appealMaxTimes_FIELDNAME = "appealMaxTimes";
    @FieldDescribe("월 최대 이의 신청 횟수, 0은 무제한")
    @Column(  name = ColumnNamePrefix + appealMaxTimes_FIELDNAME)
    private Integer appealMaxTimes = 0;

    public static final String processId_FIELDNAME = "processId";
    @FieldDescribe("사용자 정의 프로세스 id")
    @Column(name = ColumnNamePrefix + processId_FIELDNAME)
    private String processId;

    public static final String processName_FIELDNAME = "processName";
    @FieldDescribe("사용자 정의 프로세스 이름")
    @Column(name = ColumnNamePrefix + processName_FIELDNAME)
    private String processName;

    public static final String onDutyFastCheckInEnable_FIELDNAME = "onDutyFastCheckInEnable";
    @FieldDescribe("출근 빠른 체크, app에서만 유효")
    @Column(name = ColumnNamePrefix + onDutyFastCheckInEnable_FIELDNAME)
    private Boolean onDutyFastCheckInEnable = false;

    public static final String offDutyFastCheckInEnable_FIELDNAME = "offDutyFastCheckInEnable";
    @FieldDescribe("퇴근 빠른 체크, app에서만 유효")
    @Column(name = ColumnNamePrefix + offDutyFastCheckInEnable_FIELDNAME)
    private Boolean offDutyFastCheckInEnable = false;

    public static final String checkInAlertEnable_FIELDNAME = "checkInAlertEnable";
    @FieldDescribe("체크 알림, 출근 전·퇴근 후 알림을 받아 체크를 잊지 않도록 함.")
    @Column(name = ColumnNamePrefix + checkInAlertEnable_FIELDNAME)
    private Boolean checkInAlertEnable = false;

    public static final String exceptionAlertEnable_FIELDNAME = "exceptionAlertEnable";
    @FieldDescribe("비정상 체크 알림, 익일에 비정상 체크 알림을 받음.")
    @Column(name = ColumnNamePrefix + exceptionAlertEnable_FIELDNAME)
    private Boolean exceptionAlertEnable = false;

    public static final String exceptionAlertTime_FIELDNAME = "exceptionAlertTime";
    @FieldDescribe("비정상 체크 알림 시간: HH:mm")
    @Column(length = length_8B, name = ColumnNamePrefix + exceptionAlertTime_FIELDNAME)
    private String exceptionAlertTime = "09:30";

    // 0은 당일, 1은 익일
    public static final String exceptionAlertDateNumber_FIELDNAME = "exceptionAlertDateNumber";
    @FieldDescribe("비정상 체크 알림 날짜 계산 값")
    @Column(  name = ColumnNamePrefix + exceptionAlertDateNumber_FIELDNAME)
    private Integer exceptionAlertDateNumber = 1;

    // 작업 실행 날짜 기록, 당일 알림 작업 실행 여부 판단용
    public static final String exceptionAlertDate_FIELDNAME = "exceptionAlertDate";
    @FieldDescribe("비정상 체크 예약 실행 날짜: yyyy-MM-dd .")
    @Column(length = length_32B, name = ColumnNamePrefix + exceptionAlertDate_FIELDNAME)
    private String exceptionAlertDate;

    // 통계 예약 작업 표현식
    public static final String detailStatisticCronString_FIELDNAME = "detailStatisticCronString";
    @FieldDescribe("통계 예약 작업 표현식 .")
    @Column(length = length_1K, name = ColumnNamePrefix + detailStatisticCronString_FIELDNAME)
    private String detailStatisticCronString;


    public static final String closeOldAttendance_FIELDNAME = "closeOldAttendance";
    @FieldDescribe("구버전 근태 종료, 모바일 구버전 체크 화면에 안내 표시.")
    @Column(name = ColumnNamePrefix + closeOldAttendance_FIELDNAME)
    private Boolean closeOldAttendance = false;


    public static final String faceDetectionEnable_FIELDNAME = "faceDetectionEnable";
    @FieldDescribe("얼굴 인식 비교 사용 여부.")
    @Column(name = ColumnNamePrefix + faceDetectionEnable_FIELDNAME)
    private Boolean faceDetectionEnable = false;

    public static final String properties_FIELDNAME = "properties";
    @FieldDescribe("속성 객체 저장 필드.")
    @Persistent(fetch = FetchType.EAGER)
    @Strategy(JsonPropertiesValueHandler)
    @Column(length = JpaObject.length_4K, name = ColumnNamePrefix + properties_FIELDNAME)
    @CheckPersist(allowEmpty = true)
	private AttendanceV2ConfigProperties properties;


    @Transient
    private String fieldWorkExecuteScript;

    public String getFieldWorkExecuteScript() {
        if (this.fieldWorkExecuteScript == null || this.fieldWorkExecuteScript.isEmpty()) {
            this.fieldWorkExecuteScript = this.getProperties().getFieldWorkExecuteScript();
        }
        return fieldWorkExecuteScript;
    }

    public void setFieldWorkExecuteScript(String fieldWorkExecuteScript) {
        this.fieldWorkExecuteScript = fieldWorkExecuteScript;
        this.getProperties().setFieldWorkExecuteScript(fieldWorkExecuteScript);
    }

    public Integer getExceptionAlertDateNumber() {
        return exceptionAlertDateNumber;
    }

    public void setExceptionAlertDateNumber(Integer exceptionAlertDateNumber) {
        this.exceptionAlertDateNumber = exceptionAlertDateNumber;
    }

    public Boolean getFaceDetectionEnable() {
        return faceDetectionEnable;
    }

    public void setFaceDetectionEnable(Boolean faceDetectionEnable) {
        this.faceDetectionEnable = faceDetectionEnable;
    }

    public String getDetailStatisticCronString() {
      return detailStatisticCronString;
    }

    public void setDetailStatisticCronString(String detailStatisticCronString) {
      this.detailStatisticCronString = detailStatisticCronString;
    }

    public Boolean getCloseOldAttendance() {
      return closeOldAttendance;
    }

    public void setCloseOldAttendance(Boolean closeOldAttendance) {
      this.closeOldAttendance = closeOldAttendance;
    }

    public String getExceptionAlertDate() {
        return exceptionAlertDate;
    }

    public void setExceptionAlertDate(String exceptionAlertDate) {
        this.exceptionAlertDate = exceptionAlertDate;
    }

    public String getExceptionAlertTime() {
        return exceptionAlertTime;
    }

    public void setExceptionAlertTime(String exceptionAlertTime) {
        this.exceptionAlertTime = exceptionAlertTime;
    }

    public Integer getAppealMaxTimes() {
        return appealMaxTimes;
    }

    public void setAppealMaxTimes(Integer appealMaxTimes) {
        this.appealMaxTimes = appealMaxTimes;
    }

    public Boolean getOnDutyFastCheckInEnable() {
        return onDutyFastCheckInEnable;
    }

    public void setOnDutyFastCheckInEnable(Boolean onDutyFastCheckInEnable) {
        this.onDutyFastCheckInEnable = onDutyFastCheckInEnable;
    }

    public Boolean getOffDutyFastCheckInEnable() {
        return offDutyFastCheckInEnable;
    }

    public void setOffDutyFastCheckInEnable(Boolean offDutyFastCheckInEnable) {
        this.offDutyFastCheckInEnable = offDutyFastCheckInEnable;
    }

    public Boolean getCheckInAlertEnable() {
        return checkInAlertEnable;
    }

    public void setCheckInAlertEnable(Boolean checkInAlertEnable) {
        this.checkInAlertEnable = checkInAlertEnable;
    }

    public Boolean getExceptionAlertEnable() {
        return exceptionAlertEnable;
    }

    public void setExceptionAlertEnable(Boolean exceptionAlertEnable) {
        this.exceptionAlertEnable = exceptionAlertEnable;
    }

    public Boolean getAppealEnable() {
        return appealEnable;
    }

    public void setAppealEnable(Boolean appealEnable) {
        this.appealEnable = appealEnable;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public AttendanceV2ConfigProperties getProperties() {
        if (null == this.properties) {
            this.properties = new AttendanceV2ConfigProperties();
        }
        return properties;
    }

    public void setProperties(AttendanceV2ConfigProperties properties) {
        this.properties = properties;
    }

    
}
