package com.x.attendance.assemble.control.jaxrs.attendanceworkdayconfig;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
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
		// 현재 사용자 정보 조회
		EffectivePerson currentPerson = this.effectivePerson(request);
		DateOperation dateOperation = new DateOperation();
		Boolean check = true;
		Date date = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				AttendanceWorkDayConfig _attendanceWorkDayConfig = null;
				AttendanceWorkDayConfig attendanceWorkDayConfig = new AttendanceWorkDayConfig();
				if (wrapIn != null && wrapIn.getId() != null && wrapIn.getId().length() > 10) {
					if (wrapIn.getId() != null && wrapIn.getId().length() > 10) {
						// ID로 정보 존재 여부를 조회해 있으면 update, 없으면 create
						_attendanceWorkDayConfig = emc.find(wrapIn.getId(), AttendanceWorkDayConfig.class);
						if (_attendanceWorkDayConfig != null) {
							// 수정
							emc.beginTransaction(AttendanceWorkDayConfig.class);
							Wi.copier.copy(wrapIn, _attendanceWorkDayConfig);
							try {
								date = dateOperation.getDateFromString(_attendanceWorkDayConfig.getConfigDate());
								_attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
								_attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
								emc.check(_attendanceWorkDayConfig, CheckPersistType.all);
								emc.commit();
								result.setData(new Wo(_attendanceWorkDayConfig.getId()));
							} catch (Exception e) {
								Exception exception = new ExceptionWorkDayConfigProcess(e,
										"휴일 설정의 날짜를 변환하는 중 오류가 발생했습니다. Date:" + _attendanceWorkDayConfig.getConfigDate());
								result.error(exception);
								logger.error(e, currentPerson, request, null);
							}
						} else {
							emc.beginTransaction(AttendanceWorkDayConfig.class);
							Wi.copier.copy(wrapIn, attendanceWorkDayConfig);
							attendanceWorkDayConfig.setId(wrapIn.getId());// 매개변수로 전달된 ID를 레코드 ID로 사용
							try {
								date = dateOperation.getDateFromString(attendanceWorkDayConfig.getConfigDate());
								attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
								attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
								emc.persist(attendanceWorkDayConfig, CheckPersistType.all);
								emc.commit();
								result.setData(new Wo(attendanceWorkDayConfig.getId()));
							} catch (Exception e) {
								Exception exception = new ExceptionWorkDayConfigProcess(e,
										"휴일 설정의 날짜를 변환하는 중 오류가 발생했습니다. Date:" + attendanceWorkDayConfig.getConfigDate());
								result.error(exception);
								logger.error(e, currentPerson, request, null);
							}
						}
					} else {
						// 지정한 ID가 전달되지 않았습니다
						emc.beginTransaction(AttendanceWorkDayConfig.class);
						Wi.copier.copy(wrapIn, attendanceWorkDayConfig);
						try {
							date = dateOperation.getDateFromString(attendanceWorkDayConfig.getConfigDate());
							attendanceWorkDayConfig.setConfigYear(dateOperation.getYear(date));
							attendanceWorkDayConfig.setConfigMonth(dateOperation.getMonth(date));
							emc.persist(attendanceWorkDayConfig, CheckPersistType.all);
							emc.commit();

							CacheManager.notify( AttendanceWorkDayConfig.class );

							result.setData(new Wo(attendanceWorkDayConfig.getId()));
						} catch (Exception e) {
							Exception exception = new ExceptionWorkDayConfigProcess(e,
									"휴일 설정의 날짜를 변환하는 중 오류가 발생했습니다. Date:" + attendanceWorkDayConfig.getConfigDate());
							result.error(exception);
							logger.error(e, currentPerson, request, null);
						}
					}
				}
			} catch (Exception e) {
				Exception exception = new ExceptionWorkDayConfigProcess(e, "휴일·근무일 설정 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends AttendanceWorkDayConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, AttendanceWorkDayConfig> copier = WrapCopierFactory.wi(Wi.class,
				AttendanceWorkDayConfig.class, null, JpaObject.FieldsUnmodify);
		
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