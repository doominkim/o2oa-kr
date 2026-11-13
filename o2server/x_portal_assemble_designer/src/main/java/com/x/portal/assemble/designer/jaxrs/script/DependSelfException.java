package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class DependSelfException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	DependSelfException(String name, String id) {
		super("스크립트: {}, id: {}이(가) 자기 자신에 의존합니다.", name, id);
	}
}
