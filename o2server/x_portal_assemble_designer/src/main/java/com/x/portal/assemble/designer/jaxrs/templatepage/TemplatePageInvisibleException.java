package com.x.portal.assemble.designer.jaxrs.templatepage;

import com.x.base.core.project.exception.LanguagePromptException;

class TemplatePageInvisibleException extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	TemplatePageInvisibleException(String person, String name, String id) {
		super("사용자: {}에게 이 템플릿 페이지: {} id: {}에 접근할 권한이 없습니다.", person, name, id);
	}
}
