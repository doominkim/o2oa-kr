package com.x.organization.core.express.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaLoginWi extends GsonPropertyObject {

	private static final long serialVersionUID = 216758837350255868L;

	@FieldDescribe("사용자 자격 증명.")
	@Schema(description = "사용자 자격 증명.")
	private String credential;

	@FieldDescribe("비밀번호.")
	@Schema(description = "비밀번호.")
	private String password;

	@FieldDescribe("이미지 인증 코드 번호.")
	@Schema(description = "이미지 인증 코드 번호.")
	private String captcha;

	@FieldDescribe("이미지 인증 코드.")
	@Schema(description = "이미지 인증 코드.")
	private String captchaAnswer;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	public String getCaptchaAnswer() {
		return captchaAnswer;
	}

	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}

}