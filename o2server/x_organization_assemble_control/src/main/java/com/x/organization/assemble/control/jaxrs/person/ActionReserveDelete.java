package com.x.organization.assemble.control.jaxrs.person;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Custom;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;

class ActionReserveDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionReserveDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            if (Config.token().isInitialManager(flag)) {
                throw new ExceptionDenyDeleteInitialManager();
            } else {
                Business business = new Business(emc);
                Person person = business.person().pick(flag);
                if (null == person) {
                    throw new ExceptionEntityNotExist(flag, Person.class);
                }
                // 메모리에서 사람 가져오기
                person = emc.find(person.getId(), Person.class);
                if (!this.editable(business, effectivePerson, person)) {
                    throw new ExceptionAccessDenied(effectivePerson);
                }
                List<Identity> identities = this.listIdentity(business, person);
                // 신분 조직 직무 멤버 삭제, 커밋 후에야 신분 삭제 가능
                emc.beginTransaction(UnitDuty.class);
                this.removeMemberOfUnitDuty(business, identities);
                emc.commit();
                // 신분 삭제
                emc.beginTransaction(Identity.class);
                for (Identity o : identities) {
                    emc.remove(o, CheckRemoveType.all);
                }
                emc.commit();
                // 개인 속성 삭제
                // emc.beginTransaction(PersonAttribute.class);
                // this.removePersonAttribute(business, person);
                // 개인 사용자 정의 정보 삭제
                emc.beginTransaction(Custom.class);
                this.removePersonCustom(business, person);
                // 그룹 멤버 삭제
                emc.beginTransaction(Group.class);
                this.removeMemberOfGroup(business, person, identities);
                // 역할 멤버 삭제
                emc.beginTransaction(Role.class);
                this.removeMemberOfRole(business, person);
                // 조직의 관리 개인 및 상속된 관리 개인 삭제
                emc.beginTransaction(Unit.class);
                this.removeMemberOfUnitController(business, person);
                // 개인 관리자 멤버 삭제
                this.removeMemberOfPersonController(business, person);
                // 보고 대상이 삭제될 사람인 경우 삭제
                this.removeMemberOfPersonSuperior(business, person);
                emc.beginTransaction(Person.class);
                // check 통과를 위해 먼저 한 번 커밋
                emc.commit();
                CacheManager.notify(Person.class);

                Wo wo = new Wo();
                wo.setId(person.getId());
                result.setData(wo);
            }
            return result;
        }
    }

    public static class Wo extends WoId {

    }

//    private void removeMemberOfUnitController(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Unit.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
//        Root<Unit> root = cq.from(Unit.class);
//        Predicate p = cb.isMember(person.getId(), root.get(Unit_.controllerList));
//        List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
//                .collect(Collectors.toList());
//        for (Unit o : os) {
//            o.getControllerList().remove(person.getId());
//        }
//    }

//    private void removeMemberOfPersonController(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Person.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//        Root<Person> root = cq.from(Person.class);
//        Predicate p = cb.isMember(person.getId(), root.get(Person_.controllerList));
//        List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
//                .collect(Collectors.toList());
//        for (Person o : os) {
//            o.getControllerList().remove(person.getId());
//        }
//    }

//    private void removeMemberOfPersonSuperior(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Person.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Person> cq = cb.createQuery(Person.class);
//        Root<Person> root = cq.from(Person.class);
//        Predicate p = cb.equal(root.get(Person_.superior), person.getId());
//        List<Person> os = em.createQuery(cq.select(root).where(p)).getResultList();
//        for (Person o : os) {
//            o.setSuperior("");
//        }
//    }

//    private void removePersonAttribute(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
//        Root<PersonAttribute> root = cq.from(PersonAttribute.class);
//        Predicate p = cb.equal(root.get(PersonAttribute_.person), person.getId());
//        List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
//        for (PersonAttribute o : os) {
//            business.entityManagerContainer().remove(o, CheckRemoveType.all);
//        }
//    }

//    private void removePersonCustom(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Custom.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Custom> cq = cb.createQuery(Custom.class);
//        Root<Custom> root = cq.from(Custom.class);
//        Predicate p = cb.equal(root.get(Custom_.person), person.getId());
//        p = cb.or(p, cb.equal(root.get(Custom_.person), person.getDistinguishedName()));
//        List<Custom> os = em.createQuery(cq.select(root).where(p)).getResultList();
//        for (Custom o : os) {
//            business.entityManagerContainer().remove(o, CheckRemoveType.all);
//        }
//    }

//    private void removeMemberOfGroup(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Group.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Group> cq = cb.createQuery(Group.class);
//        Root<Group> root = cq.from(Group.class);
//        Predicate p = cb.isMember(person.getId(), root.get(Group_.personList));
//        List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
//        for (Group o : os) {
//            o.getPersonList().remove(person.getId());
//        }
//    }

//    private void removeMemberOfRole(Business business, Person person) throws Exception {
//        EntityManager em = business.entityManagerContainer().get(Role.class);
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
//        Root<Role> root = cq.from(Role.class);
//        Predicate p = cb.isMember(person.getId(), root.get(Role_.personList));
//        List<Role> os = em.createQuery(cq.select(root).where(p)).getResultList();
//        for (Role o : os) {
//            o.getPersonList().remove(person.getId());
//        }
//    }

}
