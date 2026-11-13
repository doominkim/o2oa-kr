package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class MoreThanSevenDayException extends PromptException {


    private static final long serialVersionUID = 7077761236199564642L;

    public MoreThanSevenDayException() {
        super("동기화 간격이 7일을 초과합니다!");
    }
}
