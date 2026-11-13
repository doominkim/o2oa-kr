package com.x.organization.assemble.personal.jaxrs.empower;

import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.accredit.Empower;
import com.x.organization.core.entity.accredit.Empower_;

abstract class BaseAction extends StandardJaxrsAction {

    protected void check(Business business, Empower empower) throws Exception {
        if (StringUtils.isEmpty(empower.getFromIdentity())) {
            throw new ExceptionEmptyFromIdentity();
        }
        if (StringUtils.isEmpty(empower.getToIdentity())) {
            throw new ExceptionEmptyToIdentity();
        }
        switch (Objects.toString(empower.getType())) {
            case Empower.TYPE_ALL:
                if (this.typeAllExist(business, empower)) {
                    throw new ExceptionTypeAllExist(empower.getFromIdentity());
                }
                break;
            case Empower.TYPE_APPLICATION:
                if (this.typeApplicationExist(business, empower)) {
                    throw new ExceptionTypeApplicationExist(empower.getFromIdentity(), empower.getApplication());
                }
                break;
            case Empower.TYPE_PROCESS:
                if (this.typeProcessExist(business, empower)) {
                    throw new ExceptionTypeProcessExist(empower.getFromIdentity(), empower.getProcess());
                }
                break;
            case Empower.TYPE_FILTER:
                if (StringUtils.isEmpty(empower.getProcess()) || StringUtils.isEmpty(empower.getFilterListData())) {
                    throw new ExceptionTypeFilter(empower.getFromIdentity(), empower.getProcess(),
                            empower.getFilterListData());
                }
                break;
            default:
                throw new ExceptionEntityFieldEmpty(Empower.class, Empower.TYPE_FIELDNAME);
        }
    }

    /**
     * 기간 내 존재 여부 확인, 1. 시작 시각이 기간 내 2. 종료 시각이 기간 내 3. 시작 시각이 지정 시작 시각보다 이르고 종료 시각이 지정 종료 시각보다 늦음
     * 합치면 !(시작 시각이 지정 종료 시각보다 늦음||종료 시각이 지정 시작 시각보다 이름)
     * 완전히 같은 시점에 위임이 발생할 가능성은 낮으므로 종료 시각이 새 시작 시각과 같은 것을 허용
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeAllExist(Business business, Empower empower) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_ALL));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    /**
     * 기간 내 존재 여부 확인, 1. 시작 시각이 기간 내 2. 종료 시각이 기간 내 3. 시작 시각이 지정 시작 시각보다 이르고 종료 시각이 지정 종료 시각보다 늦음
     * 합치면 !(시작 시각이 지정 종료 시각보다 늦음||종료 시각이 지정 시작 시각보다 이름)
     * 완전히 같은 시점에 위임이 발생할 가능성은 낮으므로 종료 시각이 새 시작 시각과 같은 것을 허용
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeApplicationExist(Business business, Empower empower) throws Exception {
        if (StringUtils.isEmpty(empower.getApplication())) {
            throw new ExceptionEntityFieldEmpty(Empower.class, Empower.APPLICATION_FIELDNAME);
        }
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_APPLICATION));
        p = cb.and(p, cb.equal(root.get(Empower_.application), empower.getApplication()));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    /**
     * 기간 내 존재 여부 확인, 1. 시작 시각이 기간 내 2. 종료 시각이 기간 내 3. 시작 시각이 지정 시작 시각보다 이르고 종료 시각이 지정 종료 시각보다 늦음
     * 합치면 !(시작 시각이 지정 종료 시각보다 늦음||종료 시각이 지정 시작 시각보다 이름)
     * 완전히 같은 시점에 위임이 발생할 가능성은 낮으므로 종료 시각이 새 시작 시각과 같은 것을 허용
     * @param business
     * @param empower
     * @return
     * @throws Exception
     */
    private boolean typeProcessExist(Business business, Empower empower) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Empower.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Empower> root = cq.from(Empower.class);
        Predicate p = cb.equal(root.get(Empower_.fromIdentity), empower.getFromIdentity());
        p = cb.and(p, cb.equal(root.get(Empower_.type), Empower.TYPE_PROCESS));
        p = cb.and(p, cb.equal(root.get(Empower_.process), empower.getProcess()));
        p = cb.and(p, cb.notEqual(root.get(Empower_.id), empower.getId()));
        p = cb.and(p, cb.equal(root.get(Empower_.enable), true));
        p = cb.and(p, cb.not(cb.or(cb.greaterThanOrEqualTo(root.get(Empower_.startTime), empower.getCompletedTime()),
                cb.lessThanOrEqualTo(root.get(Empower_.completedTime), empower.getStartTime()))));
        Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
        return count > 0;
    }

    protected String getPersonDNWithIdentityDN(Business business, String dn) throws Exception {
        Identity identity = business.identity().pick(dn);
        if (null != identity) {
            Person person = business.person().pick(identity.getPerson());
            if (null != person) {
                return person.getDistinguishedName();
            }
        }
        return null;
    }
}