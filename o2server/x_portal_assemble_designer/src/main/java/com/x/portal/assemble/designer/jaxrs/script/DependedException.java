package com.x.portal.assemble.designer.jaxrs.script;

import com.x.base.core.project.exception.LanguagePromptException;

class DependedException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	DependedException(String name, String id, String names) {
		super("스크립트: {}, id: {}을(를) 다른 스크립트가 참조합니다: {}.", name, id, names);
	}
}
