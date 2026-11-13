package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.LanguagePromptException;

class AliasDuplicateWithNameException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	AliasDuplicateWithNameException(String name) {
		super("별칭이 이름과 중복됩니다: {}.", name);
	}
}
