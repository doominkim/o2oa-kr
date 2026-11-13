package com.x.cms.assemble.control.jaxrs.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;

/**
 * 목록 설정 삭제
 * @author sword
 */
public class ActionDelete extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			//먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			View view = business.getViewFactory().get(id);

			AppInfo appInfo = appInfoServiceAdv.get(view.getAppId());
			if (!business.isAppInfoManager(effectivePerson, appInfo)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}

			//뷰에 연결된 모든 열 설정 조회
			List<String> fieldConfigIds = business.getViewFieldConfigFactory().listByViewId(id);
			List<ViewFieldConfig> fieldConfigs = emc.list( ViewFieldConfig.class, fieldConfigIds);
			//뷰에 연결된 모든 분류 연결 설정 조회
			List<String> viewCategoryIds = business.getViewCategoryFactory().listByViewId(id);
			List<ViewCategory> viewCategorys = emc.list( ViewCategory.class, viewCategoryIds );

			//데이터베이스 영속화 작업 수행
			emc.beginTransaction( View.class );
			emc.beginTransaction( ViewFieldConfig.class );
			emc.beginTransaction( ViewCategory.class );

			//모든 viewFieldConfig 삭제
			if( fieldConfigs != null && fieldConfigs.size() > 0 ){
				for( ViewFieldConfig viewFieldConfig : fieldConfigs ){
					emc.remove( viewFieldConfig, CheckRemoveType.all );
				}
			}
			if( viewCategorys != null && viewCategorys.size() > 0){
				for( ViewCategory viewCategory : viewCategorys ){
					emc.remove( viewCategory, CheckRemoveType.all );
				}
			}
			if( view != null ){
				emc.remove( view, CheckRemoveType.all );
			}
			emc.commit();

			if( view != null ){
				logService.log( emc,  effectivePerson.getDistinguishedName(), view.getName(), view.getAppId(), "", "", view.getId(), "VIEW", "删除" );
			}

			Wo wo = new Wo();
			wo.setId( view.getId() );
			result.setData( wo );

			CacheManager.notify( View.class );
			CacheManager.notify( ViewFieldConfig.class );
			CacheManager.notify( ViewCategory.class );
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	public static class Wo extends WoId {

	}
}
