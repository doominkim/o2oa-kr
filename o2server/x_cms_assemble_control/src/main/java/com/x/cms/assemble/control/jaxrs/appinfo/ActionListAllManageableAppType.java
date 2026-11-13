package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListAllManageableAppType extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListAllManageableAppType.class );

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<String> appTypes = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		Boolean isAnonymous = effectivePerson.isAnonymous();
		Boolean isManager = false;

		try {
			isManager = userManagerService.isManager( effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionAppInfoProcess(e, "사용자가 플랫폼 관리자인지 확인하는 중 오류가 발생했습니다. Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, isAnonymous, isManager );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			wos = new ArrayList<>();
			try {
				appTypes = appInfoServiceAdv.listAllAppType();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "전체 게시판 정보 객체를 조회하는 중 오류가 발생했습니다" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
			if( check ){
				//유형별 앱 ID 목록을 조회한 뒤, ID 목록으로 사용자가 접근 가능한 게시판 객체 조회
				List<String> appIdsForType = null;
				if(ListTools.isNotEmpty( appTypes ) ){
					for( String appType : appTypes ) {
						if( !"未分类".equals( appType )) {
							appIdsForType = appInfoServiceAdv.listAppIdsWithAppType( appType );
							if( !isManager && ListTools.isNotEmpty( appIdsForType ) ){
								List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
								List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
								List<String> roleNames = userManagerService.listRoleNamesByPerson( personName );
								appIdsForType = permissionQueryService.listManageableAppIdsByPerson(personName, unitNames, groupNames, roleNames, null, null, appType, null, 99 );
							}
							if( appIdsForType == null ){
								appIdsForType = new ArrayList<>();
							}
							if( appIdsForType.size() > 0 ) {
								wos.add( new Wo( appType, Long.parseLong( appIdsForType.size() + "") ));
							}
						}
					}
				}
				//미분류이면서 열람 권한이 있는 모든 게시판 목록 조회
				appIdsForType = appInfoServiceAdv.listAppIdsWithOutAppType();
				if( !isManager && ListTools.isNotEmpty( appIdsForType ) ){
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					List<String> roleNames = userManagerService.listRoleNamesByPerson( personName );
					appIdsForType = permissionQueryService.listManageableAppIdsByPerson(personName, unitNames, groupNames, roleNames, appIdsForType, null, null, null, 99 );
				}

				if( appIdsForType == null ){
					appIdsForType = new ArrayList<>();
				}

				if( appIdsForType.size() > 0 ) {
					wos.add( new Wo( "未分类", Long.parseLong( appIdsForType.size() + "") ));
				}

				CacheManager.put(cacheCategory, cacheKey, wos);
				result.setData( wos );
			}
		}
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("게시판 유형 이름")
		private String appType;

		@FieldDescribe("게시판 수")
		private Long count;

		public Wo( String _appType, Long _count ) {
			this.appType = _appType;
			this.count = _count;
		}

		public String getAppType() {
			return appType;
		}
		public void setAppType(String appType) {
			this.appType = appType;
		}
		public Long getCount() {
			return count;
		}
		public void setCount(Long count) {
			this.count = count;
		}
	}

}
