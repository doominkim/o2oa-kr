package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyExtension extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionEmptyExtension(String name) {
		super("확장자가 없는 파일은 업로드할 수 없습니다: {}.", name);
	}
}
