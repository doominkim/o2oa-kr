package com.x.base.core.project.exception;

public class ExceptionAttachmentInvalid extends PromptException {

	private static final long serialVersionUID = 3232548525722242208L;

	public static String defaultMessage = "첨부 파일: {}, 허용되지 않는 업로드 유형입니다.";

	public ExceptionAttachmentInvalid(String fileName) {
		super(defaultMessage, fileName);
	}

	public ExceptionAttachmentInvalid(String fileName, Integer fileSize) {
		super("첨부 파일: {}, 첨부 파일 크기가 제한 {}M을 초과했습니다.", fileName, fileSize);
	}

}
