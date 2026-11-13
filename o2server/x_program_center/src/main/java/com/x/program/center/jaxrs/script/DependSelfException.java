package com.x.program.center.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class DependSelfException extends PromptException {

	private static final long serialVersionUID = -1393070907328497913L;

	DependSelfException(String name, String id) {
		super("스크립트: {}, id: {}이(가) 자기 자신에 의존합니다.", name, id);
	}
}
