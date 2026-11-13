package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fancyLou on 2023/3/2.
 * Copyright © 2023 O2. All rights reserved.
 */
@Schema(name = "AttendanceV2AppealInfo", description = "근태 이의 신청 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2AppealInfo.table, indexes = {
        @javax.persistence.Index(name = PersistenceProperties.AttendanceV2AppealInfo.table + JpaObject.IndexNameMiddle + AttendanceV2AppealInfo.userId_FIELDNAME + "_IDX",
                columnList = JpaObject.ColumnNamePrefix + AttendanceV2AppealInfo.userId_FIELDNAME + "," + JpaObject.ColumnNamePrefix + AttendanceV2AppealInfo.recordDateString_FIELDNAME),
}, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2AppealInfo.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2AppealInfo  extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2AppealInfo.table;
    private static final long serialVersionUID = -2879724233155330444L;


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

    public static final String recordId_FIELDNAME = "recordId";
    @FieldDescribe("이의 신청한 출퇴근 기록 ID.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + recordId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordId;

    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("사용자 식별자")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("출퇴근 기록 날짜, yyyy-MM-dd, 이의 신청한 출퇴근 기록에서 가져옴.")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordDateString;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("출퇴근 기록 시간, 이의 신청한 출퇴근 기록에서 가져옴.")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date recordDate;


    public static final String startTime_FIELDNAME = "startTime";
    @FieldDescribe("이의 신청 시작 시간")
    @Column( length = JpaObject.length_32B, name = ColumnNamePrefix + startTime_FIELDNAME)
    private String startTime;

    public static final String endTime_FIELDNAME = "endTime";
    @FieldDescribe("이의 신청 종료 시간")
    @Column( length = JpaObject.length_32B, name = ColumnNamePrefix + endTime_FIELDNAME)
    private String endTime;

    public static final String reason_FIELDNAME = "reason";
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @FieldDescribe("이의 신청 상세, 이의 신청 프로세스 종료 후 기록.")
    @Column( length = JpaObject.length_2K, name = ColumnNamePrefix + reason_FIELDNAME)
    private String reason;

    public static final Integer status_TYPE_INIT = 0; // 처리 대기
    public static final Integer status_TYPE_PROCESSING = 1; // 결재 중
    public static final Integer status_TYPE_PROCESS_AGREE = 2; // 결재 승인
    public static final Integer status_TYPE_PROCESS_DISAGREE = 3; // 결재 반려
    public static final Integer status_TYPE_END_BY_ADMIN = 4; // 관리자 처리 완료
    public static final Integer status_TYPE_LOCK = 5; // 잠금
    public static final String status_FIELDNAME = "status";
    @FieldDescribe("이의 신청 상태:0-처리 대기, 1-결재 중(프로세스 시작됨), 2-결재 승인, 3-결재 반려, 4-관리자 처리, 5-잠금(잠긴 데이터는 관리자만 처리 가능)")
    @Column( name = ColumnNamePrefix + status_FIELDNAME)
    private Integer status = status_TYPE_INIT;

    public static final String jobId_FIELDNAME = "jobId";
    @FieldDescribe("프로세스의 jobId, 이의 신청 프로세스 종료 후 기록.")
    @Column( length = JpaObject.length_id, name = ColumnNamePrefix + jobId_FIELDNAME)
    private String jobId;


    public static final String updateStatusAdminPerson_FIELDNAME = "updateStatusAdminPerson";
    @FieldDescribe("관리자 식별자, 최종 처리자")
    @Column(length = length_96B, name = ColumnNamePrefix + updateStatusAdminPerson_FIELDNAME)
    private String updateStatusAdminPerson;



    // 매일 아침 확인 후 알림 메시지 발송
    public static final String sendStatus_FIELDNAME = "sendStatus";
    @FieldDescribe("메시지 발송 여부")
    @Column(name = ColumnNamePrefix + sendStatus_FIELDNAME)
    private Boolean sendStatus = false;


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Boolean getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Boolean sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getUpdateStatusAdminPerson() {
        return updateStatusAdminPerson;
    }

    public void setUpdateStatusAdminPerson(String updateStatusAdminPerson) {
        this.updateStatusAdminPerson = updateStatusAdminPerson;
    }

    
}
