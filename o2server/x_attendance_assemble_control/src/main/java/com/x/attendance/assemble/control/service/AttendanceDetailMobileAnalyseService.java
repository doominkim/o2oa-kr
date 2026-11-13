package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.annotation.CheckPersistType;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * @author LIYI
 *
 */
public class AttendanceDetailMobileAnalyseService {
	
	private DateOperation dateOperation = new DateOperation();
	

	/**
	 * 1. 해당 사용자의 당일 근태 데이터가 있는지 조회하고, 있으면 시간 갱신
	 * 
	 * @param emc
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public AttendanceDetail composeAttendanceDetailMobile( EntityManagerContainer emc, String id ) throws Exception {
		String recordDateString = null;
		String empName = null;
		Date signTime = null;
		Date createTime = null;
		Date onDutyTime = null;
		Date offDutyTime = null;
		List<String> ids = null;
		AttendanceDetail attendanceDetail = null;
		AttendanceDetailMobile attendanceDetailMobile = null;
		Business business = new Business(emc);
		attendanceDetailMobile = emc.find( id, AttendanceDetailMobile.class );
		if( attendanceDetailMobile != null ){
			createTime = attendanceDetailMobile.getCreateTime();
			if( createTime == null ) {
				createTime = attendanceDetailMobile.getUpdateTime();
			}
			signTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
			recordDateString = attendanceDetailMobile.getRecordDateString();
			empName = attendanceDetailMobile.getEmpName();
			ids = business.getAttendanceDetailFactory().listByEmployeeNameAndDate( empName, recordDateString );
			if( ids != null && !ids.isEmpty() ){
				for( String detailId : ids ){
					attendanceDetail = emc.find( detailId, AttendanceDetail.class );
					if( attendanceDetail != null ){
						emc.beginTransaction( AttendanceDetail.class );
						attendanceDetail.setRecordStatus( 0 );//미분석으로 설정
						if( "上班打卡".equals( attendanceDetailMobile.getSignDescription() )){
							if( attendanceDetail.getOnDutyTime() == null ){
								onDutyTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
								if( signTime.before( onDutyTime )){
									attendanceDetail.setOnDutyTime( attendanceDetailMobile.getSignTime() );
								}
							}
						}else{
							//출근 체크가 아니면 모두 퇴근 체크로 갱신
							if( attendanceDetail.getOffDutyTime() == null ){
								offDutyTime = dateOperation.getDateFromString( attendanceDetailMobile.getSignTime() );
								if( signTime.after( offDutyTime )){
									attendanceDetail.setOffDutyTime( attendanceDetailMobile.getSignTime() );
								}
							}
						}
						emc.check( attendanceDetail , CheckPersistType.all );
						emc.commit();
						return attendanceDetail;
					}
				}
			}else{
				//새 출퇴근 기록 생성
				attendanceDetail = new AttendanceDetail();
				attendanceDetail.setEmpNo( attendanceDetailMobile.getEmpNo() );
				attendanceDetail.setEmpName( attendanceDetailMobile.getEmpName() );
				if( createTime != null ) {
					attendanceDetail.setYearString( dateOperation.getYear( createTime ) );
					attendanceDetail.setMonthString( dateOperation.getMonth( createTime ) );			
				}
				attendanceDetail.setRecordDateString( attendanceDetailMobile.getRecordDateString() );
				if( "上班打卡".equals( attendanceDetailMobile.getSignDescription() )){
					attendanceDetail.setOnDutyTime( attendanceDetailMobile.getSignTime() );
				}else{
					//출근 체크가 아니면 모두 퇴근 체크로 갱신
					attendanceDetail.setOffDutyTime( attendanceDetailMobile.getSignTime() );
				}
				attendanceDetail.setRecordStatus( 0 );
				attendanceDetail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
				
				emc.beginTransaction( AttendanceDetail.class );
				emc.persist( attendanceDetail , CheckPersistType.all );
				emc.commit();
				
				return attendanceDetail;
			}
		}else{
			throw new Exception("record is not exists!id:" + id );
		}
		return null;
	}
}
