package com.x.processplatform.core.entity.element;

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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.NotEqual;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.processplatform.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Process", description = "프로세스 플랫폼 프로세스.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.Process.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.Process.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Process extends SliceJpaObject {

	private static final long serialVersionUID = 3241184900530625402L;
	private static final String TABLE = PersistenceProperties.Element.Process.table;

	public static final String SERIALPHASE_ARRIVE = "arrive";
	public static final String SERIALPHASE_INQUIRE = "inquire";

	public static final String STARTMODE_DRAFT = "draft";
	public static final String STARTMODE_INSTANCE = "instance";

	public static final String STARTABLETERMINAL_CLIENT = "client";
	public static final String STARTABLETERMINAL_MOBILE = "mobile";
	public static final String STARTABLETERMINAL_ALL = "all";
	public static final String STARTABLETERMINAL_NONE = "none";

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
		/* 기본 프로세스 이름을 의견으로 사용 여부 '예' */
		if (this.routeNameAsOpinion == null) {
			this.routeNameAsOpinion = true;
		}
		if (StringUtils.isEmpty(this.edition)) {
			this.edition = this.id;
			this.editionEnable = true;
			this.editionNumber = 1.0;
			this.editionName = this.name + "_V" + this.editionNumber;
		}
	}

	public static final String PERMISSIONWRITESCRIPT_FIELDNAME = "permissionWriteScript";
	@FieldDescribe("편집 권한 스크립트, 조직 전용 식별자 반환.")
	@Transient
	private String permissionWriteScript;

	public String getPermissionWriteScript() {
		if ((null == permissionWriteScript) && (null != properties)) {
			this.permissionWriteScript = this.properties.getPermissionWriteScript();
		}
		return permissionWriteScript;
	}

	public void setPermissionWriteScript(String permissionWriteScript) {
		this.permissionWriteScript = permissionWriteScript;
		this.getProperties().setPermissionWriteScript(permissionWriteScript);
	}

	public static final String PERMISSIONWRITESCRIPTTEXT_FIELDNAME = "permissionWriteScriptText";
	@FieldDescribe("편집 권한 스크립트 텍스트, 조직 전용 식별자 반환.")
	@Transient
	private String permissionWriteScriptText;

	public String getPermissionWriteScriptText() {
		if ((null == permissionWriteScriptText) && (null != properties)) {
			this.permissionWriteScriptText = this.properties.getPermissionWriteScriptText();
		}
		return permissionWriteScriptText;
	}

	public void setPermissionWriteScriptText(String permissionWriteScriptText) {
		this.permissionWriteScriptText = permissionWriteScriptText;
		this.getProperties().setPermissionWriteScriptText(permissionWriteScriptText);
	}
	public static final String MAINTENANCEIDENTITY_FIELDNAME = "maintenanceIdentity";
	@FieldDescribe("프로세스 관리 신분, 처리 신분을 찾을 수 없을 때의 기본 프로세스 처리 신분.")
	@Transient
	private String maintenanceIdentity;

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
		this.getProperties().setMaintenanceIdentity(maintenanceIdentity);
	}

	public static final String UPDATETABLEENABLE_FIELDNAME = "updateTableEnable";
	@FieldDescribe("자체 테이블 동기화 사용.")
	@Transient
	private Boolean updateTableEnable;

	public Boolean getUpdateTableEnable() {
		return updateTableEnable;
	}

	public void setUpdateTableEnable(Boolean updateTableEnable) {
		this.updateTableEnable = updateTableEnable;
		this.getProperties().setUpdateTableEnable(updateTableEnable);
	}

	public static final String UPDATETABLELIST_FIELDNAME = "updateTableList";
	@FieldDescribe("자체 테이블로 동기화.")
	@Transient
	private List<String> updateTableList;

	public List<String> getUpdateTableList() {
		return updateTableList;
	}

	public void setUpdateTableList(List<String> updateTableList) {
		this.updateTableList = updateTableList;
		this.getProperties().setUpdateTableList(updateTableList);
	}

	public static final String dataTraceFieldType_FIELDNAME = "dataTraceFieldType";
	@FieldDescribe("데이터 변경을 기록할 필드 설정 방식: all|전체, custom|dataTraceFieldList에 설정된 필드.")
	@Transient
	private String dataTraceFieldType;

	public String getDataTraceFieldType() {
		return dataTraceFieldType;
	}

	public void setDataTraceFieldType(String dataTraceFieldType) {
		this.dataTraceFieldType = dataTraceFieldType;
		this.getProperties().setDataTraceFieldType(dataTraceFieldType);
	}

	public static final String dataTraceFieldList_FIELDNAME = "dataTraceFieldList";
	@FieldDescribe("데이터 변경을 기록할 필드.")
	@Transient
	private List<String> dataTraceFieldList;

	public List<String> getDataTraceFieldList() {
		return dataTraceFieldList;
	}

	public void setDataTraceFieldList(List<String> dataTraceFieldList) {
		this.dataTraceFieldList = dataTraceFieldList;
		this.getProperties().setDataTraceFieldList(dataTraceFieldList);
	}

	public Boolean getProjectionFully() {
		return BooleanUtils.isTrue(this.projectionFully);
	}

	public String getBeforeArriveScript() {
		return (null == beforeArriveScript) ? "" : this.beforeArriveScript;
	}

	public String getBeforeArriveScriptText() {
		return (null == beforeArriveScriptText) ? "" : this.beforeArriveScriptText;
	}

	public String getAfterArriveScript() {
		return (null == afterArriveScript) ? "" : this.afterArriveScript;
	}

	public String getAfterArriveScriptText() {
		return (null == afterArriveScriptText) ? "" : this.afterArriveScriptText;
	}

	public String getBeforeExecuteScript() {
		return (null == beforeExecuteScript) ? "" : this.beforeExecuteScript;
	}

	public String getBeforeExecuteScriptText() {
		return (null == beforeExecuteScriptText) ? "" : this.beforeExecuteScriptText;
	}

	public String getAfterExecuteScript() {
		return (null == afterExecuteScript) ? "" : this.afterExecuteScript;
	}

	public String getAfterExecuteScriptText() {
		return (null == afterExecuteScriptText) ? "" : this.afterExecuteScriptText;
	}

	public String getBeforeInquireScript() {
		return (null == beforeInquireScript) ? "" : this.beforeInquireScript;
	}

	public String getBeforeInquireScriptText() {
		return (null == beforeInquireScriptText) ? "" : this.beforeInquireScriptText;
	}

	public String getAfterInquireScript() {
		return (null == afterInquireScript) ? "" : this.afterInquireScript;
	}

	public String getAfterInquireScriptText() {
		return (null == afterInquireScriptText) ? "" : this.afterInquireScriptText;
	}

	public Boolean getRouteNameAsOpinion() {
		return BooleanUtils.isNotFalse(routeNameAsOpinion);
	}

	public ProcessProperties getProperties() {
		if (null == this.properties) {
			this.properties = new ProcessProperties();
		}
		return this.properties;
	}

	public void setProperties(ProcessProperties properties) {
		this.properties = properties;
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.manualBeforeTaskScript = this.getProperties().getManualBeforeTaskScript();
			this.manualBeforeTaskScriptText = this.getProperties().getManualBeforeTaskScriptText();
			this.manualAfterTaskScript = this.getProperties().getManualAfterTaskScript();
			this.manualAfterTaskScriptText = this.getProperties().getManualAfterTaskScriptText();
			this.manualStayScript = this.getProperties().getManualStayScript();
			this.manualStayScriptText = this.getProperties().getManualStayScriptText();
			this.updateTableEnable = this.getProperties().getUpdateTableEnable();
			this.updateTableList = this.getProperties().getUpdateTableList();
			this.maintenanceIdentity = this.getProperties().getMaintenanceIdentity();
			this.targetAssignDataScript = this.getProperties().getTargetAssignDataScript();
			this.targetAssignDataScriptText = this.getProperties().getTargetAssignDataScriptText();
			this.manualAfterProcessingScript = this.getProperties().getManualAfterProcessingScript();
			this.manualAfterProcessingScriptText = this.getProperties().getManualAfterProcessingScriptText();
			this.dataTraceFieldType = this.getProperties().getDataTraceFieldType();
			this.dataTraceFieldList = this.getProperties().getDataTraceFieldList();
			this.permissionWriteScript = this.getProperties().getPermissionWriteScript();
			this.permissionWriteScriptText = this.getProperties().getPermissionWriteScriptText();
		}
	}

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Process.class, equals = @Equal(property = "application", field = "application"), notEquals = @NotEqual(property = "edition", field = "edition")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("프로세스 생성자.")
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorPerson;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("마지막 편집자.")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("마지막 편집 시간.")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String application_FIELDNAME = "application";
	@IdReference(Application.class)
	@FieldDescribe("프로세스 소속 앱.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Application.class))
	private String application;

	public static final String controllerList_FIELDNAME = "controllerList";
	@FieldDescribe("프로세스 관리자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ controllerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + controllerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + controllerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> controllerList;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64 인코딩 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String afterBeginScript_FIELDNAME = "afterBeginScript";
	@IdReference(Script.class)
	/** 스크립트가 이름을 사용할 수 있으므로 길이는 255 */
	@FieldDescribe("프로세스 시작 전 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterBeginScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScript;

	public static final String afterBeginScriptText_FIELDNAME = "afterBeginScriptText";
	@FieldDescribe("프로세스 시작 전 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterBeginScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterBeginScriptText;

	public static final String afterEndScript_FIELDNAME = "afterEndScript";
	@IdReference(Script.class)
	/** 스크립트가 이름을 사용할 수 있으므로 길이는 255 */
	@FieldDescribe("프로세스 종료 후 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterEndScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScript;

	public static final String afterEndScriptText_FIELDNAME = "afterEndScriptText";
	@FieldDescribe("프로세스 종료 후 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterEndScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterEndScriptText;

	public static final String startableIdentityList_FIELDNAME = "startableIdentityList";
	@FieldDescribe("지정 시작 시 Work 생성을 허용할 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableIdentityList;

	public static final String startableUnitList_FIELDNAME = "startableUnitList";
	@FieldDescribe("지정 시작 시 Work 생성을 허용할 조직.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableUnitList;

	public static final String startableGroupList_FIELDNAME = "startableGroupList";
	@FieldDescribe("지정 시작 시 Work 생성을 허용할 그룹.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ startableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableGroupList;

	public static final String startableRoleList_FIELDNAME = "startableRoleList";
	@FieldDescribe("지정 시작 시 Work 생성을 허용할 역할.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ startableRoleList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ startableRoleList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + startableRoleList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + startableRoleList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> startableRoleList;

	public static final String serialTexture_FIELDNAME = "serialTexture";
	@FieldDescribe("번호 정의.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + serialTexture_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialTexture;

	public static final String serialActivity_FIELDNAME = "serialActivity";
	@IdReference({ Agent.class, Begin.class, Cancel.class, Choice.class, Choice.class, Delay.class, Embed.class,
			End.class, Invoke.class, Manual.class, Merge.class, Parallel.class, Service.class, Split.class })
	@FieldDescribe("번호 활동 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + serialActivity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialActivity;

	public static final String serialPhase_FIELDNAME = "serialPhase";
	@FieldDescribe("번호 활동 단계는 arrive 또는 inquire 선택 가능, 기본은 비어 있으며 arrive")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + serialPhase_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String serialPhase;

	public static final String expireType_FIELDNAME = "expireType";
	@FieldDescribe("만료 방식. 선택 값 never, appoint, script")
	@Enumerated(EnumType.STRING)
	@Column(length = ExpireType.length, name = ColumnNamePrefix + expireType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ExpireType expireType = ExpireType.never;

	public static final String expireDay_FIELDNAME = "expireDay";
	@FieldDescribe("만료 날짜.")
	@Column(name = ColumnNamePrefix + expireDay_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireDay;

	public static final String expireHour_FIELDNAME = "expireHour";
	@FieldDescribe("만료 시간(시).")
	@Column(name = ColumnNamePrefix + expireHour_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer expireHour;

	public static final String expireWorkTime_FIELDNAME = "expireWorkTime";
	@FieldDescribe("만료 시간의 근무 시간 기준 여부.")
	@Column(name = ColumnNamePrefix + expireWorkTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean expireWorkTime;

	public static final String checkDraft_FIELDNAME = "checkDraft";
	@FieldDescribe("내용 없는 초안 삭제 검증 수행 여부.")
	@Column(name = ColumnNamePrefix + checkDraft_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean checkDraft;

	public static final String startableTerminal_FIELDNAME = "startableTerminal";
	@FieldDescribe("프로세스를 시작할 수 있는 단말 유형, 선택 값 client, mobile, all, none")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + startableTerminal_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String startableTerminal;

	public static final String projection_FIELDNAME = "projection";
	@FieldDescribe("필드 매핑 설정.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + projection_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projection;

	public static final String projectionFully_FIELDNAME = "projectionFully";
	@FieldDescribe("완전 매핑 실행, 진행할 때마다 모든 업무 문서, 결재 대기, 처리 완료, 열람 대기, 열람 완료, 참조 열람에 전체 필드 매핑 실행, 기본값 false")
	@Column(name = ColumnNamePrefix + projectionFully_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean projectionFully;

	public static final String routeNameAsOpinion_FIELDNAME = "routeNameAsOpinion";
	@FieldDescribe("기본 의견이 없으면 경로 이름을 기본 의견으로 사용.")
	@Column(name = ColumnNamePrefix + routeNameAsOpinion_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean routeNameAsOpinion;

	public static final String beforeArriveScript_FIELDNAME = "beforeArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 활동 도착 전 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScript;

	public static final String beforeArriveScriptText_FIELDNAME = "beforeArriveScriptText";
	@FieldDescribe("통합 활동 도착 전 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeArriveScriptText;

	public static final String afterArriveScript_FIELDNAME = "afterArriveScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 활동 도착 후 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterArriveScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScript;

	public static final String afterArriveScriptText_FIELDNAME = "afterArriveScriptText";
	@FieldDescribe("통합 활동 도착 후 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterArriveScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterArriveScriptText;

	public static final String beforeExecuteScript_FIELDNAME = "beforeExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 활동 실행 전 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScript;

	public static final String beforeExecuteScriptText_FIELDNAME = "beforeExecuteScriptText";
	@FieldDescribe("통합 활동 실행 전 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeExecuteScriptText;

	public static final String afterExecuteScript_FIELDNAME = "afterExecuteScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 활동 실행 후 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterExecuteScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScript;

	public static final String afterExecuteScriptText_FIELDNAME = "afterExecuteScriptText";
	@FieldDescribe("통합 활동 실행 후 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterExecuteScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterExecuteScriptText;

	public static final String beforeInquireScript_FIELDNAME = "beforeInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 경로 조회 전 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + beforeInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScript;

	public static final String beforeInquireScriptText_FIELDNAME = "beforeInquireScriptText";
	@FieldDescribe("통합 경로 조회 전 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + beforeInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String beforeInquireScriptText;

	public static final String afterInquireScript_FIELDNAME = "afterInquireScript";
	@IdReference(Script.class)
	@FieldDescribe("통합 경로 조회 후 이벤트 스크립트.")
	@Column(length = length_255B, name = ColumnNamePrefix + afterInquireScript_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScript;

	public static final String afterInquireScriptText_FIELDNAME = "afterInquireScriptText";
	@FieldDescribe("통합 경로 조회 후 이벤트 스크립트 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + afterInquireScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String afterInquireScriptText;

	public static final String edition_FIELDNAME = "edition";
	@FieldDescribe("버전 코드, 버전이 다른 프로세스의 코드는 같아야 함.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + edition_FIELDNAME)
	private String edition;

	public static final String editionName_FIELDNAME = "editionName";
	@FieldDescribe("버전 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + editionName_FIELDNAME)
	private String editionName;

	public static final String editionEnable_FIELDNAME = "editionEnable";
	@FieldDescribe("버전 사용")
	@Column(name = ColumnNamePrefix + editionEnable_FIELDNAME)
	private Boolean editionEnable;

	public static final String editionNumber_FIELDNAME = "editionNumber";
	@FieldDescribe("버전 번호")
	@Column(name = ColumnNamePrefix + editionNumber_FIELDNAME)
	private Double editionNumber;

	public static final String editionDes_FIELDNAME = "editionDes";
	@FieldDescribe("버전 설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + editionDes_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String editionDes;

	public static final String defaultStartMode_FIELDNAME = "defaultStartMode";
	@FieldDescribe("기본 시작 방식, draft, instance")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + defaultStartMode_FIELDNAME)
	private String defaultStartMode;

	public static final String CATEGORY_FIELDNAME = "category";
	@FieldDescribe("프로세스 분류")
	@Column(length = length_255B, name = ColumnNamePrefix + CATEGORY_FIELDNAME)
	private String category;

	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ProcessProperties properties;

	public String getManualBeforeTaskScript() {
		return manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScript(String manualBeforeTaskScript) {
		this.getProperties().setManualBeforeTaskScript(manualBeforeTaskScript);
		this.manualBeforeTaskScript = manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScriptText(String manualBeforeTaskScriptText) {
		this.getProperties().setManualBeforeTaskScriptText(manualBeforeTaskScriptText);
		this.manualBeforeTaskScriptText = manualBeforeTaskScriptText;
	}

	public void setManualAfterTaskScript(String manualAfterTaskScript) {
		this.getProperties().setManualAfterTaskScript(manualAfterTaskScript);
		this.manualAfterTaskScript = manualAfterTaskScript;
	}

	public void setManualAfterTaskScriptText(String manualAfterTaskScriptText) {
		this.getProperties().setManualAfterTaskScriptText(manualAfterTaskScriptText);
		this.manualAfterTaskScriptText = manualAfterTaskScriptText;
	}

	public void setManualStayScript(String manualStayScript) {
		this.getProperties().setManualStayScript(manualStayScript);
		this.manualStayScript = manualStayScript;
	}

	public void setManualStayScriptText(String manualStayScriptText) {
		this.getProperties().setManualStayScriptText(manualStayScriptText);
		this.manualStayScriptText = manualStayScriptText;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.getProperties().setTargetAssignDataScript(targetAssignDataScript);
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.getProperties().setTargetAssignDataScriptText(targetAssignDataScriptText);
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}

	public String getManualBeforeTaskScriptText() {
		return manualBeforeTaskScriptText;
	}

	public String getManualAfterTaskScript() {
		return manualAfterTaskScript;
	}

	public String getManualAfterTaskScriptText() {
		return manualAfterTaskScriptText;
	}

	public String getManualStayScript() {
		return manualStayScript;
	}

	public String getManualStayScriptText() {
		return manualStayScriptText;
	}

	public static final String MANUALBEFORETASKSCRIPT_FIELDNAME = "manualBeforeTaskScript";
	@FieldDescribe("결재 대기 실행 전 스크립트.")
	@Transient
	private String manualBeforeTaskScript;

	public static final String MANUALBEFORETASKSCRIPTTEXT_FIELDNAME = "manualBeforeTaskScriptText";
	@FieldDescribe("결재 대기 실행 전 스크립트 텍스트.")
	@Transient
	private String manualBeforeTaskScriptText;

	public static final String MANUALAFTERTASKSCRIPT_FIELDNAME = "manualAfterTaskScript";
	@FieldDescribe("결재 대기 실행 후 스크립트.")
	@Transient
	private String manualAfterTaskScript;

	public static final String MANUALAFTERTASKSCRIPTTEXT_FIELDNAME = "manualAfterTaskScriptText";
	@FieldDescribe("결재 대기 실행 후 스크립트 텍스트.")
	@Transient
	private String manualAfterTaskScriptText;

	public static final String MANUALSTAYSCRIPT_FIELDNAME = "manualStayScript";
	@FieldDescribe("수동 활동 체류 스크립트.")
	@Transient
	private String manualStayScript;

	public static final String MANUALSTAYSCRIPTTEXT_FIELDNAME = "manualStayScriptText";
	@FieldDescribe("수동 활동 체류 스크립트 텍스트.")
	@Transient
	private String manualStayScriptText;

	public static final String MANUALAFTERPROCESSINGSCRIPT_FIELDNAME = "manualAfterProcessingScript";
	@Transient
	@FieldDescribe("수동 단계 업무 문서 진행 후 실행 스크립트.")
	// @since 8.1.0
	private String manualAfterProcessingScript;

	public String getManualAfterProcessingScript() {
		if (null != this.manualAfterProcessingScript) {
			return this.manualAfterProcessingScript;
		} else {
			return this.getProperties().getManualAfterProcessingScript();
		}
	}

	public void setManualAfterProcessingScript(String manualAfterProcessingScript) {
		this.manualAfterProcessingScript = manualAfterProcessingScript;
		this.getProperties().setManualAfterProcessingScript(manualAfterProcessingScript);
	}

	public static final String MANUALAFTERPROCESSINGSCRIPTTEXT_FIELDNAME = "manualAfterProcessingScriptText";
	@Transient
	@FieldDescribe("수동 단계 업무 문서 진행 후 실행 스크립트 텍스트.")
	// @since 8.1.0
	private String manualAfterProcessingScriptText;

	public String getManualAfterProcessingScriptText() {
		if (null != this.manualAfterProcessingScriptText) {
			return this.manualAfterProcessingScriptText;
		} else {
			return this.getProperties().getManualAfterProcessingScriptText();
		}
	}

	public void setManualAfterProcessingScriptText(String manualAfterProcessingScriptText) {
		this.manualAfterProcessingScriptText = manualAfterProcessingScriptText;
		this.getProperties().setManualAfterProcessingScriptText(manualAfterProcessingScriptText);
	}

	public static final String TARGETASSIGNDATASCRIPT_FIELDNAME = "targetAssignDataScript";
	@FieldDescribe("데이터 실행 전 스크립트.")
	@Transient
	private String targetAssignDataScript;

	public static final String TARGETASSIGNDATASCRIPTTEXT_FIELDNAME = "targetAssignDataScriptText";
	@FieldDescribe("데이터 실행 전 스크립트 텍스트.")
	@Transient
	private String targetAssignDataScriptText;

	/* flag 플래그 비트 */

	public String getName() {
		return name;
	}

	public void setProjectionFully(Boolean projectionFully) {
		this.projectionFully = projectionFully;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getAfterEndScript() {
		return afterEndScript;
	}

	public void setAfterEndScript(String afterEndScript) {
		this.afterEndScript = afterEndScript;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}

	public List<String> getStartableIdentityList() {
		return startableIdentityList;
	}

	public void setStartableIdentityList(List<String> startableIdentityList) {
		this.startableIdentityList = startableIdentityList;
	}

	public List<String> getStartableUnitList() {
		return startableUnitList;
	}

	public void setStartableUnitList(List<String> startableUnitList) {
		this.startableUnitList = startableUnitList;
	}

	public String getAfterBeginScript() {
		return afterBeginScript;
	}

	public void setAfterBeginScript(String afterBeginScript) {
		this.afterBeginScript = afterBeginScript;
	}

	public String getAfterBeginScriptText() {
		return afterBeginScriptText;
	}

	public void setAfterBeginScriptText(String afterBeginScriptText) {
		this.afterBeginScriptText = afterBeginScriptText;
	}

	public String getAfterEndScriptText() {
		return afterEndScriptText;
	}

	public void setAfterEndScriptText(String afterEndScriptText) {
		this.afterEndScriptText = afterEndScriptText;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public String getSerialTexture() {
		return serialTexture;
	}

	public void setSerialTexture(String serialTexture) {
		this.serialTexture = serialTexture;
	}

	public String getSerialActivity() {
		return serialActivity;
	}

	public void setSerialActivity(String serialActivity) {
		this.serialActivity = serialActivity;
	}

	public ExpireType getExpireType() {
		return expireType;
	}

	public void setExpireType(ExpireType expireType) {
		this.expireType = expireType;
	}

	public Integer getExpireDay() {
		return expireDay;
	}

	public void setExpireDay(Integer expireDay) {
		this.expireDay = expireDay;
	}

	public Integer getExpireHour() {
		return expireHour;
	}

	public void setExpireHour(Integer expireHour) {
		this.expireHour = expireHour;
	}

	public Boolean getExpireWorkTime() {
		return expireWorkTime;
	}

	public void setExpireWorkTime(Boolean expireWorkTime) {
		this.expireWorkTime = expireWorkTime;
	}

	public List<String> getControllerList() {
		return controllerList;
	}

	public void setControllerList(List<String> controllerList) {
		this.controllerList = controllerList;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Boolean getCheckDraft() {
		return checkDraft;
	}

	public void setCheckDraft(Boolean checkDraft) {
		this.checkDraft = checkDraft;
	}

	public String getSerialPhase() {
		return serialPhase;
	}

	public void setSerialPhase(String serialPhase) {
		this.serialPhase = serialPhase;
	}

	public void setRouteNameAsOpinion(Boolean routeNameAsOpinion) {
		this.routeNameAsOpinion = routeNameAsOpinion;
	}

	public void setBeforeArriveScript(String beforeArriveScript) {
		this.beforeArriveScript = beforeArriveScript;
	}

	public void setBeforeArriveScriptText(String beforeArriveScriptText) {
		this.beforeArriveScriptText = beforeArriveScriptText;
	}

	public void setAfterArriveScript(String afterArriveScript) {
		this.afterArriveScript = afterArriveScript;
	}

	public void setAfterArriveScriptText(String afterArriveScriptText) {
		this.afterArriveScriptText = afterArriveScriptText;
	}

	public void setBeforeExecuteScript(String beforeExecuteScript) {
		this.beforeExecuteScript = beforeExecuteScript;
	}

	public void setBeforeExecuteScriptText(String beforeExecuteScriptText) {
		this.beforeExecuteScriptText = beforeExecuteScriptText;
	}

	public void setAfterExecuteScript(String afterExecuteScript) {
		this.afterExecuteScript = afterExecuteScript;
	}

	public void setAfterExecuteScriptText(String afterExecuteScriptText) {
		this.afterExecuteScriptText = afterExecuteScriptText;
	}

	public void setBeforeInquireScript(String beforeInquireScript) {
		this.beforeInquireScript = beforeInquireScript;
	}

	public void setBeforeInquireScriptText(String beforeInquireScriptText) {
		this.beforeInquireScriptText = beforeInquireScriptText;
	}

	public void setAfterInquireScript(String afterInquireScript) {
		this.afterInquireScript = afterInquireScript;
	}

	public void setAfterInquireScriptText(String afterInquireScriptText) {
		this.afterInquireScriptText = afterInquireScriptText;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getEditionName() {
		return editionName;
	}

	public void setEditionName(String editionName) {
		this.editionName = editionName;
	}

	public Boolean getEditionEnable() {
		return editionEnable;
	}

	public void setEditionEnable(Boolean editionEnable) {
		this.editionEnable = editionEnable;
	}

	public Double getEditionNumber() {
		return editionNumber;
	}

	public void setEditionNumber(Double editionNumber) {
		this.editionNumber = editionNumber;
	}

	public String getEditionDes() {
		return editionDes;
	}

	public void setEditionDes(String editionDes) {
		this.editionDes = editionDes;
	}

	public String getDefaultStartMode() {
		return defaultStartMode;
	}

	public void setDefaultStartMode(String defaultStartMode) {
		this.defaultStartMode = defaultStartMode;
	}

	public String getStartableTerminal() {
		return startableTerminal;
	}

	public void setStartableTerminal(String startableTerminal) {
		this.startableTerminal = startableTerminal;
	}

	public List<String> getStartableGroupList() {
		return startableGroupList;
	}

	public void setStartableGroupList(List<String> startableGroupList) {
		this.startableGroupList = startableGroupList;
	}

	public List<String> getStartableRoleList() {
		return startableRoleList;
	}

	public void setStartableRoleList(List<String> startableRoleList) {
		this.startableRoleList = startableRoleList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
