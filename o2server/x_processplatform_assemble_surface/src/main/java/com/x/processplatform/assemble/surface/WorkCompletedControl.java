package com.x.processplatform.assemble.surface;

import com.x.base.core.project.gson.GsonPropertyObject;

public abstract class WorkCompletedControl extends GsonPropertyObject {
	/* 조회 가능 여부 */
	private Boolean allowVisit;
	/* 열람 대기 처리 가능 여부 */
	private Boolean allowReadProcessing;
	/* 삭제 가능 여부 */
	private Boolean allowDelete;

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

	public Boolean getAllowDelete() {
		return allowDelete;
	}

	public void setAllowDelete(Boolean allowDelete) {
		this.allowDelete = allowDelete;
	}

	/** 관리 화면에서 문서 진행 직접 시도 가능 여부 */

}