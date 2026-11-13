package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionModifyOfficialTable extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionModifyOfficialTable() {
		super("시스템 테이블은 수정할 수 없습니다.");
	}
}
