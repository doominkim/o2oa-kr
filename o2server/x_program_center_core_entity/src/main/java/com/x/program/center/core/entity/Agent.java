package com.x.program.center.core.entity;

import java.util.Date;

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

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Agent", description = "서비스 관리 에이전트.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Agent.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Agent.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Agent extends SliceJpaObject {

	private static final long serialVersionUID = 4614564724280586452L;
	private static final String TABLE = PersistenceProperties.Agent.TABLE;

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

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("이름.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("별칭.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 확인 */
	@CitationNotExist(fields = { "name", "id", "alias" }, type = Agent.class))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String validated_FIELDNAME = "validated";
	@FieldDescribe("코드 형식 정상 여부.")
	@Column(name = ColumnNamePrefix + validated_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean validated;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("사용 여부.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public static final String text_FIELDNAME = "text";
	@FieldDescribe("스크립트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = length_10M, name = ColumnNamePrefix + text_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String text;

	public static final String cron_FIELDNAME = "cron";
	@FieldDescribe("예약 작업 corn 표현식.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + cron_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + cron_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String cron;

	public static final String lastStartTime_FIELDNAME = "lastStartTime";
	@FieldDescribe("최근 시작 시간.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastStartTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastStartTime_FIELDNAME)
	private Date lastStartTime;

	public static final String lastEndTime_FIELDNAME = "lastEndTime";
	@FieldDescribe("최근 종료 시간.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + lastEndTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastEndTime_FIELDNAME)
	private Date lastEndTime;

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

	public Boolean getValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}

	public Date getLastStartTime() {
		return lastStartTime;
	}

	public void setLastStartTime(Date lastStartTime) {
		this.lastStartTime = lastStartTime;
	}

	public Date getLastEndTime() {
		return lastEndTime;
	}

	public void setLastEndTime(Date lastEndTime) {
		this.lastEndTime = lastEndTime;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

//	public Date getAppointmentTime() {
//		return appointmentTime;
//	}
//
//	public void setAppointmentTime(Date appointmentTime) {
//		this.appointmentTime = appointmentTime;
//	}

}
