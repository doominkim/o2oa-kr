package com.x.cms.assemble.control.jaxrs.image;

import com.x.base.core.project.exception.PromptException;

class ExceptionURLInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionURLInvalid() {
		super("이미지 주소 URL이 올바르지 않아 인터넷 이미지 정보를 가져올 수 없습니다." );
	}
}
