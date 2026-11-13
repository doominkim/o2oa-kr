package com.x.base.core.project.exception;

public class ExceptionConnectDbError extends PromptException {

	private static final long serialVersionUID = 8121998765154409958L;

	public ExceptionConnectDbError() {
		super("데이터베이스 연결에 실패했습니다. 관리자에게 문의하십시오.");
	}
}
