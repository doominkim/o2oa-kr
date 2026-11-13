package com.x.cms.assemble.control.jaxrs.appinfo;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.DocumentDataHelper;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import com.x.cms.core.entity.Review;

public class ActionEraseDocumentWithAppInfo extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionEraseDocumentWithAppInfo.class);

	@AuditLog(operation = "删除栏目下所有的文档")
	protected ActionResult<Wo> execute(HttpServletRequest request, String id, EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AppInfo appInfo = null;
		Boolean check = true;
		
		if ( StringUtils.isEmpty(id) ) {
			check = false;
			Exception exception = new ExceptionIdEmpty();
			result.error(exception);
		}

		if( check ){
			try {
				appInfo = appInfoServiceAdv.get( id );
				if( appInfo == null ){
					check = false;
					Exception exception = new ExceptionAppInfoNotExists( id );
					result.error( exception );
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionAppInfoProcess( e, "지정한 ID의 게시판 정보 객체를 조회하는 중 오류가 발생했습니다. ID:" + id );
				result.error( exception );
				logger.error( e, effectivePerson, request, null);
			}
		}

		if ( check ) {
			//게시판의 문서 수 조회
			Long count = documentServiceAdv.countByAppId( id );
			List<String> idsForDelete = null;
			List<String> allFileInfoIds = null;	
			Document document = null;
			Integer queryMaxCount = 100;
			Integer whileCount = 0;
			Integer currentWhileCount = 0;
			FileInfo fileInfo = null;
			StorageMapping mapping = null;
			DocumentDataHelper documentDataHelper = null;
			
			if ( count > 0 ) {
				logger.debug("there are "+count+" documents need to delete.");
				result.setCount(count);
				whileCount =  (int) ( count/queryMaxCount + 1 );
				try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
					Business business = new Business( emc );
					
					//분류의 모든 문서 정보를 반복 삭제
					while( count > 0 && currentWhileCount<=whileCount ) {
						logger.info("search document with appId for max count '"+queryMaxCount+"' document ids.");
						idsForDelete = documentServiceAdv.listIdsByAppId( id, null, queryMaxCount );
						if( ListTools.isNotEmpty(  idsForDelete )) {
							for( String docId : idsForDelete ) {
								logger.info("system try to delete document. id: '"+docId+"'" );
								emc.beginTransaction( Document.class );
								emc.beginTransaction( FileInfo.class );
								try {
									document = emc.find( docId, Document.class );
									if( document != null ){
										logger.info("system try to delete document. title: '"+document.getTitle()+"'" );
									}
									//해당 문서와 관련된 모든 데이터 Item 정보 삭제
									documentDataHelper = new DocumentDataHelper( emc, document );
									documentDataHelper.remove();
									
									//문서의 첨부파일 정보 삭제
									allFileInfoIds = business.getFileInfoFactory().listAllByDocument( docId );
									if( ListTools.isNotEmpty( allFileInfoIds ) ){
										for( String fileInfoId : allFileInfoIds ){
											fileInfo = emc.find( fileInfoId, FileInfo.class );
											if( fileInfo != null ){
												if( "ATTACHMENT".equals( fileInfo.getFileType() )){
													mapping = ThisApplication.context().storageMappings().get( FileInfo.class, fileInfo.getStorage() );
													fileInfo.deleteContent( mapping );
												}
											}
											emc.remove( fileInfo, CheckRemoveType.all );
										}
									}
									//문서 정보 삭제
									if( document != null ){
										emc.remove( document, CheckRemoveType.all  );
									}
								}catch( Exception e ) {
										e.printStackTrace();
								}
								//먼저 트랜잭션 커밋
								emc.commit();

								try {
									//해당 문서의 모든 Review 정보 삭제
									emc.beginTransaction( Review.class );
									Review review = null;
									List<String> reviewIds = business.reviewFactory().listByDocument( docId, 2000 );
									while( ListTools.isNotEmpty( reviewIds )){
										for( String reviewId : reviewIds ){
											logger.info("system try to delete document review. id: '"+reviewId+"'" );
											review = emc.find( reviewId, Review.class );
											if( review != null ){
												emc.remove( review, CheckRemoveType.all );
											}
										}
										emc.commit();
										reviewIds = business.reviewFactory().listByDocument( docId, 2000 );
									}
								}catch( Exception e ) {
									e.printStackTrace();
								}
							}
							CacheManager.notify( Document.class );
						}

						count = documentServiceAdv.countByAppId( id );
						logger.debug("cms delete " + queryMaxCount + "documents, and left "+count+" documents need to delete.");
					}
				}
			}
		}
		
		Wo wo = new Wo();
		wo.setId(id);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WoId {

	}
}