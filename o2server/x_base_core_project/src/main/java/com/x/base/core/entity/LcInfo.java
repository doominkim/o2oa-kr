package com.x.base.core.entity;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;
import java.util.List;

/**
 * @author chengjian
 * @date 2025/02/06 17:25
 **/
public class LcInfo extends GsonPropertyObject {

    @FieldDescribe("고객 번호.")
    private String unique;
    @FieldDescribe("고객 이름.")
    private String name;
    @FieldDescribe("최상위 조직 이름.")
    private String unitName;
    @FieldDescribe("이메일.")
    private String email;
    @FieldDescribe("버전 번호.")
    private String version;
    @FieldDescribe("버전 유형.")
    private String versionType;
    @FieldDescribe("라이선스 모드.")
    private String model;
    @FieldDescribe("라이선스 일시.")
    private Date startTime;
    @FieldDescribe("라이선스 만료 일시.")
    private Date expireTime;

    private List<String> supportDbList;

    public String getUnique() {
        return unique;
    }

    public void setUnique(String unique) {
        this.unique = unique;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersionType() {
        return versionType;
    }

    public void setVersionType(String versionType) {
        this.versionType = versionType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public List<String> getSupportDbList() {
        return supportDbList;
    }

    public void setSupportDbList(List<String> supportDbList) {
        this.supportDbList = supportDbList;
    }
}
