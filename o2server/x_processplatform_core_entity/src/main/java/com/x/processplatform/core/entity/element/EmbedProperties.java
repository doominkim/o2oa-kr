package com.x.processplatform.core.entity.element;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class EmbedProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("활동 사용자 정의 데이터")
	private JsonElement customData;

	public JsonElement getCustomData() {
		return customData;
	}

	public void setCustomData(JsonElement customData) {
		this.customData = customData;
	}

	@FieldDescribe("내장 하위 프로세스 종료 이벤트(cancel과 end 활동 포함) 스크립트.")
	private String completedScript;

	@FieldDescribe("내장 하위 프로세스 종료 이벤트(cancel과 end 활동 포함) 스크립트 텍스트.")
	private String completedScriptText;

	@FieldDescribe("내장 하위 프로세스 End 노드 종료 이벤트 스크립트.")
	private String completedEndScript;

	@FieldDescribe("내장 하위 프로세스 End 노드 종료 이벤트 스크립트 텍스트.")
	private String completedEndScriptText;

	@FieldDescribe("내장 하위 프로세스 Cancel 노드 종료 이벤트 스크립트.")
	private String completedCancelScript;

	@FieldDescribe("내장 하위 프로세스 Cancel 노드 종료 이벤트 스크립트 텍스트.")
	private String completedCancelScriptText;

	public String getCompletedEndScript() {
		return completedEndScript;
	}

	public void setCompletedEndScript(String completedEndScript) {
		this.completedEndScript = completedEndScript;
	}

	public String getCompletedEndScriptText() {
		return completedEndScriptText;
	}

	public void setCompletedEndScriptText(String completedEndScriptText) {
		this.completedEndScriptText = completedEndScriptText;
	}

	public String getCompletedCancelScript() {
		return completedCancelScript;
	}

	public void setCompletedCancelScript(String completedCancelScript) {
		this.completedCancelScript = completedCancelScript;
	}

	public String getCompletedCancelScriptText() {
		return completedCancelScriptText;
	}

	public void setCompletedCancelScriptText(String completedCancelScriptText) {
		this.completedCancelScriptText = completedCancelScriptText;
	}

	public String getCompletedScript() {
		return completedScript;
	}

	public void setCompletedScript(String completedScript) {
		this.completedScript = completedScript;
	}

	public String getCompletedScriptText() {
		return completedScriptText;
	}

	public void setCompletedScriptText(String completedScriptText) {
		this.completedScriptText = completedScriptText;
	}

}
