package com.x.attendance.assemble.control.jaxrs.selfholiday;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.entity.AttendanceSelfHoliday;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

public class ActionSave extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionSave.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wrapIn = null;
		List<AttendanceSelfHoliday> holidayList = null;

		//현재 사용자 정보 조회
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		
		if( check ){
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

				AttendanceSelfHoliday attendanceSelfHoliday = null;

				if( wrapIn != null && StringUtils.isNoneEmpty( wrapIn.getEmployeeName() )
						&& StringUtils.isNoneEmpty( wrapIn.getLeaveType() )
						&& wrapIn.getStartTime() != null
						&& wrapIn.getEndTime() != null
				){
					if( wrapIn.getEmployeeName().indexOf("@P") < 0 ){//DistinguishedName이 아님
						if( wrapIn.getEmployeeName().indexOf("@I") > 0 ){
							String personName = userManagerService.getPersonNameByIdentity(wrapIn.getEmployeeName());
							if( StringUtils.isNotEmpty( personName ) ){
								wrapIn.setEmployeeName(personName);
							}
						}
					}

					if( StringUtils.isNotEmpty( wrapIn.getEmployeeName() )){
						Person person = userManagerService.getPersonObjByName(wrapIn.getEmployeeName());
						if( person != null ){
							wrapIn.setEmployeeName( person.getDistinguishedName() );
							if( StringUtils.isNotEmpty( person.getEmployee() )){
								wrapIn.setEmployeeNumber( person.getEmployee() );
							}else{
								wrapIn.setEmployeeNumber( person.getDistinguishedName() );
							}
						}

						//직원의 조직 정보 보완
						Unit unit = userManagerService.getUnitWithPersonName( wrapIn.getEmployeeNumber() );
						Unit topUnit = null;
						String unitName = null;
						String unitOu = null;
						String topUnitName = null;
						String topUnitOu = null;
						if( unit != null ){
							unitName = unit.getName();
							unitOu = unit.getDistinguishedName();
							topUnit = userManagerService.getTopUnitWithUnitName(unitOu);
						}
						if( topUnit != null ){
							topUnitName = topUnit.getName();
							topUnitOu = topUnit.getDistinguishedName();
						}
						emc.beginTransaction( AttendanceSelfHoliday.class );
						//먼저 batchFlag 기준으로 기존 데이터를 삭제한 뒤 새 데이터 저장
						if(StringUtils.isNotEmpty( wrapIn.getBatchFlag() ) ){
							holidayList = attendanceSelfHolidayServiceAdv.listWithBatchFlag( wrapIn.getBatchFlag() );
							if(ListTools.isNotEmpty( holidayList )){
								logger.info("먼저 batchFlag 기준으로 기존 데이터를 삭제한 뒤 새 데이터 저장" );
								for( AttendanceSelfHoliday holiday : holidayList ){
									emc.remove( emc.find(holiday.getId(), AttendanceSelfHoliday.class ), CheckRemoveType.all );
								}
								logger.info("삭제" + holidayList.size() + "건의 기존 휴가 정보 데이터." );
							}
						}
						if( StringUtils.isNotEmpty( wrapIn.getId() ) ){
							//ID로 정보 존재 여부를 조회해 있으면 update, 없으면 create
							attendanceSelfHoliday = emc.find( wrapIn.getId(), AttendanceSelfHoliday.class );
							if( attendanceSelfHoliday != null ){
								//기존 정보 수정
								wrapIn.copyTo( attendanceSelfHoliday );
								attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
								logger.info("수정: gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

								attendanceSelfHoliday.setUnitName( unitName );
								attendanceSelfHoliday.setUnitOu( unitOu );
								attendanceSelfHoliday.setTopUnitName( topUnitName );
								attendanceSelfHoliday.setTopUnitOu( topUnitOu );

								emc.check( attendanceSelfHoliday, CheckPersistType.all);
							}else{
								attendanceSelfHoliday = new AttendanceSelfHoliday();
								wrapIn.copyTo( attendanceSelfHoliday );
								//매개변수로 전달된 ID를 레코드 ID로 사용
								attendanceSelfHoliday.setId( wrapIn.getId() );
								attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
								logger.info("신규: gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

								attendanceSelfHoliday.setUnitName( unitName );
								attendanceSelfHoliday.setUnitOu( unitOu );
								attendanceSelfHoliday.setTopUnitName( topUnitName );
								attendanceSelfHoliday.setTopUnitOu( topUnitOu );

								emc.persist( attendanceSelfHoliday, CheckPersistType.all);
							}
						}else{
							//지정한 ID가 전달되지 않았습니다
							attendanceSelfHoliday = new AttendanceSelfHoliday();

							wrapIn.copyTo( attendanceSelfHoliday );
							attendanceSelfHoliday.setBatchFlag(wrapIn.getBatchFlag());
							logger.debug("신규, ID 없음: gson.toJson( attendanceSelfHoliday ) = " + gson.toJson( attendanceSelfHoliday ) );

							attendanceSelfHoliday.setUnitName( unitName );
							attendanceSelfHoliday.setUnitOu( unitOu );
							attendanceSelfHoliday.setTopUnitName( topUnitName );
							attendanceSelfHoliday.setTopUnitOu( topUnitOu );

							emc.persist( attendanceSelfHoliday, CheckPersistType.all);
							result.setData( new Wo( attendanceSelfHoliday.getId() ) );
						}
						emc.commit();
						result.setData( new Wo( attendanceSelfHoliday.getId() ) );

						//캐시 삭제
						//ApplicationCache.notify( AttendanceSelfHoliday.class );
						CacheManager.notify(AttendanceSelfHoliday.class);

						//직원 휴가 데이터로 관련 통계 요청 기록 기록
						//new AttendanceDetailAnalyseService().recordStatisticRequireLog( attendanceSelfHoliday );
						logger.debug("휴가 데이터가 변경되어 해당 직원의 휴가 기간 내 모든 출퇴근 기록을 분석합니다......" );

						//휴가 데이터가 수정되어 해당 직원의 휴가 기간 내 모든 출퇴근 기록을 분석합니다
						List<String> ids = attendanceDetailAnalyseServiceAdv.listAnalyseAttendanceDetailIds(attendanceSelfHoliday.getEmployeeName(), attendanceSelfHoliday.getStartTime(), attendanceSelfHoliday.getEndTime(), effectivePerson.getDebugger() );
						if( ListTools.isNotEmpty( ids ) ){
							for( String id : ids ){
								try { //저장된 근태 데이터 분석
//									ThisApplication.detailAnalyseQueue.send( id );
								} catch ( Exception e1 ) {
									e1.printStackTrace();
								}
							}
						}
					}
				}else{
					if( jsonElement == null ){
						logger.debug("전달된 직원 휴가 정보 JSON 데이터가 비어 있습니다......" );
						Exception exception = new ExceptionSelfHolidayProcess( "전달된 직원 휴가 JSON 데이터가 비어 있어 저장할 수 없습니다. 데이터 내용을 확인하십시오!" );
						result.error( exception );
					}else {
						logger.debug("전달된 데이터가 휴가 데이터 인터페이스 요구사항에 맞지 않습니다......" );
						Exception exception = new ExceptionSelfHolidayProcess( "전달된 데이터가 휴가 데이터 인터페이스 요구사항에 맞지 않습니다. 데이터 내용을 확인하십시오:" + jsonElement.getAsString() );
						result.error( exception );
					}

				}
			} catch ( Exception e ) {
				Exception exception = new ExceptionSelfHolidayProcess( e, "직원 휴가 기록 저장 중 오류가 발생했습니다." );
				result.error( exception );
				logger.error( e, currentPerson, request, null);
			}
		}
		return result;
	}
	
	public static class Wi {

		@FieldDescribe("ID, ID가 이미 있으면 수정")
		private String id;

		@FieldDescribe("직원 이름: 직원 식별자, <font color='red'>필수</font>, 직원의 distinguishedName, 예: 홍길동@zhangsan@P")
		private String employeeName;

		private String employeeNumber;

		@FieldDescribe("휴가 유형:유급 연차|유급 병가|유급 복지 휴가|무급 개인 휴가|출장|교육|기타, <font color='red'>필수</font>")
		private String leaveType;

		@FieldDescribe("시작 시간, <font color='red'>필수</font>: yyyy-mm-dd hh24:mi:ss")
		private Date startTime;

		@FieldDescribe("종료 시간, <font color='red'>필수</font>: yyyy-mm-dd hh24:mi:ss")
		private Date endTime;

		@FieldDescribe("휴가 일수, 최소 단위 0.5일, <font color='red'>필수</font>")
		private Double leaveDayNumber = 0.0;

		@FieldDescribe("휴가 설명")
		private String description;

//		@FieldDescribe("프로세스 WorkId")
//		private String docId;

		@FieldDescribe("입력 배치 식별자: 프로세스 workId, jobId, CMS 문서 ID 또는 사용자 정의 정보 입력 가능. 저장 시 batchFlag 기준으로 먼저 삭제한 뒤 새 데이터 저장")
		private String batchFlag;

		public String getId() { return id; }

		public void setId(String id) { this.id = id; }

		public String getEmployeeName() { return employeeName; }

		public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

		public String getEmployeeNumber() { return employeeNumber; }

		public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

		public String getLeaveType() { return leaveType; }

		public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

		public Date getStartTime() { return startTime; }

		public void setStartTime(Date startTime) { this.startTime = startTime; }

		public Date getEndTime() { return endTime; }

		public void setEndTime(Date endTime) { this.endTime = endTime; }

		public Double getLeaveDayNumber() { return leaveDayNumber; }

		public void setLeaveDayNumber(Double leaveDayNumber) { this.leaveDayNumber = leaveDayNumber; }

		public String getDescription() { return description; }

		public void setDescription(String description) { this.description = description; }

		public String getBatchFlag() { return batchFlag; }

		public void setBatchFlag(String batchFlag) { this.batchFlag = batchFlag; }

		public void copyTo(AttendanceSelfHoliday attendanceSelfHoliday) {
			attendanceSelfHoliday.setBatchFlag( this.batchFlag );
			attendanceSelfHoliday.setDescription( this.description );
//			attendanceSelfHoliday.setDocId( this.batchFlag );
			attendanceSelfHoliday.setEmployeeName( this.employeeName );
			attendanceSelfHoliday.setEmployeeNumber( this.employeeNumber );
			attendanceSelfHoliday.setStartTime( this.startTime );
			attendanceSelfHoliday.setEndTime( this.endTime );
			attendanceSelfHoliday.setLeaveDayNumber( this.leaveDayNumber );
			attendanceSelfHoliday.setLeaveType( this.leaveType );

		}
	}
	
	public static class Wo extends WoId {
		public Wo( String id ) {
			setId( id );
		}
	}
}