package com.x.processplatform.core.entity.content;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;
import com.x.processplatform.core.entity.PersistenceProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.*;
import org.apache.openjpa.persistence.jdbc.Index;

import javax.persistence.*;
import javax.persistence.OrderColumn;
import java.util.List;

/**
 * @author sword
 */
@Schema(name = "Handover", description = "권한 인계.")
@Entity
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Content.Handover.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Handover.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Handover extends SliceJpaObject {

	private static final long serialVersionUID = 5380401110786681261L;

	private static final String TABLE = PersistenceProperties.Content.Handover.table;

	public static final String TYPE_REPLACE = "교체";
	public static final String TYPE_EMPOWER = "위임";

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

	@Override
	public void onPersist() throws Exception {
	}

	@PostLoad
	public void postLoad() {
		if (null != this.properties) {
			this.applicationList = this.getProperties().getApplicationList();
			this.processList = this.getProperties().getProcessList();
			this.jobList = this.getProperties().getJobList();
		}
	}

	public Handover() {
		// nothing
	}

	public HandoverProperties getProperties() {
		if (null == this.properties) {
			this.properties = new HandoverProperties();
		}
		return this.properties;
	}

	public void setProperties(HandoverProperties properties) {
		this.properties = properties;
	}

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("제목.")
	@Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String title;

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("생성 사용자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + creator_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String creator;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("사용자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String targetPerson_FIELDNAME = "targetPerson";
	@FieldDescribe("대상 사용자.")
	@Column(length = length_255B, name = ColumnNamePrefix + targetPerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetPerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetPerson;

	public static final String targetIdentity_FIELDNAME = "targetIdentity";
	@FieldDescribe("대상 사용자 신분.")
	@Column(length = length_255B, name = ColumnNamePrefix + targetIdentity_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + targetIdentity_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetIdentity;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("권한 인계 유형: 교체|위임(기본값).")
	@Column(length = length_64B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String type;

	public static final String scheme_FIELDNAME = "scheme";
	@FieldDescribe("권한 인계 방안: all|권한이 있는 모든 문서(기본값), application|지정 앱 내 권한이 있는 문서, process|지정 프로세스 내 권한이 있는 문서, job|지정된 권한이 있는 문서.")
	@Column(length = length_64B, name = ColumnNamePrefix + scheme_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String scheme;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("상태: wait: 실행 대기|processing: 실행 중|processed: 실행 완료|cancel: 취소됨.")
	@Column(length = length_64B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public static final String handoverJobList_FIELDNAME = "handoverJobList";
	@FieldDescribe("인계된 문서 목록.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ handoverJobList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + handoverJobList_FIELDNAME
			+ JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + handoverJobList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + handoverJobList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> handoverJobList;

	public static final String properties_FIELDNAME = "properties";
	@FieldDescribe("속성 객체 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + properties_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private HandoverProperties properties;

	@FieldDescribe("앱 목록.")
	@Transient
	private List<String> applicationList;

	@FieldDescribe("프로세스 목록.")
	@Transient
	private List<String> processList;

	@FieldDescribe("업무 문서 목록.")
	@Transient
	private List<String> jobList;

	public List<String> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
		this.getProperties().setApplicationList(applicationList);
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
		this.getProperties().setProcessList(processList);
	}

	public List<String> getJobList() {
		return jobList;
	}

	public void setJobList(List<String> jobList) {
		this.jobList = jobList;
		this.getProperties().setJobList(jobList);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getTargetIdentity() {
		return targetIdentity;
	}

	public void setTargetIdentity(String targetIdentity) {
		this.targetIdentity = targetIdentity;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTargetPerson() {
		return targetPerson;
	}

	public void setTargetPerson(String targetPerson) {
		this.targetPerson = targetPerson;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public List<String> getHandoverJobList() {
		return handoverJobList;
	}

	public void setHandoverJobList(List<String> handoverJobList) {
		this.handoverJobList = handoverJobList;
	}
}
