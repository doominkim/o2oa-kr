package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.factory.AttendanceWorkDayConfigFactory;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

public class ActionListAll extends BaseAction {
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceWorkDayConfig> attendanceWorkDayConfigList = null;
		Business business = null;
		AttendanceWorkDayConfigFactory attendanceWorkDayConfigFactory = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			attendanceWorkDayConfigFactory = business.getAttendanceWorkDayConfigFactory();
			// ID IN ids 인 모든 앱 정보 목록 조회
			attendanceWorkDayConfigList = attendanceWorkDayConfigFactory.listAll();
			// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
			wraps = Wo.copier.copy( attendanceWorkDayConfigList );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceWorkDayConfig  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<AttendanceWorkDayConfig, Wo> copier = 
				WrapCopierFactory.wo( AttendanceWorkDayConfig.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}