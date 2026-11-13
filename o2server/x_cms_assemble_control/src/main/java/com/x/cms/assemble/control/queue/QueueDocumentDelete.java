package com.x.cms.assemble.control.queue;

import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.ThisApplication;

/**
 * Document 삭제 시 핫 이미지의 데이터도 삭제되었는지 확인 필요
 *
 */
public class QueueDocumentDelete extends AbstractQueue<String> {
	
	//{application}/{infoId}
	public void execute( String docId ) throws Exception {
		try {
			ThisApplication.context().applications().deleteQuery( x_hotpic_assemble_control.class, "cipher/hotpic/cms/" + docId );
		}catch( Exception e ) {
			e.printStackTrace();
		}		
	}
}
