package com.x.organization.assemble.control.jaxrs.personattribute;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.organization.core.entity.Person;

class ExceptionNameExistWithPerson extends LanguagePromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionNameExistWithPerson(Person person, String unique) {
		super("{}에 이름이 {} 인 속성이 이미 있습니다.", person.getName(), unique);
	}
}
