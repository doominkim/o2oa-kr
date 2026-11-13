package com.x.attendance.assemble.control.jaxrs.attendanceemployeeconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListAll extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigList = null;
		Boolean check = true;
		if (check) {
			try {
				attendanceEmployeeConfigList = attendanceEmployeeConfigServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				result.error(e);
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "전체 인원 근태 설정을 조회하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && attendanceEmployeeConfigList != null) {
			// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
			try {
				wraps = Wo.copier.copy(attendanceEmployeeConfigList);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceEmployeeProcess(e, "조회된 객체를 출력용 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceEmployeeConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceEmployeeConfig, Wo> copier = WrapCopierFactory
				.wo(AttendanceEmployeeConfig.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}