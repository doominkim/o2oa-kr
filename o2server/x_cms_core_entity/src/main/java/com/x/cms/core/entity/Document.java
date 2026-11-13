package com.x.cms.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import com.x.base.core.project.tools.StringTools;
import io.swagger.v3.oas.annotations.media.Schema;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.*;

import javax.persistence.OrderColumn;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 문서 기본 정보 클래스
 *
 * @author O2LEE
 *
 */
@Schema(name = "Document", description = "콘텐츠 관리 문서.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Document.table, indexes = {
		@javax.persistence.Index(name = Document.TABLE + JpaObject.IndexNameMiddle + Document.appId_FIELDNAME+"_UN",
				columnList = JpaObject.ColumnNamePrefix + Document.appId_FIELDNAME+","+
						JpaObject.ColumnNamePrefix + Document.categoryId_FIELDNAME)
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Document extends SliceJpaObject {

	private static final long serialVersionUID = 7668822947307502058L;
	public static final String TABLE = PersistenceProperties.Document.table;
	public static final int STRING_VALUE_MAX_LENGTH = JpaObject.length_255B;
	public static final String DOC_STATUS_PUBLISH = "published";
	public static final String DOCUMENT_TYPE_INFO = "信息";
	public static final String DOCUMENT_TYPE_DATA = "数据";

	/* 위는 JpaObject 기본 필드 */
	@Override
	public void onPersist() throws Exception {
		if (StringTools.utf8Length(this.getTitle()) > length_255B) {
			this.title = StringTools.utf8SubString(this.getTitle(), length_255B - 3);
		}
	}

	public Document() {
		this.properties = new DocumentProperties();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("数据库主键,自动生成.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	private String id = createId();
	/*
	 * =============================================================================
	 * ===== 위는 JpaObject 기본 필드
	 * =============================================================================
	 * =====
	 */

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("문서 요약")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String summary;

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("문서 제목")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + title_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String documentType_FIELDNAME = "documentType";
	@FieldDescribe("文档类型，跟随分类类型，信息 | 数据")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + documentType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String documentType = "信息";

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String appName_FIELDNAME = "appName";
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appName;

	public static final String appAlias_FIELDNAME = "appAlias";
	@FieldDescribe("게시판 별칭")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + appAlias_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appAlias;

	public static final String categoryId_FIELDNAME = "categoryId";
	@FieldDescribe("분류ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + categoryId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + categoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryId;

	public static final String categoryName_FIELDNAME = "categoryName";
	@FieldDescribe("분류 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryName;

	public static final String categoryAlias_FIELDNAME = "categoryAlias";
	@FieldDescribe("분류 별칭")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + categoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String categoryAlias;

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("바인딩된 양식 템플릿 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String form;

	public static final String formName_FIELDNAME = "formName";
	@FieldDescribe("바인딩된 양식 템플릿 이름")
	@Column(length = JpaObject.length_96B, name = ColumnNamePrefix + formName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String formName;

	public static final String importBatchName_FIELDNAME = "importBatchName";
	@FieldDescribe("파일 가져오기 배치 번호: 보통 분류ID+타임스탬프")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + importBatchName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + importBatchName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String importBatchName;

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

	public static final String ppFormId_FIELDNAME = "ppFormId";
	@FieldDescribe("프로세스 플랫폼 양식 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + ppFormId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String ppFormId;

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

	public static final String docStatus_FIELDNAME = "docStatus";
	@FieldDescribe("문서 상태: published | waitPublish | draft | archived")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + docStatus_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + docStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String docStatus = "draft";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("说明备注，可以填写说明信息，如导入信息检验失败原因等")
	@Column(length = STRING_VALUE_MAX_LENGTH, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = null;

	public static final String viewCount_FIELDNAME = "viewCount";
	@FieldDescribe("문서 조회 횟수")
	@Column(name = ColumnNamePrefix + viewCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long viewCount = 0L;

	public static final String commendCount_FIELDNAME = "commendCount";
	@FieldDescribe("문서 좋아요 수")
	@Column(name = ColumnNamePrefix + commendCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commendCount = 0L;

	public static final String commentCount_FIELDNAME = "commentCount";
	@FieldDescribe("문서 댓글 수")
	@Column(name = ColumnNamePrefix + commentCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long commentCount = 0L;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("문서 게시 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;

	public static final String modifyTime_FIELDNAME = "modifyTime";
	@FieldDescribe("문서 수정 시간")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = ColumnNamePrefix + modifyTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + modifyTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date modifyTime;

	public static final String isTop_FIELDNAME = "isTop";
	@FieldDescribe("상단 고정 여부")
	@Column(name = ColumnNamePrefix + isTop_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isTop = false;

	public static final String isAllRead_FIELDNAME = "isAllRead";
	@FieldDescribe("전원 열람 가능 여부")
	@Column(name = ColumnNamePrefix + isAllRead_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean isAllRead = false;

	public static final String hasIndexPic_FIELDNAME = "hasIndexPic";
	@FieldDescribe("대표 이미지 포함 여부")
	@Column(name = ColumnNamePrefix + hasIndexPic_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean hasIndexPic = false;

	public static final String reviewed_FIELDNAME = "reviewed";
	@FieldDescribe("是否已经更新review信息.")
	@Column(name = ColumnNamePrefix + reviewed_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean reviewed = false;

	public static final String indexPics_FIELDNAME = "indexPics";
	@FieldDescribe("대표 이미지, pictureList의 앞 3개 이미지를 목록 표시에 사용")
	@Column(name = ColumnNamePrefix + indexPics_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String indexPics;

	public static final String sequenceTitle_FIELDNAME = "sequenceTitle";
	@FieldDescribe("제목 정렬용 sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceTitle_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceTitle_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceTitle = "";

	public static final String sequenceAppAlias_FIELDNAME = "sequenceAppAlias";
	@FieldDescribe("게시판 별칭 정렬용 sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceAppAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceAppAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceAppAlias = "";

	public static final String sequenceCategoryAlias_FIELDNAME = "sequenceCategoryAlias";
	@FieldDescribe("분류 별칭 정렬용 sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCategoryAlias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCategoryAlias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCategoryAlias = "";

	public static final String sequenceCreatorPerson_FIELDNAME = "sequenceCreatorPerson";
	@FieldDescribe("작성자 정렬용 sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCreatorPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCreatorPerson_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCreatorPerson = "";

	public static final String sequenceCreatorUnitName_FIELDNAME = "sequenceCreatorUnitName";
	@FieldDescribe("작성자 조직 정렬용 sequence")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + sequenceCreatorUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sequenceCreatorUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sequenceCreatorUnitName = "";

	public static final String readPersonList_FIELDNAME = "readPersonList";
	@FieldDescribe("열람 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readPersonList;

	public static final String readUnitList_FIELDNAME = "readUnitList";
	@FieldDescribe("열람 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readUnitList;

	public static final String readGroupList_FIELDNAME = "readGroupList";
	@FieldDescribe("열람 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ readGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ readGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + readGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> readGroupList;

	public static final String authorPersonList_FIELDNAME = "authorPersonList";
	@FieldDescribe("작성자 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ authorPersonList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + authorPersonList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorPersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorPersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorPersonList;

	public static final String authorUnitList_FIELDNAME = "authorUnitList";
	@FieldDescribe("작성자 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ authorUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + authorUnitList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorUnitList;

	public static final String authorGroupList_FIELDNAME = "authorGroupList";
	@FieldDescribe("작성자 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ authorGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + authorGroupList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ authorGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + authorGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> authorGroupList;

	public static final String managerList_FIELDNAME = "managerList";
	@FieldDescribe("관리자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ managerList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + managerList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ managerList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + managerList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> managerList;

	public static final String pictureList_FIELDNAME = "pictureList";
	@FieldDescribe("대표 이미지 목록")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ pictureList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + pictureList_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ pictureList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + pictureList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> pictureList;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private DocumentProperties properties;

	public static final String stringValue01_FIELDNAME = "stringValue01";
	@FieldDescribe("业务数据String值01.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue01;

	public static final String stringValue02_FIELDNAME = "stringValue02";
	@FieldDescribe("业务数据String值02.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue02;

	public static final String stringValue03_FIELDNAME = "stringValue03";
	@FieldDescribe("业务数据String值03.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue03_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue03;

	public static final String stringValue04_FIELDNAME = "stringValue04";
	@FieldDescribe("业务数据String值04.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue04_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + stringValue04_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue04;

	public static final String stringValue05_FIELDNAME = "stringValue05";
	@FieldDescribe("业务数据String值05.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue05_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue05;

	public static final String stringValue06_FIELDNAME = "stringValue06";
	@FieldDescribe("业务数据String值06.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue06_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue06;

	public static final String stringValue07_FIELDNAME = "stringValue07";
	@FieldDescribe("业务数据String值07.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue07_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue07;

	public static final String stringValue08_FIELDNAME = "stringValue08";
	@FieldDescribe("业务数据String值08.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue08_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue08;

	public static final String stringValue09_FIELDNAME = "stringValue09";
	@FieldDescribe("业务数据String值09.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue09_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue09;

	public static final String stringValue10_FIELDNAME = "stringValue10";
	@FieldDescribe("业务数据String值10.")
	@Column(length = length_255B, name = ColumnNamePrefix + stringValue10_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String stringValue10;

	public static final String longValue01_FIELDNAME = "longValue01";
	@FieldDescribe("业务数据Long值01.")
	@Column(name = ColumnNamePrefix + longValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue01;

	public static final String longValue02_FIELDNAME = "longValue02";
	@FieldDescribe("业务数据Long值02.")
	@Column(name = ColumnNamePrefix + longValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + longValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long longValue02;

	public static final String doubleValue01_FIELDNAME = "doubleValue01";
	@FieldDescribe("业务数据Double值01.")
	@Column(name = ColumnNamePrefix + doubleValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue01;

	public static final String doubleValue02_FIELDNAME = "doubleValue02";
	@FieldDescribe("业务数据Double值02.")
	@Column(name = ColumnNamePrefix + doubleValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + doubleValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double doubleValue02;

	public static final String dateTimeValue01_FIELDNAME = "dateTimeValue01";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值01.")
	@Column(name = ColumnNamePrefix + dateTimeValue01_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue01_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue01;

	public static final String dateTimeValue02_FIELDNAME = "dateTimeValue02";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值02.")
	@Column(name = ColumnNamePrefix + dateTimeValue02_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + dateTimeValue02_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue02;

	public static final String dateTimeValue03_FIELDNAME = "dateTimeValue03";
	@Temporal(TemporalType.TIMESTAMP)
	@FieldDescribe("业务数据DateTime值03.")
	@Column(name = ColumnNamePrefix + dateTimeValue03_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date dateTimeValue03;

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

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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

	public String getDocStatus() {
		return docStatus;
	}

	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getPpFormId() {
		return ppFormId;
	}

	public void setPpFormId(String ppFormId) {
		this.ppFormId = ppFormId;
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

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Long getViewCount() {
		return viewCount;
	}

	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}

	public void addViewCount(Integer count) {
		if (this.viewCount == null) {
			this.viewCount = 0L;
		}
		this.viewCount = this.viewCount + count;
	}

	public String getCategoryAlias() {
		return categoryAlias;
	}

	public void setCategoryAlias(String categoryAlias) {
		this.categoryAlias = categoryAlias;
	}

	public List<String> getPictureList() {
		return pictureList;
	}

	public void setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
	}

	public Boolean getHasIndexPic() {
		return hasIndexPic;
	}

	public void setHasIndexPic(Boolean hasIndexPic) {
		this.hasIndexPic = hasIndexPic;
	}

	public String getIndexPics() {
		return indexPics;
	}

	public void setIndexPics(String indexPics) {
		this.indexPics = indexPics;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
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

	public List<String> getReadPersonList() {
		if (this.readPersonList == null) {
			this.readPersonList = new ArrayList<>();
		}
		return this.readPersonList;
	}

	public List<String> getReadUnitList() {
		if (this.readUnitList == null) {
			this.readUnitList = new ArrayList<>();
		}
		return this.readUnitList;
	}

	public List<String> getReadGroupList() {
		if (this.readGroupList == null) {
			this.readGroupList = new ArrayList<>();
		}
		return this.readGroupList;
	}

	public List<String> getAuthorPersonList() {
		if (this.authorPersonList == null) {
			this.authorPersonList = new ArrayList<>();
		}
		return this.authorPersonList;
	}

	public List<String> getAuthorUnitList() {
		if (this.authorUnitList == null) {
			this.authorUnitList = new ArrayList<>();
		}
		return this.authorUnitList;
	}

	public List<String> getAuthorGroupList() {
		if (this.authorGroupList == null) {
			this.authorGroupList = new ArrayList<>();
		}
		return this.authorGroupList;
	}

	public List<String> getManagerList() {
		if (this.managerList == null) {
			this.managerList = new ArrayList<>();
		}
		return this.managerList;
	}

	public void setReadPersonList(List<String> readPersonList) {
		this.readPersonList = readPersonList;
	}

	public void setReadUnitList(List<String> readUnitList) {
		this.readUnitList = readUnitList;
	}

	public void setReadGroupList(List<String> readGroupList) {
		this.readGroupList = readGroupList;
	}

	public void setAuthorPersonList(List<String> authorPersonList) {
		this.authorPersonList = authorPersonList;
	}

	public void setAuthorUnitList(List<String> authorUnitList) {
		this.authorUnitList = authorUnitList;
	}

	public void setAuthorGroupList(List<String> authorGroupList) {
		this.authorGroupList = authorGroupList;
	}

	public void setManagerList(List<String> managerList) {
		this.managerList = managerList;
	}

	public String getImportBatchName() {
		return importBatchName;
	}

	public void setImportBatchName(String importBatchName) {
		this.importBatchName = importBatchName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

	public Long getCommendCount() {
		return commendCount;
	}

	public void setCommendCount(Long commendCount) {
		this.commendCount = commendCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public void addCommentCount(Integer count) {
		if (this.commendCount == null) {
			this.commentCount = 0L;
		}
		this.commentCount = this.commentCount + count;
	}

	public void addCommendCount(Integer count) {
		if (this.commendCount == null) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount + count;
	}

	public void subCommentCount(Integer count) {
		if (this.commentCount == null) {
			this.commentCount = 0L;
		}
		this.commentCount = this.commentCount - count;
		if (this.commentCount < 0) {
			this.commentCount = 0L;
		}
	}

	public void subCommendCount(Integer count) {
		if (this.commendCount == null) {
			this.commendCount = 0L;
		}
		this.commendCount = this.commendCount - count;
		if (this.commendCount < 0) {
			this.commendCount = 0L;
		}
	}

	public Boolean getIsAllRead() {
		return isAllRead;
	}

	public void setIsAllRead(Boolean isAllRead) {
		this.isAllRead = isAllRead;
	}

	public Boolean getIsTop() {
		return isTop;
	}

	public void setIsTop(Boolean isTop) {
		this.isTop = isTop;
	}

	public String getAppAlias() {
		return appAlias;
	}

	public void setAppAlias(String appAlias) {
		this.appAlias = appAlias;
	}

	public String getSequenceTitle() {
		return sequenceTitle;
	}

	public void setSequenceTitle(String sequenceTitle) {
		this.sequenceTitle = getSequenceString(sequenceTitle);
	}

	public String getSequenceAppAlias() {
		return sequenceAppAlias;
	}

	public void setSequenceAppAlias(String sequenceAppAlias) {
		this.sequenceAppAlias = getSequenceString(sequenceAppAlias);
	}

	public String getSequenceCategoryAlias() {
		return sequenceCategoryAlias;
	}

	public void setSequenceCategoryAlias(String sequenceCategoryAlias) {
		this.sequenceCategoryAlias = getSequenceString(sequenceCategoryAlias);
	}

	public String getSequenceCreatorPerson() {
		return sequenceCreatorPerson;
	}

	public void setSequenceCreatorPerson(String sequenceCreatorPerson) {
		this.sequenceCreatorPerson = getSequenceString(sequenceCreatorPerson);
	}

	public String getSequenceCreatorUnitName() {
		return sequenceCreatorUnitName;
	}

	public void setSequenceCreatorUnitName(String sequenceCreatorUnitName) {
		this.sequenceCreatorUnitName = getSequenceString(sequenceCreatorUnitName);
	}

	public DocumentProperties getProperties() {
		if (null == this.properties) {
			this.properties = new DocumentProperties();
		}
		return this.properties;
	}

	public void setProperties(DocumentProperties properties) {
		this.properties = properties;
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public Date getDateTimeValue03() {
		return dateTimeValue03;
	}

	public void setDateTimeValue03(Date dateTimeValue03) {
		this.dateTimeValue03 = dateTimeValue03;
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

	// -------------------Reader-------------------------
	// -------------------2020-06-12 DistinguishedName의 뒤 두 부분만 저장하도록 변경, 첫 부분은 실행 중 수정될 수 있음
	public void addToReadPersonList(String readPerson) {
		this.readPersonList = addStringToList(this.readPersonList, getShortTargetFlag(readPerson));
	}

	public void addToReadUnitList(String readUnit) {
		this.readUnitList = addStringToList(this.readUnitList, getShortTargetFlag(readUnit));
	}

	public void addToReadGroupList(String readGroup) {
		this.readGroupList = addStringToList(this.readGroupList, getShortTargetFlag(readGroup));
	}

	// --------------------전체 식별자를 삭제하고, 2부분만 저장한 식별자도 삭제해야 함
	public void removeFromReadPersonList(String readPerson) {
		removeStringFromList(this.readPersonList, readPerson);
		removeStringFromList(this.readPersonList, getShortTargetFlag(readPerson));
	}

	public void removeFromReadUnitList(String readUnit) {
		removeStringFromList(this.readUnitList, readUnit);
		removeStringFromList(this.readUnitList, getShortTargetFlag(readUnit));
	}

	public void removeFromReadGroupList(String readGroup) {
		removeStringFromList(this.readGroupList, readGroup);
		removeStringFromList(this.readGroupList, getShortTargetFlag(readGroup));
	}

	// -------------------Author-------------------------
	// -------------------2020-06-12 DistinguishedName의 뒤 두 부분만 저장하도록 변경, 첫 부분은 실행 중 수정될 수 있음
	public void addToAuthorPersonList(String authorPerson) {
		this.authorPersonList = addStringToList(this.authorPersonList, getShortTargetFlag(authorPerson));
	}

	public void addToAuthorUnitList(String authorUnit) {
		this.authorUnitList = addStringToList(this.authorUnitList, getShortTargetFlag(authorUnit));
	}

	public void addToAuthorGroupList(String authorGroup) {
		this.authorGroupList = addStringToList(this.authorGroupList, getShortTargetFlag(authorGroup));
	}

	// --------------------전체 식별자를 삭제하고, 2부분만 저장한 식별자도 삭제해야 함
	public void removeFromAuthorPersonList(String authorPerson) {
		removeStringFromList(this.authorPersonList, authorPerson);
		removeStringFromList(this.authorPersonList, getShortTargetFlag(authorPerson));
	}

	public void removeFromAuthorUnitList(String authorUnit) {
		removeStringFromList(this.authorUnitList, authorUnit);
		removeStringFromList(this.authorUnitList, getShortTargetFlag(authorUnit));
	}

	public void removeFromAuthorGroupList(String authorGroup) {
		removeStringFromList(this.authorGroupList, authorGroup);
		removeStringFromList(this.authorGroupList, getShortTargetFlag(authorGroup));
	}

	// -------------------Manager-------------------------
	// -------------------2020-06-12 DistinguishedName의 뒤 두 부분만 저장하도록 변경, 첫 부분은 실행 중 수정될 수 있음
	public void addToManagerList(String manager) {
		addStringToList(this.managerList, getShortTargetFlag(manager));
	}

	// --------------------전체 식별자를 삭제하고, 2부분만 저장한 식별자도 삭제해야 함
	public void removeFromManagerList(String manager) {
		removeStringFromList(this.managerList, manager);
		removeStringFromList(this.managerList, getShortTargetFlag(manager));
	}

	/**
	 * 조직, 사용자, 그룹 이름의 distinguishedName 식별자를 두 부분만 조회, 기본 3부분이며 첫 부분은 자주 변경되어 권한 식별자로 적합하지 않음
	 *
	 * @param distinguishedName
	 * @return
	 */
	private String getShortTargetFlag(String distinguishedName) {
		String target = null;
		if (StringUtils.isNotEmpty(distinguishedName)) {
			String[] array = distinguishedName.split("@");
			StringBuffer sb = new StringBuffer();
			if (array.length == 3) {
				target = sb.append(array[1]).append("@").append(array[2]).toString();
			} else if (array.length == 2) {
				// 2단계
				target = sb.append(array[0]).append("@").append(array[1]).toString();
			} else {
				target = array[0];
			}
		}
		return target;
	}

	private List<String> addStringToList(List<String> sourceList, String targetString) {
		if (sourceList == null) {
			sourceList = new ArrayList<>();
		}
		if (!sourceList.contains(targetString)) {
			sourceList.add(targetString);
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
		return sourceList;
	}

	/**
	 * 정렬 지원 열 이름
	 */
	public static final String[] documentFieldNames = { appAlias_FIELDNAME, appId_FIELDNAME, appName_FIELDNAME,
			categoryAlias_FIELDNAME, categoryId_FIELDNAME, categoryName_FIELDNAME, commendCount_FIELDNAME,
			commentCount_FIELDNAME, creatorPerson_FIELDNAME, creatorTopUnitName_FIELDNAME, creatorUnitName_FIELDNAME,
			description_FIELDNAME, docStatus_FIELDNAME, hasIndexPic_FIELDNAME, isTop_FIELDNAME, modifyTime_FIELDNAME,
			publishTime_FIELDNAME, summary_FIELDNAME, title_FIELDNAME, viewCount_FIELDNAME, createTime_FIELDNAME,
			updateTime_FIELDNAME };

	/**
	 * 지정한 열 이름에 대응하는 sequence 열이 생성되었는지 판단
	 *
	 * @param orderField - 열 이름
	 * @return
	 */
	public static Boolean isFieldInSequence(String orderField) {
		// 정렬 열 상태 판단
		if (StringUtils.isEmpty(orderField)) {
			return true;
		}
		if (id_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (sequence_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (createTime_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (title_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (appAlias_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (appName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (categoryAlias_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (categoryName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (creatorPerson_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		if (creatorUnitName_FIELDNAME.equalsIgnoreCase(orderField)) {
			return true;
		}
		return false;
	}

	/**
	 * 지정한 열 이름에 대응하는 sequence 열 조회
	 *
	 * @param fieldName - 열 이름
	 * @return
	 */
	public static String getSequnceFieldNameWithProperty(String fieldName) {
		if (StringUtils.isEmpty(fieldName) || Document.sequence_FIELDNAME.equalsIgnoreCase(fieldName)) {
			fieldName = sequence_FIELDNAME;
		}
		if (sequence_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequence_FIELDNAME;
		}
		if (createTime_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequence_FIELDNAME;
		}
		if (id_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return id_FIELDNAME;
		}
		if (title_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceTitle_FIELDNAME;
		}
		if (appAlias_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceAppAlias_FIELDNAME;
		}
		if (appName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceAppAlias_FIELDNAME;
		}
		if (categoryAlias_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if (categoryName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCategoryAlias_FIELDNAME;
		}
		if (creatorPerson_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCreatorPerson_FIELDNAME;
		}
		if (creatorUnitName_FIELDNAME.equalsIgnoreCase(fieldName)) {
			return sequenceCreatorUnitName_FIELDNAME;
		}
		return sequence_FIELDNAME;
	}

	private String getSequenceString(String sequenceString) {
		if (StringUtils.length(sequenceString) > 60) {
			return StringUtils.substring(sequenceString, 0, 60);
		}
		return sequenceString;
	}
}
