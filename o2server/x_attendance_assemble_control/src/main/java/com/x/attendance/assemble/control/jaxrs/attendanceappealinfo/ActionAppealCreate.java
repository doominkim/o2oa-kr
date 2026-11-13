package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AppealConfig;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealCreate extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealCreate.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceAppealInfo attendanceAppealInfo = null;
		AttendanceAppealAuditInfo attendanceAppealAuditInfo = null;
		AttendanceDetail attendanceDetail = null;
		String appealAuditPersonName = null;
		String appealCheckPersonName = null;
		String appeal_auditor_type = null;
		String appeal_auditor_value = null;
		String appeal_checker_type = null;
		String appeal_checker_value = null;
		String appeal_flowType_value = null;
		String personName = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				attendanceDetail = attendanceDetailServiceAdv.get( id );
				if ( attendanceDetail == null ) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailNotExists(id);
					result.error( exception );
				}else {
					personName = attendanceDetail.getEmpName();
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "ID로 직원 체크 정보를 조회하는 중 오류가 발생했습니다! ID:"+id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			try {
				appeal_auditor_type = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDITOR_TYPE" );
				appeal_auditor_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDITOR_VALUE" );
				appeal_checker_type = attendanceSettingServiceAdv.getValueByCode( "APPEAL_CHECKER_TYPE" );
				appeal_checker_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_CHECKER_VALUE" );
				appeal_flowType_value = attendanceSettingServiceAdv.getValueByCode( "APPEAL_AUDIFLOWTYPE" );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess( e, "이의 신청 심사 설정을 가져오는 중 오류가 발생했습니다!" );
				result.error( exception);
				logger.error( e, effectivePerson, request, null);
			}
		}

		if (check) {
			// 출퇴근 기록 정보로 이의 신청 정보 기록 생성
			attendanceAppealInfo = attendanceSettingServiceAdv.composeAppealInfoWithDetailInfo( attendanceDetail, 
					wrapIn.getReason(), wrapIn.getAppealReason(),  wrapIn.getSelfHolidayType(),  wrapIn.getAddress(), 
					wrapIn.getStartTime(),  wrapIn.getEndTime(),  wrapIn.getAppealDescription() );
			// 이의 신청 승인 기록 정보 생성
			attendanceAppealAuditInfo = new AttendanceAppealAuditInfo();
			attendanceAppealAuditInfo.setId( attendanceAppealInfo.getId());
			attendanceAppealAuditInfo.setDetailId( attendanceDetail.getId() );
		}

		
		//이의 신청 심사자 조회
		if (check) {
			if( StringUtils.isNotEmpty( appeal_auditor_type ) ) {
				try {
					appealAuditPersonName = attendanceAppealInfoServiceAdv.getAppealAuditPerson( personName, attendanceAppealInfo.getUnitName(), wrapIn.getIdentity() );
					if( StringUtils.isNotEmpty( appealAuditPersonName )){
						if(StringUtils.indexOf(appealAuditPersonName, "@I")>0){
							appealAuditPersonName = userManagerService.getPersonNameByIdentity(appealAuditPersonName);
						}
						attendanceAppealAuditInfo.setProcessPerson1( appealAuditPersonName );
						attendanceAppealAuditInfo.setCurrentProcessor( appealAuditPersonName );
						attendanceAppealInfo.setCurrentProcessor( appealAuditPersonName );// 첫 번째 처리자를 현재 처리자로 설정
						attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_BUILTIN );
					}

				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "근태 대상자로 이의 신청 심사자 정보를 조회하는 중 오류가 발생했습니다! personName:"+personName );
					result.error(exception);
					logger.warn( "근태 대상자로 이의 신청 심사자 정보를 조회하는 중 오류가 발생했습니다! personName:"+personName );
					logger.error(e, effectivePerson, request, null);
				}
			}
			if(StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)){
				attendanceAppealAuditInfo.setAuditFlowType( AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW );
				attendanceAppealAuditInfo.setCurrentProcessor( "" );
				attendanceAppealInfo.setCurrentProcessor( "" );
				if(StringUtils.isNotEmpty(wrapIn.getUnitName())){
					attendanceAppealInfo.setUnitName(wrapIn.getUnitName());
					attendanceAppealAuditInfo.setProcessPersonUnit1(wrapIn.getUnitName());
					attendanceAppealAuditInfo.setProcessPersonUnit2(wrapIn.getUnitName());
				}
				if(StringUtils.isNotEmpty(wrapIn.getWorkId())){
					attendanceAppealAuditInfo.setWorkId(wrapIn.getWorkId());
				}
			}
		}
		
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isEmpty(appealAuditPersonName) ) {
				//이의 신청 심사자가 존재하지 않습니다
				check = false;
				String message = null;
				String unitLevelName = null;
				if( StringUtils.isEmpty(appeal_auditor_type)) {
					message = "소명 심사자 유형이 설정되지 않았습니다!";
				}else if( "없음".equals( appeal_auditor_type.trim() )){//현재 사용자 처리
					attendanceAppealAuditInfo.setProcessPerson1( effectivePerson.getDistinguishedName() );
					attendanceAppealAuditInfo.setCurrentProcessor( effectivePerson.getDistinguishedName() );// 첫 번째 처리자를 현재 처리자로 설정
					attendanceAppealInfo.setCurrentProcessor( effectivePerson.getDistinguishedName() );// 첫 번째 처리자를 현재 처리자로 설정
				}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( appeal_auditor_type )){
					unitLevelName = userManagerService.getUnitLevelNameWithName( attendanceDetail.getUnitName() );
					message = personName + "의 소속 부서["+unitLevelName+"] 직무["+appeal_auditor_value+"]가 없습니다!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( appeal_auditor_type )){
					message = personName + "의 [보고 대상]이 없습니다!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( appeal_auditor_type )){
					message = personName + "의 개인 속성["+appeal_auditor_value+"]가 없습니다!";
				}else if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( appeal_auditor_type )){
					message = "지정 심사자["+appeal_auditor_value+"]가 없습니다!";
				}else {
					message = "근태 소명 심사자 유형이 올바르지 않습니다! " + appeal_auditor_type ;
				}
				Exception exception = new ExceptionAttendanceAppealProcess( message );
				result.error( exception );
			}
		}
		
		//이의 신청 재검토자 조회
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( StringUtils.isNotEmpty( appeal_checker_type )   && !"없음".equals( appeal_checker_type  ) ) {
				try {
					appealCheckPersonName = attendanceAppealInfoServiceAdv.getAppealCheckPerson( personName, attendanceAppealInfo.getUnitName(), wrapIn.getIdentity() );
					attendanceAppealAuditInfo.setProcessPerson2( appealCheckPersonName );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "근태 대상자로 이의 신청 재검토자 정보를 조회하는 중 오류가 발생했습니다! personName:"+personName );
					result.error(exception);
					logger.warn( "근태 대상자로 이의 신청 재검토자 정보를 조회하는 중 오류가 발생했습니다! personName:"+personName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( StringUtils.isNotEmpty( appeal_checker_type )  && !StringUtils.equals( appeal_checker_type, "없음" ) ) {
				if( StringUtils.isEmpty(appealCheckPersonName) ) {
					//이의 신청 재검토자가 존재하지 않습니다
					check = false;
					String message = null;
					String unitLevelName = null;
					if( StringUtils.isEmpty( appeal_checker_type )) {
						message = "소명 재심사자 유형이 설정되지 않았습니다!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equals( appeal_checker_type )){
						unitLevelName = userManagerService.getUnitLevelNameWithName( attendanceDetail.getUnitName() );
						message = personName + "의 소속 부서["+unitLevelName+"] 직무["+ appeal_checker_value +"]가 없습니다!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_REPORTLEADER.equals( appeal_checker_type )){
						message = personName + "의 [보고 대상]이 없습니다!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_PERSONATTRIBUTE.equals( appeal_checker_type )){
						message = personName + "의 개인 속성["+appeal_checker_value+"]가 없습니다!";
					}else if( AppealConfig.APPEAL_AUDITTYPE_PERSON.equals( appeal_checker_type )){
						message = "지정 재심사자["+appeal_checker_value+"]가 없습니다!";
					}else {
						message = "근태 소명 재심사자 유형이 올바르지 않습니다! " + appeal_checker_type ;
					}
					Exception exception = new ExceptionAttendanceAppealProcess( message );
					result.error( exception );
				}
			}
			
		}
		
		//이의 신청 심사자 소속 조직 조회
		if (check && (!StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW))) {
			if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equalsIgnoreCase( appeal_auditor_type ) ) {
				attendanceAppealAuditInfo.setProcessPersonUnit1(
						attendanceAppealInfoServiceAdv.getPersonUnitName( appealAuditPersonName, attendanceDetail.getUnitName(), wrapIn.getIdentity() ) );
			}else {
				//보고 대상, 지정 사용자 및 사용자 속성에 지정된 사용자, 사용자 이름으로 기본 조직(첫 번째) 사용
				if( StringUtils.isNotEmpty( appealAuditPersonName ) ) {
					try {//심사자 조직 정보 조회
						attendanceAppealAuditInfo.setProcessPersonUnit1( userManagerService.getUnitNameWithPersonName( appealAuditPersonName ));
					}catch( Exception e ) {
						check = false;
						Exception exception = new ExceptionAttendanceAppealProcess(e, "심사자로 조직 정보를 가져오는 중 오류가 발생했습니다.");
						result.error(exception);
						logger.warn( "시스템이 심사자로 조직 정보를 조회하는 중 예외가 발생했습니다! personName:"+appealAuditPersonName );
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		//소속 조직명으로 이의 신청 심사자 소속 최상위 조직 조회
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isNotEmpty( attendanceAppealAuditInfo.getProcessPersonUnit1() )) {
				try {
					attendanceAppealAuditInfo.setProcessPersonTopUnit1( userManagerService.getTopUnitNameWithUnitName( attendanceAppealAuditInfo.getProcessPersonUnit1() ) );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "심사자로 최상위 조직 정보를 가져오는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.warn( "시스템이 심사자로 최상위 조직 정보를 조회하는 중 예외가 발생했습니다! personName:"+appealAuditPersonName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		//이의 신청 재검토자 소속 조직 조회
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( AppealConfig.APPEAL_AUDITTYPE_UNITDUTY.equalsIgnoreCase( appeal_auditor_type ) ) {
				attendanceAppealAuditInfo.setProcessPersonUnit2( attendanceAppealInfoServiceAdv.getPersonUnitName( appealCheckPersonName, attendanceDetail.getUnitName(), wrapIn.getIdentity() ) );
			}else {
				//보고 대상, 지정 사용자 및 사용자 속성에 지정된 사용자, 사용자 이름으로 기본 조직(첫 번째) 사용
				if( appealCheckPersonName != null  && !appealCheckPersonName.isEmpty() ) {
					try {//재검토자 조직 정보 조회
						attendanceAppealAuditInfo.setProcessPersonUnit2( userManagerService.getUnitNameWithPersonName( appealCheckPersonName ));
					}catch( Exception e ) {
						check = false;
						Exception exception = new ExceptionAttendanceAppealProcess(e, "재검토자로 조직 정보를 가져오는 중 오류가 발생했습니다.");
						result.error(exception);
						logger.warn( "시스템이 심사자로 조직 정보를 조회하는 중 예외가 발생했습니다! personName:"+appealCheckPersonName );
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		//소속 조직명으로 이의 신청 재검토자 소속 최상위 조직 조회
		if (check && !StringUtils.equals(appeal_flowType_value,AppealConfig.APPEAL_AUDIFLOWTYPE_WORKFLOW)) {
			if( StringUtils.isNotEmpty(attendanceAppealAuditInfo.getProcessPersonUnit2())) {
				try {//재검토자 최상위 조직 정보 조회
					attendanceAppealAuditInfo.setProcessPersonTopUnit2(  userManagerService.getTopUnitNameWithUnitName( attendanceAppealAuditInfo.getProcessPersonUnit2()) );
				}catch( Exception e ) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "재검토자로 최상위 조직 정보를 가져오는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.warn( "시스템이 심사자로 최상위 조직 정보를 조회하는 중 예외가 발생했습니다! personName:"+appealCheckPersonName );
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		//이의 신청 정보 저장
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.saveNewAppeal( attendanceAppealInfo, attendanceAppealAuditInfo );
				result.setData(new Wo(attendanceAppealInfo.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "근태 이의 신청 정보 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			// 이의 신청 정보 내용 채우기 - 이의 신청 정보 생성 성공. 현재 처리자[" + attendanceAppealInfo.getCurrentProcessor() + "]에게 메시지 알림 발송 시도......
			try {
				attendanceNoticeService.notifyAttendanceAppealProcessness1Message(attendanceAppealInfo);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "이의 신청은 제출되었으나 현재 처리자에게 알림을 보내는 중 오류가 발생했습니다! name:"+attendanceAppealAuditInfo.getProcessPerson1());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi extends AttendanceAppealInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		@FieldDescribe("이의 신청자 신분, 근태 대상자 신분: 근태 대상자가 여러 조직에 속하면 신분 하나를 골라 이의 신청 정보에 연결할 수 있음.")
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		@FieldDescribe("사용자 정의 프로세스 workId")
		private String workId = null;

		public String getWorkId() {
			return workId;
		}

		public void setWorkId(String workId) {
			this.workId = workId;
		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}