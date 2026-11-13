package com.x.cms.assemble.control.jaxrs.form;

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
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.FormFactory;
import com.x.cms.core.entity.element.Form;

public class ActionListByApp extends BaseAction {

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String appId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), appId );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wraps = (List<Wo>) optional.get();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				if (!business.formEditAvailable( effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 사용자에게 전체 양식 템플릿을 조회할 권한이 없습니다!");
				}
				FormFactory formFactory = business.getFormFactory();
				List<String> ids = formFactory.listByAppId(appId);// 지정 앱의 모든 양식 템플릿 목록 조회
				List<Form> formList = emc.list( Form.class, ids );
				wraps = Wo.copier.copy(formList);// 조회한 상태 있는 객체를 출력 가능한 속성 필터링 객체로 변환
				SortTools.desc( wraps, "createTime" );
				CacheManager.put(cacheCategory, cacheKey, wraps );
				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		return result;
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<>();

		public static final WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null, JpaObject.FieldsInvisible);

		static {
			excludes.add("data");
			excludes.add("mobileData");
		}

	}
}
