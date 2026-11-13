package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionRequiredParameterNotPassed extends LanguagePromptException {

    private static final long serialVersionUID = -9089355008820123519L;

    ExceptionRequiredParameterNotPassed(String parameter) {
        super("필수 매개변수가 전달되지 않았습니다: {}.", parameter);
    }
}
