package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionTypeNotSupport extends LanguagePromptException {

    private static final long serialVersionUID = 4862362281353270832L;

    ExceptionTypeNotSupport() {
        super("지원하지 않는 메뉴 유형입니다. 현재 click, view, miniprogram 세 가지 유형만 지원합니다.");
    }
}
