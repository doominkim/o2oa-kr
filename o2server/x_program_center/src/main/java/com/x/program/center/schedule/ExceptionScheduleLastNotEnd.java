package com.x.program.center.schedule;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.base.core.project.schedule.ScheduleRequest;

class ExceptionScheduleLastNotEnd extends LanguagePromptException {

	private static final long serialVersionUID = -7954335762204386602L;

	ExceptionScheduleLastNotEnd(ScheduleRequest request, String application) {
		super("예약 에이전트 건너뜀: {}, Cron 표현식: {}, 앱: {}, 마지막 시작 시간: {}, 마지막 실행이 끝나지 않았습니다.",
				request.getClassName(), request.getCron(), application, request.getLastStartTime());
	}
}