package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionPersonHasNoIdentity extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionPersonHasNoIdentity( String name ) {
		super("사용자 소속 정보를 찾을 수 없습니다. 사용자의 조직 배정 설정을 확인하세요. Name: {}", name );
	}
}
