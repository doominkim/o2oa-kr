package com.x.attendance.assemble.control.schedule.v2;

import com.x.base.core.project.exception.PromptException;

/**
 * Created by fancyLou on 2023/2/24.
 * Copyright © 2023 O2. All rights reserved.
 */
public class ExceptionQueueAttendanceV2Detail extends PromptException {


    private static final long serialVersionUID = -2347831783819851010L;

    public ExceptionQueueAttendanceV2Detail(String message) {
        super(  "근태 V2 근태 데이터 처리 중 오류가 발생했습니다:"+message );
    }
}
