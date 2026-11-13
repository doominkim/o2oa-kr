package com.x.query.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

@Schema(name = "ItemAccess", description = "업무 필드 접근 권한 설정.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.ItemAccess.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ItemAccess.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.ColumnNamePrefix + ItemAccess.itemCategoryId_FIELDNAME,
				JpaObject.ColumnNamePrefix + ItemAccess.path_FIELDNAME }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ItemAccess extends SliceJpaObject {

	private static final long serialVersionUID = 3586573657619339181L;

	private static final String TABLE = PersistenceProperties.ItemAccess.table;

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
		// nothing
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.readActivityList = this.properties.getReadActivityList();
			this.editActivityList = this.properties.getEditActivityList();
			this.readerList = this.properties.getReaderList();
			this.editorList = this.properties.getEditorList();
			this.extension = this.properties.getExtension();
			this.excludeReaderList = this.properties.getExcludeReaderList();
			this.excludeReadActivityList = this.properties.getExcludeReadActivityList();
			this.excludeEditorList = this.properties.getExcludeEditorList();
			this.excludeEditActivityList = this.properties.getExcludeEditActivityList();
		}
	}

	public static final String appId_FIELDNAME = "appId";
	@FieldDescribe("앱 ID.")
	@Column(length = length_255B, name = ColumnNamePrefix + appId_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + appId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String appId;

	public static final String itemCategoryId_FIELDNAME = "itemCategoryId";
	@FieldDescribe("프로세스 ID 또는 콘텐츠 관리 분류 ID.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + itemCategoryId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String itemCategoryId;

	public static final String itemCategory_FIELDNAME = "itemCategory";
	@FieldDescribe("필드 소속 업무: 프로세스는 pp, 콘텐츠 관리는 cms.")
	@Enumerated(EnumType.STRING)
	@Column(length = ItemCategory.length, name = ColumnNamePrefix + itemCategory_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ItemCategory itemCategory;

	public static final String path_FIELDNAME = "path";
	@FieldDescribe("필드 경로.")
	@Column(length = length_255B, name = ColumnNamePrefix + path_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + path_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String path;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getItemCategoryId() {
		return itemCategoryId;
	}

	public void setItemCategoryId(String itemCategoryId) {
		this.itemCategoryId = itemCategoryId;
	}

	public ItemCategory getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(ItemCategory itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public static final String properties_FIELDNAME = "properties";
	@Schema(description = "속성 저장 필드.")
	@FieldDescribe("속성 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private ItemAccessProperties properties;

	public ItemAccessProperties getProperties() {
		if (null == this.properties) {
			this.properties = new ItemAccessProperties();
		}
		return this.properties;
	}

	public void setProperties(ItemAccessProperties properties) {
		this.properties = properties;
	}

	@FieldDescribe("확장 정보.")
	@Transient
	private String extension;

	@FieldDescribe("조회 가능 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
	@Transient
	private List<String> readerList;

	@FieldDescribe("조회 불가 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
	@Transient
	private List<String> excludeReaderList;

	@FieldDescribe("조회 가능 프로세스 활동 목록.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'unique':'活动唯一编码','name':'活动名称','alias':'活动别名','type':'活动类型','process':'流程标志'}")
	@Transient
	private List<ItemAccessActivity> readActivityList;

	@FieldDescribe("조회 불가 프로세스 활동 목록.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'unique':'活动唯一编码','name':'活动名称','alias':'活动别名','type':'活动类型','process':'流程标志'}")
	@Transient
	private List<ItemAccessActivity> excludeReadActivityList;

	@FieldDescribe("편집 가능 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
	@Transient
	private List<String> editorList;

	@FieldDescribe("편집 불가 대상 DN 목록: 사용자, 조직, 그룹, 역할.")
	@Transient
	private List<String> excludeEditorList;

	@FieldDescribe("편집 가능 프로세스 활동 목록.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'unique':'活动唯一编码','name':'活动名称','alias':'活动别名','type':'活动类型','process':'流程标志'}")
	@Transient
	private List<ItemAccessActivity> editActivityList;

	@FieldDescribe("편집 불가 프로세스 활동 목록.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "ItemAccessActivity",
			fieldValue = "{'unique':'活动唯一编码','name':'活动名称','alias':'活动别名','type':'活动类型','process':'流程标志'}")
	@Transient
	private List<ItemAccessActivity> excludeEditActivityList;

	public List<String> getReaderList() {
		if ((null == this.readerList) && (null != this.properties)) {
			this.readerList = this.properties.getReaderList();
		}
		return readerList;
	}

	public void setReaderList(List<String> readerList) {
		this.readerList = readerList;
		this.getProperties().setReaderList(readerList);
	}

	public List<ItemAccessActivity> getReadActivityList() {
		if ((null == this.readActivityList) && (null != this.properties)) {
			this.readActivityList = this.properties.getReadActivityList();
		}
		return readActivityList;
	}

	public void setReadActivityList(
			List<ItemAccessActivity> readActivityList) {
		this.readActivityList = readActivityList;
		this.getProperties().setReadActivityList(readActivityList);
	}

	public List<String> getEditorList() {
		if ((null == this.editorList) && (null != this.properties)) {
			this.editorList = this.properties.getEditorList();
		}
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
		this.getProperties().setEditorList(editorList);
	}

	public List<ItemAccessActivity> getEditActivityList() {
		if ((null == this.editActivityList) && (null != this.properties)) {
			this.editActivityList = this.properties.getEditActivityList();
		}
		return editActivityList;
	}

	public void setEditActivityList(
			List<ItemAccessActivity> editActivityList) {
		this.editActivityList = editActivityList;
		this.getProperties().setEditActivityList(editActivityList);
	}

	public String getExtension() {
		if ((null == this.extension) && (null != this.properties)) {
			this.extension = this.properties.getExtension();
		}
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
		this.getProperties().setExtension(extension);
	}

	public List<String> getExcludeReaderList() {
		if ((null == this.excludeReaderList) && (null != this.properties)) {
			this.excludeReaderList = this.properties.getExcludeReaderList();
		}
		return excludeReaderList;
	}

	public void setExcludeReaderList(List<String> excludeReaderList) {
		this.excludeReaderList = excludeReaderList;
		this.getProperties().setExcludeReaderList(excludeReaderList);
	}

	public List<ItemAccessActivity> getExcludeReadActivityList() {
		if ((null == this.excludeReadActivityList) && (null != this.properties)) {
			this.excludeReadActivityList = this.properties.getExcludeReadActivityList();
		}
		return excludeReadActivityList;
	}

	public void setExcludeReadActivityList(
			List<ItemAccessActivity> excludeReadActivityList) {
		this.excludeReadActivityList = excludeReadActivityList;
		this.getProperties().setExcludeReadActivityList(excludeReadActivityList);
	}

	public List<String> getExcludeEditorList() {
		if ((null == this.excludeEditorList) && (null != this.properties)) {
			this.excludeEditorList = this.properties.getExcludeEditorList();
		}
		return excludeEditorList;
	}

	public void setExcludeEditorList(List<String> excludeEditorList) {
		this.excludeEditorList = excludeEditorList;
		this.getProperties().setExcludeEditorList(excludeEditorList);
	}

	public List<ItemAccessActivity> getExcludeEditActivityList() {
		if ((null == this.excludeEditActivityList) && (null != this.properties)) {
			this.excludeEditActivityList = this.properties.getExcludeEditActivityList();
		}
		return excludeEditActivityList;
	}

	public void setExcludeEditActivityList(
			List<ItemAccessActivity> excludeEditActivityList) {
		this.excludeEditActivityList = excludeEditActivityList;
		this.getProperties().setExcludeEditActivityList(excludeEditActivityList);
	}
}
