package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAppInfoNameAlreadyExists extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionAppInfoNameAlreadyExists( String name ) {
		super("앱 이름이 이미 사용 중이어서 저장할 수 없습니다. Name: {}", name );
	}
}
