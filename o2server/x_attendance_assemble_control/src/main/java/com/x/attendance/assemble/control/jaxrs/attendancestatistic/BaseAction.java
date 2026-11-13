package com.x.attendance.assemble.control.jaxrs.attendancestatistic;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.AttendanceStatisticServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class BaseAction extends StandardJaxrsAction {
	
	protected Logger logger = LoggerFactory.getLogger( BaseAction.class );
	protected UserManagerService userManagerService = new UserManagerService();
	protected AttendanceStatisticServiceAdv attendanceStatisticServiceAdv = new AttendanceStatisticServiceAdv();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();
	
	// 조직 기준으로 하위 조직 재귀 조회
	protected List<String> getUnitNameList( String unitName, List<String> unitNameList ) throws Exception {
		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		if ( StringUtils.isNotEmpty( unitName ) && !unitNameList.contains(unitName.trim())) {
			unitNameList.add(unitName.trim());

			// 해당 조직의 하위 조직 조회
			List<String> unitList = null;
			try {
				// 조회한 unit을 분석해 하위 조직이 있으면 모두 풀어냄
				unitList = userManagerService.listSubUnitNameWithParent( unitName );
			} catch (Exception e) {
				throw e;
			}
			if (unitList != null && unitList.size() > 0) {
				for (String unit : unitList) {
					getUnitNameList( unit, unitNameList);
				}
			}
		}
		return unitNameList;
	}

	// 최상위 조직으로 하위 최상위 조직 및 최상위 조직의 최고 조직 재귀 조회
	protected List<String> getTopUnitNameList( String topUnitName, List<String> unitNameList, Boolean debugger )
			throws Exception {
		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		if ( StringUtils.isNotEmpty( topUnitName ) && !unitNameList.contains(topUnitName.trim())) {
			unitNameList.add( topUnitName.trim() );

			// 해당 조직의 하위 조직 조회
			List<String> unitList = null;
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();) {
				// 모든 Top 조직 조회
				unitList = userManagerService.listSubUnitNameWithParent(topUnitName);
				if ( unitList != null && unitList.size() > 0 ) {
					logger.debug( debugger, ">>>>>>>>>>최상위 조직 이름["+topUnitName+"]으로 조회된"+unitList.size()+"개의 최상위 조직.");
					for ( String unit : unitList) {
						if (unitNameList.contains( unit )) {
							unitNameList.add( unit );
						}
						getUnitNameList( unit, unitNameList );
					}
				} else {
					logger.debug( debugger, ">>>>>>>>>>최상위 조직 이름["+topUnitName+"]으로 조회된 최상위 조직이 없습니다.");
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return unitNameList;
	}

	/**
	 * List<String> topUnitNameList, List<String> unitNameList 기준
	 * 조회 범위에 맞는 모든 조직 및 하위 조직 이름 목록 조회
	 * 
	 * @param topUnitNameList
	 * @param unitNameList
	 * @return
	 * @throws Exception
	 */
	protected List<String> getUnitNameList(List<String> topUnitNameList, List<String> unitNameList, Boolean debugger )
			throws Exception {

		if (unitNameList == null) {
			unitNameList = new ArrayList<String>();
		}
		// 먼저 최상위 조직의 모든 하위 조직을 조회해 List에 모두 추가
		// 조회한 unit을 분석해 하위 조직이 있으면 모두 풀어냄
		if (topUnitNameList != null && topUnitNameList.size() > 0) {
			for (String topUnitName : topUnitNameList) {
				// 모든 하위 최상위 조직과 모든 조직을 재귀 조회
				getTopUnitNameList(topUnitName, unitNameList, debugger );
			}
		}
		// 조회한 unit을 분석해 하위 조직이 있으면 모두 풀어냄
		if (unitNameList != null && unitNameList.size() > 0) {
			for (String unitName : unitNameList) {
				getUnitNameList(unitName, unitNameList);
			}
		}
		return unitNameList;
	}
	
	/**
	 * 근태 제외 조직 조회
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnUnitNameList() throws Exception {
		List<String> unUnitNameList = new ArrayList<String>();

		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String unitName = attendanceEmployeeConfig.getUnitName();
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isEmpty(employeeName) && StringUtils.isNotEmpty(unitName)){
					unUnitNameList.add(unitName);
					List<String> tempUnitNameList = userManagerService.listSubUnitNameWithParent(unitName);
					if(ListTools.isNotEmpty(tempUnitNameList)){
						for(String tempUnit:tempUnitNameList){
							if(!ListTools.contains(unUnitNameList, tempUnit)){
								unUnitNameList.add(tempUnit);
							}
						}
					}
				}
			} 
		}
		return unUnitNameList;
	}
	
	/**
	 * 근태 제외 사용자 조회
	 * @return
	 * @throws Exception 
	 */
	protected  List<String> getUnPersonNameList() throws Exception {
		List<String> personNameList = new ArrayList<String>();
		List<AttendanceEmployeeConfig> attendanceEmployeeConfigs = attendanceEmployeeConfigServiceAdv.listByConfigType("NOTREQUIRED");

		if(ListTools.isNotEmpty(attendanceEmployeeConfigs)){
			for (AttendanceEmployeeConfig attendanceEmployeeConfig : attendanceEmployeeConfigs) {
				String employeeName = attendanceEmployeeConfig.getEmployeeName();

				if(StringUtils.isNotEmpty(employeeName) && !ListTools.contains(personNameList, employeeName)){
					personNameList.add(employeeName);
				}
			}
		}
		return personNameList;
	}

}
