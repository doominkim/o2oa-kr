package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSRoleInfo_;

/**
 * 클래스명: BBSRoleInfoFactory<br/>
 * 엔티티 클래스: BBSRoleInfo<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-08-10 17:17:26
**/
public class BBSRoleInfoFactory extends AbstractFactory {

	public BBSRoleInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSRoleInfo 엔티티 정보 객체 조회" )
	public BBSRoleInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSRoleInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSRoleInfo 엔티티 정보 목록 조회" )
	public List<BBSRoleInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSRoleInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSRoleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSRoleInfo> cq = cb.createQuery(BBSRoleInfo.class);
		Root<BBSRoleInfo> root = cq.from(BBSRoleInfo.class);
		Predicate p = root.get(BBSRoleInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "전체 BBSRoleInfo 엔티티 정보 목록 조회" )
	public List<BBSRoleInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSRoleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSRoleInfo> cq = cb.createQuery(BBSRoleInfo.class);
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}

	//@MethodDescribe( "역할 코드로 지정 BBSRoleInfo 엔티티 정보 목록 조회" )
	public BBSRoleInfo getRoleByCode( String roleCode ) throws Exception {
		if( roleCode == null || roleCode.isEmpty() ){
			throw new Exception("roleCode is null!");
		}
		List<BBSRoleInfo> roleInfoList = null;
		EntityManager em = this.entityManagerContainer().get( BBSRoleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSRoleInfo> cq = cb.createQuery( BBSRoleInfo.class );
		Root<BBSRoleInfo> root = cq.from( BBSRoleInfo.class );
		Predicate p = cb.equal( root.get( BBSRoleInfo_.roleCode ), roleCode );
		roleInfoList = em.createQuery( cq.where(p) ).getResultList();
		if( ListTools.isNotEmpty(roleInfoList) ){
			return roleInfoList.get(0);
		}else{
			return null;
		}
	}

	//@MethodDescribe( "포럼 ID로 해당 포럼의 전체 BBSRoleInfo 엔티티 정보 목록 조회" )
	public List<String> listRoleByForumId( String forumId ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception("forumId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSRoleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSRoleInfo> root = cq.from( BBSRoleInfo.class );
		Predicate p = cb.equal( root.get( BBSRoleInfo_.forumId ), forumId );
		cq.select( root.get( BBSRoleInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
	
	/**
	 * 게시판 ID로 해당 게시판의 전체 BBSRoleInfo 엔티티 정보 목록 조회
	 * @param sectionId
	 * @param queryMainSectionId 주 게시판 ID 조회 여부
	 * @return
	 * @throws Exception
	 */
	public List<String> listRoleBySectionId( String sectionId, Boolean queryMainSectionId ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception("sectionId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSRoleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSRoleInfo> root = cq.from( BBSRoleInfo.class );
		Predicate p = cb.equal( root.get( BBSRoleInfo_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get( BBSRoleInfo_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSRoleInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}

	public List<String> listRoleByMainSectionId(String sectionId) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception("sectionId is null!");
		}
		EntityManager em = this.entityManagerContainer().get( BBSRoleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSRoleInfo> root = cq.from( BBSRoleInfo.class );
		Predicate p = cb.equal( root.get( BBSRoleInfo_.mainSectionId ), sectionId );
		cq.select( root.get( BBSRoleInfo_.id ) );
		return em.createQuery( cq.where(p) ).getResultList();
	}
}
