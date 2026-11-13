package com.x.base.core.project.exception;

public class ExceptionCollectDisable extends LanguagePromptException {

	private static final long serialVersionUID = 7551134321893884285L;

	public static String defaultMessage = "클라우드 서비스가 비활성화되어 있습니다.";

	public ExceptionCollectDisable() {
		super(defaultMessage);
	}
}
