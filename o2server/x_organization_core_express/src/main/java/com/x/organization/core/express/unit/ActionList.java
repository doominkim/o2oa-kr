package com.x.organization.core.express.unit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionList extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection, Boolean useNameFind) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getUnitList().addAll(collection);
		}
		Wo wo = context.applications().postQuery(applicationClass, "unit/list", wi).getData(Wo.class);
		return wo.getUnitList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("조직")
		private List<String> unitList = new ArrayList<>();

		@FieldDescribe("이름으로 조회할지 여부, 기본값 false")
		private Boolean useNameFind = false;

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public Boolean getUseNameFind() {
			return useNameFind;
		}

		public void setUseNameFind(Boolean useNameFind) {
			this.useNameFind = useNameFind;
		}
	}

	public static class Wo extends WoUnitListAbstract {
	}
}
