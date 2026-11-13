package com.x.organization.assemble.control.jaxrs.identity;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Unit;

class ExceptionExistInUnit extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionExistInUnit(Person person, Unit unit) {
		super("사용자: {}의 조직: {} 소속이 이미 있습니다.", person.getName(), unit.getName());
	}
}
