package com.x.message.assemble.communicate.jaxrs.im;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.message.assemble.communicate.Business;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.core.entity.IMMsg;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class ActionDeleteMessageByConversation extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            ActionDeleteMessageByConversation.class);


    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement)
            throws Exception {
        if (!effectivePerson.isManager()) {
            throw new ExceptionConversationCheckError("권한이 없습니다. 관리자가 처리해야 합니다");
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getConversationId()) && StringUtils.isEmpty(wi.getBeforeDay())) {
            throw new ExceptionConversationCheckError("매개변수가 하나 이상 필요합니다");
        }
        if (StringUtils.isNotEmpty(wi.getBeforeDay())) {
            var date = DateTools.parseDate(wi.getBeforeDay());
            if (date == null) {
                throw new ExceptionConversationCheckError("날짜가 올바르지 않습니다!");
            }
            if (date.after(new Date())) {
                throw new ExceptionConversationCheckError("날짜가 올바르지 않습니다!");
            }
        }
        LOGGER.info("메시지 삭제 실행 시작 실행자:{} , 삭제 조건:{}", effectivePerson.getDistinguishedName(), wi.toString());
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        startThread(wi);
        wo.setMessage("삭제 작업을 시작했습니다. 데이터 양에 따라 소요 시간이 다르므로 로그를 확인하세요!");
        wo.setValue(true);
        result.setData(wo);
        return result;


    }

    private void startThread(Wi wi) {
        Thread thread = new Thread(() -> {
            try {
                deleteMessages(wi);
            } catch (Exception e) {
                LOGGER.info("메시지 삭제 작업 실패, {}", wi.toString());
                LOGGER.error(e);
            }
        });
        thread.start();
    }

    private void deleteMessages(Wi wi) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            Date beforeDate = null;
            if (StringUtils.isNotEmpty(wi.getBeforeDay())) {
                beforeDate = DateTools.parseDate(wi.getBeforeDay());
            }
            int count = 0;
            boolean hasMore = true;
            while (hasMore) {
                // 데이터를 삭제 중이므로 항상 첫 페이지만 조회
                List<IMMsg> msgList = business.imConversationFactory()
                        .listMsgWithConversation(1, 20, wi.getConversationId(), beforeDate);
                if (null != msgList && !msgList.isEmpty()) {
                    for (IMMsg imMsg : msgList) {
                        ThisApplication.imMessageDeleteQueue.send(imMsg);
                        count++;
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOGGER.error(e);
                }
                if (msgList == null || msgList.size() < 20) {
                    hasMore = false;
                }
            }
            LOGGER.info("  삭제한 메시지 수 " + count + " ！！！！！！！！！");
        }
    }



    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("대화 id")
        private String conversationId;

        @FieldDescribe("마감일(yyyy-MM-dd). 작업이 이 마감일 이전의 모든 메시지 데이터를 삭제합니다 .")
        private String beforeDay;

        public String getBeforeDay() {
            return beforeDay;
        }

        public void setBeforeDay(String beforeDay) {
            this.beforeDay = beforeDay;
        }

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
    }

    public static class Wo extends GsonPropertyObject {

        private Boolean value;

        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }
    }

}