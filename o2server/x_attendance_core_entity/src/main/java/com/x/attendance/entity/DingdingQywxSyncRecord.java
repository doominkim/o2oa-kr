package com.x.attendance.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DingdingQywxSyncRecord", description = "근태 DingTalk·WeCom 동기화 로그.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.DingdingQywxSyncRecord.table, uniqueConstraints = @UniqueConstraint(name = PersistenceProperties.DingdingQywxSyncRecord.table
		+ JpaObject.IndexNameMiddle + JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
				JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }))
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class DingdingQywxSyncRecord extends SliceJpaObject {

	private static final String TABLE = PersistenceProperties.DingdingQywxSyncRecord.table;
	private static final long serialVersionUID = -7473350816056626066L;

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
	/*
	 * =============================================================================
	 * ===== 위는 JpaObject 기본 필드
	 * =============================================================================
	 * =====
	 */
	public static final String startTime_FIELDNAME = "startTime";
	@FieldDescribe("동기화 시작 시간")
	@Column(name = ColumnNamePrefix + startTime_FIELDNAME)
	private Date startTime;

	public static final String endTime_FIELDNAME = "endTime";
	@FieldDescribe("동기화 종료 시간")
	@Column(name = ColumnNamePrefix + endTime_FIELDNAME)
	private Date endTime;

	public static final String syncType_qywx = "qywx";
	public static final String syncType_dingding = "dingding";
	public static final String type_FIELDNAME = "type";
	@FieldDescribe("동기화 유형, qywx(WeCom 동기화), dingding(DingTalk 동기화)")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	private String type;

	public static final String dateFrom_FIELDNAME = "dateFrom";
	@FieldDescribe("출퇴근 기록 동기화 시작 시간")
	@Column(name = ColumnNamePrefix + dateFrom_FIELDNAME)
	private long dateFrom;

	public static final String dateTo_FIELDNAME = "dateTo";
	@FieldDescribe("출퇴근 기록 동기화 종료 시간, 시작과 종료 근무일은 최대 7일 간격")
	@Column(name = ColumnNamePrefix + dateTo_FIELDNAME)
	private long dateTo;

	public static final String status_loading = "loading";
	public static final String status_end = "end";
	public static final String status_error = "error";
	public static final String status_FIELDNAME = "status";
	@FieldDescribe("동기화 상태, loading(진행 중), end(종료), error(실행 오류)")
	@Column(length = length_32B, name = ColumnNamePrefix + status_FIELDNAME)
	private String status;

	public static final String exceptionMessage_FIELDNAME = "exceptionMessage";
	@FieldDescribe("예외 정보")
	@Column(length = length_255B, name = ColumnNamePrefix + exceptionMessage_FIELDNAME)
	private String exceptionMessage;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(long dateFrom) {
		this.dateFrom = dateFrom;
	}

	public long getDateTo() {
		return dateTo;
	}

	public void setDateTo(long dateTo) {
		this.dateTo = dateTo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
