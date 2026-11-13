package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewCategory_;

/**
 * 뷰 설정 관리 기본 기능 서비스 클래스
 * 
 * @author O2LEE
 */
public class ViewCategoryFactory extends AbstractFactory {

	public ViewCategoryFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정한 Id의 ViewCategory 정보 객체 조회
	 * @param id
	 * @return ViewCategory
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 ViewCategory 정보 객체 조회")
	public ViewCategory get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ViewCategory.class, ExceptionWhen.none );
	}
	
	/**
	 * 전체 ViewCategory 정보 ID 목록 조회
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("전체 ViewCategory 첨부파일 정보 ID 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 지정한 Id의 ViewCategory 정보 ID 목록 조회
	 * @param ids 조회할 ID 목록
	 * @return List：ViewCategory
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 ViewCategory 첨부파일 정보 ID 목록 조회")
//	public List<ViewCategory> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ViewCategory> cq = cb.createQuery( ViewCategory.class );
//		Root<ViewCategory> root = cq.from( ViewCategory.class );
//		Predicate p = root.get(ViewCategory_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 지정한 분류의 모든 뷰 설정 정보 ID 목록 조회
	 * @param id 지정한 분류 ID
	 * @return
	 * @throws Exception 
	 */
//	@MethodDescribe("지정한 분류의 모든 뷰-분류 연결 설정 정보 ID 목록 조회")
	public List<String> listByCategoryId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("게시판 listViewByCategoryId 메서드는 id가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.viewId));
		Predicate p = cb.equal(root.get( ViewCategory_.categoryId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	/**
	 * 지정한 뷰가 사용되는 분류 조회
	 * @param id 지정한 뷰 ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("지정한 뷰의 모든 뷰-분류 연결 정보 조회")
	public List<String> listByViewId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("게시판 listCategoryByViewId 메서드는 id가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select(root.get(ViewCategory_.id));
		Predicate p = cb.equal(root.get( ViewCategory_.viewId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public ViewCategory getByViewAndCategory(String viewId, String categoryId) throws Exception {
		if( StringUtils.isEmpty(viewId) ){
			throw new Exception("viewId is null!");
		}
		if( StringUtils.isEmpty(categoryId) ){
			throw new Exception("categoryId is null!");
		}
		List<ViewCategory> list = null;
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ViewCategory> cq = cb.createQuery(ViewCategory.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		Predicate p = cb.equal(root.get( ViewCategory_.viewId ), viewId);
		p = cb.and( p, cb.equal(root.get( ViewCategory_.categoryId ), categoryId));
		list = em.createQuery(cq.where(p)).getResultList();
		if( list != null && !list.isEmpty() ){
			return list.get( 0 );
		}
		return null;
	}
}