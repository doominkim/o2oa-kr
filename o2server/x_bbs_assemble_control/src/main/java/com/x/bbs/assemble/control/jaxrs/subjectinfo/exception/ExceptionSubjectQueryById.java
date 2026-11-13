package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectQueryById extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectQueryById( Throwable e, String id ) {
		super("지정한 ID로 주제 정보를 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
