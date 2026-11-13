package com.x.cms.assemble.control.jaxrs.document;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionQueryListUnReadDocIds extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListUnReadDocIds.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		List<String> nonReadIds = null;
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
		}

		if (check) {	
			//ID 범위 내에서 사용자가 열람한 문서ID 목록 조회
			try {
				nonReadIds = documentViewRecordServiceAdv.listReadDocId( wi.getIds(), effectivePerson );
				
				//차집합 계산
				wi.getIds().removeAll( nonReadIds );
				wo.setUnReadDocIds( wi.getIds() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess( e, "지정한 ID 목록에서 읽지 않은 게시글 ID 목록을 조회하는 중 오류가 발생했습니다!" );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData( wo );
		return result;
	}

	public static class Wi {
		
		@FieldDescribe( "열람 여부를 조회할 문서ID 목록." )
		private List<String> ids = null;

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "미열람 문서ID 목록." )
		private List<String> unReadDocIds = null;

		public List<String> getUnReadDocIds() {
			return unReadDocIds;
		}

		public void setUnReadDocIds(List<String> unReadDocIds) {
			this.unReadDocIds = unReadDocIds;
		}
	}
}