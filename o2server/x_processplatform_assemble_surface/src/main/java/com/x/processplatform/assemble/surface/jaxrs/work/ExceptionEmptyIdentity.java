package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyIdentity(List<String> identities) {
		super("지정한 소속이 없거나 비어 있습니다: {}.", StringUtils.join(identities, ","));
	}

	ExceptionEmptyIdentity(String identity) {
		super("지정한 소속이 없거나 비어 있습니다: {}.", identity);
	}
}
