package com.x.attendance.assemble.control.jaxrs.workplace;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListAll extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger(ActionListAll.class);
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<AttendanceWorkPlace> attendanceWorkPlaceList = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		Boolean check = true;
		if (check) {
			try {
				attendanceWorkPlaceList = attendanceWorkPlaceServiceAdv.listAll();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkPlaceProcess(e, "전체 근무 장소 정보를 조회하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if ( ListTools.isNotEmpty(attendanceWorkPlaceList) ) {
				try {
					wraps = Wo.copier.copy(attendanceWorkPlaceList);
				} catch (Exception e) {
					Exception exception = new ExceptionWorkPlaceProcess(e, "조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		result.setData(wraps);
		return result;
	}
	
	public static class Wo extends AttendanceWorkPlace  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceWorkPlace, Wo> copier = 
				WrapCopierFactory.wo( AttendanceWorkPlace.class, Wo.class, null,JpaObject.FieldsInvisible);
	}
}