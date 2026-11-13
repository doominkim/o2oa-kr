package com.x.attendance.entity.v2;

import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 출퇴근 시간 객체
 * Created by fancyLou on 2023/1/30.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2ShiftCheckTime  extends GsonPropertyObject {

    private static final long serialVersionUID = -6411723100579845550L;
    private String onDutyTime; // 출근 체크 시간 예: 09:00
    private String onDutyTimeBeforeLimit; // onDutyTime 이전 체크 제한
    private String onDutyTimeAfterLimit; // onDutyTime 이후 체크 제한
    private String offDutyTime; // 퇴근 체크 시간 예: 18:00
    private String offDutyTimeBeforeLimit; // offDutyTime 이전 체크 제한
    private String offDutyTimeAfterLimit; // offDutyTime 이후 체크 제한
    private Boolean offDutyNextDay = false; // 퇴근 체크의 익일 여부, 퇴근 체크는 날짜를 넘길 수 있음
    // 휴식 시간대, 형식은 "HH:mm-HH:mm", 예: "12:00-13:00"은 점심 휴식 시간이 12:00부터 13:00까지임을 의미
    private String restPeriod;

    public String getRestPeriod() {
        return restPeriod;
    }

    public void setRestPeriod(String restPeriod) {
        this.restPeriod = restPeriod;
    }

    public String getOnDutyTime() {
        return onDutyTime;
    }

    public void setOnDutyTime(String onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    public String getOnDutyTimeBeforeLimit() {
        return onDutyTimeBeforeLimit;
    }

    public void setOnDutyTimeBeforeLimit(String onDutyTimeBeforeLimit) {
        this.onDutyTimeBeforeLimit = onDutyTimeBeforeLimit;
    }

    public String getOnDutyTimeAfterLimit() {
        return onDutyTimeAfterLimit;
    }

    public void setOnDutyTimeAfterLimit(String onDutyTimeAfterLimit) {
        this.onDutyTimeAfterLimit = onDutyTimeAfterLimit;
    }

    public String getOffDutyTime() {
        return offDutyTime;
    }

    public void setOffDutyTime(String offDutyTime) {
        this.offDutyTime = offDutyTime;
    }

    public String getOffDutyTimeBeforeLimit() {
        return offDutyTimeBeforeLimit;
    }

    public void setOffDutyTimeBeforeLimit(String offDutyTimeBeforeLimit) {
        this.offDutyTimeBeforeLimit = offDutyTimeBeforeLimit;
    }

    public String getOffDutyTimeAfterLimit() {
        return offDutyTimeAfterLimit;
    }

    public void setOffDutyTimeAfterLimit(String offDutyTimeAfterLimit) {
        this.offDutyTimeAfterLimit = offDutyTimeAfterLimit;
    }

	public Boolean getOffDutyNextDay() {
		return offDutyNextDay;
	}

	public void setOffDutyNextDay(Boolean offDutyNextDay) {
		this.offDutyNextDay = offDutyNextDay;
	}

	 
}
