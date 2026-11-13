package com.x.attendance.assemble.control;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.service.AttendanceStatisticRequireLogServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;

/**
 * 직원 한 명의 출퇴근 정보를 분석하는 큐
 */
public class QueueAttendanceDetailStatistic extends AbstractQueue<String> {

    private static final Logger logger = LoggerFactory.getLogger(QueueAttendanceDetailStatistic.class);

    @Override
    protected void execute( String logId ) throws Exception {
        AttendanceStatisticRequireLogServiceAdv statisticRequireLogServiceAdv = new AttendanceStatisticRequireLogServiceAdv();
        AttendanceWorkDayConfigServiceAdv workDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
        AttendanceStatisticServiceAdv statisticServiceAdv = new AttendanceStatisticServiceAdv();
        AttendanceStatisticalCycleServiceAdv statisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
        AttendanceStatisticRequireLog log = statisticRequireLogServiceAdv.get(logId);

        if( log != null ){
            logger.debug("system try to statistic attendance detail, logId:" + logId );
            AttendanceStatisticalCycle attendanceStatisticalCycle  = null;
            List<AttendanceWorkDayConfig> workDayConfigList = null;
            List<AttendanceStatisticRequireLog> attendanceStatisticRequireLogList = null;
            Map<String, Map<String, List<AttendanceStatisticalCycle>>> statisticalCycleMap = null;

            try {//먼저 모든 법정 공휴일·근무일 설정 목록 조회
                workDayConfigList = workDayConfigServiceAdv.getAllWorkDayConfigWithCache(false );
            } catch ( Exception e ) {
                logger.warn("[통계] 당월 출퇴근 기록이 있는 직원 이름 목록 조회 중 예외가 발생했습니다!" );
                logger.error(e);
            }

            try{//모든 근태 통계 주기 정보를 조회해 MAP으로 구성
                statisticalCycleMap = statisticalCycleServiceAdv.getAllStatisticalCycleMapWithCache(false);
            }catch(Exception e){
                logger.warn( "[통계] 모든 근태 통계 주기 정보 조회 및 구성 중 예외가 발생했습니다." );
                logger.error(e);
            }

            //먼저 모든 통계 오류 처리
            try {
                logger.debug( false, "복구할 통계 오류 정보 처리 준비, 모든 오류 통계를 다시 계산합니다......" );
                statisticRequireLogServiceAdv.resetStatisticError();
            } catch (Exception e) {
                logger.warn("[통계] 통계 오류 정보 재설정 중 예외가 발생했습니다!" );
                logger.error(e);
            }
            //통계 유형:PERSON_PER_MONTH|UNIT_PER_MONTH|TOPUNIT_PER_MONTH|UNIT_PER_DAY|TOPUNIT_PER_DAY
            //통계 처리 상태: WAITING|PROCESSING|COMPLETE|ERROR
            if( StringUtils.equals( "PERSON_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "[직원 월별 통계] 준비, 직원:" + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try {
                    attendanceStatisticalCycle = statisticalCycleServiceAdv.getStatisticCycleByEmployee( log, statisticalCycleMap, false );
                } catch (Exception e) {
                    logger.warn("[통계] 통계 요청 기록으로 통계 주기 정보 조회 중 예외가 발생했습니다!" );
                    logger.error(e);
                }
                if( attendanceStatisticalCycle != null ){
                    try{
                        statisticServiceAdv.statisticEmployeeAttendanceForMonth( log, attendanceStatisticalCycle, workDayConfigList, statisticalCycleMap);
                    }catch(Exception e){
                        logger.warn( "[통계] 직원 월별 출퇴근 기록 분석 결과 통계 중 예외가 발생했습니다." );
                        logger.error(e);
                    }
                }
            } else if ( StringUtils.equals( "UNIT_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "[조직 월별 통계] 준비, 조직:" + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try{
                    statisticServiceAdv.statisticUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap );
                }catch(Exception e){
                    logger.warn( "[통계] 직원 월별 출퇴근 기록 분석 결과 통계 중 예외가 발생했습니다." );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "TOPUNIT_PER_MONTH", log.getStatisticType() )){
                logger.debug( false, "[최상위 조직 월별 통계] 준비, 최상위 조직:" + log.getStatisticKey() + ", 통계 월:" + log.getStatisticYear() + "-" +log.getStatisticMonth() );
                try{
                    statisticServiceAdv.statisticTopUnitAttendanceForMonth( log, workDayConfigList, statisticalCycleMap);
                }catch(Exception e){
                    logger.warn( "[통계] 직원 월별 출퇴근 기록 분석 결과 통계 중 예외가 발생했습니다." );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "UNIT_PER_DAY", log.getStatisticType() )){
                logger.debug( false, "[조직 월별 통계] 준비, 조직:" + log.getStatisticKey() + ", 통계 날짜:" + log.getStatisticDay() );
                try{
                    statisticServiceAdv.statisticUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap, false );
                }catch(Exception e){
                    logger.warn( "[통계] 조직 일별 출퇴근 기록 분석 결과 통계 중 예외가 발생했습니다." );
                    logger.error(e);
                }
            } else if ( StringUtils.equals( "TOPUNIT_PER_DAY", log.getStatisticType() )){
                logger.debug( false, "[최상위 조직 월별 통계] 준비, 최상위 조직:" + log.getStatisticKey() + ", 통계 날짜:" + log.getStatisticDay() );
                try{
                    statisticServiceAdv.statisticTopUnitAttendanceForDay( log, workDayConfigList, statisticalCycleMap );
                }catch(Exception e){
                    logger.warn( "[통계] 최상위 조직 일별 출퇴근 기록 분석 결과 통계 중 예외가 발생했습니다." );
                    logger.error(e);
                }
            }else{
                logger.warn( "statistic require log can not execute, type:" + log.getStatisticType() );
            }
            logger.debug("["+logId+"] attendance detail record statistic task execute completed。" );
        }else{
            logger.warn("attandence statistic require logId not exists, id:" + logId );
        }
    }
}
