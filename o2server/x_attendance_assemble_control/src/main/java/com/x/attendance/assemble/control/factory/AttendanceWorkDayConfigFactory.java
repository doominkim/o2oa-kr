package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.common.date.DateOperation;
import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.service.AttendanceSettingServiceAdv;
import com.x.attendance.entity.AttendanceWorkDayConfig;
import com.x.attendance.entity.AttendanceWorkDayConfig_;
import com.x.base.core.project.exception.ExceptionWhen;

/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceWorkDayConfigFactory extends AbstractFactory {

	private DateOperation dateOperation = new DateOperation();
	private AttendanceSettingServiceAdv attendanceSettingServiceAdv = new AttendanceSettingServiceAdv();

	public AttendanceWorkDayConfigFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceWorkDayConfig 정보 객체 조회")
	public AttendanceWorkDayConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceWorkDayConfig.class, ExceptionWhen.none);
	}

	//	@MethodDescribe("전체 AttendanceWorkDayConfig 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceWorkDayConfig> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkDayConfig> cq = cb.createQuery(AttendanceWorkDayConfig.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		return em.createQuery(cq).getResultList();
	}

	//@MethodDescribe("지정 Id의 AttendanceWorkDayConfig 정보 목록 조회")
	public List<AttendanceWorkDayConfig> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceWorkDayConfig>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkDayConfig> cq = cb.createQuery(AttendanceWorkDayConfig.class);
		Root<AttendanceWorkDayConfig> root = cq.from(AttendanceWorkDayConfig.class);
		Predicate p = root.get(AttendanceWorkDayConfig_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("연도·월로 전체 AttendanceWorkDayConfig 정보 목록 조회")
	public List<String> listByYearAndMonth( String year, String month ) throws Exception {
		if( year == null ){
			return null;
		}
		if( "0".equals(month) || "00".equals(month) || "(0)".equals(month)){
			month = null;
		}

		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));

		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configYear), year);
		if( month != null ){
			p = cb.and( p, cb.equal( root.get(AttendanceWorkDayConfig_.configMonth), month));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("연도와 공휴일 이름으로 전체 AttendanceWorkDayConfig 정보 목록 조회")
	public List<String> listByYearAndName( String year, String configName ) throws Exception {
		if( year == null ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));

		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configYear), year);
		if( configName != null ){
			p = cb.and( p, cb.equal( root.get(AttendanceWorkDayConfig_.configName), configName));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("공휴일 이름으로 전체 AttendanceWorkDayConfig 정보 목록 조회")
	public List<String> listByName( String configName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkDayConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceWorkDayConfig> root = cq.from( AttendanceWorkDayConfig.class);
		cq.select(root.get(AttendanceWorkDayConfig_.id));

		Predicate p = cb.equal( root.get(AttendanceWorkDayConfig_.configName), configName);

		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("공휴일 설정으로 한 달의 출근 필요 일수 계산")
	public int getWorkDaysCountForMonth( String s_year, String _month, List<AttendanceWorkDayConfig> workDayConfigList ) throws Exception {
		/**
		 * 1、당월 총 일수 계산
		 * 2、매일 날짜 순회
		 *    1)주말 여부 판단
		 *    2)근무일 조정 여부 판단
		 *    3)공휴일 여부 판단
		 */
		int workDaysCountForMonth = 0;
		//1、당월 총 일수 계산
		int daysCountForMonth = dateOperation.getDaysForMonth( s_year + "-" + _month + "-01" );
		workDaysCountForMonth = daysCountForMonth; //먼저 매일을 근무일로 가정
		//2、매일 날짜 순회
		String dateString = null;
		boolean isHoliday = true;
		for( int i = 1; i<= daysCountForMonth ; i++ ){
			isHoliday = true;
			dateString = s_year + "-" + _month + "-" + (i<10?"0"+i:i);
			//당일 주말 여부 판단
			//if( !dateOperation.isWeekend( dateOperation.getDateFromString( dateString )) ){
			if( !attendanceSettingServiceAdv.isWeekend( dateOperation.getDateFromString( dateString )) ){
				//주말이 아니면
				if( workDayConfigList != null && workDayConfigList.size() > 0 ){
					//모든 공휴일 설정을 순회하며 법정 공휴일 여부 판단
					for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
						if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
						){
							workDaysCountForMonth--;//당일은 근무일이 아니므로 당월 출근일에서 하루 차감.
							break;
						}
					}
				}
			}else{
				//주말이면
				if( workDayConfigList != null && workDayConfigList.size() > 0 ){
					//모든 공휴일 설정을 순회하며 대체 근무일 여부 판단
					for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
						if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
						){
							isHoliday = false; //설정된 대체 근무일임
							break;
						}
					}
				}
				if( isHoliday ){
					//설정된 대체 근무일이 아니면 당일은 근무일이 아니므로 당월 출근일에서 하루 차감.
					workDaysCountForMonth--;
				}
			}
		}
		return workDaysCountForMonth;
	}

	//@MethodDescribe("공휴일 설정으로 한 주기의 출근 필요 일수 계산")
	public Integer getWorkDaysCountForMonth( Date startDate, Date endDate, List<AttendanceWorkDayConfig> workDayConfigList ) throws Exception {
		/**
		 * 1、당월 총 일수 계산
		 * 2、매일 날짜 순회
		 *    1)주말 여부 판단
		 *    2)근무일 조정 여부 판단
		 *    3)공휴일 여부 판단
		 */
		boolean isHoliday = true;
		int workDaysCountForMonth = 0;
		if( endDate.getTime() > new Date().getTime()){
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(calendar.DATE,-1);
			endDate = calendar.getTime();
		}
		List<String> dateStringList = dateOperation.listDateStringBetweenDate(startDate, endDate);
		if( dateStringList != null && dateStringList.size() > 0 ){
			workDaysCountForMonth = dateStringList.size();
			for( String dateString : dateStringList){
				//당일 주말 여부 판단
				if(!attendanceSettingServiceAdv.isWeekend( dateOperation.getDateFromString( dateString ))  ){
					//주말이 아니면
					if( workDayConfigList != null && workDayConfigList.size() > 0 ){
						//모든 공휴일 설정을 순회하며 법정 공휴일 여부 판단
						for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
							if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Holiday".equalsIgnoreCase( workDayConfig.getConfigType() )
							){
								workDaysCountForMonth--;//당일은 근무일이 아니므로 당월 출근일에서 하루 차감.
								break;
							}
						}
					}
				}else{
					//주말이면
					if( workDayConfigList != null && workDayConfigList.size() > 0 ){
						//모든 공휴일 설정을 순회하며 대체 근무일 여부 판단
						for( AttendanceWorkDayConfig workDayConfig : workDayConfigList ){
							if( workDayConfig.getConfigDate().trim().equals( dateString ) && "Workday".equalsIgnoreCase( workDayConfig.getConfigType() )
							){
								isHoliday = false; //설정된 대체 근무일임
								break;
							}
						}
					}
					if( isHoliday ){
						//설정된 대체 근무일이 아니면 당일은 근무일이 아니므로 당월 출근일에서 하루 차감.
						workDaysCountForMonth--;
					}
				}
			}
		}
		return workDaysCountForMonth;
	}
}