package com.x.ai.assemble.control.jaxrs.chat;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 * @author chengjian
 * @date 2025/05/29 14:24
 **/
public class ChatTest {

    private static final String END_SSE = "[DONE]";
    private static final String CHAT_SYSTEM_MESSAGE =
            "당신은 전문적이고 친절한 도우미이며, 명확하고 체계적으로 질문에 답하는 데 능숙합니다. 현재 대화 중인 사용자의 식별자는 %s이며, 사용자 요청을 처리할 때 이 식별자를 맥락으로 참고하세요."
                    + "어떤 질문에 답하든 항상 Markdown 형식으로 답변을 구성하고 다음 규칙을 따르세요:"
                    + "1. 제목(## 또는 ###)으로 주요 내용이나 단락을 구성합니다."
                    + "2. 글머리 기호(- 또는 *)나 번호(1. 2. 3.)로 목록 항목을 나열합니다."
                    + "3. 굵게로 강조를, 기울임꼴로 보조 강조를 나타냅니다."
                    + "4. 코드나 기술 내용은 ```로 감쌉니다."
                    + "5. 인용이 필요하면 > 기호를 사용합니다."
                    + "6. 단락 사이에 빈 줄을 넣어 깔끔하게 정리합니다."
                    + "7. 표가 필요한 질문에는 Markdown 표 형식을 사용합니다."
                    + "목표는 간결하고 구조화된 Markdown 형식으로 정확하고 유용한 답변을 제공하는 것입니다. 사용자가 다른 형식을 명시적으로 요청하면 사용자 요청을 우선합니다.";

    public static void main(String[] args) {
        o2Chat();
//        aliChat();
//        deepSeekChat();
    }
    private static void o2Chat(){
        System.out.println("o2 chat start");
        String url = "https://devp.o2oa.net/x_ai_assemble_control/jaxrs/chat/completion";
        String apiKey = "XKXL_d0PoMOuNH4UC6ENkTttmZxBXbY_3Zud7y4qM383LMUzpPSJ2TtC0yShKaaX9xbl5evq6HhnKKFIYXKyww";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("generateType", "chat");
        wi.put("input", "안녕하세요");
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("o2 chat end");
    }
    private static void aliChat(){
        System.out.println("ali chat start");
        String url = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
        String apiKey = "sk-d586c1805c214e4ea68f348ebe339881";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("model", "qwen-turbo");
        wi.put("stream", true);
        wi.put("messages", List.of(Map.of("role", "system", "content", CHAT_SYSTEM_MESSAGE),
                Map.of("role", "user", "content", "안녕하세요, 자기소개를 해 주세요")));
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer "+apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("ali chat end");

    }

    public static void deepSeekChat() {
        System.out.println("deepSeek chat start");
        String url = "https://api.deepseek.com/v1/chat/completions";
        String apiKey = "sk-e22789021a12484daa6dff976fc91a9a";
        Gson gson = new Gson();
        Map<String, Object> wi = new HashMap<>();
        wi.put("model", "deepseek-chat");
        wi.put("stream", true);
        wi.put("messages", List.of(Map.of("role", "system", "content", CHAT_SYSTEM_MESSAGE),
                Map.of("role", "user", "content", "안녕하세요, 자기소개를 해 주세요")));
        String json = gson.toJson(wi);
        ClientConfig config = new ClientConfig().register(SseFeature.class);
        config.property(ClientProperties.CONNECT_TIMEOUT, 60000);
        config.property(ClientProperties.READ_TIMEOUT, 0);
        Client client = ClientBuilder.newClient(config);
        try {
            WebTarget target = client.target(url);
            EventInput eventInput = target.request(MediaType.SERVER_SENT_EVENTS)
                    .header("Authorization", "Bearer " + apiKey)
                    .post(Entity.json(json), EventInput.class);
            while (!eventInput.isClosed()) {
                final InboundEvent inboundEvent = eventInput.read();
                if (inboundEvent == null) {
                    break;
                }
                String eventName = StringUtils.isBlank(inboundEvent.getName()) ? "message"
                        : inboundEvent.getName();
                String eventData = inboundEvent.readData(String.class);
                System.out.println(eventName + ":" + eventData);
                if (END_SSE.equals(eventData)) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
        System.out.println("deepSeek chat end");
    }
}
