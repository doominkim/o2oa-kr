package com.x.bbs.assemble.control.jaxrs.configsetting.exception;

import com.x.base.core.project.exception.PromptException;

public class ExceptionConfigSettingNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionConfigSettingNotExists( String id ) {
		super("지정한 ID의 BBS 시스템 설정이 없습니다. ID:" + id );
	}
}
