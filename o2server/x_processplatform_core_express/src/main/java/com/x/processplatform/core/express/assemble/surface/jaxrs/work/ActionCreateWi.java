package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.correlation.core.express.service.processing.jaxrs.correlation.TargetWi;
import com.x.processplatform.core.express.assemble.surface.jaxrs.attachment.WiAttachment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

public class ActionCreateWi extends GsonPropertyObject {

	private static final long serialVersionUID = 5156711320925350432L;

	@FieldDescribe("지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
	@Schema(description = "지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
	private Boolean latest;

	@FieldDescribe("제목.")
	@Schema(description = "제목.")
	private String title;

	@FieldDescribe("시작 사용자 신분.")
	@Schema(description = "시작 사용자 신분.")
	private String identity;

	@FieldDescribe("업무 문서 데이터.")
	@Schema(description = "업무 문서 데이터.")
	private JsonElement data;

	@FieldDescribe("상위 업무 문서 식별자.")
	@Schema(description = "상위 업무 문서 식별자.")
	private String parentWork;

	@FieldDescribe("현재 버전이 아닌 프로세스 시작 허용, 기본값은 아니오이며 현재 버전 프로세스로 자동 업그레이드.")
	@Schema(description = "현재 버전이 아닌 프로세스 시작 허용, 기본값은 아니오이며 현재 버전 프로세스로 자동 업그레이드.")
	private Boolean allowEdition;

	@FieldDescribe("신규 생성 검사 건너뛰기 여부(기본값은 프로세스의 신규 생성 검사 설정에 따름, true로 설정하면 신규 생성 검사를 하지 않음).")
	@Schema(description = "신규 생성 검사 건너뛰기 여부(기본값은 프로세스의 신규 생성 검사 설정에 따름, true로 설정하면 신규 생성 검사를 하지 않음).")
	private Boolean skipDraftCheck = false;

	@FieldDescribe("첨부파일 목록")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment",
			fieldValue = "{'id':'첨부파일 id','name':'첨부파일 이름','site':'첨부파일 영역 분류','isSoftCopy':'소프트 복사 여부, 기본값 false. true면 실제 저장된 첨부파일을 복사하지 않고 공유하며 결재 첨부파일만 지원','copyFrom':'첨부파일 출처(cms 또는 processPlatform, 기본값 processPlatform)'}")
	private List<WiAttachment> attachmentList = new ArrayList<>();

	@FieldDescribe("관련 문서 목록.")
	@FieldTypeDescribe(fieldType = "class", fieldTypeName = "TargetWi",
			fieldValue = "{'type':'관련 대상 유형(cms 또는 processPlatform)','bundle':'관련 대상 식별자','site':'관련 콘텐츠 영역 식별자','view':'출처 뷰'}")
	private List<TargetWi> correlationTargetList;

	public List<WiAttachment> getAttachmentList() {
		return attachmentList;
	}

	public void setAttachmentList(
			List<WiAttachment> attachmentList) {
		this.attachmentList = attachmentList;
	}

	public String getParentWork() {
		return parentWork;
	}

	public void setParentWork(String parentWork) {
		this.parentWork = parentWork;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public JsonElement getData() {
		return data;
	}

	public void setData(JsonElement data) {
		this.data = data;
	}

	public Boolean getLatest() {
		return latest;
	}

	public void setLatest(Boolean latest) {
		this.latest = latest;
	}

	public Boolean getAllowEdition() {
		return allowEdition;
	}

	public void setAllowEdition(Boolean allowEdition) {
		this.allowEdition = allowEdition;
	}

	public Boolean getSkipDraftCheck() {
		return skipDraftCheck;
	}

	public void setSkipDraftCheck(Boolean skipDraftCheck) {
		this.skipDraftCheck = skipDraftCheck;
	}

	public List<TargetWi> getCorrelationTargetList() {
		return correlationTargetList;
	}

	public void setCorrelationTargetList(
			List<TargetWi> correlationTargetList) {
		this.correlationTargetList = correlationTargetList;
	}
}
