package com.x.cms.assemble.control.service;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.jaxrs.search.AppFilter;
import com.x.cms.assemble.control.jaxrs.search.CategoryFilter;
import com.x.cms.assemble.control.jaxrs.search.TopUnitNameFilter;
import com.x.cms.assemble.control.jaxrs.search.UnitNameFilter;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;

public class SearchService {

	// @MethodDescribe("사용자별로 열람 가능한 모든 게시 문서에 관련된 앱 게시판과 문서 수 나열")
	public List<AppFilter> listAppInfoSearchFilter(EntityManagerContainer emc, List<String> app_ids, String docStatus,
			String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<AppFilter> appFilterList = new ArrayList<AppFilter>();
		AppFilter appFilter = null;
		List<String> appids_user = null;
		AppInfo appInfo = null;
		Business business = new Business(emc);

		// 먼저 사용자가 접근 가능한 문서에 관련된 접근 가능한 앱 게시판 수를 distinct
		appids_user = business.getSearchFactory().listDistinctAppInfoFromDocument(app_ids, docStatus, categoryId);
		if (appids_user != null && appids_user.size() > 0) {
			for (String appid : appids_user) {
				appInfo = emc.find(appid, AppInfo.class);
				if (appInfo != null) {
					appFilter = new AppFilter(appInfo.getId(), appInfo.getAppName(),
							business.getSearchFactory().getAppInfoDocumentCount(appid, docStatus, categoryId));
					appFilterList.add(appFilter);
				}
			}
		}
		return appFilterList;
	}

	// @MethodDescribe("사용자별로 열람 가능한 모든 게시 문서에 관련된 앱 분류와 문서 수 나열")
	public List<CategoryFilter> listCategorySearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<CategoryFilter> categoryFilterList = new ArrayList<CategoryFilter>();
		CategoryFilter categoryFilter = null;
		List<String> categoryids_user = null;
		CategoryInfo categoryInfo = null;
		AppInfo appInfo = null;
		Business business = new Business(emc);
		// 먼저 사용자가 접근 가능한 문서에 관련된 접근 가능한 분류 수를 distinct
		categoryids_user = business.getSearchFactory().listDistinctCategoryFromDocument(app_ids, docStatus, categoryId);
		if (categoryids_user != null && categoryids_user.size() > 0) {
			for (String _categoryId : categoryids_user) {
				categoryInfo = emc.find(_categoryId, CategoryInfo.class);
				if (categoryInfo != null) {
					appInfo = emc.find(categoryInfo.getAppId(), AppInfo.class);
					if (appInfo != null) {
						categoryFilter = new CategoryFilter(appInfo.getId(), appInfo.getAppName(), categoryInfo.getId(),
								categoryInfo.getCategoryName(), business.getSearchFactory()
										.getCategoryInfoDocumentCount(app_ids, _categoryId, docStatus));
						categoryFilterList.add(categoryFilter);
					}
				}
			}
		}
		return categoryFilterList;
	}

	// @MethodDescribe("사용자별로 열람 가능한 모든 게시 문서에 관련된 기안 최상위 조직과 문서 수 나열")
	public List<TopUnitNameFilter> listTopUnitNameSearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<TopUnitNameFilter> topUnitFilterList = new ArrayList<TopUnitNameFilter>();
		TopUnitNameFilter topUnitFilter = null;
		List<String> topUnitNames = null;
		Business business = new Business(emc);
		// 먼저 사용자가 접근 가능한 문서에 관련된 접근 가능한 분류 수를 distinct
		topUnitNames = business.getSearchFactory().listDistinctTopUnitNameFromDocument(app_ids, docStatus, categoryId);
		if (topUnitNames != null && topUnitNames.size() > 0) {
			for (String creatorTopUnitName : topUnitNames) {
				// 반복하여 데이터 통계
				topUnitFilter = new TopUnitNameFilter(creatorTopUnitName, creatorTopUnitName,
						business.getSearchFactory().getTopUnitDocumentCount(app_ids, creatorTopUnitName, docStatus,
								categoryId));
				topUnitFilterList.add(topUnitFilter);
			}
		}
		return topUnitFilterList;
	}

	// @MethodDescribe("사용자별로 열람 가능한 모든 게시 문서에 관련된 기안 조직과 문서 수 나열")
	public List<UnitNameFilter> listUnitNameSearchFilter(EntityManagerContainer emc, List<String> app_ids,
			String docStatus, String categoryId) throws Exception {
		if (app_ids == null) {
			return null;
		}
		List<UnitNameFilter> unitFilterList = new ArrayList<UnitNameFilter>();
		UnitNameFilter unitNameFilter = null;
		List<String> unitNames = null;
		Business business = new Business(emc);
		// 먼저 사용자가 접근 가능한 문서에 관련된 접근 가능한 분류 수를 distinct
		unitNames = business.getSearchFactory().listDistinctUnitNameFromDocument(app_ids, docStatus, categoryId);
		if (unitNames != null && unitNames.size() > 0) {
			for (String creatorUnitName : unitNames) {
				// 반복하여 데이터 통계
				unitNameFilter = new UnitNameFilter(creatorUnitName, creatorUnitName, business.getSearchFactory()
						.getUnitDocumentCount(app_ids, creatorUnitName, docStatus, categoryId));
				unitFilterList.add(unitNameFilter);
			}
		}
		return unitFilterList;
	}

}
