package com.x.processplatform.core.express.assemble.surface.jaxrs.workcompleted;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2ExtendTaskWi extends GsonPropertyObject {

	private static final long serialVersionUID = -3293122515327864483L;

	@FieldDescribe("신분")
	private List<String> identityList = new ArrayList<>();

	@FieldDescribe("교체 실행 여부")
	private Boolean replace;

	@FieldDescribe("업무 문서 식별자")
	private String work;
	
	@FieldDescribe("위치 지정 신분 식별자")
	private String identity;

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public List<String> getIdentityList() {
		return identityList;
	}

	public void setIdentityList(List<String> identityList) {
		this.identityList = identityList;
	}

	public Boolean getReplace() {
		return BooleanUtils.isTrue(replace);
	}

	public void setReplace(Boolean replace) {
		this.replace = replace;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

}