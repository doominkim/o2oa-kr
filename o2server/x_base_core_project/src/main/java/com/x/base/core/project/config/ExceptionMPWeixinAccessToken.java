package com.x.base.core.project.config;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 3/2/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionMPWeixinAccessToken extends PromptException {

    private static final long serialVersionUID = 5430065198687029845L;

    public ExceptionMPWeixinAccessToken(Integer code, String message) {
        super("WeChat 공식 계정 access token을 가져오지 못했습니다. 오류 코드: {}, 오류 메시지: {}.", new Object[]{code, message});
    }
}
