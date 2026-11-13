package com.x.attendance.assemble.control.jaxrs.attendanceimportfileinfo;

import com.x.base.core.project.exception.PromptException;

class ExceptionImportFileNotExists extends PromptException {

	private static final long serialVersionUID = 1859164370743532895L;

	public ExceptionImportFileNotExists( String id ) {
		super("지정한 인원 근태 데이터 가져오기 파일 정보가 없습니다. ID:" + id );
	}
}
