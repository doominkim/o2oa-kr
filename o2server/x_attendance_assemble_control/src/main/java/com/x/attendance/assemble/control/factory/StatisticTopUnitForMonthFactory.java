package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticTopUnitForMonth;
import com.x.attendance.entity.StatisticTopUnitForMonth_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticTopUnitForMonthFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( StatisticTopUnitForMonthFactory.class );
	
	public StatisticTopUnitForMonthFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 StatisticTopUnitForMonth 정보 객체 조회")
	public StatisticTopUnitForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticTopUnitForMonth.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 StatisticTopUnitForMonth 정보 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticTopUnitForMonth> root = cq.from( StatisticTopUnitForMonth.class);
		cq.select(root.get(StatisticTopUnitForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 StatisticTopUnitForMonth 정보 목록 조회")
	public List<StatisticTopUnitForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticTopUnitForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticTopUnitForMonth> cq = cb.createQuery(StatisticTopUnitForMonth.class);
		Root<StatisticTopUnitForMonth> root = cq.from(StatisticTopUnitForMonth.class);
		Predicate p = root.get(StatisticTopUnitForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByTopUnitYearAndMonth(String topUnitName, String sYear, String sMonth) throws Exception {
		if( topUnitName == null || topUnitName.isEmpty() ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticTopUnitForMonth> root = cq.from( StatisticTopUnitForMonth.class);
		Predicate p = cb.equal( root.get(StatisticTopUnitForMonth_.topUnitName), topUnitName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticTopUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticTopUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticTopUnitForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}
	
	/**
	 * 다음 페이지 정보 데이터 조회
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticTopUnitForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticTopUnitForMonth> cq = cb.createQuery(StatisticTopUnitForMonth.class);
		Root<StatisticTopUnitForMonth> root = cq.from(StatisticTopUnitForMonth.class);

		String order = wrapIn.getOrder();//정렬 방식
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticTopUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticTopUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(StatisticTopUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(StatisticTopUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticTopUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 이전 페이지 문서 정보 데이터 조회
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticTopUnitForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticTopUnitForMonth> cq = cb.createQuery(StatisticTopUnitForMonth.class);
		Root<StatisticTopUnitForMonth> root = cq.from(StatisticTopUnitForMonth.class);

		String order = wrapIn.getOrder();//정렬 방식
		if( order == null || order.isEmpty() ){
			order = "DESC";
		}
		String orderFieldName = "";
		if(StringUtils.isNotEmpty( wrapIn.getKey())){
			orderFieldName = wrapIn.getKey();
		}else{
			orderFieldName = "sequence";
		}
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticTopUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticTopUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(StatisticTopUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(StatisticTopUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticTopUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(20).getResultList();
	}
	
	/**
	 * 조건에 맞는 문서 정보 총수 조회
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilterStatisticTopUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticTopUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticTopUnitForMonth> root = cq.from(StatisticTopUnitForMonth.class);
		Predicate p = cb.isNotNull(root.get(StatisticTopUnitForMonth_.id));

		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticTopUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticTopUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

}