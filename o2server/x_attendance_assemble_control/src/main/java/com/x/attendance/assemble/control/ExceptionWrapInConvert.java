package com.x.attendance.assemble.control;

import com.google.gson.JsonElement;
import com.x.base.core.project.exception.PromptException;

public class ExceptionWrapInConvert extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionWrapInConvert( Throwable e, JsonElement jsonElement) {
		super( "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString(), e);
	}
}
