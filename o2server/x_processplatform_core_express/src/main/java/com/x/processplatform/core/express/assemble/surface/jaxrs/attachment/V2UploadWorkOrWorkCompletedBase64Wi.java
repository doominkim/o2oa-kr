package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class V2UploadWorkOrWorkCompletedBase64Wi extends GsonPropertyObject {

	private static final long serialVersionUID = 5544072979836413166L;

	@FieldDescribe("첨부파일 위치.")
	@Schema(description = "첨부파일 위치.")
	private String site;

	@FieldDescribe("첨부파일 내용, base64 인코딩 텍스트.")
	@Schema(description = "첨부파일 내용, base64 인코딩 텍스트.")
	private String content;

	@FieldDescribe("파일 이름.")
	@Schema(description = "파일 이름.")
	private String fileName;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}