package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDocumentPermissionProcess extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionDocumentPermissionProcess() {
		super("게시글 권한을 관리하는 중 오류가 발생했습니다.");
	}
}
