package com.x.correlation.core.entity.content;

import java.util.Date;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class CorrelationProperties extends JsonProperties {

	private static final long serialVersionUID = 5628694071505848771L;

	@FieldDescribe("연관 콘텐츠 제목.")
	private String targetTitle;

	@FieldDescribe("연관 콘텐츠 분류, processPlatform: 프로세스 이름, cms: 앱 이름.")
	private String targetCategory;

	@FieldDescribe("연관 콘텐츠 생성 시간")
	private Date targetStartTime;

	@FieldDescribe("연관 콘텐츠 생성자")
	private String targetCreatorPerson;

	@FieldDescribe("출처 뷰")
	private String view;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

	public String getTargetTitle() {
		return targetTitle;
	}

	public void setTargetTitle(String targetTitle) {
		this.targetTitle = targetTitle;
	}

	public String getTargetCategory() {
		return targetCategory;
	}

	public void setTargetCategory(String targetCategory) {
		this.targetCategory = targetCategory;
	}

	public Date getTargetStartTime() {
		return targetStartTime;
	}

	public void setTargetStartTime(Date targetStartTime) {
		this.targetStartTime = targetStartTime;
	}

	public String getTargetCreatorPerson() {
		return targetCreatorPerson;
	}

	public void setTargetCreatorPerson(String targetCreatorPerson) {
		this.targetCreatorPerson = targetCreatorPerson;
	}

}