package com.x.message.assemble.communicate.factory;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.message.core.entity.IMMsgCollection;
import com.x.message.core.entity.IMMsgCollection_;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.message.assemble.communicate.AbstractFactory;
import com.x.message.assemble.communicate.Business;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMConversationExt;
import com.x.message.core.entity.IMConversationExt_;
import com.x.message.core.entity.IMConversation_;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.IMMsg_;
import javax.persistence.criteria.Subquery;
import org.apache.commons.lang3.StringUtils;

public class IMConversationFactory extends AbstractFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IMConversationFactory.class);

    public IMConversationFactory(Business business) throws Exception {
        super(business);
    }


    /**
     * businessId로 대화 목록 조회. 구성원에 person 포함
     *
     * @param person
     * @param businessId
     * @return
     * @throws Exception
     */
    public List<IMConversation> listConversationWithPersonAndBusinessId(String person,
            String businessId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMConversation.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        EntityManager emExt = this.entityManagerContainer().get(IMConversationExt.class);
        CriteriaBuilder cbExt = emExt.getCriteriaBuilder();
        CriteriaQuery<IMConversation> cq = cb.createQuery(IMConversation.class);
        Root<IMConversation> root = cq.from(IMConversation.class);
        // 하위 조건
        Subquery<IMConversationExt> subQuery = cq.subquery(IMConversationExt.class);
        Root<IMConversationExt> root1 = subQuery.from(
                emExt.getMetamodel().entity(IMConversationExt.class));
        subQuery.select(root1);
        Predicate p_permission = cbExt.equal(root1.get(IMConversationExt_.person), person);
        p_permission = cbExt.and(p_permission,
                cbExt.equal(root1.get(IMConversationExt_.conversationId),
                        root.get(IMConversation_.id)));
        subQuery.where(p_permission);
        Predicate p = cb.exists(subQuery);
        p = cb.and(p, cb.equal(root.get(IMConversation_.businessId), businessId));
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }


    /**
     * 구성원에 person이 포함된 대화 id 목록 조회
     *
     * @param person
     * @return
     * @throws Exception
     */
//	public List<IMConversation> listConversationWithPerson(String person) throws Exception {
//		EntityManager em = this.entityManagerContainer().get(IMConversation.class);
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<IMConversation> cq = cb.createQuery(IMConversation.class);
//		Root<IMConversation> root = cq.from(IMConversation.class);
//		Predicate p = cb.isMember(person, root.get(IMConversation_.personList));
//		cq.select(root).where(p);
//		return em.createQuery(cq).getResultList();
//	}
    public List<IMConversation> listConversationWithPerson2(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMConversation.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        EntityManager emExt = this.entityManagerContainer().get(IMConversationExt.class);
        CriteriaBuilder cbExt = emExt.getCriteriaBuilder();
        CriteriaQuery<IMConversation> cq = cb.createQuery(IMConversation.class);
        Root<IMConversation> root = cq.from(IMConversation.class);
        // 하위 조건
        Subquery<IMConversationExt> subQuery = cq.subquery(IMConversationExt.class);
        Root<IMConversationExt> root1 = subQuery.from(
                emExt.getMetamodel().entity(IMConversationExt.class));
        subQuery.select(root1);
        Predicate p_permission = cbExt.equal(root1.get(IMConversationExt_.person), person);
        p_permission = cbExt.and(p_permission,
                cbExt.equal(root1.get(IMConversationExt_.conversationId),
                        root.get(IMConversation_.id)));
        subQuery.where(p_permission);
        Predicate p = cb.exists(subQuery);
        cq.select(root).where(p);
        return em.createQuery(cq).getResultList();
    }


    /**
     * 현재 사용자 대화 확장 조회
     *
     * @param person
     * @param conversationId
     * @return
     * @throws Exception
     */
    public IMConversationExt getConversationExt(String person, String conversationId)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMConversationExt.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMConversationExt> cq = cb.createQuery(IMConversationExt.class);
        Root<IMConversationExt> root = cq.from(IMConversationExt.class);
        Predicate p = cb.equal(root.get(IMConversationExt_.person), person);
        p = cb.and(p, cb.equal(root.get(IMConversationExt_.conversationId), conversationId));
        cq.select(root).where(p);
        List<IMConversationExt> list = em.createQuery(cq).setMaxResults(1).getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 읽지 않은 메시지 수
     *
     * @param ext
     * @return
     * @throws Exception
     */
    public Long unreadNumber(IMConversationExt ext) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), ext.getConversationId());
        p = cb.and(p, cb.notEqual(root.get(IMMsg_.createPerson), ext.getPerson()));
        if (ext.getLastReadTime() != null) {
            p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime), ext.getLastReadTime()));
        }
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * 특정 대화의 메시지 총수 조회
     * @param conversationId 대화 id
     * @return
     * @throws Exception
     */
    public Long conversationMessageTotalCount(String conversationId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
        cq.select(cb.count(root)).where(p);
        return em.createQuery(cq).getSingleResult();
    }

    /**
     * 대화의 마지막 메시지 조회
     *
     * @param conversationId 대화 id
     * @return
     * @throws Exception
     */
    public IMMsg lastMessage(String conversationId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
        List<IMMsg> list = em.createQuery(
                        cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime))))
                .setMaxResults(1)
                .getResultList();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    // bodyFileId 필드로 메시지 검색
    public List<IMMsg> listMessageByFileId(String fileId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.bodyFileId), fileId);
        return em.createQuery( cq.select(root).where(p) ).getResultList();
    }

    /**
     * id 목록으로 메시지 객체 목록 조회
     *
     * @param ids
     * @return
     * @throws Exception
     */
    public List<IMMsg> listMsgObject(List<String> ids) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p;
        if (ids.size() > 1) {
            p = root.get(JpaObject.id_FIELDNAME).in(ids);
        } else {
            p = cb.equal(root.get(JpaObject.id_FIELDNAME), ids.get(0));
        }
        return em.createQuery(
                        cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime))))
                .getResultList();
    }


    /**
     * 대화 내 채팅 메시지 조회. 페이지 조회에 필요
     *
     * @param adjustPage
     * @param adjustPageSize
     * @param conversationId
     * @return
     * @throws Exception
     */
    public List<IMMsg> listMsgWithConversationByPage(Integer adjustPage,
            Integer adjustPageSize, String conversationId, Date lastDeleteTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
        if (lastDeleteTime != null) {
            p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime), lastDeleteTime));
        }
        cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsg_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize)
                .setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 페이지 단위 채팅 메시지 조회. 마감 시간 beforeDate가 있으면 그 이전 메시지를 조회하고, 대화 id가 있으면 대화 내 채팅 메시지를 조회
     *
     * @param conversationId 대화 id
     * @param beforeDate     마감 시간. 이 시간 이전의 모든 메시지 조회
     * @return
     * @throws Exception
     */
    public List<IMMsg> listMsgWithConversation(Integer adjustPage, Integer adjustPageSize,
            String conversationId, Date beforeDate) throws Exception {
        if (StringUtils.isEmpty(conversationId) && beforeDate == null) {
            LOGGER.info("파라미터가 올바르지 않아 메시지를 조회할 수 없습니다!!!!!!!");
            return Collections.emptyList();
        }
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.conjunction();
        if (StringUtils.isNotEmpty(conversationId)) {
            p = cb.and(p, cb.equal(root.get(IMMsg_.conversationId), conversationId));
        }
        if (beforeDate != null) {
            p = cb.and(p, cb.lessThan(root.get(IMMsg_.createTime), beforeDate));
        }
        cq.select(root).where(p).orderBy(cb.asc(root.get(IMMsg_.createTime))); // 순서: 시간 오래된 순 조회
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize)
                .setMaxResults(adjustPageSize).getResultList();
    }

    /**
     * 대화 채팅 메시지 총수 조회. 페이지 조회에 필요
     *
     * @param conversationId
     * @return
     * @throws Exception
     */
    public Long count(String conversationId, Date lastDeleteTime) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
        if (lastDeleteTime != null) {
            p = cb.and(p, cb.greaterThan(root.get(IMMsg_.createTime), lastDeleteTime));
        }
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }


    /**
     * 대화 id로 모든 메시지 id 조회. 채팅 기록 비우기용
     *
     * @param conversationId 대화 id
     * @return
     * @throws Exception
     */
    public List<String> listAllMsgIdsWithConversationId(String conversationId) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = cb.equal(root.get(IMMsg_.conversationId), conversationId);
        return em.createQuery(cq.select(root.get(IMMsg_.id)).where(p)).getResultList();
    }

    /**
     * 대화 id로 연관된 모든 대화 확장 조회. 대화 확장 삭제용
     *
     * @param conversationId 대화 id
     * @return
     * @throws Exception
     */
    public List<String> listAllConversationExtIdsWithConversationId(String conversationId)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMConversationExt.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<IMConversationExt> root = cq.from(IMConversationExt.class);
        Predicate p = cb.equal(root.get(IMConversationExt_.conversationId), conversationId);
        return em.createQuery(cq.select(root.get(IMConversationExt_.id)).where(p)).getResultList();
    }


    /**
     * 즐겨찾기 메시지 조회
     *
     * @param person 즐겨찾기한 사용자
     * @param msgId  메시지 id
     * @return
     * @throws Exception
     */
    public List<IMMsgCollection> listCollectionByPersonAndMsgId(String person, String msgId)
            throws Exception {
        if (StringUtils.isEmpty(person) && StringUtils.isEmpty(msgId)) {
            LOGGER.info("파라미터가 올바르지 않아 즐겨찾기 메시지를 조회할 수 없습니다!!!!!!!");
            return Collections.emptyList();
        }
        EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Predicate predicate = cb.conjunction();
        CriteriaQuery<IMMsgCollection> cq = cb.createQuery(IMMsgCollection.class);
        Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
        if (StringUtils.isNotEmpty(person)) {
            predicate = cb.and(predicate,
                    cb.equal(root.get(IMMsgCollection_.createPerson), person));
        }
        if (StringUtils.isNotEmpty(msgId)) {
            predicate = cb.and(predicate, cb.equal(root.get(IMMsgCollection_.messageId), msgId));
        }
        return em.createQuery(cq.select(root).where(predicate)).getResultList();
    }

    /**
     * 즐겨찾기 메시지 페이지 조회
     *
     * @param adjustPage
     * @param adjustPageSize
     * @param person
     * @return
     * @throws Exception
     */
    public List<IMMsgCollection> listCollectionWithPersonByPage(Integer adjustPage,
            Integer adjustPageSize, String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsgCollection> cq = cb.createQuery(IMMsgCollection.class);
        Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
        Predicate p = cb.equal(root.get(IMMsgCollection_.createPerson), person);
        cq.select(root).where(p).orderBy(cb.desc(root.get(IMMsgCollection_.createTime)));
        return em.createQuery(cq).setFirstResult((adjustPage - 1) * adjustPageSize)
                .setMaxResults(adjustPageSize)
                .getResultList();
    }

    /**
     * 현재 사용자 대화 확장 일괄 조회
     *
     * @param person
     * @param conversationIds
     * @return
     * @throws Exception
     */
    public java.util.Map<String, IMConversationExt> getConversationExts(String person, java.util.List<String> conversationIds)
            throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMConversationExt.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMConversationExt> cq = cb.createQuery(IMConversationExt.class);
        Root<IMConversationExt> root = cq.from(IMConversationExt.class);
        Predicate p = cb.equal(root.get(IMConversationExt_.person), person);
        p = cb.and(p, root.get(IMConversationExt_.conversationId).in(conversationIds));
        cq.select(root).where(p);
        java.util.List<IMConversationExt> list = em.createQuery(cq).getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(IMConversationExt::getConversationId, ext -> ext, (existing, replacement) -> existing));
    }

    /**
     * 읽지 않은 메시지 수 일괄 조회
     *
     * @param exts
     * @return
     * @throws Exception
     */
    public java.util.Map<String, Long> getUnreadNumbers(java.util.List<IMConversationExt> exts) throws Exception {
        if (exts.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<java.lang.Object[]> cq = cb.createQuery(java.lang.Object[].class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        // 조건 구성: conversationId in exts, createPerson != person, createTime > lastReadTime
        java.util.List<String> conversationIds = exts.stream().map(IMConversationExt::getConversationId).collect(java.util.stream.Collectors.toList());
        Predicate p = root.get(IMMsg_.conversationId).in(conversationIds);
        // 각 ext에 조건 추가
        java.util.List<Predicate> predicates = new java.util.ArrayList<>();
        for (IMConversationExt ext : exts) {
            Predicate p1 = cb.equal(root.get(IMMsg_.conversationId), ext.getConversationId());
            p1 = cb.and(p1, cb.notEqual(root.get(IMMsg_.createPerson), ext.getPerson()));
            if (ext.getLastReadTime() != null) {
                p1 = cb.and(p1, cb.greaterThan(root.get(IMMsg_.createTime), ext.getLastReadTime()));
            }
            predicates.add(p1);
        }
        Predicate orPredicate = cb.or(predicates.toArray(new Predicate[0]));
        cq.multiselect(root.get(IMMsg_.conversationId), cb.count(root)).where(orPredicate).groupBy(root.get(IMMsg_.conversationId));
        java.util.List<java.lang.Object[]> results = em.createQuery(cq).getResultList();
        return results.stream().collect(java.util.stream.Collectors.toMap(arr -> (String) arr[0], arr -> (Long) arr[1], (existing, replacement) -> existing));
    }

    /**
     * 마지막 메시지 일괄 조회
     *
     * @param conversationIds
     * @return
     * @throws Exception
     */
    public java.util.Map<String, IMMsg> getLastMessages(java.util.List<String> conversationIds) throws Exception {
        if (conversationIds.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        EntityManager em = this.entityManagerContainer().get(IMMsg.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IMMsg> cq = cb.createQuery(IMMsg.class);
        Root<IMMsg> root = cq.from(IMMsg.class);
        Predicate p = root.get(IMMsg_.conversationId).in(conversationIds);
        // 윈도 함수 또는 서브쿼리로 각 대화의 최신 메시지 조회
        // JPA Criteria가 윈도 함수를 지원하지 않아 서브쿼리 사용
        Subquery<java.util.Date> subMaxTime = cq.subquery(java.util.Date.class);
        Root<IMMsg> subRoot = subMaxTime.from(IMMsg.class);
        subMaxTime.select(cb.greatest(subRoot.get(IMMsg_.createTime)))
                .where(cb.equal(subRoot.get(IMMsg_.conversationId), root.get(IMMsg_.conversationId)));
        Predicate p2 = cb.equal(root.get(IMMsg_.createTime), subMaxTime);
        cq.select(root).where(cb.and(p, p2));
        java.util.List<IMMsg> list = em.createQuery(cq).getResultList();
        return list.stream().collect(java.util.stream.Collectors.toMap(IMMsg::getConversationId, msg -> msg, (existing, replacement) -> existing));
    }


     /**
     * 즐겨찾기 메시지 수
     *
     * @param person
     * @return
     * @throws Exception
     */
    public Long listCollectionCount(String person) throws Exception {
        EntityManager em = this.entityManagerContainer().get(IMMsgCollection.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<IMMsgCollection> root = cq.from(IMMsgCollection.class);
        Predicate p = cb.equal(root.get(IMMsgCollection_.createPerson), person);
        return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult();
    }

}