package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectContentQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectContentQueryById( Throwable e, String id ) {
		super("ID로 주제 내용을 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
