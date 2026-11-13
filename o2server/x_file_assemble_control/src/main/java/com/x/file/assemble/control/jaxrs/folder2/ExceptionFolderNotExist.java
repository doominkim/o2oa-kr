package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNotExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNotExist(String id) {
		super("지정한 폴더: {}이(가) 없습니다.", id);
	}
}
