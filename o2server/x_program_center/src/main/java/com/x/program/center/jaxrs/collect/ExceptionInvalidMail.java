package com.x.program.center.jaxrs.collect;

import java.util.Objects;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidMail extends LanguagePromptException {

   private static final long serialVersionUID = 4622760821556680073L;

    ExceptionInvalidMail(String mail) {
       super("이메일 주소 형식이 올바르지 않습니다: {}.",Objects.toString(mail));
   }
}
