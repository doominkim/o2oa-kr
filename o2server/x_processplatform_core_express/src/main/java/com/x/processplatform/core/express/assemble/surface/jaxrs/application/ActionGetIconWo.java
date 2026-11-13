package com.x.processplatform.core.express.assemble.surface.jaxrs.application;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionGetIconWo extends GsonPropertyObject {

	private static final long serialVersionUID = 2554440356814790897L;

	@FieldDescribe("앱 아이콘 base64 인코딩 값.")
	@Schema(description = "앱 아이콘 base64 인코딩 값.")
	private String icon;

	@FieldDescribe("앱 아이콘 색조.")
	@Schema(description = "앱 아이콘 색조.")
	private String iconHue;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconHue() {
		return iconHue;
	}

	public void setIconHue(String iconHue) {
		this.iconHue = iconHue;
	}

}