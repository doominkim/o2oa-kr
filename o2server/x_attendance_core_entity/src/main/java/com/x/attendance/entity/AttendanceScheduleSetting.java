package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceScheduleSetting", description = "근태 예약 작업 설정.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceScheduleSetting.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceScheduleSetting.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Table(name = PersistenceProperties.AttendanceScheduleSetting.table)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceScheduleSetting extends SliceJpaObject {

	private static final long serialVersionUID = 4555094494086574586L;
	private static final String TABLE = PersistenceProperties.AttendanceScheduleSetting.table;

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
	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("최상위 조직 이름 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + topUnitName_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("조직 이름 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitName_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName;

	public static final String unitOu_FIELDNAME = "unitOu";
	@FieldDescribe("조직 번호 distinguishedName")
	@Column( length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix + unitOu_FIELDNAME )
	@Index(name = TABLE + IndexNameMiddle + unitOu_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitOu;

	public static final String onDutyTime_FIELDNAME = "onDutyTime";
	@FieldDescribe("오전 출근 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + onDutyTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String onDutyTime;

	public static final String middayRestStartTime_FIELDNAME = "middayRestStartTime";
	@FieldDescribe("점심시간 시작|오전 퇴근 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String middayRestStartTime;

	public static final String middayRestEndTime_FIELDNAME = "middayRestEndTime";
	@FieldDescribe("점심시간 종료|오후 출근 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestEndTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String middayRestEndTime;

	public static final String offDutyTime_FIELDNAME = "offDutyTime";
	@FieldDescribe("오후 퇴근 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + offDutyTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String offDutyTime;

	public static final String signProxy_FIELDNAME = "signProxy";
	@FieldDescribe("기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근 기록)")
	@Column( name = ColumnNamePrefix + signProxy_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private Integer signProxy = 0;

	public static final String lateStartTime_FIELDNAME = "lateStartTime";
	@FieldDescribe("오전 출근 지각 기준 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + lateStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String lateStartTime;

	public static final String lateStartTimeAfternoon_FIELDNAME = "lateStartTimeAfternoon";
	@FieldDescribe("오후 출근 지각 기준 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + lateStartTimeAfternoon_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String lateStartTimeAfternoon;

	public static final String absenceStartTime_FIELDNAME = "absenceStartTime";
	@FieldDescribe("결근 기준 시간: 첫 체크가 몇 시 이후면 당일 결근으로 처리")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + absenceStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String absenceStartTime;

	public static final String leaveEarlyStartTime_FIELDNAME = "leaveEarlyStartTime";
	@FieldDescribe("오후 조퇴 기준 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveEarlyStartTime_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String leaveEarlyStartTime;

	public static final String leaveEarlyStartTimeMorning_FIELDNAME = "leaveEarlyStartTimeMorning";
	@FieldDescribe("오전 조퇴 기준 시간")
	@Column( length = JpaObject.length_32B, name = ColumnNamePrefix + leaveEarlyStartTimeMorning_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String leaveEarlyStartTimeMorning;

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

	public String getOnDutyTime() { return onDutyTime; }

	public void setOnDutyTime(String onDutyTime) { this.onDutyTime = onDutyTime; }

	public String getMiddayRestStartTime() { return middayRestStartTime; }

	public void setMiddayRestStartTime(String middayRestStartTime) { this.middayRestStartTime = middayRestStartTime; }

	public String getMiddayRestEndTime() { return middayRestEndTime; }

	public void setMiddayRestEndTime(String middayRestEndTime) { this.middayRestEndTime = middayRestEndTime; }

	public Integer getSignProxy() { return signProxy == null? 1:signProxy; }

	public void setSignProxy(Integer signProxy) { this.signProxy = signProxy; }

	public String getOffDutyTime() { return offDutyTime; }

	public void setOffDutyTime(String offDutyTime) { this.offDutyTime = offDutyTime; }

	public String getLateStartTime() {
		return lateStartTime;
	}

	public void setLateStartTime(String lateStartTime) {
		this.lateStartTime = lateStartTime;
	}

	public String getAbsenceStartTime() {
		return absenceStartTime;
	}

	public void setAbsenceStartTime(String absenceStartTime) {
		this.absenceStartTime = absenceStartTime;
	}

	public String getLeaveEarlyStartTime() {
		return leaveEarlyStartTime;
	}

	public void setLeaveEarlyStartTime(String leaveEarlyStartTime) {
		this.leaveEarlyStartTime = leaveEarlyStartTime;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getLateStartTimeAfternoon() { return lateStartTimeAfternoon; }

	public void setLateStartTimeAfternoon(String lateStartTimeAfternoon) { this.lateStartTimeAfternoon = lateStartTimeAfternoon; }

	public String getLeaveEarlyStartTimeMorning() { return leaveEarlyStartTimeMorning; }

	public void setLeaveEarlyStartTimeMorning(String leaveEarlyStartTimeMorning) { this.leaveEarlyStartTimeMorning = leaveEarlyStartTimeMorning; }
}