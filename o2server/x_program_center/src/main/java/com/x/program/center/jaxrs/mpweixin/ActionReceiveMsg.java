package com.x.program.center.jaxrs.mpweixin;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.core.entity.MPWeixinMenu;

/**
 * 위챗 공식 계정 수신 메시지 처리
 * 당분간 처리하지 않음
 * Created by fancyLou on 3/8/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionReceiveMsg extends BaseAction {


    private static Logger logger = LoggerFactory.getLogger(ActionReceiveMsg.class);


    ActionResult<Wo> execute(String signature, Long timestamp, String nonce, String echostr, InputStream inputStream) throws Exception {
        logger.info("위챗 공식 계정 메시지 수신,signature:{}, timestamp:{}, nonce:{}, echostr:{}.", signature, timestamp, nonce, echostr);
        Wo wo = new Wo();
        ActionResult<Wo> actionResult = new ActionResult<>();

        if (inputStream != null) { // 메시지 파싱
            Map<String, String> map = xmlToMap(inputStream);
            String msgType = map.get("MsgType");

            if (WX_MSG_RECEIVE_TYPE_EVENT.equals(msgType)) { // 이벤트 발생
                String event = map.get("Event");
                String eventKey = map.get("EventKey");
                logger.info("이벤트 메시지 수신: event: {} , eventKey: {}", event, eventKey);
                //TODO 현재 메뉴 클릭 이벤트와 공식 계정 구독 이벤트만 처리, 다른 메시지 유형은 처리하지 않음
                if (StringUtils.isNotEmpty(event) && event.equalsIgnoreCase(WX_MSG_RECEIVE_EVENT_CLICK)) { //메뉴 클릭 이벤트
                    MPWeixinMenu menu = findMenuWithEventKey(eventKey);
                    if (menu != null) {
                        String content = menu.getContent();
                        String toUser = map.get("FromUserName");
                        String fromUser = map.get("ToUserName");
                        // 특수 메시지 처리. 기본은 텍스트 메시지 응답, 미디어 메시지면 특수 헤더 media:image: 뒤에 위챗 소재 mediaId 지정 필요
                        if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_IMAGE)) {
                            String mediaId = content.substring(WX_MSG_BACK_MEDIA_KEY_IMAGE.length());
                            String xml = imageMessageBack(toUser, fromUser, mediaId);
                            logger.info("메뉴 클릭 응답 메시지: {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_VOICE)) {
                            String mediaId = content.substring(WX_MSG_BACK_MEDIA_KEY_VOICE.length());
                            String xml = voiceMessageBack(toUser, fromUser, mediaId);
                            logger.info("메뉴 클릭 응답 메시지: {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_VIDEO)) {
                            // 동영상 메시지는 title 과 description 두 필드가 더 필요, |로 구분  예: mediaId|title|description
                            String videoContent = content.substring(WX_MSG_BACK_MEDIA_KEY_VIDEO.length());
                            String xml = null;
                            try {
                                String[] arr = videoContent.split("\\|");
                                xml = videoMessageBack(toUser, fromUser, arr[0], arr[1], arr[2]);
                            } catch (Exception e) {
                                logger.error(e);
                                xml = txtMessageBack(toUser, fromUser, content);
                            }
                            logger.info("메뉴 클릭 응답 메시지: {}", xml);
                            wo.setText(xml);
                        } else if (content.startsWith(WX_MSG_BACK_MEDIA_KEY_SCRIPT)) {
                            String scriptId = content.substring(WX_MSG_BACK_MEDIA_KEY_SCRIPT.length());
                            if (StringUtils.isNotBlank(scriptId)) {
                                ExecuteServiceScriptThread runner1 = new ExecuteServiceScriptThread(toUser, "", scriptId);
                                Thread thread1 = new Thread(runner1);
                                thread1.start();
                            }
                            //스크립트 실행됨, 메시지 응답 안 함
                            wo.setText("success");
                            actionResult.setData(wo);
                            return actionResult;
                        } else {
                            String xml = txtMessageBack(toUser, fromUser, content);
                            logger.info("메뉴 클릭 응답 메시지: {}", xml);
                            wo.setText(xml);
                        }
                        actionResult.setData(wo);
                        return actionResult;
                    } else {
                        logger.info("해당 eventKey를 찾지 못함: {}", eventKey);
                    }
                } else if (WX_MSG_RECEIVE_EVENT_SUBSCRIBE.equalsIgnoreCase(event)) { // 구독 이벤트
                    MPWeixinMenu menu = findMenuWithEventKey(WX_MSG_RECEIVE_EVENT_SUBSCRIBE);
                    if (menu != null) {
                        String content = menu.getContent();
                        String toUser = map.get("FromUserName");
                        String fromUser = map.get("ToUserName");
                        String xml = txtMessageBack(toUser, fromUser, content);
                        logger.info("공식 계정 팔로우 응답 메시지: {}", xml);
                        wo.setText(xml);
                        actionResult.setData(wo);
                        return actionResult;
                    } else {
                        logger.info("해당 eventKey를 찾지 못함: {}", eventKey);
                    }
                }
            } else if (WX_MSG_RECEIVE_TYPE_TEXT.equalsIgnoreCase(msgType)) { // 텍스트 메시지 수신
                String text = map.get("Content");
                String toUser = map.get("FromUserName");
                logger.info("텍스트 메시지 수신: text: {} ", text);
                //TODO 현재 서비스 스크립트 비동기 실행 지원, 여기서 즉시 반환해야 함(아니면 타임아웃), 스크립트에서 위챗 고객센터 메시지로 응답 가능. 스크립트 id는 임시로 설정 파일에 저장
                String id = Config.mpweixin().getScriptId();
                if (StringUtils.isNotBlank(id)) {
                    ExecuteServiceScriptThread runner1 = new ExecuteServiceScriptThread(toUser, text, id);
                    Thread thread1 = new Thread(runner1);
                    thread1.start();
                }
            } else {
                logger.info("처리하지 않는 메시지 유형, MsgType: {}", msgType);
            }
        } else {
            logger.info("메시지 내용을 가져오지 못했습니다. inputStream 이 비어 있습니다!");
        }
        wo.setText("success");//메시지 응답 안 함
        actionResult.setData(wo);
        return actionResult;
    }


    // 텍스트 메시지 응답
    private String txtMessageBack(String toUser, String from, String content) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[text]]></MsgType>" +
                "<Content><![CDATA[" + content + "]]></Content>" +
                "</xml>";
        return xml;
    }

    // 이미지 메시지 응답
    private String imageMessageBack(String toUser, String from, String mediaId) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[image]]></MsgType>" +
                "<Image><MediaId><![CDATA[" + mediaId + "]]></MediaId></Image>" +
                "</xml>";
        return xml;
    }

    // 음성 메시지 응답
    private String voiceMessageBack(String toUser, String from, String mediaId) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[voice]]></MsgType>" +
                "<Voice><MediaId><![CDATA[" + mediaId + "]]></MediaId></Voice>" +
                "</xml>";
        return xml;
    }

    //동영상 메시지 응답
    private String videoMessageBack(String toUser, String from, String mediaId, String title, String description) {
        long time = new Date().getTime();
        String xml = "<xml>" +
                "<ToUserName><![CDATA[" + toUser + "]]></ToUserName>" +
                "<FromUserName><![CDATA[" + from + "]]></FromUserName>" +
                "<CreateTime>" + time + "</CreateTime>" +
                "<MsgType><![CDATA[video]]></MsgType>" +
                "<Video>" +
                "<MediaId><![CDATA[" + mediaId + "]]></MediaId>" +
                "<Title><![CDATA[" + title + "]]></Title>" +
                "<Description><![CDATA[" + description + "]]></Description>" +
                "</Video>" +
                "</xml>";
        return xml;
    }

    private void asyncPostServiceScript(String text) {

    }


    /**
     * xml 읽기
     *
     * @param ins
     * @return
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(InputStream ins) throws Exception {
        Map<String, String> map = new HashMap<>();
        if (ins == null) {
            return map;
        }

        try {
            SAXReader reader = new SAXReader();
            // 1. DOCTYPE 금지
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            // 2. 엔티티 금지
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 3. ⭐ 외부 DTD 금지(취약점 수정의 핵심)
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            Document doc = reader.read(ins);

            Element root = doc.getRootElement();

            List<Element> list = root.elements();

            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
            ins.close();
            return map;
        } catch (Exception e) {
            logger.error(e);
            return map;
        }
    }

    public static class Wo extends WoText {
    }


    public static class ExecuteServiceScriptThread implements Runnable {
        String toUser;
        String text;
        String scriptId;

        ExecuteServiceScriptThread(String toUser, String text, String scriptId) {
            this.toUser = toUser;
            this.text = text;
            this.scriptId = scriptId;
        }

        @Override
        public void run() {
            evalRemote();
        }

        private void evalRemote() {
            try {
                if (StringUtils.isNotBlank(scriptId)) {
                    ScriptExecuteBody body = new ScriptExecuteBody();
                    body.setKeyword(text);
                    body.setOpenId(toUser);
                    ActionResponse result = CipherConnectionAction.post(false,
                            Config.url_x_program_center_jaxrs("invoke", scriptId, "execute"), body);
                    logger.info("스크립트 실행 결과: " + result.toJson());
                } else {
                    logger.warn("서비스 스크립트 id 미설정");
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    /**
     * 스크립트 실행 전달 객체
     */
    public static class ScriptExecuteBody extends GsonPropertyObject {
        // 위챗 공식 계정 사용자의 openId
        private String openId;
        private String keyword;

        public String getOpenId() {
            return openId;
        }

        public void setOpenId(String openId) {
            this.openId = openId;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }
    }
}
