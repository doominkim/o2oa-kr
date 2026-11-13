package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectSave extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectSave( Throwable e ) {
		super("주제 정보를 저장하는 중 오류가 발생했습니다.", e );
	}
}
