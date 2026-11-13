package com.x.base.core.project.exception;

import java.util.Objects;

import com.x.base.core.project.http.EffectivePerson;

public class ExceptionAccessDeniedOrEntityNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -7354813827434276962L;

	public static String defaultMessage = "사용자: {} 권한이 없거나 대상이 존재하지 않습니다.";

	public ExceptionAccessDeniedOrEntityNotExist(String person) {
		super(defaultMessage, person);
	}

	public ExceptionAccessDeniedOrEntityNotExist(EffectivePerson effectivePerson) {
		super(defaultMessage, effectivePerson.getDistinguishedName());
	}

	public ExceptionAccessDeniedOrEntityNotExist(EffectivePerson effectivePerson, String message) {
		super("사용자: {} 권한이 없거나 대상이 존재하지 않습니다, {}.", effectivePerson.getDistinguishedName(), Objects.toString(message, ""));
		this.setLanguageKey(this.getClass().getName()+"_1");
	}

}
