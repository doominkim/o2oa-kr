package com.x.organization.assemble.authentication.jaxrs.mpweixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.x.base.core.project.config.Mpweixin;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin.MPweixinMessageTemp;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

/**
 * 템플릿 메시지 발송 테스트
 * Created by fancyLou on 3/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionTestSendTempMessage extends BaseAction  {

    private static Logger logger = LoggerFactory.getLogger(ActionTestSendTempMessage.class);

    ActionResult<Wo> execute(String personid, JsonElement jsonElement) throws Exception {
        logger.info("테스트 메시지 발송, person:"+personid);
        ActionResult<Wo> result = new ActionResult<Wo>();
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            // openid로 사용자 조회
            String personId = business.person().getWithCredential(personid);
            if (StringUtils.isEmpty(personId)) {
                throw new ExceptionPersonNotExist();
            }
            Person person = emc.find(personId, Person.class);
            String openid = person.getMpwxopenId();
            if (StringUtils.isEmpty(openid)) {
                throw new ExceptionPersonNotExist();
            }
            logger.info(openid);
            Boolean enable = Config.mpweixin().getMessageEnable();
            if (BooleanUtils.isFalse(enable)) {
                throw new ExceptionConfigError();
            }
            String tempId = Config.mpweixin().getTempMessageId();
            if (StringUtils.isEmpty(tempId)) {
                throw new ExceptionConfigError();
            }
            List<MPweixinMessageTemp> list = Config.mpweixin().getFieldList();
            logger.info("field list size: " +list.size());
            JsonObject object = jsonElement.getAsJsonObject();
            logger.info(object.toString());

            Map<String, WeixinTempMessageFieldObj> data = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                MPweixinMessageTemp filed = list.get(i);
                String name = filed.getName();
                String tempName = filed.getTempName();
                String value = object.get(name).getAsString();
                logger.info("파싱 결과 name:"+name + " value:"+value + "  tempName에 넣음:"+tempName);
                WeixinTempMessageFieldObj obj = new WeixinTempMessageFieldObj();
                obj.setValue(value);
                obj.setColor("#173177");
                data.put(tempName, obj);
            }

            WeixinTempMessage message = new WeixinTempMessage();
            message.setTouser(openid);
            message.setUrl("여기에 URL 입력");
            message.setTemplate_id(tempId);
            message.setTopcolor("#fb4747");
            message.setData(data);
            logger.info("발송할 메시지 객체:" + message.toString());
            String url = Mpweixin.default_apiAddress + "/cgi-bin/message/template/send?access_token="+ Config.mpweixin().accessToken();
            logger.info("send url:" +url);
            String response = HttpConnection.postAsString(url, null, message.toString());
            logger.info("반환:"+response);
        }

        Wo wo = new Wo();
        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    public static class WeixinTempMessage extends GsonPropertyObject {
        private String touser;
        private String template_id;
        private String url;
        private String topcolor;
        private Map<String, WeixinTempMessageFieldObj> data;

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getTemplate_id() {
            return template_id;
        }

        public void setTemplate_id(String template_id) {
            this.template_id = template_id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTopcolor() {
            return topcolor;
        }

        public void setTopcolor(String topcolor) {
            this.topcolor = topcolor;
        }

        public Map<String, WeixinTempMessageFieldObj> getData() {
            return data;
        }

        public void setData(Map<String, WeixinTempMessageFieldObj> data) {
            this.data = data;
        }
    }

    public static class WeixinTempMessageFieldObj extends GsonPropertyObject {
        private String value;
        private String color;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    public static class Wo extends WrapBoolean {

    }
}
