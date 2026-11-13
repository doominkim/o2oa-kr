package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionUpdateContentWi extends GsonPropertyObject {

	private static final long serialVersionUID = -4802496811328343606L;

	@FieldDescribe("파일 이름, 확장자를 제외한 파일 이름.")
	@Schema(description = "파일 이름, 확장자를 제외한 파일 이름.")
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
