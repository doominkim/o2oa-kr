package com.x.organization.assemble.control.jaxrs.unit;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.Unit_;

abstract class BaseAction extends StandardJaxrsAction {

	/** 고유 식별자가 비어 있지 않으면 고유 여부 확인 */
	protected boolean duplicateUniqueWhenNotEmpty(Business business, Unit unit) throws Exception {
		if (StringUtils.isNotEmpty(unit.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(unit.getId(), Unit.class, unit.getUnique())) {
				return true;
			}
		}
		return false;
	}

	/** 같은 상위 unit 아래에 중복 이름 없음 */
	protected boolean duplicateName(Business business, Unit unit) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.name), unit.getName());
		p = cb.and(p, cb.equal(root.get(Unit_.superior), Objects.toString(unit.getSuperior(), "")));
		p = cb.and(p, cb.notEqual(root.get(Unit_.id), unit.getId()));
		Long count = em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
		return count > 0;
	}

	protected boolean checkNameInvalid(Business business, Unit unit) throws Exception {
		if (StringUtils.containsAny(unit.getName(), new String[] { "\\","/"})) {
			return true;
		}
		return false;

	}

	public static class Control extends GsonPropertyObject {

		private Boolean allowEdit = false;
		private Boolean allowDelete = false;

		public Boolean getAllowEdit() {
			return allowEdit;
		}

		public void setAllowEdit(Boolean allowEdit) {
			this.allowEdit = allowEdit;
		}

		public Boolean getAllowDelete() {
			return allowDelete;
		}

		public void setAllowDelete(Boolean allowDelete) {
			this.allowDelete = allowDelete;
		}

	}

	public static abstract class WoAbstractUnit extends Unit {

		private static final long serialVersionUID = -3622801980504937581L;

		@FieldDescribe("현재 사용자의 조직 작업 가능 여부")
		private Control control = new Control();

		public Control getControl() {
			return control;
		}

		public void setControl(Control control) {
			this.control = control;
		}

	}

	protected <T extends WoAbstractUnit> void updateControl(EffectivePerson effectivePerson, Business business,
			List<T> wos) throws Exception {
		if (effectivePerson.isManager() || business.hasAnyRole(effectivePerson, OrganizationDefinition.Manager,
				OrganizationDefinition.UnitManager, OrganizationDefinition.OrganizationManager)) {
			wos.forEach(o -> {
				o.getControl().setAllowDelete(true);
				o.getControl().setAllowEdit(true);
			});
		} else {
			for (T o : wos) {
				Boolean allow = business.editable(effectivePerson, o);
				o.getControl().setAllowDelete(allow);
				o.getControl().setAllowEdit(allow);
			}
		}
	}

	protected <T extends WoAbstractUnit> void updateControl(EffectivePerson effectivePerson, Business business, T t)
			throws Exception {
		Boolean allow = business.editable(effectivePerson, t);
		t.getControl().setAllowDelete(allow);
		t.getControl().setAllowEdit(allow);
	}

}
