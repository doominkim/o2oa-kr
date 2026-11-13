package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.SortTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.factory.ViewFactory;
import com.x.cms.core.entity.element.View;

public class ActionListByForm extends BaseAction {

	@SuppressWarnings("unchecked")
	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String formId ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wraps = null;

		Cache.CacheKey cacheKey = new Cache.CacheKey( this.getClass(), formId );
		Optional<?> optional = CacheManager.get(cacheCategory, cacheKey );

		if (optional.isPresent()) {
			wraps = (List<Wo>) optional.get();
			result.setData(wraps);
		} else {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				//사용자가 모든 뷰를 볼 권한이 있는지 판단, 권한이 없으면 작업 계속 불가
				if (!business.viewEditAvailable( effectivePerson )) {
					throw new Exception("person{name:" + effectivePerson.getDistinguishedName() + "} 사용자에게 전체 뷰를 조회할 권한이 없습니다!");
				}
				//권한이 있으면 작업 계속
				ViewFactory viewFactory  = business.getViewFactory();
				List<String> ids = viewFactory.listByFormId( formId );
				List<View> viewList = emc.list( View.class, ids );

				if( viewList != null && !viewList.isEmpty() ){
					wraps = Wo.copier.copy( viewList );
					SortTools.desc( wraps, "sequence" );
					String formName = formServiceAdv.getNameWithId( formId );
					for( Wo wo :  wraps ){
						//FormId로 FormName 보충
						if(StringUtils.isNotEmpty( wo.getFormId() )) {
							wo.setFormName( formName );
						}
					}
					CacheManager.put(cacheCategory, cacheKey, wraps );
				}

				result.setData(wraps);
			} catch (Throwable th) {
				th.printStackTrace();
				result.error(th);
			}
		}

		return result;
	}

	public static class Wo extends View {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> excludes = new ArrayList<String>();

		@FieldDescribe("연결된 양식 이름.")
		private String formName = null;

		public static final WrapCopier<View, Wo> copier = WrapCopierFactory.wo( View.class, Wo.class, null, JpaObject.FieldsInvisible);

		public String getFormName() {
			return formName;
		}

		public void setFormName(String formName) {
			this.formName = formName;
		}
	}
}
