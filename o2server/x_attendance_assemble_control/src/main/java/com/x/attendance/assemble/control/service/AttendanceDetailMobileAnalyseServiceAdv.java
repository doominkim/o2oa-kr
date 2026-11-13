package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * @author LIYI
 *
 */
public class AttendanceDetailMobileAnalyseServiceAdv {
	private AttendanceDetailAnalyseService attendanceDetailAnalyseService = new AttendanceDetailAnalyseService();
	private AttendanceDetailMobileAnalyseService attendanceDetailMobileAnalyseService = new AttendanceDetailMobileAnalyseService();
	
	/**
	 * 1. 해당 사용자의 당일 근태 데이터가 있는지 조회하고, 있으면 시간 갱신
	 * 2. 현재 체크 기록이 출근 체크이면 기록만 하고 분석하지 않음
	 * 3. 현재 체크 기록이 퇴근 체크이면 근태 정보를 갱신한 뒤 근태 기록 분석
	 * 4. 해당 사용자에게 아직 미분석 근태 데이터가 있는지 조회하고, 날짜가 오늘이 아니면 모두 분석
	 *
	 * @param id
	 * @return
	 */
	public Boolean analyseAttendanceDetailMobile( String id, Boolean debugger ) throws Exception {
		Business business = null;
		AttendanceDetail attendanceDetail = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		List<String> ids = null;
		EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
		try{
			business = new Business(emc);
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				attendanceDetail = attendanceDetailMobileAnalyseService.composeAttendanceDetailMobile( emc, id );
				if( attendanceDetail != null ){
					if( StringUtils.isNotEmpty( attendanceDetail.getOffDutyTime() ) ){
//						attendanceDetailAnalyseService.analyseAttendanceDetail(emc, attendanceDetail, debugger );
					}
				}
				
				ids = business.getAttendanceDetailFactory().listAnalysenessDetailsByEmployee( attendanceDetailMobile.getEmpName() );
				if( ids != null && !ids.isEmpty() ){
					for( String detailId : ids ){
						attendanceDetail = emc.find( detailId, AttendanceDetail.class );
						if( attendanceDetail != null ){
							//모바일 체크 기록과 같은 날이 아니면 모두 분석해야 함
							if( !attendanceDetail.getRecordDateString().equals( attendanceDetailMobile.getRecordDateString() )){
								attendanceDetailAnalyseService.analyseAttendanceDetail( emc, attendanceDetail, debugger );
							}
						}
					}
				}
			}
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				emc.beginTransaction( AttendanceDetailMobile.class );
				attendanceDetailMobile.setRecordStatus( 1 );
				emc.check( attendanceDetailMobile, CheckPersistType.all );
				emc.commit();
			}
		} catch ( Exception e ) {
			attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
			if( attendanceDetailMobile != null ){
				emc.beginTransaction( AttendanceDetailMobile.class );
				attendanceDetailMobile.setRecordStatus( -1 );
				emc.check( attendanceDetailMobile, CheckPersistType.all );
				emc.commit();
			}
			throw e;
		}
		return null;
	}
}
