package com.x.base.core.project.exception;

public class ExceptionAttachmentInvalidCallback extends PromptException {

	private static final long serialVersionUID = 8275405268546054638L;

	public ExceptionAttachmentInvalidCallback(String callbackName, String fileName) {
		super(callbackName, "첨부 파일: {}, 허용되지 않는 업로드 유형입니다.", fileName);
	}

	public ExceptionAttachmentInvalidCallback(String callbackName, String fileName, Integer fileSize) {
		super(callbackName, "첨부 파일: {}, 첨부 파일 크기가 제한 {}M을 초과했습니다.", fileName, fileSize);
	}

}
