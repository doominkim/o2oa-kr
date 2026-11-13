package com.x.query.core.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "ImportRecord", description = "데이터 센터 가져오기 기록.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.soft)
@Table(name = PersistenceProperties.Import.ImportRecord.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Import.ImportRecord.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ImportRecord extends SliceJpaObject {

	private static final long serialVersionUID = 3586573657619339181L;

	private static final String TABLE = PersistenceProperties.Import.ImportRecord.table;

	public static final String STATUS_WAIT = "待导入";

	public static final String STATUS_PROCESSING = "导入中";

	public static final String STATUS_SUCCESS = "导入成功";

	public static final String STATUS_PART_SUCCESS = "部分成功";

	public static final String STATUS_FAILED = "导入失败";

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

	/* 위는 JpaObject 기본 필드 */

	public void onPersist() throws Exception {
	}

	public static final String name_FIELDNAME = "name";
	@FieldDescribe("가져오기 모델 이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String name;

	public static final String creatorPerson_FIELDNAME = "creatorPerson";
	@FieldDescribe("작성자")
	@CheckPersist(allowEmpty = true)
	@Column(length = length_255B, name = ColumnNamePrefix + creatorPerson_FIELDNAME)
	private String creatorPerson;

	public static final String modelId_FIELDNAME = "modelId";
	@FieldDescribe("소속 가져오기 모델.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + modelId_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + modelId_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = ImportModel.class) })
	private String modelId;

	public static final String query_FIELDNAME = "query";
	@FieldDescribe("소속 쿼리.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + query_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + query_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Query.class) })
	private String query;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("상태.")
	@Column(length = length_255B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String status;

	public static final String count_FIELDNAME = "count";
	@FieldDescribe("가져오기 수량.")
	@Column(name = ColumnNamePrefix + count_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Integer count;

	public static final String failCount_FIELDNAME = "failCount";
	@FieldDescribe("导入失败数量.")
	@Column(name = ColumnNamePrefix + failCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer failCount;

	public static final String distribution_FIELDNAME = "distribution";
	@FieldDescribe("가져오기 결과 설명.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + distribution_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String distribution;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("가져오기 데이터.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_20M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}

	public String getCreatorPerson() {
		return creatorPerson;
	}

	public void setCreatorPerson(String creatorPerson) {
		this.creatorPerson = creatorPerson;
	}
}
