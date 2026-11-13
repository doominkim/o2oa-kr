package com.x.processplatform.core.entity.element;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.entity.JsonProperties;
import com.x.base.core.project.annotation.FieldDescribe;

public class RouteProperties extends JsonProperties {

	private static final long serialVersionUID = -7792270726211126577L;

	@FieldDescribe("비동기 반환 사용 여부. 기본값 false")
	private Boolean asyncSupported = false;

	@FieldDescribe("우선 경로 선택 시 경로 직접 실행 여부(일표 부결), 기본값 null.")
	private Boolean soleDirect;

	@FieldDescribe("기본 선택 경로.")
	private Boolean defaultSelected;

	public Boolean getAsyncSupported() {
		return asyncSupported;
	}

	public void setAsyncSupported(Boolean asyncSupported) {
		this.asyncSupported = asyncSupported;
	}

	public Boolean getSoleDirect() {
		return BooleanUtils.isTrue(soleDirect);
	}

	public void setSoleDirect(Boolean soleDirect) {
		this.soleDirect = soleDirect;
	}

	public Boolean getDefaultSelected() {
		return defaultSelected;
	}

	public void setDefaultSelected(Boolean defaultSelected) {
		this.defaultSelected = defaultSelected;
	}

}
