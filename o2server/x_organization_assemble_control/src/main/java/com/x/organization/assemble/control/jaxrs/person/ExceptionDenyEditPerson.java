package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyEditPerson extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionDenyEditPerson(EffectivePerson effectivePerson, String name) {
		super("{} 사용자는 개인: {}을(를) 편집할 권한이 없습니다.", effectivePerson.getDistinguishedName(), name);
	}
}
