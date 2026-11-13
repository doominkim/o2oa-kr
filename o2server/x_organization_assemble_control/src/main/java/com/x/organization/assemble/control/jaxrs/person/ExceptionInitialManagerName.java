package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInitialManagerName extends LanguagePromptException {

    private static final long serialVersionUID = 4132300948670472899L;

    ExceptionInitialManagerName() {
        super("초기 관리자 식별자는 사용할 수 없습니다.");
    }
}
