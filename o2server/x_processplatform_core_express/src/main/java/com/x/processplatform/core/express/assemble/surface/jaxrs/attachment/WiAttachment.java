package com.x.processplatform.core.express.assemble.surface.jaxrs.attachment;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.content.Attachment;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chengjian
 * @date 2025/08/13 17:55
 **/
public class WiAttachment extends GsonPropertyObject {

    private static final long serialVersionUID = 6570042412000311813L;

    public static final String COPY_FROM_CMS = "cms";
    public static final String COPY_FROM_DRAFT = "draft";
    public static final String COPY_FROM_PAN = "x_pan_assemble_control";

    public static WrapCopier<Attachment, WiAttachment> copier = WrapCopierFactory.wo(Attachment.class,
            WiAttachment.class, null,JpaObject.FieldsInvisible);

    @FieldDescribe("첨부파일 식별자.")
    private String id;

    @FieldDescribe("첨부파일 이름.")
    private String name;

    @FieldDescribe("첨부파일 분류.")
    private String site;

    @FieldDescribe("소프트 복사 여부, 기본값 false, true이면 실제 저장된 첨부파일을 복사하지 않고 경로만 복사하여 첨부파일을 공유함, 프로세스 첨부파일만 지원.")
    private Boolean isSoftCopy;

    @FieldDescribe("첨부파일 출처(cms|콘텐츠 관리 첨부파일, processPlatform|프로세스 플랫폼 첨부파일, pan|기업 네트워크 드라이브 첨부파일, 기본값 processPlatform).")
    private String copyFrom;

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

    public Boolean getSoftCopy() {
        return isSoftCopy;
    }

    public void setSoftCopy(Boolean softCopy) {
        isSoftCopy = softCopy;
    }

    public String getCopyFrom() {
        return copyFrom;
    }

    public void setCopyFrom(String copyFrom) {
        this.copyFrom = copyFrom;
    }
}
