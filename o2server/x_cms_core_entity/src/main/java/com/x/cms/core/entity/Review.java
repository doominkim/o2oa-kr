package com.x.cms.core.entity;

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
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Review", description = "콘텐츠 관리 권한.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Review.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Review.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN,
						JpaObject.SEQUENCECOLUMN }) }, indexes = {
								@javax.persistence.Index(name = Review.TABLE + Review.IndexNameMiddle
										+ Review.docId_FIELDNAME, columnList = Review.ColumnNamePrefix
												+ Review.docId_FIELDNAME + "," + Review.ColumnNamePrefix
												+ Review.permissionObj_FIELDNAME) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public class Review extends SliceJpaObject {

	private static final long serialVersionUID = -570048661936488247L;

	public static final String TABLE = PersistenceProperties.Review.table;

	public static final String PERMISSION_ANY = "*";

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("데이터베이스 기본 키, 자동 생성.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 위는 JpaObject 기본 필드 */
	@Override
	public void onPersist() throws Exception {
	}

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("문서 유형: 정보|데이터.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentType;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("게시판 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("게시판 이름.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@FieldDescribe("게시판 별칭.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("분류 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("분류 이름.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("분류 별칭.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	public static final String docId_FIELDNAME = "docId";
	@FieldDescribe("문서ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + docId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docId;

	public static final String docSequence_FIELDNAME = "docSequence";
	@FieldDescribe("문서 Sequence.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + docSequence_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docSequence;

	public static final String docStatus_FIELDNAME = "docStatus";
	@FieldDescribe("문서 상태.")
	@Column(length = length_32B, name = ColumnNamePrefix + docStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String docStatus;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("문서 제목.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("기안자")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("작성자 Identity")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("작성자 조직")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("작성자 최상위 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	public static final String docCreateTime_FIELDNAME = "docCreateTime";
	@FieldDescribe("document의 기안 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 종료 시간은 비어 있을 수 없음, 비어 있으면 정렬 오류 가능 */
	@Column(name = ColumnNamePrefix + docCreateTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date docCreateTime;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("document의 게시 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	/* 종료 시간은 비어 있을 수 없음, 비어 있으면 정렬 오류 가능 */
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;

	public static final String permissionObj_FIELDNAME = "permissionObj";
	@FieldDescribe("권한 소유자")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ permissionObj_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionObj_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObj;

	public static final String permissionObjType_FIELDNAME = "permissionObjType";
	@FieldDescribe("권한 소유자 유형: PERSON|IDENTITY|UNIT|GROUP|ROLE")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + permissionObjType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionObjType;

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getCreatorIdentity() {
		return creatorIdentity;
	}

	public void setCreatorIdentity(String creatorIdentity) {
		this.creatorIdentity = creatorIdentity;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Date getDocCreateTime() {
		return docCreateTime;
	}

	public void setDocCreateTime(Date docCreateTime) {
		this.docCreateTime = docCreateTime;
	}

	public String getPermissionObj() {
		return permissionObj;
	}

	public void setPermissionObj(String permissionObj) {
		this.permissionObj = permissionObj;
	}

	public String getPermissionObjType() {
		return permissionObjType;
	}

	public void setPermissionObjType(String permissionObjType) {
		this.permissionObjType = permissionObjType;
	}

	public String getDocSequence() {
		return docSequence;
	}

	public void setDocSequence(String docSequence) {
		this.docSequence = getSequenceString(docSequence);
	}

	private String getSequenceString(String sequenceString) {
		if (StringUtils.length(sequenceString) > 60) {
			return StringUtils.substring(sequenceString, 0, 60);
		}
		return sequenceString;
	}
}
