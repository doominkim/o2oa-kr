package com.x.cms.assemble.control;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ForkJoinPool;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.message.MessageConnector;
import com.x.cms.assemble.control.queue.*;
import com.x.cms.assemble.control.timertask.PublishWaitDocumentTask;
import com.x.cms.assemble.control.timertask.Timertask_BatchOperationTask;
import com.x.cms.assemble.control.timertask.Timertask_InitOperationRunning;
import com.x.cms.assemble.control.timertask.Timertask_LogRecordCheckTask;
import com.x.cms.assemble.control.timertask.Timertask_RefreshAllDocumentReviews;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	private static final ForkJoinPool FORKJOINPOOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
			new ApplicationForkJoinWorkerThreadFactory(ThisApplication.class.getPackage()), null, false);

	public static ForkJoinPool forkJoinPool() {
		return FORKJOINPOOL;
	}

	protected static Context context;

	public static final String ROLE_CMSManager = "CMSManager@CMSManagerSystemRole@R";
	public static final String ROLE_Manager = "Manager@ManagerSystemRole@R";
	// Document 삭제 시 핫 이미지의 데이터도 삭제되었는지 확인 필요
	public static final QueueDocumentDelete queueDocumentDelete = new QueueDocumentDelete();
	// Document 제목 변경 시 인기 이미지의 데이터도 함께 업데이트해야 함
	public static final QueueDocumentUpdate queueDocumentUpdate = new QueueDocumentUpdate();
	// Document 조회 시 총 조회수를 item의 document에 반영해야 함(뷰에서 사용), 큐에서 비동기로 수정
	public static final QueueDocumentViewCountUpdate queueDocumentViewCountUpdate = new QueueDocumentViewCountUpdate();
	// 데이터베이스 일괄 처리 작업 실행
	public static final QueueBatchOperation queueBatchOperation = new QueueBatchOperation();
	// 문서 권한 일괄 처리 작업 실행
	public static final QueueDocumentOperation queueDocumentOperation = new QueueDocumentOperation();
	// Document 발행 시 모든 열람자에게 알림 발송
	public static final QueueSendDocumentNotify queueSendDocumentNotify = new QueueSendDocumentNotify();
	// 문서 데이터 매핑 실행
	public static final ProjectionExecuteQueue projectionExecuteQueue = new ProjectionExecuteQueue();

	public static final FormVersionQueue formVersionQueue = new FormVersionQueue();
	public static final ScriptVersionQueue scriptVersionQueue = new ScriptVersionQueue();

	private static final ConcurrentHashMap<String, DataImportStatus> importStatus = new ConcurrentHashMap<>();

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		CacheManager.init(context.clazz().getSimpleName());
		MessageConnector.start(context());
		context().startQueue(queueBatchOperation);
		context().startQueue(queueDocumentOperation);
		context().startQueue(queueDocumentDelete);
		context().startQueue(queueDocumentUpdate);
		context().startQueue(queueDocumentViewCountUpdate);
		context().startQueue(queueSendDocumentNotify);
		context().startQueue(projectionExecuteQueue);
		context().startQueue(formVersionQueue);
		context().startQueue(scriptVersionQueue);
		// 매일 새벽 2시에 한 번 실행
		context.schedule(Timertask_LogRecordCheckTask.class, "0 0 2 * * ?");

		context.schedule(Timertask_BatchOperationTask.class, "0 */5 * * * ?");

		/**
		 * 5분마다 발행 대기 문서 발행
		 */
		context.schedule(PublishWaitDocumentTask.class, "0 */5 * * * ?");

		// 매일 새벽 1시, 모든 문서의 권한 정보 계산
		context.schedule(Timertask_RefreshAllDocumentReviews.class, "0 0 1 * * ?");
		// 클러스터에서는 여러 대가 함께 실행되므로 Timertask_BatchOperationTask만 실행하면 됨
		context.scheduleLocal(Timertask_InitOperationRunning.class, 150);
	}

	public static void destroy() {
		try {
			FORKJOINPOOL.shutdown();
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ConcurrentMap<String, DataImportStatus> listImportStatus() {
		return importStatus;
	}

	public static DataImportStatus getDataImportStatus(String batchName) {
		if (importStatus.get(batchName) == null) {
			DataImportStatus dataImportStatus = new DataImportStatus();
			dataImportStatus.setBatchName(batchName);
			importStatus.put(batchName, dataImportStatus);
		}
		return importStatus.get(batchName);
	}
}
