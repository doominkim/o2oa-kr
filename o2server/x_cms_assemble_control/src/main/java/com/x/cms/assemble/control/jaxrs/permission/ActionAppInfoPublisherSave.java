package com.x.cms.assemble.control.jaxrs.permission;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.service.CmsBatchOperationPersistService;
import com.x.cms.assemble.control.service.CmsBatchOperationProcessService;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.core.entity.AppInfo;

public class ActionAppInfoPublisherSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionAppInfoPublisherSave.class );

	@AuditLog(operation = "게시판 게시자 저장")
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId, JsonElement jsonElement ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = null;
		AppInfo appInfo = null;
		Boolean check = true;

		if( check ){
			try {
				wi = this.convertToWrapIn( jsonElement, Wi.class );
			} catch (Exception e ) {
				check = false;
				Exception exception = new ExceptionAppCategoryAdminProcess( e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString() );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				appInfo = appInfoServiceAdv.get( appId );
				if( appInfo == null ){
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( appId );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoQueryById( e, appId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try {
				appInfoServiceAdv.updatePublisherPermission( appId, wi.getPersonList(), wi.getUnitList(), wi.getGroupList(), wi.getRoleList() );
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoPermissionSave( e, appId );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		//갱신 완료
		if( check ){
			String description = "게시판: "+appId+"권한 변경";
			new LogService().log( null,  effectivePerson.getDistinguishedName(), description, appId, "", "", appId, "APPINFO_PUBLISHER", "게시 권한 변경" );

			CacheManager.notify( AppInfo.class );
			Wo wo = new Wo();
			wo.setId( appId );
			result.setData( wo );
		}

		if( check ) {
			try {
				new CmsBatchOperationPersistService().addOperation(
						CmsBatchOperationProcessService.OPT_OBJ_APPINFO,
						CmsBatchOperationProcessService.OPT_TYPE_PERMISSION,  appId,  appId, "게시판 게시 권한 변경: ID=" +  appId );
			}catch( Exception e ) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static class Wi{

		@FieldDescribe("사용자 목록")
		private List<String> personList;

		@FieldDescribe("조직 목록")
		private List<String> unitList;

		@FieldDescribe("그룹 목록")
		private List<String> groupList;

		@FieldDescribe("역할 목록")
		private List<String> roleList;

		public List<String> getPersonList() {
			return personList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public List<String> getGroupList() {
			return groupList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}
	}

	public static class Wo extends WoId {

	}
}
