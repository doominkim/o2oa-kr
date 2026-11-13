o2.addReady(function () {
    // ip 여부
    var _isIp = function(ip) {
        var reg = /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/
        return reg.test(ip);
    };
    //x-token 지원 수정
    var uri = new URI(window.location.href);
    var options = uri.get("data");
    if (options[o2.tokenName]) {
        // 삭제
        Cookie.dispose(o2.tokenName);
        // 쓰기
        var host = window.location.host; // 도메인 
        var domain = null;
        if (_isIp(host)) {
            domain = host;
        }else {
            if (host.indexOf(".") > 0) {
                domain = host.substring(host.indexOf(".")); // 상위 도메인 예: .o2oa.net
            }
        }
        if (domain) {
            Cookie.write(o2.tokenName, options[o2.tokenName], {domain: domain, path:"/"});
        }else {
            Cookie.write(o2.tokenName, options[o2.tokenName]);
        }
    }
    layout.load = function () {
        this.options = uri.get("data");
        if (!this.options.documentId) this.options.documentId = this.options.id
        this.options.name = "cms.Document";
        this.loadDocument(this.options);

        var appNames = "cms.Document";
        layout.openApplication(null, appName, this.options, null);
    };

    if (layout.session && layout.session.user){
        _load();
    }else{
        if (layout.sessionPromise){
            layout.sessionPromise.then(function(){
                _load();
            },function(){});
        }
    }
});
