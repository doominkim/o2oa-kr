package com.x.organization.assemble.control.jaxrs.identity;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Unit;

abstract class BaseAction extends StandardJaxrsAction {

	/** 고유 식별자가 비어 있지 않으면 고유 여부 확인 */
	protected boolean uniqueDuplicateWhenNotEmpty(Business business, Identity identity) throws Exception {
		if (StringUtils.isNotEmpty(identity.getUnique())) {
			if (business.entityManagerContainer().duplicateWithFlags(identity.getId(), Identity.class,
					identity.getUnique())) {
				return true;
			}
		}
		return false;
	}

	protected Unit topUnit(Business business, Unit unit) throws Exception {
		if (Objects.equals(Unit.TOP_LEVEL, unit.getLevel())) {
			return unit;
		}
		List<Unit> os = business.unit().listSupNestedObject(unit);
		return os.get(0);
	}

	protected boolean isMember(List<String> names, Identity identity){
		return names.stream().anyMatch(ln -> identity.getUnitLevelName().startsWith(ln));
	}

}
