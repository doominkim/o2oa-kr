package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.Form;
import com.x.cms.core.entity.element.Form_;
import com.x.cms.core.express.tools.CriteriaBuilderTools;


/**
 * 분류 양식 템플릿 정보 관리 테이블 기본 기능 서비스 클래스
 *
 * @author O2LEE
 */
public class FormFactory extends ElementFactory {

	public FormFactory( Business business ) throws Exception {
		super(business);
	}

	public Form pick(String flag) throws Exception {
		return this.pick(flag, ExceptionWhen.none);
	}

	@Deprecated
	public Form pick(String flag, ExceptionWhen exceptionWhen) throws Exception {
		return this.pick(flag, Form.class);
	}

	/**
	 * @param id
	 * @return Form
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 Form 첨부파일 정보 객체 조회")
	public Form get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, Form.class, ExceptionWhen.none );
	}

	/**
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("전체 Form 첨부파일 정보 ID 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( Form.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from( Form.class );
		cq.select(root.get(Form_.id));
		return em.createQuery(cq).getResultList();
	}

	/**
	 *
	 * @param ids 조회할 ID 목록
	 * @return List：Form
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 Form 첨부파일 정보 ID 목록 조회")
//	public List<Form> list(List<String> ids) throws Exception {
//		if(ListTools.isEmpty( ids )) {
//			return null;
//		}
//		EntityManager em = this.entityManagerContainer().get( Form.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<Form> cq = cb.createQuery( Form.class );
//		Root<Form> root = cq.from( Form.class );
//		Predicate p = root.get(Form_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}

	/**
	 * 지정한 앱의 모든 양식 템플릿 정보 ID 목록 조회
	 * @param appId 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 분류의 모든 양식 템플릿 정보 ID 목록 조회")
	public List<String> listByAppId( String appId ) throws Exception {
		if( StringUtils.isEmpty(appId) ){
			throw new Exception("게시판 listByAppId 메서드는 appId가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( Form.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from( Form.class );
		cq.select(root.get(Form_.id));
		Predicate p = cb.equal(root.get( Form_.appId ), appId);
		return em.createQuery(cq.where(p)).getResultList();
	}

	public List<String> listByAppIds(List<String> appIds) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Form.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Form> root = cq.from(Form.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(appIds)) {
			p = cb.isMember(root.get(Form_.appId), cb.literal(appIds));
		}
		cq.select(root.get(Form_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Form> listFormByAppId( String appId ) throws Exception {
		if( StringUtils.isEmpty(appId) ){
			throw new Exception("게시판 listByAppId 메서드는 appId가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( Form.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Form> cq = cb.createQuery(Form.class);
		Root<Form> root = cq.from( Form.class );
		Predicate p = cb.equal(root.get( Form_.appId ), appId);
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 게시판 ID와 양식 식별자로 양식 객체 조회
	 * @param appId 게시판 ID
	 * @param formFlag 양식 식별자
	 * @return
	 * @throws Exception
	 */
	public Form getWithAppInfo(String appId, String formFlag) throws Exception {
		if( StringUtils.isEmpty(appId) ){
			throw new Exception("appId can not empty！");
		}
		if( StringUtils.isEmpty(formFlag) ){
			throw new Exception("formFlag can not empty！");
		}
		EntityManager em = this.entityManagerContainer().get( Form.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Form> cq = cb.createQuery(Form.class);
		Root<Form> root = cq.from( Form.class );
		Predicate appPre = cb.equal( root.get( Form_.appId ), appId );
		Predicate p = CriteriaBuilderTools.predicate_or( cb, cb.equal( root.get( Form_.id ), formFlag ), cb.equal( root.get( Form_.name ), formFlag ) );
//		p = CriteriaBuilderTools.predicate_or( cb, cb.equal( root.get( Form_.alias ), formFlag ), p );
		p = CriteriaBuilderTools.predicate_and( cb, appPre, p );
		List<Form> list = em.createQuery(cq.where(p)).getResultList();
		return ListTools.isEmpty( list ) ? null : list.get( 0 );
	}
}
