package com.x.attendance.assemble.control.jaxrs.workplace;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceWorkPlace;
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
		EffectivePerson currentPerson = this.effectivePerson(request);
		AttendanceWorkPlace attendanceWorkPlace = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			if (wrapIn.getErrorRange() == null || wrapIn.getPlaceName().isEmpty()) {
				wrapIn.setErrorRange(200);
			}
		}
		if (check) {
			if (wrapIn.getPlaceName() == null || wrapIn.getPlaceName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionWorkPlaceNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getLatitude() == null || wrapIn.getLatitude().isEmpty()) {
				check = false;
				Exception exception = new ExceptionLatitudeEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getLongitude() == null || wrapIn.getLongitude().isEmpty()) {
				check = false;
				Exception exception = new ExceptionLongitudeEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getPlaceAlias() == null || wrapIn.getPlaceAlias().isEmpty()) {
				wrapIn.setPlaceAlias(wrapIn.getPlaceName());
			}
		}
		if (check) {
			try {
				attendanceWorkPlace = new AttendanceWorkPlace();
				Wi.copier.copy(wrapIn, attendanceWorkPlace);
				attendanceWorkPlace.setCreator(currentPerson.getDistinguishedName());
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					attendanceWorkPlace.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionWorkPlaceProcess(e, "사용자가 전달한 데이터를 근무 장소 객체로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceWorkPlace = attendanceWorkPlaceServiceAdv.save(attendanceWorkPlace);
				result.setData(new Wo(attendanceWorkPlace.getId()));
			} catch (Exception e) {
				Exception exception = new ExceptionWorkPlaceProcess(e, "근무 장소 이름이 비어 있어 데이터를 저장할 수 없습니다.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceWorkPlace {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceWorkPlace> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceWorkPlace.class, null, JpaObject.FieldsUnmodify);

	}

	public static class Wo extends WoId {
		public Wo(String id) {
			setId(id);
		}
	}
}