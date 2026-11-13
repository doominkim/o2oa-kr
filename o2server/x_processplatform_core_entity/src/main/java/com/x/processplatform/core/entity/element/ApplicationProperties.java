package com.x.processplatform.core.entity.element;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;
import java.util.ArrayList;
import java.util.List;

public class ApplicationProperties extends JsonProperties {

	private static final long serialVersionUID = 8342746214747017734L;

	@FieldDescribe("앱 기본 양식")
	private String defaultForm;

	@FieldDescribe("프로세스 관리 신분, 처리 신분을 찾을 수 없을 때의 기본 프로세스 처리 신분.")
	private String maintenanceIdentity;

	@FieldDescribe("현재 앱의 모든 프로세스 인스턴스 관리자 목록")
	private List<String> maintainerList;

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void setMaintenanceIdentity(String maintenanceIdentity) {
		this.maintenanceIdentity = maintenanceIdentity;
	}

	public String getDefaultForm() {
		return defaultForm;
	}

	public void setDefaultForm(String defaultForm) {
		this.defaultForm = defaultForm;
	}

	public List<String> getMaintainerList() {
		return maintainerList == null ? new ArrayList<>() : maintainerList;
	}

	public void setMaintainerList(List<String> maintainerList) {
		this.maintainerList = maintainerList;
	}
}
