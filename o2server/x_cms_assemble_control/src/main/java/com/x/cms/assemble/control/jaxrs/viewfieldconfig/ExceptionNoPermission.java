package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoPermission extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionNoPermission( String personName ) {
		super( "사용자 {}에게 게시판 표시 열 설정을 작업할 권한이 없습니다!", personName );
	}
}
