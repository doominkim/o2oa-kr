package com.x.cms.assemble.control.queue;

import com.x.base.core.project.x_hotpic_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.cms.assemble.control.ThisApplication;
import com.x.cms.core.entity.Document;

/**
 * Document 제목 변경 시 인기 이미지의 데이터도 함께 업데이트해야 함
 *
 */
public class QueueDocumentUpdate extends AbstractQueue<Document> {

	private static Logger logger = LoggerFactory.getLogger(QueueDocumentUpdate.class);

	public void execute(Document document) throws Exception {
		WrapInHotPictureInfo hotPictureInfo = new WrapInHotPictureInfo();
		hotPictureInfo.setApplication("CMS");
		hotPictureInfo.setTitle(document.getTitle());
		hotPictureInfo.setInfoId(document.getId());
		try {
			ThisApplication.context().applications().postQuery(x_hotpic_assemble_control.class, "changeTitle",
					hotPictureInfo);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static class WrapInHotPictureInfo {

		@FieldDescribe("앱 이름")
		private String application = "";

		@FieldDescribe("정보 객체 ID")
		private String infoId = "";

		@FieldDescribe("정보 제목")
		private String title = "";

		@FieldDescribe("정보 접근 URL")
		private String url = "";

		@FieldDescribe("정보 이미지 URL")
		private String picId = "";

		@FieldDescribe("작성자")
		private String creator = "";

		public String getApplication() {
			return application;
		}

		public void setApplication(String application) {
			this.application = application;
		}

		public String getInfoId() {
			return infoId;
		}

		public void setInfoId(String infoId) {
			this.infoId = infoId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getCreator() {
			return creator;
		}

		public void setCreator(String creator) {
			this.creator = creator;
		}

		public String getPicId() {
			return picId;
		}

		public void setPicId(String picId) {
			this.picId = picId;
		}

	}
}
