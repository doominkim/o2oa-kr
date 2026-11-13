package com.x.portal.core.entity;

import java.util.Date;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.IdReference;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "File", description = "포털 파일.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.File.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.File.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class File extends SliceJpaObject {

	private static final long serialVersionUID = -6850325164542850879L;

	private static final String TABLE = PersistenceProperties.File.table;

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
		// this.editor = StringUtils.trimToEmpty(this.editor);
	}

	/* flag 플래그 비트 */
	/* Entity 기본 필드 끝 */

	public static final String name_FIELDNAME = "name";
	@RestrictFlag
	@FieldDescribe("파일 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = File.class, equals = @Equal(property = "portal", field = "portal")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("파일 별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	/* 별칭을 설정하면 전역에서 고유해야 함 */
	@CitationNotExist(fields = { "id", "alias" }, type = File.class))
	private String alias;

	public static final String shortUrlCode_FIELDNAME = "shortUrlCode";
	@Flag
	@FieldDescribe("파일 단축 url 코드.")
	@Column(length = length_id, name = ColumnNamePrefix + shortUrlCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + shortUrlCode_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationNotExists =
	@CitationNotExist(fields = { "id", "shortUrlCode" }, type = File.class))
	private String shortUrlCode;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String portal_FIELDNAME = "portal";
	@FieldDescribe("파일 소속 포털.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + portal_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + portal_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Portal.class) })
	@IdReference(Portal.class)
	private String portal;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("마지막 편집자.")
	@Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
			+ lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("마지막 편집 시간.")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("인코딩된 텍스트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String fileName_FIELDNAME = "fileName";
	@FieldDescribe("파일 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + fileName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String fileName;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("파일 크기.")
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPortal() {
		return portal;
	}

	public void setPortal(String portal) {
		this.portal = portal;
	}

	public Long getLength() {
		return length;
	}

	public void setLength(Long length) {
		this.length = length;
	}

	public String getShortUrlCode() {
		return shortUrlCode;
	}

	public void setShortUrlCode(String shortUrlCode) {
		this.shortUrlCode = shortUrlCode;
	}
}
