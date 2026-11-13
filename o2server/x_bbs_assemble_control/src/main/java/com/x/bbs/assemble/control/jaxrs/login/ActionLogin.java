package com.x.bbs.assemble.control.jaxrs.login;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.login.exception.ExceptionInsufficientPermissions;
import com.x.bbs.assemble.control.jaxrs.login.exception.ExceptionUserLogin;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

/**
 * 모바일 사용자 포럼 정보 접근, 홈의 모든 정보를 통합 익명 사용자 접근 가능
 *
 * @return
 */
public class ActionLogin extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionLogin.class);

	public ActionResult<RoleAndPermission> execute(@Context HttpServletRequest request,
			EffectivePerson effectivePerson) {

		ActionResult<RoleAndPermission> result = new ActionResult<>();
		Boolean isBBSManager = false;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Boolean check = true;

		if (check) {
			if ("anonymous".equalsIgnoreCase(effectivePerson.getTokenType().name())) {
				if(StringUtils.equalsAnyIgnoreCase( ThisApplication.CONFIG_BBS_ANONYMOUS_PERMISSION, "YES")){
					try {
						operationRecordService.loginOperation("anonymous", hostIp, hostName);
						result.setData(new RoleAndPermission());
					} catch (Exception e) {
						Exception exception = new ExceptionUserLogin(e, "anonymous");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}else{
					Exception exception = new ExceptionUserLogin("커뮤니티 리소스에 대한 익명 접근이 허용되지 않습니다.");
					result.error(exception);
				}
			} else {
				RoleAndPermission roleAndPermission = null;
				try {
					operationRecordService.loginOperation(effectivePerson.getDistinguishedName(), hostIp, hostName);
					roleAndPermission = userPermissionService
							.getUserRoleAndPermissionForLogin(effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					Exception exception = new ExceptionUserLogin(e, effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				try {
					isBBSManager = userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(),
							ThisApplication.BBSMANAGER);
				} catch (Exception e) {
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(),
							ThisApplication.BBSMANAGER);
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
				if (roleAndPermission != null) {
					roleAndPermission.setIsBBSManager(isBBSManager);
				}
				result.setData(roleAndPermission);
			}
		}
		return result;
	}

//	public static class Wi extends GsonPropertyObject implements Serializable {
//
//		private static final long serialVersionUID = -5076990764713538973L;
//
//	}
}
