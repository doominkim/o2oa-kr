package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWrapInViewIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionWrapInViewIdEmpty( ) {
		super( "열이 속한 뷰 ID를 가져오지 못했습니다." );
	}
}
