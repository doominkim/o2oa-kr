package com.x.calendar.core.entity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.core.tools.DateOperation;
import com.x.calendar.core.tools.LogUtil;

import io.swagger.v3.oas.annotations.media.Schema;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExDate;

/**
 * 반복 일정 호스트 정보
 * 
 * @author O2LEE
 *
 */
@Schema(name = "Calendar_EventRepeatMaster", description = "일정 반복 일정 호스트 정보.")
@SuppressWarnings("rawtypes")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Calendar_EventRepeatMaster.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Calendar_EventRepeatMaster.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Calendar_EventRepeatMaster extends SliceJpaObject implements Cloneable, Comparable {
	private static DateOperation dateOperation = new DateOperation();
	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Calendar_EventRepeatMaster.table;

	static {
		// ical4j 시간대를 시스템 기본 시간대로 강제 설정
		System.setProperty("net.fortuna.ical4j.timezone.date.floating", "true");
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

	/* 위는 JpaObject 기본 필드 */
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
	 * ===== 생성자 영역
	 * =============================================================================
	 * =====
	 */
	public Calendar_EventRepeatMaster() {
	}

	public Calendar_EventRepeatMaster(final String id, final String title, final Date startTime, final Date endTime) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.recurrenceStartTime = startTime;
		this.endTime = endTime;
		this.isAllDayEvent = false;
	}

	public Calendar_EventRepeatMaster(final String id, final String title, final Date startTime, final int duration) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.recurrenceStartTime = startTime;
		this.endTime = new Date(startTime.getTime() + duration);
		this.isAllDayEvent = false;
	}

	public Calendar_EventRepeatMaster(final String id, final String title, final Date startTime, final int duration,
			final String recurrenceRule) {
		this(id, title, startTime, duration);
		this.recurrenceRule = recurrenceRule;
	}

	public Calendar_EventRepeatMaster(final String id, final String title, final Date startTime, final Date endTime,
			final String recurrenceRule) {
		this(id, title, startTime, endTime);
		this.recurrenceRule = recurrenceRule;
	}

	/*
	 * =============================================================================
	 * ===== 아래는 업무별 데이터 테이블 필드 요구사항
	 * =============================================================================
	 * =====
	 */
	public static final String calendarId_FIELDNAME = "calendarId";
	@FieldDescribe("캘린더 계정 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + calendarId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + calendarId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String calendarId = null;

	public static final String eventType_FIELDNAME = "eventType";
	@FieldDescribe("정보 유형: CAL_EVENT | TASK_EVENT")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + eventType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + eventType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String eventType = "CAL_EVENT";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("이벤트 제목")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String title = null;

	public static final String color_FIELDNAME = "color";
	@FieldDescribe("표시 색상")
	@Column(length = JpaObject.length_8B, name = ColumnNamePrefix + color_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String color = "#1462be";

	public static final String comment_FIELDNAME = "comment";
	@FieldDescribe("비고 정보")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + comment_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String comment = null;

	public static final String commentId_FIELDNAME = "commentId";
	@FieldDescribe("비고 LOB 정보")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + commentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String commentId = null;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("이벤트 시작 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime = null;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("이벤트 종료 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + endTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date endTime = null;

	public static final String locationName_FIELDNAME = "locationName";
	@FieldDescribe("장소 설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + locationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String locationName = null;

	public static final String longitude_FIELDNAME = "longitude";
	@FieldDescribe("경도")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String longitude = null;

	public static final String latitude_FIELDNAME = "latitude";
	@FieldDescribe("위도")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String latitude = null;

	public static final String recurrenceStartTime_FIELDNAME = "recurrenceStartTime";
	@FieldDescribe("반복 시작 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + recurrenceStartTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date recurrenceStartTime = null;

	public static final String recurrenceRule_FIELDNAME = "recurrenceRule";
	@FieldDescribe("반복 날짜 표현식 RecurrenceRule")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recurrenceRule_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recurrenceRule = null;

	public static final String recurrenceExc_FIELDNAME = "recurrenceExc";
	@FieldDescribe("제외 날짜 표현식 RecurrenceExc")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recurrenceExc_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String recurrenceExc = null;

	public static final String alarm_FIELDNAME = "alarm";
	@FieldDescribe("알림 여부")
	@Column(name = ColumnNamePrefix + alarm_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean alarm = false;

	public static final String alarmTime_FIELDNAME = "alarmTime";
	@FieldDescribe("현재 이벤트 알림 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + alarmTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date alarmTime = null;

	public static final String valarmTime_config_FIELDNAME = "valarmTime_config";
	@FieldDescribe("알림 설정: 일, 시, 분, 초(음수는 사전 알림)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + valarmTime_config_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarmTime_config = "0,0,-10,0";

	public static final String valarm_mailto_FIELDNAME = "valarm_mailto";
	@FieldDescribe("알림 Email 주소, 비어 있으면 메일 알림 안 함")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + valarm_mailto_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_mailto = null;

	public static final String valarm_Summary_FIELDNAME = "valarm_Summary";
	@FieldDescribe("알림 표시 제목")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + valarm_Summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_Summary = null;

	public static final String valarm_description_FIELDNAME = "valarm_description";
	@FieldDescribe("알림 비고 정보")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + valarm_description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarm_description = null;

	public static final String isAllDayEvent_FIELDNAME = "isAllDayEvent";
	@FieldDescribe("종일 이벤트 여부")
	@Column(name = ColumnNamePrefix + isAllDayEvent_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean isAllDayEvent = false;

	public static final String daysOfDuration_FIELDNAME = "daysOfDuration";
	@FieldDescribe("이벤트 지속 일수: 0, 1 또는 N일")
	@Column(name = ColumnNamePrefix + daysOfDuration_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + daysOfDuration_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer daysOfDuration = 0;

	public static final String isPublic_FIELDNAME = "isPublic";
	@FieldDescribe("공개 이벤트 여부")
	@Column(name = ColumnNamePrefix + isPublic_FIELDNAME)
	private Boolean isPublic = false;

	public static final String source_FIELDNAME = "source";
	@FieldDescribe("정보 출처: PERSONAL| LEADER | UNIT | MEETING | BUSINESS_TRIP | HOLIDAY")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + source_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + source_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String source = "PERSONAL";

	public static final String createPerson_FIELDNAME = "createPerson";
	@FieldDescribe("작성자")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ createPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + createPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String createPerson = null;

	public static final String updatePerson_FIELDNAME = "updatePerson";
	@FieldDescribe("최종 수정자")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ updatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + updatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String updatePerson = null;

	public static final String targetType_FIELDNAME = "targetType";
	@FieldDescribe("참여자 유형: PERSON | UNIT | GROUP")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + targetType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetType = "PERSON";

	public static final String participants_FIELDNAME = "participants";
	@FieldDescribe("참여자 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + participants_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + participants_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ participants_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + participants_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> participants = null;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("관리 가능 사용자 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ manageablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageablePersonList;

	public static final String viewablePersonList_FIELDNAME = "viewablePersonList";
	@FieldDescribe("열람 가능 사용자 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewablePersonList;

	public static final String viewableUnitList_FIELDNAME = "viewableUnitList";
	@FieldDescribe("열람 가능 조직 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableUnitList;

	public static final String viewableGroupList_FIELDNAME = "viewableGroupList";
	@FieldDescribe("열람 가능 그룹 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ viewableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ viewableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ viewableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + viewableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> viewableGroupList;

	public static final String createdMonthList_FIELDNAME = "createdMonthList";
	@FieldDescribe("이미 생성된 월")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ createdMonthList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + createdMonthList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_255B, name = ColumnNamePrefix + createdMonthList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + createdMonthList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> createdMonthList;

	public static final String repeatStatus_FIELDNAME = "repeatStatus";
	@FieldDescribe("반복 상태: 생성 대기|생성 완료|삭제됨, 삭제된 항목은 더 이상 생성하지 않음")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + repeatStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + repeatStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String repeatStatus = "생성 대기";

	public Date getRecurrenceStartTime() {
		return recurrenceStartTime;
	}

	public void setRecurrenceStartTime(Date recurrenceStartTime) {
		this.recurrenceStartTime = recurrenceStartTime;
	}

	public String getCalendarId() {
		return calendarId;
	}

	public String getEventType() {
		return eventType;
	}

	public String getTitle() {
		return title;
	}

	public String getComment() {
		return comment;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public String getLocationName() {
		return locationName;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getRecurrenceRule() {
		return recurrenceRule;
	}

	public String getRecurrenceExc() {
		return recurrenceExc;
	}

	public String getValarm_mailto() {
		return valarm_mailto;
	}

	public String getValarm_Summary() {
		return valarm_Summary;
	}

	public String getValarm_description() {
		return valarm_description;
	}

	public Boolean getIsAllDayEvent() {
		return isAllDayEvent;
	}

	public String getSource() {
		return source;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public String getUpdatePerson() {
		return updatePerson;
	}

	public String getTargetType() {
		return targetType;
	}

	public String getCommentId() { return commentId; }

	public void setCommentId(String commentId) { this.commentId = commentId; }

	public List<String> getParticipants() {
		if (this.participants == null) {
			this.participants = new ArrayList<>();
		}
		return participants;
	}

	public List<String> getManageablePersonList() {
		if (this.manageablePersonList == null) {
			this.manageablePersonList = new ArrayList<>();
		}
		return manageablePersonList;
	}

	public List<String> getViewablePersonList() {
		if (this.viewablePersonList == null) {
			this.viewablePersonList = new ArrayList<>();
		}
		return viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		if (this.viewableUnitList == null) {
			this.viewableUnitList = new ArrayList<>();
		}
		return viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		if (this.viewableGroupList == null) {
			this.viewableGroupList = new ArrayList<>();
		}
		return viewableGroupList;
	}

	public List<String> getCreatedMonthList() {
		if (this.createdMonthList == null) {
			this.createdMonthList = new ArrayList<>();
		}
		return createdMonthList;
	}

	public String getRepeatStatus() {
		return repeatStatus;
	}

	public void setCalendarId(String calendarId) {
		this.calendarId = calendarId;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public void setRecurrenceRule(String recurrenceRule) {
		this.recurrenceRule = recurrenceRule;
	}

	public void setRecurrenceExc(String recurrenceExc) {
		this.recurrenceExc = recurrenceExc;
	}

	public void setValarm_mailto(String valarm_mailto) {
		this.valarm_mailto = valarm_mailto;
	}

	public void setValarm_Summary(String valarm_Summary) {
		this.valarm_Summary = valarm_Summary;
	}

	public void setValarm_description(String valarm_description) {
		this.valarm_description = valarm_description;
	}

	public void setIsAllDayEvent(Boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	public void setUpdatePerson(String updatePerson) {
		this.updatePerson = updatePerson;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setParticipants(List<String> participants) {
		this.participants = participants;
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
	}

	public void setCreatedMonthList(List<String> createdMonthList) {
		this.createdMonthList = createdMonthList;
	}

	public List<String> addParticipants(String participant) {
		getParticipants();
		if (!this.participants.contains(participant)) {
			this.participants.add(participant);
		}
		return participants;
	}

	public List<String> addManageablePerson(String manageablePerson) {
		getManageablePersonList();
		if (!this.manageablePersonList.contains(manageablePerson)) {
			this.manageablePersonList.add(manageablePerson);
		}
		return this.manageablePersonList;
	}

	public List<String> addViewablePerson(String viewablePerson) {
		getViewablePersonList();
		if (!this.viewablePersonList.contains(viewablePerson)) {
			this.viewablePersonList.add(viewablePerson);
		}
		return this.viewablePersonList;
	}

	public List<String> addViewableUnit(String viewableUnit) {
		getViewableUnitList();
		if (!this.viewableUnitList.contains(viewableUnit)) {
			this.viewableUnitList.add(viewableUnit);
		}
		return this.viewableUnitList;
	}

	public List<String> addViewableGroup(String viewableGroup) {
		getViewableGroupList();
		if (!this.viewableGroupList.contains(viewableGroup)) {
			this.viewableGroupList.add(viewableGroup);
		}
		return this.viewableGroupList;
	}

	public void setRepeatStatus(String repeatStatus) {
		this.repeatStatus = repeatStatus;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public String getValarmTime_config() {
		return valarmTime_config;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public void setValarmTime_config(String valarmTime_config) {
		this.valarmTime_config = valarmTime_config;
	}

	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getDaysOfDuration() {
		return daysOfDuration;
	}

	public void setDaysOfDuration(Integer daysOfDuration) {
		this.daysOfDuration = daysOfDuration;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * 이미 생성된 월 기록 추가: yyyy-mm
	 * 
	 * @param month
	 * @throws Exception
	 */
	public void addCreatedMonth(String month) throws Exception {
		if (StringUtils.isEmpty(month)) {
			throw new Exception("month can not be empty!");
		}
		if (createdMonthList == null) {
			createdMonthList = new ArrayList<>();
		}
		if (!createdMonthList.contains(month)) {
			createdMonthList.add(month);
		}
	}

	/**
	 * 특정 월이 이미 생성되었는지 확인
	 * 
	 * @param month
	 * @return
	 * @throws Exception
	 */
	public Boolean hasCreated(String month) throws Exception {
		if (StringUtils.isEmpty(month)) {
			throw new Exception("month can not be empty!");
		}
		if (createdMonthList == null) {
			createdMonthList = new ArrayList<>();
		}
		return createdMonthList.contains(month);
	}

	/**
	 * 하루 안에 끝나는 일정 또는 이벤트인지 여부
	 * 
	 * @return
	 */
	public boolean isWithinOneDay() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.startTime);
		final int startDay = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTime(this.endTime);
		final int endDay = cal.get(Calendar.DAY_OF_YEAR);
		return (endDay - startDay == 0);
	}

	/**
	 * 오늘의 일정 또는 이벤트인지 여부
	 * 
	 * @return
	 */
	public boolean isToday() {
		final Calendar cal = Calendar.getInstance();
		cal.setTime(this.startTime);
		final int startDay = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTime(this.endTime);
		final int endDay = cal.get(Calendar.DAY_OF_YEAR);
		final int todayDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		return (todayDay - startDay == 0) && ((todayDay - endDay == 0));
	}

	/**
	 * 새 일정 또는 이벤트 객체 복제 Calendar_Record
	 */
	@Override
	public Calendar_Event clone() {
		Object c = null;
		try {
			c = super.clone();
		} catch (final CloneNotSupportedException e) {
			return null;
		}
		return (Calendar_Event) c;
	}

	/**
	 * 제외 날짜 추가: 제외 날짜 표현식을 날짜 목록으로 변환한 뒤 제외 날짜를 추가하고 다시 표현식으로 변환
	 * 
	 * @param excDate
	 */
	public void addRecurrenceExc(final Date excDate) {
		// 표현식에 포함된 모든 날짜 목록 계산
		final List<Date> excDates = getRecurrenceExcludeDates(recurrenceExc);
		excDates.add(excDate);
		// 실행 시간을 표현식으로 변환
		final String excRule = getRecurrenceExcludeRule(excDates);
		setRecurrenceExc(excRule);
	}

	/**
	 * 반복 시간 표현식의 종료 날짜 조회
	 * @return
	 */
	public Date getRecurrenceEndDate() {
		final TimeZone tz = TimeZoneRegistryFactory.getInstance().createRegistry()
				.getTimeZone(java.util.Calendar.getInstance().getTimeZone().getID());
		if (recurrenceRule != null) {
			try {
				final Recur recur = new Recur(recurrenceRule);
				final Date dUntil = recur.getUntil();
				final DateTime dtUntil = dUntil == null ? null : new DateTime(dUntil.getTime());
				if (dtUntil != null) {
					dtUntil.setTimeZone(tz);
					return dtUntil;
				}
			} catch (final ParseException e) {
				System.out.println("cannot restore recurrence rule");
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 일정 이벤트의 다음 실행 이벤트 조회
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event getRecurringInPeriod(final Date periodStart, final Date periodEnd) throws Exception {
		final boolean isRecurring = isRecurringInPeriod(periodStart, periodEnd);
		Calendar_Event recurEvent = null;
		if (isRecurring) {
			final java.util.Calendar periodStartCal = java.util.Calendar.getInstance();
			final java.util.Calendar eventBeginCal = java.util.Calendar.getInstance();

			periodStartCal.setTime(periodStart);
			eventBeginCal.setTime(getStartTime());

			final Long duration = getEndTime().getTime() - getStartTime().getTime();

			final java.util.Calendar beginCal = java.util.Calendar.getInstance();
			beginCal.setTime(getStartTime());
			beginCal.set(java.util.Calendar.YEAR, periodStartCal.get(java.util.Calendar.YEAR));
			beginCal.set(java.util.Calendar.MONTH, periodStartCal.get(java.util.Calendar.MONTH));
			beginCal.set(java.util.Calendar.DAY_OF_MONTH, periodStartCal.get(java.util.Calendar.DAY_OF_MONTH));

			recurEvent = clone();
			recurEvent.setStartTime(beginCal.getTime());
			recurEvent.setEndTime(new Date(beginCal.getTime().getTime() + duration));
		}

		return recurEvent;
	}

	/**
	 * 이벤트가 지정한 시간 범위 안에 있는지 확인
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 * @throws Exception
	 */
	private boolean isRecurringInPeriod(final Date periodStart, final Date periodEnd) throws Exception {
		final DateList recurDates = getRecurringsInPeriod(periodStart, periodEnd);
		return (recurDates != null && !recurDates.isEmpty());
	}

	/**
	 * 지정 시간 범위 내 일정 이벤트의 실행 시간 목록 조회
	 * 
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 * @throws Exception
	 */
	private DateList getRecurringsInPeriod(final Date periodStart, final Date periodEnd) throws Exception {
		DateList recurDates = null;
		final String recurrenceRule = getRecurrenceRule();
		if (StringUtils.isNotEmpty(recurrenceRule)) {
			try {
				final Recur recur = new Recur(recurrenceRule);
				final net.fortuna.ical4j.model.Date periodStartDate = new net.fortuna.ical4j.model.Date(periodStart);
				final net.fortuna.ical4j.model.Date periodEndDate = new net.fortuna.ical4j.model.Date(periodEnd);
				final net.fortuna.ical4j.model.Date eventStartDate = new net.fortuna.ical4j.model.Date(getStartTime());
				recurDates = recur.getDates(eventStartDate, periodStartDate, periodEndDate, Value.DATE);
			} catch (final ParseException e) {
				System.out.println("cannot restore recurrence rule: " + recurrenceRule);
				e.printStackTrace();
			}

			// 제외 날짜
			final String recurrenceExc = getRecurrenceExc();
			if ( StringUtils.isNotEmpty( recurrenceExc )) {
				try {
					final ExDate exdate = new ExDate();
					if (recurrenceExc.length() > 8) {
						exdate.setValue(recurrenceExc);
					} else {
						exdate.getParameters().replace(Value.DATE);
						exdate.setValue(recurrenceExc);
					}
					DateList dateList = exdate.getDates();
					for (final Object date : dateList) {
						if (recurDates.contains(date)) {
							recurDates.remove(date);
						}
					}
				} catch (final ParseException e) {
					System.out.println("cannot restore excluded dates for this recurrence " + recurrenceRule);
					e.printStackTrace();
				}
			}
		}
		return recurDates;
	}

	/**
	 * 반복 일정 이벤트의 지정한 시간 범위 내 모든 일정 이벤트 목록 계산
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Event> getRecurringDatesInPeriod(final Date periodStart, final Date periodEnd)
			throws Exception {
		final List<Calendar_Event> lstDates = new ArrayList<Calendar_Event>();
		final DateList recurDates = getRecurringsInPeriod(periodStart, periodEnd);
		if (recurDates == null) {
			LogUtil.INFO("sorry, recurDates is null");
			return lstDates;
		}
		for (final Object obj : recurDates) {
			final net.fortuna.ical4j.model.Date date = (net.fortuna.ical4j.model.Date) obj;

			Calendar_Event recurEvent;

			final java.util.Calendar eventStartCal = java.util.Calendar.getInstance();
			eventStartCal.clear();
			eventStartCal.setTime(getStartTime());

			final java.util.Calendar eventEndCal = java.util.Calendar.getInstance();
			eventEndCal.clear();
			eventEndCal.setTime(getEndTime());

			final java.util.Calendar recurStartCal = java.util.Calendar.getInstance();
			recurStartCal.clear();
			recurStartCal.setTimeInMillis(date.getTime());

			// 단일 이벤트 지속 시간
			final long duration = getEndTime().getTime() - getStartTime().getTime();

			final java.util.Calendar beginCal = java.util.Calendar.getInstance();
			beginCal.clear();

			beginCal.set(recurStartCal.get(java.util.Calendar.YEAR), recurStartCal.get(java.util.Calendar.MONTH),
					recurStartCal.get(java.util.Calendar.DAY_OF_MONTH),
					eventStartCal.get(java.util.Calendar.HOUR_OF_DAY), eventStartCal.get(java.util.Calendar.MINUTE),
					eventStartCal.get(java.util.Calendar.SECOND));

			final java.util.Calendar endCal = java.util.Calendar.getInstance();
			endCal.clear();
			endCal.setTimeInMillis(beginCal.getTimeInMillis() + duration);

			final Date recurrenceEnd = getRecurrenceEndDate();
			if (getIsAllDayEvent() && recurrenceEnd != null && recurStartCal.getTime().after(recurrenceEnd)) {
				continue; // workaround for ical4j-bug in all day events
			}

			recurEvent = new Calendar_Event(getId(), getTitle(), new Date(beginCal.getTimeInMillis()),
					new Date(endCal.getTimeInMillis()));
			this.copyTo(recurEvent, JpaObject.FieldsUnmodify);
			recurEvent.setId(Calendar_Event.createId());
			recurEvent.setStartTime(new Date(beginCal.getTimeInMillis()));
			recurEvent.setEndTime(new Date(endCal.getTimeInMillis()));

			recurEvent.setStartTimeStr(dateOperation.getDateFromDate(recurEvent.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			recurEvent.setEndTimeStr(dateOperation.getDateFromDate(recurEvent.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			recurEvent.setRepeatMasterId(this.id);
			recurEvent.setIsAllDayEvent(this.isAllDayEvent);
			recurEvent.setDaysOfDuration(this.daysOfDuration);
			lstDates.add(recurEvent);
		}
		return lstDates;
	}

	/**
	 * Create list with excluded dates based on the exclusion rule.
	 * 
	 * @param recurrenceExc
	 * @return list with excluded dates
	 */
	private List<Date> getRecurrenceExcludeDates(final String recurrenceExc) {
		final List<Date> recurExcDates = new ArrayList<Date>();
		if ( StringUtils.isNotEmpty( recurrenceExc )) {
			try {
				final net.fortuna.ical4j.model.ParameterList pl = new net.fortuna.ical4j.model.ParameterList();
				final ExDate exdate = new ExDate(pl, recurrenceExc);
				final DateList dl = exdate.getDates();
				for (final Object date : dl) {
					final Date excDate = (Date) date;
					recurExcDates.add(excDate);
				}
			} catch (final ParseException e) {
				System.out.println("cannot restore recurrence exceptions");
				e.printStackTrace();
			}
		}
		return recurExcDates;
	}

	/**
	 * 지정한 날짜 목록을 제외 표현식으로 변환 Create exclusion rule based on list with dates.
	 * 
	 * @param dates
	 * @return string with exclude rule
	 */
	private static String getRecurrenceExcludeRule(final List<Date> dates) {
		if (ListTools.isNotEmpty( dates )) {
			final DateList dl = new DateList();
			for (final Date date : dates) {
				final net.fortuna.ical4j.model.Date dd = new net.fortuna.ical4j.model.Date(date);
				dl.add(dd);
			}
			final ExDate exdate = new ExDate(dl);
			return exdate.getValue();
		}
		return null;
	}

	/**
	 * 일정 이벤트 비교, 정렬용
	 */
	@Override
	public int compareTo(final Object o1) {
		if (!(o1 instanceof Calendar_Event)) {
			return -1;
		}
		final Calendar_Event event1 = (Calendar_Event) o1;
		return this.getStartTime().compareTo(event1.getStartTime());
	}
}