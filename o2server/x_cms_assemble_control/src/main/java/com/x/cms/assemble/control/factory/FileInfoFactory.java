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
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.FileInfo_;

public class FileInfoFactory extends AbstractFactory {

	public FileInfoFactory( Business business ) throws Exception {
		super(business);
	}

	/**
	 * @param id
	 * @return FileInfo
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 FileInfo 첨부파일 정보 객체 조회")
	public FileInfo get( String id ) throws Exception {
		return this.entityManagerContainer().find( id, FileInfo.class, ExceptionWhen.none );
	}

	/**
	 * @return List：String
	 * @throws Exception
	 */
	//@MethodDescribe("전체 FileInfo 첨부파일 정보 ID 목록 조회")
	public List<String> listAll() throws Exception {
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<FileInfo> root = cq.from( FileInfo.class );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq).getResultList();
	}

	/**
	 *
	 * @param ids 조회할 ID 목록
	 * @return List：FileInfo
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 Id의 FileInfo 첨부파일 정보 ID 목록 조회")
//	public List<FileInfo> list(List<String> ids) throws Exception {
//		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
//		CriteriaBuilder cb = em.getCriteriaBuilder();
//		CriteriaQuery<FileInfo> cq = cb.createQuery( FileInfo.class );
//		Root<FileInfo> root = cq.from( FileInfo.class );
//		Predicate p = root.get(FileInfo_.id).in(ids);
//		return em.createQuery(cq.where(p)).getResultList();
//	}

	/**
	 * 지정한 문서의 모든 첨부파일 및 파일 정보 ID 목록 조회
	 * @param doucmentId 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 문서의 모든 첨부파일 및 파일 정보 ID 목록 조회")
	public List<String> listAllByDocument( String doucmentId ) throws Exception {
		if( StringUtils.isEmpty(doucmentId) ){
			throw new Exception("게시판 listByDocument 메서드는 document가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), doucmentId);
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정한 문서의 모든 첨부파일 정보 ID 목록 조회
	 * @param doucmentId 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 문서의 모든 첨부파일 정보 ID 목록 조회")
	public List<String> listAttachmentByDocument( String doucmentId ) throws Exception {
		if( StringUtils.isEmpty(doucmentId) ){
			throw new Exception("게시판 listByDocument 메서드는 document가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), doucmentId);
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileType ), "ATTACHMENT") );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}

	/**
	 * 지정한 문서의 모든 클라우드 파일 이미지 정보 ID 목록 조회
	 * @param documentId 지정한 문서 ID
	 * @return
	 * @throws Exception
	 */
	//@MethodDescribe("지정한 문서의 모든 클라우드 파일 이미지 정보 ID 목록 조회")
	public List<String> listCloudPictureByDocument(String documentId) throws Exception {
		if( StringUtils.isEmpty(documentId) ){
			throw new Exception("게시판 listByDocument 메서드는 document가 비어 있는 조회를 허용하지 않습니다!");
		}
		EntityManager em = this.entityManagerContainer().get( FileInfo.class );
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery( String.class );
		Root<FileInfo> root = cq.from( FileInfo.class );
		Predicate p = cb.equal(root.get( FileInfo_.documentId ), documentId);
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileExtType ), "PICTURE") );
		p = cb.and( p, cb.equal(root.get( FileInfo_.fileType ), "CLOUD") );
		cq.select(root.get(FileInfo_.id));
		return em.createQuery(cq.where(p)).getResultList();
	}
}
