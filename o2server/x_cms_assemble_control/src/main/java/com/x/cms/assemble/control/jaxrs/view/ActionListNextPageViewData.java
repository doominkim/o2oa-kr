package com.x.cms.assemble.control.jaxrs.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.ExceptionWrapInConvert;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewFieldConfig;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;

public class ActionListNextPageViewData extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger( ActionListNextPageViewData.class );

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, EffectivePerson effectivePerson, String lastDocId, Integer pageSize, JsonElement jsonElement ) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		Wi wi = null;
		Wo wo = null;
		WoDocument woDocument = new WoDocument();
		List<Document> documentList = null;
		List<Document> searchResultList = new ArrayList<>();
		List<Review> reviewList =  null;
		List<String> viewIdsForCategory = null;
		Long documentCount = 0L;
		CategoryInfo category = null;
		View view = null;
		Boolean check = true;
		Boolean isManager = false;
		Boolean needData = false;
		Document document = null;
		String personName =  effectivePerson.getDistinguishedName();
		List<String> sortableFieldNames = null;
		List<String> sortableFieldNames_upperCase = null;
		QueryFilter queryFilter = null;

		if( pageSize <= 0 || pageSize == null ){
			pageSize = 12;
		}

		if( "(0)".equals( lastDocId ) || StringUtils.isEmpty( lastDocId ) ){
			lastDocId = null;
		}

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionWrapInConvert( e, jsonElement );
			result.error( exception );
			logger.error( e, effectivePerson, request, null );
		}

		if( StringUtils.isEmpty( wi.getCategoryId() ) ) {
			check = false;
			Exception exception = new ExceptionViewDateQueryCategoryIdEmpty();
			result.error( exception );
		}

		if( StringUtils.isEmpty( wi.getViewId() ) ) {
			check = false;
			Exception exception = new ExceptionViewDataQueryViewIdEmpty();
			result.error( exception );
		}

		try {
			isManager = userManagerService.isManager(effectivePerson );
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionViewInfoProcess(e, "사용자가 플랫폼 관리자인지 확인하는 중 오류가 발생했습니다. Name:" + personName);
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if( check ){
			try{
				category = categoryInfoServiceAdv.get( wi.getCategoryId() );
				if( category == null ){
					check = false;
					Exception exception = new ExceptionCategoryNotExists( wi.getCategoryId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据ID查询分类信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			try{
				view = viewServiceAdv.get( wi.getViewId() );
				if( view == null ){
					check = false;
					Exception exception = new ExceptionViewNotExists( wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据ID查询列表视图信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){
			//전달된 목록의 formId와 분류의 formId가 일치하는지 비교, 다르면 정상 조회 불가
			if( !category.getFormId().equals( view.getFormId() ) && !category.getReadFormId().equals( view.getFormId() ) ) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess( "分类和列表使用的表单不一致，无法正确查询数据。" );
				result.error( exception );
			}
		}

		if( check ){
			try{
				//현재 Category와 View의 연결 존재 여부 검사
				viewIdsForCategory = viewServiceAdv.listViewIdsWithCategoryId( wi.getCategoryId() );
				//분류와 목록 정보의 연결 존재 여부 판단, 없으면 접근 불가
				if( ListTools.isEmpty( viewIdsForCategory ) || !viewIdsForCategory.contains( wi.getViewId() )) {
					check = false;
					Exception exception = new ExceptionViewAccessDenied( wi.getCategoryId(),  wi.getViewId() );
					result.error( exception );
				}
			}catch( Exception e ){
				check = false;
				Exception exception = new ExceptionViewInfoProcess( e, "系统在根据分类ID查询列表视图ID信息时发生异常。" );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if( check ){//조회 조건 기본값 설정, 전달되지 않으면 목록 설정값 사용
			if( StringUtils.isEmpty( wi.getOrderField() ) ) {
				wi.setOrderField(view.getOrderField());
			}
			if( StringUtils.isEmpty( wi.getOrderType() ) ) {
				wi.setOrderType(view.getOrderType());
			}
			if( StringUtils.isEmpty( wi.getDocStatus() ) ) {
				wi.setDocStatus( "published" );
			}
		}

		if( check ) {
			//Document 테이블의 열을 List로 보관해 두고, 정렬 열 또는 표시 열이 문서 내부 속성인지 판단
			sortableFieldNames = Arrays.asList( Document.documentFieldNames );
			//조회 시 정렬 열에 CREATETIME처럼 대문자 문자열을 쓰는 경우가 있어 createTime이 아니므로 오류를 허용해야 함
			sortableFieldNames_upperCase = new ArrayList<>();
			for( String fieldName : sortableFieldNames ){
				sortableFieldNames_upperCase.add( fieldName.toUpperCase() );
			}

			List<ViewFieldConfig>  fieldConfigs = viewServiceAdv.listFieldConfigByView( view.getId() );
			if( ListTools.isNotEmpty( fieldConfigs )) {
				for( ViewFieldConfig config : fieldConfigs ) {
					//sortableFieldNames는 Document가 직접 가진 속성명으로, 바로 정렬할 수 있는 열
				    if( !sortableFieldNames.contains(config.getFieldName())) {
						needData = true; //목록 표시 열에 업무 데이터 열이 있으면 모든 업무 데이터를 조회해야 함.
						break;
					}
				}
			}
		}

		if (check) {
			try {
				queryFilter = wi.getQueryFilter();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionViewInfoProcess(e, "系统在获取查询条件信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( check ) {
			if( StringUtils.isNotEmpty( lastDocId )) {
				document = documentQueryService.get( lastDocId );
			}

			//정렬 열이 Document의 일반 열이 아니면 Item에서 조건에 맞는 DocumentID 목록을 조회해야 함
			//일반 Document 속성 열과 전체 대문자 속성 열 모두에서 판단해야 함, sortableFieldNames and sortableFieldNames_upperCase
			//sortableFieldNames and sortableFieldNames_upperCase 모두에 없으면 업무 속성 데이터이므로 item에서 조회해야 함
			if( !sortableFieldNames.contains(wi.getOrderField()) && !sortableFieldNames_upperCase.contains( wi.getOrderField() ) ) {
				//해당 분류에서 볼 수 있는 모든 DocId 조회
				List<String> viewableDocList = null;
				if( isManager ) {
					documentCount = documentQueryService.countWithConditionOutofPermission( queryFilter );
					viewableDocList = documentQueryService.listIdsByCategoryId( category.getId(), wi.getOrderField(), wi.getOrderType(), 2000 );
				}else {
					documentCount = documentQueryService.countWithConditionInReview( personName, queryFilter );
					viewableDocList = documentQueryService.listDocIdsWithConditionInReview( personName, wi.getOrderField(), wi.getOrderType(), queryFilter, 2000 );
				}
				logger.debug("documentCount:{}===viewableDocListCount:{}",documentCount,viewableDocList.size());
				//볼 수 있는 모든 DocId를 기준으로 Item에서 정렬된 객체 2000개를 조회해 dataObjList ( docId, sortFieldValue )로 만들어 반환
				List<SimpleItemObj> simpleItems = documentQueryService.listSortObjWithOrderFieldInData( viewableDocList, wi.getOrderField(), wi.getOrderField(), wi.getOrderType() );

				if( ListTools.isNotEmpty( simpleItems )) {
					String sequence = null;
					Boolean  pickDocument = false;
					searchResultList = new ArrayList<>();
					for( SimpleItemObj item : simpleItems ) {
						//Document 데이터 채우기
						sequence = documentQueryService.getSequence( item.getId() );
						if( StringUtils.isNotEmpty( sequence)) {
							if( document != null ) { //다음 페이지 조회 필요
								if( sequence.equalsIgnoreCase( document.getSequence() )) {
									pickDocument = true;//정식 출력 목록에 pageSize개 document 추가 시작
								}
							}else {
								pickDocument = true;//1페이지의 pageSize개 document 가져오기
							}
							if( pickDocument ) {
								if( searchResultList.size() < pageSize ) {
									searchResultList.add( documentQueryService.get( item.getId() ));
								}else {//데이터가 한 페이지만큼 채워짐
									break;
								}
							}
						}
					}
				}
			}else {
				//sortableFieldNames or sortableFieldNames_upperCase에 있으면 문서 데이터로만 정렬
				if( isManager ) {
					documentCount = documentQueryService.countWithConditionOutofPermission( queryFilter );
					if( Document.isFieldInSequence(wi.getOrderField()) ) {
						//Document에서 권한을 무시하고 직접 조회
						searchResultList = documentQueryService.listNextWithConditionOutofPermission( lastDocId, pageSize, wi.getOrderField(), wi.getOrderType(), queryFilter );
					}else {//순번이 있는 필드면 순번으로 페이지 처리, 아니면 2000건을 바로 조회해 메모리 정렬
						documentList = documentQueryService.listNextWithConditionOutofPermission( wi.getOrderField(), wi.getOrderType(), queryFilter, 2000 );
						//반복 페이지 처리, 전달된 ID의 위치를 찾아 이후 N건 조회
						if( ListTools.isNotEmpty( documentList )) {
							Boolean add2List = false;
							//한 페이지를 searchResultList에 넣어 반환
							for( Document doc : documentList ) {
								if( StringUtils.isEmpty( lastDocId ) || doc.getId().equalsIgnoreCase( lastDocId ) ) {
									add2List = true;
								}
								if( add2List ) {
									searchResultList.add( doc );
								}
								if( searchResultList.size() >= pageSize ) {
									break;
								}
							}
						}
					}
				}else {
					documentCount = documentQueryService.countWithConditionInReview( personName, queryFilter );
					if( Document.isFieldInSequence(wi.getOrderField()) ) {//순번이 있는 필드면 순번으로 페이지 처리, 아니면 2000건을 바로 조회해 메모리 정렬
						// Review 테이블에서 조건에 맞는 객체를 조회해 Document 객체 목록으로 변환
						searchResultList = documentQueryService.listNextWithConditionInReview( lastDocId, pageSize, wi.getOrderField(), wi.getOrderType(), personName, queryFilter );
					}else {
						reviewList =  documentQueryService.listNextWithConditionInReview( wi.getOrderField(), wi.getOrderType(), personName, queryFilter, 2000 );
						//반복 페이지 처리, 전달된 ID의 위치를 찾아 이후 N건 조회, Document로 변환해 searchResultList에 넣음
						if( ListTools.isNotEmpty( reviewList )) {
							Boolean add2List = false;
							List<String> docIds = new ArrayList<>();
							//한 페이지를 searchResultList에 넣어 반환
							for( Review review : reviewList ) {
								if( StringUtils.isEmpty( lastDocId ) || review.getDocId().equalsIgnoreCase( lastDocId ) ) {
									add2List = true;
								}
								if( add2List ) {
									if( !docIds.contains( review.getDocId() )) {
										docIds.add( review.getDocId() );
										searchResultList.add( documentQueryService.get(  review.getDocId()  ));
										if( searchResultList.size() >= pageSize ) {
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if( check ){
			//업무 데이터 보충
			if( searchResultList != null && searchResultList.size() > 0 ){
				for( Document _document : searchResultList ){
					wo = new Wo();
					try {
						woDocument = WoDocument.copier.copy( _document );
						if( woDocument != null ) {
							if( woDocument.getCreatorPerson() != null && !woDocument.getCreatorPerson().isEmpty() ) {
								woDocument.setCreatorPersonShort( woDocument.getCreatorPerson().split( "@" )[0]);
							}
							if( woDocument.getCreatorUnitName() != null && !woDocument.getCreatorUnitName().isEmpty() ) {
								woDocument.setCreatorUnitNameShort( woDocument.getCreatorUnitName().split( "@" )[0]);
							}
							if( woDocument.getCreatorTopUnitName() != null && !woDocument.getCreatorTopUnitName().isEmpty() ) {
								woDocument.setCreatorTopUnitNameShort( woDocument.getCreatorTopUnitName().split( "@" )[0]);
							}
						}
						wo.setDocument( woDocument );
					} catch (Exception e) {
						Exception exception = new ExceptionViewInfoProcess( e, "系统在将文档信息转换为可输出的数据信息时发生异常。" );
						result.error( exception );
						logger.error( e, effectivePerson, request, null);
					}
					//목록 표시 열에 업무 데이터가 필요 없으면 조회하지 않음
					if( needData ) { //Document에 없는 열이 있어 Data를 추가 조립해야 함
						try {
							wo.setData( documentQueryService.getDocumentData( _document ) );
						} catch (Exception e) {
							Exception exception = new ExceptionViewInfoProcess( e, "系统在根据文档信息获取文档数据信息时发生异常。" );
							result.error( exception );
							logger.error( e, effectivePerson, request, null);
						}
					}
					wos.add( wo );
				}
			}
			result.setCount( documentCount );
			result.setData( wos );
			result.setCount( documentCount );
		}

		return result;
	}

	public class Wi {

		@FieldDescribe( "상단 고정 여부: ALL|TOP|UNTOP." )
		private String topFlag = "ALL";

		@FieldDescribe("필터링할 문서의 분류 ID")
		private String categoryId;

		@FieldDescribe("조회할 목록 ID")
		private String viewId;

		@FieldDescribe("현재 조회의 정렬 열 이름, 기본은 목록 설정 기준")
		private String orderField;

		@FieldDescribe("현재 정렬 방식: DESC|ASC, 기본은 목록 설정 기준")
		private String orderType;

		@FieldDescribe("값 유형: string | date | time | datetime | boolean | boolean | text 지원")
		private String fieldType;

		@FieldDescribe("查询文档类型: 全部 | 信息 | 数据 ")
		private String documentType;

		@FieldDescribe("조회할 문서 상태: published | draft | archived")
		private String docStatus;

		public String getFieldType() {
			return fieldType;
		}

		public void setFieldType(String fieldType) {
			this.fieldType = fieldType;
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

		public String getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(String categoryId) {
			this.categoryId = categoryId;
		}

		public String getViewId() {
			return viewId;
		}

		public void setViewId(String viewId) {
			this.viewId = viewId;
		}

		public String getDocStatus() {
			return docStatus;
		}

		public void setDocStatus(String docStatus) {
			this.docStatus = docStatus;
		}

		public String getDocumentType() {
			return documentType;
		}

		public void setDocumentType(String documentType) {
			this.documentType = documentType;
		}

		public String getTopFlag() {
			return topFlag;
		}

		public void setTopFlag(String topFlag) {
			this.topFlag = topFlag;
		}

		/**
		 * 전달된 조회 파라미터로 완전한 QueryFilter 객체 구성
		 * @return
		 * @throws Exception
		 */
		public QueryFilter getQueryFilter() throws Exception {
			QueryFilter queryFilter = new QueryFilter();
			queryFilter.setJoinType( "and" );

			if( StringUtils.isNotEmpty( this.getCategoryId() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "categoryId", this.getCategoryId() ) );
			}

			//문서 유형: 전체 | 정보 | 데이터
			if( StringUtils.isNotEmpty( this.getDocumentType())) {
				if( "信息".equals( this.getDocumentType() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
				}else if( "数据".equals( this.getDocumentType() )) {
					queryFilter.addEqualsTerm( new EqualsTerm( "documentType", this.getDocumentType() ) );
				}
			}

			//상단 고정 여부: ALL|TOP|UNTOP
			if( StringUtils.isNotEmpty( this.getTopFlag())) {
				if( "TOP".equals( this.getTopFlag() )) {
					queryFilter.addIsTrueTerm( new IsTrueTerm( "isTop" ) );
				}else if( "UNTOP".equals( this.getDocumentType() )) {
					queryFilter.addIsFalseTerm( new IsFalseTerm( "isTop" ) );
				}
			}

			if( StringUtils.isNotEmpty( this.getDocStatus())) {
				queryFilter.addEqualsTerm( new EqualsTerm( "docStatus", this.getDocStatus() ) );
			}

			return queryFilter;
		}

	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe( "문서 객체 순번." )
		private String sequence = null;

		@FieldDescribe( "문서 객체 ID." )
		private String id = null;

		@FieldDescribe( "정렬 열 값." )
		private String orderFieldValue = null;

		@FieldDescribe( "作为输出的CMS文档数据对象." )
		private WoDocument document;

		@FieldDescribe( "文档所有数据信息." )
		private Map<?, ?> data;

		public WoDocument getDocument() {
			return document;
		}

		public void setDocument( WoDocument document) {
			this.document = document;
		}

		public Map<?, ?> getData() {
			return data;
		}

		public void setData(Map<?, ?> data) {
			this.data = data;
		}

		public String getSequence() {
			return sequence;
		}

		public void setSequence(String sequence) {
			this.sequence = sequence;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrderFieldValue() {
			return orderFieldValue;
		}

		public void setOrderFieldValue(String orderFieldValue) {
			this.orderFieldValue = orderFieldValue;
		}
	}

	public static class WoDocument extends Document {

		private static final long serialVersionUID = -5076990764713538973L;

		public static final WrapCopier<Document, WoDocument> copier = WrapCopierFactory.wo( Document.class, WoDocument.class, null, JpaObject.FieldsInvisible);

		@FieldDescribe("작성자 이름(약칭)")
		private String creatorPersonShort = null;

		@FieldDescribe("작성자 소속 조직(약칭)")
		private String creatorUnitNameShort = null;

		@FieldDescribe("작성자 최상위 조직(약칭)")
		private String creatorTopUnitNameShort = null;

		public String getCreatorPersonShort() {
			return creatorPersonShort;
		}

		public String getCreatorUnitNameShort() {
			return creatorUnitNameShort;
		}

		public String getCreatorTopUnitNameShort() {
			return creatorTopUnitNameShort;
		}

		public void setCreatorPersonShort(String creatorPersonShort) {
			this.creatorPersonShort = creatorPersonShort;
		}

		public void setCreatorUnitNameShort(String creatorUnitNameShort) {
			this.creatorUnitNameShort = creatorUnitNameShort;
		}

		public void setCreatorTopUnitNameShort(String creatorTopUnitNameShort) {
			this.creatorTopUnitNameShort = creatorTopUnitNameShort;
		}
	}
}
