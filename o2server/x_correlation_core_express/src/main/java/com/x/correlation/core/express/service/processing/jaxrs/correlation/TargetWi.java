package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class TargetWi extends GsonPropertyObject {

	private static final long serialVersionUID = 395825437810549953L;

	@FieldDescribe("연관 대상 유형.")
	private String type;

	@FieldDescribe("연관 대상 식별자.")
	private String bundle;

	@FieldDescribe("연관 콘텐츠 박스 식별자.")
	private String site;

	@FieldDescribe("출처 뷰.")
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

}