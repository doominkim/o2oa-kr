package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2022/9/19.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ExceptionQywxResponse extends PromptException {


    private static final long serialVersionUID = -2068458307623732091L;

    ExceptionQywxResponse(Integer retCode, String retMessage) {
        super("WeCom 통합 로그인 실패, 오류 코드: {}, 오류 메시지: {}.", retCode, retMessage);
    }
}

