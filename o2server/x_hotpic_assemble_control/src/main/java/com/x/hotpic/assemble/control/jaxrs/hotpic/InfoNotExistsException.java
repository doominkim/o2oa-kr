package com.x.hotpic.assemble.control.jaxrs.hotpic;

import com.x.base.core.project.exception.PromptException;

class InfoNotExistsException extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	InfoNotExistsException( String id ) {
		super("지정한 ID의 정보가 없습니다. ID:" + id );
	}
	
	InfoNotExistsException( String application, String id ) {
		super("지정한 ID의 정보가 없습니다. Application:" +application+ ", ID:" + id );
	}
}
