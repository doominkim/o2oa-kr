package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

class ExceptionSingleConvNotUpdate extends PromptException {

	private static final long serialVersionUID = 4132300948670472899L;

	ExceptionSingleConvNotUpdate() {
		super("1:1 대화는 속성을 수정할 수 없습니다");
	}


}
