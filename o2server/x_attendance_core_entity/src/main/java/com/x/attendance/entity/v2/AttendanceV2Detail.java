package com.x.attendance.entity.v2;

import com.x.attendance.entity.PersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;

import javax.persistence.*;
import java.util.List;

/**
 * Created by fancyLou on 2023/2/21. Copyright © 2023 O2. All rights reserved.
 */

@Schema(name = "AttendanceV2Detail", description = "근태 일별 상세 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceV2Detail.table, uniqueConstraints = {
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2Detail.table
                                 + JpaObject.IndexNameMiddle
                                 + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {
                JpaObject.IDCOLUMN,
                JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}),
        @UniqueConstraint(name = PersistenceProperties.AttendanceV2Detail.table
                                 + JpaObject.IndexNameMiddle
                                 + "person_date_UNIQUE", columnNames = {
                JpaObject.ColumnNamePrefix + AttendanceV2Detail.userId_FIELDNAME,
                JpaObject.ColumnNamePrefix + AttendanceV2Detail.recordDateString_FIELDNAME})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceV2Detail extends SliceJpaObject {

    private static final String TABLE = PersistenceProperties.AttendanceV2Detail.table;


    private static final long serialVersionUID = -4038741539513754723L;


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
    @FieldDescribe("사용자 식별자")
    @Column(length = length_96B, name = ColumnNamePrefix + userId_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String userId;

    public static final String yearString_FIELDNAME = "yearString";
    @FieldDescribe("기록 연도")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + yearString_FIELDNAME)
    private String yearString;

    public static final String monthString_FIELDNAME = "monthString";
    @FieldDescribe("기록 월")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + monthString_FIELDNAME)
    private String monthString;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("기록 일자")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)
    private String recordDateString;

    public static final String recordDay_FIELDNAME = "recordDay";
    @FieldDescribe("기록 날짜의 요일, 0-6")
    @Column(length = JpaObject.length_4B, name = ColumnNamePrefix + recordDay_FIELDNAME)
    private String recordDay;


    public static final String isWorkday_FIELDNAME = "workDay";
    @FieldDescribe("근무일 여부")
    @Column(name = ColumnNamePrefix + isWorkday_FIELDNAME)
    private Boolean workDay = false;


    //////// 통계 관련
    public static final String lateTimeDuration_FIELDNAME = "lateTimeDuration";
    @FieldDescribe("지각 시간(분)")
    @Column(name = ColumnNamePrefix + lateTimeDuration_FIELDNAME)
    private Long lateTimeDuration = 0L;

    public static final String leaveEarlierTimeDuration_FIELDNAME = "leaveEarlierTimeDuration";
    @FieldDescribe("조퇴 시간(분)")
    @Column(name = ColumnNamePrefix + leaveEarlierTimeDuration_FIELDNAME)
    private Long leaveEarlierTimeDuration = 0L;

//    public static final String workOvertimeTimeDuration_FIELDNAME = "workOvertimeTimeDuration";
//    @FieldDescribe("초과근무 시간(분)")
//    @Column(name = ColumnNamePrefix + workOvertimeTimeDuration_FIELDNAME)
//    private Long workOvertimeTimeDuration = 0L;

    public static final String workTimeDuration_FIELDNAME = "workTimeDuration";
    @FieldDescribe("근무 시간(분)")
    @Column(name = ColumnNamePrefix + workTimeDuration_FIELDNAME)
    private Long workTimeDuration = 0L;

    public static final String attendance_FIELDNAME = "attendance";
    @FieldDescribe("출근 일수")
    @Column(name = ColumnNamePrefix + attendance_FIELDNAME)
    private Integer attendance = 0;

    public static final String rest_FIELDNAME = "rest";
    @FieldDescribe("휴무 일수")
    @Column(name = ColumnNamePrefix + rest_FIELDNAME)
    private Integer rest = 0;

    public static final String absenteeismDays_FIELDNAME = "absenteeismDays";
    @FieldDescribe("무단결근 일수")
    @Column(name = ColumnNamePrefix + absenteeismDays_FIELDNAME)
    private Integer absenteeismDays = 0;

    public static final String leaveDays_FIELDNAME = "leaveDays";
    @FieldDescribe("휴가 일수")
    @Column(name = ColumnNamePrefix + leaveDays_FIELDNAME)
    private Integer leaveDays = 0;

    public static final String lateTimes_FIELDNAME = "lateTimes";
    @FieldDescribe("지각 횟수")
    @Column(name = ColumnNamePrefix + lateTimes_FIELDNAME)
    private Integer lateTimes = 0;

    public static final String leaveEarlierTimes_FIELDNAME = "leaveEarlierTimes";
    @FieldDescribe("조퇴 횟수")
    @Column(name = ColumnNamePrefix + leaveEarlierTimes_FIELDNAME)
    private Integer leaveEarlierTimes = 0;

    public static final String onDutyAbsenceTimes_FIELDNAME = "onDutyAbsenceTimes";
    @FieldDescribe("출근 체크 누락 횟수")
    @Column(name = ColumnNamePrefix + onDutyAbsenceTimes_FIELDNAME)
    private Integer onDutyAbsenceTimes = 0;

    public static final String offDutyAbsenceTimes_FIELDNAME = "offDutyAbsenceTimes";
    @FieldDescribe("퇴근 체크 누락 횟수")
    @Column(name = ColumnNamePrefix + offDutyAbsenceTimes_FIELDNAME)
    private Integer offDutyAbsenceTimes = 0;

    public static final String fieldWorkTimes_FIELDNAME = "fieldWorkTimes";
    @FieldDescribe("외근 체크 횟수")
    @Column(name = ColumnNamePrefix + fieldWorkTimes_FIELDNAME)
    private Integer fieldWorkTimes = 0;


    ///// 연관
    public static final String recordIdList_FIELDNAME = "recordIdList";
    @FieldDescribe("근태 기록 id 목록")
    @PersistentCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = ORDERCOLUMNCOLUMN)
    @ContainerTable(name = TABLE + ContainerTableNameMiddle
                           + recordIdList_FIELDNAME, joinIndex = @org.apache.openjpa.persistence.jdbc.Index(name =
            TABLE + recordIdList_FIELDNAME + JoinIndexNameSuffix))
    @ElementColumn(length = JpaObject.length_64B, name = ColumnNamePrefix + recordIdList_FIELDNAME)
    @ElementIndex(name = TABLE + recordIdList_FIELDNAME + ElementIndexNameSuffix)
    private List<String> recordIdList;

    public static final String groupId_FIELDNAME = "groupId";
    @FieldDescribe("근태 그룹 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + groupId_FIELDNAME)
    private String groupId;

    public static final String groupName_FIELDNAME = "groupName";
    @FieldDescribe("근태 그룹 이름")
    @Column(name = ColumnNamePrefix
                   + groupName_FIELDNAME)
    private String groupName;

    public static final String shiftId_FIELDNAME = "shiftId";
    @FieldDescribe("근무 id.")
    @Column(length = length_64B, name = ColumnNamePrefix + shiftId_FIELDNAME)
    private String shiftId;

    public static final String shiftName_FIELDNAME = "shiftName";
    @FieldDescribe("근무 이름, 휴무일")
    @Column(name = ColumnNamePrefix
                   + shiftName_FIELDNAME)
    private String shiftName;


    public Integer getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(Integer leaveDays) {
        this.leaveDays = leaveDays;
    }

    public Integer getFieldWorkTimes() {
        return fieldWorkTimes;
    }

    public void setFieldWorkTimes(Integer fieldWorkTimes) {
        this.fieldWorkTimes = fieldWorkTimes;
    }

    public String getRecordDay() {
        return recordDay;
    }

    public void setRecordDay(String recordDay) {
        this.recordDay = recordDay;
    }

    public Boolean getWorkDay() {
        return workDay;
    }

    public void setWorkDay(Boolean workDay) {
        this.workDay = workDay;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getYearString() {
        return yearString;
    }

    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    public String getMonthString() {
        return monthString;
    }

    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    public String getRecordDateString() {
        return recordDateString;
    }

    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    public Long getLateTimeDuration() {
        return lateTimeDuration;
    }

    public void setLateTimeDuration(Long lateTimeDuration) {
        this.lateTimeDuration = lateTimeDuration;
    }

    public Long getLeaveEarlierTimeDuration() {
        return leaveEarlierTimeDuration;
    }

    public void setLeaveEarlierTimeDuration(Long leaveEarlierTimeDuration) {
        this.leaveEarlierTimeDuration = leaveEarlierTimeDuration;
    }
//
//    public Long getWorkOvertimeTimeDuration() {
//        return workOvertimeTimeDuration;
//    }
//
//    public void setWorkOvertimeTimeDuration(Long workOvertimeTimeDuration) {
//        this.workOvertimeTimeDuration = workOvertimeTimeDuration;
//    }

    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    public Integer getAttendance() {
        return attendance;
    }

    public void setAttendance(Integer attendance) {
        this.attendance = attendance;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getAbsenteeismDays() {
        return absenteeismDays;
    }

    public void setAbsenteeismDays(Integer absenteeismDays) {
        this.absenteeismDays = absenteeismDays;
    }

    public Integer getLateTimes() {
        return lateTimes;
    }

    public void setLateTimes(Integer lateTimes) {
        this.lateTimes = lateTimes;
    }

    public Integer getLeaveEarlierTimes() {
        return leaveEarlierTimes;
    }

    public void setLeaveEarlierTimes(Integer leaveEarlierTimes) {
        this.leaveEarlierTimes = leaveEarlierTimes;
    }

    public Integer getOnDutyAbsenceTimes() {
        return onDutyAbsenceTimes;
    }

    public void setOnDutyAbsenceTimes(Integer onDutyAbsenceTimes) {
        this.onDutyAbsenceTimes = onDutyAbsenceTimes;
    }

    public Integer getOffDutyAbsenceTimes() {
        return offDutyAbsenceTimes;
    }

    public void setOffDutyAbsenceTimes(Integer offDutyAbsenceTimes) {
        this.offDutyAbsenceTimes = offDutyAbsenceTimes;
    }

    public List<String> getRecordIdList() {
        return recordIdList;
    }

    public void setRecordIdList(List<String> recordIdList) {
        this.recordIdList = recordIdList;
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
}
