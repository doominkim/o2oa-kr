package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

import java.util.List;

/**
 * @author sword
 * @date 2023/04/12 14:12
 **/
public class WoUnitDuty extends GsonPropertyObject {

    @FieldDescribe("조직 객체")
    private WoUnit woUnit;

    @FieldDescribe("개인 속성 이름")
    private String name;

    @FieldDescribe("조직")
    private String unit;

    @FieldDescribe("신분 정보")
    private List<String> identityList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public List<String> getIdentityList() {
        return identityList;
    }

    public void setIdentityList(List<String> identityList) {
        this.identityList = identityList;
    }

    public WoUnit getWoUnit() {
        return woUnit;
    }

    public void setWoUnit(WoUnit woUnit) {
        this.woUnit = woUnit;
    }


}
