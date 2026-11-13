layout = window.layout || {};
var locate = window.location;
layout.protocol = locate.protocol;
var href = locate.href;
if (href.indexOf("debugger") != -1) layout.debugger = true;
layout.desktop = layout;
layout.session = layout.session || {};
o2.addReady(function () {

        MWF.loadLP(MWF.language);

        MWF.require("MWF.xDesktop.Layout", function () {
            MWF.require("MWF.xDesktop.Authentication", null, false);
            MWF.require("MWF.xDesktop.Common", null, false);

            (function () {
                layout.load = function () {
                    var uri = href.toURI();
                    var mode = uri.getData("mode");
                    var code = uri.getData("code");
                    var redirect = uri.getData("redirect");
                    console.log("code = "+ code)
                    console.log("redirect = "+ redirect)
                    MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                        var action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_authentication", "");
                        action.getActions = function (actionCallback) {
                            this.actions = { "sso": { "uri": "/jaxrs/qiyeweixin/code/{code}", "method": "GET" },
                                            "userdetail" : {"uri": "/jaxrs/qiyeweixin/update/person/detail/{code}", "method": "GET" } };
                            if (actionCallback) actionCallback();
                        };
                        if (mode && mode === "snsapi_privateinfo") { // 获取用户详细信息，更新到Person
                            console.log("사용자 상세 정보를 가져와 Person에 업데이트")
                            action.invoke({
                                "name": "userdetail", "async": true, "parameter": { "code":  code}, "success":  function(json) {
                                // 完成认证 关闭页面
                                if (redirect) {
                                    history.replaceState(null, "page", redirect);
                                    window.location.replace(redirect);
                                    if (redirect.includes("#/")) {
                                        window.location.reload();
                                    }
                                } else {
                                    console.error("WeCom js가 로드되지 않았습니다!!!");
                                    document.id("layout").set("html", "<div>업데이트했습니다. 이제 페이지를 닫아도 됩니다!</div>");
                                }
                            }.bind(this), "failure": function (xhr, text, error) {
                                    console.log("WeCom SSO 오류 11111")
                                    document.id("layout").set("html", "<div>WeCom SSO 로그인 중 오류가 발생했습니다!</div>")
                                }.bind(this)
                            });
                        } else { // 默认的单点登录
                            console.log("기본 SSO 로그인")
                            action.invoke({
                                "name": "sso", "async": true, "parameter": { "code":  code}, "success": function (json) {
                                    console.log("인증 성공")
                                    if (redirect) {
                                        console.log("---------------------redirect 주소로 이동 준비")
                                        history.replaceState(null, "page", redirect);
                                        window.location.replace(redirect);
                                        if (redirect.includes("#/")) {
                                            window.location.reload();
                                        }
                                    } else {
                                        history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                                        "appMobile.html?app=process.TaskCenter".toURI().go();
                                    }
                                }.bind(this), "failure": function (xhr, text, error) {
                                    console.log("WeCom SSO 오류 222222")
                                    document.id("layout").set("html", "<div>WeCom SSO 로그인 중 오류가 발생했습니다!</div>")
                                }.bind(this)
                            });
                        }
                    });
                    
                };


                MWF.getJSON("res/config/config.json", function (config) {
                    if (config.proxyCenterEnable){
                        if (o2.typeOf(config.center)==="array"){
                            config.center.forEach(function(c){
                                c.port = window.location.port || 80;
                            });
                        }else{
                            config.port = window.location.port || 80;
                        }
                    }
                    layout.config = config;
                    MWF.xDesktop.getServiceAddress(layout.config, function (service, center) {
                        layout.serviceAddressList = service;
                        layout.centerServer = center;
                        layout.load();
                    }.bind(this));

                });

            })();

        });
});
