package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCreatorHasNoIdentity extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionCreatorHasNoIdentity( String message ) {
		super("마인드맵 정보 삭제 중 오류가 발생했습니다! MESSAGE:" + message );
	}
	
	public ExceptionCreatorHasNoIdentity( Throwable e, String message ) {
		super("마인드맵 정보 삭제 중 오류가 발생했습니다! MESSAGE:" + message, e );
	}
}
