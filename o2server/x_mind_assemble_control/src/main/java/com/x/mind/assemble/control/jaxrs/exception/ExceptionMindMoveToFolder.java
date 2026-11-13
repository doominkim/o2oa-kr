package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindMoveToFolder extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindMoveToFolder( String message ) {
		super("마인드맵 정보를 지정한 폴더로 이동하는 중 오류가 발생했습니다! MESSAGE:" + message );
	}
	
	public ExceptionMindMoveToFolder( Throwable e, String message ) {
		super("마인드맵 정보를 지정한 폴더로 이동하는 중 오류가 발생했습니다! MESSAGE:" + message, e );
	}
}
