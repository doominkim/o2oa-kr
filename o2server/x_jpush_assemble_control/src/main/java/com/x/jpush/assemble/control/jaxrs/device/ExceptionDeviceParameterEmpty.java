package com.x.jpush.assemble.control.jaxrs.device;

import com.x.base.core.project.exception.PromptException;

public class ExceptionDeviceParameterEmpty extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionDeviceParameterEmpty() {
		super("기기 번호 또는 기기 유형이 비어 있어 작업할 수 없습니다.");
	}
}
