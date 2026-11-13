package com.x.cms.assemble.control.service;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.organization.core.entity.PersistenceProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 조직·사용자·역할 관련 정보 서비스 클래스
 *
 * @author O2LEE
 */
public class UserManagerService {

	private static Logger logger = LoggerFactory.getLogger(UserManagerService.class);

	/**
	 * 사용자 이름으로 사용자 조회
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public Person getPerson(String personName) throws Exception {
		Person person = null;
		try {
			Business business = new Business(null);
			if(personName.split("@").length == 2){
				personName = personName.split("@")[0];
			}
			person = business.organization().person().getObject(personName);
		} catch (Exception e) {
			throw e;
		}
		return person;
	}

	/**
	 * 직원 이름으로 조직 이름 조회 사용자 신분이 여럿이면 조직 등급이 가장 높은 조직 이름 사용
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson(personName);
			if ( ListTools.isEmpty( unitNames )) {
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					unitNames = business.organization().unit().listWithPerson( personName.split("@")[0] );
				}
			}
			if ( ListTools.isNotEmpty( unitNames )) {
				for (String unitName : unitNames) {
					unit = business.organization().unit().getObject(unitName);
					if (level < unit.getLevel()) {
						level = unit.getLevel();
						result = unitName;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return result;
	}

	/**
	 * 신분 이름으로 신분이 속한 조직 이름 조회
	 *
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameByIdentity(String identity) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getWithIdentity(identity);
		} catch (NullPointerException e) {
			System.out.println("신분으로 소속 조직 이름을 조회하는 중 NullPointerException이 발생했습니다. identity: " + identity);
			return null;
		} catch (Exception e) {
			System.out.println("신분으로 소속 조직 이름을 조회하는 중 오류가 발생했습니다. identity: " + identity);
			throw e;
		}
	}

	/**
	 * 사용자 이름으로 사용자가 속한 1단계 조직 이름 조회, 사용자 신분이 여럿이면 조직 등급이 가장 높은 신분 사용
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithPerson(String personName) throws Exception {
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 전달된 perosnName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			identity = getMajorIdentityWithPerson(personName);
			if (StringUtils.isNotBlank(identity)) {
				topUnitName = business.organization().unit().getWithIdentityWithLevel(identity, 1);
			}
			return topUnitName;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 신분 이름으로 신분이 속한 최상위 조직 이름 조회
	 *
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameByIdentity(String identity) throws Exception {
		try {
			Business business = new Business(null);
			return business.organization().unit().getWithIdentityWithLevel(identity, 1);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 개인 이름으로 주 신분 조회
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getMajorIdentityWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			identities = business.organization().identity().listWithPerson(personName);
			if (ListTools.isNotEmpty( identities )) {
				if( identities.size() == 1 ) {
					return identities.get(0);
				}else{
					for (String identity : identities) {
						Identity obj = business.organization().identity().getObject(identity);
						if (obj!= null && BooleanUtils.isTrue(obj.getMajor())) {
							return identity;
						}
					}
				}
				return identities.get(0);
			}
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자 신분으로 사용자 이름 조회
	 *
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameWithIdentity(String identity) throws Exception {
		if (StringUtils.isEmpty(identity)) {
			throw new Exception("identity is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity(identity);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 속한 모든 조직 이름 목록 조회
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUnitNamesWithPerson(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			unitNames = business.organization().unit().listWithPersonSupNested(personName);
			return unitNames == null ? new ArrayList<>() : unitNames;
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자 이름으로 사용자의 모든 신분 정보 조회
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentitiesWithPerson(String personName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("userName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			return business.organization().identity().listWithPerson(personName);
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 가진 모든 역할 정보 나열
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listRoleNamesByPerson(String personName) throws Exception {
		Business business = null;
		List<String> roleList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			roleList = business.organization().role().listWithPerson(personName);
			if (roleList != null && roleList.size() > 0) {
				roleList.stream().filter(r -> !nameList.contains(r)).distinct().forEach(r -> nameList.add(r));
			}
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>() : nameList;
	}

	/**
	 * 사용자가 가진 모든 그룹 정보 나열
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listGroupNamesByPerson(String personName) throws Exception {
		Business business = null;
		List<String> groupList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( StringUtils.isNotEmpty( personName )){
				if (personName.endsWith("@P") && personName.split("@").length == 2) {
					personName = business.organization().person().get(personName.split("@")[0]);
				}else{
					personName = business.organization().person().get(personName);
				}
			}
			groupList = business.organization().group().listWithPerson(personName);
			if (groupList != null && groupList.size() > 0) {
				groupList.stream().filter(g -> !nameList.contains(g)).distinct().forEach(g -> nameList.add(g));
			}
		} catch (NullPointerException e) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList == null ? new ArrayList<>() : nameList;
	}

	/**
	 * 사용자에게 지정 플랫폼 역할(예: CMS 시스템 관리자)이 있는지 판단
	 *
	 * @param personName
	 * @param roleName
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole(String personName, String roleName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is null!");
		}
		if (StringUtils.isEmpty(roleName)) {
			throw new Exception("roleName is null!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (personName.endsWith("@P") && personName.split("@").length == 2) {
				personName = business.organization().person().get(personName.split("@")[0]);
			}else{
				personName = business.organization().person().get(personName);
			}
			return business.organization().person().hasRole(personName, roleName);
		}
	}

	/**
	 * 지정 사용자가 관리자인지 판단 1. xadmin 2. manager 역할 보유 3. CMSManager@CMSManagerSystemRole@R 역할 보유
	 *
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson == null) {
			throw new Exception("effectivePerson is null!");
		}
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.isHasPlatformRole(effectivePerson.getDistinguishedName(), ThisApplication.ROLE_CMSManager)) {
			return true;
		}
		return false;
	}

	public String getPersonIdentity(String personName, String identity) throws Exception {
		List<String> identityNames = null;
		String identityName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			identityNames = listIdentitiesWithPerson(personName);
//			LogUtil.INFO("identityNames:", identityNames );
			if (StringUtils.isEmpty(identity)) {
				if (identityNames.size() == 0) {
					throw new Exception("person has no identity. personName:" + personName);
				} else if (identityNames.size() > 0) {
					identityName = identityNames.get(0);
				}
			} else {
				// 전달된 신분이 유효한지 판단
				identityName = this.findIdentity(identityNames, identity);
				if (StringUtils.isEmpty(identity)) {
					identityName = identityNames.get(0);
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return identityName;
	}

	private String findIdentity(List<String> identityNames, String identityName) throws Exception {
		if (identityName != null && !identityName.isEmpty() && !"null".equals(identityName)) {
			if (identityNames != null && !identityNames.isEmpty()) {
				for (String identity : identityNames) {
					if (identity.equalsIgnoreCase(identityName)) {
						return identity;
					}
				}
			}
		} else {
			if (identityNames != null && !identityNames.isEmpty()) {
				return identityNames.get(0);
			}
		}
		return null;
	}

	/**
	 * 사용자, 조직, 그룹으로 사용자 정보 조회
	 * 매개변수 name은 xx@xx@x 형식이어야 합니다
	 * @param name
	 * @return
	 */
	public List<String> listPersonWithName(String name) {
		List<String> list = new ArrayList<>();
		if (StringUtils.isEmpty( name ) || name.indexOf("@") == -1) {
			return list;
		}
		Matcher matcher = PersistenceProperties.Person.distinguishedName_pattern.matcher(name);
		if(matcher.find()){
			list.add(name);
			return list;
		}
		matcher = PersistenceProperties.Unit.distinguishedName_pattern.matcher(name);
		if(matcher.find()) {
			try {
				Business business = new Business(null);
				return business.organization().person().listWithUnitSubNested(name);
			} catch (Exception e) {
				logger.warn("조직【{}】으로 사용자 조회 예외: {}", name,e.getMessage());
			}
		}
		matcher = PersistenceProperties.Identity.distinguishedName_pattern.matcher(name);
		if(matcher.find()){
			try {
				Business business = new Business(null);
				String person = business.organization().person().getWithIdentity(name);
				if(StringUtils.isNotBlank(person)){
					list.add(person);
				}
				return list;
			} catch (Exception e) {
				logger.warn("조직【{}】으로 사용자 조회 예외: {}", name,e.getMessage());
			}
		}
		matcher = PersistenceProperties.Group.distinguishedName_pattern.matcher(name);
		if(matcher.find()) {
			try {
				Business business = new Business(null);
				return business.organization().person().listWithGroup( name );
			} catch (Exception e) {
				logger.warn("조직【{}】으로 사용자 조회 예외: {}", name,e.getMessage());
			}
		}
		return list;
	}

	/**
	 * 조직 이름으로 조직 내 모든 사용자 식별자 조회, 하위 조직 포함<br/>
	 * 2020-06-12 고유 식별자로 조회하도록 변경<br/>
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithUnit(String unitName) throws Exception {
		if (StringUtils.isEmpty( unitName )) {
			throw new Exception("unitName is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName은 3단 또는 2단일 수 있으므로 일관되게 가운데 고유 식별자로 조회
			String unique = getUniqueWithName( unitName );

			return business.organization().person().listWithUnitSubNested( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 조직 객체의 고유 식별자 조회<br/>
	 *<br/>
	 * 조직 객체 식별자는 보통 3단으로, 예: 종합부@1304-73504398-13419-0347@U, 홍길동@293041-9305983-04258-0943@P<br/>
	 * 문서 권한에도 2단이 있음, 첫 단이 자주 바뀌기 때문, 예: 조직: 행정종합부@1304-73504398-13419-0347@U<br/>
	 * 따라서 조회 시 가운데 고유 식별자만 사용하는 것이 좋음<br/>
	 * @param orgObjectName
	 * @return
	 */
	private String getUniqueWithName(String orgObjectName ) {
		if( StringUtils.isNotEmpty( orgObjectName )){
			String[] array = orgObjectName.split("@");
			if( array.length == 3 ){
				return array[1];
			}else if( array.length == 2 ){
				return array[0];
			}else{
				return orgObjectName;
			}
		}
		return null;
	}

	/**
	 * 그룹 이름으로 그룹 내 모든 사용자 식별자 조회<br/>
	 * 2020-06-12 고유 식별자로 조회하도록 변경<br/>
	 *
	 * @param groupName
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithGroup(String groupName) throws Exception {
		if (StringUtils.isEmpty(groupName)) {
			throw new Exception("groupName is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName은 3단 또는 2단일 수 있으므로 일관되게 가운데 고유 식별자로 조회
			String unique = getUniqueWithName( groupName );

			return business.organization().person().listWithGroup( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 역할 이름으로 역할 구성원 내 모든 사용자 식별자 조회<br/>
	 * 2020-06-12 고유 식별자로 조회하도록 변경<br/>
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> listPersonWithRole(String role) throws Exception {
		if (StringUtils.isEmpty(role)) {
			throw new Exception("role is empty!");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//2020-06-12 unitName은 3단 또는 2단일 수 있으므로 일관되게 가운데 고유 식별자로 조회
			String unique = getUniqueWithName( role );

			return business.organization().person().listWithRole( unique );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 시스템 내 모든 최상위 조직 수 조회
	 *
	 * @return
	 * @throws Exception
	 */
	public int countTopUnit() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if (ListTools.isNotEmpty(unitNames)) {
				return unitNames.size();
			}
		} catch (Exception e) {
			throw e;
		}
		return 0;
	}

	/**
	 * 지정 조직이 최상위 조직인지 판단
	 *
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public boolean isTopUnit(String unitName) throws Exception {
		if (StringUtils.isEmpty(unitName)) {
			return false;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String[] splitName = unitName.split("@");
			if(splitName.length == 2){
				unitName = business.organization().unit().get(splitName[0]);
			}
			List<String> unitNames = business.organization().unit().listWithLevel(1);
			if (ListTools.isNotEmpty(unitNames) && unitNames.contains(unitName)) {
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	public boolean hasCategoryManagerPermission( EffectivePerson person, String appId) throws Exception {
		if( person.isManager()){
			return true;
		}
		UserManagerService userManagerService = new UserManagerService();
		//Manager 관리자
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_Manager )){
			return true;
		}
		//CMS 관리자
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_CMSManager )){
			return true;
		}

		//사용자가 해당 게시판의 관리자인지 조회
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			//관리자임
			if( ListTools.isNotEmpty(appInfo.getManageablePersonList()) && ListTools.contains( appInfo.getManageablePersonList(), person.getDistinguishedName() )){
				return true;
			}
			if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )){
				List<String> unitNames = userManagerService.listUnitNamesWithPerson( person.getDistinguishedName() );
				if( ListTools.isNotEmpty( unitNames )){
					unitNames.retainAll( appInfo.getManageableUnitList() );
					if( ListTools.isNotEmpty( unitNames )){
						return true;
					}
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )){
				List<String> groupNames = userManagerService.listGroupNamesByPerson( person.getDistinguishedName() );
				if( ListTools.isNotEmpty( groupNames )){
					groupNames.retainAll( appInfo.getManageableGroupList() );
					if( ListTools.isNotEmpty( groupNames )){
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}

		return false;
	}

	public boolean hasAppInfoManagerPermission( EffectivePerson person ) throws Exception {
		//시스템 관리자
		if( person.isManager() || person.isCipher() ){
			return true;
		}
		if( StringUtils.equalsIgnoreCase("xadmin", person.getName() ) || StringUtils.equalsIgnoreCase("xadmin", person.getDistinguishedName() ) ){
			return true;
		}
		//CMS 관리자
		UserManagerService userManagerService = new UserManagerService();
		if( userManagerService.isHasPlatformRole( person.getDistinguishedName(), ThisApplication.ROLE_CMSManager )){
			return true;
		}
		return false;
	}
}
