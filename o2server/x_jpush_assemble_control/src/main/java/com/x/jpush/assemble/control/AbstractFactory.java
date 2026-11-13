package com.x.jpush.assemble.control;

import cn.jiguang.sdk.api.PushApi;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;


public abstract class AbstractFactory {
	private static Logger logger = LoggerFactory.getLogger(AbstractFactory.class );

	private Business business;
	private PushApi pushApi = null;


	public AbstractFactory(Business business) throws Exception {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new Exception("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

	public Business business() throws Exception {
		return this.business;
	}


	/**
	 * JPush 푸시 클라이언트
	 * @return
	 */
	public PushApi jpushClient() throws Exception {
		String appKey = Config.pushConfig().getAppKey();
		String masterKey = Config.pushConfig().getMasterSecret();
		logger.info("JPush 클라이언트, appKey:"+appKey+",masterKey:"+masterKey);
		if (StringUtils.isEmpty(appKey) || StringUtils.isEmpty(masterKey)) {
			return null;
		} else {
			// 기본 푸시
			if (null == pushApi) {
				pushApi =  new PushApi.Builder()
						.setAppKey(appKey) // 필수
						.setMasterSecret(masterKey) // 필수
						.build();
			}
			return pushApi;
		}
	}

}
