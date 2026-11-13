package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUnitNotExistOrNotUniqueUnitId extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionUnitNotExistOrNotUniqueUnitId(String flag) {
		super("조직: {}이(가) 없거나 조직 uuid가 고유하지 않습니다.", flag);
	}
}
