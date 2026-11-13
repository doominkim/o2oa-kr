package com.x.processplatform.core.express.service.processing.jaxrs.workcompleted;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class ActionShiftTimeWi extends GsonPropertyObject {

	private static final long serialVersionUID = 2040132891703254119L;

	@FieldDescribe("완료된 업무 문서 식별자")
	private String id;

	@FieldDescribe("조정 분 수, 음수이면 앞으로 조정")
	private Integer adjustMinutes;

	@FieldDescribe("완료된 업무 문서 조정 여부")
	private Boolean workCompletedEnable = true;

	@FieldDescribe("Data 조정 여부")
	private Boolean dataEnable = true;

	@FieldDescribe("업무 문서 로그 조정 여부")
	private Boolean recordEnable = true;

	@FieldDescribe("처리 완료 조정 여부")
	private Boolean taskCompletedEnable = true;

	@FieldDescribe("열람 대기 조정 여부")
	private Boolean readEnable = true;

	@FieldDescribe("열람 완료 조정 여부")
	private Boolean readCompletedEnable = true;

	@FieldDescribe("참조 열람 조정 여부")
	private Boolean reviewEnable = true;

	@FieldDescribe("첨부파일 조정 여부")
	private Boolean attachmentEnable = true;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAdjustMinutes() {
		return adjustMinutes;
	}

	public void setAdjustMinutes(Integer adjustMinutes) {
		this.adjustMinutes = adjustMinutes;
	}

	public Boolean getWorkCompletedEnable() {
		return workCompletedEnable;
	}

	public void setWorkCompletedEnable(Boolean workCompletedEnable) {
		this.workCompletedEnable = workCompletedEnable;
	}

	public Boolean getDataEnable() {
		return dataEnable;
	}

	public void setDataEnable(Boolean dataEnable) {
		this.dataEnable = dataEnable;
	}

	public Boolean getRecordEnable() {
		return recordEnable;
	}

	public void setRecordEnable(Boolean recordEnable) {
		this.recordEnable = recordEnable;
	}

	public Boolean getTaskCompletedEnable() {
		return taskCompletedEnable;
	}

	public void setTaskCompletedEnable(Boolean taskCompletedEnable) {
		this.taskCompletedEnable = taskCompletedEnable;
	}

	public Boolean getReadEnable() {
		return readEnable;
	}

	public void setReadEnable(Boolean readEnable) {
		this.readEnable = readEnable;
	}

	public Boolean getReadCompletedEnable() {
		return readCompletedEnable;
	}

	public void setReadCompletedEnable(Boolean readCompletedEnable) {
		this.readCompletedEnable = readCompletedEnable;
	}

	public Boolean getReviewEnable() {
		return reviewEnable;
	}

	public void setReviewEnable(Boolean reviewEnable) {
		this.reviewEnable = reviewEnable;
	}

	public Boolean getAttachmentEnable() {
		return attachmentEnable;
	}

	public void setAttachmentEnable(Boolean attachmentEnable) {
		this.attachmentEnable = attachmentEnable;
	}

}
