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
import com.x.attendance.entity.AttendanceSetting;
import com.x.attendance.entity.AttendanceSetting_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceSettingFactory extends AbstractFactory {
	
	public AttendanceSettingFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceSetting 앱 정보 객체 조회")
	public AttendanceSetting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceSetting.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 AttendanceSetting 앱 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceSetting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSetting> cq = cb.createQuery(AttendanceSetting.class);
		Root<AttendanceSetting> root = cq.from( AttendanceSetting.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceSetting 앱 정보 목록 조회")
	public List<AttendanceSetting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceSetting>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSetting> cq = cb.createQuery(AttendanceSetting.class);
		Root<AttendanceSetting> root = cq.from(AttendanceSetting.class);
		Predicate p = root.get(AttendanceSetting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("CODE로 지정 Id의 AttendanceSetting 앱 정보 목록 조회")
	public List<String> listIdsByCode(String code) throws Exception {
		if( code == null || code.isEmpty() ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceSetting> root = cq.from(AttendanceSetting.class);
		Predicate p = cb.equal(root.get(AttendanceSetting_.configCode),  code);
		cq.select(root.get(AttendanceSetting_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	public AttendanceSetting getWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			return null;
		}
		AttendanceSetting attendanceSetting = null;
		List<AttendanceSetting> settingList = null;
		EntityManager em = this.entityManagerContainer().get(AttendanceSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceSetting> cq = cb.createQuery(AttendanceSetting.class);
		Root<AttendanceSetting> root = cq.from(AttendanceSetting.class);
		Predicate p = cb.equal(root.get( AttendanceSetting_.configCode ),  configCode );
		settingList = em.createQuery(cq.where(p)).getResultList();
		if( settingList != null && settingList.size() > 0 ){
			attendanceSetting = settingList.get(0);
		}
		return attendanceSetting;
	}
}