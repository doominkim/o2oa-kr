package com.x.base.core.project.exception;

public class ExceptionCollectConnectError extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "클라우드 서버 연결 오류입니다.";

	public ExceptionCollectConnectError() {
		super(defaultMessage);
	}
}
