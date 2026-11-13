package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionBase64Encode extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionBase64Encode( Throwable e, String url ) {
		super("이미지를 Base64로 인코딩하는 중 오류가 발생했습니다. URL: {}", url );
	}
}
