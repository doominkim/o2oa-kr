package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionViewQueryByIdEmpty extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionViewQueryByIdEmpty( Throwable e, String id ) {
		super( "뷰 ID로 뷰를 가져오는 중 오류가 발생했습니다. ID: {}", id, e );
	}
}
