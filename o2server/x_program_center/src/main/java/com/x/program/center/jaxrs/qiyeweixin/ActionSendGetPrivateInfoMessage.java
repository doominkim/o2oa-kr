package com.x.program.center.jaxrs.qiyeweixin;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.program.center.Business;

/**
 * Created by fancyLou on 2022/9/30.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionSendGetPrivateInfoMessage extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionSendGetPrivateInfoMessage.class);


    ActionResult<Wo> execute(JsonElement jsonElement)  throws Exception {
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (wi.getConsumerList() == null || wi.getConsumerList().isEmpty()) {
            throw new ExceptionNotEmpty("메시지 수신자");
        }
        if (StringUtils.isEmpty(wi.getMessageTitle())) {
            throw new ExceptionNotEmpty("메시지 제목");
        }
        if (StringUtils.isEmpty(wi.getMessageContent())) {
            throw new ExceptionNotEmpty("메시지 내용");
        }
        if (!Config.qiyeweixin().getEnable() || !Config.qiyeweixin().getMessageEnable()) {
            throw new ExceptionQywxNotEnable();
        }
        if (StringUtils.isEmpty(Config.qiyeweixin().getWorkUrl())) {
            throw new ExceptionQywxNoWorkUrl();
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            List<String> consumerPersonList = new ArrayList<>();
            Business business = new Business(emc);
            for (String e : wi.getConsumerList()) {
                if (e.endsWith( "@U" )) { // 조직
                    // 모든 사용자 목록 재귀 조회
                    List<String> persons = business.organization().person().listWithUnitSubNested( e );
                    if (persons != null && !persons.isEmpty()) {
                        consumerPersonList.addAll(persons);
                    }
                } else { // 사용자 직접 추가
                    consumerPersonList.add(e);
                }
            }
            if (!consumerPersonList.isEmpty()) {
                removeDuplicationByHashSet(consumerPersonList);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("메시지 발송 사용자 ,{} ", Arrays.toString(consumerPersonList.toArray()));
            }
            QiyeweixinTextCardMessage m = generateMessage(business, consumerPersonList, wi.getMessageTitle(), wi.getMessageContent());
            String address = Config.qiyeweixin().getApiAddress() + "/cgi-bin/message/send?access_token="
                    + Config.qiyeweixin().corpAccessToken();
            QiyeweixinMessageResp resp = HttpConnection.postAsObject(address, null, m.toString(),
                    QiyeweixinMessageResp.class);
            if (resp.getErrcode() != 0) {
                throw new ExceptionQiyeweixinMessage(resp.getErrcode(), resp.getErrmsg());
            }
            logger.info("권한 부여 메시지 발송 성공,{}.", resp.toString());
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setValue(true);
        return result;
    }

    private QiyeweixinTextCardMessage generateMessage(Business business, List<String> persons, String title, String content) throws Exception {
        QiyeweixinTextCardMessage cardMessage = new QiyeweixinTextCardMessage();
        cardMessage.setAgentid(Long.parseLong(Config.qiyeweixin().getAgentId(), 10));
        List<String> qywxIds = business.organization().person().listObject(persons).stream().map(Person::getQiyeweixinId).collect(Collectors.toList());
        cardMessage.setTouser(String.join("|", qywxIds));
        cardMessage.getTextcard().setTitle(title);
        cardMessage.getTextcard().setDescription(content);
        cardMessage.getTextcard().setUrl(messageOpenUrl());
        if (logger.isDebugEnabled()) {
            logger.debug("위챗 카드 메시지:{}", cardMessage::toString);
        }
        return cardMessage;
    }

    private String messageOpenUrl() throws Exception {
        String corpId = Config.qiyeweixin().getCorpId();
        String agentId = Config.qiyeweixin().getAgentId();
        String o2oaUrl = Config.qiyeweixin().getWorkUrl() + "qiyeweixinsso.html?mode=snsapi_privateinfo";
        if (StringUtils.isEmpty(o2oaUrl) || StringUtils.isEmpty(corpId) || StringUtils.isEmpty(agentId)) {
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("o2oa 주소:{}" , o2oaUrl);
        }
        o2oaUrl = URLEncoder.encode(o2oaUrl, DefaultCharset.name);
        if (logger.isDebugEnabled()) {
            logger.debug("encode url : {}", o2oaUrl);
        }
        String oauthUrl = Config.qiyeweixin().getOauth2Address();
        String url = oauthUrl + "/connect/oauth2/authorize?appid=" + corpId
                + "&response_type=code&scope=snsapi_privateinfo&agentid=" + agentId + "&redirect_uri=" + o2oaUrl
                + "&#wechat_redirect";
        if (logger.isDebugEnabled()) {
            logger.debug("final url : {}" , url);
        }
        return url;
    }

    /**HashSet으로 List 중복 제거(순서 없음)
     *
     * @param list
     * */
    private List<String> removeDuplicationByHashSet(List<String> list) {
        HashSet<String> set = new HashSet<>(list);
        //List 컬렉션의 모든 요소 비우기
        list.clear();
        //HashSet 객체를 List 컬렉션에 추가
        list.addAll(set);
        return list;
    }



    public static class Wo extends WrapBoolean {

    }
    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("메시지 수신자.")
        private List<String> consumerList;
        @FieldDescribe("메시지 제목.")
        private String messageTitle;
        @FieldDescribe("메시지 내용.")
        private String messageContent;

        public String getMessageTitle() {
            return messageTitle;
        }

        public void setMessageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
        }

        public String getMessageContent() {
            return messageContent;
        }

        public void setMessageContent(String messageContent) {
            this.messageContent = messageContent;
        }

        public List<String> getConsumerList() {
            return consumerList;
        }

        public void setConsumerList(List<String> consumerList) {
            this.consumerList = consumerList;
        }
    }


    /**
     * 텍스트 카드 메시지
     * 열 수 있는 업무 메시지 발송용
     */
    public static class QiyeweixinTextCardMessage extends QiyeweixinBaseMessage {


        private static final long serialVersionUID = -7805292712056929523L;

        /**
         * {
         *    "touser" : "UserID1|UserID2|UserID3",
         *    "toparty" : "PartyID1 | PartyID2",
         *    "totag" : "TagID1 | TagID2",
         *    "msgtype" : "textcard",
         *    "agentid" : 1,
         *    "textcard" : {
         *             "title" : "경품 수령 안내",
         *             "description" : "<div class=\"gray\">2016년 9월 26일</div> <div class=\"normal\">iPhone 7 1대에 당첨되신 것을 축하합니다. 수령 코드: xxxx</div><div class=\"highlight\">2016년 10월 10일 전까지 총무 담당자에게 연락하여 수령하세요</div>",
         *             "url" : "URL",
         *                         "btntxt":"더 보기"
         *    },
         *    "enable_id_trans": 0,
         *    "enable_duplicate_check": 0,
         *    "duplicate_check_interval": 1800
         * }
         */

        public QiyeweixinTextCardMessage() {
            this.setMsgtype("textcard");
        }


        private TextCard textcard = new TextCard();
        private int enable_id_trans = 0;
        private int enable_duplicate_check = 0;
        private int duplicate_check_interval = 0;


        public TextCard getTextcard() {
            return textcard;
        }

        public void setTextcard(TextCard textcard) {
            this.textcard = textcard;
        }

        public int getEnable_id_trans() {
            return enable_id_trans;
        }

        public void setEnable_id_trans(int enable_id_trans) {
            this.enable_id_trans = enable_id_trans;
        }

        public int getEnable_duplicate_check() {
            return enable_duplicate_check;
        }

        public void setEnable_duplicate_check(int enable_duplicate_check) {
            this.enable_duplicate_check = enable_duplicate_check;
        }

        public int getDuplicate_check_interval() {
            return duplicate_check_interval;
        }

        public void setDuplicate_check_interval(int duplicate_check_interval) {
            this.duplicate_check_interval = duplicate_check_interval;
        }






        public static class TextCard {

            private String title = "";
            private String description = "";
            private String url = "";
            private String btntxt = "상세";

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getBtntxt() {
                return btntxt;
            }

            public void setBtntxt(String btntxt) {
                this.btntxt = btntxt;
            }
        }

    }


    public static class QiyeweixinBaseMessage extends GsonPropertyObject {


        private static final long serialVersionUID = 6612140970748517772L;
        private String touser = "";
        private String toparty = "";
        private String totag = "";
        private Long agentid = 0L;
        private String msgtype;


        public String getMsgtype() {
            return msgtype;
        }

        public void setMsgtype(String msgtype) {
            this.msgtype = msgtype;
        }

        public String getTouser() {
            return touser;
        }

        public void setTouser(String touser) {
            this.touser = touser;
        }

        public String getToparty() {
            return toparty;
        }

        public void setToparty(String toparty) {
            this.toparty = toparty;
        }

        public String getTotag() {
            return totag;
        }

        public void setTotag(String totag) {
            this.totag = totag;
        }

        public Long getAgentid() {
            return agentid;
        }

        public void setAgentid(Long agentid) {
            this.agentid = agentid;
        }
    }



    public static class QiyeweixinMessageResp extends GsonPropertyObject{

        /**
         * <code>	 {
         * "errcode" : 0,
         * "errmsg" : "ok",
         * "invaliduser" : "userid1|userid2", // 대소문자 구분 없음, 반환 목록은 모두 소문자로 변환
         * "invalidparty" : "partyid1|partyid2",
         * "invalidtag":"tagid1|tagid2"
         * }
         * </code>
         */

        private Integer errcode;
        private String errmsg;
        private String invaliduser;
        private String invalidparty;
        private String invalidtag;

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public String getInvaliduser() {
            return invaliduser;
        }

        public void setInvaliduser(String invaliduser) {
            this.invaliduser = invaliduser;
        }

        public String getInvalidparty() {
            return invalidparty;
        }

        public void setInvalidparty(String invalidparty) {
            this.invalidparty = invalidparty;
        }

        public String getInvalidtag() {
            return invalidtag;
        }

        public void setInvalidtag(String invalidtag) {
            this.invalidtag = invalidtag;
        }

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

    }
}




