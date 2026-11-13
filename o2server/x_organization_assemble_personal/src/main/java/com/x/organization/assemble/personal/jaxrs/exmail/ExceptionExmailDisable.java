package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.exception.LanguagePromptException;

/**
 * 
 * @author ray
 *
 */
class ExceptionExmailDisable extends LanguagePromptException {

	private static final long serialVersionUID = -3439770681867963457L;

	ExceptionExmailDisable() {
		super("Tencent 기업 메일이 사용 설정되어 있지 않습니다.");
	}
}
