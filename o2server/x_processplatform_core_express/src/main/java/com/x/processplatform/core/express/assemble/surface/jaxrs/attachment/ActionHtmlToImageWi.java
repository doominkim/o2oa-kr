package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionHtmlToImageWi extends GsonPropertyObject {

	private static final long serialVersionUID = 8371465627589051014L;

	@FieldDescribe("*변환할 html.")
	@Schema(description = "*변환할 html.")
	private String workHtml;
	@FieldDescribe("이미지 제목")
	@Schema(description = "이미지 제목")
	private String title;
	@FieldDescribe("html 본문 너비, 비워 둘 수 있음.")
	@Schema(description = "html 본문 너비, 비워 둘 수 있음.")
	private Double htmlWidth;
	@FieldDescribe("html 본문 높이, 비워 둘 수 있음.")
	@Schema(description = "html 본문 높이, 비워 둘 수 있음.")
	private Double htmlHeight;
	@FieldDescribe("html의 X축 시작 위치, 비워 둘 수 있음.")
	@Schema(description = "html의 X축 시작 위치, 비워 둘 수 있음.")
	private Double startX;
	@FieldDescribe("html의 Y축 시작 위치, 비워 둘 수 있음.")
	@Schema(description = "html의 Y축 시작 위치, 비워 둘 수 있음.")
	private Double startY;
	@FieldDescribe("배경 투명 여부, 기본값 false.")
	@Schema(description = "배경 투명 여부, 기본값 false.")
	private Boolean omitBackground;
	@FieldDescribe("업무 문서 식별자, 이미지를 업무 문서의 첨부파일로 저장, 선택 항목")
	@Schema(description = "업무 문서 식별자, 이미지를 업무 문서의 첨부파일로 저장, 선택 항목")
	private String workId;
	@FieldDescribe("위치, 업무 문서 식별자가 있을 때 필수")
	@Schema(description = "위치, 업무 문서 식별자가 있을 때 필수")
	private String site;

	public String getWorkHtml() {
		return workHtml;
	}

	public void setWorkHtml(String workHtml) {
		this.workHtml = workHtml;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public Double getStartX() {
		return startX == null ? 0D : startX;
	}

	public void setStartX(Double startX) {
		this.startX = startX;
	}

	public Double getStartY() {
		return startY == null ? 0D : startX;
	}

	public void setStartY(Double startY) {
		this.startY = startY;
	}

	public Double getHtmlWidth() {
		return htmlWidth;
	}

	public void setHtmlWidth(Double htmlWidth) {
		this.htmlWidth = htmlWidth;
	}

	public Double getHtmlHeight() {
		return htmlHeight;
	}

	public void setHtmlHeight(Double htmlHeight) {
		this.htmlHeight = htmlHeight;
	}

	public Boolean getOmitBackground() {
		return omitBackground;
	}

	public void setOmitBackground(Boolean omitBackground) {
		this.omitBackground = omitBackground;
	}
}