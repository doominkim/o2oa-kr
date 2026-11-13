package com.x.portal.core.entity;

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

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "Page", description = "포털 페이지.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Page.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Page.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Page extends SliceJpaObject {

	private static final long serialVersionUID = 9009603537597089732L;

	private static final String TABLE = PersistenceProperties.Page.table;

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

	public Page() {
		this.properties = new PageProperties();
	}

	public PageProperties getProperties() {
		if (null == this.properties) {
			this.properties = new PageProperties();
		}
		return this.properties;
	}

	public void setProperties(PageProperties properties) {
		this.properties = properties;
	}

	/* flag 플래그 비트 */
	/* Entity 기본 필드 끝 */

	public String getDataOrMobileData() {
		if (StringUtils.isNotEmpty(this.getData())) {
			return this.getData();
		} else if (BooleanUtils.isTrue(this.getHasMobile())) {
			return this.getMobileData();
		}
		return this.getData();
	}

	public String getMobileDataOrData() {
		if (BooleanUtils.isTrue(this.getHasMobile())) {
			return this.getMobileData();
		} else {
			return this.getData();
		}
	}

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 동일 Portal 내 이름 중복 불가 확인 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Page.class, equals = @Equal(property = "portal", field = "portal")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@RestrictFlag
	@FieldDescribe("별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 확인 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = Page.class, equals = @Equal(property = "portal", field = "portal")))
	private String alias;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("분류")
	@Column(length = length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String category;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String portal_FIELDNAME = "portal";
	@FieldDescribe("소속 Portal.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + portal_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + portal_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = @CitationExist(type = Portal.class, fields = JpaObject.id_FIELDNAME))
	@IdReference(Portal.class)
	private String portal;

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

	public static final String hasMobile_FIELDNAME = "hasMobile";
	@FieldDescribe("모바일 내용 존재 여부.")
	@Column(name = ColumnNamePrefix + hasMobile_FIELDNAME)
	private Boolean hasMobile;

	public static final String properties_FIELDNAME = "properties";
	// @Basic(fetch = FetchType.EAGER)
	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private PageProperties properties;

	// public static String[] FLA GS = new String[] { JpaObject.id_FIELDNAME };
	//
	// public static String[] RESTRICTFLA GS = new String[] { name_FIELDNAME,
	// alias_FIELDNAME };

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPortal() {
		return portal;
	}

	public void setPortal(String portal) {
		this.portal = portal;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getMobileData() {
		return mobileData;
	}

	public void setMobileData(String mobileData) {
		this.mobileData = mobileData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getHasMobile() {
		return hasMobile;
	}

	public void setHasMobile(Boolean hasMobile) {
		this.hasMobile = hasMobile;
	}

	public String getCategory() {
		return Objects.toString(this.category, "");
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
