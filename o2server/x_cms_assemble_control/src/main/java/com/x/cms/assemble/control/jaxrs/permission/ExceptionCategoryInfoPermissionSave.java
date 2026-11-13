package com.x.cms.assemble.control.jaxrs.permission;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionCategoryInfoPermissionSave extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionCategoryInfoPermissionSave( Throwable e, String id ) {
		super( "분류 권한을 저장하는 중 오류가 발생했습니다. ID: {}" ,id, e );
	}
}
