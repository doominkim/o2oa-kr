package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionUnlawfulAddress extends LanguagePromptException {

	private static final long serialVersionUID = -3709552818045153245L;

	public ExceptionUnlawfulAddress(String address) {
		super("이 인터페이스의 서비스 주소가 허용 목록에 없습니다: {}.", address);
	}
}
