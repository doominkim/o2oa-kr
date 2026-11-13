package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.Table;

import javax.persistence.Transient;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Strategy;

// 휴가 한도 지급 규칙 정의.
@Entity
@Schema(name = "AttendanceV2LeavePolicy", description = "휴가 한도 규칙.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.AttendanceV2LeavePolicy.table)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2LeavePolicy extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2LeavePolicy.table;
    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @PostLoad
    public void postLoad() {
        this.grantScript = this.getGrantAmountProperties().getGrantScript();
    }


    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {}

    public static final String leaveTypeId_FIELDNAME = "leaveTypeId";
    @FieldDescribe("휴가 유형 ID")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveTypeId_FIELDNAME)
    private String leaveTypeId;

    public static final String policyName_FIELDNAME = "policyName";
    @FieldDescribe("규칙 이름")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + policyName_FIELDNAME)
    private String policyName;

     public static final String grantScopeType_FIELDNAME = "grantScopeType";
    @FieldDescribe("지급 범위 ALL: 전체  DEPARTMENT: 지정 직원/부서")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantScopeType_FIELDNAME)
    private String grantScopeType;

    public static final String grantScopeList_FIELDNAME = "grantScopeList";
	@FieldDescribe("grantScopeType=DEPARTMENT, 지급 범위 사용자·조직.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = PersistenceProperties.AttendanceV2LeavePolicy.table + ContainerTableNameMiddle
			+ grantScopeList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantScopeList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + grantScopeList_FIELDNAME)
	@ElementIndex(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantScopeList_FIELDNAME + ElementIndexNameSuffix)
	private List<String> grantScopeList;


    public static final String grantExcludeList_FIELDNAME = "grantExcludeList";
    @FieldDescribe("grantScopeType=DEPARTMENT, 지급 제외 사용자.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = PersistenceProperties.AttendanceV2LeavePolicy.table + ContainerTableNameMiddle
                           + grantExcludeList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantExcludeList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + grantExcludeList_FIELDNAME)
    @ElementIndex(name = PersistenceProperties.AttendanceV2LeavePolicy.table + grantExcludeList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> grantExcludeList;

//    public static final String grantType_FIELDNAME = "grantType";
//    @FieldDescribe("지급 방식: YEARLY/MONTHLY/ONE_TIME")
//    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantType_FIELDNAME)
//    private String grantType;
//
//    public static final String grantTypeValue_FIELDNAME = "grantTypeValue";
//    @FieldDescribe("지급 방식 날짜 규칙 설정: Y:01-01/MS:1,ME:1/ONE_TIME")
//    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + grantTypeValue_FIELDNAME)
//    private String grantTypeValue;

    @FieldDescribe("예약 작업 corn 표현식")
    @Column(  length = JpaObject.length_64B, name = ColumnNamePrefix + "grantCron")
    private String grantCron;

    @FieldDescribe("마지막 예약 작업 실행 시간")
    @Column(name = ColumnNamePrefix + "grantLastExecuteTime")
    private Date grantLastExecuteTime;
   
    public static final String grantAmount_FIELDNAME = "grantAmount";
    @FieldDescribe("지급 한도")
    @Column(name = ColumnNamePrefix + grantAmount_FIELDNAME)
    private Double grantAmount = 0.0;

    @FieldDescribe("지급 한도 유형의 스크립트 실행 여부, true: 스크립트 실행, false: grantAmount 필드 값 사용")
    @Column(name = ColumnNamePrefix + "grantAmountTypeUseScript")
    private Boolean grantAmountTypeUseScript = false;
    
	@FieldDescribe("지급 한도 규칙 설정, 실행 스크립트")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + "grantAmountProperties")
	private AttendanceV2LeavePolicyGrantAmountTypeProperties grantAmountProperties;

    public static final String expireType_FIELDNAME = "expireType";
    @FieldDescribe("만료 유형 THIS_YEAR / NEXT_YEAR / AFTER_GRANT")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + expireType_FIELDNAME)
    private String expireType;

    public static final String expireMonthDay_FIELDNAME = "expireMonthDay";
    @FieldDescribe("expireType = THIS_YEAR 또는 NEXT_YEAR일 때 사용, 몇 월 며칠에 만료되는지 표시, 예: \"12-31\", \"03-31\"")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + expireMonthDay_FIELDNAME)
    private String expireMonthDay;

    public static final String expireValue_FIELDNAME = "expireValue";
    @FieldDescribe("expireType = AFTER_GRANT일 때 사용, 지급 후 며칠 뒤 만료되는지 표시, 예: 365")
    @Column(name = ColumnNamePrefix + expireValue_FIELDNAME)
    private Integer expireValue;

    public static final String carryForward_FIELDNAME = "carryForward";
    @FieldDescribe("이월 허용 여부")
    @Column(name = ColumnNamePrefix + carryForward_FIELDNAME)
    private Boolean carryForward = false;

    public static final String maxCarryForward_FIELDNAME = "maxCarryForward";
    @FieldDescribe("최대 이월 한도")
    @Column(name = ColumnNamePrefix + maxCarryForward_FIELDNAME)
    private Double maxCarryForward = 0.0;

    public static final String policyVersion_FIELDNAME = "policyVersion";
    @FieldDescribe("규칙 버전")
    @Column(name = ColumnNamePrefix + policyVersion_FIELDNAME)
    private Integer policyVersion = 1;

    public static final String active_FIELDNAME = "active";
    @FieldDescribe("상태 ENABLED/DISABLED")
    @Column(name = ColumnNamePrefix + active_FIELDNAME)
    private Boolean active = true;




    @FieldDescribe("한도 지급 실행 스크립트.")
    @Transient
    private String grantScript;


    public String getGrantScript() {
        return grantScript;
    }

    public void setGrantScript(String grantScript) {
        this.getGrantAmountProperties().setGrantScript(grantScript);
        this.grantScript = grantScript;
    }

    public String getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(String leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }


    public Double getGrantAmount() {
        return grantAmount;
    }

    public void setGrantAmount(Double grantAmount) {
        this.grantAmount = grantAmount;
    }

    public String getExpireMonthDay() {
        return expireMonthDay;
    }

    public void setExpireMonthDay(String expireMonthDay) {
        this.expireMonthDay = expireMonthDay;
    }

    public String getExpireType() {
        return expireType;
    }

    public void setExpireType(String expireType) {
        this.expireType = expireType;
    }

    public Integer getExpireValue() {
        return expireValue;
    }

    public void setExpireValue(Integer expireValue) {
        this.expireValue = expireValue;
    }

    public Boolean getCarryForward() {
        return carryForward;
    }

    public void setCarryForward(Boolean carryForward) {
        this.carryForward = carryForward;
    }

    public Double getMaxCarryForward() {
        return maxCarryForward;
    }

    public void setMaxCarryForward(Double maxCarryForward) {
        this.maxCarryForward = maxCarryForward;
    }

    public Integer getPolicyVersion() {
        return policyVersion;
    }

    public void setPolicyVersion(Integer policyVersion) {
        this.policyVersion = policyVersion;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getGrantScopeType() {
        return grantScopeType;
    }

    public void setGrantScopeType(String grantScopeType) {
        this.grantScopeType = grantScopeType;
    }

    public List<String> getGrantScopeList() {
        return grantScopeList;
    }

    public void setGrantScopeList(List<String> grantScopeList) {
        this.grantScopeList = grantScopeList;
    }

    public String getGrantCron() {
        return grantCron;
    }

    public void setGrantCron(String grantCron) {
        this.grantCron = grantCron;
    }

    public Date getGrantLastExecuteTime() {
        return grantLastExecuteTime;
    }

    public void setGrantLastExecuteTime(Date grantLastExecuteTime) {
        this.grantLastExecuteTime = grantLastExecuteTime;
    }

    public AttendanceV2LeavePolicyGrantAmountTypeProperties getGrantAmountProperties() {
        if (this.grantAmountProperties == null) {
            this.grantAmountProperties = new AttendanceV2LeavePolicyGrantAmountTypeProperties();
        }
        return grantAmountProperties;
    }

    public void setGrantAmountProperties(
            AttendanceV2LeavePolicyGrantAmountTypeProperties grantAmountProperties) {
        this.grantAmountProperties = grantAmountProperties;
    }

    public List<String> getGrantExcludeList() {
        return grantExcludeList;
    }

    public void setGrantExcludeList(List<String> grantExcludeList) {
        this.grantExcludeList = grantExcludeList;
    }

    public Boolean getGrantAmountTypeUseScript() {
        return grantAmountTypeUseScript;
    }

    public void setGrantAmountTypeUseScript(Boolean grantAmountTypeUseScript) {
        this.grantAmountTypeUseScript = grantAmountTypeUseScript;
    }
}
