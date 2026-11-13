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
import com.x.calendar.core.entity.Calendar_Event_;
import com.x.calendar.core.tools.CriteriaBuilderTools;


/**
 * 캘린더 작업 기록 정보 테이블 기능 서비스 클래스
 * @author O2LEE
 */
public class Calendar_EventFactory extends AbstractFactory {
	
	public Calendar_EventFactory( Business business) throws Exception {
		super(business);
	}

	/**
	 * 지정한 Id의 캘린더 작업 기록 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Calendar_Event get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, Calendar_Event.class );
	}
	
	/**
	 * 지정한 Id의 캘린더 작업 기록 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<Calendar_Event> list(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Calendar_Event> cq = cb.createQuery(Calendar_Event.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = root.get( Calendar_Event_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	public List<String> listWithCalendarId(String calendarId) throws Exception {
		if( StringUtils.isEmpty( calendarId ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.equal( root.get( Calendar_Event_.calendarId), calendarId);
		cq.select(root.get(Calendar_Event_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 조건으로 지정한 캘린더 정보 ID 목록 조회
	 * @param key
	 * @param eventType
	 * @param source
	 * @param createPerson
	 * @param calendarIds
	 * @param personName
	 * @param unitNames
	 * @param groupNames
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithCondition( String key, String eventType, String source, String createPerson, List<String> calendarIds, 
			String personName, List<String> unitNames, List<String> groupNames, Date startTime, Date endTime ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		
		Predicate p = null;
		if( ListTools.isNotEmpty( calendarIds )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, root.get(Calendar_Event_.calendarId).in( calendarIds ));
		}		
		if( StringUtils.isNotEmpty( eventType )) {
			p =CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_Event_.eventType), eventType));
		}
		if( StringUtils.isNotEmpty( source )) {
			p =CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_Event_.source), source));
		}
		if( StringUtils.isNotEmpty( createPerson )) {
			p =CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_Event_.createPerson), createPerson));
		}
		if( startTime != null ) {
			if( endTime == null ) {
				throw new Exception("endTime is null!");
			}else {
				p =CriteriaBuilderTools.predicate_and( cb, p, cb.lessThanOrEqualTo( root.get(Calendar_Event_.startTime), endTime ));
				p =CriteriaBuilderTools.predicate_and( cb, p, cb.greaterThanOrEqualTo( root.get(Calendar_Event_.endTime), startTime ));
			}
		}

		//권한 필터링
		Predicate permission = null;
		if( StringUtils.isNotEmpty( personName )) {
			permission =CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_Event_.manageablePersonList)));
			permission =CriteriaBuilderTools.predicate_or( cb, permission, cb.isMember(personName, root.get(Calendar_Event_.viewablePersonList)));
		}
		if( ListTools.isNotEmpty( unitNames )) {
			permission =CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_Event_.viewableUnitList).in( unitNames ));
		}
		if( ListTools.isNotEmpty( groupNames )) {
			permission =CriteriaBuilderTools.predicate_or( cb, permission, root.get(Calendar_Event_.viewableGroupList).in( groupNames ));		
		}
		if( permission != null ) {
			permission = CriteriaBuilderTools.predicate_or( cb, permission, cb.isTrue( root.get(Calendar_Event_.isPublic) ) );		
		}
		p = CriteriaBuilderTools.predicate_and( cb, p, permission );
		
		//유사 검색
		Predicate p_key = null;
		if( StringUtils.isNotEmpty( key )) {
			p_key = cb.like(root.get(Calendar_Event_.title), key);
			p_key = CriteriaBuilderTools.predicate_or( cb, p_key, cb.like(root.get(Calendar_Event_.comment), key) );
		}
		
		p = CriteriaBuilderTools.predicate_and( cb, p, p_key );
		
		cq.select(root.get(Calendar_Event_.id));
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 반복 주체와 날짜로 지정한 캘린더 기록 정보 ID 목록 조회
	 * @param repeatMasterId
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws Exception
	 */
	public List<String> listWithRepeatMaster( String repeatMasterId, Date startTime, Date endTime ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		
		Predicate p = null;
		if( StringUtils.isNotEmpty( repeatMasterId )) {
			p = CriteriaBuilderTools.predicate_and( cb, p, cb.equal(root.get(Calendar_Event_.repeatMasterId), repeatMasterId));
		}
		if( startTime != null ) {
			if( endTime == null ) {
				//startTime 이후의 모든 기록 조회
				p = CriteriaBuilderTools.predicate_and( cb, p, cb.greaterThanOrEqualTo( root.get(Calendar_Event_.startTime), startTime ));
			}else {
				p = CriteriaBuilderTools.predicate_and( cb, p, cb.lessThanOrEqualTo( root.get(Calendar_Event_.startTime), endTime ));
				p = CriteriaBuilderTools.predicate_and( cb, p, cb.greaterThanOrEqualTo( root.get(Calendar_Event_.endTime), startTime ));
			}
		}
		cq.select(root.get(Calendar_Event_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public long countWithRepeatMaster(String repeatMasterId) throws Exception {
		if( StringUtils.isEmpty( repeatMasterId ) ){
			throw new Exception("repeatMasterId is empty!");
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.equal(root.get(Calendar_Event_.repeatMasterId), repeatMasterId);
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 시작·종료 시간, 제목, 반복 주체 ID 기준
	 * @param calendar_Event
	 * @return
	 * @throws Exception
	 */
	public boolean eventExists( Calendar_Event calendar_Event ) throws Exception {
		if( calendar_Event == null  ){
			throw new Exception("calendar_Event is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.equal(root.get(Calendar_Event_.startTimeStr), calendar_Event.getStartTimeStr() );
		p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(Calendar_Event_.endTimeStr), calendar_Event.getEndTimeStr() ) );
		p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(Calendar_Event_.isAllDayEvent), calendar_Event.getIsAllDayEvent() ) );
		p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(Calendar_Event_.title), calendar_Event.getTitle() ) );
		if( StringUtils.isNotEmpty( calendar_Event.getRepeatMasterId() )) {
			p = CriteriaBuilderTools.predicate_and(cb, p, cb.equal(root.get(Calendar_Event_.repeatMasterId), calendar_Event.getRepeatMasterId() ) );
		}	
		cq.select( cb.count( root ) );
		Long count = em.createQuery(cq.where(p)).getSingleResult();
		if( count > 0 ) {
			return true;
		}
		return false;
	}

	public List<String> listNeedAlarmEventIds(Date date) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.lessThanOrEqualTo( root.get(Calendar_Event_.alarmTime), date );
		p = cb.and( p, cb.isFalse( root.get(Calendar_Event_.alarmAlready ) ));
		cq.select(root.get(Calendar_Event_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listWithBundle(String bundle) throws Exception {
		if( StringUtils.isEmpty( bundle ) ) {
			throw new Exception("bundle is null!");
		}
		EntityManager em = this.entityManagerContainer().get(Calendar_Event.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Calendar_Event> root = cq.from(Calendar_Event.class);
		Predicate p = cb.equal( root.get(Calendar_Event_.bundle ), bundle);
		cq.select(root.get(Calendar_Event_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}