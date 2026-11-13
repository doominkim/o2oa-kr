package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.ViewFieldConfigFactory;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionListAll extends BaseAction {

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;
		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass() );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wraps = (List<Wo>) optional.get();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				//사용자가 모든 표시 열 설정 정보를 볼 권한이 있는지 판단, 권한이 없으면 작업 계속 불가
				if (!business.viewEditAvailable( effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 사용자에게 전체 표시 열 설정을 조회할 권한이 없습니다!");
				}
				//권한이 있으면 작업 계속
				ViewFieldConfigFactory viewFieldConfigFactory  = business.getViewFieldConfigFactory();
				List<String> ids = viewFieldConfigFactory.listAll();//모든 표시 열 설정 정보 목록 조회
				List<ViewFieldConfig> viewFieldConfigList = emc.list( ViewFieldConfig.class, ids );//ID IN ids인 모든 표시 열 설정 정보 목록 조회

				wraps = Wo.copier.copy( viewFieldConfigList );//조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환

				CacheManager.put(cacheCategory, cacheKey, wraps );
				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		return result;
	}

	public static class Wo extends ViewFieldConfig {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<ViewFieldConfig, Wo> copier = WrapCopierFactory.wo( ViewFieldConfig.class, Wo.class, null, JpaObject.FieldsInvisible);

	}

}
