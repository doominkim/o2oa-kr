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

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceSelfHoliday", description = "근태 휴일 설정.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceSelfHoliday.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceSelfHoliday.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceSelfHoliday extends SliceJpaObject {

	private static final long serialVersionUID = 2742632629888741120L;
	private static final String TABLE = PersistenceProperties.AttendanceSelfHoliday.table;

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
//	public static final String docId_FIELDNAME = "docId";
//	@FieldDescribe("프로세스 WorkId")
//	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + docId_FIELDNAME )
//	@CheckPersist(allowEmpty = true)
//	private String docId;

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("최상위 조직명")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	public static final String topUnitOu_FIELDNAME = "topUnitOu";
	@FieldDescribe("최상위 조직 번호")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitOu_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String topUnitOu;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("조직명")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String unitOu_FIELDNAME = "unitOu";
	@FieldDescribe("조직 번호")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitOu_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	public static final String employeeName_FIELDNAME = "employeeName";
	@FieldDescribe("직원 이름: 직원 식별자, distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + employeeName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String employeeName;

	public static final String employeeNumber_FIELDNAME = "employeeNumber";
	@FieldDescribe("사번, 사번이 없으면 직원 식별자로 대체 가능, 비울 수 없음")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + employeeNumber_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String employeeNumber;

	public static final String leaveType_FIELDNAME = "leaveType";
	@FieldDescribe("휴가 유형:유급 연차|유급 병가|유급 복지 휴가|무급 개인 휴가|출장|교육|기타")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveType_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String leaveType;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("시작 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + startTime_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("종료 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = ColumnNamePrefix + endTime_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private Date endTime;

	public static final String leaveDayNumber_FIELDNAME = "leaveDayNumber";
	@FieldDescribe("휴가 일수")
	@Column( name = ColumnNamePrefix + leaveDayNumber_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Double leaveDayNumber = 0.0;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("휴가 설명")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String batchFlag_FIELDNAME = "batchFlag";
	@FieldDescribe("입력 배치 식별자: 프로세스 workId, jobId, CMS 문서 ID 또는 사용자 정의 정보 입력 가능. 저장 시 batchFlag 기준으로 먼저 삭제한 뒤 새 데이터 저장")
	@Column( length = JpaObject.length_id, name = ColumnNamePrefix + batchFlag_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String batchFlag;

	public String getBatchFlag() { return this.batchFlag; }

	public void setBatchFlag(final String batchFlag) { this.batchFlag = batchFlag; }

	public String getUnitName() {
		return unitName;
	}

	public String getUnitOu() {
		return unitOu;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public void setUnitOu(String unitOu) {
		this.unitOu = unitOu;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Double getLeaveDayNumber() {
		return leaveDayNumber;
	}

	public void setLeaveDayNumber(Double leaveDayNumber) {
		this.leaveDayNumber = leaveDayNumber;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getTopUnitOu() {
		return topUnitOu;
	}

	public void setTopUnitOu(String topUnitOu) {
		this.topUnitOu = topUnitOu;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
//
//	public String getDocId() {
//		return docId;
//	}
//
//	public void setDocId(String docId) {
//		this.docId = docId;
//	}

}