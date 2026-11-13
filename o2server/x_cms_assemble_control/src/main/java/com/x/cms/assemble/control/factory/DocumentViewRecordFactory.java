package com.x.cms.assemble.control.factory;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.DocumentViewRecord;
import com.x.cms.core.entity.DocumentViewRecord_;

/**
 * 문서 권한 기본 기능 서비스 클래스
 *
 * @author O2LEE
 */
public class DocumentViewRecordFactory extends AbstractFactory {

	public DocumentViewRecordFactory(Business business) throws Exception {
		super(business);
	}

	//@MethodDescribe("지정한 Id의 DocumentViewRecord 정보 목록 조회")
	public List<DocumentViewRecord> list( List<String> ids ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentViewRecord> cq = cb.createQuery( DocumentViewRecord.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = root.get( DocumentViewRecord_.id).in( ids );
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe("문서 ID로 지정한 Id의 DocumentViewRecord 정보 목록 조회")
	public List<String> listByDocument( String docId ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), docId );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime ) ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).setMaxResults(50).getResultList();
	}

	//@MethodDescribe("방문자 이름으로 지정한 Id의 DocumentViewRecord 정보 목록 조회")
	public List<String> listByPerson( String personName, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), personName );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime ) ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).setMaxResults( maxCount ).getResultList();
	}

	public List<DocumentViewRecord> listRecordsByPerson( String personName, Integer maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentViewRecord> cq = cb.createQuery( DocumentViewRecord.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), personName );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime ) ) );
		return em.createQuery( cq.where(p) ).setMaxResults( maxCount ).getResultList();
	}

	//@MethodDescribe("방문자 이름과 문서 ID로 지정한 Id의 DocumentViewRecord 정보 목록 조회")
	public List<String> listByDocAndPerson( String documentId, String personName ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), personName );
		p = cb.and( p, cb.equal( root.get( DocumentViewRecord_.documentId ), documentId ) );
		cq.select( root.get( DocumentViewRecord_.id ));
		return em.createQuery( cq.where(p) ).getResultList();
	}

	/**
	 * 문서 ID로 해당 문서의 전체 조회 수 계산
	 * @param id
	 * @return
	 */
	public Long sumWithDocmentId(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), id );
		cq.select( cb.sumAsLong(root.get( DocumentViewRecord_.viewCount )) ).where(p);
		return em.createQuery( cq.where(p) ).getSingleResult();
	}

	public List<DocumentViewRecord> listNextWithDocIds( String docId, Integer count, Object sequenceFieldValue, String order ) throws Exception {
		if( StringUtils.isEmpty(order) ){
			order = "DESC";
		}
		if( count == null ){
			count = 12;
		}
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DocumentViewRecord> cq = cb.createQuery( DocumentViewRecord.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), docId );
		p = cb.and( p, cb.isNotNull( root.get( DocumentViewRecord_.lastViewTime ) ));
		if( sequenceFieldValue != null ){
			if( "DESC".equalsIgnoreCase( order )){
				p = cb.and( p, cb.lessThan( root.get( DocumentViewRecord_.lastViewTime ), (Date)sequenceFieldValue ));
			}else{
				p = cb.and( p, cb.greaterThan( root.get( DocumentViewRecord_.lastViewTime ), (Date)sequenceFieldValue ));
			}
		}
		if( "DESC".equalsIgnoreCase( order )){
			cq.orderBy( cb.desc( root.get( DocumentViewRecord_.lastViewTime ) ) );
		}
		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}

	public Long countWithDocIds(String docId) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), docId );
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public Long countWithDocIdAndPerson(String docId, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.documentId ), docId );
		p = cb.and( p, cb.equal( root.get( DocumentViewRecord_.viewerName ), person ));
		cq.select( cb.count( root ));
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	public List<String> listOverTime(Date limitDate) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.lessThan( root.get(DocumentViewRecord_.createTime), limitDate );
		cq.select(root.get(DocumentViewRecord_.id));
		return em.createQuery(cq.where(p)).setMaxResults(1000000).getResultList();
	}

	public Long getTotal() throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery( Long.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		cq.select(cb.count(root));
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> getRecordIdsWithCount( int maxCount ) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		cq.orderBy( cb.desc( root.get( DocumentViewRecord_.createTime )));
		cq.select(root.get(DocumentViewRecord_.id));
		return em.createQuery(cq).setMaxResults( maxCount ).getResultList();
	}

	/**
	 * 지정한 ID 목록으로 읽은 문서 ID 목록 조회
	 * @param ids
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listReadDocId(List<String> ids, String distinguishedName) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), distinguishedName );
		p = cb.and( p, root.get( DocumentViewRecord_.documentId ).in( ids ));
		cq.select(root.get( DocumentViewRecord_.documentId ));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정한 분류 ID 목록으로 읽은 문서 ID 목록 조회
	 * @param categoryIds
	 * @param distinguishedName
	 * @return
	 * @throws Exception
	 */
	public List<String> listReadDocIdWithCategory(List<String> categoryIds, String distinguishedName) throws Exception {
		EntityManager em = this.entityManagerContainer().get( DocumentViewRecord.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<DocumentViewRecord> root = cq.from( DocumentViewRecord.class );
		Predicate p = cb.equal( root.get( DocumentViewRecord_.viewerName ), distinguishedName );
		p = cb.and( p, root.get( DocumentViewRecord_.categoryId ).in( categoryIds ));
		cq.select(root.get( DocumentViewRecord_.documentId ));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
