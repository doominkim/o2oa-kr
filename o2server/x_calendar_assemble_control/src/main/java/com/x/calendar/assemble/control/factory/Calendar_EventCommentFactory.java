package com.x.calendar.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.ListTools;
import com.x.calendar.assemble.control.AbstractFactory;
import com.x.calendar.assemble.control.Business;
import com.x.calendar.core.entity.Calendar_Event;
import com.x.calendar.core.entity.Calendar_EventComment;
import com.x.calendar.core.entity.Calendar_EventComment_;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster;
import com.x.calendar.core.entity.Calendar_EventRepeatMaster_;
import com.x.calendar.core.entity.Calendar_Event_;


/**
 * 캘린더 비고 정보 테이블 기능 서비스 클래스
 * @author O2LEE
 */
public class Calendar_EventCommentFactory extends AbstractFactory {
	
	public Calendar_EventCommentFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 지정한 Id의 캘린더 비고 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_EventComment get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Calendar_EventComment.class );
	}
	
	/**
	 * 지정한 Id의 캘린더 비고 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_EventComment> list( List<String> ids ) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_EventComment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_EventComment> cq = cb.createQuery(Calendar_EventComment.class);
		Root<Calendar_EventComment> root = cq.from(Calendar_EventComment.class);
		Predicate p = root.get( Calendar_EventComment_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long countEventBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.equal( root.get(Calendar_Event_.commentId ), commentId);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long countRepeatMasterBundle( String commentId ) throws Exception {
		if( StringUtils.isEmpty( commentId ) ) {
			return 0L;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_EventRepeatMaster.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Calendar_EventRepeatMaster> root = cq.from(Calendar_EventRepeatMaster.class);
		Predicate p = cb.equal( root.get( Calendar_EventRepeatMaster_.commentId ), commentId);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 확인 시간이 now보다 이른 maxCount건의 ID 목록 조회
	 * @param now
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public List<String> listNeedCheckCommnetIds( Date now, Integer maxCount) throws Exception {
		if( now == null ) {
			now = new Date();
		}
		if( maxCount == null ) {
			maxCount = 1000;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_EventComment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_EventComment> root = cq.from(Calendar_EventComment.class);
		Predicate p = cb.lessThan( root.get( Calendar_EventComment_.checkTime ), now );
		cq.select(root.get(Calendar_EventComment_.id));
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}
}