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
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSForumInfo_;

/**
 * 클래스명: BBSForumInfoFactory<br/>
 * 엔티티 클래스: BBSForumInfo<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-08-10 17:17:26
**/
public class BBSForumInfoFactory extends AbstractFactory {

	public BBSForumInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "지정 Id의 BBSForumInfo 엔티티 정보 객체 조회" )
	public BBSForumInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSForumInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSForumInfo 엔티티 정보 목록 조회" )
	public List<BBSForumInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSForumInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSForumInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSForumInfo> cq = cb.createQuery(BBSForumInfo.class);
		Root<BBSForumInfo> root = cq.from(BBSForumInfo.class);
		Predicate p = root.get(BBSForumInfo_.id).in(ids);
		cq.orderBy( cb.asc( root.get( BBSForumInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( 1000 ).getResultList();
	}
	
	//@MethodDescribe( "전체 BBSForumInfo 엔티티 정보 목록 조회" )
	public List<BBSForumInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSForumInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSForumInfo> cq = cb.createQuery(BBSForumInfo.class);
		Root<BBSForumInfo> root = cq.from(BBSForumInfo.class);
		cq.orderBy( cb.asc( root.get( BBSForumInfo_.orderNumber ) ) );
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}
	
	//@MethodDescribe( "사용자 권한에 따른 전체 BBSForumInfo 엔티티 정보 목록 조회" )
	public List<BBSForumInfo> listAllViewAbleForumWithMyPermission( List<String> viewAbleForumIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSForumInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSForumInfo> cq = cb.createQuery(BBSForumInfo.class);
		Root<BBSForumInfo> root = cq.from(BBSForumInfo.class);
		Predicate p = cb.equal( root.get( BBSForumInfo_.forumVisible ), "모든 사용자" );
		if( ListTools.isNotEmpty(viewAbleForumIds) ){
			p = cb.or( p, root.get( BBSForumInfo_.id ).in( viewAbleForumIds ) );
		}
		cq.orderBy( cb.asc( root.get( BBSForumInfo_.orderNumber ) ) );
		return em.createQuery( cq.where(p) ).setMaxResults( 1000 ).getResultList();
	}
	
//	@MethodDescribe( "공개 범위가 전체인 BBSForumInfo 엔티티 정보 ID 목록 조회" )
	public List<String> listAllPublicForumIds() throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSForumInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSForumInfo> root = cq.from(BBSForumInfo.class);
		Predicate p = cb.equal( root.get(BBSForumInfo_.forumStatus ), "사용" );
		p = cb.and( p, cb.equal( root.get(BBSForumInfo_.forumVisible ), "모든 사용자" ) );
		cq.select( root.get( BBSForumInfo_.id ) );
		return em.createQuery( cq.where(p) ).setMaxResults( 1000 ).getResultList();
	}

	//@MethodDescribe( "사용 중인 BBSForumInfo 엔티티 정보 ID 목록 조회" )
	public List<BBSForumInfo> listAllOpenForumInfo() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSForumInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSForumInfo> cq = cb.createQuery(BBSForumInfo.class);
		Root<BBSForumInfo> root = cq.from(BBSForumInfo.class);
		Predicate p = cb.equal( root.get(BBSForumInfo_.forumStatus ), "사용" );
		cq.orderBy( cb.asc( root.get( BBSForumInfo_.orderNumber ) ) );
		return em.createQuery( cq.where(p) ).setMaxResults( 1000 ).getResultList();
	}
}
