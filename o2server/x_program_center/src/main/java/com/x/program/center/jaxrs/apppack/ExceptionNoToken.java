package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNoToken extends LanguagePromptException {

    private static final long serialVersionUID = -4443630173583290883L;

    ExceptionNoToken() {
        super("token이 없어 app 패키징 서버에 요청할 수 없습니다!");
    }
}
