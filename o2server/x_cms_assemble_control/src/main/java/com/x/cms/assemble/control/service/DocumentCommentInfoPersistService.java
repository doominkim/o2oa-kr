package com.x.cms.assemble.control.service;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.EffectivePerson;
import com.x.cms.core.entity.DocumentCommentInfo;
/**
 * 댓글 정보 영속화 서비스
 *
 * @author O2LEE
 */
public class DocumentCommentInfoPersistService {

	private DocumentCommentInfoService documentCommentInfoService = new DocumentCommentInfoService();

	public void delete( String flag, EffectivePerson effectivePerson ) throws Exception {
		if ( StringUtils.isEmpty( flag )) {
			throw new Exception("flag is empty.");
		}
		Boolean hasDeletePermission = false;
		if( effectivePerson.isManager() ) {
			hasDeletePermission = true;
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			DocumentCommentInfo documentCommentInfo = documentCommentInfoService.get(emc, flag);
			//관리자와 작성자는 삭제 가능
			if( !hasDeletePermission ) {
				//댓글 정보 작성자인지 확인
				if( documentCommentInfo.getCreatorName().equalsIgnoreCase( effectivePerson.getDistinguishedName() )) {
					hasDeletePermission = true;
				}
			}
			if( !hasDeletePermission ) {
				throw new Exception("documentCommentInfo delete permission denied.");
			}else {
				documentCommentInfoService.delete( emc, flag );
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 댓글 정보 저장
	 * @param documentCommentInfo
	 * @param effectivePerson
	 * @return
	 * @throws Exception
	 */
	public DocumentCommentInfo save( DocumentCommentInfo documentCommentInfo, String content, EffectivePerson effectivePerson ) throws Exception {
		if ( documentCommentInfo == null) {
			throw new Exception("documentCommentInfo is null.");
		}
		if( StringUtils.isEmpty( documentCommentInfo.getTitle() )) {
			if(content.length() > 70){
				documentCommentInfo.setTitle(content.substring(0, 70) + "...");
			}else{
				documentCommentInfo.setTitle(content);
			}
		}
		try ( EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			documentCommentInfo = documentCommentInfoService.save( emc, documentCommentInfo, content );
		} catch (Exception e) {
			throw e;
		}
		return documentCommentInfo;
	}
}
