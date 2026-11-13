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

@Schema(name = "AttendanceSetting", description = "근태 설정.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceSetting.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AttendanceSetting.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceSetting extends SliceJpaObject {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.AttendanceSetting.table;

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
	public static final String configCode_FIELDNAME = "configCode";
	@FieldDescribe("설정 코드")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + configCode_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configCode;

	public static final String configName_FIELDNAME = "configName";
	@FieldDescribe("설정 이름")
	@Column( length = JpaObject.length_96B, name = ColumnNamePrefix + configName_FIELDNAME )
	@CheckPersist(allowEmpty = false)
	private String configName;

	public static final String configValue_FIELDNAME = "configValue";
	@FieldDescribe("설정 내용")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + configValue_FIELDNAME )
	private String configValue;

	public static final String valueType_FIELDNAME = "valueType";
	@FieldDescribe("값 유형: select | identity | number | date | text")
	@Column( length = JpaObject.length_16B, name = ColumnNamePrefix + valueType_FIELDNAME )
	private String valueType = null;

	public static final String selectContent_FIELDNAME = "selectContent";
	@FieldDescribe("선택 값, select와 함께 사용, ‘|’로 구분")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + selectContent_FIELDNAME )
	private String selectContent = "--无--";

	public static final String isMultiple_FIELDNAME = "isMultiple";
	@FieldDescribe("다중 값 허용 여부")
	@Column( name = ColumnNamePrefix + isMultiple_FIELDNAME )
	private Boolean isMultiple = false;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column( name = ColumnNamePrefix + orderNumber_FIELDNAME )
	private Integer orderNumber = 1;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("비고 설명")
	@Column( length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME )
	private String description = null;

	public String getConfigCode() {
		return configCode;
	}

	public void setConfigCode(String configCode) {
		this.configCode = configCode;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public String getConfigValue() {
		return configValue;
	}

	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getSelectContent() {
		return selectContent;
	}

	public void setSelectContent(String selectContent) {
		this.selectContent = selectContent;
	}

	public Boolean getIsMultiple() {
		return isMultiple;
	}

	public void setIsMultiple(Boolean isMultiple) {
		this.isMultiple = isMultiple;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}