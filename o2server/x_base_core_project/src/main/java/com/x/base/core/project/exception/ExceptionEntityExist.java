package com.x.base.core.project.exception;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;

public class ExceptionEntityExist extends LanguagePromptException {

	private static final long serialVersionUID = -7099690129755484583L;

	public static String defaultMessage = "식별자: {} 인 {} 객체가 이미 있습니다.";

	public ExceptionEntityExist(String flag, JpaObject jpa) {
		super(defaultMessage, flag, (null == jpa) ? null : jpa.nameOfEntity());
	}

	public <T extends JpaObject> ExceptionEntityExist(String flag, Class<T> cls) {
		super(defaultMessage, flag, (null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityExist(List<String> ids, Class<T> cls) {
		super(defaultMessage, (null == ids) ? null : StringUtils.join(ids, ","),
				(null == cls) ? null : cls.getSimpleName());
	}

	public <T extends JpaObject> ExceptionEntityExist(String flag, String name) {
		super(defaultMessage, flag, name);
	}

	public <T extends JpaObject> ExceptionEntityExist(String flag) {
		super("식별자: {} 인 객체가 이미 있습니다.", flag);
		this.setLanguageKey(this.getClass().getName()+"_1");
	}

}
