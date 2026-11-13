package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSectionInfo_;

/**
 * 클래스명: BBSSectionInfoFactory<br/>
 * 엔티티 클래스: BBSSectionInfo<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
 **/
public class BBSSectionInfoFactory extends AbstractFactory {

	public BBSSectionInfoFactory( Business business ) throws Exception {
		super(business);
	}

	//@MethodDescribe( "지정 Id의 BBSSectionInfo 엔티티 정보 객체 조회" )
	public BBSSectionInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSectionInfo.class, ExceptionWhen.none );
	}

	//@MethodDescribe( "전달된 게시판 이름으로 DB에서 BBSSectionInfo 객체 조회" )
	public List<BBSSectionInfo> getMainSectionBySectionName( String mainSectionName ) throws Exception {
		if( mainSectionName  == null || mainSectionName.isEmpty() ){
			throw new Exception( "mainSectionName is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionName ), mainSectionName );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "지정 Id의 BBSSectionInfo 엔티티 정보 목록 조회" )
	public List<BBSSectionInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSectionInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = root.get(BBSSectionInfo_.id).in(ids);
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "전체 BBSSectionInfo 엔티티 정보 목록 조회" )
	public List<BBSSectionInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery( cq ).getResultList();
	}

	//@MethodDescribe( "포럼 ID로 전체 주 게시판 정보 목록 조회" )
	public List<BBSSectionInfo> listMainSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "주 게시판 ID로 전체 하위 게시판 정보 목록 조회" )
	public List<BBSSectionInfo> listSubSectionByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "주 게시판 ID로 전체 하위 게시판 정보 Id 목록 조회" )
	public List<String> listSubSectionIdsByMainSectionId( String sectionId ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID로 해당 포럼의 주 게시판 수 조회" )
	public Long countMainSectionByForumId( String forumId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "포럼 ID로 해당 포럼의 주 게시판과 하위 게시판 수 합계 조회" )
	public Long countAllSectionByForumId( String forumId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 ID로 해당 게시판의 하위 게시판 수 조회" )
	public Long countSubSectionByMainSectionId(String sectionId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSectionInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSectionInfo> root = cq.from( BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "포럼 ID로 해당 포럼의 전체 주 게시판 및 하위 게시판 정보 목록 조회" )
	public List<BBSSectionInfo> listAllSectionByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID로 해당 포럼의 전체 주 게시판 및 하위 게시판 정보 목록 조회" )
	public List<String> listAllSectionIdsByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID와 사용자가 접근 가능한 게시판 ID로 접근 가능한 전체 주 게시판 정보 목록 조회" )
	public List<BBSSectionInfo> viewMainSectionByForumId( String forumId, List<String> viewableSectionIds ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		Predicate or = cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" );
		if( ListTools.isNotEmpty( viewableSectionIds ) ){
			or = cb.or(p, root.get( BBSSectionInfo_.id ).in( viewableSectionIds ) );
		}
		p = cb.and( p, or );
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 ID와 사용자가 접근 가능한 게시판 ID 목록으로 접근 가능한 전체 하위 게시판 정보 목록 조회" )
	public List<BBSSectionInfo> viewSubSectionByMainSectionId( String sectionId, List<String> viewableSectionIds ) throws Exception {
		if( sectionId  == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSectionInfo> cq = cb.createQuery(BBSSectionInfo.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.mainSectionId ), sectionId );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		Predicate or = cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" );
		if( ListTools.isNotEmpty( viewableSectionIds ) ){
			or = cb.or(p, root.get( BBSSectionInfo_.id ).in( viewableSectionIds ) );
		}
		p = cb.and( p, or );
		cq.orderBy( cb.asc( root.get( BBSSectionInfo_.orderNumber ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID 목록으로 지정 포럼 범위 내 전체 공개 주 게시판 목록 조회" )
	public List<String> listVisibleToAllUserMainSectionIds( List<String> forumIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "主版块" );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.forumId ).in( forumIds ));
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID 목록, 주 게시판 ID 목록으로 지정 포럼과 주 게시판 범위 내 전체 공개 하위 게시판 목록 조회" )
	public List<String> listVisibleToAllUserSectionIds( List<String> forumIds, List<String> mainSectionIds ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.sectionLevel ), "子版块" );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.forumId ).in( forumIds ));
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and(p, root.get( BBSSectionInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "포럼 ID 목록으로 지정 포럼 범위 내 전체 공개 주 게시판과 하위 게시판 정보 목록 조회" )
	public List<String> viewSectionByForumId( List<String> viewforumIds, Boolean publicStatus ) throws Exception {
		if( viewforumIds  == null || viewforumIds.isEmpty() ){
			throw new Exception( "viewforumIds is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = root.get( BBSSectionInfo_.forumId ).in( viewforumIds );
		p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionStatus ), "启用" ));
		if( BooleanUtils.isTrue(publicStatus) ){
			p = cb.and(p, cb.equal( root.get( BBSSectionInfo_.sectionVisible ), "所有人" ));
		}
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByForumId( String forumId ) throws Exception {
		if( forumId  == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = cb.equal( root.get( BBSSectionInfo_.forumId ), forumId);
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByForumIds( List<String> forumIds ) throws Exception {
		if(ListTools.isEmpty(forumIds)){
			return new ArrayList<>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSectionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSectionInfo> root = cq.from(BBSSectionInfo.class);
		Predicate p = root.get( BBSSectionInfo_.forumId ).in( forumIds );
		cq.select( root.get( BBSSectionInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
