
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
import com.x.attendance.assemble.control.jaxrs.attendancestatistic.WrapInFilterStatisticUnitForDay;
import com.x.attendance.entity.StatisticUnitForDay;
import com.x.attendance.entity.StatisticUnitForDay_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class StatisticUnitForDayFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger(StatisticUnitForDayFactory.class);

	public StatisticUnitForDayFactory(Business business) throws Exception {
		super(business);
	}

	// @MethodDescribe("지정 Id의 StatisticUnitForDay 정보 객체 조회")
	public StatisticUnitForDay get(String id) throws Exception {
		return this.entityManagerContainer().find(id, StatisticUnitForDay.class, ExceptionWhen.none);
	}

	// @MethodDescribe("전체 StatisticUnitForDay 정보 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq).getResultList();
	}

	// @MethodDescribe("지정 Id의 StatisticUnitForDay 정보 목록 조회")
	public List<StatisticUnitForDay> list(List<String> ids) throws Exception {
		if (ids == null || ids.size() == 0) {
			return new ArrayList<StatisticUnitForDay>();
		}
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForDay> cq = cb.createQuery(StatisticUnitForDay.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = root.get(StatisticUnitForDay_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByUnitRecordDateString(List<String> unitName, String sDate) throws Exception {
		if (unitName == null || unitName.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = root.get(StatisticUnitForDay_.unitName).in(unitName);
		if (sDate == null || sDate.isEmpty()) {
			logger.error(new StatisticDateEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticDate), sDate));
		}
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByUnitRecordDateString(String unitName, String sDate) throws Exception {
		if (unitName == null || unitName.isEmpty()) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = cb.equal(root.get(StatisticUnitForDay_.unitName), unitName);
		if (sDate == null || sDate.isEmpty()) {
			logger.error(new StatisticDateEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticDate), sDate));
		}
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByUnitDayYearAndMonth(List<String> name, String year, String month) throws Exception {
		if (name == null || name.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = root.get(StatisticUnitForDay_.unitName).in(name);
		if (year == null || year.isEmpty()) {
			logger.error(new StatisticYearEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticYear), year));
		}
		if (month == null || month.isEmpty()) {
			logger.error(new StatisticMonthEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticMonth), month));
		}
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByUnitDayDate(List<String> name, String date) throws Exception {
		if (name == null || name.size() == 0) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = root.get(StatisticUnitForDay_.unitName).in(name);
		if (date == null || date.isEmpty()) {
			logger.error(new StatisticDateEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticDate), date));
		}
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	public List<String> listByUnitDayDate(String name, String date) throws Exception {
		if (name == null || name.isEmpty()) {
			logger.error(new UnitNamesEmptyException());
			return null;
		}

		EntityManager em = this.entityManagerContainer().get(StatisticUnitForDay.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = cb.equal(root.get(StatisticUnitForDay_.unitName), name);
		if (date == null || date.isEmpty()) {
			logger.error(new StatisticDateEmptyException());
		} else {
			p = cb.and(p, cb.equal(root.get(StatisticUnitForDay_.statisticDate), date));
		}
		cq.select(root.get(StatisticUnitForDay_.id));
		return em.createQuery(cq.where(p)).setMaxResults(62).getResultList();
	}

	/**
	 * 다음 페이지 정보 데이터 조회
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticUnitForDay> listIdsNextWithFilter(String id, Integer count, Object sequence,
			WrapInFilterStatisticUnitForDay wrapIn) throws Exception {
		// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForDay.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForDay> cq = cb.createQuery(StatisticUnitForDay.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForDay_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForDay_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForDay_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForDay_.sequence),sequence.toString()));
			}
		}
		/*if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.unitName).in(wrapIn.getEmployeeName()));
		}*/
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 이전 페이지 문서 정보 데이터 조회
	 * 
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<StatisticUnitForDay> listIdsPrevWithFilter(String id, Integer count, Object sequence,
			WrapInFilterStatisticUnitForDay wrapIn) throws Exception {
		// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForDay.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<StatisticUnitForDay> cq = cb.createQuery(StatisticUnitForDay.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, StatisticUnitForDay_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForDay_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(StatisticUnitForDay_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(StatisticUnitForDay_.sequence),sequence.toString()));
			}
		}
		/*if ((null != wrapIn.getEmployeeName()) && wrapIn.getEmployeeName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.unitName).in(wrapIn.getEmployeeName()));
		}*/
		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticMonth),wrapIn.getStatisticMonth()));
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
	public long getCountWithFilter(WrapInFilterStatisticUnitForDay wrapIn) throws Exception {
		// 이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( StatisticUnitForDay.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatisticUnitForDay> root = cq.from(StatisticUnitForDay.class);
		Predicate p = cb.isNotNull(root.get(StatisticUnitForDay_.id));

		if ((null != wrapIn.getUnitName()) && wrapIn.getUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.unitName).in(wrapIn.getUnitName()));
		}
		if ((null != wrapIn.getUnitName()) && wrapIn.getTopUnitName().size() > 0) {
			p = cb.and(p,root.get(StatisticUnitForDay_.topUnitName).in(wrapIn.getTopUnitName()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticYear())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticYear),wrapIn.getStatisticYear()));
		}
		if(StringUtils.isNotEmpty(wrapIn.getStatisticMonth())){
			p = cb.and(p,cb.equal(root.get(StatisticUnitForDay_.statisticMonth),wrapIn.getStatisticMonth()));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}