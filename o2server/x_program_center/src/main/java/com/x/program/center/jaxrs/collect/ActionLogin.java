package com.x.program.center.jaxrs.collect;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;

class ActionLogin extends BaseAction {

	public static final String C_Token = "c-token";

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		if(BooleanUtils.isFalse(Config.collect().getEnable())){
			throw new ExceptionDisable();
		}
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}

		String url = Config.collect().url(Collect.ADDRESS_COLLECT_LOGIN);
		Map<String, String> map = new HashMap<>();
		map.put("credential", Config.collect().getName());
		map.put("password", Config.collect().getPassword());
		ActionResponse resp = ConnectionAction.post(url, null, map);
		LoginWo loginWo = resp.getData(LoginWo.class);

		HttpToken httpToken = new HttpToken();
		httpToken.setResponseToken(request, response, C_Token, loginWo.getToken());

		wo.setCollectToken(loginWo.getToken());
		wo.setCollectTokenType(loginWo.getTokenType());
		wo.setCollectUrl(Config.collect().url());
		if(BooleanUtils.isTrue(loginWo.getVipUnit())){
			wo.setVipUnit(true);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("collect 서비스 주소")
		private String collectUrl;
		@FieldDescribe("collect 토큰")
		private String collectToken;
		@FieldDescribe("collect 토큰 유형")
		private TokenType collectTokenType;
		@FieldDescribe("VIP 조직 여부")
		private Boolean vipUnit = false;

		public String getCollectUrl() {
			return collectUrl;
		}

		public void setCollectUrl(String collectUrl) {
			this.collectUrl = collectUrl;
		}

		public String getCollectToken() {
			return collectToken;
		}

		public void setCollectToken(String collectToken) {
			this.collectToken = collectToken;
		}

		public TokenType getCollectTokenType() {
			return collectTokenType;
		}

		public void setCollectTokenType(TokenType collectTokenType) {
			this.collectTokenType = collectTokenType;
		}

		public Boolean getVipUnit() {
			return vipUnit;
		}

		public void setVipUnit(Boolean vipUnit) {
			this.vipUnit = vipUnit;
		}
	}

	public static class LoginWo extends GsonPropertyObject {

		@FieldDescribe("토큰")
		private String token;
		@FieldDescribe("이름")
		private String name;
		@FieldDescribe("토큰 유형")
		private TokenType tokenType;
		@FieldDescribe("VIP 조직 여부")
		private Boolean vipUnit;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TokenType getTokenType() {
			return tokenType;
		}

		public void setTokenType(TokenType tokenType) {
			this.tokenType = tokenType;
		}

		public Boolean getVipUnit() {
			return vipUnit;
		}

		public void setVipUnit(Boolean vipUnit) {
			this.vipUnit = vipUnit;
		}
	}

}
