package com.x.bbs.assemble.control.factory;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.AbstractFactory;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.entity.BBSConfigSetting;
import com.x.bbs.entity.BBSConfigSetting_;


/**
 * 클래스명: BBSConfigSettingFactory<br/>
 * 엔티티 클래스: BBSConfigSetting<br/>
 * 작성자: Liyi<br/>
 * 소속: O2 Team<br/>
 * 날짜: 2016-05-20 17:17:26
**/
public class BBSConfigSettingFactory extends AbstractFactory {

	public BBSConfigSettingFactory(Business business) throws Exception {
		super(business);
	}
	
	//@MethodDescribe( "지정 Id의 BBSConfigSetting 엔티티 정보 객체 조회" )
	public BBSConfigSetting get( String id ) throws Exception {
		return this.entityManagerContainer().find(id, BBSConfigSetting.class, ExceptionWhen.none);
	}
	
	//@MethodDescribe( "전체 BBSConfigSetting 엔티티 정보 목록 조회" )
	@SuppressWarnings("unused")
	public List<BBSConfigSetting> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from( BBSConfigSetting.class);
		return em.createQuery(cq).getResultList();
	}
	
	//@MethodDescribe( "지정 Id의 BBSConfigSetting 엔티티 정보 목록 조회" )
	public List<BBSConfigSetting> list(List<String> ids) throws Exception {
		if( ids == null || ids.size() == 0 ){
			return new ArrayList<BBSConfigSetting>();
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = root.get(BBSConfigSetting_.id).in(ids);
		return em.createQuery(cq.where(p)).getResultList();
	}

	//@MethodDescribe( "지정한 설정 코드로 설정 값 조회" )
	public String getValueWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system setting!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = cb.equal( root.get( BBSConfigSetting_.configCode ), configCode );
		cq.select(root.get(BBSConfigSetting_.configValue));
		List<String> valueList = em.createQuery(cq.where(p)).getResultList();
		if( ListTools.isNotEmpty(valueList) ){
			return valueList.get(0);
		}
		return null;
	}

	public BBSConfigSetting getWithConfigCode(String configCode) throws Exception {
		if( configCode == null || configCode.isEmpty() ){
			throw new Exception( "config code is null, can not find any system setting!" );
		}
		EntityManager em = this.entityManagerContainer().get(BBSConfigSetting.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<BBSConfigSetting> cq = cb.createQuery(BBSConfigSetting.class);
		Root<BBSConfigSetting> root = cq.from(BBSConfigSetting.class);
		Predicate p = cb.equal( root.get( BBSConfigSetting_.configCode ), configCode );
		List<BBSConfigSetting> valueList = em.createQuery(cq.where(p)).getResultList();
		if( ListTools.isNotEmpty(valueList) ){
			return valueList.get(0);
		}
		return null;
	}
}
