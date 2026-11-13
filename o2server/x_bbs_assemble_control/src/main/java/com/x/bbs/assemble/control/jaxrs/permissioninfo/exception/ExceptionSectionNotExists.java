package com.x.bbs.assemble.control.jaxrs.permissioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionNotExists( String id ) {
		super("지정한 ID의 섹션 정보가 없습니다. ID:" + id );
	}
}
