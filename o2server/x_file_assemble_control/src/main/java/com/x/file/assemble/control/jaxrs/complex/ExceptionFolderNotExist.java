package com.x.file.assemble.control.jaxrs.complex;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNotExist extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionFolderNotExist(String name) {
		super("확장자가 없는 파일은 업로드할 수 없습니다: {}.", name);
	}
}
