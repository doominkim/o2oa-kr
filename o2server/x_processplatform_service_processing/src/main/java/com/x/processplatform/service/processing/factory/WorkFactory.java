package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.Work_;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;

public class WorkFactory extends AbstractFactory {

	public WorkFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Work.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Work> root = cq.from(Work.class);
		Predicate p = cb.equal(root.get(Work_.job), job);
		cq.select(root.get(Work_.id)).where(p);
		return em.createQuery(cq.where(p)).getResultList();
	}

//	public List<String> listContainSplitToken(String splitToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Work.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Work> root = cq.from(Work.class);
//		Predicate p = cb.isMember(splitToken, root.get(Work_.splitTokenList));
//		cq.select(root.get(Work_.id)).where(p);
//		return em.createQuery(cq.where(p)).getResultList();
//	}

	// public Long countWithAttachment(String attachment) throws Exception {
	// EntityManager em = this.entityManagerContainer().get(Work.class);
	// CriteriaBuilder cb = em.getCriteriaBuilder();
	// CriteriaQuery<Long> cq = cb.createQuery(Long.class);
	// Root<Work> root = cq.from(Work.class);
	// Predicate p = cb.isMember(attachment, root.get(Work_.attachmentList));
	// cq.select(cb.count(root)).where(p);
	// return em.createQuery(cq).getSingleResult();
	// }

	/*
	 * activityToken의 Work 조회. 하나의 ActivityToken에 있는 Work는 이론상 하나뿐이지만, 값 기준 분할 과정에서 일시적으로 여러 개 생길 수 있음
	 */
//	public List<String> listWithActivityToken(String activityToken) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Work.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Work> root = cq.from(Work.class);
//		Predicate p = cb.equal(root.get(Work_.activityToken), activityToken);
//		cq.select(root.get(Work_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	public List<String> listWithActivityToken(List<String> activityTokens) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(Work.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<String> cq = cb.createQuery(String.class);
//		Root<Work> root = cq.from(Work.class);
//		Predicate p = root.get(Work_.activityToken).in(activityTokens);
//		cq.select(root.get(Work_.id)).where(p);
//		return em.createQuery(cq).getResultList();
//	}

//	/* hint 추가 */
//	public void addHint(Work work, String hint) {
//		if (ListTools.isEmpty(work.getHintList())) {
//			work.setHintList(new ArrayList<String>());
//		}
//		work.getHintList().add(DateTools.format(new Date()) + ":[" + work.getActivityName() + "]" + hint);
//	}

	/* 확장 메서드 */

}