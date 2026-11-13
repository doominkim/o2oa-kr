package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoBase64Encode extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoBase64Encode( Throwable e, String id ) {
		super("파일을 Base64로 인코딩하는 중 오류가 발생했습니다. Id: {}", id );
	}
}
