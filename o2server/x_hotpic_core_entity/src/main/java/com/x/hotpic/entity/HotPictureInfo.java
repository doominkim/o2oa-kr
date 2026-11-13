package com.x.hotpic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.annotation.FieldDescribe;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인기 이미지 정보 테이블
 * 
 * @author O2LEE
 */
@Schema(name = "HotPictureInfo", description = "인기 이미지 정보.")
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Entity
@Table(name = PersistenceProperties.HotPictureInfo.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.HotPictureInfo.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class HotPictureInfo extends SliceJpaObject {

	private static final long serialVersionUID = 3856138316794473794L;
	private static final String TABLE = PersistenceProperties.HotPictureInfo.table;

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
	public static final String APPLICATION_CMS = "CMS";
	public static final String APPLICATION_BBS = "BBS";

	public static final String application_FIELDNAME = "application";
	@FieldDescribe("앱 이름")
	@Column(name = "xapplication", length = JpaObject.length_64B)
	@Index(name = TABLE + IndexNameMiddle + application_FIELDNAME)
	private String application = "";

	public static final String infoId_FIELDNAME = "infoId";
	@FieldDescribe("정보 객체 ID")
	@Column(name = "xinfoId", length = JpaObject.length_id)
	private String infoId = "";

	public static final String title_FIELDNAME = "title";
	@FieldDescribe("정보 제목")
	@Column(name = "xtitle", length = JpaObject.length_255B)
	private String title = "";

	public static final String summary_FIELDNAME = "summary";
	@FieldDescribe("주제 요약")
	@Column(length = JpaObject.length_255B, name = ColumnNamePrefix + summary_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String summary = "";

//	public static final String url_FIELDNAME = "url";
//	@FieldDescribe("정보 접근 URL")
//	@Column(name = "xurl", length = JpaObject.length_255B)
//	private String url = "";

	public static final String picId_FIELDNAME = "picId";
	@FieldDescribe("정보 이미지 URL")
	@Column(name = "xpicId", length = JpaObject.length_255B)
	private String picId = "";

	public static final String creator_FIELDNAME = "creator";
	@FieldDescribe("작성자")
	@Column(name = "xcreator", length = JpaObject.length_64B)
	private String creator = "";

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getInfoId() {
		return infoId;
	}

	public void setInfoId(String infoId) {
		this.infoId = infoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getPicId() {
		return picId;
	}

	public void setPicId(String picId) {
		this.picId = picId;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}
}