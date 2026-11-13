package com.x.organization.core.express.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

class ActionListWithIdentity extends BaseAction {

	public static List<String> execute(AbstractContext context, Collection<String> collection,
									   boolean recursiveGroupFlag, boolean referenceFlag, boolean recursiveOrgFlag) throws Exception {
		Wi wi = new Wi();
		if (null != collection) {
			wi.getIdentityList().addAll(collection);
			wi.setRecursiveGroupFlag(recursiveGroupFlag);
			wi.setReferenceFlag(referenceFlag);
			wi.setRecursiveOrgFlag(recursiveOrgFlag);
		}
		Wo wo = context.applications().postQuery(applicationClass, "group/list/identity", wi).getData(Wo.class);
		return wo.getGroupList();
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("개인")
		private List<String> identityList = new ArrayList<>();

		@FieldDescribe("상위 그룹 재귀 조회 여부, 기본값 true")
		private Boolean recursiveGroupFlag = true;

		@FieldDescribe("사용자 신분 구성원·사용자 소속 조직 구성원이 속한 그룹 포함 여부, 기본값 false")
		private Boolean referenceFlag = false;

		@FieldDescribe("사용자 소속 조직의 상위 조직이 속한 그룹 재귀 여부, referenceFlag가 true일 때만, 기본값 false")
		private Boolean recursiveOrgFlag = false;

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public Boolean getReferenceFlag() {
			return referenceFlag;
		}

		public void setReferenceFlag(Boolean referenceFlag) {
			this.referenceFlag = referenceFlag;
		}

		public Boolean getRecursiveGroupFlag() {
			return recursiveGroupFlag;
		}

		public void setRecursiveGroupFlag(Boolean recursiveGroupFlag) {
			this.recursiveGroupFlag = recursiveGroupFlag;
		}

		public Boolean getRecursiveOrgFlag() {
			return recursiveOrgFlag;
		}

		public void setRecursiveOrgFlag(Boolean recursiveOrgFlag) {
			this.recursiveOrgFlag = recursiveOrgFlag;
		}
	}

	public static class Wo extends WoGroupAbstract {
	}
}
