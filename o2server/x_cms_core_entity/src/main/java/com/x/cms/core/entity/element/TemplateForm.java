package com.x.cms.core.entity.element;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "TemplateForm", description = "콘텐츠 관리 양식 템플릿.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Element.TemplateForm.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.TemplateForm.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TemplateForm extends SliceJpaObject {

	private static final long serialVersionUID = -1032370866037915354L;

	private static final String TABLE = PersistenceProperties.Element.TemplateForm.table;

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
		if (StringUtils.isEmpty(this.category)) {
			this.category = "미분류";
		}

	}

	/* 위는 JpaObject 기본 필드 */

	/* 실행 메서드 업데이트 */

	// public static String[] FLA GS = new String[] { "id", "alias" };

	/* flag 플래그 비트 */
	/* Entity 기본 필드 끝 */

	public String getDataOrMobileData() {
		if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		} else if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		}
		return null;
	}

	public String getMobileDataOrData() {
		if (StringUtils.isNotEmpty(this.getMobileData())) {
			return this.getMobileData();
		} else if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		}
		return null;
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("이름.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ name_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String name;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("템플릿 분류.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String category;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("양식 별칭.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ alias_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = AbstractPersistenceProperties.processPlatform_name_length, name = ColumnNamePrefix
			+ description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("icon Base64 인코딩 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + icon_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String icon;

	public static final String outline_FIELDNAME = "outline";
	@FieldDescribe("썸네일.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + outline_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String outline;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("텍스트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String mobileData_FIELDNAME = "mobileData";
	@FieldDescribe("모바일 텍스트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + mobileData_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mobileData;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMobileData() {
		return mobileData;
	}

	public void setMobileData(String mobileData) {
		this.mobileData = mobileData;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOutline() {
		return outline;
	}

	public void setOutline(String outline) {
		this.outline = outline;
	}

}