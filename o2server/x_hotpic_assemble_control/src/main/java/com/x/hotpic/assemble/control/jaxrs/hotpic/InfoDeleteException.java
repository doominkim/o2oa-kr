package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoDeleteException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoDeleteException( Throwable e, String id ) {
		super("정보 ID로 정보 객체를 삭제하는 중 오류가 발생했습니다. ID:" + id, e );
	}
	
	InfoDeleteException( Throwable e, String application, String id ) {
		super("정보 ID로 정보 객체를 삭제하는 중 오류가 발생했습니다. Application:"+application+", ID:" + id, e );
	}
}
