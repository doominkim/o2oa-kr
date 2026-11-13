package com.x.program.center.jaxrs.apppack;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * Created by fancyLou on 6/15/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ExceptionFileNameEmpty extends LanguagePromptException {

    private static final long serialVersionUID = -8045803301464102031L;

    ExceptionFileNameEmpty(){ super("파일 이름은 비워 둘 수 없습니다!"); }
}
