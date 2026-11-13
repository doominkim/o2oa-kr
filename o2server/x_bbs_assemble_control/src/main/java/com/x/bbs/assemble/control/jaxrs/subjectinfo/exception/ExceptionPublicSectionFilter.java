package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionPublicSectionFilter extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionPublicSectionFilter( Throwable e ) {
		super("조건에 맞는 공개 분류 정보를 조회하는 중 오류가 발생했습니다.", e );
	}
}
