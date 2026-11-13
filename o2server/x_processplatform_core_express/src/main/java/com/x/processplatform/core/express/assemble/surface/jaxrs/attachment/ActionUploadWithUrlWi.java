package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionUploadWithUrlWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6298368255152314235L;

	@FieldDescribe("*Work 또는 WorkCompleted의 id.")
	@Schema(description = "*Work 또는 WorkCompleted의 id.")
	private String workId;

	@FieldDescribe("*파일 이름, 확장자를 포함한 파일 이름.")
	@Schema(description = "*파일 이름, 확장자를 포함한 파일 이름.")
	private String fileName;

	@FieldDescribe("*첨부파일 원본 url 주소.")
	@Schema(description = "*첨부파일 원본 url 주소.")
	private String fileUrl;

	@FieldDescribe("*첨부파일 분류.")
	@Schema(description = "*첨부파일 분류.")
	private String site;

	@FieldDescribe("업로드 사용자(관리자에게만 적용).")
	@Schema(description = "업로드 사용자(관리자에게만 적용).")
	private String person;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}
}