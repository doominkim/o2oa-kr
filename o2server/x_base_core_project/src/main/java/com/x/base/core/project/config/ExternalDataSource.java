package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.Crypto;

public class ExternalDataSource extends ConfigObject {

    private static final long serialVersionUID = -2098883883740436217L;

	// 저장 불필요
    private transient String _password;

    public ExternalDataSource() {
    }

    public static final Boolean DEFAULT_ENABLE = false;

    public static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/X?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8";

    public static final String DEFAULT_USERNAME = "root";

    public static final String DEFAULT_PASSWORD = "password";

    public static final String DEFAULT_DRIVERCLASSNAME = "";

    public static final String DEFAULT_DICTIONARY = "";

    public static final Integer DEFAULT_MAXTOTAL = 100;

    public static final Integer DEFAULT_MAXIDLE = 0;

    public static final Boolean DEFAULT_STATENABLE = true;

    public static final String DEFAULT_STATFILTER = "mergeStat";

    private static final Boolean DEFAULT_SLOWSQLENABLE = true;

    private static final Integer DEFAULT_SLOWSQLTHRESHOLD = 3000;

    private static final Boolean DEFAULT_LOGSTATENABLE = false;

    private static final Integer DEFAULT_LOGSTATINTERVAL = 180;

    public static final List<String> DEFAULT_INCLUDES = new ArrayList<>();

    public static final List<String> DEFAULT_EXCLUDES = new ArrayList<>();

    public static final String DEFAULT_LOGLEVEL = "ERROR";

    public static final String DEFAULT_TRANSACTIONISOLATION = "read-committed";

    public static final Boolean DEFAULT_TESTCONNECTIONONCHECKIN = false;

    public static final Boolean DEFAULT_TESTCONNECTIONONCHECKOUT = false;

    public static final Integer DEFAULT_MAXIDLETIME = 300;

    public static final Boolean DEFAULT_AUTOCOMMIT = false;
    // 20240428 빈 값으로 변경, 기본 SCHEMA를 취소하여 다양한 postgres 변형 버전에 수동으로 맞출 수 있게 함.
    // public static final String DEFAULT_SCHEMA = "X";
    public static final String DEFAULT_SCHEMA = "";

    public static ExternalDataSource defaultInstance() {

        ExternalDataSource o = new ExternalDataSource();
        o.enable = DEFAULT_ENABLE;
        o.url = DEFAULT_URL;
        o.username = DEFAULT_USERNAME;
        o.password = DEFAULT_PASSWORD;
        o.driverClassName = DEFAULT_DRIVERCLASSNAME;
        o.dictionary = DEFAULT_DICTIONARY;
        o.maxTotal = DEFAULT_MAXTOTAL;
        o.maxIdle = DEFAULT_MAXIDLE;
        o.statEnable = DEFAULT_STATENABLE;
        o.statFilter = DEFAULT_STATFILTER;
        o.slowSqlEnable = DEFAULT_SLOWSQLENABLE;
        o.slowSqlThreshold = DEFAULT_SLOWSQLTHRESHOLD;
        o.logStatEnable = DEFAULT_LOGSTATENABLE;
        o.logStatInterval = DEFAULT_LOGSTATINTERVAL;
        o.includes = DEFAULT_INCLUDES;
        o.excludes = DEFAULT_EXCLUDES;
        o.logLevel = DEFAULT_LOGLEVEL;
        o.transactionIsolation = DEFAULT_TRANSACTIONISOLATION;
        o.testConnectionOnCheckin = DEFAULT_TESTCONNECTIONONCHECKIN;
        o.testConnectionOnCheckout = DEFAULT_TESTCONNECTIONONCHECKOUT;
        o.maxIdleTime = DEFAULT_MAXIDLETIME;
        o.autoCommit = DEFAULT_AUTOCOMMIT;
        return o;
    }

    @FieldDescribe("사용 여부, 여러 데이터베이스를 사용하면 같은 유형이어야 하고 혼용할 수 없으며 사용자 이름과 비밀번호가 같아야 함.")
    private Boolean enable;
    @FieldDescribe("jdbc 연결 주소.")
    private String url;
    @FieldDescribe("데이터베이스 jdbc 연결 사용자 이름.")
    private String username;
    @FieldDescribe("데이터베이스 jdbc 연결 비밀번호.")
    private String password;
    @FieldDescribe("데이터베이스 드라이버 클래스 이름.")
    private String driverClassName;
    @FieldDescribe("방언 클래스.")
    private String dictionary;
    @FieldDescribe("최대 사용 연결 수.")
    private Integer maxTotal;
    @FieldDescribe("최대 유휴 연결 수.")
    private Integer maxIdle;
    @FieldDescribe("통계 사용, 기본값 사용.")
    private Boolean statEnable;
    @FieldDescribe("통계 방식 설정, 기본값 mergeStat.")
    private String statFilter;
    @FieldDescribe("이 데이터베이스에 저장할 클래스 설정, 기본적으로 모든 유형을 저장하며, 고성능을 위해 클래스별로 개별 제어가 필요하면 다른 클래스를 다른 노드에 저장하여 성능을 높일 수 있음. 와일드카드 * 사용 가능.")
    private List<String> includes;
    @FieldDescribe("이 노드에 저장하지 않을 클래스, includes와 함께 실제 저장 클래스를 설정, 와일드카드 * 사용 가능.")
    private List<String> excludes;
    @FieldDescribe("기본 로그 레벨, FATAL, ERROR, WARN, INFO, TRACE. 전체 설정은 DefaultLevel=WARN, Tool=TRACE, Enhance=TRACE, METADATA=TRACE, Runtime=TRACE, Query=TRACE, DataCache=TRACE, JDBC=TRACE, SQL=TRACE.")
    private String logLevel = DEFAULT_LOGLEVEL;
    @FieldDescribe("트랜잭션 격리 수준: default, none, read-uncommitted, read-committed, repeatable-read, serializable. 기본값 default(데이터베이스에 설정된 트랜잭션 수준).")
    private String transactionIsolation;
    @FieldDescribe("풀 진입 연결 테스트, 기본값 false.")
    private Boolean testConnectionOnCheckin;
    @FieldDescribe("풀 반출 연결 테스트, 기본값 false.")
    private Boolean testConnectionOnCheckout;
    @FieldDescribe("유휴 임계값, 기본값 300초.")
    private Integer maxIdleTime;
    @FieldDescribe("자동 커밋, 기본값 false.")
    private Boolean autoCommit = DEFAULT_AUTOCOMMIT;
    @FieldDescribe("스키마.")
    private String schema = DEFAULT_SCHEMA;
    @FieldDescribe("통계 로그 기록 사용.")
    private Boolean logStatEnable;
    @FieldDescribe("통계 로그 출력 간격, 단위 분, 기본값 180.")
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

    public Boolean getSlowSqlEnable() {
        return BooleanUtils.isTrue(this.slowSqlEnable);
    }

    public Integer getSlowSqlThreshold() {
        return (null == this.slowSqlThreshold || this.slowSqlThreshold < 1) ? DEFAULT_SLOWSQLTHRESHOLD
                : this.slowSqlThreshold;
    }

    public Boolean getAutoCommit() {
        return (null == this.autoCommit) ? DEFAULT_AUTOCOMMIT : this.autoCommit;
    }

    public Integer getMaxIdleTime() {
        return maxIdleTime == null ? DEFAULT_MAXIDLETIME : this.maxIdleTime;
    }

    public Boolean getTestConnectionOnCheckin() {
        return this.testConnectionOnCheckin == null ? DEFAULT_TESTCONNECTIONONCHECKIN : this.testConnectionOnCheckin;
    }

    public Boolean getTestConnectionOnCheckout() {
        return this.testConnectionOnCheckout == null ? DEFAULT_TESTCONNECTIONONCHECKOUT : this.testConnectionOnCheckout;
    }

    public String getTransactionIsolation() {
        return StringUtils.isEmpty(this.transactionIsolation) ? DEFAULT_TRANSACTIONISOLATION
                : this.transactionIsolation;
    }

    public String getLogLevel() {
        return StringUtils.isEmpty(this.logLevel) ? DEFAULT_LOGLEVEL : this.logLevel;
    }

    public String getDriverClassName() throws Exception {
        return StringUtils.isEmpty(this.driverClassName) ? SlicePropertiesBuilder.driverClassNameOfUrl(this.url)
                : this.driverClassName;
    }

    public String getDictionary() throws Exception {
        return StringUtils.isEmpty(this.dictionary) ? SlicePropertiesBuilder.dictionaryOfUrl(this.url)
                : this.dictionary;
    }

    public String getStatFilter() {
        return StringUtils.isEmpty(this.statFilter) ? DEFAULT_STATFILTER : this.statFilter;
    }

    public Boolean getStatEnable() {
        return null == statEnable ? DEFAULT_STATENABLE : this.statEnable;
    }

    public Integer getMaxIdle() {
        if ((this.maxIdle == null) || (this.maxIdle < 1)) {
            return DEFAULT_MAXIDLE;
        } else {
            return this.maxIdle;
        }
    }

    public Integer getMaxTotal() {
        if ((this.maxTotal == null) || (this.maxTotal < 0)) {
            return DEFAULT_MAXTOTAL;
        } else {
            return this.maxTotal;
        }
    }

    public Boolean getEnable() {
        return null == enable ? DEFAULT_ENABLE : this.enable;
    }

    public String getUrl() {
        return StringUtils.isEmpty(this.url) ? DEFAULT_URL : this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (StringUtils.isEmpty(this._password)) {
            this._password = Crypto.plainText(this.password);
        }
        return this._password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getIncludes() {
        return null == includes ? DEFAULT_INCLUDES : this.includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return null == excludes ? DEFAULT_EXCLUDES : this.excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getSchema() {
        return StringUtils.isBlank(this.schema) ? DEFAULT_SCHEMA : this.schema;
    }

}
