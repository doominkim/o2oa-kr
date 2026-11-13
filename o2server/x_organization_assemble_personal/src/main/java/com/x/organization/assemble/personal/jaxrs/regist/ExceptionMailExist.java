package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionMailExist extends PromptException {

	private static final long serialVersionUID = -2489020682959861835L;

	ExceptionMailExist(String mail) {
		super("이메일: " + Objects.toString(mail) + "은(는) 이미 등록되어 있습니다.");
	}
}
