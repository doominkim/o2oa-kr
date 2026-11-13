MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
/** @class View 뷰 컴포넌트.
 * @o2cn 뷰
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var view = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var view = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.View = MWF.APPView =  new Class(
    /** @lends MWF.xApplication.process.Xform.View# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 뷰 인자(options)가 준비되었고 아직 뷰를 로드하지 않았을 때 실행. this.event로 뷰 인자를 가져올 수 있으며, this.event를 수정해 뷰 로드를 변경할 수 있음.
         * @event MWF.xApplication.process.Xform.View#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰 설계를 가져왔고 컨테이너도 준비됨. this.event로 뷰 파라미터를 얻고 this.event를 수정해 뷰 로드를 변경할 수 있음.
         * @event MWF.xApplication.process.Xform.View#loadViewLayout
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰를 비동기로 로드한 후 실행.
         * @event MWF.xApplication.process.Xform.View#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰에서 기록 하나를 선택한 후 실행.
         * @event MWF.xApplication.process.Xform.View#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰에서 기록 하나의 선택을 해제한 후 실행.
         * @since V8.0
         * @event MWF.xApplication.process.Xform.View#unselect
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰에서 기록 하나를 연 후 실행.
         * @event MWF.xApplication.process.Xform.View#openDocument, this.event로 열린 문서 파라미터를 얻을 수 있음
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadViewLayout", "loadView", "queryLoad", "postLoad", "select", "unselect", "openDocument"]
    },

    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);
        this.node.empty();
    },
    _afterLoaded: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        if(!!this.json.viewType){
            this.loadView();
        }else if (this.json.queryView){
            this.loadView();
        }else{
            if (this.json.selectViewType==="cms"){
                this.loadCMSView();
            }else if (this.json.selectViewType==="process"){
                this.loadPrcessView();
            }else{
                this.loadView();
            }
        }
    },
    /**
     * @summary 뷰 다시 로드
     * @param callback {Function} 새로 고침 후 콜백.
     * @example
     * this.form.get("fieldId").reload()
     */
    reload: function( callback ){
        if (this.view){
            if (this.view.loadViewRes && this.view.loadViewRes.res) if (this.view.loadViewRes.res.isRunning()) this.view.loadViewRes.res.cancel();
            if (this.view.getViewRes && this.view.getViewRes.res) if (this.view.getViewRes.res.isRunning()) this.view.getViewRes.res.cancel();
        }
        this.node.empty();
        this._loadReadEditAbeld();
        this.loadView( callback, true );
    },
    /**
     * @summary 뷰가 지연 로드(즉시 로드 안 함)로 설정된 경우 active 메서드로 활성화
     * @example
     * this.form.get("fieldId").active()
     */
    active: function( callback ){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        if (this.view){
            if (!this.view.loadingAreaNode) this.view.loadView( callback );
        }else{
            this.loadView( callback, true );
        }
    },
    getViewName: function (){
        var appName, viewName;
        if (this.json.viewType === "script") {
            if (this.json.viewScript && this.json.viewScript.code) {
                var data = this.form.Macro.exec(this.json.viewScript.code, this);
                if (data) {
                    appName = data.application;
                    viewName = data.view;
                }
            }
        }else{
            appName = (this.json.queryView) ? this.json.queryView.appName : this.json.application;
            viewName = (this.json.queryView) ? this.json.queryView.name : this.json.viewName;
        }
        return {appName: appName, viewName: viewName};
    },
    loadView: function( callback, force ){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        var viewObj = this.getViewName();
        var appName = viewObj.appName, viewName = viewObj.viewName;
        if( !appName || !viewName ){
            if(callback) callback();
            return ;
        }

        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }

        //var data = JSON.parse(this.json.data);
        var viewJson = {
            "application": appName,
            "viewName": viewName,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter,
            "defaultSelectedScript" : this.json.defaultSelectedScript ? this.json.defaultSelectedScript.code : null,
            "selectedAbleScript" : this.json.selectedAbleScript ? this.json.selectedAbleScript.code : null
        };

        this.fireEvent("beforeLoadView", [viewJson]);

        //MWF.xDesktop.requireApp("query.Query", "Viewer", function(){

        /**
         * @summary view 컴포넌트, 플랫폼이 이 컴포넌트로 뷰 기능을 구현
         * @member {MWF.xApplication.query.Query.Viewer}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var view = this.form.get("fieldId").view; //컴포넌트 객체 가져오기
         */
            this.view = new MWF.xApplication.query.Query.Viewer(this.node, viewJson, {
                "isload": force || (this.json.loadView!=="no"),
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onLoadLayout": function () {
                    this.fireEvent("loadViewLayout");
                }.bind(this),
                "onLoadView": function(){
                    this.fireEvent("loadView");
                    if(callback)callback();
                }.bind(this),
                "onSelect": function(item){
                    this.fireEvent("select", [item]);
                }.bind(this),
                "onUnselect": function(item){
                    this.fireEvent("unselect", [item]);
                }.bind(this),
                "onOpenDocument": function(options, item){
                    this.openOptions = {
                        "options": options,
                        "item": item
                    };
                    this.fireEvent("openDocument", [this.openOptions]);
                    this.openOptions = null;
                }.bind(this)
            }, this.form.app, this.form.Macro);
        //}.bind(this));
    },

    loadPrcessView: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }
        var viewJson = {
            "application": this.json.processView.application,
            "viewName": this.json.processView.name,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter
        };
        MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
            this.view = new MWF.xApplication.process.Application.Viewer(this.node, viewJson, {
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        }.bind(this));
    },
    loadCMSView: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        
        var filter = null;
        if (this.json.filterList && this.json.filterList.length){
            filter = [];
            this.json.filterList.each(function(entry){
                entry.value = this.form.Macro.exec(entry.code.code, this);
                //delete entry.code;
                filter.push(entry);
            }.bind(this));
        }
        var viewJson = {
            "application": this.json.cmsView.appId,
            "viewName": this.json.cmsView.name,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter
        };

        MWF.xDesktop.requireApp("process.Application", "Viewer", function(){
            this.view = new MWF.xApplication.process.Application.Viewer(this.node, viewJson, {
                "actions": {
                    "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                    "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                },
                "actionRoot": "x_cms_assemble_control",
                "resizeNode": (this.node.getStyle("height").toString().toLowerCase()!=="auto" && this.node.getStyle("height").toInt()>0),
                "onSelect": function(){
                    this.fireEvent("select");
                }.bind(this)
            });
        }.bind(this));
    },
    /**
     * @summary 뷰에서 선택된 행의 데이터 가져오기
     * @return {Object[]} 선택된 행의 데이터
     * @example
     * var data = this.form.get("fieldId").getData();
     */
    getData: function(){
        if (this.view.selectedItems.length){
            var arr = [];
            this.view.selectedItems.each(function(item){
                arr.push(item.data);
            });
            return arr;
        }else{
            return [];
        }
    }
});
