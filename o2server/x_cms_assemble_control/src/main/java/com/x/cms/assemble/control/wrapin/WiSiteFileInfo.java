package com.x.cms.assemble.control.wrapin;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.annotation.FieldTypeDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chengjian
 * @date 2025/07/31 15:38
 **/
public class WiSiteFileInfo extends GsonPropertyObject {

    public static final String TYPE_CMS = "cms";
    public static final String TYPE_PROCESS_PLATFORM = "processPlatform";

    @FieldDescribe("첨부파일 상자 분류.")
    private String site;

    @FieldDescribe("첨부파일 목록.")
    @FieldTypeDescribe(fieldType = "class", fieldTypeName = "WiAttachment", fieldValue = "[{'id':'첨부파일 id','name':'첨부파일 이름'}]")
    private List<WiAttachment> fileInfoList = new ArrayList<>();

    @FieldDescribe("첨부파일 출처(cms 또는 processPlatform).")
    private String type;

    public List<WiAttachment> getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List<WiAttachment> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
