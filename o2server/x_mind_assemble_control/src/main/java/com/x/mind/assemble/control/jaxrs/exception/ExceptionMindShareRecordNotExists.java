package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindShareRecordNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindShareRecordNotExists( String id ) {
		super("마인드맵 파일 공유 정보가 없습니다. ID:" + id );
	}
}
