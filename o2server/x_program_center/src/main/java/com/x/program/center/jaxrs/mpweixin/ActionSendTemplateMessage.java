package com.x.program.center.jaxrs.mpweixin;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.organization.core.entity.Person;
import com.x.program.center.Business;
import java.net.URLEncoder;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by fancyLou on 2024/4/9.
 * Copyright © 2024 O2. All rights reserved.
 */
public class ActionSendTemplateMessage extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionSendTemplateMessage.class);


    ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        logger.info(gson.toJson(jsonElement));
        Wo wo = new Wo();
        if (!Config.mpweixin().getEnable() || !Config.mpweixin().getMessageEnable()) {
            logger.warn("위챗 공식 계정 메시지 기능이 활성화되지 않았습니다!");
            throw new ExceptionConfigError();
        }
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getReceiver())) {
            throw new ExceptionNotEmpty("receiver");
        }
        if (StringUtils.isEmpty(wi.getTempId())) {
            throw new ExceptionNotEmpty("tempId");
        }
        if (StringUtils.isEmpty(wi.getMsgUrl())) {
            throw new ExceptionNotEmpty("msgUrl");
        }
        if (wi.getFieldMap() == null || wi.getFieldMap().isEmpty()) {
            throw new ExceptionNotEmpty("fieldMap");
        }
        Person person = getPerson(wi.getReceiver());
        if (person == null) {
            logger.warn("사용자를 찾을 수 없습니다.");
            throw new ExceptionNotExist();
        }
        String openId = person.getMpwxopenId();
        if (StringUtils.isEmpty(openId)) {
            logger.warn("사용자 {} 에 위챗 openid 가 바인딩되지 않았습니다 ", person.getName());
            wo.setValue(false);
            result.setData(wo);
            return result;
        }
        String workUrl = getOpenWorkUrl(wi.getMsgUrl());
        if (StringUtils.isEmpty(workUrl)) {
            logger.warn("잘못된 열기 주소 ", wi.getMsgUrl());
            throw new ExceptionNotEmpty("workUrl 매개변수가 설정되어 있지 않습니다");
        }
        WeixinTempMessage wxMessage = new WeixinTempMessage();
        wxMessage.setTouser(openId);
        wxMessage.setUrl(workUrl);
        wxMessage.setTemplate_id(wi.getTempId());
        wxMessage.setData(wi.getFieldMap());
        logger.debug("발송한 메시지 객체:{}.", wxMessage::toString);
        String url = Mpweixin.default_apiAddress + "/cgi-bin/message/template/send?access_token="
                + Config.mpweixin().accessToken();
        WeixinResponse response = HttpConnection.postAsObject(url, null, wxMessage.toString(),
                WeixinResponse.class);
        logger.debug("반환:{}.", response);
        if (response.getErrcode() != 0) {
            ExceptionQiyeweixinMessage e = new ExceptionQiyeweixinMessage(response.getErrcode(), response.getErrmsg());
            logger.error(e);
            throw e;
        }

        wo.setValue(true);
        result.setData(wo);
        return result;
    }

    private Person getPerson(String credential) throws Exception {
        Person person;
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            // 조회 사용자
            person = business.person().getWithCredential(credential);
        }
        return person;
    }

    private String getOpenWorkUrl(String url) {
        try {
            String workUrl = url;
            String o2oaUrl = Config.mpweixin().getWorkUrl();
            if (StringUtils.isEmpty(o2oaUrl)) {
                logger.warn( "workUrl 파라미터를 가져오지 못해 불가");
                return null;
            }
            workUrl = URLEncoder.encode(workUrl, DefaultCharset.name);
            o2oaUrl = o2oaUrl + "mpweixinsso.html?redirect=" + workUrl + "&type=login";
            o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
            String appId = Config.mpweixin().getAppid();
            String weixinUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri="
                    + o2oaUrl + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
            if (logger.isDebugEnabled()) {
                logger.debug("final url:{}.", weixinUrl);
            }
            return weixinUrl;
        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }


    public static class Wo extends WrapBoolean {

    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("수신자, 사용자 DN")
        private String receiver;

        @FieldDescribe("템플릿 메시지 id")
        private String tempId;

        @FieldDescribe("템플릿 필드 채움 내용, key는 템플릿 필드 이름, value는 해당 필드에 채울 내용")
        private Map<String, WeixinTempMessageFieldObj> fieldMap;

        @FieldDescribe("메시지 클릭 시 여는 O2OA 주소, x_desktop/ 뒤의 주소.")
        private String msgUrl;

        public String getMsgUrl() {
            return msgUrl;
        }

        public void setMsgUrl(String msgUrl) {
            this.msgUrl = msgUrl;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public String getTempId() {
            return tempId;
        }

        public void setTempId(String tempId) {
            this.tempId = tempId;
        }

        public Map<String, WeixinTempMessageFieldObj> getFieldMap() {
            return fieldMap;
        }

        public void setFieldMap(Map<String, WeixinTempMessageFieldObj> fieldMap) {
            this.fieldMap = fieldMap;
        }
    }


    public static class WeixinTempMessageFieldObj extends GsonPropertyObject {


        @FieldDescribe("템플릿 필드 채움 내용")
        private String value;
        @FieldDescribe("템플릿 필드 글자 색, 예: #173177")
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


    /**
     * 위챗 템플릿 메시지 발송 객체
     */
    public class WeixinTempMessage extends GsonPropertyObject {

        private String touser;
        private String template_id;
        private String url;
        private String topcolor;
        private Map<String, WeixinTempMessageFieldObj> data; // 템플릿 필드 데이터

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


    public static class WeixinResponse {
        private Integer errcode;
        private String errmsg;
        private Long msgid;

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public Long getMsgid() {
            return msgid;
        }

        public void setMsgid(Long msgid) {
            this.msgid = msgid;
        }
    }



}
