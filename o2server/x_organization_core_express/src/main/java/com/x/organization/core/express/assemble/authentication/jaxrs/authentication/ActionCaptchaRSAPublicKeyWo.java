package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaRSAPublicKeyWo extends GsonPropertyObject {

	private static final long serialVersionUID = 8305484005167999793L;

	@FieldDescribe("RSA 공개 키.")
	@Schema(description = "RSA 공개 키.")
	private String publicKey;

	@FieldDescribe("RSA 암호화 사용 여부.")
	@Schema(description = "RSA 암호화 사용 여부.")
	private Boolean rsaEnable;

	public Boolean getRsaEnable() {
		return rsaEnable;
	}

	public void setRsaEnable(Boolean rsaEnable) {
		this.rsaEnable = rsaEnable;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
}