package com.x.organization.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.StringTools;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OauthCode", description = "조직 Oauth 코드.")
@Entity
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.OauthCode.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.OauthCode.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class OauthCode extends SliceJpaObject {

	private static final long serialVersionUID = -7688990884958313153L;

	private static final String TABLE = PersistenceProperties.OauthCode.table;

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

	public OauthCode() {

		this.codeUsed = false;
		this.accessToken = StringTools.uniqueToken();
		this.code = StringTools.uniqueToken();

	}

	/* 실행 메서드 업데이트 */

	/** 기본 내용 끝 */
	public static final String clientId_FIELDNAME = "clientId";
	@FieldDescribe("client_id.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + clientId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + clientId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String clientId;

	public static final String person_FIELDNAME = "person";
	@FieldDescribe("바인딩된 사용자.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + person_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + person_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String person;

	public static final String scope_FIELDNAME = "scope";
	@FieldDescribe("권한 범위.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + scope_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + scope_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String scope;

	public static final String code_FIELDNAME = "code";
	@FieldDescribe("인증 code.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + code_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + code_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String code;

	public static final String accessToken_FIELDNAME = "accessToken";
	@FieldDescribe("accessToken.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + accessToken_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String accessToken;

	public static final String codeUsed_FIELDNAME = "codeUsed";
	@FieldDescribe("code 사용 여부.")
	@CheckPersist(allowEmpty = false)
	@Column(name = ColumnNamePrefix + codeUsed_FIELDNAME)
	private Boolean codeUsed;

	/** flag 플래그 비트 */

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Boolean getCodeUsed() {
		return codeUsed;
	}

	public void setCodeUsed(Boolean codeUsed) {
		this.codeUsed = codeUsed;
	}
	
	

}