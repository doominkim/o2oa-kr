package com.x.mind.entity;

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
import com.x.base.core.project.tools.DateTools;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 마인드맵 정보 테이블
 * 
 * @author O2LEE
 */
@Schema(name = "MindBaseInfo", description = "마인드맵 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.MindBaseInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MindBaseInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MindBaseInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.MindBaseInfo.table;

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
		if( null == this.getCreateTime() ) {
			this.setCreateTime( new Date() );
		}
		this.setSequence(StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId()));
		this.creator_sequence = StringUtils.join(this.creator, this.getSequence());
		this.folder_sequence = StringUtils.join(this.getFolderId(), this.getSequence());
		this.creatorUnit_sequence = StringUtils.join(this.getCreatorUnit(), this.getSequence());
		this.shared_sequence = StringUtils.join(this.getShared().toString(), this.getSequence());
		this.cooperative_sequence = StringUtils.join(this.getCooperative().toString(), this.getSequence());
	}

	public void composeSequnces() {
		if( null == this.getCreateTime() ) {
			this.setCreateTime( new Date() );
		}
		this.setSequence(StringUtils.join(DateTools.compact(this.getCreateTime()), this.getId()));
		this.creator_sequence = StringUtils.join(this.creator, this.getSequence());
		this.folder_sequence = StringUtils.join(this.getFolderId(), this.getSequence());
		this.creatorUnit_sequence = StringUtils.join(this.getCreatorUnit(), this.getSequence());
		this.shared_sequence = StringUtils.join(this.getShared().toString(), this.getSequence());
		this.cooperative_sequence = StringUtils.join(this.getCooperative().toString(), this.getSequence());
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
	// public static String[] FLA GS = new String[] { "id" };

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("마인드맵 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name = "";

	public static final String folderId_FIELDNAME = "folderId";
	@FieldDescribe("소속 디렉터리 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folderId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + folderId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String folderId = "";
	
	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("썸네일 정보")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon = "";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("비고 정보")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description = "";

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("자동 생성, 생성자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator = "";

	public static final String creatorUnit_FIELDNAME = "creatorUnit";
	@FieldDescribe("자동 생성, 생성자 소속 조직")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorUnit_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorUnit = "";

	public static final String shared_FIELDNAME = "shared";
	@FieldDescribe("자동 생성, 공유 여부")
	@Column(name = ColumnNamePrefix + shared_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean shared = false;

	public static final String cooperative_FIELDNAME = "cooperative";
	@FieldDescribe("자동 생성, 협업 모드 여부")
	@Column(name = ColumnNamePrefix + cooperative_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean cooperative = false;

	public static final String sharePersonList_FIELDNAME = "sharePersonList";
	@FieldDescribe("공유 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + sharePersonList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + sharePersonList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ sharePersonList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + sharePersonList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> sharePersonList;

	public static final String shareUnitList_FIELDNAME = "shareUnitList";
	@FieldDescribe("공유 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareUnitList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareUnitList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ shareUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareUnitList;

	public static final String shareGroupList_FIELDNAME = "shareGroupList";
	@FieldDescribe("공유 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ shareGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareGroupList;

	public static final String editorList_FIELDNAME = "editorList";
	@FieldDescribe("편집 가능 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name =  ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + editorList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + editorList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ editorList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editorList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editorList;

	public static final String shared_sequence_FIELDNAME = "shared_sequence";
	@FieldDescribe("자동 생성, shared 목록 순번.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + shared_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + shared_sequence_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String shared_sequence;

	public static final String folder_sequence_FIELDNAME = "folder_sequence";
	@FieldDescribe("자동 생성, folder 목록 순번")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + folder_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + folder_sequence_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String folder_sequence;

	public static final String creator_sequence_FIELDNAME = "creator_sequence";
	@FieldDescribe("자동 생성, creator 목록 순번.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creator_sequence_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator_sequence;

	public static final String creatorUnit_sequence_FIELDNAME = "creatorUnit_sequence";
	@FieldDescribe("자동 생성, creatorUnit 목록 순번.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creatorUnit_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + creatorUnit_sequence_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creatorUnit_sequence;

	public static final String cooperative_sequence_FIELDNAME = "cooperative_sequence";
	@FieldDescribe("자동 생성, cooperative 목록 순번.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + cooperative_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + cooperative_sequence_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String cooperative_sequence;

	public static final String fileVersion_sequence_FIELDNAME = "fileVersion";
	@FieldDescribe("자동 생성, 버전 번호")
	@Column(name = ColumnNamePrefix + fileVersion_sequence_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer fileVersion = 1;

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getCreator() {
		return creator;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreatorUnit() {
		return creatorUnit;
	}

	public void setCreatorUnit(String creatorUnit) {
		this.creatorUnit = creatorUnit;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public Boolean getShared() {
		if (this.shared == null) {
			this.shared = false;
		}
		return this.shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getFolder_sequence() {
		return folder_sequence;
	}

	public String getCreator_sequence() {
		return creator_sequence;
	}

	public String getCreatorUnit_sequence() {
		return creatorUnit_sequence;
	}

	public String getShared_sequence() {
		return shared_sequence;
	}

	public void setFolder_sequence(String folder_sequence) {
		this.folder_sequence = folder_sequence;
	}

	public void setCreator_sequence(String creator_sequence) {
		this.creator_sequence = creator_sequence;
	}

	public void setCreatorUnit_sequence(String creatorUnit_sequence) {
		this.creatorUnit_sequence = creatorUnit_sequence;
	}

	public void setShared_sequence(String shared_sequence) {
		this.shared_sequence = shared_sequence;
	}

	public List<String> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
	}

	public List<String> getSharePersonList() {
		return sharePersonList;
	}

	public List<String> getShareUnitList() {
		return shareUnitList;
	}

	public List<String> getShareGroupList() {
		return shareGroupList;
	}

	public void setSharePersonList(List<String> sharePersonList) {
		this.sharePersonList = sharePersonList;
	}

	public void setShareUnitList(List<String> shareUnitList) {
		this.shareUnitList = shareUnitList;
	}

	public void setShareGroupList(List<String> shareGroupList) {
		this.shareGroupList = shareGroupList;
	}

	public Boolean getCooperative() {
		if (cooperative == null) {
			cooperative = false;
		}
		return cooperative;
	}

	public String getCooperative_sequence() {
		return cooperative_sequence;
	}

	public void setCooperative(Boolean cooperative) {
		this.cooperative = cooperative;
	}

	public void setCooperative_sequence(String cooperative_sequence) {
		this.cooperative_sequence = cooperative_sequence;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	
}