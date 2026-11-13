package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryFormIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryFormIdEmpty() {
		super("게시글 분류에 편집 양식이 설정되지 않았거나 삭제되어 조회하거나 저장할 수 없습니다." );
	}
}
