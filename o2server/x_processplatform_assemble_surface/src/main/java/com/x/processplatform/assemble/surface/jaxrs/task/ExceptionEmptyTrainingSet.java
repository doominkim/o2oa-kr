package com.x.processplatform.assemble.surface.jaxrs.task;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionEmptyTrainingSet extends LanguagePromptException {

	private static final long serialVersionUID = 1040883405179987063L;

	ExceptionEmptyTrainingSet(String title, String id, String person) {
		super("결재 대기: {}, id: {}, 처리자: {}, 데이터 세트가 비어 있습니다.", title, id, person);
	}
}
