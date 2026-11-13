package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionDocToWordWorkOrWorkCompletedWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5681105204114768105L;

	@FieldDescribe("변환 파일명.")
	@Schema(description = "변환 파일명.")
	private String fileName;
	@FieldDescribe("첨부파일 site.")
	@Schema(description = "첨부파일 site.")
	private String site;
	@FieldDescribe("내용.")
	@Schema(description = "내용.")
	private String content;

	public String getFileName() throws Exception {
		return StringUtils.isEmpty(fileName) ? Config.processPlatform().getDocToWordDefaultFileName() : fileName;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getSite() throws Exception {
		return StringUtils.isEmpty(site) ? Config.processPlatform().getDocToWordDefaultSite() : site;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
