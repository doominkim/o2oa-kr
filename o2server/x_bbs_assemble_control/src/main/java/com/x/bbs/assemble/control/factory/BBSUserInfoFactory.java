package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSUserInfo;
import com.x.bbs.entity.BBSUserInfo_;

/**
 * 클래스명: BBSUserInfoFactory<br/>
 * 엔티티 클래스: BBSUserInfo<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-08-10 17:17:26
**/
public class BBSUserInfoFactory extends AbstractFactory {

	public BBSUserInfoFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSUserInfo 엔티티 정보 객체 조회" )
	public BBSUserInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, BBSUserInfo.class, ExceptionWhen.none );
	}
	
	//@MethodDescribe( "지정 Id의 BBSUserInfo 엔티티 정보 목록 조회" )
	public List<BBSUserInfo> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSUserInfo>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserInfo> cq = cb.createQuery(BBSUserInfo.class);
		Root<BBSUserInfo> root = cq.from(BBSUserInfo.class);
		Predicate p = root.get(BBSUserInfo_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}
	
	//@MethodDescribe( "지정 이름의 BBSUserInfo 엔티티 정보 목록 조회" )
	public List<BBSUserInfo> listByUserName( String userName ) throws Exception {
		if( userName == null || userName.isEmpty() ){
			throw new Exception( "userName is null!");
		}
		EntityManager em = this.entityManagerContainer().get(BBSUserInfo.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSUserInfo> cq = cb.createQuery(BBSUserInfo.class);
		Root<BBSUserInfo> root = cq.from(BBSUserInfo.class);
		Predicate p = cb.equal( root.get(BBSUserInfo_.userName), userName );
		return em.createQuery(cq.where(p)).getResultList();
	}
}
