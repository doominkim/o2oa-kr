package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionDuplicate extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionDuplicate(String queryName, String fieldName, Object value) {
		super("{}의 데이터 테이블 식별자와 충돌합니다, 충돌 필드 이름 {}, 충돌 값: {}.", queryName, fieldName, value);
	}
}
