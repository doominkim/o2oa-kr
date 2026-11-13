package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.service.AttendanceAppealInfoServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailAnalyseServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceDetailServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceScheduleSettingServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceWorkDayConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ListTools;

public class BaseAction extends StandardJaxrsAction{
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceDetailAnalyseServiceAdv attendanceDetailAnalyseServiceAdv = new AttendanceDetailAnalyseServiceAdv();
	protected AttendanceWorkDayConfigServiceAdv attendanceWorkDayConfigServiceAdv = new AttendanceWorkDayConfigServiceAdv();
	protected AttendanceStatisticalCycleServiceAdv attendanceStatisticCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
	protected AttendanceDetailServiceAdv attendanceDetailServiceAdv = new AttendanceDetailServiceAdv();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	protected AttendanceScheduleSettingServiceAdv attendanceScheduleSettingServiceAdv = new AttendanceScheduleSettingServiceAdv();
	protected AttendanceAppealInfoServiceAdv attendanceAppealInfoServiceAdv = new AttendanceAppealInfoServiceAdv();

	public static class WoSignFeature{

		@FieldDescribe("다음 기록 차수, 첫 번째는 1")
		private Integer signSeq = 1;

		@FieldDescribe("기록 날짜: yyyy-mm-dd.")
		private String signDate = null;

		@FieldDescribe("기록 시간: HH:mi:ss.")
		private String signTime = null;

		@FieldDescribe("打卡操作名称：上班打卡|下班打卡|午休打卡|上午下班打卡|下午上班打卡(根据不同的打卡策略稍有不同)....")
		private String checkinType = "上班打卡";

		@FieldDescribe("최종 기록 시간")
		private Date latestSignTime;

		public String getSignTime() { return signTime; }

		public void setSignTime(String signTime) { this.signTime = signTime; }

		public Integer getSignSeq() { return signSeq; }

		public void setSignSeq(Integer signSeq) { this.signSeq = signSeq; }

		public String getSignDate() { return signDate; }

		public void setSignDate(String signDate) { this.signDate = signDate; }

		public String getCheckinType() { return checkinType; }

		public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

		public Date getLatestSignTime() { return latestSignTime; }

		public void setLatestSignTime(Date latestSignTime) { this.latestSignTime = latestSignTime; }

	}


	/**
	 * 다음 기록이 어떤 기록인지 계산
	 * 3-4회 기록(오전·오후 모두 출근·퇴근 기록)
	 *
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSignFeature getWoSignFeatureWithProxy3(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSignFeature woSignFeature = new WoSignFeature();
		Date now = new Date();
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = null;

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

		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//한 번도 기록하지 않았으면 현재 시간이 오전인지 오후인지 확인, 오전이면 오전 출근 체크, 오후면 오후 출근 체크
			if( now.after(afternoonOndutyTime)){
				//오후 퇴근 시간 이후, 오후 출근 체크 기록
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}else{
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}
		}else{
			woSignFeature.setSignSeq(2);
			//현재 구간
			if( onDutyTime.after(now)){
				//오전 출근 전에는 횟수와 관계없이 오전 퇴근 기록만 가능
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
				woSignFeature.setSignSeq(2);
			}else if( now.after(onDutyTime) && morningOffdutyTime.after(now)){
				//오전 근무 시간대: 오전 퇴근 체크
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
			}else if( now.after(morningOffdutyTime) && afternoonOndutyTime.after( now )){
				//점심 휴식 시간대: 이전 기록은 오전 출근 체크일 수도, 오후 출근 체크일 수도 있음
				if( signRecordStatus.alreadyOnduty ){ //이미 오전 출근 체크함, 기록이 1회뿐이면 출근 체크
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_MORNING_OFFDUTY );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestStartTime());
				}else if( signRecordStatus.alreadyAfternoonOnDuty){
					//오전 출근 체크 없이 오후 출근 체크였다면 두 번째는 오후 퇴근 기록
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}
			}else if( now.after(afternoonOndutyTime) && offDutyTime.after(now)){
				//오후 근무 시간대: 이전이 오후 출근 체크면 다음은 오후 퇴근 체크, 아니면 오후 출근 체크
				if( signRecordStatus.alreadyAfternoonOnDuty){
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}
			}else{
				//오후 퇴근 이후에는 오후 출근·퇴근 체크 기록만 가능
				if( signRecordStatus.alreadyAfternoonOnDuty){
					woSignFeature.setSignSeq(-1);
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
				}else{
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON_ONDUTY  );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}
			}
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
	protected WoSignFeature getWoSignFeatureWithProxy2(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) throws Exception {
		DateOperation dateOperation = new DateOperation();
		WoSignFeature woSignFeature = new WoSignFeature();
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = null;

		Date now = new Date();
		Date middayRestEndTime = null;
		Date onDutyTime = null, offDutyTime = null;
		Date morningOffdutyTime = null, afternoonOndutyTime = null;
		String todayDateStr = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");

		//출근·퇴근 시간 계산
		if( StringUtils.isNotEmpty( scheduleSetting.getOnDutyTime())) {
			onDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOnDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getOffDutyTime())) {
			offDutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getOffDutyTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestStartTime())) {
			morningOffdutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestStartTime() );
		}
		if( StringUtils.isNotEmpty( scheduleSetting.getMiddayRestEndTime())) {
			afternoonOndutyTime = dateOperation.getDateFromString( todayDateStr + " " + scheduleSetting.getMiddayRestEndTime() );
		}
		signRecordStatus = getSignRecordStatus( wraps, onDutyTime, morningOffdutyTime, afternoonOndutyTime, offDutyTime );

		if( ListTools.isEmpty( wraps )){
			//한 번도 기록하지 않았으면 출근 기록만 가능
			if( woSignFeature.getSignSeq() == 0 ){
				woSignFeature.setSignSeq(1);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
			}
		}else{
			//1회 기록 후에는 정오 출근 체크 기록 또는 오후 퇴근 체크 기록 가능
			woSignFeature.setSignSeq( wraps.size() + 1 );
			//점심 휴식 또는 퇴근 기록만 가능, 출근 기록은 불가
			//현재 시간이 정오 체크 시간을 지났는지 확인
			if( morningOffdutyTime !=null && morningOffdutyTime.before( now ) ){//현재는 점심 휴식 이후
				//퇴근했는지 확인, 퇴근했으면 바로 퇴근 기록, 아니면 점심 휴식 기록도 가능
				if( offDutyTime !=null && offDutyTime.before( now ) ){
					//퇴근함, 퇴근 기록만 가능
					if( !signRecordStatus.alreadyOffDuty ){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						//퇴근 기록 완료, 더 기록할 필요 없음
						woSignFeature.setSignSeq(-1);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}
				}else{
					//아직 오후가 아님, 점심 휴식 기록 가능, 이미 했는지 확인
					if( !signRecordStatus.alreadyAfternoon ){
						//점심 휴식 기록이 없으므로 점심 휴식 기록 가능
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
						woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
					}else{
						//점심 휴식 기록 완료, 퇴근 기록 여부 확인, 없으면 퇴근 기록 가능
						if( !signRecordStatus.alreadyOffDuty ){
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
							woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
						}else{
							//퇴근 기록 완료, 더 기록할 필요 없음
							woSignFeature.setSignSeq(-1);
							woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
							woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
						}
					}
				}
			}else{
				//현재는 점심 휴식 이전, 출근 기록 또는 점심 휴식 기록인데 이미 1회 기록했으므로 출근 기록은 불가, 점심 휴식 기록 여부 확인
				if( !signRecordStatus.alreadyAfternoon ){
					//점심 휴식 기록이 없으므로 점심 휴식 기록 가능
					woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_AFTERNOON );
					woSignFeature.setSignTime(scheduleSetting.getMiddayRestEndTime());
				}else{
					//점심 휴식 기록 완료, 퇴근 기록 여부 확인, 없으면 퇴근 기록 가능
					if( !signRecordStatus.alreadyOffDuty ){
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}else{
						//퇴근 기록 완료, 더 기록할 필요 없음
						woSignFeature.setSignSeq(-1);
						woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
						woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
					}
				}
			}
		}

		return woSignFeature;
	}

	/**
	 * 다음 기록이 어떤 기록인지 계산
	 * 1-2회 기록(오전 출근, 오후 퇴근)
	 * @param wraps
	 * @param scheduleSetting
	 * @return
	 */
	protected WoSignFeature getWoSignFeatureWithProxy1(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, AttendanceScheduleSetting scheduleSetting) {
		WoSignFeature woSignFeature = new WoSignFeature();
		if( ListTools.isEmpty( wraps )){
			//한 번도 기록하지 않았으면 시간과 관계없이 출근 기록
			woSignFeature.setSignSeq(1);
			woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_ONDUTY );
			woSignFeature.setSignTime(scheduleSetting.getOnDutyTime());
		}else{
			//1회 기록했으면 퇴근 기록, 2회 기록했으면 없음
			if( wraps.size() == 1 ){
				woSignFeature.setSignSeq(2);
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
			}else{
				woSignFeature.setSignSeq(-1); //필요한 기록 없음
				woSignFeature.setCheckinType( AttendanceDetailMobile.CHECKIN_TYPE_OFFDUTY );
				woSignFeature.setSignTime(scheduleSetting.getOffDutyTime());
			}
		}
		return woSignFeature;
	}

	private ActionListMyMobileRecordToday.SignRecordStatus getSignRecordStatus(List<ActionListMyMobileRecordToday.WoMobileRecord> wraps, Date onDutyTime, Date morningOffdutyTime, Date afternoonOndutyTime, Date offDutyTime) {
		ActionListMyMobileRecordToday.SignRecordStatus signRecordStatus = new ActionListMyMobileRecordToday.SignRecordStatus();
		if( ListTools.isNotEmpty( wraps )){
			for( ActionListMyMobileRecordToday.WoMobileRecord record : wraps ){
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

	public static class SignRecordStatus {

		@FieldDescribe("是否上午上班打卡过了ONDUTY")
		private Boolean alreadyOnduty = false;

		@FieldDescribe("오전 퇴근 기록 MORNING_OFFDUTY 여부.")
		private Boolean alreadyMorningOffDuty = false;

		@FieldDescribe("是否下午上班打卡过了AFTERNOON_ONDUTY")
		private Boolean alreadyAfternoonOnDuty = false;

		@FieldDescribe("오후 퇴근 기록 OFFDUTY 여부.")
		private Boolean alreadyOffDuty = false;

		@FieldDescribe("정오 기록 AfternoonSign 여부.")
		private Boolean alreadyAfternoon = false;

		public Boolean getAlreadyOnduty() { return alreadyOnduty; }

		public void setAlreadyOnduty(Boolean alreadyOnduty) { this.alreadyOnduty = alreadyOnduty; }

		public Boolean getAlreadyMorningOffDuty() { return alreadyMorningOffDuty; }

		public void setAlreadyMorningOffDuty(Boolean alreadyMorningOffDuty) { this.alreadyMorningOffDuty = alreadyMorningOffDuty; }

		public Boolean getAlreadyAfternoonOnDuty() { return alreadyAfternoonOnDuty; }

		public void setAlreadyAfternoonOnDuty(Boolean alreadyAfternoonOnDuty) { this.alreadyAfternoonOnDuty = alreadyAfternoonOnDuty; }

		public Boolean getAlreadyOffDuty() { return alreadyOffDuty; }

		public void setAlreadyOffDuty(Boolean alreadyOffDuty) { this.alreadyOffDuty = alreadyOffDuty; }

		public Boolean getAlreadyAfternoon() { return alreadyAfternoon; }

		public void setAlreadyAfternoon(Boolean alreadyAfternoon) { this.alreadyAfternoon = alreadyAfternoon; }
	}

	public static class WoMobileRecord extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, WoMobileRecord> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				WoMobileRecord.class, null, JpaObject.FieldsInvisible);
	}

	public static class WoAttendanceAppealInfo extends AttendanceAppealInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe("근태 이의 신청 내용")
		private WoAttendanceAppealAuditInfo appealAuditInfo = null;

		public WoAttendanceAppealAuditInfo getAppealAuditInfo() { return appealAuditInfo; }

		public void setAppealAuditInfo(WoAttendanceAppealAuditInfo appealAuditInfo) { this.appealAuditInfo = appealAuditInfo; }

		public static WrapCopier<AttendanceAppealInfo, WoAttendanceAppealInfo> copier = WrapCopierFactory.wo(AttendanceAppealInfo.class,
				WoAttendanceAppealInfo.class, null, JpaObject.FieldsInvisible);

	}

	public static class WoAttendanceAppealAuditInfo extends AttendanceAppealAuditInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealAuditInfo, WoAttendanceAppealAuditInfo> copier = WrapCopierFactory.wo(AttendanceAppealAuditInfo.class,
				WoAttendanceAppealAuditInfo.class, null, JpaObject.FieldsInvisible);

	}
}
