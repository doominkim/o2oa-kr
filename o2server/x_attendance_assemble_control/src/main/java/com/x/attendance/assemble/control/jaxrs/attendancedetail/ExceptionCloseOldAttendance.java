package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import com.x.base.core.project.exception.PromptException;

public class ExceptionCloseOldAttendance extends PromptException {

  private static final long serialVersionUID = 82779070886073828L;
  
  public ExceptionCloseOldAttendance() {
    super("구버전 근태는 종료되었습니다. 새 근태를 사용하십시오");
  }
}
