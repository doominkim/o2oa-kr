package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewDataQueryViewIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewDataQueryViewIdEmpty() {
		super("목록 데이터 조회 조건 [categoryId]가 비어 있어 데이터를 조회할 수 없습니다." );
	}
}
