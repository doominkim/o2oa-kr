package com.x.attendance.assemble.control.jaxrs.attendancedetail;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.assemble.control.service.AttendanceEmployeeConfigServiceAdv;
import com.x.attendance.assemble.control.service.UserManagerService;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

public class ActionListNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListNextWithFilter.class);
	private UserManagerService userManagerService = new UserManagerService();
	protected AttendanceEmployeeConfigServiceAdv attendanceEmployeeConfigServiceAdv = new AttendanceEmployeeConfigServiceAdv();

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		EffectivePerson currentPerson = this.effectivePerson(request);
		long total = 0;
		List<AttendanceDetail> detailList = null;
		List<String> topUnitNames = new ArrayList<String>();
		List<String> unitNames = new ArrayList<String>();
		List<String> topUnitNames_tmp = null;
		List<String> unitNames_tmp = null;
		Wi wrapIn = null;
		AttendanceScheduleSetting scheduleSetting_top = null;
		AttendanceScheduleSetting scheduleSetting = null;
		Boolean check = true;
		
		List<String> unUnitNameList = new ArrayList<String>();
		List<String> personNameList = new ArrayList<String>();

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, currentPerson, request, null);
		}
		if (check) {
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// ID에 해당하는 기록의 sequence 조회
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(emc.find(id, AttendanceDetail.class),  JpaObject.sequence_FIELDNAME);
					}
				}

				// 최상위 조직 처리, 하위 최상위 조직 조회
				if ( StringUtils.isNotEmpty( wrapIn.getQ_topUnitName() )) {
					topUnitNames.add(wrapIn.getQ_topUnitName());
					scheduleSetting_top = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithUnit(wrapIn.getQ_topUnitName(), effectivePerson.getDebugger() );
					try {
						topUnitNames_tmp = userManagerService.listSubUnitNameWithParent(wrapIn.getQ_topUnitName());
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,
								"최상위 조직의 모든 하위 조직 이름을 조회하는 중 오류가 발생했습니다! TopUnit:" + wrapIn.getQ_topUnitName());
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
					if (topUnitNames_tmp != null && topUnitNames_tmp.size() > 0) {
						for (String topUnitName : topUnitNames_tmp) {
							topUnitNames.add(topUnitName);
						}
					}
					wrapIn.setTopUnitNames(topUnitNames);
				}

				// 조직 처리, 하위 조직 조회
				if ( StringUtils.isNotEmpty( wrapIn.getQ_unitName() )) {
					unitNames.add(wrapIn.getQ_unitName());
					scheduleSetting = attendanceScheduleSettingServiceAdv.getAttendanceScheduleSettingWithUnit(wrapIn.getQ_unitName(), effectivePerson.getDebugger() );
					try {
						unitNames_tmp = userManagerService.listSubUnitNameWithParent(wrapIn.getQ_unitName());
					} catch (Exception e) {
						Exception exception = new ExceptionAttendanceDetailProcess(e,
								"조직 이름으로 모든 하위 조직 이름을 조회하는 중 오류가 발생했습니다! Unit:" + wrapIn.getQ_unitName());
						result.error(exception);
						logger.error(e, currentPerson, request, null);
					}
					if (unitNames_tmp != null && unitNames_tmp.size() > 0) {
						for (String unitName : unitNames_tmp) {
							unitNames.add(unitName);
						}
					}
					wrapIn.setUnitNames(unitNames);
				}

				if (check ) {
					unUnitNameList = getUnUnitNameList();
					personNameList = getUnPersonNameList();
					// 데이터베이스에서 조건에 맞는 한 페이지 데이터 객체 조회
					//detailList = business.getAttendanceDetailFactory().listIdsNextWithFilter(id, count, sequence, wrapIn);
					detailList = business.getAttendanceDetailFactory().listIdsNextWithFilterUn(id, count, sequence, wrapIn,unUnitNameList,personNameList);
					// 데이터베이스에서 조건에 맞는 객체 총수 조회
					//total = business.getAttendanceDetailFactory().getCountWithFilter(wrapIn);
					total = business.getAttendanceDetailFactory().getCountWithFilterUn(wrapIn,unUnitNameList,personNameList);
					// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
					wraps = Wo.copier.copy(detailList);
				}

				if( scheduleSetting == null ){
					scheduleSetting = scheduleSetting_top;
				}

				if (check && ListTools.isNotEmpty( wraps )) {
					Integer signProxy = 1;
					List<AttendanceAppealInfo> appealInfos = null;
					AttendanceAppealAuditInfo appealAuditInfo = null;
					List<WoAttendanceAppealInfo> woAppealInfos = null;
					for( Wo detail : wraps ){
						if ( scheduleSetting != null ) {
							signProxy = scheduleSetting.getSignProxy();
						}
						detail.setSignProxy( signProxy );

						//이의 신청 정보 확인 및 보완
						if( detail.getAppealStatus() != 0 ){
							//십중팔구 이미 이의 신청했으므로 이의 신청 정보 조회
							appealInfos = attendanceAppealInfoServiceAdv.listWithDetailId( detail.getId() );
							if(ListTools.isNotEmpty( appealInfos ) ){
								woAppealInfos = WoAttendanceAppealInfo.copier.copy( appealInfos );
							}
							if(ListTools.isNotEmpty( woAppealInfos ) ){
								for( WoAttendanceAppealInfo woAppealInfo : woAppealInfos ){
									appealAuditInfo = attendanceAppealInfoServiceAdv.getAppealAuditInfo( woAppealInfo.getId() );
									if( appealAuditInfo != null ){
										woAppealInfo.setAppealAuditInfo( WoAttendanceAppealAuditInfo.copier.copy( appealAuditInfo ));
									}
								}
							}
							// 이의 신청 후 심사자가 없어졌으면 다시 보완
							String currentProcessor = "";
							if (woAppealInfos != null && !woAppealInfos.isEmpty()) {
								if (woAppealInfos.get(0).getAppealAuditInfo() != null) {
									currentProcessor = woAppealInfos.get(0).getAppealAuditInfo().getCurrentProcessor();
								}
							}
							detail.setAppealProcessor(currentProcessor);
							detail.setAppealInfos(woAppealInfos);
						}
					}
				}
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		result.setCount(total);
		result.setData(wraps);
		return result;
	}

	public static class Wi extends WrapInFilter{

	}

	public static class Wo extends AttendanceDetail {

		private static final long serialVersionUID = -5076990764713538973L;

		@FieldDescribe("직원 소속 조직의 근무 배정 기록 정책: 1-2회 기록(오전 출근, 오후 퇴근) 2-3회 기록(오전 출근, 오후 퇴근, 정오 1회 포함 총 3회) 3-4회 기록(오전·오후 모두 출근·퇴근 기록)")
		private Integer signProxy = 1;

		@FieldDescribe("근태 이의 신청 내용")
		private List<WoAttendanceAppealInfo> appealInfos = null;

		public List<WoAttendanceAppealInfo> getAppealInfos() { return appealInfos; }

		public void setAppealInfos(List<WoAttendanceAppealInfo> appealInfos) { this.appealInfos = appealInfos; }

		public Integer getSignProxy() {
			return signProxy;
		}

		public void setSignProxy(Integer signProxy) {
			this.signProxy = signProxy;
		}

		public static WrapCopier<AttendanceDetail, Wo> copier = WrapCopierFactory.wo(AttendanceDetail.class, Wo.class,
				null, JpaObject.FieldsInvisible);
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