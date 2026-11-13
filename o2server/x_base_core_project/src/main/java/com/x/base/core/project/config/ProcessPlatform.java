package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.NumberTools;

/**
 * @author Zhou Rui
 */
public class ProcessPlatform extends ConfigObject {

	private static final long serialVersionUID = 8025969872758663591L;

	public static final Integer DEFAULT_FORMVERSIONPERIOD = 45;

	public static final Integer DEFAULT_PROCESSVERSIONPERIOD = 45;

	public static final Integer DEFAULT_SCRIPTVERSIONPERIOD = 45;

	public static final Integer DEFAULT_FORMVERSIONCOUNT = 30;

	public static final Integer DEFAULT_PROCESSVERSIONCOUNT = 30;

	public static final Integer DEFAULT_SCRIPTVERSIONCOUNT = 30;

	public static final String DEFAULT_DOCTOWORDTYPE = "local";

	public static final String DOCTOWORDTYPE_LOCAL = "local";

	public static final String DOCTOWORDTYPE_CLOUD = "cloud";

	public static final String DEFAULT_DOCTOWORDDEFAULTFILENAME = "본문.docx";

	public static final String DEFAULT_DOCTOWORDDEFAULTSITE = "$doc";

	public static final Integer DEFAULT_EXECUTORCOUNT = 3;

	public static final Integer DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD = 5;

	public static final Boolean DEFAULT_DELETEPROCESSINUSE = false;

	public static final Boolean DEFAULT_DELETEAPPLICATIONINUSE = false;

	public static final Boolean DEFAULT_PROCESSINGSIGNALPERSISTENABLE = false;

	public static final Integer DEFAULT_ASYNCHRONOUSTIMEOUT = 60;

	public static final Integer DEFAULT_PROCESSINGTIMEOUT = 15 * 60;

	public static ProcessPlatform defaultInstance() {
		return new ProcessPlatform();
	}

	public ProcessPlatform() {
		this.maintenanceIdentity = "";
		this.formVersionCount = DEFAULT_FORMVERSIONCOUNT;
		this.processVersionCount = DEFAULT_PROCESSVERSIONCOUNT;
		this.scriptVersionCount = DEFAULT_SCRIPTVERSIONCOUNT;
		this.docToWordType = DEFAULT_DOCTOWORDTYPE;
		this.docToWordDefaultFileName = DEFAULT_DOCTOWORDDEFAULTFILENAME;
		this.docToWordDefaultSite = DEFAULT_DOCTOWORDDEFAULTSITE;
		this.executorCount = DEFAULT_EXECUTORCOUNT;
		this.executorQueueBusyThreshold = DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD;
		this.urge = new Urge();
		this.expire = new Expire();
		this.touchDelay = new TouchDelay();
		this.merge = new Merge();
		this.mergeItem = new MergeItem();
		this.touchDetained = new TouchDetained();
		this.deleteDraft = new DeleteDraft();
		this.passExpired = new PassExpired();
		this.updateTable = new UpdateTable();
		this.handoverConfig = new HandoverConfig();
		this.processingSignalPersistEnable = DEFAULT_PROCESSINGSIGNALPERSISTENABLE;
		this.asynchronousTimeout = DEFAULT_ASYNCHRONOUSTIMEOUT;
		this.processingTimeout = DEFAULT_PROCESSINGTIMEOUT;
	}

	public Integer getExecutorCount() {
		return ((null == executorCount) || (executorCount < 0)) ? DEFAULT_EXECUTORCOUNT : this.executorCount;
	}

	public Integer getExecutorQueueBusyThreshold() {
		return ((null == executorQueueBusyThreshold) || (executorQueueBusyThreshold < 1))
				? DEFAULT_EXECUTORQUEUEBUSYTHRESHOLD
				: this.executorQueueBusyThreshold;
	}

	public Integer getFormVersionCount() {
		return formVersionCount == null ? DEFAULT_FORMVERSIONCOUNT : this.formVersionCount;
	}

	public Integer getProcessVersionCount() {
		return processVersionCount == null ? DEFAULT_PROCESSVERSIONCOUNT : this.processVersionCount;
	}

	public Integer getScriptVersionCount() {
		return scriptVersionCount == null ? DEFAULT_SCRIPTVERSIONCOUNT : this.scriptVersionCount;
	}

	public String getDocToWordType() {
		return StringUtils.isEmpty(docToWordType) ? DEFAULT_DOCTOWORDTYPE : docToWordType;
	}

	public String getDocToWordDefaultFileName() {
		return StringUtils.isEmpty(docToWordDefaultFileName) ? DEFAULT_DOCTOWORDDEFAULTFILENAME
				: docToWordDefaultFileName;
	}

	public String getDocToWordDefaultSite() {
		return StringUtils.isEmpty(docToWordDefaultSite) ? DEFAULT_DOCTOWORDDEFAULTSITE : docToWordDefaultSite;
	}

	@FieldDescribe("유지보수 신분, 업무 문서에 예기치 않은 오류가 발생해 처리자를 찾을 수 없는 경우 먼저 생성 신분에 업무 문서를 할당하고, 생성 신분도 조회할 수 없으면 지정 사용자에게 할당, 기본값은 비어 있음.")
	private String maintenanceIdentity;

	@FieldDescribe("양식 이력 버전 보관 개수, 0은 보관 안 함.")
	private Integer formVersionCount;

	@FieldDescribe("프로세스 이력 버전 보관 개수, 0은 보관 안 함.")
	private Integer processVersionCount;

	@FieldDescribe("스크립트 이력 버전 보관 개수, 0은 보관 안 함.")
	private Integer scriptVersionCount;

	@FieldDescribe("HTML 서식 공문을 Word 파일로 변환하는 방식, local, cloud.")
	private String docToWordType;

	@FieldDescribe("HTML 서식 공문을 Word 파일로 변환 시 기본 파일명.")
	private String docToWordDefaultFileName;

	@FieldDescribe("HTML 서식 공문을 Word 파일로 변환 시 기본 site.")
	private String docToWordDefaultSite;

	@FieldDescribe("실행기 개수")
	private Integer executorCount;

	@FieldDescribe("실행기 대기열 혼잡 임계값")
	private Integer executorQueueBusyThreshold;

	@FieldDescribe("독촉 작업 설정, 만료 임박 발견 시 알림 메시지 발송.")
	private Urge urge;

	@FieldDescribe("마감 시간이 지난 결재 대기를 만료로 표시.")
	private Expire expire;

	@FieldDescribe("지연 작업 설정, 지연 작업을 정기적으로 실행해 지연 시간이 지나면 계속 진행.")
	private TouchDelay touchDelay;

	@FieldDescribe("병합 작업 설정, 병합 작업을 정기적으로 실행해 완료된 업무 문서의 Data를 Item 테이블에서 추출하여 WorkCompleted의 Data 필드로 병합, 기본값은 업무 문서 완료 2년 후 병합 시작.")
	private Merge merge;

	@FieldDescribe("완료된 업무 문서의 Data를 Item 테이블에서 추출하여 WorkCompleted의 Data 필드로 병합.")
	private MergeItem mergeItem;

	@FieldDescribe("초안 상태의 업무 문서 삭제.")
	private DeleteDraft deleteDraft;

	@FieldDescribe("시간 초과 업무 문서 라우팅 설정.")
	private PassExpired passExpired;

	@FieldDescribe("장시간 미처리 업무 문서 트리거.")
	private TouchDetained touchDetained;

	@FieldDescribe("장기 체류 업무 문서, 결재 대기, 열람 대기 기록 설정.")
	private LogLongDetained logLongDetained;

	@FieldDescribe("알림 설정, 알림 간격 설정.")
	private Press press;

	@FieldDescribe("사용자 정의 테이블 동기화 설정.")
	private UpdateTable updateTable;

	@FieldDescribe("권한 인계 정기 실행 설정.")
	private HandoverConfig handoverConfig;

	@FieldDescribe("이벤트 확장.")
	private ExtensionEvents extensionEvents;

	@FieldDescribe("업무 문서 처리 신호 내용 저장 여부, 기본값 false.")
	private Boolean processingSignalPersistEnable;

	@FieldDescribe("비동기 시간 초과.")
	private Integer asynchronousTimeout;

	public Integer getAsynchronousTimeout() {
		if ((asynchronousTimeout == null) || (asynchronousTimeout < 1)) {
			this.asynchronousTimeout = DEFAULT_ASYNCHRONOUSTIMEOUT;
		}
		return asynchronousTimeout;
	}

	@FieldDescribe("진행 시간 초과")
	private Integer processingTimeout;

	public Integer getProcessingTimeout() {
		if ((processingTimeout == null) || (processingTimeout < 1)) {
			this.processingTimeout = DEFAULT_PROCESSINGTIMEOUT;
		}
		return processingTimeout;
	}

	public Boolean getProcessingSignalPersistEnable() {
		if (processingSignalPersistEnable == null) {
			this.processingSignalPersistEnable = DEFAULT_PROCESSINGSIGNALPERSISTENABLE;
		}
		return processingSignalPersistEnable;
	}

	public ExtensionEvents getExtensionEvents() {
		if (null == extensionEvents) {
			this.extensionEvents = new ExtensionEvents();
		}
		return extensionEvents;
	}

	public Urge getUrge() {
		return this.urge == null ? new Urge() : this.urge;
	}

	public Expire getExpire() {
		return this.expire == null ? new Expire() : this.expire;
	}

	public UpdateTable getUpdateTable() {
		return this.updateTable == null ? new UpdateTable() : this.updateTable;
	}

	public HandoverConfig getHandoverConfig() {
		return this.handoverConfig == null ? new HandoverConfig() : this.handoverConfig;
	}

	public PassExpired getPassExpired() {
		return this.passExpired == null ? new PassExpired() : this.passExpired;
	}

	public TouchDelay getTouchDelay() {
		return this.touchDelay == null ? new TouchDelay() : this.touchDelay;
	}

	public TouchDetained getTouchDetained() {
		return this.touchDetained == null ? new TouchDetained() : this.touchDetained;
	}

	public DeleteDraft getDeleteDraft() {
		return this.deleteDraft == null ? new DeleteDraft() : this.deleteDraft;
	}

	public LogLongDetained getLogLongDetained() {
		return this.logLongDetained == null ? new LogLongDetained() : this.logLongDetained;
	}

	@Deprecated
	public Merge getMerge() {
		return this.merge == null ? new Merge() : this.merge;
	}

	public MergeItem getMergeItem() {
		return this.mergeItem == null ? new MergeItem() : this.mergeItem;
	}

	public Press getPress() {
		return this.press == null ? new Press() : this.press;
	}

	public String getMaintenanceIdentity() {
		return maintenanceIdentity;
	}

	public void save() throws IOException {
		File file = new File(Config.base(), Config.PATH_CONFIG_PROCESSPLATFORM);
		FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
	}

	public static class Urge extends ConfigObject {

		private static final long serialVersionUID = 1159238658106337292L;

		public static Urge defaultInstance() {
			return new Urge();
		}

		public static final Boolean DEFAULT_ENABLE = false;

		public static final String DEFAULT_CRON = "30 0/10 8-18 * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class Expire extends ConfigObject {

		private static final long serialVersionUID = -8659190297973094979L;

		public static Expire defaultInstance() {
			return new Expire();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "45 0/15 8-18 * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class TouchDelay extends ConfigObject {

		private static final long serialVersionUID = 8716159849643530909L;

		public static TouchDelay defaultInstance() {
			return new TouchDelay();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "5 0/5 * * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	@Deprecated
	public static class Merge extends ConfigObject {

		private static final long serialVersionUID = -5858277850858377338L;

		public static Merge defaultInstance() {
			return new Merge();
		}

		public Merge() {
			this.enable = DEFAULT_ENABLE;
			this.cron = DEFAULT_CRON;
			this.thresholdDays = DEFAULT_THRESHOLDDAYS;
			this.batchSize = DEFAULT_BATCHSIZE;
		}

		public static final Boolean DEFAULT_ENABLE = false;

		public static final String DEFAULT_CRON = "30 30 6 * * ?";

		public static final Integer DEFAULT_THRESHOLDDAYS = 365 * 2;

		public static final Integer DEFAULT_BATCHSIZE = 100;

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("기한, 완료된 업무 문서 종료 후 지정 시간 간격으로 merge 수행, 기본값은 2년 후 merge")
		private Integer thresholdDays = DEFAULT_THRESHOLDDAYS;

		@FieldDescribe("배치 크기.")
		private Integer batchSize = DEFAULT_BATCHSIZE;

		public Integer getBatchSize() {
			return NumberTools.nullOrLessThan(this.batchSize, 1) ? DEFAULT_BATCHSIZE : this.batchSize;
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public Integer getThresholdDays() {
			return (null == thresholdDays || thresholdDays < 0) ? DEFAULT_THRESHOLDDAYS : thresholdDays;
		}

	}

	public static class MergeItem extends ConfigObject {

		private static final long serialVersionUID = -961812513267653913L;

		public static MergeItem defaultInstance() {
			return new MergeItem();
		}

		public MergeItem() {
			this.enable = DEFAULT_ENABLE;
			this.cron = DEFAULT_CRON;
		}

		public static final Boolean DEFAULT_ENABLE = false;

		public static final String DEFAULT_CRON = "30 */10 * * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class TouchDetained extends ConfigObject {

		private static final long serialVersionUID = -1557669565639237145L;

		public static TouchDetained defaultInstance() {
			return new TouchDetained();
		}

		public static final String DEFAULT_CRON = "30 30 12 * * ?";

		public static final Boolean DEFAULT_ENABLE = false;

		public static final Integer DEFAULT_THRESHOLDMINUTES = 60 * 24;

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("업무 문서가 설정 시간 이상 체류하면 업무 문서 진행을 시도, 사용자 변동으로 인한 업무 문서 체류를 자동 처리 가능, 기본값 24*60분.")
		private Integer thresholdMinutes = DEFAULT_THRESHOLDMINUTES;

		public Integer getThresholdMinutes() {
			return (null == thresholdMinutes || thresholdMinutes < 0) ? DEFAULT_THRESHOLDMINUTES : thresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class DeleteDraft extends ConfigObject {

		private static final long serialVersionUID = -2127401618190754038L;

		public static DeleteDraft defaultInstance() {
			return new DeleteDraft();
		}

		public static final String DEFAULT_CRON = "0 0 20 * * ?";

		public static final Boolean DEFAULT_ENABLE = false;

		public static final Integer DEFAULT_THRESHOLDMINUTES = 60 * 24 * 10;

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("임계값 설정, 이 시간을 초과하면 삭제 가능한 초안으로 간주, 기본값 10일.")
		private Integer thresholdMinutes = DEFAULT_THRESHOLDMINUTES;

		public Integer getThresholdMinutes() {
			return (null == thresholdMinutes || thresholdMinutes < 0) ? DEFAULT_THRESHOLDMINUTES : thresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class PassExpired extends ConfigObject {

		private static final long serialVersionUID = 2660210790582417811L;

		public static PassExpired defaultInstance() {
			return new PassExpired();
		}

		public static final String DEFAULT_CRON = "5 5 8-18 * * ?";

		public static final Boolean DEFAULT_ENABLE = true;

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}
	}

	public static class LogLongDetained extends ConfigObject {

		private static final long serialVersionUID = 6183346578358650613L;

		public static LogLongDetained defaultInstance() {
			return new LogLongDetained();
		}

		public static final String DEFAULT_CRON = "0 0 4 * * ?";

		public static final Boolean DEFAULT_ENABLE = true;

		public static final Integer DEFAULT_TASKTHRESHOLDMINUTES = 60 * 24 * 10;

		public static final Integer DEFAULT_READTHRESHOLDMINUTES = 60 * 24 * 10;

		public static final Integer DEFAULT_WORKTHRESHOLDMINUTES = 60 * 24 * 10;

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식")
		private String cron = DEFAULT_CRON;

		@FieldDescribe("결재 대기 체류 임계값 설정,.")
		private Integer taskThresholdMinutes = DEFAULT_TASKTHRESHOLDMINUTES;

		@FieldDescribe("열람 대기 체류 임계값 설정,.")
		private Integer readThresholdMinutes = DEFAULT_READTHRESHOLDMINUTES;

		@FieldDescribe("업무 문서 체류 임계값 설정,.")
		private Integer workThresholdMinutes = DEFAULT_WORKTHRESHOLDMINUTES;

		public Integer getTaskThresholdMinutes() {
			return (null == taskThresholdMinutes || taskThresholdMinutes < 0) ? DEFAULT_TASKTHRESHOLDMINUTES
					: taskThresholdMinutes;
		}

		public Integer getReadThresholdMinutes() {
			return (null == readThresholdMinutes || readThresholdMinutes < 0) ? DEFAULT_READTHRESHOLDMINUTES
					: readThresholdMinutes;
		}

		public Integer getWorkThresholdMinutes() {
			return (null == workThresholdMinutes || workThresholdMinutes < 0) ? DEFAULT_WORKTHRESHOLDMINUTES
					: workThresholdMinutes;
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

	}

	public static class Press extends ConfigObject {

		private static final long serialVersionUID = -4223137429803117953L;

		public static Press defaultInstance() {
			return new Press();
		}

		public static final Integer DEFAULT_INTERVALMINUTES = 10;

		public static final Integer DEFAULT_COUNT = 3;

		@FieldDescribe("알림 간격(분)")
		private Integer intervalMinutes = DEFAULT_INTERVALMINUTES;

		@FieldDescribe("알림 개수 제한.")
		private Integer count = DEFAULT_COUNT;

		public Integer getIntervalMinutes() {
			return (intervalMinutes == null || intervalMinutes < 1) ? DEFAULT_INTERVALMINUTES : this.intervalMinutes;
		}

		public Integer getCount() {
			return (count == null || count < 0) ? DEFAULT_COUNT : this.count;
		}

	}

	public static class ExtensionEvents {

		@FieldDescribe("업무 문서 첨부파일 업로드.")
		private WorkExtensionEvents workAttachmentUploadEvents = new WorkExtensionEvents();
		@FieldDescribe("업무 문서 첨부파일 다운로드.")
		private WorkExtensionEvents workAttachmentDownloadEvents = new WorkExtensionEvents();
		@FieldDescribe("업무 문서 서식 파일을 word로 변환.")
		private WorkExtensionEvents workDocToWordEvents = new WorkExtensionEvents();
		@FieldDescribe("업무 문서 서식 파일을 OFD로 변환.")
		private WorkExtensionEvents workDocToOfdEvents = new WorkExtensionEvents();
		@FieldDescribe("완료된 업무 문서 첨부파일 업로드.")
		private WorkCompletedExtensionEvents workCompletedAttachmentUploadEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("완료된 업무 문서 첨부파일 다운로드.")
		private WorkCompletedExtensionEvents workCompletedAttachmentDownloadEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("완료된 업무 문서 서식 파일을 word로 변환.")
		private WorkCompletedExtensionEvents workCompletedDocToWordEvents = new WorkCompletedExtensionEvents();
		@FieldDescribe("완료된 업무 문서 서식 파일을 OFD로 변환.")
		private WorkCompletedExtensionEvents workCompletedDocToOfdEvents = new WorkCompletedExtensionEvents();

		public WorkExtensionEvents getWorkAttachmentUploadEvents() {
			if (null == this.workAttachmentUploadEvents) {
				this.workAttachmentUploadEvents = new WorkExtensionEvents();
			}
			return workAttachmentUploadEvents;
		}

		public WorkExtensionEvents getWorkAttachmentDownloadEvents() {
			if (null == this.workAttachmentDownloadEvents) {
				this.workAttachmentDownloadEvents = new WorkExtensionEvents();
			}
			return workAttachmentDownloadEvents;
		}

		public WorkExtensionEvents getWorkDocToWordEvents() {
			if (null == this.workDocToWordEvents) {
				this.workDocToWordEvents = new WorkExtensionEvents();
			}
			return workDocToWordEvents;
		}

		public WorkExtensionEvents getWorkDocToOfdEvents() {
			if (null == this.workDocToOfdEvents) {
				this.workDocToOfdEvents = new WorkExtensionEvents();
			}
			return workDocToOfdEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedAttachmentUploadEvents() {
			if (null == this.workCompletedAttachmentUploadEvents) {
				this.workCompletedAttachmentUploadEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedAttachmentUploadEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedAttachmentDownloadEvents() {
			if (null == this.workCompletedAttachmentDownloadEvents) {
				this.workCompletedAttachmentDownloadEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedAttachmentDownloadEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedDocToWordEvents() {
			if (null == this.workCompletedDocToWordEvents) {
				this.workCompletedDocToWordEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedDocToWordEvents;
		}

		public WorkCompletedExtensionEvents getWorkCompletedDocToOfdEvents() {
			if (null == this.workCompletedDocToOfdEvents) {
				this.workCompletedDocToOfdEvents = new WorkCompletedExtensionEvents();
			}
			return workCompletedDocToOfdEvents;
		}

	}

	public static class WorkExtensionEvents extends ArrayList<WorkExtensionEvent> {

		private static final long serialVersionUID = -2847222465064494098L;

		public Optional<WorkExtensionEvent> bind(String application, String process, String activity) {
			return this.stream().filter(o -> BooleanUtils.isTrue(o.getEnable()))
					.filter(o -> (ListTools.contains(o.getApplications(), application)
							&& ListTools.contains(o.getProcesses(), process)
							&& ListTools.contains(o.getActivities(), activity))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.contains(o.getProcesses(), process)
									&& ListTools.isEmpty(o.getActivities()))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.isEmpty(o.getProcesses()) && ListTools.isEmpty(o.getActivities()))
							|| (ListTools.isEmpty(o.getApplications()) && ListTools.isEmpty(o.getProcesses())
									&& ListTools.isEmpty(o.getActivities())))
					.sorted((x, y) -> {
						if (x.getActivities().contains(activity)) {
							return 1;
						} else if (y.getActivities().contains(activity)) {
							return -1;
						} else if (x.getProcesses().contains(process)) {
							return 1;
						} else if (y.getProcesses().contains(process)) {
							return -1;
						} else if (x.getApplications().contains(application)) {
							return 1;
						} else if (y.getApplications().contains(application)) {
							return -1;
						} else {
							return 0;
						}
					}).findFirst();
		}

	}

	public static class WorkExtensionEvent {

		private Boolean enable;

		private List<String> applications;
		private List<String> processes;
		private List<String> activities;

		private String url;

		private String custom;

		public Boolean getEnable() {
			return enable;
		}

		public List<String> getApplications() {
			return applications;
		}

		public List<String> getProcesses() {
			return processes;
		}

		public List<String> getActivities() {
			return activities;
		}

		public String getUrl() {
			return url;
		}

		public String getCustom() {
			return custom;
		}

	}

	/**
	 *
	 * 조회 우선순위 프로세스-> 앱 -> 기본 설정(프로세스와 앱이 비어 있음)
	 *
	 */
	public static class WorkCompletedExtensionEvents extends ArrayList<WorkCompletedExtensionEvent> {

		private static final long serialVersionUID = -5527994436451255023L;

		public Optional<WorkCompletedExtensionEvent> bind(String application, String process) {
			return this.stream().filter(o -> BooleanUtils.isTrue(o.getEnable()))
					.filter(o -> (ListTools.contains(o.getApplications(), application)
							&& ListTools.contains(o.getProcesses(), process))
							|| (ListTools.contains(o.getApplications(), application)
									&& ListTools.isEmpty(o.getProcesses()))
							|| (ListTools.isEmpty(o.getApplications()) && ListTools.isEmpty(o.getProcesses())))
					.sorted((x, y) -> {
						if (x.getProcesses().contains(process)) {
							return 1;
						} else if (y.getProcesses().contains(process)) {
							return -1;
						} else if (x.getApplications().contains(application)) {
							return 1;
						} else if (y.getApplications().contains(application)) {
							return -1;
						} else {
							return 0;
						}
					}).findFirst();
		}
	}

	public static class WorkCompletedExtensionEvent {

		private Boolean enable;
		private List<String> applications;
		private List<String> processes;
		private String url;
		private String custom;

		public Boolean getEnable() {
			return enable;
		}

		public List<String> getApplications() {
			return applications;
		}

		public List<String> getProcesses() {
			return processes;
		}

		public String getUrl() {
			return url;
		}

		public String getCustom() {
			return custom;
		}

	}

	public static class UpdateTable extends ConfigObject {

		private static final long serialVersionUID = -7066262450518673067L;

		public static UpdateTable defaultInstance() {
			return new UpdateTable();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "20 20 * * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}

	public static class HandoverConfig extends ConfigObject {

		private static final long serialVersionUID = -5710800319348361625L;

		public static HandoverConfig defaultInstance() {
			return new HandoverConfig();
		}

		public static final Boolean DEFAULT_ENABLE = true;

		public static final String DEFAULT_CRON = "0 0/5 * * * ?";

		@FieldDescribe("사용 여부")
		private Boolean enable = DEFAULT_ENABLE;

		@FieldDescribe("정기 실행 cron 표현식.")
		private String cron = DEFAULT_CRON;

		public String getCron() {
			if (StringUtils.isNotEmpty(this.cron) && CronExpression.isValidExpression(this.cron)) {
				return this.cron;
			} else {
				return DEFAULT_CRON;
			}
		}

		public Boolean getEnable() {
			return BooleanUtils.isTrue(this.enable);
		}

	}
}
