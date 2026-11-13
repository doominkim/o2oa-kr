package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.project.exception.CallbackPromptException;

class ExceptionDocumentNotExistsCallback extends CallbackPromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExistsCallback( String callbackName, String id ) {
		super(callbackName, "게시글 정보가 없어 작업을 계속할 수 없습니다. Id:" + id );
	}
}
