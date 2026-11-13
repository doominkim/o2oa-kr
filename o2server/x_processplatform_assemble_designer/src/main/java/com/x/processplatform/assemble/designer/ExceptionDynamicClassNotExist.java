package com.x.processplatform.assemble.designer;

import com.x.base.core.project.exception.PromptException;

class ExceptionDynamicClassNotExist extends PromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionDynamicClassNotExist(String className) {
		super("지정한 클래스 {}이(가) 없습니다.");
	}

}
