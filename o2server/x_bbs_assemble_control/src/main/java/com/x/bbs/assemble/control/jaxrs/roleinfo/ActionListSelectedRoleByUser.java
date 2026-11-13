package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionBindUserNameEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionPersonNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionListSelectedRoleByUser extends BaseAction {
	
	private static  Logger logger = LoggerFactory.getLogger( ActionListSelectedRoleByUser.class );
	
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		List<BBSRoleInfo> roleInfoList = null;
		String personName = null;
		Wi wrapIn = null;
		Boolean check = true;
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionRoleInfoProcess( e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		
		if (check) {
			if (wrapIn.getUserName() == null || wrapIn.getUserName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionBindUserNameEmpty();
				result.error( exception );
			}
		}
		if (check) {
			try {
				personName = userManagerService.getPersonNameByFlag( wrapIn.getUserName() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "사용자 정보를 조회하는 중 오류가 발생했습니다! Person:" + wrapIn.getUserName() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ( personName == null) {
				check = false;
				Exception exception = new ExceptionPersonNotExists( wrapIn.getUserName() );
				result.error( exception );
			}
		}
		if (check) {
			try {
				roleInfoList = roleInfoService.listRoleByObjectUniqueId(wrapIn.getUserName(), "사용자");
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess( e, "역할이 연결된 대상의 고유 식별자로 역할 목록을 조회하는 중 오류가 발생했습니다. Name:" + wrapIn.getUserName() + ", Type:사용자" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (roleInfoList != null) {
				try {
					wraps = Wo.copier.copy(roleInfoList);
					result.setData(wraps);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess( e, "전체 BBS 역할 정보를 출력 객체로 변환하는 중 오류가 발생했습니다." );
					result.error( exception );
					logger.error( e, effectivePerson, request, null);
				}
			}
		}
		return result;
	}
	
	public static class Wi{
		
		public static List<String> Excludes = new ArrayList<String>();
		
		private String unitName = null;
		
		private String userName = null;
		
		private String forumId = null;
		
		private String sectionId = null;
		
		private String bindRoleCode = null;
		
		private BindObject bindObject = null;
		
		private List<String> bindRoleCodes = null;
		
		private List<BindObject> bindObjectArray = null;

		public String getUnitName() {
			return unitName;
		}

		public void setUnitName(String unitName) {
			this.unitName = unitName;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getForumId() {
			return forumId;
		}

		public void setForumId(String forumId) {
			this.forumId = forumId;
		}

		public String getSectionId() {
			return sectionId;
		}

		public void setSectionId(String sectionId) {
			this.sectionId = sectionId;
		}

		public List<String> getBindRoleCodes() {
			return bindRoleCodes;
		}

		public void setBindRoleCodes(List<String> bindRoleCodes) {
			this.bindRoleCodes = bindRoleCodes;
		}

		public List<BindObject> getBindObjectArray() {
			return bindObjectArray;
		}

		public void setBindObjectArray(List<BindObject> bindObjectArray) {
			this.bindObjectArray = bindObjectArray;
		}

		public String getBindRoleCode() {
			return bindRoleCode;
		}

		public void setBindRoleCode(String bindRoleCode) {
			this.bindRoleCode = bindRoleCode;
		}

		public BindObject getBindObject() {
			return bindObject;
		}

		public void setBindObject(BindObject bindObject) {
			this.bindObject = bindObject;
		}
	}

	public static class Wo extends BBSRoleInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();	
		
		public static WrapCopier< BBSRoleInfo, Wo > copier = WrapCopierFactory.wo( BBSRoleInfo.class, Wo.class, null, JpaObject.FieldsInvisible);
		
		private List<WoPermissionInfo> permissions = null;

		public List<WoPermissionInfo> getPermissions() {
			return permissions;
		}

		public void setPermissions(List<WoPermissionInfo> permissions) {
			this.permissions = permissions;
		}
		
	}
	
	public static class WoPermissionInfo extends BBSPermissionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		public static WrapCopier< BBSPermissionInfo, WoPermissionInfo > copier = WrapCopierFactory.wo( BBSPermissionInfo.class, WoPermissionInfo.class, null, JpaObject.FieldsInvisible);
		
		//포럼 게시판 목록
		private List<WoSectionInfo> sections = null;

		public List<WoSectionInfo> getSections() {
			return sections;
		}

		public void setSections(List<WoSectionInfo> sections) {
			this.sections = sections;
		}
	}

	public static class WoSectionInfo extends BBSSectionInfo{
		
		private static final long serialVersionUID = -5076990764713538973L;
		
		public static List<String> Excludes = new ArrayList<String>();
		
		//게시판의 하위 게시판 정보 목록
		private List<WoSectionInfo> subSections = null;

		public List<WoSectionInfo> getSubSections() {
			return subSections;
		}
		public void setSubSections(List<WoSectionInfo> subSections) {
			this.subSections = subSections;
		}	
	}
}