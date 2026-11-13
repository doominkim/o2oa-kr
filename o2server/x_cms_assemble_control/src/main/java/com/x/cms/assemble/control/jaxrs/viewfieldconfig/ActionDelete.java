package com.x.cms.assemble.control.jaxrs.viewfieldconfig;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;

public class ActionDelete extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionDelete.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);

			// 먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			ViewFieldConfig viewFieldConfig = business.getViewFieldConfigFactory().get(id);

			if (null == viewFieldConfig) {
				logger.warn("viewFieldConfig{id:" + id + "} 앱 정보가 없습니다.");
			}
			// 뷰 정보 조회
			View view = business.getViewFactory().get(viewFieldConfig.getViewId());
			if (view == null) {
				throw new Exception("view{'id':'" + viewFieldConfig.getViewId() + "'}이(가) 없어 조회를 계속할 수 없습니다!");
			}
			// 데이터베이스 영속화 작업 수행
			emc.beginTransaction(ViewFieldConfig.class);
			emc.beginTransaction(View.class);
			if (null != viewFieldConfig) {
				emc.remove(viewFieldConfig, CheckRemoveType.all);
			}
			deleteFieldConfigIdFromFieldConfigList(view, id);
			emc.commit();

			// 표시 열 설정 정보 1건 삭제 성공
			logService.log(emc, effectivePerson.getDistinguishedName(), viewFieldConfig.getFieldName(), "", "", "",
					viewFieldConfig.getId(), "VIEWFIELDCONFIG", "删除");

			wrap = new Wo();
			wrap.setId(viewFieldConfig.getId());
			CacheManager.notify(ViewFieldConfig.class);
			result.setData(wrap);
		} catch (Throwable th) {
			th.printStackTrace();
			result.error(th);
		}
		return result;
	}

	private void deleteFieldConfigIdFromFieldConfigList(View view, String viewFieldConfigId) {
		if (view != null) {
			if (view.getFieldConfigList() == null) {
				view.setFieldConfigList(new ArrayList<String>());
			}
			// 분류 ID가 이미 포함되어 있는지 확인
			if (!view.getFieldConfigList().contains(viewFieldConfigId)) {
				view.getFieldConfigList().remove(viewFieldConfigId);
			} else {
			}
		} else {
		}
	}

	public static class Wo extends WoId {

	}

}
