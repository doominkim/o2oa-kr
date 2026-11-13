package com.x.base.core.project.config;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;


public class JpushConfig extends ConfigObject {

    private static final String O2_app_key_default = "9aca7cc20fe0cc987cd913ca";
    private static final String O2_master_secret_default = "96ee7e2e0daffd51bac57815";


    // 자체 패키징한 outer 패키지에서 사용하는 key
    private static final String O2_app_key_outer = "24a4af5965d2c325b33c243d";
    private static final String O2_master_secret_outer = "a7b5689399307b29957e7dce";


    public static JpushConfig defaultInstance() {
        return new JpushConfig();
    }
    public JpushConfig() {
        this.enable = true;
        this.appKey = O2_app_key_default;
        this.masterSecret = O2_master_secret_default;
        this.thirdPartyChannel = o2oaOfficialPushChannel();
        this.badgeClass = ""; // 기본값은 설정하지 않음
    }


    @FieldDescribe("사용 여부.")
    private Boolean enable;
    @FieldDescribe("JPush 앱의 AppKey")
    private String appKey;
    @FieldDescribe("JPush 앱의 Master Secret")
    private String masterSecret;
    // "thirdPartyChannel": { "xiaomi": {"channel_id": ""}, "huawei": { "importance": "NORMAL", "category": "WORK" } }
    @FieldDescribe("서드파티 채널 파라미터 설정")
    private ThirdPartyChannel thirdPartyChannel;
    @FieldDescribe("Android 앱 진입 Activity 클래스")
    private String badgeClass;



    // o2oa 공식 app 샤오미 및 화웨이 채널
    ThirdPartyChannel o2oaOfficialPushChannel() {
        ThirdPartyChannel map = new ThirdPartyChannel();
        Xiaomi xiaomi = new Xiaomi();
        xiaomi.setChannel_id("113850");
        map.setXiaomi(xiaomi);
        Huawei huawei = new Huawei();
        huawei.setImportance("NORMAL");
        huawei.setCategory("WORK");
        map.setHuawei(huawei);
        Oppo oppo = new Oppo();
        oppo.setChannel_id("o2oa_work");
        oppo.setCategory("TODO");
        oppo.setNotify_level(16);
        map.setOppo(oppo);
        Vivo vivo = new Vivo();
        vivo.setCategory("TODO");
        map.setVivo(vivo);
        return map;
    }


    /**
     * 자체 패키징으로 생성된 외부 패키지명 app에서 사용하는 config 조회
     * @return
     */
    public JpushConfig getOuterApplicationJpushConfig() {
        JpushConfig config = new JpushConfig();
        config.setAppKey(O2_app_key_outer);
        config.setMasterSecret(O2_master_secret_outer);
        config.setEnable(true);
        return config;
    }


    public String getBadgeClass() {
        return badgeClass;
    }

    public void setBadgeClass(String badgeClass) {
        this.badgeClass = badgeClass;
    }

    public Boolean getEnable() {
        return BooleanUtils.isTrue(this.enable);
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getMasterSecret() {
        return masterSecret;
    }

    public void setMasterSecret(String masterSecret) {
        this.masterSecret = masterSecret;
    }


    public ThirdPartyChannel getThirdPartyChannel() {
        return thirdPartyChannel;
    }

    public void setThirdPartyChannel(
            ThirdPartyChannel thirdPartyChannel) {
        this.thirdPartyChannel = thirdPartyChannel;
    }

    public Map<String, Object> getThirdPartyChannelMap() {
        if (this.thirdPartyChannel != null) {
            Map<String, Object> map = new HashMap<>();
            if (this.thirdPartyChannel.getXiaomi() != null) {
                map.put("xiaomi",  this.thirdPartyChannel.getXiaomi());
            }
            if (this.thirdPartyChannel.getHuawei() != null) {
                map.put("huawei",  this.thirdPartyChannel.getHuawei());
            }
            if (this.thirdPartyChannel.getOppo() != null) {
                map.put("oppo",  this.thirdPartyChannel.getOppo());
            }
            if (this.thirdPartyChannel.getVivo() != null) {
                map.put("vivo",  this.thirdPartyChannel.getVivo());
            }
            if (this.thirdPartyChannel.getHonor() != null) {
                map.put("honor",  this.thirdPartyChannel.getHonor());
            }
            return map;
        }
        return null;
    }

    public static class ThirdPartyChannel extends GsonPropertyObject {

        @FieldDescribe("샤오미 푸시 채널 파라미터")
        private Xiaomi xiaomi;
        @FieldDescribe("화웨이 푸시 채널 파라미터")
        private Huawei huawei;
        @FieldDescribe("OPPO 푸시 채널 파라미터")
        private Oppo oppo;
        @FieldDescribe("아너(Honor) 푸시 채널 파라미터")
        private Honor honor;
        @FieldDescribe("VIVO 푸시 채널 파라미터")
        private Vivo vivo;

        public Vivo getVivo() {
            return vivo;
        }

        public void setVivo(Vivo vivo) {
            this.vivo = vivo;
        }

        public Honor getHonor() {
            return honor;
        }

        public void setHonor(Honor honor) {
            this.honor = honor;
        }

        public Oppo getOppo() {
            return oppo;
        }

        public void setOppo(Oppo oppo) {
            this.oppo = oppo;
        }

        public Xiaomi getXiaomi() {
            return xiaomi;
        }

        public void setXiaomi(Xiaomi xiaomi) {
            this.xiaomi = xiaomi;
        }

        public Huawei getHuawei() {
            return huawei;
        }

        public void setHuawei(Huawei huawei) {
            this.huawei = huawei;
        }
    }


    public static class Vivo extends GsonPropertyObject {

        private static final long serialVersionUID = 4441517807393020333L;
        @FieldDescribe("vivo 제조사 메시지 시나리오 식별자")
        private String category;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static class Oppo  extends GsonPropertyObject {

        private static final long serialVersionUID = 2691848272185396738L;
        @FieldDescribe("android 알림 channel_id")
        private String channel_id;
        @FieldDescribe("OPPO 제조사 메시지 시나리오 식별자")
        private String category;
        @FieldDescribe("OPPO 알림 표시줄 메시지 알림 등급")
        private Integer notify_level;

        public String getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(String channel_id) {
            this.channel_id = channel_id;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Integer getNotify_level() {
            return notify_level;
        }

        public void setNotify_level(Integer notify_level) {
            this.notify_level = notify_level;
        }
    }

    public static class Xiaomi extends GsonPropertyObject {

        private static final long serialVersionUID = -372457646001459805L;
        @FieldDescribe("샤오미 푸시의 channel_id")
        private String channel_id;

        public String getChannel_id() {
            return channel_id;
        }

        public void setChannel_id(String channel_id) {
            this.channel_id = channel_id;
        }
    }

    public static class Huawei extends GsonPropertyObject {

        private static final long serialVersionUID = 7441536586208244234L;
        @FieldDescribe("화웨이 알림 표시줄 메시지 스마트 분류")
        private String importance;
        @FieldDescribe("화웨이 제조사 메시지 시나리오 식별자")
        private String category;

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public static class Honor extends GsonPropertyObject {


        private static final long serialVersionUID = -6989617318639425429L;
        @FieldDescribe("아너(Honor) 알림 표시줄 메시지 스마트 분류")
        private String importance;

        public String getImportance() {
            return importance;
        }

        public void setImportance(String importance) {
            this.importance = importance;
        }
    }
}
