package com.x.processplatform.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Control extends GsonPropertyObject {

	private static final long serialVersionUID = -4033469981088793137L;

	// 관리 가능 여부
	private Boolean allowManage;
	// 조회 가능 여부
	private Boolean allowVisit;
	// 직접 진행 가능 여부
	private Boolean allowProcessing;
	// 열람 대기 처리 가능 여부
	private Boolean allowReadProcessing;
	// 데이터 저장 가능 여부
	private Boolean allowSave;
	// 처리자 재설정 가능 여부
	private Boolean allowReset;
	// 추가 결재자 지정 가능 여부
	private Boolean allowAddTask;
	// 조정 가능 여부
	private Boolean allowReroute;
	// 삭제 가능 여부
	private Boolean allowDelete;
	// 합의 분기 추가 가능 여부
	private Boolean allowAddSplit;
	// 회수 가능 여부
	private Boolean allowRetract;
	// 회수 가능 여부 V3
	private Boolean allowV3Retract;
	// 롤백 가능 여부
	private Boolean allowRollback;
	// 알림 가능 여부
	private Boolean allowPress;
	// 결재 대기 보류(결재 대기 시간 측정 일시정지) 가능 여부
	private Boolean allowPause;
	// 결재 대기 보류 취소(결재 대기 시간 측정 재개) 가능 여부
	private Boolean allowResume;
	// 반려 가능 여부
	private Boolean allowGoBack;
	// 종료 가능 여부
	private Boolean allowTerminate;

	/**
	 * 권한은 반드시 조회하며, 아래 두 필드는 재조회하지 않음 업무 문서 제목
	 */
	private String workTitle;
	private String creatorPerson;
	// 업무 문서의 job
	private String workJob;

	public Boolean getAllowManage() {
		return allowManage;
	}

	public void setAllowManage(Boolean allowManage) {
		this.allowManage = allowManage;
	}

	public Boolean getAllowVisit() {
		return allowVisit;
	}

	public void setAllowVisit(Boolean allowVisit) {
		this.allowVisit = allowVisit;
	}

	public Boolean getAllowProcessing() {
		return allowProcessing;
	}

	public void setAllowProcessing(Boolean allowProcessing) {
		this.allowProcessing = allowProcessing;
	}

	public Boolean getAllowReadProcessing() {
		return allowReadProcessing;
	}

	public void setAllowReadProcessing(Boolean allowReadProcessing) {
		this.allowReadProcessing = allowReadProcessing;
	}

	public Boolean getAllowSave() {
		return allowSave;
	}

	public void setAllowSave(Boolean allowSave) {
		this.allowSave = allowSave;
	}

	public Boolean getAllowReset() {
		return allowReset;
	}

	public void setAllowReset(Boolean allowReset) {
		this.allowReset = allowReset;
	}

	public Boolean getAllowAddTask() {
		return allowAddTask;
	}

	public void setAllowAddTask(Boolean allowAddTask) {
		this.allowAddTask = allowAddTask;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public Boolean getAllowAddSplit() {
		return allowAddSplit;
	}

	public void setAllowAddSplit(Boolean allowAddSplit) {
		this.allowAddSplit = allowAddSplit;
	}

	public Boolean getAllowRetract() {
		return allowRetract;
	}

	public void setAllowRetract(Boolean allowRetract) {
		this.allowRetract = allowRetract;
	}

	public Boolean getAllowV3Retract() {
		return allowV3Retract;
	}

	public void setAllowV3Retract(Boolean allowV3Retract) {
		this.allowV3Retract = allowV3Retract;
	}

	public Boolean getAllowRollback() {
		return allowRollback;
	}

	public void setAllowRollback(Boolean allowRollback) {
		this.allowRollback = allowRollback;
	}

	public Boolean getAllowPress() {
		return allowPress;
	}

	public void setAllowPress(Boolean allowPress) {
		this.allowPress = allowPress;
	}

	public Boolean getAllowPause() {
		return allowPause;
	}

	public void setAllowPause(Boolean allowPause) {
		this.allowPause = allowPause;
	}

	public Boolean getAllowResume() {
		return allowResume;
	}

	public void setAllowResume(Boolean allowResume) {
		this.allowResume = allowResume;
	}

	public Boolean getAllowGoBack() {
		return allowGoBack;
	}

	public void setAllowGoBack(Boolean allowGoBack) {
		this.allowGoBack = allowGoBack;
	}

	public Boolean getAllowTerminate() {
		return allowTerminate;
	}

	public void setAllowTerminate(Boolean allowTerminate) {
		this.allowTerminate = allowTerminate;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getWorkJob() {
		return workJob;
	}

	public void setWorkJob(String workJob) {
		this.workJob = workJob;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}
}
