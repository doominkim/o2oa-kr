package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RerouteWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6729193803512068864L;

	@FieldDescribe("활동 노드")
	private String activity;

	@FieldDescribe("모든 work 병합 여부")
	private Boolean mergeWork;

	@FieldDescribe("수동 활동 강제 처리자")
	private List<String> distinguishedNameList = new ArrayList<>();

	public Boolean getMergeWork() {
		return BooleanUtils.isTrue(mergeWork);
	}

	public List<String> getDistinguishedNameList() {
		if (null == distinguishedNameList) {
			this.distinguishedNameList = new ArrayList<>();
		}
		return this.distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setMergeWork(Boolean mergeWork) {
		this.mergeWork = mergeWork;
	}

}