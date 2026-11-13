package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class DataServer extends ConfigObject {

    private static final long serialVersionUID = 77120835343101221L;

    private static final Integer DEFAULT_TCPPORT = 20050;
    private static final Integer DEFAULT_WEBPORT = null;
    private static final Integer DEFAULT_CACHESIZE = 512;
    private static final Boolean DEFAULT_JMXENABLE = false;
    private static final Integer DEFAULT_MAXTOTAL = 50;
    private static final Integer DEFAULT_MAXIDLE = 0;
    private static final Boolean DEFAULT_STATENABLE = true;
    private static final String DEFAULT_STATFILTER = "mergeStat";
    private static final Boolean DEFAULT_SLOWSQLENABLE = true;
    private static final Integer DEFAULT_SLOWSQLTHRESHOLD = 3000;
    private static final Boolean DEFAULT_LOGSTATENABLE = false;
    private static final Integer DEFAULT_LOGSTATINTERVAL = 180;
    private static final Integer DEFAULT_LOCKTIMEOUT = 120000;
    private static final String DEFAULT_LOGLEVEL = "WARN";

    public static DataServer defaultInstance() {
        return new DataServer();
    }

    public DataServer() {
        this.enable = true;
        this.tcpPort = DEFAULT_TCPPORT;
        this.webPort = DEFAULT_WEBPORT;
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.cacheSize = DEFAULT_CACHESIZE;
        this.jmxEnable = DEFAULT_JMXENABLE;
        this.maxTotal = DEFAULT_MAXTOTAL;
        this.maxIdle = DEFAULT_MAXIDLE;
        this.logLevel = DEFAULT_LOGLEVEL;
        this.statEnable = DEFAULT_STATENABLE;
        this.statFilter = DEFAULT_STATFILTER;
        this.slowSqlEnable = DEFAULT_SLOWSQLENABLE;
        this.slowSqlThreshold = DEFAULT_SLOWSQLTHRESHOLD;
        this.lockTimeout = DEFAULT_LOCKTIMEOUT;
        this.logStatEnable = DEFAULT_LOGSTATENABLE;
        this.logStatInterval = DEFAULT_LOGSTATINTERVAL;
    }

    @FieldDescribe("사용 여부, 사용 가능한 externalDataSources.json 파일이 없으면 기본적으로 노드에서 로컬 H2 데이터베이스를 기본 데이터베이스로 사용.")
    private Boolean enable;
    @FieldDescribe("H2 데이터베이스 jdbc 연결 포트, 로그인 사용자 이름: sa, 비밀번호는 xadmin 비밀번호. 데이터베이스는 /o2server/local/repository/data/X.mv.db에 생성되며, 데이터베이스 파일이 생성되면 해당 데이터베이스 비밀번호가 생성됨.")
    private Integer tcpPort;
    @FieldDescribe("H2 데이터베이스 web 포트, H2는 web 클라이언트를 제공하며 이 포트는 web 클라이언트 접근 포트. 사용자 이름 sa, 비밀번호는 xadmin 데이터베이스 최초 생성 비밀번호.")
    private Integer webPort;
    @FieldDescribe("이 데이터베이스에 저장할 클래스 설정, 기본적으로 모든 유형을 저장하며, 고성능을 위해 클래스별로 개별 제어가 필요하면 다른 클래스를 다른 노드에 저장하여 성능을 높일 수 있음. 와일드카드 * 사용 가능")
    private List<String> includes;
    @FieldDescribe("이 노드에 저장하지 않을 클래스, includes와 함께 실제 저장 클래스를 설정, 와일드카드 * 사용 가능")
    private List<String> excludes;
    @FieldDescribe("jmx 시작 여부, 사용 시 로컬 jmx 클라이언트로 접근 가능, 원격 jmx 클라이언트는 미지원.")
    private Boolean jmxEnable;
    @FieldDescribe("H2 데이터베이스 캐시 크기, H2가 캐시로 사용할 메모리 크기를 M 단위로 설정, 기본값 512M.")
    private Integer cacheSize;
    @FieldDescribe("기본 로그 레벨, FATAL, ERROR, WARN, INFO, TRACE. 전체 설정은 DefaultLevel=WARN, Tool=TRACE, Enhance=TRACE, METADATA=TRACE, Runtime=TRACE, Query=TRACE, DataCache=TRACE, JDBC=TRACE, SQL=TRACE")
    private String logLevel;
    @FieldDescribe("최대 사용 연결 수")
    private Integer maxTotal;
    @FieldDescribe("최대 유휴 연결 수")
    private Integer maxIdle;
    @FieldDescribe("통계 사용, 기본값 사용 안 함")
    private Boolean statEnable;
    @FieldDescribe("통계 방식 설정, 기본값 mergeStat")
    private String statFilter;
    @FieldDescribe("기본 잠금 시간 제한(밀리초).")
    private Integer lockTimeout;
    @FieldDescribe("통계 로그 기록 사용.")
    private Boolean logStatEnable;
    @FieldDescribe("통계 로그 출력 간격.")
    private Integer logStatInterval;
    @FieldDescribe("느린 sql 실행 기록 사용 여부.")
    private Boolean slowSqlEnable;
    @FieldDescribe("느린 sql 실행 기록 임계값, 밀리초, 기본값 3000밀리초.")
    private Integer slowSqlThreshold;

    public Boolean getLogStatEnable() {
        return BooleanUtils.isTrue(this.logStatEnable);
    }

    public Integer getLogStatInterval() {
        return (null == this.logStatInterval || this.logStatInterval < 1) ? DEFAULT_LOGSTATINTERVAL
                : this.logStatInterval;
    }

    public Integer getLockTimeout() {
        return (null == this.lockTimeout || this.lockTimeout < 1) ? DEFAULT_LOCKTIMEOUT : this.lockTimeout;
    }

    public String getLogLevel() {
        return StringUtils.isEmpty(this.logLevel) ? DEFAULT_LOGLEVEL : this.logLevel;
    }

    public Boolean getSlowSqlEnable() {
        return BooleanUtils.isTrue(this.slowSqlEnable);
    }

    public Integer getSlowSqlThreshold() {
        return (null == this.slowSqlThreshold || this.slowSqlThreshold < 1) ? DEFAULT_SLOWSQLTHRESHOLD
                : this.slowSqlThreshold;
    }

    public String getStatFilter() {
        return StringUtils.isEmpty(this.statFilter) ? DEFAULT_STATFILTER : this.statFilter;
    }

    public Boolean getStatEnable() {
        return BooleanUtils.isNotFalse(this.statEnable);
    }

    public Integer getMaxIdle() {
        if ((this.maxIdle == null) || (this.maxIdle < 1)) {
            return DEFAULT_MAXIDLE;
        } else {
            return this.maxTotal;
        }
    }

    public Integer getMaxTotal() {
        if ((this.maxTotal == null) || (this.maxTotal < 0)) {
            return DEFAULT_MAXTOTAL;
        } else {
            return this.maxTotal;
        }
    }

    public Boolean getJmxEnable() {
        return BooleanUtils.isTrue(this.jmxEnable);
    }

    public Integer getCacheSize() {
        return (this.cacheSize == null || this.cacheSize < DEFAULT_CACHESIZE) ? DEFAULT_CACHESIZE : this.cacheSize;
    }

    public Integer getTcpPort() {
        if (null != this.tcpPort && this.tcpPort > 0) {
            return this.tcpPort;
        }
        return DEFAULT_TCPPORT;
    }

    public Integer getWebPort() {
        if (null != this.webPort && this.webPort > 0) {
            return this.webPort;
        }
        return DEFAULT_WEBPORT;
    }

    public Boolean getEnable() {
        return BooleanUtils.isTrue(this.enable);
    }

    public List<String> getIncludes() {
        if (null != this.includes) {
            return this.includes;
        }
        return new ArrayList<>();
    }

    public List<String> getExcludes() {
        if (null != this.excludes) {
            return this.excludes;
        }
        return new ArrayList<>();
    }

    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setWebPort(Integer webPort) {
        this.webPort = webPort;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setJmxEnable(Boolean jmxEnable) {
        this.jmxEnable = jmxEnable;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

}
