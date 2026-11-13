package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageDeviceEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageDeviceEmpty() {
		super("연결된 기기가 없어 메시지를 보낼 수 없습니다.");
	}
}
