package com.x.attendance.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionDingdingFindNoArgumentError extends PromptException {
    public ExceptionDingdingFindNoArgumentError() {
        super("올바른 매개변수가 전달되지 않았습니다");
    }
}
