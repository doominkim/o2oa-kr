package com.x.base.core.project.exception;

public class ExceptionCollectValidateFailure extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "클라우드 서버 인증에 실패했습니다.";

	public ExceptionCollectValidateFailure() {
		super(defaultMessage);
	}
}
