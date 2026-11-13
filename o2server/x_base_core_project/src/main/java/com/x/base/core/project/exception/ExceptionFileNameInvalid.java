package com.x.base.core.project.exception;

/**
 * @author sword
 */
public class ExceptionFileNameInvalid extends PromptException {

	private static final long serialVersionUID = -283505161497831794L;

	public ExceptionFileNameInvalid(String str) {
		super("첨부 파일 이름: {}, 규칙에 맞지 않습니다.", str);
	}
}
