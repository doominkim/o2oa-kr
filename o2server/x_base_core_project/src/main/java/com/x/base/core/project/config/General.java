package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.BaseTools;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.Host;
import com.x.base.core.project.tools.NumberTools;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class General extends ConfigObject {

	private static final long serialVersionUID = 4393280516414081348L;
	private static final Boolean DEFAULT_WEBSOCKETENABLE = true;
	private static final Boolean DEFAULT_CONFIGAPIENABLE = true;
	private static final Set<String> DEFAULT_SCRIPTINGALLOWEDCLASSES = new HashSet<>(Arrays
			.asList(String.class.getName(), List.class.getName(), ArrayList.class.getName(), Arrays.class.getName()));
	private static final Set<String> DEFAULT_ACCESSDENYURIS = Set.of("/jaxrs/application.wadl");
	private static final Boolean DEFAULT_REQUESTLOGENABLE = false;
	private static final Integer DEFAULT_REQUESTLOGRETAINDAYS = 7;
	private static final Boolean DEFAULT_REQUESTLOGBODYENABLE = false;

	private static final Boolean DEFAULT_DEPLOYRESOURCEENABLE = false;
	private static final Boolean DEFAULT_DEPLOYWARENABLE = false;

	private static final Boolean DEFAULT_STATENABLE = false;
	private static final String DEFAULT_STATEXCLUSIONS = "*.js,*.gif,*.jpg,*.png,*.css,*.ico";
	private static final Boolean DEFAULT_EXPOSEJEST = false;
	private static final String DEFAULT_REFERERHEADCHECKREGULAR = "";
	private static final String DEFAULT_ACCESSCONTROLALLOWORIGIN = "";
	private static final String DEFAULT_IDFORMATCHECKREGULAR = "";
	public static final String DEFAULT_HTTP_WHITE = "127.0.0.1";
	private static final String ALL_HTTP_WHITE = "*";
	private static final List<String> DEFAULT_HTTPWHITELIST = List.of(DEFAULT_HTTP_WHITE);
	private static final List<String> DEFAULT_TRUSTPROXYIPLIST = List.of("127.0.0.1");
	private static final Integer DEFAULT_STORAGEENCRYPT = 0;
	private static final Integer DEFAULT_WEBSERVERCACHECONTROLMAXAGE = 0;
	private static final Map<String, String> DEFAULT_SUPPORTED_LANGUAGES = defaultSupportedLanguages();
	private static final Boolean DEFAULT_GRAALVMEVALASPROMISE = true;
	private static final Boolean DEFAULT_TABLEROWSELECTENABLE = true;

	// Insertion order is the order of the language selector (profile, system settings).
	private static Map<String, String> defaultSupportedLanguages() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("ko", "한국어");
		map.put("zh-CN", "중국어(간체)");
		map.put("en", "English");
		return Collections.unmodifiableMap(map);
	}

	public static General defaultInstance() {
		General o = new General();
		o.webSocketEnable = DEFAULT_WEBSOCKETENABLE;
		o.configApiEnable = DEFAULT_CONFIGAPIENABLE;
		o.scriptingAllowedClasses = DEFAULT_SCRIPTINGALLOWEDCLASSES;
		o.requestLogEnable = DEFAULT_REQUESTLOGENABLE;
		o.requestLogRetainDays = DEFAULT_REQUESTLOGRETAINDAYS;
		o.requestLogBodyEnable = DEFAULT_REQUESTLOGBODYENABLE;
		o.deployResourceEnable = DEFAULT_DEPLOYRESOURCEENABLE;
		o.deployWarEnable = DEFAULT_DEPLOYWARENABLE;
		o.statEnable = DEFAULT_STATENABLE;
		o.statExclusions = DEFAULT_STATEXCLUSIONS;
		o.exposeJest = DEFAULT_EXPOSEJEST;
		o.refererHeadCheckRegular = DEFAULT_REFERERHEADCHECKREGULAR;
		o.accessControlAllowOrigin = DEFAULT_ACCESSCONTROLALLOWORIGIN;
		o.idFormatCheckRegular = DEFAULT_IDFORMATCHECKREGULAR;
		o.httpWhiteList = DEFAULT_HTTPWHITELIST;
		o.attachmentConfig = new AttachmentConfig();
		o.storageEncrypt = DEFAULT_STORAGEENCRYPT;
		o.webServerCacheControlMaxAge = DEFAULT_WEBSERVERCACHECONTROLMAXAGE;
		o.supportedLanguages = DEFAULT_SUPPORTED_LANGUAGES;
		o.graalvmEvalAsPromise = DEFAULT_GRAALVMEVALASPROMISE;
		o.accessDenyUris = DEFAULT_ACCESSDENYURIS;
		o.disableExportEnable = false;
		o.trustProxyIpList = DEFAULT_TRUSTPROXYIPLIST;
		return o;
	}

	@FieldDescribe("접근 로그 기능 사용.")
	private Boolean requestLogEnable;

	@FieldDescribe("접근 로그 보관 일수, 기본값 7일.")
	private Integer requestLogRetainDays;

	@FieldDescribe("접근 로그에 post 또는 put의 body 내용 기록 여부, content-type이 application/json인 요청에만 유효.")
	private Boolean requestLogBodyEnable;

	@FieldDescribe("webSocket 연결 사용 여부.")
	private Boolean webSocketEnable;

	@FieldDescribe("인터페이스를 통한 시스템 설정 수정 허용.")
	private Boolean configApiEnable;

	@FieldDescribe("war 패키지 배포 허용 여부.")
	private Boolean deployWarEnable;

	@FieldDescribe("정적 리소스 배포 허용 여부.")
	private Boolean deployResourceEnable;

	@FieldDescribe("통계 사용, 기본값은 통계 사용.")
	private Boolean statEnable;

	@FieldDescribe("통계 제외 경로, 기본값은 *.js,*.gif,*.jpg,*.png,*.css,*.ico 제외")
	private String statExclusions;

	@FieldDescribe("jest 인터페이스 노출.")
	private Boolean exposeJest;

	@FieldDescribe("스크립트에서 로드 가능한 클래스(com.x.base 및 com.x.organization으로 시작하는 플랫폼 클래스는 기본 로드됨).")
	private Set<String> scriptingAllowedClasses;

	@FieldDescribe("http referer 검증 정규식, CSRF 공격 방어 검증에 사용 가능, 예시:(.+?)o2oa.net(.+?)")
	private String refererHeadCheckRegular = "";

	@FieldDescribe("교차 출처 리소스 공유 허용, http 응답의 Access-Control-Allow-Origin 헤더 설정, CORS 공격 방어에 사용 가능, 예시:https://www.o2oa.net")
	private String accessControlAllowOrigin = "";

	@FieldDescribe("콘텐츠 보안 정책(CSP)")
	private String contentSecurityPolicy = "";

	@FieldDescribe("첨부파일 업로드 크기 또는 유형 제한.")
	private AttachmentConfig attachmentConfig;

	@FieldDescribe("객체 id 형식 검증 정규식.")
	private String idFormatCheckRegular = "";

	@FieldDescribe("외부 http 인터페이스 서비스 주소 화이트리스트, *는 제한 없음.")
	private List<String> httpWhiteList;

	@FieldDescribe("저장 암호화.1:AES,2:AES/GCM/NoPadding, 비어 있거나 기타 값이면 암호화하지 않음.")
	private Integer storageEncrypt;

	@FieldDescribe("web 서버 Cache-Control max-age 헤더 설정, 0이면 Cache-Control을 설정하지 않음.")
	private Integer webServerCacheControlMaxAge;

	@FieldDescribe("다국어 설정, 기본으로 중국어와 영어 지원.")
	private Map<String, String> supportedLanguages;

	@FieldDescribe("graalvm 실행 스크립트를 promise로 래핑.")
	private Boolean graalvmEvalAsPromise;

	@FieldDescribe("접근 제한 uri 주소.")
	private Set<String> accessDenyUris;

	@FieldDescribe("접근 제한 uri 주소.")
	private Boolean disableExportEnable;

	@FieldDescribe("x_query_assemble_surface 모듈의 listRowSelect, rowCountWhere 인터페이스 사용 여부")
	private Boolean tableRowSelectEnable = DEFAULT_TABLEROWSELECTENABLE;

	@FieldDescribe("신뢰할 수 있는 프록시 서버 ip 목록, 예: o2server 접근 시 nginx로 전달하는 경우 nginx의 ip 입력.")
	private List<String> trustProxyIpList;

	public List<String> getTrustProxyIpList() {
		return trustProxyIpList;
	}

	public Boolean getDisableExportEnable() {
		return disableExportEnable != null && disableExportEnable;
	}

	public void setDisableExportEnable(Boolean disableExportEnable) {
		this.disableExportEnable = disableExportEnable;
	}

	public Boolean getTableRowSelectEnable() {
		return (null == this.tableRowSelectEnable) ? DEFAULT_TABLEROWSELECTENABLE : this.tableRowSelectEnable;
	}

	public Set<String> getAccessDenyUris() {
		return (null == this.accessDenyUris) ? DEFAULT_ACCESSDENYURIS : this.accessDenyUris;
	}

	public void setAccessDenyUris(Set<String> accessDenyUris) {
		this.accessDenyUris = accessDenyUris;
	}

	public Boolean getGraalvmEvalAsPromise() {
		return BooleanUtils.isNotFalse(this.graalvmEvalAsPromise);
	}

	public Integer getWebServerCacheControlMaxAge() {
		return NumberTools.nullOrLessThan(this.webServerCacheControlMaxAge, 0) ? DEFAULT_WEBSERVERCACHECONTROLMAXAGE
				: this.webServerCacheControlMaxAge;
	}

	public Integer getStorageEncrypt() {
		if (this.storageEncrypt == null || this.storageEncrypt < 0 || this.storageEncrypt > 2) {
			return DEFAULT_STORAGEENCRYPT;
		} else {
			return this.storageEncrypt;
		}
	}

	public String getIdFormatCheckRegular() {
		return this.idFormatCheckRegular;
	}

	public String getContentSecurityPolicy() {
		return contentSecurityPolicy;
	}

	public String getRefererHeadCheckRegular() {
		return (StringUtils.isBlank(refererHeadCheckRegular) ? DEFAULT_REFERERHEADCHECKREGULAR
				: this.refererHeadCheckRegular);
	}

	public String getAccessControlAllowOrigin() {
		return (StringUtils.isBlank(accessControlAllowOrigin) ? DEFAULT_ACCESSCONTROLALLOWORIGIN
				: this.accessControlAllowOrigin);
	}

	public Boolean getExposeJest() {
		return BooleanUtils.isNotFalse(this.exposeJest);
	}

	public String getStatExclusions() {
		return (StringUtils.isEmpty(statExclusions) ? DEFAULT_STATEXCLUSIONS : this.statExclusions) + ",/druid/*";
	}

	public Boolean getStatEnable() {
		return BooleanUtils.isNotFalse(statEnable);
	}

	public Boolean getRequestLogEnable() {
		return BooleanUtils.isTrue(this.requestLogEnable);
	}

	public Integer getRequestLogRetainDays() {
		return (null == this.requestLogRetainDays || this.requestLogRetainDays < 1) ? DEFAULT_REQUESTLOGRETAINDAYS
				: this.requestLogRetainDays;
	}

	public Boolean getRequestLogBodyEnable() {
		return BooleanUtils.isTrue(this.requestLogBodyEnable);
	}

	public Set<String> getScriptingAllowedClasses() {
		return (null == this.scriptingAllowedClasses) ? DEFAULT_SCRIPTINGALLOWEDCLASSES : this.scriptingAllowedClasses;
	}

	public Boolean getWebSocketEnable() {
		return null == this.webSocketEnable ? DEFAULT_WEBSOCKETENABLE : this.webSocketEnable;
	}

	public Boolean getConfigApiEnable() {
		return null == this.configApiEnable ? DEFAULT_CONFIGAPIENABLE : this.configApiEnable;
	}

	public Boolean getDeployWarEnable() {

		return null == this.deployWarEnable ? DEFAULT_DEPLOYWARENABLE : this.deployWarEnable;
	}

	public Boolean getDeployResourceEnable() {

		return null == this.deployResourceEnable ? DEFAULT_DEPLOYRESOURCEENABLE : this.deployResourceEnable;
	}

	public Map<String, String> getSupportedLanguages() {
		return supportedLanguages == null || supportedLanguages.isEmpty() ? DEFAULT_SUPPORTED_LANGUAGES
				: supportedLanguages;
	}

	public List<String> getHttpWhiteList() {
		Set<String> httpWhiteSet = httpWhiteList == null ? new HashSet<>(DEFAULT_HTTPWHITELIST)
				: new HashSet<>(httpWhiteList);
		if (httpWhiteSet.isEmpty() || httpWhiteSet.contains(ALL_HTTP_WHITE)) {
			return new ArrayList<>();
		}
		httpWhiteSet.add(Host.ROLLBACK_IPV4);
		httpWhiteSet.add(Collect.Default_server);
		httpWhiteSet.add(Collect.Default_appPackServerHost);
		try {
			Config.nodes().entrySet().stream().forEach(n -> httpWhiteSet.add(n.getKey()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>(httpWhiteSet);
	}

	public AttachmentConfig getAttachmentConfig() {
		return this.attachmentConfig == null ? new AttachmentConfig() : attachmentConfig;
	}

	public static class AttachmentConfig extends ConfigObject {

		private static final long serialVersionUID = -5672631798073576284L;

		public static AttachmentConfig defaultInstance() {
			return new AttachmentConfig();
		}

		public static final Integer DEFAULT_FILE_SIZE = 2047;

		@FieldDescribe("첨부파일 크기 제한(단위 M, 최대 2048M).")
		private Integer fileSize = DEFAULT_FILE_SIZE;

		@FieldDescribe("업로드 허용 파일 확장자")
		private List<String> fileTypeIncludes = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf",
				"hwp", "hwpx", "txt", "csv", "md", "xapp", "text", "zip", "rar", "7z", "mp3", "mp4", "png", "jpg",
				"jpeg", "gif", "webp");

		@FieldDescribe("업로드 금지 파일 확장자")
		private List<String> fileTypeExcludes = new ArrayList<>();

		public Integer getFileSize() {
			return fileSize;
		}

		public List<String> getFileTypeIncludes() {
			return fileTypeIncludes;
		}

		public List<String> getFileTypeExcludes() {
			return fileTypeExcludes;
		}

	}

	public void save() throws Exception {
		File file = new File(Config.base(), Config.PATH_CONFIG_GENERAL);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
		BaseTools.executeSyncFile(Config.PATH_CONFIG_GENERAL);
	}

}
