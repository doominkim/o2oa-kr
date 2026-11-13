package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjecttypeEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjecttypeEmpty() {
		super("주제 유형이 비어 있어 조회할 수 없습니다." );
	}
}
