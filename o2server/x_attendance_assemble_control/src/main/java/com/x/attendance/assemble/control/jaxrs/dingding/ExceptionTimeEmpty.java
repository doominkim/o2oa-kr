package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2020-04-01.
 * Copyright © 2020 O2. All rights reserved.
 */
class ExceptionTimeEmpty extends PromptException {

    public ExceptionTimeEmpty() {
        super("전달된 시간 매개변수가 올바르지 않습니다!");
    }
}
