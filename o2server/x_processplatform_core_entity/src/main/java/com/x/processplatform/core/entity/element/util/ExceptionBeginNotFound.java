package com.x.processplatform.core.entity.element.util;

import com.x.base.core.project.exception.PromptException;

public class ExceptionBeginNotFound extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionBeginNotFound() {
		super("시작 노드를 찾을 수 없습니다.");
	}
}
