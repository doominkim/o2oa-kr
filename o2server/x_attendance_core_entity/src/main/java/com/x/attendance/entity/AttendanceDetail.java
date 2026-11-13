package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AttendanceDetail", description = "근태 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.AttendanceDetail.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.AttendanceDetail.table
        + JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = {JpaObject.IDCOLUMN,
        JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN}))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AttendanceDetail extends SliceJpaObject {

    private static final long serialVersionUID = 3856138316794473794L;
    private static final String TABLE = PersistenceProperties.AttendanceDetail.table;

    public AttendanceDetail() {
    }

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
        this.setSequence(StringUtils.join(this.empName, this.recordDateString, this.getId()));
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
    public static final String empNo_FIELDNAME = "empNo";
    @FieldDescribe("사번")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + empNo_FIELDNAME)

    private String empNo;

    public static final String empName_FIELDNAME = "empName";
    @FieldDescribe("직원 이름 distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + empName_FIELDNAME)

    private String empName;

    public static final String topUnitName_FIELDNAME = "topUnitName";
    @FieldDescribe("최상위 조직 이름 distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + topUnitName_FIELDNAME)

    private String topUnitName;

    public static final String unitName_FIELDNAME = "unitName";
    @FieldDescribe("조직 이름 distinguishedName")
    @Column(length = AbstractPersistenceProperties.organization_name_length, name = ColumnNamePrefix
            + unitName_FIELDNAME)

    private String unitName;

    public static final String recordDate_FIELDNAME = "recordDate";
    @FieldDescribe("체크 기록 날짜")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = ColumnNamePrefix + recordDate_FIELDNAME)

    private Date recordDate;

    public static final String yearString_FIELDNAME = "yearString";
    @FieldDescribe("출퇴근 기록 연도")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + yearString_FIELDNAME)

    private String yearString;

    public static final String monthString_FIELDNAME = "monthString";
    @FieldDescribe("출퇴근 기록 월")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + monthString_FIELDNAME)

    private String monthString;

    public static final String recordDateString_FIELDNAME = "recordDateString";
    @FieldDescribe("출퇴근 기록 일자 문자열")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + recordDateString_FIELDNAME)

    private String recordDateString;

    public static final String cycleYear_FIELDNAME = "cycleYear";
    @FieldDescribe("통계 주기 연도")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleYear_FIELDNAME)

    private String cycleYear;

    public static final String cycleMonth_FIELDNAME = "cycleMonth";
    @FieldDescribe("통계 주기 월")
    @Column(length = JpaObject.length_16B, name = ColumnNamePrefix + cycleMonth_FIELDNAME)

    private String cycleMonth;

    public static final String selfHolidayDayTime_FIELDNAME = "selfHolidayDayTime";
    @FieldDescribe("휴가 시간대:오전|오후|종일")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + selfHolidayDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String selfHolidayDayTime = "없음";

    public static final String absentDayTime_FIELDNAME = "absentDayTime";
    @FieldDescribe("결근 시간대:오전|오후|종일")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + absentDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String absentDayTime = "없음";

    public static final String abnormalDutyDayTime_FIELDNAME = "abnormalDutyDayTime";
    @FieldDescribe("출퇴근 기록 이상 시간대:오전|오후|종일")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + abnormalDutyDayTime_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private String abnormalDutyDayTime = "없음";

    public static final String getSelfHolidayDays_FIELDNAME = "getSelfHolidayDays";
    @FieldDescribe("휴가 일수: 0|0.5|1")
    @Column(name = ColumnNamePrefix + getSelfHolidayDays_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Double getSelfHolidayDays = 0.0;

    public static final String onWorkTime_FIELDNAME = "onWorkTime";
    @FieldDescribe("출근 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + onWorkTime_FIELDNAME)

    private String onWorkTime;

    public static final String offWorkTime_FIELDNAME = "offWorkTime";
    @FieldDescribe("퇴근 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + offWorkTime_FIELDNAME)

    private String offWorkTime;

    public static final String middayRestStartTime_FIELDNAME = "middayRestStartTime";
    @FieldDescribe("점심 휴식 시작 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestStartTime_FIELDNAME)

    private String middayRestStartTime;

    public static final String middayRestEndTime_FIELDNAME = "middayRestEndTime";
    @FieldDescribe("점심 휴식 종료 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + middayRestEndTime_FIELDNAME)

    private String middayRestEndTime;

    public static final String onDutyTime_FIELDNAME = "onDutyTime";
    @FieldDescribe("출근 기록 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + onDutyTime_FIELDNAME)

    private String onDutyTime;

    public static final String morningOffDutyTime_FIELDNAME = "morningOffDutyTime";
    @FieldDescribe("오후 퇴근 기록 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + morningOffDutyTime_FIELDNAME)

    private String morningOffDutyTime;

    public static final String afternoonOnDutyTime_FIELDNAME = "afternoonOnDutyTime";
    @FieldDescribe("오후 출근 기록 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + afternoonOnDutyTime_FIELDNAME)

    private String afternoonOnDutyTime;

    public static final String offDutyTime_FIELDNAME = "offDutyTime";
    @FieldDescribe("퇴근 기록 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + offDutyTime_FIELDNAME)

    private String offDutyTime;

    public static final String lateTimeDuration_FIELDNAME = "lateTimeDuration";
    @FieldDescribe("지각 시간")
    @Column(name = ColumnNamePrefix + lateTimeDuration_FIELDNAME)
    private Long lateTimeDuration = 0L;

    public static final String leaveEarlierTimeDuration_FIELDNAME = "leaveEarlierTimeDuration";
    @FieldDescribe("조퇴 시간")
    @Column(name = ColumnNamePrefix + leaveEarlierTimeDuration_FIELDNAME)
    private Long leaveEarlierTimeDuration = 0L;

    public static final String workOvertimeTimeDuration_FIELDNAME = "workOvertimeTimeDuration";
    @FieldDescribe("초과 근무 시간")
    @Column(name = ColumnNamePrefix + workOvertimeTimeDuration_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Long workOvertimeTimeDuration = 0L;

    public static final String workTimeDuration_FIELDNAME = "workTimeDuration";
    @FieldDescribe("출근 시간")
    @Column(name = ColumnNamePrefix + workTimeDuration_FIELDNAME)
    private Long workTimeDuration = 0L;

    public static final String attendance_FIELDNAME = "attendance";
    @FieldDescribe("출근 일수(0|0.5|1)")
    @Column(name = ColumnNamePrefix + attendance_FIELDNAME)
    private Double attendance = 1.0;

    public static final String absence_FIELDNAME = "absence";
    @FieldDescribe("결근 일수(0|0.5|1)")
    @Column(name = ColumnNamePrefix + absence_FIELDNAME)
    private Double absence = 0.0;

    public static final String recordStatus_FIELDNAME = "recordStatus";
    @FieldDescribe("기록 상태: 0-미분석 1-분석 완료")
    @Column(name = ColumnNamePrefix + recordStatus_FIELDNAME)
    private Integer recordStatus = 0;

    public static final String batchName_FIELDNAME = "batchName";
    @FieldDescribe("가져오기 배치 번호: 가져온 파일의 ID")
    @Column(length = JpaObject.length_96B, name = ColumnNamePrefix + batchName_FIELDNAME)

    private String batchName;

    public static final String description_FIELDNAME = "description";
    @FieldDescribe("비고")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)

    private String description;

    public static final String appealStatus_FIELDNAME = "appealStatus";
    @FieldDescribe("소명 상태: 0-미소명, 1-소명 중, -1-소명 반려, 9-소명 승인")
    @Column(name = ColumnNamePrefix + appealStatus_FIELDNAME)
    private Integer appealStatus = 0;

    public static final String appealReason_FIELDNAME = "appealReason";
    @FieldDescribe("이의 신청 사유")
    @Column(length = JpaObject.length_64B, name = ColumnNamePrefix + appealReason_FIELDNAME)

    private String appealReason;

    public static final String appealProcessor_FIELDNAME = "appealProcessor";
    @FieldDescribe("이의 신청 처리자")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + appealProcessor_FIELDNAME)

    private String appealProcessor;

    public static final String appealDescription_FIELDNAME = "appealDescription";
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @FieldDescribe("이의 신청 상세 설명")
    @Column(length = JpaObject.length_2K, name = ColumnNamePrefix + appealDescription_FIELDNAME)

    private String appealDescription;

    public static final String isHoliday_FIELDNAME = "isHoliday";
    @FieldDescribe("법정 공휴일 여부")
    @Column(name = ColumnNamePrefix + isHoliday_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isHoliday = false;

    public static final String isWorkday_FIELDNAME = "isWorkday";
    @FieldDescribe("대체 근무일 여부")
    @Column(name = ColumnNamePrefix + isWorkday_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isWorkday = false;

    public static final String isGetSelfHolidays_FIELDNAME = "isGetSelfHolidays";
    @FieldDescribe("휴가 여부")
    @Column(name = ColumnNamePrefix + isGetSelfHolidays_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isGetSelfHolidays = false;

    public static final String leaveType_FIELDNAME = "leaveType";
    @FieldDescribe("휴가 유형")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + leaveType_FIELDNAME)

    private String leaveType;

    public static final String isAbsent_FIELDNAME = "isAbsent";
    @FieldDescribe("결근 여부")
    @Column(name = ColumnNamePrefix + isAbsent_FIELDNAME)
    private Boolean isAbsent = false;

    public static final String isAbnormalDuty_FIELDNAME = "isAbnormalDuty";
    @FieldDescribe("출퇴근 기록 이상 여부")
    @Column(name = ColumnNamePrefix + isAbnormalDuty_FIELDNAME)
    private Boolean isAbnormalDuty = false;

    public static final String isLackOfTime_FIELDNAME = "isLackOfTime";
    @FieldDescribe("근무 시간 부족 여부")
    @Column(name = ColumnNamePrefix + isLackOfTime_FIELDNAME)
    private Boolean isLackOfTime = false;

    public static final String isWorkOvertime_FIELDNAME = "isWorkOvertime";
    @FieldDescribe("초과 근무 여부")
    @Column(name = ColumnNamePrefix + isWorkOvertime_FIELDNAME)
    private Boolean isWorkOvertime = false;

    public static final String isLeaveEarlier_FIELDNAME = "isLeaveEarlier";
    @FieldDescribe("조퇴 여부")
    @Column(name = ColumnNamePrefix + isLeaveEarlier_FIELDNAME)
    private Boolean isLeaveEarlier = false;

    public static final String isLate_FIELDNAME = "isLate";
    @FieldDescribe("지각 여부")
    @Column(name = ColumnNamePrefix + isLate_FIELDNAME)
    private Boolean isLate = false;

    public static final String isWeekend_FIELDNAME = "isWeekend";
    @FieldDescribe("주말 여부")
    @Column(name = ColumnNamePrefix + isWeekend_FIELDNAME)
    @CheckPersist(allowEmpty = false)
    private Boolean isWeekend = false;

    public static final String archiveTime_FIELDNAME = "archiveTime";
    @FieldDescribe("기록 보관 시간")
    @Column(length = JpaObject.length_32B, name = ColumnNamePrefix + archiveTime_FIELDNAME)

    private String archiveTime;

    public static final String isExternal_FIELDNAME = "isExternal";
    @FieldDescribe("범위 밖 기록 여부")
    @Column(name = ColumnNamePrefix + isExternal_FIELDNAME)

    private Boolean isExternal = false;

    public static final String recordAddress_FIELDNAME = "recordAddress";
    @FieldDescribe("출퇴근 기록 주소")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + recordAddress_FIELDNAME)

    private String recordAddress;

    public static final String optMachineType_FIELDNAME = "optMachineType";
    @FieldDescribe("기기 정보")
    @Column(length = JpaObject.length_255B, name = ColumnNamePrefix + optMachineType_FIELDNAME)

    private String optMachineType;

    public String getRecordAddress() {
        return recordAddress;
    }

    public void setRecordAddress(String recordAddress) {
        this.recordAddress = recordAddress;
    }

    public String getOptMachineType() {
        return optMachineType;
    }

    public void setOptMachineType(String optMachineType) {
        this.optMachineType = optMachineType;
    }

    public String getMorningOffDutyTime() {
        return morningOffDutyTime;
    }

    public void setMorningOffDutyTime(String morningOffDutyTime) {
        this.morningOffDutyTime = morningOffDutyTime;
    }

    public String getAfternoonOnDutyTime() {
        return afternoonOnDutyTime;
    }

    public void setAfternoonOnDutyTime(String afternoonOnDutyTime) {
        this.afternoonOnDutyTime = afternoonOnDutyTime;
    }

    /**
     * 근무 시간 부족 여부 조회
     *
     * @return
     */
    public Boolean getIsLackOfTime() {
        return isLackOfTime;
    }

    /**
     * 근무 시간 부족 여부 설정
     *
     * @param isLackOfTime
     */
    public void setIsLackOfTime(Boolean isLackOfTime) {
        this.isLackOfTime = isLackOfTime;
    }

    /**
     * 직원 번호 조회(String)
     *
     * @return
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * 직원 번호 설정(String)
     *
     * @param empNo
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * 직원 이름 조회(String)
     *
     * @return
     */
    public String getEmpName() {
        if (empName != null) {
            empName = empName.trim();
        }
        return empName;
    }

    /**
     * 직원 이름 설정(String)
     *
     * @param empName
     */
    public void setEmpName(String empName) {
        if (empName != null) {
            empName = empName.trim();
        }
        this.empName = empName;
    }

    /**
     * 직원 지각 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsLate() {
        return isLate;
    }

    /**
     * 직원 지각 여부 설정(Integer)
     *
     * @param isLate
     */
    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }

    /**
     * 직원 조퇴 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsLeaveEarlier() {
        return isLeaveEarlier;
    }

    /**
     * 직원 조퇴 여부 설정(Integer)
     *
     * @param isLeaveEarlier
     */
    public void setIsLeaveEarlier(Boolean isLeaveEarlier) {
        this.isLeaveEarlier = isLeaveEarlier;
    }

    /**
     * 직원 출퇴근 기록 일자 조회(String)
     *
     * @return
     */
    public String getRecordDateString() {
        return recordDateString;
    }

    /**
     * 직원 출퇴근 기록 일자 설정(String)
     *
     * @param recordDateString
     */
    public void setRecordDateString(String recordDateString) {
        this.recordDateString = recordDateString;
    }

    /**
     * 직원 출퇴근 기록 일자 조회(Date)
     *
     * @return
     */
    public Date getRecordDate() {
        return recordDate;
    }

    /**
     * 직원 출퇴근 기록 일자 설정(Date)
     *
     * @param recordDate
     */
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * 직원 출퇴근 기록 가져오기 배치 번호 - 가져온 파일 ID 조회(String)
     *
     * @return
     */
    public String getBatchName() {
        return batchName;
    }

    /**
     * 직원 출퇴근 기록 가져오기 배치 번호 - 가져온 파일 ID 설정(String)
     *
     * @param batchName
     */
    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    /**
     * 직원 표준 출근 시간 조회(String)
     *
     * @return
     */
    public String getOnWorkTime() {
        return onWorkTime;
    }

    /**
     * 직원 표준 출근 시간 설정(String)
     *
     * @param onWorkTime
     */
    public void setOnWorkTime(String onWorkTime) {
        this.onWorkTime = onWorkTime;
    }

    /**
     * 직원 표준 퇴근 시간 조회(String)
     *
     * @return
     */
    public String getOffWorkTime() {
        return offWorkTime;
    }

    /**
     * 직원 표준 퇴근 시간 설정(String)
     *
     * @param offWorkTime
     */
    public void setOffWorkTime(String offWorkTime) {
        this.offWorkTime = offWorkTime;
    }

    /**
     * 직원 지각 시간 조회(Long)
     *
     * @return
     */
    public Long getLateTimeDuration() {
        return lateTimeDuration;
    }

    /**
     * 직원 지각 시간 설정(Long)
     *
     * @param lateTimeDuration
     */
    public void setLateTimeDuration(Long lateTimeDuration) {
        this.lateTimeDuration = lateTimeDuration;
    }

    /**
     * 직원 조퇴 시간 조회(Long)
     *
     * @return
     */
    public Long getLeaveEarlierTimeDuration() {
        return leaveEarlierTimeDuration;
    }

    /**
     * 직원 조퇴 시간 설정(Long)
     *
     * @param leaveEarlierTimeDuration
     */
    public void setLeaveEarlierTimeDuration(Long leaveEarlierTimeDuration) {
        this.leaveEarlierTimeDuration = leaveEarlierTimeDuration;
    }

    /**
     * 직원 결근 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsAbsent() {
        return isAbsent;
    }

    /**
     * 직원 결근 여부 설정(Integer)
     *
     * @param isAbsent
     */
    public void setIsAbsent(Boolean isAbsent) {
        this.isAbsent = isAbsent;
    }

    /**
     * 직원 초과 근무 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsWorkOvertime() {
        return isWorkOvertime;
    }

    /**
     * 직원 초과 근무 여부 설정(Integer)
     *
     * @param isWorkOvertime
     */
    public void setIsWorkOvertime(Boolean isWorkOvertime) {
        this.isWorkOvertime = isWorkOvertime;
    }

    /**
     * 직원 초과근무 시간 조회(Long)
     *
     * @return
     */
    public Long getWorkOvertimeTimeDuration() {
        return workOvertimeTimeDuration;
    }

    /**
     * 직원 초과근무 시간 설정(Long)
     *
     * @param workOvertimeTimeDuration
     */
    public void setWorkOvertimeTimeDuration(Long workOvertimeTimeDuration) {
        this.workOvertimeTimeDuration = workOvertimeTimeDuration;
    }

    /**
     * 당일 법정 공휴일 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsHoliday() {
        return isHoliday;
    }

    /**
     * 당일 법정 공휴일 여부 설정(Integer)
     *
     * @param isHoliday
     */
    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }

    /**
     * 직원 출근 시간(분) 조회(Long)
     *
     * @return
     */
    public Long getWorkTimeDuration() {
        return workTimeDuration;
    }

    /**
     * 직원 출근 시간(분) 설정(Long)
     *
     * @param workTimeDuration
     */
    public void setWorkTimeDuration(Long workTimeDuration) {
        this.workTimeDuration = workTimeDuration;
    }

    /**
     * 직원 출근 일수(0, 0.5, 1) 조회(Double)
     *
     * @return
     */
    public Double getAttendance() {
        return attendance;
    }

    /**
     * 직원 출근 일수(0, 0.5, 1) 설정(Double)
     *
     * @param attendance
     */
    public void setAttendance(Double attendance) {
        this.attendance = attendance;
    }

    /**
     * 직원 출근 체크 시간 조회(String)
     *
     * @return
     */
    public String getOnDutyTime() {
        return onDutyTime;
    }

    /**
     * 직원 출근 체크 시간 설정(String)
     *
     * @param onDutyTime
     */
    public void setOnDutyTime(String onDutyTime) {
        this.onDutyTime = onDutyTime;
    }

    /**
     * 직원 퇴근 체크 시간 조회(String)
     *
     * @return
     */
    public String getOffDutyTime() {
        return offDutyTime;
    }

    /**
     * 직원 퇴근 체크 시간 설정(String)
     *
     * @param offDutyTime
     */
    public void setOffDutyTime(String offDutyTime) {
        this.offDutyTime = offDutyTime;
    }

    /**
     * 직원 출퇴근 기록 분석 상태 조회: 0-미분석 1-분석 완료 -1-분석 오류(String)
     *
     * @return
     */
    public Integer getRecordStatus() {
        return recordStatus;
    }

    /**
     * 직원 출퇴근 기록 분석 상태 설정: 0-미분석 1-분석 완료 -1-분석 오류(String)
     *
     * @param recordStatus
     */
    public void setRecordStatus(Integer recordStatus) {
        this.recordStatus = recordStatus;
    }

    /**
     * 직원 출퇴근 체크 당일 대체 근무일 여부 조회(String)
     *
     * @return
     */
    public Boolean getIsWorkday() {
        return isWorkday;
    }

    /**
     * 직원 출퇴근 체크 당일 대체 근무일 여부 설정(String)
     *
     * @param isWorkday
     */
    public void setIsWorkday(Boolean isWorkday) {
        this.isWorkday = isWorkday;
    }

    /**
     * 직원 출퇴근 체크 당일 주말 여부 조회(String)
     *
     * @return
     */
    public Boolean getIsWeekend() {
        return isWeekend;
    }

    /**
     * 직원 출퇴근 체크 당일 주말 여부 설정(String)
     *
     * @param isWeekend
     */
    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    /**
     * 직원 출퇴근 기록 분석 처리 설명 조회: 시스템 분석 시 자동 입력(String)
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     * 직원 출퇴근 기록 분석 처리 설명 설정: 시스템 분석 시 자동 입력(String)
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 직원 소속 최상위 조직 이름 조회(String)
     *
     * @return
     */
    public String getTopUnitName() {
        return topUnitName;
    }

    /**
     * 직원 소속 최상위 조직 이름 설정(String)
     *
     * @param topUnitName
     */
    public void setTopUnitName(String topUnitName) {
        this.topUnitName = topUnitName;
    }

    /**
     * 직원 소속 조직 이름 조회(String)
     *
     * @return
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * 직원 소속 조직 이름 설정(String)
     *
     * @param unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * 직원 출퇴근 기록 연도 조회(String)
     *
     * @return
     */
    public String getYearString() {
        return yearString;
    }

    /**
     * 직원 출퇴근 기록 연도 설정(String)
     *
     * @param yearString
     */
    public void setYearString(String yearString) {
        this.yearString = yearString;
    }

    /**
     * 직원 출퇴근 기록 월 조회(String)
     *
     * @return
     */
    public String getMonthString() {
        return monthString;
    }

    /**
     * 직원 출퇴근 기록 월 설정(String)
     *
     * @param monthString
     */
    public void setMonthString(String monthString) {
        this.monthString = monthString;
    }

    /**
     * 직원 휴가 중 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsGetSelfHolidays() {
        return isGetSelfHolidays;
    }

    /**
     * 직원 휴가 중 여부 설정(Integer)
     *
     * @param isGetSelfHolidays
     */
    public void setIsGetSelfHolidays(Boolean isGetSelfHolidays) {
        this.isGetSelfHolidays = isGetSelfHolidays;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    /**
     * 휴가 시간대 조회: 없음, 오전, 오후, 종일(String)
     *
     * @return
     */
    public String getSelfHolidayDayTime() {
        return selfHolidayDayTime;
    }

    /**
     * 휴가 시간대 설정: 없음, 오전, 오후, 종일(String)
     *
     * @param selfHolidayDayTime
     */
    public void setSelfHolidayDayTime(String selfHolidayDayTime) {
        this.selfHolidayDayTime = selfHolidayDayTime;
    }

    /**
     * 직원 휴가 일수 조회: 0,0.5,1(String)
     *
     * @return
     */
    public Double getGetSelfHolidayDays() {
        return getSelfHolidayDays;
    }

    /**
     * 직원 휴가 일수 설정: 0,0.5,1(String)
     *
     * @param getSelfHolidayDays
     */
    public void setGetSelfHolidayDays(Double getSelfHolidayDays) {
        this.getSelfHolidayDays = getSelfHolidayDays;
    }

    /**
     * 직원 결근 일수 조회: 0,0.5,1(String)
     *
     * @return
     */
    public Double getAbsence() {
        return absence;
    }

    /**
     * 직원 결근 일수 설정: 0,0.5,1(String)
     *
     * @param absence
     */
    public void setAbsence(Double absence) {
        this.absence = absence;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 상태 조회:0-미신청, 1-신청 중, -1-신청 반려, 9-신청 승인
     *
     * @return
     */
    public Integer getAppealStatus() {
        return appealStatus;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 상태 조회:0-미신청, 1-신청 중, -1-신청 반려, 9-신청 승인
     *
     * @param appealStatus
     */
    public void setAppealStatus(Integer appealStatus) {
        this.appealStatus = appealStatus;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 간단 사유 조회(String)
     *
     * @return
     */
    public String getAppealReason() {
        return appealReason;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 간단 사유 설정(String)
     *
     * @param appealReason
     */
    public void setAppealReason(String appealReason) {
        this.appealReason = appealReason;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 상세 사유 조회(String)
     *
     * @return
     */
    public String getAppealDescription() {
        return appealDescription;
    }

    /**
     * 직원 출퇴근 기록 분석 결과 이의 신청 상세 사유 설정(String)
     *
     * @param appealDescription
     */
    public void setAppealDescription(String appealDescription) {
        this.appealDescription = appealDescription;
    }

    /**
     * 직원 결근 시간대 조회: 없음, 오전, 오후, 종일(String)
     *
     * @return
     */
    public String getAbsentDayTime() {
        return absentDayTime;
    }

    /**
     * 직원 결근 시간대 설정: 없음, 오전, 오후, 종일(String)
     *
     * @param absentDayTime
     */
    public void setAbsentDayTime(String absentDayTime) {
        this.absentDayTime = absentDayTime;
    }

    /**
     * 직원 출퇴근 기록 이상 시간대 조회: 없음, 오전, 오후, 종일(String)
     *
     * @return
     */
    public String getAbnormalDutyDayTime() {
        return abnormalDutyDayTime;
    }

    /**
     * 직원 출퇴근 기록 이상 시간대 설정: 없음, 오전, 오후, 종일(String)
     *
     * @param abnormalDutyDayTime
     */
    public void setAbnormalDutyDayTime(String abnormalDutyDayTime) {
        this.abnormalDutyDayTime = abnormalDutyDayTime;
    }

    /**
     * 직원 출퇴근 기록 이상 여부 조회(Integer)
     *
     * @return
     */
    public Boolean getIsAbnormalDuty() {
        return isAbnormalDuty;
    }

    /**
     * 직원 출퇴근 기록 이상 여부 설정(Integer)
     *
     * @param isAbnormalDuty
     */
    public void setIsAbnormalDuty(Boolean isAbnormalDuty) {
        this.isAbnormalDuty = isAbnormalDuty;
    }

    public String getCycleYear() {
        return cycleYear;
    }

    public void setCycleYear(String cycleYear) {
        this.cycleYear = cycleYear;
    }

    public String getCycleMonth() {
        return cycleMonth;
    }

    public void setCycleMonth(String cycleMonth) {
        this.cycleMonth = cycleMonth;
    }

    public String getArchiveTime() {
        return archiveTime;
    }

    public void setArchiveTime(String archiveTime) {
        this.archiveTime = archiveTime;
    }

    public String getAppealProcessor() {
        return appealProcessor;
    }

    public void setAppealProcessor(String appealProcessor) {
        this.appealProcessor = appealProcessor;
    }

    public String getMiddayRestStartTime() {
        return middayRestStartTime;
    }

    public void setMiddayRestStartTime(String middayRestStartTime) {
        this.middayRestStartTime = middayRestStartTime;
    }

    public String getMiddayRestEndTime() {
        return middayRestEndTime;
    }

    public void setMiddayRestEndTime(String middayRestEndTime) {
        this.middayRestEndTime = middayRestEndTime;
    }

    public Boolean getHoliday() {
        return isHoliday;
    }

    public void setHoliday(Boolean holiday) {
        isHoliday = holiday;
    }

    public Boolean getWorkday() {
        return isWorkday;
    }

    public void setWorkday(Boolean workday) {
        isWorkday = workday;
    }

    public Boolean getGetSelfHolidays() {
        return isGetSelfHolidays;
    }

    public void setGetSelfHolidays(Boolean getSelfHolidays) {
        isGetSelfHolidays = getSelfHolidays;
    }

    public Boolean getAbsent() {
        return isAbsent;
    }

    public void setAbsent(Boolean absent) {
        isAbsent = absent;
    }

    public Boolean getAbnormalDuty() {
        return isAbnormalDuty;
    }

    public void setAbnormalDuty(Boolean abnormalDuty) {
        isAbnormalDuty = abnormalDuty;
    }

    public Boolean getLackOfTime() {
        return isLackOfTime;
    }

    public void setLackOfTime(Boolean lackOfTime) {
        isLackOfTime = lackOfTime;
    }

    public Boolean getWorkOvertime() {
        return isWorkOvertime;
    }

    public void setWorkOvertime(Boolean workOvertime) {
        isWorkOvertime = workOvertime;
    }

    public Boolean getLeaveEarlier() {
        return isLeaveEarlier;
    }

    public void setLeaveEarlier(Boolean leaveEarlier) {
        isLeaveEarlier = leaveEarlier;
    }

    public Boolean getLate() {
        return isLate;
    }

    public void setLate(Boolean late) {
        isLate = late;
    }

    public Boolean getWeekend() {
        return isWeekend;
    }

    public void setWeekend(Boolean weekend) {
        isWeekend = weekend;
    }

    public Boolean getIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    /**
     * 해당 데이터의 분석 결과 삭제
     */
    public void refresh() {
        this.recordStatus = 0; // 기록 분석 상태
        this.cycleYear = "1999"; // 통계 연도
        this.cycleMonth = "01"; // 통계 월

        this.isLackOfTime = false; // 근무 시간 부족 여부
        this.isAbnormalDuty = false; // 출퇴근 기록 이상 여부
        this.isAbsent = false; // 결근 여부
        this.isLate = false; // 지각 여부
        this.isLeaveEarlier = false; // 조퇴 여부
        this.isWorkOvertime = false; // 초과 근무 여부

        this.isWorkday = false; // 대체 근무일 여부
        this.isHoliday = false; // 공휴일 여부
        this.isWeekend = false; // 주말 여부
        this.isGetSelfHolidays = false; // 휴가 여부

        this.absence = 0.0; // 결근 일수
        this.attendance = 1.0; // 출근 일수
        this.lateTimeDuration = 0L; // 지각 시간(분)
        this.leaveEarlierTimeDuration = 0L; // 조퇴 시간(분)

        this.workOvertimeTimeDuration = 0L; // 초과 근무 시간(분)
        this.workTimeDuration = 0L; // 근무 시간(분)

        this.appealDescription = ""; // 이의 신청 상세
        this.appealReason = ""; // 이의 신청 사유
        this.appealStatus = 0; // 이의 신청 상태
        this.appealProcessor = "";

        this.absentDayTime = "없음"; // 결근 시간대: 없음, 오전, 오후, 종일
        this.selfHolidayDayTime = "없음"; // 휴가 시간대: 없음, 오전, 오후, 종일
        this.abnormalDutyDayTime = "없음"; // 출퇴근 기록 이상 시간대: 없음, 오전, 오후, 종일
    }
}