package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.tools.DateTools;
import com.x.program.center.schedule.TriggerAgent.Pair;

class ExceptionAgentLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionAgentLastNotEnd(Pair pair) {
		super("에이전트 실행 건너뜀: {}, 이름: {}, Cron 표현식: {}, 이전 시작 시간: {}, 이전 실행이 끝나지 않았습니다.", pair.getId(),
				pair.getName(), pair.getCron(),
				(pair.getLastStartTime() == null ? "" : DateTools.format(pair.getLastStartTime())));
	}
}