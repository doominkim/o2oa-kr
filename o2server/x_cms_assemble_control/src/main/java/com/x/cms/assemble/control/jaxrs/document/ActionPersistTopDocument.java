package com.x.cms.assemble.control.jaxrs.document;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.core.entity.Document;

public class ActionPersistTopDocument extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistTopDocument.class);

	@AuditLog(operation = "문서 상단 고정")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;

		if (check) {
			try {
				document = documentQueryService.get( id );
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
					throw exception;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 정보를 가져오는 중 오류가 발생했습니다. Id:" + id + ", Name:" + effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {				
				documentPersistService.topDocument( document.getId() );
				
				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );				
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "문서를 상단 고정하는 중 오류가 발생했습니다. Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}
		
		if (check) {
			new CmsBatchOperationPersistService().addOperation( 
					CmsBatchOperationProcessService.OPT_OBJ_DOCUMENT, 
					CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  document.getId(),  document.getId(), "문서 권한 갱신: ID=" +  document.getId() );
		}

		CacheManager.notify( Document.class );

		return result;
	}
	
	public static class Wo extends WoId {

	}
}