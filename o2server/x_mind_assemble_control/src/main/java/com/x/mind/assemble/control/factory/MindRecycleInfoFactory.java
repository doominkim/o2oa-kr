package com.x.mind.assemble.control.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.mind.assemble.control.AbstractFactory;
import com.x.mind.assemble.control.Business;
import com.x.mind.entity.MindRecycleInfo;
import com.x.mind.entity.MindRecycleInfo_;


/**
 * 클래스 이름: MindRecycleInfoFactory<br/>
 * 엔티티 클래스: MindRecycleInfo<br/>
 * 작성자: O2LEE<br/>
 * 소속: O2 Team<br/>
 * 일시: 2016-11-15 17:17:26 
**/
public class MindRecycleInfoFactory extends AbstractFactory {

	public MindRecycleInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정 Id의 휴지통 마인드맵 엔티티 정보 객체 조회
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MindRecycleInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, MindRecycleInfo.class, ExceptionWhen.none );
	}
	
	/**
	 * 지정 Id의 휴지통 마인드맵 엔티티 정보 목록 조회
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	public List<MindRecycleInfo> list( List<String> ids ) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<MindRecycleInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindRecycleInfo> cq = cb.createQuery(MindRecycleInfo.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = root.get(MindRecycleInfo_.id).in(ids);
		cq.orderBy( cb.desc( root.get( MindRecycleInfo_.updateTime ) ) );
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 전체 휴지통 마인드맵 엔티티 정보 목록 조회
	 * @return
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		cq.orderBy( cb.desc( root.get( MindRecycleInfo_.updateTime ) ) );
		cq.select( root.get(MindRecycleInfo_.id ) );
		return em.createQuery( cq ).setMaxResults( 10000 ).getResultList();
	}

	/**
	 * 조건에 맞는 휴지통 마인드맵 정보 건수 검색
	 * @param folderId  소속 디렉터리 ID
	 * @param name	  마인드맵 이름(부분 검색)
	 * @param person  마인드맵 생성자
	 * @param unit       마인드맵 생성 조직
	 * @param hasShared  공유 여부
	 * @return
	 * @throws Exception
	 */
	public Long count( String folderId, String name, String person, String unit, Boolean hasShared ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = cb.isNotNull( root.get(MindRecycleInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.id), folderId));
		}
		if( person != null && !person.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), person));
		}
		if( unit != null && !unit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), unit));
		}
		if( hasShared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), hasShared));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name ), "%" + name + "%" ));
		}
		cq.select( cb.count( root ) );
		return em.createQuery(cq.where(p)).getSingleResult();
	}

	/**
	 * 조건에 맞는 휴지통 마인드맵 정보 ID 목록 검색
	 * @param folderId  소속 디렉터리 ID
	 * @param name	  마인드맵 이름(부분 검색)
	 * @param person  마인드맵 생성자
	 * @param unit       마인드맵 생성 조직
	 * @param hasShared  공유 여부
	 * @return
	 * @throws Exception
	 */
	public List<String> list( String folderId, String name, String person, String unit, Boolean hasShared ) throws Exception {
		EntityManager em = this.entityManagerContainer().get(MindRecycleInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<MindRecycleInfo> root = cq.from(MindRecycleInfo.class);
		Predicate p = cb.isNotNull( root.get(MindRecycleInfo_.id ) );
		if( folderId != null && !folderId.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.id), folderId));
		}
		if( person != null && !person.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), person));
		}
		if( unit != null && !unit.isEmpty() ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), unit));
		}
		if( hasShared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), hasShared));
		}
		if( name != null && !name.isEmpty() ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name ), "%" + name + "%" ));
		}
		cq.orderBy( cb.asc( root.get( MindRecycleInfo_.updateTime ) ) );
		cq.select( root.get(MindRecycleInfo_.id ) );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 조건에 맞는 마인드맵 휴지통 정보 ID 목록 검색(페이지)
	 * @param count
	 * @param name
	 * @param folderId
	 * @param creator
	 * @param creatorUnit
	 * @param sequenceFieldValue
	 * @param orderField
	 * @param orderType
	 * @param inMindIds 
	 * @return
	 * @throws Exception 
	 */
	public List<MindRecycleInfo> listNextPageWithFilter(Integer count, String key, String folderId, Boolean shared, String creator, 
			String creatorUnit, Object sequenceFieldValue, String orderField, String orderType, List<String> inMindIds) throws Exception {
		if( orderType == null || orderType.isEmpty() ){
			orderType = "DESC";
		}
		
		EntityManager em = this.entityManagerContainer().get( MindRecycleInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<MindRecycleInfo> cq = cb.createQuery( MindRecycleInfo.class );
		Root<MindRecycleInfo> root = cq.from( MindRecycleInfo.class );
		
		Predicate p = root.get( MindRecycleInfo_.id ).isNotNull();
		if( ListTools.isNotEmpty( inMindIds )) {
			p = root.get( MindRecycleInfo_.id ).in( inMindIds );
		}

		if( sequenceFieldValue != null ){
			if( "folderId".equals( orderField  )){//폴더
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.folder_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.folder_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.folder_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "shared".equals( orderField  )){//공유 여부
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.shared_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.shared_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.shared_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "createTime".equals( orderField  )){//생성 시간
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.createTime ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.createTime ), (Date)sequenceFieldValue ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.createTime ), (Date)sequenceFieldValue ));
				}
			}else if( "creatorUnit".equals( orderField  )){//생성자 소속 조직
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.creatorUnit_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.creatorUnit_sequence ), sequenceFieldValue.toString() ));
				}
			}else if( "creator".equals( orderField  )){//작성자
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.creator_sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.creator_sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.creator_sequence ), sequenceFieldValue.toString() ));
				}
			}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
				p = cb.and( p, cb.isNotNull( root.get( MindRecycleInfo_.sequence ) ));
				if( "DESC".equalsIgnoreCase( orderType )){
					p = cb.and( p, cb.lessThan( root.get( MindRecycleInfo_.sequence ), sequenceFieldValue.toString() ));
				}else{
					p = cb.and( p, cb.greaterThan( root.get( MindRecycleInfo_.sequence ), sequenceFieldValue.toString() ));
				}
			}
		}
		
		if(  StringUtils.isNotEmpty(folderId) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.folderId), folderId));
		}
		if(  shared != null ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.shared), shared));
		}
		if(  StringUtils.isNotEmpty(creatorUnit) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creatorUnit), creatorUnit));
		}
		if(  StringUtils.isNotEmpty(creator) ){
			p = cb.and( p, cb.equal( root.get( MindRecycleInfo_.creator), creator));
		}
		if(  StringUtils.isNotEmpty(key) ){
			p = cb.and( p, cb.like( root.get( MindRecycleInfo_.name), "%"+key+"%"));
		}
		
		if( "folderId".equals( orderField  )){//폴더
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.folder_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.folder_sequence ) ) );
			}
		}else if( "shared".equals( orderField  )){//공유 여부
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.shared_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.shared_sequence ) ));
			}
		}else if( "createTime".equals( orderField  )){//생성 시간
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.createTime ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.createTime ) ));
			}
		}else if( "creatorUnit".equals( orderField  )){//생성자 소속 조직
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.creatorUnit_sequence ) ));
			}
		}else if( "creator".equals( orderField  )){//작성자
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.creator_sequence ) ));
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.creator_sequence ) ));
			}
		}else if(  JpaObject.sequence_FIELDNAME.equals( orderField  )){//sequence
			if( "DESC".equalsIgnoreCase( orderType )){
				cq.orderBy( cb.desc( root.get( MindRecycleInfo_.sequence ) ) );
			}else{
				cq.orderBy( cb.asc( root.get( MindRecycleInfo_.sequence ) ) );
			}
		}

		return em.createQuery(cq.where(p)).setMaxResults( count ).getResultList();
	}
}
