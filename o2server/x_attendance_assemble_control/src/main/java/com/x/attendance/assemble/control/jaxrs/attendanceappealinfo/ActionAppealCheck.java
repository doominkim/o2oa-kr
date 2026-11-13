package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionAppealCheck extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionAppealCheck.class );
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		List<String> ids = null;
		List<ProcessWo> wos = new ArrayList<>();
		ProcessWo processWo = null;
		String unitName = null;
		String topUnitName = null;
		AttendanceAppealInfo attendanceAppealInfo = null;
		Wi wrapIn = null;
		Date now = new Date();
		Boolean check = true;
		Boolean subProcessCheck = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if( wrapIn.getIds() == null || wrapIn.getIds().isEmpty() ) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess( "재검토할 이의 신청 ID가 비어 있습니다!" );
				result.error(exception);
			}else {
				ids =  wrapIn.getIds();
				wo.setTotal( ids.size() );
			}
		}
		
		if ( check ) {
			
			for( String id : ids ) {
				
				subProcessCheck = true;
				processWo = new ProcessWo();
				processWo.setId(id);
				
				try {
					attendanceAppealInfo = attendanceAppealInfoServiceAdv.get( id );
					if (attendanceAppealInfo == null) {
						subProcessCheck = false;
						processWo.setSuccess( false );
						processWo.setTitle( "제목 없음" );
						processWo.setDiscription("근태 소명 정보가 없습니다!");
						Exception exception = new ExceptionAttendanceAppealNotExists(id);
						result.error(exception);
					}else {
						processWo.setTitle( "근태 소명-" + attendanceAppealInfo.getEmpName() + "-" + attendanceAppealInfo.getRecordDateString() );
					}
				} catch (Exception e) {
					subProcessCheck = false;
					processWo.setSuccess( false );
					processWo.setTitle( "제목 없음" );
					processWo.setDiscription("근태 소명 정보 조회 중 오류가 발생했습니다!");
					Exception exception = new ExceptionAttendanceAppealProcess(e, "ID로 근태 이의 신청 데이터를 조회하는 중 오류가 발생했습니다. ID:"+ id);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				
				if( subProcessCheck ) {
					try {
						unitName = userManagerService.getUnitNameWithPersonName(effectivePerson.getDistinguishedName());
						if (unitName != null) {
							topUnitName = userManagerService.getTopUnitNameWithUnitName( unitName );
						} else {
							check = false;
							processWo.setSuccess( false );
							processWo.setDiscription("소명자의 조직이 없습니다. " + effectivePerson.getDistinguishedName() );
							Exception exception = new ExceptionPersonHasNoUnit(effectivePerson.getDistinguishedName());
							result.error(exception);
						}
					} catch (Exception e) {
						check = false;
						processWo.setSuccess( false );
						processWo.setDiscription("소명자 조직 조회 중 오류가 발생했습니다! " + effectivePerson.getDistinguishedName() );
						Exception exception = new ExceptionAttendanceAppealProcess(e, "직원 이름으로 조직 정보를 조회하는 중 오류가 발생했습니다! name:"+effectivePerson.getDistinguishedName());
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				if( subProcessCheck ) {
					try {
						attendanceAppealInfo = attendanceAppealInfoServiceAdv.secondProcessAttendanceAppeal(id, unitName, topUnitName, effectivePerson.getDistinguishedName(), // processorName
								now, // processTime
								wrapIn.getOpinion2(), // opinion
								wrapIn.getStatus() // status
						);
						processWo.setDiscription("소명 처리 성공!");
					} catch (Exception e) {
						check = false;
						processWo.setSuccess( false );
						processWo.setDiscription( "소명 처리 실패!" );
						Exception exception = new ExceptionAttendanceAppealProcess(e, id);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
				if( subProcessCheck ) {
					wo.setSuccessCount( wo.getSuccessCount() + 1 );
				}else {
					wo.setErrorCount( wo.getErrorCount() );
				}
				wos.add( processWo );
			}
			wo.setProcessRecord( wos );
			result.setData( wo );
		}
		return result;
	}
	
	public static class Wi extends AttendanceAppealInfo {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>(JpaObject.FieldsUnmodify);

		@FieldDescribe("재검토할 ID 목록")
		private List<String> ids = null;

		@FieldDescribe("재검토 의견")
		private String opinion2;

		@FieldDescribe("이의 신청자 신분, 근태 대상자 신분: 근태 대상자가 여러 조직에 속하면 신분 하나를 골라 이의 신청 정보에 연결할 수 있음.")
		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public List<String> getIds() {
			return ids;
		}

		public void setIds(List<String> ids) {
			this.ids = ids;
		}

		public String getOpinion2() { return opinion2; }

		public void setOpinion2(String opinion2) { this.opinion2 = opinion2; }
	}
	
	public static class Wo {
		
		private Integer total = 0;
		
		private Integer successCount = 0;
		
		private Integer errorCount = 0;
		
		private List<ProcessWo> processRecord = null;

		public Integer getTotal() {
			return total;
		}

		public Integer getSuccessCount() {
			return successCount;
		}

		public Integer getErrorCount() {
			return errorCount;
		}

		public List<ProcessWo> getProcessRecord() {
			return processRecord;
		}

		public void setTotal(Integer total) {
			this.total = total;
		}

		public void setSuccessCount(Integer successCount) {
			this.successCount = successCount;
		}

		public void setErrorCount(Integer errorCount) {
			this.errorCount = errorCount;
		}

		public void setProcessRecord(List<ProcessWo> processRecord) {
			this.processRecord = processRecord;
		}		
	}
	
	public static class ProcessWo {
		
		private String id = null;
		
		private String title = null;
		
		private String discription = null;
		
		private Boolean success = true;
		
		public String getId() {
			return id;
		}
		public String getTitle() {
			return title;
		}
		public String getDiscription() {
			return discription;
		}
		public Boolean getSuccess() {
			return success;
		}
		public void setId(String id) {
			this.id = id;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public void setDiscription(String discription) {
			this.discription = discription;
		}
		public void setSuccess(Boolean success) {
			this.success = success;
		}
	}
}