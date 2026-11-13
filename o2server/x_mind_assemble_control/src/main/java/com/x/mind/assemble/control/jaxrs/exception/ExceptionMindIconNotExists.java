package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindIconNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindIconNotExists( String id ) {
		super("지정한 ID의 마인드맵 썸네일이 없습니다. ID:" + id );
	}
}
