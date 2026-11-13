package com.x.program.center.jaxrs.collect;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.ApplicationServer;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

/**
 * Created by fancyLou on 7/27/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionCheckQrCode extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionCheckQrCode.class);

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    ActionResult<Wo> execute() throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wo wo = new Wo();
        wo.setApplicationList(new ArrayList<AbstractWoProxy.Application>());
        Map.Entry<String, CenterServer> firstCenter = Config.nodes().centerServers().first();
        if (firstCenter == null) {
            throw new ExceptionProxyEmpty("센터 서비스");
        }
        String protocol = firstCenter.getValue().getHttpProtocol();
        if (StringUtils.isEmpty(protocol)) {
            throw new ExceptionProxyEmpty("http 접근 프로토콜");
        }
        wo.setHttpProtocol(protocol);

        AbstractWoProxy.Center center = new AbstractWoProxy.Center();
        String centerHost = firstCenter.getValue().getProxyHost();
        if (StringUtils.isEmpty(centerHost)) {
            throw new ExceptionProxyEmpty("센터 서버 프록시 Host");
        }
        center.setProxyHost(centerHost);
        center.setProxyPort(firstCenter.getValue().getProxyPort());
        wo.setCenter(center);

        for (Map.Entry<String, Node> en : Config.nodes().entrySet()) {
            if (null != en.getValue()) {
                WebServer webServer = en.getValue().getWeb();
                if (null != webServer && BooleanUtils.isTrue(webServer.getEnable())) {
                    AbstractWoProxy.Web web = new AbstractWoProxy.Web();
                    web.setProxyHost(StringUtils.isNotEmpty(webServer.getProxyHost()) ? webServer.getProxyHost() : centerHost); // 중앙 서버 host 사용이 설정되지 않았습니다
                    web.setProxyPort(webServer.getProxyPort());
                    wo.setWeb(web);
                }
                ApplicationServer applicationServer = en.getValue().getApplication();
                if (null != applicationServer && BooleanUtils.isTrue(applicationServer.getEnable())) {
                    AbstractWoProxy.Application application = new AbstractWoProxy.Application();
                    application.setNode(en.getKey());
                    application.setProxyHost(StringUtils.isNotEmpty(applicationServer.getProxyHost()) ? applicationServer.getProxyHost() : centerHost);// 중앙 서버 host 사용이 설정되지 않았습니다
                    application.setProxyPort(applicationServer.getProxyPort());
                    wo.getApplicationList().add(application);
                }
            }
        }
        if (wo.getWeb() == null) {
            throw new ExceptionProxyEmpty("web 서버");
        }

        String url = protocol + "://" + wo.getWeb().getProxyHost() + ":" + wo.getWeb().getProxyPort() + "/x_desktop/appMobileConnectCheck.html";
        logger.info("QR 스캔 연결 테스트 주소: " + url);

        /**
         * QR 코드 생성
         */
        int width = 200; // QR 코드 이미지 너비
        int height = 200; // QR 코드 이미지 높이
        String format = "png";// QR 코드 이미지 형식

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        // hints.put(EncodeHintType.CHARACTER_SET, DefaultCharset.name); //
        // 내용에 사용하는 문자셋 인코딩
        hints.put(EncodeHintType.MARGIN, "1");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q.toString());

        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, out);
            wo.setQrcode(Base64.encodeBase64String(out.toByteArray()));
        }
        result.setData(wo);
        return result;
    }


    public static class Wo extends  AbstractWoProxy {

        @FieldDescribe("QR 코드 이미지 base64")
        private String qrcode;

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }
    }
}
