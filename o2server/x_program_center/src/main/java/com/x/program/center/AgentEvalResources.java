package com.x.program.center;

import com.x.base.core.project.script.AbstractResources;
import com.x.organization.core.express.Organization;

/**
 * 스크립트 실행 중 객체 주입용
 */
public class AgentEvalResources extends AbstractResources {
	private Organization organization;

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

}