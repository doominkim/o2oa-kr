package com.x.cms.assemble.control.jaxrs.document;

import com.x.base.core.project.exception.LanguagePromptException;

class ExceptionExcelRead extends LanguagePromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	ExceptionExcelRead( Throwable e ) {
		super("EXCEL 파일에서 데이터를 가져오는 중 오류가 발생했습니다." , e );
	}
}
