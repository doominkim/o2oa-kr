package com.x.meeting.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Meeting", description = "회의 관리 회의.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Meeting.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Meeting.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Meeting extends SliceJpaObject {

	private static final long serialVersionUID = 8117315785915863335L;
	private static final String TABLE = PersistenceProperties.Meeting.table;

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
		this.pinyin = StringUtils.lowerCase(PinyinHelper.convertToPinyinString(subject, "", PinyinFormat.WITHOUT_TONE));
		this.pinyinInitial = StringUtils.lowerCase(PinyinHelper.getShortPinyin(subject));
	}

	/* 실행 메서드 업데이트 */

	public static final String subject_FIELDNAME = "subject";
	@FieldDescribe("이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subject_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + subject_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String subject;

	public static final String pinyin_FIELDNAME = "pinyin";
	@FieldDescribe("name 병음.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyin_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyin_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyin;

	public static final String pinyinInitial_FIELDNAME = "pinyinInitial";
	@FieldDescribe("name 병음 첫 글자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + pinyinInitial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + pinyinInitial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String pinyinInitial;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	/* summary 필드로 변경 */
	@Deprecated
	private String description;

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("설명")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + summary_FIELDNAME)
	private String summary;

	public static final String room_FIELDNAME = "room";
	@FieldDescribe("소속 층.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + room_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + room_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationExists = { @CitationExist(type = Room.class) })
	private String room;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("시작 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String completedTime_FIELDNAME = "completedTime";
	@FieldDescribe("종료 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + completedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + completedTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date completedTime;

	public static final String inviteMemberList_FIELDNAME = "inviteMemberList";
	@FieldDescribe("초대 사용자, 신분, 조직, 그룹.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ inviteMemberList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + inviteMemberList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + inviteMemberList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + inviteMemberList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> inviteMemberList;

	public static final String invitePersonList_FIELDNAME = "invitePersonList";
	@FieldDescribe("초대 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ invitePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + invitePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + invitePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> invitePersonList;

	public static final String inviteDelPersonList_FIELDNAME = "inviteDelPersonList";
	@FieldDescribe("초대 사용자, 신분, 조직 삭제 목록.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ inviteDelPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ inviteDelPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + inviteDelPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + inviteDelPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> inviteDelPersonList;

	public static final String acceptPersonList_FIELDNAME = "acceptPersonList";
	@FieldDescribe("수락 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ acceptPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + acceptPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + acceptPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + acceptPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> acceptPersonList;

	public static final String rejectPersonList_FIELDNAME = "rejectPersonList";
	@FieldDescribe("거절 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ rejectPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + rejectPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + rejectPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + rejectPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> rejectPersonList;

	public static final String checkinPersonList_FIELDNAME = "checkinPersonList";
	@FieldDescribe("출석 체크 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ checkinPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ checkinPersonList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + checkinPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + checkinPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> checkinPersonList;

	public static final String confirmStatus_FIELDNAME = "confirmStatus";
	@FieldDescribe("회의 예약 상태")
	@Enumerated(EnumType.STRING)
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + confirmStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	@FieldTypeDescribe(fieldType = "enum", fieldValue = "allow|deny|wait", fieldTypeName = "com.x.meeting.core.entity.ConfirmStatus")
	private ConfirmStatus confirmStatus;

	public static final String manualCompleted_FIELDNAME = "manualCompleted";
	@FieldDescribe("회의 수동 종료 여부")
	@Column(name = ColumnNamePrefix + manualCompleted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean manualCompleted;

	public static final String actualStartTime_FIELDNAME = "actualStartTime";
	@FieldDescribe("실제 시작 시간.")
	@Temporal(TemporalType.TIME)
	@Column(name = ColumnNamePrefix + actualStartTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + actualStartTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date actualStartTime;

	public static final String actualCompletedTime_FIELDNAME = "actualCompletedTime";
	@FieldDescribe("실제 종료 시간.")
	@Temporal(TemporalType.TIME)
	@Column(name = ColumnNamePrefix + actualCompletedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + actualCompletedTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date actualCompletedTime;

	public static final String applicant_FIELDNAME = "applicant";
	@FieldDescribe("회의 신청자")
	@Column(length = length_255B, name = ColumnNamePrefix + applicant_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicant_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String applicant;

	public static final String auditor_FIELDNAME = "auditor";
	@FieldDescribe("회의 심사자")
	@Column(length = length_255B, name = ColumnNamePrefix + auditor_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + auditor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String auditor;

	public static final String memo_FIELDNAME = "memo";
	@FieldDescribe("비고.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + memo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String memo;

	public static final String hostUnit_FIELDNAME = "hostUnit";
	@FieldDescribe("주관 부서")
	@Column(length = length_255B, name = ColumnNamePrefix + hostUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostUnit;

	public static final String hostPerson_FIELDNAME = "hostPerson";
	@FieldDescribe("사회자")
	@Column(length = length_255B, name = ColumnNamePrefix + hostPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + hostPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String hostPerson;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("회의 유형")
	@Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String type;

	public static final String mode_FIELDNAME = "mode";
	@FieldDescribe("회의 방식: online|온라인 회의;offline|오프라인 회의")
	@Column(length = length_255B, name = ColumnNamePrefix + mode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mode;

	public static final String roomId_FIELDNAME = "roomId";
	@FieldDescribe("온라인 회의 번호")
	@Column(length = length_255B, name = ColumnNamePrefix + roomId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roomId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomId;

	public static final String roomLink_FIELDNAME = "roomLink";
	@FieldDescribe("온라인 회의 링크")
	@Column(length = length_255B, name = ColumnNamePrefix + roomLink_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomLink;

	public static final String roomInviteCode_FIELDNAME = "roomInviteCode";
	@FieldDescribe("온라인 회의 초대 코드")
	@Column(length = length_255B, name = ColumnNamePrefix + roomInviteCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roomInviteCode_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String roomInviteCode;

	public static final String externalPerson_FIELDNAME = "externalPerson";
	@FieldDescribe("외부 참석자")
	@Column(length = 500, name = ColumnNamePrefix + externalPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String externalPerson;

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedTime) {
		this.completedTime = completedTime;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getActualStartTime() {
		return actualStartTime;
	}

	public void setActualStartTime(Date actualStartTime) {
		this.actualStartTime = actualStartTime;
	}

	public Date getActualCompletedTime() {
		return actualCompletedTime;
	}

	public void setActualCompletedTime(Date actualCompletedTime) {
		this.actualCompletedTime = actualCompletedTime;
	}

	public List<String> getInvitePersonList() {
		return invitePersonList;
	}

	public void setInvitePersonList(List<String> invitePersonList) {
		this.invitePersonList = invitePersonList;
	}

	public List<String> getAcceptPersonList() {
		return acceptPersonList;
	}

	public void setAcceptPersonList(List<String> acceptPersonList) {
		this.acceptPersonList = acceptPersonList;
	}

	public List<String> getRejectPersonList() {
		return rejectPersonList;
	}

	public void setRejectPersonList(List<String> rejectPersonList) {
		this.rejectPersonList = rejectPersonList;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public ConfirmStatus getConfirmStatus() {
		return confirmStatus;
	}

	public void setConfirmStatus(ConfirmStatus confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public Boolean getManualCompleted() {
		return manualCompleted;
	}

	public void setManualCompleted(Boolean manualCompleted) {
		this.manualCompleted = manualCompleted;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyinInitial() {
		return pinyinInitial;
	}

	public void setPinyinInitial(String pinyinInitial) {
		this.pinyinInitial = pinyinInitial;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<String> getCheckinPersonList() {
		return checkinPersonList;
	}

	public void setCheckinPersonList(List<String> checkinPersonList) {
		this.checkinPersonList = checkinPersonList;
	}

	public String getHostUnit() {
		return hostUnit;
	}

	public void setHostUnit(String hostUnit) {
		this.hostUnit = hostUnit;
	}

	public String getHostPerson() {
		return hostPerson;
	}

	public void setHostPerson(String hostPerson) {
		this.hostPerson = hostPerson;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 출석 체크 사용자 1명 추가
	 *
	 * @param distinguishedName
	 */
	public void addCheckinPerson(String distinguishedName) {
		this.checkinPersonList = ListTools.addStringToList(distinguishedName, this.checkinPersonList);
	}

	/**
	 * 출석 체크 사용자 1명 삭제
	 *
	 * @param distinguishedName
	 */
	public void removeCheckinPerson(String distinguishedName) {
		this.checkinPersonList = ListTools.removeStringFromList(distinguishedName, this.checkinPersonList);
	}

	public List<String> getInviteDelPersonList() {
		return inviteDelPersonList;
	}

	public void setInviteDelPersonList(List<String> inviteDelPersonList) {
		this.inviteDelPersonList = inviteDelPersonList;
	}

	public List<String> getInviteMemberList() {
		return inviteMemberList;
	}

	public void setInviteMemberList(List<String> inviteMemberList) {
		this.inviteMemberList = inviteMemberList;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public String getRoomLink() {
		return roomLink;
	}

	public void setRoomLink(String roomLink) {
		this.roomLink = roomLink;
	}

	public String getRoomInviteCode() {
		return roomInviteCode;
	}

	public void setRoomInviteCode(String roomInviteCode) {
		this.roomInviteCode = roomInviteCode;
	}

	public String getExternalPerson() {
		return externalPerson;
	}

	public void setExternalPerson(String externalPerson) {
		this.externalPerson = externalPerson;
	}
}
