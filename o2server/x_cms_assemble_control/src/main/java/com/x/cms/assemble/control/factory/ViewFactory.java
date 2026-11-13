package com.x.cms.assemble.control.factory;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.View;
import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewCategory_;
import com.x.cms.core.entity.element.View_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * 뷰 설정 관리 기본 기능 서비스 클래스
 *
 * @author O2LEE
 */
public class ViewFactory extends AbstractFactory {

	public ViewFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * 지정한 Id의 View 정보 객체 조회
	 * @param id
	 * @return View
	 * @throws Exception
	 */
	public View get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, View.class );
	}

	public View flag( String flag ) throws Exception {
		return this.entityManagerContainer().flag( flag, View.class );
	}

	/**
	 * 전체 View 정보 ID 목록 조회
	 * @return List：String
	 * @throws Exception
	 */
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * 지정한 분류의 모든 뷰 설정 정보 ID 목록 조회
	 * @param categoryId 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByCategoryId( String categoryId ) throws Exception {
		if( StringUtils.isEmpty(categoryId) ){
			throw new Exception("게시판 listByCategoryId 메서드는 categoryId가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( ViewCategory.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ViewCategory> root = cq.from( ViewCategory.class );
		cq.select( root.get(ViewCategory_.viewId ));
		Predicate p = cb.equal(root.get( ViewCategory_.categoryId ), categoryId );
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정한 앱 ID의 모든 뷰 설정 정보 ID 목록 조회
	 * @param id 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByAppId( String id ) throws Exception {
		if( StringUtils.isEmpty(id) ){
			throw new Exception("게시판 listByAppId 메서드는 id가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.appId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정한 양식 ID의 모든 뷰 설정 정보 ID 목록 조회
	 * @param id 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	public List<String> listByFormId( String id ) throws Exception {
		if( StringUtils.isEmpty(id) ){
			throw new Exception("게시판 listByFormId 메서드는 id가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( View.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<View> root = cq.from( View.class );
		cq.select(root.get(View_.id));
		Predicate p = cb.equal(root.get( View_.formId ), id);
		return em.createQuery(cq.where(p)).getResultList();
	}

}
