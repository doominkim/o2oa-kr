package com.x.program.center.jaxrs.apppack;


import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 패키징 서버 점검 기능
 * Created by fancyLou on 6/11/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionConnectPackServer extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(ActionConnectPackServer.class);

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<Wo>();
        Wo wo = new Wo();
        if (BooleanUtils.isNotTrue(connect())) {
            wo.setStatus(1); // o2 클라우드 미연결
            result.setData(wo);
            return result;
        }
        if (BooleanUtils.isFalse(Config.collect().getEnable())) {
            wo.setStatus(1); // o2 클라우드 미사용
            result.setData(wo);
            return result;
        }
        if (BooleanUtils.isNotTrue(validate(Config.collect().getName(), Config.collect().getPassword()))) {
            wo.setStatus(2); // o2 클라우드 미로그인
            result.setData(wo);
            return result;
        }
        String token = getPackServerSSOToken();
        if (StringUtils.isEmpty(token)) {
            wo.setStatus(3); // 미인증
        }else {
            wo.setStatus(1001);
        }
        result.setData(wo);
        return result;
    }


    /**
     * collect 서버 연결 확인
     * @return
     */
    private Boolean connect() {
        try {
            String url = Config.collect().url(Collect.ADDRESS_COLLECT_ECHO);
            ActionResponse actionResponse = ConnectionAction.get(url, null);
            return Objects.equals(ActionResult.Type.success, actionResponse.getType());
        } catch (Exception e) {
            logger.info("collect 서버 연결에 실패했습니다");
            logger.error(e);
            return false;
        }
    }

    /**
     * collect 서버 인증 확인
     * @param name  collect 계정
     * @param password collect 비밀번호
     * @return 인증 성공 시 true, 아니면 false 반환
     */
    private Boolean validate(String name, String password) {
        try {
            String url = Config.collect().url(Collect.ADDRESS_COLLECT_VALIDATE);
            Map<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("password", password);
            ActionResponse resp = ConnectionAction.post(url, null, map);
            return resp.getData(ReturnWoBoolean.class).getValue();
        } catch (Exception e) {
            logger.info("collect 서버 로그인에 실패했습니다");
            logger.error( e);
        }
        return false;
    }

    /**
     * 패키징 서버 인증 반환 객체
     */
    public static class AuthTokenData implements Serializable {

        private String collectName;
        private String token;

        public String getCollectName() {
            return collectName;
        }

        public void setCollectName(String collectName) {
            this.collectName = collectName;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    public static class Wo extends GsonPropertyObject {

        @FieldDescribe( "연결 상태, 1: o2 클라우드 서비스 미사용, 2: o2 클라우드 미로그인, 3: 패키징 서버 미로그인 , 1001: 성공" )
        private int status; //   1 o2 클라우드 서비스 미사용, 2 o2 클라우드 미로그인, 3 apppack 서비스 미로그인 , 1001 성공
        @FieldDescribe( "패키징 서버 주소" )
        private String packServerUrl;

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPackServerUrl() {
            return packServerUrl;
        }

        public void setPackServerUrl(String packServerUrl) {
            this.packServerUrl = packServerUrl;
        }
    }

    private static class ReturnWoBoolean extends WrapBoolean {
    }
}
