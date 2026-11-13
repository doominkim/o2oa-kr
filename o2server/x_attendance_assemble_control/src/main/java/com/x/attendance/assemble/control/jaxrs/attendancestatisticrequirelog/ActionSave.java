package com.x.attendance.assemble.control.jaxrs.attendancestatisticrequirelog;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceStatisticRequireLog;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
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
		Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (wrapIn != null) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceStatisticRequireLog _attendanceStatisticRequireLog = null;
				AttendanceStatisticRequireLog attendanceStatisticRequireLog = new AttendanceStatisticRequireLog();
				if (wrapIn.getId() != null && wrapIn.getId().length() > 10) {
					// ID로 정보 존재 여부를 조회해 있으면 update, 없으면 create
					_attendanceStatisticRequireLog = emc.find(wrapIn.getId(), AttendanceStatisticRequireLog.class);
					if (_attendanceStatisticRequireLog != null) {
						// 수정
						emc.beginTransaction(AttendanceStatisticRequireLog.class);
						Wi.copier.copy(wrapIn, _attendanceStatisticRequireLog);
						emc.check(_attendanceStatisticRequireLog, CheckPersistType.all);
						emc.commit();
					} else {
						emc.beginTransaction(AttendanceStatisticRequireLog.class);
						Wi.copier.copy(wrapIn, attendanceStatisticRequireLog);
						attendanceStatisticRequireLog.setId(wrapIn.getId());// 매개변수로 전달된 ID를 레코드 ID로 사용
						emc.persist(attendanceStatisticRequireLog, CheckPersistType.all);
						emc.commit();
					}
				} else {
					// 지정한 ID가 전달되지 않았습니다
					emc.beginTransaction(AttendanceStatisticRequireLog.class);
					Wi.copier.copy(wrapIn, attendanceStatisticRequireLog);
					emc.persist(attendanceStatisticRequireLog, CheckPersistType.all);
					emc.commit();
				}
				result.setData(new Wo(attendanceStatisticRequireLog.getId()));
			} catch (Exception e) {
				Exception exception = new ExceptionStatisticRequireProcess(e, "통계 기간 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceStatisticRequireLog {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceStatisticRequireLog> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceStatisticRequireLog.class, null, JpaObject.FieldsUnmodify);
		
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