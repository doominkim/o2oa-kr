package com.x.processplatform.assemble.surface.jaxrs.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Application_;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.entity.element.Process_;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 2022-11-22
 * 시스템 메뉴->프로세스에서 사용 가능한 앱을 표시하는 메서드, 앱에 설정된 사용 가능 신분과 사용 가능 조직으로 결정
 * 
 * @author ray
 *
 */
class ActionListWithPerson extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionListWithPerson.class);

    /**
     * 1. 신분이 사용 가능 목록에 있음 <br/>
     * 2. 조직이 사용 가능 조직에 있음 <br/>
     * 4. 신분과 조직 제한 없음 <br/>
     * 5. 개인이 앱 관리자에 포함됨 <br/>
     * 6. 앱 생성자 <br/>
     * 7. 개인이 Manage 권한 또는 ProcessPlatformManager 신분을 가짐
     */
    @SuppressWarnings("unchecked")
    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        ActionResult<List<Wo>> result = new ActionResult<>();
        List<Wo> wos = new ArrayList<>();
        CacheKey cacheKey = new CacheKey(this.getClass(), effectivePerson.getDistinguishedName());
        Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
        if (optional.isPresent()) {
            wos = (List<Wo>) optional.get();
        } else {
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Business business = new Business(emc);
                List<String> identities = business.organization().identity().listWithPerson(effectivePerson);
                // 부서 및 상위 부서 제거, 1단계 부서가 사용 가능하면 그 하위 2단계 부서도 사용 가능
                List<String> units = business.organization().unit().listWithPersonSupNested(effectivePerson);
                List<String> roles = business.organization().role().listWithPerson(effectivePerson);
                // List<String> groups =
                // business.organization().group().listWithIdentity(identities);
//                List<String> ids = this.list(business, effectivePerson, roles, identities, units, groups);
                List<String> ids = listFromApplication(business, effectivePerson, roles, identities, units);
                for (String id : ids) {
                    Application o = business.application().pick(id);
                    if (null != o) {
                        wos.add(Wo.copier.copy(o));
                    }
                }
                wos = business.application().sort(wos);
                CacheManager.put(cacheCategory, cacheKey, wos);
            }
        }
        result.setData(wos);
        return result;
    }

    @Schema(name = "com.x.processplatform.assemble.surface.jaxrs.application.ActionListWithPerson$Wo")
    public static class Wo extends Application {

        private static final long serialVersionUID = -4862564047240738097L;

        static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
                JpaObject.FieldsInvisible);

    }

    private List<String> listFromApplication(Business business, EffectivePerson effectivePerson, List<String> roles,
            List<String> identities, List<String> units) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Application.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Application> root = cq.from(Application.class);
        if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
            Predicate p = cb.and(cb.isEmpty(root.get(Application_.availableIdentityList)),
                    cb.isEmpty(root.get(Application_.availableUnitList)));
            p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Application_.controllerList)));
            // p = cb.or(p, cb.equal(root.get(Application_.creatorPerson),
            // effectivePerson.getDistinguishedName()));
            if (ListTools.isNotEmpty(identities)) {
                p = cb.or(p, root.get(Application_.availableIdentityList).in(identities));
            }
            if (ListTools.isNotEmpty(units)) {
                p = cb.or(p, root.get(Application_.availableUnitList).in(units));
            }
            cq.where(p);
        }
        return em.createQuery(cq.select(root.get(Application_.id))).getResultList().stream().distinct()
                .collect(Collectors.toList());
    }

    /**
     * 
     * Process에서 시작 가능한 Process의 application 조회.
     */
    private List<String> listFromProcess(Business business, EffectivePerson effectivePerson, List<String> roles,
            List<String> identities, List<String> units, List<String> groups) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Process.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Process> root = cq.from(Process.class);
        Predicate p = cb.conjunction();
        if (effectivePerson.isNotManager() && (!business.organization().person().hasRole(effectivePerson,
                OrganizationDefinition.Manager, OrganizationDefinition.ProcessPlatformManager))) {
            p = cb.and(cb.isEmpty(root.get(Process_.startableIdentityList)),
                    cb.isEmpty(root.get(Process_.startableUnitList)),
                    cb.isEmpty(root.get(Process_.startableGroupList)));
            p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Process_.controllerList)));
           // p = cb.or(p, cb.equal(root.get(Process_.creatorPerson), effectivePerson.getDistinguishedName()));
            if (ListTools.isNotEmpty(identities)) {
                p = cb.or(p, root.get(Process_.startableIdentityList).in(identities));
            }
            if (ListTools.isNotEmpty(units)) {
                p = cb.or(p, root.get(Process_.startableUnitList).in(units));
            }
            if (ListTools.isNotEmpty(groups)) {
                p = cb.or(p, root.get(Process_.startableGroupList).in(groups));
            }
        }
        cq.select(root.get(Process_.application)).where(p);
        return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
    }
}