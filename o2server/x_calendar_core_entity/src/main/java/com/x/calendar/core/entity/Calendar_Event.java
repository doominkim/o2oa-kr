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

import io.swagger.v3.oas.annotations.media.Schema;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.ExDate;

/**
 * 일정 이벤트 정보
 * 
 * @author O2LEE
 *
 */
@Schema(name = "Calendar_Event", description = "일정 이벤트 정보.")
@SuppressWarnings("rawtypes")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Calendar_Event.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Calendar_Event.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Calendar_Event extends SliceJpaObject implements Cloneable, Comparable {

	private static final long serialVersionUID = 1325197931747463979L;
	private static final String TABLE = PersistenceProperties.Calendar_Event.table;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;

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
	public Calendar_Event() {
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final Date endTime) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = endTime;
		this.isAllDayEvent = false;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final int duration) {
		this.id = id;
		this.title = title;
		this.startTime = startTime;
		this.endTime = new Date(startTime.getTime() + duration);
		this.isAllDayEvent = false;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final int duration,
			final String recurrenceRule) {
		this(id, title, startTime, duration);
		this.recurrenceRule = recurrenceRule;
	}

	public Calendar_Event(final String id, final String title, final Date startTime, final Date endTime,
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

	public static final String repeatMasterId_FIELDNAME = "repeatMasterId";
	@FieldDescribe("반복 일정 호스트 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + repeatMasterId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + repeatMasterId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String repeatMasterId = null;

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

	public static final String startTimeStr_FIELDNAME = "startTimeStr";
	@FieldDescribe("이벤트 시작 시간(yyyy-MM-dd HH:mm:ss)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + startTimeStr_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String startTimeStr = null;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("이벤트 종료 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + endTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date endTime = null;

	public static final String endTimeStr_FIELDNAME = "endTimeStr";
	@FieldDescribe("이벤트 종료 시간(yyyy-MM-dd HH:mm:ss)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + endTimeStr_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endTimeStr_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String endTimeStr = null;

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

	public static final String alarmAlready_FIELDNAME = "alarmAlready";
	@FieldDescribe("이미 알림 여부")
	@Column(name = ColumnNamePrefix + alarmAlready_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean alarmAlready = false;

	public static final String valarmTime_config_FIELDNAME = "valarmTime_config";
	@FieldDescribe("알림 설정: 일, 시, 분, 초(음수는 사전 알림)")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + valarmTime_config_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String valarmTime_config = "0,0,0,0";

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

	public static final String bundle_FIELDNAME = "bundle";
	@FieldDescribe("바인딩 출처 데이터 식별자")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + bundle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + bundle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String bundle;

	public static final String createPerson_FIELDNAME = "createPerson";
	@FieldDescribe("작성자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + createPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + createPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String createPerson = null;

	public static final String updatePerson_FIELDNAME = "updatePerson";
	@FieldDescribe("최종 수정자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + updatePerson_FIELDNAME)
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

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
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

	public String getRepeatMasterId() {
		return repeatMasterId;
	}

	public void setRepeatMasterId(String repeatMasterId) {
		this.repeatMasterId = repeatMasterId;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getValarmTime_config() {
		return valarmTime_config;
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

	public Boolean getAlarmAlready() {
		return alarmAlready;
	}

	public void setAlarmAlready(Boolean alarmAlready) {
		this.alarmAlready = alarmAlready;
	}

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCommentId() { return commentId; }

	public void setCommentId(String commentId) { this.commentId = commentId; }

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
	 * 이벤트가 지정 시간 범위 내에 있는지 판단 Check if the event recurs within the given period
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 */
	public boolean isRecurringInPeriod(final Date periodStart, final Date periodEnd) {
		final DateList recurDates = getRecurringsInPeriod(periodStart, periodEnd);
		return (recurDates != null && !recurDates.isEmpty());
	}

	/**
	 * 지정 시간 범위 내 일정 이벤트의 실행 시간 목록 조회
	 * 
	 * @param periodStart
	 * @param periodEnd
	 * @return
	 */
	private DateList getRecurringsInPeriod(final Date periodStart, final Date periodEnd) {
		DateList recurDates = null;
		final String recurrenceRule = getRecurrenceRule();
		if (recurrenceRule != null && !recurrenceRule.equals("")) {
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

			final String recurrenceExc = getRecurrenceExc();
			if (recurrenceExc != null && !recurrenceExc.equals("")) {
				try {
					final ExDate exdate = new ExDate();
					if (recurrenceExc.length() > 8) {
						exdate.setValue(recurrenceExc);
					} else {
						exdate.getParameters().replace(Value.DATE);
						exdate.setValue(recurrenceExc);
					}
					for (final Object date : exdate.getDates()) {
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