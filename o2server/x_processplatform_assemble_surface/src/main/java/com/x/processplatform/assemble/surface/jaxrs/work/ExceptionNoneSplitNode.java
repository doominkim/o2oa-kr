package com.x.processplatform.assemble.surface.jaxrs.work;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionNoneSplitNode extends LanguagePromptException {

	private static final long serialVersionUID = -7038279889683420366L;

	ExceptionNoneSplitNode(String workId) {
		super("문서가 거친 분할 단계를 찾을 수 없습니다: {}.", workId);
	}

}
