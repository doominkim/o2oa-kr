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
@Schema(name = "AttendanceV2AlertMessage", description = "근태 알림 메시지 객체.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2AlertMessage.table, indexes = {
        @Index(name = PersistenceProperties.AttendanceV2AlertMessage.table + JpaObject.IndexNameMiddle
                + "status_time_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2AlertMessage.sendStatus_FIELDNAME + "," + JpaObject.ColumnNamePrefix
                + AttendanceV2AlertMessage.sendDateTime_FIELDNAME)},
        uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2AlertMessage.table
                + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {
                JpaObject.IDCOLUMN, JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN,
                JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2AlertMessage extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2AlertMessage.table;
    private static final long serialVersionUID = 4469504288529954958L;


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

    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("사용자 식별자")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;


    public static final String sendDateTime_FIELDNAME = "sendDateTime";
    @FieldDescribe("메시지 발송 시간.")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + sendDateTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date sendDateTime;



    public static final String checkInType_FIELDNAME = "checkInType";
    @FieldDescribe("근태 유형: OnDuty|OffDuty")
    @Column(length = length_16B, name = ColumnNamePrefix + checkInType_FIELDNAME)
    private String checkInType;


    // 매일 아침 확인 후 알림 메시지 발송
    public static final String sendStatus_FIELDNAME = "sendStatus";
    @FieldDescribe("메시지 발송 여부")
    @Column(name = ColumnNamePrefix + sendStatus_FIELDNAME)
    private Boolean sendStatus = false;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getSendDateTime() {
        return sendDateTime;
    }

    public void setSendDateTime(Date sendDateTime) {
        this.sendDateTime = sendDateTime;
    }


    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public Boolean getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Boolean sendStatus) {
        this.sendStatus = sendStatus;
    }
}
