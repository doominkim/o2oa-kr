package com.x.cms.assemble.control.jaxrs.script;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Log;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.ScriptVersion;

import java.util.Date;
import java.util.List;

class ActionUpdate extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			e.printStackTrace();
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Script script = emc.find(id, Script.class);
				if (null == script) {
					throw new ExceptionEntityNotExist(id);
				}
				AppInfo appInfo = emc.find(script.getAppId(), AppInfo.class);
				if (null == appInfo) {
					throw new ExceptionAppInfoNotExists(script.getAppId());
				}
				Business business = new Business(emc);
				if (!business.isAppInfoManager(effectivePerson, appInfo)) {
					throw new ExceptionAccessDenied(effectivePerson);
				}
				emc.beginTransaction(Script.class);
				wrapIn.copyTo(script, JpaObject.ID_DISTRIBUTEFACTOR);
				script.setLastUpdatePerson(effectivePerson.getDistinguishedName());
				script.setLastUpdateTime(new Date());
				emc.commit();
				// 모든 Script 캐시 삭제
				CacheManager.notify(Script.class);
				// 로그 기록
				emc.beginTransaction(Log.class);
				logService.log(emc, effectivePerson.getDistinguishedName(), script.getName(), script.getAppId(), "", "",
						script.getId(), "SCRIPT", "更新");
				emc.commit();

				Wo wo = new Wo();
				wo.setId(script.getId());
				result.setData(wo);

				// 이력 버전 저장
				ThisApplication.scriptVersionQueue.send(new ScriptVersion(wo.getId(), jsonElement, effectivePerson.getDistinguishedName()));
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		return result;
	}

	public class Wi extends GsonPropertyObject {

		@FieldDescribe("생성 시간.")
		private Date createTime;

		@FieldDescribe("更新时间.")
		private Date updateTime;

		@FieldDescribe("ID.")
		private String id;

		@FieldDescribe("스크립트 이름.")
		private String name;

		@FieldDescribe("스크립트 별칭.")
		private String alias;

		@FieldDescribe("스크립트 설명.")
		private String description;

		@FieldDescribe("검증 성공 여부.")
		private Boolean validated;

		@FieldDescribe("소속 게시판 ID.")
		private String appId;

		@FieldDescribe("스크립트 내용.")
		private String text;

		@FieldDescribe("의존 스크립트 ID 목록.")
		private List<String> dependScriptList;

		public Date getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Date createTime) {
			this.createTime = createTime;
		}

		public Date getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Date updateTime) {
			this.updateTime = updateTime;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAlias() {
			return alias;
		}

		public void setAlias(String alias) {
			this.alias = alias;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Boolean getValidated() {
			return validated;
		}

		public void setValidated(Boolean validated) {
			this.validated = validated;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public List<String> getDependScriptList() {
			return dependScriptList;
		}

		public void setDependScriptList(List<String> dependScriptList) {
			this.dependScriptList = dependScriptList;
		}

	}

	public static class Wo extends WoId {

	}
}
