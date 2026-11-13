package com.x.jpush.assemble.control.jaxrs.message;


import cn.jiguang.sdk.api.PushApi;
import cn.jiguang.sdk.bean.push.PushSendParam;
import cn.jiguang.sdk.bean.push.PushSendResult;
import cn.jiguang.sdk.bean.push.audience.Audience;
import cn.jiguang.sdk.bean.push.message.notification.NotificationMessage;
import cn.jiguang.sdk.bean.push.options.Options;
import cn.jiguang.sdk.enums.platform.Platform;
import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.jpush.assemble.control.Business;
import com.x.jpush.assemble.control.JpushConst;
import com.x.jpush.core.entity.PushDevice;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

public class ActionSendMessage extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionSendMessage.class);

    protected ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("execute action 'ActionSendMessage' .");
        }
        ActionResult<Wo> result = new ActionResult<>();
        Wo wraps = new Wo();
        if (jsonElement == null) {
            throw new ExceptionSendMessageEmpty();
        }
        Wi wi = convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isEmpty(wi.getPerson()) || StringUtils.isEmpty(wi.getMessage())) {
            throw new ExceptionSendMessageEmpty();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("JPush 푸시 메시지: {}", wi.toString());
        }
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            logger.info("JPush 푸시 채널 사용 중, JPush로 메시지 발송, 사용자: {}", wi.getPerson());
            List<PushDevice> pushDeviceList = business.pushDeviceFactory().listJpushDevice(wi.getPerson());
            if (pushDeviceList != null && !pushDeviceList.isEmpty()) {
                send2Jpush(pushDeviceList, wi, business.pushDeviceFactory().jpushClient());
            } else {
                logger.warn("JPush 푸시 기기가 비어 있음, {}", wi.getPerson());
            }
            wraps.setValue(true);
            result.setData(wraps);
        } catch (Exception e) {
            logger.error(e);
            throw new ExceptionSendMessage(e, "푸시 메시지를 보내는 중 오류가 발생했습니다!");
        }
        if (logger.isDebugEnabled()) {
            logger.debug("action 'ActionSendMessage' execute completed!");
        }
        return result;
    }

    /**
     * JPush 푸시 메시지
     *
     * @param pushDeviceList
     * @param wi
     * @param client
     * @throws Exception
     */
    private void send2Jpush(List<PushDevice> pushDeviceList, Wi wi, PushApi client) throws Exception {
        List<String> jiguangDeviceList = pushDeviceList.stream().map(PushDevice::getDeviceId)
                .collect(Collectors.toList());
        PushSendParam param = new PushSendParam();
        NotificationMessage.Android android = new NotificationMessage.Android();
        android.setAlert(wi.getMessage());
        android.setPriority(0);
        String badgeClass = Config.pushConfig().getBadgeClass();
        if (StringUtils.isEmpty(badgeClass)) {
            badgeClass = JpushConst.launchActivity;
        }
        android.setBadgeClass(badgeClass);
        android.setBadgeAddNumber(1);
        android.setChannelId(androidChannelId);
        NotificationMessage.IOS iOS = new NotificationMessage.IOS();
        iOS.setAlert(wi.getMessage());
        iOS.setBadge("+1");
        Map<String, Object> extras = new HashMap<>();
        if (wi.getStringExtras() != null) {
            extras.putAll(wi.getStringExtras());
        }
        if (wi.getNumberExtras() != null) {
            extras.putAll(wi.getNumberExtras());
        }
        if (wi.getBooleanExtras() != null) {
            extras.putAll(wi.getBooleanExtras());
        }
        if (wi.getJsonExtras() != null) {
            extras.putAll(wi.getJsonExtras());
        }
        if (!extras.isEmpty()) {
            android.setExtras(extras);
            iOS.setExtras(extras);
        }
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setAlert(wi.getMessage());
        notificationMessage.setAndroid(android);
        notificationMessage.setIos(iOS);
        param.setNotification(notificationMessage);
        // 푸시 대상 기기 설정
        Audience audience = new Audience();
        audience.setRegistrationIdList(jiguangDeviceList);
        param.setAudience(audience);
        param.setPlatform(Arrays.asList(Platform.android, Platform.ios));
        // 푸시 기타 파라미터 설정
        Options options = new Options();
        options.setApnsProduction(!BooleanUtils.isFalse(wi.getApnsProduction()));
        options.setClassification(1); // 시스템 메시지 1 운영 메시지 0
        Map<String, Object> thirdMap =  Config.pushConfig().getThirdPartyChannelMap() ;
        if (thirdMap != null && !thirdMap.isEmpty()) {
            options.setThirdPartyChannel(thirdMap);
        }

        param.setOptions(options);
        logger.info("JPush 푸시 body: {}", param.toString());
        // 발송
        PushSendResult pushResult = client.send(param);
        logger.info("JPush 푸시 발송 결과:{}.", pushResult);
    }

    public static class Wi extends GsonPropertyObject {

        @FieldDescribe("사용자")
        private String person;

        @FieldDescribe("메시지 내용")
        private String message;

        @FieldDescribe("문자열 확장")
        private Map<String, String> stringExtras;
        @FieldDescribe("숫자 확장")
        private Map<String, Number> numberExtras;
        @FieldDescribe("불리언 확장")
        private Map<String, Boolean> booleanExtras;
        @FieldDescribe("객체 확장")
        private Map<String, Object> jsonExtras;

        private Boolean apnsProduction;

        public Boolean getApnsProduction() {
            return apnsProduction;
        }

        public void setApnsProduction(Boolean apnsProduction) {
            this.apnsProduction = apnsProduction;
        }

        public Map<String, String> getStringExtras() {
            return stringExtras;
        }

        public void setStringExtras(Map<String, String> stringExtras) {
            this.stringExtras = stringExtras;
        }

        public Map<String, Number> getNumberExtras() {
            return numberExtras;
        }

        public void setNumberExtras(Map<String, Number> numberExtras) {
            this.numberExtras = numberExtras;
        }

        public Map<String, Boolean> getBooleanExtras() {
            return booleanExtras;
        }

        public void setBooleanExtras(Map<String, Boolean> booleanExtras) {
            this.booleanExtras = booleanExtras;
        }

        public Map<String, Object> getJsonExtras() {
            return jsonExtras;
        }

        public void setJsonExtras(Map<String, Object> jsonExtras) {
            this.jsonExtras = jsonExtras;
        }

        public String getPerson() {
            return person;
        }

        public void setPerson(String person) {
            this.person = person;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Wo extends WrapBoolean {

    }
}
