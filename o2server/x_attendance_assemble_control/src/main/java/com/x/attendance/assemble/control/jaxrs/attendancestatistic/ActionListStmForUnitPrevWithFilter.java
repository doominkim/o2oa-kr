package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class ActionListStmForUnitPrevWithFilter extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListStmForUnitPrevWithFilter.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id, Integer count, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<StatisticUnitForMonth> statisticList = null;
		WrapInFilterStatisticUnitForMonth wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, WrapInFilterStatisticUnitForMonth.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, currentPerson, request, null);
		}
		if(check ){
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// ID에 해당하는 기록의 sequence 조회
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(
								emc.find(id, StatisticUnitForMonth.class ),  JpaObject.sequence_FIELDNAME);
					}
				}

				//하위 조직 데이터를 조직 통계 데이터 조회 범위에 포함
				List<String> unitNameList = getUnitNameList(wrapIn.getTopUnitName(), wrapIn.getUnitName(), effectivePerson.getDebugger() );			
				wrapIn.setUnitName(unitNameList);
				// 데이터베이스에서 조건에 맞는 한 페이지 데이터 객체 조회
				statisticList = business.getStatisticUnitForMonthFactory().listIdsNextWithFilter(id, count, sequence,
						wrapIn);

				// 데이터베이스에서 조건에 맞는 객체 총수 조회
				total = business.getStatisticUnitForMonthFactory().getCountWithFilter(wrapIn);

				// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
				wraps = Wo.copier.copy(statisticList);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		result.setCount(total);
		result.setData(wraps);
		return result;
	}

	public static class Wo extends StatisticUnitForMonth  {
		
		private static final long serialVersionUID = -5076990764713538973L;		
		
		public static WrapCopier<StatisticUnitForMonth, Wo> copier = 
				WrapCopierFactory.wo( StatisticUnitForMonth.class, Wo.class, null, JpaObject.FieldsInvisible);
	}
}