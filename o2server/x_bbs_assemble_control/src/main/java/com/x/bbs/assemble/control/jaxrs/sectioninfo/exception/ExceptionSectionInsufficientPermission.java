package com.x.bbs.assemble.control.jaxrs.sectioninfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSectionInsufficientPermission extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSectionInsufficientPermission( String name, String forum ) {
		super("사용자 ["+ name +"]에게 포럼 분류 ["+ forum +"] 관리자가 아니므로 섹션 정보를 관리할 수 없습니다!" );
	}
}
