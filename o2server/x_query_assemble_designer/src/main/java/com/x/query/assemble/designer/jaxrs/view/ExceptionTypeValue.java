package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.View;

class ExceptionTypeValue extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionTypeValue(String value) {
		super("유형 값은 {} 또는 {}여야 합니다. {} 값은 허용되지 않습니다.",View.TYPE_CMS ,  View.TYPE_PROCESSPLATFORM,value);
	}
}
