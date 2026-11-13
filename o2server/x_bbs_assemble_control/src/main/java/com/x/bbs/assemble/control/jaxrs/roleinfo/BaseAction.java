package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

public class BaseAction extends StandardJaxrsAction {
	private static  Logger logger = LoggerFactory.getLogger(BaseAction.class);
	protected BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	protected BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSUserInfoService userInfoService = new BBSUserInfoService();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();

	protected void checkUserPermission(BindObject bindObject) throws Exception {
		// wrapIn.getBindObject()를 사용자 목록으로 파싱하여 각 사용자별 권한 분석
		List<String> userNames = new ArrayList<>();
		if (bindObject != null) {
			if ("组织".equals(bindObject.getObjectType())) {
				try {
					userNames = userManagerService.listPersonNamesWithUnitName(bindObject.getObjectName());
				} catch (Exception e) {
					throw e;
				}
			} else if ("群组".equals(bindObject.getObjectType())) {
				try {
					userNames = userManagerService.listPersonNamesWithGroupName(bindObject.getObjectName());
				} catch (Exception e) {
					throw e;
				}
			} else {
				// 사용자인 경우
				userNames.add(bindObject.getObjectName());
			}
		}
		if ( ListTools.isNotEmpty( userNames )) {
			for (String name : userNames) {
				checkUserPermission(name);
			}
		}
	}

	protected void checkUserPermission(String userName) {
		Gson gson = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		RoleAndPermission roleAndPermission = null;
		String permissionContent = null;

		roleAndPermission = new RoleAndPermission();
		roleAndPermission.setPerson(userName);
		// 해당 사용자의 역할과 권한 정보 확인
		try {
			roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
			roleAndPermission.setRoleInfoList(roleCodes);
		} catch (Exception e) {
			logger.warn("system list all role for user got an exception.");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( roleCodes )) {
			try {
				permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
			} catch (Exception e) {
				logger.warn("system list all permission for user got an exception.");
				logger.error(e);
			}
		}
		try {
			gson = XGsonBuilder.instance();
			permissionContent = gson.toJson(roleAndPermission);
		} catch (Exception e) {
			logger.warn("system translate object to json got an exception.");
			logger.error(e);
		}
		try {// 데이터베이스에서 사용자 정보를 조회하여 정보 갱신
			userInfoService.updatePermission(userName, permissionContent);
		} catch (Exception e) {
			logger.warn("system save user info got an exception. username:" + userName);
			logger.error(e);
		}
	}

}
