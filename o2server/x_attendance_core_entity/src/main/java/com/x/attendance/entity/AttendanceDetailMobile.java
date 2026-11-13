package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceDetailMobile", description = "모바일 근태 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceDetailMobile.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetailMobile.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetailMobile extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceDetailMobile.table;

	public AttendanceDetailMobile() {
	}

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
		this.setSequence(StringUtils.join(this.empName, this.recordDateString, this.getId()));
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
	public static final String empNo_FIELDNAME = "empNo";
	@FieldDescribe("사번")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + empNo_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String empNo;

	public static final String empName_FIELDNAME = "empName";
	@FieldDescribe("직원 이름 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + empName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String empName;

	public static final String recordDateString_FIELDNAME = "recordDateString";
	@FieldDescribe("출퇴근 기록 일자 문자열")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String recordDateString;

	public static final String recordDate_FIELDNAME = "recordDate";
	@FieldDescribe("체크 기록 날짜")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + recordDate_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Date recordDate;

	public static final String CHECKIN_TYPE_ONDUTY = "오전 출근 체크";
	public static final String CHECKIN_TYPE_MORNING_OFFDUTY = "오전 퇴근 체크";
	public static final String CHECKIN_TYPE_AFTERNOON_ONDUTY = "오후 출근 체크";
	public static final String CHECKIN_TYPE_OFFDUTY = "오후 퇴근 체크";
	public static final String CHECKIN_TYPE_AFTERNOON = "점심 체크";
	//	public static final String CHECKIN_TYPE_OUTSIDE = "외출 체크";

	public static final String checkin_type_FIELDNAME = "checkin_type";
	@FieldDescribe("체크 유형. 문자열이며 현재 오전 출근 체크, 오전 퇴근 체크, 오후 출근 체크, 오후 퇴근 체크, 외근 체크, 점심 체크가 있음")
	@Column(name = ColumnNamePrefix + checkin_type_FIELDNAME, length = length_32B)
	private String checkin_type;

	public static final String checkin_time_FIELDNAME = "checkin_time";
	@FieldDescribe("출퇴근 체크 시간. Unix 타임스탬프")
	@Column(name = ColumnNamePrefix + checkin_time_FIELDNAME )
	private long checkin_time;

	public static final String signTime_FIELDNAME = "signTime";
	@FieldDescribe("체크 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + signTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String signTime;

	public static final String signDescription_FIELDNAME = "signDescription";
	@FieldDescribe("출퇴근 체크 설명")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + signDescription_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String signDescription;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("기타 설명 비고")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String recordAddress_FIELDNAME = "recordAddress";
	@FieldDescribe("출퇴근 체크 장소 설명")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + recordAddress_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String recordAddress;

	public static final String longitude_FIELDNAME = "longitude";
	@FieldDescribe("경도")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String longitude;

	public static final String latitude_FIELDNAME = "latitude";
	@FieldDescribe("위도")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String latitude;

	public static final String optMachineType_FIELDNAME = "optMachineType";
	@FieldDescribe("조작 기기 유형: 휴대폰 브랜드|PAD|PC|기타")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + optMachineType_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String optMachineType;

	public static final String optSystemName_FIELDNAME = "optSystemName";
	@FieldDescribe("조작 기기 유형: Mac|Windows|IOS|Android|기타")
	@Column( length = JpaObject.length_128B, name = ColumnNamePrefix + optSystemName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String optSystemName;

	public static final String recordStatus_FIELDNAME = "recordStatus";
	@FieldDescribe("기록 상태: 0-미분석 1-분석 완료")
	@Column( name = ColumnNamePrefix + recordStatus_FIELDNAME )
	private Integer recordStatus = 0;

	public static final String workAddress_FIELDNAME = "workAddress";
	@FieldDescribe("출퇴근 체크 장소 설명")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + workAddress_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String workAddress;

	public static final String isExternal_FIELDNAME = "isExternal";
	@FieldDescribe("범위 밖 기록 여부")
	@Column(name = ColumnNamePrefix + isExternal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isExternal = false;

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
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

	public String getSignTime() {
		return signTime;
	}

	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}

	public String getSignDescription() {
		return signDescription;
	}

	public void setSignDescription(String signDescription) {
		this.signDescription = signDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecordAddress() {
		return recordAddress;
	}

	public void setRecordAddress(String recordAddress) {
		this.recordAddress = recordAddress;
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

	public String getOptMachineType() {
		return optMachineType;
	}

	public void setOptMachineType(String optMachineType) {
		this.optMachineType = optMachineType;
	}

	public String getOptSystemName() {
		return optSystemName;
	}

	public void setOptSystemName(String optSystemName) {
		this.optSystemName = optSystemName;
	}

	public Integer getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(Integer recordStatus) {
		this.recordStatus = recordStatus;
	}

	public String getCheckin_type() { return checkin_type; }

	public void setCheckin_type(String checkin_type) { this.checkin_type = checkin_type; }

	public long getCheckin_time() { return checkin_time; }

	public void setCheckin_time(long checkin_time) { this.checkin_time = checkin_time; }

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public Boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(Boolean isExternal) {
		this.isExternal = isExternal;
	}
}