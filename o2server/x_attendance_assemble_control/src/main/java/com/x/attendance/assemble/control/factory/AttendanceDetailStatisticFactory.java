package com.x.attendance.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetail_;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * 출퇴근 정보 통계 서비스 클래스, 출퇴근 정보 테이블 기준 통계
 */
public class AttendanceDetailStatisticFactory extends AbstractFactory {

	private static  Logger logger = LoggerFactory.getLogger( AttendanceDetailStatisticFactory.class );
	
	public AttendanceDetailStatisticFactory(Business business) throws Exception {
		super(business);
	}

	/**
	 * 직원, 연월 기준 비정상 체크 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByEmployeeCycleYearAndMonth(List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 근무시간 부족 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 조퇴 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 지각 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLateByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 퇴근 체크 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 직원, 연월 기준 횟수 통계, 즉 출근 일수
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countDutyDaysByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception {
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordStatus ), 1);
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 퇴근 체크 횟수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByEmployeeCycleYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.empName).in( employeeNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 연월 기준 휴가 일수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByEmployeeYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		// 주말이 아니거나, 주말이지만 근무일로 조정됨
		Predicate p1 = cb.isFalse(root.get( AttendanceDetail_.isWeekend ));
		p1 = cb.or( p1, cb.and(cb.isTrue(root.get( AttendanceDetail_.isWeekend )), cb.isTrue(root.get( AttendanceDetail_.isWorkday )) ));
								
		Predicate p = root.get( AttendanceDetail_.empName).in( employeeNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isFalse(root.get( AttendanceDetail_.isHoliday ))); //공휴일 아님
		p = cb.and( p, p1 ); //주말이 아니며 대체 근무일로 조정되지 않음
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear ));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		}
		
		//총수 조회
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	
	
	/**
	 * 직원, 연월 기준 결근 일수 통계
	 * @param employeeNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByEmployeeYearAndMonth( List<String> employeeNames, String cycleYear, String cycleMonth) throws Exception{
		if( employeeNames == null || employeeNames.size() == 0 ){
			logger.error( new EmployeeNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get(AttendanceDetail_.empName).in( employeeNames );		
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbsent) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear));
		}
		
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 연월 기준 비정상 체크 횟수 통계
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByUnitCycleYearAndMonth(List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 연월 기준 근무시간 부족 횟수 통계
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 연월 기준 조퇴 횟수 통계
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 연월 기준 지각 횟수 통계
	 * @param unitNames
	 * @param cycleYear
	 * @param cycleMonth
	 * @return
	 * @throws Exception
	 */
	public Long countLateByUnitCycleYearAndMonth( List<String> unitNames, String cycleYear, String cycleMonth) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( cycleYear == null || cycleYear.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleYear), cycleYear));
		}
		if( cycleMonth == null || cycleMonth.isEmpty() ){
			logger.error( new CycleMonthEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.cycleMonth), cycleMonth));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 비정상 체크 횟수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByUnitAndDate(List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 근무시간 부족 횟수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 조퇴 횟수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 지각 횟수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 출퇴근 날짜 기준 결근 일수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 직원, 출퇴근 날짜·월 기준 휴가 일수 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.unitName ).in( unitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}		
		//총수 조회
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 퇴근 체크 인원 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByUnitAndDate( List<String> unitNames, String recordDate ) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.unitName).in( unitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.unitName), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 조직, 출퇴근 날짜 기준 출근 체크 인원 통계
	 * @param unitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByUnitAndDate( List<String> unitNames, String recordDate) throws Exception{
		if( unitNames == null || unitNames.size() == 0 ){
			logger.error( new UnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.unitName ).in( unitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new CycleYearEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.unitName), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 비정상 체크 횟수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countAbNormalDutyByTopUnitAndDate(List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isAbnormalDuty) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 근무시간 부족 횟수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLackOfTimeByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLackOfTime) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 조퇴 횟수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLeaveEarlierByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLeaveEarlier ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 지각 횟수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countLateByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		p = cb.and( p, cb.isTrue( root.get( AttendanceDetail_.isLate ) ));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 결근 일수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumAbsenceDaysByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);	
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}
		//총수 조회
		cq.select( cb.sum( root.get(AttendanceDetail_.absence ) ) );	
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜·월 기준 휴가 일수 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Double sumOnSelfHolidayDaysByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Double> cq = cb.createQuery(Double.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);			
		Predicate p = root.get( AttendanceDetail_.topUnitName ).in( topUnitNames );
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate));
		}		
		//총수 조회
		cq.select( cb.sum( root.get( AttendanceDetail_.getSelfHolidayDays ) ) );	
				
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 출근 체크 인원 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOffDutyByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.offDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.offDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.topUnitName ).in( topUnitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
	
	/**
	 * 최상위 조직, 출퇴근 날짜 기준 출근 체크 인원 통계
	 * @param topUnitNames
	 * @param recordDate
	 * @return
	 * @throws Exception
	 */
	public Long countOnDutyByTopUnitAndDate( List<String> topUnitNames, String recordDate ) throws Exception{
		if( topUnitNames == null || topUnitNames.size() == 0 ){
			logger.error( new TopUnitNamesEmptyException() );
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get( AttendanceDetail_.onDutyTime ));
		p = cb.and( p, cb.notEqual( root.get( AttendanceDetail_.onDutyTime), ""));
		p = cb.and( p, root.get( AttendanceDetail_.topUnitName).in( topUnitNames ));
		p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordStatus ), 1));
		if( recordDate == null || recordDate.isEmpty() ){
			logger.error( new RecordDateEmptyException() );
		}else{
			p = cb.and( p, cb.equal( root.get( AttendanceDetail_.recordDateString), recordDate));
		}
		//총수 조회
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}
}