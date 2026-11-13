package com.x.file.core.entity.personal;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 휴지통
 * @author sword
 */
@Schema(name = "Recycle", description = "클라우드 파일 개인 휴지통.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Personal.Recycle.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Recycle.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Recycle extends SliceJpaObject {

	private static final long serialVersionUID = 5424940251448214931L;

	private static final String TABLE = PersistenceProperties.Personal.Recycle.table;

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
		/* 확장자가 비어 있으면 null 제거 */
		this.extension = StringUtils.trimToEmpty(extension);
	}

	/* 실행 메서드 업데이트 */

	public Recycle() {

	}

	public Recycle(String person, String name, String fileId, String fileType) throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		Date now = new Date();
		this.setCreateTime(now);
		this.person = person;
		this.name = name;
		this.fileId = fileId;
		this.fileType = fileType;
		this.validTime = DateTools.getDateAfterYearAdjust(new Date(), null, null, 30);
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("소속 사용자.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("삭제한 파일 또는 디렉터리 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	@CitationNotExist(fields = { "name", "id" }, type = Recycle.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String name;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("파일 또는 디렉터리 id.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	@CitationNotExist(fields = { "fileId", "id" }, type = Recycle.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String fileId;

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("삭제한 파일 유형: 파일(attachment)|디렉터리(folder).")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + fileType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String fileType;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("확장자, 확장자가 있는 파일만 업로드 허용.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String extension;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("파일 크기.")
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String validTime_FIELDNAME = "validTime";
	@FieldDescribe("보관 기한")
	@Column(name = ColumnNamePrefix + validTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + validTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date validTime;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
