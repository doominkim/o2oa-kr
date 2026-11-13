package com.x.general.assemble.control.jaxrs.invoice;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMapping extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionAllocateStorageMapping() {
		super("스토리지를 할당할 수 없습니다.");
	}
}
