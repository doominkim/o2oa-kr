package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
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
		AttendanceAppealInfo attendanceAppealInfo = null;
		Boolean check = true;
		if (check) {
			if (id == null || id.isEmpty() || "(0)".equals(id)) {
				check = false;
				result.error(new Exception("전달된 id가 비어 있거나 올바르지 않아 데이터를 조회할 수 없습니다."));
			}
		}
		if (check) {
			try {
				attendanceAppealInfo = attendanceAppealInfoServiceAdv.get(id);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceAppealProcess(e, "ID로 근태 체크 기록 데이터를 조회하는 중 오류가 발생했습니다. ID:" + id);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (attendanceAppealInfo != null) {
				AttendanceAppealAuditInfo auditInfo = attendanceAppealInfoServiceAdv.getAppealAuditInfo( attendanceAppealInfo.getId() );
				try {
					wrap = Wo.copier.copy(attendanceAppealInfo);
					if( auditInfo != null ) {
						wrap.setAppealAuditInfo( WoAttendanceAppealAuditInfo.copier.copy( auditInfo ));
					}
					result.setData(wrap);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceAppealProcess(e, "이의 신청 정보를 출력 객체로 변환하는 중 오류가 발생했습니다!");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends AttendanceAppealInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealInfo, Wo> copier = WrapCopierFactory.wo(AttendanceAppealInfo.class,
				Wo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("근태 이의 신청 심사 내용")
		private WoAttendanceAppealAuditInfo appealAuditInfo = null;

		public WoAttendanceAppealAuditInfo getAppealAuditInfo() {
			return appealAuditInfo;
		}

		public void setAppealAuditInfo(WoAttendanceAppealAuditInfo appealAuditInfo) {
			this.appealAuditInfo = appealAuditInfo;
		}
	}

	public static class WoAttendanceAppealAuditInfo extends AttendanceAppealAuditInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealAuditInfo, WoAttendanceAppealAuditInfo> copier = WrapCopierFactory.wo(AttendanceAppealAuditInfo.class,
				WoAttendanceAppealAuditInfo.class, null, JpaObject.FieldsInvisible);

	}
}