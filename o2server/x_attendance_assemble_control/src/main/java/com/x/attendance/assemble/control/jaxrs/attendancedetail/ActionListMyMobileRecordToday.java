package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
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
 * 로그인 사용자의 당일 모든 모바일 출퇴근 기록 정보 조회
 */
public class ActionListMyMobileRecordToday extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListMyMobileRecordToday.class);
	private DateOperation dateOperation = new DateOperation();

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

		// 설정 확인: 구버전 근태 비활성화 여부
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			AttendanceV2Config config = null; // 설정 객체
			List<AttendanceV2Config> configs = emc.listAll(AttendanceV2Config.class);
			if (configs != null && !configs.isEmpty()) {
					config = configs.get(0);
			}
			if (config != null && BooleanUtils.isTrue(config.getCloseOldAttendance())) {
				throw new ExceptionCloseOldAttendance();
			}
		}

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

		//마지막 출퇴근 기록 정보로 다음 기록 정보 계산
		WoSignFeature woSignFeature = null;
		if (check
				&& !StringUtils.equalsAnyIgnoreCase("xadmin", effectivePerson.getName())
				&& !StringUtils.equalsAnyIgnoreCase("cipher", effectivePerson.getName())) {
			//기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근 기록)
			scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( effectivePerson.getDistinguishedName(), effectivePerson.getDebugger() );

			if( scheduleSetting != null ){
				woScheduleSetting = WoScheduleSetting.copier.copy(scheduleSetting);

				if( woScheduleSetting.getSignProxy() == 3 ){
					//3-4회 기록(오전·오후 모두 출근·퇴근 기록)
					woSignFeature = getWoSignFeatureWithProxy3(wraps, woScheduleSetting);
				}else if( woScheduleSetting.getSignProxy() == 2 ){
					//2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회)
					woSignFeature = getWoSignFeatureWithProxy2(wraps, woScheduleSetting);
				}else{
					//1-2회 기록(오전 출근, 오후 퇴근)
					woSignFeature = getWoSignFeatureWithProxy1(wraps, woScheduleSetting);
				}
			}
			if( woSignFeature != null ){
				woSignFeature.setSignDate( signDate );
			}
		}
		
		//근무 배정 상세 목록
		 List<WoSignFeature> scheduleInfos = new ArrayList<>();
				if (check
						&& !StringUtils.equalsAnyIgnoreCase("xadmin", effectivePerson.getName())
						&& !StringUtils.equalsAnyIgnoreCase("cipher", effectivePerson.getName())) {
					//기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근

					if( woScheduleSetting != null ){
						WoSignFeature scheduleInfo1 = new WoSignFeature();
						scheduleInfo1.setSignSeq(1);
						scheduleInfo1.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
						scheduleInfo1.setSignTime(scheduleSetting.getOnDutyTime());
						scheduleInfo1.setSignDate( signDate );
						scheduleInfos.add(scheduleInfo1);
						

						if( woScheduleSetting.getSignProxy() == 3 ){
							//3-4회 기록(오전·오후 모두 출근·퇴근 기록)
							WoSignFeature scheduleInfo2 = new WoSignFeature();
							scheduleInfo2.setSignSeq(2);
							scheduleInfo2.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
							scheduleInfo2.setSignTime(scheduleSetting.getMiddayRestStartTime());
							scheduleInfo2.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo2);
							
							WoSignFeature scheduleInfo3 = new WoSignFeature();
							scheduleInfo3.setSignSeq(3);
							scheduleInfo3.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY );
							scheduleInfo3.setSignTime(scheduleSetting.getMiddayRestEndTime());
							scheduleInfo3.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo3);
							
						}else if( woScheduleSetting.getSignProxy() == 2 ){
							//2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회)
							WoSignFeature scheduleInfo3 = new WoSignFeature();
							scheduleInfo3.setSignSeq(3);
							scheduleInfo3.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
							scheduleInfo3.setSignTime(scheduleSetting.getMiddayRestEndTime());
							scheduleInfo3.setSignDate( signDate );
							scheduleInfos.add(scheduleInfo3);
						}else{
							//1-2회 기록(오전 출근, 오후 퇴근)
						}
						
						WoSignFeature scheduleInfo4 = new WoSignFeature();
						scheduleInfo4.setSignSeq(4);
						scheduleInfo4.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						scheduleInfo4.setSignTime(scheduleSetting.getOffDutyTime());
						scheduleInfo4.setSignDate( signDate );
						scheduleInfos.add(scheduleInfo4);
					}
					/*if( scheduleIf != null ){
						scheduleIf.setSignDate( signDate );
					}*/
				}

		Wo wo = new Wo();
		wo.setRecords( wraps );
		wo.setFeature( woSignFeature );
		wo.setScheduleSetting( woScheduleSetting );
		wo.setScheduleInfos(scheduleInfos);
		result.setCount(total);
		result.setData(wo);

		return result;
	}

	public static class Wo{

		@FieldDescribe("모든 출퇴근 기록.")
		private List<WoMobileRecord> records;

		@FieldDescribe("개인 관련 근무 배정 정보")
		private WoScheduleSetting scheduleSetting;

		@FieldDescribe("다음 출퇴근 기록 정보")
		private WoSignFeature feature;
		
		@FieldDescribe("근무 배정 상세")
		private List<WoSignFeature> scheduleInfos;

		public WoScheduleSetting getScheduleSetting() { return scheduleSetting; }

		public void setScheduleSetting(WoScheduleSetting scheduleSetting) { this.scheduleSetting = scheduleSetting; }

		public List<WoMobileRecord> getRecords() { return records; }

		public void setRecords(List<WoMobileRecord> records) { this.records = records; }

		public WoSignFeature getFeature() { return feature; }

		public void setFeature(WoSignFeature feature) { this.feature = feature; }
		
		public List<WoSignFeature> getScheduleInfos() { return scheduleInfos; }

		public void setScheduleInfos(List<WoSignFeature> scheduleInfos) { this.scheduleInfos = scheduleInfos; }
	}

	public static class WoScheduleSetting extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, WoScheduleSetting> copier = WrapCopierFactory.wo(AttendanceScheduleSetting.class,
				WoScheduleSetting.class, null, JpaObject.FieldsInvisible);
	}


}