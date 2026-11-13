package com.x.processplatform.assemble.surface.jaxrs.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Applications;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.JsonArrayOperator;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.x_processplatform_service_processing;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.JobControlBuilder;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.core.express.service.processing.jaxrs.data.DataWi;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateArrayDataWithJob extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionUpdateArrayDataWithJob.class);
	private static final String OPERATE_ADD = "add";
	private static final String OPERATE_DELETE = "delete";
	private static final String OPERATE_MOVE = "move";

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String job, JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}, job:{}.", effectivePerson::getDistinguishedName, () -> job);
		ActionResult<Wo> result = new ActionResult<>();
		if (null == jsonElement || (!jsonElement.isJsonObject())) {
			throw new ExceptionNotJsonObject();
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(wi.getData() != null && (!jsonElement.isJsonObject())){
			throw new ExceptionNotJsonObject();
		}
		if(StringUtils.isEmpty(wi.getPath())){
			throw new ExceptionFieldEmpty("path");
		}
		if(StringUtils.isEmpty(wi.getMethod())){
			throw new ExceptionFieldEmpty("method");
		}
		JsonElement data;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Control control = new JobControlBuilder(effectivePerson, business, job).enableAllowSave().build();
			if (BooleanUtils.isNotTrue(control.getAllowSave())) {
				throw new ExceptionAccessDenied(effectivePerson, job);
			}
			data = this.convert(business, job, wi);
		}
		DataWi dataWi = new DataWi(effectivePerson.getDistinguishedName(), data);
		Wo wo = ThisApplication.context().applications().putQuery(x_processplatform_service_processing.class,
				Applications.joinQueryUri("data", "job", job), dataWi, job).getData(
				Wo.class);
		result.setData(wo);
		return result;
	}

	private JsonElement convert(Business business, String job, Wi wi) throws Exception {
		JsonElement data = this.getData(business, job);
		JsonElement oldData = XGsonBuilder.extract(data, wi.getPath());
		if(oldData!=null && !oldData.isJsonArray()){
			throw new ExceptionCustom("지정한 path 경로의 데이터가 배열 객체가 아닙니다");
		}
		JsonArray arrayData = oldData == null ? new JsonArray() : oldData.getAsJsonArray();
		switch (wi.getMethod()) {
		case OPERATE_ADD:
			arrayData = JsonArrayOperator.insert(arrayData, wi.getIndex(), wi.getData().getAsJsonObject());
			break;
		case OPERATE_DELETE:
			arrayData = JsonArrayOperator.remove(arrayData, wi.getIndex());
			break;
		case OPERATE_MOVE:
			arrayData = JsonArrayOperator.move(arrayData, wi.getIndex(), wi.getToIndex());
			break;
		default:
			throw new ExceptionCustom("작업 유형이 일치하지 않습니다: " + wi.getMethod());
		}
		JsonObject newData = new JsonObject();
		this.addJsonToPath(newData, data, wi.getPath(), arrayData);
		return newData;
	}

	private void addJsonToPath(JsonObject root, JsonElement data, String path, JsonElement value) {
		XGsonBuilder.setValueToPath(data, path, value);
		String[] pathParts = StringUtils.split(path, PATH_DOT);
		root.add(pathParts[0], XGsonBuilder.extract(data, pathParts[0]));
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("배열 조작 방법: add|delete|move")
		private String method;
		@FieldDescribe("추가할 배열 인덱스, 삭제할 배열 인덱스 또는 이동 시작 인덱스")
		private Integer index;
		@FieldDescribe("이동 대상 인덱스")
		private Integer toIndex;
		@FieldDescribe("업무 데이터에서 데이터 객체의 path 경로, 예: datatable.data")
		private String path;
		@FieldDescribe("json 데이터 객체")
		@FieldTypeDescribe(fieldType = "class", fieldTypeName = "JsonElement", fieldValue = "{}")
		private JsonElement data;

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public Integer getIndex() {
			return index == null ? 0 : index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

		public Integer getToIndex() {
			return toIndex == null ? 0 : toIndex;
		}

		public void setToIndex(Integer toIndex) {
			this.toIndex = toIndex;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = -2942168134266650614L;

	}

}
