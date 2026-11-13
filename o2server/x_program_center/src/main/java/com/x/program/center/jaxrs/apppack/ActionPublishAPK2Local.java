package com.x.program.center.jaxrs.apppack;

import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.JpushConfig;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;
import com.x.program.center.core.entity.AppPackApkFile;
import com.x.program.center.jaxrs.config.ActionSave;

/**
 * Created by fancyLou on 11/30/21.
 * Copyright © 2021 O2. All rights reserved.
 */
public class ActionPublishAPK2Local extends BaseAction {

    private static Logger logger = LoggerFactory.getLogger(ActionPublishAPK2Local.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
        ActionResult<Wo> result = new ActionResult<>();
        Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
        if (StringUtils.isBlank(wi.getApkPath())) {
            throw new ExceptionEmptyProperty("apkPath");
        }
        if (StringUtils.isBlank(wi.getPackInfoId())) {
            throw new ExceptionEmptyProperty("packInfoId");
        }
        try {
            String fileName = wi.getPackInfoId() + ".apk";
            StorageMapping mapping = ThisApplication.context().storageMappings().random(AppPackApkFile.class);
            if (null == mapping) {
                throw new ExceptionAllocateStorageMapping();
            }
            try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                AppPackApkFile appPackApkFile = new AppPackApkFile();
                appPackApkFile.setName(fileName);
                appPackApkFile.setExtension(StringUtils.lowerCase(FilenameUtils.getExtension(fileName)));
                appPackApkFile.setStorage(mapping.getName());
                appPackApkFile.setPackInfoId(wi.getPackInfoId());
                Date now = new Date();
                appPackApkFile.setCreateTime(now);
                appPackApkFile.setLastUpdateTime(now);
                appPackApkFile.setStatus(AppPackApkFile.statusInit);
                appPackApkFile.setAppVersionNo(wi.getAppVersionNo());
                appPackApkFile.setAppVersionName(wi.getAppVersionName());
                appPackApkFile.setIsPackAppIdOuter(wi.getIsPackAppIdOuter());
                emc.check(appPackApkFile, CheckPersistType.all);
                appPackApkFile.saveContent(mapping, new byte[] {}, fileName);
                emc.beginTransaction(AppPackApkFile.class);
                emc.persist(appPackApkFile);
                emc.commit();

                // apk 파일 비동기 다운로드 및 업데이트 시작
                DownloadJob job = new DownloadJob(wi, appPackApkFile.getId());
                job.start();
            }
            Wo wo = new Wo();
            wo.setValue(true);
            result.setData(wo);
        } catch (Exception e) {
            logger.error(e);

        }
        return result;
    }

    public class DownloadJob extends Thread {
        private Wi wi;
        private String id;

        DownloadJob(Wi wi, String appPackApkFileId) {
            this.wi = wi;
            this.id = appPackApkFileId;
        }

        @Override
        public void run() {
            logger.info("apk 파일 다운로드 시작");
            try {
                StorageMapping mapping = ThisApplication.context().storageMappings().random(AppPackApkFile.class);
                if (null == mapping) {
                    throw new ExceptionAllocateStorageMapping();
                }
                String url = Config.collect().appPackServerUrl() + wi.getApkPath() + "?token=" + getPackServerSSOToken();
                logger.info("apk 다운로드 url: " + url);
                byte[] bytes = ConnectionAction.getBinary(url, null);
                try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                    AppPackApkFile file = emc.find(this.id, AppPackApkFile.class);
                    if (file != null) {
                        emc.beginTransaction(AppPackApkFile.class);
                        file.updateContent(mapping, bytes);
                        file.setStatus(AppPackApkFile.statusCompleted);
                        file.setLastUpdateTime(new Date());
                        emc.commit();
                        // 배포 성공, jpush 설정 파일을 수정해 outer 패키지의 key를 설정해야 함
                        if (StringUtils.isNotBlank(file.getIsPackAppIdOuter())
                                && "2".equals(file.getIsPackAppIdOuter())) {
                            updateJpushConfig();
                        }
                        updateAppUrl(wi.getWebUrl());
                        logger.info("배포 apk 다운로드에 성공했습니다!");
                    } else {
                        logger.error(new Exception("오류, 해당 파일 객체를 찾을 수 없습니다, id:" + id));
                    }
                }

            } catch (Exception e) {
                logger.error(e);
                logger.info("다운로드 실패, 오류 상태 업데이트.");
                try {
                    try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
                        AppPackApkFile file = emc.find(this.id, AppPackApkFile.class);
                        if (file != null) {
                            emc.beginTransaction(AppPackApkFile.class);
                            file.setStatus(AppPackApkFile.statusError);
                            file.setLastUpdateTime(new Date());
                            emc.commit();
                        } else {
                            logger.error(new IllegalStateException("오류, 해당 파일 객체를 찾을 수 없습니다, id:" + id + "."));
                        }
                    }
                } catch (Exception e1) {
                    logger.error(e1);
                }
            }
        }

        private void updateJpushConfig() {
            try {
                logger.info("jpush 파일 수정 시작.......");
                JpushConfig newConfig = JpushConfig.defaultInstance().getOuterApplicationJpushConfig();
                updateConfigFileOnLine(Config.NAME_CONFIG_JPUSH, newConfig.toString());
            } catch (Exception e) {
                logger.error(e);
            }
        }

        /**
         * appUrl collect.json 설정 파일 업데이트, 로그인 화면에서 QR 스캔으로 다운로드 가능
         */
        private void updateAppUrl(String webUrl) {
            try {
                logger.info("updateAppUrl : " + webUrl);
                if (StringUtils.isNotEmpty(webUrl)) {
                    String myUrl = webUrl;
                    String protocol = Config.nodes().centerServers().first().getValue().getHttpProtocol();
                    String webProxyHost = "";
                    int webProxyPort = 80;
                    for (Map.Entry<String, Node> en : Config.nodes().entrySet()) {
                        if (null != en.getValue()) {
                            WebServer webServer = en.getValue().getWeb();
                            if (null != webServer && BooleanUtils.isTrue(webServer.getEnable())) {
                                webProxyHost = webServer.getProxyHost();
                                webProxyPort = webServer.getProxyPort();
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(webProxyHost)) {
                        String url = protocol + "://" + webProxyHost + ":" + webProxyPort;
                        if (!url.equals(webUrl)) {
                            myUrl = url;
                        }
                    }
                    logger.info("최종 URL :" + myUrl);
                    Config.collect().setAppUrl(myUrl + "/x_desktop/appDownload.html");
                    updateConfigFileOnLine(Config.NAME_CONFIG_COLLECT, Config.collect().toString());
                }

            } catch (Exception e) {
                logger.error(e);
            }
        }

        /**
         * 설정 파일 업데이트
         * 
         * @param fileName 설정 파일 이름 예: jpushConfig.json
         * @param content  설정 파일 내용 json
         * @throws Exception
         */
        private void updateConfigFileOnLine(String fileName, String content) throws Exception {
            logger.info("설정 파일 업데이트! 파일: {} , 내용: {}", fileName, content);
            JPushConfigSaveWi wi = new JPushConfigSaveWi();
            wi.setFileName(fileName);
            wi.setFileContent(content);
            ActionResponse response = CipherConnectionAction.post(false,
                    Config.url_x_program_center_jaxrs("config", "save"), wi);
            if (response != null) {
                ActionSave.Wo wo = response.getData(ActionSave.Wo.class);
                if (wo != null && wo.getStatus() != null) {
                    logger.info("[{}] 설정 파일 수정 저장에 성공했습니다!", fileName);
                }
            } else {
                logger.info("[{}] 설정 파일 저장에 실패했습니다, 반환값이 비어 있습니다!", fileName);
            }
        }
    }

    public static class JPushConfigSaveWi extends GsonPropertyObject {
        private String fileName;
        private String fileContent;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileContent() {
            return fileContent;
        }

        public void setFileContent(String fileContent) {
            this.fileContent = fileContent;
        }
    }

    public static class Wi extends GsonPropertyObject {
        @FieldDescribe("app 다운로드 경로.")
        private String apkPath;
        @FieldDescribe("app 패키징 id")
        private String packInfoId;

        // 버전 번호 아직 없음
        @FieldDescribe("app 버전 이름")
        private String appVersionName;
        @FieldDescribe("app 버전 빌드 번호")
        private String appVersionNo;
        @FieldDescribe("외부 패키지명 사용 여부, 2: net.zoneland.x.bpm.mobile.v1.zoneXBPM.outer 를 apk의 applicationId로 사용")
        private String isPackAppIdOuter;

        @FieldDescribe("프런트엔드 web 주소, 예: http://dd.o2oa.net")
        private String webUrl;

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getAppVersionName() {
            return appVersionName;
        }

        public void setAppVersionName(String appVersionName) {
            this.appVersionName = appVersionName;
        }

        public String getAppVersionNo() {
            return appVersionNo;
        }

        public void setAppVersionNo(String appVersionNo) {
            this.appVersionNo = appVersionNo;
        }


        public String getApkPath() {
            return apkPath;
        }

        public void setApkPath(String apkPath) {
            this.apkPath = apkPath;
        }

        public String getPackInfoId() {
            return packInfoId;
        }

        public void setPackInfoId(String packInfoId) {
            this.packInfoId = packInfoId;
        }

        public String getIsPackAppIdOuter() {
            return isPackAppIdOuter;
        }

        public void setIsPackAppIdOuter(String isPackAppIdOuter) {
            this.isPackAppIdOuter = isPackAppIdOuter;
        }
    }

    public static class Wo extends WrapBoolean {

    }
}
