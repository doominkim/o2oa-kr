package com.x.program.center;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.program.center.schedule.AndFxSyncOrganization;
import com.x.program.center.schedule.Area;
import com.x.program.center.schedule.Cleanup;
import com.x.program.center.schedule.CleanupCode;
import com.x.program.center.schedule.CollectPerson;
import com.x.program.center.schedule.CreateActivityUnique;
import com.x.program.center.schedule.DingdingSyncOrganization;
import com.x.program.center.schedule.DingdingSyncOrganizationTrigger;
import com.x.program.center.schedule.FireSchedule;
import com.x.program.center.schedule.QiyeweixinSyncOrganization;
import com.x.program.center.schedule.QiyeweixinSyncOrganizationTrigger;
import com.x.program.center.schedule.TriggerAgent;
import com.x.program.center.schedule.WeLinkSyncOrganization;
import com.x.program.center.schedule.WeLinkSyncOrganizationTrigger;
import com.x.program.center.schedule.ZhengwuDingdingSyncOrganization;
import com.x.program.center.schedule.ZhengwuDingdingSyncOrganizationTrigger;

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

	public static Context context() {
		return context;
	}

	public static final CenterQueue centerQueue = new CenterQueue();

	public static final LogQueue logQueue = new LogQueue();

	public static final List<Object> dingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> weLinkSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> zhengwuDingdingSyncOrganizationCallbackRequest = new ArrayList<>();

	public static final List<Object> qiyeweixinSyncOrganizationCallbackRequest = new ArrayList<>();

	public static void init() {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			context().startQueue(centerQueue);
			context().startQueue(logQueue);

			// 정무 DingTalk 가져오기 동기화
			if (BooleanUtils.isTrue(Config.zhengwuDingding().getEnable())) {
				// 동기화 작업 시작
				context().scheduleLocal(ZhengwuDingdingSyncOrganization.class, Config.zhengwuDingding().getSyncCron());
				// 강제 동기화 작업 하나 추가
				context().scheduleLocal(ZhengwuDingdingSyncOrganizationTrigger.class,
						Config.zhengwuDingding().getForceSyncCron());
			}
			// WeCom 가져오기 동기화
			if (BooleanUtils.isTrue(Config.qiyeweixin().getEnable())
					&& StringUtils.isNotBlank(Config.qiyeweixin().getForceSyncCron())) {
				// 동기화 작업 시작
				context().scheduleLocal(QiyeweixinSyncOrganization.class, Config.qiyeweixin().getSyncCron());
				// 강제 동기화 작업 하나 추가
				context().scheduleLocal(QiyeweixinSyncOrganizationTrigger.class,
						Config.qiyeweixin().getForceSyncCron());
			}
			// DingTalk 동기화
			if (BooleanUtils.isTrue(Config.dingding().getEnable())
					&& StringUtils.isNotBlank(Config.dingding().getForceSyncCron())) {
				// 동기화 작업 시작
				context().scheduleLocal(DingdingSyncOrganization.class, Config.dingding().getSyncCron());
				// 강제 동기화 작업 하나 추가
				context().scheduleLocal(DingdingSyncOrganizationTrigger.class, Config.dingding().getForceSyncCron());
			}
			// WeLink 동기화
			if (BooleanUtils.isTrue(Config.weLink().getEnable())
					&& StringUtils.isNotBlank(Config.weLink().getForceSyncCron())) {
				// 동기화 작업 시작
				context().scheduleLocal(WeLinkSyncOrganization.class, Config.weLink().getSyncCron());
				// 강제 동기화 작업 하나 추가
				context().scheduleLocal(WeLinkSyncOrganizationTrigger.class, Config.weLink().getForceSyncCron());
			}
			// 모바일 오피스 동기화
			if (StringUtils.isNotBlank(Config.andFx().getForceSyncCron())) {
				context().scheduleLocal(AndFxSyncOrganization.class, Config.weLink().getForceSyncCron());
			}

			// 실행 간격 300초에서 120초로 단축
			context().scheduleLocal(FireSchedule.class, 180, 120);
			context().scheduleLocal(CleanupCode.class, 10, 60 * 30);
			context().scheduleLocal(Cleanup.class, 10, 60 * 30);
			context().scheduleLocal(CollectPerson.class, 10, 60 * 60 * 2);
			// 실행 간격 60초에서 30초로 단축
			context().scheduleLocal(TriggerAgent.class, 150, 30);
			/* 행정 구역 주 1회 업데이트 */
			context().scheduleLocal(Area.class, 300, 60 * 60 * 24 * 7);
			// 업그레이드 업데이트, activity의 unique 값 보충
			context.scheduleLocal(CreateActivityUnique.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			FORKJOINPOOL.shutdown();
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
