package com.x.cms.assemble.control.wrapin;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * @author chengjian
 * @date 2025/07/31 15:37
 **/
public class WiAttachment  extends GsonPropertyObject {

    private static final long serialVersionUID = 6570042412000311813L;

    @FieldDescribe("첨부파일 식별자.")
    private String id;

    @FieldDescribe("첨부파일 이름.")
    private String name;

    @FieldDescribe("첨부파일 분류.")
    private String site;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
