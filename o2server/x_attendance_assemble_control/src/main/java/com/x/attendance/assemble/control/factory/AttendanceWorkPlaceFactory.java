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
import com.x.attendance.entity.AttendanceWorkPlace;
import com.x.attendance.entity.AttendanceWorkPlace_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 근무지 정보 서비스 클래스
 */
public class AttendanceWorkPlaceFactory extends AbstractFactory {
	
	public AttendanceWorkPlaceFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceWorkPlace 앱 정보 객체 조회")
	public AttendanceWorkPlace get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceWorkPlace.class, ExceptionWhen.none);
	}
	
//	@MethodDescribe("전체 AttendanceWorkPlace 앱 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceWorkPlace> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkPlace.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkPlace> cq = cb.createQuery(AttendanceWorkPlace.class);
		Root<AttendanceWorkPlace> root = cq.from( AttendanceWorkPlace.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceWorkPlace 앱 정보 목록 조회")
	public List<AttendanceWorkPlace> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceWorkPlace>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceWorkPlace.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceWorkPlace> cq = cb.createQuery(AttendanceWorkPlace.class);
		Root<AttendanceWorkPlace> root = cq.from(AttendanceWorkPlace.class);
		Predicate p = root.get(AttendanceWorkPlace_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}	
}