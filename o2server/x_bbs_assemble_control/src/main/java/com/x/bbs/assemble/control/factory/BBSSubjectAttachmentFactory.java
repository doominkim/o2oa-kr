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
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectAttachment_;

/**
 * 클래스명: BBSSubjectAttachmentFactory<br/>
 * 엔티티 클래스: BBSSubjectAttachment<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-08-10 17:17:26
**/
public class BBSSubjectAttachmentFactory extends AbstractFactory {

	public BBSSubjectAttachmentFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSSubjectAttachment 엔티티 정보 객체 조회" )
	public BBSSubjectAttachment get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSSubjectAttachment.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSSubjectAttachment 엔티티 정보 목록 조회" )
	public List<BBSSubjectAttachment> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSSubjectAttachment>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = root.get(BBSSubjectAttachment_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "주제 ID로 BBSSubjectAttachment 엔티티 정보 목록 조회" )
	public List<BBSSubjectAttachment> listBySubjectId( String subjectId ) throws Exception {
		if( subjectId == null || subjectId.isEmpty() ){
			throw new Exception("subject id is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = cb.equal( root.get(BBSSubjectAttachment_.subjectId ), subjectId );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "전체 BBSSubjectAttachment 엔티티 정보 목록 조회" )
	public List<BBSSubjectAttachment> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSSubjectAttachment> cq = cb.createQuery(BBSSubjectAttachment.class);
		return em.createQuery( cq ).setMaxResults( 1000 ).getResultList();
	}

	/**
	 * 지정 게시판 ID로 전체 첨부파일 정보 ID 목록 조회
	 * @param sectionId
	 * @param queryMainSectionId 주 게시판 ID 조회 여부
	 * @return
	 * @throws Exception 
	 */
	public List<String> listBySectionId(String sectionId, Boolean queryMainSectionId ) throws Exception {
		if( StringUtils.isNotEmpty( sectionId )){
			throw new Exception("sectionId id is empty!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSSubjectAttachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSSubjectAttachment> root = cq.from(BBSSubjectAttachment.class);
		Predicate p = cb.equal( root.get(BBSSubjectAttachment_.sectionId ), sectionId );
		if( queryMainSectionId ) {
			p = cb.or( p, cb.equal( root.get(BBSSubjectAttachment_.mainSectionId ), sectionId ) );
		}
		cq.select( root.get( BBSSubjectAttachment_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
