package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import org.apache.openjpa.persistence.jdbc.Strategy;

/**
 * 출퇴근 기록 Created by fancyLou on 2023/2/20. Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2CheckInRecord", description = "출퇴근 기록.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2CheckInRecord.table, indexes = {
        @javax.persistence.Index(name = PersistenceProperties.AttendanceV2CheckInRecord.table
                                        + "_UDS_IDX", columnList = JpaObject.ColumnNamePrefix
                                                                   + AttendanceV2CheckInRecord.userId_FIELDNAME
                                                                   + ","
                                                                   + JpaObject.ColumnNamePrefix
                                                                   + AttendanceV2CheckInRecord.recordDateString_FIELDNAME
                                                                   + ","
                                                                   + JpaObject.ColumnNamePrefix
                                                                   + AttendanceV2CheckInRecord.recordDate_FIELDNAME),
        @javax.persistence.Index(name = PersistenceProperties.AttendanceV2CheckInRecord.table
                                        + "_URD_IDX", columnList = JpaObject.ColumnNamePrefix
                                                                   + AttendanceV2CheckInRecord.userId_FIELDNAME
                                                                   + ","
                                                                   + JpaObject.ColumnNamePrefix
                                                                   + AttendanceV2CheckInRecord.recordDate_FIELDNAME),
        @javax.persistence.Index(name = PersistenceProperties.AttendanceV2CheckInRecord.table
                                        + "_RD_IDX", columnList = JpaObject.ColumnNamePrefix
                                                                  + AttendanceV2CheckInRecord.recordDate_FIELDNAME)

}, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2CheckInRecord.table
                                 + JpaObject.IndexNameMiddle
                                 + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {
                JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}),
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2CheckInRecord.table
                                 + JpaObject.IndexNameMiddle
                                 + "person_date_type_time_UNIQUE", columnNames = {
                JpaObject.ColumnNamePrefix + AttendanceV2CheckInRecord.userId_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2CheckInRecord.recordDateString_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2CheckInRecord.checkInType_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2CheckInRecord.preDutyTime_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2CheckInRecord extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2CheckInRecord.table;
    private static final long serialVersionUID = -2959741414964993090L;


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


    public static final String userId_FIELDNAME = "userId";
    @FieldDescribe("체크한 사용자 식별자 DN")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("출퇴근 기록 날짜 문자열: YYYY-MM-dd")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String recordDateString;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("출퇴근 기록 시간: YYYY-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Date recordDate;


    // 근무 정보의 체크 시간, 체크가 필요한 각 항목에 대응
    public static final String preDutyTime_FIELDNAME = "preDutyTime";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + preDutyTime_FIELDNAME)
    @FieldDescribe("체크 시간")
    private String preDutyTime;
    public static final String preDutyTimeBeforeLimit_FIELDNAME = "preDutyTimeBeforeLimit";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix
                                                  + preDutyTimeBeforeLimit_FIELDNAME)
    @FieldDescribe("체크 시간 이전 체크 제한 시간")
    private String preDutyTimeBeforeLimit;
    public static final String preDutyTimeAfterLimit_FIELDNAME = "preDutyTimeAfterLimit";
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix
                                                  + preDutyTimeAfterLimit_FIELDNAME)
    @FieldDescribe("체크 시간 이후 체크 제한 시간")
    private String preDutyTimeAfterLimit;


    // 출퇴근 체크 데이터 출처
    public static final String SOURCE_TYPE_USER_CHECK = "USER_CHECK"; // 사용자 체크
    public static final String SOURCE_TYPE_AUTO_CHECK = "AUTO_CHECK"; // 시스템 자동 체크
    public static final String SOURCE_TYPE_FAST_CHECK = "FAST_CHECK"; // 빠른 체크
    public static final String SOURCE_TYPE_SYSTEM_IMPORT = "SYSTEM_IMPORT"; // 시스템 가져오기
    public static final String sourceType_FIELDNAME = "sourceType";
    @FieldDescribe("체크 데이터 출처: USER_CHECK|AUTO_CHECK|FAST_CHECK|SYSTEM_IMPORT")
    @Column(length = length_128B, name = ColumnNamePrefix + sourceType_FIELDNAME)
    private String sourceType;

    //시간 결과
    //Normal: 정상;
    //Early: 조퇴;
    //Late: 지각;
    //SeriousLate: 심각한 지각;
    //Absenteeism: 무단결근 지각;
    //NotSigned: 미체크
    //PreCheckIn: 사전 저장 데이터; 체크 준비용
    public static final String CHECKIN_RESULT_NORMAL = "Normal";
    public static final String CHECKIN_RESULT_Early = "Early";
    public static final String CHECKIN_RESULT_Late = "Late";
    public static final String CHECKIN_RESULT_SeriousLate = "SeriousLate";
    public static final String CHECKIN_RESULT_Absenteeism = "Absenteeism";
    public static final String CHECKIN_RESULT_NotSigned = "NotSigned";
    public static final String CHECKIN_RESULT_PreCheckIn = "PreCheckIn";

    public static final String checkInResult_FIELDNAME = "checkInResult";
    @FieldDescribe("체크 결과: Normal|Early|Late|SeriousLate|NotSigned")
    @Column(length = length_32B, name = ColumnNamePrefix + checkInResult_FIELDNAME)
    private String checkInResult;

    //근태 유형 OnDuty: 출근 OffDuty: 퇴근
    public static final String OnDuty = "OnDuty";
    public static final String OffDuty = "OffDuty";

    public static final String checkInType_FIELDNAME = "checkInType";
    @FieldDescribe("근태 유형: OnDuty|OffDuty")
    @Column(length = length_16B, name = ColumnNamePrefix + checkInType_FIELDNAME)
    private String checkInType;

    public static final String sourceDevice_FIELDNAME = "sourceDevice";
    @FieldDescribe("출처 기기")
    @Column(length = JpaObject.length_128B, name = ColumnNamePrefix + sourceDevice_FIELDNAME)
    private String sourceDevice;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("기타 설명 비고")
    @Column(name = ColumnNamePrefix + description_FIELDNAME)
    private String description;

    public static final String recordAddress_FIELDNAME = "recordAddress";
    @FieldDescribe("출퇴근 체크 장소 설명")
    @Column(name = ColumnNamePrefix + recordAddress_FIELDNAME)
    private String recordAddress;

    public static final String longitude_FIELDNAME = "longitude";
    @FieldDescribe("경도")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + longitude_FIELDNAME)
    private String longitude;

    public static final String latitude_FIELDNAME = "latitude";
    @FieldDescribe("위도")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + latitude_FIELDNAME)
    private String latitude;


    public static final String signDescription_FIELDNAME = "signDescription";
    @FieldDescribe("외근 체크 설명")
    @Column(name = ColumnNamePrefix + signDescription_FIELDNAME)
    private String signDescription;

    public static final String fieldWork_FIELDNAME = "fieldWork";
    @FieldDescribe("외근 체크 여부.")
    @Column(name = ColumnNamePrefix + fieldWork_FIELDNAME)
    private Boolean fieldWork;

    public static final String fieldWorkPhotoFileIdList_FIELDNAME = "fieldWorkPhotoFileIdList";
    @FieldDescribe("외근 체크 사진 첨부파일 ID 목록.")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + fieldWorkPhotoFileIdList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + fieldWorkPhotoFileIdList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix
                                                         + fieldWorkPhotoFileIdList_FIELDNAME)
    @ElementIndex(name = TABLE + fieldWorkPhotoFileIdList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> fieldWorkPhotoFileIdList;


    public static final String groupId_FIELDNAME = "groupId";
    @FieldDescribe("대응 근태 그룹 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
    private String groupId;

    public static final String groupName_FIELDNAME = "groupName";
    @FieldDescribe("근태 그룹 이름")
    @Column(name = ColumnNamePrefix
                   + groupName_FIELDNAME)
    private String groupName;


    public static final String groupCheckType_FIELDNAME = "groupCheckType";
    @FieldDescribe("근태 그룹의 근태 유형, 자율 근무 시에는 체크 처리 방식이 다르며 체크 시간이 필요 없음")
    @Column(length = length_16B, name = ColumnNamePrefix
                                        + groupCheckType_FIELDNAME)
    private String groupCheckType;

    public static final String shiftId_FIELDNAME = "shiftId";
    @FieldDescribe("대응 근무 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
    private String shiftId;

    public static final String shiftName_FIELDNAME = "shiftName";
    @FieldDescribe("근무조 이름")
    @Column(name = ColumnNamePrefix
                   + shiftName_FIELDNAME)
    private String shiftName;

    public static final String workPlaceIdd_FIELDNAME = "workPlaceId";
    @FieldDescribe("대응 근무지 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + workPlaceIdd_FIELDNAME)
    private String workPlaceId;

    public static final String placeName_FIELDNAME = "placeName";
    @FieldDescribe("장소 이름")
    @Column(name = ColumnNamePrefix
                   + placeName_FIELDNAME)
    private String placeName;


    public static final String offDutyNextDay_FIELDNAME = "offDutyNextDay";
    @FieldDescribe("익일 여부")
    @Column(name = ColumnNamePrefix + offDutyNextDay_FIELDNAME)
    private Boolean offDutyNextDay = false; // 퇴근 체크의 익일 여부, 퇴근 체크는 날짜를 넘길 수 있음


    public static final String appealId_FIELDNAME = "appealId";
    @FieldDescribe("근태 이의 신청 기록 ID(신청 승인 후 기록).")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + appealId_FIELDNAME)
    private String appealId;


    public static final String leaveDataId_FIELDNAME = "leaveDataId";
    @FieldDescribe("외출 데이터 id, 외출 데이터와 연관, 값이 있으면 외출 시간대 내에 있음.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + leaveDataId_FIELDNAME)
    private String leaveDataId;

    public static final String requestDataId_FIELDNAME = "requestDataId";
    @FieldDescribe("휴가 데이터 id, 휴가 데이터와 연관, 값이 있으면 휴가 시간대 내에 있음.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + requestDataId_FIELDNAME)
    private String requestDataId;


    public static final String fieldWorkJobId_FIELDNAME = "fieldWorkJobId";
    @FieldDescribe("외근 체크 연관 업무 문서 id.")
    @Column(length = JpaObject.length_id, name = ColumnNamePrefix + fieldWorkJobId_FIELDNAME)
    private String fieldWorkJobId;

    public static final String fieldWorkJobStatus_FIELDNAME = "fieldWorkJobStatus";
    @FieldDescribe("외근 체크 연관 업무 문서 상태, 1:결재 중, 2:심사 완료.)")
    @Column( name = ColumnNamePrefix + fieldWorkJobStatus_FIELDNAME)
    private Integer fieldWorkJobStatus;

    public String getFieldWorkJobId() {
        return fieldWorkJobId;
    }

    public void setFieldWorkJobId(String fieldWorkJobId) {
        this.fieldWorkJobId = fieldWorkJobId;
    }

    public Integer getFieldWorkJobStatus() {
        return fieldWorkJobStatus;
    }

    public void setFieldWorkJobStatus(Integer fieldWorkJobStatus) {
        this.fieldWorkJobStatus = fieldWorkJobStatus;
    }

    public void startFieldWorkJob(String jobId) {
        this.setFieldWorkJobId(jobId);
        this.setFieldWorkJobStatus(1);
    }
    public void finishFieldWorkJob() {
        this.setFieldWorkJobStatus(2);
    }
    public void cancelFieldWorkJob() {
        this.setFieldWorkJobId(null);
        this.setFieldWorkJobStatus(null);
    }

    /**
     * 현재 출퇴근 기록의 비정상 데이터 여부 판단
     *
     * @param fieldWorkMarkError 외근을 이상 데이터로 표시할지 여부
     * @return true 비정상 데이터
     */
    public boolean checkResultException(boolean fieldWorkMarkError) {
        if (fieldWorkMarkError) {
            return (getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                    && BooleanUtils.isTrue(getFieldWork())
                    || (
                            !getCheckInResult().equals(
                                    AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                            && !getCheckInResult().equals(
                                    AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL)
                            && !hasLeaveOrRequest()));
        } else {
            return !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_PreCheckIn)
                   && !hasLeaveOrRequest()
                   && !getCheckInResult().equals(AttendanceV2CheckInRecord.CHECKIN_RESULT_NORMAL);
        }
    }


    /**
     * 휴가 또는 외출 기록 존재 여부
     */
    public boolean hasLeaveOrRequest() {
        return StringUtils.isNotEmpty(getLeaveDataId()) || StringUtils.isNotEmpty(
                getRequestDataId());
    }

    public Boolean getOffDutyNextDay() {
        return offDutyNextDay;
    }

    public void setOffDutyNextDay(Boolean offDutyNextDay) {
        this.offDutyNextDay = offDutyNextDay;
    }

    public String getRequestDataId() {
        return requestDataId;
    }

    public void setRequestDataId(String requestDataId) {
        this.requestDataId = requestDataId;
    }

    public String getLeaveDataId() {
        return leaveDataId;
    }

    public void setLeaveDataId(String leaveDataId) {
        this.leaveDataId = leaveDataId;
    }

    public String getAppealId() {
        return appealId;
    }

    public void setAppealId(String appealId) {
        this.appealId = appealId;
    }

    public String getPreDutyTime() {
        return preDutyTime;
    }

    public void setPreDutyTime(String preDutyTime) {
        this.preDutyTime = preDutyTime;
    }

    public String getPreDutyTimeBeforeLimit() {
        return preDutyTimeBeforeLimit;
    }

    public void setPreDutyTimeBeforeLimit(String preDutyTimeBeforeLimit) {
        this.preDutyTimeBeforeLimit = preDutyTimeBeforeLimit;
    }

    public String getPreDutyTimeAfterLimit() {
        return preDutyTimeAfterLimit;
    }

    public void setPreDutyTimeAfterLimit(String preDutyTimeAfterLimit) {
        this.preDutyTimeAfterLimit = preDutyTimeAfterLimit;
    }

    public Boolean getFieldWork() {
        return fieldWork;
    }

    public void setFieldWork(Boolean fieldWork) {
        this.fieldWork = fieldWork;
    }

    public List<String> getFieldWorkPhotoFileIdList() {
        return fieldWorkPhotoFileIdList;
    }

    public void setFieldWorkPhotoFileIdList(List<String> fieldWorkPhotoFileIdList) {
        this.fieldWorkPhotoFileIdList = fieldWorkPhotoFileIdList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCheckInResult() {
        return checkInResult;
    }

    public void setCheckInResult(String checkInResult) {
        this.checkInResult = checkInResult;
    }

    public String getCheckInType() {
        return checkInType;
    }

    public void setCheckInType(String checkInType) {
        this.checkInType = checkInType;
    }

    public String getSourceDevice() {
        return sourceDevice;
    }

    public void setSourceDevice(String sourceDevice) {
        this.sourceDevice = sourceDevice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecordAddress() {
        return recordAddress;
    }

    public void setRecordAddress(String recordAddress) {
        this.recordAddress = recordAddress;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSignDescription() {
        return signDescription;
    }

    public void setSignDescription(String signDescription) {
        this.signDescription = signDescription;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getWorkPlaceId() {
        return workPlaceId;
    }

    public void setWorkPlaceId(String workPlaceId) {
        this.workPlaceId = workPlaceId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getGroupCheckType() {
        return groupCheckType;
    }

    public void setGroupCheckType(String groupCheckType) {
        this.groupCheckType = groupCheckType;
    }
}
