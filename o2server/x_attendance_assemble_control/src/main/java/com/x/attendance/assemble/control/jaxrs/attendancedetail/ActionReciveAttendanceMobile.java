package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.v2.AttendanceV2Config;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;

public class ActionReciveAttendanceMobile extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveAttendanceMobile.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		EffectivePerson currentPerson = this.effectivePerson( request );
		AttendanceDetailMobile attendanceDetailMobile = new AttendanceDetailMobile();
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		List<WoMobileRecord> wraps = new ArrayList<>();
		Wi wrapIn = null;
		Boolean check = true;
		Date now = null;
		String signDate = null;
		String signTime = null;
		
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

		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
			//1000000000000L = Sun Sep 09 2001 09:46:40 GMT+0800 (중국 표준시)
			if( wrapIn.getCheckin_time() > 1000000000000L  ){
				now = new Date( wrapIn.getCheckin_time() );
			}else{
				now = new Date();
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if( check ){
			try {
				signDate = dateOperation.getDateStringFromDate( now, "YYYY-MM-DD");
				signTime = dateOperation.getDateStringFromDate( now, "HH:mm:ss");

				attendanceDetailMobile.setRecordDateString( signDate ); //체크 날짜
				attendanceDetailMobile.setSignTime( signTime ); //체크 시간
				attendanceDetailMobile.setCheckin_time( now.getTime() );
				attendanceDetailMobile.setRecordDate( now );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWrapInConvert(e, jsonElement);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if( check ){
			if (StringUtils.isNotEmpty(wrapIn.getDescription())) {
				attendanceDetailMobile.setDescription(wrapIn.getDescription());
			}
			if( StringUtils.isNotEmpty(wrapIn.getRecordAddress()) ){
				attendanceDetailMobile.setRecordAddress( wrapIn.getRecordAddress() );
			}
			if( StringUtils.isNotEmpty(wrapIn.getWorkAddress()) ){
				attendanceDetailMobile.setWorkAddress( wrapIn.getWorkAddress() );
			}
			if( StringUtils.isNotEmpty(wrapIn.getOptMachineType()) ){
				attendanceDetailMobile.setOptMachineType( wrapIn.getOptMachineType() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLatitude())){
				attendanceDetailMobile.setLatitude( wrapIn.getLatitude() );
			}
		}
		if( check ){
			if( StringUtils.isNotEmpty(wrapIn.getLongitude())){
				attendanceDetailMobile.setLongitude( wrapIn.getLongitude() );
			}
		}
		if( check ){
			//범위 밖 기록 여부, 기본값 아니요
			if( wrapIn.getIsExternal() ){
				attendanceDetailMobile.setIsExternal(true);
			}else{
				attendanceDetailMobile.setIsExternal(false);
			}
		}
		if( check ){
			String distinguishedName = wrapIn.getEmpName();
			if( StringUtils.isEmpty( distinguishedName )){
				distinguishedName = currentPerson.getDistinguishedName();
			}

			Person person = userManagerService.getPersonObjByName( distinguishedName );

			if( person != null ){
				attendanceDetailMobile.setEmpName( person.getDistinguishedName() );
				if( StringUtils.isEmpty( wrapIn.getEmpNo() )){
					if( person != null ){
						if( StringUtils.isNotEmpty( person.getEmployee() )){
							attendanceDetailMobile.setEmpNo(person.getEmployee());
						}else{
							attendanceDetailMobile.setEmpNo( distinguishedName );
						}
					}
				}
			}else{
				//사용자가 존재하지 않습니다
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(
						"근태 대상자가 없습니다. DistinguishedName:" + distinguishedName );
				result.error(exception);
			}
		}

		//현재 기록의 checking_type 계산
		//먼저 해당 직원의 모든 근태 데이터 조회
		if (check) {
			try {
				attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobile( attendanceDetailMobile.getEmpName(), signDate );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"조건으로 직원 모바일 체크 정보 목록을 조회하는 중 오류가 발생했습니다. DistinguishedName:" + attendanceDetailMobile.getEmpName() + ",Date:" + signDate);
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
			AttendanceScheduleSetting scheduleSetting = null;
			//기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근 기록)
			scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithPerson( attendanceDetailMobile.getEmpName(), effectivePerson.getDebugger() );
			if( scheduleSetting != null ){
				if( scheduleSetting.getSignProxy() == 3 ){
					//3-4회 기록(오전·오후 모두 출근·퇴근 기록)
					woSignFeature = getWoSignFeatureWithProxy3(wraps, scheduleSetting);
				}else if( scheduleSetting.getSignProxy() == 2 ){
					//2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회)
					woSignFeature = getWoSignFeatureWithProxy2(wraps, scheduleSetting);
				}else{
					//1-2회 기록(오전 출근, 오후 퇴근)
					woSignFeature = getWoSignFeatureWithProxy1(wraps, scheduleSetting);
				}
			}
			if( woSignFeature != null ){
				woSignFeature.setSignDate( signDate );
			}

			//attendanceDetailMobile.setCheckin_type( woSignFeature.getCheckinType() );
			attendanceDetailMobile.setCheckin_type(wrapIn.getCheckin_type());
			if( StringUtils.isEmpty( wrapIn.getSignDescription() )){
				attendanceDetailMobile.setSignDescription( wrapIn.getCheckin_type() );
			}else{
				attendanceDetailMobile.setSignDescription( wrapIn.getSignDescription() );
			}
		}

		if( check ){
			if( StringUtils.isNotEmpty( wrapIn.getId() )){
				attendanceDetailMobile.setId( wrapIn.getId() );
			}
			try {
				attendanceDetailMobile = attendanceDetailServiceAdv.save( attendanceDetailMobile );
				result.setData( new Wo( attendanceDetailMobile.getId() ) );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "직원 모바일 체크 정보 저장 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}

		if( check ){
			//해당 직원의 모든 모바일 근태 데이터를 통합해 즉시 분석 대기열로 send
			attendanceDetailServiceAdv.pushToDetail( attendanceDetailMobile.getEmpName(), attendanceDetailMobile.getRecordDateString(), effectivePerson.getDebugger() );
		}
		return result;
	}
	
	public static class Wi {
		
		@FieldDescribe( "Id, 비어 있어도 됨, ID가 중복이면 기존 데이터 수정." )
		private String id;
		
//		@FieldDescribe( "사번, 비어 있어도 됨, 비어 있으면 empName과 동일." )
		private String empNo;

		@FieldDescribe( "직원 식별자, 비어 있어도 되지만 틀리면 안 됨: DistinguishedName, 비어 있으면 현재 로그인 사용자 사용." )
		private String empName;

//		@FieldDescribe( "출퇴근 기록 날짜 문자열: yyyy-mm-dd, 필수 입력." )
		private String recordDateString;

		@FieldDescribe("기록 유형. 문자열, 현재: 오전 출근 기록, 오전 퇴근 기록, 오후 출근 기록, 오후 퇴근 기록, 외출 기록, 정오 기록")
		private String checkin_type;

		@FieldDescribe("기록 시간, 비어 있어도 됨, 비어 있으면 서버 현재 시간 사용. Unix 타임스탬프")
		private long checkin_time;

//		@FieldDescribe( "기록 시간: hh24:mi:ss, 필수 입력." )
		private String signTime;

//		@FieldDescribe( "출퇴근 기록 설명: 출근 기록, 퇴근 기록, 비어 있어도 됨." )
		private String signDescription;

		@FieldDescribe( "其他说明备注, 可以为空." )
		private String description;

		@FieldDescribe( "기록 장소 설명, 비어 있어도 됨." )
		private String recordAddress = "未知";
		
		@FieldDescribe( "경도, 비어 있어도 됨." )
		private String longitude;

		@FieldDescribe( "위도, 비어 있어도 됨." )
		private String latitude;

		@FieldDescribe( "操作设备类别：手机品牌|PAD|PC|其他, 可以为空." )
		private String optMachineType = "其他";

		@FieldDescribe( "操作设备类别：Mac|Windows|IOS|Android|其他, 可以为空." )
		private String optSystemName = "其他";

		@FieldDescribe( "근무지 설명, 비어 있어도 됨." )
		private String workAddress = "";

		@FieldDescribe("범위 밖 기록 여부")
		private Boolean isExternal = false;

		public String getRecordDateString() {
			return recordDateString;
		}
		public void setRecordDateString(String recordDateString) {
			this.recordDateString = recordDateString;
		}
		public String getSignDescription() {
			return signDescription;
		}
		public void setSignDescription(String signDescription) {
			this.signDescription = signDescription;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getRecordAddress() {
			return recordAddress;
		}
		public void setRecordAddress(String recordAddress) {
			this.recordAddress = recordAddress;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getOptMachineType() {
			return optMachineType;
		}
		public void setOptMachineType(String optMachineType) {
			this.optMachineType = optMachineType;
		}
		public String getOptSystemName() {
			return optSystemName;
		}
		public void setOptSystemName(String optSystemName) {
			this.optSystemName = optSystemName;
		}
		public String getEmpNo() {
			return empNo;
		}
		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
		public String getEmpName() {
			return empName;
		}
		public void setEmpName(String empName) {
			this.empName = empName;
		}
		public String getSignTime() {
			return signTime;
		}
		public void setSignTime(String signTime) {
			this.signTime = signTime;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCheckin_type() { return checkin_type; }
		public void setCheckin_type(String checkin_type) { this.checkin_type = checkin_type; }
		public long getCheckin_time() { return checkin_time; }
		public void setCheckin_time(long checkin_time) { this.checkin_time = checkin_time; }
		public String getWorkAddress() {
			return workAddress;
		}
		public void setWorkAddress(String workAddress) {
			this.workAddress = workAddress;
		}
		public Boolean getIsExternal() {
			return isExternal;
		}
		public void setIsExternal(Boolean isExternal) {
			this.isExternal = isExternal;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}