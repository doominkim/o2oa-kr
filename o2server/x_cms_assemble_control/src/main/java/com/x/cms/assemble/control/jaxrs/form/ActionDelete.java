package com.x.cms.assemble.control.jaxrs.form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.WrapOutId;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

/**
 * 양식 삭제
 * @author sword
 */
public class ActionDelete extends BaseAction {

	protected ActionResult<WrapOutId> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = null;
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business( emc );
			Form form = business.getFormFactory().get( id );
			if(form == null){
				throw new ExceptionFormNotExist(id);
			}
			AppInfo appInfo = emc.find(form.getAppId(), AppInfo.class);
			if (!business.isAppInfoManager( effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			// 먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			List<String> viewIds = business.getViewFactory().listByFormId(id);
			List<String> fieldConfigIds = null;
			List<ViewFieldConfig> fieldConfigs = null;
			List<String> viewCategoryIds = null;
			List<ViewCategory> viewCategorys = null;
			View view = null;

			emc.beginTransaction( Form.class );
			emc.beginTransaction( View.class );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( ViewCategory.class );

			if( viewIds != null && !viewIds.isEmpty() ){
				for( String viewId : viewIds ){
					view = business.getViewFactory().get( viewId );
					fieldConfigIds = business.getViewFieldConfigFactory().listByViewId( viewId );
					fieldConfigs = emc.list( ViewFieldConfig.class, fieldConfigIds );
					viewCategoryIds = business.getViewCategoryFactory().listByViewId( viewId );
					viewCategorys = emc.list( ViewCategory.class, viewCategoryIds );
//							business.getViewCategoryFactory().list( viewCategoryIds );
					//해당 양식에 대응하는 뷰의 모든 열 정보 삭제
					if( fieldConfigs != null && !fieldConfigs.isEmpty() ){
						for( ViewFieldConfig viewFieldConfig : fieldConfigs ){
							emc.remove( viewFieldConfig, CheckRemoveType.all );
						}
					}
					//해당 양식에 대응하는 뷰의 모든 분류-뷰 관계 삭제
					if( viewCategorys != null && !viewCategorys.isEmpty() ){
						for( ViewCategory viewCategory : viewCategorys ){
							emc.remove( viewCategory, CheckRemoveType.all );
						}
					}
					//해당 양식에 대응하는 뷰 정보 삭제
					if( view != null ){
						emc.remove( view, CheckRemoveType.all );
					}
				}
			}
			if( form != null ){//마지막으로 양식 정보 삭제
				emc.remove( form, CheckRemoveType.all );
				emc.commit();
				logService.log( emc, effectivePerson.getDistinguishedName(), form.getName(), form.getAppId(), "", "", form.getId(), "FORM", "삭제");
			}
			wrap = new WrapOutId( form.getId() );

			CacheManager.notify( Form.class );
			CacheManager.notify( View.class );
			CacheManager.notify( ViewFieldConfig.class );
			CacheManager.notify( ViewCategory.class );

			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
