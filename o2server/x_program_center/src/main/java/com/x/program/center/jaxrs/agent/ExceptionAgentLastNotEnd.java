package com.x.program.center.jaxrs.agent;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionAgentLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionAgentLastNotEnd(String id, String name, String alias, String date) {
		super("에이전트 id: {}, name: {}, alias: {}의 이전 실행이 아직 끝나지 않았습니다, 이전 시작 시간: {}.", id, name, alias, date);
	}
}