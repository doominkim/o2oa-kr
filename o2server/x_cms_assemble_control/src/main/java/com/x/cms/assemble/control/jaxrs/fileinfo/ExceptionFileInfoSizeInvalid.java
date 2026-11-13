package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionFileInfoSizeInvalid extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionFileInfoSizeInvalid() {
		super("첨부파일 크기 size 매개변수가 숫자가 아니어서 조회할 수 없습니다." );
	}
}
