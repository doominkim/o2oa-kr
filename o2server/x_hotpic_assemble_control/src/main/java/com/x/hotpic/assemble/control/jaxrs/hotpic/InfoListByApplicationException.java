package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoListByApplicationException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoListByApplicationException( Throwable e, String application ) {
		super("앱 이름으로 정보 객체 목록을 조회하는 중 오류가 발생했습니다. Application:" + application, e );
	}
	
	InfoListByApplicationException( Throwable e, String application, String id ) {
		super("앱 이름으로 정보 객체 목록을 조회하는 중 오류가 발생했습니다. Application:" + application + ", InfoId:" + id , e );
	}
}
