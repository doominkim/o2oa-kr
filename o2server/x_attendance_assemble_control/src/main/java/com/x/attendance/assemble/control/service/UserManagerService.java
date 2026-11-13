package com.x.attendance.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.Business;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.ListTools;

/**
 * 사용자 조직 최상위 조직 정보 관리 서비스 클래스
 * 
 * @author LIYI
 *
 */
public class UserManagerService {

	private static Logger logger = LoggerFactory.getLogger(UserManagerService.class);

	public List<String> listIdentitiesWithPerson(String personName) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().identity().listWithPerson(personName);
		}
	}

	/**
	 * 직원의 소속 조직 조회, 사용자 신분이 여러 개면 조직 등급이 가장 높은 조직 이름 사용
	 * 
	 * @param personName dn
	 * @return unit 역시 dn
	 * @throws Exception
	 */
	public String getUnitNameWithPersonName(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		Integer level = 0;
		String result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson(personName);
			if (ListTools.isNotEmpty(unitNames)) {
				for (String unitName : unitNames) {
					if (StringUtils.isNotEmpty(unitName) && !"null".equals(unitName)) {
						unit = business.organization().unit().getObject(unitName);
						if (level < unit.getLevel()) {
							level = unit.getLevel();
							result = unitName;
						}
					}
				}
			}
		}
		return result;
	}

	public Unit getUnitWithPersonName(String personName) throws Exception {
		List<String> unitNames = null;
		Business business = null;
		Integer level = 0;
		Unit result = null;
		Unit unit = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unitNames = business.organization().unit().listWithPerson(personName);
			if (ListTools.isNotEmpty(unitNames)) {
				for (String unitName : unitNames) {
					if (StringUtils.isNotEmpty(unitName) && !"null".equals(unitName)) {
						unit = business.organization().unit().getObject(unitName);
						if (level < unit.getLevel()) {
							level = unit.getLevel();
							result = unit;
						}
					}
				}
			}
		}
		return unit;
	}

	/**
	 * 신분 이름으로 조직 이름 조회
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getUnitNameWithIdentity(String identity) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (StringUtils.equalsAnyIgnoreCase("xadmin", identity)
					|| StringUtils.equalsAnyIgnoreCase("cipher", identity)) {
				return null;
			}
			return business.organization().unit().getWithIdentity(identity);
		}
	}

	/**
	 * 신분 이름으로 조직 이름 조회
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public Unit getUnitWithIdentity(String identity) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			if (StringUtils.equalsAnyIgnoreCase("xadmin", identity)
					|| StringUtils.equalsAnyIgnoreCase("cipher", identity)) {
				return null;
			}
			String name = business.organization().unit().getWithIdentity(identity);
			return business.organization().unit().getObject(name);
		}
	}

	/**
	 * 신분 이름으로 최상위 조직 이름 조회
	 * 
	 * @param identity
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameByIdentity(String identity) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			String unitName = business.organization().unit().getWithIdentityWithLevel(identity, 1);
			if (StringUtils.isEmpty(unitName)) {
				unitName = getUnitNameWithIdentity(identity);
			}
			return unitName;
		}
	}

	/**
	 * 조직 이름 유효성 확인
	 * 
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public String checkUnitNameExists(String unitName) throws Exception {
		if (unitName == null || unitName.isEmpty()) {
			throw new Exception("name is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().get(unitName);
		}
	}

	/**
	 * 직원 이름으로 최상위 조직 이름 조회, 신분이 여러 개면 첫 번째 신분만 사용
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithPersonName(String personName) throws Exception {
		String identity = null;
		String topUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 전달된 personName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			personName = business.organization().person().get(personName);
			identity = getIdentityWithPerson(personName);
			if (StringUtils.isNotEmpty(identity)) {
				topUnitName = business.organization().unit().getWithIdentityWithLevel(identity, 1);
			}
			if (StringUtils.isEmpty(topUnitName)) {
				topUnitName = getUnitNameWithIdentity(identity);
			}
			return topUnitName;
		}
	}

	/**
	 * 개인 이름으로 모든 신분 중 조직 등급이 가장 높은 신분 조회
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	private String getIdentityWithPerson(String personName) throws Exception {
		List<String> identities = null;
		String unitName = null;
		Integer level = 0;
		String resultIdentity = null;
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 전달된 personName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			personName = business.organization().person().get(personName);
			identities = business.organization().identity().listWithPerson(personName);
			if (identities != null && !identities.isEmpty()) {
				for (String identity : identities) {
					unitName = business.organization().unit().getWithIdentity(identity);
					unit = business.organization().unit().getObject(unitName);
					if (level < unit.getLevel()) {
						level = unit.getLevel();
						resultIdentity = identity;
					}
				}
			}
			return resultIdentity;
		}
	}

	/**
	 * 상위 조직 이름 조회
	 * 
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public String getParentUnitWithUnitName(String unitName) throws Exception {
		Unit currentUnit = null;
		Unit parentUnit = null;
		String parentUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			currentUnit = business.organization().unit().getObject(unitName);
			if (currentUnit != null) {
				parentUnitName = currentUnit.getSuperior();
				if (StringUtils.isNotEmpty(parentUnitName) && !"0".equals(parentUnitName)) {
					try {
						parentUnit = business.organization().unit().getObject(currentUnit.getSuperior());
						if (parentUnit == null) {
							return null;
						} else {
							return parentUnit.getDistinguishedName();
						}
					} catch (NullPointerException e) {
						logger.error(e);
					}
				}
			}
		}
		return null;
	}

	/**
	 * 조직 이름으로 최상위 조직 이름 조회(재귀)
	 * 
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public String getTopUnitNameWithUnitName(String unitName) throws Exception {
		Unit currentUnit = null;
		Unit parentUnit = null;
		String parentUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			currentUnit = business.organization().unit().getObject(unitName);
			if (currentUnit != null) {
				parentUnitName = currentUnit.getSuperior();
				if (StringUtils.isNotEmpty(parentUnitName) && !"0".equals(parentUnitName)) {
					try {
						parentUnit = business.organization().unit().getObject(parentUnitName);
					} catch (NullPointerException e) {
					}
				}
				if (parentUnit == null) {
					return currentUnit.getDistinguishedName();
				} else {
					return getTopUnitNameWithUnitName(parentUnit.getDistinguishedName());
				}
			} else {
				throw new Exception("unit is not exists:" + unitName);
			}
		}
	}

	public Unit getTopUnitWithUnitName(String unitName) throws Exception {
		Unit currentUnit = null;
		Unit parentUnit = null;
		String parentUnitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			currentUnit = business.organization().unit().getObject(unitName);
			if (currentUnit != null) {
				parentUnitName = currentUnit.getSuperior();
				if (StringUtils.isNotEmpty(parentUnitName) && !"0".equals(parentUnitName)) {
					try {
						parentUnit = business.organization().unit().getObject(parentUnitName);
					} catch (NullPointerException e) {
						logger.error(e);
					}
				}
				if (parentUnit == null) {
					return currentUnit;
				} else {
					return getTopUnitWithUnitName(parentUnit.getDistinguishedName());
				}
			} else {
				throw new Exception("unit is not exists:" + unitName);
			}
		}
	}

	/**
	 * 상위 조직 이름으로 모든 하위 조직 이름 목록 조회
	 * 
	 * @param parentUnitName
	 * @return
	 * @throws Exception
	 */
	public List<String> listSubUnitNameWithParent(String parentUnitName) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithUnitSubNested(parentUnitName);
		}
	}

	public List<Person> listAllPersons() throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().listAllObject();
		}
	}

	/**
	 * 조직 이름으로 모든 사용자 조회(재귀)
	 *
	 * @param Unit
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithUnitSubNested(String Unit) throws Exception {
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().listWithUnitSubNested(Unit);
		}
	}

	/**
	 * 사용자 이름으로 사용자 이름 조회
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Person getPersonObjByName(String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			throw new Exception("identityName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getObject(name);
		}
	}

	/**
	 * 개인 이름으로 주 신분 조회
	 *
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getMajorIdentityWithPerson(String personName) throws Exception {
		List<String> identities = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			// 전달된 perosnName이 개인일 수도, 신분일 수도 있으므로 호환 처리
			personName = business.organization().person().get(personName);
			identities = business.organization().identity().listWithPerson(personName);
			if (ListTools.isNotEmpty(identities)) {
				if (identities.size() == 1) {
					return identities.get(0);
				} else {
					for (String identity : identities) {
						Identity obj = business.organization().identity().getObject(identity);
						if ((null != obj) && BooleanUtils.isTrue(obj.getMajor())) {
							return identity;
						}
					}
				}
				return identities.get(0);
			}
			return null;
		}
	}

	/**
	 * 사용자 신분 정보로 사용자 이름 조회
	 * 
	 * @param identityName
	 * @return
	 * @throws Exception
	 */
	public String getPersonNameByIdentity(String identityName) throws Exception {
		if (identityName == null || identityName.isEmpty()) {
			throw new Exception("identityName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().getWithIdentity(identityName);
		}
	}

	/**
	 * 사용자 존재 여부 확인
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String checkPersonExists(String personName) throws Exception {
		if (personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().person().get(personName);
		}
	}

	public Boolean checkHasPerson(String personName, String unitName, Boolean recursive) throws Exception {
		if (personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (unitName == null || unitName.isEmpty()) {
			throw new Exception("unitName is null!");
		}
		if (recursive == null) {
			throw new Exception("recursive is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().checkHasPerson(personName, unitName, recursive);
		}
	}

	public List<String> listUnitNamesWithPerson(String personName) throws Exception {
		if (personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unit().listWithPerson(personName);
		}
	}

	public List<String> listAttributeWithPersonWithName(String personName, String attributeName) throws Exception {
		if (personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (attributeName == null || attributeName.isEmpty()) {
			throw new Exception("attributeName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().personAttribute().listAttributeWithPersonWithName(personName, attributeName);
		}
	}

	/**
	 * 개인으로 보고 대상 조회
	 * 
	 * @param personName
	 * @return
	 * @throws Exception
	 */
	public String getReporterWithPerson(String personName) throws Exception {
		if (StringUtils.isEmpty(personName)) {
			throw new Exception("personName is null!");
		}
		Person person = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			person = business.organization().person().getObject(personName);
			if (person != null) {
				if (person.getSuperior() != null) {
					return business.organization().person().get(person.getSuperior());
				}
			}
		}
		return null;
	}

	public List<String> getUnitDutyWithUnitWithDuty(String unitName, String dutyName) throws Exception {
		if (unitName == null || unitName.isEmpty()) {
			throw new Exception("unitName is null!");
		}
		if (dutyName == null || dutyName.isEmpty()) {
			throw new Exception("dutyName is null!");
		}
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			return business.organization().unitDuty().listIdentityWithUnitWithName(unitName, dutyName);
		}
	}

	public List<String> getUnitDutyWithPersonWithDuty(String personName, String dutyName) throws Exception {
		if (personName == null || personName.isEmpty()) {
			throw new Exception("personName is null!");
		}
		if (dutyName == null || dutyName.isEmpty()) {
			throw new Exception("dutyName is null!");
		}
		String unitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			try {
				unitName = getUnitNameWithPersonName(personName);
			} catch (NullPointerException ex) {
				throw new Exception("개인으로 조직 정보를 조회하는 중 오류가 발생했습니다. person:" + personName);
			}
			if (unitName == null || unitName.isEmpty()) {
				try {
					unitName = getUnitNameWithIdentity(personName);
				} catch (NullPointerException ex) {
					throw new Exception("개인으로 조직 정보를 조회하는 중 오류가 발생했습니다. person:" + personName);
				}
			}
			if (unitName == null || unitName.isEmpty()) {
				return null;
			} else {
				try {
					return business.organization().unitDuty().listIdentityWithUnitWithName(unitName, dutyName);
				} catch (NullPointerException ex) {
					throw new Exception("조직으로 조직 직무를 조회하는 중 오류가 발생했습니다. unitName:" + unitName + ", dutyName:" + dutyName);
				}
			}
		}
	}

	public List<String> getUnitDutyWithIdentityWithDuty(String identity, String dutyName) throws Exception {
		if (identity == null || identity.isEmpty()) {
			throw new Exception("identity is null!");
		}
		if (dutyName == null || dutyName.isEmpty()) {
			throw new Exception("dutyName is null!");
		}
		String unitName = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			try {
				unitName = getUnitNameWithIdentity(identity);
			} catch (NullPointerException ex) {
				throw new Exception("개인 소속으로 조직 정보를 조회하는 중 오류가 발생했습니다. identity:" + identity);
			}
			if (unitName == null || unitName.isEmpty()) {
				throw new Exception("개인 소속으로 상위 조직 정보를 찾을 수 없습니다. identity:" + identity);
			} else {
				try {
					return business.organization().unitDuty().listIdentityWithUnitWithName(unitName, dutyName);
				} catch (NullPointerException ex) {
					throw new Exception("조직으로 조직 직무를 조회하는 중 오류가 발생했습니다. unitName:" + unitName + ", dutyName:" + dutyName);
				}
			}
		}
	}

	public String getUnitLevelNameWithName(String unitName) throws Exception {
		if (unitName == null || unitName.isEmpty()) {
			throw new Exception("unitName is null!");
		}
		Unit unit = null;
		Business business = null;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			business = new Business(emc);
			unit = business.organization().unit().getObject(unitName);
			if (unit != null) {
				return unit.getLevelName();
			}
		}
		return null;
	}
}