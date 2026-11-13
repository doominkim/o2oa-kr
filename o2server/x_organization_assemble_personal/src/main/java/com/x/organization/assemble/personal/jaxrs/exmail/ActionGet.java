package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 텐센트 기업 메일에서 API를 처음 개통할 때 인증 전용. 인증 포트는 80, 443 또는 8080이어야 하며 텐센트는 다른 포트에 접근하지 않음.
 * 
 * @author ray
 *
 */
class ActionGet extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String msg_signature, String timestamp, String nonce,
			String echostr) throws Exception {

		LOGGER.debug("텐센트 기업 메일 수신, msg_signature:{}, timestamp:{}, nonce:{}, echostr:{}.", () -> msg_signature,
				() -> timestamp, () -> nonce, () -> echostr);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		WXBizMsgCrypt crypt = new WXBizMsgCrypt(Config.exmail().getToken(), Config.exmail().getEncodingAesKey(),
				Config.exmail().getCorpId());
		String value = crypt.VerifyURL(msg_signature, timestamp, nonce, echostr);
		wo.setText(value);
		result.setData(wo);
		return result;
	}
	
	@Schema(name = "com.x.organization.assemble.personal.jaxrs.exmail.ActionGet$Wo")
	public static class Wo extends WoText {

		private static final long serialVersionUID = -8449349214756231403L;
	}

}
