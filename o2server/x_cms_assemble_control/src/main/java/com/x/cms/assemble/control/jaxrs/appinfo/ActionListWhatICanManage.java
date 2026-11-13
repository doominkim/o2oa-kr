package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class ActionListWhatICanManage extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListWhatICanManage.class);

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = null;
		List<AppInfo> appInfoList = null;
		List<CategoryInfo> catacoryList = null;
		List<WoCategory> wrapOutCatacoryList = null;
		List<String> app_ids = null;
		Business business = new Business(null);
		Boolean isXAdmin = business.isManager( effectivePerson );
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), personName, "manage", isXAdmin );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);

		if (optional.isPresent()) {
			result.setData((List<Wo>)optional.get());
		} else {
			if (check) {
				if (isXAdmin) {
					try {
						appInfoList = appInfoServiceAdv.listAll( null, "전체");
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e, "전체 게시판 정보 객체를 조회하는 중 오류가 발생했습니다");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				} else {
					try {
						List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
						List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
						List<String> roleNames = userManagerService.listRoleNamesByPerson( personName );
						app_ids = permissionQueryService.listManageableAppIdsByPerson( personName, unitNames, groupNames, roleNames, null, null, null, "전체", 1000 );
						if (app_ids != null && !app_ids.isEmpty()) {
							try {
								appInfoList = appInfoServiceAdv.list( app_ids );
							} catch (Exception e) {
								check = false;
								Exception exception = new ExceptionAppInfoProcess(e, "ID 목록으로 게시판 정보 객체를 조회하는 중 오류가 발생했습니다.");
								result.error(exception);
								logger.error(e, effectivePerson, request, null);
							}
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e,
								"사용자 권한으로 관리하는 전체 게시판 정보를 조회하는 중 오류가 발생했습니다. Name:" + personName);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				if (appInfoList != null && !appInfoList.isEmpty()) {
					try {
						wos = Wo.copier.copy(appInfoList);
						SortTools.asc( wos, "appInfoSeq");
						CacheManager.put(cacheCategory, cacheKey, wos);
						result.setData(wos);
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionAppInfoProcess(e, "조회한 게시판 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다.");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
			if (check) {
				if ( wos != null && wos.size() > 0) {
					List<String> query_categoryList = null;
					for ( Wo wo : wos ) {
						try {
							wo.setConfig( appInfoServiceAdv.getConfigJson( wo.getId() ) );
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionAppInfoProcess(e, "ID로 게시판 설정 정보를 조회하는 중 오류가 발생했습니다. ID=" + wo.getId() );
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
						query_categoryList = new ArrayList<>();
						if( ListTools.isNotEmpty( wo.getCategoryList() )) {
							for( String categoryId : wo.getCategoryList() ) {
								if( categoryId != null && !"null".equalsIgnoreCase( categoryId.trim() ) ) {
									query_categoryList.add( categoryId );
								}
							}
						}
						try {
							catacoryList = categoryInfoServiceAdv.list( query_categoryList );
							if ( catacoryList != null && !catacoryList.isEmpty() ) {
								try {
									wrapOutCatacoryList = WoCategory.copier.copy( catacoryList );
									wo.setWrapOutCategoryList( wrapOutCatacoryList );
								} catch (Exception e) {
									check = false;
									Exception exception = new ExceptionAppInfoProcess(e, "조회한 분류 정보 객체를 출력 데이터로 변환하는 중 오류가 발생했습니다.");
									result.error(exception);
									logger.error(e, effectivePerson, request, null);
								}
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionAppInfoProcess(e, "ID 목록으로 분류 정보 객체를 조회하는 중 오류가 발생했습니다.");
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
					}
				}
			}
		}
		return result;
	}
}
