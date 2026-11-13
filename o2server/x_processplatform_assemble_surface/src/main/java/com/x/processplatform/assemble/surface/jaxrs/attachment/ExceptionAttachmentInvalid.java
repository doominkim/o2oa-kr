package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAttachmentInvalid extends LanguagePromptException {

	private static final long serialVersionUID = 3232548525722242208L;

	public static String defaultMessage = "첨부 파일: {}, 허용되지 않는 업로드 유형입니다.";

	ExceptionAttachmentInvalid(String fileName) {
		super(defaultMessage, fileName);
	}

	ExceptionAttachmentInvalid(String fileName, Integer fileSize) {
		super("첨부 파일: {}, 첨부 파일 크기가 제한 {}M을 초과했습니다.", fileName, fileSize);
		this.setLanguageKey(this.getClass().getName()+"_1");
	}

}
