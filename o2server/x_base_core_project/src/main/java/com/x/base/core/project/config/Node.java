package com.x.base.core.project.config;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.CronTools;
import com.x.base.core.project.tools.NumberTools;

public class Node extends ConfigObject {

	private static final long serialVersionUID = -6598734923326779693L;

	private static final Integer DEFAULT_NODEAGENTPORT = 20010;
	private static final String DEFAULT_BANNER = "O2OA";
	private static final Boolean DEFAULT_SELFHEALTHCHECKENABLE = false;
	// private static final Integer DEFAULT_ORDER = 0;

	public static Node defaultInstance() {
		Node o = new Node();
		o.enable = true;
		o.center = CenterServer.defaultInstance();
		o.application = ApplicationServer.defaultInstance();
		o.web = WebServer.defaultInstance();
		o.data = DataServer.defaultInstance();
		o.storage = StorageServer.defaultInstance();
		o.dumpData = new ScheduleDumpData();
		o.restoreData = new ScheduleRestoreData();
		o.nodeAgentEnable = true;
		o.nodeAgentEncrypt = true;
		o.nodeAgentPort = DEFAULT_NODEAGENTPORT;
		o.autoStart = true;
		o.selfHealthCheckEnable = DEFAULT_SELFHEALTHCHECKENABLE;
		// o.order = DEFAULT_ORDER;
		return o;
	}

	@FieldDescribe("사용 여부")
	private Boolean enable;
	@FieldDescribe("노드 순서, 노드 선출 순서 0,1,2...")
	private Integer order;
	@FieldDescribe("Center 서버 설정")
	private CenterServer center;
	@FieldDescribe("Application 서버 설정")
	private ApplicationServer application;
	@FieldDescribe("Web 서버 설정")
	private WebServer web;
	@FieldDescribe("Data 서버 설정")
	private DataServer data;
	@FieldDescribe("Storage 서버 설정")
	private StorageServer storage;
	@FieldDescribe("정기 데이터 내보내기 설정")
	private ScheduleDumpData dumpData;
	@FieldDescribe("정기 데이터 가져오기 설정")
	private ScheduleRestoreData restoreData;
	@FieldDescribe("정기 java stack trace 실행")
	private ScheduleStackTrace stackTrace;
	@FieldDescribe("노드 프록시 사용 여부")
	private Boolean nodeAgentEnable;
	@FieldDescribe("노드 포트 사용 여부")
	private Integer nodeAgentPort;
	@FieldDescribe("노드 프록시 암호화 사용 여부")
	private Boolean nodeAgentEncrypt;
	@FieldDescribe("서버 콘솔 시작 식별자")
	private String banner;
	@FieldDescribe("자동 시작 여부")
	private Boolean autoStart;
	@FieldDescribe("노드 모듈 상태 자체 점검 사용 여부, 사용 시 center에 제출하기 전에 모듈 상태 점검 수행. 기본값 false")
	private Boolean selfHealthCheckEnable;

//	public Integer getOrder() {
//		return order == null ? DEFAULT_ORDER : this.order;
//	}

	public Boolean getSelfHealthCheckEnable() {
		return BooleanUtils.isTrue(selfHealthCheckEnable);
	}

	protected void setCenter(CenterServer centerServer) {
		this.center = centerServer;
	}

	public Boolean autoStart() {
		return BooleanUtils.isNotFalse(autoStart);
	}

	public Boolean getEnable() {
		return BooleanUtils.isTrue(this.enable);
	}

	public CenterServer getCenter() {
		return (center == null) ? CenterServer.defaultInstance() : this.center;
	}

	public ApplicationServer getApplication() {
		return (application == null) ? ApplicationServer.defaultInstance() : this.application;
	}

	public WebServer getWeb() {
		return (web == null) ? WebServer.defaultInstance() : this.web;
	}

	public DataServer getData() {
		return (data == null) ? DataServer.defaultInstance() : this.data;
	}

	public StorageServer getStorage() {
		return (storage == null) ? StorageServer.defaultInstance() : this.storage;
	}

	public String getBanner() {
		return StringUtils.isBlank(this.banner) ? DEFAULT_BANNER : this.banner;
	}

	public Integer nodeAgentPort() {
		if (null == this.nodeAgentPort || this.nodeAgentPort < 0) {
			return DEFAULT_NODEAGENTPORT;
		}
		return this.nodeAgentPort;
	}

	public Boolean nodeAgentEnable() {
		return BooleanUtils.isTrue(this.nodeAgentEnable);
	}

	public Boolean nodeAgentEncrypt() {
		return BooleanUtils.isNotFalse(this.nodeAgentEncrypt);
	}

	public ScheduleDumpData dumpData() {
		return (dumpData == null) ? new ScheduleDumpData() : this.dumpData;
	}

	public ScheduleRestoreData restoreData() {
		return (restoreData == null) ? new ScheduleRestoreData() : this.restoreData;
	}

	public ScheduleStackTrace stackTrace() {
		return (stackTrace == null) ? new ScheduleStackTrace() : this.stackTrace;
	}

	public static class ScheduleDumpData extends ConfigObject {

		private static final long serialVersionUID = -3841277189943216415L;

		public static ScheduleDumpData defaultInstance() {
			return new ScheduleDumpData();
		}

		public boolean available() {
			return CronTools.available(this.cron());
		}

		@FieldDescribe("사용 여부, 기본값은 사용 안 함.")
		private Boolean enable = false;

		@FieldDescribe("정기 작업 cron 표현식, 기본값은 매일 새벽 2시에 백업.")
		private String cron = "";

		@FieldDescribe("최대 보관 개수, 초과 시 가장 오래된 데이터를 자동 삭제.")
		private Integer size = 7;

		@FieldDescribe("백업 경로")
		private String path = "";

		public Boolean enable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String cron() {
			return (null == cron) ? "5 0 2 * * ?" : this.cron;
		}

		public Integer size() {
			return (null == size) ? 14 : this.size;
		}

		public String path() {
			return StringUtils.trim(path);
		}

	}

	public static class ScheduleRestoreData extends ConfigObject {

		private static final long serialVersionUID = 3352945576815162805L;

		public static ScheduleRestoreData defaultInstance() {
			return new ScheduleRestoreData();
		}

		public boolean available() {
			return CronTools.available(this.cron) && StringUtils.isNotEmpty(this.path);
		}

		@FieldDescribe("사용 여부.")
		private Boolean enable = false;

		@FieldDescribe("정기 작업 cron 표현식")
		private String cron = "";

		@FieldDescribe("복원 경로")
		private String path = "";

		public Boolean enable() {
			return (BooleanUtils.isTrue(this.enable)) ? true : false;
		}

		public String cron() {
			return (null == cron) ? "" : this.cron;
		}

		public String path() {
			return StringUtils.trim(path);
		}

	}

	public static class ScheduleStackTrace extends ConfigObject {

		private static final long serialVersionUID = -7308289859339511471L;

		private static final Boolean DEFAULT_ENABLE = false;
		private static final Integer DEFAULT_INTERVAL = 20;

		public ScheduleStackTrace() {
			this.enable = DEFAULT_ENABLE;
			this.interval = DEFAULT_INTERVAL;
		}

		public static ScheduleStackTrace defaultInstance() {
			return new ScheduleStackTrace();
		}

		@FieldDescribe("사용 여부.")
		private Boolean enable = false;

		@FieldDescribe("jstack 실행 간격, 기본값 20초.")
		private Integer interval = DEFAULT_INTERVAL;

		public Boolean getEnable() {
			return (BooleanUtils.isTrue(this.enable));
		}

		public Integer getInterval() {
			return NumberTools.nullOrLessThan(interval, 0) ? DEFAULT_INTERVAL : this.interval;
		}

	}

}
