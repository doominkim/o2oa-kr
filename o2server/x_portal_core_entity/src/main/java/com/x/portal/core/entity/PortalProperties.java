package com.x.portal.core.entity;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sword
 */
public class PortalProperties extends JsonProperties {

	@FieldDescribe("배지 연결 스크립트.")
	private String cornerMarkScript;

	@FieldDescribe("배지 스크립트.")
	private String cornerMarkScriptText;

	public String getCornerMarkScript() {
		return cornerMarkScript;
	}

	public void setCornerMarkScript(String cornerMarkScript) {
		this.cornerMarkScript = cornerMarkScript;
	}

	public String getCornerMarkScriptText() {
		return cornerMarkScriptText;
	}

	public void setCornerMarkScriptText(String cornerMarkScriptText) {
		this.cornerMarkScriptText = cornerMarkScriptText;
	}
}
