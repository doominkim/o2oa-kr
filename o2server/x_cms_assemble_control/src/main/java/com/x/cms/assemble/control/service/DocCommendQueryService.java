package com.x.cms.assemble.control.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;

/**
 * 문서 좋아요 정보 조회 관리 서비스 클래스
 *
 * @author O2LEE
 */
public class DocCommendQueryService {

	private DocCommendService docCommendService = new DocCommendService();

	public List<String> listByDocAndPerson( String docId, String personName, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listByDocAndPerson(emc, docId, personName, maxCount, type);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByCommentAndPerson(String commentId, String personName, Integer maxCount) throws Exception {
		if( StringUtils.isEmpty( commentId ) ){
			return null;
		}
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listByCommentAndPerson(emc, commentId, personName, maxCount);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listByDocument( String docId, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( docId ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listByDocument( emc, docId, maxCount, type);
		} catch ( Exception e ) {
			throw e;
		}
	}

	public List<String> listWithPerson( String personName, Integer maxCount, String type) throws Exception {
		if( StringUtils.isEmpty( personName ) ){
			return null;
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create() ) {
			return docCommendService.listWithPerson(emc, personName, maxCount, type);
		} catch ( Exception e ) {
			throw e;
		}
	}
}
