package com.x.attendance.assemble.control.jaxrs.v2.detail.model;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 2023/3/15.
 * Copyright © 2023 O2. All rights reserved.
 */
public class StatisticWo extends GsonPropertyObject {


    @FieldDescribe("사용자 식별자")
    private String userId;

    @FieldDescribe("근무 시간(분)")
    private Long workTimeDuration = 0L;
    @FieldDescribe("평균 근무 시간, 근무 시간/근무일 수, (시간)") //
    private String averageWorkTimeDuration = "0.0";
    @FieldDescribe("출근 대상 일수")
    private Integer workDays = 0;
    @FieldDescribe("출근 일수")
    private Integer attendance = 0;
    @FieldDescribe("휴무 일수")
    private Integer rest = 0;
    @FieldDescribe("무단결근 일수")
    private Integer absenteeismDays = 0;
    @FieldDescribe("지각 횟수")
    private Integer lateTimes = 0;
    @FieldDescribe("조퇴 횟수")//
    private Integer leaveEarlierTimes = 0;
    @FieldDescribe("미체크 횟수") // 출근·퇴근 횟수 합계
    private Integer absenceTimes = 0;
    @FieldDescribe("외근 체크 횟수")
    private Integer fieldWorkTimes = 0;
    @FieldDescribe("휴가 일수")
    private Integer leaveDays = 0;
    @FieldDescribe("이의 신청 횟수")
    private Integer appealNums = 0;

    @FieldDescribe("일별 데이터")
    private List<DetailWo> detailList;


    public Integer getWorkDays() {
        return workDays;
    }

    public void setWorkDays(Integer workDays) {
        this.workDays = workDays;
    }

    public Integer getFieldWorkTimes() {
        return fieldWorkTimes;
    }

    public void setFieldWorkTimes(Integer fieldWorkTimes) {
        this.fieldWorkTimes = fieldWorkTimes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    public String getAverageWorkTimeDuration() {
        return averageWorkTimeDuration;
    }

    public void setAverageWorkTimeDuration(String averageWorkTimeDuration) {
        this.averageWorkTimeDuration = averageWorkTimeDuration;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getAbsenteeismDays() {
        return absenteeismDays;
    }

    public void setAbsenteeismDays(Integer absenteeismDays) {
        this.absenteeismDays = absenteeismDays;
    }

    public Integer getLateTimes() {
        return lateTimes;
    }

    public void setLateTimes(Integer lateTimes) {
        this.lateTimes = lateTimes;
    }

    public Integer getLeaveEarlierTimes() {
        return leaveEarlierTimes;
    }

    public void setLeaveEarlierTimes(Integer leaveEarlierTimes) {
        this.leaveEarlierTimes = leaveEarlierTimes;
    }

    public Integer getAbsenceTimes() {
        return absenceTimes;
    }

    public void setAbsenceTimes(Integer absenceTimes) {
        this.absenceTimes = absenceTimes;
    }

    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getAppealNums() {
        return appealNums;
    }

    public void setAppealNums(Integer appealNums) {
        this.appealNums = appealNums;
    }

    public List<DetailWo> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<DetailWo> detailList) {
        this.detailList = detailList;
    }
}
