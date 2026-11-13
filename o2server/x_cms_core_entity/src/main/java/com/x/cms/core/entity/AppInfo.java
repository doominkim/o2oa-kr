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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.OrderColumn;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 게시판 정보
 *
 * @author O2LEE
 *
 */
@Schema(name = "AppInfo", description = "内容管理栏目信息.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AppInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.AppInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AppInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.AppInfo.table;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("데이터베이스 기본 키, 자동 생성.")
	@Flag
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();

	@Override
	public void onPersist() throws Exception {
	}
	public static final String appName_FIELDNAME = "appName";
	@Flag
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appName_FIELDNAME)
	@CheckPersist(citationNotExists = {
			@CitationNotExist(fields = "appName", type = AppInfo.class) }, allowEmpty = false)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@Flag
	@FieldDescribe("게시판 별칭")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appAlias;

	public static final String appType_FIELDNAME = "appType";
	@FieldDescribe("게시판 유형")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appType = "未分类";

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("默认文档类型：信息 | 数据")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String documentType = "信息";

	public static final String appInfoSeq_FIELDNAME = "appInfoSeq";
	@FieldDescribe("栏目信息排序号")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appInfoSeq_FIELDNAME)
	private String appInfoSeq;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("栏目信息说明")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String appIcon_FIELDNAME = "appIcon";
	@FieldDescribe("아이콘 icon Base64 인코딩 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + appIcon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appIcon;

	public static final String iconColor_FIELDNAME = "iconColor";
	@FieldDescribe("아이콘 주 색조.")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + iconColor_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String iconColor;

	public static final String appMemo_FIELDNAME = "appMemo";
	@FieldDescribe("备注信息")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appMemo_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String appMemo;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("작성자, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorPerson;

	public static final String creatorIdentity_FIELDNAME = "creatorIdentity";
	@FieldDescribe("작성자 Identity, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorIdentity;

	public static final String creatorUnitName_FIELDNAME = "creatorUnitName";
	@FieldDescribe("작성자 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorUnitName;

	public static final String creatorTopUnitName_FIELDNAME = "creatorTopUnitName";
	@FieldDescribe("작성자 최상위 조직, 시스템이 생성한 경우 비어 있을 수 있음.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ creatorTopUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String creatorTopUnitName;

	public static final String anonymousAble_FIELDNAME = "anonymousAble";
	@Column(name = ColumnNamePrefix + anonymousAble_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean anonymousAble = true;

	public static final String allowAnonymousAccessDoc_FIELDNAME = "allowAnonymousAccessDoc";
	@FieldDescribe("게시판 하위 문서의 익명 접근 허용 여부.")
	@Column(name = ColumnNamePrefix + allowAnonymousAccessDoc_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean allowAnonymousAccessDoc = false;

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

	public static final String sendNotify_FIELDNAME = "sendNotify";
	@FieldDescribe("문서 게시 후 독자 알림 여부: true | false")
	@Column(name = ColumnNamePrefix + sendNotify_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean sendNotify = true;

	public static final String showAllDocuments_FIELDNAME = "showAllDocuments";
	@FieldDescribe("게시판 전체 문서 표시 여부: true | false")
	@Column(name = ColumnNamePrefix + showAllDocuments_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean showAllDocuments = true;

	public static final String allowWaitPublish_FIELDNAME = "allowWaitPublish";
	@FieldDescribe("게시판 예약 게시 허용 여부: true | false(기본값)")
	@Column(name = ColumnNamePrefix + allowWaitPublish_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean allowWaitPublish = false;

	public static final String defaultEditForm_FIELDNAME = "defaultEditForm";
	@FieldDescribe("기본 편집 양식")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + defaultEditForm_FIELDNAME)
	private String defaultEditForm;

	public static final String defaultReadForm_FIELDNAME = "defaultReadForm";
	@FieldDescribe("기본 열람 양식")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + defaultReadForm_FIELDNAME)
	private String defaultReadForm;

	public static final String categoryList_FIELDNAME = "categoryList";
	@FieldDescribe("분류 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + categoryList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + categoryList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ categoryList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + categoryList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> categoryList;

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

	public static final String publishableRoleList_FIELDNAME = "publishableRoleList";
	@FieldDescribe("게시 가능 역할")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ publishableRoleList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ publishableRoleList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ publishableRoleList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + publishableRoleList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> publishableRoleList;

	public static final String manageablePersonList_FIELDNAME = "manageablePersonList";
	@FieldDescribe("게시판 관리 가능 사용자")
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
	@FieldDescribe("게시판 관리 가능 조직")
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
	@FieldDescribe("게시판 관리 가능 그룹")
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

	public static final String manageableRoleList_FIELDNAME = "manageableRoleList";
	@FieldDescribe("게시판 관리 가능 역할")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ manageableRoleList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ manageableRoleList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ manageableRoleList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + manageableRoleList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> manageableRoleList;

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	/**
	 * 게시판 이름 조회
	 *
	 * @return
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * 게시판 이름 설정
	 *
	 * @return
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * 게시판 별칭 조회
	 *
	 * @return
	 */
	public String getAppAlias() {
		return appAlias;
	}

	/**
	 * 게시판 별칭 설정
	 *
	 * @return
	 */
	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	/**
	 * 게시판 정렬 번호 조회
	 *
	 * @return
	 */
	public String getAppInfoSeq() {
		return appInfoSeq;
	}

	/**
	 * 게시판 정렬 번호 설정
	 *
	 * @return
	 */
	public void setAppInfoSeq(String appInfoSeq) {
		try {
			if (Integer.parseInt(appInfoSeq) < 10) {
				this.appInfoSeq = "0" + Integer.parseInt(appInfoSeq);
			} else {
				this.appInfoSeq = appInfoSeq;
			}
		} catch (Exception e) {
			this.appInfoSeq = "999";
		}
	}

	/**
	 * 게시판 설명 조회
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 게시판 설명 설정
	 *
	 * @return
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 게시판 아이콘 접근 경로 조회
	 *
	 * @return
	 */
	public String getAppIcon() {
		return appIcon;
	}

	/**
	 * 게시판 아이콘 접근 경로 설정
	 *
	 * @return
	 */
	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	/**
	 * 게시판 정보 비고 조회
	 *
	 * @return
	 */
	public String getAppMemo() {
		return appMemo;
	}

	/**
	 * 게시판 정보 비고 설정
	 *
	 * @return
	 */
	public void setAppMemo(String appMemo) {
		this.appMemo = appMemo;
	}

	public List<String> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<String> categoryList) {
		this.categoryList = categoryList;
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

	public String getCreatorUnitName() {
		return creatorUnitName;
	}

	public void setCreatorUnitName(String creatorUnitName) {
		this.creatorUnitName = creatorUnitName;
	}

	public String getIconColor() {
		return iconColor;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
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

	public List<String> getPublishableRoleList() {
		return publishableRoleList == null ? new ArrayList<>() : publishableRoleList;
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

	public List<String> getManageableRoleList() {
		return manageableRoleList == null ? new ArrayList<>() : manageableRoleList;
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
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setPublishableUnitList(List<String> publishableUnitList) {
		this.publishableUnitList = publishableUnitList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setPublishableGroupList(List<String> publishableGroupList) {
		this.publishableGroupList = publishableGroupList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
	}

	public void setPublishableRoleList(List<String> publishableRoleList) {
		this.publishableRoleList = publishableRoleList;
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
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

	public void setManageableRoleList(List<String> manageableRoleList) {
		this.manageableRoleList = manageableRoleList;
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

	public void addPublishableRole(String roleName) {
		addStringToList(this.publishableRoleList, roleName);
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

	public void addManageableRole(String roleName) {
		addStringToList(this.manageableRoleList, roleName);
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

	public void removePublishableRole(String roleName) {
		removeStringFromList(this.publishableRoleList, roleName);
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

	public void removeManageableRole(String roleName) {
		removeStringFromList(this.manageableRoleList, roleName);
	}

	public Boolean getAnonymousAble() {
		return anonymousAble;
	}

	public void setAnonymousAble(Boolean anonymousAble) {
		this.anonymousAble = anonymousAble;
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
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
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
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
			allPeoplePublish = true;
		} else {
			allPeoplePublish = false;
		}
		return sourceList;
	}

	public Boolean getAllPeopleView() {
		if (ListTools.isEmpty(this.viewablePersonList) && ListTools.isEmpty(this.viewableUnitList)
				&& ListTools.isEmpty(this.viewableGroupList)) {
			allPeopleView = true;
		} else {
			allPeopleView = false;
		}
		return allPeopleView;
	}

	public Boolean getAllPeoplePublish() {
		if (ListTools.isEmpty(this.publishablePersonList) && ListTools.isEmpty(this.publishableUnitList)
				&& ListTools.isEmpty(this.publishableGroupList) && ListTools.isEmpty(this.publishableRoleList)) {
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

	public Boolean getSendNotify() {
		return sendNotify;
	}

	public void setSendNotify(Boolean sendNotify) {
		this.sendNotify = sendNotify;
	}

	public Boolean getShowAllDocuments() { return this.showAllDocuments == null? true : this.showAllDocuments; }

	public void setShowAllDocuments(final Boolean showAllDocuments) { this.showAllDocuments = showAllDocuments; }

	public String getDefaultEditForm() {
		return defaultEditForm;
	}

	public void setDefaultEditForm(String defaultEditForm) {
		this.defaultEditForm = defaultEditForm;
	}

	public String getDefaultReadForm() {
		return defaultReadForm;
	}

	public void setDefaultReadForm(String defaultReadForm) {
		this.defaultReadForm = defaultReadForm;
	}

	public Boolean getAllowWaitPublish() {
		return allowWaitPublish;
	}

	public void setAllowWaitPublish(Boolean allowWaitPublish) {
		this.allowWaitPublish = allowWaitPublish;
	}

	public Boolean getAllowAnonymousAccessDoc() {
		return allowAnonymousAccessDoc;
	}

	public void setAllowAnonymousAccessDoc(Boolean allowAnonymousAccessDoc) {
		this.allowAnonymousAccessDoc = allowAnonymousAccessDoc;
	}
}
