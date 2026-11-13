package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoQueryById extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoQueryById( Throwable e, String id ) {
		super( "지정한 ID의 분류 정보 객체를 조회하는 중 오류가 발생했습니다. ID: {}" ,id, e );
	}
}
