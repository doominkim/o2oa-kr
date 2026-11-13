package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUsedDefaultForm extends LanguagePromptException {

	private static final long serialVersionUID = 5028290914582880718L;

	ExceptionUsedDefaultForm(String name, String id) {
		super("양식 name: {} id: {}은(는) 앱의 기본 양식입니다.", name, id);
	}
}
