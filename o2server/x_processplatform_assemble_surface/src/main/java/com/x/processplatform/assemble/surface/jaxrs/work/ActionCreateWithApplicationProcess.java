package com.x.processplatform.assemble.surface.jaxrs.work;

import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Process;
import com.x.processplatform.core.express.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcessWi;

import io.swagger.v3.oas.annotations.media.Schema;

/*
 * 앱 이름과 프로세스 이름으로 생성, process로 직접 생성하는 것과 거의 동일
 * */
class ActionCreateWithApplicationProcess extends BaseCreateAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateWithApplicationProcess.class);

    ActionResult<List<Wo>> execute(EffectivePerson effectivePerson, String applicationFlag, String processFlag,
            JsonElement jsonElement) throws Exception {
        LOGGER.debug("execute:{}, applicationFlag:{}, processFlag:{}.", effectivePerson::getDistinguishedName,
                () -> applicationFlag, () -> processFlag);
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
            Application application = business.application().pick(applicationFlag);
            if (null == application) {
                throw new ExceptionApplicationNotExist(applicationFlag);
            }
            process = business.process().pickProcessEditionEnabled(application, processFlag);
            if (null == process) {
                throw new ExceptionProcessNotExist(processFlag);
            }
            if (BooleanUtils.isNotTrue(wi.getAllowEdition()) && StringUtils.isNotEmpty(process.getEdition())
                    && BooleanUtils.isFalse(process.getEditionEnable())) {
                process = business.process().pickEnabled(process.getApplication(), process.getEdition());
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
                /* 이전에 생성한 초안을 직접 열지 판단, 초안 판단 기준:결재 대기는 있고 처리 완료는 없음 */
                workId = lastestWorkId = this.latest(business, process, identity);
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
        if(BooleanUtils.isTrue(wi.getSkipDraftCheck())){
            this.updateWorkDraftCheck(workId);
        }
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

    @Schema(description = "com.x.processplatform.assemble.surface.jaxrs.work.ActionCreateWithApplicationProcess$Wi")
    public static class Wi extends ActionCreateWithApplicationProcessWi {

        private static final long serialVersionUID = -6891996789972160214L;

    }

}
