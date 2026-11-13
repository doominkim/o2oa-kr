package com.x.ai.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.AnonymousCipherManagerUserJaxrsFilter;

import javax.servlet.annotation.WebFilter;

/**
 * web 서비스 필터, 로그인 없이 접근 가능
 * @author sword
 */
@WebFilter(urlPatterns = {
}, asyncSupported = true )
public class AnonymousJaxrsServicePathFilter extends AnonymousCipherManagerUserJaxrsFilter {
}
