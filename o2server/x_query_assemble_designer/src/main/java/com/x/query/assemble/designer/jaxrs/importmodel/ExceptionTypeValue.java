package com.x.query.assemble.designer.jaxrs.importmodel;

import com.x.base.core.project.exception.LanguagePromptException;
import com.x.query.core.entity.ImportModel;

class ExceptionTypeValue extends LanguagePromptException {

	private static final long serialVersionUID = -4641740144236932958L;

	ExceptionTypeValue(String value) {
		super("유형 값은 {}, {} 또는 {}여야 합니다. {} 값은 허용되지 않습니다.",ImportModel.TYPE_CMS , ImportModel.TYPE_PROCESSPLATFORM, ImportModel.TYPE_DYNAMIC_TABLE,value);
	}
}
