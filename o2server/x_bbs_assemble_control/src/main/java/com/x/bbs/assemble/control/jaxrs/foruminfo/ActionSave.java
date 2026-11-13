package com.x.bbs.assemble.control.jaxrs.foruminfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumInfoProcess;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumNameEmpty;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumTypeCategoryEmpty;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionForumTypeCategoryInvalid;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionInsufficientPermissions;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionPersonNotExists;
import com.x.bbs.assemble.control.jaxrs.foruminfo.exception.ExceptionPersonQuery;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Boolean check = true;
		Wo wo = new Wo();
		String[] names = null;
		String[] typeCategory = null;
		String personName = null;
		List<String> forumManagerList = new ArrayList<>();
		Wi wrapIn = null;
		BBSForumInfo forumInfo = new BBSForumInfo();
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();

		if (check) {
			//저장 작업 권한 판단
			try {
				if ( !ThisApplication.isBBSManager(effectivePerson) ) {
					check = false;
					logger.warn("사용자에게 BBSManager 역할이 없고 시스템 관리자도 아닙니다! USER:" + effectivePerson.getDistinguishedName());
					Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionInsufficientPermissions(effectivePerson.getDistinguishedName(), ThisApplication.BBSMANAGER );
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		try {
			wrapIn = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionForumInfoProcess(e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		// 포럼 이름 검증
		if (check) {
			if ( StringUtils.isEmpty( wrapIn.getForumName() ) ) {
				check = false;
				Exception exception = new ExceptionForumNameEmpty();
				result.error(exception);
			}
		}
		
		// 포럼 분류 검증: 정보|질문|투표, 이 세 가지 중 하나만 가능
		if (check) {
			if ( StringUtils.isEmpty( wrapIn.getTypeCategory() )) {
				check = false;
				Exception exception = new ExceptionForumTypeCategoryEmpty();
				result.error(exception);
			}
		}
		
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getForumManagerName() )) {
				forumManagerList = ListTools.toList( wrapIn.getForumManagerName().split(",") );
			}
		}
		
		if (check) {
			typeCategory = wrapIn.getTypeCategory().split("\\|");
			if (typeCategory != null && typeCategory.length > 0) {
				for (String category : typeCategory) {
					if (!"정보".equals(category) && !"질문".equals(category) && !"투표".equals(category)) {
						check = false;
						Exception exception = new ExceptionForumTypeCategoryInvalid(category);
						result.error(exception);
					}
				}
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getSubjectType() )) {
				wrapIn.setSubjectType("뉴스|토론");
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( wrapIn.getForumManagerName() )) {
				// 지정 사용자 존재 여부 판단
				names = wrapIn.getForumManagerName().split(",");
				for (String name : names) {
					try {
						personName = userManagerService.getPersonNameByFlag(name);
						if (personName == null) {
							check = false;
							Exception exception = new ExceptionPersonNotExists(name);
							result.error(exception);
							break;
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionPersonQuery(e, name);
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
						break;
					}
				}
			} else {
				wrapIn.setForumManagerName(effectivePerson.getDistinguishedName());
			}
		}
		if (check) {
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
		}
		
		if (check) {
			if( StringUtils.isEmpty( wrapIn.getForumColor())) {
				wrapIn.setForumColor( "#1462be" );
			}
		}
		
		if (check) {
			forumInfo = Wi.copier.copy(wrapIn);
			
			List<String> arrayList = new ArrayList<>(); 
			if( StringUtils.equals( wrapIn.getForumVisible(), "권한에 따라" )) {
				if( StringUtils.isNotEmpty( wrapIn.getForumVisibleResult())) {
					arrayList.clear();
					Collections.addAll(arrayList, wrapIn.getForumVisibleResult().split( "," ));
					forumInfo.setVisiblePermissionList( arrayList );
				}
			}else {
				forumInfo.setVisiblePermissionList( new ArrayList<>() );
			}
		}
		
		if (check) {
			try {				
				forumInfo.setForumManagerList( forumManagerList );
				if( StringUtils.isNotEmpty( wrapIn.getId() )) {
					forumInfo.setId( wrapIn.getId() );
				}
				forumInfo = forumInfoServiceAdv.save( forumInfo );
				wo.setId(forumInfo.getId());

				CacheManager.notify( BBSForumInfo.class );
				CacheManager.notify( BBSSectionInfo.class );
				CacheManager.notify( BBSSubjectInfo.class );
				
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "BBS 포럼 분류 정보를 저장하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if( check ) {
			try {
				operationRecordService.forumOperation( effectivePerson.getDistinguishedName(), forumInfo, "SAVE", hostIp, hostName );
			}catch( Exception e ) {
				check = false;
				Exception exception = new ExceptionForumInfoProcess(e, "BBS 포럼 작업 기록 저장 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		
		if (check) {
			try {
				// 포럼 정보 추가 성공, 권한과 역할 정보 계속 추가
				permissionInfoService.createForumPermission( forumInfo );
			} catch (Exception e) {
				check = false;
				logger.warn("system create forum permission info got an exception!");
				logger.error(e);
			}
		}
		
		if (check) {
			try {
				// 포럼 정보 추가 성공, 권한과 역할 정보 계속 추가
				roleInfoService.createForumRole( effectivePerson, forumInfo );
			} catch (Exception e) {
				check = false;
				result.error(e);
				logger.warn("system create forum role info got an exception!");
				logger.error(e);
			}
		}
		if (check) {// 포럼 관리자 권한 설정 확인
			try {
				forumInfoServiceAdv.checkForumManager( forumInfo );
			} catch (Exception e) {
				logger.warn("system bind role for forum manager got an exception!");
				logger.error(e);
			}
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends BBSForumInfo {
		
		@FieldDescribe("포럼 관리자.")
		private String forumManagerName = null;

		@FieldDescribe("포럼 공개 범위.")
		private String forumVisibleResult ;
		
		private static final long serialVersionUID = -5076990764713538973L;

		public static WrapCopier<Wi, BBSForumInfo> copier = WrapCopierFactory.wi(Wi.class, BBSForumInfo.class, null,
				JpaObject.FieldsUnmodify);

		public String getForumManagerName() {
			return forumManagerName;
		}

		public void setForumManagerName(String forumManagerName) {
			this.forumManagerName = forumManagerName;
		}

		public String getForumVisibleResult() {
			return forumVisibleResult;
		}

		public void setForumVisibleResult(String forumVisibleResult) {
			this.forumVisibleResult = forumVisibleResult;
		}
		
	}

	public static class Wo extends WoId {

	}

}