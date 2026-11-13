package com.x.processplatform.core.express.service.processing.jaxrs.process;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionListWithProcessWi extends GsonPropertyObject {

	private static final long serialVersionUID = -6093458504525871536L;

	@FieldDescribe("프로세스(여러 값은 쉼표로 구분).")
	@Schema(description = "프로세스(여러 값은 쉼표로 구분).")
	private List<String> processList;

	@FieldDescribe("같은 버전 프로세스 동시 조회 여부(true|false).")
	@Schema(description = "같은 버전 프로세스 동시 조회 여부(true|false).")
	private boolean includeEdition;

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public boolean isIncludeEdition() {
		return includeEdition;
	}

	public void setIncludeEdition(boolean includeEdition) {
		this.includeEdition = includeEdition;
	}
}