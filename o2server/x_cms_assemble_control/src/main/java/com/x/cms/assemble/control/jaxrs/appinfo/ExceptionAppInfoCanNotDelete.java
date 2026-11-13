package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoCanNotDelete extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoCanNotDelete(Long count) {
		super("이 게시판에 아직 {}개의 분류가 있습니다. 분류를 삭제한 뒤 게시판을 삭제하세요.", count);
	}
}
