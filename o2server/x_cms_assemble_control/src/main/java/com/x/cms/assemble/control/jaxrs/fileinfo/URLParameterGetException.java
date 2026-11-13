package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.LanguagePromptException;

public class URLParameterGetException extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public URLParameterGetException(Throwable e ) {
		super("전달된 URL 매개변수를 해석하는 중 오류가 발생했습니다.", e);
	}
}
