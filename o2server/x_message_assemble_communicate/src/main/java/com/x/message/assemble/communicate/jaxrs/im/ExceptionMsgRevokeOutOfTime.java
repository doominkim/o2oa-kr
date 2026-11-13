package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.project.exception.PromptException;

public class ExceptionMsgRevokeOutOfTime extends PromptException {

    public ExceptionMsgRevokeOutOfTime() {
        super("메시지 회수 가능 시간이 지났습니다!");
    }
}
