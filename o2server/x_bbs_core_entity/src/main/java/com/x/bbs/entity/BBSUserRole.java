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
 * 포럼 역할 사용자 정보 테이블
 * 
 * @author LIYI
 */
@Schema(name = "BBSUserRole", description = "포럼 역할 사용자 정보.")
@ContainerEntity(dumpSize = 200, type = ContainerEntity.Type.element, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.BBSUserRole.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.BBSUserRole.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BBSUserRole extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.BBSUserRole.table;

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
	 * =========================================================================
	 * ========= 위는 JpaObject 기본 필드
	 * =========================================================================
	 * =========
	 */

	/*
	 * =========================================================================
	 * ========= 아래는 업무별 데이터 테이블 필드 요구사항
	 * =========================================================================
	 * =========
	 */
	public static final String forumId_FIELDNAME = "forumId";
	@FieldDescribe("포럼 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + forumId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + forumId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String forumId = "";

	public static final String sectionId_FIELDNAME = "sectionId";
	@FieldDescribe("게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + sectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + sectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String sectionId = "";

	public static final String mainSectionId_FIELDNAME = "mainSectionId";
	@FieldDescribe("상위 게시판 ID")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + mainSectionId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + mainSectionId_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String mainSectionId = "";

	public static final String topUnitName_FIELDNAME = "topUnitName";
	@FieldDescribe("대상이 속한 최상위 조직 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + topUnitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + topUnitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String topUnitName = null;

	public static final String unitName_FIELDNAME = "unitName";
	@FieldDescribe("대상이 속한 조직 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + unitName_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + unitName_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String unitName = null;

	public static final String objectName_FIELDNAME = "objectName";
	@FieldDescribe("대상 이름: 이름, 조직, 그룹 이름 등")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + objectName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectName = null;

	public static final String objectType_FIELDNAME = "objectType";
	@FieldDescribe("대상 유형: 사용자|조직|그룹")
	@Column(length = JpaObject.length_16B, name = ColumnNamePrefix + objectType_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + objectType_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String objectType = null;

	public static final String uniqueId_FIELDNAME = "uniqueId";
	@FieldDescribe("대상 고유 식별자")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + uniqueId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + uniqueId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String uniqueId = null;

	public static final String roleId_FIELDNAME = "roleId";
	@FieldDescribe("역할 Id")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleId_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleId_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleId = null;

	public static final String roleCode_FIELDNAME = "roleCode";
	@FieldDescribe("역할 코드")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleCode_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + roleCode_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleCode = null;

	public static final String roleName_FIELDNAME = "roleName";
	@FieldDescribe("역할 이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + roleName_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String roleName = null;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer orderNumber = 1;

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getTopUnitName() {
		return topUnitName;
	}

	public void setTopUnitName(String topUnitName) {
		this.topUnitName = topUnitName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
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

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getForumId() {
		return forumId;
	}

	public String getSectionId() {
		return sectionId;
	}

	public String getMainSectionId() {
		return mainSectionId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public void setMainSectionId(String mainSectionId) {
		this.mainSectionId = mainSectionId;
	}

}