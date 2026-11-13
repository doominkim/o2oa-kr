package com.x.cms.assemble.control.jaxrs.fileinfo;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.FileInfo;
import javax.servlet.http.HttpServletRequest;

public class ActionDelete extends BaseAction {

	private static final  Logger logger = LoggerFactory.getLogger(ActionDelete.class);
	protected ActionResult<Wo> execute( HttpServletRequest request, EffectivePerson effectivePerson, String id ) throws Exception {
		logger.debug("ActionDelete fileInfo:{}.", effectivePerson.getDistinguishedName());
		ActionResult<Wo> result = new ActionResult<>();

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			// 먼저 작업할 앱 정보 존재 여부를 ID로 조회하고, 없으면 작업을 계속할 수 없음
			FileInfo fileInfo = business.getFileInfoFactory().get(id);
			if (null == fileInfo) {
				throw new ExceptionEntityNotExist(id, FileInfo.class);
			}
			// 문서 정보 존재 여부 판단
			Document document = business.getDocumentFactory().get(fileInfo.getDocumentId());
			if (null == document) {
				throw new ExceptionEntityNotExist(fileInfo.getDocumentId(), Document.class);
			}
			if(!business.isDocumentEditor(effectivePerson, null, null, document)){
				throw new ExceptionAccessDenied(effectivePerson);
			}

			// 파일을 삭제하고 기록 및 문서 연결 정보도 삭제
			StorageMapping mapping = ThisApplication.context().storageMappings().get(FileInfo.class, fileInfo.getStorage());

			// FTP에서 파일 삭제
			fileInfo.deleteContent(mapping);
			emc.beginTransaction(FileInfo.class);
			emc.remove(fileInfo);
			emc.commit();

			CacheManager.notify(Document.class);
			CacheManager.notify(FileInfo.class);

			// 첨부파일 정보 1건 삭제 성공
			logService.log(emc, effectivePerson.getDistinguishedName(), fileInfo.getName(), fileInfo.getAppId(),
					fileInfo.getId(), fileInfo.getDocumentId(), fileInfo.getId(), "FILE", "삭제");

			Wo wo = new Wo();
			wo.setId( fileInfo.getId() );
			result.setData( wo );

		}
		return result;
	}
	public static class Wo extends WoId {

	}
}
