package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentAccessDenied extends PromptException {

	private static final long serialVersionUID = 9085364457175859374L;

	ExceptionDocumentAccessDenied(String person, String title, String docId) {
		super("사용자 {}에게 게시글에 접근할 권한이 없습니다. name: {} id: {}.", person, title, docId);
	}

}
