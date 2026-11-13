package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptySplitValueAfterTrim extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptySplitValueAfterTrim(String id) {
		super("문서: {}, 필터링 후 분할 값이 비어 있습니다.", id);
	}
}
