package com.x.attendance.assemble.control.jaxrs.qywx;

import com.x.base.core.project.exception.PromptException;

public class SyncDateException extends PromptException {


    private static final long serialVersionUID = -6409463169780597687L;

    public SyncDateException() {
        super("전달된 동기화 시간이 올바르지 않습니다!");
    }
}
