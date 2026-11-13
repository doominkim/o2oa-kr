package com.x.attendance.assemble.control.schedule;

import com.x.base.core.project.exception.PromptException;

class AnalyseMobileDetailByIdException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	AnalyseMobileDetailByIdException( String id ) {
		super("ID로 모바일 체크 정보를 분석하는 중 오류가 발생했습니다. ID:" + id );
	}
}
