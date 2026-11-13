package com.x.query.assemble.surface.jaxrs.view;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.MD5Tool;
import com.x.query.assemble.surface.Business;
import com.x.query.assemble.surface.ThisApplication;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;
import com.x.query.core.express.plan.FilterEntry;
import com.x.query.core.express.plan.Runtime;
import com.x.query.core.express.plan.SelectEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.list.TreeList;

class ActionBundleV2 extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionBundleV2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id, JsonElement jsonElement) throws Exception {
		logger.debug("jsonElement:{}.", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		View view;
		Runtime runtime;
		Business business;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			view = business.pick(id, View.class);
			if (null == view) {
				throw new ExceptionEntityNotExist(id, View.class);
			}
			Query query = business.pick(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(view.getQuery(), Query.class);
			}
			if (!business.readable(effectivePerson, view)) {
				throw new ExceptionAccessDenied(effectivePerson, view);
			}
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			if (null == wi) {
				wi = new Wi();
			}
			runtime = this.runtime(effectivePerson, business, view, wi.getFilterList(), wi.getOrderList(), wi.getParameter(),
					wi.getSize(), false);
			runtime.page = wi.getPage();
		}
		Pair<List<String>, Long> pair = this.fetchBundleV2(view, runtime, ThisApplication.forkJoinPool());
		Wo wo = new Wo();
		wo.setValueList(pair.first());
		wo.setCount(pair.second());
		wo.setKey(MD5Tool.getMD5Str(effectivePerson.getDistinguishedName() + Config.token().getCipher()));
		result.setData(wo);
		result.setCount(pair.second());
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("프런트 지정 정렬 열")
		private List<SelectEntry> orderList = new TreeList<>();

		@FieldDescribe("필터")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "com.x.query.core.express.plan.FilterEntry", fieldValue = "{value='',otherValue='',path='',formatType='',logic='',comparison=''}", fieldSample = "{'logic':'논리 연산:and|or','path':'data 데이터 경로:$work.title','comparison':'비교 연산자:equals|notEquals|like|notLike|greaterThan|greaterThanOrEqualTo|lessThan|lessThanOrEqualTo|range','value':'7월','formatType':'textValue|numberValue|dateTimeValue|booleanValue'}")
		private List<FilterEntry> filterList = new TreeList<>();

		@FieldDescribe("파라미터")
		private Map<String, String> parameter = new HashMap<>();

		@FieldDescribe("페이지당 개수")
		private Integer size = 0;

		@FieldDescribe("페이지 번호")
		private Integer page = 0;

		public List<SelectEntry> getOrderList() {
			return orderList;
		}

		public void setOrderList(List<SelectEntry> orderList) {
			this.orderList = orderList;
		}

		public List<FilterEntry> getFilterList() {
			return filterList;
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

		public Integer getSize() {
			return size;
		}

		public void setSize(Integer size) {
			this.size = size;
		}

		public Integer getPage() {
			return page;
		}

		public void setPage(Integer page) {
			this.page = page;
		}
	}

	public static class Wo extends WrapStringList {

		@FieldDescribe("execute 접근 키 문자열.")
		private String key;

		@FieldDescribe("총량.")
		private Long count;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Long getCount() {
			return count;
		}

		public void setCount(Long count) {
			this.count = count;
		}
	}

}
