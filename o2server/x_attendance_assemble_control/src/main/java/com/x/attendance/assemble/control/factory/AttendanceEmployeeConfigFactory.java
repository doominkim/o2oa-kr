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
import com.x.attendance.entity.AttendanceEmployeeConfig;
import com.x.attendance.entity.AttendanceEmployeeConfig_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 직원 근태 요구 설정 서비스
 */
public class AttendanceEmployeeConfigFactory extends AbstractFactory {
	
	public AttendanceEmployeeConfigFactory(Business business) throws Exception {
		super(business);
	}

//	@MethodDescribe("지정 Id의 AttendanceEmployeeConfig 정보 객체 조회")
	public AttendanceEmployeeConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceEmployeeConfig.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 AttendanceEmployeeConfig 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceEmployeeConfig> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceEmployeeConfig> cq = cb.createQuery(AttendanceEmployeeConfig.class);
		Root<AttendanceEmployeeConfig> root = cq.from( AttendanceEmployeeConfig.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe("지정 Id의 AttendanceEmployeeConfig 정보 목록 조회")
	public List<AttendanceEmployeeConfig> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceEmployeeConfig>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceEmployeeConfig> cq = cb.createQuery(AttendanceEmployeeConfig.class);
		Root<AttendanceEmployeeConfig> root = cq.from(AttendanceEmployeeConfig.class);
		Predicate p = root.get(AttendanceEmployeeConfig_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe("설정 분류로 AttendanceEmployeeConfig 정보 목록 조회")
	public List<String> listByConfigType( String configType ) throws Exception {
		if( configType == null || configType.isEmpty()){
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceEmployeeConfig.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AttendanceEmployeeConfig> root = cq.from(AttendanceEmployeeConfig.class);
		Predicate p = cb.equal(root.get(AttendanceEmployeeConfig_.configType), configType);	
		cq.select(root.get(AttendanceEmployeeConfig_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}	
}