package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;


// 휴가 잔여량의 핵심 테이블.
@Entity
@Schema(name = "AttendanceV2LeaveLedger", description = "휴가 한도 배치.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeaveLedger.table, indexes = {
        @Index(name = PersistenceProperties.AttendanceV2LeaveLedger.table + JpaObject.IndexNameMiddle
                + "person_type_active_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2LeaveLedger.person_FIELDNAME + "," + JpaObject.ColumnNamePrefix
                + AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME + "," + JpaObject.ColumnNamePrefix
                + AttendanceV2LeaveLedger.active_FIELDNAME),
        @Index(name = PersistenceProperties.AttendanceV2LeaveLedger.table + JpaObject.IndexNameMiddle
                + "active_expire_IDX", columnList = JpaObject.ColumnNamePrefix
                + AttendanceV2LeaveLedger.active_FIELDNAME + "," + JpaObject.ColumnNamePrefix
                + AttendanceV2LeaveLedger.expireTime_FIELDNAME),
        @Index(name = PersistenceProperties.AttendanceV2LeaveLedger.table + JpaObject.IndexNameMiddle
                + "person_IDX", columnList = JpaObject.ColumnNamePrefix + AttendanceV2LeaveLedger.person_FIELDNAME),
        @Index(name = PersistenceProperties.AttendanceV2LeaveLedger.table + JpaObject.IndexNameMiddle
                + "type_IDX", columnList = JpaObject.ColumnNamePrefix + AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME)},
        uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceV2LeaveLedger.table
                + JpaObject.IndexNameMiddle + "person_type_period_UNIQUE", columnNames = {
                JpaObject.ColumnNamePrefix + AttendanceV2LeaveLedger.person_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2LeaveLedger.grantPeriod_FIELDNAME}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeaveLedger extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2LeaveLedger.table;
    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {}

    public static final String person_FIELDNAME = "person";
    @FieldDescribe("사용자")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + person_FIELDNAME)
    private String person;

    public static final String leaveTypeId_FIELDNAME = "leaveTypeId";
    @FieldDescribe("휴가 유형")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveTypeId_FIELDNAME)
    private String leaveTypeId;

    public static final String policyId_FIELDNAME = "policyId";
    @FieldDescribe("규칙 ID")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + policyId_FIELDNAME)
    private String policyId;

    public static final String grantPeriod_FIELDNAME = "grantPeriod";
    @FieldDescribe("지급 주기, 규칙의 지급 방식에 따름. 연 단위면 yyyy, 월 단위면 yyyy-MM, 일회성 지급이면 일회성")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + grantPeriod_FIELDNAME)
    private String grantPeriod;

    public static final String grantAmount_FIELDNAME = "grantAmount";
    @FieldDescribe("지급 한도")
    @Column(name = ColumnNamePrefix + grantAmount_FIELDNAME)
    private Double grantAmount = 0.0;

    public static final String usedAmount_FIELDNAME = "usedAmount";
    @FieldDescribe("사용 한도")
    @Column(name = ColumnNamePrefix + usedAmount_FIELDNAME)
    private Double usedAmount = 0.0;

    public static final String remainingAmount_FIELDNAME = "remainingAmount";
    @FieldDescribe("잔여 한도")
    @Column(name = ColumnNamePrefix + remainingAmount_FIELDNAME)
    private Double remainingAmount = 0.0;

    public static final String grantTime_FIELDNAME = "grantTime";
    @FieldDescribe("지급 시간")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + grantTime_FIELDNAME)
    private Date grantTime;


    public static final String expireTime_FIELDNAME = "expireTime";
    @FieldDescribe("만료 시간")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
    private Date expireTime;

    public static final String active_FIELDNAME = "active";
    @FieldDescribe("상태")
    @Column(name = ColumnNamePrefix + active_FIELDNAME)
    private Boolean active = true;


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

    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

    public Double getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(Double usedAmount) {
        this.usedAmount = usedAmount;
    }

    public Double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(Double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public String getGrantPeriod() {
        return grantPeriod;
    }

    public void setGrantPeriod(String grantPeriod) {
        this.grantPeriod = grantPeriod;
    }
    
}
