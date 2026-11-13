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
 * 문서, 댓글 좋아요
 *
 * @author O2LEE
 *
 */
@Schema(name = "DocumentCommend", description = "콘텐츠 관리 문서 댓글.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.DocumentCommend.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.DocumentCommend.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DocumentCommend extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.DocumentCommend.table;

	public static final String COMMEND_TYPE_DOCUMENT = "document";
	public static final String COMMEND_TYPE_COMMENT = "comment";

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

	@Override
	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 위는 JpaObject 기본 필드
	 * =============================================================================
	 * =====
	 */

	public static final String documentId_FIELDNAME = "documentId";
	@FieldDescribe("문서ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + documentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + documentId_FIELDNAME)
	private String documentId;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("문서 제목")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String commentId_FIELDNAME = "commentId";
	@FieldDescribe("문서 댓글 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + commentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String commentId;

	public static final String commentTitle_FIELDNAME = "commentTitle";
	@FieldDescribe("문서 댓글 제목")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + commentTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String commentTitle;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("문서 또는 댓글 작성자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String commendPerson_FIELDNAME = "commendPerson";
	@FieldDescribe("좋아요 누른 사용자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + commendPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@Index(name = TABLE + IndexNameMiddle + commendPerson_FIELDNAME)
	private String commendPerson;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("좋아요 유형: document(문서 좋아요, 기본값), comment(댓글 좋아요)")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + type_FIELDNAME, columnDefinition = "varchar(64) DEFAULT 'document'")
	@CheckPersist(allowEmpty = true)
	private String type = COMMEND_TYPE_DOCUMENT;

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getCommendPerson() {
		return commendPerson;
	}

	public void setCommendPerson(String commendPerson) {
		this.commendPerson = commendPerson;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCommentId() {
		return commentId;
	}

	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}

	public String getCommentTitle() {
		return commentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		this.commentTitle = commentTitle;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
