package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.config.Mpweixin;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

/**
 * Created by fancyLou on 3/8/21. Copyright © 2021 O2. All rights reserved.
 */
public class ActionLoginWithCode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLoginWithCode.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String code) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(code)) {
			throw new ExceptionNoCode();
		}
		if (Config.mpweixin() == null || BooleanUtils.isFalse(Config.mpweixin().getEnable())) {
			throw new ExceptionConfigError();
		}

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Mpweixin.WeixinAuth2AccessResp resp = Config.mpweixin().mpAuth2(code);
			if (resp == null) {
				throw new ExceptionGetAccessTokenFail();
			}
			logger.info("사용자 openid accessToken 획득," + resp.toString());

			Business business = new Business(emc);
			// openid로 사용자 조회
			String personId = business.person().getWithCredential(resp.getOpenid());
			if (StringUtils.isEmpty(personId)) { // 바인딩되지 않은 사용자
				Wo wo = new Wo();
				wo.setUnbind(true);
				wo.setMpwxopenId(resp.getOpenid());
				result.setData(wo);
				return result;
			}
			Person person = emc.find(personId, Person.class);
			Wo wo = Wo.copier.copy(person);
			List<String> roles = business.organization().role().listWithPerson(person.getDistinguishedName());
			wo.setRoleList(roles);
			EffectivePerson effective = new EffectivePerson(wo.getDistinguishedName(), TokenType.user,
					Config.token().getCipher(), Config.person().getEncryptType());
			wo.setToken(effective.getToken());
			HttpToken httpToken = new HttpToken();
			httpToken.setToken(request, response, effective);
			result.setData(wo);
		}
		return result;

	}

	public static class Wo extends Person {
		private static final long serialVersionUID = -5779845017679699316L;

		public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsInvisible);

		static {
			Excludes.add("password");
		}
		static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null, Excludes);

		@FieldDescribe("로그인 token")
		private String token;

		@FieldDescribe("미바인딩 표시, true이면 프론트엔드에서 객체의 mpwxopenId 필드를 가져와 바인딩 작업을 해야 함")
		private Boolean unbind;

		public Boolean getUnbind() {
			return unbind;
		}

		public void setUnbind(Boolean unbind) {
			this.unbind = unbind;
		}

		private List<String> roleList;

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}
}
