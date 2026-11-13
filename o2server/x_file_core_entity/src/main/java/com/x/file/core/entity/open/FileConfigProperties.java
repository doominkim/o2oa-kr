package com.x.file.core.entity.open;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * 클라우드 파일 시스템 확장 설정
 * @author sword
 */
public class FileConfigProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("업로드 허용 파일 확장자")
	private List<String> fileTypeIncludes = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",
			"xapp", "text", "zip", "rar", "mp3", "mp4", "png", "jpg", "gif");

	@FieldDescribe("업로드 금지 파일 확장자")
	private List<String> fileTypeExcludes = new ArrayList<>();

	public List<String> getFileTypeIncludes() {
		return fileTypeIncludes;
	}

	public void setFileTypeIncludes(List<String> fileTypeIncludes) {
		this.fileTypeIncludes = fileTypeIncludes;
	}

	public List<String> getFileTypeExcludes() {
		return fileTypeExcludes;
	}

	public void setFileTypeExcludes(List<String> fileTypeExcludes) {
		this.fileTypeExcludes = fileTypeExcludes;
	}

}
