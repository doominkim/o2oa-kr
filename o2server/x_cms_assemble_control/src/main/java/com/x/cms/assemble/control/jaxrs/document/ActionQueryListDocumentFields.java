package com.x.cms.assemble.control.jaxrs.document;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.cms.core.entity.Document;

public class ActionQueryListDocumentFields extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setFieldNames( Arrays.asList( Document.documentFieldNames ) );
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

		@FieldDescribe( "목록에 사용할 수 있는 문서 객체의 열 이름." )
		private List<String> fieldNames = null;

		public List<String> getFieldNames() {
			return fieldNames;
		}

		public void setFieldNames(List<String> fieldNames) {
			this.fieldNames = fieldNames;
		}		
	}
}