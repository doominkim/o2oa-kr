package com.x.cms.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.cms.core.entity.PersistenceProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;

/**
 * @author sword
 */
@Schema(name = "FormVersion", description = "콘텐츠 관리 양식 이력 버전.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.log, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Element.FormVersion.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Element.FormVersion.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class FormVersion extends SliceJpaObject {

	private static final long serialVersionUID = 7998098495556641050L;
	private static final String TABLE = PersistenceProperties.Element.FormVersion.table;

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
	public void onPersist() throws Exception {
		// nothing
	}

	/* 실행 메서드 업데이트 */

	public FormVersion() {

	}

	public FormVersion(String form, JsonElement jsonElement, String person) {
		this.form = form;
		this.data = XGsonBuilder.toJson(jsonElement);
		this.person = person;
	}

	public static final String form_FIELDNAME = "form";
	@FieldDescribe("소속 양식.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + form_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + form_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String form;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("작업자.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ person_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String person;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("텍스트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public String getForm() {
		return form;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
}
