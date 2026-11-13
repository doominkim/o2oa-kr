package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionNotEmpty extends LanguagePromptException {


    ExceptionNotEmpty(String name) {
        super("{}은(는) 비워 둘 수 없습니다.", name);
    }
}
