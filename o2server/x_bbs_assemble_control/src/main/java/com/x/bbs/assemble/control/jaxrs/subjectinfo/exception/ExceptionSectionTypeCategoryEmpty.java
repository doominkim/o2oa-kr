package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionTypeCategoryEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionTypeCategoryEmpty( String sectionId ) {
		super("섹션 주제 분류 typeCategory가 비어 있어 주제를 게시할 수 없습니다. Section:" + sectionId);
	}
}
