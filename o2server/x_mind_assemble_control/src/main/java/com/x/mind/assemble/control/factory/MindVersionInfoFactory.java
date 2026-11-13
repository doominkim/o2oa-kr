package com.x.mind.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindVersionContent;
import com.x.mind.entity.MindVersionInfo;
import com.x.mind.entity.MindVersionInfo_;


/**
 * 클래스 이름: MindVersionInfoFactory<br/>
 * 엔티티 클래스: MindVersionInfo<br/>
 * 작성자: O2LEE<br/>
 * 소속: O2 Team<br/>
 * 일시: 2016-11-15 17:17:26 
**/
public class MindVersionInfoFactory extends AbstractFactory {

	public MindVersionInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정 Id의 마인드맵 버전 엔티티 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindVersionInfo getVersionInfoWithId( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindVersionInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * ID 목록으로 이전 버전 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public List<MindVersionInfo> listVersionsWithIds(List<String> ids) throws Exception {
		if( ListTools.isEmpty( ids ) ){
			return null;
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = root.get(MindVersionInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 지정 Id의 마인드맵 버전 내용 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindVersionContent getVersionContentWithId( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindVersionContent.class, ExceptionWhen.none );
	}

	/**
	 * 마인드맵 ID로 해당 마인드맵의 모든 버전 정보 조회
	 * @param id
	 * @return
	 * @throws Exception 
	 */
	public List<String> listVersionsWithMindId(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("마인드맵 ID가 비어 있어 버전 정보 목록을 조회할 수 없습니다!");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.select( root.get(MindVersionInfo_.id) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * ID로 마인드맵의 이전 버전 수 조회
	 * @param mindId
	 * @return
	 * @throws Exception
	 */
	public Long countMindVersionWithMindId(String mindId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get( MindVersionInfo_.mindId), mindId);
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 마인드맵 ID로 가장 오래된 버전 정보 ID 조회
	 * @param mindId
	 * @return
	 * @throws Exception 
	 */
	public MindVersionInfo getEarliestVersionInfoId(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("마인드맵 ID가 비어 있어 버전 정보를 조회할 수 없습니다!");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.orderBy( cb.asc( root.get( MindVersionInfo_.updateTime ) ) );
		List<MindVersionInfo> versions = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(ListTools.isNotEmpty( versions )) {
			 return versions.get(0);
		}
		return null;
	}

	public MindVersionInfo getLatestVersionWithMind(String mindId) throws Exception {
		if( StringUtils.isEmpty( mindId ) ){
			new Exception("마인드맵 ID가 비어 있어 버전 정보를 조회할 수 없습니다!");
		}
		EntityManager em = this.entityManagerContainer().get(MindVersionInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindVersionInfo> cq = cb.createQuery(MindVersionInfo.class);
		Root<MindVersionInfo> root = cq.from(MindVersionInfo.class);
		Predicate p = cb.equal( root.get(MindVersionInfo_.mindId) , mindId);
		cq.orderBy( cb.desc( root.get( MindVersionInfo_.updateTime ) ) );
		List<MindVersionInfo> versions = em.createQuery(cq.where(p)).setMaxResults(1).getResultList();
		if(ListTools.isNotEmpty( versions )) {
			 return versions.get(0);
		 }
		return null;
	}

	
}
