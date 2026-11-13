package com.x.attendance.assemble.control.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetailMobile;
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

/**
 * 근태 출퇴근 기록 분석 서비스 클래스
 * 2. 하루 3회 체크: 출근·퇴근 2회 외에 점심 휴식 시간에도 1회 체크하여 직원의 회사 내 활동 확인
 */
class ComposeDetailWithMobileInSignProxy2 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy2.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 점심 휴식 시간 기록이 있는 완전한 출퇴근 기록 구성
	 * 1. 출근 기록 시간: 가장 이른 기록이 출근 기록 시간
	 * 2. 점심 기록 시간: 점심 휴식 시작~종료 시간 내 가장 늦은 기록을 점심 기록 시간으로 사용
	 * 3. 퇴근 기록 시간: 가장 늦은 기록 사용
	 * @param mobileDetails
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceDetail compose(List<AttendanceDetailMobile> mobileDetails, AttendanceScheduleSetting scheduleSetting, Boolean debugger) throws Exception {
		AttendanceDetailMobile mobileDetail = mobileDetails.get( 0 );
		String onDutyTime = getOnDutyTime( mobileDetails );
		String offDutyTime = getOffDutyTime( mobileDetails );
		String recordAddress = this.getRecordAddress(mobileDetails);
		String optMachineType = this.getOptMachineType(mobileDetails);

		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime() )){
			scheduleSetting.setMiddayRestStartTime("11:30");
		}

		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime() )){
			scheduleSetting.setMiddayRestStartTime("13:30");
		}

		String afternonnOnDutyTime =  getAfternoonOnDutyTime( mobileDetails, scheduleSetting.getMiddayRestStartTime(), scheduleSetting.getMiddayRestEndTime() );

		//직원의 당일 근태 출퇴근 기록 구성
		AttendanceDetail detail = new AttendanceDetail();
		detail.setEmpNo( mobileDetail.getEmpNo() );
		detail.setEmpName( mobileDetail.getEmpName() );
		detail.setRecordAddress(recordAddress);
		detail.setOptMachineType(optMachineType);
		if( mobileDetail.getRecordDate() != null ) {
			detail.setYearString( dateOperation.getYear( mobileDetail.getRecordDate() ) );
			detail.setMonthString( dateOperation.getMonth( mobileDetail.getRecordDate() ) );
		}
		detail.setRecordDateString( mobileDetail.getRecordDateString() );
		detail.setOnDutyTime( onDutyTime ); //가장 이른 기록을 당일 출근 기록으로 사용
		if( StringUtils.equals( afternonnOnDutyTime, onDutyTime )){
			detail.setAfternoonOnDutyTime( null );
		}else{
			detail.setAfternoonOnDutyTime( afternonnOnDutyTime ); //점심 휴식 기록 시간
		}
		if( StringUtils.equals(offDutyTime, onDutyTime ) || StringUtils.equals( afternonnOnDutyTime, offDutyTime ) ){
			detail.setOffDutyTime( null );
		}else{
			detail.setOffDutyTime( offDutyTime ); //가장 늦은 기록을 당일 퇴근 기록으로 사용
		}
		detail.setRecordStatus( 0 );
		detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
		return detail;
	}

	/**
	 * 점심 휴식 시작~종료 시간 내 가장 늦은 기록을 점심 휴식 기록 시간으로 사용
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @param middayRestEndTime
	 * @return
	 */
	private String getAfternoonOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime, String middayRestEndTime) throws Exception {
		Date moningOndutyTime = null;
		Date signTime = null, restStartTime=null, restEndTime = null;
		String resultTime = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				restStartTime = dateOperation.getDateFromString( middayRestStartTime );
				restEndTime = dateOperation.getDateFromString( middayRestEndTime );
				if( moningOndutyTime != null && signTime != null && restStartTime.before( signTime ) && restEndTime.after( signTime )) {
					if( moningOndutyTime.before( signTime ) ){
						moningOndutyTime = signTime;
						resultTime = detailMobile.getSignTime();
					}
				}else if( moningOndutyTime == null ){
					moningOndutyTime = signTime;
					resultTime = detailMobile.getSignTime();
				}
			}
		}
		return resultTime;
	}

	/**
	 * 가장 늦은 기록 시간을 퇴근 기록 시간으로 설정
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOffDutyTime( List<AttendanceDetailMobile> mobileDetails ) throws Exception {
		Date offDutyTime = null;
		Date signTime = null;
		String offDutyTimeString = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
				if( offDutyTime != null && signTime != null && offDutyTime.before( signTime )) {
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}else if( offDutyTime == null ){
					offDutyTime = signTime;
					offDutyTimeString = detailMobile.getSignTime();
				}
			}
		}
		return offDutyTimeString;
	}

	/**
	 * 첫 기록 시간을 출근 기록 시간으로 설정
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOnDutyTime(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		Date onDutyTime = null;
		Date signTime = null;
		String onDutyTimeString = null;
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			if( onDutyTime != null && signTime != null && onDutyTime.after( signTime )) {
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}else if( onDutyTime == null ){
				onDutyTime = signTime;
				onDutyTimeString = detailMobile.getSignTime();
			}
		}
		return onDutyTimeString;
	}

	/**
	 * 출퇴근 기록 주소 조회
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getRecordAddress(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		String mobileAddress = "";
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			String recordAddress = detailMobile.getRecordAddress();
			String workAddress = detailMobile.getWorkAddress();
			String oneAddress = "";
			if(StringUtils.isNotEmpty(workAddress)){
				oneAddress = workAddress;
			}else{
				oneAddress = recordAddress;
			}
			if(StringUtils.isEmpty(mobileAddress)){
				mobileAddress = oneAddress;
			}else{
				if(StringUtils.isNotEmpty(oneAddress) && !StringUtils.contains(mobileAddress,oneAddress)){
					mobileAddress = mobileAddress+","+oneAddress;
				}
			}
		}
		return mobileAddress;
	}

	/**
	 * 기기 정보 조회
	 * @param mobileDetails
	 * @return
	 * @throws Exception
	 */
	private String getOptMachineType(List<AttendanceDetailMobile> mobileDetails) throws Exception {
		String optMachineType = "";
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			String tmpOptMachineType = detailMobile.getOptMachineType();
			if(StringUtils.isNotEmpty(tmpOptMachineType)){
				if(StringUtils.isEmpty(optMachineType)){
					optMachineType = tmpOptMachineType;
				}else{
					if(!StringUtils.contains(optMachineType,tmpOptMachineType)){
						optMachineType = optMachineType+","+tmpOptMachineType;
					}
				}
			}
		}
		return optMachineType;
	}
}
