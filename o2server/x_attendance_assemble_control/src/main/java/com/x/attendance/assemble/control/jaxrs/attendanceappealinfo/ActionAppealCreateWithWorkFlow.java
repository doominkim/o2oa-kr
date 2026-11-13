package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealCreateWithWorkFlow extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealCreateWithWorkFlow.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceDetail attendanceDetail = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
			if( StringUtils.isEmpty( wrapIn.getWorkId() )){
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess("결재로 이의 신청을 시작할 때 workId는 비워 둘 수 없습니다");
				result.error(exception);
			}
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		//전달된 근태 정보 ID로 근태 정보 객체 조회
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
				if ( attendanceDetail == null ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailNotExists(id);
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "ID로 직원 체크 정보를 조회하는 중 오류가 발생했습니다! ID:"+id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 출퇴근 기록 정보로 이의 신청 정보 기록 생성
			attendanceAppealInfo = attendanceSettingServiceAdv.composeAppealInfoWithDetailInfo( attendanceDetail, 
					wrapIn.getReason(), wrapIn.getAppealReason(),  wrapIn.getSelfHolidayType(),  wrapIn.getAddress(), 
					wrapIn.getStartTime(),  wrapIn.getEndTime(),  wrapIn.getAppealDescription() );

//			// 이의 신청 상태: 0-미신청, 1-신청 중, -1-신청 반려, 9-신청 승인!
//			attendanceAppealInfo.setStatus(wrapIn.getStatus());

			// 이의 신청 승인 기록 정보 생성
			attendanceAppealAuditInfo = new AttendanceAppealAuditInfo();
			attendanceAppealAuditInfo.setId( attendanceAppealInfo.getId());
			attendanceAppealAuditInfo.setDetailId( attendanceDetail.getId() );
			attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW );
			attendanceAppealAuditInfo.setWorkId( wrapIn.getWorkId());
		}

		//이의 신청 정보 저장
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo, attendanceAppealAuditInfo );

				result.setData(new Wo(attendanceAppealInfo.getId()));
			} catch (Exception e) {
				Exception exception = new ExceptionAttendanceAppealProcess(e, "근태 이의 신청 정보 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("이의 신청자 신분, 근태 대상자 신분: 근태 대상자가 여러 조직에 속하면 신분 하나를 골라 이의 신청 정보에 연결할 수 있음.")
		private String identity = null;

		@FieldDescribe("근태 프로세스의 workId.")
		private String workId = null;

		@FieldDescribe("이의 신청 시작 시간")
		private String startTime;

		@FieldDescribe("이의 신청 종료 시간")
		private String endTime;

		@FieldDescribe("이의 신청 사유 요약(60자)")
		private String appealReason;

		@FieldDescribe("휴가 유형")
		private String selfHolidayType;

		@FieldDescribe("주소")
		private String address;

		@FieldDescribe("이의 신청 상세 사유, 500자")
		private String reason;

		@FieldDescribe("이의 신청 상세 설명, 500자")
		private String appealDescription;

//		@FieldDescribe("승인 상태: 이의 신청 상태: 0-미신청, 1-신청 중, -1-신청 반려, 9-신청 승인")
//		private Integer status = 0;

		public String getWorkId() { return workId; }

		public void setWorkId(String workId) { this.workId = workId; }

		public String getIdentity() { return identity; }

		public void setIdentity(String identity) { this.identity = identity; }

		public String getStartTime() { return startTime; }

		public void setStartTime(String startTime) { this.startTime = startTime; }

		public String getEndTime() { return endTime; }

		public void setEndTime(String endTime) { this.endTime = endTime; }

		public String getAppealReason() { return appealReason; }

		public void setAppealReason(String appealReason) { this.appealReason = appealReason; }

		public String getSelfHolidayType() { return selfHolidayType; }

		public void setSelfHolidayType(String selfHolidayType) { this.selfHolidayType = selfHolidayType; }

		public String getAddress() { return address; }

		public void setAddress(String address) { this.address = address; }

		public String getReason() { return reason; }

		public void setReason(String reason) { this.reason = reason; }

		public String getAppealDescription() { return appealDescription; }

		public void setAppealDescription(String appealDescription) { this.appealDescription = appealDescription; }
//
//		public Integer getStatus() { return status; }
//
//		public void setStatus(Integer status) { this.status = status; }
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}