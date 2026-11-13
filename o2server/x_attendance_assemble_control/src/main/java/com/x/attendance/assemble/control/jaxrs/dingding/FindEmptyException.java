package com.x.attendance.assemble.control.jaxrs.dingding;

import com.x.base.core.project.exception.PromptException;

public class FindEmptyException  extends PromptException {


    private static final long serialVersionUID = 4335298261104588212L;

    public FindEmptyException() {
        super("데이터를 찾을 수 없습니다!");
    }
}
