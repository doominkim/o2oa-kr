package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionDuplicate(String queryName, String fieldName, Object value) {
		super("{}의 조회 설정 식별자와 충돌합니다, 충돌 필드 이름 {}, 충돌 값: {}.", queryName, fieldName, value);
	}
}
