package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListMobileWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListMobileWithFilter.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, Integer page,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		List<Wo> wraps = new ArrayList<>();
		List<Wo> allResultWrap = null;
		List<AttendanceDetailMobile> attendanceDetailMobileList = null;
		Date datetime = null;
		DateOperation dateOperation = new DateOperation();
		Long total = 0L;
		Integer selectTotal = 0;
		Boolean check = true;
		Wi wrapIn = null;
		Boolean queryConditionIsNull = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}

		if (check) {
			if (page == null) {
				page = 1;
			}
		}
		if (check) {
			if (count == null) {
				count = 20;
			}
		}
		if (page <= 0) {
			page = 1;
		}
		if (count <= 0) {
			count = 20;
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getEmpNo() )) {
				queryConditionIsNull = false;
			}
			if ( StringUtils.isNotEmpty( wrapIn.getEmpName() )) {
				queryConditionIsNull = false;
			}
			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				queryConditionIsNull = false;
				if ( StringUtils.isEmpty( wrapIn.getEndDate() )) {
					wrapIn.setEndDate(wrapIn.getStartDate());
				}
			}
			if (queryConditionIsNull) {
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getEndDate() )) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getEndDate());
					wrapIn.setEndDate(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD")); // 종료 날짜
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"조회 종료 날짜 형식이 올바르지 않습니다. 형식: yyyy-mm-dd. 날짜:" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
					// 잘못된 형식은 비움, endDate 없으면 startDate 당일 데이터만 조회
					if ( StringUtils.isNotEmpty( wrapIn.getEndDate() )) {
						wrapIn.setEndDate(null);
					}
				}

			}
			if ( StringUtils.isNotEmpty( wrapIn.getStartDate() )) {
				try {
					datetime = dateOperation.getDateFromString(wrapIn.getStartDate());
					wrapIn.setStartDate(dateOperation.getDateStringFromDate(datetime, "YYYY-MM-DD")); // 시작 날짜
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"조회 시작 날짜 형식이 올바르지 않습니다. 형식: yyyy-mm-dd. 날짜:" + wrapIn.getStartDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		// 조회 최대 항목 수
		selectTotal = page * count;
		if (check) {
			if (selectTotal > 0) {
				try {
					total = attendanceDetailServiceAdv.countAttendanceDetailMobileForPage(wrapIn.getEmpNo(),
							wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(),
							wrapIn.getEndDate());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"조건으로 직원 모바일 체크 정보 건수를 조회하는 중 오류가 발생했습니다." + "EmpNo:" + wrapIn.getEmpNo() + ", EmpName:"
									+ wrapIn.getEmpName() + ", SignDescription:" + wrapIn.getSignDescription()
									+ ", StartDate:" + wrapIn.getStartDate() + ", EndDate:" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			if (selectTotal > 0 && total > 0) {
				try {
					attendanceDetailMobileList = attendanceDetailServiceAdv.listAttendanceDetailMobileForPage(
							wrapIn.getEmpNo(), wrapIn.getEmpName(), wrapIn.getSignDescription(), wrapIn.getStartDate(),
							wrapIn.getEndDate(), selectTotal);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e,
							"조건으로 직원 모바일 체크 정보 목록을 조회하는 중 오류가 발생했습니다." + "EmpNo:" + wrapIn.getEmpNo() + ", EmpName:" + wrapIn.getEmpName()
									+ ", SignDescription:" + wrapIn.getSignDescription() + ", StartDate:"
									+ wrapIn.getStartDate() + ", EndDate:" + wrapIn.getEndDate());
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			if (attendanceDetailMobileList != null && !attendanceDetailMobileList.isEmpty()) {
				try {
					allResultWrap = Wo.copier.copy(attendanceDetailMobileList);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceDetailProcess(e, "직원 모바일 체크 정보를 출력 객체로 변환하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, currentPerson, request, null);
				}
			}
		}
		if (check) {
			int startIndex = (page - 1) * count;
			int endIndex = page * count;
			for (int i = 0; allResultWrap != null && i < allResultWrap.size(); i++) {
				if (i >= startIndex && i < endIndex) {
					wraps.add(allResultWrap.get(i));
				}
			}
		}
		result.setCount(total);
		result.setData(wraps);

		return result;
	}

	public static class Wi {

		@FieldDescribe("사번, 사번으로 기록 조회")
		private String empNo;

		@FieldDescribe("직원 이름, 직원 이름으로 기록 조회.")
		private String empName;

		@FieldDescribe("시작 날짜: yyyy-mm-dd.")
		private String startDate;

		@FieldDescribe("종료 날짜: yyyy-mm-dd, 시작 날짜만 입력하고 종료 날짜를 비우면 시작 날짜 하루만 조회")
		private String endDate;

		@FieldDescribe("출퇴근 기록 설명: 출근 기록, 퇴근 기록.")
		private String signDescription;

		public String getEmpNo() {
			return empNo;
		}

		public void setEmpNo(String empNo) {
			this.empNo = empNo;
		}

		public String getEmpName() {
			return empName;
		}

		public void setEmpName(String empName) {
			this.empName = empName;
		}

		public String getStartDate() {
			return startDate;
		}

		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getSignDescription() {
			return signDescription;
		}

		public void setSignDescription(String signDescription) {
			this.signDescription = signDescription;
		}

	}

	public static class Wo extends AttendanceDetailMobile {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceDetailMobile, Wo> copier = WrapCopierFactory.wo(AttendanceDetailMobile.class,
				Wo.class, null, JpaObject.FieldsInvisible);
	}
}