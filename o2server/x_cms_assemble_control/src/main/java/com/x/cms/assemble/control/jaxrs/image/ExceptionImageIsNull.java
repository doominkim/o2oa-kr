package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionImageIsNull extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionImageIsNull( String url ) {
		super("이미지 주소 URL에서 이미지 정보를 가져오지 못했습니다! URL: {}", url );
	}
}
