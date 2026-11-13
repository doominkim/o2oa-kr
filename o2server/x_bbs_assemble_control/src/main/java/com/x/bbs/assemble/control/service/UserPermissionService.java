package com.x.bbs.assemble.control.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.MethodExcuteResult;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSShutup;
import com.x.bbs.entity.BBSUserInfo;

/**
 * 사용자 조직 최상위 조직 정보 관리 서비스 클래스
 *
 * @author LIYI
 *
 */
public class UserPermissionService {

	private static  Logger logger = LoggerFactory.getLogger(UserPermissionService.class);
	private Cache.CacheCategory cacheCategory = new Cache.CacheCategory(BBSUserInfo.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
	private UserManagerService userManagerService = new UserManagerService();

	public Boolean hasPermission(String userName, String permissionCode) {
		RoleAndPermission roleAndPermission = null;
		List<String> permissionCodeList = null;
		try {
			roleAndPermission = getUserRoleAndPermission(userName);
			if (roleAndPermission != null) {
				permissionCodeList = roleAndPermission.getPermissionInfoList();
			}
			if ( ListTools.isNotEmpty( permissionCodeList )) {
				for (String _permissionCode : permissionCodeList) {
					if (permissionCode.equalsIgnoreCase(_permissionCode)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.warn("시스템이 직원으로 포럼 사용자 권한·역할 정보 목록을 조회하는 중 예외가 발생했습니다!");
			logger.error(e);
		}

		return false;
	}

	public List<BBSPermissionInfo> getUserPermissionInfoList(String userName) {
		RoleAndPermission roleAndPermission = null;
		List<BBSPermissionInfo> permissionList = null;
		List<String> permissionCodes = null;
		try {
			roleAndPermission = getUserRoleAndPermission(userName);
			if (roleAndPermission != null) {
				permissionCodes = roleAndPermission.getPermissionInfoList();
			}
			if ( ListTools.isNotEmpty( permissionCodes )) {
				permissionList = permissionInfoService.listPermissionByCodes(permissionCodes);
			}
		} catch (Exception e) {
			logger.warn("시스템이 직원으로 포럼 사용자 권한·역할 정보 목록을 조회하는 중 예외가 발생했습니다!");
			logger.error(e);
		}
		return permissionList;
	}

	/**
	 * 사용자 권한 정보 조회, 캐시된 사용자 정보 우선 사용
	 *
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public RoleAndPermission getUserRoleAndPermission(String userName) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;

		if (userName == null) {
			throw new Exception("user name is null!");
		}

		userInfo = getUserInfoFromCache(userName);

		if (userInfo != null) {
			content = userInfo.getPermissionContent();
			if (content != null) {
				roleAndPermission = gson.fromJson(content, RoleAndPermission.class);
			}
		}

		return roleAndPermission;
	}

	/**
	 * 사용자 정보 조회, 캐시 정보 우선 사용
	 *
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public BBSUserInfo getUserInfoFromCache(String userName) throws Exception {
		BBSUserInfo userInfo = null;
		Boolean check = true;
		if (userName == null) {
			throw new Exception("user name is null!");
		}

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), userName );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			return (BBSUserInfo) optional.get();
		} else {
			if (check) {
				try {
					userInfo = userInfoService.getByUserName(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("시스템이 직원으로 포럼 사용자 정보를 조회하는 중 예외가 발생했습니다!");
					logger.error(e);
				}
				// 그래도 비어 있으면 사용자 정보를 새로 구성하고 모든 권한 정보 스캔
				if (userInfo == null) {
					userInfo = composeUserRoleAndPermission(userName);
					return userInfo;
				}
			}
		}
		CacheManager.put(cacheCategory, cacheKey, userInfo );
		return userInfo;
	}

	public RoleAndPermission getUserRoleAndPermissionForLogin(String userName) throws Exception {
		Gson gson = XGsonBuilder.instance();
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;
		Boolean check = true;
		if (userName == null) {
			throw new Exception("user name is null!");
		}
		if (check) {
			userInfo = composeUserRoleAndPermission(userName);
		}
		if (check) {
			content = userInfo.getPermissionContent();
			if (content != null) {
				roleAndPermission = gson.fromJson(content, RoleAndPermission.class);
			}
		}
		return roleAndPermission;
	}

	private BBSUserInfo composeUserRoleAndPermission(String userName) throws Exception {
		Gson gson = null;
		String content = null;
		BBSUserInfo userInfo = null;
		RoleAndPermission roleAndPermission = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		Boolean check = true;
		Boolean isBBSManager = false;

		if (userName == null) {
			throw new Exception("userName is null!");
		} else {
			if (check) {
				try {
					roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("시스템이 직원으로 모든 역할 정보를 조회하는 중 예외가 발생했습니다!");
					logger.error(e);
				}
			}
			if (check) {
				if ( ListTools.isNotEmpty( roleCodes )) {
					try {
						permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
					} catch (Exception e) {
						check = false;
						logger.warn("시스템이 직원으로 모든 권한 정보를 조회하는 중 예외가 발생했습니다!");
						logger.error(e);
					}
				}
			}
			if (check) {
				isBBSManager = userManagerService.isHasPlatformRole(userName, ThisApplication.BBSMANAGER);
				roleAndPermission = new RoleAndPermission();
				roleAndPermission.setPerson(userName);
				roleAndPermission.setRoleInfoList(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
				roleAndPermission.setIsBBSManager(isBBSManager);
			}

			if (check) {
				if (roleAndPermission != null) {
					gson = XGsonBuilder.instance();
					content = gson.toJson(roleAndPermission);
				}
			}
			if (check) {
				try {
					userInfo = userInfoService.getByUserName(userName);
				} catch (Exception e) {
					check = false;
					logger.warn("시스템이 직원으로 포럼 사용자 정보를 조회하는 중 예외가 발생했습니다!");
					logger.error(e);
				}

				if (userInfo == null) {
					userInfo = new BBSUserInfo();
					userInfo.setNickName(userName);
					userInfo.setUserName(userName);
					userInfo.setLastOperationTime(new Date());
					userInfo.setLastVisitTime(new Date());
					userInfo.setOnline(true);
				}
				userInfo.setPermissionContent(content);
				userInfo = userInfoService.save(userInfo);

				Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), userName );
				CacheManager.put(cacheCategory, cacheKey, userInfo );
			}
		}
		return userInfo;
	}

	public void logout(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			throw new Exception("name is null!");
		}
		userInfoService.logout(name);
	}

	public MethodExcuteResult getViewForumIdsFromUserPermission( EffectivePerson currentPerson ) {
		MethodExcuteResult methodExcuteResult = new MethodExcuteResult();
		List<String> ids = new ArrayList<String>();
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionInfo> forumViewPermissionList = null;

		// 익명 사용자가 아니면 해당 사용자가 접근 가능한 전체 포럼 정보 조회
		if ( currentPerson != null && !"anonymous".equalsIgnoreCase(currentPerson.getTokenType().name())) {
			if ( methodExcuteResult.getSuccess()) {// 사용자가 가진 모든 권한 목록 조회
				try {
					permissionList = getUserPermissionInfoList( currentPerson.getDistinguishedName() );
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("사용자의 권한 목록을 가져오는 중 오류가 발생했습니다");
					logger.warn(
							"system get all permission list from ThisApplication.userPermissionInfoMap got an exception!");
					logger.error(e);
				}
			}
			if ( methodExcuteResult.getSuccess() ) {// 사용자가 접근 가능한 포럼 관련 권한 조회
				try {
					forumViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction("FORUM_VIEW", permissionList );
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("사용자의 권한 목록에서 포럼 접근 권한을 거르는 중 오류가 발생했습니다");
					logger.warn("system filter FORUM_VIEW permission from user permission list got an exception!");
					logger.error(e);
				}
			}

			if ( methodExcuteResult.getSuccess() ) {// 접근 가능한 포럼 ID 목록 조회
				if ( ListTools.isNotEmpty( forumViewPermissionList )) {
					for (BBSPermissionInfo permission : forumViewPermissionList) {
						ids.add(permission.getForumId());
					}
				}
			}
		}

		methodExcuteResult.setBackObject( ids );

		return methodExcuteResult;
	}

	public MethodExcuteResult getViewSectionIdsFromUserPermission(EffectivePerson currentPerson) {
		MethodExcuteResult methodExcuteResult = new MethodExcuteResult();
		List<String> ids = new ArrayList<String>();
		List<BBSPermissionInfo> permissionList = null;
		List<BBSPermissionInfo> sectionViewPermissionList = null;
		// 익명 사용자가 아니면 해당 사용자가 접근 가능한 전체 포럼 정보 조회
		if (currentPerson != null && !"anonymous".equalsIgnoreCase(currentPerson.getTokenType().name())) {
			if (methodExcuteResult.getSuccess()) {// 사용자가 가진 모든 권한 목록 조회
				try {
					permissionList = getUserPermissionInfoList(currentPerson.getDistinguishedName());
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("사용자의 권한 목록을 가져오는 중 오류가 발생했습니다");
					logger.warn(
							"system get all permission list from ThisApplication.userPermissionInfoMap got an exception!");
					logger.error(e);
				}
			}
			if (methodExcuteResult.getSuccess()) {// 사용자가 접근 가능한 포럼 관련 권한 조회
				try {
					sectionViewPermissionList = permissionInfoService
							.filterPermissionListByPermissionFunction("SECTION_VIEW", permissionList);
				} catch (Exception e) {
					methodExcuteResult.setSuccess(false);
					methodExcuteResult.error(e);
					methodExcuteResult.setMessage("사용자의 권한 목록에서 포럼 접근 권한을 거르는 중 오류가 발생했습니다");
					logger.warn("system filter FORUM_VIEW permission from user permission list got an exception!");
					logger.error(e);
				}
			}
			if (methodExcuteResult.getSuccess()) {// 접근 가능한 포럼 ID 목록 조회
				if ( ListTools.isNotEmpty( sectionViewPermissionList )) {
					for (BBSPermissionInfo permission : sectionViewPermissionList) {
						ids.add(permission.getSectionId());
					}
				}
			}
		}
		methodExcuteResult.setBackObject(ids);
		return methodExcuteResult;
	}

	/**
	 * 지정한 사용자의 사용 중지 여부 판단
	 * @param person
	 * @return
	 * @throws Exception
	 */
	public boolean personHasShutup(String person) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			return emc.countEqual(BBSShutup.class, BBSShutup.person_FIELDNAME, person) > 0;
		}
	}

}
