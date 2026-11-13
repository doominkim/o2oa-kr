package com.x.correlation.core.express.service.processing.jaxrs.correlation;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.List;

public class SiteTargetWi extends GsonPropertyObject {

	private static final long serialVersionUID = -5876504008709428039L;

	@FieldDescribe("연관 콘텐츠 박스 식별자.")
	private String site;

	@FieldDescribe("연결 대상.")
	private List<TargetWi> targetList;

	public List<TargetWi> getTargetList() {
		return targetList;
	}

	public void setTargetList(List<TargetWi> targetList) {
		this.targetList = targetList;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
}
