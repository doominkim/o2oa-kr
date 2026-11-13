package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class WebServer extends ConfigObject {

    private static final long serialVersionUID = 7240874589722986538L;

    public static WebServer defaultInstance() {
        WebServer o = new WebServer();
        o.enable = true;
        o.port = DEFAULT_HTTP_PORT;
        o.sslEnable = false;
        o.proxyHost = "";
        o.proxyPort = DEFAULT_HTTP_PORT;
        o.proxyCenterEnable = DEFAULT_PROXYCENTERENABLE;
        o.proxyApplicationEnable = DEFAULT_PROXYAPPLICATIONENABLE;
        o.proxyTimeOut = DEFAULT_PROXY_TIMEOUT;
        return o;
    }

    private static final Integer DEFAULT_HTTP_PORT = 80;
    private static final Integer DEFAULT_HTTPS_PORT = 443;
    private static final Boolean DEFAULT_PROXYCENTERENABLE = true;
    private static final Boolean DEFAULT_PROXYAPPLICATIONENABLE = true;
    private static final Integer DEFAULT_PROXY_TIMEOUT = 300;

    @FieldDescribe("사용 여부")
    private Boolean enable;
    @FieldDescribe("http/https 포트, 사용자가 주소 입력 후 실제로 접근하는 첫 번째 포트. http 프로토콜 기본 80 포트, https 기본 443 포트.")
    private Integer port;
    @FieldDescribe("ssl 전송 암호화 사용 여부, 사용 시 config/keystore 파일을 키 파일로 사용. config/token.json 파일의 sslKeyStorePassword 필드를 키 비밀번호로, sslKeyManagerPassword를 관리 비밀번호로 사용.")
    private Boolean sslEnable;
    @FieldDescribe("프록시 호스트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 주소를 나타내기 위해 이 주소를 설정해야 함.")
    private String proxyHost;
    @FieldDescribe("프록시 포트, 서버가 apache/nginx 등 프록시 서버를 통해 공인망에 매핑되거나 라우터로 포트 매핑된 경우 공인망 접근 포트를 나타내기 위해 이 주소를 설정해야 함.")
    private Integer proxyPort;
    @FieldDescribe("center 서버 프록시 사용 여부.")
    private Boolean proxyCenterEnable;
    @FieldDescribe("application 서버 프록시 사용 여부")
    private Boolean proxyApplicationEnable;
    @FieldDescribe("프록시 연결 시간 초과, 기본값 300(초)")
    private Integer proxyTimeOut;

    public Boolean getProxyCenterEnable() {
        return proxyCenterEnable == null ? DEFAULT_PROXYCENTERENABLE : this.proxyCenterEnable;
    }

    public Boolean getProxyApplicationEnable() {
        return proxyApplicationEnable == null ? DEFAULT_PROXYAPPLICATIONENABLE : this.proxyApplicationEnable;
    }

    public Boolean getEnable() {
        return BooleanUtils.isTrue(this.enable);
    }

    public Integer getPort() {
        if ((null != this.port) && (this.port > 0) && (this.port < 65535)) {
            return this.port;
        } else {
            if (BooleanUtils.isTrue(this.getSslEnable())) {
                return DEFAULT_HTTPS_PORT;
            } else {
                return DEFAULT_HTTP_PORT;
            }

        }
    }

    public Boolean getSslEnable() {
        return BooleanUtils.isTrue(this.sslEnable);
    }

    public String getProxyHost() {
        return StringUtils.isNotEmpty(this.proxyHost) ? this.proxyHost : "";
    }

    public Integer getProxyPort() {
        if (null != this.proxyPort && this.proxyPort > 0 && this.proxyPort < 65535) {
            return this.proxyPort;
        } else {
            return this.getPort();
        }
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

    public void setProxyApplicationEnable(Boolean proxyApplicationEnable) {
        this.proxyApplicationEnable = proxyApplicationEnable;
    }

    public void setProxyCenterEnable(Boolean proxyCenterEnable) {
        this.proxyCenterEnable = proxyCenterEnable;
    }

    public Integer getProxyTimeOut() {
        return proxyTimeOut == null ? DEFAULT_PROXY_TIMEOUT : this.proxyTimeOut;
    }

    public void setProxyTimeOut(Integer proxyTimeOut) {
        this.proxyTimeOut = proxyTimeOut;
    }

}
