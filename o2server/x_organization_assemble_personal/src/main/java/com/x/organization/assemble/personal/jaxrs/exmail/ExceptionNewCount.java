package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * 
 * @author ray
 *
 */
class ExceptionNewCount extends LanguagePromptException {

	private static final long serialVersionUID = -3885997486474873786L;

	ExceptionNewCount(String msg) {
		super("새 메일 수를 가져오지 못했습니다: {}.", msg);
	}

}
