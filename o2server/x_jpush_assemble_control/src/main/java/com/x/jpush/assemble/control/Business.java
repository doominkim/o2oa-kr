package com.x.jpush.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.jpush.assemble.control.factory.PushDeviceFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	// 조직 구조 관리 관련 팩토리 서비스 클래스
	private Organization organization;

	// 예시 데이터 테이블 팩토리 서비스 클래스
	private PushDeviceFactory pushDeviceFactory;

	public PushDeviceFactory pushDeviceFactory() throws Exception {
		if (null == this.pushDeviceFactory) {
			this.pushDeviceFactory = new PushDeviceFactory(this);
		}
		return pushDeviceFactory;
	}

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

}
