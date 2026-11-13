package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;

public class ActionReciveSingleAttendance extends BaseAction {
	/*
	 * =============================================================================
	 * =====출퇴근 데이터 부분 수정용, 예: 오전 기록에 영향 없이 오후 기록만 수정
	 * =============================================================================
	 * =====
	 */
	
	private static  Logger logger = LoggerFactory.getLogger( ActionReciveSingleAttendance.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		DateOperation dateOperation = new DateOperation();
		AttendanceDetail attendanceDetail = new AttendanceDetail();
		Boolean check = true;
		List<String> ids = null;
		Wi wrapIn = null;
		Date datetime = null;
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			if (wrapIn.getRecordDateString() == null || wrapIn.getRecordDateString().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRecordDateEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getEmpName() == null || wrapIn.getEmpName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionPersonNameEmpty();
				result.error(exception);
			}
		}
		
		/*if (check) {
			try {
				ids = attendanceDetailServiceAdv.listDetailByNameAndDate( wrapIn.getEmpName(), wrapIn.getRecordDateString() );
				if(ListTools.isNotEmpty(ids)){
					attendanceDetail = attendanceDetailServiceAdv.get(ids.get(0));
					if(attendanceDetail==null){
						check = false;
						Exception exception = new ExceptionDetailNotExists(ids.get(0));
						result.error(exception);
					}
				}else{
					attendanceDetail = new AttendanceDetail();
					
					check = false;
					Exception exception = new ExceptionSingleDetailNotExists();
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"시스템이 직원 이름, 기록 날짜로 출퇴근 상세 정보 ID 목록을 조회하는 중 예외가 발생했습니다!"
						+ "Name:" + wrapIn.getEmpName() + ", RecordDate:" + wrapIn.getRecordDateString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
			
			
		}*/
		
		if (check) {
			try {
				datetime = dateOperation.getDateFromString(wrapIn.getRecordDateString());
				attendanceDetail.setRecordDate(datetime);
				attendanceDetail.setRecordDateString(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD"));
				attendanceDetail.setYearString(dateOperation.getYear(datetime));
				attendanceDetail.setMonthString(dateOperation.getMonth(datetime));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 체크 정보의 체크 날짜 형식이 올바르지 않습니다. 형식: yyyy-mm-dd. 날짜:" + wrapIn.getRecordDateString());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			if (wrapIn.getOnDutyTime() != null && wrapIn.getOnDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getOnDutyTime());
					attendanceDetail.setOnDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 오전 출근 기록 시간
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 오전 출근 체크 시간 형식이 올바르지 않습니다. 형식: HH:mm:ss. 날짜:" + wrapIn.getOnDutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			if (wrapIn.getMorningOffdutyTime() != null && wrapIn.getMorningOffdutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getMorningOffdutyTime());
					attendanceDetail.setMorningOffDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 오전 퇴근 기록 시간
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 오전 퇴근 체크 시간 형식이 올바르지 않습니다. 형식: HH:mm:ss. 날짜:" + wrapIn.getMorningOffdutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			if (wrapIn.getAfternoonOnDutyTime() != null && wrapIn.getAfternoonOnDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getAfternoonOnDutyTime());
					attendanceDetail.setAfternoonOnDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 오후 출근 기록 시간
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 오후 출근 체크 시간 형식이 올바르지 않습니다. 형식: HH:mm:ss. 날짜:" + wrapIn.getAfternoonOnDutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) {
			if (wrapIn.getOffDutyTime() != null && wrapIn.getOffDutyTime().trim().length() > 0) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getOffDutyTime());
					attendanceDetail.setOffDutyTime(dateOperation.getDateStringFromDate(datetime, "HH:mm:ss")); // 오후 퇴근 기록 시간
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 퇴근 체크 시간 형식이 올바르지 않습니다. 형식: HH:mm:ss. 날짜:" + wrapIn.getOffDutyTime());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if( check ){
			String distinguishedName = wrapIn.getEmpName();
			if( StringUtils.isEmpty( distinguishedName )){
				distinguishedName = effectivePerson.getDistinguishedName();
			}

			Person person = userManagerService.getPersonObjByName( distinguishedName );

			if( person != null ){
				attendanceDetail.setEmpName( person.getDistinguishedName() );
				if( StringUtils.isEmpty( wrapIn.getEmpNo() )){
					if( person != null ){
						if( StringUtils.isNotEmpty( person.getEmployee() )){
							attendanceDetail.setEmpNo(person.getEmployee());
						}else{
							attendanceDetail.setEmpNo( distinguishedName );
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

		if (check) {
			try {
				
				attendanceDetail = attendanceDetailServiceAdv.saveSingle(attendanceDetail);
				result.setData( new Wo( attendanceDetail.getId() ));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "직원 체크 정보 저장 중 오류가 발생했습니다." );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			//저장된 근태 데이터 분석
			try {
//				ThisApplication.detailAnalyseQueue.send( attendanceDetail.getId() );
			} catch ( Exception e1 ) {
				e1.printStackTrace();
			}
		}


		return result;
	}
	
	public static class Wi {

		@FieldDescribe( "직원 식별자, <font color='red'>필수</font>, 직원의 distinguishedName." )
		private String empName = null;

		private String empNo = null;

		@FieldDescribe( "기록 날짜, 형식 yyyy-mm-dd, 비어 있어도 됨." )
		private String recordDateString = null;

		@FieldDescribe( "오전 출근 기록 시간, 형식 hh24:mi:ss, 비어 있어도 됨, 비어 있으면 미기록." )
		private String onDutyTime = null;

		@FieldDescribe("오전 근무 퇴근 체크 시간, 기록 정책 2와 3에서 사용, 형식 hh24:mi:ss, 비어 있어도 됨, 비어 있으면 미기록.")
		private String morningOffdutyTime;

		@FieldDescribe("오후 출근 체크 시간, 기록 정책 2와 3에서 사용, 형식 hh24:mi:ss, 비어 있어도 됨, 비어 있으면 미기록.")
		private String afternoonOnDutyTime;

		@FieldDescribe( "오후 퇴근 기록 시간, hh24:mi:ss, 비어 있어도 됨, 비어 있으면 미기록." )
		private String offDutyTime = null;

		public String getMorningOffdutyTime() { return morningOffdutyTime; }
		public void setMorningOffdutyTime(String morningOffdutyTime) { this.morningOffdutyTime = morningOffdutyTime; }
		public String getAfternoonOnDutyTime() { return afternoonOnDutyTime; }
		public void setAfternoonOnDutyTime(String afternoonOnDutyTime) { this.afternoonOnDutyTime = afternoonOnDutyTime; }
		public String getEmpName() {
			return empName;
		}
		public void setEmpName(String empName) {
			this.empName = empName;
		}
		public String getEmpNo() {
			return empNo;
		}
		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}
		public String getRecordDateString() {
			return recordDateString;
		}
		public void setRecordDateString(String recordDateString) {
			this.recordDateString = recordDateString;
		}
		public String getOnDutyTime() {
			return onDutyTime;
		}
		public void setOnDutyTime(String onDutyTime) {
			this.onDutyTime = onDutyTime;
		}
		public String getOffDutyTime() {
			return offDutyTime;
		}
		public void setOffDutyTime(String offDutyTime) {
			this.offDutyTime = offDutyTime;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}