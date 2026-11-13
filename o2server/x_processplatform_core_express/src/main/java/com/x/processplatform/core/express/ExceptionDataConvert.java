package com.x.processplatform.core.express;

import com.x.base.core.project.exception.PromptException;

class ExceptionDataConvert extends PromptException {

	private static final long serialVersionUID = -2875973555673919197L;

	ExceptionDataConvert(Exception e, String job) {
		super(e, "DataItem을 Data 객체로 변환할 수 없습니다, job: {}.", job);
	}
}
