package com.x.query.service.processing.jaxrs.table;

import com.x.base.core.project.exception.LanguagePromptException;

public class ExceptionBundleEmpty extends LanguagePromptException {

	private static final long serialVersionUID = -87643358931771164L;

	public ExceptionBundleEmpty() {
		super("식별자는 비워 둘 수 없습니다.");
	}

}
