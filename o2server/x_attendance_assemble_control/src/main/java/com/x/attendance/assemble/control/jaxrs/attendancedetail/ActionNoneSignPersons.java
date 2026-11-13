package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.ListTools;

/**
 * 출근 시간 X분 전 미기록 사용자 정보 조회 인터페이스
 * 1. 기록 대상 전체 사용자 정보 목록 조회
 * 2. 지정 시간 이전에 이미 기록한 사용자 목록 조회
 * 3. 기록 대상 전체 사용자 목록에서 이미 기록한 사용자를 제외하고 반환
 */
public class ActionNoneSignPersons extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionNoneSignPersons.class );
	
	protected ActionResult<List<String>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<String>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Wi wrapIn = null;
		Boolean check = true;
		DateOperation dateOperation = new DateOperation();
		List<String> allNeedSignPersons = new ArrayList<>();
		List<String> signedPersons = new ArrayList<>();
		List<String> wos = new ArrayList<>();
		String signType = "all";
		String[] signTypeArray = null;
		List<String> signTypeList = new ArrayList<>();
		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if( StringUtils.isEmpty( wrapIn.getDeadline() ) ) {
			wrapIn.setDeadline( dateOperation.getNowDateTime() );
		}

		if( StringUtils.isEmpty( wrapIn.getSignType() )){
			signType = "all";
		}else{
			signType = wrapIn.getSignType();
		}

		signTypeArray = signType.split("#");
		for( String type : signTypeArray ){
			signTypeList.add( type );
		}

		if ( check ) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listByConfigType( "REQUIRED" );
				if( ListTools.isEmpty( attendanceEmployeeConfigList ) ) {
					//근태 대상 사용자 설정이 없으면 전원 근태
					List<Person> allPersonObjs = userManagerService.listAllPersons();
					if(ListTools.isNotEmpty( allPersonObjs )){
						for( Person person : allPersonObjs ){
							allNeedSignPersons.add( person.getDistinguishedName() );
						}
					}
				}else{
					for( AttendanceEmployeeConfig person : attendanceEmployeeConfigList ){
						allNeedSignPersons.add( person.getEmployeeName() );
					}
				}
			} catch ( Exception e ) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess( e, "근태 대상자 설정 목록을 조회하는 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, currentPerson, request, null );
			}
		}

		if (check) {
			//지정 마감일 이전에 이미 출퇴근 체크한 사용자 조회
			if( signTypeList.contains( "all" )){
				signedPersons = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "all" );
			}else {
				if( signTypeList.contains( "onDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "onDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "offDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "offDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "morningOffDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "morningOffDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
				if( signTypeList.contains( "afternoonOnDuty" )){
					List<String> signedPersons_tmp = attendanceDetailServiceAdv.listSignedPersonsWithDeadLine( wrapIn.getDeadline(), "afternoonOnDuty" );
					if(ListTools.isNotEmpty( signedPersons_tmp )){
						for( String tmp : signedPersons_tmp ){
							if( !signedPersons.contains( tmp )){
								signedPersons.add( tmp );
							}
						}
					}
				}
			}
		}

		if (check) {
			//이미 기록한 사용자 제외
			if( ListTools.isNotEmpty( signedPersons )){
				for( String distinguishedName : signedPersons ){
					ListTools.removeStringFromList( distinguishedName, allNeedSignPersons);
				}
			}
		}

		if (check) {
			for( String distinguishedName : allNeedSignPersons ){
				wos.add( distinguishedName );
			}
		}

		result.setData( wos );
		return result;
	}

	public static class Wi{

		@FieldDescribe( "마감 시점, 입력하지 않으면 현재 시간을 마감 시간으로 사용" )
		private String deadline = null;

		@FieldDescribe( "미기록 유형: all#onDuty#offDuty#morningOffDuty#afternoonOnDuty, #으로 구분해 여러 개 선택 가능" )
		private String signType = null;

		public String getDeadline() {
			return deadline;
		}

		public void setDeadline(String deadline) {
			this.deadline = deadline;
		}

		public String getSignType() { return signType; }

		public void setSignType(String signType) { this.signType = signType; }
	}

	public static class Wo extends WoText {
		public Wo( String text ) {
			setText( text );
		}
	}
}