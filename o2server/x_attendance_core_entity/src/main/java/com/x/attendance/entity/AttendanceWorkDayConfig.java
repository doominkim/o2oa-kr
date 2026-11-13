package com.x.attendance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceWorkDayConfig", description = "근태 통계 요청 로그.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceWorkDayConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceWorkDayConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceWorkDayConfig extends SliceJpaObject {

	private static final long serialVersionUID = -7226058083321275722L;
	private static final String TABLE = PersistenceProperties.AttendanceWorkDayConfig.table;

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
	public static final String configName_FIELDNAME = "configName";
	@FieldDescribe("설정 항목 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + configName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configName = "";

	public static final String configYear_FIELDNAME = "configYear";
	@FieldDescribe("설정 연도")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + configYear_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configYear = "2016";

	public static final String configMonth_FIELDNAME = "configMonth";
	@FieldDescribe("설정 월")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + configMonth_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configMonth = "";

	public static final String configDate_FIELDNAME = "configDate";
	@FieldDescribe("설정 날짜")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + configDate_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configDate = "";

	public static final String configType_FIELDNAME = "configType";
	@FieldDescribe("설정 유형: Holiday|Workday")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + configType_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configType = "Holiday";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설정 설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public String getConfigDate() {
		return configDate;
	}

	public void setConfigDate(String configDate) {
		this.configDate = configDate;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getConfigYear() {
		return configYear;
	}

	public void setConfigYear(String configYear) {
		this.configYear = configYear;
	}

	public String getConfigMonth() {
		return configMonth;
	}

	public void setConfigMonth(String configMonth) {
		this.configMonth = configMonth;
	}

}