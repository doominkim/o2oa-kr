package com.x.hotpic.assemble.control.jaxrs;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;
import javax.servlet.annotation.WebFilter;

/**
 * web 서비스 필터, 지정한 URL을 사용자 인증이 필요한 서비스로 정의하며 사용자가 로그인하지 않으면 해당 서비스에 접근할 수 없음
 */
@WebFilter(urlPatterns = { "/jaxrs/user/*", "/jaxrs/manager/*" }, asyncSupported = true)
public class JaxrsManagerUserFilter extends CipherManagerUserJaxrsFilter {
}
