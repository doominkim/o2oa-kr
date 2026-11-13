package com.x.message.assemble.communicate.jaxrs.im;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.Application;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.tuple.Pair;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.message.MessageConnector;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.message.assemble.communicate.ThisApplication;
import com.x.message.assemble.communicate.jaxrs.im.ActionImConfig.Wo;
import com.x.message.core.entity.IMConversation;
import com.x.message.core.entity.IMMsg;
import com.x.message.core.entity.Message;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

abstract class BaseAction extends StandardJaxrsAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAction.class);

    public static final String IM_CONFIG_KEY_NAME = "imConfig"; // 이 설정은 객체로 web.json에 기록되며 imConfig를 key 이름으로 사용

    public static final String ICON_UNKOWN = "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";

    protected static final int CONVERSATION_ICON_SIZE = 155;

    /**
     * 목록 일부를 무작위로 조회
     *
     * @param originalList 원본 목록
     * @param cutSize      잘라낼 목록 크기
     * @return 잘라낸 목록
     */
    private <T> List<T> randomCutList(List<T> originalList, int cutSize) {
        // 원본 목록 수정을 피하기 위해 새 목록 생성
        List<T> copyList = new ArrayList<>(originalList);
        // 목록 순서 섞기
        Collections.shuffle(copyList);
        return copyList.subList(0, Math.min(cutSize, copyList.size()));
    }

    /**
     * 그룹 채팅 9칸 합성 프로필 이미지 생성
     *
     * @param conversationId 대화 id
     */
    protected void generateConversationIcon(String conversationId) {
        Thread thread = new Thread(() -> {
            try {
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance()
                        .create()) {
                    IMConversation conversation = emc.find(conversationId, IMConversation.class);
                    if (conversation == null) {
                        throw new ExceptionConversationNotExist();
                    }
                    if (!IMConversation.CONVERSATION_TYPE_GROUP.equals(conversation.getType())) {
                        return;
                    }
                    if (conversation.getPersonList() == null || conversation.getPersonList()
                            .isEmpty() || conversation.getPersonList().size() < 3) {
                        return;
                    }
                    Application app = ThisApplication.context().applications()
                            .randomWithWeight(x_organization_assemble_control.class.getName());
                    List<String> personListForIcon = randomCutList(conversation.getPersonList(), 9);
                    List<String> personIconList = new ArrayList<>(personListForIcon.size());
                    for (String personDN : personListForIcon) {
                        try {
                            byte[] personIcon = ThisApplication.context().applications()
                                    .getQueryBinary(false, app, "person/" + URLEncoder.encode(personDN,
                                            DefaultCharset.name) + "/icon");
                            if (personIcon == null) {
                                continue;
                            }
                            var base64 = Base64.getEncoder()
                                    .encodeToString(personIcon);
                            personIconList.add(base64);
                        } catch (Exception e) {
                            LOGGER.error(e);
                        }
                    }
                    emc.beginTransaction(IMConversation.class);
                    conversation.setGroupIcon(defaultConvsersationIcon(personIconList));
                    conversation.setUpdateTime(new Date());
                    emc.check(conversation, CheckPersistType.all);
                    emc.commit();
                    CacheManager.notify(IMConversation.class);
                }
            } catch (Exception e) {
                LOGGER.error( e);
            }
        });
        thread.start();
    }


    /**
     * 기본 대화 아이콘. 여러 프로필 이미지 아이콘을 하나로 합침
     *
     * @param base64IconList 목록 수 3-9개, 3, 9 포함
     * @return 합친 프로필 이미지 base64
     */
    private String defaultConvsersationIcon(List<String> base64IconList) {
        if (base64IconList == null || base64IconList.size() < 3 || base64IconList.size() > 9) {
            LOGGER.info("base64IconList 수가 올바르지 않습니다. 3-9 사이여야 하며 3, 9 포함");
            return null;
        }
        int numImages = base64IconList.size();
        int imageSize = 45; // 합친 프로필 이미지 크기
        int cols = 3; // 열 수
        if (numImages == 3 || numImages == 4) { // 3, 4개 프로필 이미지일 때는 두 행뿐이므로 프로필 이미지 크기와 열 수가 다름
            imageSize = 70;
            cols = 2;
        }
        BufferedImage mergedImage = new BufferedImage(CONVERSATION_ICON_SIZE,
                CONVERSATION_ICON_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = mergedImage.createGraphics();
        // 배경색 설정
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CONVERSATION_ICON_SIZE, CONVERSATION_ICON_SIZE);
        try {
            for (int i = 0; i < numImages; i++) {
                String base64Icon = base64IconList.get(i);
                BufferedImage image = ImageIO.read(
                        new ByteArrayInputStream(Base64.getDecoder().decode(base64Icon)));
                Image scaledImage =
                        image.getScaledInstance(imageSize, imageSize, Image.SCALE_SMOOTH);
                var pair = getImagePosition(i, numImages, imageSize, cols);
                g2d.drawImage(scaledImage, pair.first(), pair.second(), null);
            }
        } catch (IOException e) {
            LOGGER.error(e);
        }
        g2d.dispose();
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(mergedImage, "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            LOGGER.error(e);
        }
        LOGGER.info("이미지 합치기 오류?");
        return null;
    }

    // 합친 이미지에서 각 프로필 이미지의 위치 계산
    private Pair<Integer, Integer> getImagePosition(int i, int numImages, int imageSize, int cols) {
        int gap = 5; // 이미지 간 간격 및 바깥 여백
        // topY는 5, ,6개일 때 전체 세로 가운데 정렬에 사용
        int topY = 0;
        if (numImages == 5 || numImages == 6) {
            topY = imageSize / 2;
        }
        // 열과 행을 계산하고 그 위치로 x y 계산
        int row = i / cols;
        int col = i % cols;
        int x = col * (imageSize + gap) + gap;
        int y = row * (imageSize + gap) + gap + topY;
        // 특수 처리: 3개일 때 두 번째 행 가로 가운데 정렬
        if (numImages == 3 && i == 2) {
            x = (155 - imageSize) / 2;
            y = imageSize + gap + gap; // Second row position
        } else if (numImages == 5 && i >= 3) {
            // 특수 처리: 이미지 5개일 때 네 번째, 다섯 번째 전체 가로 가운데 정렬
            x = (155 - 2 * imageSize - gap) / 2 + col * (imageSize + gap);
            y = imageSize + gap + gap + topY; // Second row position
        } else if (numImages == 7 && i == 6) {
            // 특수 처리: 이미지 7개일 때 7번째 전체 가로 가운데 정렬
            x = (155 - imageSize) / 2 + col * (imageSize + gap);
            y = (imageSize + gap) * 2 + gap;
        } else if (numImages == 8 && i >= 6) {
            // 특수 처리: 이미지 7개일 때 7, 8번째 전체 가로 가운데 정렬
            x = (155 - 2 * imageSize - gap) / 2 + col * (imageSize + gap);
            y = (imageSize + gap) * 2 + gap;
        }
        return Pair.of(x, y);
    }


    protected ConversationInvokeValue checkConversationInvoke(EffectivePerson effectivePerson,
            String operateType,
            String type, List<String> newMembers, List<String> oldMembers, String newTitle,
            String newNote) throws Exception {
        // 검사 스크립트 설정 여부
        ActionResult<Wo> config = new ActionImConfig().execute(effectivePerson);
        String script = config.getData().getConversationCheckInvoke();
        if (script != null) {
            script = script.trim();
        }
        ConversationInvokeValue value = new ConversationInvokeValue();
        value.setResult(true);
        if (StringUtils.isNotEmpty(script)) {
            LOGGER.info("스크립트 검증 실행 {}", script);
            ConversationInvokeWi wi = new ConversationInvokeWi();
            wi.setOperator(effectivePerson.getDistinguishedName());
            wi.setOperateType(operateType);
            wi.setType(type);
            if ("create".equals(operateType)) {
                wi.setAddMembers(newMembers);
            } else if ("update".equals(operateType)) {
                // 추가 구성원 계산 (newMembers - oldMembers)
                List<String> addedMembers = new ArrayList<>(newMembers);
                addedMembers.removeAll(oldMembers);
                wi.setAddMembers(addedMembers);
                // 삭제 구성원 계산 (oldMembers - newMembers)
                List<String> removedMembers = new ArrayList<>(oldMembers);
                removedMembers.removeAll(newMembers);
                wi.setRemoveMembers(removedMembers);
                wi.setTitle(newTitle);
                wi.setNote(newNote);
            }
            ActionResponse response = CipherConnectionAction.post(false, 4000, 8000,
                    Config.url_x_program_center_jaxrs("invoke", script, "execute"), wi);
            ConversationInvokeWo result = response.getData(ConversationInvokeWo.class);
            value.setResult(result == null || result.value == null || !BooleanUtils.isFalse(
                    result.value.result));
            String msg = "";
            if (result != null && result.value != null) {
                msg = result.value.msg;
            }
            value.setMsg(msg);
        }
        return value;
    }

    // 대화 메시지 발송
    public void sendConversationMsg(List<String> persons, IMConversation conversation,
            String messageType) {
        for (String person : persons) {
            String title = "대화 메시지";
            MessageConnector.send(messageType, title, person, conversation);
        }
    }

    // 채팅 메시지 발송
    public void sendWsMessage(IMConversation conversation, IMMsg msg, String messageType,
            EffectivePerson effectivePerson) {
        // 메시지 발송
        List<String> persons = conversation.getPersonList();
        // 원래 자신을 제외했으나 지금은 제외하지 않음. 다중 단말 조작 가능성이 있어 다중 단말 메시지 동기화 가능
//        persons.removeIf(s -> (effectivePerson.getDistinguishedName().equals(s)));
        String name = "";
        try {
            name = effectivePerson.getDistinguishedName().substring(0,
                    effectivePerson.getDistinguishedName().indexOf("@"));
        } catch (Exception e) {
            LOGGER.error(e);
        }
        for (String person : persons) {
            LOGGER.info("im 메시지 발송, person: " + person + " messageType: " + messageType);
            String title = name + " 님이 보낸 메시지가 있습니다";
            if (person.equals(effectivePerson.getDistinguishedName())) {
                title = "새 메시지가 있습니다";
            }
            Message message = new Message();
//            String body = imMessageBody(msg);
            message.setTitle(title);
            message.setPerson(person);
            message.setType(messageType);
            message.setId("");
            message.setBody(msg.toString());
            try {
                // websocket 메시지 발송
                ThisApplication.wsConsumeQueue.send(message);
            } catch (Exception e) {
                LOGGER.error(e);
            }
            // 채팅 메시지 발송 시 온라인이 아니면 app 푸시 메시지 발송
            if (!person.equals(effectivePerson.getDistinguishedName())
                && MessageConnector.TYPE_IM_CREATE.equals(messageType)) {
                try {
                    if (!ThisApplication.wsClients().containsValue(person)) {
                        LOGGER.info("app으로 im 메시지 푸시, person: " + person);

                        if (BooleanUtils.isTrue(Config.pushConfig().getEnable())) {
                            ThisApplication.pmsinnerConsumeQueue.send(message);
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error(e);
                }
            }
        }
    }


    private String imMessageBody(IMMsg msg) {
        String json = msg.getBody();
        ActionMsgCreate.IMMessageBody body = gson.fromJson(json,
                ActionMsgCreate.IMMessageBody.class);
        if ("text".equals(body.getType())) {
            return body.getBody();
        } else if ("emoji".equals(body.getType())) {
            return "[이모티콘]";
        } else if ("image".equals(body.getType())) {
            return "[이미지]";
        } else if ("audio".equals(body.getType())) {
            return "[음성]";
        } else if ("location".equals(body.getType())) {
            return "[위치]";
        } else if ("file".equals(body.getType())) {
            return "[파일]";
        } else if ("process".equals(body.getType())) {
            return "[결재]";
        } else if ("cms".equals(body.getType())) {
            return "[게시글]";
        } else {
            return "[기타]";
        }
    }


    public static class ConversationInvokeWo extends GsonPropertyObject {

        @FieldDescribe("invoke 반환 결과.")
        private ConversationInvokeValue value;

        public ConversationInvokeValue getValue() {
            return value;
        }

        public void setValue(
                ConversationInvokeValue value) {
            this.value = value;
        }
    }

    public static class ConversationInvokeValue extends GsonPropertyObject {

        @FieldDescribe("반환 결과.")
        private Boolean result;
        @FieldDescribe("메시지.")
        private String msg;

        public Boolean getResult() {
            return result;
        }

        public void setResult(Boolean result) {
            this.result = result;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public static class ConversationInvokeWi extends GsonPropertyObject {

        @FieldDescribe("현재 처리자.")
        private String operator;
        @FieldDescribe("작업 유형, create|update.")
        private String operateType;

        @FieldDescribe("대화 유형, single|group.")
        private String type;
        @FieldDescribe("추가 사용자 목록.")
        private List<String> addMembers;
        @FieldDescribe("삭제 사용자 목록.")
        private List<String> removeMembers;
        @FieldDescribe("그룹 채팅 제목.")
        private String title;
        @FieldDescribe("그룹 채팅 공지.")
        private String note;

        public String getOperateType() {
            return operateType;
        }

        public void setOperateType(String operateType) {
            this.operateType = operateType;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getAddMembers() {
            return addMembers;
        }

        public void setAddMembers(List<String> addMembers) {
            this.addMembers = addMembers;
        }

        public List<String> getRemoveMembers() {
            return removeMembers;
        }

        public void setRemoveMembers(List<String> removeMembers) {
            this.removeMembers = removeMembers;
        }
    }
}

