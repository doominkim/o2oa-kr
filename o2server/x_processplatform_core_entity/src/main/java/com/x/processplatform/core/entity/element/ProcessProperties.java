package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.List;

public class ProcessProperties extends JsonProperties {

	private static final long serialVersionUID = 1L;

	@FieldDescribe("결재 대기 실행 전 스크립트.")
	private String manualBeforeTaskScript;

	@FieldDescribe("결재 대기 실행 전 스크립트 텍스트.")
	private String manualBeforeTaskScriptText;

	@FieldDescribe("결재 대기 실행 후 스크립트.")
	private String manualAfterTaskScript;

	@FieldDescribe("결재 대기 실행 후 스크립트 텍스트.")
	private String manualAfterTaskScriptText;

	@FieldDescribe("업무 문서 진행 후 실행 스크립트.")
	private String manualAfterProcessingScript;

	@FieldDescribe("업무 문서 진행 후 실행 스크립트 텍스트.")
	private String manualAfterProcessingScriptText;

	@FieldDescribe("수동 활동 체류 스크립트.")
	private String manualStayScript;

	@FieldDescribe("수동 활동 체류 스크립트 텍스트.")
	private String manualStayScriptText;

	@FieldDescribe("자체 테이블 동기화 사용.")
	private Boolean updateTableEnable;

	@FieldDescribe("자체 테이블로 동기화.")
	private List<String> updateTableList;

	@FieldDescribe("프로세스 관리 신분, 처리 신분을 찾을 수 없을 때의 기본 프로세스 처리 신분.")
	private String maintenanceIdentity;

	@FieldDescribe("데이터 스크립트.")
	private String targetAssignDataScript;

	@FieldDescribe("데이터 스크립트 텍스트.")
	private String targetAssignDataScriptText;

	@FieldDescribe("편집 권한 스크립트, 조직 전용 식별자 반환.")
	private String permissionWriteScript;

	@FieldDescribe("편집 권한 스크립트 텍스트, 조직 전용 식별자 반환.")
	private String permissionWriteScriptText;

	@FieldDescribe("데이터 변경을 기록할 필드.")
	private List<String> dataTraceFieldList;

	@FieldDescribe("데이터 변경을 기록할 필드 설정 방식: all|전체, custom|dataTraceFieldList에 설정된 필드.")
	private String dataTraceFieldType;

	public String getPermissionWriteScript() {
		return permissionWriteScript;
	}

	public void setPermissionWriteScript(String permissionWriteScript) {
		this.permissionWriteScript = permissionWriteScript;
	}

	public String getPermissionWriteScriptText() {
		return permissionWriteScriptText;
	}

	public void setPermissionWriteScriptText(String permissionWriteScriptText) {
		this.permissionWriteScriptText = permissionWriteScriptText;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
	}

	public Boolean getUpdateTableEnable() {
		return updateTableEnable;
	}

	public void setUpdateTableEnable(Boolean updateTableEnable) {
		this.updateTableEnable = updateTableEnable;
	}

	public List<String> getUpdateTableList() {
		return updateTableList;
	}

	public void setUpdateTableList(List<String> updateTableList) {
		this.updateTableList = updateTableList;
	}

	public String getManualBeforeTaskScript() {
		return manualBeforeTaskScript;
	}

	public void setManualBeforeTaskScript(String manualBeforeTaskScript) {
		this.manualBeforeTaskScript = manualBeforeTaskScript;
	}

	public String getManualBeforeTaskScriptText() {
		return manualBeforeTaskScriptText;
	}

	public void setManualBeforeTaskScriptText(String manualBeforeTaskScriptText) {
		this.manualBeforeTaskScriptText = manualBeforeTaskScriptText;
	}

	public String getManualAfterTaskScript() {
		return manualAfterTaskScript;
	}

	public void setManualAfterTaskScript(String manualAfterTaskScript) {
		this.manualAfterTaskScript = manualAfterTaskScript;
	}

	public String getManualAfterTaskScriptText() {
		return manualAfterTaskScriptText;
	}

	public void setManualAfterTaskScriptText(String manualAfterTaskScriptText) {
		this.manualAfterTaskScriptText = manualAfterTaskScriptText;
	}

	public String getManualStayScript() {
		return manualStayScript;
	}

	public void setManualStayScript(String manualStayScript) {
		this.manualStayScript = manualStayScript;
	}

	public String getManualStayScriptText() {
		return manualStayScriptText;
	}

	public void setManualStayScriptText(String manualStayScriptText) {
		this.manualStayScriptText = manualStayScriptText;
	}

	public String getTargetAssignDataScript() {
		return targetAssignDataScript;
	}

	public void setTargetAssignDataScript(String targetAssignDataScript) {
		this.targetAssignDataScript = targetAssignDataScript;
	}

	public String getTargetAssignDataScriptText() {
		return targetAssignDataScriptText;
	}

	public void setTargetAssignDataScriptText(String targetAssignDataScriptText) {
		this.targetAssignDataScriptText = targetAssignDataScriptText;
	}

	public List<String> getDataTraceFieldList() {
		return dataTraceFieldList;
	}

	public void setDataTraceFieldList(List<String> dataTraceFieldList) {
		this.dataTraceFieldList = dataTraceFieldList;
	}

	public String getManualAfterProcessingScript() {
		return manualAfterProcessingScript;
	}

	public void setManualAfterProcessingScript(String manualAfterProcessingScript) {
		this.manualAfterProcessingScript = manualAfterProcessingScript;
	}

	public String getManualAfterProcessingScriptText() {
		return manualAfterProcessingScriptText;
	}

	public void setManualAfterProcessingScriptText(String manualAfterProcessingScriptText) {
		this.manualAfterProcessingScriptText = manualAfterProcessingScriptText;
	}

	public String getDataTraceFieldType() {
		return dataTraceFieldType;
	}

	public void setDataTraceFieldType(String dataTraceFieldType) {
		this.dataTraceFieldType = dataTraceFieldType;
	}
}
