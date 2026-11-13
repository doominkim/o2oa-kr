package com.x.processplatform.core.express.service.processing.jaxrs.work;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Data;

public class ActionAssignCreateWi extends GsonPropertyObject {

	private static final long serialVersionUID = -797953382184336448L;

	@FieldDescribe("앱 식별자")
	private String application;
	@FieldDescribe("프로세스 식별자")
	private String process;
	@FieldDescribe("신분 식별자")
	private String identity;
	@FieldDescribe("제목")
	private String title;
	@FieldDescribe("업무 데이터")
	private Data data;
	@FieldDescribe("첨부파일")
	private List<WiAttachment> attachmentList = new ArrayList<>();
	@FieldDescribe("첨부파일 소프트 복사 여부, true이면 실제 저장된 첨부파일을 복사하지 않고 첨부파일 기록만 복사하여 첨부파일을 공유함.")
	private Boolean attachmentSoftCopy;
	@FieldDescribe("자동 흐름")
	private Boolean processing;
	@FieldDescribe("하위 프로세스 호출 시 상위 업무 문서 id 기록")
	private String parentWork;
	@FieldDescribe("하위 프로세스 호출 시 상위 업무 문서 job 기록")
	private String parentJob;

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getParentJob() {
		return parentJob;
	}

	public void setParentJob(String parentJob) {
		this.parentJob = parentJob;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public List<WiAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(List<WiAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public Boolean getProcessing() {
		return processing;
	}

	public void setProcessing(Boolean processing) {
		this.processing = processing;
	}

	public Boolean getAttachmentSoftCopy() {
		return attachmentSoftCopy;
	}

	public void setAttachmentSoftCopy(Boolean attachmentSoftCopy) {
		this.attachmentSoftCopy = attachmentSoftCopy;
	}

	public static class WiAttachment extends Attachment {

		private static final long serialVersionUID = 1954637399762611493L;

		public static final WrapCopier<WiAttachment, Attachment> copier = WrapCopierFactory.wi(WiAttachment.class,
				Attachment.class, null, JpaObject.FieldsUnmodify);

	}
}