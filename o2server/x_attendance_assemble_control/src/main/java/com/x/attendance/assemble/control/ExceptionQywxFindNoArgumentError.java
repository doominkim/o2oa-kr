package com.x.attendance.assemble.control;

import com.x.base.core.project.exception.PromptException;

class ExceptionQywxFindNoArgumentError extends PromptException {
    public ExceptionQywxFindNoArgumentError() {
        super("올바른 매개변수가 전달되지 않았습니다");
    }
}
