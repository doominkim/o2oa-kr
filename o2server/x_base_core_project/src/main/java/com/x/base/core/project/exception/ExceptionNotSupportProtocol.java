package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionNotSupportProtocol extends LanguagePromptException {

	private static final long serialVersionUID = -5849117416911160227L;

	public ExceptionNotSupportProtocol(String address) {
		super("지원하지 않는 주소 접근 프로토콜입니다: {}.", address);
	}
}
