package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang3.StringUtils;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.CriteriaQueryTools;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.AttendanceCycles;
import com.x.attendance.assemble.control.jaxrs.attendancedetail.WrapInFilter;
import com.x.attendance.entity.AttendanceDetail;
import com.x.attendance.entity.AttendanceDetail_;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceDetailFactory extends AbstractFactory {
	
	public AttendanceDetailFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceDetail 정보 객체 조회")
	public AttendanceDetail get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceDetail.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 AttendanceDetail 정보 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("전체 AttendanceDetail 정보 목록 조회")
	public String getMaxRecordDate() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();		
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery( AttendanceDetail.class );
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);		
		cq.orderBy( cb.desc( root.get( AttendanceDetail_.recordDateString) ) );	
		List<AttendanceDetail> resultList = em.createQuery(cq).setMaxResults(1).getResultList();
		if( resultList == null || resultList.size() == 0 ){
			return null;
		}else{
			return resultList.get(0).getRecordDateString();
		}
	}
	
	//@MethodDescribe("직원 이름과 출퇴근 날짜로 AttendanceDetail 정보 목록 조회")
	public List<String> listByEmployeeNameAndDate( String employeeName, String recordDateString ) throws Exception {
		
		if( employeeName == null || recordDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  employeeName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.recordDateString ),  recordDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("직원 이름과 출퇴근 날짜로 AttendanceDetail 정보 목록 조회")
	public List<AttendanceDetail> listDetailByEmployeeNameAndDate( String employeeName, String recordDateString ) throws Exception {
		
		if( employeeName == null || recordDateString == null ){
			return null;
		}		
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  employeeName );
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.recordDateString ),  recordDateString ) );
		return em.createQuery(cq.where( p )).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceDetail 정보 목록 조회")
	public List<AttendanceDetail> list(List<String> ids) throws Exception {
		List<AttendanceDetail> resultList = null;
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceDetail>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceDetail.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = root.get(AttendanceDetail_.id).in(ids);
		resultList = em.createQuery( cq.where(p) ).getResultList();
		if( resultList == null ){
			resultList = new ArrayList<AttendanceDetail>();
		}
		return resultList;
	}

	/**
	 * 시간 범위 내 모든 출퇴근 기록 분석
	 * 1、시간이 전달되지 않았거나 잘못되었으면 분석되지 않은 모든 출퇴근 기록 분석
	 * 2、보관되지 않은 것만 분석하고 이미 보관된 것은 더 이상 분석하지 않음
	 * @param startDateString
	 * @param endDateString
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 시작·종료 시간으로 분석되지 않은 AttendanceDetail 정보 목록 조회")
	public List<String> getAllAnalysenessDetails(String startDateString, String endDateString, String personName ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//일반적으로 항상 true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //보관되지 않은 것만 다시 분석
		if( StringUtils.isNotEmpty( personName ) ) {
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName ), personName)); //직원 이름 일치
		}
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//시작·종료 시간 값이 있으면 해당 기간의 모든 출퇴근 기록을 분석하며, 이미 분석된 것도 다시 분석
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString과 endDateString이 모두 비어 있으면 분석되지 않은 것만 분석
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //미분석
				statusArray.add( -1 ); //오류 있음
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.id ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	/**
	 * 시간 범위 내 모든 출퇴근 기록 분석
	 * 1、시간이 전달되지 않았거나 잘못되었으면 분석되지 않은 모든 출퇴근 기록 분석
	 * 2、보관되지 않은 것만 분석하고 이미 보관된 것은 더 이상 분석하지 않음
	 * 3、forceFlag가 true이면 (미분석, 분석 완료, 오류) 모두 분석
	 * @param startDateString
	 * @param endDateString
	 * @param personName
	 * @param forceFlag
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 시작·종료 시간으로 분석되지 않은 AttendanceDetail 정보 목록 조회")
	public List<String> getAllAnalysenessDetailsForce(String startDateString, String endDateString, String personName ,Boolean forceFlag) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//일반적으로 항상 true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //보관되지 않은 것만 다시 분석
		if( StringUtils.isNotEmpty( personName ) ) {
			p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName ), personName)); //직원 이름 일치
		}
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//시작·종료 시간 값이 있으면 해당 기간의 모든 출퇴근 기록을 분석하며, 이미 분석된 것도 다시 분석
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString과 endDateString이 모두 비어 있으면 분석되지 않은 것만 분석
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //미분석
				if(forceFlag){
					statusArray.add( 1 ); //분석 완료
				}
				statusArray.add( -1 ); //오류 있음
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.id ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	/**
	 * 시간 범위 내 모든 출퇴근 기록 분석
	 * 1、시간이 전달되지 않았거나 잘못되었으면 분석되지 않은 모든 출퇴근 기록 분석
	 * 2、보관되지 않은 것만 분석하고 이미 보관된 것은 더 이상 분석하지 않음
	 * @param startDateString
	 * @param endDateString
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 시작·종료 시간으로 분석되지 않은 직원 이름 목록 조회")
	public List<String> getAllAnalysenessPersonNames(String startDateString, String endDateString ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//일반적으로 항상 true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //보관되지 않은 것만 다시 분석
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//시작·종료 시간 값이 있으면 해당 기간의 모든 출퇴근 기록을 분석하며, 이미 분석된 것도 다시 분석
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString과 endDateString이 모두 비어 있으면 분석되지 않은 것만 분석
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //미분석
				statusArray.add( -1 ); //오류 있음
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		/*cq.distinct(true).select( root.get( AttendanceDetail_.empName ) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();*/
		cq.select( root.get( AttendanceDetail_.empName ) ).where(p);
		return em.createQuery(cq).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * 시간 범위 내 모든 출퇴근 기록 분석(분석 완료/미분석 선택 가능)
	 * 1、시간이 전달되지 않았거나 잘못되었으면 분석되지 않은 모든 출퇴근 기록 분석
	 * 2、보관되지 않은 것만 분석하고 이미 보관된 것은 더 이상 분석하지 않음
	 * 3、forceFlag가 true이면 (미분석, 분석 완료, 오류) 모두 분석
	 * @param startDateString
	 * @param endDateString
	 * @param forceFlag
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 시작·종료 시간으로 직원 이름 목록 조회")
	public List<String> getAllAnalysenessPersonNamesForce(String startDateString, String endDateString ,Boolean forceFlag) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		
		//일반적으로 항상 true, id is not null
		Predicate p = root.get( AttendanceDetail_.id ).isNotNull();
		p = cb.and( p, root.get( AttendanceDetail_.archiveTime ).isNull()); //보관되지 않은 것만 다시 분석
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//시작·종료 시간 값이 있으면 해당 기간의 모든 출퇴근 기록을 분석하며, 이미 분석된 것도 다시 분석
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString과 endDateString이 모두 비어 있으면 분석되지 않은 것만 분석
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //미분석
				if(forceFlag){
					statusArray.add( 1 ); //분석 완료
				}
				statusArray.add( -1 ); //오류 있음
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		
		cq.select( root.get( AttendanceDetail_.empName ) ).where(p);
		return em.createQuery(cq).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 시작·종료 시간으로 분석되지 않은 AttendanceDetail 정보 목록 조회")
	public List<String> getUserAnalysenessDetails(String empName, String startDateString, String endDateString) throws Exception {
		DateOperation dateOperation = new DateOperation();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));

		Predicate p = cb.equal( root.get(AttendanceDetail_.empName),  empName );
		
		Date startDate = null;
		Date endDate = null;
		try{
			startDate = dateOperation.getDateFromString( startDateString );
		}catch(Exception e){
			startDate = null;
		}
		try{
			endDate = dateOperation.getDateFromString( endDateString );
		}catch(Exception e){
			endDate = null;
		}
		//시작·종료 시간 값이 있으면 해당 기간의 모든 출퇴근 기록을 분석하며, 이미 분석된 것도 다시 분석
		if( startDate != null  && endDate != null ){
			p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, endDate));
		}else{
			if( startDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), startDate, new Date()));
			}
			if( endDate != null ){
				p = cb.and( p, cb.between( root.get( AttendanceDetail_.recordDate), new Date(), endDate));
			}
			if( startDate == null && endDate == null ){
				//startDateString과 endDateString이 모두 비어 있으면 분석되지 않은 것만 분석
				List<Integer> statusArray = new ArrayList<Integer>();
				statusArray.add( 0 ); //미분석
				statusArray.add( -1 ); //오류 있음
				p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
			}
		}
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("지정한 연도·월로 AttendanceDetail 정보 목록 조회")
	public List<AttendanceDetail> getDetailsByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보 목록 조회")
	public List<AttendanceDetail> getDetailsByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("지정한 연도·월로 분석된 AttendanceDetail 정보의 조직 목록 조회")
	public List<String> getDetailsUnitsByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	/**
	 * 지각, 결근, 조퇴, 근무시간 부족, 비정상 체크 중 이의 신청이 승인되지 않은 것
	 * @param year
	 * @param month
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("내보낼 모든 이상 데이터(이의 신청 안 한 것, 이의 신청 미승인) 조회")
	public List<String> getDetailsWithAllAbnormalCase( String year, String month ) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		
		Predicate p = cb.lessThan( root.get(AttendanceDetail_.appealStatus), 9); //9이면 이의 신청 승인
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		
		Predicate orCase = cb.isTrue(root.get(AttendanceDetail_.isLate)); //지각
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isLeaveEarlier)) ); //또는 조퇴
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isAbnormalDuty) )); //또는 비정상 체크
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isAbsent) )); //또는 결근
		orCase = cb.or( orCase, cb.isTrue( root.get(AttendanceDetail_.isLackOfTime) )); //또는 근무시간 부족
		
		Predicate where = cb.and( p, orCase );
		
		return em.createQuery(cq.where(where)).setMaxResults(20000).getResultList();
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 조직 목록 조회")
	public List<String> getDetailsUnitsByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 최상위 조직 이름 목록 조회")
	public List<String> distinctDetailsTopUnitNamesByYearAndMonth(String year, String month) throws Exception {
		if( year == null || month == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.topUnitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		return em.createQuery(cq.where(p)).setMaxResults(1000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 직원 이름 목록 조회")
	public List<String> distinctDetailsEmployeeNamesByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.empName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 최상위 조직 이름 목록 조회")
	public List<String> distinctDetailsTopUnitNamesByCycleYearAndMonth(String cycleYear, String cycleMonth) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.topUnitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 조직 이름 목록 조회")
	public List<String> distinctDetailsUnitNamesByCycleYearAndMonth( String cycleYear, String cycleMonth ) throws Exception {
		if( cycleYear == null || cycleMonth == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("지정한 통계 주기 연도·월로 AttendanceDetail 정보의 조직 이름 목록 조회")
	public List<String> distinctDetailsUnitNamesByCycleYearAndMonth( String cycleYear, String cycleMonth, String employeeName ) throws Exception {
		if( cycleYear == null || cycleMonth == null || employeeName == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.unitName ));
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), cycleYear ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), cycleMonth ));
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), employeeName ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList().stream().distinct().collect(Collectors.toList());
	}


	//@MethodDescribe("batchName으로 한 번에 가져온 모든 데이터 기록 목록 조회")
	public List<String> listByBatchName( String file_id ) throws Exception{
		if( file_id == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.batchName), file_id );
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}

	//@MethodDescribe("연도·월로 특정 사용자의 출퇴근 데이터 기록 목록 조회")
	public List<String> listUserAttendanceDetailByYearAndMonth(String user, String year, String month)  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("주기 연도·월로 특정 사용자의 출퇴근 데이터 기록 목록 조회")
	public List<String> listUserAttendanceDetailByCycleYearAndMonth(String user, String year, String month)  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listDetailByCycleYearAndMonthWithOutStatus( String user, String year, String month )  throws Exception {
		if( user == null || user.isEmpty() ||year == null || month == null || year.isEmpty() || month.isEmpty()  ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.isNotNull( root.get(AttendanceDetail_.id) );
		if( StringUtils.isNotEmpty( user ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleYear), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.cycleMonth), month ));
		}

		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//사용자와 출퇴근 날짜로 출퇴근 기록 조회
		public List<String> listDetailByNameAndDate( String user, String dateStr )  throws Exception {
			if( user == null || user.isEmpty() ||dateStr == null ){
				return null;
			}
			EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
			cq.select( root.get(AttendanceDetail_.id ));
			//일반적으로 항상 true, id is not null
			Predicate p = cb.isNotNull( root.get(AttendanceDetail_.id) );
			if( StringUtils.isNotEmpty( user ) ){
				p = cb.and(p, cb.equal( root.get(AttendanceDetail_.empName), user ));
			}
			if( StringUtils.isNotEmpty( dateStr ) ){
				p = cb.and(p, cb.equal( root.get(AttendanceDetail_.recordDateString), dateStr ));
			}
			
	
			return em.createQuery(cq.where(p)).getResultList();
		}
	
	//@MethodDescribe("연도·월로 특정 조직의 출퇴근 데이터 기록 목록 조회")
	public List<String> listUnitAttendanceDetailByYearAndMonth( List<String> unitNames, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( unitNames != null && unitNames.size() > 0 ){
			p = cb.and(p, root.get(AttendanceDetail_.unitName).in(unitNames));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("연도·월로 특정 최상위 조직의 출퇴근 데이터 기록 목록 조회")
	public List<String> listTopUnitAttendanceDetailByYearAndMonth(List<String> topUnitNames, String year, String month)  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.equal( root.get(AttendanceDetail_.recordStatus), 1 );
		if( ListTools.isNotEmpty(  topUnitNames ) ){
			p = cb.and(p, root.get(AttendanceDetail_.topUnitName).in( topUnitNames ));
		}
		if( StringUtils.isNotEmpty( year ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.yearString), year ));
		}
		if( StringUtils.isNotEmpty( month ) ){
			p = cb.and(p, cb.equal( root.get(AttendanceDetail_.monthString), month ));
		}
		return em.createQuery(cq.where(p)).getResultList();
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
	public List<AttendanceDetail> listIdsNextWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}	
	
	/**
	 * 다음 페이지 정보 데이터 조회(근태 미참여 사용자 제외)
	 * @param id
	 * @param count
	 * @param sequence
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<AttendanceDetail> listIdsNextWithFilterUn( String id, Integer count, Object sequence, WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}

	/**
	 * 조건으로 근태 정보 ids 조회(근태 미참여 사용자 제외)
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	//사용자와 출퇴근 날짜로 출퇴근 기록 조회
	public List<AttendanceDetail> listIdsWithFilterUn( WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList )  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.getResultList();
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
	public List<AttendanceDetail> listIdsPrevWithFilter( String id, Integer count, Object sequence, WrapInFilter wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceDetail> cq = cb.createQuery(AttendanceDetail.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);

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
		Order _order = CriteriaQueryTools.setOrder(cb, root, AttendanceDetail_.class, orderFieldName,order);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));
		if ((null != sequence) ) {
			if(StringUtils.equalsIgnoreCase(order, "DESC")){
				p = cb.and(p,cb.greaterThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}else{
				p = cb.and(p,cb.lessThan(root.get(AttendanceDetail_.sequence),sequence.toString()));
			}
		}
		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		Query query = em.createQuery(cq.select(root).where(p).orderBy(_order) );
		return query.setMaxResults(count).getResultList();
	}
	
	/**
	 * 조건에 맞는 문서 정보 총수 조회
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilter( WrapInFilter wrapIn ) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));

		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
	
	/**
	 * 조건에 맞는 문서 정보 총수 조회(근태 미참여 사용자 제외)
	 * @param wrapIn
	 * @return
	 * @throws Exception
	 */
	public long getCountWithFilterUn( WrapInFilter wrapIn ,List<String> unUnitNameList,List<String> personNameList) throws Exception {
		//이전 페이지 마지막 항목의 sequence 값을 먼저 가져와, 값이 있으면 이 sequence 값을 기준으로 이후 count건 조회
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<AttendanceDetail> root = cq.from(AttendanceDetail.class);
		Predicate p = cb.isNotNull(root.get(AttendanceDetail_.id));

		if ((null != wrapIn.getQ_empName()) && (!wrapIn.getQ_empName().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.empName),wrapIn.getQ_empName()));
		}
		if ((null != wrapIn.getUnitNames()) && wrapIn.getUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.unitName).in(wrapIn.getUnitNames()));
		}
		if ((null != wrapIn.getTopUnitNames()) && wrapIn.getTopUnitNames().size() > 0) {
			p = cb.and(p,root.get(AttendanceDetail_.topUnitName).in(wrapIn.getTopUnitNames()));
		}
		if ((null != wrapIn.getCycleYear()) && (!wrapIn.getCycleYear().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleYear),wrapIn.getCycleYear()));
		}
		if ((null != wrapIn.getCycleMonth()) && (!wrapIn.getCycleMonth().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.cycleMonth),wrapIn.getCycleMonth()));
		}
		if ((null != wrapIn.getQ_year() ) && (!wrapIn.getQ_year().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.yearString),wrapIn.getQ_year()));
		}
		if ((null != wrapIn.getQ_month()) && (!wrapIn.getQ_month().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.monthString),wrapIn.getQ_month()));
		}
		if ((null != wrapIn.getQ_date()) && (!wrapIn.getQ_date().isEmpty())) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordDateString),wrapIn.getQ_date()));
		}
		if ( wrapIn.getRecordStatus() != 999 ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.recordStatus),wrapIn.getRecordStatus()));
		}
		if (wrapIn.getIsAbsent() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isAbsent),wrapIn.getIsAbsent()));
		}
		if (wrapIn.getIsLate() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLate),wrapIn.getIsLate()));
		}
		if (wrapIn.getIsLackOfTime() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLackOfTime),wrapIn.getIsLackOfTime()));
		}
		if (wrapIn.getIsLeaveEarlier() != null ) {
			p = cb.and(p,cb.equal(root.get(AttendanceDetail_.isLeaveEarlier),wrapIn.getIsLeaveEarlier()));
		}
		if (ListTools.isNotEmpty(unUnitNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.unitName).in(unUnitNameList)));
		}
		if (ListTools.isNotEmpty(personNameList)) {
			p = cb.and(p,cb.not(root.get(AttendanceDetail_.empName).in(personNameList)));
		}

		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getByUserAndRecordDate(String employeeName, String recordDateStringFormated)  throws Exception{		
		if( employeeName == null || employeeName.isEmpty() || recordDateStringFormated == null || recordDateStringFormated.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.equal( root.get(AttendanceDetail_.empName), employeeName );
		p = cb.and(p, cb.equal( root.get(AttendanceDetail_.recordDateString), recordDateStringFormated ));	
		cq.select( root.get(AttendanceDetail_.id ));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	
	public List<AttendanceCycles> getCyclesFromDetailWithDateSplit( Date startDate, Date endDate )  throws Exception{
		if( startDate == null || startDate == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceCycles> cq = cb.createQuery(AttendanceCycles.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.between( root.get(AttendanceDetail_.recordDate), startDate, endDate);
		
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		selectionList.add(root.get(AttendanceDetail_.cycleYear ));
		selectionList.add(root.get(AttendanceDetail_.cycleMonth ));
		cq.multiselect(selectionList);
		
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	public List<AttendanceCycles> getCyclesFromDetailWithDateSplit( String empName, Date startDate, Date endDate )  throws Exception{
		if( startDate == null || startDate == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceCycles> cq = cb.createQuery(AttendanceCycles.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		Predicate p = cb.between( root.get(AttendanceDetail_.recordDate), startDate, endDate);
		p = cb.and( p, cb.equal( root.get(AttendanceDetail_.empName), empName));
		List<Selection<?>> selectionList = new ArrayList<Selection<?>>();
		selectionList.add(root.get(AttendanceDetail_.cycleYear ));
		selectionList.add(root.get(AttendanceDetail_.cycleMonth ));
		cq.multiselect(selectionList);
		
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
	
	//@MethodDescribe("보관되지 않은 출퇴근 기록 목록 조회, 최대 2000건")
	public List<String> listNonArchiveDetailInfoIds()  throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select( root.get(AttendanceDetail_.id ));
		//일반적으로 항상 true, id is not null
		Predicate p = cb.isNotNull( root.get(AttendanceDetail_.archiveTime) );
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}

	public List<String> listAnalysenessDetailsByEmployee( String empName ) throws Exception {
		List<Integer> statusArray = new ArrayList<Integer>();
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.empName ), empName );
		statusArray.add( 0 ); //미분석
		statusArray.add( -1 ); //오류 있음
		p = cb.and( p, root.get( AttendanceDetail_.recordStatus).in( statusArray ));
		return em.createQuery(cq.where(p)).setMaxResults(20000).getResultList();
	}

	public List<String> listRecordWithDateAndNoOffDuty( String recordDate ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate );
		Predicate offDutyTime_1 = cb.isNull(root.get( AttendanceDetail_.offDutyTime ));
		Predicate offDutyTime_2 = cb.equal( root.get( AttendanceDetail_.offDutyTime ), "" );
		p = cb.and( p, cb.or( offDutyTime_1, offDutyTime_2 ));
		return em.createQuery(cq.where(p)).setMaxResults(100000).getResultList();
	}

	/**
	 * 지정 마감일 이전에 이미 출퇴근 체크한 사용자 조회
	 * @param deadline
	 * @param type:all#onDuty#offDuty#morningOffDuty#afternoonOnDuty
	 * @return
	 * @throws Exception
	 */
    public List<String> listSignedPersonsWithDeadLine( String deadline, String type ) throws Exception {
		DateOperation dateOperation = new DateOperation();
		if( StringUtils.isEmpty( deadline )){
			deadline = dateOperation.getNowDateTime();
		}
		Date deadlineDate = dateOperation.getDateFromString( deadline, "yyyy-MM-dd HH:mm:ss");
		String recordDate = dateOperation.getDate( deadlineDate, "yyyy-MM-dd");
		String deadlineTime = dateOperation.getDate( deadlineDate, "HH:mm:ss");

		EntityManager em = this.entityManagerContainer().get( AttendanceDetail.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceDetail> root = cq.from( AttendanceDetail.class);
		cq.select(root.get(AttendanceDetail_.id));
		Predicate p = cb.equal( root.get( AttendanceDetail_.recordDateString ), recordDate );
		if( StringUtils.equalsAnyIgnoreCase("onDuty", type)){
			Predicate p_type = cb.lessThan( root.get( AttendanceDetail_.onDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("offDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.offDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("morningOffDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.morningOffDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else if(StringUtils.equalsAnyIgnoreCase("afternoonOnDuty", type)){
			Predicate p_type = cb.lessThan(root.get( AttendanceDetail_.afternoonOnDutyTime ), deadlineTime);
			p = cb.and( p, p_type );
		}else{
			Predicate p_or = cb.or(
					cb.lessThan( root.get( AttendanceDetail_.onDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.morningOffDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.afternoonOnDutyTime ), deadlineTime),
					cb.lessThan(root.get( AttendanceDetail_.offDutyTime ), deadlineTime)
			);
			p = cb.and( p, p_or );
		}
		cq.select(root.get(AttendanceDetail_.empName));
		return em.createQuery(cq.where(p)).setMaxResults(100000).getResultList().stream().distinct().collect(Collectors.toList());
	}
}