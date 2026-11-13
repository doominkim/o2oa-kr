package com.x.cms.assemble.control.jaxrs.categoryinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.query.core.entity.View;

/**
 * 분류에 연결된 가져오기 목록 ID만 별도 갱신
 * @author O2LEE
 *
 */
public class ActionSaveImportView extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionSaveImportView.class );

	@AuditLog(operation = "분류 문서 가져오기 뷰 수정")
	protected ActionResult<Wo> execute(  HttpServletRequest request, EffectivePerson effectivePerson, String categoryId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		CategoryInfo categoryInfo = null;
		View view = null;
		Wi wi = null;
		Boolean check = true;

		if( StringUtils.isEmpty(categoryId) ){
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error( exception );
		}
		
		if( check ){
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if( categoryInfo == null ){
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists( categoryId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "ID로 분류 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + categoryId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		try {
			wi = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionCategoryInfoProcess(e,
					"JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
//		
//		if( StringUtils.isEmpty( wi.getViewAppId() ) ){
//			check = false;
//			Exception exception = new ExceptionViewAppIdEmpty();
//			result.error( exception );
//		}
//		if( StringUtils.isEmpty( wi.getViewId() )  ){
//			check = false;
//			Exception exception = new ExceptionViewIdEmpty();
//			result.error( exception );
//		}
		
//		if( check ){
//			if( StringUtils.isNotEmpty( wi.getViewId() )) {
//				try {
//					view = queryViewService.getQueryView( wi.getViewId() );
//					if( view == null ){
//						check = false;
//						Exception exception = new ExceptionViewInfoNotExists( wi.getViewId() );
//						result.error( exception );
//					}
//				} catch (Exception e) {
//					check = false;
//					Exception exception = new ExceptionCategoryInfoProcess( e, "ID로 데이터 뷰 정보 객체를 조회하는 중 예외가 발생했습니다. VIEWID:" + wi.getViewId() );
//					result.error( exception );
//					logger.error( e, effectivePerson, request, null);
//				}
//			}
//		}
		
		if( check ){
			try {
				categoryInfoServiceAdv.bindImportViewId( categoryInfo, view, wi.getViewAppId() );

				CacheManager.notify(AppInfo.class);
				CacheManager.notify(CategoryInfo.class);
				
				Wo wo = new Wo();
				wo.setId(categoryId);
				result.setData(wo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionCategoryInfoProcess( e, "분류에 가져오기 목록 ID를 연결하는 중 오류가 발생했습니다. category ID:" + categoryId + ", bind view ID:" + view.getId() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		
		return result;
	}
	public static class Wi {

		@FieldDescribe("데이터 뷰 앱 ID")
		private String viewAppId = null;
		
		@FieldDescribe("데이터 뷰 ID")
		private String viewId = null;

		public String getViewAppId() {
			return viewAppId;
		}

		public String getViewId() {
			return viewId;
		}

		public void setViewAppId(String viewAppId) {
			this.viewAppId = viewAppId;
		}

		public void setViewId(String viewId) {
			this.viewId = viewId;
		}
	}
	
	public static class Wo extends WoId {

	}
}