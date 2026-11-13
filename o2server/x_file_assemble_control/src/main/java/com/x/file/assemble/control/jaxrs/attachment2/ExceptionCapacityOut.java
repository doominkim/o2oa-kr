package com.x.file.assemble.control.jaxrs.attachment2;

import com.x.base.core.project.exception.PromptException;

class ExceptionCapacityOut extends PromptException {

	private static final long serialVersionUID = 5186249956453089454L;

	ExceptionCapacityOut(long curCapacity, long limitCapacity) {
		super("저장 용량을 초과했습니다. 사용 예정: {}M, 한도: {}M", curCapacity, limitCapacity);
	}

}
