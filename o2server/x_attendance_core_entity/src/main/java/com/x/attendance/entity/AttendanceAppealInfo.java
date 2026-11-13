package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceAppealInfo", description = "근태 이의 신청 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceAppealInfo.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceAppealInfo.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceAppealInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AttendanceAppealInfo.table;

	public AttendanceAppealInfo() {
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
	public static final String detailId_FIELDNAME = "detailId";
	@FieldDescribe("이의 신청 출퇴근 기록 ID.")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + detailId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String detailId;

	public static final String empName_FIELDNAME = "empName";
	@FieldDescribe("이의 신청 직원 식별자:distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + empName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String empName;

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("직원 소속 최상위 조직 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME)
	private String topUnitName;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("직원 소속 조직 이름 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String unitName;

	public static final String recordDate_FIELDNAME = "recordDate";
	@FieldDescribe("기록 일자")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + recordDate_FIELDNAME)
	private Date recordDate;

	public static final String yearString_FIELDNAME = "yearString";
	@FieldDescribe("이의 신청 연도")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + yearString_FIELDNAME)
	private String yearString;

	public static final String monthString_FIELDNAME = "monthString";
	@FieldDescribe("이의 신청 월")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + monthString_FIELDNAME)
	private String monthString;

	public static final String appealDateString_FIELDNAME = "appealDateString";
	@FieldDescribe("이의 신청 일자 문자열")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + appealDateString_FIELDNAME)
	private String appealDateString;

	public static final String recordDateString_FIELDNAME = "recordDateString";
	@FieldDescribe("기록 일자 문자열")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
	private String recordDateString;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("이의 신청 시작 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + startTime_FIELDNAME)
	private String startTime;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("이의 신청 종료 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + endTime_FIELDNAME)
	private String endTime;

	public static final String appealReason_FIELDNAME = "appealReason";
	@FieldDescribe("이의 신청 사유 요약(60자)")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + appealReason_FIELDNAME)
	private String appealReason;

	public static final String selfHolidayType_FIELDNAME = "selfHolidayType";
	@FieldDescribe("휴가 유형")
	@Column( length = JpaObject.length_64B, name = ColumnNamePrefix + selfHolidayType_FIELDNAME)
	private String selfHolidayType;

	public static final String address_FIELDNAME = "address";
	@FieldDescribe("주소")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + address_FIELDNAME )
	private String address;

	public static final String reason_FIELDNAME = "reason";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("이의 신청 상세 사유")
	@Column( length = JpaObject.length_2K, name = ColumnNamePrefix + reason_FIELDNAME)
	private String reason;

	public static final String appealDescription_FIELDNAME = "appealDescription";
	@Lob
	@FieldDescribe("이의 신청 상세 설명")
	@Column( length = JpaObject.length_2K, name = ColumnNamePrefix + appealDescription_FIELDNAME)
	private String appealDescription;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("승인 상태: 0-처리 대기, 1-승인, -1-반려, 2-다음 승인 필요")
	@Column( name = ColumnNamePrefix + status_FIELDNAME)
	private Integer status = 0;

	public static final String currentProcessor_FIELDNAME = "currentProcessor";
	@FieldDescribe("현재 심사자")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + currentProcessor_FIELDNAME)
	private String currentProcessor;

	public static final String archiveTime_FIELDNAME = "archiveTime";
	@FieldDescribe("보관 시간 문자열")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + archiveTime_FIELDNAME )
	private String archiveTime;

//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPerson1_FIELDNAME = "processPerson1";
////	@FieldDescribe("승인자 1")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPerson1_FIELDNAME)
//	private String processPerson1;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPersonUnit1_FIELDNAME = "processPersonUnit1";
////	@FieldDescribe("승인자 조직 1")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPersonUnit1_FIELDNAME)
//	private String processPersonUnit1;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPersonTopUnit1_FIELDNAME = "processPersonTopUnit1";
////	@FieldDescribe("승인자 최상위 조직 1")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPersonTopUnit1_FIELDNAME)
//	private String processPersonTopUnit1;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String opinion1_FIELDNAME = "opinion1";
////	@FieldDescribe("승인 의견 1")
//	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + opinion1_FIELDNAME )
//	private String opinion1;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processTime1_FIELDNAME = "processTime1";
////	@FieldDescribe("승인 일자 1")
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column( name = ColumnNamePrefix + processTime1_FIELDNAME)
//	private Date processTime1;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPerson2_FIELDNAME = "processPerson2";
////	@FieldDescribe("승인자 2")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPerson2_FIELDNAME)
//	private String processPerson2;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPersonUnit2_FIELDNAME = "processPersonUnit2";
////	@FieldDescribe("승인자 조직 2")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPersonUnit2_FIELDNAME )
//	private String processPersonUnit2;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processPersonTopUnit2_FIELDNAME = "processPersonTopUnit2";
////	@FieldDescribe("승인자 최상위 조직 2")
//	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + processPersonTopUnit2_FIELDNAME )
//	private String processPersonTopUnit2;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String opinion2_FIELDNAME = "opinion2";
////	@FieldDescribe("승인 의견 2")
//	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + opinion2_FIELDNAME )
//	private String opinion2;
//
//	//2020-05-15 새 버전에서 이 속성 폐지, 승인 정보는 AttendanceAppealAuditInfo에 기록
//	public static final String processTime2_FIELDNAME = "processTime2";
////	@FieldDescribe("승인 일자 2")
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column( name = ColumnNamePrefix + processTime2_FIELDNAME )
//	private Date processTime2;

	public String getDetailId() {
		return detailId;
	}

	public void setDetailId(String detailId) {
		this.detailId = detailId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getYearString() {
		return yearString;
	}

	public void setYearString(String yearString) {
		this.yearString = yearString;
	}

	public String getMonthString() {
		return monthString;
	}

	public void setMonthString(String monthString) {
		this.monthString = monthString;
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAppealReason() {
		return appealReason;
	}

	public void setAppealReason(String appealReason) {
		this.appealReason = appealReason;
	}

	public String getAppealDescription() {
		return appealDescription;
	}

	public void setAppealDescription(String appealDescription) {
		this.appealDescription = appealDescription;
	}

	public String getCurrentProcessor() {
		return currentProcessor;
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

	public void setCurrentProcessor(String currentProcessor) {
		this.currentProcessor = currentProcessor;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSelfHolidayType() {
		return selfHolidayType;
	}

	public void setSelfHolidayType(String selfHolidayType) {
		this.selfHolidayType = selfHolidayType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getArchiveTime() {
		return archiveTime;
	}

	public void setArchiveTime(String archiveTime) {
		this.archiveTime = archiveTime;
	}

	public String getAppealDateString() {
		return appealDateString;
	}

	public void setAppealDateString(String appealDateString) {
		this.appealDateString = appealDateString;
	}

//	public String getProcessPerson1() {
//		return processPerson1;
//	}
//
//	public void setProcessPerson1(String processPerson1) {
//		this.processPerson1 = processPerson1;
//	}
//
//	public String getProcessPersonUnit1() {
//		return processPersonUnit1;
//	}
//
//	public void setProcessPersonUnit1(String processPersonUnit1) {
//		this.processPersonUnit1 = processPersonUnit1;
//	}
//
//	public String getProcessPersonTopUnit1() {
//		return processPersonTopUnit1;
//	}
//
//	public void setProcessPersonTopUnit1(String processPersonTopUnit1) { this.processPersonTopUnit1 = processPersonTopUnit1; }
//
//	public String getOpinion1() {
//		return opinion1;
//	}
//
//	public void setOpinion1(String opinion1) {
//		this.opinion1 = opinion1;
//	}
//
//	public Date getProcessTime1() {
//		return processTime1;
//	}
//
//	public void setProcessTime1(Date processTime1) {
//		this.processTime1 = processTime1;
//	}
//
//	public String getProcessPerson2() {
//		return processPerson2;
//	}
//
//	public void setProcessPerson2(String processPerson2) {
//		this.processPerson2 = processPerson2;
//	}
//
//	public String getProcessPersonUnit2() {
//		return processPersonUnit2;
//	}
//
//	public void setProcessPersonUnit2(String processPersonUnit2) {
//		this.processPersonUnit2 = processPersonUnit2;
//	}
//
//	public String getProcessPersonTopUnit2() {
//		return processPersonTopUnit2;
//	}
//
//	public void setProcessPersonTopUnit2(String processPersonTopUnit2) { this.processPersonTopUnit2 = processPersonTopUnit2; }
//
//	public String getOpinion2() {
//		return opinion2;
//	}
//
//	public void setOpinion2(String opinion2) {
//		this.opinion2 = opinion2;
//	}
//
//	public Date getProcessTime2() {
//		return processTime2;
//	}
//
//	public void setProcessTime2(Date processTime2) {
//		this.processTime2 = processTime2;
//	}
}