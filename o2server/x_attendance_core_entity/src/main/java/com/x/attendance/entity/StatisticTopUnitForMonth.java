package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StatisticTopUnitForMonth", description = "근태 최상위 조직 월별 통계.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.StatisticTopUnitForMonth.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.StatisticTopUnitForMonth.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class StatisticTopUnitForMonth extends SliceJpaObject {

	private static final long serialVersionUID = 2327868796380781985L;
	private static final String TABLE = PersistenceProperties.StatisticTopUnitForMonth.table;

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
		String topUnitName = null;
		if (this.topUnitName != null) {
			topUnitName = this.topUnitName.split("@")[0];
		} else {
			topUnitName = this.topUnitName;
		}
		this.setSequence(StringUtils.join(this.statisticYear + this.statisticMonth, topUnitName, this.getId()));
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
	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("최상위 조직명")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String topUnitName;

	public static final String statisticYear_FIELDNAME = "statisticYear";
	@FieldDescribe("통계 연도")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + statisticYear_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String statisticYear;

	public static final String statisticMonth_FIELDNAME = "statisticMonth";
	@FieldDescribe("통계 월")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + statisticMonth_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String statisticMonth;

	public static final String employeeCount_FIELDNAME = "employeeCount";
	@FieldDescribe("출근 대상 인일수")
	@Column(name = ColumnNamePrefix + employeeCount_FIELDNAME)
	private Double employeeCount;

	public static final String onDutyEmployeeCount_FIELDNAME = "onDutyEmployeeCount";
	@FieldDescribe("실제 출근 인일수")
	@Column(name = ColumnNamePrefix + onDutyEmployeeCount_FIELDNAME)
	private Double onDutyEmployeeCount;

	public static final String absenceDayCount_FIELDNAME = "absenceDayCount";
	@FieldDescribe("결근 인일수")
	@Column(name = ColumnNamePrefix + absenceDayCount_FIELDNAME)
	private Double absenceDayCount;

	public static final String onSelfHolidayCount_FIELDNAME = "onSelfHolidayCount";
	@FieldDescribe("휴가 인일수")
	@Column(name = ColumnNamePrefix + onSelfHolidayCount_FIELDNAME)
	private Double onSelfHolidayCount;

	public static final String onDutyCount_FIELDNAME = "onDutyCount";
	@FieldDescribe("출근 체크 인원")
	@Column(name = ColumnNamePrefix + onDutyCount_FIELDNAME)
	private Long onDutyCount;

	public static final String offDutyCount_FIELDNAME = "offDutyCount";
	@FieldDescribe("퇴근 체크 인원")
	@Column(name = ColumnNamePrefix + offDutyCount_FIELDNAME)
	private Long offDutyCount;

	public static final String lateCount_FIELDNAME = "lateCount";
	@FieldDescribe("지각 인원")
	@Column(name = ColumnNamePrefix + lateCount_FIELDNAME)
	private Long lateCount;

	public static final String leaveEarlyCount_FIELDNAME = "leaveEarlyCount";
	@FieldDescribe("조퇴 인원")
	@Column(name = ColumnNamePrefix + leaveEarlyCount_FIELDNAME)
	private Long leaveEarlyCount;

	public static final String lackOfTimeCount_FIELDNAME = "lackOfTimeCount";
	@FieldDescribe("근무 시간 부족 인원 횟수")
	@Column(name = ColumnNamePrefix + lackOfTimeCount_FIELDNAME)
	private Long lackOfTimeCount;

	public static final String abNormalDutyCount_FIELDNAME = "abNormalDutyCount";
	@FieldDescribe("비정상 체크 인원 횟수")
	@Column(name = ColumnNamePrefix + abNormalDutyCount_FIELDNAME)
	private Long abNormalDutyCount;

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getStatisticYear() {
		return statisticYear;
	}

	public void setStatisticYear(String statisticYear) {
		this.statisticYear = statisticYear;
	}

	public String getStatisticMonth() {
		return statisticMonth;
	}

	public void setStatisticMonth(String statisticMonth) {
		this.statisticMonth = statisticMonth;
	}

	public Double getEmployeeCount() {
		return employeeCount;
	}

	public void setEmployeeCount(Double employeeCount) {
		this.employeeCount = employeeCount;
	}

	public Double getOnDutyEmployeeCount() {
		return onDutyEmployeeCount;
	}

	public void setOnDutyEmployeeCount(Double onDutyEmployeeCount) {
		this.onDutyEmployeeCount = onDutyEmployeeCount;
	}

	public Double getAbsenceDayCount() {
		return absenceDayCount;
	}

	public void setAbsenceDayCount(Double absenceDayCount) {
		this.absenceDayCount = absenceDayCount;
	}

	public Double getOnSelfHolidayCount() {
		return onSelfHolidayCount;
	}

	public void setOnSelfHolidayCount(Double onSelfHolidayCount) {
		this.onSelfHolidayCount = onSelfHolidayCount;
	}

	public Long getOnDutyCount() {
		return onDutyCount;
	}

	public void setOnDutyCount(Long onDutyCount) {
		this.onDutyCount = onDutyCount;
	}

	public Long getOffDutyCount() {
		return offDutyCount;
	}

	public void setOffDutyCount(Long offDutyCount) {
		this.offDutyCount = offDutyCount;
	}

	public Long getLateCount() {
		return lateCount;
	}

	public void setLateCount(Long lateCount) {
		this.lateCount = lateCount;
	}

	public Long getLeaveEarlyCount() {
		return leaveEarlyCount;
	}

	public void setLeaveEarlyCount(Long leaveEarlyCount) {
		this.leaveEarlyCount = leaveEarlyCount;
	}

	public Long getLackOfTimeCount() {
		return lackOfTimeCount;
	}

	public void setLackOfTimeCount(Long lackOfTimeCount) {
		this.lackOfTimeCount = lackOfTimeCount;
	}

	public Long getAbNormalDutyCount() {
		return abNormalDutyCount;
	}

	public void setAbNormalDutyCount(Long abNormalDutyCount) {
		this.abNormalDutyCount = abNormalDutyCount;
	}

}