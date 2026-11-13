package com.x.base.core.project.exception;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionDuplicateRestrictFlag extends LanguagePromptException {

	private static final long serialVersionUID = 1051687204485351261L;

	public static String defaultMessage = "엔터티 클래스: {}, 식별자: {}, 지정한 유형의 기존 값과 중복됩니다.";

	public <T extends JpaObject> ExceptionDuplicateRestrictFlag(Class<T> cls, String value) {
		super(defaultMessage, cls.getName(), value);
	}

	public <T extends JpaObject> ExceptionDuplicateRestrictFlag(Class<T> cls, List<String> values) {
		super(defaultMessage, cls.getName(), StringUtils.join(values, ","));
	}

}
