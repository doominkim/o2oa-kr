package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AttendanceScheduleSetting attendanceScheduleSetting = null;
		Wi wrapIn = null;
		String topUnitName = null;
		String unitName = null;
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
			if (StringUtils.isEmpty(wrapIn.getUnitName())) {
				check = false;
				Exception exception = new ExceptionScheduleUnitEmpty();
				result.error(exception);
			}else {
				try {
					unitName = userManagerService.checkUnitNameExists(wrapIn.getUnitName());
					System.out.println(">>>>>>>>>wrapIn.getUnitName()不为空，判断unitName是否存在！ unitName：" + unitName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceScheduleProcess(e, "조직 이름으로 조직 정보를 조회하는 중 오류가 발생했습니다. Name:" + wrapIn.getUnitName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (StringUtils.isEmpty(unitName) ) {
				check = false;
				Exception exception = new ExceptionCanNotFindUnitWithUnitName(wrapIn.getUnitName());
				result.error(exception);
			}
		}
		
		//최상위 조직 조회
		if (check) {
			if (StringUtils.isNotEmpty(unitName) ) {
				try {
					topUnitName = userManagerService.getTopUnitNameWithUnitName(unitName);
					System.out.println(">>>>>>>>>userManagerService.getTopUnitNameWithUnitName(unitName) = " + topUnitName );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceScheduleProcess(e, "조직 이름으로 상위 최상위 조직 정보를 조회하는 중 오류가 발생했습니다. unitName:" + unitName);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		
		if (check) {
			wrapIn.setUnitName(unitName);
			wrapIn.setTopUnitName(topUnitName);
		}
		
		if (check) {
			attendanceScheduleSetting = new AttendanceScheduleSetting();
			try {
				attendanceScheduleSetting = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceScheduleSetting.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e,
						"조회된 가져오기 파일 객체를 출력용 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			//기록 횟수 변경에 따른 기록 시간 값 변화 계산
			Integer signProxy = attendanceScheduleSetting.getSignProxy();
			switch(signProxy){
				case 1 :
					attendanceScheduleSetting.setMiddayRestStartTime("");
					attendanceScheduleSetting.setMiddayRestEndTime("");
					attendanceScheduleSetting.setLateStartTimeAfternoon("");
					attendanceScheduleSetting.setLeaveEarlyStartTimeMorning("");
					break;
				case 2 :
					attendanceScheduleSetting.setLateStartTimeAfternoon("");
					break;
			}


			try {
				attendanceScheduleSetting = attendanceScheduleSettingServiceAdv.save(attendanceScheduleSetting);
				result.setData(new Wo(attendanceScheduleSetting.getId()));
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e, "조직 근무 편성 정보 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceScheduleSetting> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceScheduleSetting.class, null, JpaObject.FieldsUnmodify);

		private String identity = null;

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}
	}

	public static class Wo extends WoId {
		public Wo(String id) {
			setId(id);
		}
	}
}