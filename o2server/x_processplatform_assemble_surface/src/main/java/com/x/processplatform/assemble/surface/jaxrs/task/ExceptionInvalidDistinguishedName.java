package com.x.processplatform.assemble.surface.jaxrs.task;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionInvalidDistinguishedName extends LanguagePromptException {

	private static final long serialVersionUID = -5515077418025884395L;

	ExceptionInvalidDistinguishedName(List<String> names) {
		super("유효하지 않은 고유 조직 식별자: {}.", StringUtils.join(names, ","));
	}

}
