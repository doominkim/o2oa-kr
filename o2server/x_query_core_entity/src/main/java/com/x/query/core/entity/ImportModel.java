package com.x.query.core.entity;

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
import com.x.base.core.entity.annotation.CitationExist;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Equal;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "ImportModel", description = "데이터 센터 가져오기 모델.")
@Entity
@ContainerEntity(dumpSize = 10, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Import.ImportModel.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Import.ImportModel.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class ImportModel extends SliceJpaObject {

	private static final long serialVersionUID = 8585967903130343838L;

	private static final String TABLE = PersistenceProperties.Import.ImportModel.table;

	public static final Integer MAX_COUNT = 5000;

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

	/* 위는 JpaObject 기본 필드 */

	@Override
	public void onPersist() throws Exception {
		if ((this.count == null) || (this.count < 1)) {
			this.count = MAX_COUNT;
		}
	}

	public Integer getCount() {
		if ((this.count == null) || (this.count < 1)) {
			return MAX_COUNT;
		} else {
			return this.count;
		}
	}

	/* 실행 메서드 업데이트 */

	/* Entity 기본 필드 끝 */

	/* 프런트와 대응하기 위함 */
	public static final String TYPE_PROCESSPLATFORM = "process";

	public static final String TYPE_CMS = "cms";

	public static final String TYPE_DYNAMIC_TABLE = "dynamicTable";

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("이름.")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 이름 중복 검증 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = ImportModel.class, equals = @Equal(field = "query", property = "query")))
	private String name;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationNotExists =
	/* 이름 중복 검증 */
	@CitationNotExist(fields = { "name", "id",
			"alias" }, type = ImportModel.class, equals = @Equal(field = "query", property = "query")))
	private String alias;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String query_FIELDNAME = "query";
	@FieldDescribe("소속 쿼리.")
	@Column(length = JpaObject.length_id, name = ColumnNamePrefix + query_FIELDNAME)
	@Index(name = TABLE + ColumnNamePrefix + query_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationExists = { @CitationExist(type = Query.class) })
	private String query;

	public static final String layout_FIELDNAME = "layout";
	@FieldDescribe("표시 레이아웃.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + layout_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String layout;

	public static final String data_FIELDNAME = "data";
	@FieldDescribe("접근 방안.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + data_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String data;

	public static final String code_FIELDNAME = "code";
	@FieldDescribe("프런트 실행 스크립트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + code_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String code;

	public static final String enableValid_FIELDNAME = "enableValid";
	@FieldDescribe("검증 사용 여부.")
	@Column(name = ColumnNamePrefix + enableValid_FIELDNAME)
	private Boolean enableValid;

	public static final String type_FIELDNAME = "type";
	@FieldDescribe("유형.")
	@Column(length = length_32B, name = ColumnNamePrefix + type_FIELDNAME)
	@CheckPersist(allowEmpty = false, simplyString = false)
	private String type;

	public static final String processStatus_FIELDNAME = "processStatus";
	@FieldDescribe("프로세스 상태.")
	@Column(length = length_32B, name = ColumnNamePrefix + processStatus_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String processStatus;

	public static final String availableIdentityList_FIELDNAME = "availableIdentityList";
	@FieldDescribe("사용 허용 사용자.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableIdentityList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableIdentityList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableIdentityList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableIdentityList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableIdentityList;

	public static final String availableUnitList_FIELDNAME = "availableUnitList";
	@FieldDescribe("사용 허용 조직.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableUnitList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
					+ availableUnitList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableUnitList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableUnitList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableUnitList;

	public static final String availableGroupList_FIELDNAME = "availableGroupList";
	@FieldDescribe("사용 허용 그룹.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle
			+ availableGroupList_FIELDNAME, joinIndex = @Index(name = TABLE + IndexNameMiddle
			+ availableGroupList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + availableGroupList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + availableGroupList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> availableGroupList;

	public static final String count_FIELDNAME = "count";
	@FieldDescribe("최대 가져오기 수량.")
	@Column(name = ColumnNamePrefix + count_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer count;

	public static final String display_FIELDNAME = "display";
	@FieldDescribe("프런트 표시 여부.")
	@Column(name = ColumnNamePrefix + display_FIELDNAME)
	private Boolean display;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getEnableValid() {
		return enableValid;
	}

	public void setEnableValid(Boolean enableValid) {
		this.enableValid = enableValid;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<String> getAvailableIdentityList() {
		return availableIdentityList;
	}

	public void setAvailableIdentityList(List<String> availableIdentityList) {
		this.availableIdentityList = availableIdentityList;
	}

	public List<String> getAvailableUnitList() {
		return availableUnitList;
	}

	public void setAvailableUnitList(List<String> availableUnitList) {
		this.availableUnitList = availableUnitList;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public Boolean getDisplay() {
		return display;
	}

	public void setDisplay(Boolean display) {
		this.display = display;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public List<String> getAvailableGroupList() {
		return availableGroupList;
	}

	public void setAvailableGroupList(List<String> availableGroupList) {
		this.availableGroupList = availableGroupList;
	}
}
