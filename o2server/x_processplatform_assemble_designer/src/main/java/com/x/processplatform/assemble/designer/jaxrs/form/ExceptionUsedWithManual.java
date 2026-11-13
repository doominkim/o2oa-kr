package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionUsedWithManual extends LanguagePromptException {

	private static final long serialVersionUID = -9089355008820123519L;

	ExceptionUsedWithManual(String name, String id, List<String> names) {
		super("양식 name: {} id: {}을(를) 프로세스의 사람 처리 노드가 사용 중입니다: {}.", name, id, StringUtils.join(names, ","));
	}
}
