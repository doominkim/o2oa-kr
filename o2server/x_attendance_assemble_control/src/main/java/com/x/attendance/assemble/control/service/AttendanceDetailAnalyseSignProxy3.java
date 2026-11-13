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
 * 2. 하루 3회 체크: 출근·퇴근 2회 외에 점심 휴식 시간에도 1회 체크하여 직원의 회사 내 활동 확인
 *
 */
class AttendanceDetailAnalyseSignProxy3 {
	
	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailAnalyseSignProxy3.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 3. 하루 4회 체크: 오전 출근, 오전 퇴근, 오후 출근, 오후 퇴근 4회 체크
	 * @param detail
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 */
	public AttendanceDetail analyse( AttendanceDetail detail, AttendanceScheduleSetting scheduleSetting, Boolean debugger ) throws Exception {
		Date onWorkTime = null, offWorkTime = null;
		Date onDutyTime = null, offDutyTime = null, morningOffdutyTime = null, afternoonOndutyTime = null;
		Date lateStartTime = null, leaveEarlyStartTime = null, absenceStartTime = null, morningEndTime = null, afternoonStartTime = null;

		//주말이면서 대체 근무일이 아니거나 공휴일인 경우
		Boolean isNotWorkDay = ( detail.getIsWeekend() && !detail.getIsWorkday()) || detail.getIsHoliday();

		//직원 휴가 상태
		Boolean isSelfHoliday_FullDay = detail.getIsGetSelfHolidays() && "全天".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday_Morning = detail.getIsGetSelfHolidays() && "上午".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday_Afternoon = detail.getIsGetSelfHolidays() && "下午".equalsIgnoreCase( detail.getSelfHolidayDayTime() );
		Boolean isSelfHoliday = isSelfHoliday_Afternoon || isSelfHoliday_Morning || isSelfHoliday_FullDay;

		//먼저 현재 체크 정보의 출퇴근 시간 요건을 초기화. 이 요건은 직원 소속 조직의 근무 일정 정보로 조회한 것
		onWorkTime = AttendanceDetailAnalyseCoreService.getOnWorkTimeFromDetail( detail, debugger );
		morningEndTime = AttendanceDetailAnalyseCoreService.getMiddleRestStartTimeFromDetail( detail, scheduleSetting, debugger );
		afternoonStartTime = AttendanceDetailAnalyseCoreService.getMiddleRestEndTimeFromDetail( detail, scheduleSetting, debugger );
		offWorkTime = AttendanceDetailAnalyseCoreService.getOffWorkTimeFromDetail( detail, debugger );
		
		onDutyTime = AttendanceDetailAnalyseCoreService.getOnDutyTimeFromDetail( detail, debugger );
		offDutyTime = AttendanceDetailAnalyseCoreService.getOffDutyTimeFromDetail( detail, debugger );
		morningOffdutyTime = AttendanceDetailAnalyseCoreService.getMorningOffdutyTimeFromDetail( detail, debugger );
		afternoonOndutyTime = AttendanceDetailAnalyseCoreService.getAfternoonOndutyTimeFromDetail( detail, debugger );

		lateStartTime = AttendanceDetailAnalyseCoreService.getLateStartTimeFromDetail( detail, scheduleSetting, debugger );
		leaveEarlyStartTime = AttendanceDetailAnalyseCoreService.getLeaveEarlyStartTimeFromDetail( detail, scheduleSetting, debugger );
		absenceStartTime = AttendanceDetailAnalyseCoreService.getAbsenceStartTimeFromDetail( detail, scheduleSetting, debugger );

		if ( onWorkTime != null && offWorkTime != null && morningEndTime != null && afternoonStartTime != null ) {
			logger.debug( debugger, "출퇴근 근무 일정 정보 조회 정상: onWorkTime=" +  onWorkTime + "， morningEndTime="+morningEndTime + "， afternoonStartTime="+afternoonStartTime + "， offWorkTime="+offWorkTime );
			logger.debug( debugger, "출퇴근 체크 정보 조회 정상: onDutyTime=" +  onDutyTime + "， morningOffdutyTime="+morningOffdutyTime + "， afternoonOndutyTime="+afternoonOndutyTime + "， offDutyTime="+offDutyTime );

			//규칙: 직원이 출근·퇴근 체크를 모두 하지 않아 체크 시간이 하나도 없으면 결근으로 처리될 수 있음
			if ( onDutyTime == null && offDutyTime == null ) {
				//직원이 이미 종일 휴가이면 결근이 아니며 근태 결과는 정상
				if( isSelfHoliday_FullDay || isNotWorkDay ){
					logger.debug( debugger, "全天请假不计缺勤。" );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( false );
					detail.setAbsence( 0.0 );
					detail.setAbsentDayTime( "无" );
					detail.setWorkTimeDuration( 0L );
				}else{
					//그렇지 않고 체크도 없고 휴가·주말·공휴일도 아니면 결근으로 처리(직원이 출근·퇴근 체크를 모두 하지 않아 체크 시간이 하나도 없는 경우)
					logger.debug( debugger, "휴가 아님, 근무일, 결근 1일 기록." );
					detail.setAttendance( 0.0 );
					detail.setIsAbsent( true );
					detail.setAbsence( 1.0 );
					detail.setAbsentDayTime("全天");
					detail.setWorkTimeDuration( 0L );
				}
			}else{
				detail.setAttendance( 1.0 );//기본은 종일 출근
				detail.setIsAbsent( false );//기본은 결근 아님
				detail.setAbsence( 0.0 );//기본은 결근 아님

				//=========================================================================================================
				//=====오전: 직원이 이미 출근 체크한 경우================================================================================
				//=========================================================================================================
				if( onDutyTime != null ){
					logger.debug( debugger, "上午打过卡，时间：onDutyTime=" + onDutyTime );
					//결근 기준 시간: absenceStartTimes는 설정하지 않아도 되며, 설정하지 않으면 null이 되어 체크 시간이 늦어도 결근으로 처리되지 않음
					//오전 출근 체크를 함. 근무 일정 설정에 결근 기준 시간이 있으면 직원 결근 여부를 판단하고, 휴가가 아니면 반일 결근으로 처리
					if( absenceStartTime != null && onDutyTime.after( absenceStartTime )){
						logger.debug( debugger, "上午打卡时间晚于缺勤计时时间：" + absenceStartTime );
						//직원이 이미 휴가를 냈는지 판단
						if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가는 출근으로 계산하지 않지만 휴가를 냈으면 결근이 아님" );
							detail.setIsAbsent( false );
							detail.setAbsence(0.0);
						}else{
							logger.debug( debugger, "휴가 아님, 반일 결근" );
							detail.setAbsentDayTime("上午");
							detail.setIsAbsent( true );
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);//결근 일수 증가 + 0.5
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//출근 일수 감소 - 0.5
					}else if( lateStartTime != null && onDutyTime.after( lateStartTime )){
						//오전 출근 체크를 했고 결근 기준 시간을 넘지 않음. 근무 일정 설정에 지각 기준 시간이 있으면 휴가도 주말도 아닐 때 지각 여부 판단
						logger.debug( debugger, "上午打卡时间晚于迟到计时时间......" );
						if( isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가를 냈으면 지각이 아님" );
							detail.setLateTimeDuration( 0L ); //휴가 중이면 지각이 아님
							detail.setIsLate( false );//휴가 중이면 지각이 아님
						}else{
							//지각은 지각 기준 시간이 아니라 출근 시간부터 계산
							long minutes = dateOperation.getMinutes( onWorkTime, onDutyTime );
							detail.setLateTimeDuration( minutes );//휴가가 아니면 지각 시간 계산
							detail.setIsLate( true );//휴가가 아니면 지각
							logger.debug( debugger, "지각 1회 기록, 지각 시간: minutes=" + minutes );
						}

					}
				}else{
					logger.debug( debugger, "员工上午缺卡" );
					if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
						logger.debug( debugger, "휴가는 근태에 포함하지 않으며 체크가 필요 없고 이상이 아님" );
						detail.setIsAbsent( false );
						detail.setAbnormalDutyDayTime("无");
						detail.setIsAbnormalDuty( false );
					}else{
						logger.debug(debugger, "체크 없음, 휴가 아님, 근무일이면 결근으로 처리.");
						detail.setAbnormalDutyDayTime("上午");
						detail.setIsAbnormalDuty(true);
						detail.setAbsentDayTime("上午");
						detail.setIsAbsent(true);
						AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
					}
					AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//출근 일수 감소 - 0.5
					logger.debug( debugger, "上午工作时长, 未打卡：minutes= 0 分钟。" );
					detail.setWorkTimeDuration( 0L );
				}

				//=========================================================================================================
				//=====오전: 직원이 이미 퇴근 체크한 경우 morningOffdutyTime================================================================================
				//=========================================================================================================
				if( morningOffdutyTime != null ){//퇴근 체크, 오전에는 조퇴 여부를 판단하지 않음
					long minutes = 0L;
					//오전 퇴근 체크를 했으므로 오전 근무 시간 계산
					if (onDutyTime != null ) {
						minutes = dateOperation.getMinutes(onDutyTime, morningOffdutyTime);
					}
					detail.setWorkTimeDuration( minutes );
					logger.debug( debugger, "直接计算上午工作时长, 从"+ onDutyTime +"~" + morningOffdutyTime + " ：minutes=" + minutes + "분." );
				}else{
					//오후 출근 시간이 되었는지 확인
					if( new Date().before(afternoonStartTime )){
						logger.debug( debugger, "上午还未结束，先不分析结果。" );
					}else{
						logger.debug( debugger, "员工上午未签退，属于异常状态，缺卡" );
						if(   isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가 또는 비근무일은 근태에 포함하지 않으며 체크가 필요 없고 이상 상태가 아님" );
//							detail.setLeaveEarlierTimeDuration( 0L );
//							detail.setIsLeaveEarlier( false );
						}else{
							detail.setAbnormalDutyDayTime("上午");
							detail.setAbsentDayTime("上午");
							detail.setIsAbnormalDuty(true);
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
							logger.debug( debugger, "휴가 아님, 미체크이면 체크 누락 및 결근 상태로 처리." );
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//출근 일수 감소 - 0.5
						//직원 퇴근 미체크, 하루 전체 근무 시간을 계산하되 오후 시간은 계산하지 않음
						detail.setWorkTimeDuration( 0L );
						logger.debug( debugger, "员工未签退，上午工作时长暂时记为0分钟，等待补卡或者补提请假申请。" );
					}
				}

				//=========================================================================================================
				//=====오후: 직원이 이미 퇴근 체크한 경우 afternoonOndutyTime================================================================================
				//=========================================================================================================
				if( afternoonOndutyTime != null ){
					logger.debug( debugger, "下午打卡签到，时间：afternoonOndutyTime=" + afternoonOndutyTime );
				}else{
					if( new Date().after( afternoonStartTime )){
						logger.debug( debugger, "员工下午上班缺卡" );
						if(  isSelfHoliday_Morning || isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가는 근태에 포함하지 않으며 체크가 필요 없고 이상이 아님" );
//							detail.setIsAbsent( false );
//							detail.setAbnormalDutyDayTime("없음");
//							detail.setIsAbnormalDuty( false );
						}else{
							logger.debug(debugger, "체크 없음, 휴가 아님, 근무일이면 결근으로 처리.");
							detail.setAbnormalDutyDayTime("下午");
							detail.setIsAbnormalDuty(true);
							detail.setAbsentDayTime("下午");
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//출근 일수 감소 - 0.5
						logger.debug( debugger, "下午工作时长, 未打卡：minutes= 0 分钟。" );
//						detail.setWorkTimeDuration( 0L );
					}
				}

				//=========================================================================================================
				//=====오후: 직원이 이미 퇴근 체크한 경우================================================================================
				//=========================================================================================================
				if( offDutyTime != null ){
					long minutes = 0L;
					logger.debug( debugger, "조퇴 계산 시간: leaveEarlyStartTime=" + leaveEarlyStartTime );
					if( leaveEarlyStartTime != null && offDutyTime.before( leaveEarlyStartTime )){
						logger.debug( debugger, "下午打卡时间早于早退计时时间" );
						if(  isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가·휴일은 근태에 포함하지 않으며 출근·조퇴로 계산하지 않음" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
						}else{
							minutes = dateOperation.getMinutes( offDutyTime, offWorkTime );//조퇴 시간 계산
							detail.setLeaveEarlierTimeDuration(minutes); //조퇴 시간
							detail.setIsLeaveEarlier( true );
						}
					}
//					else if ((leaveEarlyStartTime != null && offDutyTime.after(leaveEarlyStartTime)) && offDutyTime.before(offWorkTime)) {
//						//조퇴 기준 시간 이후, 퇴근 시간 이전에 체크
//						logger.debug(debugger, "오후 체크 시간이 조퇴 계산 시간보다 늦고 퇴근 시간보다 이름......");
//						if ( isSelfHoliday_Afternoon || isSelfHoliday_Afternoon || isNotWorkDay ) {
//							logger.debug(debugger, "휴가는 근태에 포함하지 않으며 출근은 반일만 계산, 휴가를 냈으면 조퇴가 아님");
//							detail.setLeaveEarlierTimeDuration(0L);
//							detail.setIsLeaveEarlier(false);
//						} else {
//							minutes = dateOperation.getMinutes(offDutyTime, offWorkTime);//조퇴 시간 계산
//							detail.setLeaveEarlierTimeDuration(minutes); //조퇴 시간
//							detail.setIsLeaveEarlier(true);
//						}
//					}
					//오후 퇴근 체크를 했으므로 하루 전체 근무 시간 계산
					minutes = dateOperation.getMinutes( afternoonStartTime, offDutyTime);
					logger.debug( debugger, "计算下午工作时长, 从"+ afternoonStartTime +"~" + offDutyTime + " ：minutes=" + minutes + "분." );
					logger.debug( debugger, "直接计算全天工作时长, "+ detail.getWorkTimeDuration() +"+" + minutes + "=" + detail.getWorkTimeDuration() + minutes + "분." );
					detail.setWorkTimeDuration( detail.getWorkTimeDuration() + minutes );//오전 근무 시간 + 오후 근무 시간 기록
				}else{
					//직원 퇴근 미체크: 아직 체크 시간이 되지 않았거나 체크하지 않은 두 가지 경우
					//체크일이 오늘인데 오늘이 아직 끝나지 않은 경우
					if( detail.getRecordDateString().equals( dateOperation.getNowDate() )) {
						detail.setAbnormalDutyDayTime("无");
						detail.setIsAbnormalDuty(false);
						logger.debug( debugger, "오늘이 아직 끝나지 않아 결과를 분석하지 않습니다." );
					}else {
						logger.debug( debugger, "员工下午未打卡，属于异常状态，缺卡" );
						if(  isSelfHoliday_Afternoon || isNotWorkDay ){
							logger.debug( debugger, "휴가 또는 비근무일은 근태에 포함하지 않으며 체크가 필요 없고 이상 상태가 아님" );
							detail.setLeaveEarlierTimeDuration( 0L );
							detail.setIsLeaveEarlier( false );
						}else{
							if( StringUtils.equals( "上午", detail.getAbnormalDutyDayTime()) ){
								detail.setAbnormalDutyDayTime("上午|下午");
							}else {
								detail.setAbnormalDutyDayTime("下午");
							}
							if( StringUtils.equals( "上午", detail.getAbsentDayTime()) ){
								detail.setAbsentDayTime("全天");
							}else{
								detail.setAbsentDayTime("下午");
							}
							detail.setIsAbnormalDuty(true);
							detail.setIsAbsent(true);
							AttendanceDetailAnalyseCoreService.increaseAbsenceStatusForAttendanceDetail(detail);
							logger.debug( debugger, "휴가 아님, 미체크이면 체크 누락 및 결근 상태로 처리." );
						}
						AttendanceDetailAnalyseCoreService.increaseAttendanceStatusForAttendanceDetail(detail);//출근 일수 감소 - 0.5
						//직원 퇴근 미체크, 하루 전체 근무 시간을 계산하되 오후 시간은 계산하지 않음
//						detail.setWorkTimeDuration( 0L );
						logger.debug( debugger, "员工未签退，全天工作时长暂时记为0分钟，等待补卡或者补提请假申请。" );
					}
				}
			}
			detail.setRecordStatus( 1 );
		}else{
			logger.warn( "출퇴근 근무 일정 정보가 올바르게 설정되지 않아 근태 기록을 분석할 수 없습니다: onWorkTime=" +  onWorkTime + "， morningEndTime="+morningEndTime + "， afternoonStartTime=" +  afternoonStartTime + "， offWorkTime="+offWorkTime );
		}
		return detail;
	}
}
