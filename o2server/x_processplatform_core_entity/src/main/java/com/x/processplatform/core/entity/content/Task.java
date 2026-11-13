package com.x.processplatform.core.entity.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.element.Route;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Task", description = "프로세스 플랫폼 결재 대기.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Task.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Task.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Task extends SliceJpaObject implements ProjectionInterface {

	private static final long serialVersionUID = -5448210797584958826L;
	private static final String TABLE = PersistenceProperties.Content.Task.table;

	public static final String ACT_CREATE = "create";
	public static final String ACT_RESET = "reset";
	public static final String ACT_ADD = "add";

	public static final Long DEFAULT_ORDERNUMBER = 0L;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("결재 대기 식별자.")
	@Schema(description = "결재 대기 식별자.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	/* 위는 JpaObject 기본 필드 */

	public void onPersist() throws Exception {
		if (StringUtils.isEmpty(this.startTimeMonth) && (null != this.startTime)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
		if (StringTools.utf8Length(this.getProperties().getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getProperties().getTitle(), length_255B - 3) + "...";
		}
		if (StringTools.utf8Length(this.getProperties().getOpinion()) > length_255B) {
			this.opinion = StringTools.utf8SubString(this.getProperties().getOpinion(), length_255B - 3) + "...";
		}
		// 추가 결재에서 routeName은 추가 결재자 표시에 사용, 너무 길면 저장할 수 없음.
		if (StringTools.utf8Length(this.getRouteName()) > length_255B) {
			this.routeName = StringTools.utf8SubString(this.getRouteName(), length_255B - 3) + "...";
		}
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			if (StringUtils.isNotEmpty(this.getProperties().getTitle())) {
				this.title = this.properties.getTitle();
			}
			if (StringUtils.isNotEmpty(this.getProperties().getOpinion())) {
				this.opinion = this.properties.getOpinion();
			}
			this.routeNameDisable = this.properties.getRouteNameDisable();
			this.prevTaskIdentity = this.properties.getPrevTaskIdentity();
			this.prevTaskIdentityList = this.properties.getPrevTaskIdentityList();
			this.act = this.properties.getAct();
			this.actPerson = this.properties.getActPerson();
		}
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
		this.getProperties().setOpinion(opinion);
	}

	public String getOpinion() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getOpinion())) {
			return this.properties.getOpinion();
		} else {
			return this.opinion;
		}
	}

	public void setTitle(String title) {
		this.title = title;
        if (StringTools.utf8Length(title) > length_255B) {
            this.title = StringTools.utf8SubString(title, length_255B - 3) + "...";
        }
		this.getProperties().setTitle(title);
	}

	public String getTitle() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getTitle())) {
			return this.properties.getTitle();
		} else {
			return this.title;
		}
	}

	// 실행 메서드 업데이트

	public Task() {
		this.properties = new TaskProperties();
	}

	public Task(Work work, String act, String distinguishedName, String person, String unit, String empowerFromIdentity,
			Date startTime, Date expireTime, List<Route> routes, Boolean allowRapid) {
		this();
		this.job = work.getJob();
		this.setTitle(work.getTitle());
		this.startTime = startTime;
		this.work = work.getId();
		this.application = work.getApplication();
		this.applicationName = work.getApplicationName();
		this.applicationAlias = work.getApplicationAlias();
		this.process = work.getProcess();
		this.processName = work.getProcessName();
		this.processAlias = work.getProcessAlias();
		this.serial = work.getSerial();
		this.person = person;
		this.identity = distinguishedName;
		this.distinguishedName = distinguishedName;
		this.unit = unit;
		this.empowerFromIdentity = empowerFromIdentity;
		this.activity = work.getActivity();
		this.activityName = work.getActivityName();
		this.activityAlias = work.getActivityAlias();
		this.activityDescription = work.getActivityDescription();
		this.activityType = work.getActivityType();
		this.activityToken = work.getActivityToken();
		this.creatorPerson = work.getCreatorPerson();
		this.creatorIdentity = work.getCreatorIdentity();
		this.creatorUnit = work.getCreatorUnit();
		this.workCreateType = work.getWorkCreateType();
		this.expireTime = expireTime;
		this.routeNameDisable = false;
		this.routeName = "";
		this.routeAlias = "";
		this.opinion = "";
		this.modified = false;
		this.allowRapid = allowRapid;
		this.copyProjectionFields(work);
		updateRoute(routes);
		// 반드시 이 메서드 사용. act는 Transient 객체
		this.setAct(act);
	}

	public Task updateRoute(List<Route> routes) {
		this.routeList = new ArrayList<>();
		this.routeNameList = new ArrayList<>();
		this.routeOpinionList = new ArrayList<>();
		this.routeDecisionOpinionList = new ArrayList<>();
		if (ListTools.isNotEmpty(routes)) {
			routes.stream().sorted(Comparator.comparing(Route::getOrderNumber, Comparator.nullsLast(Integer::compareTo))
					.thenComparing(Route::getUpdateTime, Date::compareTo)).forEach(o -> {
						this.routeList.add(o.getId());
						this.routeNameList.add(o.getName());
						this.routeOpinionList.add(StringUtils.trimToEmpty(o.getOpinion()));
						this.routeDecisionOpinionList.add(StringUtils.trimToEmpty(o.getDecisionOpinion()));
					});
		}
		return this;
	}

	public static final String ROUTENAMEDISABLE_FIELDNAME = "routeNameDisable";
	@Transient
	@FieldDescribe("결재 대기의 routeName 비활성 여부, 반려 결재 대기에서 way=jump로 설정하면 바로 이동하므로 routeName 불필요.")
	private Boolean routeNameDisable;

	public Boolean getRouteNameDisable() {
		if ((null == this.routeNameDisable) && (null != this.properties)) {
			this.routeNameDisable = this.properties.getRouteNameDisable();
		}
		return this.routeNameDisable;
	}

	public void setRouteNameDisable(Boolean routeNameDisable) {
		this.routeNameDisable = routeNameDisable;
		this.getProperties().setRouteNameDisable(routeNameDisable);
	}

	public static final String PREVTASKIDENTITYLIST_FIELDNAME = "prevTaskIdentityList";
	@Transient
	@FieldDescribe("이전 수동 단계 처리자 목록.")
	private List<String> prevTaskIdentityList;

	public List<String> getPrevTaskIdentityList() {
		if ((null != this.properties) && (null == this.prevTaskIdentityList)) {
			this.prevTaskIdentityList = this.properties.getPrevTaskIdentityList();
		}
		return this.prevTaskIdentityList;
	}

	public void setPrevTaskIdentityList(List<String> prevTaskIdentityList) {
		this.getProperties().setPrevTaskIdentityList(prevTaskIdentityList);
		this.prevTaskIdentityList = prevTaskIdentityList;
	}

	public static final String PREVTASKIDENTITY_FIELDNAME = "prevTaskIdentity";
	@Transient
	@FieldDescribe("이전 수동 단계 처리자.")
	private String prevTaskIdentity;

	public String getPrevTaskIdentity() {
		if ((null != this.properties) && (null == this.prevTaskIdentity)) {
			this.prevTaskIdentity = this.properties.getPrevTaskIdentity();
		}
		return this.prevTaskIdentity;
	}

	public void setPrevTaskIdentity(String prevTaskIdentity) {
		this.getProperties().setPrevTaskIdentity(prevTaskIdentity);
		this.prevTaskIdentity = prevTaskIdentity;
	}

	public static final String ACT_FIELDNAME = "act";
	@Transient
	@FieldDescribe("Ticket 생성 방식, create, reset, add.")
	private String act;

	public String getAct() {
		if ((null != this.properties) && (null == this.act)) {
			this.act = this.properties.getAct();
		}
		return this.act;
	}

	public void setAct(String act) {
		this.getProperties().setAct(act);
		this.act = act;
	}

	public static final String ACTPERSON_FIELDNAME = "actPerson";
	@Transient
	@FieldDescribe("작업자, 추가 결재에서 추가 결재를 실행한 사람 기록")
	private String actPerson;

	public String getActPerson() {
		if ((null != this.properties) && (null == this.actPerson)) {
			this.actPerson = this.properties.getActPerson();
		}
		return this.actPerson;
	}

	public void setActPerson(String actPerson) {
		this.getProperties().setActPerson(actPerson);
		this.actPerson = actPerson;
	}

	public TaskProperties getProperties() {
		if (null == this.properties) {
			this.properties = new TaskProperties();
		}
		return this.properties;
	}

	public void setProperties(TaskProperties properties) {
		this.properties = properties;
	}

	public static final String job_FIELDNAME = "job";
	@Schema(description = "작업 식별자.")
	@FieldDescribe("작업.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String title_FIELDNAME = "title";
	@Schema(description = "업무 문서 제목.")
	@FieldDescribe("제목.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String startTime_FIELDNAME = "startTime";
	@Schema(description = "결재 대기 시작 시간.")
	@FieldDescribe("시작 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	// 시작 시간은 비어 있으면 안 됨, 비어 있으면 정렬 오류 가능
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String startTimeMonth_FIELDNAME = "startTimeMonth";
	@Schema(description = "결재 대기 시작 시간의 연월 텍스트 값, Filter 분류에 사용.")
	@FieldDescribe("Filter에서 분류에 사용.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + startTimeMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTimeMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

	public static final String work_FIELDNAME = "work";
	@Schema(description = "업무 문서 식별자.")
	@FieldDescribe("업무 문서 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + work_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + work_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String work;

	public static final String application_FIELDNAME = "application";
	@Schema(description = "프로세스 앱 식별자.")
	@FieldDescribe("앱.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@Schema(description = "프로세스 앱 이름.")
	@FieldDescribe("앱 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String applicationAlias_FIELDNAME = "applicationAlias";
	@Schema(description = "프로세스 앱 별칭.")
	@FieldDescribe("앱 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String process_FIELDNAME = "process";
	@Schema(description = "프로세스 식별자.")
	@FieldDescribe("프로세스 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@Schema(description = "프로세스 이름.")
	@FieldDescribe("프로세스 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String processAlias_FIELDNAME = "processAlias";
	@Schema(description = "프로세스 별칭.")
	@FieldDescribe("프로세스 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String serial_FIELDNAME = "serial";
	@Schema(description = "번호.")
	@FieldDescribe("번호")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + serial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + serial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serial;

	public static final String person_FIELDNAME = "person";
	@Schema(description = "현재 처리자.")
	@FieldDescribe("현재 처리자")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String identity_FIELDNAME = "identity";
	@Schema(description = "현재 처리자 신분.")
	@FieldDescribe("현재 처리자 Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + identity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + identity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String identity;

	public static final String unit_FIELDNAME = "unit";
	@Schema(description = "현재 처리자 신분 소속 조직.")
	@FieldDescribe("현재 처리자 소속 조직.")
	@Column(length = length_255B, name = ColumnNamePrefix + unit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String unit;

	public static final String empowerFromIdentity_FIELDNAME = "empowerFromIdentity";
	@Schema(description = "위임 원 신분.")
	@FieldDescribe("위임 원 Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + empowerFromIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + empowerFromIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String empowerFromIdentity;

	public static final String activity_FIELDNAME = "activity";
	@Schema(description = "활동 식별자.")
	@FieldDescribe("활동 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activity;

	public static final String activityName_FIELDNAME = "activityName";
	@Schema(description = "활동 이름.")
	@FieldDescribe("활동 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityAlias_FIELDNAME = "activityAlias";
	@Schema(description = "활동 별칭.")
	@FieldDescribe("활동 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityAlias;

	public static final String activityDescription_FIELDNAME = "activityDescription";
	@Schema(description = "활동 설명.")
	@FieldDescribe("활동 설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityDescription_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityDescription;

	public static final String activityType_FIELDNAME = "activityType";
	@Schema(description = "활동 유형.")
	@FieldDescribe("활동 유형.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ActivityType activityType;

	public static final String activityToken_FIELDNAME = "activityToken";
	@Schema(description = "활동 토큰.")
	@FieldDescribe("활동 Token.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@Schema(description = "업무 문서 생성 사용자.")
	@FieldDescribe("생성자")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@Schema(description = "업무 문서 생성 사용자 신분.")
	@FieldDescribe("작성자 Identity")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@Schema(description = "업무 문서 생성 사용자 신분 소속 부서.")
	@FieldDescribe("생성자 부서")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;

	public static final String expireTime_FIELDNAME = "expireTime";
	@Schema(description = "결재 대기 마감 시간.")
	@FieldDescribe("작업 마감 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Index(name = TABLE + IndexNameMiddle + expireTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
	private Date expireTime;

	public static final String expired_FIELDNAME = "expired";
	@Schema(description = "결재 대기 마감 시간 초과 여부.")
	@FieldDescribe("시간 초과 여부.")
	@Column(name = ColumnNamePrefix + expired_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean expired;

	public static final String urgeTime_FIELDNAME = "urgeTime";
	@Schema(description = "결재 대기 마감 시간 도래 전 독촉 시간.")
	@FieldDescribe("독촉 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + urgeTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + urgeTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date urgeTime;

	public static final String urged_FIELDNAME = "urged";
	@Schema(description = "독촉 진행 여부.")
	@FieldDescribe("독촉 완료 여부.")
	@Column(name = ColumnNamePrefix + urged_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean urged;

	public static final String routeList_FIELDNAME = "routeList";
	@Schema(description = "결재 대기에서 선택 가능한 경로 식별자 목록.")
	@FieldDescribe("현재 활동에서 선택 가능한 경로.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + routeList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + routeList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeList = new ArrayList<>();

	public static final String routeNameList_FIELDNAME = "routeNameList";
	@Schema(description = "결재 대기에서 선택 가능한 경로 이름 목록.")
	@FieldDescribe("현재 활동에서 선택 가능한 경로 이름.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeNameList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + routeNameList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeNameList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeNameList = new ArrayList<>();

	public static final String routeOpinionList_FIELDNAME = "routeOpinionList";
	@Schema(description = "결재 대기에서 선택 가능한 경로별 기본 의견 목록.")
	@FieldDescribe("현재 활동에서 선택 가능한 경로별 기본 의견.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeOpinionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + routeOpinionList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeOpinionList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeOpinionList = new ArrayList<>();

	public static final String routeDecisionOpinionList_FIELDNAME = "routeDecisionOpinionList";
	@Schema(description = "결재 대기 경로 그룹 목록, 다중 값은 #로 구분.")
	@FieldDescribe("결정 의견 목록, #로 구분.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ routeDecisionOpinionList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ routeDecisionOpinionList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + routeDecisionOpinionList_FIELDNAME)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@CheckPersist(allowEmpty = true)
	private List<String> routeDecisionOpinionList = new ArrayList<>();

	public static final String ROUTENAME_FIELDNAME = "routeName";
	@Schema(description = "결재 대기에서 선택한 경로 이름.")
	@FieldDescribe("선택한 경로 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + ROUTENAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeName;

	public static final String ROUTEALIAS_FIELDNAME = "routeAlias";
	@Schema(description = "결재 대기에서 선택한 경로 별칭.")
	@FieldDescribe("결재 대기에서 선택한 경로 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + ROUTEALIAS_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String routeAlias;

	public static final String opinion_FIELDNAME = "opinion";
	@Schema(description = "결재 대기 처리 의견.")
	@FieldDescribe("처리 의견.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + opinion_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + opinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	protected String opinion;

	public static final String opinionLob_FIELDNAME = "opinionLob";
	@Schema(description = "결재 대기 처리 의견 긴 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + opinionLob_FIELDNAME)
	private String opinionLob;

	public static final String modified_FIELDNAME = "modified";
	@Schema(description = "프런트엔드 데이터 저장 여부.")
	@FieldDescribe("프런트에서 데이터 저장 여부.")
	@Column(name = ColumnNamePrefix + modified_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean modified;

	public static final String VIEWTIME_FIELDNAME = "viewTime";
	@Schema(description = "최초 조회 시간.")
	@FieldDescribe("최초 조회 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + VIEWTIME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date viewTime;

	public static final String allowRapid_FIELDNAME = "allowRapid";
	@Schema(description = "빠른 처리 허용 여부.")
	@FieldDescribe("빠른 처리 허용.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + allowRapid_FIELDNAME)
	private Boolean allowRapid;

	public static final String mediaOpinion_FIELDNAME = "mediaOpinion";
	@Schema(description = "멀티미디어 결재 대기 처리 의견.")
	@FieldDescribe("멀티미디어 의견.")
	@Column(length = length_255B, name = ColumnNamePrefix + mediaOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mediaOpinion;

	public static final String first_FIELDNAME = "first";
	@Schema(description = "첫 결재 대기 여부, 결재 대기와 초안 구분에 사용.")
	@FieldDescribe("첫 결재 대기 여부, 결재 대기와 초안 구분에 사용.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + first_FIELDNAME)
	private Boolean first;

	public static final String properties_FIELDNAME = "properties";
	@Schema(description = "속성 저장 필드.")
	@FieldDescribe("속성 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private TaskProperties properties;

	public static final String series_FIELDNAME = "series";
	@Schema(description = "작업 일련번호, 같은 작업은 같은 일련번호를 가짐.")
	@FieldDescribe("작업 일련번호, 같은 작업은 같은 일련번호를 가짐.")
	@Column(length = length_id, name = ColumnNamePrefix + series_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + series_FIELDNAME)
	private String series;

	public static final String pause_FIELDNAME = "pause";
	@Schema(description = "결재 대기의 보류(시간 측정 일시 중지) 상태 여부.")
	@FieldDescribe("결재 대기의 보류(시간 측정 일시 중지) 상태 여부.")
	@Column(name = ColumnNamePrefix + pause_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean pause;

	public static final String workCreateType_FIELDNAME = "workCreateType";
	@Schema(description = "업무 문서 생성 유형, surface, assign.")
	@FieldDescribe("업무 문서 생성 유형, surface, assign.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workCreateType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCreateType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCreateType;

	public static final String LABEL_FIELDNAME = "label";
	@Schema(description = "결재 대기 증빙 식별자.")
	@FieldDescribe("결재 대기 증빙 식별자.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + LABEL_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + LABEL_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String label;

	public static final String DISTINGUISHEDNAME_FIELDNAME = "distinguishedName";
	@Schema(description = "처리 대상.")
	@FieldDescribe("처리 대상.")
	@Column(length = length_255B, name = ColumnNamePrefix + DISTINGUISHEDNAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + DISTINGUISHEDNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distinguishedName;

	public static final String FROMDISTINGUISHEDNAME_FIELDNAME = "fromDistinguishedName";
	@Schema(description = "위임 처리 대상.")
	@FieldDescribe("위임 처리 대상.")
	@Column(length = length_255B, name = ColumnNamePrefix + FROMDISTINGUISHEDNAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMDISTINGUISHEDNAME_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fromDistinguishedName;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 내림차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Long orderNumber;

	public static final String stringValue01_FIELDNAME = "stringValue01";
	@Schema(description = "업무 데이터 String 값01.")
	@FieldDescribe("업무 데이터 String 값01.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue01;

	public static final String stringValue02_FIELDNAME = "stringValue02";
	@Schema(description = "업무 데이터 String 값02.")
	@FieldDescribe("업무 데이터 String 값02.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue02;

	public static final String stringValue03_FIELDNAME = "stringValue03";
	@Schema(description = "업무 데이터 String 값03.")
	@FieldDescribe("업무 데이터 String 값03.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue03;

	public static final String stringValue04_FIELDNAME = "stringValue04";
	@Schema(description = "업무 데이터 String 값04.")
	@FieldDescribe("업무 데이터 String 값04.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue04;

	public static final String stringValue05_FIELDNAME = "stringValue05";
	@Schema(description = "업무 데이터 String 값05.")
	@FieldDescribe("업무 데이터 String 값05.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue05;

	public static final String stringValue06_FIELDNAME = "stringValue06";
	@Schema(description = "업무 데이터 String 값06.")
	@FieldDescribe("업무 데이터 String 값06.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue06_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue06_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue06;

	public static final String stringValue07_FIELDNAME = "stringValue07";
	@Schema(description = "업무 데이터 String 값07.")
	@FieldDescribe("업무 데이터 String 값07.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue07_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue07_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue07;

	public static final String stringValue08_FIELDNAME = "stringValue08";
	@Schema(description = "업무 데이터 String 값08.")
	@FieldDescribe("업무 데이터 String 값08.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue08_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue08_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue08;

	public static final String stringValue09_FIELDNAME = "stringValue09";
	@Schema(description = "업무 데이터 String 값09.")
	@FieldDescribe("업무 데이터 String 값09.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue09_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue09_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue09;

	public static final String stringValue10_FIELDNAME = "stringValue10";
	@Schema(description = "업무 데이터 String 값10.")
	@FieldDescribe("업무 데이터 String 값10.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue10_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue10_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue10;

	public static final String doubleValue01_FIELDNAME = "doubleValue01";
	@Schema(description = "업무 데이터 Double 값01.")
	@FieldDescribe("업무 데이터 Double 값01.")
	@Column(name = ColumnNamePrefix + doubleValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue01;

	public static final String doubleValue02_FIELDNAME = "doubleValue02";
	@Schema(description = "업무 데이터 Double 값02.")
	@FieldDescribe("업무 데이터 Double 값02.")
	@Column(name = ColumnNamePrefix + doubleValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue02;

	public static final String doubleValue03_FIELDNAME = "doubleValue03";
	@Schema(description = "업무 데이터 Double 값 03.")
	@FieldDescribe("업무 데이터 Double 값 03.")
	@Column(name = ColumnNamePrefix + doubleValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue03;

	public static final String doubleValue04_FIELDNAME = "doubleValue04";
	@Schema(description = "업무 데이터 Double 값 04.")
	@FieldDescribe("업무 데이터 Double 값 04.")
	@Column(name = ColumnNamePrefix + doubleValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue04;

	public static final String doubleValue05_FIELDNAME = "doubleValue05";
	@Schema(description = "업무 데이터 Double 값 05.")
	@FieldDescribe("업무 데이터 Double 값 05.")
	@Column(name = ColumnNamePrefix + doubleValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue05;

	public static final String longValue01_FIELDNAME = "longValue01";
	@Schema(description = "업무 데이터 Long 값01.")
	@FieldDescribe("업무 데이터 Long 값01.")
	@Column(name = ColumnNamePrefix + longValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue01;

	public static final String longValue02_FIELDNAME = "longValue02";
	@Schema(description = "업무 데이터 Long 값02.")
	@FieldDescribe("업무 데이터 Long 값02.")
	@Column(name = ColumnNamePrefix + longValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue02;

	public static final String longValue03_FIELDNAME = "longValue03";
	@Schema(description = "업무 데이터 Long 값 03.")
	@FieldDescribe("업무 데이터 Long 값 03.")
	@Column(name = ColumnNamePrefix + longValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue03;

	public static final String longValue04_FIELDNAME = "longValue04";
	@Schema(description = "업무 데이터 Long 값 04.")
	@FieldDescribe("업무 데이터 Long 값 04.")
	@Column(name = ColumnNamePrefix + longValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue04;

	public static final String longValue05_FIELDNAME = "longValue05";
	@Schema(description = "업무 데이터 Long 값 05.")
	@FieldDescribe("업무 데이터 Long 값 05.")
	@Column(name = ColumnNamePrefix + longValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue05;

	public static final String dateTimeValue01_FIELDNAME = "dateTimeValue01";
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "업무 데이터 DateTime 값01.")
	@FieldDescribe("업무 데이터 DateTime 값01.")
	@Column(name = ColumnNamePrefix + dateTimeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue01;

	public static final String dateTimeValue02_FIELDNAME = "dateTimeValue02";
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "업무 데이터 DateTime 값02.")
	@FieldDescribe("업무 데이터 DateTime 값02.")
	@Column(name = ColumnNamePrefix + dateTimeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue02;

	public static final String dateTimeValue03_FIELDNAME = "dateTimeValue03";
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "업무 데이터 DateTime 값03.")
	@FieldDescribe("업무 데이터 DateTime 값03.")
	@Column(name = ColumnNamePrefix + dateTimeValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue03;

	public static final String dateTimeValue04_FIELDNAME = "dateTimeValue04";
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "업무 데이터 DateTime 값 04.")
	@FieldDescribe("업무 데이터 DateTime 값 04.")
	@Column(name = ColumnNamePrefix + dateTimeValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue04;

	public static final String dateTimeValue05_FIELDNAME = "dateTimeValue05";
	@Temporal(TemporalType.TIMESTAMP)
	@Schema(description = "업무 데이터 DateTime 값 05.")
	@FieldDescribe("업무 데이터 DateTime 값 05.")
	@Column(name = ColumnNamePrefix + dateTimeValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue05;

	public static final String dateValue01_FIELDNAME = "dateValue01";
	@Temporal(TemporalType.DATE)
	@Schema(description = "업무 데이터 Date 값01.")
	@FieldDescribe("업무 데이터 Date 값01.")
	@Column(name = ColumnNamePrefix + dateValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue01;

	public static final String dateValue02_FIELDNAME = "dateValue02";
	@Temporal(TemporalType.DATE)
	@Schema(description = "업무 데이터 Date 값02.")
	@FieldDescribe("업무 데이터 Date 값02.")
	@Column(name = ColumnNamePrefix + dateValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue02;

	public static final String timeValue01_FIELDNAME = "timeValue01";
	@Temporal(TemporalType.TIME)
	@Schema(description = "업무 데이터 Time 값01.")
	@FieldDescribe("업무 데이터 Time 값01.")
	@Column(name = ColumnNamePrefix + timeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue01;

	public static final String timeValue02_FIELDNAME = "timeValue02";
	@Temporal(TemporalType.TIME)
	@Schema(description = "업무 데이터 Time 값02.")
	@FieldDescribe("업무 데이터 Time 값02.")
	@Column(name = ColumnNamePrefix + timeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue02;

	public static final String booleanValue01_FIELDNAME = "booleanValue01";
	@Schema(description = "업무 데이터 Boolean 값01.")
	@FieldDescribe("업무 데이터 Boolean 값01.")
	@Column(name = ColumnNamePrefix + booleanValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue01;

	public static final String booleanValue02_FIELDNAME = "booleanValue02";
	@Schema(description = "업무 데이터 Boolean 값02.")
	@FieldDescribe("업무 데이터 Boolean 값02.")
	@Column(name = ColumnNamePrefix + booleanValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue02;

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
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

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public Boolean getModified() {
		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;
	}

	public Boolean getAllowRapid() {
		return allowRapid;
	}

	public void setAllowRapid(Boolean allowRapid) {
		this.allowRapid = allowRapid;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public void setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOpinionLob() {
		return opinionLob;
	}

	public void setOpinionLob(String opinionLob) {
		this.opinionLob = opinionLob;
	}

	public Date getUrgeTime() {
		return urgeTime;
	}

	public void setUrgeTime(Date urgeTime) {
		this.urgeTime = urgeTime;
	}

	public Boolean getUrged() {
		return urged;
	}

	public void setUrged(Boolean urged) {
		this.urged = urged;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public List<String> getRouteOpinionList() {
		return routeOpinionList;
	}

	public void setRouteOpinionList(List<String> routeOpinionList) {
		this.routeOpinionList = routeOpinionList;
	}

	public String getMediaOpinion() {
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.mediaOpinion = mediaOpinion;
	}

	public List<String> getRouteDecisionOpinionList() {
		return routeDecisionOpinionList;
	}

	public void setRouteDecisionOpinionList(List<String> routeDecisionOpinionList) {
		this.routeDecisionOpinionList = routeDecisionOpinionList;
	}

	public String getEmpowerFromIdentity() {
		return empowerFromIdentity;
	}

	public void setEmpowerFromIdentity(String empowerFromIdentity) {
		this.empowerFromIdentity = empowerFromIdentity;
	}

	public Boolean getFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

	public String getStringValue01() {
		return stringValue01;
	}

	public void setStringValue01(String stringValue01) {
		this.stringValue01 = stringValue01;
	}

	public String getStringValue02() {
		return stringValue02;
	}

	public void setStringValue02(String stringValue02) {
		this.stringValue02 = stringValue02;
	}

	public String getStringValue03() {
		return stringValue03;
	}

	public void setStringValue03(String stringValue03) {
		this.stringValue03 = stringValue03;
	}

	public String getStringValue04() {
		return stringValue04;
	}

	public void setStringValue04(String stringValue04) {
		this.stringValue04 = stringValue04;
	}

	public String getStringValue05() {
		return stringValue05;
	}

	public void setStringValue05(String stringValue05) {
		this.stringValue05 = stringValue05;
	}

	public String getStringValue06() {
		return stringValue06;
	}

	public void setStringValue06(String stringValue06) {
		this.stringValue06 = stringValue06;
	}

	public String getStringValue07() {
		return stringValue07;
	}

	public void setStringValue07(String stringValue07) {
		this.stringValue07 = stringValue07;
	}

	public String getStringValue08() {
		return stringValue08;
	}

	public void setStringValue08(String stringValue08) {
		this.stringValue08 = stringValue08;
	}

	public String getStringValue09() {
		return stringValue09;
	}

	public void setStringValue09(String stringValue09) {
		this.stringValue09 = stringValue09;
	}

	public String getStringValue10() {
		return stringValue10;
	}

	public void setStringValue10(String stringValue10) {
		this.stringValue10 = stringValue10;
	}

	public Double getDoubleValue03() {
		return doubleValue03;
	}

	public void setDoubleValue03(Double doubleValue03) {
		this.doubleValue03 = doubleValue03;
	}

	public Double getDoubleValue04() {
		return doubleValue04;
	}

	public void setDoubleValue04(Double doubleValue04) {
		this.doubleValue04 = doubleValue04;
	}

	public Double getDoubleValue05() {
		return doubleValue05;
	}

	public void setDoubleValue05(Double doubleValue05) {
		this.doubleValue05 = doubleValue05;
	}

	public Long getLongValue03() {
		return longValue03;
	}

	public void setLongValue03(Long longValue03) {
		this.longValue03 = longValue03;
	}

	public Long getLongValue04() {
		return longValue04;
	}

	public void setLongValue04(Long longValue04) {
		this.longValue04 = longValue04;
	}

	public Long getLongValue05() {
		return longValue05;
	}

	public void setLongValue05(Long longValue05) {
		this.longValue05 = longValue05;
	}

	public Date getDateTimeValue03() {
		return dateTimeValue03;
	}

	public void setDateTimeValue03(Date dateTimeValue03) {
		this.dateTimeValue03 = dateTimeValue03;
	}

	public Date getDateTimeValue04() {
		return dateTimeValue04;
	}

	public void setDateTimeValue04(Date dateTimeValue04) {
		this.dateTimeValue04 = dateTimeValue04;
	}

	public Date getDateTimeValue05() {
		return dateTimeValue05;
	}

	public void setDateTimeValue05(Date dateTimeValue05) {
		this.dateTimeValue05 = dateTimeValue05;
	}

	public Boolean getBooleanValue01() {
		return booleanValue01;
	}

	public void setBooleanValue01(Boolean booleanValue01) {
		this.booleanValue01 = booleanValue01;
	}

	public Boolean getBooleanValue02() {
		return booleanValue02;
	}

	public void setBooleanValue02(Boolean booleanValue02) {
		this.booleanValue02 = booleanValue02;
	}

	public Double getDoubleValue01() {
		return doubleValue01;
	}

	public void setDoubleValue01(Double doubleValue01) {
		this.doubleValue01 = doubleValue01;
	}

	public Double getDoubleValue02() {
		return doubleValue02;
	}

	public void setDoubleValue02(Double doubleValue02) {
		this.doubleValue02 = doubleValue02;
	}

	public Long getLongValue01() {
		return longValue01;
	}

	public void setLongValue01(Long longValue01) {
		this.longValue01 = longValue01;
	}

	public Long getLongValue02() {
		return longValue02;
	}

	public void setLongValue02(Long longValue02) {
		this.longValue02 = longValue02;
	}

	public Date getDateTimeValue01() {
		return dateTimeValue01;
	}

	public void setDateTimeValue01(Date dateTimeValue01) {
		this.dateTimeValue01 = dateTimeValue01;
	}

	public Date getDateTimeValue02() {
		return dateTimeValue02;
	}

	public void setDateTimeValue02(Date dateTimeValue02) {
		this.dateTimeValue02 = dateTimeValue02;
	}

	public Date getDateValue01() {
		return dateValue01;
	}

	public void setDateValue01(Date dateValue01) {
		this.dateValue01 = dateValue01;
	}

	public Date getDateValue02() {
		return dateValue02;
	}

	public void setDateValue02(Date dateValue02) {
		this.dateValue02 = dateValue02;
	}

	public Date getTimeValue01() {
		return timeValue01;
	}

	public void setTimeValue01(Date timeValue01) {
		this.timeValue01 = timeValue01;
	}

	public Date getTimeValue02() {
		return timeValue02;
	}

	public void setTimeValue02(Date timeValue02) {
		this.timeValue02 = timeValue02;
	}

	public String getWorkCreateType() {
		return workCreateType;
	}

	public void setWorkCreateType(String workCreateType) {
		this.workCreateType = workCreateType;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public List<String> getRouteList() {
		return routeList;
	}

	public void setRouteList(List<String> routeList) {
		this.routeList = routeList;
	}

	public List<String> getRouteNameList() {
		return routeNameList;
	}

	public void setRouteNameList(List<String> routeNameList) {
		this.routeNameList = routeNameList;
	}

	public Boolean getPause() {
		return pause;
	}

	public void setPause(Boolean pause) {
		this.pause = pause;
	}

	public Date getViewTime() {
		return viewTime;
	}

	public void setViewTime(Date viewTime) {
		this.viewTime = viewTime;
	}

	public String getRouteAlias() {
		return routeAlias;
	}

	public void setRouteAlias(String routeAlias) {
		this.routeAlias = routeAlias;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getFromDistinguishedName() {
		return fromDistinguishedName;
	}

	public void setFromDistinguishedName(String fromDistinguishedName) {
		this.fromDistinguishedName = fromDistinguishedName;
	}

}
