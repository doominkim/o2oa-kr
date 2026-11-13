package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionModeWo extends GsonPropertyObject {

	private static final long serialVersionUID = -7141628080400713769L;

	@FieldDescribe("사용자명·비밀번호 로그인 사용.")
	@Schema(description = "사용자명·비밀번호 로그인 사용.")
	private Boolean userPwdLogin = true;

	@FieldDescribe("이미지 인증 코드 로그인 사용.")
	@Schema(description = "이미지 인증 코드 로그인 사용.")
	private Boolean captchaLogin = true;

	@FieldDescribe("SMS 인증 코드 로그인 사용.")
	@Schema(description = "SMS 인증 코드 로그인 사용.")
	private Boolean codeLogin = false;

	@FieldDescribe("QR 코드 스캔 로그인.")
	@Schema(description = "QR 코드 스캔 로그인.")
	private Boolean bindLogin = false;

	@FieldDescribe("얼굴 인식 로그인.")
	@Schema(description = "얼굴 인식 로그인.")
	private Boolean faceLogin = false;

	@FieldDescribe("2단계 인증 로그인.")
	@Schema(description = "2단계 인증 로그인.")
	private Boolean twoFactorLogin = false;

	public Boolean getCodeLogin() {
		return codeLogin;
	}

	public void setCodeLogin(Boolean codeLogin) {
		this.codeLogin = codeLogin;
	}

	public Boolean getBindLogin() {
		return bindLogin;
	}

	public void setBindLogin(Boolean bindLogin) {
		this.bindLogin = bindLogin;
	}

	public Boolean getFaceLogin() {
		return faceLogin;
	}

	public void setFaceLogin(Boolean faceLogin) {
		this.faceLogin = faceLogin;
	}

	public Boolean getCaptchaLogin() {
		return captchaLogin;
	}

	public void setCaptchaLogin(Boolean captchaLogin) {
		this.captchaLogin = captchaLogin;
	}

	public Boolean getTwoFactorLogin() {
		return twoFactorLogin;
	}

	public void setTwoFactorLogin(Boolean twoFactorLogin) {
		this.twoFactorLogin = twoFactorLogin;
	}

	public Boolean getUserPwdLogin() {
		return userPwdLogin;
	}

	public void setUserPwdLogin(Boolean userPwdLogin) {
		this.userPwdLogin = userPwdLogin;
	}
}
