package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.attendance.entity.StatisticUnitForDay;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionShowStdForUnit extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionShowStdForUnit.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String name, String year, String month ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<String> ids = null;
		List<StatisticUnitForDay> statisticUnitForDay_list = null;
		List<String> unitNames = null;
		Boolean check = true;
		
		if ("(0)".equals(year)) {
			year = null;
		}
		if ("(0)".equals(month)) {
			month = null;
		}
		if( check ){
			if( name == null || name.isEmpty() ){
				check = false;
				Exception exception = new ExceptionQueryStatisticUnitNameEmpty();
				result.error( exception );
			}
		}
		if( check ){
			try {
				unitNames = userManagerService.listSubUnitNameWithParent( name );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess( e, "조직 이름으로 모든 하위 조직 이름을 조회하는 중 오류가 발생했습니다! Unit:" + name );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( unitNames == null ){
				unitNames = new ArrayList<>();
			}
			unitNames.add( name );
		}
		if( check ){
			try {
				ids = attendanceStatisticServiceAdv.listStatisticUnitForDay_ByUnitDayYearAndMonth( unitNames, year, month );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAttendanceStatisticProcess( e, 
					"조직 이름 목록과 연월로 조직 일별 통계 데이터 ID 목록을 조회하는 중 오류가 발생했습니다. Name:"+unitNames+", Year:"+year+", Month:" + month
				);
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if( check ){
			if( ids != null && !ids.isEmpty() ){
				try {
					statisticUnitForDay_list = attendanceStatisticServiceAdv.listUnitForDay( ids );
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "ID 목록으로 조직 일별 통계 데이터 목록을 조회하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		if( check ){
			if( statisticUnitForDay_list != null ){
				try {
					wraps = Wo.copier.copy( statisticUnitForDay_list );
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionAttendanceStatisticProcess( e, "조회된 조직 일별 통계 객체를 출력용 정보로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}

	public static class Wo extends StatisticUnitForDay  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticUnitForDay, Wo> copier = 
				WrapCopierFactory.wo( StatisticUnitForDay.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}