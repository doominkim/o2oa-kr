package com.x.file.assemble.control.jaxrs.attachment;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;
import com.x.file.assemble.control.Business;
import com.x.file.assemble.control.ThisApplication;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment_;
import com.x.file.core.entity.personal.Folder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

class ActionUploadCallback extends BaseAction {

	// @HttpMethodDescribe(value = "Attachment 객체 생성, 상위 디렉터리가 없으면 (0)으로 대체.", response =
	// WrapOutId.class)
	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String folderId, String callback,
			String fileName, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			if ((!StringUtils.isEmpty(folderId)) && (!StringUtils.equalsIgnoreCase(folderId, EMPTY_SYMBOL))) {
				Folder folder = emc.find(folderId, Folder.class);
				if (null == folder) {
					throw new ExceptionFolderNotExistCallback(callback, folderId);
				}
				if (!StringUtils.equals(business.organization().person().get(folder.getPerson()),
						effectivePerson.getDistinguishedName())) {
					throw new ExceptionFolderAccessDeniedCallback(effectivePerson, callback, folder);
				}
				folderId = folder.getId();
			} else {
				folderId = null;
			}
			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			if (null == mapping) {
				throw new ExceptionAllocateStorageMaapingCallback(callback);
			}
			/** 검증을 위해 모든 필수 필드를 채워야 함 */

			/** 파일명 인코딩 변환 */
			if (StringUtils.isEmpty(fileName)) {
				fileName = new String(disposition.getFileName().getBytes(DefaultCharset.charset_iso_8859_1),
						DefaultCharset.charset);
			}
			fileName = FilenameUtils.getName(fileName);
			/** 확장자 없는 파일 업로드 금지 */
			if (StringUtils.isEmpty(FilenameUtils.getExtension(fileName))) {
				throw new ExceptionEmptyExtensionCallback(callback, fileName);
			}
			/** 같은 디렉터리 내 파일명은 고유 */
			if (this.exist(business, fileName, folderId)) {
				throw new ExceptionSameNameFileExistCallback(callback, fileName);
			}
			FileTools.verifyConstraint(bytes.length, fileName, callback);
			Attachment attachment = new Attachment(mapping.getName(), fileName, effectivePerson.getDistinguishedName(),
					folderId);
			emc.check(attachment, CheckPersistType.all);
			attachment.saveContent(mapping, bytes, fileName);
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment);
			emc.commit();
			WoObject woObject = new WoObject();
			woObject.setId(attachment.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	private Boolean exist(Business business, String fileName, String folderId) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.name), fileName);
		if (StringUtils.isNotEmpty(folderId)) {
			p = cb.and(p, cb.equal(root.get(Attachment_.folder), folderId));
		} else {
			p = cb.and(p, cb.or(cb.isNull(root.get(Attachment_.folder)), cb.equal(root.get(Attachment_.folder), "")));
		}
		return em.createQuery(cq.select(cb.count(root)).where(p)).getSingleResult() > 0;
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {
	}

}
