package com.x.attendance.assemble.control.jaxrs.v2.file;

import com.x.base.core.project.exception.PromptException;

class ExceptionAllocateStorageMapping extends PromptException {

	private static final long serialVersionUID = 5900374070936937166L;

	ExceptionAllocateStorageMapping() {
		super("스토리지를 가져올 수 없습니다.");
	}
}
