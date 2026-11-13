package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewInfoFormIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewInfoFormIdEmpty() {
		super("양식 ID [formId]가 비어 있어 데이터를 저장할 수 없습니다." );
	}
}
