package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEditNotAllowed extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionEditNotAllowed(Long count) {
		super( "이 분류에 아직 {}개의 게시글이 있습니다. 게시글을 모두 삭제한 뒤 분류를 삭제하세요.", count);
	}
}
