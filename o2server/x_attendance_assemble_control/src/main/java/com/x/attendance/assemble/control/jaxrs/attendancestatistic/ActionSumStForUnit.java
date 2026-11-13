package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionSumStForUnit extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSumStForUnit.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String name,
			String year, String month) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wraps = null;
		StatisticUnitForMonth statisticUnitForMonth = null;
		List<String> unitNameList = new ArrayList<String>();
		Object employeeCount = null;
		Object onDutyEmployeeCount = null;
		Object absenceDayCount = null;
		Object onSelfHolidayCount = null;
		Object lateCount = null;
		Object leaveEarlyCount = null;
		Object offDutyCount = null;
		Object onDutyCount = null;
		Object abNormalDutyCount = null;
		Object lackOfTimeCount = null;
		Boolean check = true;

		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if (check) {
			if (name == null || name.isEmpty()) {
				check = false;
				Exception exception = new ExceptionQueryStatisticUnitNameEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				unitNameList = userManagerService.listSubUnitNameWithParent(name);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e, "조직 이름으로 모든 하위 조직 이름을 조회하는 중 오류가 발생했습니다! Unit:" + name);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (unitNameList == null) {
				unitNameList = new ArrayList<>();
			}
			unitNameList.add(name);
		}
		if (check) {
			try {
				absenceDayCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_AbsenceDayCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '결근 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				onSelfHolidayCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_OnSelfHolidayCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '휴가 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				lateCount = attendanceStatisticServiceAdv.sumUnitForMonth_LateCount_ByUnitYearAndMonth(unitNameList,
						year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '지각 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				leaveEarlyCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_LeaveEarlyCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '조퇴 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				onDutyCount = attendanceStatisticServiceAdv.sumUnitForMonth_OnDutyCount_ByUnitYearAndMonth(unitNameList,
						year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '출근 체크 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				offDutyCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_OffDutyCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '퇴근 체크 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				abNormalDutyCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_AbNormalDutyCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '결근 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				lackOfTimeCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_LackOfTimeCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '근무 시간 부족 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				onDutyEmployeeCount = attendanceStatisticServiceAdv
						.sumUnitForMonth_AttendanceDayCount_ByUnitYearAndMonth(unitNameList, year, month);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e,
						"조직 월별 통계의 '결근 일수' 합계를 조회하는 중 오류가 발생했습니다. Name:" + unitNameList + ", Year:" + year + ", Month:" + month);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			double count = 0.0;
			statisticUnitForMonth = new StatisticUnitForMonth(lateCount == null ? 0 : (long) lateCount,
					leaveEarlyCount == null ? 0 : (long) leaveEarlyCount,
					offDutyCount == null ? 0 : (long) offDutyCount, onDutyCount == null ? 0 : (long) onDutyCount,
					absenceDayCount == null ? 0 : (double) absenceDayCount,
					employeeCount == null ? 0 : (double) employeeCount,
					onDutyEmployeeCount == null ? 0 : (double) onDutyEmployeeCount,
					onSelfHolidayCount == null ? 0 : (double) onSelfHolidayCount,
					lackOfTimeCount == null ? 0 : (long) lackOfTimeCount,
					abNormalDutyCount == null ? 0 : (long) abNormalDutyCount);
			if (onDutyEmployeeCount == null) {
				count = 0.0;
			} else {
				count = (double) onDutyEmployeeCount;
			}
			if (statisticUnitForMonth != null) {
				statisticUnitForMonth.setOnDutyEmployeeCount(count);
			}
		}
		if (check) {
			try {
				wraps = Wo.copier.copy(statisticUnitForMonth);
				result.setData(wraps);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess(e, "조회된 조직 월별 통계 객체를 출력용 정보로 변환하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wo extends StatisticUnitForMonth {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<StatisticUnitForMonth, Wo> copier = WrapCopierFactory.wo(StatisticUnitForMonth.class,
				Wo.class, null, JpaObject.FieldsInvisible);
	}
}