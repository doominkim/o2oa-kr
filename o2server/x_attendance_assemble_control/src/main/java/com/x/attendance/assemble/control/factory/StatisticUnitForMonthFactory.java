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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticUnitForMonth;
import com.x.attendance.entity.StatisticUnitForMonth;
import com.x.attendance.entity.StatisticUnitForMonth_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticUnitForMonthFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( StatisticUnitForMonthFactory.class );
	
	public StatisticUnitForMonthFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 StatisticUnitForMonth 정보 객체 조회")
	public StatisticUnitForMonth get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, StatisticUnitForMonth.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 StatisticUnitForMonth 정보 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		cq.select(root.get(StatisticUnitForMonth_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 StatisticUnitForMonth 정보 목록 조회")
	public List<StatisticUnitForMonth> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<StatisticUnitForMonth>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);
		Predicate p = root.get(StatisticUnitForMonth_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	
	
	
	public List<String> listByUnitYearAndMonth( String unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.isEmpty() ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		Predicate p = cb.equal( root.get(StatisticUnitForMonth_.unitName), unitName);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticUnitForMonth_.id));
		return em.createQuery(cq.where(p)).setMaxResults(60).getResultList();
	}
	
	public List<String> listByUnitYearAndMonth( List<String> unitNames, String sYear, String sMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0  ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in(unitNames);
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		cq.select(root.get(StatisticUnitForMonth_.id));
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
	public List<StatisticUnitForMonth> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
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
	public List<StatisticUnitForMonth> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForMonth> cq = cb.createQuery(StatisticUnitForMonth.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForMonth_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForMonth_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
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
	public long getCountWithFilter( WrapInFilterStatisticUnitForMonth wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from(StatisticUnitForMonth.class);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForMonth_.id));

		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForMonth_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForMonth_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 지각 횟수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 비정상 체크 횟수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 근무시간 부족 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByUnitYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 조퇴 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 퇴근 체크 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 출근 체크 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 출근 인일 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 휴가 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직 이름, 통계 연월로 최상위 조직 모든 사용자 결근 건수 합계 통계
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByUnitYearAndMonth( List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );		
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 수 통계
	 * @return
	 * @throws Exception
	 */
	public Long sumLateCountByTopUnitNamesYearAndMonth(List<String> unitName, String sYear, String sMonth) throws Exception{
		if( unitName == null || unitName.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lateCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.unitName).in( unitName );
		if( sYear == null || sYear.isEmpty() ){
			logger.error( new StatisticYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), sYear));
		}
		if( sMonth == null || sMonth.isEmpty() ){
			logger.error( new StatisticMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), sMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 출근 인일수 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAttendanceDayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyEmployeeCount ) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 이상 출퇴근 기록 횟수 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumAbNormalDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.abNormalDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 근무시간 부족 횟수 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLackOfTimeCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.lackOfTimeCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 조퇴 인원 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumLeaveEarlyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.leaveEarlyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 퇴근 체크 인원 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOffDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.offDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 출근 체크 인원 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long sumOnDutyCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onDutyCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 휴가 인원 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}	
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.onSelfHolidayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	/**
	 * 최상위 조직명, 통계 연월로 최상위 조직 전체 사용자 결근 인일 합계 통계
	 * @param topUnitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDayCountByTopUnitNamesYearAndMonth( List<String> topUnitNames, String cycleYear, String cycleMonth ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForMonth.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<StatisticUnitForMonth> root = cq.from( StatisticUnitForMonth.class);		
		//총수 조회
		cq.select( cb.sum( root.get(StatisticUnitForMonth_.absenceDayCount) ) );		
		Predicate p = root.get(StatisticUnitForMonth_.topUnitName).in( topUnitNames );
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(StatisticUnitForMonth_.statisticMonth), cycleMonth));
		}
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}