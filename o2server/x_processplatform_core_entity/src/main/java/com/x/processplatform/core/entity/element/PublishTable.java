package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author sword
 */
public class PublishTable extends JsonProperties {

	private static final long serialVersionUID = 6232849915945948766L;

	public static final String TABLE_DATA_BY_SCRIPT = "script";
	public static final String TABLE_DATA_BY_PATH = "dataPath";

	@FieldDescribe("데이터 테이블 이름")
	private String tableName;

	@FieldDescribe("데이터 조회 방식")
	private String queryTableDataBy;

	@FieldDescribe("데이터 경로")
	private String queryTableDataPath;

	@FieldDescribe("데이터 스크립트.")
	private String targetAssignDataScript;

	@FieldDescribe("데이터 스크립트 텍스트.")
	private String targetAssignDataScriptText;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQueryTableDataBy() {
		return queryTableDataBy;
	}

	public void setQueryTableDataBy(String queryTableDataBy) {
		this.queryTableDataBy = queryTableDataBy;
	}

	public String getQueryTableDataPath() {
		return queryTableDataPath;
	}

	public void setQueryTableDataPath(String queryTableDataPath) {
		this.queryTableDataPath = queryTableDataPath;
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
}
