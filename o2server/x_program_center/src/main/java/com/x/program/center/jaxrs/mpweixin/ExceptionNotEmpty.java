package com.x.program.center.jaxrs.mpweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNotEmpty  extends LanguagePromptException {

    private static final long serialVersionUID = 4862362281353270832L;

    ExceptionNotEmpty(String name) {
        super("속성: {}은(는) 비워 둘 수 없습니다.", name);
    }
}
