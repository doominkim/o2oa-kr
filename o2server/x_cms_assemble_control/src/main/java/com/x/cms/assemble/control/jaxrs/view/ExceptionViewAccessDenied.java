package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewAccessDenied extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewAccessDenied(String categoryId, String viewId) {
		super("뷰 id: {} 분류 id: {}에 접근할 권한이 없습니다.", categoryId, viewId );
	}
}
