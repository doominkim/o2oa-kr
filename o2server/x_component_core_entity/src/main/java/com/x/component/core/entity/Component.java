package com.x.component.core.entity;

import java.util.ArrayList;
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

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Component", description = "컴포넌트 내용.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Component.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Component.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Component extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;

	private static final String TABLE = PersistenceProperties.Component.table;

	public static final String TYPE_SYSTEM = "system";

	public static final String TYPE_CUSTOM = "custom";

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

	/* 실행 메서드 업데이트 */

	public static final String NAME_FIELDNAME = "name";
	@Flag
	@FieldDescribe("이름, 중복 불가.")
	@Schema(description = "이름, 중복 불가.")
	@Column(length = length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + NAME_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists = {
			/* 이름 중복 검증 */
			@CitationNotExist(fields = "name", type = Component.class) })
	private String name;

	public static final String TITLE_FIELDNAME = "title";
	@FieldDescribe("제목.")
	@Schema(description = "제목.")
	@Column(length = length_255B, name = ColumnNamePrefix + TITLE_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String title;

	public static final String VISIBLE_FIELDNAME = "visible";
	@FieldDescribe("표시되는 앱 여부.")
	@Schema(description = "표시되는 앱 여부.")
	@Column(name = ColumnNamePrefix + VISIBLE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean visible;

	public static final String ORDERNUMBER_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 오름차순, 비어 있으면 마지막")
	@Schema(description = "정렬 번호, 오름차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + ORDERNUMBER_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + ORDERNUMBER_FIELDNAME)
	private Integer orderNumber;

	public static final String PATH_FIELDNAME = "path";
	@FieldDescribe("앱 경로.")
	@Schema(description = "앱 경로.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + PATH_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String path;

	public static final String ICONPATH_FIELDNAME = "iconPath";
	@FieldDescribe("아이콘 위치.")
	@Schema(description = "아이콘 위치.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + ICONPATH_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String iconPath;

	public static final String ALLOWLIST_FIELDNAME = "allowList";
	@FieldDescribe("접근 가능 사용자.")
	@Schema(description = "접근 가능 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ ALLOWLIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + ALLOWLIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + ALLOWLIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + ALLOWLIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> allowList = new ArrayList<>();

	public static final String DENYLIST_FIELDNAME = "denyList";
	@FieldDescribe("접근 거부 사용자.")
	@Schema(description = "접근 거부 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ DENYLIST_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + DENYLIST_FIELDNAME
					+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + DENYLIST_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + DENYLIST_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> denyList = new ArrayList<>();

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("유형:system|custom")
	@Schema(description = "유형:system|custom")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getAllowList() {
		return allowList;
	}

	public void setAllowList(List<String> allowList) {
		this.allowList = allowList;
	}

	public List<String> getDenyList() {
		return denyList;
	}

	public void setDenyList(List<String> denyList) {
		this.denyList = denyList;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getIconPath() {
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

}