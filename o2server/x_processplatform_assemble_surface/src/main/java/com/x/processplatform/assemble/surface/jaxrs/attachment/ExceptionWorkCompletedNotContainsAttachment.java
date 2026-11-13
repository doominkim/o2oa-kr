package com.x.processplatform.assemble.surface.jaxrs.attachment;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkCompletedNotContainsAttachment extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionWorkCompletedNotContainsAttachment(String title, String workCompletedId, String attachmentName,
			String attachmentId) {
		super("완료된 문서 title: {} id: {}에서 첨부 파일 name: {} id: {}을(를) 찾을 수 없습니다.", title, workCompletedId,
				attachmentName, attachmentId);
	}

}
