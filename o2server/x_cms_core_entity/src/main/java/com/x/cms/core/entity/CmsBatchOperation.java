package com.x.cms.core.entity;

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

/**
 * 데이터 후처리, 예: 게시판 이름 변경으로 인해 모든 분류와 문서 별칭을 수정해야 하는 경우
 */
@Schema(name = "CmsBatchOperation", description = "콘텐츠 관리 데이터 후처리.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.CmsBatchOperation.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.CmsBatchOperation.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CmsBatchOperation extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;
	@SuppressWarnings("unused")
	private static final String TABLE = PersistenceProperties.CmsBatchOperation.table;

	/* 위는 JpaObject 기본 필드 */
	public void onPersist() throws Exception {

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
	/*
	 * =============================================================================
	 * ===== 위는 JpaObject 기본 필드
	 * =============================================================================
	 * =====
	 */

	public static final String optType_FIELDNAME = "optType";
	@FieldDescribe("작업 유형")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + optType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String optType;

	public static final String objType_FIELDNAME = "objType";
	@FieldDescribe("객체 유형")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + objType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objType;

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("바인딩된 ID")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String bundle;

	public static final String oldInfo_FIELDNAME = "oldInfo";
	@FieldDescribe("이전 정보: 제목, 이름 수정 시 사용")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + oldInfo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String oldInfo;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("비고")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String errorCount_FIELDNAME = "errorCount";
	@FieldDescribe("실행 오류 횟수")
	@Column(name = ColumnNamePrefix + errorCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer errorCount;

	public static final String isRunning_FIELDNAME = "isRunning";
	@FieldDescribe("실행 중 여부")
	@Column(name = ColumnNamePrefix + isRunning_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isRunning;

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOldInfo() {
		return oldInfo;
	}

	public void setOldInfo(String oldInfo) {
		this.oldInfo = oldInfo;
	}

	public Integer getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(Integer errorCount) {
		this.errorCount = errorCount;
	}

	public void addErrorCount(Integer count) {
		if (this.errorCount == null) {
			this.errorCount = 0;
		}
		this.errorCount = this.errorCount + count;
	}

	public Boolean getIsRunning() {
		return isRunning;
	}

	public void setIsRunning(Boolean isRunning) {
		this.isRunning = isRunning;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("OBJ_TYPE: ").append(this.objType);
		sb.append(", OPT_TYPE: ").append(this.optType);
		sb.append(", BUNDLE: ").append(this.bundle);
		sb.append(", OLDINFO: ").append(this.oldInfo);
		sb.append(", DESCRIPTION: ").append(this.description);
		return sb.toString();
	}
}