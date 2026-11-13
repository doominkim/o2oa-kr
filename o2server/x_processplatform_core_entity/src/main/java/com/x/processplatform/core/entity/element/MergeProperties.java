package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class MergeProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("활동 사용자 정의 데이터")
	private JsonElement customData;

	@FieldDescribe("병합 단계, 0 최대한 병합, 1 1단계 병합, 2 2단계 이하 병합, 3 3단계 이하 병합.")
	private Integer mergeLayerThreshold;

	public Integer getMergeLayerThreshold() {
		return mergeLayerThreshold;
	}

	public void setMergeLayerThreshold(Integer mergeLayerThreshold) {
		this.mergeLayerThreshold = mergeLayerThreshold;
	}

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

}
