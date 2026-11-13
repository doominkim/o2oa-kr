package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionLoadImageFromURL extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionLoadImageFromURL( Throwable e, String url ) {
		super("지정한 URL에서 이미지 정보를 가져오는 중 오류가 발생했습니다! URL: {}", url, e );
	}
}
