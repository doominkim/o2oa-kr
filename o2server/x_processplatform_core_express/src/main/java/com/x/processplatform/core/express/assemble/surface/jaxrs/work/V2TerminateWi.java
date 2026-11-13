package com.x.processplatform.core.express.assemble.surface.jaxrs.work;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class V2TerminateWi extends GsonPropertyObject {

	private static final long serialVersionUID = 6045785174271544769L;

	@FieldDescribe("결재 대기 선택 경로 이름.")
	private String routeName;

	@FieldDescribe("결재 대기 처리 의견.")
	private String opinion;

	@FieldDescribe("작업자, 기본값은 현재 로그인 사용자.")
	private String operator;

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
}
