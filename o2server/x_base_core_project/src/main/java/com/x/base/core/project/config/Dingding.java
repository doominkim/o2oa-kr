package com.x.base.core.project.config;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;

public class Dingding extends ConfigObject {

	@FieldDescribe("사용 여부")
	private Boolean enable;

	@FieldDescribe("딩톡 corpId")
	private String corpId;

	@FieldDescribe("agentId")
	private String agentId;

	@FieldDescribe("앱의 key, 고유 식별자")
	private String appKey;

	@FieldDescribe("앱의 비밀 키")
	private String appSecret;

	@FieldDescribe("콜백 신호로 동기화 검사 실행, 기본 10분마다 실행, 그 사이 딩톡 콜백 신호를 받으면 사용자 동기화 작업 실행.")
	private String syncCron;

	@FieldDescribe("강제 동기화 cron, 기본값 매일 8시와 12시에 강제 동기화.")
	private String forceSyncCron;

	@FieldDescribe("사용자만 동기화할지 여부(신규 사용자 생성 및 휴대폰 번호로 딩톡 ID 연결).")
	private Boolean syncPersonOnly;

	@FieldDescribe("oapi 서버 주소")
	private String oapiAddress;

	@FieldDescribe("콜백 token")
	private String token = "";

	@FieldDescribe("콜백 encodingAesKey")
	private String encodingAesKey = "";

	@FieldDescribe("딩톡 메시지에서 업무 문서를 여는 url 주소, 예: https://sample.o2oa.net/x_desktop/")
	private String workUrl = "";

	@FieldDescribe("딩톡 메시지 처리 완료 후 이동할 특정 포털 페이지의 Id")
	private String messageRedirectPortal = "";

	@FieldDescribe("메시지 푸시 사용 여부")
	private Boolean messageEnable;

	@FieldDescribe("딩톡 QR 코드 로그인 사용 여부")
	private Boolean scanLoginEnable;

	@FieldDescribe("딩톡 QR 코드 로그인 AppId")
	private String scanLoginAppId;

	@FieldDescribe("딩톡 QR 코드 로그인 appSecret")
	private String scanLoginAppSecret;

	@FieldDescribe("근태 정보 사용 여부")
	private Boolean attendanceSyncEnable;

	@FieldDescribe("관련 조직(상하위 조직) 동기화 여부")
	private Boolean syncUnionOrgEnable;

	public static Dingding defaultInstance() {
		return new Dingding();
	}

	public static final String dingdingLogo = "https://res.o2oa.net/app/dingding-logo.png";
	public static final Boolean default_enable = false;
	public static final String default_appKey = "";
	public static final String default_corpId = "";
	public static final String default_appSecret = "";
	public static final String default_agentId = "";
	public static final String default_syncCron = "10 0/10 * * * ?";
	public static final String default_forceSyncCron = "10 45 8,12 * * ?";
	public static final String default_oapiAddress = "https://oapi.dingtalk.com";
	public static final String default_workUrl = "";
	public static final String default_messageRedirectPortal = "";
	public static final Boolean default_messageEnable = true;
	public static final Boolean default_scanLoginEnable = false;
	public static final String default_scanLoginAppId = "";
	public static final String default_scanLoginAppSecret = "";
	public static final Boolean default_attendanceSyncEnable = false;
	public static final Boolean default_syncUnionOrgEnable = true;

	public Dingding() {
		this.enable = default_enable;
		this.corpId = default_corpId;
		this.appKey = default_appKey;
		this.appSecret = default_appSecret;
		this.agentId = default_agentId;
		this.syncCron = default_syncCron;
		this.forceSyncCron = default_forceSyncCron;
		this.oapiAddress = default_oapiAddress;
		this.workUrl = default_workUrl;
		this.messageRedirectPortal = default_messageRedirectPortal;
		this.messageEnable = default_messageEnable;
		this.scanLoginEnable = default_scanLoginEnable;
		this.scanLoginAppId = default_scanLoginAppId;
		this.scanLoginAppSecret = default_scanLoginAppSecret;
		this.attendanceSyncEnable = default_attendanceSyncEnable;
		this.syncUnionOrgEnable = default_syncUnionOrgEnable;
		this.syncPersonOnly = false;
	}

	private static String cachedCorpAccessToken;
	private static Date cachedCorpAccessTokenDate;

	private static String cachedJsapiTicket;
	private static Date cachedJsapiTicketDate;

	public Boolean getMessageEnable() {
		return BooleanUtils.isTrue(this.messageEnable);
	}

	public String getAgentId() {
		return StringUtils.isEmpty(this.agentId) ? default_agentId : this.agentId;
	}

	public String getOapiAddress() {
		return StringUtils.isEmpty(this.oapiAddress) ? default_oapiAddress : this.oapiAddress;
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public String getSyncCron() {
		return StringUtils.isEmpty(this.syncCron) ? default_syncCron : this.syncCron;
	}

	public String getForceSyncCron() {
		return StringUtils.isEmpty(this.forceSyncCron) ? default_forceSyncCron : this.forceSyncCron;
	}

	public Boolean getSyncPersonOnly() {
		return BooleanUtils.isTrue(syncPersonOnly);
	}

	public void setSyncPersonOnly(Boolean syncPersonOnly) {
		this.syncPersonOnly = syncPersonOnly;
	}

	public String corpAccessToken() throws Exception {
		if ((StringUtils.isNotEmpty(cachedCorpAccessToken) && (null != cachedCorpAccessTokenDate))
				&& (cachedCorpAccessTokenDate.after(new Date()))) {
			return cachedCorpAccessToken;
		} else {
			String address = this.getOapiAddress() + "/gettoken?appkey=" + this.getAppKey() + "&appsecret="
					+ this.getAppSecret();
			AccessTokenResp resp = HttpConnection.getAsObject(address, null, AccessTokenResp.class);
			if (resp.getErrcode() != 0) {
				throw new ExceptionDingdingCorpAccessToken(resp.getErrcode(), resp.getErrmsg());
			}
			cachedCorpAccessToken = resp.getAccess_token();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MINUTE, 90);
			cachedCorpAccessTokenDate = cal.getTime();
			return cachedCorpAccessToken;
		}
	}

	public String getJsapiTicket() throws Exception {
		if ((StringUtils.isNotEmpty(cachedJsapiTicket) && (null != cachedJsapiTicketDate))
				&& (cachedJsapiTicketDate.after(new Date()))) {
			return cachedJsapiTicket;
		} else {
			String address = getOapiAddress() + "/get_jsapi_ticket?access_token=" + corpAccessToken()
					+ "&type=jsapi";
			JsapiTicketResp resp = HttpConnection.getAsObject(address, null, JsapiTicketResp.class);
			cachedJsapiTicket = resp.getTicket();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, 1);
			cachedJsapiTicketDate = cal.getTime();
			return cachedJsapiTicket;
		}
	}

	public static class AccessTokenResp {

		private String access_token;
		private Integer errcode;
		private String errmsg;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

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
	}

	public static class JsapiTicketResp {

		private String ticket;

		public String getTicket() {
			return ticket;
		}

		public void setTicket(String ticket) {
			this.ticket = ticket;
		}
	}

	public String getAppKey() {
		return appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public String getCorpId() {
		return corpId;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_DINGDING);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_DINGDING);
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setCorpId(String corpId) {
		this.corpId = corpId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public void setSyncCron(String syncCron) {
		this.syncCron = syncCron;
	}

	public void setForceSyncCron(String forceSyncCron) {
		this.forceSyncCron = forceSyncCron;
	}

	public void setOapiAddress(String oapiAddress) {
		this.oapiAddress = oapiAddress;
	}

	public void setMessageEnable(Boolean messageEnable) {
		this.messageEnable = messageEnable;
	}

	public String getWorkUrl() {
		return workUrl;
	}

	public void setWorkUrl(String workUrl) {
		this.workUrl = workUrl;
	}

	public String getMessageRedirectPortal() {
		return messageRedirectPortal;
	}

	public void setMessageRedirectPortal(String messageRedirectPortal) {
		this.messageRedirectPortal = messageRedirectPortal;
	}

	public Boolean getScanLoginEnable() {
		return scanLoginEnable;
	}

	public void setScanLoginEnable(Boolean scanLoginEnable) {
		this.scanLoginEnable = scanLoginEnable;
	}

	public String getScanLoginAppId() {
		return scanLoginAppId;
	}

	public void setScanLoginAppId(String scanLoginAppId) {
		this.scanLoginAppId = scanLoginAppId;
	}

	public String getScanLoginAppSecret() {
		return scanLoginAppSecret;
	}

	public void setScanLoginAppSecret(String scanLoginAppSecret) {
		this.scanLoginAppSecret = scanLoginAppSecret;
	}

	public Boolean getAttendanceSyncEnable() {
		return attendanceSyncEnable;
	}

	public void setAttendanceSyncEnable(Boolean attendanceSyncEnable) {
		this.attendanceSyncEnable = attendanceSyncEnable;
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

	public Boolean getSyncUnionOrgEnable() {
		return syncUnionOrgEnable;
	}

	public void setSyncUnionOrgEnable(Boolean syncUnionOrgEnable) {
		this.syncUnionOrgEnable = syncUnionOrgEnable;
	}
}
