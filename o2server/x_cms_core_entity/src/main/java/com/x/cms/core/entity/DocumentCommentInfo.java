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
 * 정보 댓글 테이블
 */
@Schema(name = "DocumentCommentInfo", description = "콘텐츠 관리 정보 댓글.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.DocumentCommentInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentCommentInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentCommentInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentCommentInfo.table;

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
	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appName;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("분류ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("분류 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryName;

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("문서ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("댓글 제목: 없으면 주제와 동일")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	private String title = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("상위 댓글ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId = "";

	public static final String isPrivate_FIELDNAME = "isPrivate";
	@FieldDescribe("비공개 댓글 여부")
	@Column(name = ColumnNamePrefix + isPrivate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isPrivate = false;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("작성자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorName = "";

	public static final String commentAuditStatus_FIELDNAME = "commentAuditStatus";
	@FieldDescribe("댓글 검토 상태: 검토 없음|검토 대기|검토 통과")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + commentAuditStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commentAuditStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String commentAuditStatus = "검토 없음";

	public static final String auditorName_FIELDNAME = "auditorName";
	@FieldDescribe("심사자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + auditorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + auditorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditorName = "";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer orderNumber = 1;

	public static final String commendCount_FIELDNAME = "commendCount";
	@FieldDescribe("댓글 좋아요 수")
	@Column(name = ColumnNamePrefix + commendCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commendCount = 0L;

	public Long getCommendCount() {
		return commendCount;
	}

	public void setCommendCount(Long commendCount) {
		this.commendCount = commendCount;
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

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCommentAuditStatus() {
		return commentAuditStatus;
	}

	public void setCommentAuditStatus(String commentAuditStatus) {
		this.commentAuditStatus = commentAuditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public void addCommendCount(Integer count) {
		if (this.commendCount == null) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount + count;
	}

	public void subCommendCount(Integer count) {
		if (this.commendCount == null) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount - count;
		if (this.commendCount < 0) {
			this.commendCount = 0L;
		}
	}
}