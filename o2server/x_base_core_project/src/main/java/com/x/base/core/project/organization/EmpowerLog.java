package com.x.base.core.project.organization;

import java.util.Date;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class EmpowerLog extends GsonPropertyObject {

	private static final long serialVersionUID = -2962512888249730611L;

	@FieldDescribe("신분.")
	private String fromIdentity;

	@FieldDescribe("위임 신분.")
	private String toIdentity;

	@FieldDescribe("앱.")
	private String application;

	@FieldDescribe("앱 이름.")
	private String applicationName;

	@FieldDescribe("앱 별칭.")
	private String applicationAlias;

	@FieldDescribe("프로세스 ID.")
	private String process;

	@FieldDescribe("프로세스 이름.")
	private String processName;

	@FieldDescribe("프로세스 별칭.")
	private String processAlias;

	@FieldDescribe("현재 활동 ID")
	private String activity;

	@FieldDescribe("활동 이름")
	private String activityName;

	@FieldDescribe("활동 별칭")
	private String activityAlias;

	@FieldDescribe("작업 식별자.")
	private String job;

	@FieldDescribe("work 식별자.")
	private String work;

	@FieldDescribe("위임 시간.")
	private Date empowerTime;

	@FieldDescribe("제목.")
	private String title;

	public EmpowerLog setFromIdentity(String fromIdentity) {
		this.fromIdentity = fromIdentity;
		return this;
	}

	public EmpowerLog setToIdentity(String toIdentity) {
		this.toIdentity = toIdentity;
		return this;
	}

	public EmpowerLog setApplication(String application) {
		this.application = application;
		return this;
	}

	public EmpowerLog setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public EmpowerLog setApplicationAlias(String applicationAlias) {
		this.applicationAlias = applicationAlias;
		return this;
	}

	public EmpowerLog setProcessName(String processName) {
		this.processName = processName;
		return this;
	}

	public EmpowerLog setProcessAlias(String processAlias) {
		this.processAlias = processAlias;
		return this;
	}

	public EmpowerLog setProcess(String process) {
		this.process = process;
		return this;
	}

	public EmpowerLog setTitle(String title) {
		this.title = title;
		return this;
	}

	public EmpowerLog setJob(String job) {
		this.job = job;
		return this;
	}

	public EmpowerLog setWork(String work) {
		this.work = work;
		return this;
	}

	public EmpowerLog setEmpowerTime(Date empowerTime) {
		this.empowerTime = empowerTime;
		return this;
	}

	public EmpowerLog setActivity(String activity) {
		this.activity = activity;
		return this;
	}

	public EmpowerLog setActivityName(String activityName) {
		this.activityName = activityName;
		return this;
	}

	public EmpowerLog setActivityAlias(String activityAlias) {
		this.activityAlias = activityAlias;
		return this;
	}

	public String getFromIdentity() {
		return fromIdentity;
	}

	public String getToIdentity() {
		return toIdentity;
	}

	public String getApplication() {
		return application;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public String getApplicationAlias() {
		return applicationAlias;
	}

	public String getProcess() {
		return process;
	}

	public String getProcessName() {
		return processName;
	}

	public String getProcessAlias() {
		return processAlias;
	}

	public String getJob() {
		return job;
	}

	public String getWork() {
		return work;
	}

	public Date getEmpowerTime() {
		return empowerTime;
	}

	public String getTitle() {
		return title;
	}

	public String getActivity() {
		return activity;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getActivityAlias() {
		return activityAlias;
	}

}