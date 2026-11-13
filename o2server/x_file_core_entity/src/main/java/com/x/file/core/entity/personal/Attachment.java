package com.x.file.core.entity.personal;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.StorageObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.DateTools;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Attachment", description = "클라우드 파일 개인 파일.")
@ContainerEntity(dumpSize = 5, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Personal.Attachment.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Attachment.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Storage(type = StorageType.file)
public class Attachment extends StorageObject {

	private static final long serialVersionUID = 7706126788445253456L;

	private static final String TABLE = PersistenceProperties.Personal.Attachment.table;

	private static final String PERSONAL = "personal";

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
		this.lastUpdateTime = new Date();
		/* 최상위이면 디렉터리를 비움 */
		this.folder = StringUtils.trimToEmpty(this.folder);
		/* 확장자가 비어 있으면 null 제거 */
		this.extension = StringUtils.trimToEmpty(extension);
		/* 공유 사용자 중복 제거 */
		if (null != this.shareList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.shareList);
			this.shareList = set.asList();
		}
		/* 편집 가능 사용자는 반드시 공유 사용자에 포함되어야 함 */
		if (null != this.editorList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.editorList);
			this.editorList = set.asList();
		}
	}

	/* 실행 메서드 업데이트 */

	public Attachment() {

	}

	public Attachment(String storage, String name, String person, String folder) throws Exception {
		if (StringUtils.isEmpty(storage)) {
			throw new Exception("storage can not be empty.");
		}
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		this.storage = storage;
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.extension = StringUtils.lowerCase(FilenameUtils.getExtension(name));
		this.person = person;
		this.lastUpdatePerson = person;
		this.folder = folder;
		if (null == this.extension) {
			throw new Exception("extension can not be null.");
		}
	}

	@Override
	public String path() throws Exception {
		if (null == this.person) {
			throw new Exception("person can not be null.");
		}
		if (StringUtils.isEmpty(id)) {
			throw new Exception("id can not be empty.");
		}
		String str = this.person;
		str += PATHSEPARATOR;
		str += PERSONAL;
		str += PATHSEPARATOR;
		str += DateTools.compactDate(this.getCreateTime());
		str += PATHSEPARATOR;
		str += this.id;
		str += StringUtils.isEmpty(this.extension) ? "" : ("." + this.extension);
		return str;
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getExtension() {
		return extension;
	}

	@Override
	public void setExtension(String extension) {
		this.extension = extension;
	}

	@Override
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	@Override
	public Boolean getDeepPath() {
		return BooleanUtils.isTrue(this.deepPath);
	}

	@Override
	public void setDeepPath(Boolean deepPath) {
		this.deepPath = deepPath;
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("소속 사용자.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("파일 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 같은 사용자의 같은 디렉터리 내 이름 중복 불가 */
	@CitationNotExist(fields = { "name", "id" }, type = Attachment.class, equals = {
			@Equal(property = "person", field = "person"), @Equal(property = "folder", field = "folder") }))
	private String name;

	public static final String extension_FIELDNAME = "extension";
	@FieldDescribe("확장자, 확장자가 있는 파일만 업로드 허용.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + extension_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true)
	private String extension;

	public static final String storage_FIELDNAME = "storage";
	@FieldDescribe("스토리지 이름, 즉 여러 저장 노드의 이름.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + storage_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + storage_FIELDNAME)
	private String storage;

	public static final String length_FIELDNAME = "length";
	@FieldDescribe("파일 크기.")
	@Index(name = TABLE + IndexNameMiddle + length_FIELDNAME)
	@Column(name = ColumnNamePrefix + length_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Long length;

	public static final String shareList_FIELDNAME = "shareList";
	@FieldDescribe("공유 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + shareList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareList;

	public static final String editorList_FIELDNAME = "editorList";
	@FieldDescribe("편집 가능 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + editorList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + editorList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + editorList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + editorList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> editorList;

	public static final String folder_FIELDNAME = "folder";
	@FieldDescribe("파일 소속 분류.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + folder_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + folder_FIELDNAME)
	/* 상위 디렉터리가 반드시 존재해야 함 */
	@CheckPersist(allowEmpty = true, citationExists = @CitationExist(type = Folder.class, equals = @Equal(property = "person", field = "person")))
	private String folder;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("최종 수정 시간")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String lastUpdatePerson_FIELDNAME = "lastUpdatePerson";
	@FieldDescribe("최종 수정 사용자")
	@Column(length = length_255B, name = ColumnNamePrefix + lastUpdatePerson_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdatePerson_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String lastUpdatePerson;

	public static final String deepPath_FIELDNAME = "deepPath";
	@FieldDescribe("더 깊은 경로 사용 여부.")
	@CheckPersist(allowEmpty = true)
	@Column(name = ColumnNamePrefix + deepPath_FIELDNAME)
	private Boolean deepPath;

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public List<String> getShareList() {
		return shareList;
	}

	public void setShareList(List<String> shareList) {
		this.shareList = shareList;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getLastUpdatePerson() {
		return lastUpdatePerson;
	}

	public void setLastUpdatePerson(String lastUpdatePerson) {
		this.lastUpdatePerson = lastUpdatePerson;
	}

	public List<String> getEditorList() {
		return editorList;
	}

	public void setEditorList(List<String> editorList) {
		this.editorList = editorList;
	}

}