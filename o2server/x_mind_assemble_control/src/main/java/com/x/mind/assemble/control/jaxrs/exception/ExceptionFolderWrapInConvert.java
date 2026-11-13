package com.x.mind.assemble.control.jaxrs.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionFolderWrapInConvert extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionFolderWrapInConvert( Throwable e, String jsonString ) {
		super("JsonElement를 마인드맵 폴더 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonString, e );
	}
}
