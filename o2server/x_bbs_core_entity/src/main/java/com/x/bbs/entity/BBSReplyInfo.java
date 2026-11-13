package com.x.bbs.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
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
 * 주제 정보 테이블
 *
 * @author LIYI
 */
@Schema(name = "BBSReplyInfo", description = "포럼 권한-역할 바인딩 관계.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSReplyInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSReplyInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSReplyInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSReplyInfo.table;

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

	/*
	 * =============================================================================
	 * ===== 아래는 업무별 데이터 테이블 필드 요구사항
	 * =============================================================================
	 * =====
	 */
	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("포럼 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("포럼 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumName = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionId = "";

	public static final String sectionName_FIELDNAME = "sectionName";
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sectionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String sectionName = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("상위 게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionId = "";

	public static final String mainSectionName_FIELDNAME = "mainSectionName";
	@FieldDescribe("상위 게시판 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + mainSectionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String mainSectionName = "";

	public static final String subjectId_FIELDNAME = "subjectId";
	@FieldDescribe("주제 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + subjectId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subjectId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("답글 제목: 없으면 주제와 동일")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("상위 답글 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId = "";

	public static final String picId_FIELDNAME = "picId";
	@FieldDescribe("이미지 정보 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + picId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + picId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String picId = "";

	public static final String content_FIELDNAME = "content";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("내용")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + content_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String content = "";

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("작성자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorName = "";

	public static final String nickName_FIELDNAME = "nickName";
	@FieldDescribe("작성자 닉네임")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + nickName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String nickName = "";

	public static final String replyAuditStatus_FIELDNAME = "replyAuditStatus";
	@FieldDescribe("댓글 검토 상태: 검토 없음|검토 대기|검토 통과")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + replyAuditStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + replyAuditStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String replyAuditStatus = "검토 없음";

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

	public static final String machineName_FIELDNAME = "machineName";
	@FieldDescribe("기기 유형: 휴대폰|태블릿|개인용 컴퓨터 등")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + machineName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + machineName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String machineName = "PC";

	public static final String systemType_FIELDNAME = "systemType";
	@FieldDescribe("시스템 이름")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + systemType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + systemType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String systemType = "Windows";

	public static final String hostIp_FIELDNAME = "hostIp";
	@FieldDescribe("IP 주소")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + hostIp_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostIp_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostIp = "";

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

	public String getMainSectionName() {
		return mainSectionName;
	}

	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getReplyAuditStatus() {
		return replyAuditStatus;
	}

	public void setReplyAuditStatus(String replyAuditStatus) {
		this.replyAuditStatus = replyAuditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}
