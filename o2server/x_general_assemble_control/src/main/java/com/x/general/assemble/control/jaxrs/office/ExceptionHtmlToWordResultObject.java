package com.x.general.assemble.control.jaxrs.office;

import com.x.base.core.project.exception.PromptException;

class ExceptionHtmlToWordResultObject extends PromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionHtmlToWordResultObject(String flag) {
		super("지정한 Word 결과가 없습니다: {}.", flag);
	}

}
