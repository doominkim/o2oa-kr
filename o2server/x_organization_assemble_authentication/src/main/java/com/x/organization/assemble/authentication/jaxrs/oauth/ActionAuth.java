package com.x.organization.assemble.authentication.jaxrs.oauth;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Oauth;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoSeeOther;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.OauthCode;

class ActionAuth extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionAuth.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String response_type, String client_id,
			String redirect_uri, String scope, String state) throws Exception {
		// response_type: 권한 부여 유형, 필수 항목, 값은 "code"로 고정
		// client_id: 클라이언트 ID, 필수 항목
		// redirect_uri: 리다이렉트 URI, 선택 항목
		// scope: 요청 권한 범위, 선택 항목
		// state: 클라이언트의 현재 상태, 임의 값 지정 가능하며 인증 서버가 이 값을 그대로 반환함.
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			if (effectivePerson.isAnonymous()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (!StringUtils.equalsIgnoreCase(response_type, "code")) {
				throw new ExceptionResponseTypeNotCode(response_type);
			}
			if (StringUtils.isEmpty(redirect_uri)) {
				throw new ExceptionRedirectUriEmpty();
			}
			if (StringUtils.isEmpty(client_id)) {
				throw new ExceptionClientIdEmpty();
			}
			Oauth oauth = Config.token().findOauth(client_id);
			if (null == oauth) {
				throw new ExceptionOauthNotExist(client_id);
			}
			OauthCode oauthCode = new OauthCode();
			oauthCode.setClientId(oauth.getClientId());
			oauthCode.setScope(this.getScope(oauth, scope));
			String person = this.getPerson(business, effectivePerson);
			if (StringUtils.isEmpty(person)) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			oauthCode.setPerson(person);
			emc.beginTransaction(OauthCode.class);
			emc.persist(oauthCode, CheckPersistType.all);
			emc.commit();
			if (StringUtils.containsAny(redirect_uri, "?", "&")) {
				redirect_uri += "&code=" + URLEncoder.encode(oauthCode.getCode(), DefaultCharset.name);
			} else {
				redirect_uri += "?code=" + URLEncoder.encode(oauthCode.getCode(), DefaultCharset.name);
			}
			if (StringUtils.isNotEmpty(state)) {
				redirect_uri += "&state=" + URLEncoder.encode(state, DefaultCharset.name);
			}
			Wo wo = new Wo();
			wo.setUrl(redirect_uri);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoSeeOther {

	}

	private String getPerson(Business business, EffectivePerson effectivePerson) throws Exception {
		if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
			return Config.token().initialManagerInstance().getId();
		} else {
			return business.person().getWithCredential(effectivePerson.getDistinguishedName());
		}
	}
}
