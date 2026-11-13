package com.x.hotpic.assemble.control.queueTask;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.hotpic.assemble.control.queueTask.queue.DocumentCheckQueue;
import com.x.hotpic.assemble.control.service.BbsInfoChecker;
import com.x.hotpic.assemble.control.service.CmsInfoChecker;
import com.x.hotpic.assemble.control.service.HotPictureInfoServiceAdv;
import com.x.hotpic.assemble.control.service.inf.InfoCheckerInf;

public class DocumentExistsCheckTask extends AbstractQueue<DocumentCheckQueue> {

	private static Logger logger = LoggerFactory.getLogger( DocumentExistsCheckTask.class );
	private HotPictureInfoServiceAdv hotPictureInfoServiceAdv = new HotPictureInfoServiceAdv();
	
	/**
	 * 정보 존재 여부를 확인하고, 존재하지 않으면 데이터 삭제
	 * 
	 * @param documentCheckQueue
	 * @throws Exception
	 */
	protected void execute( DocumentCheckQueue documentCheckQueue ) throws Exception {
		
		Integer idx = documentCheckQueue.getIdx();
		String infoId = documentCheckQueue.getDocId();
		String infoType = documentCheckQueue.getDocType();
		String infoTitle = documentCheckQueue.getDocTitle();
		
		InfoCheckerInf infoCheckerInf = null;
		Boolean exists = false;
		if( idx < 100 ){
			if ("CMS".equalsIgnoreCase( infoType )) {
				infoCheckerInf = new CmsInfoChecker();
				//logger.info( "CMS Document is exists:" + infoTitle );
			} else if ("BBS".equalsIgnoreCase( infoType )) {
				infoCheckerInf = new BbsInfoChecker();
				//logger.info( "BBS Document is exists:" + infoTitle );
			} else {
				logger.info("hot picture application is not in CMS or BBS.delete document title:" + infoTitle );
				try {
					hotPictureInfoServiceAdv.deleteWithInfoId( infoId );
				} catch (Exception e) {
					logger.warn("system delete not exists picture info got an exception.");
					logger.error(e);
				}
			}
			if (infoCheckerInf != null) {
				exists = infoCheckerInf.check( infoId );
			}
			if (!exists) {
				hotPictureInfoServiceAdv.deleteWithInfoId( infoId );
				logger.info("Timertask Hotpicture InfoExistsCheckTask : hot picture has deleted.title:" + infoTitle );
			}
		}else{
			try {// 삭제(100건 초과분은 의미 없음)
				hotPictureInfoServiceAdv.deleteWithInfoId( infoId );
			} catch (Exception e) {
				logger.warn("system delete not exists picture info got an exception.");
				logger.error(e);
			}
		}
	}
}
