package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionSubjectTypeInvalid extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionSubjectTypeInvalid( String type ) {
		super("섹션 [정보 유형] 값이 올바르지 않아 저장할 수 없습니다! 분류: " + type );
	}
	
	public ExceptionSectionSubjectTypeInvalid( String[] type ) {
		super("섹션 [정보 유형] 값이 올바르지 않아 저장할 수 없습니다! 분류: " + type );
	}
}
