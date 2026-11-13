package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionSuperiorNotExist extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSuperiorNotExist(String flag, String superiorFlag) {
		super("조직: {}의 상위 조직: {}이(가) 없습니다.", flag, superiorFlag);
	}
}
