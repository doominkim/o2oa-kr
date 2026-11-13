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
import com.x.cms.core.entity.element.ViewFieldConfig;
import com.x.cms.core.entity.element.ViewFieldConfig_;

/**
 * 뷰 설정 관리 기본 기능 서비스 클래스
 * 
 * @author O2LEE
 */
public class ViewFieldConfigFactory extends AbstractFactory {

	public ViewFieldConfigFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정한 Id의 ViewFieldConfig 정보 객체 조회
	 * @param id
	 * @return ViewFieldConfig
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 ViewFieldConfig 정보 객체 조회")
	public ViewFieldConfig get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, ViewFieldConfig.class, ExceptionWhen.none );
	}
	
	/**
	 * 전체 ViewFieldConfig 정보 ID 목록 조회
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("전체 ViewFieldConfig 첨부파일 정보 ID 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
		cq.select(root.get(ViewFieldConfig_.id));
		return em.createQuery(cq).getResultList();
	}
	
	/**
	 * 지정한 Id의 ViewFieldConfig 정보 ID 목록 조회
	 * @param ids 조회할 ID 목록
	 * @return List：ViewFieldConfig
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 ViewFieldConfig 첨부파일 정보 ID 목록 조회")
//	public List<ViewFieldConfig> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<ViewFieldConfig> cq = cb.createQuery( ViewFieldConfig.class );
//		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
//		Predicate p = root.get(ViewFieldConfig_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}
	
	/**
	 * 지정한 뷰의 모든 표시 열 설정 정보 ID 목록 조회
	 * @param id 지정한 뷰 ID
	 * @return
	 * @throws Exception 
	 */
	//@MethodDescribe("지정한 뷰의 모든 표시 열 설정 정보 ID 목록 조회")
	public List<String> listByViewId( String id ) throws Exception {		
		if( StringUtils.isEmpty(id) ){
			throw new Exception("게시판 listByViewId 메서드는 id가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( ViewFieldConfig.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewFieldConfig> root = cq.from( ViewFieldConfig.class );
		cq.select(root.get(ViewFieldConfig_.id));
		Predicate p = cb.equal(root.get( ViewFieldConfig_.viewId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}
}