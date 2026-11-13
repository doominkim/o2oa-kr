package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionFileInfoContentRead extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFileInfoContentRead(Throwable e ) {
		super("파일 출력을 읽는 중 오류가 발생했습니다.", e);
	}
}
