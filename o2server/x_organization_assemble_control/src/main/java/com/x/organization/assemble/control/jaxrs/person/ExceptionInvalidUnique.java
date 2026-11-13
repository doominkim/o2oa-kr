package com.x.organization.assemble.control.jaxrs.person;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidUnique extends LanguagePromptException {

    private static final long serialVersionUID = -1792144070153264245L;

    ExceptionInvalidUnique(String name) {
       super("고유 코드에는 예약어나 특수 문자를 사용할 수 없습니다: {}.", name);
   }
}
