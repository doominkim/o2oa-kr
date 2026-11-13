package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessage extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessage(Throwable e, String message) {
		super("메시지 전송 오류! message:" + message, e );
	}
}
