package com.x.program.center.jaxrs.script;

import com.x.base.core.project.exception.PromptException;

class DependNotExistedException extends PromptException {

	private static final long serialVersionUID = -8307485757019993917L;

	DependNotExistedException(String name, String id, String depend) {
		super("스크립트: {}, id: {}, 의존하는 스크립트: {}이(가) 없습니다.", name, id, depend);
	}
}
