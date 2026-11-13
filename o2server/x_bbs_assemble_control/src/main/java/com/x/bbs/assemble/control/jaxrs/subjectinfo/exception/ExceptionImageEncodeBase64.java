package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionImageEncodeBase64 extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionImageEncodeBase64( Throwable e, String id ) {
		super("이미지 파일을 Base64로 인코딩하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
