package com.x.file.assemble.control.jaxrs.folder2;

import com.x.base.core.project.exception.PromptException;

class ExceptionFolderNameExist extends PromptException {

	private static final long serialVersionUID = 7750207007061165350L;

	ExceptionFolderNameExist(String person, String name, String superior) {
		super("사용자: {}, 지정한 폴더: {}이(가) 이미 있습니다. 상위 폴더: {}.", person, name, superior);
	}
}
