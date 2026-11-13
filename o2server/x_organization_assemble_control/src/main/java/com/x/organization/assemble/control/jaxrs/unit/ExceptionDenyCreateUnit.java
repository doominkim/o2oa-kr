package com.x.organization.assemble.control.jaxrs.unit;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyCreateUnit extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyCreateUnit(EffectivePerson effectivePerson, String name) {
		super("{} 사용자는 조직: {}을(를) 만들 권한이 없습니다.", effectivePerson.getDistinguishedName(), name);
	}
}
