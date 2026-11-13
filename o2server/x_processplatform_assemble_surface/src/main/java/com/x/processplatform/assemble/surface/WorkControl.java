package com.x.processplatform.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class WorkControl extends GsonPropertyObject {

	private static final long serialVersionUID = -3251746953649257039L;
	/* 조회 가능 여부 */
	private Boolean allowVisit;
	/* 직접 진행 가능 여부 */
	private Boolean allowProcessing;
	/* 열람 대기 처리 가능 여부 */
	private Boolean allowReadProcessing;
	/* 데이터 저장 가능 여부 */
	private Boolean allowSave;
	/* 처리자 재설정 가능 여부 */
	private Boolean allowReset;
	/* 열람 대기 처리자 가능 여부 */
	private Boolean allowReadReset;
	/* 회수 가능 여부 */
	private Boolean allowRetract;
	/* 조정 가능 여부 */
	private Boolean allowReroute;
	/* 삭제 가능 여부 */
	private Boolean allowDelete;

	/** 관리 화면에서 문서 진행 직접 시도 가능 여부 */

	// private Boolean allowManageProcessing;

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

	public Boolean getAllowRetract() {
		return allowRetract;
	}

	public void setAllowRetract(Boolean allowRetract) {
		this.allowRetract = allowRetract;
	}

	public Boolean getAllowReroute() {
		return allowReroute;
	}

	public void setAllowReroute(Boolean allowReroute) {
		this.allowReroute = allowReroute;
	}

	public Boolean getAllowProcessing() {
		return allowProcessing;
	}

	public void setAllowProcessing(Boolean allowProcessing) {
		this.allowProcessing = allowProcessing;
	}

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	public Boolean getAllowVisit() {
		return allowVisit;
	}

	public void setAllowVisit(Boolean allowVisit) {
		this.allowVisit = allowVisit;
	}

	public Boolean getAllowReadProcessing() {
		return allowReadProcessing;
	}

	public void setAllowReadProcessing(Boolean allowReadProcessing) {
		this.allowReadProcessing = allowReadProcessing;
	}

	public Boolean getAllowReadReset() {
		return allowReadReset;
	}

	public void setAllowReadReset(Boolean allowReadReset) {
		this.allowReadReset = allowReadReset;
	}

}