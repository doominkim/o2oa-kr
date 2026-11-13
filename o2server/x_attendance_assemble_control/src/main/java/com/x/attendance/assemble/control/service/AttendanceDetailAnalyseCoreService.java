package com.x.attendance.assemble.control.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * @author LIYI
 *
 */
class AttendanceDetailAnalyseCoreService {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseCoreService.class );
	private AttendanceSelfHolidayService attendanceSelfHolidayService = new AttendanceSelfHolidayService();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();
	private AttendanceStatisticalCycleService attendanceStatisticalCycleService = new AttendanceStatisticalCycleService();
	private static DateOperation dateOperation = new DateOperation();
	private UserManagerService userManagerService = new UserManagerService();

	/**
	 * 출퇴근 기록 분석 핵심 메서드, 출퇴근 기록 한 건 분석<br/>
		<b>지각</b>: 최종 “지각 기준 시간”보다 늦음(당일이 주말·공휴일이 아닌 정상 근무일이고 휴가가 아니거나, 당일이 대체 근무일인 경우)<br/>
		<b>조퇴</b>: 퇴근 체크 시간이 “지각 기준 시간”보다 이르고 당일 근무 시간이 9시간 미만(당일이 주말·공휴일이 아닌 정상 근무일이고 휴가가 아니거나, 당일이 대체 근무일인 경우)<br/>
		<b>이상</b>: 출퇴근 체크 데이터 중 1건이 없거나, 출퇴근 체크 시간이 모두 최종 기준 시간 안에 있으면서 9시간 미만(당일이 주말·공휴일이 아닌 정상 근무일이고 휴가가 아니거나, 당일이 대체 근무일인 경우)<br/>
		<b>결근</b>: 당일 체크 데이터가 없고 당일이 근무일인 경우(당일이 주말·공휴일이 아닌 정상 근무일이고 휴가가 아니거나, 당일이 대체 근무일인 경우)
	 * @param detail
	 * @param scheduleSetting
	 * @throws Exception 
	 */
	AttendanceDetail analyseCore( AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger ) throws Exception {
		if( dateOperation == null ){
			dateOperation = new DateOperation();
		}
		if( detail == null ){
			throw new Exception("detail is null!" );
		}
		if( scheduleSetting == null ){
			throw new Exception("scheduleSetting is null, empName:" + detail.getEmpName() );
		}
		//근태 체크 규칙에 따라 어떤 규칙으로 근태 결과를 분석할지 판단
		if( 2 == scheduleSetting.getSignProxy() ){
			//2. 하루 3회 체크: 출근·퇴근 2회 외에 점심 휴식 시간에도 1회 체크하여 직원의 회사 내 활동 확인
			detail = new AttendanceDetailAnalyseSignProxy2().analyse(detail, scheduleSetting, debugger);
		}else if( 3 == scheduleSetting.getSignProxy() ){
			//3. 하루 4회 체크: 오전 출근, 오전 퇴근, 오후 출근, 오후 퇴근 4회 체크
			detail = new AttendanceDetailAnalyseSignProxy3().analyse(detail, scheduleSetting, debugger);
		}else{
			//1. 하루 출퇴근 2회만 체크
			detail = new AttendanceDetailAnalyseSignProxy1().analyse(detail, scheduleSetting, debugger);
		}
		return detail;
	}

	static Date getAfternoonOndutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		if( StringUtils.isNotEmpty( detail.getAfternoonOnDutyTime()) ){
			try {
				logger.debug( debugger, "[오후 출근 체크 시간] 포맷 afternoonOndutyTime=" +  detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,afternoonOndutyTime=" + detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				logger.warn( "시스템이 시간을 변환하는 중 예외가 발생했습니다, afternoonOndutyTime=" + detail.getRecordDateString() + " " + detail.getAfternoonOnDutyTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>오전 퇴근 체크 시간이 비어 있습니다!");
		}
		return null;
	}

	static Date getMorningOffdutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		if( StringUtils.isNotEmpty( detail.getMorningOffDutyTime()) ){
			try {
				logger.debug( debugger, "[오전 퇴근 체크 시간] 포맷 morningOffdutyTime=" +  detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,morningOffdutyTime=" + detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				logger.warn( "시스템이 시간을 변환하는 중 예외가 발생했습니다, morningOffdutyTime=" + detail.getRecordDateString() + " " + detail.getMorningOffDutyTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>오전 퇴근 체크 시간이 비어 있습니다!");
		}
		return null;
	}

	static Date getAbsenceStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getAbsenceStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>[결근 기준 시간] 포맷 absenceStartTime=" +  detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,absenceStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				logger.warn( "시스템이 시간을 변환하는 중 예외가 발생했습니다, absenceStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getAbsenceStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>오전 결근 시간 설정이 비어 있습니다! 시스템은 오전 결근을 판단하지 않습니다");
		}
		return null;
	}

	static Date getLeaveEarlyStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getLeaveEarlyStartTime() ) ){
			try {
				logger.debug( debugger, ">>>>>>>>>>[조퇴 기준 시간] 포맷 leaveEarlyStartTime=" +  detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,leaveEarlyStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				logger.warn( "시스템이 시간을 변환하는 중 예외가 발생했습니다, leaveEarlyStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLeaveEarlyStartTime() );
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>조퇴 시간 설정이 비어 있습니다! 시스템은 조퇴를 판단하지 않습니다");
		}
		return null;
	}

	static Date getLateStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		if( StringUtils.isNotEmpty( scheduleSetting.getLateStartTime() ) ){
			try {
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime() );
			} catch (Exception e) {
				detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,lateStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime() );
				logger.warn( "시스템이 시간을 변환하는 중 예외가 발생했습니다, lateStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getLateStartTime());
				logger.error(e);
			}
		}else{
			logger.debug( debugger, ">>>>>>>>>>지각 시간 설정이 비어 있습니다! 시스템은 지각을 판단하지 않습니다");
		}
		return null;
	}


	/**
	 * 보완된 근태 출퇴근 기록으로 당일 오전 퇴근(점심 휴식 시작) 체크 시간 조회
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	static Date getMiddleRestStartTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		try {
			if( scheduleSetting.getMiddayRestStartTime() != null ){
				logger.debug(debugger, "middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime());
				return dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestStartTime());
			}
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.debug(debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, middleRestStartTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 보완된 근태 출퇴근 기록으로 당일 오후 출근(점심 휴식 종료) 체크 시간 조회
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	static Date getMiddleRestEndTimeFromDetail(AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger) {
		try {
			if( StringUtils.isNotEmpty(scheduleSetting.getMiddayRestEndTime())){
				logger.debug(debugger, "middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestEndTime());
				return dateOperation.getDateFromString(detail.getRecordDateString() + " " + detail.getMiddayRestEndTime());
			}
		} catch (Exception e) {
			detail.setDescription(detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.debug(debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, middleRestEndTime=" + detail.getRecordDateString() + " " + scheduleSetting.getMiddayRestStartTime() + " - " + scheduleSetting.getMiddayRestEndTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 보완된 근태 출퇴근 기록으로 당일 출근 체크 시간 조회
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOnWorkTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOnWorkTime() ) ){
				logger.debug( debugger, "onWorkTime " );
			}else{
				logger.debug( debugger, "onWorkTime=" +  detail.getRecordDateString() + " " + detail.getOnWorkTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime() );
			logger.debug( debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, onWorkTime=" + detail.getRecordDateString() + " " + detail.getOnWorkTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 보완된 근태 출퇴근 기록으로 당일 출근 체크 시간 조회
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOffWorkTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOffWorkTime() ) ){
				logger.debug( debugger, "offWorkTime이 비어 있음 " );
			}else{
				logger.debug( debugger, "offWorkTime=" +  detail.getRecordDateString() + " " + detail.getOffWorkTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.debug( debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, offWorkTime=" + detail.getRecordDateString() + " " + detail.getOffWorkTime() );
			logger.error(e);
		}
		return null;
	}

	/**
	 * 보완된 근태 출퇴근 기록으로 당일 실제 출근 체크 시간 조회
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOnDutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOnDutyTime() ) ){
				logger.debug( debugger, "onDutyTime이 비어 있음 " );
			}else{
				logger.debug( debugger, "onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOnDutyTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime() );
			logger.debug( debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, onDutyTime=" + detail.getRecordDateString() + " " + detail.getOnDutyTime());
			logger.error(e);
		}
		return null;
	}
	/**
	 * 보완된 근태 출퇴근 기록으로 당일 실제 퇴근 체크 시간 조회
	 * @param detail
	 * @param debugger
	 * @return
	 */
	static Date getOffDutyTimeFromDetail(AttendanceDetail detail, Boolean debugger) {
		try {
			if( StringUtils.isEmpty( detail.getOffDutyTime() ) ){
				logger.debug( debugger, "offDutyTime " );
			}else{
				logger.debug( debugger, "offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
				return dateOperation.getDateFromString( detail.getRecordDateString() + " " + detail.getOffDutyTime() );
			}
		} catch (Exception e) {
			detail.setDescription( detail.getDescription() + "; 시스템 시간 변환 중 오류 발생,offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime() );
			logger.debug( debugger, "시스템이 시간을 변환하는 중 예외가 발생했습니다, offDutyTime=" + detail.getRecordDateString() + " " + detail.getOffDutyTime());
			logger.error(e);
		}
		return null;
	}

	/**
	 * 체크 정보를 분석하여 체크 데이터에 결근 일수 정보 추가, 기본값 0, 매번 0.5일씩 증가
	 * @param detail
	 */
	static void increaseAbsenceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double absence = detail.getAbsence();
		if (absence < 1.0) {
			detail.setAbsence( absence + 0.5 );
		}
	}

	//

	/**
	 * 체크 정보를 분석하여 체크 데이터의 출근 일수 정보 계산, 기본값 1, 매번 0.5일씩 감소
	 * @param detail
	 */
	static void increaseAttendanceStatusForAttendanceDetail(AttendanceDetail detail) {
		Double attendance = detail.getAttendance();
		if (attendance > 0.0) {
			detail.setAttendance(attendance - 0.5);
		}
	}

}
