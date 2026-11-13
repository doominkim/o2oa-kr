package com.x.cms.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 콘텐츠 관리 로그 정보 테이블
 *
 */
@Schema(name = "Log", description = "콘텐츠 관리 로그 정보.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Log.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Log.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Log extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.Log.table;

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
	public static final String operationType_FIELDNAME = "operationType";
	@FieldDescribe("작업 유형: 추가|수정|삭제|조회|검색")
	@Index(name = TABLE + IndexNameMiddle + operationType_FIELDNAME)
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + operationType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operationType;

	public static final String operationLevel_FIELDNAME = "operationLevel";
	@FieldDescribe("작업 수준: 앱|분류|문서|파일")
	@Index(name = TABLE + IndexNameMiddle + operationLevel_FIELDNAME)
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + operationLevel_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operationLevel;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("작업 대상: 앱 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("작업 대상: 분류 ID")
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryId;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("작업 대상: 문서 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentId;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("작업 대상: 파일 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileId;

	public static final String operatorUid_FIELDNAME = "operatorUid";
	@FieldDescribe("작업자 UID")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operatorUid_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + operatorUid_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operatorUid;

	public static final String operatorName_FIELDNAME = "operatorName";
	@FieldDescribe("작업자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + operatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String operatorName;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("작업 설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	/**
	 * 작업 유형 조회
	 * 
	 * @return
	 */
	public String getOperationType() {
		return operationType;
	}

	/**
	 * 작업 유형 설정
	 * 
	 * @param operationType
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	/**
	 * 작업자 계정 조회
	 * 
	 * @return
	 */
	public String getOperatorUid() {
		return operatorUid;
	}

	/**
	 * 작업자 계정 설정
	 * 
	 * @param operatorUid
	 */
	public void setOperatorUid(String operatorUid) {
		this.operatorUid = operatorUid;
	}

	/**
	 * 작업자 이름 조회
	 * 
	 * @return
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * 작업자 이름 설정
	 * 
	 * @param operatorName
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * 작업 로그 설명 조회
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 작업 로그 설명 설정
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 작업 대상 앱 ID 조회
	 * 
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 작업 대상 앱 ID 설정
	 * 
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 작업 대상 분류 ID 조회
	 * 
	 * @return
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * 작업 대상 분류 ID 설정
	 * 
	 * @param appId
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * 작업 대상 문서 ID 조회
	 * 
	 * @return
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * 작업 대상 문서 ID 설정
	 * 
	 * @param appId
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * 작업 대상 파일 ID 조회
	 * 
	 * @return
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * 작업 대상 파일 ID 설정
	 * 
	 * @param appId
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * 작업 대상 수준: 앱|분류|문서|파일
	 * 
	 * @return
	 */
	public String getOperationLevel() {
		return operationLevel;
	}

	/**
	 * 작업 대상 수준: 앱|분류|문서|파일
	 * 
	 * @return
	 */
	public void setOperationLevel(String operationLevel) {
		this.operationLevel = operationLevel;
	}
}