package com.x.program.center.jaxrs.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

class ActionGetLicenseInfo extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetLicenseInfo.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception{

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wo wo = new Wo();
		wo.setVersion(Config.version());
		try {
			Class<?> licenseToolsCls = Class.forName("com.x.base.core.lc.LcTools");
			String info = (String) MethodUtils.invokeStaticMethod(licenseToolsCls, "getInfo");
			if(StringUtils.isNotBlank(info)){
				wo = XGsonBuilder.instance().fromJson(info, Wo.class);
			}
		} catch (Exception e) {
			LOGGER.debug(e.getMessage());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("고객 이름.")
		private String name;
		@FieldDescribe("최상위 조직 이름.")
		private String unitName;
		@FieldDescribe("이메일.")
		private String email;
		@FieldDescribe("버전 번호.")
		private String version;
		@FieldDescribe("버전 유형.")
		private String versionType;
		@FieldDescribe("라이선스 모드.")
		private String model;
		@FieldDescribe("라이선스 일시.")
		private Date startTime;
		@FieldDescribe("라이선스 만료 일시.")
		private Date expireTime;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getVersionType() {
			return versionType;
		}

		public void setVersionType(String versionType) {
			this.versionType = versionType;
		}

		public String getModel() {
			return model;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public Date getStartTime() {
			return startTime;
		}

		public void setStartTime(Date startTime) {
			this.startTime = startTime;
		}

		public Date getExpireTime() {
			return expireTime;
		}

		public void setExpireTime(Date expireTime) {
			this.expireTime = expireTime;
		}
	}

}
