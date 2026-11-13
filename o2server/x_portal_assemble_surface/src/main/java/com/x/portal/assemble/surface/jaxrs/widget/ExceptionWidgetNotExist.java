package com.x.portal.assemble.surface.jaxrs.widget;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionWidgetNotExist extends LanguagePromptException {

	private static final long serialVersionUID = -4908883340253465376L;

	ExceptionWidgetNotExist(String id) {
		super("지정한 위젯이 없습니다: {}.", id);
	}

}
