package com.x.jpush.assemble.control.jaxrs.message;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSendMessageAPNSFileNotExist extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSendMessageAPNSFileNotExist() {
		super("Apple 푸시 인증서 파일이 없어 메시지를 보낼 수 없습니다.");
	}
}
