package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

public class CenterServer extends ConfigObject {

	private static final long serialVersionUID = 8147826320846595611L;

	private static final Boolean DEFAULT_ENABLE = true;
	private static final Integer DEFAULT_PORT = 80;
	private static final Integer DEFAULT_ORDER = 0;

	public static CenterServer defaultInstance() {
		CenterServer o = new CenterServer();
		o.enable = DEFAULT_ENABLE;
		o.order = DEFAULT_ORDER;
		o.sslEnable = false;
		o.port = DEFAULT_PORT;
		o.httpProtocol = "";
		o.proxyHost = "";
		o.proxyPort = DEFAULT_PORT;
		return o;
	}

	@FieldDescribe("사용 여부")
	private Boolean enable;
	@FieldDescribe("center 노드 순서, 0,1,2... 순으로 배열")
	private Integer order;
	@FieldDescribe("ssl 전송 암호화 사용 여부, 사용 시 config/keystore 파일을 키 파일로 사용. config/token.json 파일의 sslKeyStorePassword 필드를 키 비밀번호로, sslKeyManagerPassword를 관리 비밀번호로 사용.")
	private Boolean sslEnable;
	@FieldDescribe("포트, center 서버 포트, 기본값 20030")
	private Integer port;
	@FieldDescribe("외부 http 접근 프로토콜, http/https")
	private String httpProtocol;
	@FieldDescribe("프록시 호스트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 주소를 나타내기 위해 이 주소를 설정해야 함.")
	private String proxyHost;
	@FieldDescribe("프록시 포트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 포트를 나타내기 위해 이 주소를 설정해야 함.")
	private Integer proxyPort;

	public Boolean getEnable() {
		return enable == null ? DEFAULT_ENABLE : this.enable;
	}

	public String getHttpProtocol() {
		return StringUtils.equals("https", this.httpProtocol) ? "https" : "http";
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return DEFAULT_PORT;
	}

	public String getProxyHost() {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return this.getPort();
	}

	public Integer getOrder() {
		return order == null ? DEFAULT_ORDER : this.order;
	}

	public void save() throws IOException, URISyntaxException {
		File file = new File(Config.base(), Config.PATH_CONFIG_CENTERSERVER);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setHttpProtocol(String httpProtocol) {
		this.httpProtocol = httpProtocol;
	}

}
