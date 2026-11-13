package com.x.organization.assemble.control.jaxrs.group;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.http.EffectivePerson;

class ExceptionDenyDeleteGroup extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionDenyDeleteGroup(EffectivePerson effectivePerson, String name) {
		super("{} 사용자는 그룹: {}을(를) 삭제할 권한이 없습니다.", effectivePerson.getDistinguishedName(), name);
	}
}
