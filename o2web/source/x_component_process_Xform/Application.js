MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Application 포털에 삽입된 시스템 component 객체 또는 웹 페이지 iframe(모듈 배포에 설정된 웹 페이지 URL).
 * @o2cn 삽입된 시스템 앱
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var application = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var application = this.target; //컴포넌트 자체의 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Application = MWF.APPApplication =  new Class(
    /** @lends MWF.xApplication.process.Xform.Application# */
    {
    Extends: MWF.APP$Module,
    options: {
        /**
         * component 객체 초기화 후 로드 전에 발생, this.event로 component 객체를 가져올 수 있음.
         * @event MWF.xApplication.process.Xform.Application#queryLoadApplication
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "queryLoadApplication"]
    },

    _loadUserInterface: function(){
        /**
         * @ignore
         * @member parentLine
         * @memberOf MWF.xApplication.process.Xform.Application#
         */

        /**
         * @ignore
         * @member getSource
         * @memberOf MWF.xApplication.process.Xform.Application#
         */
        this.node.empty();
        if( this.json.activeType !== "delay" ){
            if( !o2.api ){
                MWF.require("MWF.framework", function () {
                    this.loadApplication();
                }.bind(this));
            }else{
                this.loadApplication();
            }
        }
    },
    /**
     * @summary 컴포넌트가 지연 활성화로 설정된 경우 active 메서드로 활성화
     * @param {Function} callback 활성화 후 콜백 메서드(컴포넌트 로드 완료는 보장하지 않음), 이미 활성화된 경우에도 이 메서드는 실행됨.
     * @example
     * var app = this.form.get("fieldId");
     * app.active(function(){
     *     //do someting
     * })
     */
    active: function(callback){
        if (!this.loaded) {
            this.reload(callback);
        } else {
            if (callback) callback();
        }
    },
    /**
     * @summary 삽입된 객체 다시 로드
     * @param {Function} callback 다시 로드 후 콜백
     * @example
     * this.form.get("fieldId").reload()
     */
    reload: function(callback){
        this.clean();
        this.loadApplication( callback );
    },
    /**
     * @summary 현재 삽입된 객체 지우기
     * @example
     * this.form.get("fieldId").clean()
     */
    clean: function(){
        if(this.component){
            try{
                this.component.close();
                if(this.node)this.node.empty();
            }catch (e) {
                console.log(e);
                if(this.node)this.node.empty();
            }
            this.component = null;
        }
        if( this.iframe ){
            this.iframe.destroy();
            if(this.node)this.node.empty();
            this.iframe = null;
        }
    },
    loadApplication: function ( callback ) {
        this.clean();
        if(this.node){
            this.node.empty();
            this.node.setStyles({
                "position": "relative"
            });
            if( !this.json.styles || (!this.json.styles["background"] && !this.json.styles["background-color"] )){
                this.node.setStyle("background-color", "#eee");
            }
        }
        var status = this.getComponentStatus() || null;
        var options = this.getComponentOptions() || {};
        this.getComponentPath(function (componentPath) {
            if( componentPath && componentPath.indexOf("@url:") === 0 ){
                this.loadIframe( componentPath.substring(5, componentPath.length ), callback );
            }else{
                this.loadComponent( componentPath, status, options, callback );
            }
        }.bind(this))
    },
    /**
     * @summary Iframe 로드
     * @param {String} src iframe의 src, 예: 'https://www.baidu.com/'
     * @example
     * this.form.get("fieldId").clean(); //현재 삽입된 객체 지우기
     * this.form.get("fieldId").loadIframe('https://www.baidu.com/'); //iframe 로드
     */
    loadIframe: function( src, callback ){
        var attr = {
            "src": src,
            "width": "100%",
            "height": "100%",
            "frameborder": "0px",
            "scrolling": "auto",
            "seamless": "seamless"
        };

        /**
         * @summary 모듈 배포 설정이 @url:로 시작하는 링크일 때 삽입되는 iframe.
         * @member {Object}
         * @example
         * var iframe = this.form.get("fieldId").iframe; //iframe 가져오기
         * iframe.src; //iframe 주소 가져오기
         */
        this.iframe = new Element("iframe", attr).inject( this.node );
        this.loaded = true;
        if(callback)callback();
    },
    /**
     * @summary 시스템 컴포넌트 로드
     * @param {String} path 컴포넌트 경로, 예: 'Calendar'
     * @param {Object|null} [status] 컴포넌트 상태
     * @param {Object|null} [options] 컴포넌트 옵션
     * @param {Function} [callback] 로드 후 콜백
     * @example
     * this.form.get("fieldId").clean(); //현재 삽입된 객체 지우기
     * this.form.get("fieldId").loadComponent('Calendar'); //일정 로드
     * @example
     * this.form.get("fieldId").clean(); //현재 삽입된 객체 지우기
     * this.form.get("fieldId").loadComponent('cms.Module', {
     *  "columnId":"25434995-45d2-4c9a-a344-55ad0deff071"
     *  }); //id가 25434995-45d2-4c9a-a344-55ad0deff071인 콘텐츠 관리 게시판 로드
     */
    loadComponent: function ( path, status, options, callback ) {
        var clazz = MWF.xApplication;
        if( o2.typeOf(path) !== "string" )return;
        path.split(".").each(function (a) {
            clazz[a] = clazz[a] || {};
            clazz = clazz[a];
        });
        clazz.options = clazz.options || {};

        var _load = function () {
            if( clazz.Main ){
                var opt = options || {};
                // var stt = status || {};
                opt.embededParent = this.node;

                /**
                 * @summary 삽입된 component 객체.
                 * @member {Object}
                 * @example
                 * var app = this.form.get("fieldId").component; //component 객체 가져오기
                 * app.recordStatus(); //앱의 현재 상태 가져오기
                 * app.refresh();      //앱 새로 고침
                 * app.dialog(option); //대화 상자 표시(MWF.widget.Dialog 참고)
                 * app.notice(content, type, target, where, offset); //알림 메시지 표시
                 * app.confirm(type, e, title, text, width, height, ok, cancel); //확인 창 표시
                 * app.alert(type, e, title, text, width, height); //정보 창 표시
                 * app.addEvent(type, fun); //앱에 이벤트 바인딩
                 */
                this.component = new clazz.Main(this.form.app.desktop, opt);
                this.component.status = status;
                this.fireEvent("queryLoadApplication", this.component);
                this.component.load();
                this.component.setEventTarget(this.form.app);
                var _self = this;
                this.component.refresh = function () {
                    if( layout.inBrowser ){
                        window.location.reload();
                    }else{
                        _self.form.app.refresh();
                    }
                };
            }else{
                if( MWF.xApplication.process && MWF.xApplication.process.Xform && MWF.xApplication.process.Xform.LP ){
                    this.form.app.notice(MWF.xApplication.process.Xform.LP.applicationNotFound+":"+path, "error");
                }else{
                    this.form.app.notice(this.form.app.lp.applicationNotFound+":"+path, "error");
                }
            }
            this.loaded = true;
            if(callback)callback();
        }.bind(this);

        try{
            MWF.xDesktop.requireApp(path, "lp."+o2.language, null, false);
            MWF.xDesktop.requireApp(path, "Main", null, false);
            if (clazz.loading && clazz.loading.then){
                clazz.loading.then(function(){
                    _load();
                });
            }else{
                _load();
            }
        }catch (e) {
            this.form.app.notice( e.message, "error" );
        }
    },
    /**
     * @summary 양식 디자인에 설정된 component 객체의 경로 가져오기
     * @param {Function} callback 경로를 가져온 후의 콜백 메서드, 인자는 경로
     * @example
     * this.form.get("fieldId").getComponentPath(function(path){
     *     //path는 경로
     * })
     */
    getComponentPath: function(callback){
        var path;
        if (this.json.componentType==="script"){
            if (this.json.componentScript && this.json.componentScript.code){
                path = this.form.Macro.exec(this.json.componentScript.code, this);
            }
        }else{
            if (this.json.componentSelected && this.json.componentSelected!=="none"){
                path = this.json.componentSelected;
            }else{
                path = "";
            }
        }
        Promise.resolve(path).then(function (p) {
            callback(p || "");
        })
    },
    /**
     * @summary 양식 디자인에 설정된 component 객체의 인자 가져오기
     * @return 설정된 인자
     * @example
     * var param = this.form.get("fieldId").getComponentOptions()
     */
    getComponentOptions : function(){
        var params = "";
        if( this.json.optionsType === "map" ){
            params = this.json.optionsMapList;
        }else if( this.json.optionsType === "script" ){
            var code = (this.json.optionsScript) ? this.json.optionsScript.code : "";
            if (code){
                params = this.form.Macro.exec(code, this);
            }
        }
        return params;
    },
    /**
     * @summary 양식 디자인에 설정된 component 객체의 상태 가져오기
     * @return 설정된 상태
     * @example
     * var param = this.form.get("fieldId").getComponentStatus()
     */
    getComponentStatus: function(){
        var params = "";
        if( this.json.statusType === "map" ){
            params = this.json.statusMapList;
        }else if( this.json.statusType === "script" ){
            var code = (this.json.statusScript) ? this.json.statusScript.code : "";
            if (code){
                params = this.form.Macro.exec(code, this);
            }
        }
        return params;
    }
});
