package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionHtmlToPdfWi extends GsonPropertyObject {

	private static final long serialVersionUID = 8513838302143557434L;

	@FieldDescribe("변환할 html.")
	@Schema(description = "변환할 html.")
	private String workHtml;

	@FieldDescribe("pdf 변환 페이지 너비, 기본값 A4.")
	@Schema(description = "pdf 변환 페이지 너비, 기본값 A4.")
	private Float pageWidth;

	@FieldDescribe("pdf 제목.")
	@Schema(description = "pdf 제목.")
	private String title;

	public String getWorkHtml() {
		return workHtml;
	}

	public void setWorkHtml(String workHtml) {
		this.workHtml = workHtml;
	}

	public Float getPageWidth() {
		return pageWidth;
	}

	public void setPageWidth(Float pageWidth) {
		this.pageWidth = pageWidth;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}