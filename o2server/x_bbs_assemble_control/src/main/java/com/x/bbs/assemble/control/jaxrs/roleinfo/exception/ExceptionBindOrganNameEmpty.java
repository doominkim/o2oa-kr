package com.x.bbs.assemble.control.jaxrs.roleinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBindOrganNameEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionBindOrganNameEmpty() {
		super("조직 이름이 비어 있어 조회할 수 없습니다." );
	}
}
