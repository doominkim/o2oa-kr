package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class AttendanceV2PersonConfigProperties extends JsonProperties{

    private static final long serialVersionUID = -3073887599579463766L;
    @FieldDescribe("출근 전 출퇴근 체크 메시지 알림.")
    private Boolean checkInAlertOnDutyEnable = true;

    @FieldDescribe("퇴근 후 출퇴근 체크 메시지 알림.")
    private Boolean checkInAlertOffDutyEnable = true;


    public Boolean getCheckInAlertOnDutyEnable() {
        return checkInAlertOnDutyEnable;
    }

    public void setCheckInAlertOnDutyEnable(Boolean checkInAlertOnDutyEnable) {
        this.checkInAlertOnDutyEnable = checkInAlertOnDutyEnable;
    }

    public Boolean getCheckInAlertOffDutyEnable() {
        return checkInAlertOffDutyEnable;
    }

    public void setCheckInAlertOffDutyEnable(Boolean checkInAlertOffDutyEnable) {
        this.checkInAlertOffDutyEnable = checkInAlertOffDutyEnable;
    }

}
