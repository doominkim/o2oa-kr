package com.x.attendance.assemble.control.jaxrs.v2.shift;

import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2ShiftWorkTimeHelper;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTime;
import com.x.attendance.entity.v2.AttendanceV2ShiftCheckTimeProperties;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

abstract class BaseAction extends StandardJaxrsAction {

    protected void checkShiftProperties(AttendanceV2ShiftCheckTimeProperties properties) throws Exception {
        if (properties == null || properties.getTimeList() == null || properties.getTimeList().isEmpty()) {
            throw new ExceptionEmptyParameter("근무조 출퇴근 체크 시간");
        }
        List<AttendanceV2ShiftCheckTime> timeList = properties.getTimeList();
        AttendanceV2ShiftCheckTime time1 = timeList.get(0);
        checkOnDutyTimeAndOffDutyTime(time1);
        if (timeList.size() > 1) {
            checkOnDutyTimeAndOffDutyTime(timeList.get(1));
        }
        if (timeList.size() > 2) {
            checkOnDutyTimeAndOffDutyTime(timeList.get(2));
        }
    }

    // 근무 시간 계산
    protected long shiftWorkTime(AttendanceV2ShiftCheckTimeProperties properties) throws Exception {
        if (properties == null || properties.getTimeList() == null || properties.getTimeList().isEmpty()) {
            throw new ExceptionEmptyParameter("근무조 출퇴근 체크 시간");
        }
        return AttendanceV2ShiftWorkTimeHelper.shiftWorkMinutes(properties);
    }
    

    private void checkOnDutyTimeAndOffDutyTime(AttendanceV2ShiftCheckTime time) throws Exception {
        if (StringUtils.isEmpty(time.getOnDutyTime())) {
            throw new ExceptionEmptyParameter("출근 시간");
        }
        if (StringUtils.isEmpty(time.getOffDutyTime())) {
            throw new ExceptionEmptyParameter("퇴근 시간");
        }
        if (!AttendanceV2ShiftWorkTimeHelper.restPeriodAvailable(time.getRestPeriod())) {
            throw new ExceptionOnDutyOffDuty("휴식 시간 형식이 잘못되었습니다");
        }
        String onDuty = time.getOnDutyTime().replace(":", "");
        String offDuty = time.getOffDutyTime().replace(":", "");
        if (BooleanUtils.isTrue(time.getOffDutyNextDay())) {
            if (Integer.parseInt(offDuty) > Integer.parseInt(onDuty)) {
                throw new ExceptionOnDutyOffDuty("익일 퇴근 시간은 출근 시간보다 늦을 수 없습니다");
            }
        } else {
            if (Integer.parseInt(onDuty) > Integer.parseInt(offDuty)) {
                throw new ExceptionOnDutyOffDuty("출근 시간은 퇴근 시간보다 늦을 수 없습니다");
            }
        }
        if (StringUtils.isNotEmpty(time.getOnDutyTimeBeforeLimit())) {
            String onDutyBefore = time.getOnDutyTimeBeforeLimit().replace(":", "");
            if (Integer.parseInt(onDutyBefore) > Integer.parseInt(onDuty)) {
                throw new ExceptionOnDutyOffDuty("출근 전 시간은 출근 시간보다 늦을 수 없습니다");
            }
        }
        if (StringUtils.isNotEmpty(time.getOnDutyTimeAfterLimit())) {
            String onDutyAfter = time.getOnDutyTimeAfterLimit().replace(":", "");
            if (Integer.parseInt(onDutyAfter) < Integer.parseInt(onDuty)) {
                throw new ExceptionOnDutyOffDuty("출근 후 시간은 출근 시간보다 이를 수 없습니다");
            }
        }
        if (StringUtils.isNotEmpty(time.getOffDutyTimeBeforeLimit())) {
            String offDutyBefore = time.getOffDutyTimeBeforeLimit().replace(":", "");
            if (Integer.parseInt(offDutyBefore) > Integer.parseInt(offDuty)) {
                throw new ExceptionOnDutyOffDuty("퇴근 전 시간은 퇴근 시간보다 늦을 수 없습니다");
            }
        }
        if (StringUtils.isNotEmpty(time.getOffDutyTimeAfterLimit())) {
            String offDutyAfter = time.getOffDutyTimeAfterLimit().replace(":", "");
            if (Integer.parseInt(offDutyAfter) < Integer.parseInt(offDuty)) {
                throw new ExceptionOnDutyOffDuty("퇴근 후 시간은 퇴근 시간보다 이를 수 없습니다");
            }
        }

    }
}
