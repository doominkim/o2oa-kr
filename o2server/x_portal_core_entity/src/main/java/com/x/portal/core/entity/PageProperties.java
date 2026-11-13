package com.x.portal.core.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class PageProperties extends JsonProperties {

	@FieldDescribe("연결 Widget.")
	private List<String> relatedWidgetList = new ArrayList<String>();

	@FieldDescribe("모바일 연결 Widget.")
	private List<String> mobileRelatedWidgetList = new ArrayList<String>();

	@FieldDescribe("연결 스크립트.")
	private Map<String, String> relatedScriptMap = new LinkedHashMap<String, String>();

	@FieldDescribe("모바일 연결 스크립트.")
	private Map<String, String> mobileRelatedScriptMap = new LinkedHashMap<String, String>();

	public List<String> getRelatedWidgetList() {
		return this.relatedWidgetList == null ? new ArrayList<String>() : this.relatedWidgetList;
	}

	public List<String> getMobileRelatedWidgetList() {
		return this.mobileRelatedWidgetList == null ? new ArrayList<String>() : this.mobileRelatedWidgetList;
	}

	public Map<String, String> getRelatedScriptMap() {
		return this.relatedScriptMap == null ? new LinkedHashMap<String, String>() : this.relatedScriptMap;
	}

	public Map<String, String> getMobileRelatedScriptMap() {
		return this.mobileRelatedScriptMap == null ? new LinkedHashMap<String, String>() : this.mobileRelatedScriptMap;
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

}
