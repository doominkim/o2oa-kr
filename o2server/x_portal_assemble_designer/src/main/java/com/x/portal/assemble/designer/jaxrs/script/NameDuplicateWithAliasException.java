package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class NameDuplicateWithAliasException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	NameDuplicateWithAliasException(String name) {
		super("이름이 별칭과 중복됩니다: {}.", name);
	}
}
