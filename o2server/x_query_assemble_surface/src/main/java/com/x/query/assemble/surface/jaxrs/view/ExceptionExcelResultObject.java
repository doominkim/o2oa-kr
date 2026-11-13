package com.x.query.assemble.surface.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionExcelResultObject extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionExcelResultObject(String flag) {
		super("지정한 Excel 결과가 없습니다: {}.", flag);
	}

}
