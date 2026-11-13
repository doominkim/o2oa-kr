package com.x.query.core.express.statement;

import com.x.base.core.project.exception.PromptException;

class ExceptionSqlUnsafe extends PromptException {

    private static final long serialVersionUID = 1L;

    ExceptionSqlUnsafe(String detail) {
        super("sql 검증 실패: {}", detail);
    }
}
