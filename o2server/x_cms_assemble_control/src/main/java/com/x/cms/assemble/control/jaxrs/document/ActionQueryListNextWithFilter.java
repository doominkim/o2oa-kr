package com.x.cms.assemble.control.jaxrs.document;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.Review;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.InTerm;
import com.x.cms.core.express.tools.filter.term.NotInTerm;

public class ActionQueryListNextWithFilter extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionQueryListNextWithFilter.class);

	protected ActionResult<List<Wo>> execute( HttpServletRequest request, String id, Integer count, JsonElement jsonElement, EffectivePerson effectivePerson ) {
		ActionResult<List<Wo>> result = new ActionResult<>();
		Long total = 0L;
		Wi wi = null;
		List<Wo> wos = new ArrayList<>();
		List<Document> documentList = null;
		List<Document> searchResultList = new ArrayList<>();
		List<Review> reviewList =  null;
		Boolean check = true;
		Boolean isManager = false;
		String personName = effectivePerson.getDistinguishedName();
		QueryFilter queryFilter = null;

		if ( count == 0 ) { count = 20; }
		if ( StringUtils.isEmpty( id ) || "(0)".equals( id ) ) { id = null; }

		try {
			wi = this.convertToWrapIn( jsonElement, Wi.class );
		} catch (Exception e ) {
			check = false;
			Exception exception = new ExceptionDocumentInfoProcess( e, "JSON 정보를 객체로 변환하는 중 오류가 발생했습니다. JSON:" + jsonElement.toString() );
			result.error( exception );
			logger.error( e, effectivePerson, request, null);
		}
		if ( wi == null ) { wi = new Wi(); }

		if( StringUtils.isEmpty( wi.getDocumentType() )) {
			wi.setDocumentType( "정보" );
		}

		if( StringUtils.isEmpty( wi.getOrderField() )) {
			wi.setOrderField( "createTime" );
		}

		if( StringUtils.isEmpty( wi.getOrderType() )) {
			wi.setOrderType( "DESC" );
		}

		if( ListTools.isEmpty( wi.getStatusList() )) {
			List<String> status = new ArrayList<>();
			status.add( "published" );
			wi.setStatusList( status );
		}

		if (check) {
			try {
				queryFilter = wi.getQueryFilter();
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "조회 조건 정보를 가져오는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( check ) {
			try {
				if( effectivePerson.isManager() || userManagerService.isHasPlatformRole( effectivePerson.getDistinguishedName(), "CMSManager" )) {
					isManager = true;
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 관리자인지 확인하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if( check ) {
			//열람 여부 조회, 해당 ID로 IN 연산을 해야 해서 효율이 다소 낮음
			List<String> readDocIds = null;
			if( "READ".equalsIgnoreCase( wi.getReadFlag() )) { //열람한 것만 조회
				//해당 사용자가 열람한 모든 문서ID 목록 조회
				try {
					readDocIds = documentViewRecordServiceAdv.listDocIdsByPerson( personName, 2000 );
					if( ListTools.isEmpty( readDocIds )) {
						readDocIds = new ArrayList<>();
						readDocIds.add( "no Document readed" );
					}
					if( isManager ) {
						queryFilter.addInTerm( new InTerm( "id", new ArrayList<>( readDocIds ) ) );
					}else {
						queryFilter.addInTerm( new InTerm( "docId", new ArrayList<>( readDocIds ) ) );
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 읽은 게시글 ID 목록을 조회하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}else if("UNREAD".equalsIgnoreCase( wi.getReadFlag() )) { //열람하지 않은 것만 조회
				//해당 사용자가 열람한 모든 문서ID 목록 조회
				try {
					readDocIds = documentViewRecordServiceAdv.listDocIdsByPerson( personName, 2000 );
					if( ListTools.isNotEmpty( readDocIds )) {
						if( isManager ) {
							queryFilter.addNotInTerm( new NotInTerm( "id", new ArrayList<>( readDocIds ) ) );
						}else {
							queryFilter.addNotInTerm( new NotInTerm( "docId", new ArrayList<>( readDocIds ) ) );
						}
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 읽은 게시글 ID 목록을 조회하는 중 오류가 발생했습니다.");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		if (check) { // Review 테이블에서 조건에 맞는 객체 총수 조회
			try {
				if( isManager ) { //Document에서 권한을 무시하고 직접 조회
					total = documentQueryService.countWithConditionOutofPermission( queryFilter );
				}else {
					total = documentQueryService.countWithConditionInReview( personName, queryFilter );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 조회할 수 있는 게시글 수를 가져오는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			//document와 Review는 sequence 외에 title, appAlias, categoryAlias, categoryName, creatorUnitName 5개 정렬 열의 페이지 조회를 지원
			//sequence와 title, appAlias, categoryAlias, categoryName, creatorUnitName 외의 다른 열 정렬은 모두 메모리에서 페이지 처리
			try {
				if( isManager ) {
					//정렬 열이 Document 테이블에 sequence 인덱스 열로 되어 있는지 판단, 그렇다면 document 테이블에서만 조회하면 됨.
					if( Document.isFieldInSequence( wi.getOrderField()) ) { //Document에서 권한을 무시하고 직접 조회
						searchResultList = documentQueryService.listNextWithConditionOutofPermission( id, count, wi.getOrderField(), wi.getOrderType(), queryFilter );
					}else {
						//사용자 권한으로 문서 2000건의 전체 정보를 조회한 뒤 특정 속성으로 정렬하고 메모리에서 페이지 처리
						documentList = documentQueryService.listNextWithConditionOutofPermission( wi.getOrderField(), wi.getOrderType(), queryFilter, 2000 );
						//반복 페이지 처리, 전달된 ID의 위치를 찾아 이후 N건 조회
						if( ListTools.isNotEmpty( documentList )) {
							Boolean add2List = false;
							//한 페이지를 searchResultList에 넣어 반환
							for( Document document : documentList ) {
								if( StringUtils.isEmpty( id ) || document.getId().equalsIgnoreCase( id ) ) {
									add2List = true;
								}
								if( add2List ) { searchResultList.add( document ); }
								if( searchResultList.size() >= count ) { break; }
							}
						}
					}
				}else {
					if( Document.isFieldInSequence(wi.getOrderField()) ) { // Review 테이블에서 조건에 맞는 객체를 조회해 Document 객체 목록으로 변환
						searchResultList = documentQueryService.listNextWithConditionInReview( id, count, wi.getOrderField(), wi.getOrderType(), personName, queryFilter );
					}else {
						reviewList =  documentQueryService.listNextWithConditionInReview( wi.getOrderField(), wi.getOrderType(), personName, queryFilter, 2000 );
						//반복 페이지 처리, 전달된 ID의 위치를 찾아 이후 N건 조회, Document로 변환해 searchResultList에 넣음
						searchResultList = new ArrayList<>();
						if( ListTools.isNotEmpty( reviewList )) {
							Boolean add2List = false;
							//한 페이지를 searchResultList에 넣어 반환
							for( Review review : reviewList ) {
								if( StringUtils.isEmpty( id ) || review.getDocId().equalsIgnoreCase( id ) ) {
									add2List = true;
								}
								if( add2List ) {
									searchResultList.add(documentQueryService.get( review.getDocId() ) );
								}
								if( searchResultList.size() >= count ) {
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionDocumentInfoProcess(e, "사용자가 접근할 수 있는 게시글 ID 목록으로 페이지 조회하는 중 오류가 발생했습니다.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		if (check) {
			if ( searchResultList != null ) {
				Wo wo = null;
				for( Document document : searchResultList ) {
					if( document != null ){
						try {
							wo = Wo.copier.copy( document );
							if( wo.getCreatorPerson() != null && !wo.getCreatorPerson().isEmpty() ) {
								wo.setCreatorPersonShort( wo.getCreatorPerson().split( "@" )[0]);
							}
							if( wo.getCreatorUnitName() != null && !wo.getCreatorUnitName().isEmpty() ) {
								wo.setCreatorUnitNameShort( wo.getCreatorUnitName().split( "@" )[0]);
							}
							if( wo.getCreatorTopUnitName() != null && !wo.getCreatorTopUnitName().isEmpty() ) {
								wo.setCreatorTopUnitNameShort( wo.getCreatorTopUnitName().split( "@" )[0]);
							}
							if( wi.getNeedData() ) {
								//데이터 조립 필요
								wo.setData( documentQueryService.getDocumentData( document ) );
							}
						} catch (Exception e) {
							check = false;
							Exception exception = new ExceptionDocumentInfoProcess(e, "문서 데이터 내용을 가져오는 중 오류가 발생했습니다. Id:" + document.getCategoryId());
							result.error(exception);
							logger.error(e, effectivePerson, request, null);
						}
						wos.add( wo );
					}
				}
			}
		}
		result.setCount(total);
		result.setData(wos);
		return result;
	}

	public class DocumentCacheForFilter {

		private Long total = 0L;
		private List<Wo> documentList = null;

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public List<Wo> getDocumentList() {
			return documentList;
		}

		public void setDocumentList(List<Wo> documentList) {
			this.documentList = documentList;
		}
	}

	public static class Wi extends WrapInDocumentFilter{

	}

	public static class Wo extends WrapOutDocumentList {

		public static List<String> excludes = new ArrayList<String>();

		public static final WrapCopier<Document, Wo> copier = WrapCopierFactory.wo( Document.class, Wo.class, null,JpaObject.FieldsInvisible);

	}
}
