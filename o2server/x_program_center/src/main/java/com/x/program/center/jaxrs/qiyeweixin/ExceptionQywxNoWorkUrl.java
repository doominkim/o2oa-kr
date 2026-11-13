package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionQywxNoWorkUrl extends LanguagePromptException {


    ExceptionQywxNoWorkUrl() {
        super("WeCom에 문서를 여는 URL이 설정되어 있지 않습니다.");
    }
}
