package com.x.organization.assemble.express.jaxrs.unit;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;

class ActionGetWithIdentityWithType extends BaseAction {

	/*** 지정한 신분이 속한 재귀 그룹을 찾아 지정한 level의 그룹 반환 */
	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			CacheKey cacheKey = new CacheKey(this.getClass(), wi.getIdentity(), wi.getType());
			Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
			if (optional.isPresent()) {
				result.setData((Wo) optional.get());
			} else {
				Wo wo = this.get(business, wi);
				CacheManager.put(cacheCategory, cacheKey, wo);
				result.setData(wo);
			}
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("신분")
		private String identity;

		@FieldDescribe("조직 유형")
		private String type;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("조직 식별 이름")
		private String unit;

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}

	private Wo get(Business business, Wi wi) throws Exception {
		Wo wo = new Wo();
		if (StringUtils.isNotEmpty(wi.getIdentity()) && StringUtils.isNotEmpty(wi.getType())) {
			Identity identity = business.identity().pick(wi.getIdentity());
			if (null != identity) {
				Unit unit = business.unit().pick(identity.getUnit());
				if (null != unit) {
					List<String> unitIds = business.unit().listSupNested(unit.getId());
					/** 직속 소속 조직도 검색 범위에 포함 */
					unitIds.add(unit.getId());
					List<Unit> units = business.entityManagerContainer().list(Unit.class, unitIds);
					units = business.unit().sort(units);
					for (int i = units.size() - 1; i >= 0; i--) {
						Unit o = units.get(i);
						if (o.getTypeList().contains(wi.getType())) {
							wo.setUnit(o.getDistinguishedName());
							return wo;
						}
					}
				}
			}
		}
		return wo;
	}

}
