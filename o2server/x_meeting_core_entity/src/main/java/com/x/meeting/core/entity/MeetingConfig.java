package com.x.meeting.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;

/**
 * 회의 시스템 설정
 * @author sword
 */
@Schema(name = "MeetingConfig", description = "회의 시스템 설정.")
@ContainerEntity(dumpSize = 100, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.MeetingConfig.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.MeetingConfig.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class MeetingConfig extends SliceJpaObject {

	private static final long serialVersionUID = 3125621703902377659L;
	private static final String TABLE = PersistenceProperties.MeetingConfig.table;

	public static final String DEFINITION_MEETING_CONFIG = "meetingConfig";

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
		if (this.properties == null) {
			this.properties = new MeetingConfigProperties();
		}
	}

	public MeetingConfig() {
		this.properties = new MeetingConfigProperties();
	}

	/* 실행 메서드 업데이트 */

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("설정 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME, unique = true)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("속성 객체 저장 필드(json 객체).")
	@Persistent(fetch = FetchType.EAGER)
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private MeetingConfigProperties properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MeetingConfigProperties getProperties() {
		if (null == this.properties) {
			this.properties = new MeetingConfigProperties();
		}
		return this.properties;
	}

	public void setProperties(MeetingConfigProperties properties) {
		this.properties = properties;
	}

}
