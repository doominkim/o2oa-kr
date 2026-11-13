package com.x.message.core.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 채팅 대화 객체
 */
@Schema(name = "IMConversation", description = "메시지 채팅.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.IMConversation.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.IMConversation.table + JpaObject.IndexNameMiddle
                + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class IMConversation extends SliceJpaObject  {


    private static final String TABLE = PersistenceProperties.IMConversation.table;
    private static final long serialVersionUID = 1928069073101719523L;


    @Override
    public void onPersist() throws Exception {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("데이터베이스 기본 키, 자동 생성.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();


    //대화 유형 single 1:1 대화, group 그룹 채팅
    public static final String CONVERSATION_TYPE_SINGLE = "single";
    public static final String CONVERSATION_TYPE_GROUP = "group";

    public static final String type_FIELDNAME = "type";
    @FieldDescribe("대화 유형 1:1, 그룹.")
    @Column(length = length_255B, name = ColumnNamePrefix + type_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String type; //대화 유형 1:1, 그룹


    public static final String title_FIELDNAME = "title";
    @FieldDescribe("대화 제목 이름, 그룹명")
    @Column(length = length_255B, name = ColumnNamePrefix + title_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String title; //대화 제목 이름, 그룹명


    public static final String personList_FIELDNAME = "personList";
    @FieldDescribe("대화 대상, 그룹 구성원.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
            + personList_FIELDNAME, joinIndex = @Index(name = TABLE + personList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + personList_FIELDNAME)
    @ElementIndex(name = TABLE + personList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> personList;

    public static final String adminPerson_FIELDNAME = "adminPerson";
    @FieldDescribe("관리자. 그룹 대화에는 관리자 필요")
    @Column(length = length_255B, name = ColumnNamePrefix + adminPerson_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String adminPerson; //관리자

    public static final String note_FIELDNAME = "note";
    @FieldDescribe("그룹 공지")
    @Column(length = length_2K, name = ColumnNamePrefix + note_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String note;


    public static final String lastMessageTime_FIELDNAME = "lastMessageTime";
    @FieldDescribe("대화 마지막 메시지 시간")
    @Column(name = ColumnNamePrefix + lastMessageTime_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private Date lastMessageTime;




    public static final String businessId_FIELDNAME = "businessId";
    @FieldDescribe("업무 객체 Id. (프로세스의 jobId)")
    @Column(length = length_128B, name = ColumnNamePrefix + businessId_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String businessId;

    public static final String businessBody_FIELDNAME = "businessBody";
    @FieldDescribe("업무 객체 내용.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = length_10M, name = ColumnNamePrefix + businessBody_FIELDNAME)
    private String businessBody;

    //업무 유형 process 프로세스
    public static final String CONVERSATION_BUSINESS_TYPE_PROCESS = "process";
    public static final String CONVERSATION_BUSINESS_TYPE_CMS = "cms";

    public static final String businessType_FIELDNAME = "businessType";
    @FieldDescribe("업무 유형. process")
    @Column(length = length_16B, name = ColumnNamePrefix + businessType_FIELDNAME)
    @CheckPersist(allowEmpty = true)
    private String businessType;


    public static final String icon_FIELDNAME = "groupIcon";
    @FieldDescribe("그룹 채팅 프로필 이미지.")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(length = JpaObject.length_128K, name = ColumnNamePrefix + icon_FIELDNAME)
    private String groupIcon;

    public String getGroupIcon() {
        return groupIcon;
    }

    public void setGroupIcon(String groupIcon) {
        this.groupIcon = groupIcon;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessBody() {
        return businessBody;
    }

    public void setBusinessBody(String businessBody) {
        this.businessBody = businessBody;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPersonList() {
        return personList;
    }

    public void setPersonList(List<String> personList) {
        this.personList = personList;
    }

    public String getAdminPerson() {
        return adminPerson;
    }

    public void setAdminPerson(String adminPerson) {
        this.adminPerson = adminPerson;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
