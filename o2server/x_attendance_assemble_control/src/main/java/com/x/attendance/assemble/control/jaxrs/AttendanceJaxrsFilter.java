package com.x.attendance.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

/**
 * web 서비스 필터, 지정한 URL을 사용자 인증이 필요한 서비스로 정의하며 사용자가 로그인하지 않으면 해당 서비스에 접근할 수 없음
 */
@WebFilter(urlPatterns = {
		"/jaxrs/workplace/*",
		"/jaxrs/attendanceadmin/*",
		"/jaxrs/attendancedetail/*",
		"/jaxrs/attendancedetail/mobile/*",
		"/jaxrs/attendancedetailbakup/*",
		"/jaxrs/attendanceappealInfo/*",
		"/jaxrs/attendanceimportfileinfo/*",
		"/jaxrs/file/*",
		"/jaxrs/attendanceschedulesetting/*",
		"/jaxrs/attendancesetting/*",
		"/jaxrs/attendanceworkdayconfig/*",
		"/jaxrs/attendanceselfholiday/*",
		"/jaxrs/fileimport/*",
		"/jaxrs/statistic/*",
		"/jaxrs/statisticshow/*",
		"/jaxrs/attendancestatisticalcycle/*",
		"/jaxrs/attendancestatisticrequirelog/*",
		"/jaxrs/attendanceemployeeconfig/*",
		"/jaxrs/uuid/*",
		"/servlet/*"
}, asyncSupported = true)
public class AttendanceJaxrsFilter extends CipherManagerUserJaxrsFilter {
	
}