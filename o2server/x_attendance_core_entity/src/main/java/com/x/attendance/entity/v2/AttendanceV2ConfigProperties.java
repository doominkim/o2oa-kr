package com.x.attendance.entity.v2;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.Collections;
import java.util.List;

public class AttendanceV2ConfigProperties extends JsonProperties {

    private static final long serialVersionUID = 2401225663110698389L;

    final List<String> defaultList = Collections.singletonList("근태 관리자");

    @FieldDescribe("휴가 관리 기본 데이터 초기화 여부.")
    private Boolean leaveTypeInitialized = false;
    @FieldDescribe("퇴근 후 알림 분 수.")
    private Integer checkInAlertOffDutyAfterMinutes = 10;
    @FieldDescribe("출근 전 알림 분 수.")
    private Integer checkInAlertOnDutyBeforeMinutes = 10;
    @FieldDescribe("중국 공휴일 데이터 동기화 사용 여부")
    private Boolean chineseHolidaySyncEnable = true;
    @FieldDescribe("근태 관리자 직무 이름")
    private List<String> statisticUnitDutyNameList =  defaultList;
    @FieldDescribe("외근 체크 실행 스크립트")
    private String fieldWorkExecuteScript;


    public String getFieldWorkExecuteScript() {
        return fieldWorkExecuteScript;
    }

    public void setFieldWorkExecuteScript(String fieldWorkExecuteScript) {
        this.fieldWorkExecuteScript = fieldWorkExecuteScript;
    }

    public List<String> getStatisticUnitDutyNameList() {
        return statisticUnitDutyNameList;
    }

    public void setStatisticUnitDutyNameList(List<String> statisticUnitDutyNameList) {
        this.statisticUnitDutyNameList = statisticUnitDutyNameList;
    }

    public Integer getCheckInAlertOffDutyAfterMinutes() {
        return checkInAlertOffDutyAfterMinutes;
    }

    public void setCheckInAlertOffDutyAfterMinutes(Integer checkInAlertOffDutyAfterMinutes) {
        this.checkInAlertOffDutyAfterMinutes = checkInAlertOffDutyAfterMinutes;
    }

    public Integer getCheckInAlertOnDutyBeforeMinutes() {
        return checkInAlertOnDutyBeforeMinutes;
    }

    public void setCheckInAlertOnDutyBeforeMinutes(Integer checkInAlertOnDutyBeforeMinutes) {
        this.checkInAlertOnDutyBeforeMinutes = checkInAlertOnDutyBeforeMinutes;
    }

    public Boolean getLeaveTypeInitialized() {
        return leaveTypeInitialized;
    }


    public void setLeaveTypeInitialized(Boolean leaveTypeInitialized) {
        this.leaveTypeInitialized = leaveTypeInitialized;
    }

    public Boolean getChineseHolidaySyncEnable() {
        return chineseHolidaySyncEnable;
    }

    public void setChineseHolidaySyncEnable(Boolean chineseHolidaySyncEnable) {
        this.chineseHolidaySyncEnable = chineseHolidaySyncEnable;
    }
}
