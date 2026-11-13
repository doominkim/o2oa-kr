package com.x.attendance.assemble.control.jaxrs.v2;

import com.x.base.core.project.exception.PromptException;

import java.util.List;

public class ExceptionParticipateConflict extends PromptException {


	private static final long serialVersionUID = 3096847989690594565L;

	public ExceptionParticipateConflict(List<String> peopleList) {
		super("인원: 【"+String.join(", ", peopleList) + "】은(는) 이미 다른 근태 그룹에 속해 있습니다!");
	}
}
