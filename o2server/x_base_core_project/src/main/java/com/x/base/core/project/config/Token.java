package com.x.base.core.project.config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;

public class Token extends ConfigObject {

	private static final long serialVersionUID = 397619753053929409L;

	private static final String surfix = "o2platform";

	public static final String defaultInitialManager = "xadmin";

	public static final String defaultInitialManagerDistinguishedName = "xadmin@o2oa@P";

	public static final String initPassword = "o2oa@2022";

	public static final String defaultSslKeyStorePassword = "123456";
	public static final String defaultSslKeyManagerPassword = "123456";

	public static final Boolean DEFAULT_RSAENABLE = false;

	// 이 객체는 임시 계산용으로 저장 불필요
	private transient String _cipher = "";
	// 이 객체는 임시 계산용으로 저장 불필요
	private transient String _password = "";

	public static Token defaultInstance() {
		return new Token();
	}

	public Token() {
		this.key = "";
		this.password = "";
		this.sslKeyStorePassword = defaultSslKeyStorePassword;
		this.sslKeyManagerPassword = defaultSslKeyManagerPassword;

		this.rsaEnable = DEFAULT_RSAENABLE;
	}

	// 암호화용 key, 비밀번호 암호화에 사용
	@FieldDescribe("비밀번호 암호화용 비밀 키, 변경 시 사용자 비밀번호 검증 실패 발생.")
	private String key;

	@FieldDescribe("초기 관리자 비밀번호, 내부 데이터베이스와 FTP 파일 서버, http의 token 암호화에 사용.")
	private String password;

	@FieldDescribe("ssl 비밀번호")
	private String sslKeyStorePassword;

	@FieldDescribe("ssl 관리 비밀번호")
	private String sslKeyManagerPassword;

	@FieldDescribe("LDAP 인증 설정")
	private LdapAuth ldapAuth;

	@FieldDescribe("sso 로그인 설정")
	private List<Sso> ssos = new ArrayList<>();

	@FieldDescribe("oauth SSO 설정")
	private List<Oauth> oauths = new ArrayList<>();

	@FieldDescribe("클라이언트로서의 SSO 설정")
	private List<OauthClient> oauthClients = new ArrayList<>();

	@FieldDescribe("rsa 암호화 사용.")
	private Boolean rsaEnable = DEFAULT_RSAENABLE;

	public Boolean getRsaEnable() {
		return null == this.rsaEnable ? DEFAULT_RSAENABLE : this.rsaEnable;
	}

	// 앞의 코드는 key+surfix 결과는 nullo2platform
	public String getKey() {
		String val = Objects.toString(key, "") + surfix;
		return StringUtils.substring(val, 0, 8);
	}

	public void setKey(String key) {
		if (StringUtils.equals(key, StringUtils.substring(surfix, 0, 8))) {
			this.key = null;
		} else {
			this.key = key;
		}
	}

	public String getCipher() {
		if (StringUtils.isEmpty(this._cipher)) {
			this._cipher = DigestUtils.md5Hex(this.getPassword());
		}
		return this._cipher;
	}

	public String getPassword() {
		if (StringUtils.isEmpty(this._password)) {
			this._password = StringUtils.isEmpty(this.password) ? initPassword : Crypto.plainText(this.password);
		}
		return this._password;
	}

	public String getConfigPwd() {
		return Crypto.plainText(this.password);
	}

	public void setPassword(String password)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		this.password = Crypto.formattedDefaultEncrypt(password);
	}

	public String getInitialManager() {
		return defaultInitialManager;
	}

	public String getInitialManagerDistinguishedName() {
		return defaultInitialManagerDistinguishedName;
	}

	public String getSslKeyStorePassword() {
		return StringUtils.isEmpty(this.sslKeyStorePassword) ? defaultSslKeyStorePassword : this.sslKeyStorePassword;
	}

	public void setSslKeyStorePassword(String sslKeyStorePassword) {
		if (StringUtils.equals(sslKeyStorePassword, defaultSslKeyStorePassword)) {
			this.sslKeyStorePassword = null;
		} else {
			this.sslKeyStorePassword = sslKeyStorePassword;
		}
	}

	public String getSslKeyManagerPassword() {
		return StringUtils.isEmpty(this.sslKeyManagerPassword) ? defaultSslKeyManagerPassword
				: this.sslKeyManagerPassword;
	}

	public void setSslKeyManagerPassword(String sslKeyManagerPassword) {
		if (StringUtils.equals(sslKeyManagerPassword, defaultSslKeyManagerPassword)) {
			this.sslKeyManagerPassword = null;
		} else {
			this.sslKeyManagerPassword = sslKeyManagerPassword;
		}
	}

	public List<Oauth> getOauths() {
		if (null == this.oauths) {
			return new ArrayList<>();
		}
		return this.oauths;
	}

	public List<Sso> getSsos() {
		if (null == this.ssos) {
			return new ArrayList<>();
		}
		return this.ssos;
	}

	public LdapAuth getLdapAuth() {
		return this.ldapAuth == null ? LdapAuth.defaultInstance() : this.ldapAuth;
	}

	public void setLdapAuth(LdapAuth ldapAuth) {
		this.ldapAuth = ldapAuth;
	}

	public void setOauths(List<Oauth> oauths) {
		this.oauths = oauths;
	}

	public void setSsos(List<Sso> ssos) {
		this.ssos = ssos;
	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_TOKEN);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_TOKEN);
	}

	public boolean isInitialManager(String name) throws Exception {
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return Config.ternaryManagement().isTernaryManagement(name);
		} else if(BooleanUtils.isTrue(Config.person().getXadminEnable())){
			return StringUtils.equals(this.getInitialManager(), name)
					|| StringUtils.equals(this.getInitialManagerDistinguishedName(), name);
		}
		return false;
	}

	public boolean verifyPassword(String name, String password) throws Exception {
		if (BooleanUtils.isTrue(Config.ternaryManagement().getEnable())) {
			return Config.ternaryManagement().verifyPassword(name, password);
		} else {
			return StringUtils.equals(this.getPassword(), password);
		}
	}

	public InitialManager initialManagerInstance() {
		InitialManager o = new InitialManager();
		String name = this.getInitialManager();
		o.name = name;
		o.id = name;
		o.employee = name;
		o.display = name;
		o.mail = name + "@o2oa.net";
		o.setDistinguishedName(defaultInitialManagerDistinguishedName);
		o.weixin = "";
		o.qq = "";
		o.weibo = "";
		o.mobile = "";
		o.roleList = new ArrayList<>();
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.Manager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.OrganizationManager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.MeetingManager));
		o.roleList.add(OrganizationDefinition.toDistinguishedName(OrganizationDefinition.ProcessPlatformManager));
		return o;
	}

	public class InitialManager extends GsonPropertyObject {

		private static final long serialVersionUID = 6295964037824026773L;

		private String name;
		private String unique;
		private String id;
		private String distinguishedName;
		private String employee;
		private String display;
		private String mail;
		private String weixin;
		private String qq;
		private String weibo;
		private String mobile;
		private String pinyin;
		private String pinyinInitial;
		private List<String> roleList;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmployee() {
			return employee;
		}

		public void setEmployee(String employee) {
			this.employee = employee;
		}

		public String getDisplay() {
			return display;
		}

		public void setDisplay(String display) {
			this.display = display;
		}

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getWeixin() {
			return weixin;
		}

		public void setWeixin(String weixin) {
			this.weixin = weixin;
		}

		public String getQq() {
			return qq;
		}

		public void setQq(String qq) {
			this.qq = qq;
		}

		public String getWeibo() {
			return weibo;
		}

		public void setWeibo(String weibo) {
			this.weibo = weibo;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public String getUnique() {
			return unique;
		}

		public void setUnique(String unique) {
			this.unique = unique;
		}

		public String getDistinguishedName() {
			return distinguishedName;
		}

		public void setDistinguishedName(String distinguishedName) {
			this.distinguishedName = distinguishedName;
		}

		public String getPinyin() {
			return pinyin;
		}

		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}

		public String getPinyinInitial() {
			return pinyinInitial;
		}

		public void setPinyinInitial(String pinyinInitial) {
			this.pinyinInitial = pinyinInitial;
		}

	}

	public Oauth findOauth(String clientId) {
		for (Oauth o : this.getOauths()) {
			if (StringUtils.equalsIgnoreCase(clientId, o.getClientId())) {
				return o;
			}
		}
		return null;
	}

	public Sso findSso(String client) {
		for (Sso o : this.getSsos()) {
			if (StringUtils.equalsIgnoreCase(client, o.getClient())) {
				return o;
			}
		}
		return null;
	}

	public static class Oauth extends ConfigObject {

		public static Oauth defaultInstance() {
			return new Oauth();
		}

		public Oauth() {
			this.enable = false;
			this.clientId = "";
			this.mapping = new LinkedHashMap<>();

		}

		@FieldDescribe("사용 여부")
		private Boolean enable;

		@FieldDescribe("클라이언트 이름")
		private String clientId;

		@FieldDescribe("비밀 키")
		private String clientSecret;

		@FieldDescribe("로그인 주소")
		private String loginUrl;

		@FieldDescribe("반환값")
		private Map<String, String> mapping;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public Map<String, String> getMapping() {
			if (null == mapping) {
				return new LinkedHashMap<>();
			}
			return mapping;
		}

		public void setMapping(Map<String, String> mapping) {
			this.mapping = mapping;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public String getLoginUrl() {
			return loginUrl;
		}

		public void setLoginUrl(String loginUrl) {
			this.loginUrl = loginUrl;
		}

	}

	public static class OauthClient extends ConfigObject {

		public static OauthClient defaultInstance() {
			return new OauthClient();
		}

		public static final String default_authParameter = "client_id={$client_id}&redirect_uri={$redirect_uri}";
		public static final String default_infoParameter = "access_token={$access_token}";
		public static final String default_tokenParameter = "client_id={$client_id}&client_secret={$client_secret}&redirect_uri={$redirect_uri}&grant_type=authorization_code&code={$code}";

		public OauthClient() {
			this.enable = false;
			this.name = "";
			this.displayName = "";
			this.icon = "";
			this.clientId = "";
			this.clientSecret = "";
			this.authAddress = "";
			this.authParameter = default_authParameter;
			this.authMethod = "GET";
			this.tokenAddress = "";
			this.tokenParameter = default_tokenParameter;
			this.tokenMethod = "POST";
			this.tokenType = "json";
			this.infoAddress = "";
			this.infoParameter = default_infoParameter;
			this.infoMethod = "GET";
			this.infoType = "json";
			this.infoCredentialField = "openId";
			this.infoScriptText = "";
			this.bindingEnable = false;
			this.bindingField = "";
		}

		@FieldDescribe("사용 여부.")
		private Boolean enable = false;
		@FieldDescribe("이름.")
		private String name = "";
		@FieldDescribe("표시 이름.")
		private String displayName = "";
		@FieldDescribe("아이콘.")
		private String icon = "";
		@FieldDescribe("사용자 oauth2 인증의 client_id.")
		private String clientId = "";
		@FieldDescribe("사용자 oauth2 인증의 client_secret.")
		private String clientSecret = "";
		@FieldDescribe("인증 후 이동 주소.")
		private String authAddress = "";
		@FieldDescribe("키 요청 메서드 파라미터.")
		private String authParameter = "";
		@FieldDescribe("키 요청 메서드. 일반적으로 GET")
		private String authMethod = "GET";
		@FieldDescribe("토큰 요청 URL.")
		private String tokenAddress = "";
		@FieldDescribe("토큰 요청 메서드 파라미터.")
		private String tokenParameter = "";
		@FieldDescribe("토큰 요청 메서드. 일반적으로 POST")
		private String tokenMethod = "POST";
		@FieldDescribe("token 정보 형식. json 형식 또는 form 형식")
		private String tokenType = "json";
		@FieldDescribe("정보 요청 URL.")
		private String infoAddress = "";
		@FieldDescribe("정보 요청 메서드 파라미터.")
		private String infoParameter = "";
		@FieldDescribe("정보 요청 메서드. 일반적으로 GET")
		private String infoMethod = "GET";
		@FieldDescribe("info 정보 형식. json 형식, form 형식 또는 script 형식")
		private String infoType = "json";
		@FieldDescribe("info 정보에서 개인을 식별하는 필드.")
		private String infoCredentialField = "username";
		@FieldDescribe("info 정보에서 개인을 식별하는 필드.")
		private String infoScriptText = "";
		@FieldDescribe("사용자 바인딩 허용 여부, 허용 시 사용자가 직접 바인딩 가능.")
		private Boolean bindingEnable = false;
		@FieldDescribe("바인딩 필드, 상대측 사용자 식별자, 일반적으로 openId를 개인 필드에 바인딩, 선택 가능 값은 open1Id,open2Id,open3Id,open4Id,open5Id")
		private String bindingField = "";

		public String getDisplayName() {
			return displayName;
		}

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getClientSecret() {
			return clientSecret;
		}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public String getAuthAddress() {
			return authAddress;
		}

		public void setAuthAddress(String authAddress) {
			this.authAddress = authAddress;
		}

		public String getTokenAddress() {
			return tokenAddress;
		}

		public void setTokenAddress(String tokenAddress) {
			this.tokenAddress = tokenAddress;
		}

		public String getInfoAddress() {
			return infoAddress;
		}

		public void setInfoAddress(String infoAddress) {
			this.infoAddress = infoAddress;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAuthMethod() {
			return authMethod;
		}

		public void setAuthMethod(String authMethod) {
			this.authMethod = authMethod;
		}

		public String getTokenMethod() {
			return tokenMethod;
		}

		public void setTokenMethod(String tokenMethod) {
			this.tokenMethod = tokenMethod;
		}

		public String getInfoMethod() {
			return infoMethod;
		}

		public void setInfoMethod(String infoMethod) {
			this.infoMethod = infoMethod;
		}

		public String getInfoType() {
			return infoType;
		}

		public void setInfoType(String infoType) {
			this.infoType = infoType;
		}

		public String getInfoCredentialField() {
			return infoCredentialField;
		}

		public void setInfoCredentialField(String infoCredentialField) {
			this.infoCredentialField = infoCredentialField;
		}

		public String getAuthParameter() {
			return authParameter;
		}

		public void setAuthParameter(String authParameter) {
			this.authParameter = authParameter;
		}

		public String getTokenParameter() {
			return tokenParameter;
		}

		public void setTokenParameter(String tokenParameter) {
			this.tokenParameter = tokenParameter;
		}

		public String getInfoParameter() {
			return infoParameter;
		}

		public void setInfoParameter(String infoParameter) {
			this.infoParameter = infoParameter;
		}

		public String getTokenType() {
			return tokenType;
		}

		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getInfoScriptText() {
			return infoScriptText;
		}

		public void setInfoScriptText(String infoScriptText) {
			this.infoScriptText = infoScriptText;
		}

		public Boolean getBindingEnable() {
			return bindingEnable;
		}

		public void setBindingEnable(Boolean bindingEnable) {
			this.bindingEnable = bindingEnable;
		}

		public String getBindingField() {
			return bindingField;
		}

		public void setBindingField(String bindingField) {
			this.bindingField = bindingField;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

	}

	public static class Sso extends ConfigObject {

		public static Sso defaultInstance() {
			return new Sso();
		}

		public Sso() {
			this.enable = false;
			this.client = "";
			this.key = "";
		}

		@FieldDescribe("사용 여부")
		private Boolean enable;

		@FieldDescribe("이름")
		private String client;

		@FieldDescribe("비밀 키")
		private String key;

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
	}

	public static class LdapAuth extends ConfigObject {

		public static LdapAuth defaultInstance() {
			return new LdapAuth();
		}

		public LdapAuth() {
			this.enable = false;
			this.ldapUrl = "";
			this.bindDnUser = "";
			this.bindDnPwd = "";
			this.baseDn = "";
			this.userDn = "";
		}

		@FieldDescribe("사용 여부")
		private Boolean enable;

		@FieldDescribe("LDAP 서버 예:ldap://127.0.0.1:389")
		private String ldapUrl;

		@FieldDescribe("BindDn 사용자(관리 권한이 있는 사용자), 예:cn=root")
		private String bindDnUser;

		@FieldDescribe("BindDn 사용자의 비밀번호")
		private String bindDnPwd;

		@FieldDescribe("LDAP 조회 루트 이름 예:dc=zone,DC=COM")
		private String baseDn;

		@FieldDescribe("인증 사용자 바인딩 속성:uid, 휴대폰 번호, 직원 번호 또는 이메일(baseDn에서 조회된 데이터가 유일하고 o2에서 연결된 사용자를 찾을 수 있어야 함)")
		private String userDn;

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getLdapUrl() {
			return ldapUrl;
		}

		public void setLdapUrl(String ldapUrl) {
			this.ldapUrl = ldapUrl;
		}

		public String getBaseDn() {
			return baseDn;
		}

		public void setBaseDn(String baseDn) {
			this.baseDn = baseDn;
		}

		public String getUserDn() {
			return userDn;
		}

		public void setUserDn(String userDn) {
			this.userDn = userDn;
		}

		public String getBindDnUser() {
			return bindDnUser;
		}

		public void setBindDnUser(String bindDnUser) {
			this.bindDnUser = bindDnUser;
		}

		public String getBindDnPwd() {
			return StringUtils.isBlank(this.bindDnPwd) ? this.bindDnPwd : Crypto.plainText(this.bindDnPwd);
		}

		public void setBindDnPwd(String bindDnPwd) {
			this.bindDnPwd = bindDnPwd;
		}
	}

	public List<OauthClient> getOauthClients() {
		return oauthClients;
	}

	public void setOauthClients(List<OauthClient> oauthClients) {
		this.oauthClients = oauthClients;
	}

}
