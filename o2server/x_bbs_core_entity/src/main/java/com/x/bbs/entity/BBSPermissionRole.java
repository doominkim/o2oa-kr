package com.x.bbs.entity;

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

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 포럼 권한-역할 바인딩 관계 테이블
 * 
 * @author LIYI
 */
@Schema(name = "BBSPermissionRole", description = "포럼 권한-역할 바인딩 관계.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSPermissionRole.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSPermissionRole.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSPermissionRole extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSPermissionRole.table;

	public BBSPermissionRole() {
		super();
	}

	public BBSPermissionRole(String forumId, String forumName, String sectionId, String sectionName,
			String mainSectionId, String mainSectionName, String permissionType, String permissionName,
			String permissionCode, String roleId, String roleName, String roleCode, String description,
			Integer orderNumber) {
		super();
		this.forumId = forumId;
		this.forumName = forumName;
		this.sectionId = sectionId;
		this.sectionName = sectionName;
		this.mainSectionId = mainSectionId;
		this.mainSectionName = mainSectionName;
		this.permissionType = permissionType;
		this.permissionName = permissionName;
		this.permissionCode = permissionCode;
		this.roleId = roleId;
		this.roleName = roleName;
		this.roleCode = roleCode;
		this.description = description;
		this.orderNumber = orderNumber;
	}

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

	public void onPersist() throws Exception {
	}
	/*
	 * =============================================================================
	 * ===== 위는 JpaObject 기본 필드
	 * =============================================================================
	 * =====
	 */

	/*
	 * =============================================================================
	 * ===== 아래는 업무별 데이터 테이블 필드 요구사항
	 * =============================================================================
	 * =====
	 */
	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("포럼 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String forumName_FIELDNAME = "forumName";
	@FieldDescribe("포럼 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + forumName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumName = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionId = "";

	public static final String sectionName_FIELDNAME = "sectionName";
	@FieldDescribe("게시판 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionName = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("상위 게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionId = "";

	public static final String mainSectionName_FIELDNAME = "mainSectionName";
	@FieldDescribe("상위 게시판 이름")
	@Column(length = JpaObject.length_128B, name = ColumnNamePrefix + mainSectionName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionName = "";

	public static final String permissionType_FIELDNAME = "permissionType";
	@FieldDescribe("권한 유형: 포럼 권한|섹션 권한|기타 권한")
	@Column(length = JpaObject.length_32B, name = ColumnNamePrefix + permissionType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionType = "기타 권한";

	public static final String permissionName_FIELDNAME = "permissionName";
	@FieldDescribe("권한 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionName = "";

	public static final String permissionCode_FIELDNAME = "permissionCode";
	@FieldDescribe("권한 코드")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + permissionCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + permissionCode_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String permissionCode = "";

	public static final String roleId_FIELDNAME = "roleId";
	@FieldDescribe("역할 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + roleId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleId = "";

	public static final String roleName_FIELDNAME = "roleName";
	@FieldDescribe("역할 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleName = "";

	public static final String roleCode_FIELDNAME = "roleCode";
	@FieldDescribe("역할 코드")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleCode_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleCode = "";

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String description = "";

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public String getForumName() {
		return forumName;
	}

	public void setForumName(String forumName) {
		this.forumName = forumName;
	}

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

	public String getMainSectionName() {
		return mainSectionName;
	}

	public void setMainSectionName(String mainSectionName) {
		this.mainSectionName = mainSectionName;
	}

	public String getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String permissionType) {
		this.permissionType = permissionType;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getPermissionCode() {
		return permissionCode;
	}

	public void setPermissionCode(String permissionCode) {
		this.permissionCode = permissionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}
}