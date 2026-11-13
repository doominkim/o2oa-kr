package com.x.organization.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PersonExtend", description = "조직 사용자 확장.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.PersonExtend.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.PersonExtend.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class PersonExtend extends SliceJpaObject {

	private static final long serialVersionUID = -6552971639462131523L;

	private static final String TABLE = PersistenceProperties.PersonExtend.TABLE;

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
		// nothing
	}

	public PersonExtendProperties getProperties() {
		if (null == this.properties) {
			this.properties = new PersonExtendProperties();
		}
		return this.properties;
	}

	public void setProperties(PersonExtendProperties properties) {
		this.properties = properties;
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.extend = this.getProperties().getExtend();
		}
	}

	public static final String EXTEND_FIELDNAME = "extend";
	@FieldDescribe("저장 데이터.")
	@Transient
	private JsonElement extend;

	public JsonElement getExtend() {
		return this.extend;
	}

	public void setExtend(JsonElement extend) {
		this.extend = extend;
		this.getProperties().setExtend(extend);
	}

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("개인 확장 소유 개인, 비워 둘 수 없음.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Person.class) })
	private String person;

	public static final String TYPE_FIELDNAME = "type";
	@FieldDescribe("유형.")
	@Column(length = length_255B, name = ColumnNamePrefix + TYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TYPE_FIELDNAME)
	// 같은 사용자는 한 유형의 데이터만 가짐, 중복 불가
	@CitationNotExist(fields = {
			TYPE_FIELDNAME }, type = PersonExtend.class, equals = @Equal(property = PERSON_FIELDNAME, field = PERSON_FIELDNAME))
	private String type;

	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private PersonExtendProperties properties;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}