package com.x.bbs.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
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
@Schema(name = "BBSSubjectInfo", description = "论坛主题信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSSubjectInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSSubjectInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSSubjectInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSSubjectInfo.table;

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

	public static final String picId_FIELDNAME = "picId";
	@FieldDescribe("홈 이미지 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + picId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String picId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("주제명: 제목")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = "";

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("主题类别：讨论，新闻等等,根据版块设置")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type = "新闻";

	public static final String typeCategory_FIELDNAME = "typeCategory";
	@FieldDescribe("主题的类别,不同的类别有不同的操作:信息|问题|投票")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + typeCategory_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + typeCategory_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String typeCategory = "信息";

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("주제 요약")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String summary = null;

	public static final String latestReplyTime_FIELDNAME = "latestReplyTime";
	@FieldDescribe("최근 답글 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + latestReplyTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date latestReplyTime = null;

	public static final String latestReplyUser_FIELDNAME = "latestReplyUser";
	@FieldDescribe("최근 답글 사용자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + latestReplyUser_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String latestReplyUser = null;

	public static final String latestReplyId_FIELDNAME = "latestReplyId";
	@FieldDescribe("최근 답글 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + latestReplyId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String latestReplyId = null;

	public static final String replyTotal_FIELDNAME = "replyTotal";
	@FieldDescribe("답글 수")
	@Column(name = ColumnNamePrefix + replyTotal_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long replyTotal = 0L;

	public static final String viewTotal_FIELDNAME = "viewTotal";
	@FieldDescribe("조회 수")
	@Column(name = ColumnNamePrefix + viewTotal_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long viewTotal = 0L;

	public static final String hot_FIELDNAME = "hot";
	@FieldDescribe("주제 인기도")
	@Column(name = ColumnNamePrefix + hot_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Long hot = 0L;

	public static final String stopReply_FIELDNAME = "stopReply";
	@FieldDescribe("답글 금지")
	@Column(name = ColumnNamePrefix + stopReply_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean stopReply = false;

	public static final String recommendToBBSIndex_FIELDNAME = "recommendToBBSIndex";
	@FieldDescribe("시스템 첫 페이지에 추천")
	@Column(name = ColumnNamePrefix + recommendToBBSIndex_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean recommendToBBSIndex = false;

	public static final String bBSIndexSetterName_FIELDNAME = "bBSIndexSetterName";
	@FieldDescribe("홈 추천자 이름")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + bBSIndexSetterName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String bBSIndexSetterName = null;

	public static final String bBSIndexSetterTime_FIELDNAME = "bBSIndexSetterTime";
	@FieldDescribe("첫 페이지 추천 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + bBSIndexSetterTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date bBSIndexSetterTime = null;

	public static final String recommendToForumIndex_FIELDNAME = "recommendToForumIndex";
	@FieldDescribe("포럼 첫 페이지에 추천")
	@Column(name = ColumnNamePrefix + recommendToForumIndex_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean recommendToForumIndex = false;

	public static final String forumIndexSetterName_FIELDNAME = "forumIndexSetterName";
	@FieldDescribe("포럼 추천인 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + forumIndexSetterName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String forumIndexSetterName = "";

	public static final String forumIndexSetterTime_FIELDNAME = "forumIndexSetterTime";
	@FieldDescribe("포럼 추천 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + forumIndexSetterTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date forumIndexSetterTime = null;

	public static final String topToSection_FIELDNAME = "topToSection";
	@FieldDescribe("게시판 상단 고정")
	@Column(name = ColumnNamePrefix + topToSection_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean topToSection = false;

	public static final String topToMainSection_FIELDNAME = "topToMainSection";
	@FieldDescribe("상위 게시판 상단 고정")
	@Column(name = ColumnNamePrefix + topToMainSection_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean topToMainSection = false;

	public static final String topToForum_FIELDNAME = "topToForum";
	@FieldDescribe("포럼 상단 고정")
	@Column(name = ColumnNamePrefix + topToForum_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean topToForum = false;

	public static final String topToBBS_FIELDNAME = "topToBBS";
	@FieldDescribe("전체 상단 고정")
	@Column(name = ColumnNamePrefix + topToBBS_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean topToBBS = false;

	public static final String isTopSubject_FIELDNAME = "isTopSubject";
	@FieldDescribe("상단 고정 주제 여부")
	@Column(name = ColumnNamePrefix + isTopSubject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isTopSubject = false;

	public static final String isCreamSubject_FIELDNAME = "isCreamSubject";
	@FieldDescribe("우수 주제")
	@Column(name = ColumnNamePrefix + isCreamSubject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isCreamSubject = false;

	public static final String isCompleted_FIELDNAME = "isCompleted";
	@FieldDescribe("해결 여부: 질문 글용")
	@Column(name = ColumnNamePrefix + isCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isCompleted = false;

	public static final String acceptReplyId_FIELDNAME = "acceptReplyId";
	@FieldDescribe("채택된 답글 ID: 질문글용, 질문글은 답글 하나를 채택 가능")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + acceptReplyId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String acceptReplyId = null;

	public static final String screamSetterName_FIELDNAME = "screamSetterName";
	@FieldDescribe("우수 주제 설정자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + screamSetterName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String screamSetterName = null;

	public static final String screamSetterTime_FIELDNAME = "screamSetterTime";
	@FieldDescribe("우수글 설정 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + screamSetterTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date screamSetterTime = null;

	public static final String isOriginalSubject_FIELDNAME = "isOriginalSubject";
	@FieldDescribe("원본 주제")
	@Column(name = ColumnNamePrefix + isOriginalSubject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isOriginalSubject = false;

	public static final String originalSetterName_FIELDNAME = "originalSetterName";
	@FieldDescribe("원작 설정자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + originalSetterName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String originalSetterName = "";

	public static final String originalSetterTime_FIELDNAME = "originalSetterTime";
	@FieldDescribe("원본 설정 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + originalSetterTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date originalSetterTime = null;

	public static final String isRecommendSubject_FIELDNAME = "isRecommendSubject";
	@FieldDescribe("게시판 관리자 추천 주제")
	@Column(name = ColumnNamePrefix + isRecommendSubject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isRecommendSubject = false;

	public static final String recommendorName_FIELDNAME = "recommendorName";
	@FieldDescribe("추천인 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recommendorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recommendorName = "";

	public static final String recommendTime_FIELDNAME = "recommendTime";
	@FieldDescribe("추천 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + recommendTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date recommendTime = null;

	public static final String creatorName_FIELDNAME = "creatorName";
	@FieldDescribe("작성자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorName = "";

	public static final String nickName_FIELDNAME = "nickName";
	@FieldDescribe("작성자 닉네임")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + nickName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String nickName = "";

	public static final String lastUpdateUser_FIELDNAME = "lastUpdateUser";
	@FieldDescribe("최종 수정자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + lastUpdateUser_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String lastUpdateUser = "";

	public static final String subjectAuditStatus_FIELDNAME = "subjectAuditStatus";
	@FieldDescribe("主题审核状态：无审核|待审核|审核通过")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + subjectAuditStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectAuditStatus = "无审核";

	public static final String auditorName_FIELDNAME = "auditorName";
	@FieldDescribe("심사자 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + auditorName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditorName = null;

	public static final String subjectStatus_FIELDNAME = "subjectStatus";
	@FieldDescribe("主题状态：启用|关闭|锁定    锁定后是不允许任何人修改和回复的")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + subjectStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subjectStatus = "启用";

	public static final String voteLimitTime_FIELDNAME = "voteLimitTime";
	@FieldDescribe("투표 마감 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + voteLimitTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date voteLimitTime = null;

	public static final String voteResultVisible_FIELDNAME = "voteResultVisible";
	@FieldDescribe("투표 결과 공개 여부")
	@Column(name = ColumnNamePrefix + voteResultVisible_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean voteResultVisible = false;

	public static final String votePersonVisible_FIELDNAME = "votePersonVisible";
	@FieldDescribe("投票人信息是否可见")
	@Column(name = ColumnNamePrefix + votePersonVisible_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean votePersonVisible = false;

	public static final String anonymousSubject_FIELDNAME = "anonymousSubject";
	@FieldDescribe("익명 게시 여부")
	@Column(name = ColumnNamePrefix + anonymousSubject_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean anonymousSubject = false;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public static final String attachmentList_FIELDNAME = "attachmentList";
	@FieldDescribe("첨부파일 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + attachmentList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + attachmentList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + attachmentList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + attachmentList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> attachmentList;

	public static final String editorList_FIELDNAME = "editorList";
	@FieldDescribe("편집자 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + editorList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + editorList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + editorList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editorList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editorList;

	public static final String machineName_FIELDNAME = "machineName";
	@FieldDescribe("기기 유형: 휴대폰|태블릿|개인용 컴퓨터 등")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + machineName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String machineName = "PC";

	public static final String systemType_FIELDNAME = "systemType";
	@FieldDescribe("시스템 이름")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + systemType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String systemType = "Windows";

	public static final String hostIp_FIELDNAME = "hostIp";
	@FieldDescribe("IP 주소")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + hostIp_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String hostIp = "";

	public static final String grade_FIELDNAME = "grade";
	@FieldDescribe("평점")
	@Column(name = ColumnNamePrefix + grade_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer grade = 10;

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
		return title == null ? null : title.trim();
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public Date getLatestReplyTime() {
		return latestReplyTime;
	}

	public void setLatestReplyTime(Date latestReplyTime) {
		this.latestReplyTime = latestReplyTime;
	}

	public String getLatestReplyUser() {
		return latestReplyUser;
	}

	public void setLatestReplyUser(String latestReplyUser) {
		this.latestReplyUser = latestReplyUser;
	}

	public String getLatestReplyId() {
		return latestReplyId;
	}

	public void setLatestReplyId(String latestReplyId) {
		this.latestReplyId = latestReplyId;
	}

	public Long getReplyTotal() {
		return replyTotal;
	}

	public void setReplyTotal(Long replyTotal) {
		this.replyTotal = replyTotal;
	}

	public Long getViewTotal() {
		return viewTotal;
	}

	public void setViewTotal(Long viewTotal) {
		this.viewTotal = viewTotal;
	}

	public Long getHot() {
		return hot;
	}

	public void setHot(Long hot) {
		this.hot = hot;
	}

	public Boolean getStopReply() {
		return stopReply;
	}

	public void setStopReply(Boolean stopReply) {
		this.stopReply = stopReply;
	}

	public Boolean getRecommendToBBSIndex() {
		return recommendToBBSIndex;
	}

	public void setRecommendToBBSIndex(Boolean recommendToBBSIndex) {
		this.recommendToBBSIndex = recommendToBBSIndex;
	}

	public Boolean getRecommendToForumIndex() {
		return recommendToForumIndex;
	}

	public void setRecommendToForumIndex(Boolean recommendToForumIndex) {
		this.recommendToForumIndex = recommendToForumIndex;
	}

	public Boolean getTopToSection() {
		return topToSection;
	}

	public void setTopToSection(Boolean topToSection) {
		this.topToSection = topToSection;
	}

	public Boolean getTopToForum() {
		return topToForum;
	}

	public void setTopToForum(Boolean topToForum) {
		this.topToForum = topToForum;
	}

	public Boolean getTopToBBS() {
		return topToBBS;
	}

	public void setTopToBBS(Boolean topToBBS) {
		this.topToBBS = topToBBS;
	}

	public Boolean getIsCreamSubject() {
		return isCreamSubject;
	}

	public void setIsCreamSubject(Boolean isCreamSubject) {
		this.isCreamSubject = isCreamSubject;
	}

	public Boolean getIsOriginalSubject() {
		return isOriginalSubject;
	}

	public void setIsOriginalSubject(Boolean isOriginalSubject) {
		this.isOriginalSubject = isOriginalSubject;
	}

	public Boolean getIsRecommendSubject() {
		return isRecommendSubject;
	}

	public void setIsRecommendSubject(Boolean isRecommendSubject) {
		this.isRecommendSubject = isRecommendSubject;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getSubjectAuditStatus() {
		return subjectAuditStatus;
	}

	public void setSubjectAuditStatus(String subjectAuditStatus) {
		this.subjectAuditStatus = subjectAuditStatus;
	}

	public String getSubjectStatus() {
		return subjectStatus;
	}

	public void setSubjectStatus(String subjectStatus) {
		this.subjectStatus = subjectStatus;
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

	public String getbBSIndexSetterName() {
		return bBSIndexSetterName;
	}

	public void setbBSIndexSetterName(String bBSIndexSetterName) {
		this.bBSIndexSetterName = bBSIndexSetterName;
	}

	public Date getbBSIndexSetterTime() {
		return bBSIndexSetterTime;
	}

	public void setbBSIndexSetterTime(Date bBSIndexSetterTime) {
		this.bBSIndexSetterTime = bBSIndexSetterTime;
	}

	public String getForumIndexSetterName() {
		return forumIndexSetterName;
	}

	public void setForumIndexSetterName(String forumIndexSetterName) {
		this.forumIndexSetterName = forumIndexSetterName;
	}

	public Date getForumIndexSetterTime() {
		return forumIndexSetterTime;
	}

	public void setForumIndexSetterTime(Date forumIndexSetterTime) {
		this.forumIndexSetterTime = forumIndexSetterTime;
	}

	public String getScreamSetterName() {
		return screamSetterName;
	}

	public void setScreamSetterName(String screamSetterName) {
		this.screamSetterName = screamSetterName;
	}

	public Date getScreamSetterTime() {
		return screamSetterTime;
	}

	public void setScreamSetterTime(Date screamSetterTime) {
		this.screamSetterTime = screamSetterTime;
	}

	public String getOriginalSetterName() {
		return originalSetterName;
	}

	public void setOriginalSetterName(String originalSetterName) {
		this.originalSetterName = originalSetterName;
	}

	public Date getOriginalSetterTime() {
		return originalSetterTime;
	}

	public void setOriginalSetterTime(Date originalSetterTime) {
		this.originalSetterTime = originalSetterTime;
	}

	public String getRecommendorName() {
		return recommendorName;
	}

	public void setRecommendorName(String recommendorName) {
		this.recommendorName = recommendorName;
	}

	public Date getRecommendTime() {
		return recommendTime;
	}

	public void setRecommendTime(Date recommendTime) {
		this.recommendTime = recommendTime;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	public Boolean getTopToMainSection() {
		return topToMainSection;
	}

	public void setTopToMainSection(Boolean topToMainSection) {
		this.topToMainSection = topToMainSection;
	}

	public List<String> getAttachmentList() {
		return attachmentList == null ? new ArrayList<>() : attachmentList;
	}

	public void setAttachmentList(List<String> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public Boolean getIsTopSubject() {
		return isTopSubject;
	}

	public void setIsTopSubject(Boolean isTopSubject) {
		this.isTopSubject = isTopSubject;
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

	public Boolean getIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(Boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getTypeCategory() {
		return typeCategory;
	}

	public void setTypeCategory(String typeCategory) {
		this.typeCategory = typeCategory;
	}

	public String getAcceptReplyId() {
		return acceptReplyId;
	}

	public void setAcceptReplyId(String acceptReplyId) {
		this.acceptReplyId = acceptReplyId;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public Date getVoteLimitTime() {
		return voteLimitTime;
	}

	public Boolean getVoteResultVisible() {
		return voteResultVisible;
	}

	public Boolean getVotePersonVisible() {
		return votePersonVisible;
	}

	public void setVoteLimitTime(Date voteLimitTime) {
		this.voteLimitTime = voteLimitTime;
	}

	public void setVoteResultVisible(Boolean voteResultVisible) {
		this.voteResultVisible = voteResultVisible;
	}

	public void setVotePersonVisible(Boolean votePersonVisible) {
		this.votePersonVisible = votePersonVisible;
	}

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Boolean getAnonymousSubject() {
		return anonymousSubject;
	}

	public void setAnonymousSubject(Boolean anonymousSubject) {
		this.anonymousSubject = anonymousSubject;
	}

	public List<String> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}
}
