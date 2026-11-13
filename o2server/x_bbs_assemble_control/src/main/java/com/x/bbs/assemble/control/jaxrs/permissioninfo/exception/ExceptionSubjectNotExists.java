package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectNotExists( String id ) {
		super("지정한 ID의 주제 정보가 없습니다. Id:" + id );
	}
}
