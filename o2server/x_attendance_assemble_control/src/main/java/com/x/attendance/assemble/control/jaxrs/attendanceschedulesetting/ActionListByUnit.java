package com.x.attendance.assemble.control.jaxrs.attendanceschedulesetting;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListByUnit extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListByUnit.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String name)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<String> ids = null;
		List<AttendanceScheduleSetting> attendanceScheduleSettingList = null;
		Boolean check = true;

		if (check) {
			if (name == null || name.isEmpty()) {
				check = false;
				Exception exception = new ExceptionScheduleNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				ids = attendanceScheduleSettingServiceAdv.listByUnitName(name);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e,
						"조직 이름으로 조직 근무 편성 목록을 조회하는 중 오류가 발생했습니다. Name:" + name);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && ids != null && !ids.isEmpty()) {
			try {
				attendanceScheduleSettingList = attendanceScheduleSettingServiceAdv.list(ids);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e, "ID 목록으로 조직 근무 편성 목록을 조회하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && attendanceScheduleSettingList != null) {
			try {
				wraps = Wo.copier.copy(attendanceScheduleSettingList);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceScheduleProcess(e,
						"조회된 가져오기 파일 객체를 출력용 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceScheduleSetting {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceScheduleSetting, Wo> copier = WrapCopierFactory
				.wo(AttendanceScheduleSetting.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}