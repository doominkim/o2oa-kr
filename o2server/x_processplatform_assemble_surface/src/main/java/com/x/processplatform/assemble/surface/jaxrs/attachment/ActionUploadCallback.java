package com.x.processplatform.assemble.surface.jaxrs.attachment;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCallback;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.Control;
import com.x.processplatform.assemble.surface.ThisApplication;
import com.x.processplatform.assemble.surface.WorkControlBuilder;
import com.x.processplatform.core.entity.content.Attachment;
import com.x.processplatform.core.entity.content.Work;

@Deprecated
class ActionUploadCallback extends BaseAction {
	ActionResult<Wo<WoObject>> execute(EffectivePerson effectivePerson, String workId, String callback, String site,
			String fileName, byte[] bytes, FormDataContentDisposition disposition) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo<WoObject>> result = new ActionResult<>();
			Business business = new Business(emc);
			/* 뒤에서 다시 저장해야 함 */
			Work work = emc.find(workId, Work.class);
			/** work 존재 여부 판단 */
			if (null == work) {
				throw new ExceptionWorkNotExistCallback(callback, workId);
			}
			/** 결재 대기 수를 집계해 사용자의 첨부파일 업로드 가능 여부 판단 */
			Control control = new WorkControlBuilder(effectivePerson, business, work).enableAllowProcessing().build();
			if (BooleanUtils.isNotTrue(control.getAllowProcessing())) {
				throw new ExceptionWorkAccessDeniedCallback(callback, effectivePerson.getDistinguishedName(),
						work.getTitle(), work.getId());
			}
			if (StringUtils.isEmpty(fileName)) {
				fileName = this.fileName(disposition);
			}
			/* 가능한 첨부파일 이름 조정 */
			fileName = this.adjustFileName(business, work.getJob(), fileName);
			/** 확장자 없는 파일 업로드 금지 */
			// if (StringUtils.isEmpty(fileName)) {
			// throw new ExceptionEmptyExtension(fileName);
			// }
			this.verifyConstraint(bytes.length, fileName, callback);

			StorageMapping mapping = ThisApplication.context().storageMappings().random(Attachment.class);
			Attachment attachment = this.concreteAttachment(work, effectivePerson, site);
			attachment.saveContent(mapping, bytes, fileName, Config.general().getStorageEncrypt());
			attachment.setType((new Tika()).detect(bytes, fileName));
			// emc.beginTransaction(Work.class);
			emc.beginTransaction(Attachment.class);
			emc.persist(attachment, CheckPersistType.all);
			// work.getAttachmentList().add(attachment.getId());
			emc.commit();
			WoObject woObject = new WoObject();
			woObject.setId(attachment.getId());
			Wo<WoObject> wo = new Wo<>(callback, woObject);
			result.setData(wo);
			return result;
		}
	}

	private Attachment concreteAttachment(Work work, EffectivePerson effectivePerson, String site) throws Exception {
		Attachment attachment = new Attachment();
		attachment.setCompleted(false);
		attachment.setPerson(effectivePerson.getDistinguishedName());
		attachment.setLastUpdatePerson(effectivePerson.getDistinguishedName());
		attachment.setSite(site);
		/** 디렉터리 값 판단용 */
		attachment.setWorkCreateTime(work.getCreateTime());
		attachment.setApplication(work.getApplication());
		attachment.setProcess(work.getProcess());
		attachment.setJob(work.getJob());
		attachment.setActivity(work.getActivity());
		attachment.setActivityName(work.getActivityName());
		attachment.setActivityToken(work.getActivityToken());
		attachment.setActivityType(work.getActivityType());
		return attachment;
	}

	public static class Wo<T> extends WoCallback<T> {
		public Wo(String callbackName, T t) {
			super(callbackName, t);
		}
	}

	public static class WoObject extends WoId {

		private static final long serialVersionUID = -6126855595194424970L;

	}

}
