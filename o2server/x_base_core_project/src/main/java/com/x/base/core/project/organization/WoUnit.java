package com.x.base.core.project.organization;

import com.x.base.core.project.annotation.FieldDescribe;

/**
 * @author sword
 * @date 2023/04/12 14:10
 **/
public class WoUnit extends Unit {

    @FieldDescribe("상위 조직 객체.")
    private WoUnit woSupDirectUnit;

    public WoUnit getWoSupDirectUnit() {
        return woSupDirectUnit;
    }

    public void setWoSupDirectUnit(WoUnit woSupDirectUnit) {
        this.woSupDirectUnit = woSupDirectUnit;
    }

}
