package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingService;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 사전 기록: 현재 사용자와 해당 근무 배정 설정으로 기록 결과 사전 판단
 */
public class ActionMobilePreview extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionMobilePreview.class);
	private DateOperation dateOperation = new DateOperation();
	private AttendanceScheduleSettingService attendanceScheduleSettingService = new AttendanceScheduleSettingService();

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<WoMobileRecord> wraps = new ArrayList<>();
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Long total = 0L;
		Boolean check = true;
		AttendanceScheduleSetting scheduleSetting = null;
		WoScheduleSetting woScheduleSetting = null;
		String signDate = dateOperation.getDateStringFromDate( new Date(), "YYYY-MM-DD");

		//먼저 해당 직원의 모든 근태 데이터 조회
		if (check) {
			try {
				attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobile( effectivePerson.getDistinguishedName(), signDate );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"조건으로 직원 모바일 체크 정보 목록을 조회하는 중 오류가 발생했습니다. DistinguishedName:" + effectivePerson.getDistinguishedName() + ",Date:" + signDate);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if ( ListTools.isNotEmpty(attendanceDetailMobileList)) {
				try {
					wraps = WoMobileRecord.copier.copy(attendanceDetailMobileList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 모바일 체크 정보를 출력 객체로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}

		//현재 시간과 근무 배정 설정으로 현재 사전 기록 정보 계산
		WoSign woSignFeature = null;
		if (check) {
			//기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근 기록)
				scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( effectivePerson.getDistinguishedName(), effectivePerson.getDebugger() );

			if( scheduleSetting != null ){
				woScheduleSetting = WoScheduleSetting.copier.copy(scheduleSetting);

				if( woScheduleSetting.getSignProxy() == 3 ){
					//3-4회 기록(오전·오후 모두 출근·퇴근 기록)
					woSignFeature = this.getWoSignWithProxy3(wraps, woScheduleSetting);
				}else if( woScheduleSetting.getSignProxy() == 2 ){
					//2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회)
					woSignFeature = this.getWoSignWithProxy2(wraps, woScheduleSetting);
				}else{
					//1-2회 기록(오전 출근, 오후 퇴근)
					woSignFeature = this.getWoSignWithProxy1(wraps, woScheduleSetting);
				}
			}
		}

		Wo wo = new Wo();
		wo.setFeature( woSignFeature );
		result.setCount(total);
		result.setData(wo);

		return result;
	}

	/**
	 * 다음 기록이 어떤 기록인지 계산
	 * 1-2회 기록(오전 출근, 오후 퇴근)
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	private WoSign getWoSignWithProxy1(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		WoSign woSignFeature = new WoSign();
		Date now = new Date();
		String recordDateString = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		//근무 배정 설정의 출근 시간
		Date onDutyTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getOnDutyTime() );
		//근무 배정 설정의 지각 시간
		Date lateStartTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getLateStartTime() );
		//근무 배정의 결근 시간
		Date absenceStartTime = dateOperation.getDateFromString( recordDateString + " " + scheduleSetting.getAbsenceStartTime() );
		//근무 배정의 조퇴 시간
		Date leaveEarlyStartTime = dateOperation.getDateFromString( recordDateString + " " + scheduleSetting.getLeaveEarlyStartTime() );
		//근무 배정 설정의 퇴근 시간
		Date offDutyTime = dateOperation.getDateFromString( recordDateString+ " " + scheduleSetting.getOffDutyTime() );

		//현재 시간과 근무 배정 설정으로 현재 시간이 어느 구간인지 판단
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && absenceStartTime.after(now)){
			woSignFeature.setSignSeq(2);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(absenceStartTime.before(now) && leaveEarlyStartTime.after(now)){
			//결근 시간이 지나면 두 번째 기록-퇴근 체크로 간주
			woSignFeature.setSignSeq(4);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	/**
	 * 다음 기록이 어떤 기록인지 계산
	 * 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회)
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	private WoSign getWoSignWithProxy2(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSign woSignFeature = new WoSign();
		SignRecordStatus signRecordStatus = null;

		Date now = new Date();
		Date middayRestEndTime = null;
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");

		//출근·퇴근 시간 계산
		if( StringUtils.isNotEmpty( scheduleSetting.getOnDutyTime())) {
			//근무 배정 설정의 출근 시간
			onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getOffDutyTime())) {
			//근무 배정 설정의 퇴근 시간
			offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestStartTime())) {
			//근무 배정 설정의 점심 휴식 시작 시간
			morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())) {
			//근무 배정 설정의 점심 휴식 종료 시간
			afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		}
		//근무 배정 설정의 지각 시간
		Date lateStartTime = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTime() );
		//근무 배정의 결근 시간
		Date absenceStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getAbsenceStartTime() );
		//근무 배정의 조퇴 시간
		Date leaveEarlyStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTime() );
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		//현재 시간과 근무 배정 설정으로 현재 시간이 어느 구간인지 판단
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && morningOffdutyTime.after(now)){
			if(absenceStartTime.after(now)){
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			}else{
				woSignFeature.setSignSeq(3);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			}
		}else if(morningOffdutyTime.before(now) && afternoonOndutyTime.after(now)){
			//점심 휴식 시작 시간이 지나면 두 번째 기록-정오 출근 체크로 간주
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON);
		}else if(afternoonOndutyTime.before(now) && leaveEarlyStartTime.after(now)){
			//점심 휴식 종료 시간 경과-정오 출근 체크 또는 오후 퇴근 체크일 수 있음
			if(signRecordStatus.getAlreadyAfternoon()){
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON);
			}else{
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
			}
		} else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	/**
	 * 다음 기록이 어떤 기록인지 계산
	 * 3-4회 기록(오전·오후 모두 출근·퇴근 기록)
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSign getWoSignWithProxy3(List<WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSign woSignFeature = new WoSign();
		Date now = new Date();
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		SignRecordStatus signRecordStatus = null;

		if( StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime())){
			return null;
		}

		if( StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime())){
			return null;
		}

		//출근·퇴근 시간 계산
		onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		//근무 배정 설정의 지각 시간(오전 지각, 오후 지각)
		Date lateStartTime = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTime() );
		Date lateStartTimeAfternoon = dateOperation.getDateFromString( todayDateStr+ " " + scheduleSetting.getLateStartTimeAfternoon());
		//근무 배정의 결근 시간
		Date absenceStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getAbsenceStartTime() );
		//근무 배정의 조퇴 시간(오전 조퇴, 오후 조퇴)
		Date leaveEarlyStartTimeMorning = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTimeMorning() );
		Date leaveEarlyStartTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getLeaveEarlyStartTime() );
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		//현재 시간과 근무 배정 설정으로 현재 시간이 어느 구간인지 판단
		if(onDutyTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.after(now)){
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
		}else if(lateStartTime.before(now) && leaveEarlyStartTimeMorning.after(now)) {
			if(signRecordStatus.getAlreadyOnduty()){
				//오전 출근 기록이 이미 있으므로 결과는 오전 조퇴
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}else{
				//오전 출근 기록 없음, 즉 현재 첫 기록이므로 결과는 오전 지각
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				if (absenceStartTime.after(now)) {
					woSignFeature.setSignSeq(3);
					woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY);
				}
			}
		}else if(leaveEarlyStartTimeMorning.before(now) && morningOffdutyTime.after(now)){
			//오전 조퇴 시간이 지나면 두 번째 기록-오전 퇴근 기록으로 간주
			if (absenceStartTime.after(now)) {
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			} else {
				woSignFeature.setSignSeq(3);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}

		}else if(morningOffdutyTime.before(now) && afternoonOndutyTime.after(now)){
			//이때 기록은 오전 퇴근 기록일 수도, 오후 출근 기록일 수도 있음
			woSignFeature.setSignSeq(1);
			if(signRecordStatus.getAlreadyMorningOffDuty()){
				//오전 퇴근 기록이 이미 있으므로 이번은 오후 출근 기록
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY);
			}else{
				//오전 퇴근 기록이 없으므로 이번은 오전 퇴근 기록
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY);
			}
		}else if(afternoonOndutyTime.before(now) && leaveEarlyStartTime.after(now)){
			//점심 휴식 종료 시간 경과-오후 출근 체크 또는 오후 퇴근
			if(signRecordStatus.getAlreadyAfternoonOnDuty()){
				//오후 출근 기록이 이미 있으므로 이번은 오후 퇴근 기록
				woSignFeature.setSignSeq(4);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
			}else{
				//오후 출근 기록이 없으므로 이번은 오후 출근 기록
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY);
			}
		} else{
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType(AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY);
		}
		return woSignFeature;
	}

	private SignRecordStatus getSignRecordStatus(List<WoMobileRecord> wraps, Date onDutyTime, Date morningOffdutyTime, Date afternoonOndutyTime, Date offDutyTime) {
		SignRecordStatus signRecordStatus = new SignRecordStatus();
		if( ListTools.isNotEmpty( wraps )){
			for( WoMobileRecord record : wraps ){
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyOnduty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON, record.getCheckin_type() )){
					signRecordStatus.setAlreadyAfternoon( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyMorningOffDuty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyAfternoonOnDuty( true );
				}
				if( StringUtils.equalsAnyIgnoreCase( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY, record.getCheckin_type() )){
					signRecordStatus.setAlreadyOffDuty( true );
				}
			}
		}
		return signRecordStatus;
	}

	public static class Wo{

		@FieldDescribe("다음 출퇴근 기록 정보")
		private WoSign feature;

		public WoSign getFeature() { return feature; }

		public void setFeature(WoSign feature) { this.feature = feature; }
	}

	public static class WoScheduleSetting extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, WoScheduleSetting> copier = WrapCopierFactory.wo(AttendanceScheduleSetting.class,
				WoScheduleSetting.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoSign{

		@FieldDescribe("사전 기록 결과 유형: 1정상|2지각|3결근|4조퇴|5완료")
		private Integer signSeq = 1;

		@FieldDescribe("打卡操作名称：上班打卡|下班打卡|午休打卡|上午下班打卡|下午上班打卡(根据不同的打卡策略稍有不同)....")
		private String checkinType = "上班打卡";

		public Integer getSignSeq() { return signSeq; }

		public void setSignSeq(Integer signSeq) { this.signSeq = signSeq; }


		public String getCheckinType() { return checkinType; }

		public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

	}

}