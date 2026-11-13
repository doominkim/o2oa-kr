package com.x.attendance.assemble.control;

import com.x.base.core.project.ApplicationForkJoinWorkerThreadFactory;
import com.x.base.core.project.message.MessageConnector;
import java.util.List;

import java.util.concurrent.ForkJoinPool;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.schedule.v2.AttendanceV2DetailGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2HolidaySyncTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2LeaveLedgerExpireTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2LeavePolicyGrantTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2MessageSendTask;
import com.x.attendance.assemble.control.schedule.v2.AttendanceV2TodayMessageDataGenerateTask;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2Detail;
import com.x.attendance.assemble.control.schedule.v2.QueueAttendanceV2LeavePolicyGrant;
import com.x.attendance.assemble.control.service.v2.AttendanceV2LeaveManagerService;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ThisApplication {


  private static final Logger LOGGER = LoggerFactory.getLogger(ThisApplication.class);


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
 
    public static final String ROLE_AttendanceManager = "AttendanceManager@AttendanceManagerSystemRole@R";

    // V2
    public static final QueueAttendanceV2Detail queueV2Detail = new QueueAttendanceV2Detail();
    public static final QueueAttendanceV2LeavePolicyGrant queueV2LeavePolicyGrant = new QueueAttendanceV2LeavePolicyGrant();

    public static void init() throws Exception {
        try {
            CacheManager.init(context.clazz().getSimpleName());
            MessageConnector.start(context());
            /////////////////// V2///
            // 근태 통계 관련 큐 처리
            context.startQueue(queueV2Detail);
            context.startQueue(queueV2LeavePolicyGrant);
            // 설정 객체, 근태 통계 타이머 설정 가능
            AttendanceV2Config config = null; 
            String cronString = null;
            boolean leaveTypeInitialized = false; // 근태 휴가 유형 기본 데이터 초기화 여부
            boolean chineseHolidaySyncEnable = true; // 중국 공휴일 데이터 동기화 사용 여부
            try  {
                EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
                List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
                if (configs != null && !configs.isEmpty()) {
                    config = configs.get(0);
                }
                if (config != null) {
                    cronString = config.getDetailStatisticCronString();
                    leaveTypeInitialized = config.getProperties() != null && BooleanUtils.isTrue(config.getProperties().getLeaveTypeInitialized());
                    chineseHolidaySyncEnable = !(config.getProperties() != null && BooleanUtils.isFalse(config.getProperties().getChineseHolidaySyncEnable()));
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            if (StringUtils.isEmpty(cronString)) {
                cronString = "0 0 3 * * ?";
            }
            // 근태 휴가 유형 기본 데이터 초기화
            if (!leaveTypeInitialized){
                new AttendanceV2LeaveManagerService().initDefaultLeaveTypeData();
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("타이머 표현식 {}", cronString);
            }
            // 매일 새벽 3시, 전날 근태 데이터 계산
            context.schedule(AttendanceV2DetailGenerateTask.class, cronString);
            // 매일 새벽 3시 30분, 당일 발송할 메시지 데이터 재계산.
            context.schedule(AttendanceV2TodayMessageDataGenerateTask.class, "0 30 3 * * ?");
            // 4시부터 5분마다 근태 관련 메시지 발송 작업 확인
            context.schedule(AttendanceV2MessageSendTask.class, "0 0/5 4-23 * * ?");
            // 30분마다 한 번 실행, 작업 발송.
            context.schedule(AttendanceV2LeavePolicyGrantTask.class, "0 0/30 * * * ?");
            // 매일 새벽 2시 30분, 만료된 휴가 한도 배치 처리.
            context.schedule(AttendanceV2LeaveLedgerExpireTask.class, "0 30 2 * * ?");
            // 매일 새벽 1시, 중국 공휴일 데이터 동기화.
            if (chineseHolidaySyncEnable) {
                context.schedule(AttendanceV2HolidaySyncTask.class, "0 0 1 * * ?");
            }

        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    public static void destroy() {
        try {
            FORKJOINPOOL.shutdown();
            CacheManager.shutdown();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }
}
