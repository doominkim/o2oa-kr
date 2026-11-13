package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionResponseHeaderSet extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionResponseHeaderSet(Throwable e ) {
		super("파일 다운로드 시 responseHeader를 설정하는 중 오류가 발생했습니다.", e);
	}
}
