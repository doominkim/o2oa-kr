package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V3RetractWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5176027793859273796L;

	@FieldDescribe(" 완료된 업무 문서 식별자")
	private String taskCompleted;

	@FieldDescribe("회수 결재 대기 식별자")
	private List<String> retractTaskList;

	public List<String> getRetractTaskList() {
		return retractTaskList;
	}

	public void setRetractTaskList(List<String> retractTaskList) {
		this.retractTaskList = retractTaskList;
	}

	public String getTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(String taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

}
