package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSUserRole;
import com.x.bbs.entity.BBSUserRole_;

/**
 * 클래스명: BBSUserRoleFactory<br/>
 * 엔티티 클래스: BBSUserRole<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-08-10 17:17:26
**/
public class BBSUserRoleFactory extends AbstractFactory {

	public BBSUserRoleFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSUserRole 엔티티 정보 객체 조회" )
	public BBSUserRole get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSUserRole.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSUserRole 엔티티 정보 목록 조회" )
	public List<BBSUserRole> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSUserRole>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserRole> cq = cb.createQuery(BBSUserRole.class);
		Root<BBSUserRole> root = cq.from(BBSUserRole.class);
		Predicate p = root.get(BBSUserRole_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "전체 BBSUserRole 엔티티 정보 목록 조회" )
	public List<BBSUserRole> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSUserRole.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserRole> cq = cb.createQuery(BBSUserRole.class);
		return em.createQuery( cq ).setMaxResults( 5000 ).getResultList();
	}

	//@MethodDescribe( "연결 대상 고유 식별자와 연결 대상 유형으로 연결된 전체 역할 ID 목록 조회" )
	public List<String> listRoleIdsByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null || uniqueId.isEmpty() ){
			throw new Exception("uniqueId is null!");
		}
		if( objectType == null || objectType.isEmpty() ){
			throw new Exception("objectType is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.uniqueId ), uniqueId );
		p = cb.and( p, cb.equal( root.get( BBSUserRole_.objectType ), objectType ));
		cq.select( root.get( BBSUserRole_.roleId ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	//@MethodDescribe( "연결 대상 고유 식별자와 연결 대상 유형으로 연결된 전체 역할 코드 목록 조회" )
	public List<String> listRoleCodeByObjectUniqueId( String uniqueId, String objectType ) throws Exception {
		if( uniqueId == null || uniqueId.isEmpty() ){
			throw new Exception("uniqueId is null!");
		}
		if( objectType == null || objectType.isEmpty() ){
			throw new Exception("objectType is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.uniqueId ), uniqueId );
		p = cb.and( p, cb.equal( root.get( BBSUserRole_.objectType ), objectType ));
		cq.select( root.get( BBSUserRole_.roleCode ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "역할 코드로 전체 사용자-역할 연결 정보 객체 ID 목록 조회" )
	public List<String> listIdsByRoleCode( String roleCode ) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.roleCode ), roleCode );
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	//@MethodDescribe( "지정 연결 대상 고유 식별자 목록으로 해당 대상에 연결된 전체 역할 ID 목록 조회" )
	public List<String> listRoleIdsByObjectUnique(List<String> objectUniqueIds) throws Exception {
		if( objectUniqueIds == null || objectUniqueIds.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = root.get( BBSUserRole_.uniqueId ).in( objectUniqueIds );
		cq.select( root.get( BBSUserRole_.roleId ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	//@MethodDescribe( "지정 연결 대상 고유 식별자 목록으로 해당 대상에 연결된 전체 역할 코드 목록 조회" )
	public List<String> listRoleCodesByObjectUnique( List<String> objectUniqueIds ) throws Exception {
		if( objectUniqueIds == null || objectUniqueIds.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = root.get( BBSUserRole_.uniqueId ).in( objectUniqueIds );
		cq.select( root.get( BBSUserRole_.roleCode ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listByForumId(String forumId) throws Exception {
		if( StringUtils.isEmpty( forumId ) ){
			throw new Exception("forumId is empty!!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.forumId ), forumId );
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 게시판 정보 ID로 전체 사용자-역할 연결 정보 조회
	 * @param sectionId
	 * @param queryMainSectionId
	 * @return
	 * @throws Exception 
	 */
	public List<String> listBySectionId(String sectionId, Boolean queryMainSectionId) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception("sectionId is empty!!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSUserRole.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSUserRole> root = cq.from( BBSUserRole.class );
		Predicate p = cb.equal( root.get( BBSUserRole_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get( BBSUserRole_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSUserRole_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
}
