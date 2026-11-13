package com.x.organization.assemble.control.jaxrs.unitattribute;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;

abstract class BaseAction extends StandardJaxrsAction {

	/** 고유 식별자가 비어 있지 않으면 고유 여부 확인 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, UnitAttribute unitAttribute) throws Exception {
		if (StringUtils.isNotEmpty(unitAttribute.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(unitAttribute.getId(), UnitAttribute.class,
					unitAttribute.getUnique())) {
				return true;
			}
		}
		return false;
	}

	/** 같은 unit 아래에서 이름 중복 불가 */
	protected boolean duplicateOnUnit(Business business, Unit unit, String name, UnitAttribute exclude)
			throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<UnitAttribute> root = cq.from(UnitAttribute.class);
		Predicate p = cb.equal(root.get(UnitAttribute_.name), name);
		p = cb.and(p, cb.equal(root.get(UnitAttribute_.unit), unit.getId()));
		p = cb.and(p, cb.notEqual(root.get(UnitAttribute_.id), exclude.getId()));
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

}
