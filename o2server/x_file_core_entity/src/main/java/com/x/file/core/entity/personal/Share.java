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
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.file.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Share", description = "클라우드 파일 개인 공유 파일.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Personal.Share.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Personal.Share.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Share extends SliceJpaObject {

	private static final long serialVersionUID = -2692331804201640002L;

	private static final String TABLE = PersistenceProperties.Personal.Share.table;

	public static final String SHARE_TYPE_PASSWORD = "password";

	public static final String FILE_TYPE_FOLDER = "folder";
	public static final String FILE_TYPE_ATTACHMENT = "attachment";

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
		this.lastUpdateTime = new Date();
		/* 확장자가 비어 있으면 null 제거 */
		this.extension = StringUtils.trimToEmpty(extension);
		/* 공유 사용자 중복 제거 */
		if (null != this.shareUserList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.shareUserList);
			this.shareUserList = set.asList();
		}
		/* 공유 조직 중복 제거 */
		if (null != this.shareOrgList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.shareOrgList);
			this.shareOrgList = set.asList();
		}
		/* 차단 사용자 중복 제거 */
		if (null != this.shieldUserList) {
			ListOrderedSet<String> set = new ListOrderedSet<>();
			set.addAll(this.shieldUserList);
			this.shieldUserList = set.asList();
		}
	}

	/* 실행 메서드 업데이트 */

	public Share() {

	}

	public Share(String name, String person, String folder, String originFile, Long length, String type)
			throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("name can not be empty.");
		}
		if (StringUtils.isEmpty(person)) {
			throw new Exception("person can not be empty.");
		}
		Date now = new Date();
		this.setCreateTime(now);
		this.lastUpdateTime = now;
		this.name = name;
		this.person = person;
		this.length = length;
	}

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("소속 사용자.")
	@Column(length = length_255B, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("공유한 파일 또는 디렉터리 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 같은 사용자가 같은 이름의 파일을 여러 번 공유할 수 없음 */
	@CitationNotExist(fields = { "name", "id" }, type = Share.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String name;

	public static final String fileId_FIELDNAME = "fileId";
	@FieldDescribe("파일 또는 디렉터리 id.")
	@Column(length = JpaObject.length_64B, name = ColumnNamePrefix + fileId_FIELDNAME)
	@CheckPersist(allowEmpty = false, fileNameString = true, citationNotExists =
	/* 같은 사용자가 같은 파일 또는 디렉터리를 여러 번 공유할 수 없음 */
	@CitationNotExist(fields = { "fileId", "id" }, type = Share.class, equals = {
			@Equal(property = "person", field = "person") }))
	private String fileId;

	public static final String fileType_FIELDNAME = "fileType";
	@FieldDescribe("공유 파일 유형: 파일(attachment)|디렉터리(folder).")
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

	public static final String shareType_FIELDNAME = "shareType";
	@FieldDescribe("공유 유형: 비밀번호 공유(password)|지정 사용자 또는 조직 공유(member).")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + shareType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String shareType;

	public static final String password_FIELDNAME = "password";
	@FieldDescribe("공유 비밀번호.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + password_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + password_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String password;

	public static final String validTime_FIELDNAME = "validTime";
	@FieldDescribe("유효 시간")
	@Column(name = ColumnNamePrefix + validTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + validTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date validTime;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("최종 수정 시간")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public static final String shareUserList_FIELDNAME = "shareUserList";
	@FieldDescribe("공유 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareUserList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareUserList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + shareUserList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareUserList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareUserList;

	public static final String shareOrgList_FIELDNAME = "shareOrgList";
	@FieldDescribe("공유 조직")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareOrgList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareOrgList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + shareOrgList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareOrgList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareOrgList;

	public static final String shareGroupList_FIELDNAME = "shareGroupList";
	@FieldDescribe("공유 그룹")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shareGroupList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shareGroupList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + shareGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shareGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shareGroupList;

	public static final String shieldUserList_FIELDNAME = "shieldUserList";
	@FieldDescribe("차단 사용자")
	@PersistentCollection(fetch = FetchType.EAGER)
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + shieldUserList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + shieldUserList_FIELDNAME + JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + shieldUserList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + shieldUserList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> shieldUserList;

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

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getValidTime() {
		return validTime;
	}

	public void setValidTime(Date validTime) {
		this.validTime = validTime;
	}

	public List<String> getShareUserList() {
		return shareUserList;
	}

	public void setShareUserList(List<String> shareUserList) {
		this.shareUserList = shareUserList;
	}

	public List<String> getShareOrgList() {
		return shareOrgList;
	}

	public void setShareOrgList(List<String> shareOrgList) {
		this.shareOrgList = shareOrgList;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getShareType() {
		return shareType;
	}

	public void setShareType(String shareType) {
		this.shareType = shareType;
	}

	public List<String> getShieldUserList() {
		return shieldUserList;
	}

	public void setShieldUserList(List<String> shieldUserList) {
		this.shieldUserList = shieldUserList;
	}

	public List<String> getShareGroupList() {
		return shareGroupList;
	}

	public void setShareGroupList(List<String> shareGroupList) {
		this.shareGroupList = shareGroupList;
	}
}
