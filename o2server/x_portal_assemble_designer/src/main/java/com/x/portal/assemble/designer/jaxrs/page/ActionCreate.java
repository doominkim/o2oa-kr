package com.x.portal.assemble.designer.jaxrs.page;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.ThisApplication;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.PageVersion;
import com.x.portal.core.entity.Portal;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ActionCreate extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Portal portal = emc.find(wi.getPortal(), Portal.class);
			if (!business.editable(effectivePerson, portal)) {
				throw new InsufficientPermissionException(effectivePerson.getDistinguishedName());
			}
			emc.beginTransaction(Page.class);
			Page page = Wi.copier.copy(wi);
			this.checkName(business, page);
			this.checkAlias(business, page);
			page.getProperties().setRelatedWidgetList(wi.getRelatedWidgetList());
			page.getProperties().setMobileRelatedWidgetList(wi.getMobileRelatedWidgetList());
			page.getProperties().setRelatedScriptMap(wi.getRelatedScriptMap());
			page.getProperties().setMobileRelatedScriptMap(wi.getMobileRelatedScriptMap());
			emc.persist(page, CheckPersistType.all);
			emc.beginTransaction(Portal.class);
			/** 홈 페이지 갱신 */
			if (this.isBecomeFirstPage(business, portal, page)) {
				portal.setFirstPage(page.getId());
			} else if (StringUtils.isEmpty(portal.getFirstPage())
					|| (null == emc.find(portal.getFirstPage(), Page.class))) {
				/* 첫 페이지면 이 페이지를 현재 페이지로 설정 */
				portal.setFirstPage(page.getId());
			}
			if(StringUtils.isNotBlank(wi.getCornerMarkScript()) && StringUtils.isNotBlank(wi.getCornerMarkScriptText())) {
				portal.setCornerMarkScript(wi.getCornerMarkScript());
				portal.setCornerMarkScriptText(wi.getCornerMarkScriptText());
			}
			emc.commit();
			CacheManager.notify(Page.class);
			CacheManager.notify(Portal.class);
			// 이력 버전 저장
			ThisApplication.pageVersionQueue.send(new PageVersion(page.getId(), jsonElement, effectivePerson.getDistinguishedName()));
			Wo wo = new Wo();
			wo.setId(page.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends Page {

		private static final long serialVersionUID = 6624639107781167248L;

		static WrapCopier<Wi, Page> copier = WrapCopierFactory.wi(Wi.class, Page.class, null,
				JpaObject.FieldsUnmodifyExcludeId);

		@FieldDescribe("연결 Widget.")
		private List<String> relatedWidgetList = new ArrayList<>();

		@FieldDescribe("모바일 연결 Widget.")
		private List<String> mobileRelatedWidgetList = new ArrayList<>();

		@FieldDescribe("연결 스크립트.")
		private Map<String, String> relatedScriptMap = new LinkedHashMap<>();

		@FieldDescribe("모바일 연결 스크립트.")
		private Map<String, String> mobileRelatedScriptMap = new LinkedHashMap<>();

		@FieldDescribe("배지 연결 스크립트.")
		private String cornerMarkScript;

		@FieldDescribe("배지 스크립트 텍스트.")
		private String cornerMarkScriptText;

		public List<String> getRelatedWidgetList() {
			return this.relatedWidgetList == null ? new ArrayList<>() : this.relatedWidgetList;
		}

		public List<String> getMobileRelatedWidgetList() {
			return this.mobileRelatedWidgetList == null ? new ArrayList<>() : this.mobileRelatedWidgetList;
		}

		public Map<String, String> getRelatedScriptMap() {
			return this.relatedScriptMap == null ? new LinkedHashMap<>() : this.relatedScriptMap;
		}

		public Map<String, String> getMobileRelatedScriptMap() {
			return this.mobileRelatedScriptMap == null ? new LinkedHashMap<>() : this.mobileRelatedScriptMap;
		}

		public void setRelatedWidgetList(List<String> relatedWidgetList) {
			this.relatedWidgetList = relatedWidgetList;
		}

		public void setMobileRelatedWidgetList(List<String> mobileRelatedWidgetList) {
			this.mobileRelatedWidgetList = mobileRelatedWidgetList;
		}

		public void setRelatedScriptMap(Map<String, String> relatedScriptMap) {
			this.relatedScriptMap = relatedScriptMap;
		}

		public void setMobileRelatedScriptMap(Map<String, String> mobileRelatedScriptMap) {
			this.mobileRelatedScriptMap = mobileRelatedScriptMap;
		}

		public String getCornerMarkScript() {
			return cornerMarkScript;
		}

		public void setCornerMarkScript(String cornerMarkScript) {
			this.cornerMarkScript = cornerMarkScript;
		}

		public String getCornerMarkScriptText() {
			return cornerMarkScriptText;
		}

		public void setCornerMarkScriptText(String cornerMarkScriptText) {
			this.cornerMarkScriptText = cornerMarkScriptText;
		}
	}

	public static class Wo extends WoId {

	}

}
