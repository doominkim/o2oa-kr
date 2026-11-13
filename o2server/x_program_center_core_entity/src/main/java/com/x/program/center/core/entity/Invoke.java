package com.x.program.center.core.entity;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

/**
 * @author sword
 * 서비스 관리 인터페이스
 */
@Schema(name = "Invoke", description = "서비스 관리 인터페이스.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Invoke.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Invoke.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Invoke extends SliceJpaObject {

	private static final long serialVersionUID = 8877822163007579542L;
	private static final String TABLE = PersistenceProperties.Invoke.TABLE;
	public static final String CATEGORY_DEFAULT = "미분류";

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
		this.category = StringUtils.isBlank(this.category) ? CATEGORY_DEFAULT : StringUtils.trimToEmpty(this.category);
	}

	@PostLoad
	public void postLoad() {
		this.category = StringUtils.isBlank(this.category) ? CATEGORY_DEFAULT : this.category;
		this.enableAnonymous = this.enableAnonymous != null && this.enableAnonymous;
	}

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("이름.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = true)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	private String name;

	public static final String enableToken_FIELDNAME = "enableToken";
	@FieldDescribe("token 인증 사용 여부.")
	@Column(name = ColumnNamePrefix + enableToken_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean enableToken = true;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("별칭.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = true, citationNotExists =
	// 같은 앱 안에서 이름 중복 불가 확인
	@CitationNotExist(fields = { "name", "id", "alias" }, type = Invoke.class))
	private String alias;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("분류.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String category;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String validated_FIELDNAME = "validated";
	@FieldDescribe("코드 형식 정상 여부.")
	@Column(name = ColumnNamePrefix + validated_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean validated;

	public static final String enable_FIELDNAME = "enable";
	@FieldDescribe("사용 여부.")
	@Column(name = ColumnNamePrefix + enable_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Boolean enable;

	public static final String enableAnonymous_FIELDNAME = "enableAnonymous";
	@FieldDescribe("익명 사용자의 인터페이스 접근 허용 여부.")
	@Column(name = ColumnNamePrefix + enableAnonymous_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Boolean enableAnonymous = false;

	public static final String text_FIELDNAME = "text";
	@FieldDescribe("스크립트 내용.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + text_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String text;

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

	public static final String remoteAddrRegex_FIELDNAME = "remoteAddrRegex";
	@FieldDescribe("접근 주소 표현식.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + remoteAddrRegex_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	@Index(name = TABLE + IndexNameMiddle + remoteAddrRegex_FIELDNAME)
	private String remoteAddrRegex;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("사용자 정의 필드.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String executorList_FIELDNAME = "executorList";
	@FieldDescribe("실행 권한")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ executorList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle + executorList_FIELDNAME
			+ JoinIndexNameSuffix))
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + executorList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + executorList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> executorList;

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

	public String getRemoteAddrRegex() {
		return remoteAddrRegex;
	}

	public void setRemoteAddrRegex(String remoteAddrRegex) {
		this.remoteAddrRegex = remoteAddrRegex;
	}

	public Boolean getEnableToken() {
		return enableToken;
	}

	public void setEnableToken(Boolean enableToken) {
		this.enableToken = enableToken;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<String> getExecutorList() {
		return executorList;
	}

	public void setExecutorList(List<String> executorList) {
		this.executorList = executorList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Boolean getEnableAnonymous() {
		return enableAnonymous;
	}

	public void setEnableAnonymous(Boolean enableAnonymous) {
		this.enableAnonymous = enableAnonymous;
	}
}
