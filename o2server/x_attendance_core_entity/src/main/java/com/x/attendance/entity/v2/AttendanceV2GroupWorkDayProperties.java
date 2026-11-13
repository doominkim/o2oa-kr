package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.DateTools;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Created by fancyLou on 2023/3/17.
 * Copyright © 2023 O2. All rights reserved.
 */
public class AttendanceV2GroupWorkDayProperties extends JsonProperties {

    @FieldDescribe("일요일")
    private AttendanceV2GroupWorkDay sunday;
    @FieldDescribe("월요일")
    private AttendanceV2GroupWorkDay monday;
    @FieldDescribe("화요일")
    private AttendanceV2GroupWorkDay tuesday;
    @FieldDescribe("수요일")
    private AttendanceV2GroupWorkDay wednesday;
    @FieldDescribe("목요일")
    private AttendanceV2GroupWorkDay thursday;
    @FieldDescribe("금요일")
    private AttendanceV2GroupWorkDay friday;
    @FieldDescribe("토요일")
    private AttendanceV2GroupWorkDay saturday;


    /**
     * 날짜로 근무 id 조회
     * @param date
     * @return
     */
    public String shiftIdWithDate(Date date) {
        // 요일
        try {
            AttendanceV2GroupWorkDay workDay = null;
            int day = DateTools.dayForWeekAttendanceV2(date);
            switch (day){
                case 0:
                    workDay = getSunday();
                    break;
                case 1:
                    workDay = getMonday();
                    break;
                case 2:
                    workDay = getTuesday();
                    break;
                case 3:
                    workDay = getWednesday();
                    break;
                case 4:
                    workDay = getThursday();
                    break;
                case 5:
                    workDay = getFriday();
                    break;
                case 6:
                    workDay = getSaturday();
                    break;
            }
            if (workDay != null) {
                return workDay.getShiftId();
            }
        } catch (Exception ignore) { }
        return null;
    }

    /**
     * 데이터 존재 여부 판단
     * 선택되어 있고 선택한 날짜에 근무가 설정되어 있음
     * @return
     */
    public boolean validateNotEmpty() {
        if (getSunday() != null) {
            if (getSunday().isChecked() && StringUtils.isNotEmpty(getSunday().getShiftId())) {
                return true;
            }
        }
        if (getMonday() != null) {
            if (getMonday().isChecked() && StringUtils.isNotEmpty(getMonday().getShiftId())) {
                return true;
            }
        }
        if (getTuesday() != null) {
            if (getTuesday().isChecked() && StringUtils.isNotEmpty(getTuesday().getShiftId())) {
                return true;
            }
        }
        if (getWednesday() != null) {
            if (getWednesday().isChecked() && StringUtils.isNotEmpty(getWednesday().getShiftId())) {
                return true;
            }
        }
        if (getThursday() != null) {
            if (getThursday().isChecked() && StringUtils.isNotEmpty(getThursday().getShiftId())) {
                return true;
            }
        }
        if (getFriday() != null) {
            if (getFriday().isChecked() && StringUtils.isNotEmpty(getFriday().getShiftId())) {
                return true;
            }
        }
        if (getSaturday() != null) {
            if (getSaturday().isChecked() && StringUtils.isNotEmpty(getSaturday().getShiftId())) {
                return true;
            }
        }
        return false;
    }




    public AttendanceV2GroupWorkDay getSunday() {
        return sunday;
    }

    public void setSunday(AttendanceV2GroupWorkDay sunday) {
        this.sunday = sunday;
    }

    public AttendanceV2GroupWorkDay getMonday() {
        return monday;
    }

    public void setMonday(AttendanceV2GroupWorkDay monday) {
        this.monday = monday;
    }

    public AttendanceV2GroupWorkDay getTuesday() {
        return tuesday;
    }

    public void setTuesday(AttendanceV2GroupWorkDay tuesday) {
        this.tuesday = tuesday;
    }

    public AttendanceV2GroupWorkDay getWednesday() {
        return wednesday;
    }

    public void setWednesday(AttendanceV2GroupWorkDay wednesday) {
        this.wednesday = wednesday;
    }

    public AttendanceV2GroupWorkDay getThursday() {
        return thursday;
    }

    public void setThursday(AttendanceV2GroupWorkDay thursday) {
        this.thursday = thursday;
    }

    public AttendanceV2GroupWorkDay getFriday() {
        return friday;
    }

    public void setFriday(AttendanceV2GroupWorkDay friday) {
        this.friday = friday;
    }

    public AttendanceV2GroupWorkDay getSaturday() {
        return saturday;
    }

    public void setSaturday(AttendanceV2GroupWorkDay saturday) {
        this.saturday = saturday;
    }

    public static class AttendanceV2GroupWorkDay extends GsonPropertyObject {

        @FieldDescribe("체크 필요 여부")
        private boolean checked; //
        @FieldDescribe("근무 id")
        private String shiftId; //
        @FieldDescribe("근무조 객체") // , 저장하지 않고 프런트엔드 반환 시 제공
        private AttendanceV2Shift shift;


        public AttendanceV2Shift getShift() {
            return shift;
        }

        public void setShift(AttendanceV2Shift shift) {
            this.shift = shift;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getShiftId() {
            return shiftId;
        }

        public void setShiftId(String shiftId) {
            this.shiftId = shiftId;
        }
    }

}
