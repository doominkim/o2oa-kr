package com.x.attendance.assemble.control.jaxrs.attendancestatisticalcycle;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.service.AttendanceStatisticalCycleServiceAdv;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionCycleDetail extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCycleDetail.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String year,
			String month) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		AttendanceStatisticalCycle attendanceStatisticalCycle = null;
		Map<String, Map<String, List<AttendanceStatisticalCycle>>> allCycleMap = null;
		AttendanceStatisticalCycleServiceAdv attendanceStatisticalCycleServiceAdv = new AttendanceStatisticalCycleServiceAdv();
		String topUnitName = null, unitName = null;
		Boolean check = true;

		if (check) {
			if (year == null || year.isEmpty()) {
				check = false;
				Exception exception = new ExceptionCycleYearEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (month == null || month.isEmpty()) {
				check = false;
				Exception exception = new ExceptionCycleMonthEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				logger.info("++++++++사용자의 최상위 조직 조회 시도: personName:" + effectivePerson.getDistinguishedName() );
				topUnitName = userManagerService.getTopUnitNameWithPersonName(effectivePerson.getDistinguishedName());
				if ( StringUtils.isEmpty( topUnitName ) ) {
					check = false;
					Exception exception = new ExceptionCanNotFindTopUnitNameByPerson( effectivePerson.getDistinguishedName());
					result.error(exception);
				}
			} catch (ExceptionPersonHasNoIdentity e) {
				check = false;
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetTopUnitNameByPerson(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				unitName = userManagerService.getUnitNameWithPersonName(effectivePerson.getDistinguishedName());
				if ( StringUtils.isEmpty( unitName ) ) {
					check = false;
					Exception exception = new ExceptionCanNotFindUnitNameByPerson( effectivePerson.getDistinguishedName());
					result.error(exception);
				}
			} catch (ExceptionPersonHasNoIdentity e) {
				check = false;
				result.error(e);
				logger.error(e, effectivePerson, request, null);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionGetUnitNameByPerson(e, effectivePerson.getDistinguishedName());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				allCycleMap = attendanceStatisticalCycleServiceAdv.getCycleMapFormAllCycles(effectivePerson.getDebugger());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionStatisticCycleProcess(e, "모든 통계 기간을 조회·구성하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				attendanceStatisticalCycle = attendanceStatisticalCycleServiceAdv.getAttendanceDetailStatisticCycle(
						topUnitName, unitName, year, month, allCycleMap, effectivePerson.getDebugger());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionStatisticCycleProcess(e, "직원의 최상위 조직과 조직으로 통계 기간을 조회하는 중 오류가 발생했습니다. TopUnit:"
						+ topUnitName + ", Unit:" + unitName + ", CycleYear:" + year + ", CycleMonth:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (attendanceStatisticalCycle != null) {
				try {
					wrap = Wo.copier.copy(attendanceStatisticalCycle);
					result.setData(wrap);
				} catch (Exception e) {
					Exception exception = new ExceptionStatisticCycleProcess(e, "조회된 통계 기간 객체를 출력용 정보로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends AttendanceStatisticalCycle {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceStatisticalCycle, Wo> copier = WrapCopierFactory
				.wo(AttendanceStatisticalCycle.class, Wo.class, null, JpaObject.FieldsInvisible);
	}

}