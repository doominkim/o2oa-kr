package com.x.cms.assemble.control.jaxrs.search;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.SearchServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;

class ActionListAppSearchFilterForDocStatus extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(SearchFilterAction.class);
	private AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	private SearchServiceAdv searchServiceAdv = new SearchServiceAdv();
	private UserManagerService userManagerService = new UserManagerService();

	public ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String docStatus, String categoryId ) {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		List<String> app_ids = null;
		List<AppFilter> appFilterList = null;
		List<CategoryFilter> categoryFilterList = null;
		List<TopUnitNameFilter> topUnitFilterList = null;
		List<UnitNameFilter> unitFilterList = null;
		Boolean isXAdmin = false;
		Boolean check = true;
		Boolean isAnonymous = effectivePerson.isAnonymous();
		String personName = effectivePerson.getDistinguishedName();
		// 문서 유형: 전체|정보|데이터
		String documentType = "信息";
		if (check) {
			try {
				isXAdmin = userManagerService.isManager( effectivePerson);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e,
						"사용자가 플랫폼 관리자인지 확인하는 중 오류가 발생했습니다. Name:" + personName);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (isXAdmin) {
				try {
					app_ids = appInfoServiceAdv.listAllIds(documentType);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSearchProcess(e, "系统在查询所有栏目信息ID列表时发生异常。");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				try {
					List<String> unitNames = userManagerService.listUnitNamesWithPerson( personName );
					List<String> groupNames = userManagerService.listGroupNamesByPerson( personName );
					List<String> roleNames = userManagerService.listRoleNamesByPerson(personName);
					app_ids = permissionQueryService.listViewableAppIdByPerson(
							personName, isAnonymous, unitNames, groupNames, roleNames, null, null, documentType, null,1000 );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSearchProcess( e,
							"系统在根据用户权限查询所有可见的栏目信息时发生异常。Name:" + personName );
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		// 1. 게시판 통계 목록 조회
		if (check) {
			// 사용자가 접근 가능한 문서와 관련된 모든 게시판 정보 및 게시판별 문서 수 조회
			try {
				appFilterList = searchServiceAdv.listAppInfoSearchFilter(app_ids, docStatus, categoryId );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e,
						"접근 가능한 게시판 ID 목록, 게시글 상태, 접근 가능한 분류 ID로 관련 게시판 이름 목록을 집계하는 중 오류가 발생했습니다."
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		// 2. 분류 통계 목록 조회
		if (check) {
			try {
				categoryFilterList = searchServiceAdv.listCategorySearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e,
						"접근 가능한 게시판 ID 목록, 게시글 상태, 접근 가능한 분류 ID로 관련 분류 이름 목록을 집계하는 중 오류가 발생했습니다."
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				topUnitFilterList = searchServiceAdv.listTopUnitSearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e,
						"접근 가능한 게시판 ID 목록, 게시글 상태, 접근 가능한 분류 ID로 관련 최상위 조직 이름 목록을 집계하는 중 오류가 발생했습니다."
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				unitFilterList = searchServiceAdv.listUnitNameSearchFilter(app_ids, docStatus, categoryId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSearchProcess(e,
						"접근 가능한 게시판 ID 목록, 게시글 상태, 접근 가능한 분류 ID로 관련 조직 이름 목록을 집계하는 중 오류가 발생했습니다."
						+ "AppIds:" + app_ids
						+ ", DocStatus:" + docStatus
						+ ", CategoryId:" +  categoryId
						);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			wrap.setAppfileter_list(appFilterList);
			wrap.setCategoryfileter_list(categoryFilterList);
			wrap.setTopUnitNamefileter_list(topUnitFilterList);
			wrap.setUnitNamefileter_list(unitFilterList);
			result.setData(wrap);
		}
		return result;
	}

	public static class Wo {

		public static List<String> excludes = new ArrayList<String>();

		private List<AppFilter> appfileter_list = null;

		private List<CategoryFilter> categoryfileter_list = null;

		private List<TopUnitNameFilter> topUnitfileter_list = null;

		private List<UnitNameFilter> unitfileter_list = null;

		public List<UnitNameFilter> getUnitNamefileter_list() {
			return unitfileter_list;
		}

		public void setUnitNamefileter_list(List<UnitNameFilter> unitfileter_list) {
			this.unitfileter_list = unitfileter_list;
		}

		public List<TopUnitNameFilter> getTopUnitNamefileter_list() {
			return topUnitfileter_list;
		}

		public void setTopUnitNamefileter_list(List<TopUnitNameFilter> topUnitfileter_list) {
			this.topUnitfileter_list = topUnitfileter_list;
		}

		public List<AppFilter> getAppfileter_list() {
			return appfileter_list;
		}

		public void setAppfileter_list(List<AppFilter> appfileter_list) {
			this.appfileter_list = appfileter_list;
		}

		public List<CategoryFilter> getCategoryfileter_list() {
			return categoryfileter_list;
		}

		public void setCategoryfileter_list(List<CategoryFilter> categoryfileter_list) {
			this.categoryfileter_list = categoryfileter_list;
		}

	}
}
