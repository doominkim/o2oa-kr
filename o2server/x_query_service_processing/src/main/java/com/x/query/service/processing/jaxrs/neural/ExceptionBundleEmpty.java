package com.x.query.service.processing.jaxrs.neural;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionBundleEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionBundleEmpty(String model) {
		super("신경망({}) 범위의 데이터가 비어 있습니다.", model);
	}
}
