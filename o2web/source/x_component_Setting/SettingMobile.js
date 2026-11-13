MWF.xDesktop.requireApp("Setting", "Document", null, false);
MWF.xApplication.Setting.MobileConnectDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_connectSetting);

        var o2CloudConnected = false;
        this.actions.collectConnected(function (json) {
            o2CloudConnected = json.data.value;
        }.bind(this), function (json) {
            o2CloudConnected = false;
        }.bind(this), false);

        this.mobileO2CloudConnectInput = new MWF.xApplication.Setting.Document.Button(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_connectO2Cloud, "infor": this.mobile_connectO2Cloud_infor, "action": this.lp.mobile_connectO2Cloud_action },
            //"data": {"key": "proxyData", "valueKey": "ssos", "notEmpty": false },
            "value": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "itemTitle": o2CloudConnected ? this.lp.mobile_connectO2Cloud_success : this.lp.mobile_connectO2Cloud_error,
            "icon": o2CloudConnected ? "cloud.png" : "cloud_error.png",
            "action": function (e) {
                layout.desktop.openApplication(e, "Collect");
            }.bind(this)
        });

        this.mobileHttpProtocolInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_httpProtocol, "infor": this.lp.mobile_httpProtocol_infor },
            "data": { "key": "proxyData", "valueKey": "httpProtocol" },
            "value": this.explorer.proxyData.httpProtocol,
            "options": [{ "value": "http", "text": "http" }, { "value": "https", "text": "https" }]
        });

        this.mobileCenterInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_center, "infor": this.lp.mobile_center_infor, "editAction": this.lp.mobile_center_action },
            "data": { "key": "proxyData", "valueKey": "center", "notEmpty": false },
            "value": this.explorer.proxyData.center,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "center.png"
        });

        this.mobileWebInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_web, "infor": this.lp.mobile_web_infor, "editAction": this.lp.mobile_web_action },
            "data": { "key": "proxyData", "valueKey": "web", "notEmpty": false },
            "value": this.explorer.proxyData.web,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "icon": "webserver.png"
        });

        this.mobileApplicationInput = new MWF.xApplication.Setting.Document.List(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_application, "infor": this.lp.mobile_application_infor, "editAction": this.lp.mobile_application_action },
            "data": { "key": "proxyData", "valueKey": "applicationList", "notEmpty": false },
            "value": this.explorer.proxyData.applicationList,
            "itemTitle": "{proxyHost}:{proxyPort}",
            "readonly": ["node"],
            "icon": "server.png"
        });
    }
});

MWF.xApplication.Setting.MobileModuleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_moduleSetting);

        var options = [{ "value": "", "text": this.lp.mobile_index_defalue }];
        if (this.explorer.portalData.portalList && this.explorer.portalData.portalList.length) {
            this.explorer.portalData.portalList.each(function (portal) {
                options.push({
                    "value": portal.id,
                    "text": portal.name
                });
            }.bind(this));
        }

        this.mobileIndexInput = new MWF.xApplication.Setting.Document.Select(this.explorer, this.node, {
            "lp": { "title": this.lp.mobile_index, "infor": this.lp.mobile_index_infor },
            "data": { "key": "nativeData", "valueKey": "indexPortal" },
            "value": this.explorer.nativeData.indexPortal,
            "options": options
        });

        //모바일 간편 모드
        var simpleModeTitle = this.lp.mobile_module_simple_mode;
        var simpleModeInfor = this.lp.mobile_module_simple_mode_infor;
        new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": { "title": simpleModeTitle, "infor": simpleModeInfor },
            "data": { "key": "nativeData", "valueKey": "simpleMode", "notEmpty": false },
            "value": this.explorer.nativeData.simpleMode
        });

        // app 주소록 권한 뷰
        var contactPermissionViewTitle = this.lp.mobile_module_contact_permission_view;
        var contactPermissionViewInfor = this.lp.mobile_module_contact_permission_view_infor;
        new MWF.xApplication.Setting.Document.Input(this.explorer, this.node, {
            "lp": { "title": contactPermissionViewTitle, "infor": contactPermissionViewInfor, "action": this.lp.mobile_module_contact_permission_view_action},
            "data": { "key": "nativeData", "valueKey": "contactPermissionView", "notEmpty": false },
            "value": this.explorer.nativeData.contactPermissionView || "addressPowerView" // 기본 뷰 이름 addressPowerView
        });

        //시스템 알림 표시
        var systemMessageSwitchTitle = this.lp.mobile_module_system_message_switch;
        var systemMessageSwitchInfor = this.lp.mobile_module_system_message_switch_infor;
        new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
            "lp": { "title": systemMessageSwitchTitle, "infor": systemMessageSwitchInfor },
            "data": { "key": "nativeData", "valueKey": "systemMessageSwitch", "notEmpty": false },
            "value": this.explorer.nativeData.systemMessageSwitch
        });

        this.explorer.nativeData.nativeAppList.each(function (app, i) {
            var title = this.lp.mobile_module.replace("{name}", app.name);
            var infor = this.lp.mobile_module_infor.replace("{name}", app.name);
            new MWF.xApplication.Setting.Document.Check(this.explorer, this.node, {
                "lp": { "title": title, "infor": infor },
                "data": { "key": "nativeData", "valueKey": "nativeAppList." + i + ".enable", "notEmpty": false },
                "value": app.enable
            });
        }.bind(this));
    }
});

// app 온라인 패키징 기능
MWF.xApplication.Setting.AppPackOnlineDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        var cssurl = "../x_component_Setting/$Main/default/apppack/index.css";
        var htmlurl = "../x_component_Setting/$Main/default/apppack/index.html";
        this.contentAreaNode.loadAll({ "css": [cssurl], "html": [htmlurl] }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
            this.node = this.apppackBoxNode;
            this.checkConnectAppPackServer();
        }.bind(this));
    },
    // app 패키징 서버 연결 상태 확인, O2 클라우드 로그인 여부 포함
    checkConnectAppPackServer: function () {
        o2.Actions.load("x_program_center").AppPackAction.connect(function(json){
            if (json && json.data) {
                var data = json.data
                if (data.status === 1001) { // 성공 시 token 조회
                    this.token = data.token;
                    this.packServerUrl = data.packServerUrl;
                    this.apppackErrorMsgNode.setStyles({
                        "display": "none"
                    });
                    this.loadAppPackInfo();
                } else if (data.status === 1) { // o2 클라우드 미연결 o2 클라우드 미사용
                    this.showErrorMsg(this.lp.mobile_apppack_message_o2cloud_not_enable);
                    // this.app.notice(this.lp.mobile_apppack_message_o2cloud_not_enable, "error", this.contentAreaNode);
                } else if (data.status === 2) { // o2 클라우드 미로그인
                    // this.app.notice(this.lp.mobile_apppack_message_o2cloud_not_login, "error", this.contentAreaNode);
                    this.showErrorMsg(this.lp.mobile_apppack_message_o2cloud_not_login);
                } else if (data.status === 3) { // 패키징 서버 인증 실패
                    // this.app.notice(this.lp.mobile_apppack_message_apppack_server_login_fail, "error", this.contentAreaNode);
                    this.showErrorMsg(this.lp.mobile_apppack_message_apppack_server_login_fail);
                }
            } else {
                // this.app.notice(this.lp.mobile_apppack_message_check_connect_fail, "error", this.contentAreaNode);
                this.showErrorMsg(this.lp.mobile_apppack_message_check_connect_fail);
            }
        }.bind(this));
    },
    // 최근 패키징 정보 로드 
    loadAppPackInfo: function () {
        this.showLoading();
        o2.Actions.load("x_program_center").AppPackAction.packInfo(this.token, function(json){
            this.hiddenLoading();
            if (json && json.type === "success") {
                this.packInfo = json.data;
                this.showPackInfoDetail();
            } else {
                console.log("패키징 정보 조회에 실패했습니다.")
                this.loadConfigProxy();
            }
        }.bind(this), function(err){
            console.log("오류: 패키징 정보를 찾지 못했습니다!");
            console.log(err);
            this.hiddenLoading();
            this.loadConfigProxy();
        }.bind(this));
    },
    showLoading: function() {
        if (this.packLoadingNode) {
            this.hiddenLoading();
        }
        this.packLoadingNode = new Element("div", { "class": "pack-loading" }).inject(this.apppackBoxNode);
        new Element("div", {"text": "Loading......"}).inject(this.packLoadingNode);
    },
    hiddenLoading: function() {
        if (this.packLoadingNode) {
            this.packLoadingNode.destroy();
            this.packLoadingNode = null;
        }
    },
    // 오류 정보 표시
    showErrorMsg: function(msg) {
        this.apppackErrorMsgNode.set("text", msg);
        this.apppackErrorMsgNode.setStyles({
            "display": ""
        });
    },
    // 제출 양식 표시
    showForm: function() {
        this.apppackShowBodyNode.setStyles({
            "display": "none"
        });
        this.apppackFormBodyNode.setStyles({
            "display": ""
        });
        this.apppackBtnNode.addEvents({
            "click": function(e) {
                this.submitPack();
            }.bind(this)
        })
    },
    // 패키징 상세 표시
    showPackInfoDetail: function () {
        if (this.packInfo) {
            this.apppackAppNameShowNode.set("text", this.packInfo.appName);
            this.apppackProtocolShowNode.set("text", this.packInfo.o2ServerProtocol);
            this.apppackHostShowNode.set("text", this.packInfo.o2ServerHost);
            this.apppackPortShowNode.set("text", this.packInfo.o2ServerPort);
            this.apppackContextShowNode.set("text", this.packInfo.o2ServerContext);
            this.apppackAppVersionNameShowNode.set("text", this.packInfo.versionName);
            this.apppackAppBuildNoShowNode.set("text", this.packInfo.buildNo);
            this.apppackUrlMappingShowNode.set("text", this.packInfo.urlMapping);
            var isPackAppIdOuter = this.lp.mobile_apppack_form_enable_outer_status_no;
            if (this.packInfo.isPackAppIdOuter && this.packInfo.isPackAppIdOuter === "2") {
                isPackAppIdOuter = this.lp.mobile_apppack_form_enable_outer_status_yes;
            }
            this.apppackIsPackAppIdOuterShowNode.set("text", isPackAppIdOuter);
            // var deleteHms = this.lp.mobile_apppack_form_enable_delete_hms_status_no;
            // if (this.packInfo.deleteHuawei && this.packInfo.deleteHuawei === "2") {
            //     deleteHms = this.lp.mobile_apppack_form_enable_delete_hms_status_yes;
            // }
            // this.apppackIsDeleteHmsShowNode.set("text", deleteHms);
            this.apppackLogoShowImgNode.set("src", this.packServerUrl + this.packInfo.appLogoPath + "?token=" + this.token);
            var status = ""
            if (this.packInfo.packStatus === "0") {
                status = this.lp.mobile_apppack_status_order_inline;
                this.apppackStatusRefreshNode.setStyles({
                    "display": ""
                });
                this.apppackStatusRefreshNode.addEvents({
                    "click": function(e) {
                        this.loadAppPackInfo();
                    }.bind(this)
                })
                this.apppackReInputBtnNode.setStyles({
                    "display": "none"
                });
                this.apppackRePackBtnNode.setStyles({"display": "none"})
            } else if (this.packInfo.packStatus === "1") {
                status =  this.lp.mobile_apppack_status_packing
                this.apppackStatusRefreshNode.setStyles({
                    "display": ""
                });
                this.apppackStatusRefreshNode.addEvents({
                    "click": function(e) {
                        this.loadAppPackInfo();
                    }.bind(this)
                })
                this.apppackReInputBtnNode.setStyles({
                    "display": "none"
                });
                this.apppackRePackBtnNode.setStyles({"display": "none"})
            } else if (this.packInfo.packStatus === "2") {
                status = this.lp.mobile_apppack_status_pack_end
                this.apppackStatusRefreshNode.setStyles({
                    "display": "none"
                });
                this.apppackReInputBtnNode.setStyles({
                    "display": ""
                });
                this.apppackReInputBtnNode.addEvents({
                    "click": function(e) {
                        this.reInput();
                    }.bind(this)
                })
                this.apppackRePackBtnNode.setStyles({"display": ""})
                this.apppackRePackBtnNode.addEvents({
                    "click": function(e) {
                        this.reSubmitPack();
                    }.bind(this)
                })
            } else {
                status = this.lp.mobile_apppack_status_pack_error
                this.apppackStatusRefreshNode.setStyles({
                    "display": "none"
                });
                this.apppackReInputBtnNode.setStyles({
                    "display": ""
                });
                this.apppackReInputBtnNode.addEvents({
                    "click": function(e) {
                        this.reInput();
                    }.bind(this)
                })
                this.apppackRePackBtnNode.setStyles({"display": "none"})
            }
            this.apppackStatusShowNode.set("text", status);
            
            if (this.packInfo.apkPath && this.packInfo.packStatus === "2") {
                // 게시 영역 표시
                this.apppackPublishShowNode.setStyles({ "display": ""});
                if (!this.packInfo.appFile) { // 아직 로컬에 게시되지 않음
                    this.apppackPublishBtnNode.setStyles({ "display": ""}); //게시 버튼 표시
                    this.apppackPublishBtnNode.addEvents({
                        "click": function(e) {
                            this.publishAPK2Local();
                        }.bind(this)
                    });
                    this.apppackPublishStatusInfoNode.set("text", "");
                    // 게시 안내 팝업
                    this.confirm(this.lp.alert, this.lp.mobile_apppack_message_confirm_publish, function() {
                        this.publishAPK2Local();
                    }.bind(this));
                } else if (this.packInfo.appFile.status === 0) { // 게시 중
                    this.apppackPublishBtnNode.setStyles({ "display": "none"}); //게시 버튼 숨김

                    this.apppackPublishStatusInfoNode.set("text", this.lp.mobile_apppack_publish_status_doing);
                } else if (this.packInfo.appFile.status === 1) { // 게시 완료
                    this.apppackPublishBtnNode.setStyles({ "display": "none"}); //게시 버튼 숨김

                    this.apppackPublishStatusInfoNode.set("text",  this.lp.mobile_apppack_publish_status_completed);
                     // 게시 완료, 다운로드 버튼 표시
                    var url = o2.Actions.getHost("x_program_center") + "/x_program_center/jaxrs/apppack/pack/info/file/download/" + this.packInfo.appFile.id ;
                    url = o2.filterUrl(url);
                    this.apppackDownloadLinkNode.set("href", url);
                    this.apppackDownloadShowNode.setStyles({
                        "display": ""
                    });
                } else if (this.packInfo.appFile.status === 2) { // 게시 실패
                    this.apppackPublishBtnNode.setStyles({ "display": ""}); //게시 버튼 표시

                    this.apppackPublishStatusInfoNode.set("text", this.lp.mobile_apppack_publish_status_fail);
                    this.apppackPublishBtnNode.addEvents({
                        "click": function(e) {
                            this.publishAPK2Local();
                        }.bind(this)
                    });
                } else {
                    this.apppackPublishStatusInfoNode.set("text", "");
                }
            } else {
                this.apppackDownloadShowNode.setStyles({
                    "display": "none"
                });
            }
            this.apppackShowBodyNode.setStyles({
                "display": ""
            });
            this.apppackFormBodyNode.setStyles({
                "display": "none"
            });
            
        }
    },
    // 패키징된 apk를 로컬에 게시
    publishAPK2Local: function() {
        if (this.packInfo) {
            var url = o2.Actions.getHost("") 
            var data = {
                'token': this.token,
                'apkPath': this.packInfo.apkPath,
                'packInfoId': this.packInfo.id,
                'appVersionName': this.packInfo.versionName,
                'appVersionNo': this.packInfo.buildNo,
                'isPackAppIdOuter': this.packInfo.isPackAppIdOuter,
                'webUrl': url,
            }
            this.showLoading();
            o2.Actions.load("x_program_center").AppPackAction.publishApk(data, function(json){
                this.hiddenLoading();
                if (json.data && json.data.value) {
                    // 제출 성공 시 최신 패키징 객체 정보 조회
                    this.loadAppPackInfo();
                }else {
                    this.app.notice(json.message, "error", this.contentAreaNode);
                }
            }.bind(this), function (error) {
                this.hiddenLoading();
                console.log(error);
                this.app.notice(error, "error", this.contentAreaNode);
            }.bind(this));
        } else {
            console.log("패키징 정보가 없어 배포할 수 없습니다");
        }
    },
    // 다시 입력 양식 제출
    reInput: function () {
        // 내용 채우기
        if (this.packInfo) {
            this.apppackProtocolInputNode.set("value", this.packInfo.o2ServerProtocol);
            this.apppackHostInputNode.set("value", this.packInfo.o2ServerHost);
            this.apppackPortInputNode.set("value", this.packInfo.o2ServerPort);
            this.apppackContextInputNode.set("value", this.packInfo.o2ServerContext);
            this.apppackAppNameInputNode.set("value", this.packInfo.appName);
            this.apppackAppVersionNameInputNode.set("value", ""); // 다시 작성 버전 번호 초기화
            this.apppackAppBuildNoInputNode.set("value", ""); // 다시 작성 버전 번호 초기화
            this.apppackUrlMappingInputNode.set("value", this.packInfo.urlMapping);
            var isPackAppIdOuter = false;
            if (this.packInfo.isPackAppIdOuter && this.packInfo.isPackAppIdOuter === "2") {
                isPackAppIdOuter = true;
            }
            this.apppackIsPackAppIdOuterInputNode.set("checked", isPackAppIdOuter);
            // var deleteHms = false;
            // if (this.packInfo.deleteHuawei && this.packInfo.deleteHuawei === "2") {
            //     deleteHms = true;
            // }
            // this.apppackIsDeleteHmsInputNode.set("checked", deleteHms);
        }
        this.showForm();
    },
    // 로컬에 설정된 proxy 주소 조회
    loadConfigProxy: function () {
        o2.Actions.load("x_program_center").ConfigAction.getProxy(function(json){
            this.hiddenLoading();
            // 채우기
            if (json && json.data) {
                var data = json.data
                if (data.httpProtocol) {
                    this.apppackProtocolInputNode.set("value", data.httpProtocol);
                } else {
                    this.apppackProtocolInputNode.set("value", "http");
                }
                if (data.center) {
                    if (data.center.proxyHost) {
                        this.apppackHostInputNode.set("value", data.center.proxyHost);
                    }
                    if (data.center.proxyPort) {
                        this.apppackPortInputNode.set("value", data.center.proxyPort);
                    } else {
                        this.apppackPortInputNode.set("value", "20030");
                    }
                } else {
                    this.apppackPortInputNode.set("value", "20030");
                }
            }
            this.apppackContextInputNode.set("value", "/x_program_center");
            this.showForm();
        }.bind(this))
    },
    // 기존 자료로 바로 다시 패키징
    reSubmitPack: function() {
        this.confirm(this.lp.alert, this.lp.mobile_apppack_message_alert_submit, function() {
            this.showLoading();
            o2.Actions.load("x_program_center").AppPackAction.androidPackReStart(this.token, function (json) {
                console.log(json)
                this.hiddenLoading();
                if (json.data && json.data.value) {
                    // 제출 성공 시 최신 패키징 객체 정보 조회
                    this.loadAppPackInfo();
                } else {
                    this.app.notice(json.message, "error", this.contentAreaNode);
                }
            }.bind(this), function (error) {
                this.hiddenLoading();
                console.log(error);
                this.app.notice(error, "error", this.contentAreaNode);
            }.bind(this));
        }.bind(this));
    },
    // 패키징 제출
    submitPack: function () {
       
        var appName = this.apppackAppNameInputNode.get("value");
        if (!appName || appName === "") {
            this.app.notice(this.lp.mobile_apppack_message_appname_not_empty, "error", this.contentAreaNode);
            return;
        }
        if (appName.length > 6) {
            this.app.notice(this.lp.mobile_apppack_message_appname_len_max_6, "error", this.contentAreaNode);
            return;
        }
        var files = this.apppackLogoInputNode.files;
        if (files.length) {
            var file = files.item(0);
            var fileExt = file.name.substring(file.name.lastIndexOf("."));
            if (fileExt.toLowerCase() !== ".png") {
                this.app.notice(this.lp.mobile_apppack_message_app_logo_need_png, "error", this.contentAreaNode);
                return;
            }
        } else {
            this.app.notice(this.lp.mobile_apppack_message_app_logo_not_empty, "error", this.contentAreaNode);
            return;
        }
        var protocol = this.apppackProtocolInputNode.get("value");
        if (!protocol || protocol === "") {
            this.app.notice(this.lp.mobile_apppack_message_portocol_not_empty, "error", this.contentAreaNode);
            return;
        }
        if (protocol !== "http" && protocol !== "https") {
            this.app.notice(this.lp.mobile_apppack_message_portocol_http_https, "error", this.contentAreaNode);
            return;
        }
        var host = this.apppackHostInputNode.get("value");
        if (!host || host === "") {
            this.app.notice(this.lp.mobile_apppack_message_host_not_empty, "error", this.contentAreaNode);
            return;
        }
        if (host.startsWith("http://") || host.startsWith("https://")) {
            this.app.notice(this.lp.mobile_apppack_message_host_format_error, "error", this.contentAreaNode);
            return;
        }
        if (host.indexOf(":") > 0) {
            this.app.notice(this.lp.mobile_apppack_message_host_format_error, "error", this.contentAreaNode);
            return;
        }

        var port = this.apppackPortInputNode.get("value");
        if (!port || port === "") {
            this.app.notice(this.lp.mobile_apppack_message_port_not_empty, "error", this.contentAreaNode);
            return;
        }
        var context = this.apppackContextInputNode.get("value");
        if (!context || context === "") {
            this.app.notice(this.lp.mobile_apppack_message_context_not_empty, "error", this.contentAreaNode);
            return;
        }
        var appVersionName = this.apppackAppVersionNameInputNode.get("value");
        var appBuildNo = this.apppackAppBuildNoInputNode.get("value");
        var isPackAppIdOuter = this.apppackIsPackAppIdOuterInputNode.checked ? "2" : "1";
        // var deleteHms = this.apppackIsDeleteHmsInputNode.checked ? "2" : "1";
        var urlMapping = this.apppackUrlMappingInputNode.get("value");
        this.confirm(this.lp.alert, this.lp.mobile_apppack_message_alert_submit, function() {
            this.showLoading();
            var formData = new FormData();
            formData.append('file', file);
            formData.append('fileName', file.name);
            formData.append('appName', appName);
            formData.append('o2ServerProtocol', protocol);
            formData.append('o2ServerHost', host);
            formData.append('o2ServerPort', port);
            formData.append('o2ServerContext', context);
            formData.append('appVersionName', appVersionName);
            formData.append('appBuildNo', appBuildNo);
            formData.append('isPackAppIdOuter', isPackAppIdOuter);
            formData.append('deleteHuawei', "1");
            formData.append('urlMapping', urlMapping);
            formData.append('token', this.token);
            
            o2.Actions.load("x_program_center").AppPackAction.androidPackStart(formData, "{}",function (json) {
                console.log(json)
                this.hiddenLoading();
                if (json.data && json.data.value) {
                    // 제출 성공 시 최신 패키징 객체 정보 조회
                    this.loadAppPackInfo();
                } else {
                    this.app.notice(json.message, "error", this.contentAreaNode);
                }
            }.bind(this), function (error) {
                this.hiddenLoading();
                console.log(error);
                this.app.notice(error, "error", this.contentAreaNode);
            }.bind(this));
        }.bind(this));
        
    },
    //확인
    confirm: function (title, text, okCallback) {
        var width = 600;
        var height = 110;
        var size = this.app.content.getSize();
        var x = (size.x - width) / 2;
        var y = (size.y - height) / 2;
        MWF.require("MWF.xDesktop.Dialog", function () {
            var dlg = new MWF.xDesktop.Dialog({
                "title": title,
                "style": "settingStyle",
                "top": y,
                "left": x,
                "width": width,
                "height": height,
                "text": text,
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function () {
                            if (okCallback) { okCallback(); }
                            this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function () { this.close(); }
                    }
                ]
            });
            dlg.show();
        }.bind(this));
    }
});


// 위챗 메뉴
MWF.xApplication.Setting.MPWeixinMenuSettingDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        var cssurl = "../x_component_Setting/$Main/default/mpweixin/mpweixin.css";
        var htmlurl = "../x_component_Setting/$Main/default/mpweixin/menu.html";
        this.contentAreaNode.loadAll({ "css": [cssurl], "html": [htmlurl] }, { "bind": { "lp": this.lp, "data": {} }, "module": this }, function () {
            this.node = this.mpwxMenuNode;
            this.initMenuData();
            this.loadMenu();
            this.loadPublishBtn();
            this.subscribeEvent();
        }.bind(this));
    },
    initMenuData: function() {
         //데이터 처리
        // var list = this.explorer.mpweixinListData;
        if (!this.menuList) {
            this.menuList = this.explorer.mpweixinListData || [];
        }
        //subscribe 제거
        var subscribe = null;
        for (let index = 0; index < this.menuList.length; index++) {
            const element = this.menuList[index];
            if (element.type && element.type === "subscribe") {
                subscribe = element;
            }
        }
        if (subscribe) {
            this.menuList.erase(subscribe);
        }
    },
    //메뉴 게시 클릭 이벤트
    loadPublishBtn: function() {
        this.mpwxPublishBtnNode.addEvents({
            "click": function(){
                if (this.menuList && this.menuList.length > 0) {
                    this.confirm(this.lp.alert, this.lp.mobile_mpweixin_menu_msg_publish_2_wxmp, function () {
                        o2.Actions.load("x_program_center").MPWeixinAction.menuCreate2Weixin(function (json) {
                            this.app.notice(this.lp.mobile_mpweixin_menu_msg_publish_success, "success", this.contentAreaNode);
                        }.bind(this));
                    }.bind(this));
                }
            }.bind(this)
        });
    },
    // 팔로우 응답 메시지
    subscribeEvent: function() {
        if (this.explorer.mpweixinSubscribe) {
            this.mpweixinSubscribe = this.explorer.mpweixinSubscribe;
            if (this.mpweixinSubscribe.name && this.mpweixinSubscribe.name !== "") { //이미 존재하므로 name은 반드시 비어 있지 않음
                this.mpwxMenuSubscribeInputNode.set("value", this.mpweixinSubscribe.content || "");
            } else { // 새 항목 데이터 없음
                this.mpweixinSubscribe = {
                    id: "mock_new",
                    name: "subscribe",
                    type: "subscribe"
                };
            }
        } else {
            this.mpweixinSubscribe = {
                id: "mock_new",
                name: "subscribe",
                type: "subscribe"
            };
        }
        //저장 이벤트
        this.mpwxSaveSubscribeBtnNode.addEvents({
            "click": function(){
                this.saveSubscribeMenu();
            }.bind(this)
        });
    },
    saveSubscribeMenu: function() {
        var content = this.mpwxMenuSubscribeInputNode.get("value");
        if (!content || content === "") {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_subscribe_content_error_empty, "error", this.contentAreaNode);
            return;
        }
        this.mpweixinSubscribe.content = content;
        //새로 만든 메뉴 click 유형은 key 설정 필요 
        if (this.mpweixinSubscribe.id.startsWith('mock_')) {
            delete this.mpweixinSubscribe.id; //삭제할 id 추가
        }
        //데이터 기록 원격 기록 및 상위 배열에도 기록
        if (!this.mpweixinSubscribe.id) { //신규
            o2.Actions.load("x_program_center").MPWeixinAction.menuAdd(this.mpweixinSubscribe,function (json) {
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.contentAreaNode);
            }.bind(this));
        }else { // 수정
            o2.Actions.load("x_program_center").MPWeixinAction.menuUpdate(this.mpweixinSubscribe.id, this.mpweixinSubscribe,function (json) {
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.contentAreaNode);
            }.bind(this));
        }
    },
    loadMenu: function () {
        this.mpwxMenuListNode.empty();
        // 메뉴 생성
        if (this.menuList && this.menuList.length > 0) {
            if (!this.showSubId) {
                this.showSubId = this.menuList[0].id;//showSubId 초기화: 표시 중인 하위 메뉴를 의미
            }
            if (!this.currentMenuId) {
                this.currentMenuId = this.menuList[0].id;//currentMenuId 초기화: 현재 선택된 메뉴 ID를 의미
            }
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                this.firstLevelMenu(element);
            }
        }
        this.firstLevelAddButton();
    },
    //첫 번째 레벨 새로 만들기 버튼
    firstLevelAddButton: function () {
        var addLi = new Element("li", { "class": "js_addMenuBox pre_menu_item grid_item no_extra" }).inject(this.mpwxMenuListNode);
        new Element("a", { "class": "pre_menu_link js_addL1Btn", "href": "javascript:void(0);" }).inject(addLi);
        new Element("i", { "class": "icon14_menu_add" }).inject(addLi);
        //클릭 이벤트
        addLi.addEvents({
            "click": function () {
                this.createNewFirstMenuMock();
            }.bind(this)
        });
    },
    // 첫 번째 레벨 메뉴 UI
    // <li class="jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3  current selected" id="menu_0">
    //     <a href="javascript:void(0);" class="pre_menu_link" draggable="false">
    //     <i class="icon_menu_dot js_icon_menu_dot dn" style="display: none;"></i>
    //     <i class="icon20_common sort_gray"></i>
    //     <span class="js_l1Title">메뉴 이름</span>
    // </a>
    // <div class="sub_pre_menu_box js_l2TitleBox" >
    //     <ul class="sub_pre_menu_list">
    //     <li id="1618290787255_subMenu_menu_0_0" class="jslevel2">
    //     <a href="javascript:void(0);" class="jsSubView" draggable="false">
    //         <span class="sub_pre_menu_inner js_sub_pre_menu_inner">
    //             <i class="icon20_common sort_gray"></i>
    //             <span class="js_l2Title">하위 메뉴 이름</span>
    //         </span>
    //     </a>
    // </li>
    //     <li id="1618290787255_subMenu_menu_0_0" class="jslevel2 current"><a href="javascript:void(0);" class="jsSubView" draggable="false"><span class="sub_pre_menu_inner js_sub_pre_menu_inner"><i class="icon20_common sort_gray"></i><span class="js_l2Title">하위 메뉴 이름</span></span></a></li>
    //     <li class="js_addMenuBox">
    //     <a href="javascript:void(0);" class="jsSubView js_addL2Btn" title="하위 메뉴는 최대 5개까지 추가 가능" draggable="false">
    //     <span class="sub_pre_menu_inner js_sub_pre_menu_inner">
    //     <i class="icon14_menu_add"></i></span></a>
    // </li>
    //     </ul>
    //     <i class="arrow arrow_out"></i>
    //     <i class="arrow arrow_in"></i>
    // </div>
    // </li>
    firstLevelMenu: function (menu) {
        var sub_button = menu.sub_button;
        var menuClass = "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3";
        if (this.currentMenuId && this.currentMenuId === menu.id) {
            menuClass = "jsMenu pre_menu_item grid_item jslevel1 ui-sortable ui-sortable-disabled size1of3 current";
        }
        var menuLi = new Element("li", { "class": menuClass }).inject(this.mpwxMenuListNode);
        var menuA = new Element("a", { "class": "pre_menu_link", "href": "javascript:void(0);" }).inject(menuLi);
        new Element("span", { "class": "js_l1Title", "text": menu.name }).inject(menuA);
        menuLi.addEvents({
            "click": function () {
                menu.o2Level = "1"
                this.clickMenu(menu.id, menu);
            }.bind(this)
        });
        //하위 메뉴
        var subDiv = new Element("div", { "class": "sub_pre_menu_box js_l2TitleBox" }).inject(menuLi);
        var subUl = new Element("ul", { "class": "sub_pre_menu_list" }).inject(subDiv);
        if (!sub_button || sub_button.length < 5) {
            this.secondLevelAddButton(menu, subUl); //추가 버튼을 위에 배치
        }
        if (sub_button && sub_button.length > 0) {
            // 하위 메뉴는 역순
            sub_button.sort(function (a, b) {
                var x = a.order.toLowerCase();
                var y = b.order.toLowerCase();
                if (x < y) { return 1; }
                if (x > y) { return -1; }
                return 0;
            })
            for (let index = 0; index < sub_button.length; index++) {
                const element = sub_button[index];
                this.secondLevelMenu(element, subUl, menu);
            }
        }
        new Element("i", { "class": "arrow arrow_out" }).inject(subDiv);
        new Element("i", { "class": "arrow arrow_in" }).inject(subDiv);
        if (this.showSubId && this.showSubId === menu.id) {
            subDiv.setStyles({
                "display": ""
            });
        } else {
            subDiv.setStyles({
                "display": "none"
            });
        }

    },
    // 하위 메뉴 새로 만들기 버튼
    secondLevelAddButton: function (parentMenu, parentNode) {
        var addLi = new Element("li", { "class": "js_addMenuBox" }).inject(parentNode);
        var addA = new Element("a", { "class": "jsSubView js_addL2Btn", "href": "javascript:void(0);" }).inject(addLi);
        var span = new Element("span", { "class": "sub_pre_menu_inner js_sub_pre_menu_inner" }).inject(addA);
        new Element("i", { "class": "icon14_menu_add" }).inject(span);
        addLi.addEvents({
            "click": function (ev) {
                console.log("새 하위 메뉴 추가 클릭, 상위 메뉴: " + parentMenu.name);
                this.createNewSubMenuMock(parentMenu);
                ev.stopPropagation()
            }.bind(this)
        });
    },
    // 하위 메뉴 UI
    secondLevelMenu: function (menu, parentNode, parentMenu) {
        var menuClass = "jslevel2";
        if (this.currentMenuId && this.currentMenuId === menu.id) {
            menuClass = "jslevel2 current";
        }
        var menuLi = new Element("li", { "class": menuClass }).inject(parentNode);
        var menuA = new Element("a", { "class": "jsSubView", "href": "javascript:void(0);" }).inject(menuLi);
        var firstSapn = new Element("span", { "class": "sub_pre_menu_inner js_sub_pre_menu_inner" }).inject(menuA);
        new Element("i", { "class": "icon20_common sort_gray" }).inject(firstSapn);
        new Element("span", { "class": "js_l2Title", "text": menu.name }).inject(firstSapn);
        menuLi.addEvents({
            "click": function (ev) {
                menu.o2Level = "2"
                this.clickMenu(parentMenu.id, menu);
                ev.stopPropagation();
            }.bind(this)
        });
    },
    //오른쪽 영역 로드
    menuRightBoxCreate: function (menu) {
        this.mpwxMenuFormHeaderNode.empty();
        // 헤더
        new Element("h4", { "class": "global_info", "text": menu.name }).inject(this.mpwxMenuFormHeaderNode);
        var deleteNode = new Element("div", { "class": "global_extra" }).inject(this.mpwxMenuFormHeaderNode);
        var deleteA = new Element("a", { "href": "javascript:void(0);", "text": this.lp.mobile_mpweixin_menu_deleteBtnName_label }).inject(deleteNode);
        deleteA.addEvents({
            "click": function (ev) {
                this.deleteAction(menu);
                
                ev.stopPropagation();
            }.bind(this)
        })

        // 양식 페이지
        new MWF.xApplication.Setting.MPWeixinMenuSettingDocument.MenuForm(this, this.mpwxMenuFormNode, menu);
    },
    //메뉴 클릭 시 실행
    clickMenu: function (showSubId, menu) {
        this.showSubId = showSubId; //showSubId: 표시 중인 하위 메뉴를 의미
        this.currentMenuId = menu.id;//currentMenuId: 현재 선택된 메뉴 ID를 의미
        this.menuRightBoxCreate(menu);
        this.loadMenu();
    },
    //하위 메뉴 생성
    createNewSubMenuMock: function (parent) {
        let sub = parent.sub_button;
        //최대 5개까지 생성 가능
        if (sub && sub.length >= 5 ) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_sub_max_len, "error", this.contentAreaNode);
            return;
        }
        if (parent.id.startsWith('mock_')) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_parent_no_save, "error", this.contentAreaNode);
            return;
        }
        // 정렬 번호 생성 
        let order = "000001";
        if (sub && sub.length > 0) {
            let bigestOrder = sub[0].order;//첫 번째 항목 위에서 이미 정렬했으므로 첫 번째 항목이 가장 큼
            const intOrder = parseInt(bigestOrder);
            if (!isNaN(intOrder)) {
                let o = intOrder + 1;
                let os = '' + o;
                for (let len = os.length; len < 6; len = os.length) {
                    os = "0" + os;
                }
                order = os;
            }
        }
        // id가 mock_로 시작 정식 저장 시 신규임을 알 수 있어 제거하고 백엔드에서 id 생성
        let body = {
            "id": "mock_" + o2.uuid(),
            "name": this.lp.mobile_mpweixin_menu_default_new_name,
            "parentId": parent.id,
            "type": "click", //기본 텍스트 메시지
            "order": order,
            "o2Level": "2" // 2 두 번째 레벨 메뉴
        };
        if (sub) {
            parent.sub_button.push(body);
        } else {
            parent.sub_button = [body];
        }
        for (let index = 0; index < this.menuList.length; index++) {
            const element = this.menuList[index];
            if (element.id === parent.id) {
                this.menuList[index] = parent;
            }
        }
        this.clickMenu(parent.id, body);
    },
    // 첫 번째 레벨 메뉴 생성
    createNewFirstMenuMock: function() {
        //최대 3개까지 생성 가능
        if (this.menuList && this.menuList.length >=3 ) {
            this.app.notice(this.lp.mobile_mpweixin_menu_msg_first_max_len, "error", this.contentAreaNode);
            return;
        }
        // 정렬 번호 생성 
        let order = "000001";
        if (this.menuList && this.menuList.length > 0) {
            let bigestOrder = this.menuList[this.menuList.length-1].order;//마지막 항목이 가장 큰 번호
            console.log("가장 큰 order " + bigestOrder);
            const intOrder = parseInt(bigestOrder);
            if (!isNaN(intOrder)) {
                let o = intOrder + 1;
                let os = '' + o;
                for (let len = os.length; len < 6; len = os.length) {
                    os = "0" + os;
                }
                order = os;
            }
            console.log("가장 큰 order " + intOrder);
        }
        // id가 mock_로 시작 정식 저장 시 신규임을 알 수 있어 제거하고 백엔드에서 id 생성
        let body = {
            "id": "mock_" + o2.uuid(),
            "name": this.lp.mobile_mpweixin_menu_default_new_name,
            "type": "click", //기본 텍스트 메시지
            "order": order,
            "o2Level": "1" // 1 첫 번째 레벨 메뉴
        };
        if (this.menuList) {
            this.menuList.push(body);
        } else {
            this.menuList = [body];
        }
        this.clickMenu(body.id, body);
    },
    // list 안의 데이터 객체 갱신
    setMenuData(menu) {
        if (this.menuList && this.menuList.length > 0) {
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                if (element.id === menu.id) {
                    this.menuList[index] = menu;
                    break;
                }
                var flag = false;
                if (element.sub_button && element.sub_button.length > 0) {
                    for (let i = 0; i < element.sub_button.length; i++) {
                        const child = element.sub_button[i];
                        if (child.id === menu.id) {
                            flag = true;
                            element.sub_button[i] = menu;
                        }
                    }
                }
                if (flag) {
                    this.menuList[index] = element;
                    break;
                }
            }
            this.loadMenu();
        }
    },
    // 데이터 삭제
    deleteAction: function(menu) {
        if (menu.id) {
            if (menu.id.startsWith("mock_")) {
                console.log("DB에 저장되지 않은 데이터이므로 바로 삭제합니다.");
                this.removeDataUI(menu);
            }else {
                this.confirm(this.lp.alert, this.lp.mobile_mpweixin_menu_delete_alert_msg, function () {
                    o2.Actions.load("x_program_center").MPWeixinAction.menuDelete(menu.id,function (json) {
                        //데이터 새로 고침
                        this.removeDataUI(menu);
                        this.app.notice(this.lp.mobile_mpweixin_menu_delete_success, "success", this.contentAreaNode);
                    }.bind(this));
                }.bind(this));
            }
        }
    },
    // 데이터 삭제 후 ui 갱신
    removeDataUI: function(menu) {
        this.mpwxMenuFormHeaderNode.empty();
        this.mpwxMenuFormNode.empty();
        this.showSubId = null;
        this.currentMenuId = null;
        if (this.menuList && this.menuList.length > 0) {
            for (let index = 0; index < this.menuList.length; index++) {
                const element = this.menuList[index];
                if (element.id === menu.id) {
                    this.menuList.erase(menu);
                    console.log("삭제 완료(상위 메뉴)")
                    break;
                }
                var flag = false;
                if (element.sub_button && element.sub_button.length > 0) {
                    for (let i = 0; i < element.sub_button.length; i++) {
                        const child = element.sub_button[i];
                        if (child.id === menu.id) {
                            flag = true;
                            element.sub_button.erase(menu);
                            console.log("삭제 완료(하위 메뉴)")
                        }
                    }
                }
                if (flag) {
                    this.menuList[index] = element;
                    break;
                }
            }
            this.loadMenu();
        }         
    },
    //확인
    confirm: function (title, text, okCallback) {
        var width = 600;
        var height = 110;
        var size = this.app.content.getSize();
        var x = (size.x - width) / 2;
        var y = (size.y - height) / 2;
        MWF.require("MWF.xDesktop.Dialog", function () {
            var dlg = new MWF.xDesktop.Dialog({
                "title": title,
                "style": "settingStyle",
                "top": y,
                "left": x,
                "width": width,
                "height": height,
                "text": text,
                "maskNode": this.app.content,
                "container": this.app.content,
                "buttonList": [
                    {
                        "text": this.lp.ok,
                        "action": function () {
                            if (okCallback) { okCallback(); }
                            this.close();
                        }
                    },
                    {
                        "text": this.lp.cancel,
                        "action": function () { this.close(); }
                    }
                ]
            });
            dlg.show();
        }.bind(this));
    }
});

//메뉴 양식 기능
MWF.xApplication.Setting.MPWeixinMenuSettingDocument.MenuForm = new Class({
    Implements: [Options, Events],
    initialize: function (menuApp, contentAreaNode, menu, options) {
        this.setOptions(options);
        this.menuApp = menuApp;
        this.explorer = menuApp.explorer;
        this.app = this.explorer.app;
        this.lp = this.app.lp;
        this.contentAreaNode = contentAreaNode;
        this.actions = this.app.actions;
        this.css = this.app.css;
        this.menu = menu;
        this.load();
    },
    load: function () {
        this.contentAreaNode.empty();
        this.nameInputCreate();
        this.orderInputCreate();
        if (this.menu.sub_button && this.menu.sub_button.length > 0) { //상위 메뉴 하위 메뉴가 있으면 유형 편집 불필요
            this.haSubMenus = true;
        } else {
            this.haSubMenus = false;
            this.typeRadioCreate();
        }
        this.saveButtonCreate();
    },
    //메뉴 이름
    nameInputCreate: function () {
        this.nameInputBoxNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.nameInputBoxNode);
        new Element("strong", { "class": "title js_menuTitle", "text": this.lp.mobile_mpweixin_menu_form_name_label }).inject(labelNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(this.nameInputBoxNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.nameInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.name }).inject(spanNode);
        //이벤트?
        var nameTips = this.lp.mobile_mpweixin_menu_form_name_tips_4
        if (this.menu.o2Level && this.menu.o2Level === "1"){
            nameTips = this.lp.mobile_mpweixin_menu_form_name_tips_4 
        } else {
            nameTips = this.lp.mobile_mpweixin_menu_form_name_tips_6
        }
        this.nameInputTipsNode = new Element("p", { "class": "frm_tips js_titleNolTips", "text": nameTips }).inject(inputNode);
        this.nameInputErrorTipsNode = new Element("p", { "class": "frm_msg fail js_titleEorTips dn", "text": this.lp.mobile_mpweixin_menu_form_name_error }).inject(inputNode);
    },
    //메뉴 정렬 번호
    orderInputCreate: function () {
        this.orderInputBoxNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.orderInputBoxNode);
        new Element("strong", { "class": "title js_menuTitle", "text": this.lp.mobile_mpweixin_menu_form_order_label }).inject(labelNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(this.orderInputBoxNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.orderInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.order }).inject(spanNode);
        //이벤트?
        this.orderInputTipsNode = new Element("p", { "class": "frm_tips js_titleNolTips", "text": this.lp.mobile_mpweixin_menu_form_order_tips }).inject(inputNode);
        this.orderInputErrorTipsNode = new Element("p", { "class": "frm_msg fail js_titleEorTips dn", "text": this.lp.mobile_mpweixin_menu_form_order_error }).inject(inputNode);
    },
    //메뉴 유형 
    typeRadioCreate: function () {
        this.typeRadioBoxNode = new Element("div", { "class": "frm_control_group" }).inject(this.contentAreaNode);
        var labelNode = new Element("label", { "class": "frm_label" }).inject(this.typeRadioBoxNode);
        new Element("strong", { "class": "title js_menuContent", "text": this.lp.mobile_mpweixin_menu_form_radio_label }).inject(labelNode);
        var groupNode = new Element("div", { "class": "frm_controls frm_vertical_pt" }).inject(this.typeRadioBoxNode);
        //메시지 유형
        this.msgTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.msgTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_msg }).inject(this.msgTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.msgTypeRadioBoxNode);
        this.msgTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("click")
            }.bind(this)
        });
        //웹 페이지 유형
        this.urlTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.urlTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_url }).inject(this.urlTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.urlTypeRadioBoxNode);
        this.urlTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("view")
            }.bind(this)
        });
        //미니 프로그램
        this.miniprogramTypeRadioBoxNode = new Element("label", { "class": "frm_radio_label js_radio_sendMsg" }).inject(groupNode);
        new Element("i", { "class": "icon_radio" }).inject(this.miniprogramTypeRadioBoxNode);
        new Element("span", { "class": "lbl_content", "text": this.lp.mobile_mpweixin_menu_form_radio_type_miniprogram }).inject(this.miniprogramTypeRadioBoxNode);
        new Element("input", { "class": "frm_radio", "type": "radio" }).inject(this.miniprogramTypeRadioBoxNode);
        this.miniprogramTypeRadioBoxNode.addEvents({
            "click": function () {
                this.changeMenuType("miniprogram")
            }.bind(this)
        });
        // 3개 유형의 구체적 내용
        this.typeContentCreate();

        this.changeMenuType(this.menu.type)
    },
    //저장 버튼
    saveButtonCreate: function () {
        //<span id="pubBt" class="btn btn_input btn_primary"><button>저장 및 게시</button></span>
        var buttonDiv = new Element("div", { "class": "btn-box" }).inject(this.contentAreaNode);
        this.submitButtonNode = new Element("span", { "class": "btn btn_input btn_primary" }).inject(buttonDiv);
        new Element("button", { "text": this.lp.save }).inject(this.submitButtonNode);
        this.submitButtonNode.addEvents({
            "click": function () {
                this.submitAction();
            }.bind(this)
        })
    },
    typeContentCreate: function () {
        var outContentNode = new Element("div", { "class": "menu_content_container" }).inject(this.contentAreaNode);
        this.msgContentCreate(outContentNode);
        this.urlContentCreate(outContentNode);
        this.miniprogramContentCreate(outContentNode);
    },
    //텍스트 메시지
    msgContentCreate: function (outContentNode) {
        this.msgTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_msg_tips }).inject(this.msgTypeContentNode);
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.msgTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_msg_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_textarea_box with_counter counter_in append" }).inject(inputNode);
        this.msgTypeContentInputNode = new Element("textarea", { "class": "frm_textarea js_menu_name", "value": this.menu.content }).inject(spanNode);
    },
    // 웹 링크
    urlContentCreate: function (outContentNode) {
        this.urlTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_url_tips }).inject(this.urlTypeContentNode);
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.urlTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_url_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.urlTypeContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.url }).inject(spanNode);
    },
    // 미니 프로그램
    miniprogramContentCreate: function (outContentNode) {
        this.miniprogramTypeContentNode = new Element("div", { "class": "menu_content dn" }).inject(outContentNode);
        new Element("p", { "class": "menu_content_tips tips_global js_url_tips", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_tips }).inject(this.miniprogramTypeContentNode);
        // 미니 프로그램 id
        var inputOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_label }).inject(inputOutNode);
        var inputNode = new Element("div", { "class": "frm_controls" }).inject(inputOutNode);
        var spanNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputNode);
        this.miniprogramTypeAppIdContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.appid , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_placeholder }).inject(spanNode);

        // 미니 프로그램 경로
        var inputPathOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
        new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_label }).inject(inputPathOutNode);
        var inputPathNode = new Element("div", { "class": "frm_controls" }).inject(inputPathOutNode);
        var spanPathNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputPathNode);
        this.miniprogramTypePathContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.pagepath , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_placeholder }).inject(spanPathNode);
        // 예비 웹 페이지
         var inputUrlOutNode = new Element("div", { "class": "frm_control_group js_setNameBox" }).inject(this.miniprogramTypeContentNode);
         new Element("label", { "class": "frm_label", "text": this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_label }).inject(inputUrlOutNode);
         var inputUrlNode = new Element("div", { "class": "frm_controls" }).inject(inputUrlOutNode);
         var spanUrlNode = new Element("span", { "class": "frm_input_box with_counter counter_in append" }).inject(inputUrlNode);
         this.miniprogramTypeUrlContentInputNode = new Element("input", { "class": "frm_input js_menu_name", "type": "text", "value": this.menu.url , "placeholder": this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_placeholder }).inject(spanUrlNode);
    },
    changeMenuType: function (type) {
        if (type) {
            this.menu.type = type;
            if (type === "view") {
                this.msgTypeRadioBoxNode.removeClass("selected");
                this.msgTypeContentNode.addClass("dn");
                this.urlTypeRadioBoxNode.addClass("selected");
                this.urlTypeContentNode.removeClass("dn");
                this.miniprogramTypeRadioBoxNode.removeClass("selected");
                this.miniprogramTypeContentNode.addClass("dn");
            } else if (type === "miniprogram") {
                this.msgTypeRadioBoxNode.removeClass("selected");
                this.msgTypeContentNode.addClass("dn");
                this.urlTypeRadioBoxNode.removeClass("selected");
                this.urlTypeContentNode.addClass("dn");
                this.miniprogramTypeRadioBoxNode.addClass("selected");
                this.miniprogramTypeContentNode.removeClass("dn");
            } else {
                this.msgTypeRadioBoxNode.addClass("selected");
                this.msgTypeContentNode.removeClass("dn");
                this.urlTypeRadioBoxNode.removeClass("selected");
                this.urlTypeContentNode.addClass("dn");
                this.miniprogramTypeRadioBoxNode.removeClass("selected");
                this.miniprogramTypeContentNode.addClass("dn");
            }
        } else {
            this.msgTypeRadioBoxNode.addClass("selected");
            this.msgTypeContentNode.removeClass("dn");
            this.urlTypeRadioBoxNode.removeClass("selected");
            this.urlTypeContentNode.addClass("dn");
            this.miniprogramTypeRadioBoxNode.removeClass("selected");
            this.miniprogramTypeContentNode.addClass("dn");
        }
    },
    //메뉴 데이터 제출
    submitAction: function() {
        var name = this.nameInputNode.get("value");
        if (!name || name === "") {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_name_error_empty, "error", this.menuApp.contentAreaNode);
            return;
        }
        if (this.menu.o2Level && this.menu.o2Level === "1") {
            if (name.length > 4) {
                this.app.notice(this.lp.mobile_mpweixin_menu_form_name_error_max_len_4, "error", this.menuApp.contentAreaNode);
                return;
            }
        } else {
            if (name.length > 6) {
                this.app.notice(this.lp.mobile_mpweixin_menu_form_name_error_max_len_6, "error", this.menuApp.contentAreaNode);
                return;
            }
        }
        var order = this.orderInputNode.get("value");
        if (!order || order === "") {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_empty, "error", this.menuApp.contentAreaNode);
            return;
        }
        //숫자
        var reg = /^[\d]+$/;
        var s = reg.test(order);
        if (s === false) {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_not_number, "error", this.menuApp.contentAreaNode);
            return;
        }
        if (order.length > 6) {
            this.app.notice(this.lp.mobile_mpweixin_menu_form_order_error_max_len, "error", this.menuApp.contentAreaNode);
            return;
        }
        this.menu.name = name;
        this.menu.order = order;
        //상위 메뉴 name과 order만 필요
        if (this.haSubMenus && this.haSubMenus === true) {
            console.log("하위 메뉴가 있습니다");
        }else {
            var type = this.menu.type;
            if (type === "view") { //웹 페이지
                var url = this.urlTypeContentInputNode.get("value");
                if (!url || url === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_url_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.url = url;
                //새로 만든 메뉴의 삭제 id
                if (this.menu.id.startsWith('mock_')) {
                    delete this.menu.id; //삭제할 id 추가
                }
            } else if (type === "miniprogram") { //미니 프로그램
                var appid = this.miniprogramTypeAppIdContentInputNode.get("value");
                if (!appid || appid === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_appid_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                var path = this.miniprogramTypePathContentInputNode.get("value");
                if (!path || path === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_path_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                var url = this.miniprogramTypeUrlContentInputNode.get("value");
                if (!url || url === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_miniprogram_url_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.appid = appid;
                this.menu.pagepath = path;
                this.menu.url = url;
                //새로 만든 메뉴의 삭제 id
                if (this.menu.id.startsWith('mock_')) {
                    delete this.menu.id; //삭제할 id 추가
                }
            } else { //메시지
                var content = this.msgTypeContentInputNode.get("value");
                if (!content || content === "") {
                    this.app.notice(this.lp.mobile_mpweixin_menu_form_type_msg_error_empty, "error", this.menuApp.contentAreaNode);
                    return;
                }
                this.menu.content = content;
                //새로 만든 메뉴 click 유형은 key 설정 필요 
                if (this.menu.id.startsWith('mock_')) {
                    this.menu.key = this.menu.id;
                    delete this.menu.id; //삭제할 id 추가
                }
            }
        }
        //데이터 기록 원격 기록 및 상위 배열에도 기록
        if (!this.menu.id) { //신규
            o2.Actions.load("x_program_center").MPWeixinAction.menuAdd(this.menu,function (json) {
                this.menu.id = json.data.id;//id 갱신
                this.menuApp.setMenuData(this.menu);
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.menuApp.contentAreaNode);
            }.bind(this));
        }else { // 수정
            o2.Actions.load("x_program_center").MPWeixinAction.menuUpdate(this.menu.id, this.menu,function (json) {
                this.menuApp.setMenuData(this.menu);
                this.app.notice(this.lp.mobile_mpweixin_menu_save_success, "success", this.menuApp.contentAreaNode);
            }.bind(this));
        }
    },



});

MWF.xApplication.Setting.MobileStyleDocument = new Class({
    Extends: MWF.xApplication.Setting.Document,
    load: function () {
        this.node = new Element("div", { "styles": { "overflow": "hidden", "padding-bottom": "80px" } }).inject(this.contentAreaNode);
        this.titleName = new Element("div", { "styles": this.explorer.css.explorerContentTitleNode }).inject(this.node);
        this.titleName.set("text", this.lp.mobile_styleSetting);

        this.explorer.imagesData.images.each(function (img, i) {
            var imgName = this.lp.mobile_style_imgs[img.name];
            var title = this.lp.mobile_style.replace("{name}", imgName);
            var infor = this.lp.mobile_style_infor.replace("{name}", imgName);

            new MWF.xApplication.Setting.Document.Image(this.explorer, this.node, {
                "lp": { "title": title, "infor": "" },
                "data": { "key": "imagesData", "valueKey": "applicationList", "notEmpty": false },
                "value": img,
                "itemTitle": infor,
                "iconData": img.value
            });
        }.bind(this));
    }
});