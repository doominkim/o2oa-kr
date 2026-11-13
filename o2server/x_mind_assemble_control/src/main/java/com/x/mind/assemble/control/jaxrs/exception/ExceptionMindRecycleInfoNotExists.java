package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindRecycleInfoNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindRecycleInfoNotExists( String id ) {
		super("지정한 ID의 휴지통 마인드맵 정보가 없습니다. ID:" + id );
	}
}
