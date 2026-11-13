package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceEmployeeConfig attendanceEmployeeConfig = null;
		Boolean check = true;

		if (check) {
			if (id == null || id.isEmpty()) {
				check = false;
				Exception exception = new ExceptionConfigIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				attendanceEmployeeConfig = attendanceEmployeeConfigServiceAdv.get(id);
				if (attendanceEmployeeConfig == null) {
					check = false;
					Exception exception = new ExceptionConfigNotExists(id);
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "ID로 인원 근태 설정을 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				wrap = Wo.copier.copy(attendanceEmployeeConfig);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "조회된 객체를 출력용 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends AttendanceEmployeeConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceEmployeeConfig, Wo> copier = WrapCopierFactory
				.wo(AttendanceEmployeeConfig.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

}