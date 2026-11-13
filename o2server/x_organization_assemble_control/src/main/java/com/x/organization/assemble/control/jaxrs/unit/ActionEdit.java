package com.x.organization.assemble.control.jaxrs.unit;

import com.x.organization.core.entity.Person;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;

class ActionEdit extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionEdit.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
            Unit unit = business.unit().pick(flag);
            Unit oldUnit = unit;
            boolean checkFlag = false;

            if (null == unit) {
                throw new ExceptionUnitNotExist(flag);
            }
            if (!business.editable(effectivePerson, unit)) {
                throw new ExceptionDenyEditUnit(effectivePerson, flag);
            }
            if (StringUtils.isEmpty(wi.getName())) {
                throw new ExceptionNameEmpty();
            }

            /** pick으로 가져온 객체는 다시 조회해야 함 */
            emc.beginTransaction(Unit.class);
            unit = emc.find(unit.getId(), Unit.class);
            Gson gsontool = new Gson();
            String strOriginalUnit = gsontool.toJson(unit);

            unit.setControllerList(ListTools.extractProperty(
                    business.person().pick(ListTools.trim(unit.getControllerList(), true, true)),
                    JpaObject.id_FIELDNAME, String.class, true, true));
            Wi.copier.copy(wi, unit);
            /** 고유 식별자가 비어 있지 않으면 고유 여부 확인 */
            if (this.duplicateUniqueWhenNotEmpty(business, unit)) {
                throw new ExceptionDuplicateUnique(unit.getName(), unit.getUnique());
            }
            if (this.checkNameInvalid(business, unit)) {
                throw new ExceptionNameInvalid(unit.getName());
            }
            /** 같은 레벨에서 name 중복 여부 확인 */
            if (this.duplicateName(business, unit)) {
                throw new ExceptionDuplicateName(unit.getName());
            }
            // 상위 조직 정렬이 변경되었을 수 있으므로 모두 다시 계산함.
            business.unit().adjustInherit(unit);
            emc.check(unit, CheckPersistType.all);
            emc.commit();
            CacheManager.notify(Unit.class);

            /** 조직 레벨이나 조직명이 변경되었는지 확인하고, 변경되었으면 현재 조직과 하위 조직 구성원의 신분(조직명, 조직 레벨명)을 다시 계산 */
            checkFlag = this.checkUnitTypeName(oldUnit, unit);
            if (checkFlag) {
                this.updateIdentityUnitNameAndUnitLevelName(oldUnit, unit, business);
            }

            Wo wo = new Wo();
            wo.setId(unit.getId());
            result.setData(wo);
            return result;

        }

    }

    public static class Wo extends WoId {
    }

    public static class Wi extends Unit {

        private static final long serialVersionUID = -7527954993386512109L;

        static WrapCopier<Wi, Unit> copier = WrapCopierFactory.wi(Wi.class, Unit.class, null,
                ListTools.toList(JpaObject.FieldsUnmodify, Unit.pinyin_FIELDNAME, Unit.pinyinInitial_FIELDNAME,
                        Unit.level_FIELDNAME, Unit.levelName_FIELDNAME));
    }

    /**
     * 조직 표식으로 신분 목록 조회
     *
     * @param business
     * @param unit
     * @return
     * @throws Exception
     */
    private List<Identity> listIdentityByUnitFlag(Business business, Unit unit) throws Exception {
        if (null == unit.getId() || StringUtils.isEmpty(unit.getId()) || null == unit) {
            throw new ExceptionUnitNotExist(unit.getId());
        }
        EntityManager em = business.entityManagerContainer().get(Identity.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
        Root<Identity> root = cq.from(Identity.class);
        Predicate p = cb.equal(root.get(Identity_.unit), unit.getId());
        List<Identity> os = em.createQuery(cq.select(root).where(p)).getResultList();
        return os;
    }

    void updateIdentityUnitNameAndUnitLevelName(Unit oldUnit, Unit unit, Business business) throws Exception {
        boolean isTopUpdate = (Unit.TOP_LEVEL.equals(oldUnit.getLevel()) || Unit.TOP_LEVEL.equals(unit.getLevel()))
                && !oldUnit.getSuperior().equals(unit.getSuperior());
        List<Unit> unitList = new ArrayList<>();
        unitList.add(unit);
        unitList.addAll(business.unit().listSubNestedObject(unit));
        EntityManagerContainer emc = business.entityManagerContainer();
        for (Unit u : unitList) {
            List<Identity> identityList = this.listIdentityByUnitFlag(business, u);
            if (ListTools.isNotEmpty(identityList)) {
                String unitName = u.getName();
                String unitLevelName = u.getLevelName();
                for (Identity i : identityList) {
                    Identity identity = emc.find(i.getId(), Identity.class);
                    identity.setUnitName(unitName);
                    identity.setUnitLevelName(unitLevelName);
                    emc.beginTransaction(Identity.class);
                    emc.check(identity, CheckPersistType.all);
                    emc.commit();
                    if(isTopUpdate) {
                        Person person = emc.find(identity.getPerson(), Person.class);
                        emc.beginTransaction(Person.class);
                        Set<String> topUnitList = new HashSet<>(person.getTopUnitList());
                        if(Unit.TOP_LEVEL.equals(oldUnit.getLevel())) {
                            topUnitList.remove(unit.getId());
                            topUnitList.add(unit.getSuperior());
                        }else{
                            topUnitList.remove(oldUnit.getSuperior());
                            topUnitList.add(unit.getId());
                        }
                        person.setTopUnitList(new ArrayList<>(topUnitList));
                        emc.commit();
                    }
                    CacheManager.notify(Identity.class);
                    CacheManager.notify(Person.class);
                }
            }

        }
    }

    private boolean checkUnitTypeName(Unit oldUnit, Unit unit) throws Exception {
        List<String> oldUnitType = oldUnit.getTypeList();
        List<String> unitType = unit.getTypeList();
        // 두 list가 같은지 확인
        if (oldUnitType.retainAll(unitType) || (!StringUtils.equals(oldUnit.getName(), unit.getName()))
                || (!StringUtils.equals(oldUnit.getSuperior(), unit.getSuperior()))) {
            return true;
        }
        return false;
    }

}
