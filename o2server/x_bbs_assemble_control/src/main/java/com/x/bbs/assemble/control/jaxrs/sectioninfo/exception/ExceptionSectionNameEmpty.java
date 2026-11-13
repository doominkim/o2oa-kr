package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionNameEmpty() {
		super("섹션 이름이 비어 있어 조회할 수 없습니다." );
	}
}
