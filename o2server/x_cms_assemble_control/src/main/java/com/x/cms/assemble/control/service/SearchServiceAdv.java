package com.x.cms.assemble.control.service;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.jaxrs.search.AppFilter;
import com.x.cms.assemble.control.jaxrs.search.CategoryFilter;
import com.x.cms.assemble.control.jaxrs.search.TopUnitNameFilter;
import com.x.cms.assemble.control.jaxrs.search.UnitNameFilter;

/**
 * 데이터 조회 서비스 클래스(고급)
 * 고급 서비스는 Service로 트랜잭션 제어 가능
 * 
 * @author O2LEE
 */
public class SearchServiceAdv {
	
	private SearchService searchService = new SearchService();
	
	public List<AppFilter> listAppInfoSearchFilter( List<String> app_ids, String docStatus, String categoryId ) throws Exception {
		if( ListTools.isEmpty( app_ids ) ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listAppInfoSearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<CategoryFilter> listCategorySearchFilter( List<String> app_ids, String docStatus, String categoryId ) throws Exception {
		if( ListTools.isEmpty( app_ids ) ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listCategorySearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<TopUnitNameFilter> listTopUnitSearchFilter(List<String> app_ids, String docStatus, String categoryId) throws Exception {
		if( ListTools.isEmpty( app_ids ) ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listTopUnitNameSearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<UnitNameFilter> listUnitNameSearchFilter(List<String> app_ids, String docStatus, String categoryId) throws Exception {
		if( ListTools.isEmpty( app_ids ) ){
			return null;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return searchService.listUnitNameSearchFilter( emc, app_ids, docStatus, categoryId );
		} catch ( Exception e ) {
			throw e;
		}
	}

}
