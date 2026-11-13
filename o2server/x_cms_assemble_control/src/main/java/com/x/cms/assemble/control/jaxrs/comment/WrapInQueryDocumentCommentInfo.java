package com.x.cms.assemble.control.jaxrs.comment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;
import com.x.cms.core.express.tools.filter.term.LikeTerm;

public class WrapInQueryDocumentCommentInfo {
	
	@FieldDescribe("정렬에 사용할 속성.")
	private String orderField = "orderNumber";

	@FieldDescribe("정렬 방식: DESC | ASC.")
	private String orderType = "ASC";
	
	@FieldDescribe("검색 조건: 게시판ID")
	private String appId;

	@FieldDescribe("검색 조건: 분류ID")
	private String categoryId;

	@FieldDescribe("검색 조건: 문서ID")
	private String documentId;

	@FieldDescribe("검색 조건: 댓글 제목")
	private String title;

	@FieldDescribe("검색 조건: 상위 댓글ID")
	private String parentId;

	@FieldDescribe("검색 조건: 작성자 이름")
	private String creatorName;

	@FieldDescribe("검색 조건: 댓글 심사 상태: 심사 없음|심사 대기|심사 통과")
	private String commentAuditStatus;

	@FieldDescribe("검색 조건: 심사자")
	private String auditorName;
	
	@FieldDescribe("댓글 권한: 비공개 댓글 | 공개 댓글 | 전체 댓글.")
	private String permission = "private | public | all";
	
	private Long rank = 0L;	
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCommentAuditStatus() {
		return commentAuditStatus;
	}

	public void setCommentAuditStatus(String commentAuditStatus) {
		this.commentAuditStatus = commentAuditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
	}

	/**
	 * 전달된 조회 파라미터로 완전한 QueryFilter 객체 구성
	 * @return
	 */
	public QueryFilter getQueryFilter() {
		QueryFilter queryFilter = new QueryFilter();		
		queryFilter.setJoinType( "and" );
		
		//조회 조건 객체 구성
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( "title", this.getTitle() ) );
		}
		if( StringUtils.isNotEmpty( this.getAppId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "appId", this.getAppId() ) );
		}
		if( StringUtils.isNotEmpty( this.getCategoryId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "categoryId", this.getCategoryId() ) );
		}
		if( StringUtils.isNotEmpty( this.getDocumentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "documentId", this.getDocumentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getParentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "parentId", this.getParentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getAuditorName() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "auditorName", this.getAuditorName() ) );
		}
		if( StringUtils.isNotEmpty( this.getCommentAuditStatus() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "commentAuditStatus", this.getCommentAuditStatus() ) );
		}
		if( StringUtils.isNotEmpty( this.getCreatorName() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "creatorName", this.getCreatorName() ) );
		}
		if( StringUtils.isNotEmpty( this.getPermission() )) {
			if( "public".equalsIgnoreCase( this.getPermission() )) {
				queryFilter.addIsFalseTerm( new IsFalseTerm( "isPrivate" ));
			}else if( "private".equalsIgnoreCase( this.getPermission() )) {
				queryFilter.addIsTrueTerm( new IsTrueTerm( "isPrivate" ));
			}
		}
		return queryFilter;
	}
}
