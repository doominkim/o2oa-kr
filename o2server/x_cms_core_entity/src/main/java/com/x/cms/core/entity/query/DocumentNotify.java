package com.x.cms.core.entity.query;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

/**
 * 메시지 알림 대상
 *
 * @author sword
 * @date 2022/03/10 16:45
 **/
public class DocumentNotify extends GsonPropertyObject {

    private String documentId;

    @FieldDescribe("메시지 알림 대상: 사용자, 조직 또는 그룹")
    private List<String> notifyPersonList;

    @FieldDescribe("문서 열람 범위에 따라 발송 여부: true(notifyPersonList 값 무시)|false(기본)")
    private Boolean notifyByDocumentReadPerson;

    @FieldDescribe("문서 작성자 알림 여부: true(기본)|false")
    private Boolean notifyCreatePerson;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public List<String> getNotifyPersonList() {
        return notifyPersonList;
    }

    public void setNotifyPersonList(List<String> notifyPersonList) {
        this.notifyPersonList = notifyPersonList;
    }

    public Boolean getNotifyByDocumentReadPerson() {
        return notifyByDocumentReadPerson;
    }

    public void setNotifyByDocumentReadPerson(Boolean notifyByDocumentReadPerson) {
        this.notifyByDocumentReadPerson = notifyByDocumentReadPerson;
    }

    public Boolean getNotifyCreatePerson() {
        return notifyCreatePerson;
    }

    public void setNotifyCreatePerson(Boolean notifyCreatePerson) {
        this.notifyCreatePerson = notifyCreatePerson;
    }
}
