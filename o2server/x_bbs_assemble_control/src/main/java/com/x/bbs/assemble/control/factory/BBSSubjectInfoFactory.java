package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.common.date.DateOperation;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSSubjectContent;
import com.x.bbs.entity.BBSSubjectContent_;
import com.x.bbs.entity.BBSSubjectInfo;
import com.x.bbs.entity.BBSSubjectInfo_;

/**
 * 클래스명: BBSSubjectInfoFactory<br/>
 * 엔티티 클래스: BBSSubjectInfo<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
**/
public class BBSSubjectInfoFactory extends AbstractFactory {

	public BBSSubjectInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe( "지정 Id의 BBSSubjectInfo 엔티티 정보 객체 조회" )
	public BBSSubjectInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectInfo.class, ExceptionWhen.none );
	}

	//@MethodDescribe( "지정 Id의 BBSSubjectInfo 엔티티 정보 목록 조회" )
	public List<BBSSubjectInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSubjectInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get(BBSSubjectInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 정보로 표시할 전체 상단 고정 주제 목록 조회" )
	public List<String> listAllTopSubject( String forumId, String mainSectionId, String sectionId, String creatorName, Date startTime , Date endTime  ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		cq.select(root.get( BBSSubjectInfo_.id));

		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );

		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		Predicate top_or = null;
		Predicate top_toforum_or = null;
		Predicate top_tomainsection_or = null;
		Predicate top_tosection_or = null;
		if( StringUtils.isNotEmpty( forumId ) ){
			top_toforum_or = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
			top_toforum_or = cb.and( top_toforum_or, cb.isTrue( root.get( BBSSubjectInfo_.topToForum )) );
			top_or = top_toforum_or;
		}

		if( StringUtils.isNotEmpty( mainSectionId ) ){//지정 주 게시판의 전체 상단 고정 주제
			top_tomainsection_or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId );
			top_tomainsection_or = cb.and( top_tomainsection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToMainSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tomainsection_or );
			}else{
				top_or = top_tomainsection_or;
			}
		}

		if( StringUtils.isNotEmpty( sectionId ) ){//지정 게시판의 전체 상단 고정 주제
			top_tosection_or = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
			top_tosection_or = cb.and( top_tosection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tosection_or );
			}else{
				top_or = top_tosection_or;
			}
		}



		if( top_or != null ){
			p = cb.and( p, top_or );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 정보로 표시할 전체 상단 고정 주제 목록 조회" )
	public List<String> listTopSubjectByType( String forumId, String mainSectionId, String sectionId,String subjectType, String creatorName, Date startTime , Date endTime  ) throws Exception {
		if( forumId == null || forumId.isEmpty() ){
			throw new Exception( "forumId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		cq.select(root.get( BBSSubjectInfo_.id));

		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );

		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.type ), subjectType ) );
		}

		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p,  cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		Predicate top_or = null;
		Predicate top_toforum_or = null;
		Predicate top_tomainsection_or = null;
		Predicate top_tosection_or = null;
		if( StringUtils.isNotEmpty( forumId ) ){
			top_toforum_or = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
			top_toforum_or = cb.and( top_toforum_or, cb.isTrue( root.get( BBSSubjectInfo_.topToForum )) );
			top_or = top_toforum_or;
		}

		if( StringUtils.isNotEmpty( mainSectionId ) ){//지정 주 게시판의 전체 상단 고정 주제
			top_tomainsection_or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId );
			top_tomainsection_or = cb.and( top_tomainsection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToMainSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tomainsection_or );
			}else{
				top_or = top_tomainsection_or;
			}
		}

		if( StringUtils.isNotEmpty( sectionId ) ){//지정 게시판의 전체 상단 고정 주제
			top_tosection_or = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
			top_tosection_or = cb.and( top_tosection_or, cb.isTrue( root.get( BBSSubjectInfo_.topToSection )) );
			if( top_or != null ){
				top_or = cb.or( top_or, top_tosection_or );
			}else{
				top_or = top_tosection_or;
			}
		}



		if( top_or != null ){
			p = cb.and( p, top_or );
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 정보로 포럼 내 주제 수 조회" )
	public Long countByForumId( String forumId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 정보로 포럼 내 주제 목록 조회" )
	public List<BBSSubjectInfo> listByForumId( String forumId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 정보로 게시판 내 주제 수 조회, 하위 게시판 내 주제 수 포함" )
	public Long countByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 ID로 전체 주제 목록 조회, 하위 게시판 내 주제 포함" )
	public List<BBSSubjectInfo> listByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 게시판 ID로 전체 주제 ID 목록 조회, 하위 게시판 내 주제 포함
	 * @param sectionId
	 * @param withTopSubject 상단 고정 주제 포함 여부
	 * @return
	 * @throws Exception
	 */
	public List<String> listIdsByMainAndSubSectionId( String sectionId, Boolean withTopSubject ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
		p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		if( withTopSubject != null ){
			if( !withTopSubject ){
				Predicate p1 = cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}else{
				Predicate p1 = cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) );
				p = cb.and( p, p1 );
			}
		}
		cq.select( root.get( BBSSubjectInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "게시판 ID로 전체 주제 수 조회, 하위 게시판 내 주제 제외" )
	public Long countSubjectIdsBySection( String sectionId ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 ID로 전체 주제 목록 조회, 하위 게시판 내 주제 제외" )
	public List<BBSSubjectInfo> listSubjectBySection( String sectionId ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(100).getResultList();
	}

	//@MethodDescribe( "게시판 ID로 전체 주제 목록 조회, 하위 게시판 내 주제 제외" )
	public List<String> listSubjectIdsBySection( String sectionId, Integer maxResults ) throws Exception {
		if( sectionId == null || sectionId.isEmpty() ){
			throw new Exception( "sectionId is null!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.sectionId), sectionId);
		cq.select( root.get(BBSSubjectInfo_.id) );
		if( maxResults == null || maxResults == 0 ){
			return em.createQuery(cq.where(p)).getResultList();
		}else{
			return em.createQuery(cq.where(p)).setMaxResults( maxResults ).getResultList();
		}
	}

	//@MethodDescribe( "게시판 ID, 주 게시판 ID, 게시판 ID, 작성자 이름으로 조건에 맞는 전체 주제 목록 조회, 하위 게시판 내 주제 수 제외" )
	public Long countSubjectInSectionForPage(String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, List<String> viewSectionIds , Date startTime , Date endTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.select( cb.count( root ) );
		//SELECT COUNT(b) FROM BBSSubjectInfo b WHERE ((b.id IS NOT NULL AND b.sectionId IN ('1c1d9dfc-0034-4d9a-adc7-bb4b3925bbd5')) AND b.title LIKE 'Count')
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 이름, 제목 유형, 주 게시판 ID, 게시판 ID, 작성자 이름으로 조건에 맞는 전체 주제 목록 조회, 하위 게시판 내 주제 수 제외" )
	public Long countSubjectWithSubjectTypeSectionForPage(String sectionName,String subjectType, String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, List<String> viewSectionIds , Date startTime , Date endTime) throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( sectionName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionName ), sectionName));
		}
		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.type ), subjectType));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}

		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.select( cb.count( root ) );
		//SELECT COUNT(b) FROM BBSSubjectInfo b WHERE ((b.id IS NOT NULL AND b.sectionId IN ('1c1d9dfc-0034-4d9a-adc7-bb4b3925bbd5')) AND b.title LIKE 'Count')
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "게시판 ID, 주 게시판 ID, 게시판 ID, 작성자 이름으로 조건에 맞는 전체 주제 목록 조회, 하위 게시판 내 주제 제외" )
	public List<BBSSubjectInfo> listSubjectInSectionForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, Integer maxRecordCount, List<String> viewSectionIds, Date startTime , Date endTime) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			   p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			   p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "게시판 이름, 주제 유형, 게시판 ID, 주 게시판 ID, 게시판 ID, 작성자 이름으로 조건에 맞는 전체 주제 목록 조회, 하위 게시판 내 주제 제외" )
	public List<BBSSubjectInfo> listSubjectWithSubjectTypeForPage(String sectionName,String subjectType, String searchTitle, String forumId, String mainSectionId, String sectionId, String creatorName, Boolean needPicture, Boolean isTopSubject, Integer maxRecordCount, List<String> viewSectionIds, Date startTime , Date endTime) throws Exception {
		if( maxRecordCount == null ){
			throw new Exception( "maxRecordCount is null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isNotNull( root.get(BBSSubjectInfo_.id ) );

		if( StringUtils.isNotEmpty( sectionName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionName ), sectionName));
		}
		if( StringUtils.isNotEmpty( subjectType ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.type ), subjectType));
		}
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		if( ListTools.isNotEmpty( viewSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds ) );
		}
		if( isTopSubject != null ){
			if( isTopSubject ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}

		if(startTime!= null) {
			p = cb.and(p, cb.greaterThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), startTime));
		}

		if(endTime!= null) {
			p = cb.and(p, cb.lessThanOrEqualTo(root.get(BBSSubjectInfo_.createTime), endTime));
		}

		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "지정 주제 ID의 이전 주제 ID 조회, 최신 답글 시간순 정렬" )
	public List<BBSSubjectInfo> listLastSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery( BBSSubjectInfo.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.greaterThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
	}

	//@MethodDescribe( "마지막 답글 시간 기준으로 더 이른 답글이 있는지 수량 조회" )
	public Long countLastSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< Long> cq = cb.createQuery(  Long.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.greaterThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 주제 ID의 다음 주제 ID 조회, 최신 답글 시간순 정렬" )
	public List<BBSSubjectInfo> listNextSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery( BBSSubjectInfo.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.lessThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.latestReplyTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
	}

	//@MethodDescribe( "마지막 답글 시간 기준으로 더 늦은 답글이 있는지 수량 조회" )
	public Long countNextSubject( Date latestReplyTime ) throws Exception {
		if( latestReplyTime == null ){
			throw new Exception( "latestReplyTime is null!" );
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery< Long> cq = cb.createQuery(  Long.class );
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.lessThan( root.get( BBSSubjectInfo_.latestReplyTime ), latestReplyTime );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "포럼 ID, 주 게시판 ID, 게시판 ID로 지정 사용자가 작성한 주제 수 조회" )
	public Long countUserSubjectForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, String creatorName ) throws Exception {
		if( creatorName == null || creatorName.isEmpty() ){
			throw new Exception( "creatorName can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( needPicture != null ){
			if( needPicture ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "포럼 ID, 주 게시판 ID, 게시판 ID로 지정 사용자가 작성한 주제 목록 조회, 페이징" )
	public List<BBSSubjectInfo> listUserSubjectForPage( String searchTitle, String forumId, String mainSectionId, String sectionId, Boolean needPicture, Boolean withTopSubject, Integer maxRecordCount, String creatorName ) throws Exception {
		if( creatorName == null || creatorName.isEmpty() ){
			throw new Exception( "creatorName can not null." );
		}
		if( maxRecordCount == null ){
			maxRecordCount = 20;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName );
		if( needPicture != null ){
			if( needPicture ){
				p = cb.and( p, cb.isTrue( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}else{
				p = cb.and( p, cb.isFalse( root.get( BBSSubjectInfo_.isTopSubject ) ) );
			}
		}
		if( forumId != null ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId));
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId));
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId));
		}
		if( BooleanUtils.isTrue(needPicture) ){
			p = cb.and( p, cb.isNotNull( root.get( BBSSubjectInfo_.picId ) ),  cb.notEqual( root.get( BBSSubjectInfo_.picId ), ""));
		}
		if( StringUtils.isNotEmpty( searchTitle ) ){
			p = cb.and( p, cb.like( root.get( BBSSubjectInfo_.title ), searchTitle ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( maxRecordCount ).getResultList();
	}

	//@MethodDescribe( "지정 사용자가 오늘 작성한 주제 수 조회" )
	public Long countSubjectForTodayByUserName( String userName ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 사용자가 작성한 우수 주제 수 조회" )
	public Long countCreamSubjectByUserName( String userName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 사용자가 작성한 원본 주제 수 조회" )
	public Long countOriginalSubjectByUserName( String userName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( userName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), userName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 게시판에서 당일 작성된 주제 수 조회" )
	public Long countSubjectForTodayBySectionId( String sectionId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( sectionId ) ){
			Predicate or = cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId );
			or = cb.or( or, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
			p = cb.and( p, or );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 범위에서 당일 작성된 주제 수 조회" )
	public Long countForTodayByForumId( String forumId ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.greaterThanOrEqualTo( root.get( BBSSubjectInfo_.createTime ), dateOperation.getTodayStartTime() );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 포럼 ID 목록, 주 게시판 ID 목록, 게시판 ID 목록으로 BBS 홈에 추천된 주제 목록 조회" )
	public List<BBSSubjectInfo> listRecommendedSubjectForBBSIndex( List<String> forumIds, List<String> mainSectionIds, List<String> sectionIds, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.forumId ).in( forumIds ) );
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		if( ListTools.isNotEmpty( sectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( sectionIds ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "지정 포럼 ID 목록, 주 게시판 ID 목록, 게시판 ID 목록으로 포럼 홈에 추천된 주제 목록 조회" )
	public List<BBSSubjectInfo> listRecommendedSubjectForForumIndex( List<String> forumIds, List<String> mainSectionIds, List<String> sectionIds, Integer count) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToForumIndex ) );
		if( ListTools.isNotEmpty( forumIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.forumId ).in( forumIds ) );
		}
		if( ListTools.isNotEmpty( mainSectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.mainSectionId ).in( mainSectionIds ) );
		}
		if( ListTools.isNotEmpty( sectionIds ) ){
			p = cb.and( p, root.get( BBSSubjectInfo_.sectionId ).in( sectionIds ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "지정 게시판 ID 목록으로 조건에 맞는 주제 수 검색" )
	public Long countSubjectSearchInSectionForPage( String searchContent, List<String> viewSectionIds ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			throw new Exception( "viewSectionIds can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds );
		Predicate or = cb.like( root.get( BBSSubjectInfo_.title ), "%"+searchContent+"%" );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.sectionName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.forumName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.creatorName ), "%"+searchContent+"%" ) );
		p = cb.and( p, or );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 게시판 ID 목록으로 조건에 맞는 주제 목록 검색, 페이징" )
	public List<BBSSubjectInfo> listSubjectSearchInSectionForPage( String searchContent, List<String> viewSectionIds, Integer count ) throws Exception {
		if( searchContent == null || searchContent.isEmpty() ){
			throw new Exception( "searchContent can not null." );
		}
		if( viewSectionIds == null || viewSectionIds.isEmpty() ){
			throw new Exception( "viewSectionIds can not null." );
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = root.get( BBSSubjectInfo_.sectionId ).in( viewSectionIds );
		Predicate or = cb.like( root.get( BBSSubjectInfo_.title ), "%"+searchContent+"%" );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.sectionName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.forumName ), "%"+searchContent+"%" ) );
		or = cb.or( or, cb.like( root.get( BBSSubjectInfo_.creatorName ), "%"+searchContent+"%" ) );
		p = cb.and( p, or );
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "지정 게시판 ID, 주 게시판 ID, 게시판 ID와 지정 사용자로 우수 주제 수 조회" )
	public Long countCreamedSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 게시판 ID, 주 게시판 ID, 게시판 ID와 지정 사용자로 우수 주제 목록 조회, 페이징" )
	public List<BBSSubjectInfo> listCreamedSubjectInSectionForPage( String forumId, String mainSectionId, String sectionId, String creatorName, Integer count ) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.isCreamSubject ) );
		if( StringUtils.isNotEmpty( forumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), forumId ) );
		}
		if( StringUtils.isNotEmpty( mainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), mainSectionId ) );
		}
		if( StringUtils.isNotEmpty( sectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	//@MethodDescribe( "지정 게시판 ID, 주 게시판 ID, 게시판 ID와 지정 사용자로 BBS 홈에 추천된 주제 수 조회" )
	public Long countRecommendedSubjectInSectionForPage( String searchForumId, String searchMainSectionId, String searchSectionId, String creatorName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( StringUtils.isNotEmpty( searchForumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), searchForumId ) );
		}
		if( StringUtils.isNotEmpty( searchMainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), searchMainSectionId ) );
		}
		if( StringUtils.isNotEmpty( searchSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), searchSectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	//@MethodDescribe( "지정 게시판 ID, 주 게시판 ID, 게시판 ID와 지정 사용자로 BBS 홈에 추천된 주제 목록 조회" )
	public List<BBSSubjectInfo> listRecommendedSubjectInSectionForPage( String searchForumId, String searchMainSectionId,
			String searchSectionId, String creatorName, Integer count) throws Exception {
		if( count == null ){
			count = 10;
		}
		EntityManager em = this.entityManagerContainer().get( BBSSubjectInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectInfo> cq = cb.createQuery(BBSSubjectInfo.class);
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.isTrue( root.get( BBSSubjectInfo_.recommendToBBSIndex ) );
		if( StringUtils.isNotEmpty( searchForumId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.forumId ), searchForumId ) );
		}
		if( StringUtils.isNotEmpty( searchMainSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), searchMainSectionId ) );
		}
		if( StringUtils.isNotEmpty( searchSectionId ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.sectionId ), searchSectionId ) );
		}
		if( StringUtils.isNotEmpty( creatorName ) ){
			p = cb.and( p, cb.equal( root.get( BBSSubjectInfo_.creatorName ), creatorName ) );
		}
		cq.orderBy( cb.desc( root.get( BBSSubjectInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	public List<BBSSubjectContent> getSubjectContent( String id ) throws Exception {
		if( id == null || id.isEmpty() ){
			throw new Exception("id is null");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectContent.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectContent> cq = cb.createQuery(BBSSubjectContent.class);
		Root<BBSSubjectContent> root = cq.from( BBSSubjectContent.class );
		Predicate p = cb.equal( root.get( BBSSubjectContent_.id ), id );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정 게시판에서 기간 내 작성된 주제 수 조회
	 * @param sectionId
	 * @param todayStartTime
	 * @param todayEndTime
	 * @param queryMainSection  주 게시판이 sectionId인 데이터도 조회할지 여부
	 * @return
	 * @throws Exception
	 */
	public Long countSubjectByDate(String sectionId, Date todayStartTime, Date todayEndTime, boolean queryMainSection) throws Exception {
		if( StringUtils.isEmpty( sectionId ) ){
			throw new Exception("sectionId is null");
		}
		if( todayStartTime == null ) {
			todayStartTime = new DateOperation().getTodayStartTime();
		}
		if( todayEndTime == null ) {
			todayEndTime = new DateOperation().getTodayEndTime();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<BBSSubjectInfo> root = cq.from( BBSSubjectInfo.class );
		Predicate p = cb.equal( root.get( BBSSubjectInfo_.sectionId ), sectionId );
		if( queryMainSection ) {
			p = cb.or( p, cb.equal( root.get( BBSSubjectInfo_.mainSectionId ), sectionId ));
		}
		Predicate p_time = cb.between( root.get( BBSSubjectInfo_.createTime ), todayStartTime, todayEndTime ) ;
		p = cb.and( p, p_time );
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listSubjectIdsByCreator(String creatorName) throws Exception {
		if( StringUtils.isBlank(creatorName) ){
			return new ArrayList<>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<BBSSubjectInfo> root = cq.from(BBSSubjectInfo.class);
		Predicate p = cb.equal(  root.get(BBSSubjectInfo_.creatorName), creatorName);
		cq.select( root.get(BBSSubjectInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

}
