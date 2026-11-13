package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Strategy;

import javax.persistence.*;
import java.util.List;

/**
 * 근태 그룹
 */
@Schema(name = "AttendanceV2Group", description = "근태 그룹.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Group.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2Group.table
                                 + JpaObject.IndexNameMiddle
                                 + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {
                JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Group extends SliceJpaObject {


    private static final String TABLE = PersistenceProperties.AttendanceV2Group.table;
    private static final long serialVersionUID = -1148943360608366700L;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @FieldDescribe("데이터베이스 기본 키, 자동 생성.")
    @Id
    @Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
    private String id = createId();

    public void onPersist() throws Exception {
    }
    /*
     * =============================================================================
     * ===== 위는 JpaObject 기본 필드
     * =============================================================================
     * =====
     */

    /*
     * =============================================================================
     * ===== 아래는 업무별 데이터 테이블 필드 요구사항
     * =============================================================================
     * =====
     */
    public static final String groupName_FIELDNAME = "groupName";
    @FieldDescribe("근태 그룹 이름")
    @Column(name = ColumnNamePrefix + groupName_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String groupName;

    // 근태 유형 고정 근무제 자율 체크 교대 근무제, 현재는 고정 근무제만 지원
    public static final String CHECKTYPE_Fixed = "1"; // 고정 근무제
    public static final String CHECKTYPE_Free = "2"; // 자율 체크
    public static final String CHECKTYPE_Arrangement = "3"; // 교대 근무제
    public static final String checkType_FIELDNAME = "checkType";
    @FieldDescribe("체크 유형 ")
    @Column(name = ColumnNamePrefix + checkType_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String checkType;


    public static final String shiftId_FIELDNAME = "shiftId";
    @FieldDescribe("공휴일 대체 근무 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
    private String shiftId;


    public static final String operator_FIELDNAME = "operator";
    @FieldDescribe("최종 처리자")
    @Column(name = ColumnNamePrefix + operator_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String operator;

    public static final int status_auto = 2; //데이터 상태 자동 저장
    public static final int status_manual = 1; //데이터 상태 수동 저장
    public static final String status_FIELDNAME = "status";
    @FieldDescribe("근태 그룹 데이터 상태.")
    @Column(name = ColumnNamePrefix + status_FIELDNAME)
    private Integer status;


    public static final String assistAdminList_FIELDNAME = "assistAdminList";
    @FieldDescribe("보조 관리자.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + assistAdminList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + assistAdminList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + assistAdminList_FIELDNAME)
    @ElementIndex(name = TABLE + assistAdminList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> assistAdminList;

    public static final String participateList_FIELDNAME = "participateList";
    @FieldDescribe("근태 체크 대상 사용자·조직.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + participateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + participateList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + participateList_FIELDNAME)
    @ElementIndex(name = TABLE + participateList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> participateList; // 근태 체크 인원·조직

    public static final String unParticipateList_FIELDNAME = "unParticipateList";
    @FieldDescribe("근태 체크가 필요 없는 사용자.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + unParticipateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + unParticipateList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + unParticipateList_FIELDNAME)
    @ElementIndex(name = TABLE + unParticipateList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> unParticipateList; // 근태 체크가 필요 없는 사용자, 사용자만

    public static final String trueParticipantList_FIELDNAME = "trueParticipantList";
    @FieldDescribe("실제 근태 체크 대상 사용자 목록, participateList와 unParticipateList를 조합해 계산한 결과.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + trueParticipantList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + trueParticipantList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + trueParticipantList_FIELDNAME)
    @ElementIndex(name = TABLE + trueParticipantList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> trueParticipantList; // 반드시 근태 체크해야 하는 사용자, 사용자만

    public static final String workDateList_FIELDNAME = "workDateList";
    @FieldDescribe("근태 근무일 설정. 체크 유형이 자율 체크일 때 설정 필요, 예: 1,2,3,4,5. 0-6은 일요일~토요일.")
    @Column(length = length_64B, name = ColumnNamePrefix + workDateList_FIELDNAME)
    private String workDateList; // 체크 근무일 설정, 예: 1,2,3,4,5. 0-6은 일요일~토요일

    public static final String workDateProperties_FIELDNAME = "workDateProperties";
    @FieldDescribe("근태 근무일 설정. 체크 유형이 고정 근무제일 때 설정 필요, 체크일마다 근무 설정 가능")
    @Persistent
    @Strategy(JsonPropertiesValueHandler)
    @Column(length = JpaObject.length_10M, name = ColumnNamePrefix + workDateProperties_FIELDNAME)
    private AttendanceV2GroupWorkDayProperties workDateProperties;

    public static final String allowFieldWork_FIELDNAME = "allowFieldWork";
    @FieldDescribe("외근 체크 허용 여부.")
    @Column(name = ColumnNamePrefix + allowFieldWork_FIELDNAME)
    private Boolean allowFieldWork = true;

    public static final String requiredFieldWorkRemarks_FIELDNAME = "requiredFieldWorkRemarks";
    @FieldDescribe("외근 체크 비고 필수 여부.")
    @Column(name = ColumnNamePrefix + requiredFieldWorkRemarks_FIELDNAME)
    private Boolean requiredFieldWorkRemarks = true;

    public static final String requiredFieldWorkPhoto_FIELDNAME = "requiredFieldWorkPhoto";
    @FieldDescribe("외근 체크 사진 필수 여부.")
    @Column(name = ColumnNamePrefix + requiredFieldWorkPhoto_FIELDNAME)
    private Boolean requiredFieldWorkPhoto = false;

    public static final String fieldWorkMarkError_FIELDNAME = "fieldWorkMarkError";
    @FieldDescribe("외근 체크를 비정상 데이터로 처리할지 여부.")
    @Column(name = ColumnNamePrefix + fieldWorkMarkError_FIELDNAME)
    private Boolean fieldWorkMarkError = true;

    public static final String workPlaceIdList_FIELDNAME = "workPlaceIdList";
    @FieldDescribe("근무지 목록.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + workPlaceIdList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + workPlaceIdList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix
                                                         + workPlaceIdList_FIELDNAME)
    @ElementIndex(name = TABLE + workPlaceIdList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> workPlaceIdList;


    public static final String requiredCheckInDateList_FIELDNAME = "requiredCheckInDateList";
    @FieldDescribe("반드시 체크해야 하는 날짜, 날짜 | 근무 id | 반복 여부 포함, 예: 2023-01-01|shiftId|week.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + requiredCheckInDateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + requiredCheckInDateList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + requiredCheckInDateList_FIELDNAME)
    @ElementIndex(name = TABLE + requiredCheckInDateList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> requiredCheckInDateList;


    public static final String noNeedCheckInDateList_FIELDNAME = "noNeedCheckInDateList";
    @FieldDescribe("체크가 필요 없는 날짜, 날짜 | 반복 여부 포함, 예: 2023-01-01|week .")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + noNeedCheckInDateList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + noNeedCheckInDateList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(name = ColumnNamePrefix + noNeedCheckInDateList_FIELDNAME)
    @ElementIndex(name = TABLE + noNeedCheckInDateList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> noNeedCheckInDateList;


    public AttendanceV2GroupWorkDayProperties getWorkDateProperties() {
        return workDateProperties;
    }

    public void setWorkDateProperties(AttendanceV2GroupWorkDayProperties workDateProperties) {
        this.workDateProperties = workDateProperties;
    }


    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getParticipateList() {
        return participateList;
    }

    public void setParticipateList(List<String> participateList) {
        this.participateList = participateList;
    }

    public List<String> getUnParticipateList() {
        return unParticipateList;
    }

    public void setUnParticipateList(List<String> unParticipateList) {
        this.unParticipateList = unParticipateList;
    }

    public String getWorkDateList() {
        return workDateList;
    }

    public void setWorkDateList(String workDateList) {
        this.workDateList = workDateList;
    }

    public Boolean getAllowFieldWork() {
        return allowFieldWork;
    }

    public void setAllowFieldWork(Boolean allowFieldWork) {
        this.allowFieldWork = allowFieldWork;
    }

    public Boolean getRequiredFieldWorkRemarks() {
        return requiredFieldWorkRemarks;
    }

    public void setRequiredFieldWorkRemarks(Boolean requiredFieldWorkRemarks) {
        this.requiredFieldWorkRemarks = requiredFieldWorkRemarks;
    }

    public Boolean getRequiredFieldWorkPhoto() {
        return requiredFieldWorkPhoto;
    }

    public void setRequiredFieldWorkPhoto(Boolean requiredFieldWorkPhoto) {
        this.requiredFieldWorkPhoto = requiredFieldWorkPhoto;
    }

    public List<String> getWorkPlaceIdList() {
        return workPlaceIdList;
    }

    public void setWorkPlaceIdList(List<String> workPlaceIdList) {
        this.workPlaceIdList = workPlaceIdList;
    }

    public List<String> getRequiredCheckInDateList() {
        return requiredCheckInDateList;
    }

    public void setRequiredCheckInDateList(List<String> requiredCheckInDateList) {
        this.requiredCheckInDateList = requiredCheckInDateList;
    }

    public List<String> getNoNeedCheckInDateList() {
        return noNeedCheckInDateList;
    }

    public void setNoNeedCheckInDateList(List<String> noNeedCheckInDateList) {
        this.noNeedCheckInDateList = noNeedCheckInDateList;
    }

    public List<String> getTrueParticipantList() {
        return trueParticipantList;
    }

    public void setTrueParticipantList(List<String> trueParticipantList) {
        this.trueParticipantList = trueParticipantList;
    }

    public Boolean getFieldWorkMarkError() {
        return fieldWorkMarkError;
    }

    public void setFieldWorkMarkError(Boolean fieldWorkMarkError) {
        this.fieldWorkMarkError = fieldWorkMarkError;
    }

    public List<String> getAssistAdminList() {
        return assistAdminList;
    }

    public void setAssistAdminList(List<String> assistAdminList) {
        this.assistAdminList = assistAdminList;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}
