package com.x.general.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;

@Schema(name = "ApplicationDict", description = "프로세스 플랫폼 데이터 사전.")
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.ApplicationDict.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.ApplicationDict.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }),
		@UniqueConstraint(name = PersistenceProperties.ApplicationDict.table + JpaObject.IndexNameMiddle
		+ ApplicationDict.application_FIELDNAME, columnNames = { JpaObject.ColumnNamePrefix + ApplicationDict.application_FIELDNAME,
				JpaObject.ColumnNamePrefix + ApplicationDict.name_FIELDNAME }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ApplicationDict extends SliceJpaObject {

	private static final long serialVersionUID = -8292188532826223591L;
	private static final String TABLE = PersistenceProperties.ApplicationDict.table;
	public static final String PROJECT_PORTAL = "portal";
	public static final String PROJECT_SERVICE = "service";

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
	public void onPersist() {
	}

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("앱.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + application_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String application;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("사전 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@FieldDescribe("별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	@CitationNotExist(fields = { "id", "alias",
			"name" }, type = ApplicationDict.class, equals = @Equal(property = "application", field = "application")))
	private String alias;

	public static final String project_FIELDNAME = "project";
	@FieldDescribe("소속 프로젝트.")
	@Column(length = length_255B, name = ColumnNamePrefix + project_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String project;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getName() {
		return name;
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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
}
