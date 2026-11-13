package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.bean.BindObject;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionBindObjectNameEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionBindObjectTypeInvalid;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionGroupNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionPersonNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionUnitNotExists;

public class ActionBindRoleToUser extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionBindRoleToUser.class);

	protected ActionResult<WrapOutBoolean> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		BindObject bindObject = null;
		String object = null;
		wrap.setValue(false);
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionRoleInfoProcess(e,
					"JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getBindObject() == null || wrapIn.getBindObject().getObjectName() == null
					|| wrapIn.getBindObject().getObjectName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionBindObjectNameEmpty();
				result.error(exception);
			} else {
				bindObject = wrapIn.getBindObject();
			}
		}
		if (check) {
			// 모든 대상을 순회하며 실제 존재 여부 확인
			if ("사용자".equals(wrapIn.getBindObject().getObjectType())) {
				try {
					object = userManagerService.getPersonNameByFlag(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new ExceptionPersonNotExists(bindObject.getObjectName());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"사용자 정보를 조회하는 중 오류가 발생했습니다! Person:" + bindObject.getObjectName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else if ("조직".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.checkUnitExistsWithFlag(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new ExceptionUnitNotExists(bindObject.getObjectName());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"조직 정보를 조회하는 중 오류가 발생했습니다! Unit:" + bindObject.getObjectName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else if ("그룹".equals(bindObject.getObjectType())) {
				try {
					object = userManagerService.checkGroupExsitsWithName(bindObject.getObjectName());
					if (object == null) {
						check = false;
						Exception exception = new ExceptionGroupNotExists(bindObject.getObjectName());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"그룹 정보를 조회하는 중 오류가 발생했습니다! Group:" + bindObject.getObjectName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else {
				check = false;
				Exception exception = new ExceptionBindObjectTypeInvalid(bindObject.getObjectType());
				result.error(exception);
			}
		}
		if (check) {
			try {
				roleInfoService.bindRoleToUser(wrapIn.getBindObject(), wrapIn.getBindRoleCodes());
				wrap.setValue(true);
				result.setData(wrap);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess(e, "사용자 이름과 역할 코드 목록으로 역할을 연결하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				checkUserPermission(wrapIn.getBindObject());
			} catch (Exception e) {
				logger.warn("system check user permission got an exception!");
				logger.error(e);
			}
		}
		return result;
	}

	public static class Wi {

		public static List<String> Excludes = new ArrayList<String>();

		@FieldDescribe("조직 이름")
		private String unitName = null;

		@FieldDescribe("사용자 이름")
		private String userName = null;

		@FieldDescribe("포럼 Id")
		private String forumId = null;

		@FieldDescribe("구역 Id")
		private String sectionId = null;

		@FieldDescribe("연결 역할")
		private String bindRoleCode = null;

		@FieldDescribe("연결 대상")
		private BindObject bindObject = null;

		@FieldDescribe("연결 역할 목록")
		private List<String> bindRoleCodes = null;

		@FieldDescribe("연결 대상 목록")
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
}