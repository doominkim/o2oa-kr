package com.x.processplatform.core.entity.content;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.processplatform.ManualTaskIdentityMatrix;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import com.x.processplatform.core.entity.content.WorkProperties.GoBackStore;
import com.x.processplatform.core.entity.element.ActivityType;
import com.x.processplatform.core.entity.ticket.Ticket;
import com.x.processplatform.core.entity.ticket.Tickets;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Work", description = "프로세스 플랫폼 업무 문서.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Work.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Work.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

/**
 * 7.2.0 버전 manualTaskIdentityMatrix 추가
 * 
 * @author ray
 *
 */
public class Work extends SliceJpaObject implements ProjectionInterface {

	private static final long serialVersionUID = 7668822947307502058L;
	private static final String TABLE = PersistenceProperties.Content.Work.table;
	public static final String TITLEALIAS_SUBJECT = "subject";
	public static final String WORKCREATETYPE_SURFACE = "surface";
	public static final String WORKCREATETYPE_ASSIGN = "assign";

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
		if (StringUtils.isEmpty(this.startTimeMonth) && (null != this.startTime)) {
			this.startTimeMonth = DateTools.format(this.startTime, DateTools.format_yyyyMM);
		}
		this.serial = Objects.toString(this.serial, "");
		// add by Ray 20200622
		if (StringTools.utf8Length(this.getProperties().getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getProperties().getTitle(), length_255B - 3) + "...";
		}
		if (null == this.getTickets()) {
			this.manualTaskIdentityText = "";
		} else {
			this.setManualTaskIdentityText(this.getTickets().bubble().stream().map(Ticket::distinguishedName)
					.map(OrganizationDefinition::name).filter(Objects::nonNull).collect(Collectors.joining(",")));
		}
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			if (StringUtils.isNotEmpty(this.getProperties().getTitle())) {
				this.title = this.getProperties().getTitle();
			}
			this.splitValueList = this.getProperties().getSplitValueList();
			this.embedTargetJob = this.getProperties().getEmbedTargetJob();
			this.embedCompleted = this.getProperties().getEmbedCompleted();
			this.manualTaskIdentityMatrix = this.getProperties().getManualTaskIdentityMatrix();
			this.parentJob = this.getProperties().getParentJob();
			this.parentWork = this.getProperties().getParentWork();
			this.goBackStore = this.getProperties().getGoBackStore();
			this.goBackActivityToken = this.getProperties().getGoBackActivityToken();
			this.splitTokenValueMap = this.getProperties().getSplitTokenValueMap();
			this.tickets = this.getProperties().getTickets();
			this.serviceValue = this.getProperties().getServiceValue();
			this.manualEmpowerMap = this.getProperties().getManualEmpowerMap();
			this.forceRouteEnable = this.getProperties().getForceRouteEnable();
			this.manualTaskIdentityText = this.getProperties().getManualTaskIdentityText();
		}
	}

	/* 실행 메서드 업데이트 */

	public Work() {
		this.properties = new WorkProperties();
	}

	public Work(Work work) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		Work copy = XGsonBuilder.convert(work, Work.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
		this.copyProjectionFields(work);
	}

	public Work(WorkCompleted workCompleted) throws Exception {
		this();
		Work copy = XGsonBuilder.convert(workCompleted, Work.class);
		copy.copyTo(this, JpaObject.id_FIELDNAME);
		this.setId(workCompleted.getWork());
		this.copyProjectionFields(workCompleted);
	}

	public WorkProperties getProperties() {
		if (null == this.properties) {
			this.properties = new WorkProperties();
		}
		return this.properties;
	}

	public void setProperties(WorkProperties properties) {
		this.properties = properties;
	}

	public void setTitle(String title) {
		this.title = title;
		this.getProperties().setTitle(title);
	}

	public String getTitle() {
		if ((null != this.properties) && StringUtils.isNotEmpty(this.properties.getTitle())) {
			return this.properties.getTitle();
		} else {
			return this.title;
		}
	}

	public String getWorkCreateType() {
		return StringUtils.equals(workCreateType, WORKCREATETYPE_ASSIGN) ? WORKCREATETYPE_ASSIGN
				: WORKCREATETYPE_SURFACE;
	}

	public List<String> getSplitValueList() {
		return this.splitValueList;
	}

	public void setSplitValueList(List<String> splitValueList) {
		this.splitValueList = splitValueList;
		this.getProperties().setSplitValueList(splitValueList);
	}

	public String getEmbedTargetJob() {
		return embedTargetJob;
	}

	public void setEmbedTargetJob(String embedTargetJob) {
		this.getProperties().setEmbedTargetJob(embedTargetJob);
		this.embedTargetJob = embedTargetJob;

	}

	public String getEmbedCompleted() {
		return embedCompleted;
	}

	public void setEmbedCompleted(String embedCompleted) {
		this.getProperties().setEmbedCompleted(embedCompleted);
		this.embedCompleted = embedCompleted;
	}

	public ManualTaskIdentityMatrix getManualTaskIdentityMatrix() {
		if (null == this.manualTaskIdentityMatrix) {
			this.manualTaskIdentityMatrix = this.getProperties().getManualTaskIdentityMatrix();
		}
		return this.manualTaskIdentityMatrix;
	}

	public void setManualTaskIdentityMatrix(ManualTaskIdentityMatrix manualTaskIdentityMatrix) {
		this.manualTaskIdentityMatrix = manualTaskIdentityMatrix;
		this.getProperties().setManualTaskIdentityMatrix(manualTaskIdentityMatrix);
	}

	public void setParentWork(String parentWork) {
		this.getProperties().setParentWork(parentWork);
		this.parentWork = parentWork;

	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentJob(String parentJob) {
		this.getProperties().setParentJob(parentJob);
		this.parentJob = parentJob;
	}

	public String getParentJob() {
		return parentJob;
	}

	public GoBackStore getGoBackStore() {
		return goBackStore;
	}

	public void setGoBackStore(GoBackStore goBackStore) {
		this.goBackStore = goBackStore;
		this.getProperties().setGoBackStore(goBackStore);
	}

	public String getGoBackActivityToken() {
		return goBackActivityToken;
	}

	public void setGoBackActivityToken(String goBackActivityToken) {
		this.goBackActivityToken = goBackActivityToken;
		this.getProperties().setGoBackActivityToken(goBackActivityToken);
	}

	public Map<String, String> getSplitTokenValueMap() {
		return splitTokenValueMap;
	}

	public void setSplitTokenValueMap(Map<String, String> splitTokenValueMap) {
		this.splitTokenValueMap = splitTokenValueMap;
		this.getProperties().setSplitTokenValueMap(splitTokenValueMap);
	}

	public void setTickets(Tickets tickets) {
		this.getProperties().setTickets(tickets);
		this.tickets = tickets;
	}

	public Tickets getTickets() {
		return tickets;
	}

	public Map<String, Object> getServiceValue() {
		return this.serviceValue;
	}

	public void setServiceValue(Map<String, Object> serviceValue) {
		this.getProperties().setServiceValue(serviceValue);
		this.serviceValue = serviceValue;
	}

	public Map<String, String> getManualEmpowerMap() {
		return manualEmpowerMap;
	}

	public void setManualEmpowerMap(Map<String, String> manualEmpowerMap) {
		this.getProperties().setManualEmpowerMap(manualEmpowerMap);
		this.manualEmpowerMap = manualEmpowerMap;
	}

	public Boolean getForceRouteEnable() {
		if (null == this.forceRouteEnable) {
			this.forceRouteEnable = this.getProperties().getForceRouteEnable();
		}
		return forceRouteEnable;
	}

	public void setForceRouteEnable(Boolean forceRouteEnable) {
		this.getProperties().setForceRouteEnable(forceRouteEnable);
		this.forceRouteEnable = forceRouteEnable;
	}

	public String getManualTaskIdentityText() {
		if ((null == this.manualTaskIdentityText) && (null != this.properties)) {
			this.manualTaskIdentityText = this.getProperties().getManualTaskIdentityText();
		}
		return manualTaskIdentityText;
	}

	public void setManualTaskIdentityText(String manualTaskIdentityText) {
		this.getProperties().setManualTaskIdentityText(manualTaskIdentityText);
		this.manualTaskIdentityText = manualTaskIdentityText;
	}

	public static final String MANUALEMPOWERMAP_FIELDNAME = "manualEmpowerMap";
	@Transient
	@Deprecated(since = "8.2", forRemoval = true)
	@FieldDescribe("위임 대상")
	private Map<String, String> manualEmpowerMap = new LinkedHashMap<>();

	public static final String SERVICEVALUE_FIELDNAME = "serviceValue";
	@Transient
	@FieldDescribe("서비스 콜백 값")
	private Map<String, Object> serviceValue = new LinkedHashMap<>();

	public static final String GOBACKACTIVITYTOKEN_FIELDNAME = "goBackActivityToken";
	@Transient
	@FieldDescribe("goBack으로 이동 반려 시 사용.")
	private String goBackActivityToken;

	public static final String GOBACKSTORE_FIELDNAME = "goBackStore";
	@Transient
	@FieldDescribe("되돌리기 임시 저장 데이터.")
	private GoBackStore goBackStore;

	public static final String SPLITVALUELIST_FIELDNAME = "splitValueList";
	@Transient
	@FieldDescribe("분할할 값")
	private List<String> splitValueList;

	public static final String EMBEDTARGETJOB_FIELDNAME = "embedTargetJob";
	@Transient
	@FieldDescribe("Embed 활동이 생성한 Work의 Job.")
	private String embedTargetJob;

	public static final String EMBEDCOMPLETED_FIELDNAME = "embedCompleted";
	@Transient
	@FieldDescribe("하위 프로세스 반환 식별자.")
	private String embedCompleted;

	public static final String SPLITTOKENVALUEMAP_FIELDNAME = "splitTokenValueMap";
	@Transient
	@FieldDescribe("분할 값 저장 객체.")
	private Map<String, String> splitTokenValueMap;

	public static final String MANUALTASKIDENTITYMATRIX_FIELDNAME = "manualTaskIdentityMatrix";
	@FieldDescribe("결재 대기 신분 매트릭스.")
	@Transient
	private ManualTaskIdentityMatrix manualTaskIdentityMatrix;

	public static final String PARENTWORK_FIELDNAME = "parentWork";
	@Transient
	@FieldDescribe("상위 업무 문서, 현재 업무 문서가 하위 프로세스 호출로 생성된 경우.")
	private String parentWork;

	public static final String PARENTJOB_FIELDNAME = "parentJob";
	@Transient
	@FieldDescribe("상위 업무 문서 Job, 현재 업무 문서가 하위 프로세스 호출로 생성된 경우.")
	private String parentJob;

	public static final String TICKETS_FIELDNAME = "tickets";
	@Transient
	@FieldDescribe("결재 대기 증빙.")
	private Tickets tickets;

	public static final String FORCEROUTEENABLE_FIELDNAME = "forceRouteEnable";
	@Transient
	@FieldDescribe("강제 경로.")
	private Boolean forceRouteEnable;

	public static final String MANUALTASKIDENTITYTEXT_FIELDNAME = "manualTaskIdentityText";
	@Transient
	@FieldDescribe("현재 처리자 신분 병합 텍스트, ','로 구분, 이 필드는 현재 업무 문서의 처리자 표시에만 사용, 인덱스 없음.")
	private String manualTaskIdentityText;

	public static final String job_FIELDNAME = "job";
	@FieldDescribe("문서")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + job_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + job_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String job;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("제목")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("업무 문서 시작 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date startTime;

	public static final String startTimeMonth_FIELDNAME = "startTimeMonth";
	@FieldDescribe("Filter에서 분류에 사용.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + startTimeMonth_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startTimeMonth_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String startTimeMonth;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("생성자, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("생성자 Identity, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("생성자 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnit;

	public static final String creatorUnitLevelName_FIELDNAME = "creatorUnitLevelName";
	@FieldDescribe("생성자 조직 계층 이름.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@CheckPersist(allowEmpty = true)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + creatorUnitLevelName_FIELDNAME)
	private String creatorUnitLevelName;

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("앱 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String applicationName_FIELDNAME = "applicationName";
	@FieldDescribe("앱 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + applicationName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationName;

	public static final String applicationAlias_FIELDNAME = "applicationAlias";
	@FieldDescribe("앱 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + applicationAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String applicationAlias;

	public static final String process_FIELDNAME = "process";
	@FieldDescribe("프로세스 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + process_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + process_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String process;

	public static final String processName_FIELDNAME = "processName";
	@FieldDescribe("프로세스 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + processName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + processName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processName;

	public static final String processAlias_FIELDNAME = "processAlias";
	@FieldDescribe("프로세스 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + processAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processAlias;

	public static final String activity_FIELDNAME = "activity";
	@FieldDescribe("현재 활동 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activity;

	public static final String activityType_FIELDNAME = "activityType";
	@FieldDescribe("활동 유형.")
	@Enumerated(EnumType.STRING)
	@Column(length = ActivityType.length, name = ColumnNamePrefix + activityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType activityType;

	public static final String activityName_FIELDNAME = "activityName";
	@FieldDescribe("활동 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityName;

	public static final String activityAlias_FIELDNAME = "activityAlias";
	@FieldDescribe("활동 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityAlias;

	public static final String activityDescription_FIELDNAME = "activityDescription";
	@FieldDescribe("활동 설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + activityDescription_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityDescription_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityDescription;

	public static final String activityToken_FIELDNAME = "activityToken";
	@FieldDescribe("활동 식별 번호, 활동에 진입할 때마다 새로 생성")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + activityToken_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityToken_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String activityToken;

	public static final String activityUnique_FIELDNAME = "activityUnique";
	@Schema(description = "활동 단계 고유 식별자")
	@FieldDescribe("활동 단계 고유 식별자")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + activityUnique_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String activityUnique;

	public static final String activityArrivedTime_FIELDNAME = "activityArrivedTime";
	@FieldDescribe("활동 도착 시간")
	@Column(name = ColumnNamePrefix + activityArrivedTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + activityArrivedTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date activityArrivedTime;

	public static final String serial_FIELDNAME = "serial";
	@FieldDescribe("번호")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + serial_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + serial_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serial;

	public static final String dataChanged_FIELDNAME = "dataChanged";
	@FieldDescribe("현재 업무 문서의 저장 수정 작업 여부, 기본 생성 후 수정되지 않은 것인지 판단에 사용.")
	@Column(name = ColumnNamePrefix + dataChanged_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean dataChanged;

	public static final String workThroughManual_FIELDNAME = "workThroughManual";
	@FieldDescribe("수동 노드 통과 여부, 초안 여부 판단에 사용. 도착 단계에서 판단.")
	@Column(name = ColumnNamePrefix + workThroughManual_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean workThroughManual;

	public static final String workCreateType_FIELDNAME = "workCreateType";
	@FieldDescribe("업무 문서 생성 유형, surface, assgin")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + workCreateType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workCreateType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workCreateType;

	public static final String workStatus_FIELDNAME = "workStatus";
	@FieldDescribe("업무 문서 상태")
	@Enumerated(EnumType.STRING)
	@Column(length = WorkStatus.length, name = ColumnNamePrefix + workStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + workStatus_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private WorkStatus workStatus;

	public static final String beforeExecuted_FIELDNAME = "beforeExecuted";
	@FieldDescribe("실행 전 통과 여부")
	@Column(name = ColumnNamePrefix + beforeExecuted_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean beforeExecuted;

//	@Deprecated(since = "8.2,tickets 사용 후 이 필드 삭제 예정.", forRemoval = true)
//	public static final String MANUALTASKIDENTITYTEXT_FIELDNAME = "manualTaskIdentityText";
//	@FieldDescribe("현재 처리자 신분 병합 텍스트, ','로 구분, 너무 길면 잘림, 이 필드는 현재 업무 문서의 처리자 표시에만 사용, 인덱스 없음.")
//	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + MANUALTASKIDENTITYTEXT_FIELDNAME)
//	@CheckPersist(allowEmpty = true)
//	private String manualTaskIdentityText;

	/** Split Attribute */
	public static final String splitting_FIELDNAME = "splitting";
	@FieldDescribe("분할 중인 업무 문서 여부")
	@Column(name = ColumnNamePrefix + splitting_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean splitting;

	public static final String splitToken_FIELDNAME = "splitToken";
	@FieldDescribe("분할 업무 문서 토큰")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + splitToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitToken;

	public static final String splitTokenList_FIELDNAME = "splitTokenList";
	@FieldDescribe("분할 업무 문서에서 생성된 Token")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ splitTokenList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + splitTokenList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = JpaObject.length_id, name = ColumnNamePrefix + splitTokenList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + splitTokenList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> splitTokenList;

	public static final String splitValue_FIELDNAME = "splitValue";
	@FieldDescribe("분할 값")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + splitValue_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String splitValue;

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("사용 양식")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	public static final String destinationRoute_FIELDNAME = "destinationRoute";
	@FieldDescribe("도착 시 사용한 경로")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + destinationRoute_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationRoute;

	public static final String destinationRouteName_FIELDNAME = "destinationRouteName";
	@FieldDescribe("도착 시 사용한 경로")
	@Column(length = length_255B, name = ColumnNamePrefix + destinationRouteName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationRouteName;

	public static final String destinationActivityType_FIELDNAME = "destinationActivityType";
	@Enumerated(EnumType.STRING)
	@FieldDescribe("현재 활동 유형")
	@Column(length = ActivityType.length, name = ColumnNamePrefix + destinationActivityType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + destinationActivityType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ActivityType destinationActivityType;

	public static final String destinationActivity_FIELDNAME = "destinationActivity";
	@FieldDescribe("대상 활동의 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + destinationActivity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + destinationActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String destinationActivity;

	public static final String expireTime_FIELDNAME = "expireTime";
	@FieldDescribe("작업 마감 시간.")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + expireTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + expireTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date expireTime;

	public static final String embedTargetWork_FIELDNAME = "embedTargetWork";
	@FieldDescribe("Embed 활동이 생성한 WorkId, embed가 targetWork를 생성한 후 inquire 단계에서 진행시키는 데 사용.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + embedTargetWork_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + embedTargetWork_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String embedTargetWork;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private WorkProperties properties;

	public static final String stringValue01_FIELDNAME = "stringValue01";
	@FieldDescribe("업무 데이터 String 값01.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue01;

	public static final String stringValue02_FIELDNAME = "stringValue02";
	@FieldDescribe("업무 데이터 String 값02.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue02;

	public static final String stringValue03_FIELDNAME = "stringValue03";
	@FieldDescribe("업무 데이터 String 값03.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue03;

	public static final String stringValue04_FIELDNAME = "stringValue04";
	@FieldDescribe("업무 데이터 String 값04.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue04;

	public static final String stringValue05_FIELDNAME = "stringValue05";
	@FieldDescribe("업무 데이터 String 값05.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue05;

	public static final String stringValue06_FIELDNAME = "stringValue06";
	@FieldDescribe("업무 데이터 String 값06.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue06_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue06_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue06;

	public static final String stringValue07_FIELDNAME = "stringValue07";
	@FieldDescribe("업무 데이터 String 값07.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue07_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue07_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue07;

	public static final String stringValue08_FIELDNAME = "stringValue08";
	@FieldDescribe("업무 데이터 String 값08.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue08_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue08_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue08;

	public static final String stringValue09_FIELDNAME = "stringValue09";
	@FieldDescribe("업무 데이터 String 값09.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue09_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue09_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue09;

	public static final String stringValue10_FIELDNAME = "stringValue10";
	@FieldDescribe("업무 데이터 String 값10.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue10_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue10_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue10;

	public static final String booleanValue01_FIELDNAME = "booleanValue01";
	@FieldDescribe("업무 데이터 Boolean 값01.")
	@Column(name = ColumnNamePrefix + booleanValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue01;

	public static final String booleanValue02_FIELDNAME = "booleanValue02";
	@FieldDescribe("업무 데이터 Boolean 값02.")
	@Column(name = ColumnNamePrefix + booleanValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean booleanValue02;

	public static final String doubleValue01_FIELDNAME = "doubleValue01";
	@FieldDescribe("업무 데이터 Double 값01.")
	@Column(name = ColumnNamePrefix + doubleValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue01;

	public static final String doubleValue02_FIELDNAME = "doubleValue02";
	@FieldDescribe("업무 데이터 Double 값02.")
	@Column(name = ColumnNamePrefix + doubleValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue02;

	public static final String doubleValue03_FIELDNAME = "doubleValue03";
	@FieldDescribe("업무 데이터 Double 값 03.")
	@Column(name = ColumnNamePrefix + doubleValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue03;

	public static final String doubleValue04_FIELDNAME = "doubleValue04";
	@FieldDescribe("업무 데이터 Double 값 04.")
	@Column(name = ColumnNamePrefix + doubleValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue04;

	public static final String doubleValue05_FIELDNAME = "doubleValue05";
	@FieldDescribe("업무 데이터 Double 값 05.")
	@Column(name = ColumnNamePrefix + doubleValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue05;

	public static final String longValue01_FIELDNAME = "longValue01";
	@FieldDescribe("업무 데이터 Long 값01.")
	@Column(name = ColumnNamePrefix + longValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue01;

	public static final String longValue02_FIELDNAME = "longValue02";
	@FieldDescribe("업무 데이터 Long 값02.")
	@Column(name = ColumnNamePrefix + longValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue02;

	public static final String longValue03_FIELDNAME = "longValue03";
	@FieldDescribe("업무 데이터 Long 값 03.")
	@Column(name = ColumnNamePrefix + longValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue03;

	public static final String longValue04_FIELDNAME = "longValue04";
	@FieldDescribe("업무 데이터 Long 값 04.")
	@Column(name = ColumnNamePrefix + longValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue04;

	public static final String longValue05_FIELDNAME = "longValue05";
	@FieldDescribe("업무 데이터 Long 값 05.")
	@Column(name = ColumnNamePrefix + longValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue05;

	public static final String dateTimeValue01_FIELDNAME = "dateTimeValue01";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("업무 데이터 DateTime 값01.")
	@Column(name = ColumnNamePrefix + dateTimeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue01;

	public static final String dateTimeValue02_FIELDNAME = "dateTimeValue02";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("업무 데이터 DateTime 값02.")
	@Column(name = ColumnNamePrefix + dateTimeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue02;

	public static final String dateTimeValue03_FIELDNAME = "dateTimeValue03";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("업무 데이터 DateTime 값03.")
	@Column(name = ColumnNamePrefix + dateTimeValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue03;

	public static final String dateTimeValue04_FIELDNAME = "dateTimeValue04";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("업무 데이터 DateTime 값 04.")
	@Column(name = ColumnNamePrefix + dateTimeValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue04;

	public static final String dateTimeValue05_FIELDNAME = "dateTimeValue05";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("업무 데이터 DateTime 값 05.")
	@Column(name = ColumnNamePrefix + dateTimeValue05_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue05;

	public static final String dateValue01_FIELDNAME = "dateValue01";
	@Temporal(TemporalType.DATE)
	@FieldDescribe("업무 데이터 Date 값01.")
	@Column(name = ColumnNamePrefix + dateValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue01;

	public static final String dateValue02_FIELDNAME = "dateValue02";
	@Temporal(TemporalType.DATE)
	@FieldDescribe("업무 데이터 Date 값02.")
	@Column(name = ColumnNamePrefix + dateValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateValue02;

	public static final String timeValue01_FIELDNAME = "timeValue01";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("업무 데이터 Time 값01.")
	@Column(name = ColumnNamePrefix + timeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue01;

	public static final String timeValue02_FIELDNAME = "timeValue02";
	@Temporal(TemporalType.TIME)
	@FieldDescribe("업무 데이터 Time 값02.")
	@Column(name = ColumnNamePrefix + timeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + timeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date timeValue02;

	public static final String OBJECTSECURITYCLEARANCE_FIELDNAME = "objectSecurityClearance";
	@FieldDescribe("객체 보안 등급 식별자.")
	@Column(name = ColumnNamePrefix + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + OBJECTSECURITYCLEARANCE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer objectSecurityClearance;

	public Integer getObjectSecurityClearance() {
		return objectSecurityClearance;
	}

	public void setObjectSecurityClearance(Integer objectSecurityClearance) {
		this.objectSecurityClearance = objectSecurityClearance;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public WorkStatus getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(WorkStatus workStatus) {
		this.workStatus = workStatus;
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

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityToken() {
		return activityToken;
	}

	public void setActivityToken(String activityToken) {
		this.activityToken = activityToken;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Boolean getSplitting() {
		return splitting;
	}

	public void setSplitting(Boolean splitting) {
		this.splitting = splitting;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
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

	public String getSplitToken() {
		return splitToken;
	}

	public void setSplitToken(String splitToken) {
		this.splitToken = splitToken;
	}

	public String getSplitValue() {
		return splitValue;
	}

	public void setSplitValue(String splitValue) {
		this.splitValue = splitValue;
	}

	public List<String> getSplitTokenList() {
		return splitTokenList;
	}

	public void setSplitTokenList(List<String> splitTokenList) {
		this.splitTokenList = splitTokenList;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getDestinationRoute() {
		return destinationRoute;
	}

	public void setDestinationRoute(String destinationRoute) {
		this.destinationRoute = destinationRoute;
	}

	public String getDestinationActivity() {
		return destinationActivity;
	}

	public void setDestinationActivity(String destinationActivity) {
		this.destinationActivity = destinationActivity;
	}

	public String getDestinationRouteName() {
		return destinationRouteName;
	}

	public void setDestinationRouteName(String destinationRouteName) {
		this.destinationRouteName = destinationRouteName;
	}

	public Date getActivityArrivedTime() {
		return activityArrivedTime;
	}

	public void setActivityArrivedTime(Date activityArrivedTime) {
		this.activityArrivedTime = activityArrivedTime;
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	public ActivityType getDestinationActivityType() {
		return destinationActivityType;
	}

	public void setDestinationActivityType(ActivityType destinationActivityType) {
		this.destinationActivityType = destinationActivityType;
	}

	public String getStartTimeMonth() {
		return startTimeMonth;
	}

	public void setStartTimeMonth(String startTimeMonth) {
		this.startTimeMonth = startTimeMonth;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public void setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public void setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public String getEmbedTargetWork() {
		return embedTargetWork;
	}

	public void setEmbedTargetWork(String embedTargetWork) {
		this.embedTargetWork = embedTargetWork;
	}

	public Boolean getBeforeExecuted() {
		return beforeExecuted;
	}

	public void setBeforeExecuted(Boolean beforeExecuted) {
		this.beforeExecuted = beforeExecuted;
	}

	public String getCreatorUnitLevelName() {
		return creatorUnitLevelName;
	}

	public void setCreatorUnitLevelName(String creatorUnitLevelName) {
		this.creatorUnitLevelName = creatorUnitLevelName;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

	public static String getActivitydescriptionFieldname() {
		return activityDescription_FIELDNAME;
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

	public Boolean getDataChanged() {
		return dataChanged;
	}

	public void setDataChanged(Boolean dataChanged) {
		this.dataChanged = dataChanged;
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

	public Boolean getWorkThroughManual() {
		return workThroughManual;
	}

	public void setWorkThroughManual(Boolean workThroughManual) {
		this.workThroughManual = workThroughManual;
	}

	public void setWorkCreateType(String workCreateType) {
		this.workCreateType = workCreateType;
	}

	public String getActivityUnique() {
		return activityUnique;
	}

	public void setActivityUnique(String activityUnique) {
		this.activityUnique = activityUnique;
	}

}