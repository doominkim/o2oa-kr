package com.x.attendance.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.entity.AttendanceAppealInfo;

public class AttendanceNoticeService {

	/**
	 * 신청 승인, 신청자에게 알림
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @param person
	 * @throws Exception
	 */
	public void notifyAttendanceAppealAcceptMessage(AttendanceAppealInfo attendanceAppealInfo, String person)
			throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getEmpName();
//			String messageContent = "안녕하세요. [" + attendanceAppealInfo.getRecordDateString() + "] 근태 결과 이의 신청이 승인되었습니다.";
			if (StringUtils.isNotEmpty( targetName )) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if ( StringUtils.isNotEmpty( name )) {
						// @Todo
//						AttendanceAppealAcceptMessage message = new AttendanceAppealAcceptMessage( name, attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent );
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 신청 반려, 신청자에게 알림
	 * 
	 * @param business
	 * @param meeting
	 * @param person
	 * @throws Exception
	 */
	public void notifyAttendanceAppealRejectMessage(AttendanceAppealInfo attendanceAppealInfo, String person)
			throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getEmpName();
//			String messageContent = "안녕하세요. [" + attendanceAppealInfo.getRecordDateString() + "] 근태 결과 이의 신청이 반려되었습니다.";
			if ( StringUtils.isNotEmpty( targetName )) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if ( StringUtils.isNotEmpty( name )) {
//						AttendanceAppealRejectMessage message = new AttendanceAppealRejectMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 처리할 이의 신청 접수 - 처리자1
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @throws Exception
	 */
	public void notifyAttendanceAppealProcessness1Message(AttendanceAppealInfo attendanceAppealInfo) throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getCurrentProcessor();
//			String messageContent = "신청자 " + attendanceAppealInfo.getEmpName() + "의 근태 결과 이의 신청이 접수되었습니다. 신청 유형은 [" + attendanceAppealInfo.getAppealReason() + "]입니다. 검토해 주십시오!";
			if ( StringUtils.isNotEmpty( targetName )) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if ( StringUtils.isNotEmpty( name )) {
//						AttendanceAppealInviteMessage message = new AttendanceAppealInviteMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}

	/**
	 * 처리할 이의 신청 접수 - 처리자2
	 * 
	 * @param business
	 * @param attendanceDetail
	 * @throws Exception
	 */
	public void notifyAttendanceAppealProcessness2Message(AttendanceAppealInfo attendanceAppealInfo) throws Exception {
		if (attendanceAppealInfo != null) {
			String targetName = attendanceAppealInfo.getCurrentProcessor();
//			String messageContent = "신청자 " + attendanceAppealInfo.getEmpName() + "의 근태 결과 이의 신청이 접수되었습니다. 신청 유형은 [" + attendanceAppealInfo.getAppealReason() + "]입니다. 재검토해 주십시오!";
			if ( StringUtils.isNotEmpty( targetName )) {
				String[] array = targetName.split(",");
				for (String name : array) {
					if ( StringUtils.isNotEmpty( name )) {
//						AttendanceAppealInviteMessage message = new AttendanceAppealInviteMessage(name,
//								attendanceAppealInfo.getId(), attendanceAppealInfo.getDetailId(), messageContent);
//						Collaboration.send(message);
					}
				}
			}
		} else {
			throw new Exception("attendanceAppealInfo is null, can not send message!");
		}
	}
}
