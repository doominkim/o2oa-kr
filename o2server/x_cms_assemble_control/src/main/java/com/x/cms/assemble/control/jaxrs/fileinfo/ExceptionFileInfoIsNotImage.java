package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoIsNotImage extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoIsNotImage( String id ) {
		super("파일이 이미지 형식이 아니어서 이미지로 변환할 수 없습니다. Id{}", id );
	}
}
