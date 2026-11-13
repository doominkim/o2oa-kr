package com.x.cms.assemble.control.jaxrs.viewcategory;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;

public class ActionDelete extends BaseAction {

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			//먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			ViewCategory viewCategory = business.getViewCategoryFactory().get(id);
			if (null == viewCategory) {
				throw new Exception("view{id:" + id + "} 앱 정보가 없습니다.");
			}

			//데이터베이스 영속화 작업 수행
			emc.beginTransaction( ViewCategory.class );
			emc.remove( viewCategory, CheckRemoveType.all );
			emc.commit();

			Wo wo = new Wo();
			wo.setId( viewCategory.getId() );
			result.setData(wo);

			CacheManager.notify( View.class );
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
