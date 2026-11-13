package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.DocumentCommend;

public class ActionPersistUnCommend extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionPersistUnCommend.class);

	@AuditLog(operation = "取消文档点赞")
	protected ActionResult<Wo> execute(HttpServletRequest request, String docId, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;

		if (check) {
			try {
				List<String> ids = docCommendPersistService.delete( docId, effectivePerson.getDistinguishedName(), DocumentCommend.COMMEND_TYPE_DOCUMENT);
				Wo wo = new Wo();
				wo.setIds( ids );
				result.setData( wo );
			} catch (Exception e) {
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자와 게시글 ID로 좋아요 정보를 삭제하는 중 오류가 발생했습니다. docId:" + docId );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
				throw exception;
			}
		}

		CacheManager.notify( Document.class );

		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private List<String> ids = null;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}


	}
}
