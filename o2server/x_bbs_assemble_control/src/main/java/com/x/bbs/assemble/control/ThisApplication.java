package com.x.bbs.assemble.control;

import java.util.List;

import com.x.bbs.assemble.control.schedule.*;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.Context;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.queue.NickNameConsumeQueue;
import com.x.bbs.assemble.control.queue.QueueNewReplyNotify;
import com.x.bbs.assemble.control.queue.QueueNewSubjectNotify;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ThisApplication {

	private ThisApplication() {
		// nothing
	}

	protected static Context context;
	public static final String BBSMANAGER = "BBSManager";
	public static final QueueNewReplyNotify queueNewReplyNotify = new QueueNewReplyNotify();
	public static final QueueNewSubjectNotify queueNewSubjectNotify = new QueueNewSubjectNotify();
	public static final NickNameConsumeQueue nickNameConsumeQueue = new NickNameConsumeQueue();
	public static String CONFIG_BBS_ANONYMOUS_PERMISSION = "YES";

	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			CacheManager.init(context.clazz().getSimpleName());
			CONFIG_BBS_ANONYMOUS_PERMISSION = (new BBSConfigSettingService())
					.getValueWithConfigCode("BBS_ANONYMOUS_PERMISSION");
			initAllSystemConfig();
			MessageConnector.start(context());
			context().startQueue(queueNewReplyNotify);
			context().startQueue(queueNewSubjectNotify);
			context().startQueue(nickNameConsumeQueue);
			context.schedule(SubjectTotalStatisticTask.class, "0 0 1 * * ?");
			context.schedule(UserCountTodaySetZeroTask.class, "0 1 0 * * ?");
			context.schedule(ReleaseShutupTask.class, "0 10 0 * * ?");
			context.schedule(SubjectReplyTotalStatisticTask.class, "0 40 * * * ?");
			context.schedule(UserSubjectReplyPermissionStatisticTask.class, "0 0/30 * * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
			CacheManager.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getRoleAndPermissionCacheKey(String personName) {
		return "RoleAndPermission.withPerson." + personName;
	}

	private static void initAllSystemConfig() {
		BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
		BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
		BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
		BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
		BBSConfigSettingService configSettingService = new BBSConfigSettingService();
		List<BBSForumInfo> forumInfoList = null;
		List<BBSSectionInfo> sectionInfoList = null;

		try {
			configSettingService.initAllSystemConfig();
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all config setting got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if (forumInfoList != null) {
				for (BBSForumInfo forumInfo : forumInfoList) {
					permissionInfoService.createForumPermission(forumInfo);
					roleInfoService.createForumRole(null, forumInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all forum permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			sectionInfoList = sectionInfoServiceAdv.listAll();
			if (sectionInfoList != null) {
				for (BBSSectionInfo sectionInfo : sectionInfoList) {
					permissionInfoService.createSectionPermission(sectionInfo);
					roleInfoService.createSectionRole("System", sectionInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all section permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}
	}

	/**
	 * 사용자가 BBS 시스템 관리 권한이 있는지 판단 1. 시스템 관리자 Manager 또는 xadmin 2. BBSManager 역할을 가진 사용자
	 *
	 * @param effectivePerson
	 * @return
	 */
	public static Boolean isBBSManager(EffectivePerson effectivePerson) {
		UserManagerService userManagerService = new UserManagerService();
		try {
			if (userManagerService.isHasPlatformRole(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER)
					|| effectivePerson.isManager()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 사용자가 지정한 포럼을 관리할 권한이 있는지 판단 1. 시스템 관리자, BBS 관리자 2. 지정 포럼에 설정된 관리자
	 *
	 * @param effectivePerson
	 * @param forumInfo
	 * @return
	 */
	public static Boolean isForumManager(EffectivePerson effectivePerson, BBSForumInfo forumInfo) {
		if (BooleanUtils.isTrue(isBBSManager(effectivePerson))) {
			return true;
		}
		if (forumInfo != null && ListTools.isNotEmpty(forumInfo.getForumManagerList())) {
			for (String name : forumInfo.getForumManagerList()) {
				if (effectivePerson.getDistinguishedName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

}
