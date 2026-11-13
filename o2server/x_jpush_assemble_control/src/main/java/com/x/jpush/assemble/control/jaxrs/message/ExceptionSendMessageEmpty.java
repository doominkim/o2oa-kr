package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageEmpty() {
		super("메시지 내용이 비어 있어 메시지를 보낼 수 없습니다.");
	}
}
