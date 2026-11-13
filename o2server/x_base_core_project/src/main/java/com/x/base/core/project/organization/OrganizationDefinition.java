package com.x.base.core.project.organization;

import com.x.base.core.project.http.EffectivePerson;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.list.UnmodifiableList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;

public class OrganizationDefinition {

	public static final String Manager = "Manager";
	public static final String Manager_description = "시스템 관리자(시스템 역할). 모든 역할의 권한을 가지며 모든 내용을 관리할 수 있습니다.";

	public static final String AttendanceManager = "AttendanceManager";
	public static final String AttendanceManager_description = "근태 관리자(시스템 역할). 근태 관리 관련 설정을 관리하고 데이터 관리 기능을 사용할 수 있습니다.";

	public static final String OrganizationManager = "OrganizationManager";
	public static final String OrganizationManager_description = "조직 정보 관리자(시스템 역할). 조직도, 사용자, 그룹, 역할 관련 설정과 관리 기능을 사용할 수 있습니다.";

	public static final String PersonManager = "PersonManager";
	public static final String PersonManager_description = "사용자 정보 관리자(시스템 역할). 조직 관리의 사용자 관리 기능을 사용할 수 있습니다.";

	public static final String GroupManager = "GroupManager";
	public static final String GroupManager_description = "그룹 정보 관리자(시스템 역할). 조직 관리의 그룹 관리 기능을 사용할 수 있습니다.";

	public static final String UnitManager = "UnitManager";
	public static final String UnitManager_description = "조직 정보 관리자(시스템 역할). 조직 관리의 조직도 관리 기능을 사용할 수 있습니다.";

	public static final String RoleManager = "RoleManager";
	public static final String RoleManager_description = "역할 정보 관리자(시스템 역할). 조직 관리의 역할 관리 기능을 사용할 수 있습니다.";

	public static final String ProcessPlatformManager = "ProcessPlatformManager";
	public static final String ProcessPlatformManager_description = "프로세스 플랫폼 관리자(시스템 역할). 프로세스 플랫폼과 프로세스 설계를 관리하고 프로세스 인스턴스를 조회, 단계 이동, 삭제할 수 있습니다.";

	public static final String ProcessPlatformCreator = "ProcessPlatformCreator";
	public static final String ProcessPlatformCreator_description = "프로세스 설계 작성자(시스템 역할). 프로세스를 설계하고 프로세스 앱을 추가, 설계할 수 있습니다.";

	public static final String MeetingManager = "MeetingManager";
	public static final String MeetingManager_description = "회의 관리자(시스템 역할). 회의 장소, 회의실, 회의 등의 정보와 회의 관리 시스템의 모든 설정을 관리할 수 있습니다.";

	public static final String MeetingViewer = "MeetingViewer";
	public static final String MeetingViewer_description = "회의 관찰자(시스템 역할). 모든 회의 정보를 볼 수 있습니다.";

	public static final String PortalManager = "PortalManager";
	public static final String PortalManager_description = "포털 관리자(시스템 역할). 포털 앱을 설계하고 관리할 수 있습니다.";

	public static final String PortalCreator = "PortalCreator";
	public static final String PortalCreator_description = "포털 작성자(시스템 역할). 포털을 설계하고 추가, 설계할 수 있습니다.";

	public static final String BBSManager = "BBSManager";
	public static final String BBSManager_description = "커뮤니티 관리자(시스템 역할). 포럼 분류와 섹션 생성, 권한 설정, 게시물 관리 등을 할 수 있습니다.";

	public static final String CMSManager = "CMSManager";
	public static final String CMSManager_description = "게시판 시스템 관리자(시스템 역할). 게시판과 분류를 설계하고, 양식과 목록을 설계하며 게시글을 관리할 수 있습니다.";

	public static final String CMSCreator = "CMSCreator";
	public static final String CMSCreator_description = "게시판 작성자(시스템 역할). 게시판을 설계하고 추가, 설계할 수 있습니다.";

	public static final String OKRManager = "OKRManager";
	public static final String OKRManager_description = "실행력 관리자(시스템 역할). 실행력 관리 시스템을 설정하고 업무 내용을 관리할 수 있습니다.";

	public static final String CRMManager = "CRMManager";
	public static final String CRMManager_description = "CRM 관리자(시스템 역할). CRM 시스템 관련 설정과 고객 정보, 영업 기회 등의 정보를 관리할 수 있습니다.";

	public static final String TeamWorkManager = "TeamWorkManager";
	public static final String TeamWorkManager_description = "TeamWork 관리자(시스템 역할). TeamWork 시스템 관련 설정과 프로젝트, 작업 등의 정보를 관리할 수 있습니다.";

	public static final String QueryManager = "QueryManager";
	public static final String QueryManager_description = "데이터 센터 관리자(시스템 역할). 데이터 센터에서 뷰 관리, 통계 관리 등을 할 수 있습니다.";

	public static final String QueryCreator = "QueryCreator";
	public static final String QueryCreator_description = "데이터 센터 작성자(시스템 역할). 데이터 센터에서 뷰 추가, 통계 관리 등을 할 수 있습니다.";

	public static final String MessageManager = "MessageManager";
	public static final String MessageManager_description = "메시지 관리자(시스템 역할). 시스템에서 생성된 메시지를 관리할 수 있습니다.";

	public static final String HotPictureManager = "HotPictureManager";
	public static final String HotPictureManager_description = "핫이슈 이미지 관리 권한(시스템 역할). 시스템에서 생성된 이미지를 관리할 수 있습니다.";

	public static final String SearchPrivilege = "SearchPrivilege";
	public static final String SearchPrivilege_description = "검색 관리자(시스템 역할). 권한과 관계없이 시스템 내용을 검색할 수 있습니다.";

	public static final String FileManager = "FileManager";
	public static final String FileManager_description = "웹하드 관리자(시스템 역할). 웹하드 시스템 관련 설정을 할 수 있습니다.";

	public static final String ServiceManager = "ServiceManager";
	public static final String ServiceManager_description = "서비스 관리자(시스템 역할). 서비스 관리의 인터페이스와 에이전트를 설정할 수 있습니다.";

	public static final String SystemManager = "SystemManager";
	public static final String SystemManager_description = "3권 분리 관리의 시스템 관리자(시스템 역할). 사용자 추가와 시스템 운영 유지보수를 담당합니다.";

	public static final String SecurityManager = "SecurityManager";
	public static final String SecurityManager_description = "3권 분리 관리의 보안 관리자(시스템 역할). 권한 설정과 감사 로그, 사용자 및 시스템 관리자 작업 검토 분석을 담당합니다.";

	public static final String AuditManager = "AuditManager";
	public static final String AuditManager_description = "3권 분리 관리의 보안 감사자(시스템 역할). 시스템 관리자와 보안 관리자의 작업을 감사, 추적합니다.";

	public static final String RoleDefinitionSuffix = "SystemRole";

	public static final Pattern person_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@P$");

	public static final Pattern personAttribute_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@PA$");

	public static final Pattern group_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@G$");

	public static final Pattern role_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@R$");

	public static final Pattern identity_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@I$");

	public static final Pattern unit_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@U$");

	public static final Pattern unitAttribute_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@UA$");

	public static final Pattern unitDuty_distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@UD$");

	public static final Pattern distinguishedName_pattern = Pattern.compile("^(.+)\\@(\\S+)\\@(P|PA|G|R|I|U|UA|UD)$");

	public static final String NAME_JOIN_CHAR = "@";

	public static final List<String> DEFAULTROLES = new UnmodifiableList<>(ListTools.toList(Manager, SystemManager,
			SecurityManager, AuditManager, AttendanceManager, OrganizationManager, PersonManager, GroupManager,
			UnitManager, RoleManager, ProcessPlatformManager, ProcessPlatformCreator, MeetingManager, MeetingViewer,
			PortalManager, PortalCreator, BBSManager, CMSManager, CMSCreator, OKRManager, CRMManager, TeamWorkManager,
			QueryManager, MessageManager, HotPictureManager, SearchPrivilege, FileManager, ServiceManager));

	public static boolean isSystemUser(String name) {
		return EffectivePerson.CIPHER.equalsIgnoreCase(name) || EffectivePerson.XADMIN.equalsIgnoreCase(name);
	}
	public static String toDistinguishedName(String name) {
		if (!StringUtils.contains(name, NAME_JOIN_CHAR)) {
			name = name + NAME_JOIN_CHAR + name + RoleDefinitionSuffix + "@R";
		}
		return name;
	}

	public static String name(String distinguishedName) {
		if (StringUtils.contains(distinguishedName, NAME_JOIN_CHAR)) {
			return StringUtils.substringBefore(distinguishedName, NAME_JOIN_CHAR);
		}
		return distinguishedName;
	}

	public static String unique(String distinguishedName) {
		if (StringUtils.isEmpty(distinguishedName)) {
			return distinguishedName;
		}
		Matcher matcher = distinguishedName_pattern.matcher(distinguishedName);
		if (matcher.find()) {
			return matcher.group(2);
		}
		return distinguishedName;
	}

	public static List<String> name(List<String> list) {
		List<String> os = new ArrayList<>();
		if (ListTools.isNotEmpty(list)) {
			for (String str : list) {
				if (StringUtils.isNotEmpty(str)) {
					os.add(name(str));
				}
			}
		}
		return os;
	}

	public static boolean isDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isIdentityDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (identity_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isPersonDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (person_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isPersonAttributeDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (personAttribute_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unit_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitAttributeDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unitAttribute_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isUnitDutyDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (unitDuty_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isGroupDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (group_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static boolean isRoleDistinguishedName(String str) {
		if (StringUtils.isEmpty(str)) {
			return false;
		}
		if (role_distinguishedName_pattern.matcher(str).find()) {
			return true;
		}
		return false;
	}

	public static DistinguishedNameCategory distinguishedNameCategory(List<String> list) {
		DistinguishedNameCategory category = new DistinguishedNameCategory();
		if (ListTools.isNotEmpty(list)) {
			list.stream().forEach(o -> {
				if (isIdentityDistinguishedName(o)) {
					category.getIdentityList().add(o);
				} else if (isPersonDistinguishedName(o)) {
					category.getPersonList().add(o);
				} else if (isPersonAttributeDistinguishedName(o)) {
					category.getPersonAttributeList().add(o);
				} else if (isUnitDistinguishedName(o)) {
					category.getUnitList().add(o);
				} else if (isUnitAttributeDistinguishedName(o)) {
					category.getUnitAttributeList().add(o);
				} else if (isUnitDutyDistinguishedName(o)) {
					category.getUnitDutyList().add(o);
				} else if (isGroupDistinguishedName(o)) {
					category.getGroupList().add(o);
				} else if (isRoleDistinguishedName(o)) {
					category.getRoleList().add(o);
				} else {
					category.getUnknownList().add(o);
				}
			});
		}
		return category;

	}

	public static class DistinguishedNameCategory {
		private List<String> personList = new ArrayList<>();
		private List<String> personAttributeList = new ArrayList<>();
		private List<String> unitList = new ArrayList<>();
		private List<String> unitAttributeList = new ArrayList<>();
		private List<String> unitDutyList = new ArrayList<>();
		private List<String> identityList = new ArrayList<>();
		private List<String> groupList = new ArrayList<>();
		private List<String> roleList = new ArrayList<>();
		private List<String> unknownList = new ArrayList<>();

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public List<String> getPersonAttributeList() {
			return personAttributeList;
		}

		public void setPersonAttributeList(List<String> personAttributeList) {
			this.personAttributeList = personAttributeList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public List<String> getUnitAttributeList() {
			return unitAttributeList;
		}

		public void setUnitAttributeList(List<String> unitAttributeList) {
			this.unitAttributeList = unitAttributeList;
		}

		public List<String> getUnitDutyList() {
			return unitDutyList;
		}

		public void setUnitDutyList(List<String> unitDutyList) {
			this.unitDutyList = unitDutyList;
		}

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public List<String> getUnknownList() {
			return unknownList;
		}

		public void setUnknownList(List<String> unknownList) {
			this.unknownList = unknownList;
		}
	}

}
