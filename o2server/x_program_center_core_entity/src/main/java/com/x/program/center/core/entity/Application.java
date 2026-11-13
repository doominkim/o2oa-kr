package com.x.program.center.core.entity;

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
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Application", description = "서비스 관리 앱.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.Application.TABLE, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Application.TABLE + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Application extends SliceJpaObject {

	private static final long serialVersionUID = 874852784032487858L;

	private static final String TABLE = PersistenceProperties.Application.TABLE;

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

	@FieldDescribe("이름")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String name;

	public static final String category_FIELDNAME = "category";
	@FieldDescribe("분류")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + category_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + category_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String category;

	public static final String subCategory_FIELDNAME = "subCategory";
	@FieldDescribe("하위 분류.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + subCategory_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String subCategory;

	public static final String version_FIELDNAME = "version";
	@FieldDescribe("버전")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + version_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String version;

	public static final String price_FIELDNAME = "price";
	@FieldDescribe("가격.")
	@Column(name = ColumnNamePrefix + price_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double price;

	public static final String describe_FIELDNAME = "describe";
	@FieldDescribe("설명.필수")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1K, name = ColumnNamePrefix + describe_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String describe;

	public static final String abort_FIELDNAME = "abort";
	@FieldDescribe("앱 소개")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_4K, name = ColumnNamePrefix + abort_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String abort;

	public static final String installSteps_FIELDNAME = "installSteps";
	@FieldDescribe("앱 설치 단계")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + installSteps_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String installSteps;

	public static final String icon_FIELDNAME = "icon";
	@FieldDescribe("첫 화면 이미지 Base64 인코딩 텍스트.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_1M, name = ColumnNamePrefix + icon_FIELDNAME)
	private String icon;

	public static final String publisher_FIELDNAME = "publisher";
	@FieldDescribe("게시자.")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + publisher_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publisher_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private String publisher;

	public static final String publishTime_FIELDNAME = "publishTime";
	@FieldDescribe("게시 시간")
	@Column(name = ColumnNamePrefix + publishTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + publishTime_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date publishTime;

	public static final String grade_FIELDNAME = "grade";
	@FieldDescribe("평점.")
	@Column(name = ColumnNamePrefix + grade_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + grade_FIELDNAME)
	private Double grade;

	public static final String commentCount_FIELDNAME = "commentCount";
	@FieldDescribe("댓글 수")
	@Column(name = ColumnNamePrefix + commentCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + commentCount_FIELDNAME)
	private Integer commentCount;

	public static final String orderNumber_FIELDNAME = "orderNumber";
	@FieldDescribe("정렬 번호, 오름차순, 비어 있으면 마지막")
	@Column(name = ColumnNamePrefix + orderNumber_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + orderNumber_FIELDNAME)
	private Integer orderNumber;

	public static final String recommend_FIELDNAME = "recommend";
	@FieldDescribe("추천 지수")
	@Column(name = ColumnNamePrefix + recommend_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + recommend_FIELDNAME)
	private Integer recommend;

	public static final String downloadCount_FIELDNAME = "downloadCount";
	@FieldDescribe("다운로드 횟수")
	@Column(name = ColumnNamePrefix + downloadCount_FIELDNAME)
	private Integer downloadCount;

	public static final String lastUpdateTime_FIELDNAME = "lastUpdateTime";
	@FieldDescribe("최종 수정 시간")
	@Column(name = ColumnNamePrefix + lastUpdateTime_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + lastUpdateTime_FIELDNAME)
	@CheckPersist(allowEmpty = false)
	private Date lastUpdateTime;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Integer getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(Integer downloadCount) {
		this.downloadCount = downloadCount;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getAbort() {
		return abort;
	}

	public void setAbort(String abort) {
		this.abort = abort;
	}

	public String getInstallSteps() {
		return installSteps;
	}

	public void setInstallSteps(String installSteps) {
		this.installSteps = installSteps;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Date getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Date publishTime) {
		this.publishTime = publishTime;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getRecommend() {
		return recommend;
	}

	public void setRecommend(Integer recommend) {
		this.recommend = recommend;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Double getGrade() {
		return grade;
	}

	public void setGrade(Double grade) {
		this.grade = grade;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}