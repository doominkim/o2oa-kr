package com.x.program.center.jaxrs.dingding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.jaxrs.dingding.encrypt.DingTalkEncryptor;
import com.x.program.center.jaxrs.dingding.encrypt.Utils;


/**
 * Created by fancyLou on 2020-10-26.
 * Copyright © 2020 O2. All rights reserved.
 */
public class ActionSyncOrganizationCallbackPost extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSyncOrganizationCallbackPost.class);

    private List<String> tags = new ArrayList<>(Arrays.asList("user_add_org", "user_modify_org", "user_leave_org", "user_active_org", "org_dept_create", "org_dept_modify", "org_dept_remove"));

    Map<String, String> execute(EffectivePerson effectivePerson, String signature, String timestamp, String nonce, JsonElement body) throws Exception {
        String params = "signature:" + signature + " timestamp:" + timestamp + " nonce:" + nonce + " body:" + body;
        logger.info(params);
        if (Config.dingding().getEnable()) {
            Wi wi = this.convertToWrapIn(body, Wi.class);
            DingTalkEncryptor dingTalkEncryptor = new DingTalkEncryptor(Config.dingding().getToken(), Config.dingding().getEncodingAesKey(), Config.dingding().getCorpId());
            String plainText = dingTalkEncryptor.getDecryptMsg(signature, timestamp, nonce, wi.getEncrypt());
            logger.info("복호화 결과:" + plainText);
            DingtalkEvent event = gson.fromJson(plainText, DingtalkEvent.class);
            if ("check_url".equals(event.getEventType())) { //콜백 주소 확인
                logger.info("url 확인, 처리 불필요");
            } else if (tags.contains(event.getEventType())) {
                logger.info("주소록 변경, 예약 작업 큐 메시지 추가.");
                ThisApplication.dingdingSyncOrganizationCallbackRequest.add(new Object());
            } else {
                logger.info("무시한 유형, {}", event.getEventType());
            }
            return dingTalkEncryptor.getEncryptedMap("success", System.currentTimeMillis(), Utils.getRandomStr(8));
        }else {
            throw new ExceptionNotPullSync();
        }

    }


    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("암호화 문자열")
        private String encrypt;

        public String getEncrypt() {
            return encrypt;
        }

        public void setEncrypt(String encrypt) {
            this.encrypt = encrypt;
        }
    }

    public static class DingtalkEvent extends GsonPropertyObject {
        @FieldDescribe("이벤트 유형")
        private String EventType;

        public String getEventType() {
            return EventType;
        }

        public void setEventType(String eventType) {
            EventType = eventType;
        }
    }
}
