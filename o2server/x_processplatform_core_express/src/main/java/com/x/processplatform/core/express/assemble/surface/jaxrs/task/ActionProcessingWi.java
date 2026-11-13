package com.x.processplatform.core.express.assemble.surface.jaxrs.task;

import java.util.List;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionProcessingWi extends GsonPropertyObject {

	private static final long serialVersionUID = -870819172943535910L;

	@FieldDescribe("결재 대기 선택 경로 이름.")
	@Schema(description = "결재 대기 선택 경로 이름.")
	private String routeName;

//	@FieldDescribe("결정.")
//	@Schema(description = "결정.")
//	private String decision;

	@FieldDescribe("결재 대기 처리 의견.")
	@Schema(description = "결재 대기 처리 의견.")
	private String opinion;

	@FieldDescribe("멀티미디어 의견.")
	@Schema(description = "멀티미디어 의견.")
	private String mediaOpinion;

	@FieldDescribe("경로 데이터.")
	@Schema(description = "경로 데이터.")
	private JsonElement routeData;

	@FieldDescribe("새로 추가된 결재 대기 처리자 조직 전용 식별자.")
	@Schema(description = "새로 추가된 결재 대기 처리자 조직 전용 식별자.")
	private List<String> distinguishedNameList;

	@FieldDescribe("새로 추가된 결재 대기 처리자 신분.")
	@Schema(description = "새로 추가된 결재 대기 처리자 신분.")
	private List<String> appendTaskIdentityList;

	@FieldDescribe("위임 신분 무시.")
	@Schema(description = "위임 신분 무시.")
	private List<String> ignoreEmpowerIdentityList;

	@FieldDescribe("결재 대기 처리 유형: goBack.")
	@Schema(description = "결재 대기 처리 유형: goBack.")
	private String action;

	@FieldDescribe("action의 매개변수 객체.")
	@Schema(description = "action의 매개변수 객체.")
	private JsonElement option;

//	public String getDecision() {
//		return decision;
//	}
//
//	public void setDecision(String decision) {
//		this.decision = decision;
//	}

	public List<String> getAppendTaskIdentityList() {
		return appendTaskIdentityList;
	}

	public void setAppendTaskIdentityList(List<String> appendTaskIdentityList) {
		this.appendTaskIdentityList = appendTaskIdentityList;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public JsonElement getOption() {
		return option;
	}

	public void setOption(JsonElement option) {
		this.option = option;
	}

	public List<String> getDistinguishedNameList() {
		return distinguishedNameList;
	}

	public void setDistinguishedNameList(List<String> distinguishedNameList) {
		this.distinguishedNameList = distinguishedNameList;
	}

	public List<String> getIgnoreEmpowerIdentityList() {
		return ListTools.trim(ignoreEmpowerIdentityList, true, true);
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getMediaOpinion() {
		return mediaOpinion;
	}

	public void setMediaOpinion(String mediaOpinion) {
		this.mediaOpinion = mediaOpinion;
	}

	public JsonElement getRouteData() {
		return routeData;
	}

	public void setRouteData(JsonElement routeData) {
		this.routeData = routeData;
	}

	public void setIgnoreEmpowerIdentityList(List<String> ignoreEmpowerIdentityList) {
		this.ignoreEmpowerIdentityList = ignoreEmpowerIdentityList;
	}
}