package com.x.organization.assemble.authentication.jaxrs.qiyeweixin;

import java.security.MessageDigest;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 2022/9/29.
 * Copyright © 2022 O2. All rights reserved.
 */
public class ActionJssdkSignInfo extends BaseAction {


    private static Logger logger = LoggerFactory.getLogger(ActionJssdkSignInfo.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
      if (null == Config.qiyeweixin()) {
          throw new ExceptionQywexinNotConfigured();
      }
      if (!Config.qiyeweixin().getEnable()) {
          throw new ExceptionQywexinNotConfigured();
      }
      ActionResult<Wo> result = new ActionResult<>();
      Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
      if (StringUtils.isEmpty(wi.getUrl())) {
        throw new ExceptionUrlEmpty();
      }

      String noncestr = "o2oa";
      if (StringUtils.isNotEmpty(wi.getNonceStr())) {
        noncestr = wi.getNonceStr();
      }
      long timestamp = new Date().getTime();
      String jsticket = "";
      // 두 ticket의 차이를 알 수 없어 기본으로 getJsapiTicket() 사용
      if (wi.getJsticketType() != null && "app".equals(wi.getJsticketType())) {
        jsticket = Config.qiyeweixin().getAppJsapiTicket();
          if (logger.isDebugEnabled()) {
              logger.debug("앱의 jsticket: "+jsticket);
          }
      } else {
        jsticket = Config.qiyeweixin().getJsapiTicket();
          if (logger.isDebugEnabled()) {
              logger.debug("기업의 jsticket: "+jsticket);
          }
      }
      String corpId = Config.qiyeweixin().getCorpId();
      String agentId = Config.qiyeweixin().getAgentId();
      if (logger.isDebugEnabled()) {
        logger.debug("매개변수 jsticket: "+jsticket + " noncestr:  "+noncestr + " timestamp: "+ timestamp + " url: " +wi.getUrl() + " " + corpId + " " + agentId);
      }
      String signStr = sha1(jsticket, noncestr, timestamp, wi.getUrl());
      logger.info("암호화 결과 "+ signStr);  
      Wo wo = new Wo();
      wo.setAgentid(agentId);
      wo.setCorpid(corpId);
      wo.setNonceStr(noncestr);
      wo.setTimestamp(timestamp);
      wo.setSignature(signStr);
      result.setData(wo);
      return result;

    }

    // sha1 암호화 후 문자열로 변환
    private String bytesToHex(byte[] digest) {
      StringBuffer hexstr = new StringBuffer();
      String shaHex = "";
      for (int i = 0; i < digest.length; i++) {
          shaHex = Integer.toHexString(digest[i] & 0xFF);
          if (shaHex.length() < 2) {
              hexstr.append(0);
          }
          hexstr.append(shaHex);
      }
      return hexstr.toString();
    }

    // sha1 암호화
    private String sha1(String jsticket,String noncestr, Long timestamp, String url) throws Exception {
      String str = "jsapi_ticket="+jsticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;
      if (logger.isDebugEnabled()) {
         logger.debug("sha1 암호화 원본 문자열 " + str);
      }
      MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
      sha1.reset();
      sha1.update(str.getBytes("UTF-8"));
      return bytesToHex(sha1.digest());
    }




    public static class Wi extends GsonPropertyObject {

        private static final long serialVersionUID = -1860237519725394442L;
        // # 및 그 뒷부분 미포함
        @FieldDescribe("서명 주소, 현재 기업 위챗 jssdk를 사용하는 url, 필수")
        private String url;

        @FieldDescribe("임의 문자열, 생략 가능")
        private String nonceStr;

        // 만약 jsticketType == "app"
        private String jsticketType;


        
        
        public String getJsticketType() {
          return jsticketType;
        }

        public void setJsticketType(String jsticketType) {
          this.jsticketType = jsticketType;
        }

        public String getNonceStr() {
          return nonceStr;
        }

        public void setNonceStr(String nonceStr) {
          this.nonceStr = nonceStr;
        }

        public String getUrl() {
          return url;
        }

        public void setUrl(String url) {
          this.url = url;
        }

      }

	public static class Wo extends GsonPropertyObject {
		private static final long serialVersionUID = -5490364649895712312L;


    private String signature;
		private String nonceStr;
		private Long timestamp;
		private String corpid;
		private String agentid;
    public String getSignature() {
      return signature;
    }
    public void setSignature(String signature) {
      this.signature = signature;
    }
    public String getNonceStr() {
      return nonceStr;
    }
    public void setNonceStr(String nonceStr) {
      this.nonceStr = nonceStr;
    }
    public Long getTimestamp() {
      return timestamp;
    }
    public void setTimestamp(Long timestamp) {
      this.timestamp = timestamp;
    }
    public String getCorpid() {
      return corpid;
    }
    public void setCorpid(String corpid) {
      this.corpid = corpid;
    }
    public String getAgentid() {
      return agentid;
    }
    public void setAgentid(String agentid) {
      this.agentid = agentid;
    }


    
  }
}