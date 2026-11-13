package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Process;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated(forRemoval = true)
class ActionCreateForce extends BaseCreateAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateForce.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String processFlag, JsonElement jsonElement)
			throws Exception {
		LOGGER.debug("execute:{}, processFlag:{}.", effectivePerson::getDistinguishedName, () -> processFlag);
		// 새 업무 문서 id
		String workId = "";
		// 기존 초안 id
		String lastestWorkId = "";
		String identity = "";
		Process process = null;
		ActionResult<List<Wo>> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			identity = this.decideCreatorIdentity(business, effectivePerson, wi.getIdentity());
			process = business.process().pick(processFlag);
			if (null == process) {
				throw new ExceptionProcessNotExist(processFlag);
			}
			List<String> identities = List.of(identity);
			List<String> units = business.organization().unit().listWithIdentitySupNested(identity);
			List<String> groups = business.organization().group().listWithIdentity(identities);
			String person = business.organization().person().getWithIdentity(identity);
			List<String> roles = business.organization().role().listWithPerson(person);
			if (!business.process().startable(effectivePerson, identities, units, groups, roles, process)) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			if (BooleanUtils.isTrue(wi.getLatest())) {
				// 이전에 생성한 초안을 직접 열지 판단, 초안 판단 기준:결재 대기는 있고 처리 완료는 없음
				workId = lastestWorkId = this.latest(business, process, identity);
			}
		}
		if (StringUtils.isEmpty(workId)) {
			workId = this.createWork(process.getId(), wi.getData());
		}
		// Work 정보 설정
		if (BooleanUtils.isFalse(wi.getLatest()) || (StringUtils.isEmpty(lastestWorkId))) {
			updateWork(identity, workId, wi.getTitle(), wi.getParentWork());
			// 업무 문서 구동, 비큐 방식 사용
			this.processingCreateWork(workId);
		} else {
			// 초안이면 이후 직접 열기 준비
			workId = lastestWorkId;
		}
		List<Wo> wos = assemble(effectivePerson, workId);
		result.setData(wos);
		return result;
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateForce$Wi")
	public class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -7304888738391800411L;

		@FieldDescribe("지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
		@Schema(description = "지정 사용자의 기존 초안을 직접 열기, 초안 판단:업무 문서에 처리 완료가 없고 이 사용자의 결재 대기만 하나 있음.")
		private Boolean latest;

		@FieldDescribe("제목.")
		@Schema(description = "제목.")
		private String title;

		@FieldDescribe("시작 사용자 신분.")
		@Schema(description = "시작 사용자 신분.")
		private String identity;

		@FieldDescribe("업무 문서 데이터.")
		@Schema(description = "업무 문서 데이터.")
		private JsonElement data;

		@FieldDescribe("상위 업무 문서 식별자.")
		@Schema(description = "상위 업무 문서 식별자.")
		private String parentWork;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIdentity() {
			return identity;
		}

		public void setIdentity(String identity) {
			this.identity = identity;
		}

		public JsonElement getData() {
			return data;
		}

		public void setData(JsonElement data) {
			this.data = data;
		}

		public Boolean getLatest() {
			return latest;
		}

		public void setLatest(Boolean latest) {
			this.latest = latest;
		}

		public String getParentWork() {
			return parentWork;
		}

		public void setParentWork(String parentWork) {
			this.parentWork = parentWork;
		}

	}

}
