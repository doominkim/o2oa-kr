package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import javax.servlet.http.HttpServletRequest;

public class ActionPersistPublishCancel extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionPersistPublishCancel.class);

	@AuditLog(operation = "문서 게시 상태 취소")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Document document = null;
		Boolean check = true;

		if (check) {
			try {
				document = documentQueryService.get(id);
				if (null == document) {
					check = false;
					Exception exception = new ExceptionDocumentNotExists(id);
					result.error(exception);
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
				modifyDocStatus(id, "draft", effectivePerson.getDistinguishedName());
				document.setDocStatus("draft");
				document.setPublishTime(null);
				document = documentPersistService.refreshDocInfoData( document );
				CacheManager.notify(Document.class);

				Wo wo = new Wo();
				wo.setId( document.getId() );
				result.setData( wo );

			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "게시글 상태를 게시로 변경하는 중 오류가 발생했습니다. Id:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
				logService.log(emc, effectivePerson.getDistinguishedName(),
						document.getCategoryAlias() + ":" + document.getTitle(), document.getAppId(),
						document.getCategoryId(), document.getId(), "", "DOCUMENT", "게시 취소");
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		if (check) {
			try {
				documentPersistService.refreshDocumentPermission( id, null, null );
				document = documentQueryService.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "게시된 게시글의 접근·관리 권한을 회수하는 중 오류가 발생했습니다!");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
