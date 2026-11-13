package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInvalidServerAddress extends PromptException {

    private static final long serialVersionUID = -1367282830558670567L;

    ExceptionInvalidServerAddress(String ip) {
        super("프록시 IP: {}이(가) 규칙에 맞지 않습니다. 관리자에게 문의하세요.", ip);
    }
}
