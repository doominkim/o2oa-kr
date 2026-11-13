package com.x.base.core.project.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

public class Components extends ConfigObject {

    private static final long serialVersionUID = -6261924434387247937L;

    public static final String NAME_SYSTEMCONFIG = "systemconfig";
    public static final String NAME_ORG = "Org";
    public static final String NAME_CMSMANAGER = "cmsManager";
    public static final String NAME_APPLICATIONEXPLORER = "ApplicationExplorer";
    public static final String NAME_PORTALEXPLORER = "PortalExplorer";
    public static final String NAME_DATAEXPLORER = "DataExplorer";
    public static final String NAME_SERVICEMANAGER = "service.ServiceManager";
    public static final String NAME_APPSTORE = "appstore";
    public static final String NAME_APPCENTER = "AppCenter";
    public static final String NAME_LOGVIEWER = "LogViewer";
    public static final String NAME_PROFILE = "Profile";
    public static final String NAME_BAM = "BAM";
    public static final String NAME_CMS = "cms";
    public static final String NAME_WORKCENTER = "WorkCenter";
    public static final String NAME_HOMEPAGE = "Homepage";
    public static final String NAME_HOTARTICLE = "HotArticle";
    public static final String NAME_MEETING = "Meeting";
    public static final String NAME_ATTENDANCE = "attendancev2";
    public static final String NAME_FORUM = "Forum";
    public static final String NAME_MINDER = "Minder";
    public static final String NAME_CALENDAR = "Calendar";
    public static final String NAME_FTSEARCH = "ftsearch";
    public static final String NAME_IM = "IMV2";
    public static final String NAME_AI = "AI";

    public static final List<String> SYSTEM_NAME_NAMES = ListTools.toList(NAME_SYSTEMCONFIG, NAME_ORG, NAME_CMSMANAGER,
            NAME_APPLICATIONEXPLORER, NAME_PORTALEXPLORER, NAME_DATAEXPLORER, NAME_SERVICEMANAGER, NAME_APPSTORE,
            NAME_APPCENTER, NAME_LOGVIEWER, NAME_PROFILE, NAME_BAM, NAME_CMS, NAME_WORKCENTER, NAME_HOMEPAGE,
            NAME_HOTARTICLE, NAME_MEETING, NAME_ATTENDANCE, NAME_FORUM, NAME_MINDER, NAME_CALENDAR,
            NAME_FTSEARCH, NAME_IM, NAME_AI);

    public static final String APPICON_PNG = "appicon.png";

    public static Component systemComponent(String name) {
        switch (name) {
            case NAME_SYSTEMCONFIG:
                return new Component(NAME_SYSTEMCONFIG, NAME_SYSTEMCONFIG, "시스템 설정", APPICON_PNG, 1,
                        Component.TYPE_SYSTEM);
            case NAME_ORG:
                return new Component(NAME_ORG, NAME_ORG, "조직 관리", APPICON_PNG, 2, Component.TYPE_SYSTEM);
            case NAME_CMSMANAGER:
                return new Component(NAME_CMSMANAGER, "cms.Column", "게시판 설정", APPICON_PNG, 3, Component.TYPE_SYSTEM);
            case NAME_APPLICATIONEXPLORER:
                return new Component(NAME_APPLICATIONEXPLORER, "process.ApplicationExplorer", "프로세스 관리", APPICON_PNG, 4,
                        Component.TYPE_SYSTEM);
            case NAME_PORTALEXPLORER:
                return new Component(NAME_PORTALEXPLORER, "portal.PortalExplorer", "포털 관리", APPICON_PNG, 5,
                        Component.TYPE_SYSTEM);
            case NAME_DATAEXPLORER:
                return new Component(NAME_DATAEXPLORER, "query.QueryExplorer", "데이터 센터", APPICON_PNG, 6,
                        Component.TYPE_SYSTEM);
            case NAME_SERVICEMANAGER:
                return new Component(NAME_SERVICEMANAGER, NAME_SERVICEMANAGER, "서비스 관리", APPICON_PNG, 7,
                        Component.TYPE_SYSTEM);
            case NAME_APPSTORE:
                return new Component(NAME_APPSTORE, NAME_APPSTORE, "앱 마켓", APPICON_PNG, 8, Component.TYPE_SYSTEM);
            case NAME_APPCENTER:
                return new Component(NAME_APPCENTER, NAME_APPCENTER, "앱 관리", APPICON_PNG, 9, Component.TYPE_SYSTEM);
            case NAME_LOGVIEWER:
                return new Component(NAME_LOGVIEWER, NAME_LOGVIEWER, "로그", APPICON_PNG, 10, Component.TYPE_SYSTEM);
            case NAME_PROFILE:
                return new Component(NAME_PROFILE, NAME_PROFILE, "개인 설정", APPICON_PNG, 11, Component.TYPE_SYSTEM);
            case NAME_BAM:
                return new Component(NAME_BAM, NAME_BAM, "프로세스 모니터링", APPICON_PNG, 12, Component.TYPE_SYSTEM);
            case NAME_CMS:
                return new Component(NAME_CMS, "cms.Index", "게시판 홈", APPICON_PNG, 12, Component.TYPE_SYSTEM);
            case NAME_WORKCENTER:
                return new Component(NAME_WORKCENTER, "process.workcenter", "업무함", APPICON_PNG, 13,
                        Component.TYPE_SYSTEM);
            case NAME_HOMEPAGE:
                return new Component(NAME_HOMEPAGE, NAME_HOMEPAGE, "홈", APPICON_PNG, 14, Component.TYPE_SYSTEM);
            case NAME_HOTARTICLE:
                return new Component(NAME_HOTARTICLE, NAME_HOTARTICLE, "핫이슈", APPICON_PNG, 15, Component.TYPE_SYSTEM);
            case NAME_MEETING:
                return new Component(NAME_MEETING, NAME_MEETING, "회의 관리", APPICON_PNG, 18, Component.TYPE_SYSTEM);
            case NAME_ATTENDANCE:
                return new Component(NAME_ATTENDANCE, NAME_ATTENDANCE, "근태 관리", APPICON_PNG, 20, Component.TYPE_SYSTEM);
            case NAME_FORUM:
                return new Component(NAME_FORUM, NAME_FORUM, "포럼", APPICON_PNG, 21, Component.TYPE_SYSTEM);
            case NAME_MINDER:
                return new Component(NAME_MINDER, NAME_MINDER, "마인드맵 편집기", APPICON_PNG, 22, Component.TYPE_SYSTEM);
            case NAME_CALENDAR:
                return new Component(NAME_CALENDAR, NAME_CALENDAR, "일정", APPICON_PNG, 23, Component.TYPE_SYSTEM);
            case NAME_FTSEARCH:
                return new Component(NAME_FTSEARCH, NAME_FTSEARCH, "검색", APPICON_PNG, 25, Component.TYPE_SYSTEM);
            case NAME_IM:
                return new Component(NAME_IM, NAME_IM, "메신저", APPICON_PNG, 26, Component.TYPE_SYSTEM);
            case NAME_AI:
                return new Component(NAME_AI, NAME_AI, "AI 비서", APPICON_PNG, 27, Component.TYPE_SYSTEM);
            default:
                return null;
        }
    }

    public Components() {
        this.systems.add(systemComponent(NAME_SYSTEMCONFIG));
        this.systems.add(systemComponent(NAME_ORG));
        this.systems.add(systemComponent(NAME_CMSMANAGER));
        this.systems.add(systemComponent(NAME_CMS));
        this.systems.add(systemComponent(NAME_APPLICATIONEXPLORER));
        this.systems.add(systemComponent(NAME_PORTALEXPLORER));
        this.systems.add(systemComponent(NAME_DATAEXPLORER));
        this.systems.add(systemComponent(NAME_SERVICEMANAGER));
        this.systems.add(systemComponent(NAME_APPSTORE));
        this.systems.add(systemComponent(NAME_APPCENTER));
        this.systems.add(systemComponent(NAME_LOGVIEWER));
        this.systems.add(systemComponent(NAME_PROFILE));
        this.systems.add(systemComponent(NAME_BAM));
        this.systems.add(systemComponent(NAME_WORKCENTER));
        this.systems.add(systemComponent(NAME_HOMEPAGE));
        this.systems.add(systemComponent(NAME_HOTARTICLE));
        this.systems.add(systemComponent(NAME_MEETING));
        this.systems.add(systemComponent(NAME_ATTENDANCE));
        this.systems.add(systemComponent(NAME_FORUM));
        this.systems.add(systemComponent(NAME_MINDER));
        this.systems.add(systemComponent(NAME_CALENDAR));
        this.systems.add(systemComponent(NAME_FTSEARCH));
        this.systems.add(systemComponent(NAME_IM));
        this.systems.add(systemComponent(NAME_AI));
    }

    public static Components defaultInstance() {
        return new Components();
    }

    @FieldDescribe("기본 모듈")
    private List<Component> systems = new ArrayList<>();

    public void save() throws Exception {
        File file = new File(Config.base(), Config.PATH_CONFIG_PORTAL);
        FileUtils.write(file, XGsonBuilder.toJson(this), DefaultCharset.charset);
    }

    public static class Component {

        public static final String TYPE_SYSTEM = "system";
        public static final String TYPE_CUSTOM = "custom";

        public Component() {

        }

        public Component(String name, String path, String title, String iconPath, Integer orderNumber, String type) {
            this.name = name;
            this.path = path;
            this.title = title;
            this.iconPath = iconPath;
            this.orderNumber = orderNumber;
            this.type = type;
        }

        @FieldDescribe("이름")
        private String name;

        @FieldDescribe("경로")
        private String path;

        @FieldDescribe("제목")
        private String title;

        @FieldDescribe("iconPath")
        private String iconPath;

        @FieldDescribe("정렬 번호")
        private Integer orderNumber;

        @FieldDescribe("유형")
        private String type;

        @FieldDescribe("허용 목록, person과 role 혼용 가능")
        private List<String> allowList = new ArrayList<>();

        @FieldDescribe("금지 목록, person과 role 혼용 가능")
        private List<String> dentyList = new ArrayList<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIconPath() {
            return iconPath;
        }

        public void setIconPath(String iconPath) {
            this.iconPath = iconPath;
        }

        public List<String> getAllowList() {
            return allowList;
        }

        public void setAllowList(List<String> allowList) {
            this.allowList = allowList;
        }

        public List<String> getDentyList() {
            return dentyList;
        }

        public void setDentyList(List<String> dentyList) {
            this.dentyList = dentyList;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

    }

    public List<Component> getSystems() {
        return systems;
    }

    public void setSystems(List<Component> systems) {
        this.systems = systems;
    }

}
