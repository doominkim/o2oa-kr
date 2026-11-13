package com.x.cms.core.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
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
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 콘텐츠 관리 게시판 디렉터리 분류 정보
 *
 * @author O2LEE
 *
 */
@Schema(name = "CategoryInfo", description = "콘텐츠 관리 분류 정보.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.CategoryInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.CategoryInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CategoryInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.CategoryInfo.table;
	public static final String DOCUMENT_TYPE_DATA = "데이터";

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
	public static final String categoryName_FIELDNAME = "categoryName";
	@Flag
	@FieldDescribe("분류 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryName;

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("분류 소속 게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appName;

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("기본 문서 유형: 정보 | 데이터")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentType = "정보";

	public static final String parentId_FIELDNAME = "parentId";
	@FieldDescribe("상위 분류 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + parentId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + parentId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String parentId;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@Flag
	@FieldDescribe("분류 별칭")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryAlias;

	public static final String workflowType_FIELDNAME = "workflowType";
	@FieldDescribe("프로세스 유형: 결재 프로세스 사용 안 함|자유 결재 프로세스|고정 결재 프로세스")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + workflowType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workflowType = "결재 프로세스 사용 안 함";

	public static final String workflowAppId_FIELDNAME = "workflowAppId";
	@FieldDescribe("프로세스 앱 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workflowAppId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workflowAppId;

	public static final String workflowAppName_FIELDNAME = "workflowAppName";
	@FieldDescribe("프로세스 앱 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workflowAppName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workflowAppName;

	public static final String workflowName_FIELDNAME = "workflowName";
	@FieldDescribe("프로세스 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + workflowName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workflowName;

	public static final String workflowFlag_FIELDNAME = "workflowFlag";
	@FieldDescribe("프로세스 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + workflowFlag_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String workflowFlag;

	public static final String formId_FIELDNAME = "formId";
	@FieldDescribe("연결된 편집 양식 템플릿 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + formId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formId;

	public static final String formName_FIELDNAME = "formName";
	@FieldDescribe("연결된 편집 양식 템플릿 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + formName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formName;

	public static final String readFormId_FIELDNAME = "readFormId";
	@FieldDescribe("연결된 열람 양식 템플릿 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + readFormId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readFormId;

	public static final String readFormName_FIELDNAME = "readFormName";
	@FieldDescribe("연결된 열람 양식 템플릿 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + readFormName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String readFormName;

	public static final String defaultViewId_FIELDNAME = "defaultViewId";
	@FieldDescribe("기본 뷰 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + defaultViewId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String defaultViewId;

	public static final String defaultViewName_FIELDNAME = "defaultViewName";
	@FieldDescribe("기본 뷰 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + defaultViewName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String defaultViewName;

	public static final String categorySeq_FIELDNAME = "categorySeq";
	@FieldDescribe("분류 정보 정렬 번호")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categorySeq_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categorySeq_FIELDNAME)
	private String categorySeq;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("분류 정보 설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String categoryIcon_FIELDNAME = "categoryIcon";
	@FieldDescribe("아이콘 icon Base64 인코딩 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_32K, name = ColumnNamePrefix + categoryIcon_FIELDNAME)
	private String categoryIcon;

	public static final String categoryMemo_FIELDNAME = "categoryMemo";
	@FieldDescribe("비고 정보")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + categoryMemo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String categoryMemo;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("작성자, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorPerson_FIELDNAME)
	@Index(name = TABLE + "_creatorPerson")
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("작성자 Identity, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@Index(name = TABLE + "_creatorIdentity")
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("작성자 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@Index(name = TABLE + "_creatorUnitName")
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("작성자 최상위 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@Index(name = TABLE + "_creatorTopUnitName")
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	public static final String allPeopleView_FIELDNAME = "allPeopleView";
	@FieldDescribe("열람 범위는 모든 사람.")
	@Column(name = ColumnNamePrefix + allPeopleView_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean allPeopleView = true;

	public static final String allPeoplePublish_FIELDNAME = "allPeoplePublish";
	@FieldDescribe("게시 범위: 게시판에 모든 사람이 게시 가능.")
	@Column(name = ColumnNamePrefix + allPeoplePublish_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean allPeoplePublish = true;

	public static final String importViewAppId_FIELDNAME = "importViewAppId";
	@FieldDescribe("데이터 가져오기에 연결된 데이터 뷰의 게시판 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + importViewAppId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importViewAppId = null;

	public static final String importViewId_FIELDNAME = "importViewId";
	@FieldDescribe("데이터 가져오기에 연결된 데이터 뷰 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + importViewId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importViewId = null;

	public static final String importViewName_FIELDNAME = "importViewName";
	@FieldDescribe("데이터 가져오기에 연결된 데이터 뷰 이름.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + importViewName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importViewName = null;

	public static final String anonymousAble_FIELDNAME = "anonymousAble";
	@FieldDescribe("익명 접근 허용 여부.")
	@Column(name = ColumnNamePrefix + anonymousAble_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean anonymousAble = true;

	public static final String indexAble_FIELDNAME = "indexAble";
	@FieldDescribe("전문 검색 허용 여부.")
	@Column(name = ColumnNamePrefix + indexAble_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean indexAble = true;

	public static final String sendNotify_FIELDNAME = "sendNotify";
	@FieldDescribe("문서 게시 후 독자 알림 여부: true | false")
	@Column(name = ColumnNamePrefix + sendNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean sendNotify = true;

	public static final String blankToAllNotify_FIELDNAME = "blankToAllNotify";
	@FieldDescribe("문서 게시 범위가 비어 있을 때 전원 알림 여부: true(기본)|false")
	@Column(name = ColumnNamePrefix + blankToAllNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean blankToAllNotify = true;

	public static final String projection_FIELDNAME = "projection";
	@FieldDescribe("필드 매핑 설정.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_2K, name = ColumnNamePrefix + projection_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String projection;

	public static final String viewablePersonList_FIELDNAME = "viewablePersonList";
	@FieldDescribe("게시 열람 사용자")
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
	@FieldDescribe("게시 열람 조직")
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
	@FieldDescribe("게시 열람 그룹")
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

	public static final String publishablePersonList_FIELDNAME = "publishablePersonList";
	@FieldDescribe("게시 가능 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishablePersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishablePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishablePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishablePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishablePersonList;

	public static final String publishableUnitList_FIELDNAME = "publishableUnitList";
	@FieldDescribe("게시 가능 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableUnitList;

	public static final String publishableGroupList_FIELDNAME = "publishableGroupList";
	@FieldDescribe("게시 가능 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ publishableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableGroupList;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("분류 관리 가능 사용자")
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

	public static final String manageableUnitList_FIELDNAME = "manageableUnitList";
	@FieldDescribe("분류 관리 가능 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ manageableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageableUnitList;

	public static final String manageableGroupList_FIELDNAME = "manageableGroupList";
	@FieldDescribe("분류 관리 가능 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ manageableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageableGroupList;

	/**
	 * 분류 이름 조회
	 *
	 * @return
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * 분류 이름 설정
	 *
	 * @param categoryName
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * 분류 소속 게시판 ID 조회
	 *
	 * @return
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * 분류 소속 게시판 ID 설정
	 *
	 * @param appId
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * 상위 분류 ID 조회
	 *
	 * @return
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * 상위 분류 ID 설정
	 *
	 * @param parentId
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 분류 별칭 조회
	 *
	 * @return
	 */
	public String getCategoryAlias() {
		return categoryAlias;
	}

	/**
	 * 분류 별칭 설정
	 *
	 * @param categoryAlias
	 */
	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	/**
	 * 분류 정보 정렬 번호 조회
	 *
	 * @return
	 */
	public String getCategorySeq() {
		return categorySeq;
	}

	/**
	 * 분류 정렬 번호 설정
	 *
	 * @param categorySeq
	 */
	public void setCategorySeq(String categorySeq) {
		if(StringUtils.isBlank(categorySeq)){
			this.categorySeq = "000";
		}else{
			try {
				this.categorySeq = String.format("%0" + 3 + "d", Integer.parseInt(categorySeq));
			} catch (NumberFormatException e) {
			}
		}
	}

	/**
	 * 분류 설명 정보 조회
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 분류 설명 정보 설정
	 *
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 분류 아이콘 조회
	 *
	 * @return
	 */
	public String getCategoryIcon() {
		return categoryIcon;
	}

	/**
	 * 분류 아이콘 설정
	 *
	 * @param categoryIcon
	 */
	public void setCategoryIcon(String categoryIcon) {
		this.categoryIcon = categoryIcon;
	}

	/**
	 * 분류 비고 조회
	 *
	 * @return
	 */
	public String getCategoryMemo() {
		return categoryMemo;
	}

	/**
	 * 분류 비고 설정
	 *
	 * @param categoryMemo
	 */
	public void setCategoryMemo(String categoryMemo) {
		this.categoryMemo = categoryMemo;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
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

	public String getReadFormId() {
		return readFormId;
	}

	public void setReadFormId(String readFormId) {
		this.readFormId = readFormId;
	}

	public String getReadFormName() {
		return readFormName;
	}

	public void setReadFormName(String readFormName) {
		this.readFormName = readFormName;
	}

	public String getDefaultViewId() {
		return defaultViewId;
	}

	public String getDefaultViewName() {
		return defaultViewName;
	}

	public void setDefaultViewId(String defaultViewId) {
		this.defaultViewId = defaultViewId;
	}

	public void setDefaultViewName(String defaultViewName) {
		this.defaultViewName = defaultViewName;
	}

	public String getWorkflowType() {
		return workflowType;
	}

	public String getWorkflowAppName() {
		return workflowAppName;
	}

	public String getWorkflowFlag() {
		return workflowFlag;
	}

	public void setWorkflowType(String workflowType) {
		this.workflowType = workflowType;
	}

	public void setWorkflowAppName(String workflowAppName) {
		this.workflowAppName = workflowAppName;
	}

	public void setWorkflowFlag(String workflowFlag) {
		this.workflowFlag = workflowFlag;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getWorkflowAppId() {
		return workflowAppId;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowAppId(String workflowAppId) {
		this.workflowAppId = workflowAppId;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public String getCreatorTopUnitName() {
		return creatorTopUnitName;
	}

	public void setCreatorTopUnitName(String creatorTopUnitName) {
		this.creatorTopUnitName = creatorTopUnitName;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Boolean getBlankToAllNotify() {
		return blankToAllNotify;
	}

	public void setBlankToAllNotify(Boolean blankToAllNotify) {
		this.blankToAllNotify = blankToAllNotify;
	}

	public List<String> getViewablePersonList() {
		return viewablePersonList == null ? new ArrayList<>() : viewablePersonList;
	}

	public List<String> getViewableUnitList() {
		return viewableUnitList == null ? new ArrayList<>() : viewableUnitList;
	}

	public List<String> getViewableGroupList() {
		return viewableGroupList == null ? new ArrayList<>() : viewableGroupList;
	}

	public List<String> getPublishablePersonList() {
		return publishablePersonList == null ? new ArrayList<>() : publishablePersonList;
	}

	public List<String> getPublishableUnitList() {
		return publishableUnitList == null ? new ArrayList<>() : publishableUnitList;
	}

	public List<String> getPublishableGroupList() {
		return publishableGroupList == null ? new ArrayList<>() : publishableGroupList;
	}

	public List<String> getManageablePersonList() {
		return manageablePersonList == null ? new ArrayList<>() : manageablePersonList;
	}

	public List<String> getManageableUnitList() {
		return manageableUnitList == null ? new ArrayList<>() : manageableUnitList;
	}

	public List<String> getManageableGroupList() {
		return manageableGroupList == null ? new ArrayList<>() : manageableGroupList;
	}

	public void setViewablePersonList(List<String> viewablePersonList) {
		this.viewablePersonList = viewablePersonList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setViewableUnitList(List<String> viewableUnitList) {
		this.viewableUnitList = viewableUnitList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setViewableGroupList(List<String> viewableGroupList) {
		this.viewableGroupList = viewableGroupList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setPublishablePersonList(List<String> publishablePersonList) {
		this.publishablePersonList = publishablePersonList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
	}

	public void setPublishableGroupList(List<String> publishableGroupList) {
		this.publishableGroupList = publishableGroupList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setManageablePersonList(List<String> manageablePersonList) {
		this.manageablePersonList = manageablePersonList;
	}

	public void setManageableUnitList(List<String> manageableUnitList) {
		this.manageableUnitList = manageableUnitList;
	}

	public void setManageableGroupList(List<String> manageableGroupList) {
		this.manageableGroupList = manageableGroupList;
	}

	public void addViewablePerson(String personName) {
		addStringToList(this.viewablePersonList, personName);
	}

	public void addViewableUnit(String unitName) {
		addStringToList(this.viewableUnitList, unitName);
	}

	public void addViewableGroup(String groupName) {
		addStringToList(this.viewableGroupList, groupName);
	}

	public void addPublishablePerson(String personName) {
		addStringToList(this.publishablePersonList, personName);
	}

	public void addPublishableUnit(String unitName) {
		addStringToList(this.publishableUnitList, unitName);
	}

	public void addPublishableGroup(String groupName) {
		addStringToList(this.publishableGroupList, groupName);
	}

	public void addManageablePerson(String personName) {
		addStringToList(this.manageablePersonList, personName);
	}

	public void addManageableUnit(String unitName) {
		addStringToList(this.manageableUnitList, unitName);
	}

	public void addManageableGroup(String groupName) {
		addStringToList(this.manageableGroupList, groupName);
	}

	public void removeViewablePerson(String personName) {
		removeStringFromList(this.viewablePersonList, personName);
	}

	public void removeViewableUnit(String unitName) {
		removeStringFromList(this.viewableUnitList, unitName);
	}

	public void removeViewableGroup(String groupName) {
		removeStringFromList(this.viewableGroupList, groupName);
	}

	public void removePublishablePerson(String personName) {
		removeStringFromList(this.publishablePersonList, personName);
	}

	public void removePublishableUnit(String unitName) {
		removeStringFromList(this.publishableUnitList, unitName);
	}

	public void removePublishableGroup(String groupName) {
		removeStringFromList(this.publishableGroupList, groupName);
	}

	public void removeManageablePerson(String personName) {
		removeStringFromList(this.manageablePersonList, personName);
	}

	public void removeManageableUnit(String unitName) {
		removeStringFromList(this.manageableUnitList, unitName);
	}

	public void removeManageableGroup(String groupName) {
		removeStringFromList(this.manageableGroupList, groupName);
	}

	private List<String> addStringToList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (!sourceList.contains(targetString)) {
			sourceList.add(targetString);
		}
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return sourceList;
	}

	private List<String> removeStringFromList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (sourceList.contains(targetString)) {
			sourceList.remove(targetString);
		}
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return sourceList;
	}

	public Boolean getAllPeopleView() {
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			this.allPeopleView = true;
		} else {
			this.allPeopleView = false;
		}
		return allPeopleView;
	}

	public Boolean getAllPeoplePublish() {
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return allPeoplePublish;
	}

	public void setAllPeopleView(Boolean allPeopleView) {
		this.allPeopleView = allPeopleView;
	}

	public void setAllPeoplePublish(Boolean allPeoplePublish) {
		this.allPeoplePublish = allPeoplePublish;
	}

	public String getImportViewId() {
		return importViewId;
	}

	public void setImportViewId(String importViewId) {
		this.importViewId = importViewId;
	}

	public String getImportViewName() {
		return importViewName;
	}

	public void setImportViewName(String importViewName) {
		this.importViewName = importViewName;
	}

	public String getImportViewAppId() {
		return importViewAppId;
	}

	public void setImportViewAppId(String importViewAppId) {
		this.importViewAppId = importViewAppId;
	}

	public Boolean getAnonymousAble() {
		return anonymousAble;
	}

	public void setAnonymousAble(Boolean anonymousAble) {
		this.anonymousAble = anonymousAble;
	}

	public Boolean getSendNotify() {
		return sendNotify;
	}

	public void setSendNotify(Boolean sendNotify) {
		this.sendNotify = sendNotify;
	}

	public String getProjection() {
		return projection;
	}

	public void setProjection(String projection) {
		this.projection = projection;
	}

	public Boolean getIndexAble() {
		return indexAble;
	}

	public void setIndexAble(Boolean indexAble) {
		this.indexAble = indexAble;
	}
}
