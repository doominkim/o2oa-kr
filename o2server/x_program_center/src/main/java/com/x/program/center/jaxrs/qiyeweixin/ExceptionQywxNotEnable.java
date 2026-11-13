package com.x.program.center.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 3/12/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionQywxNotEnable extends LanguagePromptException {


    ExceptionQywxNotEnable() {
        super("WeCom 메시지가 사용 설정되어 있지 않습니다.");
    }
}
