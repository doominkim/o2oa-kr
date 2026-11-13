package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;

public class ActionPersistChangeCategory extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistChangeCategory.class);

	@AuditLog(operation = "移动文档")
	protected ActionResult<Wo> execute(HttpServletRequest request, JsonElement jsonElement, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		List<Document> documents = null;
		List<String> ids = null;
		String categoryId = null;
		CategoryInfo categoryInfo = null;
		Boolean check = true;
		Wo wo = new Wo();
		Wi wi = null;

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다.");
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if( ListTools.isEmpty( wi.getIds() )) {
				check = false;
				Exception exception = new ExceptionDocumentIdEmpty();
				result.error( exception );
			}
			ids = wi.getIds();
		}
		
		if (check) {
			if( StringUtils.isEmpty( wi.getCategoryId() )) {
				check = false;
				Exception exception = new ExceptionCategoryIdEmpty();
				result.error( exception );
			}
			categoryId = wi.getCategoryId();
		}

		if (check) {	
			//먼저 분류 존재 여부 조회
			try {
				categoryInfo = categoryInfoServiceAdv.get( categoryId );
				if (categoryInfo == null) {
					check = false;
					Exception exception = new ExceptionCategoryInfoNotExists(categoryId);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "ID로 분류 정보를 조회하는 중 오류가 발생했습니다! ID: " + categoryId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {	
			//먼저 분류 존재 여부 조회
			wo.setTotal( ids.size() );
			try {
				documents = documentQueryService.list( ids );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "ID 목록으로 게시글 정보를 조회하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if( ListTools.isNotEmpty( documents )) {
				List<String> success = new ArrayList<>();
				List<String> failture = new ArrayList<>();
				for( Document document : documents ) {					
					try {
						//각 document의 분류 정보 수정
						if ( documentPersistService.changeCategory( document, categoryInfo ) ) {
							success.add( document.getId() );
						}else {
							failture.add( document.getId() );
						}
					} catch (Exception e) {
						Exception exception = new ExceptionDocumentInfoProcess(e, "document의 분류 정보를 수정하는 중 오류가 발생했습니다! ID=" + document.getId() );
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
					
					try {
						//문서 접근 권한 재설정 메시지를 처리 큐로 발송
						new CmsBatchOperationPersistService().addOperation( 
								CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
								CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  document.getId(),  document.getId(), "刷新文档权限：ID=" +  document.getId() );
					} catch (Exception e) {
						Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 접근 권한 재설정 메시지를 처리 대기열에 보내는 중 오류가 발생했습니다!");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				wo.setFailtureList( failture );
				wo.setSuccessList( success );
				CacheManager.notify(Document.class);
			}
		}
		result.setCount(Long.parseLong(  wo.getTotal().toString() ) );
		result.setData( wo );
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "분류를 변경할 문서ID 목록." )
		private List<String> ids = null;
		
		@FieldDescribe( "대상 분류ID." )
		private String categoryId = null;

		public List<String> getIds() {
			return ids;
		}

		public String getCategoryId() {
			return categoryId;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}
	}

	public static class Wo extends GsonPropertyObject {
		
		@FieldDescribe( "분류를 변경할 문서ID 총 개수." )
		private Integer total =0;
		
		@FieldDescribe( "분류 변경에 성공한 문서ID 목록." )
		private List<String> successList = null;
		
		@FieldDescribe( "분류 변경에 실패한 문서ID 목록." )
		private List<String> failtureList = null;
		
		public List<String> getSuccessList() {
			return successList;
		}
		public List<String> getFailtureList() {
			return failtureList;
		}
		public void setSuccessList(List<String> successList) {
			this.successList = successList;
		}
		public void setFailtureList(List<String> failtureList) {
			this.failtureList = failtureList;
		}
		public Integer getTotal() {
			return total;
		}
		public void setTotal(Integer total) {
			this.total = total;
		}
	}
}