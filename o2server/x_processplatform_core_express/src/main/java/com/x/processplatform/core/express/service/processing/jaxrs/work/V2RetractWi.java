package com.x.processplatform.core.express.service.processing.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2RetractWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4469000409900931079L;

	@FieldDescribe("업무 로그.")
	private String workLog;

	@FieldDescribe("완료된 업무 문서 식별자.")
	private String taskCompleted;

	public String getTaskCompleted() {
		return taskCompleted;
	}

	public void setTaskCompleted(String taskCompleted) {
		this.taskCompleted = taskCompleted;
	}

	public String getWorkLog() {
		return workLog;
	}

	public void setWorkLog(String workLog) {
		this.workLog = workLog;
	}

}