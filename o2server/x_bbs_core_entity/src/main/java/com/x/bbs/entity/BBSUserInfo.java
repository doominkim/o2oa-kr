package com.x.bbs.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 포럼 사용자 정보 테이블
 * 
 * @author LIYI
 */
@Schema(name = "BBSUserInfo", description = "포럼 사용자 정보.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSUserInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSUserInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSUserInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSUserInfo.table;

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

	public static final String userName_FIELDNAME = "userName";
	@FieldDescribe("사용자 식별자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + userName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + userName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String userName = null;

	public static final String nickName_FIELDNAME = "nickName";
	@FieldDescribe("닉네임")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + nickName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String nickName = null;

	public static final String sex_FIELDNAME = "sex";
	@FieldDescribe("성별: 0-여 1-남")
	@Column(name = ColumnNamePrefix + sex_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer sex = 0;

	public static final String cardId_FIELDNAME = "cardId";
	@FieldDescribe("주민등록번호")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + cardId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String cardId = null;

	public static final String mobile_FIELDNAME = "mobile";
	@FieldDescribe("휴대전화")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + mobile_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobile = "";

	public static final String credit_FIELDNAME = "credit";
	@FieldDescribe("포인트")
	@Column(name = ColumnNamePrefix + credit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long credit = 0L;

	public static final String subjectCount_FIELDNAME = "subjectCount";
	@FieldDescribe("주제 수")
	@Column(name = ColumnNamePrefix + subjectCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long subjectCount = 0L;

	public static final String subjectCountToday_FIELDNAME = "subjectCountToday";
	@FieldDescribe("오늘 게시한 주제 수")
	@Column(name = ColumnNamePrefix + subjectCountToday_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long subjectCountToday = 0L;

	public static final String replyCount_FIELDNAME = "replyCount";
	@FieldDescribe("답글 수")
	@Column(name = ColumnNamePrefix + replyCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long replyCount = 0L;

	public static final String replyCountToday_FIELDNAME = "replyCountToday";
	@FieldDescribe("오늘 작성한 답글 수")
	@Column(name = ColumnNamePrefix + replyCountToday_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long replyCountToday = 0L;

	public static final String creamCount_FIELDNAME = "creamCount";
	@FieldDescribe("우수글 수")
	@Column(name = ColumnNamePrefix + creamCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long creamCount = 0L;

	public static final String originalCount_FIELDNAME = "originalCount";
	@FieldDescribe("원본글 수")
	@Column(name = ColumnNamePrefix + originalCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long originalCount = 0L;

	public static final String popularity_FIELDNAME = "popularity";
	@FieldDescribe("인기")
	@Column(name = ColumnNamePrefix + popularity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long popularity = 0L;

	public static final String fansCount_FIELDNAME = "fansCount";
	@FieldDescribe("팬 수")
	@Column(name = ColumnNamePrefix + fansCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long fansCount = 0L;

	public static final String signContent_FIELDNAME = "signContent";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("서명 내용")
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + signContent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String signContent = "";

	public static final String permissionContent_FIELDNAME = "permissionContent";
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@FieldDescribe("권한 역할 내용")
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + permissionContent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String permissionContent = null;

	public static final String online_FIELDNAME = "online";
	@FieldDescribe("온라인 상태")
	@Column(name = ColumnNamePrefix + online_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean online = false;

	public static final String lastVisitTime_FIELDNAME = "lastVisitTime";
	@FieldDescribe("마지막 방문 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastVisitTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date lastVisitTime = null;

	public static final String lastOperationTime_FIELDNAME = "lastOperationTime";
	@FieldDescribe("최근 작업 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + lastOperationTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastOperationTime = null;

	public static final String userStatus_FIELDNAME = "userStatus";
	@FieldDescribe("사용자 상태: 사용|사용 중지")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + userStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String userStatus = "사용";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Long getCredit() {
		return credit;
	}

	public void setCredit(Long credit) {
		this.credit = credit;
	}

	public Long getSubjectCount() {
		return subjectCount;
	}

	public void setSubjectCount(Long subjectCount) {
		this.subjectCount = subjectCount;
	}

	public Long getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(Long replyCount) {
		this.replyCount = replyCount;
	}

	public Long getCreamCount() {
		return creamCount;
	}

	public void setCreamCount(Long creamCount) {
		this.creamCount = creamCount;
	}

	public Long getOriginalCount() {
		return originalCount;
	}

	public void setOriginalCount(Long originalCount) {
		this.originalCount = originalCount;
	}

	public Long getPopularity() {
		return popularity;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	public Long getFansCount() {
		return fansCount;
	}

	public void setFansCount(Long fansCount) {
		this.fansCount = fansCount;
	}

	public String getSignContent() {
		return signContent;
	}

	public void setSignContent(String signContent) {
		this.signContent = signContent;
	}

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}

	public Date getLastVisitTime() {
		return lastVisitTime;
	}

	public void setLastVisitTime(Date lastVisitTime) {
		this.lastVisitTime = lastVisitTime;
	}

	public Date getLastOperationTime() {
		return lastOperationTime;
	}

	public void setLastOperationTime(Date lastOperationTime) {
		this.lastOperationTime = lastOperationTime;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getPermissionContent() {
		return permissionContent;
	}

	public void setPermissionContent(String permissionContent) {
		this.permissionContent = permissionContent;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public Long getSubjectCountToday() {
		return subjectCountToday;
	}

	public void setSubjectCountToday(Long subjectCountToday) {
		this.subjectCountToday = subjectCountToday;
	}

	public Long getReplyCountToday() {
		return replyCountToday;
	}

	public void setReplyCountToday(Long replyCountToday) {
		this.replyCountToday = replyCountToday;
	}
}