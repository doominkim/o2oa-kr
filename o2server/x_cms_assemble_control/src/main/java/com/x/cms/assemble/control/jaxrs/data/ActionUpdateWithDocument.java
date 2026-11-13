package com.x.cms.assemble.control.jaxrs.data;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.Document;
import java.util.Date;

class ActionUpdateWithDocument extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/** 빈 데이터 제출로 data가 비워지는 것을 방지 */
			if (jsonElement.isJsonNull()) {
				throw new ExceptionNullData();
			}
			if (jsonElement.isJsonArray()) {
				throw new ExceptionArrayData();
			}
			if (jsonElement.isJsonPrimitive()) {
				throw new ExceptionPrimitiveData();
			}
			if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().entrySet().isEmpty()) {
				throw new ExceptionEmptyData();
			}
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Document document = emc.find(id, Document.class);
			if (null == document) {
				throw new ExceptionDocumentNotExists(id);
			}
			if (!business.isDocumentEditor(effectivePerson, null, null, document)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			JsonElement source = getData(business, id);
			JsonElement merge = XGsonBuilder.merge(jsonElement, source);
			/** title과 serial을 먼저 수정한 뒤 DataItem 수정, 기존 DataItem에도 title과 serial 데이터가 있기 때문. */
			this.updateTitleSerialObjectSecurityClearance(business, document, merge);
			this.updateData(business, document, merge);
			emc.beginTransaction(Document.class);
			document.setModifyTime(new Date());
			emc.commit();
			Wo wo = new Wo();
			wo.setId(document.getId());
			result.setData(wo);

			CacheManager.notify( Document.class );

			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
