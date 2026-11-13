package com.x.calendar.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.Business;


/**
 * 조직·사용자·역할 관련 정보 서비스 클래스
 * 
 * @author O2LEE
 */
public class UserManagerService {
	
	/**
	 * 직원 이름으로 조직 이름 조회
	 * 사용자 신분이 여러 개이면 조직 등급이 가장 높은 조직 이름 사용
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithPerson( String personName ) throws Exception {
		List<String> unitNames = null;		
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson( personName );
			if( unitNames != null && !unitNames.isEmpty() ) {
				for( String unitName : unitNames ) {
					unit = business.organization().unit().getObject( unitName );
					if( level < unit.getLevel() ) {
						level = unit.getLevel();
						result = unitName;
					}
				}
			}
		} catch ( Exception e ) {
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
	public String getUnitNameByIdentity( String identity ) throws Exception {
		Business business = null;
		String unitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitName = business.organization().unit().getWithIdentity( identity );
			if ( unitName == null ) {// 해당 신분으로 조직 정보를 조회할 수 없음
				return null;
			} else {
				return unitName;
			}
		} catch( NullPointerException e ) {
			return null;
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
	public String getTopUnitNameByIdentity( String identity ) throws Exception {
		Business business = null;
		String unitName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			if ( unitName == null ) {
				return null;
			} else {
				return unitName;
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 사용자 이름으로 사용자가 속한 최상위 조직 이름 조회, 사용자 신분이 여러 개이면 조직 등급이 가장 높은 신분 사용
	 * @param personName
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithPerson( String personName ) throws Exception {
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//전달된 perosnName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			personName = business.organization().person().get( personName );
			identity = getIdentityWithPerson( personName );
			if( identity != null && !identity.isEmpty() ){
				topUnitName = business.organization().unit().getWithIdentityWithLevel( identity, 1 );
			}
			return topUnitName;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 개인 이름으로 모든 신분 중 조직 등급이 가장 높은 신분 조회
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getIdentityWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		String unitName = null;
		Integer level = 0;
		String resultIdentity = null;
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			//전달된 perosnName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			personName = business.organization().person().get( personName );
			identities = business.organization().identity().listWithPerson( personName );
			if( identities != null && !identities.isEmpty() ) {
				for( String identity : identities ) {
					unitName = business.organization().unit().getWithIdentity( identity );
					unit = business.organization().unit().getObject( unitName );
					if( level < unit.getLevel() ) {
						level = unit.getLevel();
						resultIdentity = identity;
					}
				}
			}
			return resultIdentity;
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 현재 사용자에게 지정 신분 정보가 있는지 조회
	 * @param name
	 * @param loginIdentity
	 * @return
	 * @throws Exception 
	 */
	public boolean hasIdentity( String personName, String loginIdentity ) throws Exception {
		if( loginIdentity == null || loginIdentity.isEmpty() ){
			throw new Exception( "loginIdentity is null!" );
		}
		Business business = null;
		List<String> identities = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
			if( identities.contains( loginIdentity )){
				return true;
			}
		} catch ( Exception e ) {
			throw e;
		}
		return false;
	}

	/**
	 * 사용자 신분으로 사용자 이름 조회
	 * @param identity
	 * @return
	 * @throws Exception 
	 */
	public String getPersonNameByIdentity( String identity ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity( identity );
		} catch ( Exception e ) {
			throw e;
		}
	}	

	/**
	 * 사용자 고유 식별자로 사용자 객체 조회
	 * @param flag
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameWithFlag( String flag ) throws Exception {
		if( flag == null || flag.isEmpty() ){
			throw new Exception( "flag is null!" );
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().get( flag );
		} catch ( Exception e ) {
			throw e;
		}
	}

	/**
	 * 사용자에게 지정 플랫폼 역할(예: CMS 시스템 관리자)이 있는지 판단
	 * 
	 * @param name
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public boolean isHasPlatformRole( String personName, String roleName) throws Exception {
		if ( personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (roleName == null || roleName.isEmpty()) {
			throw new Exception("roleName is null!");
		}
		
		List<String> roleList = null;
		String splitStr = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( personName );
			
			if ( ListTools.isNotEmpty(roleList) ) {
				for( String role : roleList ) {
					splitStr =  role.split( "@" )[0];
					if( StringUtils.equalsIgnoreCase(roleName, splitStr)) {
						return true;
					}
				}
			} else {
				return false;
			}
		} catch( NullPointerException e ) {
			return false;
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	/**
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public Boolean isIdentityExsits( String identity ) throws Exception {
		if( identity == null || identity.isEmpty() ){
			throw new Exception( "identity is null!" );
		}
		Business business = null;
		String result = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			result = business.organization().identity().get( identity );
			if( result == null || result.isEmpty() ){
				return false;
			}else{
				return true;
			}
		} catch ( Exception e ) {
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
	public String getPersonNameWithIdentity( String identity ) throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity( identity );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 사용자 식별자로 사용자 객체 조회
	 * 
	 * @param falg
	 * @return
	 * @throws Exception
	 */
	public Person getPersonWithFlag( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag ) ) {
			throw new Exception("flag is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getObject( flag );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	public Unit getUnitWIthFlag(String flag) throws Exception {
		if ( StringUtils.isEmpty( flag ) ) {
			throw new Exception("flag is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().getObject( flag );
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 속한 모든 조직 이름 목록 조회
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public List<String> listUnitNamesWithPerson( String personName ) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPersonSupNested( personName );
			return unitNames;
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 사용자 이름으로 사용자의 모든 신분 정보 조회
	 * 
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentitiesWithPerson( String userName ) throws Exception {
		if (userName == null || userName.isEmpty()) {
			throw new Exception("userName is null!");
		}
		Business business = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			business = new Business(emc);
			return business.organization().identity().listWithPerson(userName);
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 가진 모든 역할 정보 나열
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> listRoleNamesByPerson( String personName ) throws Exception {
		Business business = null;
		List<String> roleList = null;
		List<String> nameList = new ArrayList<String>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			roleList = business.organization().role().listWithPerson( personName );
			if (roleList != null && roleList.size() > 0) {
				roleList.stream().filter( r -> !nameList.contains( r )).distinct().forEach( r -> nameList.add( r ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}

	/**
	 * 사용자가 가진 모든 그룹 정보 나열
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
			groupList = business.organization().group().listWithPerson( personName );
			if (groupList != null && groupList.size() > 0) {
				groupList.stream().filter( g -> !nameList.contains( g )).distinct().forEach( g -> nameList.add( g ));
			}
		} catch( NullPointerException e ) {
			return null;
		} catch (Exception e) {
			throw e;
		}
		return nameList;
	}
	
	public String getPersonIdentity( String personName, String identity ) throws Exception {
		List<String> identityNames = null;
		String identityName = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			identityNames = listIdentitiesWithPerson( personName );
			if( identity == null || identity.isEmpty() ) {
				if ( identityNames.size() == 0 ) {
					throw new Exception("person has no identity.");
				} else if ( identityNames.size() > 0 ) {
					identityName = identityNames.get(0);
				}
			}else {
				//전달된 신분이 유효한지 판단
				identityName = this.findIdentity( identityNames, identity );
				if ( null == identity || identity.isEmpty() ) {
					throw new Exception("identity is invalid!identity:" + identity );
				}
			}
		} catch (Exception e) {
			throw e;
		}
		return identityName;
	}
	
	private String findIdentity( List<String> identityNames, String identityName ) throws Exception {
		if( identityName != null && !identityName.isEmpty() && !"null".equals(identityName)){
			if( identityNames != null && !identityNames.isEmpty() ){
				for ( String identity : identityNames ) {
					if ( identity.equalsIgnoreCase(identityName) ) {
						return identity;
					}
				}
			}
		}else{
			if( identityNames != null && !identityNames.isEmpty() ){
				return identityNames.get(0);
			}
		}
		return null;
	}

	public List<String> getPersonPermissionCodes(String personName ) throws Exception {
		List<String> queryObjectNames = new ArrayList<>();
		List<String> groupNames = null;
		List<String> roleNames = null;
		List<String> unitNames = null;
		List<String> identities = null;
		List<String> dutyNames = null;
		
		//먼저 개인과 관련된 모든 조직·역할 및 그룹 코드 조회
		groupNames = listGroupNamesByPerson( personName );
		roleNames = listRoleNamesByPerson( personName );
		unitNames = listUnitNamesWithPerson( personName );
		identities = listIdentitiesWithPerson( personName );
		dutyNames = listAllDutyWithPerson( personName );
		
		queryObjectNames.add( personName );
		
		//세 목록을 하나로 합친 뒤 분류 권한 조회
		if( identities != null && !identities.isEmpty() ) {
			for( String identity : identities ) {
				if( !queryObjectNames.contains( identity )) {
					queryObjectNames.add( identity );
				}
			}
		}
		if( groupNames != null && !groupNames.isEmpty() ) {
			for( String name : groupNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		if( roleNames != null && !roleNames.isEmpty() ) {
			for( String name : roleNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		if( unitNames != null && !unitNames.isEmpty() ) {
			for( String name : unitNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		if( dutyNames != null && !dutyNames.isEmpty() ) {
			for( String name : dutyNames ) {
				if( !queryObjectNames.contains( name )) {
					queryObjectNames.add( name );
				}
			}
		}
		return queryObjectNames;
	}

	public List<String> listIdentityWithUnitName( String unit ) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().identity().listWithUnitSubNested(unit);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 지정 조직의 지정 직무에 해당하는 신분 목록 조회
	 * 
	 * @param unit
	 * @param dutyName
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentityWithDutyInUnit(String unit, String dutyName) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unitDuty().listIdentityWithUnitWithName( unit, dutyName );
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 지정 조직의 지정 직무에 해당하는 신분 목록 조회
	 * 
	 * @param unit
	 * @param dutyNames
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdentityWithDutyInUnit( String unit, String[] dutyNames ) throws Exception {
		List<String> identities = null;
		List<String> resultList = new ArrayList<>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if( dutyNames != null && dutyNames.length > 0 ) {
				for( String dutyName : dutyNames ) {
					identities = business.organization().unitDuty().listIdentityWithUnitWithName( unit, dutyName );
					if( identities != null && !identities.isEmpty() ) {
						for( String identity : identities ) {
							if( !resultList.contains( identity )) {
								resultList.add( identity );
							}
						}
					}
				}
			}
			return resultList;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<String> listAllDutyWithPerson( String personName ) throws Exception {
		List<String> identities = null;
		List<String> dutyNames = null;
		List<String> resultList = new ArrayList<>();
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			identities = business.organization().identity().listWithPerson( personName );
			if( identities != null && !identities.isEmpty() ) {
				for( String identity : identities ) {
					dutyNames = business.organization().unitDuty().listNameWithIdentity( identity );
					if( dutyNames != null && !identities.isEmpty() ) {
						for( String dutyName : dutyNames ) {
							if( !resultList.contains( dutyName )) {
								resultList.add( dutyName );
							}
						}
					}
				}
			}
			return resultList;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 관리자인지 판단
	 * @param request
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public Boolean isManager(HttpServletRequest request, EffectivePerson effectivePerson) throws Exception {
		if( effectivePerson.isManager() ) {
			return true;
		}
		if( "xadmin".equalsIgnoreCase( effectivePerson.getName() )) {
			return true;
		}
		return isHasPlatformRole(effectivePerson.getDistinguishedName(), "CalendarManager");
	}

	
}