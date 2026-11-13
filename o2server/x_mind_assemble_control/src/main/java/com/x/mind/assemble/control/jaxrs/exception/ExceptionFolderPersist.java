package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderPersist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderPersist( Throwable e ) {
		super("폴더 정보 저장 중 오류가 발생했습니다!", e );
	}
}
