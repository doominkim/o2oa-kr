package com.x.cms.assemble.control.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDataAlreadyExist extends LanguagePromptException {

	private static final long serialVersionUID = -665095222445791960L;

	ExceptionDataAlreadyExist(String title, String docId) {
		super("게시글 title: {} id: {}의 데이터가 이미 있습니다.", title, docId );
	}
}
