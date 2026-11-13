
/**
 * 위챗 공식계정 SSO 로그인
 * 현재 이 페이지에는 계정 연동과 단일 로그인 두 가지 기능이 있음
 */
layout.addReady(function(){
    (function(layout){
        layout.mobile = true;
        var href = locate.href;
        if (href.indexOf("debugger") != -1) layout.debugger = true;
 
        var _bindWeixin2User = function (redirect) {
            var uri = href.toURI();
            var code = uri.getData("code"); //WeChat code
            if (code) {
                o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.bindWithCode(code, function (json) {
                    //연동되었습니다
                    // var box = new Element("div", { "style": "text-align: center;" }).inject($("appContent"));
                    // new Element("h2", { "text": "연동되었습니다!" }).inject(box);
                    // o2.api.page.notice("연동되었습니다!", "success");
                    _redirectTo(redirect);
                }, function (err) {
                    console.error(err);
                })
            } else {
                console.error('WeChat code가 전달되지 않아 연동할 수 없습니다');
            }
        };
        var _redirectTo = function (redirect) {
            if (redirect) {
                history.replaceState(null, "page", redirect);
                redirect.toURI().go();
            } else {
                history.replaceState(null, "page", "../x_desktop/appMobile.html?app=process.TaskCenter");
                "appMobile.html?app=process.TaskCenter".toURI().go();
            }
        };
        
        ///////////////////////////
        var uri = href.toURI();
        var redirect = uri.getData("redirect"); //로그인 성공 후 이동할 주소
        var code = uri.getData("code"); //WeChat code
        console.log("code：" + code)
        var type = uri.getData("type"); // bind는 연동
        console.log("type：" + type)
        if (type && type === "bind") { // 연동하려면 로그인 필요
            if (layout.session && layout.session.user){
                _bindWeixin2User(redirect);
            }else{
                if (layout.sessionPromise){
                    layout.sessionPromise.then(function(){
                        _bindWeixin2User(redirect);
                    },function(){});
                }
            }
        } else { //code는 단일 로그인
            o2.Actions.load("x_organization_assemble_authentication").MPweixinAction.loginWithCode(code, function (json) {
                layout.session.user = json.data;
                _redirectTo(redirect);
            }, function (err) {
                console.error(err)
            });
        }

    })(layout);
});