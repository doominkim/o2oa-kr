package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * Created by fancyLou on 3/8/21. Copyright © 2021 O2. All rights reserved.
 */
public class Mpweixin extends ConfigObject {

	@FieldDescribe("사용 여부.")
	private Boolean enable;
	@FieldDescribe("게시 사용 여부.")
	private Boolean enablePublish;
	@FieldDescribe("위챗 오픈 플랫폼 appid")
	private String appid;
	@FieldDescribe("위챗 오픈 플랫폼 appSecret")
	private String appSecret = "";
	@FieldDescribe("위챗 공식 계정에 설정한 token")
	private String token = "";
	@FieldDescribe("위챗 공식 계정에 설정한 encodingAesKey")
	private String encodingAesKey = "";
	@FieldDescribe("위챗 공식 계정 메시지에서 업무 문서를 여는 url 주소, 예:https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";
	@FieldDescribe("위챗 공식 계정 테스트 메뉴의 포털 주소")
	private String portalId = "";
	@FieldDescribe("텍스트 메시지 수신 시 기본 실행할 서비스 스크립트 id")
	private String scriptId = "";

	@FieldDescribe("공식 계정 템플릿 메시지 사용 여부")
	private Boolean messageEnable;
	@FieldDescribe("공식 계정 템플릿 메시지 id")
	private String tempMessageId;
	@FieldDescribe("템플릿 필드")
	private List<MPweixinMessageTemp> fieldList;

	public static final String default_apiAddress = "https://api.weixin.qq.com";
	public static final Boolean default_enable = false;

	public static Mpweixin defaultInstance() {
		return new Mpweixin();
	}

	public Mpweixin() {
		this.enable = default_enable;
		this.enablePublish = default_enable; // 게시 기능 비활성화, sample에서 사용자가 메뉴를 임의로 게시하는 것을 방지
		this.appid = "";
		this.appSecret = "";
		this.token = "";
		this.encodingAesKey = "";
		this.workUrl = "";
		this.portalId = "";
		this.scriptId = "";
		this.messageEnable = false;
		this.tempMessageId = "";
		this.fieldList = new ArrayList<>();
	}

	/**
	 * code로 accessToken, openid 등 정보 조회
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public WeixinAuth2AccessResp mpAuth2(String code) throws Exception {
		String address = default_apiAddress + "/sns/oauth2/access_token?appid=" + this.getAppid() + "&secret="
				+ this.getAppSecret() + "&code=" + code + "&grant_type=authorization_code";
		WeixinAuth2AccessResp resp = HttpConnection.getAsObject(address, null, WeixinAuth2AccessResp.class);
		if (resp.getErrcode() != null && resp.getErrcode() != 0) {
			throw new ExceptionMPWeixinAccessToken(resp.getErrcode(), resp.getErrmsg());
		}
		return resp;
	}

	private static String cacheAccessToken;
	private static Date cacheAccessTokenDate;

	/// cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
	// accesstoken 조회
	public String accessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cacheAccessToken) && (null != cacheAccessTokenDate))
				&& (cacheAccessTokenDate.after(new Date()))) {
			return cacheAccessToken;
		} else {
			String address = default_apiAddress + "/cgi-bin/token?grant_type=client_credential&appid=" + this.getAppid()
					+ "&secret=" + this.getAppSecret();
			System.out.println(address);
			WeixinAuth2AccessResp resp = HttpConnection.getAsObject(address, null, WeixinAuth2AccessResp.class);
			if (resp.getErrcode() != null && resp.getErrcode() != 0) {
				throw new ExceptionMPWeixinAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cacheAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cacheAccessTokenDate = cal.getTime();
			return cacheAccessToken;
		}
	}

	public Boolean getEnablePublish() {
		return enablePublish;
	}

	public void setEnablePublish(Boolean enablePublish) {
		this.enablePublish = enablePublish;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEncodingAesKey() {
		return encodingAesKey;
	}

	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}

	public String getPortalId() {
		return portalId;
	}

	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}

	public Boolean getMessageEnable() {
		return messageEnable;
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public String getTempMessageId() {
		return tempMessageId;
	}

	public void setTempMessageId(String tempMessageId) {
		this.tempMessageId = tempMessageId;
	}

	public List<MPweixinMessageTemp> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<MPweixinMessageTemp> fieldList) {
		this.fieldList = fieldList;
	}

	public String getScriptId() {
		return scriptId;
	}

	public void setScriptId(String scriptId) {
		this.scriptId = scriptId;
	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	/**
	 * 위챗에서 code로 accessToken, openid 등 데이터 조회
	 */
	public static class WeixinAuth2AccessResp extends GsonPropertyObject {
		private String access_token;
		private Integer expires_in;
		private String refresh_token;
		private String openid;
		private String scope;
		private String unionid;
		// 오류 정보 code
		private Integer errcode;
		private String errmsg;

		public Integer getErrcode() {
			return errcode;
		}

		public void setErrcode(Integer errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}

		public String getRefresh_token() {
			return refresh_token;
		}

		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}

		public String getOpenid() {
			return openid;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		public String getScope() {
			return scope;
		}

		public void setScope(String scope) {
			this.scope = scope;
		}

		public String getUnionid() {
			return unionid;
		}

		public void setUnionid(String unionid) {
			this.unionid = unionid;
		}
	}

	public static class MPweixinMessageTemp extends ConfigObject {

		@FieldDescribe("프로세스 필드명")
		private String name;
		@FieldDescribe("템플릿 필드명")
		private String tempName = "";

		public static MPweixinMessageTemp defaultInstance() {
			return new MPweixinMessageTemp();
		}

		public MPweixinMessageTemp() {
			this.name = "";
			this.tempName = "";
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTempName() {
			return tempName;
		}

		public void setTempName(String tempName) {
			this.tempName = tempName;
		}
	}

}
