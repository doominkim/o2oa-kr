package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionReAnalyseWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionReAnalyseWithFilter.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		String q_empName = null;
		String q_year = null;
		String q_month = null;
		List<String> ids = null;
		Wi wrapIn = null;
		Wo wo = new Wo();
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
			if (wrapIn == null) {
				wrapIn = new Wi();
			}
			q_empName = wrapIn.getQ_empName();
			q_year = wrapIn.getQ_year();
			q_month = wrapIn.getQ_month();
		}

		if (check) {
			if(StringUtils.isEmpty( q_year )){
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty("조회할 체크 정보의 연도는 비워 둘 수 없습니다");
				result.error(exception);
			}
		}
		if (check) {
			if(StringUtils.isEmpty( q_month )){
				check = false;
				Exception exception = new ExceptionQueryParameterEmpty("조회할 체크 정보의 월은 비워 둘 수 없습니다");
				result.error(exception);
			}
		}

		if (check) {
			if(StringUtils.isEmpty( q_empName )){
				q_empName = effectivePerson.getDistinguishedName();
			}
		}

		if (check) {
			try {
				ids = attendanceDetailServiceAdv.listDetailByCycleYearAndMonthWithOutStatus( q_empName, q_year, q_month );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceDetailProcess(e,
						"직원 이름과 연월로 체크 상세 정보 ID 목록을 조회하는 중 오류가 발생했습니다!"
						+ "Name:" + q_empName + ", Year:" + q_year + ", Month:" + q_month);
				result.error(exception);
				logger.error(e, currentPerson, request, null);
			}
		}

		if (check) {
			if (ListTools.isNotEmpty( ids )) {

				if( ids.size() > 100000 ){
					Exception exception = new ExceptionAttendanceDetailProcess("재분석할 체크 기록이 너무 많습니다. 재분석 범위를 줄이십시오(1회 최대 10만 건). 분석할 체크 정보 수:" + ids.size() );
					result.error(exception);
				}else{
					try {
						int seq = 0;
						for( String id : ids){
							seq++;
//							ThisApplication.detailAnalyseQueue.send( id );
						}
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,"재분석할 체크 기록을 분석 대기열로 보내는 중 오류가 발생했습니다!");
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
				}

				result.setCount(Long.parseLong(ids.size() + ""));
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe( "조회 대상 사용자: DistinguishedName." )
		private String q_empName;

		@FieldDescribe( "조회 연도." )
		private String q_year;

		@FieldDescribe( "조회 월." )
		private String q_month;

		public String getQ_empName() {
			return q_empName;
		}

		public String getQ_year() {
			return q_year;
		}

		public String getQ_month() {
			return q_month;
		}

		public void setQ_empName(String q_empName) {
			this.q_empName = q_empName;
		}

		public void setQ_year(String q_year) {
			this.q_year = q_year;
		}

		public void setQ_month(String q_month) {
			this.q_month = q_month;
		}
	}

	public static class Wo{
	}
}