package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDevicePushTypeError extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDevicePushTypeError() {
		super("푸시 유형이 올바르지 않아 작업할 수 없습니다.");
	}
}
