package com.x.cms.assemble.control.jaxrs.permission.element;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.organization.OrganizationDefinition;

public class PermissionInfo {

	@FieldDescribe( "권한 유형: 독자|열람|작성자|관리" )
	private String permission =  PermissionName.READER ;

	@FieldDescribe( "사용 대상 분류: 모든 사용자|조직|사용자|그룹" )
	private String permissionObjectType = "";

	@FieldDescribe( "사용 대상 코드: 모든 사용자|조직 코드|사용자 UID|그룹 코드" )
	private String permissionObjectCode = "";

	@FieldDescribe( "사용 대상 이름: 모든 사용자|조직 이름|사용자 이름|그룹 이름" )
	private String permissionObjectName = "";

	public PermissionInfo() {}

	public PermissionInfo( String permission, String permissionObjectType, String permissionObjectCode, String permissionObjectName) {
		super();
		this.permission = permission;
		this.permissionObjectType = permissionObjectType;
		this.permissionObjectCode = permissionObjectCode;
		this.permissionObjectName = permissionObjectName;
		if(OrganizationDefinition.isPersonDistinguishedName(permissionObjectName)){
			this.permissionObjectType = "사용자";
		}else if(OrganizationDefinition.isUnitDistinguishedName(permissionObjectName)){
			this.permissionObjectType = "조직";
		}else if(OrganizationDefinition.isGroupDistinguishedName(permissionObjectName)){
			this.permissionObjectType = "그룹";
		}
	}

	public String getPermission() {
		return permission;
	}

	public String getPermissionObjectType() {
		return permissionObjectType;
	}

	public String getPermissionObjectCode() {
		return permissionObjectCode;
	}

	public String getPermissionObjectName() {
		return permissionObjectName;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public void setPermissionObjectType(String permissionObjectType) {
		this.permissionObjectType = permissionObjectType;
	}

	public void setPermissionObjectCode(String permissionObjectCode) {
		this.permissionObjectCode = permissionObjectCode;
	}

	public void setPermissionObjectName(String permissionObjectName) {
		this.permissionObjectName = permissionObjectName;
	}
}
