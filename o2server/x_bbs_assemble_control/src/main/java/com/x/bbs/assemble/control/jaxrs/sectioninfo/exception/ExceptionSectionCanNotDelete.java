package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionCanNotDelete extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionCanNotDelete( String id ) {
		super("섹션에 하위 섹션 또는 주제가 남아 있어 삭제할 수 없습니다! ID=" + id);
	}
}
