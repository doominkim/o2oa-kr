package com.x.ai.assemble.control.jaxrs.chat;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.ai.assemble.control.Business;
import com.x.ai.assemble.control.ThisApplication;
import com.x.ai.assemble.control.bean.AiConfig;
import com.x.ai.assemble.control.bean.ChartWi;
import com.x.ai.core.entity.AiModel;
import com.x.ai.core.entity.Clue;
import com.x.ai.core.entity.Completion;
import com.x.ai.core.entity.Completion_;
import com.x.ai.core.entity.File;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject_;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * @author sword
 */
public class ActionChat extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionChat.class);
    private static final String CHART_URL = "/ai-gateway-completion/generate";
    private static final String END_SSE = "[DONE]";
    private static final String EVENT_NAME_MESSAGE = "message";
    private static final String CHAT_SYSTEM_MESSAGE =
            "당신은 전문적이고 친절한 비서이며, 명확하고 체계적으로 질문에 답합니다. 현재 대화 중인 사용자는 %s입니다. 친절하게 대화하세요. 특별한 요청이 없으면 한국어로 답하세요."
                    + "어떤 질문에 답하든 항상 Markdown 형식으로 답변을 구성하고 다음 규칙을 따르세요: "
                    + "1. 제목(## 또는 ###)으로 주요 내용이나 단락을 구성합니다. "
                    + "2. 글머리 기호(- 또는 *)나 번호(1. 2. 3.)로 목록을 나열합니다. "
                    + "3. 굵게로 강조하고, 기울임으로 보조 강조합니다. "
                    + "4. 코드나 기술 내용은 ```로 감쌉니다. "
                    + "5. 인용이 필요하면 > 기호를 사용합니다. "
                    + "6. 단락 사이에 빈 줄을 두어 깔끔하게 정리합니다. "
                    + "7. 표가 필요하면 Markdown 표 형식을 사용합니다. "
                    + "목표는 간결하고 구조화된 Markdown 형식으로 정확하고 유용한 답변을 제공하는 것입니다. 사용자가 다른 형식을 명시적으로 요청하면 그 요청을 우선합니다.";

    void execute(EffectivePerson effectivePerson, Sse sse, SseEventSink eventSink,
            JsonElement jsonElement) {
        ActionResult<String> actionResult = new ActionResult<>();
        actionResult.setType(ActionResult.Type.error);
        try (eventSink) {
            try {
                Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
                wi.setPerson(effectivePerson.getUnique());
                wi.setToken(effectivePerson.getToken());
                if (!checkFilePermission(effectivePerson, wi)) {
                    logger.warn("파일 권한이 부족합니다.");
                    actionResult.setMessage("파일 권한이 부족합니다.");
                    this.sendMsg(sse, eventSink, EVENT_NAME_MESSAGE, gson.toJson(actionResult));
                    return;
                }
                if (StringUtils.isNotBlank(wi.getInput())) {
                    AiConfig aiConfig = Business.getConfig();
                    if (BooleanUtils.isTrue(aiConfig.getO2AiEnable())
                            && StringUtils.isNotBlank(aiConfig.getO2AiBaseUrl())
                            && StringUtils.isNotBlank(aiConfig.getO2AiToken())) {
                        o2Chat(wi, sse, eventSink, aiConfig);
                    } else {
                        AiModel model = getActiveModel(wi.getEndpointName());
                        if (model != null) {
                            aiChat(wi, sse, eventSink, model, effectivePerson);
                        } else {
                            logger.warn("사용 가능한 모델이 설정되지 않았습니다.");
                            actionResult.setMessage("사용 가능한 모델을 설정하도록 관리자에게 문의하세요.");
                            this.sendMsg(sse, eventSink, EVENT_NAME_MESSAGE, gson.toJson(actionResult));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e);
                actionResult.setMessage("시스템 오류: " + e.getMessage());
                this.sendMsg(sse, eventSink, EVENT_NAME_MESSAGE, gson.toJson(actionResult));
            }
        }
    }

    private boolean checkFilePermission(EffectivePerson effectivePerson, Wi wi) throws Exception {
        if(effectivePerson.isManager() || ListTools.isEmpty(wi.getReferenceIdList())){
            return true;
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            for (String id : wi.getReferenceIdList()){
                File file = emc.flag(id, File.class);
                if(file!=null && !file.getCreator().equals(effectivePerson.getDistinguishedName())){
                    return false;
                }
            }
        }
        return true;
    }

    private AiModel getActiveModel(String name) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            AiModel model = null;
            if(StringUtils.isNotBlank(name)){
                model = emc.firstEqual(AiModel.class, AiModel.name_FIELDNAME, name);
            }
            if(model == null){
                model = emc.firstEqual(AiModel.class, AiModel.asDefault_FIELDNAME, true);
            }
            return model;
        }
    }

    private void o2Chat(Wi wi, Sse sse, SseEventSink eventSink, AiConfig aiConfig)
            throws Exception {
        if (StringUtils.isBlank(wi.getGenerateType())) {
            wi.setGenerateType(ChartWi.GENERATE_TYPE_AUTO);
        }
        if (!ChartWi.GENERATE_TYPE_CHAT.equals(wi.getGenerateType())) {
            final Business business = new Business(null);
            List<String> permissionList = new ArrayList<>(
                    business.organization().person().getAuthInfo(wi.getPerson()));
            List<String> unitList = permissionList.stream()
                    .filter(OrganizationDefinition::isUnitDistinguishedName)
                    .collect(Collectors.toList());
            permissionList.addAll(business.organization().unit().listWithUnitSupNested(unitList));
            permissionList.addAll(
                    permissionList.stream().filter(OrganizationDefinition::isDistinguishedName)
                            .map(o -> StringUtils.substringAfter(o,
                                    OrganizationDefinition.NAME_JOIN_CHAR)).collect(
                                    Collectors.toList()));
            permissionList = permissionList.stream().distinct().filter(StringUtils::isNoneBlank)
                    .collect(Collectors.toList());
            wi.setPermissionList(permissionList);
        }

        String json = gson.toJson(wi);
        logger.info("chat to {} body: {}", CHART_URL, json);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(aiConfig.getO2AiBaseUrl() + CHART_URL);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + aiConfig.getO2AiToken())
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed() && !eventSink.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    logger.warn("chart sse from {} read nothing", CHART_URL);
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? EVENT_NAME_MESSAGE
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                if (StringUtils.isNotBlank(eventData)) {
                    this.sendMsg(sse, eventSink, eventName, eventData);
                }
            }
        } finally {
            client.close();
        }
    }

    private void aiChat(Wi wi, Sse sse, SseEventSink eventSink, AiModel model, EffectivePerson effectivePerson) throws Exception {
        logger.info("chat to {} model:{} input:{}", model.getType(), model.getModel(),
                StringTools.utf8SubString(wi.getInput(), 20) + "...");
        Map<String, Object> data = new HashMap<>();
        data.put("model", model.getModel());
        data.put("stream", true);
        data.put("enable_thinking", BooleanUtils.isTrue(wi.getThinkingEnabled()));
        List<Map<String, Object>> messages = new ArrayList<>();
        final String contentKey = "content";
        final String roleKey = "role";
        messages.add(Map.of(roleKey, "system", contentKey, String.format(CHAT_SYSTEM_MESSAGE, effectivePerson.getName())));
        if (StringUtils.isNotBlank(wi.getClueId())) {
            List<Completion> completions = listCompletion(wi.getClueId());
            completions.stream().sorted(Comparator.comparing(Completion::getCreateTime))
                    .forEach(o -> {
                        messages.add(Map.of(roleKey, "user", contentKey, o.getInput()));
                        messages.add(Map.of(roleKey, "assistant", contentKey, o.getContent()));
                    });
        }
        messages.add(this.joinContent(wi, roleKey, contentKey));
        data.put("messages", messages);
        String json = gson.toJson(data);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        StringBuilder sb = new StringBuilder();
        try {
            WebTarget target = client.target(model.getCompletionUrl());
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + model.getApiKey())
                    .post(Entity.json(json), EventInput.class);
            this.saveClue(wi);
            boolean isFirst = true;
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    logger.warn("chart sse from {} read nothing", CHART_URL);
                    break;
                }
                if (isFirst) {
                    this.sendMsg(sse, eventSink, "status",
                            "{\"generateType\":\"chat\", \"clueId\":\"" + wi.getClueId() + "\"}");
                    isFirst = false;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? EVENT_NAME_MESSAGE
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                if (StringUtils.isNotBlank(eventData)) {
                    if (EVENT_NAME_MESSAGE.equals(eventName) && !END_SSE.equals(eventData)) {
                        sb.append(picContent(eventData));
                    }
                    this.sendMsg(sse, eventSink, eventName, eventData);
                }
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } finally {
            client.close();
        }
        wi.setContent(sb.toString());
        this.saveChart(wi);
    }

    private Map<String, Object> joinContent(Wi wi, String roleKey, String contentKey) throws  Exception{
        Map<String, Object> msgMap = new HashMap<>();
        msgMap.put(roleKey, "user");
        if(ListTools.isEmpty(wi.getReferenceIdList())){
            msgMap.put(contentKey, wi.getInput());
        }else{
            msgMap.put(roleKey, "user");
            List<Map<String, Object>> contentList = new ArrayList<>();
            msgMap.put(contentKey, contentList);
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                Tika tika = new Tika();
                String imageType = "image_url";
                for (String referenceId : wi.getReferenceIdList()){
                    File file = emc.find(referenceId, File.class);
                    if(file != null){
                        Map<String, Object> contentMap = new HashMap<>();
                        contentMap.put("type","text");
                        StorageMapping mapping = ThisApplication.context().storageMappings()
                                .get(File.class, file.getStorage());
                        byte[] bytes = file.readContent(mapping);
                        switch (file.getExtension()) {
                            case "png":
                                contentMap.put("type",imageType);
                                contentMap.put(imageType, Map.of("url","data:image/png;base64,"+ Base64.encodeBase64String(bytes)));
                                break;
                            case "jpg":
                            case "jpeg":
                                contentMap.put("type",imageType);
                                contentMap.put(imageType, Map.of("url","data:image/jpeg;base64,"+ Base64.encodeBase64String(bytes)));
                                break;
                            default:
                                StringBuilder builder = new StringBuilder();
                                try (InputStream input = new ByteArrayInputStream(bytes)){
                                    builder.append(tika.parseToString(input));
                                }
                                logger.info("file length: {}, content: {}", bytes.length, builder);
                                contentMap.put("text", builder.toString());
                                break;
                        }
                        contentList.add(contentMap);
                    }
                }
            }
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("type","text");
            contentMap.put("text", wi.getInput());
            contentList.add(contentMap);
        }
        return msgMap;
    }

    private void sendMsg(Sse sse, SseEventSink eventSink, String eventName, String eventData) {
        if (eventSink.isClosed()) {
            return;
        }
        final OutboundSseEvent sseEvent = sse.newEventBuilder()
                .name(eventName)
                .mediaType(MediaType.TEXT_PLAIN_TYPE)
                .data(String.class, eventData).build();
        eventSink.send(sseEvent);
    }

    private void saveClue(Wi wi) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Clue clue = null;
            if (StringUtils.isNotBlank(wi.getClueId())) {
                clue = emc.find(wi.getClueId(), Clue.class);
            }
            if (clue == null) {
                clue = new Clue(wi.getPerson(), wi.getInput());
                emc.beginTransaction(Clue.class);
                emc.persist(clue);
                emc.commit();
            }
            wi.setClueId(clue.getId());
        }
    }

    private void saveChart(Wi wi) {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Clue clue = null;
            if (StringUtils.isNotBlank(wi.getClueId())) {
                clue = emc.find(wi.getClueId(), Clue.class);
            }
            if (clue == null) {
                clue = new Clue(wi.getPerson(), wi.getInput());
                emc.beginTransaction(Clue.class);
                emc.persist(clue);
                emc.commit();
            }
            Completion completion = new Completion(wi.getPerson(), clue.getId(), wi.getInput(),
                    wi.getContent());
            completion.setReferenceIdList(wi.getReferenceIdList());
            emc.beginTransaction(Completion.class);
            emc.persist(completion);
            emc.commit();
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private String picContent(String content) {
        String data = "";
        if (StringUtils.isBlank(content)) {
            return data;
        }
        JsonObject jo = gson.fromJson(content, JsonObject.class);
        if (jo != null && jo.has("choices")) {
            JsonArray choices = jo.getAsJsonArray("choices");
            if (!choices.isEmpty()) {
                data = XGsonBuilder.extractString(choices.get(0), "delta.content");
            }
        }
        return data == null ? "" : data;
    }

    private List<Completion> listCompletion(String clueId) throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            EntityManager em = emc.get(Completion.class);
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Completion> cq = cb.createQuery(Completion.class);
            Root<Completion> root = cq.from(Completion.class);
            Predicate p = cb.equal(root.get(Completion_.clueId), clueId);
            cq.select(root).where(p).orderBy(cb.desc(root.get(JpaObject_.createTime)));
            return em.createQuery(cq).setMaxResults(5).getResultList();
        }
    }

    public static class Wi extends ChartWi {

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

}
