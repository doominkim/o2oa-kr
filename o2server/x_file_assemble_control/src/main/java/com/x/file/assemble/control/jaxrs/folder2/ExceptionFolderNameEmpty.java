package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameEmpty extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameEmpty() {
		super("폴더 이름은 비워 둘 수 없습니다.");
	}
}
