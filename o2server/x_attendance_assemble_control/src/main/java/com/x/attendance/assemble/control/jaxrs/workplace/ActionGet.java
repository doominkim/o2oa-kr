package com.x.attendance.assemble.control.jaxrs.workplace;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionGet extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGet.class);
	
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		Wo wrap = null;
		AttendanceWorkPlace attendanceWorkPlace = null;
		Boolean check = true;
		if (check) {
			try {
				attendanceWorkPlace = attendanceWorkPlaceServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkPlaceProcess(e, "ID로 근무 장소 정보를 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			if (attendanceWorkPlace != null) {
				try {
					wrap = Wo.copier.copy(attendanceWorkPlace);
					result.setData(wrap);
				} catch (Exception e) {
					Exception exception = new ExceptionWorkPlaceProcess(e, "조회 결과를 출력용 데이터로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}

			}
		}
		return result;
	}

	public static class Wo extends AttendanceWorkPlace  {
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static WrapCopier<AttendanceWorkPlace, Wo> copier = 
				WrapCopierFactory.wo( AttendanceWorkPlace.class, Wo.class, null,JpaObject.FieldsInvisible);
	}

}