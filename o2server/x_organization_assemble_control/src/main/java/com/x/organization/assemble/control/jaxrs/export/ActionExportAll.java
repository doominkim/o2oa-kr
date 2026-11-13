package com.x.organization.assemble.control.jaxrs.export;

import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.entity.Role;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.assemble.control.ThisApplication;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.PersonAttribute_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import com.x.organization.core.entity.Unit_;

/**
 * 가져온 파일은 파일 저장소를 사용하지 않고 DB의 BLOB 열에 직접 저장
 */
public class ActionExportAll extends BaseAction {

	private static final Logger logger = LoggerFactory.getLogger(ActionExportAll.class);
	List<Unit> allUnitList = new ArrayList<>();
	List<String> allUnitAttributeList = new ArrayList<>();
	List<Person> allPersonList = new ArrayList<>();
	List<String> allPersonAttributeList = new ArrayList<>();
	List<Identity> allIdentityList = new ArrayList<>();
	List<UnitDuty> allDutyList = new ArrayList<>();
	List<Group> allGroupList = new ArrayList<>();
	List<Role> allRoleList = new ArrayList<>();
	Workbook wb = new XSSFWorkbook();

	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, Boolean stream ) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = null;
		String fileName = "person_export_" + DateTools.formatDate(new Date()) + ".xlsx";
		//설명 sheet 생성
		this.createNoticeSheet();

		// 내보낼 데이터 먼저 조회
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			this.listUnit(business);
			this.listPerson(business);
			this.listIdentity(business);
			this.listDuty(business);
			this.listGroup(business);
			this.listRole(business);

			// 조직 정보 결과를 EXCEL로 구성
			this.composeUnit( business, "조직 정보", allUnitList );

			// 사용자 기본 정보 결과를 EXCEL로 구성
			this.composePerson( business, "사용자 기본 정보", allPersonList );

			// 사용자 신분 정보 결과를 EXCEL로 구성
			this.composeIdentity( business, "사용자 소속 정보", allIdentityList );

			// 직무 정보 결과를 EXCEL로 구성
			this.composeDuty( business, "직무 정보", allDutyList );

			// 그룹 정보 결과를 EXCEL로 구성
			this.composeGroup( business, "그룹 정보", allGroupList );

			this.composeRole( business, "역할 정보", allRoleList );
		}

		if( wb != null ) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                wb.write(bos);
                wo = new Wo(bos.toByteArray(),
                        this.contentType(stream, fileName),
                        this.contentDisposition(stream, fileName));
            }
		}
		result.setData(wo);
		return result;
	}

	private void listUnit(Business business) throws Exception {
		List<Unit> topUnitList = this.listTopUnit(business);
		if(ListTools.isNotEmpty(topUnitList)){
			allUnitList.addAll(topUnitList);
			for (Unit unitItem : topUnitList) {
				List<Unit> ulist= this.listSubNested(business, unitItem);
				if(ListTools.isNotEmpty(ulist)){
					allUnitList.addAll(ulist);
				}
			}
		}

		if(ListTools.isNotEmpty(allUnitList)){
			allUnitList = business.unit().sort(allUnitList);
			for(Unit unit:allUnitList){
				List<UnitAttribute> us = this.ListUnitAttributes("unitattribute/list/unit/",unit.getId());
				if(ListTools.isNotEmpty(us)){
					for(UnitAttribute u : us){
						String uName = u.getName();
						if(StringUtils.isNotEmpty(uName) &&  !allUnitAttributeList.contains(uName)){
							allUnitAttributeList.add(uName);
						}
					}
				}
			}
		}
	}

	private void listPerson(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Person.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> cq = cb.createQuery(Person.class);
		Root<Person> root = cq.from(Person.class);
		allPersonList = em.createQuery(cq.select(root)).getResultList();
		if(ListTools.isNotEmpty(allPersonList)){
			for(Person person:allPersonList){
				List<PersonAttribute> ps = this.listAttributeWithPerson(business,person.getId());
				for(PersonAttribute o:ps){
					String pName = o.getName();
					if(StringUtils.isNotEmpty(pName) &&  !allPersonAttributeList.contains(pName)){
						allPersonAttributeList.add(pName);
					}
				}
			}
		}
	}

	private void listIdentity(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
		Root<Identity> root = cq.from(Identity.class);
		allIdentityList = em.createQuery(cq.select(root)).getResultList();
	}

	private void listDuty(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		allDutyList = em.createQuery(cq.select(root)).getResultList();
	}

	private void listGroup(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Group.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Group> cq = cb.createQuery(Group.class);
		Root<Group> root = cq.from(Group.class);
		allGroupList = em.createQuery(cq.select(root)).getResultList();
	}

	private void listRole(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Role.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Role> cq = cb.createQuery(Role.class);
		Root<Role> root = cq.from(Role.class);
		allRoleList = em.createQuery(cq.select(root)).getResultList();
	}

	private List<Unit> listTopUnit(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Unit.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Unit> cq = cb.createQuery(Unit.class);
		Root<Unit> root = cq.from(Unit.class);
		Predicate p = cb.equal(root.get(Unit_.level), 1);
		List<Unit> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<Unit> listSubNested(Business business,Unit unit) throws Exception {
		List<Unit> os = business.unit().listSubNestedObject(unit);
		return os;
	}

	private void createNoticeSheet() throws Exception {

			Row row = null;
			// 새 표 생성
			Sheet sheet = wb.createSheet("주의 사항");

			// 먼저 헤더 생성
			row = sheet.createRow(2);
			row.createCell(0).setCellValue("주의 사항:");

			row = sheet.createRow(4);
			row.createCell(0).setCellValue("1. 표 안의 셀을 병합(분할)하지 마세요. 각 열의 순서를 바꾸거나 삭제하면 데이터가 꼬일 수 있습니다.");

			row = sheet.createRow(6);
			row.createCell(0).setCellValue("2. * 표시는 필수 항목입니다.");

			row = sheet.createRow(8);
			row.createCell(0).setCellValue("3. 형식에 특별한 요구 사항이 있으면 열 이름의 메모를 참고하세요.");

			row = sheet.createRow(10);
			row.createCell(0).setCellValue("4. 표의 예시 데이터는 참고용이므로 실제로 가져올 때는 삭제하세요.");

			row = sheet.createRow(12);
			row.createCell(0).setCellValue("5. 각 시트의 순서를 바꾸지 말고 이 시트를 삭제하지 마세요.");

	}

	private void composeUnit(Business business, String sheetName, List<Unit> unitList) throws Exception {
		Unit unit = null;
		EntityManagerContainer em = business.entityManagerContainer();

		Row row = null;
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("조직 이름 *");
		row.createCell(1).setCellValue("고유 코드 *");
		row.createCell(2).setCellValue("조직 레벨 이름 *");
		row.createCell(3).setCellValue("상위 조직 코드");
		row.createCell(4).setCellValue("설명");
		row.createCell(5).setCellValue("정렬 순서");
		if(ListTools.isNotEmpty(allUnitAttributeList)){
			for(int n = 0; n < allUnitAttributeList.size(); n++){
				row.createCell(5+n+1).setCellValue("("+allUnitAttributeList.get(n)+")");
			}
		}
		if (ListTools.isNotEmpty(unitList) ) {
			for (int i = 0; i < unitList.size(); i++) {
				unit = unitList.get(i);
				row = sheet.createRow(i + 1);
				row.createCell(0).setCellValue(unit.getName());
				row.createCell(1).setCellValue(unit.getUnique());
				if(ListTools.isNotEmpty(unit.getTypeList())){
					row.createCell(2).setCellValue(StringUtils.join(unit.getTypeList(), ","));
				}else{
					row.createCell(2).setCellValue("");
				}
				String superior = unit.getSuperior();
				if(StringUtils.isEmpty(superior)){
					row.createCell(3).setCellValue("");
				}else{
					Unit u = null;
					u = em.flag(unit.getSuperior(), Unit.class);
					if(u != null){
						row.createCell(3).setCellValue(u.getUnique());
					}else{
						row.createCell(3).setCellValue(unit.getSuperior());
					}

				}
				row.createCell(4).setCellValue(unit.getDescription());
				row.createCell(5).setCellValue(unit.getOrderNumber()+"");
				List<UnitAttribute> os= this.ListUnitAttributes("unitattribute/list/unit/",unit.getId());
				if(ListTools.isNotEmpty(allUnitAttributeList) && ListTools.isNotEmpty(os)){
					for(int m = 0; m < allUnitAttributeList.size(); m++){
						String uName = allUnitAttributeList.get(m);
						String uValue = "";
						for(UnitAttribute o : os){
							if(uName.equals(o.getName())){
								//uValue = o.getAttributeList().toString();
								uValue = String.join(",", o.getAttributeList().stream().map(String::valueOf).collect(Collectors.toList()));
							}
						}
						row.createCell(5+m+1).setCellValue(uValue);
					}
				}
			}
		}
	}

	private void composePerson(Business business, String sheetName, List<Person> personList) throws Exception {
		Person person = null;

		Row row = null;
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("사용자 이름 *");
		row.createCell(1).setCellValue("고유 코드 *");
		row.createCell(2).setCellValue("휴대전화 번호 *");
		row.createCell(3).setCellValue("사번");
		row.createCell(4).setCellValue("사무실 전화");
		row.createCell(5).setCellValue("성별");
		row.createCell(6).setCellValue("이메일");
		if(ListTools.isNotEmpty(allPersonAttributeList)){
			for(int n = 0; n < allPersonAttributeList.size(); n++){
				row.createCell(6+n+1).setCellValue("("+allPersonAttributeList.get(n)+")");
			}
		}
		if (ListTools.isNotEmpty(personList) ) {
			for (int i = 0; i < personList.size(); i++) {
				person = personList.get(i);
				row = sheet.createRow(i + 1);
				row.createCell(0).setCellValue(person.getName());
				row.createCell(1).setCellValue(person.getUnique());
				row.createCell(2).setCellValue(person.getMobile());
				row.createCell(3).setCellValue(person.getEmployee());
				row.createCell(4).setCellValue(person.getOfficePhone());
				row.createCell(5).setCellValue(Objects.toString(person.getGenderType(),""));
				row.createCell(6).setCellValue(person.getMail());
				List<PersonAttribute> os= this.listAttributeWithPerson(business,person.getId());
				if(ListTools.isNotEmpty(allPersonAttributeList) && ListTools.isNotEmpty(os)){
					for(int m = 0; m < allPersonAttributeList.size(); m++){
						String pName = allPersonAttributeList.get(m);
						String pValue = "";
						for(PersonAttribute o : os){
							if(pName.equals(o.getName())){
								//pValue = o.getAttributeList().toString();
								pValue = String.join(",", o.getAttributeList().stream().map(String::valueOf).collect(Collectors.toList()));
							}
						}
						row.createCell(6+m+1).setCellValue(pValue);
					}
				}
			}
		}
	}

	private void composeIdentity(Business business, String sheetName, List<Identity> identityList) throws Exception {
		Identity identity = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		Row row = null;
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("사용자 고유 코드 *");
		row.createCell(1).setCellValue("조직 고유 코드 *");
		row.createCell(2).setCellValue("소속 코드");
		row.createCell(3).setCellValue("주/겸직");
		if (ListTools.isNotEmpty(identityList) ) {
			for (int i = 0; i < identityList.size(); i++) {
				identity = identityList.get(i);
				row = sheet.createRow(i + 1);
				Person person = emc.flag(identity.getPerson(), Person.class);
				Unit unit = emc.flag(identity.getUnit(), Unit.class);
				if(person != null){
					row.createCell(0).setCellValue(person.getUnique());
					if(unit != null){
						List<String> idutyList = new ArrayList<>();
						String unitId = "";
						unitId = unit.getId();
						row.createCell(1).setCellValue(unit.getUnique());
						row.createCell(2).setCellValue(identity.getUnique());
						row.createCell(3).setCellValue(String.valueOf(identity.getMajor()));
					}
				}

			}
		}
	}

	private void composeDuty(Business business, String sheetName, List<UnitDuty> dutyList) throws Exception {
		UnitDuty duty = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		Row row = null;
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(40);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("직무 이름 *");
		row.createCell(1).setCellValue("직무 소속 조직 고유 코드 *");
		row.createCell(2).setCellValue("직무 코드");
		row.createCell(3).setCellValue("직무 설명");
		row.createCell(4).setCellValue("직무 구성원 고유 코드");
		row.createCell(5).setCellValue("직무 구성원 소속 조직 고유 코드");
		if (ListTools.isNotEmpty(dutyList) ) {

			int currentRow = 0;
			for (int i = 0; i < dutyList.size(); i++) {
				duty = dutyList.get(i);
				Unit unit = emc.flag(duty.getUnit(), Unit.class);
				List<String> identityList = duty.getIdentityList();
				if (ListTools.isNotEmpty(identityList)) {
					for (int j = 0; j < identityList.size(); j++) {
						String identityId = identityList.get(j);
						Identity identity = emc.flag(identityId, Identity.class);
						if (identity != null) {
							currentRow = currentRow+1;
							row = sheet.createRow(currentRow);
							row.createCell(0).setCellValue(duty.getName());
							if (unit != null) {
								row.createCell(1).setCellValue(unit.getUnique());
							} else {
								row.createCell(1).setCellValue("");
							}
							row.createCell(2).setCellValue(duty.getUnique());
							row.createCell(3).setCellValue(duty.getDescription());
							Person iperson = emc.flag(identity.getPerson(), Person.class);
							Unit iunit = emc.flag(identity.getUnit(), Unit.class);
							if (iperson != null) {
								row.createCell(4).setCellValue(iperson.getUnique());
							} else {
								row.createCell(4).setCellValue("");
							}
							if (iunit != null) {
								row.createCell(5).setCellValue(iunit.getUnique());
							} else {
								row.createCell(5).setCellValue("");
							}
						}
					}
				} else {
					currentRow = currentRow+1;
					row = sheet.createRow(currentRow);
					row.createCell(0).setCellValue(duty.getName());
					if (unit != null) {
						row.createCell(1).setCellValue(unit.getUnique());
					} else {
						row.createCell(1).setCellValue("");
					}
					row.createCell(2).setCellValue(duty.getUnique());
					row.createCell(3).setCellValue(duty.getDescription());
					row.createCell(4).setCellValue("");
					row.createCell(5).setCellValue("");
				}
			}
		}
	}

	private void composeGroup(Business business, String sheetName, List<Group> groupList) throws Exception {
		Group group = null;

		Row row = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("그룹 이름 *");
		row.createCell(1).setCellValue("그룹 코드 *");
		row.createCell(2).setCellValue("사용자 고유 코드");
		row.createCell(3).setCellValue("소속 고유 코드");
		row.createCell(4).setCellValue("조직 고유 코드");
		row.createCell(5).setCellValue("하위 그룹 코드");
		row.createCell(6).setCellValue("설명");
		if (ListTools.isNotEmpty(groupList) ) {
			int forNumber = 0;
			for (int i = 0; i < groupList.size(); i++) {
				group = groupList.get(i);
				//forNumber = i;
				List<String> personList = group.getPersonList();
				List<String> unitList = group.getUnitList();
				List<String> groupsList = group.getGroupList();
				List<String> identityList = group.getIdentityList();

				if(ListTools.isEmpty(personList) && ListTools.isEmpty(identityList) && ListTools.isEmpty(unitList) && ListTools.isEmpty(groupsList)){
					forNumber = forNumber+1;
					row = sheet.createRow(forNumber);
					row.createCell(0).setCellValue(group.getName());
					row.createCell(1).setCellValue(group.getUnique());
					row.createCell(2).setCellValue("");
					row.createCell(3).setCellValue("");
					row.createCell(4).setCellValue("");
					row.createCell(5).setCellValue("");
					row.createCell(6).setCellValue(group.getDescription());
				}else{
					if(ListTools.isNotEmpty(personList)){
						for(String personId : personList){
							Person person = emc.flag(personId, Person.class);
							if(person != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(group.getName());
								row.createCell(1).setCellValue(group.getUnique());
								row.createCell(2).setCellValue(person.getUnique());
								row.createCell(3).setCellValue("");
								row.createCell(4).setCellValue("");
								row.createCell(5).setCellValue("");
								row.createCell(6).setCellValue(group.getDescription());
							}
						}
					}
					if(ListTools.isNotEmpty(identityList)){
						for(String identityId : identityList){
							Identity identity = emc.flag(identityId, Identity.class);
							if(identity != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(group.getName());
								row.createCell(1).setCellValue(group.getUnique());
								row.createCell(2).setCellValue("");
								row.createCell(3).setCellValue(identity.getUnique());
								row.createCell(4).setCellValue("");
								row.createCell(5).setCellValue("");
								row.createCell(6).setCellValue(group.getDescription());
							}
						}
					}
					if(ListTools.isNotEmpty(unitList)){
						for(String unitId : unitList){
							Unit unit = emc.flag(unitId, Unit.class);
							if(unit != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(group.getName());
								row.createCell(1).setCellValue(group.getUnique());
								row.createCell(2).setCellValue("");
								row.createCell(3).setCellValue("");
								row.createCell(4).setCellValue(unit.getUnique());
								row.createCell(5).setCellValue("");
								row.createCell(6).setCellValue(group.getDescription());
							}
						}
					}
					if(ListTools.isNotEmpty(groupsList)){
						for(String groupId : groupsList){
							Group subGroup = emc.flag(groupId, Group.class);
							if(subGroup != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(group.getName());
								row.createCell(1).setCellValue(group.getUnique());
								row.createCell(2).setCellValue("");
								row.createCell(3).setCellValue("");
								row.createCell(4).setCellValue("");
								row.createCell(5).setCellValue(subGroup.getUnique());
								row.createCell(6).setCellValue(group.getDescription());
							}
						}
					}
				}

			}
		}
	}

	private void composeRole(Business business, String sheetName, List<Role> roleList) throws Exception {
		Role role = null;
		Row row = null;
		EntityManagerContainer emc = business.entityManagerContainer();
		// 새 표 생성
		Sheet sheet = wb.createSheet(sheetName);
		sheet.setDefaultColumnWidth(25);
		// 먼저 헤더 생성
		row = sheet.createRow(0);
		row.createCell(0).setCellValue("역할 이름 *");
		row.createCell(1).setCellValue("역할 코드 *");
		row.createCell(2).setCellValue("사용자 고유 코드");
		row.createCell(3).setCellValue("그룹 고유 코드");
		row.createCell(4).setCellValue("설명");
		if (ListTools.isNotEmpty(roleList) ) {
			int forNumber = 0;
			for (int i = 0; i < roleList.size(); i++) {
				role = roleList.get(i);
				List<String> personList = role.getPersonList();
				List<String> groupsList = role.getGroupList();

				if(ListTools.isEmpty(personList) && ListTools.isEmpty(groupsList)){
					if(!OrganizationDefinition.DEFAULTROLES.contains(role.getName())) {
						forNumber = forNumber + 1;
						row = sheet.createRow(forNumber);
						row.createCell(0).setCellValue(role.getName());
						row.createCell(1).setCellValue(role.getUnique());
						row.createCell(2).setCellValue("");
						row.createCell(3).setCellValue("");
						row.createCell(4).setCellValue(role.getDescription());
					}
				}else{
					if(ListTools.isNotEmpty(personList)){
						for(String personId : personList){
							Person person = emc.flag(personId, Person.class);
							if(person != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(role.getName());
								row.createCell(1).setCellValue(role.getUnique());
								row.createCell(2).setCellValue(person.getUnique());
								row.createCell(3).setCellValue("");
								row.createCell(4).setCellValue(role.getDescription());
							}
						}
					}
					if(ListTools.isNotEmpty(groupsList)){
						for(String groupId : groupsList){
							Group group = emc.flag(groupId, Group.class);
							if(group != null){
								forNumber = forNumber+1;
								row = sheet.createRow(forNumber);
								row.createCell(0).setCellValue(role.getName());
								row.createCell(1).setCellValue(role.getUnique());
								row.createCell(2).setCellValue("");
								row.createCell(3).setCellValue(group.getUnique());
								row.createCell(4).setCellValue(role.getDescription());
							}
						}
					}
				}

			}
		}
	}

	private List<PersonAttribute> listAttributeWithPerson(Business business,String personId) throws Exception{
		EntityManager em = business.entityManagerContainer().get(PersonAttribute.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PersonAttribute> cq = cb.createQuery(PersonAttribute.class);
		Root<PersonAttribute> root = cq.from(PersonAttribute.class);
		Predicate p = cb.equal(root.get(PersonAttribute_.person), personId);
		List<PersonAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}

	private List<UnitDuty> listDutyWithIdentity(Business business,String identityId) throws Exception{
		EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
		Root<UnitDuty> root = cq.from(UnitDuty.class);
		Predicate p = cb.isMember(identityId, root.get(UnitDuty_.identityList));
		List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
		return os;
	}
	private List<UnitAttribute> ListUnitAttributes(String path , String unitId) throws Exception{
		ActionResponse resp =  ThisApplication.context().applications()
				.getQuery(x_organization_assemble_control.class, path+unitId);
		return resp.getDataAsList(UnitAttribute.class);
	}

	public static class Wo extends WoFile {
		public Wo(byte[] bytes, String contentType, String contentDisposition) {
			super(bytes, contentType, contentDisposition);
		}
	}

}
