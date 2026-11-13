package com.x.file.assemble.control.jaxrs.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.x.base.core.project.tools.FileTools;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.AuditLog;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

public class ActionUploadWithUrl extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionUploadWithUrl.class);

	@AuditLog(operation = "上传附件")
	protected ActionResult<Wo> execute(EffectivePerson effectivePerson,
										JsonElement jsonElement) throws Exception {
		logger.debug("ActionFileUploadWithUrl receive:{}.", jsonElement.toString());
		if (effectivePerson.isAnonymous()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		ActionResult<Wo> result = new ActionResult<>();

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isEmpty(wi.getReference())){
			throw new ExceptionEntityFieldEmpty(File.class, "reference");
		}
		if(StringUtils.isEmpty(wi.getFileName())){
			throw new ExceptionEntityFieldEmpty(File.class, "fileName");
		}
		if(StringUtils.isEmpty(wi.getFileUrl())){
			throw new ExceptionEntityFieldEmpty(File.class, "fileUrl");
		}
		if(wi.getScale()==null){
			throw new ExceptionEntityFieldEmpty(File.class, "scale");
		}
		String fileName = wi.getFileName();
		if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
			throw new ExceptionEmptyExtension(fileName);
		}

		File file = null;
		ReferenceType type = EnumUtils.getEnum(ReferenceType.class, wi.getReferenceType());
		StorageMapping mapping = ThisApplication.context().storageMappings().random( File.class );
		if (null == mapping) {
			throw new ExceptionAllocateStorageMaaping();
		}
		byte[] bytes = CipherConnectionAction.getBinary(false, wi.getFileUrl());
		if(bytes==null || bytes.length==0){
			throw new ExceptionEntityFieldEmpty(File.class, "bytes");
		}
		FileTools.verifyConstraint(bytes.length, fileName, null);
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			 ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			/* 원본 이미지 먼저 저장 */
			file = new File(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), type, wi.getReference());
			emc.check(file, CheckPersistType.all);
			file.saveContent(mapping, in, fileName);
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			wo.setOrigId(file.getId());
		}

		/*압축 이미지 저장*/
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
			 ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			if ((wi.getScale() > 0) && ArrayUtils.contains(IMAGE_EXTENSIONS_COMPRESS, file.getExtension())) {
				String fileNameThumbnail = fileName.substring(0, fileName.lastIndexOf(".")) + "_t" + "."+ file.getExtension();
				File fileThumbnail = new File(mapping.getName(), fileNameThumbnail,effectivePerson.getDistinguishedName(), type, wi.getReference());
				/** 압축이 필요한 첨부파일인 경우 */
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					BufferedImage image = ImageIO.read(in);
					if (image.getWidth() > wi.getScale()) {
						/** 이미지 실제 크기가 scale보다 크면 압축 */
						BufferedImage scalrImage = Scalr.resize(image, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, wi.getScale());
						ImageIO.write(scalrImage, fileThumbnail.getExtension(), baos);
					} else {
						/** 이미지 실제 크기가 scale보다 작으면 압축하지 않고 원본 저장 */
						ImageIO.write(image, fileThumbnail.getExtension(), baos);
					}
					try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
						fileThumbnail.saveContent(mapping, bais, fileNameThumbnail);
					}

					emc.beginTransaction(File.class);
					emc.persist(fileThumbnail);
					emc.commit();
					wo.setId(fileThumbnail.getId());
				}

			} else {
				//압축하지 않고 원본 이미지 id 저장
				wo.setId(file.getId());
			}
		}

		CacheManager.notify(File.class);

		result.setData(wo);

		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -3707551996175419386L;

		@FieldDescribe("파일 유형: processPlatformJob|processPlatformForm|mindInfo|portalPage|cmsDocument|forumDocument|forumReply|component|teamworkProject")
		private String referenceType;

		@FieldDescribe("파일 이름, 확장자를 포함한 파일명.")
		private String fileName;

		@FieldDescribe("*첨부파일 원본 url 주소.")
		private String fileUrl;

		@FieldDescribe("*연관 id.")
		private String reference;

		@FieldDescribe("축소·확대, 지정 너비가 <=0이면 압축하지 않음")
		private Integer scale;

		public String getReferenceType() {
			return referenceType;
		}

		public void setReferenceType(String referenceType) {
			this.referenceType = referenceType;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String getFileUrl() {
			return fileUrl;
		}

		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}

		public String getReference() {
			return reference;
		}

		public void setReference(String reference) {
			this.reference = reference;
		}

		public Integer getScale() {
			return scale;
		}

		public void setScale(Integer scale) {
			this.scale = scale;
		}
	}

	public static class Wo extends GsonPropertyObject {

		public Wo() {

		}

		public Wo(String id) throws Exception {
			this.id = id;
		}

		@FieldDescribe("썸네일 id")
		private String id;

		@FieldDescribe("원본 이미지 id")
		private String origId;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getOrigId() {
			return origId;
		}

		public void setOrigId(String origId) {
			this.origId = origId;
		}
	}
}
