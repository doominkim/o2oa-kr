package com.x.cms.assemble.control.jaxrs;

import javax.servlet.annotation.WebFilter;

import com.x.base.core.project.jaxrs.CipherManagerUserJaxrsFilter;

/**
 * web 서비스 필터, 지정한 URL을 사용자 인증이 필요한 서비스로 정의하며 사용자가 로그인하지 않으면 해당 서비스에 접근할 수 없음
 */
@WebFilter(urlPatterns = {
        "/jaxrs/appcategoryadmin/*",
        "/jaxrs/appcategorypermission/*",
        "/jaxrs/appinfo/*",
        "/jaxrs/appconfig/*",
        "/jaxrs/categoryinfo/*",
        "/jaxrs/data/*",
        "/jaxrs/document/*",
        "/jaxrs/fileinfo/*",
        "/jaxrs/file/*",
        "/jaxrs/form/*",
        "/jaxrs/formversion/*",
        "/jaxrs/view/*",
        "/jaxrs/viewcategory/*",
        "/jaxrs/viewfieldconfig/*",
        "/jaxrs/image/*",
        "/jaxrs/log/*",
        "/jaxrs/design/appdict/*",
        "/jaxrs/surface/appdict/*",
        "/jaxrs/script/*",
        "/jaxrs/scriptversion/*",
        "/jaxrs/uuid/*",
        "/jaxrs/viewrecord/*",
        "/jaxrs/searchfilter/*",
        "/jaxrs/templateform/*",
        "/jaxrs/input/*",
        "/jaxrs/output/*",
        "/jaxrs/permission/*",
        "/jaxrs/docpermission/*",
        "/jaxrs/comment/*",
        "/jaxrs/commend/*",
        "/servlet/*"
}, asyncSupported = true)
public class CmsJaxrsFilter extends CipherManagerUserJaxrsFilter {

}
