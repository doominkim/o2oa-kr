package com.x.base.core.project.config;

import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class ApplicationServer extends ConfigObject {

	private static final long serialVersionUID = 4182216855396608843L;

	public static ApplicationServer defaultInstance() {
		ApplicationServer o = new ApplicationServer();
		o.enable = true;
		o.port = DEFAULT_PORT;
		o.sslEnable = false;
		o.proxyHost = "";
		o.proxyPort = DEFAULT_PORT;
		o.includes = new CopyOnWriteArrayList<>();
		o.excludes = new CopyOnWriteArrayList<>();
		return o;
	}

	private static final Integer DEFAULT_PORT = 80;

	@FieldDescribe("사용 여부")
	private Boolean enable;
	@FieldDescribe("http/https 포트, 프론트엔드에 데이터 접근 인터페이스 제공. 기본값 20020 포트.")
	private Integer port;
	@FieldDescribe("ssl 전송 암호화 사용 여부, 사용 시 config/keystore 파일을 키 파일로 사용. config/token.json 파일의 sslKeyStorePassword 필드를 키 비밀번호로, sslKeyManagerPassword를 관리 비밀번호로 사용.")
	private Boolean sslEnable;
	@FieldDescribe("프록시 호스트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 주소를 나타내기 위해 이 주소를 설정해야 함.")
	private String proxyHost;
	@FieldDescribe("프록시 포트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 포트를 나타내기 위해 이 주소를 설정해야 함.")
	private Integer proxyPort;
	@FieldDescribe("탑재 앱, 클러스터 환경에서 서버 부하를 줄이기 위해 일부 앱만 탑재하도록 선택 가능, *를 와일드카드로 사용 가능.")
	private CopyOnWriteArrayList<String> includes;
	@FieldDescribe("탑재하지 않을 앱 선택, includes 값과 함께 사용하여 탑재 앱을 선택하거나 제외 가능, *를 와일드카드로 사용 가능.")
	private CopyOnWriteArrayList<String> excludes;

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public Integer getPort() {
		if (null != this.port && this.port > 0 && this.port < 65535) {
			return this.port;
		}
		return DEFAULT_PORT;
	}

	public Boolean getSslEnable() {
		return BooleanUtils.isTrue(this.sslEnable);
	}

	public String getProxyHost() throws Exception {
		return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
	}

	public Integer getProxyPort() {
		if (null != this.proxyPort && this.proxyPort > 0) {
			return this.proxyPort;
		}
		return this.getPort();
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setSslEnable(Boolean sslEnable) {
		this.sslEnable = sslEnable;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(Integer proxyPort) {
		this.proxyPort = proxyPort;
	}

	public CopyOnWriteArrayList<String> getIncludes() {
		return includes;
	}

	public void setIncludes(CopyOnWriteArrayList<String> includes) {
		this.includes = includes;
	}

	public CopyOnWriteArrayList<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(CopyOnWriteArrayList<String> excludes) {
		this.excludes = excludes;
	}

}
