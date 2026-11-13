package com.x.attendance.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.attendance.assemble.control.AbstractFactory;
import com.x.attendance.assemble.control.Business;
import com.x.attendance.entity.AttendanceStatisticalCycle;
import com.x.attendance.entity.AttendanceStatisticalCycle_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceStatisticalCycleFactory extends AbstractFactory {
	
	public AttendanceStatisticalCycleFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceStatisticalCycle 정보 객체 조회")
	public AttendanceStatisticalCycle get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceStatisticalCycle.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 AttendanceStatisticalCycle 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceStatisticalCycle> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticalCycle.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticalCycle> cq = cb.createQuery( AttendanceStatisticalCycle.class );
		Root<AttendanceStatisticalCycle> root = cq.from( AttendanceStatisticalCycle.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceStatisticalCycle 정보 목록 조회")
	public List<AttendanceStatisticalCycle> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceStatisticalCycle>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticalCycle.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceStatisticalCycle> cq = cb.createQuery(AttendanceStatisticalCycle.class);
		Root<AttendanceStatisticalCycle> root = cq.from(AttendanceStatisticalCycle.class);
		Predicate p = root.get(AttendanceStatisticalCycle_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("매개변수로 AttendanceStatisticalCycle 정보 목록 조회")
	public List<String> listByParameter( String topUnitName, String organizatinName, String cycleYear, String cycleMonth ) throws Exception{
		EntityManager em = this.entityManagerContainer().get(AttendanceStatisticalCycle.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceStatisticalCycle> root = cq.from(AttendanceStatisticalCycle.class);
		Predicate p = cb.equal( root.get( AttendanceStatisticalCycle_.cycleYear), cycleYear);		
		if( topUnitName != null ){ 
			 p = cb.and( p, cb.equal( root.get( AttendanceStatisticalCycle_.topUnitName), topUnitName));
		}
		if( organizatinName != null ){ 
			 p = cb.and( p, cb.equal( root.get( AttendanceStatisticalCycle_.unitName), organizatinName));
		}
		if( cycleMonth != null ){ 
			 p = cb.and( p, cb.equal( root.get( AttendanceStatisticalCycle_.cycleMonth), cycleMonth));
		}
		cq.select(root.get(AttendanceStatisticalCycle_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}