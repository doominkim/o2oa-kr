package com.x.calendar.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerJaxrsFilter;

/**
 * web 서비스 필터, 지정한 URL을 사용자 인증이 필요한 서비스로 정의하며 사용자가 로그인하지 않으면 해당 서비스에 접근할 수 없음
 * 
 * @author O2LEE *
 */
@WebFilter(urlPatterns = { "/jaxrs/message/*" }, asyncSupported = true)
public class MessageJaxrsFilter extends CipherManagerJaxrsFilter {

}