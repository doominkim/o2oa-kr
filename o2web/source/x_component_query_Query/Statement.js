MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.xDesktop.requireApp("query.Query", "Viewer", null, false);
/** @classdesc Statement 데이터 센터의 쿼리 뷰. 이 장의 스크립트 컨텍스트는 <b>{@link module:queryStatement|queryStatement}</b>
 * @class
 * @o2cn 쿼리 뷰
 * @extends MWF.xApplication.query.Query.Viewer
 * @o2category QueryStatement
 * @o2range {QueryStatement}
 * @hideconstructor
 * @example
 * //쿼리 뷰 이벤트에서 이 클래스 조회
 * var view = this.target;
 * @example
 * //쿼리 뷰 항목, 작업 표시줄 컴포넌트, 페이지 이벤트에서 이 클래스 조회
 * var view = this.target.view;
 * @example
 * //api 호출로 알림
 * this.queryStatement.notice("this is my information", "info");
 * */
MWF.xApplication.query.Query.Statement = MWF.QStatement = new Class(
    /** @lends MWF.xApplication.query.Query.Statement# */
    {
    Extends: MWF.QViewer,
    options: {
        "lazy": false,
        /**
         * @ignore
         * @event MWF.xApplication.query.Query.Statement#queryLoadCategoryRow
         */
        /**
         * @ignore
         * @event MWF.xApplication.query.Query.Statement#postLoadCategoryRow
         */
        /**
         * 쿼리 Excel 내보내기 이벤트. 이때 내보낼 데이터는 준비되어 있으며 this.target으로 쿼리 뷰 객체 조회 가능. this.event는 다음과 같음:
         * <pre><code class='language-js'>{
         *       data : data, //객체 배열, 내보낼 데이터. 첫 번째 배열은 제목. 수정하면 내보내는 excel 내용도 바뀜.
         *       colWidthArray : colWidthArr, //배열, 열별 너비
         *       title : excelName //문자열, 내보낼 파일명
         * }</code></pre>
         * @event MWF.xApplication.query.Query.Statement#export
         */
        /**
         * 쿼리 Excel 내보내기에서 행마다 생성 후 실행되는 이벤트. this.target으로 쿼리 뷰 객체를, this.event로 다음 내용을 조회 가능
         * <pre><code class='language-js'>{
         *       data : data, //객체, 현재 행의 내보낼 데이터. 수정하면 내보내는 excel 내용도 바뀜.
         *       index : 1, //숫자, 내보낼 행 번호, 1부터 시작
         *       source : source //객체, 서버에서 가져온 원본 데이터
         * }</code></pre>
         * @event MWF.xApplication.query.Query.Statement#exportRow
         */
        "moduleEvents": ["queryLoad", "postLoad", "beforeLoadPageData", "postLoadPageData", "postLoadPage", "selectRow", "unselectRow",
            "queryLoadItemRow", "postLoadItemRow", "queryLoadCategoryRow", "postLoadCategoryRow", "export", "exportRow"]
    },
    initialize: function (container, json, options, app, parentMacro) {
        //이 클래스에는 세 가지 이벤트가 있음.
        //하나는 options로 전달되는 이벤트로 loadView, openDocument, select, unselect 포함
        //하나는 사용자가 설정한 이벤트로, this.options.moduleEvents에 정의된 것은 클래스 이벤트
        //또 하나도 사용자가 설정한 이벤트로, this.options.moduleEvents에 정의되지 않은 것은 this.node의 DOM 이벤트

        this.setOptions(options);

        this.path = "../x_component_query_Query/$Viewer/";
        this.cssPath = "../x_component_query_Query/$Viewer/" + this.options.style + "/css.wcss";
        this._loadCss();
        this.lp = MWF.xApplication.query.Query.LP;

        this.app = app;

        this.container = $(container);

        this.json = json || {};

        this.parentMacro = parentMacro;

        this.originalJson = Object.clone(json);

        this.viewJson = null;
        this.filterItems = [];
        this.searchStatus = "none"; //none, custom, default


        this.items = [];
        this.selectedItems = [];
        this.hideColumns = [];
        this.openColumns = [];
        this.parameter = {};

        this.gridJson = null;

        if (this.options.isload) {
            this.init(function () {
                this.load();
            }.bind(this));
        }

    },
    init: function (callback) {
        if (this.json.view && this.json.format) {
            this.viewJson = JSON.decode(this.json.view);
            this.statementJson = this.json;
            this.statementJson.viewJson = this.viewJson;
            if (callback) callback();
        } else {
            this.getView(callback);
        }
        o2.loadCss('../x_component_process_FormDesigner/Module/Form/skin/v10/view.css');
    },
    loadMacro: function (callback) {
        MWF.require("MWF.xScript.Macro", function () {
            this.Macro = new MWF.Macro.ViewContext(this);
            if (callback) callback();
        }.bind(this));
    },
    createActionbarNode: function () {
        this.actionbarAreaNode.empty();
        if (typeOf(this.json.showActionbar) === "boolean" && this.json.showActionbar !== true) return;
        if (typeOf(this.viewJson.actionbarHidden) === "boolean") {
            if (this.viewJson.actionbarHidden === true || !this.viewJson.actionbarList || !this.viewJson.actionbarList.length) return;
            this.actionbar = new MWF.xApplication.query.Query.Statement.Actionbar(this.actionbarAreaNode, this.viewJson.actionbarList[0], this, {});
            this.actionbar.load();
        }
    },
    _loadPageNode: function () {
        this.viewPageAreaNode.empty();
        if( this.viewJson.pagingbarHidden === true ){
            return;
        }
        if (!this.paging) {
            var json;
            if (!this.viewJson.pagingList || !this.viewJson.pagingList.length) {
                json = {
                    "firstPageText": this.lp.firstPage,
                    "lastPageText": this.lp.lastPage
                };
            } else {
                json = this.viewJson.pagingList[0];
                // var jsonStr = JSON.stringify(json);
                // jsonStr = o2.bindJson(jsonStr, {"lp": MWF.xApplication.query.Query.LP.form});
                // json = JSON.parse(jsonStr);
            }
            this.paging = new MWF.xApplication.query.Query.Statement.Paging(this.viewPageAreaNode, json, this, {});
            this.paging.load();
        } else {
            this.paging.reload();
        }
    },
    // _initPage: function(){
    //     var i = this.count/this.json.pageSize;
    //     this.pages = (i.toInt()<i) ? i.toInt()+1 : i;
    //     this.currentPage = this.options.defaultPage || 1;
    //     this.options.defaultPage = null;
    // },
    lookup: function (data, callback) {
        if (this.lookuping) return;
        this.lookuping = true;
        // this.getLookupAction(function(){
        //     if (this.json.application){

        var d = Object.clone( data || {} );
        // d.count = this.json.count;
        // this.lookupAction.bundleView(this.json.id, d, function(json){
        //     this.bundleItems = json.data.valueList;

        // this._initPage();

        this.loadParameter(d);
        this.loadFilter(d);

        this.currentPage = this.options.defaultPage || 1;
        this.options.defaultPage = null;

        if (this.noDataTextNode) this.noDataTextNode.destroy();
        this.loadCurrentPageData(function (json) {
            if (this.count || (json.data && json.data.length )) {
                this.fireEvent("postLoad"); //사용자 설정 이벤트
                this.lookuping = false;
                if (callback) callback(this);
            } else {
                this.viewPageAreaNode.empty();
                if (this.viewJson.noDataText) {
                    var noDataTextNodeStyle = this.css.noDataTextNode;
                    if (this.viewJson.viewStyles && this.viewJson.viewStyles["noDataTextNode"]) {
                        noDataTextNodeStyle = this.viewJson.viewStyles["noDataTextNode"];
                    }
                    this.noDataTextNode = new Element("div", {
                        "styles": noDataTextNodeStyle,
                        "text": this.viewJson.noDataText
                    }).inject(this.contentAreaNode);
                }
                // if (this.loadingAreaNode){
                //     this.loadingAreaNode.destroy();
                //     this.loadingAreaNode = null;
                // }
                this.fireEvent("postLoad"); //사용자 설정 이벤트
                this.lookuping = false;
                if (callback) callback(this);
            }


        }.bind(this), true, "all");

        // }.bind(this));
        // }
        // }.bind(this));
    },
    loadFilter: function (data) {
        this.filterList = [];
        const list = (data.filterList || []).filter( function(d){ return d.filterType !== "parameter" });
        list.each(function (d) {
            var pName = d.path.replace(/\./g, "_");

            var parameterName = pName;
            var suffix = 1;
            while( this.parameter[parameterName] ){
                parameterName = pName + "_" + suffix;
                suffix++;
            }

            this.parameter[parameterName] = this._parseFilterValue(d);

            d.value = parameterName;

            if( !d.logic )d.logic = "and";

            this.filterList.push(d);
        }.bind(this))
    },
    _parseFilterValue: function (d, ignoreLike) {
        var value = d.value;
        if (!ignoreLike && (d.comparison === "like" || d.comparison === "notLike")) {
            if (value.substr(0, 1) !== "%") value = "%" + value;
            if (value.substr(value.length - 1, 1) !== "%") value = value + "%";
            return value; //"%"+value+"%";
        } else {
            if( ["sql", "sqlScript"].contains(this.statementJson.format) ){
                if (d.formatType === "numberValue"){
                    value = parseFloat(value);
                }
            }else{
                if (d.formatType === "dateTimeValue" || d.formatType === "datetimeValue") {
                    value = "{ts '" + value + "'}";
                } else if (d.formatType === "dateValue") {
                    value = "{d '" + value + "'}";
                } else if (d.formatType === "timeValue") {
                    value = "{t '" + value + "'}";
                } else if (d.formatType === "numberValue"){
                    value = parseFloat(value);
                }
            }
            return value;
        }
    },
    loadParameter: function (data) {
        this.parameter = {};
        var parameter = this.json.parameter ? Object.clone(this.json.parameter) : {};
        //시스템 기본 매개변수
        (this.viewJson.parameterList || []).each(function (f) {
            var value = f.value;
            if (parameter && parameter[f.parameter]) {
                value = parameter[f.parameter];
                delete parameter[f.parameter];
            }
            if (f.valueType === "script") {
                value = this.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
            }
            value = this.parseParameterValue( f, value );

            this.parameter[f.parameter] = value;
        }.bind(this));
        //전달된 매개변수
        for (var p in parameter) {
            var value = parameter[p];
            var configs = (this.json.parameterList || []).filter( function(d){ return d.parameter === p; } );
            if( configs.length > 0 ){
                value = this.parseParameterValue(configs[0], value);
            }else{
                if (typeOf(value) === "date") {
                    if( ["sql", "sqlScript"].contains(this.statementJson.format) ){
                        value = value.format("db");
                    }else{
                        value = "{ts '" + value.format("db") + "'}";
                    }
                }
            }
            this.parameter[p] = value;
        }
        //사용자 입력 사용자 정의 매개변수
        if(data.filterList && data.filterList.length){
            const parameterList = data.filterList.filter( function(d){ return d.filterType === "parameter"; });
            if(parameterList && parameterList.length){
                parameterList.forEach( (d)=>{
                    const value = d.isParseParameter === false ? d.value : this._parseFilterValue(d, true);
                    if(this.parameter.hasOwnProperty(d.parameter)){
                        if(Array.isArray(this.parameter[d.parameter])){
                            this.parameter[d.parameter].push(value);
                        }else{
                            this.parameter[d.parameter] = [this.parameter[d.parameter], value];
                        }
                        this.parameter[d.parameter] = this.parameter[d.parameter];
                    }else{
                        this.parameter[d.parameter] = value;
                    }
                });
            }
        }
    },
    parseParameterValue: function( f, value ){
        // var user = layout.user;
        if (typeOf(value) === "date") {
            value = value.format("db");
        }
        switch (value) {
            case "@person":
                // value = user.distinguishedName;
                value = "";
                break;
            case "@identityList":
                // value = user.identityList.map(function (d) {
                //     return d.distinguishedName;
                // });
                value = "";
                break;
            case "@unitList":
                // o2.Actions.load("x_organization_assemble_express").UnitAction.listWithPerson({"personList": [user.distinguishedName]}, function (json) {
                //     value = json.unitList;
                // }, null, false);
                value = "";
                break;
            case "@unitAllList":
                // o2.Actions.load("x_organization_assemble_express").UnitAction.listWithIdentitySupNested({"personList": [user.distinguishedName]}, function (json) {
                //     value = json.unitList;
                // }, null, false);
                value = "";
                break;
            case "@year":
                value = (new Date().getFullYear()).toString();
                break;
            case "@season":
                var m = new Date().format("%m");
                if (["01", "02", "03"].contains(m)) {
                    value = "1"
                } else if (["04", "05", "06"].contains(m)) {
                    value = "2"
                } else if (["07", "08", "09"].contains(m)) {
                    value = "3"
                } else {
                    value = "4"
                }
                break;
            case "@month":
                value = new Date().format("%Y-%m");
                break;
            case "@time":
                value = new Date().format("db");
                break;
            case "@date":
                value = new Date().format("%Y-%m-%d");
                break;
            default:
        }
        if( !["sql", "sqlScript"].contains(this.statementJson.format) ) {
            if( typeOf(value) === "string" && value.substr(0, 1) !== "{" ){
                if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
                    value = "{ts '" + value + "'}"
                } else if (f.formatType === "dateValue") {
                    value = "{d '" + value + "'}"
                } else if (f.formatType === "timeValue") {
                    value = "{t '" + value + "'}"
                }
            }
        }
        return value;
    },
    loadCurrentPageData: function (callback, async, type) {
        //페이지 이동 시 이전 items를 비워야 하는지?
        if (this.pageloading) return;
        this.pageloading = true;

        if( this.io ){
            this.items.each(function(item){
                this.io.unobserve(item.node);
            }.bind(this))
        }

        this.items = [];

        var p = this.currentPage;
        var d = {
            "filterList": this.filterList,
            "parameter": this.parameter
        };

        if(this.viewTable){
            while (this.viewTable.rows.length > 1) {
                this.viewTable.deleteRow(-1);
            }
            if( this.viewTable.rows.length>0 && !this.viewTable.rows[0].hasClass("viewTitleLine") ){
                this.viewTable.deleteRow(0);
            }
        }else{
            this.contentAreaNode.empty();
        }

        this.contentAreaNode.scrollTo(0, 0);

        //this.createLoadding();
        this.fireEvent("beforeLoadPageData", [d])

        this.loadViewRes = o2.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            this.options.statementId || this.options.statementName || this.options.statementAlias ||
            this.json.statementId || this.json.statementName || this.json.statementAlias,
            type || "data", p, this.json.pageSize, d, function (json) {

                if (type === "all" || type === "count") {
                    if (typeOf(json.count) === "number") {
                        this.count = json.count;
                        var i = this.count / this.json.pageSize;
                        this.pages = (i.toInt() < i) ? i.toInt() + 1 : i;
                    }
                }

                this.gridJson = json.data;
                this.setSelectedableFlag();

                this.fireEvent("postLoadPageData");

                // if (this.viewJson.group.column){
                //     this.gridJson = json.data.groupGrid;
                // }else{
                //     this.gridJson = json.data.grid;
                this.loadData();
                // }
                if (this.gridJson.length) this._loadPageNode();
                if (this.loadingAreaNode) {
                    this.loadingAreaNode.destroy();
                    this.loadingAreaNode = null;
                }

                this.pageloading = false;

                this.fireEvent("loadView"); //options 전달된 이벤트
                this.fireEvent("postLoadPage");

                if (callback) callback(json);
            }.bind(this), null, async === false ? false : true);
    },
    getView: function (callback) {
        this.getViewRes = o2.Actions.load("x_query_assemble_surface").StatementAction.get(
            this.json.statementId || this.json.statementName || this.json.statementAlias,
            function (json) {
                var viewData = JSON.decode(json.data.view);
                if (!this.json.pageSize) this.json.pageSize = viewData.pageSize || "20";
                this.viewJson = viewData.data;
                this.json.application = json.data.query;
                //this.json = Object.merge(this.json, json.data);
                this.statementJson = json.data;
                this.statementJson.viewJson = this.viewJson;
                if (callback) callback();
            }.bind(this)
        );
    },
    loadDataMobile: function(){
        if (this.gridJson.length) {
            this.totalMap = {};
            this.gridJson.each(function (line, i) {
                this.items.push(new MWF.xApplication.query.Query.Statement.MobileItem(this, line, null, i, null, this.options.lazy));
            }.bind(this));
        } else {
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    loadData: function () {
        if (o2.isMediaMobile() && this.viewJson.mobileTemplate && this.viewJson.mobileTemplate.trim()!==""){
            this.loadDataMobile();
            return;
        }

        if (this.getSelectFlag() === "multi" && this.viewJson.allowSelectAll) {
            if (this.selectTitleCell && this.selectTitleCell.retrieve("selectAllLoaded")) {
                this.setUnSelectAllStyle();
            } else {
                this.createSelectAllNode();
            }
        } else if (this.selectAllNode) {
            this.clearSelectAllStyle();
        }

        if (this.gridJson.length) {
            // if( !this.options.paging ){
            this.totalMap = {};
            this.gridJson.each(function (line, i) {
                this.items.push(new MWF.xApplication.query.Query.Statement.Item(this, line, null, i, null, this.options.lazy));
            }.bind(this));
            // }else{
            //     this.loadPaging();
            // }
        } else {
            if (this.viewPageAreaNode) this.viewPageAreaNode.empty();
        }
    },
    loadDataByPaging: function () {
        if (this.isItemsLoading) return;
        if (!this.isItemsLoaded) {
            var from = Math.min(this.pageNumber * this.options.perPageCount, this.gridJson.length);
            var to = Math.min((this.pageNumber + 1) * this.options.perPageCount + 1, this.gridJson.length);
            this.isItemsLoading = true;
            this.totalMap = {};
            for (var i = from; i < to; i++) {
                this.items.push(new MWF.xApplication.query.Query.Statement.Item(this, this.gridJson[i], null, i, null, this.options.lazy));
            }
            this.isItemsLoading = false;
            this.pageNumber++;
            if (to == this.gridJson.length) this.isItemsLoaded = true;
        }
    },
    getFilter: function () {
        var filterData = [];
        if (this.searchStatus === "custom") {
            if (this.filterItems.length) {
                this.filterItems.each(function (filter) {
                    if( !filter.data.logic )filter.data.logic = "and";
                    filterData.push(filter.data);
                }.bind(this));
            }
        }
        if (this.searchStatus === "default") {
            var key = this.viewSearchInputNode.get("value");
            if (key && key !== this.lp.searchKeywork) {
                this.viewJson.customFilterList.each(function (entry) {
                    if (entry.formatType === "textValue") {
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                    }
                    if (entry.formatType === "numberValue") {
                        var v = key.toFloat();
                        if (!isNaN(v)) {
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "like"
                            };
                            filterData.push(d);
                        }
                    }
                }.bind(this));
            }
        }
        return (filterData.length) ? filterData : null;
    },

    //v10 검색
    _filterData: function(data, value, comparison, logic='and'){
        debugger;
        const filterData = value ? {
            "filterType": data.filterType,
            "parameter": data.parameter,
            "logic": logic,
            "path": data.path,
            "title": data.title,
            "comparison": comparison,
            "isParseParameter": data.isParseParameter,
            "comparisonTitle": '',
            "value": value,
            "formatType": (data.formatType=="datetimeValue") ? "dateTimeValue": data.formatType
        } : null;
        if (filterData) this.customFilterListData.push(filterData);
    },
    createSearchDatetimeNode: function(filter, mode){
        const div = new Element("div.search-item");

        if (!o2.isMediaMobile()){
            const title = new Element("div.search-item-title");
            title.textContent = filter.title;
            div.append(title);
        }

        const isParameter = filter.filterType !== 'parameter';

        const dateTimeInputL = new Element("oo-datetime");
        dateTimeInputL.setAttribute('placeholder', filter.title+ (isParameter ? '' : this.lp.begin));
        mode && dateTimeInputL.setAttribute('mode', mode);
        div.append(dateTimeInputL);

        if( !isParameter ){
            const toNode = new Element("div.search-item-flag");
            toNode.textContent = '-';

            const dateTimeInputU = new Element("oo-datetime");
            dateTimeInputU.setAttribute('placeholder', filter.title+this.lp.end);
            mode && dateTimeInputU.setAttribute('mode', mode);
            div.append(toNode, dateTimeInputU);
        }
        return div;
    },

    viewSearchCustomAddToFilter: function () {
        var pathIdx = this.viewSearchCustomPathListNode.selectedIndex;
        var comparisonIdx = this.viewSearchCustomComparisonListNode.selectedIndex;
        if (pathIdx === -1) {
            MWF.xDesktop.notice("error", {
                "x": "left",
                "y": "top"
            }, this.lp.filterErrorTitle, this.viewSearchCustomPathListNode, {"x": 0, "y": 85});
            return false;
        }
        if (comparisonIdx === -1) {
            MWF.xDesktop.notice("error", {
                "x": "left",
                "y": "top"
            }, this.lp.filterErrorComparison, this.viewSearchCustomComparisonListNode, {"x": 0, "y": 85});
            return false;
        }
        var pathOption = this.viewSearchCustomPathListNode.options[pathIdx];
        var entry = pathOption.retrieve("entry");
        if (entry) {
            var pathTitle = entry.title;
            var path = entry.path;
            var comparison = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("value");
            var comparisonTitle = this.viewSearchCustomComparisonListNode.options[comparisonIdx].get("text");
            var value = "";

            if (entry.valueType === "script" && entry.valueScript && entry.valueScript.code) {
                var idx = this.viewSearchCustomValueNode.selectedIndex;
                if (idx !== -1) {
                    var v = this.viewSearchCustomValueNode.options[idx].get("value");
                    value = entry.formatType === "booleanValue" ? (v === "true") : v;
                }
            } else {
                switch (entry.formatType) {
                    case "numberValue":
                        value = this.viewSearchCustomValueNode.get("value");
                        break;
                    case "dateTimeValue":
                        value = this.viewSearchCustomValueNode.get("value");
                        break;
                    case "booleanValue":
                        var idx = this.viewSearchCustomValueNode.selectedIndex;
                        if (idx !== -1) {
                            var v = this.viewSearchCustomValueNode.options[idx].get("value");
                            value = (v === "true");
                        }
                        break;
                    default:
                        value = this.viewSearchCustomValueNode.get("value");
                }
            }

            if (value === "") {
                MWF.xDesktop.notice("error", {
                    "x": "left",
                    "y": "top"
                }, this.lp.filterErrorValue, this.viewSearchCustomValueContentNode, {"x": 0, "y": 85});
                return false;
            }

            this.filterItems.push(new MWF.xApplication.query.Query.Statement.Filter(this, {
                "logic": "and",
                "path": path,
                "title": pathTitle,
                "comparison": comparison,
                "comparisonTitle": comparisonTitle,
                "value": value,
                "formatType": (entry.formatType == "datetimeValue") ? "dateTimeValue" : entry.formatType
            }, this.viewSearchCustomFilterContentNode));

            this.searchCustomView();
        }
    },
    searchView: function(){
        if (this.viewJson.customFilterList) {
            var key = this.viewSearchInputNode.get("value");
            if (key && key !== this.lp.searchKeywork) {
                var filterData = [];
                this.filterItems = [];
                this.viewJson.customFilterList.each(function (entry) {
                    if (entry.formatType === "textValue") {
                        var d = {
                            "path": entry.path,
                            "value": key,
                            "formatType": entry.formatType,
                            "logic": "or",
                            "comparison": "like"
                        };
                        filterData.push(d);
                        this.filterItems.push({"data":d});
                    }
                    if (entry.formatType === "numberValue") {
                        var v = key.toFloat();
                        if (!isNaN(v)) {
                            var d = {
                                "path": entry.path,
                                "value": v,
                                "formatType": entry.formatType,
                                "logic": "or",
                                "comparison": "equals"
                            };
                            filterData.push(d);
                            this.filterItems.push({"data":d});
                        }
                    }
                }.bind(this));

                if( this.json.filter ){
                    this.json.filter.clone().each(function(f){
                        filterData.push(f);
                    })
                }

                this.createViewNode({"filterList": filterData}, null, true);
            }else{
                this.filterItems = [];
                var filterData = this.json.filter ? this.json.filter.clone() : [];
                this.createViewNode( {"filterList": filterData}, null, true );
            }
        }
    },
    //검색 관련 끝

    /**
     * @method MWF.xApplication.query.Query.Statement#getParentEnvironment
     * @summary 현재 뷰가 양식 또는 페이지에 포함된 경우 이 메서드로 양식이나 페이지의 컨텍스트 조회.
     * @see {@link module:queryStatement.getParentEnvironment|자세한 내용은 this.queryStatement.getParentEnvironment 참고}
     * @return {Object}
     * @example
     *  this.target.getParentEnvironment();
     */

    /**
     * @method MWF.xApplication.query.Query.Statement#getPageData
     * @summary 현재 페이지 데이터 조회.
     * @see {@link module:queryStatement.getPageData|자세한 내용은 this.queryStatement.getPageData 참고}
     * @return {Object}
     * @example
     *  this.target.getPageData();
     */

    /**
     * @method MWF.xApplication.query.Query.Statement#toPage
     * @summary 지정한 페이지로 이동.
     * @see {@link module:queryStatement.toPage|자세한 내용은 this.queryStatement.toPage 참고}
     * @param {Number} pageNumber 이동할 페이지 번호
     * @param {Function} callback 이동한 페이지 데이터 로드 완료 후 콜백 메서드.
     * @example
     *  //　2페이지로 이동하고 해당 페이지 데이터 조회.
     *  this.target.toPage( 2, function(){
     *      var data = this.target.getPageData();
     *  }.bind(this) )
     */

    /**
     * @method MWF.xApplication.query.Query.Statement#getSelectedData
     * @summary 선택한 항목의 데이터 조회.
     * @see {@link module:queryStatement.getSelectedData|자세한 내용은 this.queryStatement.getSelectedData 참고}
     * @static
     * @return {Object[]} 선택된 항목의 데이터.
     * @o2syntax
     * var data = this.target.getSelectedData();
     */

    /**
     * @method MWF.xApplication.query.Query.Statement#switchView
     * @ignore
     */

    /**
     * @method MWF.xApplication.query.Query.Statement#getViewInfor
     * @ignore
     */

    /**
     * @summary 쿼리 뷰 설정 정보 조회.
     * @see {@link module:queryStatement.getStatementInfor|자세한 내용은 this.queryStatement.getStatementInfor 참고}
     * @return {Object}
     * @example
     *  this.target.getStatementInfor();
     */
    getStatementInfor: function () {
        return this.statementJson;
    },
    /**
     * @summary 뷰 현재 페이지의 기본 정보 조회.
     * @see {@link module:queryStatement.getPageInfor|자세한 내용은 this.queryStatement.getPageInfor 참고}
     * @return {Object}
     * @example
     *  this.target.getPageInfor();
     */
    getPageInfor: function () {
        return {
            pages: this.pages,
            perPageCount: this.json.pageSize,
            currentPageNumber: this.currentPage
        };
    },

    /**
     * @summary 현재 뷰를 다른 뷰로 전환.
     * @see {@link module:queryStatement.switchStatement|자세한 내용은 this.queryStatement.switchStatement 참고}
     * @param {Object} json 이동할 매개변수 설정
     */
    switchStatement: function (json) {
        this.switchView(json);
    },

    /**
     * @summary 쿼리 뷰 필터 조건 설정. 이 메서드는 뷰의 기본 필터 조건(뷰 개발 시 추가한 필터 조건)을 수정하지 않고 그 위에 추가함.
     * @see {@link module:queryStatement.setStatementFilter|자세한 내용은 this.queryStatement.setStatementFilter 참고}
     * @param {(ViewFilter[]|ViewFilter|Null)} [filter] 필터 조건
     * @param {StatementParameter} [parameter] 필터 조건.
     * @param {Function} callback 필터링 후 데이터를 다시 로드한 뒤의 콜백 메서드.
     * @param {Boolean} [keepSelected] 선택 사항, 선택 결과 유지 여부
     */
    setFilter: function (filter, parameter, callback, keepSelected) {
        if (this.lookuping || this.pageloading) return;
        if (!filter) filter = [];
        if (!parameter) parameter = {};
        this.json.filter = filter;
        this.json.parameter = parameter;
        if (this.viewAreaNode) {
            this.createViewNode({"filterList": this.json.filter.clone()}, callback, keepSelected);
        }
    },

    exportView: function(){
        const flag = this.options.statementId || this.options.statementName || this.options.statementAlias
            || this.json.statementId || this.json.statementName || this.json.statementAlias;
        const exporter = new MWF.xApplication.query.Query.Statement.Exporter({
            statementFlag: this.viewJson.exportStatement?.id || flag,
            filterList: this.filterList || [],
            parameter: this.parameter || {},
            allowSelectColumn: this.viewJson.allowSelectColumn
        }, this)
        exporter.exportView();
    },
    // getDataByPath: function (obj, path) {
    //     var pathList = path.split(".");
    //     for (var i = 0; i < pathList.length; i++) {
    //         var p = pathList[i];
    //         if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
    //         if (obj[p]) {
    //             obj = obj[p];
    //         } else if(obj[p] === undefined || obj[p] === null) {
    //             obj = "";
    //             break;
    //         } else {
    //             obj = obj[p];
    //             break;
    //         }
    //     }
    //     return obj
    // }
});

/** @classdesc StatementItem 데이터 센터 쿼리 뷰의 항목. 이 장의 스크립트 컨텍스트는 <b>{@link module:queryStatement|queryStatement}</b>
 * @class
 * @extends MWF.xApplication.query.Query.Viewer.Item
 * @o2cn 쿼리 뷰 항목(행)
 * @o2category QueryStatement
 * @o2range {QueryStatement}
 * @hideconstructor
 * @example
 * //쿼리 뷰에서 행 조회
 * var item = this.target.items[0];
 */
MWF.xApplication.query.Query.Statement.Item = new Class(
    /** @lends MWF.xApplication.query.Query.Statement.Item# */
    {
    Extends: MWF.xApplication.query.Query.Viewer.Item,
    initialize: function (view, data, prev, i, category, lazy) {
        this.view = view;
        this.data = data;
        this.dataString = JSON.stringify(data);
        this.css = this.view.css;
        this.isSelected = false;

        this.category = category;
        this.prev = prev;
        this.idx = i;
        this.clazzType = "item";
        this.lazy = lazy;
        this.odd = this.view.items.length % 2 === 1;
        this.load();
    },
    _load: function () {
        this.loading = true;

        if(!this.node)this.view.fireEvent("queryLoadItemRow", [null, this]);

        var viewStyles = this.view.viewJson.viewStyles;
        var viewContentTdNode = (viewStyles && viewStyles["contentTd"]) ? viewStyles["contentTd"] : this.css.viewContentTdNode;
        if( this.odd ){
            viewContentTdNode = ( viewStyles && viewStyles["zebraContentTd"] && Object.keys(viewStyles["zebraContentTd"].length > 0)) ? viewStyles["zebraContentTd"] : viewContentTdNode;
        }

        if(!this.node)this.loadNode();

        //if (this.view.json.select==="single" || this.view.json.select==="multi"){
        this.selectTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
        this.selectTd.setStyles({"cursor": "pointer"});
        this.selectTd.setStyles(this.css.viewSelectTdNode);
        if (this.view.json.itemStyles) this.selectTd.setStyles(this.view.json.itemStyles);

        //var selectFlag = this.view.json.select || this.view.viewJson.select ||  "none";
        var selectFlag = this.view.getSelectFlag();
        if (this.data.$selectedEnable && ["multi", "single"].contains(selectFlag) && this.view.viewJson.selectBoxShow === "always") {
            var viewStyles = this.view.viewJson.viewStyles;
            if (viewStyles) {
                if (selectFlag === "single") {
                    if (viewStyles["radioNode"] && viewStyles["radioNode"].className){
                        this.selectTd.addClass(viewStyles["radioNode"].className);
                    }
                    this.selectTd.setStyles(viewStyles["radioNode"]);
                } else {
                    if (viewStyles["checkboxNode"] && viewStyles["checkboxNode"].className){
                        this.selectTd.addClass(viewStyles["checkboxNode"].className);
                    }
                    this.selectTd.setStyles(viewStyles["checkboxNode"]);
                }
            } else {
                var iconName = "checkbox";
                if (selectFlag === "single") iconName = "radiobox";
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/" + iconName + ".png) center center no-repeat"});
            }
        }

        if (this.view.isSelectTdHidden()) {
            this.selectTd.hide();
        }
        //}

        //번호
        var sequence = 1 + this.view.json.pageSize * (this.view.currentPage - 1) + this.idx;
        this.data["$sequence"] = sequence;
        if (this.view.viewJson.isSequence === "yes") {
            this.sequenceTd = new Element("td", {"styles": viewContentTdNode}).inject(this.node);
            this.sequenceTd.setStyles({
                "width": "30px",
                "text-align": "center"
            });
            if (this.view.json.itemStyles) this.sequenceTd.setStyles(this.view.json.itemStyles);
            this.sequenceTd.set("text", sequence);
        }

        Object.each(this.view.entries, function (c, k) {
            //if (cell){
            if (this.view.hideColumns.indexOf(k) === -1) {
                var td = new Element("td", {"styles": viewContentTdNode}).inject(this.node);

                var cell = this.getText(c, k, td); //this.data[k];
                if (cell === undefined || cell === null) cell = "";

                var v = cell;
                if (c.isHtml) {
                    td.set("html", v);
                } else {
                    td.set("text", v);
                }

                // if (k!== this.view.viewJson.group.column){
                var total;
                switch (c.total){
                    case 'number':
                        if( parseFloat(v).toString() !== "NaN" ){ //숫자로 변환 가능
                            total = this.view.totalMap[ c.column ];
                            this.view.totalMap[ c.column ] = total ? total.plus(v.toString()) : new Decimal(v.toString());
                        }
                        break;
                    case 'count':
                        total = this.view.totalMap[ c.column ];
                        this.view.totalMap[ c.column ] = total ? total.plus(v) : new Decimal(v);
                        break;
                }


                if (typeOf(c.contentProperties) === "object") td.setProperties(c.contentProperties);
                if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                if (typeOf(c.contentStyles) === "object") td.setStyles(c.contentStyles);
                // }else{
                //     if (this.view.json.itemStyles) td.setStyles(this.view.json.itemStyles);
                // }

                if (this.view.openColumns.indexOf(k) !== -1) {
                    this.setOpenWork(td, c)
                }

                // if (k!== this.view.viewJson.group.column){
                Object.each(c.events || {}, function (e, key) {
                    if (e.code) {
                        if (key === "loadContent") {
                            this.view.Macro.fire(e.code,
                                {"node": td, "json": c, "data": v, "view": this.view, "row": this});
                        } else if (key !== "loadTitle") {
                            td.addEvent(key, function (event) {
                                return this.view.Macro.fire(
                                    e.code,
                                    {"node": td, "json": c, "data": v, "view": this.view, "row": this},
                                    event
                                );
                            }.bind(this));
                        }
                    }
                }.bind(this));
                // }
            }
            //}
        }.bind(this));

        if(this.placeholderTd){
            this.placeholderTd.destroy();
            this.placeholderTd = null;
        }

        //기본 선택
        var selectedFlag;

        var defaultSelectedScript = this.view.json.defaultSelectedScript || this.view.viewJson.defaultSelectedScript;
        if (!this.isSelected && defaultSelectedScript) {
            // var flag = this.view.json.select || this.view.viewJson.select ||  "none";
            // if ( flag ==="single" || flag==="multi"){
            //
            // }
            selectedFlag = this.view.Macro.exec(defaultSelectedScript,
                {"node": this.node, "data": this.data, "view": this.view, "row": this});
        }

        //selectedItems에 있는지(사용자가 직접 선택했는지) 판단
        if (!this.isSelected && this.view.selectedItems.length) {
            for (var i = 0; i < this.view.selectedItems.length; i++) {
                if (this.view.selectedItems[i].dataString === this.dataString) {
                    selectedFlag = "true";
                    break;
                }
            }
        }

        if (selectedFlag) {
            if (selectedFlag === "multi" || selectedFlag === "single") {
                this.select(selectedFlag);
            } else if (selectedFlag.toString() === "true") {
                var f = this.view.json.select || this.view.viewJson.select || "none";
                if (f === "single" || f === "multi") {
                    this.select();
                }
            }
        }

        this.setEvent();

        this.view.fireEvent("postLoadItemRow", [null, this]);

        this.loading = false;
        this.loaded = true;
    },
    selected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        this.view.selectedItems.push(this);
        var viewStyles = this.view.viewJson.viewStyles;
        if( viewStyles ){
            this.selectTd.removeClass( viewStyles["checkboxNode"].className );
            if (viewStyles["checkedCheckboxNode"].className){
                this.selectTd.addClass( viewStyles["checkedCheckboxNode"].className );
            }
            this.selectTd.setStyles( viewStyles["checkedCheckboxNode"] );
            this.node.setStyles( viewStyles["contentSelectedTr"] );
        }else{
            this.selectTd.setStyles({"background": "url("+"../x_component_query_Query/$Viewer/default/icon/checkbox_checked.png) center center no-repeat"});
            this.node.setStyles(this.css.viewContentTrNode_selected);
        }
        this.isSelected = true;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("selectRow", [this]);
        this.view.fireEvent("select", [{
            "selected": true,
            "item": this,
            "data": this.data
        }]); //options 전달된 이벤트
    },
    unSelected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        var viewStyles = this.view.viewJson.viewStyles;
        if( this.view.viewJson.selectBoxShow !=="always" ){
            this.selectTd.removeClass( viewStyles["checkedCheckboxNode"].className );
            this.selectTd.setStyles({"background": "transparent"});
        }else{
            this.selectTd.setStyles({opacity: 1});
            if (viewStyles) {
                this.selectTd.removeClass( viewStyles["checkedCheckboxNode"].className );
                if (viewStyles["checkboxNode"].className){
                    this.selectTd.addClass( viewStyles["checkboxNode"].className );
                }
                if (viewStyles["checkboxNode"].className){
                    this.selectTd.addClass(viewStyles["checkboxNode"].className);
                }
                this.selectTd.setStyles(viewStyles["checkboxNode"]);
            }else{
                this.selectTd.setStyles({"background": "url(" + "../x_component_query_Query/$Viewer/default/icon/checkbox.png) center center no-repeat"});
            }
        }
        if( viewStyles ){
            this.node.setStyles( viewStyles["contentTr"] );
        }else{
            this.node.setStyles(this.css.viewContentTrNode);
        }
        this.isSelected = false;
        if( from !== "view" && from !=="category" && this.view.viewJson.allowSelectAll ){
            this.view.checkSelectAllStatus();
            if( this.category )this.category.checkSelectAllStatus();
        }
        this.view.fireEvent("unselectRow", [this]);
        this.view.fireEvent("unselect", [{
            "selected": false,
            "item": this,
            "data": this.data
        }]); //options 전달된 이벤트
    },
    getDataByPath: function (obj, path) {
        var pathList = path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else if(obj[p] === undefined || obj[p] === null) {
                obj = "";
                break;
            } else {
                obj = obj[p];
                break;
            }
        }
        return obj
    },
    getText: function (c, k, td) {
        var path = c.path, code = c.code, obj = this.data;
        if (!path) {
           var co = code && code.trim();
           if( !co )return "";
           obj = "";
        } else if (path === "$all") {
        } else {
            obj = this.getDataByPath(obj, path);
        }

        if (code && code.trim()) obj = this.view.Macro.exec(code, {
            "value": obj,
            "data": this.data,
            "entry": c,
            "node": td,
            "json": c,
            "row": this
        });

        var toName = function (value) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    value[idx] = toName(v)
                })
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, key) {
                    value[key] = toName(v);
                })
            } else if (typeOf(value) === "string") {
                value = o2.name.cn(value)
            }
            return value;
        };

        var d;
        if (obj != undefined && obj != null) {
            if (typeOf(obj) === "array") {
                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
            } else if (typeOf(obj) === "object") {
                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
            } else {
                d = c.isName ? o2.name.cn(obj.toString()) : obj;
            }
        }

        return d;
    },
    setOpenWork: function (td, column) {
        td.setStyle("cursor", "pointer");
        td.addEvents({
            "mouseover": function(){
                td.setStyles({
                    "color": "var(--oo-color-main)"
                });
            }.bind(this),
            "mouseout": function(){
                td.setStyles({
                    "color": "unset"
                });
            }.bind(this)
        })
        if (column.clickCode) {
            if (!this.view.Macro) {
                MWF.require("MWF.xScript.Macro", function () {
                    this.view.businessData = {};
                    this.view.Macro = new MWF.Macro.ViewContext(this.view);
                }.bind(this), false);
            }
            td.addEvent("click", function (ev) {
                var result = this.view.Macro.fire(column.clickCode, this, ev);
                ev.stopPropagation();
                return result;
            }.bind(this));
        } else if (this.view.statementJson.entityCategory === "official" && column.idPath) {
            var id = this.getDataByPath(this.data, column.idPath);
            if (id) {
                if (this.view.statementJson.entityClassName === "com.x.cms.core.entity.Document") {
                    td.addEvent("click", function (ev) {
                        this.openCms(ev, id);
                        ev.stopPropagation();
                    }.bind(this));
                } else {
                    td.addEvent("click", function (ev) {
                        this.openWork(ev, id);
                        ev.stopPropagation();
                    }.bind(this));
                }
            }
        }
    },
    openCms: function (e, id) {
        var options = {"documentId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 전달된 이벤트
        layout.desktop.openApplication(e, "cms.Document", options);
    },
    openWork: function (e, id) {
        var options = {"workId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 전달된 이벤트
        layout.desktop.openApplication(e, "process.Work", options);
    }
});

MWF.xApplication.query.Query.Statement.MobileItem = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.MobileItem,
    initialize: function (view, data, prev, i, category, lazy) {
        this.view = view;
        this.data = data;
        this.dataString = JSON.stringify(data);
        this.css = this.view.css;
        this.isSelected = false;

        this.category = category;
        this.prev = prev;
        this.idx = i;
        this.clazzType = "item";
        this.lazy = lazy;
        this.odd = this.view.items.length % 2 === 1;

        this.contentAreaNode = this.view.contentAreaNode;
        this.load();
    },
    load: function () {
        this.loading = true;

        this.view.fireEvent("queryLoadItemRow", [null, this]);

        const html = this.view.viewJson.mobileTemplate;
        const cols = this.view.viewJson.selectList;

        this.node = new Element('div.mobile-view-item');
        this.selectNode = new Element('div.mobile-view-item-select').inject(this.node);
        this.contentNode = new Element('label.mobile-view-item-content').inject(this.node);
        this.flagNode = new Element('div.mobile-view-item-flag.ooicon-arrow_forward').inject(this.node);

        const itemHtml = cols.reduce( (html, col)=>{
            var text = this.getText(col);
            if (text === undefined || text === null) text = "";
            return html
                .replace(new RegExp(`\{${col.column}\}`, 'g'), text)
                .replace(new RegExp(`\{${col.displayName}\}`, 'g'), text);
        }, html);
        this.contentNode.set('html', itemHtml);

        this.contentAreaNode.appendChild(this.node);

        const flag = this.view.isSelectTdHidden() ? 'no' : this.view.getSelectFlag();

        switch( flag ){
            case 'single':
                this._createSelectNode('radio');
                break;
            case 'multi':
                this._createSelectNode('checkbox');
                break;
            default:
                this.selectNode.hide();
                break;
        }

        this.setOpenWork((flag === 'single' || flag === 'multi') ? this.flagNode : this.node);

        this.view.fireEvent("postLoadItemRow", [null, this]);

        this.loading = false;
        this.loaded = true;
    },
    _createSelectNode: function(name){
        const node = new Element('oo-'+name).inject(this.selectNode);
        this.view.tmpCheckId = this.view.tmpCheckId || (this.view.tmpCheckId = o2.uuid());
        node.setAttribute('name', `mobile-view-`+name+this.view.tmpCheckId);
        node.setAttribute('value', this.data.bundle);
        node.setAttribute('id', 'item'+this.data.bundle);

        node.addEventListener('change', (ev)=>{
            this.select();
            ev.stopPropagation();
        });

        this.contentNode.addEventListener('click', (ev)=>{
            node._elements.input.click();
            ev.stopPropagation();
        });

        this._checkSelected();
    },
    selected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }
        this.view.selectedItems.push(this);

        this.isSelected = true;

        this.view.fireEvent("selectRow", [this]);
        this.view.fireEvent("select", [{
            "selected": true,
            "item": this,
            "data": this.data
        }]); //options 전달된 이벤트
    },
    unSelected: function( from ){
        for(var i=0; i<this.view.selectedItems.length; i++){
            var item = this.view.selectedItems[i];
            if( item.dataString === this.dataString ){
                this.view.selectedItems.erase(item);
                break;
            }
        }

        this.isSelected = false;

        this.view.fireEvent("unselectRow", [this]);
        this.view.fireEvent("unselect", [{
            "selected": false,
            "item": this,
            "data": this.data
        }]); //options 전달된 이벤트
    },
    getDataByPath: function (obj, path) {
        var pathList = path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else if(obj[p] === undefined || obj[p] === null) {
                obj = "";
                break;
            } else {
                obj = obj[p];
                break;
            }
        }
        return obj
    },
    getText: function (c, k, td) {
        var path = c.path, code = c.code, obj = this.data;
        if (!path) {
            var co = code && code.trim();
            if( !co )return "";
            obj = "";
        } else if (path === "$all") {
        } else {
            obj = this.getDataByPath(obj, path);
        }

        if (code && code.trim()) obj = this.view.Macro.exec(code, {
            "value": obj,
            "data": this.data,
            "entry": c,
            "node": td,
            "json": c,
            "row": this
        });

        var toName = function (value) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    value[idx] = toName(v)
                })
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, key) {
                    value[key] = toName(v);
                })
            } else if (typeOf(value) === "string") {
                value = o2.name.cn(value)
            }
            return value;
        };

        var d;
        if (obj != undefined && obj != null) {
            if (typeOf(obj) === "array") {
                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
            } else if (typeOf(obj) === "object") {
                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
            } else {
                d = c.isName ? o2.name.cn(obj.toString()) : obj;
            }
        }

        return d;
    },
    setOpenWork: function (node){
        debugger;
        const clickColumn = this.view.viewJson.selectList.find((col)=>{ return !!col.clickCode });
        if(clickColumn){
            this._setOpenWork(node, clickColumn);
        }else if (this.view.statementJson.entityCategory === "official") {
            var columnWithId = this.view.viewJson.selectList.find((col)=>{ return !!col.idPath; });
            if(columnWithId){
                var id = this.getDataByPath(this.data, columnWithId.idPath);
                if (id) {
                    if (this.view.statementJson.entityClassName === "com.x.cms.core.entity.Document") {
                        node.addEvent("click", function (ev) {
                            this.openCms(ev, id);
                            ev.stopPropagation();
                        }.bind(this));
                    } else {
                        node.addEvent("click", function (ev) {
                            this.openWork(ev, id);
                            ev.stopPropagation();
                        }.bind(this));
                    }
                }
            }
        }
    },
    _setOpenWork: function (node, column) {
        if (!this.view.Macro) {
            MWF.require("MWF.xScript.Macro", function () {
                this.view.businessData = {};
                this.view.Macro = new MWF.Macro.ViewContext(this.view);
            }.bind(this), false);
        }
        node.addEvent("click", function (ev) {
            var result = this.view.Macro.fire(column.clickCode, this, ev);
            ev.stopPropagation();
            return result;
        }.bind(this));
    },
    openCms: function (e, id) {
        var options = {"documentId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 전달된 이벤트
        layout.desktop.openApplication(e, "cms.Document", options);
    },
    openWork: function (e, id) {
        var options = {"workId": id};
        this.view.fireEvent("openDocument", [options, this]); //options 전달된 이벤트
        layout.desktop.openApplication(e, "process.Work", options);
    }
});

MWF.xApplication.query.Query.Statement.Filter = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Filter
});

/** @class Actionbar 쿼리 뷰 작업 표시줄 컴포넌트.
 * @o2cn 쿼리 뷰 작업 표시줄
 * @example
 * //쿼리 뷰에서 이 컴포넌트 조회 가능
 * var actionbar = this.target.actionbar; //뷰에서 작업 표시줄 조회
 * //방법 2
 * var actionbar = this.target; //작업 막대와 작업 자체의 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.query.Query.Viewer.Actionbar
 * @o2category QueryStatement
 * @o2range {QueryStatement}
 * @hideconstructor
 */
MWF.xApplication.query.Query.Statement.Actionbar = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Actionbar
});

/** @class Actionbar 쿼리 뷰 페이지 컴포넌트.
 * @o2cn 쿼리 뷰 페이지 컴포넌트
 * @example
 * //쿼리 뷰에서 이 컴포넌트 조회 가능
 * var actionbar = this.target.paging; //뷰에서 작업 표시줄 조회
 * //방법 2
 * var actionbar = this.target; //페이지 컴포넌트 자체 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.query.Query.Viewer.Paging
 * @o2category QueryStatement
 * @o2range {QueryStatement}
 * @hideconstructor
 */
MWF.xApplication.query.Query.Statement.Paging = new Class({
    Extends: MWF.xApplication.query.Query.Viewer.Paging
});


MWF.xApplication.query.Query.Statement.Exporter = new Class({
    Implements: [Options, Events],
    options: {
        statementFlag: '',
        filterList: [],
        parameter: {},
        ignoreDialog: false,
        allowSelectColumn: false
    },
    initialize: function (options, viewer) {
        this.setOptions(options);
        this.viewer = viewer;
        this.lp = MWF.xApplication.query.Query.LP;
        this.Macro = viewer ? viewer.Macro : new MWF.Macro.ViewContext(this);
    },
    exportView: function(){
        this._getView().then(()=>{
            this.lookup( function(){
                if(this.options.ignoreDialog){
                    var total = this.getExportTotalCount();
                    var max = this.getExportMaxCount();
                    var end = Math.min( total, max );
                    this._doExportView(1, end);
                }else{
                    this.showDialog();
                }
            }.bind(this))
        })
    },
    getExportTotalCount: function(){
        return this.count || 100000;
    },
    getExportMaxCount: function(){
        return 2000;
    },
    showDialog: function(){
        var _self = this;
        var total = this.getExportTotalCount();

        var lp = this.lp.viewExport;

        var node = this.exportExcelDlgNode = new Element("div");
        const labelStyle = 'label-style="width:3.2vw;"';

        var html = `
        <div style="display: flex; flex-direction: column; gap: 10px;padding: 20px">
            <div>
                <oo-input ${labelStyle} label='${lp.fileName}' class='filename' value='' style='width:100%'></oo-input><span>
            </div>
            <div>
                <oo-input ${labelStyle} type="number" label='${lp.exportRange}' class='start' value='${this.exportExcelStart || 1}'></oo-input>
                <oo-input type="number" label='${lp.to}' class='end' value='${this.exportExcelEnd || total}'></oo-input>
                <span>${lp.item}</span>
            </div>
            <div style="color: #666;">${ lp.description.replace("{count}", total )}</div>
            <div>
                <div style="padding: 0.8em 1em; font-size: 1.14286rem; display: ${this.options.allowSelectColumn?'block':'none'};">열 선택</div>
                <div class="selector"></div>
            </div>
        </div>
       `;
        node.set("html", html);
        if(this.options.allowSelectColumn){
            this.loadSelector(node.querySelector('.selector'));
        }

        var check = function () {
            if(this.value.length == 1){
                this.value = this.value.replace(/[^1-9]/g,'')
            }else{
                this.value = this.value.replace(/\D/g,'')
            }
            if( this.value.toInt() > total ){
                this.value = total;
            }
        }
        node.getElement(".start")?.addEvent( "keyup", function(){ check.call(this) } );
        node.getElement(".end")?.addEvent( "keyup", function(){ check.call(this) } );


        const _setSelectedColumn = ()=>{
            const selectedIds = this.selector.selectedItems.map(item=>item.data.id);
            this.selectedColumns = this.viewJson.selectList.filter(d=>{
                return selectedIds.includes(d.id);
            })
        }

        let widthPc = this.options.allowSelectColumn ? '840' : '600';
        let heightPc = this.options.allowSelectColumn ? '750' : '300';

        var dlg = o2.DL.open({
            "title": this.lp.exportExcel,
            "style": layout.mobile ? 'v10_mobile' : "v10",
            "isResize": false,
            "content": node,
            "width": layout.mobile ? '100%' : widthPc,
            "height" : layout.mobile ? '100%' : heightPc,
            "buttonList": [
                {
                    "type": "ok",
                    "text": MWF.LP.process.button.ok,
                    "action": function (d, e) {
                        var start = node.getElement(".start").get("value");
                        var end = node.getElement(".end").get("value");
                        var filename = node.getElement(".filename").get("value");
                        if( !start || !end ){
                            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, lp.inputIntegerNotice, node, {"x": 0, "y": 85});
                            return false;
                        }
                        start = Math.max(start.toInt(), 1);
                        end = end.toInt();
                        if( end < start ){
                            MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, lp.startLargetThanEndNotice, node, {"x": 0, "y": 85});
                            return false;
                        }
                        if(this.selector){
                            if(!this.selector.selectedItems.length){
                                MWF.xDesktop.notice("error", {"x": "left", "y": "top"}, lp.selectColumnNotice, node, {"x": 0, "y": 85});
                                return false;
                            }else{
                                _setSelectedColumn();
                            }
                        }
                        this.exportExcelStart = start;
                        this.exportExcelEnd = end;

                        this._doExportView(start, end, filename);
                        dlg.close();
                    }.bind(this)
                },
                {
                    "type": "cancel",
                    "text": MWF.LP.process.button.cancel,
                    "action": function () { dlg.close(); }
                }
            ]
        });
    },
    loadSelector: function (node) {
        const selectList = this.viewJson.selectList.filter(c=>{
            return c.hideColumn !== true && c.exportEnable !== false
        })
        o2.xDesktop.requireApp('Template', 'Selector.Custom', null, false);
        var selector = new MWF.xApplication.Template.Selector.Custom(node, {
            "style": layout.mobile ? "v10_mobile" : "v10_flow",
            "embedded" : true,
            "hasTop": true,
            "selectableItems": selectList.map(d=>{
                return {
                    name: d.displayName, id: d.id,
                    isItem: true, column: d
                }
            }),
            "values": selectList.map(d=>d.id)
        });
        selector.load();
        this.selector = selector;
    },
    _getView: function () {
        if(!this.getViewRes)this.getViewRes = o2.Actions.load("x_query_assemble_surface").StatementAction.get(
            this.options.statementFlag,
            function (json) {
                var viewData = JSON.decode(json.data.view);
                this.viewJson = viewData.data;
                this.statementJson = json.data;
                this.statementJson.viewJson = this.viewJson;

                this.entries = {};
                this.viewJson.selectList.each(function(column){
                    this.entries[column.column] = column;
                }.bind(this));

            }.bind(this)
        );
        return this.getViewRes;
    },
    _doExportView: function(start, end, filename){
        Promise.resolve(this.loadExportData(start, end)).then(function (data) {
            var excelName;
            debugger;
            if( this.options.ignoreDialog ){
                excelName = this.statementJson.name;
            }else{
                excelName = filename || (this.statementJson.name + "(" + start + "-" + end + ")");
            }

            this.viewer.createLoadding();

            var exportArray = [];

            var titleArray = [];
            var colWidthArr = [];
            var dateIndexArray = [];
            var numberIndexArray = [];
            var totalArray = [];
            var idx = 0;

            if (this.viewJson.isSequence === "yes") {
                titleArray.push( this.lp.sequence );
                colWidthArr.push(100);
                totalArray.push('');
                idx = idx + 1;
            }

            const selectList = this.selectedColumns || this.viewJson.selectList;

            selectList.each(function (c, k) {
                if ( c.hideColumn !== true && c.exportEnable !== false) {
                    titleArray.push(c.displayName);
                    colWidthArr.push(c.exportWidth || 200);
                    //var column = c.column.toLowerCase();
                    //if( column.contains('date') || column.contains('time') )dateIndexArray.push(idx);
                    if( c.isTime )dateIndexArray.push(idx);
                    if( c.isNumber )numberIndexArray.push(idx);
                    totalArray.push( ['number', 'count'].contains(c.total) ? new Decimal(0) : '' );
                    idx++;
                }
            }.bind(this));
            exportArray.push(titleArray);

            var rowIndex = 0;
            data.forEach(function (d, i) {
                rowIndex = rowIndex + 1;

                var columnIndex = 0;
                var dataArray = [];
                if (this.viewJson.isSequence === "yes") {
                    dataArray.push( (start-1)+rowIndex );
                    columnIndex++;
                }
                selectList.each(function (c, i) {
                    if ( c.hideColumn !== true && c.exportEnable !== false) {
                        var text = this.getExportText(c, c.column, d);
                        dataArray.push( text );
                        switch (c.total){
                            case 'number':
                                if( parseFloat(text).toString() !== "NaN" ) { //숫자로 변환 가능
                                    totalArray[columnIndex] = totalArray[columnIndex].plus(text);
                                }
                                break;
                            case 'count':
                                totalArray[columnIndex] = totalArray[columnIndex].plus(1);
                                break;
                        }
                        columnIndex++;
                    }
                }.bind(this));
                //exportRow 이벤트
                var argu = {"index":rowIndex, "source": d, "data":dataArray};
                this.viewer?.fireEvent("exportRow", [argu]);
                this.fireEvent("exportRow", [argu]);
                exportArray.push( argu.data || dataArray );
            }.bind(this));

            var hasTotal = false;
            totalArray = totalArray.map(function (d){
                if( d ){
                    hasTotal = true;
                    return d.toString();
                }else{
                    return '';
                }
            });

            if( hasTotal ){
                totalArray[0] = MWF.xApplication.query.Query.LP.total + " " + totalArray[0];
                exportArray.push( totalArray );
            }


            var headTextScript = this.viewJson.exportHeadText;
            var headText = headTextScript ? this.Macro.exec(headTextScript, this) : '';

            var headStyleScript = this.viewJson.exportHeadStyle;
            var headStyle = headStyleScript ? this.Macro.exec(headStyleScript, this) : null;

            var titleStyleScript = this.viewJson.exportColumnTitleStyle;
            var titleStyle = titleStyleScript ? this.Macro.exec(titleStyleScript, this) : null;

            var contentStyleScript = this.viewJson.exportColumnContentStyle;
            var contentStyle = contentStyleScript ? this.Macro.exec(contentStyleScript, this) : null;


            //export 이벤트
            var arg = {
                headText: headText,
                headStyle: headStyle,
                titleStyle: titleStyle,
                contentStyle: contentStyle,
                data : exportArray,
                colWidthArray : colWidthArr,
                title : excelName
            };
            this.viewer?.fireEvent("export", [arg]);
            this.fireEvent("export", [arg]);

            if (this.viewer?.loadingAreaNode) {
                this.viewer.loadingAreaNode.destroy();
                this.viewer.loadingAreaNode = null;
            }

            const options = {
                hasTitle: true,
                headText: arg.headText,
                sheetName: "",
                colWidthArr: arg.colWidthArray || colWidthArr,
                dateIndexArray: dateIndexArray,
                numberIndexArray: numberIndexArray,
                offsetRowIndex: 0,
                offsetColumnIndex: 0,
                startAddress: '' //예: H12
            };
            if(arg.headStyle)options.headStyle = arg.headStyle;
            if(arg.columnTitleStyle)options.columnTitleStyle = arg.titleStyle;
            if(arg.columnContentStyle)options.columnContentStyle = arg.contentStyle;

            new MWF.ExcelExporter({
                fileName: excelName,
                worksheet: [options]
            }).execute([arg.data || exportArray]);

        }.bind(this))
    },
    lookup: function( callback ){
        var p = o2.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            this.options.statementFlag,
            'count', 1, 1, {
                filterList: this.options.filterList,
                parameter: this.options.parameter
            });
        return Promise.resolve( p ).then(function (json) {
            this.count = json.count;
            if(callback)callback();
        }.bind(this));
    },
    loadExportData: function(start, end, callback){
        start = start - 1;
        var differ = end - start;
        var count;
        if( differ < 10000 ){
            count = differ;
        }else{
            count = 10000;
        }
        var page = Math.floor( start / count ) + 1;
        var startIndex = start % count;
        var endIndex = end % count;
        var loaded = (page - 1)*count;
        var list = [];
        do{
            var promise = o2.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
                this.options.statementFlag,
                "data", page, count, {
                    "filterList": this.options.filterList,
                    "parameter": this.options.parameters
                });
            list.push(promise);
            loaded = loaded + count;
            page = page + 1;
        }while( end > loaded );
        var result = [];
        return Promise.all( list ).then(function (arr) {
            arr.each(function (json, i) {
                var data = json.data;
                var length = json.data.length;
                if( i === 0 && i === list.length - 1 ){
                    data.splice( 0, startIndex );
                    if( length > endIndex && endIndex > 0 ){
                        data.splice( endIndex - startIndex , length - endIndex );
                    }
                }else if( i === 0 ){
                    data.splice( 0, startIndex );
                }else if( i=== list.length - 1 ){
                    if( length > endIndex && endIndex > 0 )data.splice( endIndex, length - endIndex );
                }
                result.push(...data);
            });
            if( callback )callback(result);
            return result;
        });

    },
    getDataByPath: function (obj, path) {
        var pathList = path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else if(obj[p] === undefined || obj[p] === null) {
                obj = "";
                break;
            } else {
                obj = obj[p];
                break;
            }
        }
        return obj
    },
    getExportText: function (c, k, data) {
        var path = c.path, code = c.code, obj = data;
        if (!path) {
        } else if (path === "$all") {
        } else {
            obj = this.getDataByPath(obj, path);
        }

        try{
            if (code && code.trim()) obj = this.Macro.exec(code, {
                "value": obj,
                "data": data,
                "entry": c,
                "json": c
            });
        }catch (e) {}

        if( !obj )return "";

        var toName = function (value) {
            if (typeOf(value) === "array") {
                Array.each(value, function (v, idx) {
                    value[idx] = toName(v)
                })
            } else if (typeOf(value) === "object") {
                Object.each(value, function (v, key) {
                    value[key] = toName(v);
                })
            } else if (typeOf(value) === "string") {
                value = o2.name.cn(value)
            }
            return value;
        };

        var d;
        if (obj != undefined && obj != null) {
            if (typeOf(obj) === "array") {
                d = c.isName ? JSON.stringify(toName(Array.clone(obj))) : JSON.stringify(obj);
            } else if (typeOf(obj) === "object") {
                d = c.isName ? JSON.stringify(toName(Object.clone(obj))) : JSON.stringify(obj);
            } else {
                d = c.isName ? o2.name.cn(obj.toString()) : obj;
            }
        }

        if( typeOf( d ) === "number" ){
            d = Math.round(d, 2);
        }

        return d;
    }
})


// MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
// MWF.xApplication.query.Query.Statement.ExcelUtils = new Class({
//     Extends: MWF.xApplication.Template.utils.ExcelUtils
// });
