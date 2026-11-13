package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;

public class WorkTime extends ConfigObject {

	public static WorkTime defaultInstance() {
		WorkTime o = new WorkTime();
		return o;
	}

	public WorkTime() {
		this.amStart = "09:00:00";
		this.amEnd = "11:30:00";
		this.pmStart = "13:00:00";
		this.pmEnd = "17:30:00";
		this.weekends = new int[] { 1, 7 };
		this.holidays = new String[] {};
		this.workdays = new String[] {};
	}

	@FieldDescribe("근무 시간 오전 시작 시간")
	private String amStart;
	@FieldDescribe("근무 시간 오전 종료 시간")
	private String amEnd;
	@FieldDescribe("근무 시간 오후 시작 시간")
	private String pmStart;
	@FieldDescribe("근무 시간 오후 종료 시간")
	private String pmEnd;
	@FieldDescribe("고정 공휴일, 형식은 [\"2019-01-01\",\"2019-05-01\"]")
	private String[] holidays;
	@FieldDescribe("고정 근무일, 형식은 [\"2019-01-01\",\"2019-05-01\"]")
	private String[] workdays;
	@FieldDescribe("주말 설정, 기본 형식은 [1,7]이며 1은 일요일, 7은 토요일.")
	private int[] weekends;

	public String getAmStart() {
		return amStart;
	}

	public void setAmStart(String amStart) {
		this.amStart = amStart;
	}

	public String getAmEnd() {
		return amEnd;
	}

	public void setAmEnd(String amEnd) {
		this.amEnd = amEnd;
	}

	public String getPmStart() {
		return pmStart;
	}

	public void setPmStart(String pmStart) {
		this.pmStart = pmStart;
	}

	public String getPmEnd() {
		return pmEnd;
	}

	public void setPmEnd(String pmEnd) {
		this.pmEnd = pmEnd;
	}

	public String[] getHolidays() {
		return holidays;
	}

	public void setHolidays(String[] holidays) {
		this.holidays = holidays;
	}

	public String[] getWorkdays() {
		return workdays;
	}

	public void setWorkdays(String[] workdays) {
		this.workdays = workdays;
	}

	public int[] getWeekends() {
		return weekends;
	}

	public void setWeekends(int[] weekends) {
		this.weekends = weekends;
	}

}
