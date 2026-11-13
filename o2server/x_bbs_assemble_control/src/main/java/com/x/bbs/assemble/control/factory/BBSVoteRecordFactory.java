package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSVoteRecord;
import com.x.bbs.entity.BBSVoteRecord_;

/**
 * 클래스명: BBSVoteRecordFactory<br/>
 * 엔티티 클래스: BBSVoteRecord<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
**/
public class BBSVoteRecordFactory extends AbstractFactory {

	public BBSVoteRecordFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSVoteRecord 엔티티 정보 객체 조회" )
	public BBSVoteRecord get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSVoteRecord.class, ExceptionWhen.none );
	}
	
//	@MethodDescribe( "지정 Id의 BBSVoteRecord 엔티티 정보 목록 조회" )
	public List<BBSVoteRecord> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSVoteRecord>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSVoteRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery(BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from(BBSVoteRecord.class);
		Predicate p = root.get(BBSVoteRecord_.id).in(ids);
		cq.orderBy( cb.desc( root.get( BBSVoteRecord_.sequence ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "주제 정보 ID로 전체 투표 기록 정보 ID 목록 조회" )
	public List<String> listVoteRecordBySubjectIds( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		cq.select(root.get( BBSVoteRecord_.id));
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "주제 정보 ID로 전체 투표 기록 정보 ID 목록 조회" )
	public List<BBSVoteRecord> listVoteRecordBySubject( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery( BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long getVoteCountByUserAndSubject(String personName, String subjectId) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.votorName ), personName );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId ) );
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long getVoteCountByUserAndOption(String personName, String optionId) throws Exception {
		if( personName == null || personName.isEmpty() ){
			throw new Exception( "personName is null!" );
		}
		if( optionId == null || optionId.isEmpty() ){
			throw new Exception( "subjectId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.votorName ), personName );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), optionId ) );
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long countVoteRecordForSubject( String subjectId, String voteOptionId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class);
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		if( StringUtils.isNotEmpty( voteOptionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), voteOptionId ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listVoteUserForSubject( String subjectId, String voteOptionId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class);
		Predicate p = cb.equal( root.get( BBSVoteRecord_.subjectId ), subjectId );
		if( StringUtils.isNotEmpty( voteOptionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), voteOptionId ));
		}
		cq.select( root.get( BBSVoteRecord_.votorName ) );
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public List<BBSVoteRecord> listVoteRecordForPage(String subjectId, String voteOptionId, Integer maxRecordCount) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSVoteRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery(BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from(BBSVoteRecord.class);
		Predicate p = cb.isNotNull( root.get( BBSVoteRecord_.id ) );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionId ), voteOptionId ));
		cq.orderBy( cb.desc( root.get( BBSVoteRecord_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	/**
	 * 사용자와 선택 항목 그룹으로 사용자가 선택한 전체 항목 목록 조회
	 * @param personName
	 * @param optionGroupId
	 * @return
	 * @throws Exception
	 */
	public List<BBSVoteRecord> listVoteCountByUserAndGroup(String personName, String optionGroupId) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			throw new Exception( "personName is empty!" );
		}
		if( StringUtils.isEmpty( optionGroupId ) ){
			throw new Exception( "optionGroupId is empty!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSVoteRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSVoteRecord> cq = cb.createQuery( BBSVoteRecord.class);
		Root<BBSVoteRecord> root = cq.from( BBSVoteRecord.class );
		Predicate p = cb.equal( root.get( BBSVoteRecord_.votorName ), personName );
		p = cb.and( p, cb.equal( root.get( BBSVoteRecord_.optionGroupId ), optionGroupId ));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
