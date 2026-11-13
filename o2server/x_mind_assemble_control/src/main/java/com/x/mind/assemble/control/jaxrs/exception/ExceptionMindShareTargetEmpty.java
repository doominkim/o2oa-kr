package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMindShareTargetEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionMindShareTargetEmpty() {
		super("마인드맵 공유 대상이 비어 있어 파일을 공유할 수 없습니다!");
	}
}
