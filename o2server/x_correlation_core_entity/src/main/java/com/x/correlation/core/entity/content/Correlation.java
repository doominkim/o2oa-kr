package com.x.correlation.core.entity.content;

import static com.x.base.core.entity.StorageType.processPlatform;

import java.util.Date;

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

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.correlation.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Correlation", description = "관련 콘텐츠.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Content.Correlation.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Content.Correlation.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = processPlatform)
public class Correlation extends SliceJpaObject {

	private static final long serialVersionUID = -6564254194819206271L;
	private static final String TABLE = PersistenceProperties.Content.Correlation.TABLE;

	public static final String TYPE_CMS = "cms";
	public static final String TYPE_PROCESSPLATFORM = "processPlatform";

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

	@PostLoad
	public void postLoad() {
		this.targetTitle = this.getProperties().getTargetTitle();
		this.targetCategory = this.getProperties().getTargetCategory();
		this.targetStartTime = this.getProperties().getTargetStartTime();
		this.targetCreatorPerson = this.getProperties().getTargetCreatorPerson();
		this.view = this.getProperties().getView();
	}

	public Correlation() {
		// nothing
	}

	@Transient
	@FieldDescribe("연관 콘텐츠 제목.")
	private String targetTitle;

	@Transient
	@FieldDescribe("연관 콘텐츠 분류, processPlatform: 프로세스 이름, cms: 앱 이름.")
	private String targetCategory;

	@Transient
	@FieldDescribe("연관 콘텐츠 생성 시간")
	private Date targetStartTime;

	@Transient
	@FieldDescribe("연관 콘텐츠 생성자")
	private String targetCreatorPerson;

	@Transient
	@FieldDescribe("출처 뷰")
	private String view;

	public String getView() {
		return this.view;
	}

	public void setView(String view) {
		this.view = view;
		this.getProperties().setView(view);
	}

	public String getTargetTitle() {
		return this.targetTitle;
	}

	public void setTargetTitle(String targetTitle) {
		this.targetTitle = targetTitle;
		this.getProperties().setTargetTitle(targetTitle);
	}

	public String getTargetCategory() {
		return this.targetCategory;
	}

	public void setTargetCategory(String targetCategory) {
		this.targetCategory = targetCategory;
		this.getProperties().setTargetCategory(targetCategory);
	}

	public Date getTargetStartTime() {
		return this.targetStartTime;
	}

	public void setTargetStartTime(Date targetStartTime) {
		this.targetStartTime = targetStartTime;
		this.getProperties().setTargetStartTime(targetStartTime);
	}

	public String getTargetCreatorPerson() {
		return this.targetCreatorPerson;
	}

	public void setTargetCreatorPerson(String targetCreatorPerson) {
		this.targetCreatorPerson = targetCreatorPerson;
		this.getProperties().setTargetCreatorPerson(targetCreatorPerson);
	}

	public CorrelationProperties getProperties() {
		if (null == this.properties) {
			this.properties = new CorrelationProperties();
		}
		return this.properties;
	}

	public void setProperties(CorrelationProperties properties) {
		this.properties = properties;
	}

	public static final String FROMTYPE_FIELDNAME = "fromType";
	@FieldDescribe("원본 유형 cms, processPlatform.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + FROMTYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMTYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromType;

	public static final String TARGETTYPE_FIELDNAME = "targetType";
	@FieldDescribe("연관 대상 유형, cms.processPlatform.")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + TARGETTYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TARGETTYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetType;

	public static final String FROMBUNDLE_FIELDNAME = "fromBundle";
	@FieldDescribe("원본 식별자.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + FROMBUNDLE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + FROMBUNDLE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fromBundle;

	public static final String TARGETBUNDLE_FIELDNAME = "targetBundle";
	@FieldDescribe("연관 대상 식별자.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + TARGETBUNDLE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + TARGETBUNDLE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String targetBundle;

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("파일 소유자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String SITE_FIELDNAME = "site";
	@FieldDescribe("연관 콘텐츠 박스 분류.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + SITE_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	@Index(name = TABLE + IndexNameMiddle + SITE_FIELDNAME)
	private String site;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 오름차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String PROPERTIES_FIELDNAME = "properties";
	@Schema(description = "속성 저장 필드.")
	@FieldDescribe("속성 저장 필드.")
	@Persistent
	@Strategy(JsonPropertiesValueHandler)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + PROPERTIES_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private CorrelationProperties properties;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getFromType() {
		return fromType;
	}

	public void setFromType(String fromType) {
		this.fromType = fromType;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getFromBundle() {
		return fromBundle;
	}

	public void setFromBundle(String fromBundle) {
		this.fromBundle = fromBundle;
	}

	public String getTargetBundle() {
		return targetBundle;
	}

	public void setTargetBundle(String targetBundle) {
		this.targetBundle = targetBundle;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}
