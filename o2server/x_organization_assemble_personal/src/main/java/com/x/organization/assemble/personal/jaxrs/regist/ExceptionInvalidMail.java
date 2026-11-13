package com.x.organization.assemble.personal.jaxrs.regist;

import java.util.Objects;

import com.x.base.core.project.exception.PromptException;

class ExceptionInvalidMail extends PromptException {

   private static final long serialVersionUID = 4622760821556680073L;

    ExceptionInvalidMail(String mail) {
       super("이메일 주소 형식이 올바르지 않습니다: " + Objects.toString(mail) + ".");
   }
}
