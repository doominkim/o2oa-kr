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
import com.x.attendance.entity.AttendanceScheduleSetting;
import com.x.attendance.entity.AttendanceScheduleSetting_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceScheduleSettingFactory extends AbstractFactory {
	
	public AttendanceScheduleSettingFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceScheduleSetting 앱 정보 객체 조회")
	public AttendanceScheduleSetting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceScheduleSetting.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe("전체 AttendanceScheduleSetting 앱 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceScheduleSetting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceScheduleSetting> cq = cb.createQuery(AttendanceScheduleSetting.class);
		Root<AttendanceScheduleSetting> root = cq.from( AttendanceScheduleSetting.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceScheduleSetting 앱 정보 목록 조회")
	public List<AttendanceScheduleSetting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceScheduleSetting>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceScheduleSetting> cq = cb.createQuery(AttendanceScheduleSetting.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = root.get(AttendanceScheduleSetting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("조직 이름으로 조직 근무 편성 정보 객체 조회")
	public List<String> listByUnitName( String unitName ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = cb.equal(root.get(AttendanceScheduleSetting_.unitName), unitName );
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("조직 이름으로 조직 근무 편성 정보 객체 조회")
	public List<String> listByUnitNames( List<String> unitNameList ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = root.get(AttendanceScheduleSetting_.unitName).in(unitNameList);
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("최상위 조직 이름으로 최상위 조직 근무 편성 정보 객체 조회")
	public List<String> listByTopUnitName( String topUnitName ) throws Exception{		
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = cb.equal(root.get(AttendanceScheduleSetting_.topUnitName), topUnitName );
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByUnitName( String unitName, String topUnitName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceScheduleSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceScheduleSetting> root = cq.from(AttendanceScheduleSetting.class);
		Predicate p = cb.equal(root.get(AttendanceScheduleSetting_.topUnitName), topUnitName );
		p = cb.and( p, cb.equal(root.get(AttendanceScheduleSetting_.unitName), unitName ) );
		cq.select(root.get(AttendanceScheduleSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}