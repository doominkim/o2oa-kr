package com.x.program.center.jaxrs.apppack;

import com.google.gson.reflect.TypeToken;
import com.x.base.core.project.bean.NameValuePair;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.jaxrs.apppack.ActionConnectPackServer.AuthTokenData;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

	private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);



	private static String cachedPackServerToken;
	private static Date cachedPackServerTokenDate;

	/**
	 * 현재 sso 완료 후 token 조회
	 * @return 인증 성공 시 token, 아니면 null 반환
	 */
	public String getPackServerSSOToken() {
		if ((StringUtils.isNotEmpty(cachedPackServerToken) && (null != cachedPackServerTokenDate))
			&& (cachedPackServerTokenDate.after(new Date()))) {
			return cachedPackServerToken;
		}
		String token = login2AppPackServer();
		if (StringUtils.isNotEmpty(token)) {
			cachedPackServerToken = token;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR, 24); // token 24시간 캐시
			cachedPackServerTokenDate = cal.getTime();
			return cachedPackServerToken;
		}
		return null;
	}

	/**
	 * apppack 서버 로그인
	 * @return 인증 성공 시 token, 아니면 null 반환
	 */
	private String login2AppPackServer() {
		try {
			String url = Config.collect().appPackServerApi(Collect.ADDRESS_APPPACK_AUTH);
			Map<String, String> map = new HashMap<>();
			map.put("collectName", Config.collect().getName()); // o2 클라우드 계정
			map.put("password", Config.collect().getPassword()); // o2 클라우드 비밀번호
			String result = HttpConnection.postAsString(url, null, XGsonBuilder.instance().toJson(map));
			logger.info("패키징 서버 인증, 결과: " + result);
			Type type = new TypeToken<AppPackResult<AuthTokenData>>() {
			}.getType();
			AppPackResult<AuthTokenData> packResult = XGsonBuilder.instance().fromJson(result, type);
			if (StringUtils.isNotEmpty(packResult.getResult()) && packResult.getResult().equals(AppPackResult.result_success)) {
				return packResult.getData().getToken();
			} else {
				logger.info("패키징 서버 인증 실패");
			}
		} catch (Exception e) {
			logger.info("app 패키징 서버 로그인에 실패했습니다");
			logger.error(e);
		}
		return null;
	}



	protected PackInfoFromServer getPackInfo() {
		try {
			String collectNameEncode = URLEncoder.encode(Config.collect().getName(), DefaultCharset.name);
			String url = Config.collect().appPackServerApi(String.format(Collect.ADDRESS_APPPACK_INFO, collectNameEncode));
			logger.info("패키징 정보 요청, url:" + url);
			ArrayList<NameValuePair> heads = new ArrayList<>();
			heads.add(new NameValuePair("token", getPackServerSSOToken()));
			String result = HttpConnection.getAsString(url, heads);
			logger.info("패키징 정보 수신, 결과: " + result);
			Type type = new TypeToken<AppPackResult<PackInfoFromServer>>() {
			}.getType();
			AppPackResult<PackInfoFromServer> appPackResult = XGsonBuilder.instance().fromJson(result, type);
			if (appPackResult != null && StringUtils.isNotEmpty(appPackResult.getResult()) && appPackResult.getResult().equals(AppPackResult.result_success)) {
				return appPackResult.getData();
			}
		}catch (Exception e) {
			logger.error(e);
		}
		return null;
	}



	public class AppPackResult<T> extends GsonPropertyObject {
		public static final String result_failure = "failure";
		public static final String result_success = "success";

		private String result;
		private String message;
		private String time;
		protected T data;

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}
	}

	/**
	 * 패키징 정보 객체
	 */
	public static class PackInfoFromServer extends GsonPropertyObject {

		private String id;
		private String appName;
		// o2oa 서버 정보
		private String o2ServerProtocol; // 중앙 서버 프로토콜 http | https
		private String o2ServerHost; // 중앙 서버 주소  ip 또는 도메인
		private String o2ServerPort; // 중앙 서버 포트 포트 번호
		private String o2ServerContext; //  /x_program_center
		// collect 계정
		private String collectName;
		private String createTime;
		// 패키징 상태 0 준비(저장) 1 패키징 시작 2 패키징 종료 3 결과 피드백 완료
		private String packStatus;

		private String appLogoPath; // logo 이미지 주소 상대 경로
		private String apkPath; // apk 다운로드 주소 상대 경로

		// 버전 이름
		private String versionName;
		// 버전 번호 100
		private String buildNo;
		// 외부 패키지명 사용 여부, 2: net.zoneland.x.bpm.mobile.v1.zoneXBPM.outer 를 apk의 applicationId로 사용, 기본값은 기존 applicationId 사용(구버전 호환)
		private String isPackAppIdOuter;

		// 프록시 주소
		private String urlMapping;

		//오류 정보
		private String packErrorLogs;

		// 2 는 화웨이 푸시 패키지 삭제를 의미, 화웨이가 아닌 휴대폰에서 app 실행 시 HMS 서비스 프레임워크 설치가 필요하지 않도록 함
		private String deleteHuawei;


		private AppPackApkFile appFile; // 연결된 다운로드 파일


		public String getPackErrorLogs() {
			return packErrorLogs;
		}

		public void setPackErrorLogs(String packErrorLogs) {
			this.packErrorLogs = packErrorLogs;
		}

		public String getDeleteHuawei() {
			return deleteHuawei;
		}

		public void setDeleteHuawei(String deleteHuawei) {
			this.deleteHuawei = deleteHuawei;
		}

		public String getUrlMapping() {
			return urlMapping;
		}

		public void setUrlMapping(String urlMapping) {
			this.urlMapping = urlMapping;
		}

		public String getVersionName() {
			return versionName;
		}

		public void setVersionName(String versionName) {
			this.versionName = versionName;
		}

		public String getBuildNo() {
			return buildNo;
		}

		public void setBuildNo(String buildNo) {
			this.buildNo = buildNo;
		}

		public String getIsPackAppIdOuter() {
			return isPackAppIdOuter;
		}

		public void setIsPackAppIdOuter(String isPackAppIdOuter) {
			this.isPackAppIdOuter = isPackAppIdOuter;
		}

		public AppPackApkFile getAppFile() {
			return appFile;
		}

		public void setAppFile(AppPackApkFile appFile) {
			this.appFile = appFile;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getAppName() {
			return appName;
		}

		public void setAppName(String appName) {
			this.appName = appName;
		}


		public String getO2ServerProtocol() {
			return o2ServerProtocol;
		}

		public void setO2ServerProtocol(String o2ServerProtocol) {
			this.o2ServerProtocol = o2ServerProtocol;
		}

		public String getO2ServerHost() {
			return o2ServerHost;
		}

		public void setO2ServerHost(String o2ServerHost) {
			this.o2ServerHost = o2ServerHost;
		}

		public String getO2ServerPort() {
			return o2ServerPort;
		}

		public void setO2ServerPort(String o2ServerPort) {
			this.o2ServerPort = o2ServerPort;
		}

		public String getO2ServerContext() {
			return o2ServerContext;
		}

		public void setO2ServerContext(String o2ServerContext) {
			this.o2ServerContext = o2ServerContext;
		}

		public String getCollectName() {
			return collectName;
		}

		public void setCollectName(String collectName) {
			this.collectName = collectName;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public String getPackStatus() {
			return packStatus;
		}

		public void setPackStatus(String packStatus) {
			this.packStatus = packStatus;
		}

		public String getAppLogoPath() {
			return appLogoPath;
		}

		public void setAppLogoPath(String appLogoPath) {
			this.appLogoPath = appLogoPath;
		}

		public String getApkPath() {
			return apkPath;
		}

		public void setApkPath(String apkPath) {
			this.apkPath = apkPath;
		}
	}
}