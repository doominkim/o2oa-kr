package com.x.attendance.assemble.control.jaxrs.attendanceappealinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ExceptionWrapInConvert;
import com.x.attendance.entity.AttendanceAppealAuditInfo;
import com.x.attendance.entity.AttendanceAppealInfo;
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

//권한 기반 이의 신청 조회, 부서 관리자는 부서, 회사 관리자는 회사 범위만 조회
//근태 관리자 또는 최고 관리자는 전체 조회
public class ActionListNextWithFilterWithManager extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionListNextWithFilterWithManager.class);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id,
			Integer count, JsonElement jsonElement) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		Long total = 0L;
		List<AttendanceAppealInfo> detailList = null;
		WrapInFilterAppeal wrapIn = null;
		Boolean check = true;
		Boolean isManager = false;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, WrapInFilterAppeal.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionWrapInConvert(e, jsonElement);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}
		if (check) {
			try {
				EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				Business business = new Business(emc);

				// ID에 해당하는 기록의 sequence 조회
				Object sequence = null;
				if (id == null || "(0)".equals(id) || id.isEmpty()) {
					logger.debug(effectivePerson, ">>>>>>>>>>첫 페이지 조회, 전달된 id 없음");
				} else {
					if (!StringUtils.equalsIgnoreCase(id, StandardJaxrsAction.EMPTY_SYMBOL)) {
						sequence = PropertyUtils.getProperty(emc.find(id, AttendanceAppealInfo.class),  JpaObject.sequence_FIELDNAME);
					}
				}

//				isManager = business.isManager(effectivePerson);
				// 데이터베이스에서 조건에 맞는 한 페이지 데이터 객체 조회(현재 승인자 기준)
//				Map<String,List<String>> unitMap = business.getAttendanceAppealInfoFactory().listUnits(effectivePerson);
				detailList = business.getAttendanceAppealInfoFactory().listIdsNextWithFilterWithManager(id, count, sequence,
						wrapIn,null, false);
				// 데이터베이스에서 조건에 맞는 객체 총수 조회
				total = business.getAttendanceAppealInfoFactory().getCountWithFilterWithManager(wrapIn, null ,false);
				// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
				wraps = Wo.copier.copy(detailList);

				if(ListTools.isNotEmpty( wraps )){
					AttendanceAppealAuditInfo auditInfo = null;
					for( Wo wo : wraps ){
						auditInfo = attendanceAppealInfoServiceAdv.getAppealAuditInfo( wo.getId() );
						if( auditInfo != null ){
							wo.setAppealAuditInfo( WoAttendanceAppealAuditInfo.copier.copy( auditInfo ));
						}
					}
				}
				// 조회 목록 정렬
				result.setCount(total);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}
		result.setData(wraps);
		return result;
	}

	public static class Wo extends AttendanceAppealInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealInfo, Wo> copier = WrapCopierFactory.wo(AttendanceAppealInfo.class,
				Wo.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("근태 이의 신청 심사 내용")
		private WoAttendanceAppealAuditInfo appealAuditInfo = null;

		public WoAttendanceAppealAuditInfo getAppealAuditInfo() {
			return appealAuditInfo;
		}

		public void setAppealAuditInfo(WoAttendanceAppealAuditInfo appealAuditInfo) {
			this.appealAuditInfo = appealAuditInfo;
		}

	}

	public static class WoAttendanceAppealAuditInfo extends AttendanceAppealAuditInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<AttendanceAppealAuditInfo, WoAttendanceAppealAuditInfo> copier = WrapCopierFactory.wo(AttendanceAppealAuditInfo.class,
				WoAttendanceAppealAuditInfo.class, null, JpaObject.FieldsInvisible);

	}
}