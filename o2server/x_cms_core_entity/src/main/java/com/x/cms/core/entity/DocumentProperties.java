package com.x.cms.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.entity.query.DocumentNotify;

import java.util.List;

/**
 * 문서 확장 설정
 * @author sword
 */
public class DocumentProperties extends JsonProperties {

	private static final long serialVersionUID = -1259157593040432239L;

	@FieldDescribe("메시지 알림 대상")
	private DocumentNotify documentNotify;

	@FieldDescribe("문서 내용 이미지 목록")
	private List<String> cloudPictures;

	public DocumentNotify getDocumentNotify() {
		return documentNotify;
	}

	public void setDocumentNotify(DocumentNotify documentNotify) {
		this.documentNotify = documentNotify;
	}

	public List<String> getCloudPictures() {
		return cloudPictures;
	}

	public void setCloudPictures(List<String> cloudPictures) {
		this.cloudPictures = cloudPictures;
	}
}
