package com.x.cms.core.express.tools;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject_;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.core.entity.Document;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.DateBetweenTerm;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.InTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;
import com.x.cms.core.express.tools.filter.term.LikeTerm;
import com.x.cms.core.express.tools.filter.term.MemberTerm;
import com.x.cms.core.express.tools.filter.term.NotEqualsTerm;
import com.x.cms.core.express.tools.filter.term.NotInTerm;
import com.x.cms.core.express.tools.filter.term.NotMemberTerm;


public class CriteriaBuilderTools {
	
	public static Predicate predicate_or( CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target ) {
		if( predicate == null ) {
			return predicate_target;	
		}else {
			if( predicate_target != null ) {
				return criteriaBuilder.or( predicate, predicate_target );
			}else {
				return predicate;	
			}
		}
	}
	
	public static Predicate predicate_and( CriteriaBuilder criteriaBuilder, Predicate predicate, Predicate predicate_target ) {
		if( predicate == null ) {
			return predicate_target;	
		}else {
			if( predicate_target != null ) {
				return criteriaBuilder.and( predicate, predicate_target );	
			}else {
				return predicate;	
			}
		}
	}
	
	/**
	 * 필터 조건에 따라 조회문 구성
	 * @param cls  -조회 대상 엔티티 클래스명
	 * @param cls_  -조회 대상 엔티티 클래스에 대응하는 StaticMetamodel
	 * @param em  -EntityManager
	 * @param queryFilter  -조회 필터 조건
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> Predicate composePredicateWithQueryFilter( 
			Class<T> cls, Class<T_> cls_, EntityManager em,  QueryFilter queryFilter ) throws NoSuchFieldException, SecurityException {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery( cls );
		Root<T> root = cq.from( cls );
		Predicate p = null;
		return composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter ) ;
	}
	
	/**
	 * 필터 조건에 따라 조회문 구성
	 * @param cls_  -조회 대상 엔티티 클래스에 대응하는 StaticMetamodel
	 * @param cb  -CriteriaBuilder
	 * @param p  -Predicate
	 * @param root  -조회 루트
	 * @param queryFilter  -조회 필터 조건
	 * @return
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> Predicate composePredicateWithQueryFilter( 
			Class<T_> cls_, CriteriaBuilder cb, Predicate p, Root<T> root,  QueryFilter queryFilter ) throws NoSuchFieldException, SecurityException {
		if( queryFilter == null ) {
			queryFilter = new QueryFilter();
		}
		//조건 조립
		if( ListTools.isNotEmpty( queryFilter.getEqualsTerms() )) {
			for( EqualsTerm term : queryFilter.getEqualsTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.equal( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getNotEqualsTerms() )) {
			for( NotEqualsTerm term : queryFilter.getNotEqualsTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.notEqual( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.notEqual( root.get( cls_.getDeclaredField( term.getName() ).getName() ), term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getInTerms() )) {
			for( InTerm term : queryFilter.getInTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || ListTools.isEmpty( term.getValue())) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ) );
				}
			}
		}
		if( ListTools.isNotEmpty( queryFilter.getNotInTerms() )) {
			for( NotInTerm term : queryFilter.getNotInTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || ListTools.isEmpty( term.getValue())) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ).not());
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, root.get( cls_.getDeclaredField( term.getName() ).getName() ).in( term.getValue() ).not() );
				}
			}
		}		
		if( ListTools.isNotEmpty( queryFilter.getMemberTerms() )) {
			for( MemberTerm term : queryFilter.getMemberTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ) );
				}
			}
		}		
		if( ListTools.isNotEmpty( queryFilter.getNotMemberTerms() )) {
			for( NotMemberTerm term : queryFilter.getNotMemberTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
						p = CriteriaBuilderTools.predicate_and( cb, p, cb.isNotMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isNotMember( term.getValue(), root.get( cls_.getDeclaredField( term.getName() ).getName() ) ) );
				}
			}
		}
				
		if( ListTools.isNotEmpty( queryFilter.getLikeTerms())) {
			for( LikeTerm term : queryFilter.getLikeTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || StringUtils.isEmpty( term.getValue().toString() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.like( root.get( cls_.getDeclaredField( term.getName() ).getName() ), "%"+term.getValue()+"%" ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.like( root.get( cls_.getDeclaredField( term.getName() ).getName() ), "%"+term.getValue()+"%" ));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getIsTrueTerms())) {
			for( IsTrueTerm term : queryFilter.getIsTrueTerms() ) {
				if( StringUtils.isEmpty( term.getName() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isTrue( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isTrue( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getIsFalseTerms())) {
			for( IsFalseTerm term : queryFilter.getIsFalseTerms() ) {
				if( StringUtils.isEmpty( term.getName() )) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.isFalse( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.isFalse( root.get( cls_.getDeclaredField( term.getName() ).getName() )));
				}
			}
		}
		
		if( ListTools.isNotEmpty( queryFilter.getDateBetweenTerms())) {
			for( DateBetweenTerm term : queryFilter.getDateBetweenTerms() ) {
				if( StringUtils.isEmpty( term.getName() ) || term.getValue() == null || ListTools.isEmpty( term.getValue() ) || term.getValue().size() < 2) {
					continue;
				}
				if( "and".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_and( cb, p, cb.between( root.get(term.getName()), term.getValue().get(0), term.getValue().get(1) ));
				} else if( "or".equalsIgnoreCase( queryFilter.getJoinType() )) {
					p = CriteriaBuilderTools.predicate_or( cb, p, cb.between( root.get(term.getName()), term.getValue().get(0), term.getValue().get(1) ));
				}
			}
		}
		
		//조회 조건 재귀 계속
		if( queryFilter.getAnd() != null  ) {
			queryFilter.setJoinType( "and" );
			composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter.getAnd() ) ;
		}
		
		if( queryFilter.getOr() != null  ) {
			queryFilter.setJoinType( "or" );
			composePredicateWithQueryFilter( cls_, cb, p, root,  queryFilter.getOr() ) ;
		}
		
		return p;
	}
	
	/**
	 * 공통 페이지 조회
	 * @param em  -EntityManager
	 * @param cls  -조회 대상 엔티티 클래스명
	 * @param cls_  -조회 대상 엔티티 클래스에 대응하는 StaticMetamodel
	 * @param maxCount  -출력 최대 항목 수
	 * @param queryFilter  -조회 필터 조건
	 * @param sequenceFieldValue  -이전 항목의 시퀀스 값
	 * @param orderField  -정렬 열 이름, 기본 createTime
	 * @param order  -정렬 방식, 기본 DESC
	 * @return
	 * @throws Exception
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_> List<T> listNextWithCondition(  EntityManager em, Class<T> cls, Class<T_> cls_, Integer maxCount, 
			QueryFilter queryFilter, Object sequenceFieldValue,  String orderField, String order ) throws Exception {
		
		if( StringUtils.isEmpty( order ) ){
			order = "DESC";
		}
		if( StringUtils.isEmpty( orderField )){
			orderField = "createTime";
		}
		if( maxCount == null || maxCount == 0 ) {
			maxCount = 20;
		}
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery( cls );
		Root<T> root = cq.from( cls );
		
		Predicate p = null;
		//시퀀스 기준 정렬
		if( sequenceFieldValue != null ){
			if( "DESC".equalsIgnoreCase( order )){
				p = predicate_and( cb, p, cb.lessThan( root.get( cls_.getDeclaredField( orderField ).getName() ), sequenceFieldValue.toString() ));
			}else{
				p = predicate_and( cb, p, cb.greaterThan( root.get( cls_.getDeclaredField( orderField ).getName() ), sequenceFieldValue.toString() ));
			}
		}
		
		//필터 조건으로 조회문 구성
		p = CriteriaBuilderTools.composePredicateWithQueryFilter( cls_, cb, p, root, queryFilter );
		
		//요구에 따라 조회 결과 정렬
		if( "DESC".equalsIgnoreCase( order )){
			cq.orderBy( cb.desc( root.get( cls_.getDeclaredField( orderField ).getName() )));
		}else{
			cq.orderBy( cb.asc( root.get( cls_.getDeclaredField( orderField ).getName() )) );
		}
		
		return em.createQuery(cq.where(p).distinct(true)).setMaxResults( maxCount ).getResultList();
	}
	
	/**
	 * 조건에 따라 정렬문 구성
	 * @param <T>
	 * @param <T_>
	 * @param cb
	 * @param root
	 * @param cls_
	 * @param fieldName
	 * @param orderType
	 * @return
	 */
	public static <T extends JpaObject, T_ extends SliceJpaObject_>Order getOrder( CriteriaBuilder cb, Root<T> root, Class<T_> cls_, String fieldName, String orderType ) {
		if( StringUtils.isEmpty( fieldName )) {
			fieldName = Document.sequence_FIELDNAME;
		}
		
		Boolean fieldExists = false;
		if( Document.sequence_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.sequence_FIELDNAME;
		}else if( Document.createTime_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.createTime_FIELDNAME;
		}else if( Document.updateTime_FIELDNAME.equalsIgnoreCase( fieldName )) {
			fieldExists = true;
			fieldName = Document.updateTime_FIELDNAME;
		}else {
			Field[] fields = cls_.getDeclaredFields();
			for( Field field : fields ) {
				if( field.getName().equalsIgnoreCase( fieldName ) ) {
					fieldName = field.getName(); //대소문자 영향을 피하도록 정렬 열 이름 보정
					fieldExists = true;
				}
			}
		}
		if( !fieldExists ) { //정렬 열이 없으면 sequence를 바로 사용해 SQL이 정상 실행되도록 함
			fieldName = Document.sequence_FIELDNAME;
		}
		if( StringUtils.isEmpty( orderType )) {
			orderType = "desc";
		}
		if( "desc".equalsIgnoreCase( orderType )) {
			return cb.desc( root.get( fieldName ).as(String.class));
		}else {
			return cb.asc( root.get( fieldName ).as(String.class));
		}
	}
}
