package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSOperationRecord;
import com.x.bbs.entity.BBSOperationRecord_;

/**
 * 클래스명: BBSOperationRecordFactory<br/>
 * 엔티티 클래스: BBSOperationRecord<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
**/
public class BBSOperationRecordFactory extends AbstractFactory {

	public BBSOperationRecordFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSOperationRecord 엔티티 정보 객체 조회" )
	public BBSOperationRecord get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSOperationRecord.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSOperationRecord 엔티티 정보 목록 조회" )
	public List<BBSOperationRecord> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSOperationRecord>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSOperationRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSOperationRecord> cq = cb.createQuery(BBSOperationRecord.class);
		Root<BBSOperationRecord> root = cq.from(BBSOperationRecord.class);
		Predicate p = root.get(BBSOperationRecord_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "작업 이력이 있는 사용자 이름 목록 조회, 중복 제거" )
	public List<String> distinctAllOperationUserNames() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSOperationRecord.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSOperationRecord> root = cq.from(BBSOperationRecord.class);
		Predicate p = cb.isNotNull( root.get(BBSOperationRecord_.id) );
		cq.select( root.get(BBSOperationRecord_.operatorName) );
		return em.createQuery(cq.where(p)).getResultList().stream().distinct().collect(Collectors.toList());
	}
}
