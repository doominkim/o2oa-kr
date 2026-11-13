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
import com.x.attendance.entity.AttendanceImportFileInfo;
import com.x.attendance.entity.AttendanceImportFileInfo_;
import com.x.base.core.project.exception.ExceptionWhen;
/**
 * 시스템 설정 정보 테이블 기본 기능 서비스 클래스
 */
public class AttendanceImportFileInfoFactory extends AbstractFactory {
	
	public AttendanceImportFileInfoFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정 Id의 AttendanceImportFileInfo 앱 정보 객체 조회")
	public AttendanceImportFileInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, AttendanceImportFileInfo.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe("전체 AttendanceImportFileInfo 앱 정보 목록 조회")
	@SuppressWarnings("unused")
	public List<AttendanceImportFileInfo> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(AttendanceImportFileInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceImportFileInfo> cq = cb.createQuery(AttendanceImportFileInfo.class);
		Root<AttendanceImportFileInfo> root = cq.from( AttendanceImportFileInfo.class);
		return em.createQuery(cq).getResultList();
	}
	
//	@MethodDescribe("지정 Id의 AttendanceImportFileInfo 앱 정보 목록 조회")
	public List<AttendanceImportFileInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<AttendanceImportFileInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(AttendanceImportFileInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AttendanceImportFileInfo> cq = cb.createQuery(AttendanceImportFileInfo.class);
		Root<AttendanceImportFileInfo> root = cq.from(AttendanceImportFileInfo.class);
		Predicate p = root.get(AttendanceImportFileInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}	
}