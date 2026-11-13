package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfoConfig;

/**
 * 게시판 정보 관리 서비스 클래스(고급)
 * 고급 서비스는 Service로 트랜잭션 제어 가능
 *
 * @author O2LEE
 */
public class AppInfoServiceAdv {

	private AppInfoService appInfoService = new AppInfoService();

	public AppInfo get( String id ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.get(emc, id);
		} catch (Exception e) {
			throw e;
		}
	}

	public AppInfoConfig getConfigObject(String id ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.getConfigObject( emc, id );
		} catch (Exception e) {
			throw e;
		}
	}

	public String getConfigJson(String id ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.getConfigJson( emc, id );
		} catch (Exception e) {
			throw e;
		}
	}

	public AppInfo getWithFlag( String flag ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.getWithFlag( emc, flag );
		} catch (Exception e) {
			throw e;
		}
	}

	public Long countCategoryByAppId(String id, String documentType) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.countCategoryByAppId(emc, id, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public void delete( String id, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty(id )) {
			throw new Exception("id is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			appInfoService.delete(emc, id );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> listAll( String appType, String documentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAll(emc, appType, documentType);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listAllIds(String documentType) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			List<String> list = appInfoService.listAllIds(emc, documentType);
			List<String> result =  new ArrayList<>();
			if( ListTools.isNotEmpty( list )) {
				for( String id : list ) {
					ListTools.addStringToList(id, result);
				}
			}
			return result;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> list(List<String> app_ids) throws Exception {
		if (ListTools.isEmpty( app_ids )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.list( AppInfo.class,  app_ids );
//			return appInfoService.list(emc, app_ids);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 게시판 정보 저장 서비스
	 * @param appInfo
	 * @param config
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public AppInfo save( AppInfo appInfo, String config, EffectivePerson effectivePerson) throws Exception {
		if ( appInfo == null) {
			throw new Exception("appInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			// 해당 앱 게시판에 관리자가 있는지 검사, 없으면 현재 로그인 사용자를 앱 게시판 관리자로 지정
			if( ListTools.isEmpty( appInfo.getManageablePersonList())  && ListTools.isEmpty( appInfo.getManageableUnitList())  &&ListTools.isEmpty( appInfo.getManageableGroupList())) {
				if( "xadmin".equalsIgnoreCase( effectivePerson.getName() )) {
					appInfo.addManageablePerson( "xadmin" );
				}else {
					appInfo.addManageablePerson( effectivePerson.getDistinguishedName() );
				}
			}
			appInfo = appInfoService.save( emc, appInfo, config );
		} catch (Exception e) {
			throw e;
		}
		return appInfo;
	}

	public AppInfoConfig saveConfig( String appId, String config, EffectivePerson effectivePerson) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is null.");
		}
		if ( StringUtils.isEmpty( config )) {
			throw new Exception("config content is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.saveConfig( emc, appId, config );
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> listByAppName(String appName) throws Exception {
		if ( StringUtils.isEmpty(appName )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppName(emc, appName);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<AppInfo> listAppInfoWithAliases( List<String> appAliases) throws Exception {
		if ( ListTools.isEmpty( appAliases )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAppInfoByAppAliases(emc, appAliases);
		} catch (Exception e) {
			throw e;
		}
	}

	public List<String> getWithAlias(String appAlias) throws Exception {
		if ( StringUtils.isEmpty(appAlias )) {
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listByAppAlias(emc, appAlias);
		} catch (Exception e) {
			throw e;
		}
	}

	public void saveAppInfoIcon( String appId, String base64, String iconMainColor ) throws Exception {
		if ( StringUtils.isEmpty(appId )) {
			throw new Exception("appId is null");
		}
		if ( StringUtils.isEmpty(base64 )) {
			throw new Exception("base64 is null");
		}
		if ( StringUtils.isEmpty(iconMainColor)) {
			throw new Exception("iconMainColor is null");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo app = emc.find( appId, AppInfo.class );
			if( app == null ) {
				throw new Exception("appInfo not exists.id:" + appId);
			}else {
				emc.beginTransaction( AppInfo.class );
				app.setAppIcon(base64);
				app.setIconColor(iconMainColor);
				emc.check( app, CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 게시판 관리자 권한 정보 갱신
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception
	 */
	public void updateManagerPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList, List<String> roleList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setManageablePersonList(personList);
			appInfo.setManageableUnitList(unitList);
			appInfo.setManageableGroupList(groupList);
			appInfo.setManageableRoleList(roleList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 게시판 게시 권한 정보 갱신
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception
	 */
	public void updatePublisherPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList, List<String> roleList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setPublishablePersonList(personList);
			appInfo.setPublishableUnitList(unitList);
			appInfo.setPublishableGroupList(groupList);
			appInfo.setPublishableRoleList(roleList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 게시판 공개 권한 정보 갱신
	 * @param appId
	 * @param personList
	 * @param unitList
	 * @param groupList
	 * @throws Exception
	 */
	public void updateViewerPermission(String appId, List<String> personList, List<String> unitList, List<String> groupList) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			throw new Exception("appId is empty.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			appInfo.setViewablePersonList(personList);
			appInfo.setViewableUnitList(unitList);
			appInfo.setViewableGroupList(groupList);
			emc.check(appInfo , CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	public void updateAllPermission(AppInfo appInfo) throws Exception {
		if ( appInfo == null ) {
			throw new Exception("appInfo is null.");
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo_entity = emc.find( appInfo.getId(), AppInfo.class );
			if( appInfo_entity != null ) {
				emc.beginTransaction( AppInfo.class );
				appInfo_entity.setManageablePersonList(appInfo.getManageablePersonList() );
				appInfo_entity.setPublishablePersonList( appInfo.getPublishablePersonList() );
				appInfo_entity.setPublishableUnitList( appInfo.getPublishableUnitList() );
				appInfo_entity.setPublishableGroupList( appInfo.getPublishableGroupList() );
				appInfo_entity.setViewablePersonList( appInfo.getViewablePersonList() );
				appInfo_entity.setViewableUnitList( appInfo.getViewableUnitList() );
				appInfo_entity.setViewableGroupList( appInfo.getViewableGroupList() );
				if( StringUtils.isEmpty( appInfo_entity.getDocumentType() )) {
					appInfo_entity.setDocumentType( "정보" );
				}
				emc.check(appInfo_entity , CheckPersistType.all );
				emc.commit();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 지정 게시판의 관리자인지 판단
	 * @param appId
	 * @param distinguishedName
	 * @param groupNames
	 * @param unitNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoManager(String appId, String distinguishedName, List<String> unitNames, List<String> groupNames, List<String> roleNames) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			return isAppInfoManager(appInfo, distinguishedName, unitNames, groupNames, roleNames);
		} catch (Exception e) {
			throw e;
		}
	}


	/**
	 * 사용자가 지정 게시판의 게시자 권한을 가졌는지 판단
	 * @param appId
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoPublisher(String appId, String personName, List<String> unitNames, List<String> groupNames, List<String> roleNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			return isAppInfoPublisher(appInfo, personName, unitNames, groupNames, roleNames);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 지정 게시판의 접근 권한을 가졌는지 판단
	 * @param appId
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoViewer(String appId, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			return isAppInfoViewer(appInfo, personName, unitNames, groupNames);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 사용자가 지정 게시판의 관리자인지 판단
	 * @param appInfo
	 * @param distinguishedName
	 * @param groupNames
	 * @param unitNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoManager( AppInfo appInfo, String distinguishedName, List<String> unitNames, List<String> groupNames, List<String> roleNames) throws Exception {
		if( appInfo != null ) {
			if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )){
				if( appInfo.getManageablePersonList().contains( distinguishedName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )){
				if( ListTools.containsAny( unitNames, appInfo.getManageableUnitList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )){
				if( ListTools.containsAny( groupNames, appInfo.getManageableGroupList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableRoleList() )){
				if( ListTools.containsAny( roleNames, appInfo.getManageableRoleList()) ) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 사용자가 지정 게시판의 게시자 권한을 가졌는지 판단
	 * @param appInfo
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoPublisher( AppInfo appInfo, String personName, List<String> unitNames, List<String> groupNames, List<String> roleNames ) throws Exception {
		if( appInfo != null )  {
			if( appInfo != null ) {
				if( ListTools.isNotEmpty( appInfo.getPublishablePersonList() )){
					if( appInfo.getPublishablePersonList().contains( personName )) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )){
					if( ListTools.containsAny( unitNames, appInfo.getPublishableUnitList()) ) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )){
					if( ListTools.containsAny( groupNames, appInfo.getPublishableGroupList()) ) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getPublishableRoleList() )){
					if( ListTools.containsAny( roleNames, appInfo.getPublishableRoleList()) ) {
						return true;
					}
				}

				if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )){
					if( appInfo.getManageablePersonList().contains( personName )) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )){
					if( ListTools.containsAny( unitNames, appInfo.getManageableUnitList()) ) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )){
					if( ListTools.containsAny( groupNames, appInfo.getManageableGroupList()) ) {
						return true;
					}
				}
				if( ListTools.isNotEmpty( appInfo.getManageableRoleList() )){
					if( ListTools.containsAny( roleNames, appInfo.getManageableRoleList()) ) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 사용자가 지정 게시판의 접근 권한을 가졌는지 판단
	 * @param appInfo
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @return
	 * @throws Exception
	 */
	public Boolean isAppInfoViewer( AppInfo appInfo, String personName, List<String> unitNames, List<String> groupNames ) throws Exception {
		if( appInfo != null ) {
			if( ListTools.isNotEmpty( appInfo.getViewablePersonList() )){
				if( appInfo.getViewablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getViewableUnitList() )){
				if( ListTools.containsAny( unitNames, appInfo.getViewableUnitList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getViewableGroupList() )){
				if( ListTools.containsAny( groupNames, appInfo.getViewableGroupList()) ) {
					return true;
				}
			}

			if( ListTools.isNotEmpty( appInfo.getPublishablePersonList() )){
				if( appInfo.getPublishablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getPublishableUnitList() )){
				if( ListTools.containsAny( unitNames, appInfo.getPublishableUnitList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getPublishableGroupList() )){
				if( ListTools.containsAny( groupNames, appInfo.getPublishableGroupList()) ) {
					return true;
				}
			}

			if( ListTools.isNotEmpty( appInfo.getManageablePersonList() )){
				if( appInfo.getManageablePersonList().contains( personName )) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableUnitList() )){
				if( ListTools.containsAny( unitNames, appInfo.getManageableUnitList()) ) {
					return true;
				}
			}
			if( ListTools.isNotEmpty( appInfo.getManageableGroupList() )){
				if( ListTools.containsAny( groupNames, appInfo.getManageableGroupList()) ) {
					return true;
				}
			}
		}
		return false;
	}

	public void reviewed(String appId) throws Exception {
		if ( StringUtils.isEmpty( appId )) {
			return;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AppInfo appInfo = emc.find( appId, AppInfo.class );
			emc.beginTransaction( AppInfo.class );
			emc.persist( appInfo, CheckPersistType.all );
			emc.commit();
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 시스템에 존재하는 모든 게시판 유형 조회
	 * @return
	 * @throws Exception
	 */
	public List<String> listAllAppType() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAllAppType(emc);
		} catch (Exception e) {
			throw e;
		}
	}

//	public Long countAppInfoWithAppType(String type) throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			return appInfoService.countAppInfoWithAppType(emc, type);
//		} catch (Exception e) {
//			throw e;
//		}
//	}
//
//	public Long countAppInfoWithOutAppType() throws Exception {
//		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
//			return appInfoService.countAppInfoWithOutAppType(emc );
//		} catch (Exception e) {
//			throw e;
//		}
//	}

	/**
	 * 지정 앱 유형으로 해당 유형의 모든 콘텐츠 관리 게시판 ID 목록 조회
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List<String> listAppIdsWithAppType(String type) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAppIdsWithAppType(emc, type);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 분류되지 않은 모든 콘텐츠 관리 게시판 ID 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<String> listAppIdsWithOutAppType() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return appInfoService.listAppIdsWithOutAppType(emc);
		} catch (Exception e) {
			throw e;
		}
	}
}
