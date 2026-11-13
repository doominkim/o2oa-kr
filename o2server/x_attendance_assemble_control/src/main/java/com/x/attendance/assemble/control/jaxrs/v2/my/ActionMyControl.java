package com.x.attendance.assemble.control.jaxrs.v2.my;

import com.x.attendance.entity.v2.AttendanceV2Config;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.Identity;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.organization.Unit;
import com.x.base.core.project.tools.DefaultCharset;
import org.apache.commons.lang3.StringUtils;

public class ActionMyControl extends BaseAction {

    ActionResult<Wo> execute(EffectivePerson person) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (business.isManager(person)) { // 관리자는 바로 반환, 개인 정보 조회 불필요
                wo.setAdmin(true);
                result.setData(wo);
                return result;
            }
            AttendanceV2Config config = this.getConfig(emc);
            // 설정된 직무
            List<String> dutyNameList = config.getProperties().getStatisticUnitDutyNameList();
            if (dutyNameList != null && !dutyNameList.isEmpty()) {
                // 개인 정보를 조회해 [설정된] 직무가 있는지 판단
                String encodePerson = URLEncoder.encode(person.getDistinguishedName(),
                        DefaultCharset.name);
                WoPerson p = ThisApplication.context().applications()
                        .getQuery(x_organization_assemble_control.class, "person/" + encodePerson)
                        .getData(WoPerson.class);
                if (p != null && p.getWoIdentityList() != null && !p.getWoIdentityList()
                        .isEmpty()) {
                    List<WoUnitDuty> woUnitDutyList = new ArrayList<>();
                    for (WoIdentity identity : p.getWoIdentityList()) {
                        if (identity.getWoUnitDutyList() != null && !identity.getWoUnitDutyList()
                                .isEmpty()) {
                            woUnitDutyList.addAll(identity.getWoUnitDutyList().stream()
                                    .filter((d) -> dutyNameList.contains(d.getName()))
                                    .collect(Collectors.toList()));
                        }
                    }
                    if (!woUnitDutyList.isEmpty()) {
                        wo.setReadAdmin(true);
                        wo.setUnitDutyList(woUnitDutyList);
                    }
                }
            }
            // 근태 그룹 보조 관리자 조회
            wo.setAssistAdmin(business.getAttendanceV2ManagerFactory()
                    .isAssistAdmin(person.getDistinguishedName()));
            result.setData(wo);
            return result;
        }
    }

    private AttendanceV2Config getConfig(EntityManagerContainer emc) throws Exception {
        List<AttendanceV2Config> list = emc.listAll(AttendanceV2Config.class);
        AttendanceV2Config config;
        if (list != null && !list.isEmpty()) {
            config = list.get(0);
        } else {
            config = new AttendanceV2Config();
        }
        return config;
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 6260606941234950656L;
        @FieldDescribe("관리자 또는 근태 관리자 여부")
        private Boolean admin;
        @FieldDescribe("[근태 관리자] 직무 보유 여부")
        private Boolean readAdmin;
        @FieldDescribe("근태 관리자 직무 목록")
        private List<WoUnitDuty> unitDutyList;
        @FieldDescribe("근태 그룹 보조 관리자 여부")
        private Boolean assistAdmin;

        public Boolean getAdmin() {
            return admin;
        }

        public void setAdmin(Boolean admin) {
            this.admin = admin;
        }

        public Boolean getReadAdmin() {
            return readAdmin;
        }

        public void setReadAdmin(Boolean readAdmin) {
            this.readAdmin = readAdmin;
        }

        public Boolean getAssistAdmin() {
            return assistAdmin;
        }

        public void setAssistAdmin(Boolean assistAdmin) {
            this.assistAdmin = assistAdmin;
        }

        public List<WoUnitDuty> getUnitDutyList() {
            return unitDutyList;
        }

        public void setUnitDutyList(List<WoUnitDuty> unitDutyList) {
            this.unitDutyList = unitDutyList;
        }


    }

    public static class WoUnit extends Unit {

        private static final long serialVersionUID = 5521625368318946265L;


    }

    public static class WoUnitDuty extends GsonPropertyObject {

        private static final long serialVersionUID = 8887183560643765452L;


        @FieldDescribe("직무 이름")
        private String name;
        @FieldDescribe("고유 식별자")
        private String unique;
        @FieldDescribe("식별 이름")
        private String distinguishedName;

        @FieldDescribe("조직 객체")
        private WoUnit woUnit;


        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUnique() {
            return unique;
        }

        public void setUnique(String unique) {
            this.unique = unique;
        }

        public String getDistinguishedName() {
            return distinguishedName;
        }

        public void setDistinguishedName(String distinguishedName) {
            this.distinguishedName = distinguishedName;
        }


    }

    public static class WoIdentity extends Identity {

        private static final long serialVersionUID = -6779245805216952773L;


        @FieldDescribe("조직 객체")
        private WoUnit woUnit;

        @FieldDescribe("조직 직무 객체")
        private List<WoUnitDuty> woUnitDutyList;

        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }

        public List<WoUnitDuty> getWoUnitDutyList() {
            return woUnitDutyList;
        }

        public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
            this.woUnitDutyList = woUnitDutyList;
        }

    }

    public static class WoPerson extends Person {

        private static final long serialVersionUID = -6787590791993664117L;

        @FieldDescribe("신분 객체")
        private List<WoIdentity> woIdentityList;

        public List<WoIdentity> getWoIdentityList() {
            return woIdentityList;
        }

        public void setWoIdentityList(List<WoIdentity> woIdentityList) {
            this.woIdentityList = woIdentityList;
        }

    }
}
