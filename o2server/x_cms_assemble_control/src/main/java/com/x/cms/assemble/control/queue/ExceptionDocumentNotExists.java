package com.x.cms.assemble.control.queue;

import com.x.base.core.project.exception.PromptException;

class ExceptionDocumentNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentNotExists( String docId ) {
		super("게시글 정보가 없어 데이터를 저장할 수 없습니다. ID:" + docId );
	}
}
