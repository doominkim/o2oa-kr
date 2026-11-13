package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectWrapIn extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectWrapIn( Throwable e ) {
		super("사용자가 입력한 데이터를 주제 정보 객체로 변환하는 중 오류가 발생했습니다.", e );
	}
}
