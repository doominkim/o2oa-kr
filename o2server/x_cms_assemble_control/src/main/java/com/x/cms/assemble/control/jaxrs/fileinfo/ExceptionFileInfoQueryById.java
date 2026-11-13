package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoQueryById extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoQueryById( Throwable e, String id ) {
		super("지정한 ID의 파일 정보를 조회하는 중 오류가 발생했습니다. Id: {}", id, e );
	}
}
