package com.x.attendance.entity.v2;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;

/**
 * 근무조
 */
@Schema(name = "AttendanceV2Shift", description = "근무조.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table, uniqueConstraints = {
		@UniqueConstraint(name = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Shift extends SliceJpaObject {


	private static final String TABLE = com.x.attendance.entity.PersistenceProperties.AttendanceV2Shift.table;
	private static final long serialVersionUID = 4442626785709902731L;

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
	public static final String shiftName_FIELDNAME = "shiftName";
	@FieldDescribe("근무조 이름")
	@Column(name = ColumnNamePrefix
                   + shiftName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String shiftName;


	public static final String operator_FIELDNAME = "operator";
	@FieldDescribe("최종 처리자")
	@Column(name = ColumnNamePrefix
                   + operator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String operator;


	public static final String PROPERTIES_FIELDNAME = "properties";
	@FieldDescribe("근무조 출퇴근 체크 시간.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private AttendanceV2ShiftCheckTimeProperties properties;

	public static final String workTime_FIELDNAME = "workTime";
	@FieldDescribe("근무 시간(분).")
	@Column(name = ColumnNamePrefix + workTime_FIELDNAME)
	private Integer workTime; // 근무 시간(분), 예: 480

	public static final String needLimitWorkTime_FIELDNAME = "needLimitWorkTime";
	@FieldDescribe("근무 시간 부족 시 조퇴 처리 여부.")
	@Column(name = ColumnNamePrefix + needLimitWorkTime_FIELDNAME)
	private Boolean needLimitWorkTime = true;

	public static final String seriousTardinessLateTime_FIELDNAME = "seriousTardinessLateMinutes";
	@FieldDescribe("심각 지각 분 수.")
	@Column(name = ColumnNamePrefix + seriousTardinessLateTime_FIELDNAME)
	private Integer seriousTardinessLateMinutes; // 심각 지각 분 수, 예: 30

	public static final String absenteeismLateTime_FIELDNAME = "absenteeismLateMinutes";
	@FieldDescribe("결근 처리 지각 분 수.")
	@Column(name = ColumnNamePrefix + absenteeismLateTime_FIELDNAME)
	private Integer absenteeismLateMinutes; // 결근 처리 지각 분 수, 예: 30


	public static final String lateAndEarlyOnTime_FIELDNAME = "lateAndEarlyOnTime";
	@FieldDescribe("출근 최대 허용 지각 시간")
	@Column(name = ColumnNamePrefix
                   + lateAndEarlyOnTime_FIELDNAME)
	private String lateAndEarlyOnTime; // 출근 최대 허용 지각 시간 - 출근이 몇 분 늦으면 퇴근도 몇 분 늦게 해야 함. 예: 30은 30분

	public static final String lateAndEarlyOffTime_FIELDNAME = "lateAndEarlyOffTime";
	@FieldDescribe("퇴근 최대 허용 조기 퇴근 시간")
	@Column(name = ColumnNamePrefix
                   + lateAndEarlyOffTime_FIELDNAME)
	private String lateAndEarlyOffTime; // 퇴근 최대 허용 조기 퇴근 시간, 출근을 몇 분 일찍 하면 퇴근을 몇 분 일찍 할 수 있음. 예: 90은 1시간 30분


	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getShiftName() {
		return shiftName;
	}

	public void setShiftName(String shiftName) {
		this.shiftName = shiftName;
	}

	public AttendanceV2ShiftCheckTimeProperties getProperties() {
		return properties;
	}

	public void setProperties(AttendanceV2ShiftCheckTimeProperties properties) {
		this.properties = properties;
	}

	public Integer getSeriousTardinessLateMinutes() {
		return seriousTardinessLateMinutes;
	}

	public void setSeriousTardinessLateMinutes(Integer seriousTardinessLateMinutes) {
		this.seriousTardinessLateMinutes = seriousTardinessLateMinutes;
	}

	public Integer getAbsenteeismLateMinutes() {
		return absenteeismLateMinutes;
	}

	public void setAbsenteeismLateMinutes(Integer absenteeismLateMinutes) {
		this.absenteeismLateMinutes = absenteeismLateMinutes;
	}

	public String getLateAndEarlyOnTime() {
		return lateAndEarlyOnTime;
	}

	public void setLateAndEarlyOnTime(String lateAndEarlyOnTime) {
		this.lateAndEarlyOnTime = lateAndEarlyOnTime;
	}

	public String getLateAndEarlyOffTime() {
		return lateAndEarlyOffTime;
	}

	public void setLateAndEarlyOffTime(String lateAndEarlyOffTime) {
		this.lateAndEarlyOffTime = lateAndEarlyOffTime;
	}

  public Integer getWorkTime() {
    return workTime == null ? 0 : workTime;
  }

  public void setWorkTime(Integer workTime) {
    this.workTime = workTime;
  }

  public Boolean getNeedLimitWorkTime() {
    return needLimitWorkTime;
  }

  public void setNeedLimitWorkTime(Boolean needLimitWorkTime) {
    this.needLimitWorkTime = needLimitWorkTime;
  }

	

}