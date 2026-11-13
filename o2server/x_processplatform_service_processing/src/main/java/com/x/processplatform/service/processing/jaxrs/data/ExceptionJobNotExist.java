package com.x.processplatform.service.processing.jaxrs.data;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionJobNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionJobNotExist(String job) {
		super("지정한 job이 없습니다: {}.", job);
	}

}
