package com.x.ai.assemble.control;

import com.google.gson.JsonObject;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.factory.CmsItemFactory;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.express.Organization;

/**
 * 앱 업무 서비스 클래스
 * @author sword
 */
public class Business {

	private static final Logger logger = LoggerFactory.getLogger(Business.class);

	/**
	 * 사용자 정의 앱 설정 파일 이름, 실제 설정에 맞게 수정, 파일은 json 형식이어야 함
	 */
	public static final String CUSTOM_CONFIG_NAME = "o2oa_ai";

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	/**
	 * 설정 조회
	 * @return
	 * @throws Exception
	 */
	public static synchronized AiConfig getConfig(){
		AiConfig aiConfig = null;
		try {
			JsonObject jsonObject = Config.customConfig(CUSTOM_CONFIG_NAME);
			if (jsonObject!=null){
				aiConfig = XGsonBuilder.convert(jsonObject, AiConfig.class);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}
		return aiConfig == null ? new AiConfig() : aiConfig;
	}

	/**
	 * 조직 구조 관리 관련 팩토리 서비스 클래스
	 */
	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private CmsItemFactory cmsItem;

	public CmsItemFactory cmsItem() throws Exception {
		if (null == this.cmsItem) {
			this.cmsItem = new CmsItemFactory(this);
		}
		return cmsItem;
	}

}
