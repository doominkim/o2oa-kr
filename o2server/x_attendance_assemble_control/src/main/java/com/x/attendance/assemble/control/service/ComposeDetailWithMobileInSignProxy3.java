package com.x.attendance.assemble.control.service;

import java.util.ArrayList;
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
 * 3. 하루 4회 체크: 오전 출근, 오전 퇴근, 오후 출근, 오후 퇴근 4회 체크
 */
class ComposeDetailWithMobileInSignProxy3 {
	
	private static  Logger logger = LoggerFactory.getLogger( ComposeDetailWithMobileInSignProxy3.class );
	private DateOperation dateOperation = new DateOperation();

	/**
	 * 점심 휴식 시간 기록이 있는 완전한 출퇴근 기록 구성
	 * 1. 오전 출근 기록 시간: 오전 근무 시간대 중 가장 이른 기록 시간이 오전 출근 기록 시간
	 * 2. 오전 퇴근 기록 시간: 오전 근무 시간대 중 가장 늦은 기록 시간이 오전 퇴근 기록 시간
	 * 3. 오후 출근 기록 시간: 오후 근무 시간대 중 가장 이른 기록 시간이 오후 출근 기록 시간
	 * 3. 오후 퇴근 기록 시간: 오후 근무 시간대 중 가장 늦은 기록 시간이 오후 퇴근 기록 시간
	 * @param mobileDetails
	 * @param scheduleSetting
	 * @param debugger
	 * @return
	 * @throws Exception
	 */
	public AttendanceDetail compose(List<AttendanceDetailMobile> mobileDetails, AttendanceScheduleSetting scheduleSetting, Boolean debugger) throws Exception {
		AttendanceDetailMobile mobileDetail = mobileDetails.get( 0 );
		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestStartTime() )){
			scheduleSetting.setMiddayRestStartTime("11:30");
		}
		if(StringUtils.isEmpty( scheduleSetting.getMiddayRestEndTime() )){
			scheduleSetting.setMiddayRestEndTime("13:30");
		}

		List<String> usedMobileDetailIds = new ArrayList<>();
		String middayRestStartTime = scheduleSetting.getMiddayRestStartTime();
		String middayRestEndTime = scheduleSetting.getMiddayRestEndTime();
		String onWorkTimeStr = scheduleSetting.getOnDutyTime();
		String offWorkTimeStr = scheduleSetting.getOffDutyTime();
		// 출근 기록 데이터 계산
		String onDutyTime = getOnDutyTime( mobileDetails, middayRestStartTime , usedMobileDetailIds);
		// 점심 퇴근 기록 데이터 계산
		String morningOffdutyTime = getMorningOffDutyTime(mobileDetails, onWorkTimeStr, middayRestEndTime, usedMobileDetailIds);
		// 점심 출근 기록 데이터 계산
		String afternoonOndutyTime = getAfternoonOnDutyTime( mobileDetails, middayRestStartTime, offWorkTimeStr , usedMobileDetailIds);
		// 퇴근 기록 데이터 계산
		String offDutyTime = getOffDutyTime( mobileDetails, middayRestEndTime , usedMobileDetailIds);

		String recordAddress = this.getRecordAddress(mobileDetails);
		String optMachineType = this.getOptMachineType(mobileDetails);

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
		detail.setOnDutyTime( onDutyTime ); //오전 출근 기록
		detail.setMorningOffDutyTime( morningOffdutyTime ); //오전 퇴근 기록
		detail.setAfternoonOnDutyTime( afternoonOndutyTime ); //오후 출근 기록
		detail.setOffDutyTime( offDutyTime ); //오후 퇴근 기록
		detail.setRecordStatus( 0 );
		detail.setBatchName( "FromMobile_" + dateOperation.getNowTimeChar() );
		return detail;
	}

	/**
	 * 오전 퇴근 기록 시간 계산: 오전 출근 시간에 가장 가까운 기록 시간
	 * 오전 출근 이후부터 오후 출근 이전까지 가장 늦은 기록 사용
	 * 2023-1-6 로직 수정: 가장 늦은 기록이 아니라 가장 이른 기록으로 변경(이미 사용한 기록은 제외되므로)
	 *
	 * @param mobileDetails
	 * @param onWorkTimeStr 오전 출근 시간
	 * @param middayRestEndTime 오후 출근 시간
	 * @param usedMobileDetailIds    이미 사용한 출퇴근 기록
	 * @return
	 */
	private String getMorningOffDutyTime(List<AttendanceDetailMobile> mobileDetails, String onWorkTimeStr, String middayRestEndTime, List<String> usedMobileDetailIds) throws Exception {
		Date moningOffdutyTime = null;
		Date signTime = null, afternoonOnDutyTime = null, onWorkTime = null;
		String result = null;
		String lastUsedDetailId = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				// 이미 처리한 데이터 건너뜀
				if (usedMobileDetailIds.contains(detailMobile.getId())) {
					continue;
				}
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				afternoonOnDutyTime = dateOperation.getDateFromString( middayRestEndTime );
				onWorkTime = dateOperation.getDateFromString( onWorkTimeStr );

				//오전 출근 전과 오후 출근 후의 기록 제외
				if( signTime.before( onWorkTime ) || signTime.after( afternoonOnDutyTime )){
					continue;
				}
				if( moningOffdutyTime != null && signTime != null && moningOffdutyTime.after( signTime ) ) {
					moningOffdutyTime = signTime;
					result = detailMobile.getSignTime();
					lastUsedDetailId = detailMobile.getId();
				}else if( moningOffdutyTime == null ){
					moningOffdutyTime = signTime;
					result = detailMobile.getSignTime();
					lastUsedDetailId = detailMobile.getId();
				}
			}
		}
		if (StringUtils.isNotEmpty(lastUsedDetailId)) {
			usedMobileDetailIds.add(lastUsedDetailId);
		}
		return result;
	}

	/**
	 * 오후 출근 기록 계산: 오후 출근 시간에 가장 가까운 기록 시간
	 * 오전 퇴근 이후, 오후 퇴근 이전 가장 이른 기록 시간 사용
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @param offWorkTimeStr
	 * @param usedMobileDetailIds    이미 사용한 출퇴근 기록
	 * @return
	 */
	private String getAfternoonOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime, String offWorkTimeStr, List<String> usedMobileDetailIds ) throws Exception {
		Date afternoonOnDutyTime = null;
		Date signTime = null, morningOffWorkTime=null, offWorkTime = null;
		String result = null;
		String lastUsedDetailId = null;
		if( ListTools.isNotEmpty( mobileDetails ) && mobileDetails.size() >=2 ) {
			for( AttendanceDetailMobile detailMobile : mobileDetails ) {
				// 이미 처리한 데이터 건너뜀
				if (usedMobileDetailIds.contains(detailMobile.getId())) {
					continue;
				}
				signTime = dateOperation.getDateFromString( detailMobile.getSignTime() );
				morningOffWorkTime = dateOperation.getDateFromString( middayRestStartTime );
				offWorkTime = dateOperation.getDateFromString( offWorkTimeStr );

				//오전 퇴근 이전 기록과 오후 퇴근 이후 기록은 제외하고, 오전 퇴근 후부터 오후 퇴근 전까지의 기록 사용
				if( signTime.before(morningOffWorkTime) || signTime.after( offWorkTime )){
					continue;
				}
				if( afternoonOnDutyTime != null && signTime != null && signTime.before( afternoonOnDutyTime )) {
					afternoonOnDutyTime = signTime;
					result = detailMobile.getSignTime();
					lastUsedDetailId = detailMobile.getId();
				}else if( afternoonOnDutyTime == null ){
					afternoonOnDutyTime = signTime;
					result = detailMobile.getSignTime();
					lastUsedDetailId = detailMobile.getId();
				}
			}
		}
		if (StringUtils.isNotEmpty(lastUsedDetailId)) {
			usedMobileDetailIds.add(lastUsedDetailId);
		}
		return result;
	}

	/**
	 * 오후 출근 이후 모든 기록 중 가장 늦은 기록을 당일 오후 퇴근 기록으로 사용
	 *
	 * @param mobileDetails
	 * @param usedMobileDetailIds    이미 사용한 출퇴근 기록
	 * @return
	 * @throws Exception
	 */
	private String getOffDutyTime( List<AttendanceDetailMobile> mobileDetails, String middayRestEndTime, List<String> usedMobileDetailIds  ) throws Exception {
		Date offDutyTime = null, signTime = null, afterOndutyTime=null;
		String result = null;
		String lastUsedDetailId = null;
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			// 이미 처리한 데이터 건너뜀
			if (usedMobileDetailIds.contains(detailMobile.getId())) {
				continue;
			}
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			afterOndutyTime = dateOperation.getDateFromString( middayRestEndTime );
			//오후 출근 이전 기록은 오후 퇴근 기록에 포함하지 않음
			if( signTime.before( afterOndutyTime )){
				continue;
			}
			if( offDutyTime != null && signTime != null && offDutyTime.before( signTime )) {
				offDutyTime = signTime;
				result = detailMobile.getSignTime();
				lastUsedDetailId = detailMobile.getId();
			}else if( offDutyTime == null ){
				offDutyTime = signTime;
				result = detailMobile.getSignTime();
				lastUsedDetailId = detailMobile.getId();
			}
		}
		if (StringUtils.isNotEmpty(lastUsedDetailId)) {
			usedMobileDetailIds.add(lastUsedDetailId);
		}
		return result;
	}

	/**
	 * 첫 기록 시간을 출근 기록 시간으로 설정
	 * 첫째 구간과 둘째 구간에서만 가능하며, 오전 퇴근 시간 이전에 기록해야 당일 오전 출근 기록으로 인정
	 * @param mobileDetails
	 * @param middayRestStartTime
	 * @param usedMobileDetailIds 이미 사용한 출퇴근 기록
	 * @return
	 * @throws Exception
	 */
	private String getOnDutyTime(List<AttendanceDetailMobile> mobileDetails, String middayRestStartTime , List<String> usedMobileDetailIds) throws Exception {
		Date onDutyTime = null, signTime = null, morningOffdutyTime=null;
		String result = null;

		String lastUsedId = null;
		for( AttendanceDetailMobile detailMobile : mobileDetails ) {
			signTime = dateOperation.getDateFromString(detailMobile.getSignTime() );
			morningOffdutyTime = dateOperation.getDateFromString( middayRestStartTime );
			//오전 퇴근 시간 이후의 기록 제외
			if( signTime.after( morningOffdutyTime )){
				continue;
			}
			if( onDutyTime != null && signTime != null && onDutyTime.after( signTime )) {
				onDutyTime = signTime;
				result = detailMobile.getSignTime();
				lastUsedId = detailMobile.getId();
			}else if( onDutyTime == null ){
				onDutyTime = signTime;
				result = detailMobile.getSignTime();
				lastUsedId = detailMobile.getId();
			}
		}
		if (StringUtils.isNotEmpty(lastUsedId)) {
			usedMobileDetailIds.add(lastUsedId);
		}
		return result;
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
