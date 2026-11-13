package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectFilter( Throwable e ) {
		super("조건에 맞는 주제 정보를 조회하는 중 오류가 발생했습니다.", e );
	}
}
