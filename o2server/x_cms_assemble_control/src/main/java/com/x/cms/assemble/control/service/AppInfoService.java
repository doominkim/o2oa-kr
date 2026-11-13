package com.x.cms.assemble.control.service;

import com.x.cms.core.entity.element.File;
import com.x.cms.core.entity.element.Script;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.AppInfoConfig;
import com.x.cms.core.entity.element.AppDict;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class AppInfoService {

	public AppInfoConfig getConfigObject(EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.appInfoConfigFactory().get(id);
	}

	public String getConfigJson(EntityManagerContainer emc, String id ) throws Exception {
		Business business = new Business( emc );
		return business.appInfoConfigFactory().getContent(id);
	}

	public List<String> listAllIds(EntityManagerContainer emc, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllIds(documentType);
	}

	public List<AppInfo> listAll(EntityManagerContainer emc, String appType, String documentType) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAll( appType, documentType);
	}

	public void delete( EntityManagerContainer emc, String id ) throws Exception {
		List<String> ids = null;
		List<String> feildIds = null;
		List<String> dictIds = null;
		List<String> viewReleIds = null;
		AppInfo appInfo = null;
		Business business = new Business( emc );

		emc.beginTransaction( AppInfo.class );
		emc.beginTransaction( AppInfoConfig.class );
		emc.beginTransaction( AppDict.class );
		emc.beginTransaction( AppDictItem.class );
		emc.beginTransaction( View.class );
		emc.beginTransaction( ViewCategory.class );
		emc.beginTransaction( ViewFieldConfig.class );
		emc.beginTransaction( Script.class);
		emc.beginTransaction( File.class);

		//게시판 아래 모든 목록 뷰와 모든 목록 뷰 열 정보도 포함
		ids = business.getViewFactory().listByAppId( id );
		if( ListTools.isNotEmpty( ids ) ){
			View view = null;
			ViewFieldConfig viewFieldConfig = null;
			ViewCategory viewCategory = null;
			for( String del_id : ids ){
				view = emc.find( del_id, View.class );
				feildIds = business.getViewFieldConfigFactory().listByViewId( del_id );
				viewReleIds = business.getViewCategoryFactory().listByViewId( del_id );
				//목록 설정 열 삭제
				if( ListTools.isNotEmpty( feildIds ) ){
					for( String feild_id : feildIds ){
						viewFieldConfig = emc.find( feild_id, ViewFieldConfig.class );
						if( viewFieldConfig != null ) {
							emc.remove( viewFieldConfig, CheckRemoveType.all  );
						}
					}
				}
				//목록과 분류의 연결 정보 삭제
				if( ListTools.isNotEmpty( viewReleIds ) ){
					for( String rele_id : viewReleIds ){
						viewCategory = emc.find( rele_id, ViewCategory.class );
						if( viewCategory != null ) {
							emc.remove( viewCategory, CheckRemoveType.all  );
						}
					}
				}
				//목록 정보 삭제
				if( view != null ) {
					emc.remove( view, CheckRemoveType.all  );
				}
			}
		}

		//게시판 아래 모든 데이터 사전과 데이터 사전 설정 열 정보도 포함
		ids = business.getAppDictFactory().listWithAppInfo( id );
		if( ListTools.isNotEmpty( ids ) ){
			AppDict appDict = null;
			AppDictItem appDictItem = null;
			for (String del_id : ids) {
				appDict = emc.find(del_id, AppDict.class);
				dictIds = business.getAppDictItemFactory().listWithAppDict( del_id );
				if( ListTools.isNotEmpty( dictIds ) ){
					for ( String dict_id : dictIds ) {
						appDictItem = emc.find( dict_id, AppDictItem.class );
						if( appDictItem != null ) {
							emc.remove( appDictItem, CheckRemoveType.all  );
						}
					}
				}
				if( appDict != null ) {
					emc.remove(appDict, CheckRemoveType.all );
				}

			}
		}

		//게시판 스크립트 정보 삭제
		List<Script> scriptList = business.getScriptFactory().listScriptWithApp( id);
		for (Script script : scriptList){
			emc.remove(script);
		}

		//게시판 리소스 정보 삭제
		List<File> fileList = business.fileFactory().listWithApplicationObject( id);
		for (File file : fileList){
			emc.remove(file);
		}

		//게시판 설정 지원 정보 삭제
		AppInfoConfig appInfoConfig = business.appInfoConfigFactory().get( id );
		if( appInfoConfig != null ){
			emc.remove( appInfoConfig, CheckRemoveType.all );
		}

		//게시판 정보 삭제
		appInfo = emc.find( id, AppInfo.class );
		if( appInfo != null ){
			emc.remove( appInfo, CheckRemoveType.all );
		}
		emc.commit();
	}

	public AppInfo get(EntityManagerContainer emc, String id) throws Exception {
		if( StringUtils.isEmpty( id )) {
			return null;
		}
		return emc.find( id, AppInfo.class );
	}

	public AppInfo getWithFlag(EntityManagerContainer emc, String flag) throws Exception {
		if( StringUtils.isEmpty( flag )) {
			return null;
		}
		return emc.flag( flag, AppInfo.class );
	}

	public Long countCategoryByAppId(EntityManagerContainer emc, String id, String documentType ) throws Exception {
		Business business = new Business( emc );
		return business.getCategoryInfoFactory().countByAppId( id, documentType );
	}

	/**
	 * 게시판 정보 추가 또는 갱신
	 * @param emc
	 * @param wrapIn
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public AppInfo save( EntityManagerContainer emc, AppInfo wrapIn, String config ) throws Exception {
		AppInfo appInfo = null;
		AppInfoConfig appInfoConfig = null;
		if( wrapIn.getId() == null ){
			wrapIn.setId( AppInfo.createId() );
		}
		appInfo = emc.find( wrapIn.getId(), AppInfo.class );
		appInfoConfig = emc.find( wrapIn.getId(), AppInfoConfig.class );

		emc.beginTransaction( AppInfo.class );
		if( appInfo == null ){//게시판 정보 1건 추가
			appInfo = new AppInfo();
			wrapIn.copyTo( appInfo );
			if( StringUtils.isNotEmpty( wrapIn.getId() ) ){
				appInfo.setId( wrapIn.getId() );
			}
			if( StringUtils.isEmpty( appInfo.getAppAlias() )) {
				appInfo.setAppAlias( appInfo.getAppName() );
			}
			if( StringUtils.isEmpty( appInfo.getAppType() )) {
				appInfo.setAppType( "未分类" );
			}
			emc.persist( appInfo, CheckPersistType.all);
		}else{
			wrapIn.copyTo(appInfo, JpaObject.FieldsUnmodify );
			appInfo.setAppIcon( appInfo.getAppIcon() );
			if( StringUtils.isEmpty( appInfo.getAppAlias() )) {
				appInfo.setAppAlias( appInfo.getAppName() );
			}
			if( StringUtils.isEmpty( appInfo.getAppType() )) {
				appInfo.setAppType( "未分类" );
			}
			emc.check( appInfo, CheckPersistType.all );
		}

		emc.beginTransaction( AppInfoConfig.class );
		if( appInfoConfig == null ){
			appInfoConfig = new AppInfoConfig();
			appInfoConfig.setId( appInfo.getId() );
			appInfoConfig.setConfig( config );
			emc.beginTransaction( AppInfoConfig.class );
			emc.persist( appInfoConfig, CheckPersistType.all);
		}else{
			appInfoConfig.setConfig( config );
			emc.beginTransaction( AppInfoConfig.class );
			emc.check( appInfoConfig, CheckPersistType.all );
		}

		emc.commit();
		return appInfo;
	}

	/**
	 * 게시판 설정 지원 정보 추가 또는 갱신
	 * @param emc
	 * @param appId
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public AppInfoConfig saveConfig( EntityManagerContainer emc, String appId, String config ) throws Exception {
		AppInfo appInfo = null;
		AppInfoConfig appInfoConfig = null;
		appInfo = emc.find( appId, AppInfo.class );
		appInfoConfig = emc.find( appId, AppInfoConfig.class );

		if( appInfo == null ){//게시판 정보 1건 추가
			throw new Exception("appinfo not exists!id=" + appId );
		}

		emc.beginTransaction( AppInfoConfig.class );
		if( appInfoConfig == null ){
			appInfoConfig = new AppInfoConfig();
			appInfoConfig.setId( appInfo.getId() );
			appInfoConfig.setConfig( config );
			emc.beginTransaction( AppInfoConfig.class );
			emc.persist( appInfoConfig, CheckPersistType.all);
		}else{
			appInfoConfig.setConfig( config );
			emc.beginTransaction( AppInfoConfig.class );
			emc.check( appInfoConfig, CheckPersistType.all );
		}
		emc.commit();
		return appInfoConfig;
	}

	public List<String> listByAppName( EntityManagerContainer emc, String appName) throws Exception {
		if( StringUtils.isEmpty(appName ) ){
			throw new Exception( "appName is null!" );
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listByAppName( appName );
	}
	public List<String> listByAppAlias(EntityManagerContainer emc, String appAlias) throws Exception {
		if( StringUtils.isEmpty(appAlias ) ){
			throw new Exception( "appAlias is null!" );
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listByAppAlias( appAlias );
	}
	public List<AppInfo> listAppInfoByAppAliases(EntityManagerContainer emc, List<String> appAliases) throws Exception {
		if( ListTools.isEmpty( appAliases )){
			return null;
		}
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppInfoByAppAlias( appAliases );
	}

	public List<String> listAllAppType(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAllAppType();
	}

//	public Long countAppInfoWithAppType(EntityManagerContainer emc, String type) throws Exception {
//		Business business = new Business( emc );
//		return business.getAppInfoFactory().countAppInfoWithAppType( type );
//	}
//
//	public Long countAppInfoWithOutAppType(EntityManagerContainer emc ) throws Exception {
//		Business business = new Business( emc );
//		return business.getAppInfoFactory().countAppInfoWithOutAppType( );
//	}

	public List<String> listAppIdsWithAppType(EntityManagerContainer emc, String type) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppIdsWithAppType(type);
	}

	public List<String> listAppIdsWithOutAppType(EntityManagerContainer emc ) throws Exception {
		Business business = new Business( emc );
		return business.getAppInfoFactory().listAppIdsWithOutAppType();
	}

}
