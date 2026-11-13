package com.x.hotpic.assemble.control.jaxrs.hotpic;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;

public class WrapInFilter{
	
	@FieldDescribe( "앱 이름: CMS|BBS 등." )
	private String application = null;
	
	@FieldDescribe( "정보 ID." )
	private String infoId = null;
	
	@FieldDescribe( "정보 제목(부분 일치 검색)." )
	private String title = null;
	
	public static List<String> Excludes = new ArrayList<String>( JpaObject.FieldsUnmodify );

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}
	
}