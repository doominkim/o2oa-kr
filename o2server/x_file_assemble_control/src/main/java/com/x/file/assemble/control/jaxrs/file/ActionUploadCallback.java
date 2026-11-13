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
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.open.File;
import com.x.file.core.entity.open.ReferenceType;

class ActionUploadCallback extends BaseAction {

	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String referenceType, String reference,
			Integer scale, String callback, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		if (effectivePerson.isAnonymous()) {
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create();
				ByteArrayInputStream in = new ByteArrayInputStream(bytes)) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			ReferenceType type = EnumUtils.getEnum(ReferenceType.class, referenceType);
			StorageMapping mapping = ThisApplication.context().storageMappings().random(File.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaapingCallback(callback);
			}
			/** createTime으로 path를 생성해야 하므로 먼저 값을 할당하고 검증한 뒤 저장 */
			/** 확장자 없는 파일 업로드 금지 */
			/** 파일명 인코딩 변환 */
			String fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
					DefaultCharset.charset);
			fileName = FilenameUtils.getName(fileName);
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtensionCallback(callback, fileName);
			}
			FileTools.verifyConstraint(bytes.length, fileName, null);
			File file = new File(mapping.getName(), fileName, effectivePerson.getDistinguishedName(), type, reference);
			emc.check(file, CheckPersistType.all);
			if ((scale > 0) && ArrayUtils.contains(IMAGE_EXTENSIONS_COMPRESS, file.getExtension())) {
				/** 압축이 필요한 첨부파일인 경우 */
				try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					BufferedImage image = ImageIO.read(in);
					if (image.getWidth() > scale) {
						/** 이미지 실제 크기가 scale보다 크면 압축 */
						BufferedImage scalrImage = Scalr.resize(image, Method.QUALITY, Mode.FIT_TO_WIDTH, scale);
						ImageIO.write(scalrImage, file.getExtension(), baos);
					} else {
						/** 이미지 실제 크기가 scale보다 작으면 압축하지 않고 원본 저장 */
						ImageIO.write(image, file.getExtension(), baos);
					}
					try (ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray())) {
						file.saveContent(mapping, bais, fileName);
					}
				}
			} else {
				file.saveContent(mapping, in, fileName);
			}
			emc.beginTransaction(File.class);
			emc.persist(file);
			emc.commit();
			CacheManager.notify(File.class);
			WoObject woObject = new WoObject();
			woObject.setId(file.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
	}
}
