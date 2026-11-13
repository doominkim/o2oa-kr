package com.x.query.assemble.surface.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Plan;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.SelectEntries;
import com.x.query.core.express.plan.SelectEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

class ActionExecuteWithQueryV2 extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionExecuteWithQueryV2.class);

	ActionResult<Plan> execute(EffectivePerson effectivePerson, String flag, String queryFlag,
			Integer page, Integer size, JsonElement jsonElement) throws Exception {
		logger.debug("jsonElement:{}.", jsonElement);
		ActionResult<Plan> result = new ActionResult<>();
		page = (page == null || page < 1) ? 1 : page;
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		View view;
		Runtime runtime;
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			Query query = business.pick(queryFlag, Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(queryFlag, Query.class);
			}
			view = business.view().getWithQueryObject(flag, query);
			if (null == view) {
				throw new ExceptionEntityNotExist(flag, View.class);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getOrderList(),
					wi.getParameter(), size, true);
			runtime.page = page;
			runtime.hasBundle = true;
			runtime.selectList = wi.getSelectList();
		}
		Pair<List<String>, Long> pair;
		if(StringUtils.isBlank(wi.getSearchKey())) {
			pair = this.fetchBundleV2(view, runtime,
					ThisApplication.forkJoinPool());
		}else{
			ExecuteV2Search search = new ExecuteV2Search();
			pair = search.search(view, runtime, wi.getSearchKey(), page, size);
		}
		runtime.bundleList = pair.first();
		Plan plan = this.accessPlan(business, view, runtime, ThisApplication.forkJoinPool());
		result.setData(plan);
		result.setCount(pair.second());
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("프런트 지정 정렬 열")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "SelectEntry", fieldValue = "{\"orderType\": \"\",\"column\": \"\",\"displayName\": \"\",\"path\": \"\"}")
		private List<SelectEntry> orderList;

		@FieldDescribe("필터")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "FilterEntry", fieldValue = "{value='',otherValue='',path='',formatType='',logic='',comparison=''}", fieldSample = "{'logic':'논리 연산:and|or','path':'data 데이터 경로:$work.title','comparison':'비교 연산자:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo|range','value':'7월','formatType':'textValue|numberValue|dateTimeValue|booleanValue'}")
		private List<FilterEntry> filterList;

		@FieldDescribe("지정 필드 목록")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "SelectEntry", fieldValue = "{\"orderType\": \"\",\"column\": \"\",\"displayName\": \"\",\"path\": \"\"}")
		private SelectEntries selectList;

		@FieldDescribe("파라미터")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("유사 검색 내용.")
		private String searchKey;

		public List<FilterEntry> getFilterList() {
			return filterList == null ? new TreeList<>() : filterList;
		}

		public void setFilterList(List<FilterEntry> filterList) {
			this.filterList = filterList;
		}

		public Map<String, String> getParameter() {
			return parameter;
		}

		public void setParameter(Map<String, String> parameter) {
			this.parameter = parameter;
		}

		public String getSearchKey() {
			return searchKey;
		}

		public void setSearchKey(String searchKey) {
			this.searchKey = searchKey;
		}

		public List<SelectEntry> getOrderList() {
			return orderList == null ? new TreeList<>() : orderList;
		}

		public void setOrderList(List<SelectEntry> orderList) {
			this.orderList = orderList;
		}

		public SelectEntries getSelectList() {
			return selectList;
		}

		public void setSelectList(SelectEntries selectList) {
			this.selectList = selectList;
		}
	}

}
