package com.x.correlation.service.processing.jaxrs.correlation;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBundleNotMatch extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionBundleNotMatch(String bundle, String other) {
		super("식별자가 일치하지 않습니다: {}, {}.", bundle, other);
	}
}
