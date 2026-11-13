package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionEmptyProperty extends LanguagePromptException {

    private static final long serialVersionUID = -4443630173583290883L;

    ExceptionEmptyProperty(String propertyName) {
        super("매개변수 "+propertyName+" 은(는) 비워 둘 수 없습니다!");
    }
}
