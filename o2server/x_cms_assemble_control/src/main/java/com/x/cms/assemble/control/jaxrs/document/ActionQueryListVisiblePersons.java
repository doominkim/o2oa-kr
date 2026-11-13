package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.ReviewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ActionQueryListVisiblePersons extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListVisiblePersons.class);

	/**
	 * 문서ID로 해당 문서의 모든 열람 가능자 목록 조회
	 * @param request
	 * @param id
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		AppInfo appInfo = null;
		CategoryInfo categoryInfo = null;
		Document document = null;
		Boolean check = true;
		String personName = effectivePerson.getDistinguishedName();
		
		if ( StringUtils.isEmpty(id)) {
			check = false;
			Exception exception = new ExceptionDocumentIdEmpty();
			result.error(exception);
		}
		
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), id );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wo = ( Wo ) optional.get();
			result.setData(wo);
		} else {
			if (check) {
				try {
					document = documentQueryService.get(id);
					if (document == null) {
						check = false;
						Exception exception = new ExceptionDocumentNotExists(id);
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 정보를 가져오는 중 오류가 발생했습니다. Id:" + id + ", Name:" + personName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				try {
					categoryInfo = categoryInfoServiceAdv.get( document.getCategoryId() );
					if (categoryInfo == null) {
						check = false;
						Exception exception = new ExceptionCategoryInfoNotExists(document.getCategoryId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "ID로 분류 정보를 조회하는 중 오류가 발생했습니다! ID: " + document.getCategoryId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}

			if (check) {
				try {
					appInfo = appInfoServiceAdv.get( document.getAppId() );
					if (appInfo == null) {
						check = false;
						Exception exception = new ExceptionAppInfoNotExists(document.getAppId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "ID로 게시판 정보를 조회하는 중 오류가 발생했습니다! ID: " + document.getAppId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			
			if (check) {
				//해당 문서의 열람자 수 계산
				ReviewService reviewService = new ReviewService();
				List<String> persons = reviewService.listPermissionPersons( appInfo, categoryInfo, document );
				
				if( ListTools.isNotEmpty( persons )) {
					//*일 수 있음, 일반적으로 모든 사용자 식별자 목록
					if( persons.contains( "*" )) {
						List<String> allPersons = listPersonWithUnit( document.getCreatorTopUnitName() );
						if( ListTools.isNotEmpty( allPersons )) {
							for( String person : persons ) {
								if( StringUtils.equals( "*" , person ) && allPersons.contains( person )) {
									allPersons.add( person );
								}
							}
						}
						persons = allPersons;
					}
				}
				if( persons == null ) {
					persons = new ArrayList<>();
				}
				
				//중복 제거
		        HashSet<String> set = new HashSet<String>( persons );
		        persons.clear();
		        persons.addAll(set);
				
				wo.setValueList(persons);
				result.setData(wo);
				result.setCount( Long.parseLong( persons.size() + ""));
				CacheManager.put(cacheCategory, cacheKey, wo );
			}
		}
		return result;
	}
	
	/**
	 * 조직 이름으로 해당 조직의 모든 사용자 식별자 조회
	 * @param unitName
	 * @return
	 */
	private List<String> listPersonWithUnit(String unitName) {
		UserManagerService  userManagerService = new UserManagerService();
		List<String> persons = null;
		try {
			persons = userManagerService.listPersonWithUnit(unitName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return persons;
	}

	public static class Wo extends WrapStringList {

	}

}