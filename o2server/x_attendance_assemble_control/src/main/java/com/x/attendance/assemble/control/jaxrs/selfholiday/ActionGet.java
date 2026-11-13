package com.x.attendance.assemble.control.jaxrs.selfholiday;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionGet extends BaseAction {

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceWorkDayConfig attendanceWorkDayConfig = null;

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {

			attendanceWorkDayConfig = emc.find(id, AttendanceWorkDayConfig.class);

			if (attendanceWorkDayConfig != null) {
				// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
				wrap = Wo.copier.copy(attendanceWorkDayConfig);
				// 조회 목록 정렬
				result.setData(wrap);
			}

		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends AttendanceWorkDayConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceWorkDayConfig, Wo> copier = WrapCopierFactory
				.wo(AttendanceWorkDayConfig.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

}