package com.x.bbs.assemble.control.jaxrs.login;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionMobileIndex extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionMobileIndex.class);
	private WrapCopier<BBSForumInfo, Wo> forum_wrapout_copier = WrapCopierFactory.wo(BBSForumInfo.class, Wo.class,
			null, JpaObject.FieldsInvisible);

	protected ActionResult<List<Wo>> execute(HttpServletRequest request, EffectivePerson effectivePerson)
			throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = new ArrayList<>();
		List<BBSPermissionInfo> permissionList = null;
		Boolean check = true;
		EffectivePerson currentPerson = this.effectivePerson(request);

		if (check) {
			try {
				permissionList = getPermissionListByUser(currentPerson);
			} catch (Exception e) {
				permissionList = null;
			}
		}

		// 로그인 사용자로 접근 가능한 전체 포럼 정보 목록 조회
		if (check) {
			wraps = getForumInfoListByPerson(currentPerson, permissionList);
		}

		if (check) {
			if ( ListTools.isNotEmpty( wraps )) {// 각 포럼에서 사용자가 접근 가능한 전체 게시판 정보 조회
				for (Wo wrapOutForumInfoForIndex : wraps) {
					composeMainSectionForForumInIndex(wrapOutForumInfoForIndex, permissionList);
				}
			}
		}
		result.setData(wraps);
		return result;
	}

	private Wo composeMainSectionForForumInIndex(Wo wrapOutForumInfoForIndex, List<BBSPermissionInfo> permissionList) {
		if (wrapOutForumInfoForIndex == null) {
			return null;
		}
		List<String> sectionIds = new ArrayList<String>();
		List<BBSSectionInfo> sectionInfoList = null;
		List<WoSectionInfoForIndex> wrapSectionInfoList = null;
		List<BBSPermissionInfo> sectionViewPermissionList = null;

		try {
			sectionViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction("SECTION_VIEW",
					permissionList);
		} catch (Exception e) {
			logger.warn("system filter SECTION_VIEW permission from user permission list got an exception!");
			logger.error(e);
			sectionViewPermissionList = null;
		}

		if ( ListTools.isNotEmpty( sectionViewPermissionList )) {
			for (BBSPermissionInfo permission : sectionViewPermissionList) {
				if (permission.getMainSectionId() != null && !sectionIds.contains(permission.getMainSectionId())) {
					sectionIds.add(permission.getMainSectionId());
				}
			}
		}
		try {
			sectionInfoList = sectionInfoServiceAdv.viewMainSectionByForumId(wrapOutForumInfoForIndex.getId(),
					sectionIds);
		} catch (Exception e) {
			logger.warn("system query all mainSection info got an exception!");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( sectionInfoList )) {
			try {
				wrapSectionInfoList = WoSectionInfoForIndex.copier.copy(sectionInfoList);
				wrapOutForumInfoForIndex.setSectionInfoList(wrapSectionInfoList);
			} catch (Exception e) {
				logger.warn("system copy forum list to wraps got an exception!");
				logger.error(e);
			}
		}
		return wrapOutForumInfoForIndex;
	}

	/**
	 * 로그인 사용자 권한으로 접근 가능한 전체 포럼 정보 목록 조회
	 * 
	 * @param currentPerson
	 * @param permissionList
	 * @return
	 */
	private List<Wo> getForumInfoListByPerson(EffectivePerson currentPerson, List<BBSPermissionInfo> permissionList) {
		if (currentPerson == null) {
			return null;
		}
		List<String> forumIds = new ArrayList<String>();
		List<BBSForumInfo> forumInfoList = null;
		List<BBSPermissionInfo> forumViewPermissionList = null;
		List<Wo> wraps = new ArrayList<>();

		try {
			forumViewPermissionList = permissionInfoService.filterPermissionListByPermissionFunction("FORUM_VIEW",
					permissionList);
		} catch (Exception e) {
			logger.warn("system filter FORUM_VIEW permission from user permission list got an exception!");
			logger.error(e);
			forumViewPermissionList = null;
		}

		if ( ListTools.isNotEmpty( forumViewPermissionList )) {
			for (BBSPermissionInfo permission : forumViewPermissionList) {
				forumIds.add(permission.getForumId());
			}
		}

		try {
			forumInfoList = forumInfoServiceAdv.listAllViewAbleForumWithUserPermission(forumIds);
			if (forumInfoList == null) {
				forumInfoList = new ArrayList<BBSForumInfo>();
			}
		} catch (Exception e) {
			logger.warn("system query all forum info got an exception!");
			logger.error(e);
			return null;
		}

		if ( ListTools.isNotEmpty( forumInfoList )) {
			try {
				wraps = forum_wrapout_copier.copy(forumInfoList);
			} catch (Exception e) {
				logger.warn("system copy forum list to wraps got an exception!");
				logger.error(e);
				return null;
			}

		}
		return wraps;
	}

	/**
	 * 사용자 정보로 해당 사용자의 전체 권한 목록 조회
	 * 
	 * @param currentPerson
	 * @return
	 */
	private List<BBSPermissionInfo> getPermissionListByUser(EffectivePerson currentPerson) {
		List<BBSPermissionInfo> permissionList = null;
		// 익명 사용자가 아니면 해당 사용자가 접근 가능한 전체 포럼 정보 조회
		if ( currentPerson != null && !"anonymous".equalsIgnoreCase(currentPerson.getTokenType().name())) {
			try {
				permissionList = userPermissionService.getUserPermissionInfoList(currentPerson.getDistinguishedName());
			} catch (Exception e) {
				logger.warn(
						"system get all user permission list from ThisApplication.userPermissionInfoMap got an exception!");
				logger.error(e);
				permissionList = null;
			}
		}
		return permissionList;
	}

	public static class Wo extends BBSForumInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<BBSForumInfo, Wo> copier = WrapCopierFactory.wo(BBSForumInfo.class, Wo.class, null,
				JpaObject.FieldsInvisible);

		// 포럼 게시판 목록
		private List<WoSectionInfoForIndex> sectionInfoList = null;

		public List<WoSectionInfoForIndex> getSectionInfoList() {
			return sectionInfoList;
		}

		public void setSectionInfoList(List<WoSectionInfoForIndex> sectionInfoList) {
			this.sectionInfoList = sectionInfoList;
		}
	}

	public static class WoSectionInfoForIndex extends BBSSectionInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<BBSSectionInfo, WoSectionInfoForIndex> copier = WrapCopierFactory
				.wo(BBSSectionInfo.class, WoSectionInfoForIndex.class, null, JpaObject.FieldsInvisible);
		// 게시판의 하위 게시판 정보 목록
		private List<WoSectionInfoForIndex> subSections = null;

		public List<WoSectionInfoForIndex> getSubSections() {
			return subSections;
		}

		public void setSubSections(List<WoSectionInfoForIndex> subSections) {
			this.subSections = subSections;
		}
	}

}