package com.x.organization.assemble.control.jaxrs.inputperson;

import com.x.base.core.project.exception.PromptException;

public class ExceptionInputResultObject extends PromptException {

    private static final long serialVersionUID = 9085364457175859374L;

    ExceptionInputResultObject(String flag) {
        super("객체가 없습니다: {}.", flag);
    }

}
