package com.x.portal.assemble.designer.jaxrs.page;

import com.x.base.core.project.exception.LanguagePromptException;

class PageNotExistedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	PageNotExistedException(String id) {
		super("지정한 페이지가 없습니다: {}.", id);
	}
}
