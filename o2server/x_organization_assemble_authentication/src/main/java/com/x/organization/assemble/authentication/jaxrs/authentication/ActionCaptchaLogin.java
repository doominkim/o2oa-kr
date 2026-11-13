package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCaptchaLoginWi;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionCaptchaLogin extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCaptchaLogin.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		this.validate(wi);
		if(BooleanUtils.isTrue(Config.person().getTwoFactorLogin()) || (BooleanUtils.isFalse(
				Config.person().getCaptchaLogin()) && BooleanUtils.isFalse(
				Config.person().getUserPwdLogin()))){
			throw new ExceptionLoginDisable();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			Wo wo = null;
			String password = password(wi.getPassword());
			if (BooleanUtils.isTrue(Config.person().getCaptchaLogin()) && (BooleanUtils
					.isFalse(business.instrument().captcha().validate(wi.getCaptcha(), wi.getCaptchaAnswer())))) {
				throw new ExceptionInvalidCaptcha();
			}
			if (Config.token().isInitialManager(wi.getCredential())) {
				if (!Config.token().verifyPassword(wi.getCredential(), password)) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				}
				wo = this.manager(request, response, wi.getCredential(), Wo.class);
			} else {
				// 일반 사용자 로그인. 관리자 역할을 가질 수도 있음. 같은 식별자(name 중복 허용)의 인증 추가
				List<String> people = this.listWithCredential(business, wi.getCredential());
				Person person = null;
				if (people.isEmpty()) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				} else if (people.size() == 1) {
					person = this.personLogin(business, people.get(0), password, wi.getCredential());
				} else {
					person = this.peopleLogin(business, people, password, wi.getCredential());
				}
				if (null == person) {
					throw new ExceptionPersonNotExistOrInvalidPassword();
				} else {
					wo = this.user(request, response, business, person, Wo.class);
				}
			}
			result.setData(wo);
			return result;
		}
	}

	private void validate(Wi wi) throws Exception {
		if (StringUtils.isEmpty(wi.getCredential())) {
			throw new ExceptionCredentialEmpty();
		}
		if (StringUtils.isEmpty(wi.getPassword())) {
			throw new ExceptionPasswordEmpty();
		}
		// 설정으로 이미지 인증 코드를 건너뛸 수 있음.
		if (BooleanUtils.isTrue(Config.person().getCaptchaLogin())
				&& (StringUtils.isEmpty(wi.getCaptcha()) || StringUtils.isEmpty(wi.getCaptchaAnswer()))) {
			throw new ExceptionCaptchaEmpty();
		}
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCaptchaLogin$Wi")
	public static class Wi extends ActionCaptchaLoginWi {

		private static final long serialVersionUID = 216758837350255868L;

	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCaptchaLogin$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 4940814657548190978L;
	}

}
