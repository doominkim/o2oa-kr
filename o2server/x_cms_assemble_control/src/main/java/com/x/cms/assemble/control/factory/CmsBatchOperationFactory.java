package com.x.cms.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.DateTools;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.CmsBatchOperation;
import com.x.cms.core.entity.CmsBatchOperation_;

/**
 * 일괄 처리 작업 정보 기록, 예: 대량의 분류, 문서 등의 연쇄 삭제
 */
public class CmsBatchOperationFactory extends AbstractFactory {

	public CmsBatchOperationFactory(Business business) throws Exception {
		super(business);
	}

	public List<CmsBatchOperation> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CmsBatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CmsBatchOperation> cq = cb.createQuery( CmsBatchOperation.class );
		Root<CmsBatchOperation> root = cq.from( CmsBatchOperation.class );
		Predicate p = root.get( CmsBatchOperation_.id).in( ids );
		cq.orderBy( cb.asc( root.get( CmsBatchOperation_.createTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<CmsBatchOperation> list(Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CmsBatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CmsBatchOperation> cq = cb.createQuery( CmsBatchOperation.class );
		Root<CmsBatchOperation> root = cq.from( CmsBatchOperation.class );
		cq.orderBy( cb.asc( root.get( CmsBatchOperation_.createTime ) ) );
		return em.createQuery(cq).setMaxResults(maxCount).getResultList();
	}

	public List<CmsBatchOperation> list(Integer maxCount, Integer minutesAgo) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CmsBatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CmsBatchOperation> cq = cb.createQuery( CmsBatchOperation.class );
		Root<CmsBatchOperation> root = cq.from( CmsBatchOperation.class );
		Predicate p = cb.lessThan(root.get(CmsBatchOperation_.createTime), DateTools.getAdjustTimeDay(new Date(), 0, 0, -minutesAgo, 0));
		cq.where(p).orderBy( cb.asc( root.get( CmsBatchOperation_.createTime ) ) );
		return em.createQuery(cq).setMaxResults(maxCount).getResultList();
	}
	
	public List<CmsBatchOperation> listNotRun(Integer maxCount) throws Exception {
		EntityManager em = this.entityManagerContainer().get( CmsBatchOperation.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CmsBatchOperation> cq = cb.createQuery( CmsBatchOperation.class );
		Root<CmsBatchOperation> root = cq.from( CmsBatchOperation.class );
		Predicate p = cb.isFalse( root.get( CmsBatchOperation_.isRunning ) );
		cq.orderBy( cb.asc( root.get( CmsBatchOperation_.createTime ) ) );
		return em.createQuery(cq.where(p)).setMaxResults(maxCount).getResultList();
	}
}