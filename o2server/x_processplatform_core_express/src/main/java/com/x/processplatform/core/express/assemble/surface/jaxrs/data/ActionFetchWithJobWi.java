package com.x.processplatform.core.express.assemble.surface.jaxrs.data;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionFetchWithJobWi extends GsonPropertyObject {

	private static final long serialVersionUID = -8049393171576534852L;

	@FieldDescribe("조회 경로.")
	@Schema(description = "조회 경로.")
	private List<String> pathList;

	public List<String> getPathList() {
		return pathList;
	}

	public void setPathList(List<String> pathList) {
		this.pathList = pathList;
	}

}