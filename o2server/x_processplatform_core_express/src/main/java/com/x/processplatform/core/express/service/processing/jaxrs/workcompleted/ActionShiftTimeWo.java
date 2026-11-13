package com.x.processplatform.core.express.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.jaxrs.WrapBoolean;

public class ActionShiftTimeWo extends WrapBoolean {

	private static final long serialVersionUID = 2040132891703254119L;

	@FieldDescribe("완료된 업무 문서 식별자")
	private String id;

	@FieldDescribe("조정 분 수")
	private Integer adjustMinutes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAdjustMinutes() {
		return adjustMinutes;
	}

	public void setAdjustMinutes(Integer adjustMinutes) {
		this.adjustMinutes = adjustMinutes;
	}

}
