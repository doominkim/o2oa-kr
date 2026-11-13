package com.x.portal.assemble.surface.jaxrs.portal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;

class ActionGet extends BaseAction {

    /**
     * 1.신분이 사용 가능 목록에 있음 2.부서가 사용 가능 부서에 있음 3.회사가 사용 가능 회사에 있음 4.신분, 부서 또는 회사 제한 없음 5.개인이 앱 관리자에 포함
     * 6.이 Portal의 생성자 7.개인에게 Manage 권한 있음 8.개인이 PortalManager 보유
     */
    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            Portal o = business.portal().pick(flag);
            if (null == o) {
                throw new ExceptionPortalNotExist(flag);
            }
            if (!business.portal().visible(effectivePerson, o)) {
                throw new ExceptionPortalAccessDenied(effectivePerson.getDistinguishedName(), o.getName(), o.getId());
            }
            Wo wo = Wo.copier.copy(o);
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends Portal {

        private static final long serialVersionUID = 727247858785981924L;

        static WrapCopier<Portal, Wo> copier = WrapCopierFactory.wo(Portal.class, Wo.class, null,
                ListTools.toList(JpaObject.FieldsInvisible, Portal.controllerList_FIELDNAME,
                        Portal.lastUpdatePerson_FIELDNAME, Portal.creatorPerson_FIELDNAME,
                        Portal.availableGroupList_FIELDNAME, Portal.availableIdentityList_FIELDNAME,
                        Portal.availableUnitList_FIELDNAME));

    }
}