package com.x.mind.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindFolderInfo;
import com.x.mind.entity.MindFolderInfo_;


/**
 * 클래스 이름: MindFolderInfoFactory<br/>
 * 엔티티 클래스: MindFolderInfo<br/>
 * 작성자: O2LEE<br/>
 * 소속: O2 Team<br/>
 * 일시: 2016-11-15 17:17:26 
**/
public class MindFolderInfoFactory extends AbstractFactory {

	public MindFolderInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정 Id의 마인드맵 폴더 엔티티 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindFolderInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindFolderInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * 지정 Id의 마인드맵 폴더 엔티티 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindFolderInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<MindFolderInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindFolderInfo> cq = cb.createQuery(MindFolderInfo.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = root.get(MindFolderInfo_.id).in(ids);
		cq.orderBy( cb.desc( root.get( MindFolderInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 전체 마인드맵 폴더 엔티티 정보 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		cq.select( root.get(MindFolderInfo_.id ) );
		return em.createQuery( cq ).setMaxResults( 10000 ).getResultList();
	}

	/**
	 * 조건에 맞는 디렉터리 정보 건수 조회
	 * @param parentId
	 * @param folderId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public Long count( String parentId, String folderId, String name, String creator, String creatorUnit ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.isNotNull( root.get(MindFolderInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.id), folderId));
		}
		if( creator != null && !creator.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creator), creator));
		}
		if( creatorUnit != null && !creatorUnit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creatorUnit), creatorUnit));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.parentId ), parentId));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindFolderInfo_.name ), "%" + name + "%" ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 조건에 맞는 디렉터리 정보 조회
	 * @param parentId
	 * @param folderId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public List<String> list( String parentId, String folderId, String name, String creator, String creatorUnit ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.isNotNull( root.get(MindFolderInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.id), folderId));
		}
		if( creator != null && !creator.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creator), creator));
		}
		if( creatorUnit != null && !creatorUnit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.creatorUnit), creatorUnit));
		}
		if( parentId != null && !parentId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindFolderInfo_.parentId ), parentId));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindFolderInfo_.name ), "%" + name + "%" ));
		}
		cq.select( root.get(MindFolderInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 폴더 ID로 모든 하위 폴더 수 조회
	 * @param folderId
	 * @return
	 * @throws Exception
	 */
	public Long countChildWithFolder(String folderId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindFolderInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindFolderInfo> root = cq.from(MindFolderInfo.class);
		Predicate p = cb.equal(root.get( MindFolderInfo_.parentId ), folderId );
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}
