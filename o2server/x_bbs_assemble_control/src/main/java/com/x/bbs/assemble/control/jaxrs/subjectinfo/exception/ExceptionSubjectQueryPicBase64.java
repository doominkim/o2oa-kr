package com.x.bbs.assemble.control.jaxrs.subjectinfo.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionSubjectQueryPicBase64 extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionSubjectQueryPicBase64( Throwable e, String id ) {
		super("지정한 ID의 주제 이미지 인코딩 내용을 조회하는 중 오류가 발생했습니다. ID:" + id, e );
	}
}
