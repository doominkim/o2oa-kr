package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNoPackInfo extends LanguagePromptException {

    private static final long serialVersionUID = -4443630173583290883L;

    ExceptionNoPackInfo() {
        super("현재 O2 클라우드 계정에 이전 패키징 정보가 없습니다!");
    }
}
