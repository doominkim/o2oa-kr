MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
//MWF.xDesktop.requireApp("process.Xform", "widget.View", null, false);
/** @class Statement 쿼리 뷰 컴포넌트.
 * @o2cn 쿼리 뷰
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var statement = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var statement = this.target; //컴포넌트 자체 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Statement = MWF.APPStatement =  new Class(
    /** @lends MWF.xApplication.process.Xform.Statement# */
{
	Extends: MWF.APP$Module,
    options: {
        /**
         * 뷰 인자(options)가 준비되었고 아직 뷰를 로드하지 않았을 때 실행. this.event로 뷰 인자를 가져올 수 있으며, this.event를 수정해 뷰 로드를 변경할 수 있음.
         * @event MWF.xApplication.process.Xform.Statement#beforeLoadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 뷰 설계를 가져왔고 컨테이너도 준비됨. this.event로 뷰 파라미터를 얻고 this.event를 수정해 뷰 로드를 변경할 수 있음.
         * @event MWF.xApplication.process.Xform.Statement#loadViewLayout
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 쿼리 뷰 비동기 로드 후 실행.
         * @event MWF.xApplication.process.Xform.Statement#loadView
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 쿼리 뷰에서 기록 하나를 선택한 후 실행.
         * @event MWF.xApplication.process.Xform.Statement#select
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 쿼리 뷰에서 기록 하나의 선택을 해제한 후 실행.
         * @since V8.0
         * @event MWF.xApplication.process.Xform.Statement#unselect
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 쿼리 뷰에서 기록 하나를 연 후 실행.
         * @event MWF.xApplication.process.Xform.Statement#openDocument, this.event로 열린 문서 파라미터를 얻을 수 있음
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "beforeLoadView", "loadViewLayout", "loadView", "queryLoad", "postLoad", "select", "unselect", "openDocument"]
    },

    _loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

        MWF.xDesktop.requireApp("query.Query", "Statement", null, false);
        this.node.empty();
    },
    _afterLoaded: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        if(!!this.json.statementType){
            this.loadView();
        }else if (this.json.queryStatement){
            this.loadView();
        }
    },
    /**
     * @summary 쿼리 뷰 다시 로드
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
     * @summary 쿼리 뷰가 지연 로드(즉시 로드 안 함)로 설정된 경우 active 메서드로 활성화
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
        var appName, statementName, statementId;
        if (this.json.statementType === "script") {
            if (this.json.statementScript && this.json.statementScript.code) {
                var data = this.form.Macro.exec(this.json.statementScript.code, this);
                if (typeOf(data) === 'object') {
                    appName = data.application;
                    statementName = data.statement;
                }else if(typeOf(data) === 'string'){
                    statementName = data;
                }
            }
        }else{
            appName = (this.json.queryStatement) ? this.json.queryStatement.appName : this.json.application;
            statementName =  (this.json.queryStatement) ? this.json.queryStatement.name : this.json.statementName;
            statementId = (this.json.queryStatement) ? this.json.queryStatement.id : this.json.statementId;
        }
        return {appName: appName, statementName: statementName, statementId: statementId};
    },
    loadView: function( callback, force ){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        // if (!this.json.queryStatement) return "";
        var viewObj = this.getViewName();
        var appName = viewObj.appName, statementName = viewObj.statementName, statementId = viewObj.statementId;
        if( !statementName && !statementId ){
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

        var parameter = null;
        if( this.json.parameterList && this.json.parameterList.length ){
            parameter = {};
            this.json.parameterList.each(function(entry){
                parameter[entry.parameter] = this.parseParameter(entry);
            }.bind(this));
        }



        //var data = JSON.parse(this.json.data);
        var viewJson = {
            "application": appName,
            "statementName": statementName,
            "statementId": statementId,
            "isTitle": this.json.isTitle || "yes",
            "select": this.json.select || "none",
            "titleStyles": this.json.titleStyles,
            "itemStyles": this.json.itemStyles,
            "isExpand": this.json.isExpand || "no",
            "showActionbar" : this.json.actionbar === "show",
            "filter": filter,
            "parameter": parameter,
            "parameterList": this.json.parameterList,
            "defaultSelectedScript" : this.json.defaultSelectedScript ? this.json.defaultSelectedScript.code : null,
            "selectedAbleScript" : this.json.selectedAbleScript ? this.json.selectedAbleScript.code : null
        };

        this.fireEvent("beforeLoadView", [viewJson]);

        //MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
        /**
         * @summary Statement 컴포넌트, 플랫폼이 이 컴포넌트로 쿼리 뷰 기능을 구현
         * @member {MWF.xApplication.query.Query.Statement}
         * @example
         *  //스크립트에서 이 컴포넌트를 가져올 수 있음
         * var view = this.form.get("fieldId").view; //컴포넌트 객체 가져오기
         */
            this.view = new MWF.xApplication.query.Query.Statement(this.node, viewJson, {
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
    /**
     * @summary 쿼리 뷰에서 선택된 행의 데이터 가져오기
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
    },
    parseParameter: function (f) {
        var value = f.value;
        if( f.valueType === "script" ){
            value = this.form.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
        }
        return value;
        // 나머지는 queryStatement에서 해석
        // if (typeOf(value) === "date") {
        //     value = value.format("db");
        // }
        // var user = layout.user;
        // switch (value) {
            // case "@person":
            //     value = user.distinguishedName;
            //     break;
            // case "@identityList":
            //     value = user.identityList.map(function (d) {
            //         return d.distinguishedName;
            //     });
            //     break;
            // case "@unitList":
            //     o2.Actions.load("x_organization_assemble_express").UnitAction.listWithPerson({"personList": [user.distinguishedName]}, function (json) {
            //         value = json.unitList;
            //     }, null, false);
            //     break;
            // case "@unitAllList":
            //     o2.Actions.load("x_organization_assemble_express").UnitAction.listWithIdentitySupNested({"personList": [user.distinguishedName]}, function (json) {
            //         value = json.unitList;
            //     }, null, false);
            //     break;
        //     case "@year":
        //         value = (new Date().getFullYear()).toString();
        //         break;
        //     case "@season":
        //         var m = new Date().format("%m");
        //         if (["01", "02", "03"].contains(m)) {
        //             value = "1"
        //         } else if (["04", "05", "06"].contains(m)) {
        //             value = "2"
        //         } else if (["07", "08", "09"].contains(m)) {
        //             value = "3"
        //         } else {
        //             value = "4"
        //         }
        //         break;
        //     case "@month":
        //         value = new Date().format("%Y-%m");
        //         break;
        //     case "@time":
        //         value = new Date().format("db");
        //         break;
        //     case "@date":
        //         value = new Date().format("%Y-%m-%d");
        //         break;
        //     default:
        // }
        //
        // if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
        //     value = "{ts '" + value + "'}"
        // } else if (f.formatType === "dateValue") {
        //     value = "{d '" + value + "'}"
        // } else if (f.formatType === "timeValue") {
        //     value = "{t '" + value + "'}"
        // }
        // return value;
    }
});
