package com.x.processplatform.assemble.designer.jaxrs.process;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWorkProcessing extends LanguagePromptException {

	private static final long serialVersionUID = 3768001625178470667L;

	ExceptionWorkProcessing(String name, String id, Long count) {
		super("프로세스: {}, id: {}에 진행 중인 작업 인스턴스가 {}개 있습니다. 진행 중인 작업 인스턴스가 없는지 확인한 뒤 프로세스를 삭제하세요.", name, id, count);
	}
}
