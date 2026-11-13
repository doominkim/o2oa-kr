package com.x.file.core.entity.open;

import com.x.base.core.project.organization.OrganizationDefinition;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * File 생성 방식은 두 가지:<br/>
 * 1. servlet으로 업로드<br/>
 * 2. Attachment를 통해 덤프<br/>
 *
 */
@Schema(name = "File", description = "클라우드 파일 저장 파일.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Open.File.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Open.File.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.file)
public class File extends StorageObject {

	private static final long serialVersionUID = 9009603537597089732L;

	private static final String TABLE = PersistenceProperties.Open.File.table;

	private static final String OPEN = "open";

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

	public File() {

	}

	public File(String storage, String name, String person, ReferenceType referenceType, String reference)
			throws Exception {
		if (StringUtils.isEmpty(storage)) {
			throw new Exception("storage can not be empty.");
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		if (null == referenceType) {
			throw new Exception("referenceType can not be null.");
		}
		if (StringUtils.isEmpty(reference)) {
			throw new Exception("reference can not be empty.");
		}
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = OrganizationDefinition.unique(person);
		this.referenceType = referenceType;
		this.reference = reference;
		if (null == this.extension) {
			throw new Exception("extension can not be null.");
		}
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public static final String NAME_FIELDNAME = "name";
	@FieldDescribe("이름.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + NAME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + NAME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String PERSON_FIELDNAME = "person";
	@FieldDescribe("소속 사용자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + PERSON_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + PERSON_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String REFERENCETYPE_FIELDNAME = "referenceType";
	@FieldDescribe("연관 유형.")
	@Enumerated(EnumType.STRING)
	@Column(length = ReferenceType.length, name = ColumnNamePrefix + REFERENCETYPE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + REFERENCETYPE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private ReferenceType referenceType;

	public static final String REFERENCE_FIELDNAME = "reference";
	@FieldDescribe("연관 ID.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + REFERENCE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + REFERENCE_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String reference;

	public static final String EXTENSION_FIELDNAME = "extension";
	@FieldDescribe("확장자, 확장자가 있는 파일만 업로드 허용.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + EXTENSION_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + EXTENSION_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String STORAGE_FIELDNAME = "storage";
	@FieldDescribe("스토리지 이름, 즉 여러 저장 노드의 이름.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + STORAGE_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + STORAGE_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	private String storage;

	public static final String LENGTH_FIELDNAME = "length";
	@FieldDescribe("파일 크기.")
	@Column(name = ColumnNamePrefix + LENGTH_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + LENGTH_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String LASTUPDATETIME_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("최종 수정 시간")
	@Column(name = ColumnNamePrefix + LASTUPDATETIME_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + LASTUPDATETIME_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

//	public static final String LASTNOTEXISTEDTIME_FIELDNAME = "lastNotExistedTime";
//	@FieldDescribe("최종 수정 시간")
//	@Column(name = ColumnNamePrefix + LASTNOTEXISTEDTIME_FIELDNAME)
//	@Index(name = TABLE + IndexNameMiddle + LASTNOTEXISTEDTIME_FIELDNAME)
//	@CheckPersist(allowEmpty = true)
//	private Date lastNotExistedTime;

	public static final String DEEPPATH_FIELDNAME = "deepPath";
	@FieldDescribe("더 깊은 경로 사용 여부.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + DEEPPATH_FIELDNAME)
	private Boolean deepPath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public String getStorage() {
		return storage;
	}

	@Override
	public void setStorage(String storage) {
		this.storage = storage;
	}

	@Override
	public Long getLength() {
		return length;
	}

	@Override
	public void setLength(Long length) {
		this.length = length;
	}

	@Override
	public String path() {
		if (null == this.person) {
			throw new IllegalStateException("person can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new IllegalStateException("id can not be empty.");
		}
		if (StringUtils.isEmpty(reference)) {
			throw new IllegalStateException("reference can not be empty.");
		}
		if (Objects.isNull(this.getCreateTime())) {
			throw new IllegalStateException("createTime can not be empty.");
		}
		if (Objects.isNull(this.referenceType)) {
			throw new IllegalStateException("referenceType can not be empty.");
		}

		String str = this.person;
		str += PATHSEPARATOR;
		str += OPEN;
		str += PATHSEPARATOR;
		str += referenceType.toString();
		str += PATHSEPARATOR;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += reference;
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

//	public Date getLastNotExistedTime() {
//		return lastNotExistedTime;
//	}
//
//	public void setLastNotExistedTime(Date lastNotExistedTime) {
//		this.lastNotExistedTime = lastNotExistedTime;
//	}

}
