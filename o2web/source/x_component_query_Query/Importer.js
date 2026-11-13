MWF.xApplication.query = MWF.xApplication.query || {};
MWF.xApplication.query.Query = MWF.xApplication.query.Query || {};
MWF.require("MWF.widget.Common", null, false);
MWF.require("MWF.xScript.Macro", null, false);
MWF.require("o2.widget.Dialog", null, false);
MWF.xDesktop.requireApp("query.Query", "lp."+o2.language, null, false);
/** @classdesc Importer 데이터 센터의 가져오기 모델.
 * @class
 * @o2cn 가져오기 모델
 * @o2category QueryImporter
 * @o2range {QueryImporter}
 * @hideconstructor
 * @example
 * //가져오기 모델 이벤트에서 이 클래스 조회
 * var view = this.target;
 * */
MWF.xApplication.query.Query.Importer = MWF.QImporter = new Class(
    /** @lends MWF.xApplication.query.Query.Importer# */
    {
        Implements: [Options, Events],
        Extends: MWF.widget.Common,
        options: {
            "style": "default",
            "moduleEvents": [
                /**
                 * importer(가져오기 모델 객체)를 불러올 때 실행. this.target으로 현재 객체 조회 가능.
                 * @event MWF.xApplication.query.Query.Importer#queryLoad
                 */
                "queryLoad",
                /**
                 * 가져오기 전 발생, this.event는 가져올 데이터이며 this.event를 수정해 데이터를 변경할 수 있음.
                 * @event MWF.xApplication.query.Query.Importer#beforeImport
                 * @example
                 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
                 *[
                 *  [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
                 *  [ "제목2","김철수","남","전문대","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
                 *]
                 */
                "beforeImport",
                /**
                 * 프런트 검증 성공 후 서버에서 가져오기를 마친 뒤 발생, this.event는 서버가 반환한 가져오기 결과.
                 * @event MWF.xApplication.query.Query.Importer#afterImport
                 * @example
                 * <caption>this.event 형식은 다음과 같음:</caption>
                 * {
                 *     "status": "가져오기 성공", //가져오기 결과: 상태는 "가져오기 성공","일부 성공","가져오기 실패"
                 *     "data": {}, //프런트에서 구성한 가져올 데이터
                 *     "rowList": [], //프런트에서 구성한 행 객체 배열
                 *     "count" : 10, //가져오기 총 개수
                 *     "failCount": 0, //실패 개수
                 *     "distribution": "" //가져오기 시 오류 정보
                 * }
                 */
                "afterImport",
                /**
                 * 데이터가 생성되어 프런트에서 데이터 검증 시 발생, this.event는 가져올 데이터.
                 * @event MWF.xApplication.query.Query.Importer#validImport
                 * @example
                 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
                 * {
                 *     "data" : [
                 *          [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
                 *          [ "제목2","김철수","남","전문대","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
                 * 	    ],
                 *     "rowList": [], //가져온 행 객체, 데이터 형식은 이 장 API의 afterCreateRowData 설명 참고.
                 *     "validted" : true  //검증 통과 여부, 이 이벤트에서 수정해 강제 가져오기 여부를 결정할 수 있음
                 * }
                 */
                "validImport",
                /**
                 * 행별 가져올 데이터를 생성하기 전에 발생. this.event는 현재 행 객체를 가리키며, this.event.importData를 수정해 데이터를 변경할 수 있음.
                 * @event MWF.xApplication.query.Query.Importer#beforeCreateRowData
                 */
                "beforeCreateRowData",
                /**
                 * 가져올 각 행의 데이터를 생성한 후에 발생, this.event는 현재 행 객체를 가리킵니다.
                 * @event MWF.xApplication.query.Query.Importer#afterCreateRowData
                 * @example
                 * <caption>this.event 형식은 다음과 같음:</caption>
                 * {
                 *     "importedData": [ "제목1","홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //가져온 데이터
                 *     "data" : {//가져오기 모델로 생성한 업무 데이터
                 *  	    "subject", "제목1", //subject는 가져오기 모델 열 설정의 경로
                 *  	 	"name" : "홍길동",
                 *  	    ...
                 *     },
                 *     "document": { //가져오기 대상이 콘텐츠 관리이면 document 객체 포함
                 *          "title": "제목1"
                 *          "identity": "xxx@xxx@I"
                 *          ...
                 *     },
                 *     "work": { //가져오기 대상이 프로세스 관리이면 work 객체 포함
                 *          "title": "제목1"
                 *          "identity": "xxx@xxx@I"
                 *          ...
                 *     },
                 *     "errorTextList" : [],  //오류 정보
                 *     "errorTextListExcel": [] //오류 화면에서 Excel 내보내기 시 오류 정보
                 * }
                 */
                "afterCreateRowData"
            ]
        },
        initialize: function(container, json, options, app, parentMacro){

            this.setOptions(options);

            this.path = "../x_component_query_Query/$Importer/";
            this.cssPath = "../x_component_query_Query/$Importer/"+this.options.style+"/css.wcss";
            this._loadCss();
            this.lp = MWF.xApplication.query.Query.LP;

            this.app = app;

            this.json = json;

            this.container = container;

            this.parentMacro = parentMacro;

            this.lookupAction = MWF.Actions.get("x_query_assemble_surface");

        },
        load: function(){
            this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils();

            this.getImporterJSON( function () {
                this.loadMacro( function () {
                    this._loadModuleEvents();
                    this.fireEvent("queryLoad");
                    this.importFromExcel();
                }.bind(this));
            }.bind(this));
        },
        loadMacro: function (callback) {
            return new Promise((resolve) => {
                MWF.require("MWF.xScript.Macro", function () {
                    this.Macro = new MWF.Macro.ViewContext(this);
                    if (callback) callback();
                    resolve();
                }.bind(this));
            })
        },
        createLoadding: function(){
            this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
            new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
            var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
            new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
            var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
            loadingTextNode.set("text", "loading...");
        },
        getImporterJSON: function(callback){
            return new Promise( (resolve, reject)=> {
                if( this.importerJson && this.json ){
                    resolve(this.json);
                    if (callback) callback();
                }else{
                    if (this.json.name){
                        this.lookupAction.getImportModel(this.json.name, this.json.application, function(json){
                            this.importerId = json.data.id;
                            this.importerJson = JSON.decode(json.data.data);
                            json.data.data = this.importerJson;
                            this.json = Object.merge(this.json, json.data);
                            resolve(this.json);
                            if (callback) callback();
                        }.bind(this));
                    }else{
                        this.lookupAction.getImportModelById(this.json.id, function(json){
                            this.importerId = json.data.id;
                            this.importerJson = JSON.decode(json.data.data);
                            json.data.data = this.importerJson;
                            this.json.application = json.data.query;
                            this.json = Object.merge(this.json, json.data);
                            resolve(this.json);
                            if (callback) callback();
                        }.bind(this));
                    }
                }
            });
        },
        _loadModuleEvents : function(){
            Object.each(this.json.data.events, function(e, key){
                if (e.code){
                    if (this.options.moduleEvents.indexOf(key)!==-1){
                        this.addEvent(key, function(event, target){
                            return this.Macro.fire(e.code, target || this, event);
                        }.bind(this));
                    }
                }
            }.bind(this));
        },
        getDateColIndexArray: function(){
            var dateColIndexArray = [];
            this.json.data.columnList.each(function(columnJson, index){
                var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
                if( ["date","dateTime"].contains(dataType) )dateColIndexArray.push( index );
            }.bind(this));
            return dateColIndexArray;
        },
        getOrgColIndexArray : function(){
            var orgColIndexArray = [];
            this.json.data.columnList.each(function(columnJson, index){
                if( columnJson.isName ) {
                    orgColIndexArray.push(index);
                }
                // }else if( this.json.type === "cms" ){
                //     if( columnJson.isPublisher || columnJson.isAuthor || columnJson.isReader ){
                //         orgColIndexArray.push( index );
                //     }
                // }
            }.bind(this));
            return orgColIndexArray;
        },
        importFromExcel : function(){

            this.rowList = [];

            this.identityMapImported = {};
            this.personMapImported = {};
            this.unitMapImported = {};
            this.groupMapImported = {};

            this.lookupAction.getUUID( function (json){
                this.recordId = json.data;
            }.bind(this), null, false);

            this.excelUtils.upload( this.getDateColIndexArray(), function (importedData) {

                importedData = importedData.filter(function (array) {
                    for( var i=0; i<array.length; i++ ){
                        if(array[i])return true;
                    }
                    return false;
                });
                if( !importedData.length )return;

                var titleLength = importedData[0].length;
                importedData.each(function (array) {
                    for( var i=0; i<titleLength; i++){
                        if( typeOf(array[i]) === "null" )array[i] = "";
                    }
                });

                this.progressBar = new MWF.xApplication.query.Query.Importer.ProgressBar( this, {
                    "onPostShow": function(){
                        this.progressBar.showCheckData();
                        var level = this.getTitleLevel();
                        if( importedData.length >= level ){
                            importedData.splice(0, level);
                        this.importedData = importedData;
                        }else{
                            this.importedData = [];
                        }

                        this.fireEvent("beforeImport", [this.importedData]);
                        Promise.resolve( this.importedData.promise ).then(function () {
                            this.listOrgDataFromDb(
                                this.getImportedOrgData(),
                                function () {
                                    this.importedData.each( function( lineData, lineIndex ){
                                        this.rowList.push( new MWF.xApplication.query.Query.Importer.Row( this, lineData, lineIndex ) )
                                    }.bind(this));

                                    var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                                    Promise.resolve(isValid).then(function ( isValid1 ) {
                                        if( isValid1 ){
                                            this.doImportData();
                                        }else{
                                            this.openImportedErrorDlg();
                                        }
                                    }.bind(this));
                                }.bind(this)
                            );
                        }.bind(this));
                    }.bind(this)
                });
                this.progressBar.importerId = this.importerId;
            }.bind(this));
        },
        getData : function(){
            var data = ( this.rowList || [] ).map( function(row){
                return row.getResult();
            });
            return data;
        },
        doImportData: function(){
            //데이터 생성
            // this.rowList.each( function( row, i ){
            //     row.createData();
            // }.bind(this));

            //데이터 재검증(계산된 내용)
            var date = new Date();

            //var flag = true;
            // this.rowList.each( function(row, index){
            //     if( row.errorTextList.length )flag = false;
            // }.bind(this));

            // var arg = {
            //     validted : flag,
            //     data : this.importedData,
            //     rowList : this.rowList
            // };
            // this.fireEvent( "import", [arg] );

            // Promise.resolve( arg.promise ).then(function(){
            // flag = arg.validted;
            //
            // if( !flag ){
            //     this.openImportedErrorDlg();
            //     return;
            // }

            var data = this.getData();

            // this.lookupAction.getUUID(function(json){
            //     this.recordId = json.data;
                this.lookupAction.executImportModel(this.json.id, {
                    "recordId": this.recordId,
                    "data" : data
                }, function () {
                    //this.showImportingStatus( data, date )
                    this.progressBar.showImporting( this.recordId, function( data ){
                        data.data = data;
                        data.rowList = this.rowList;
                        this.fireEvent("afterImport", data);
                        return data;
                    }.bind(this), date);

                }.bind(this), function (xhr) {
                    var requestJson = JSON.parse(xhr.responseText);
                    this.app.notice(requestJson.message, "error");
                    this.progressBar.close();
                }.bind(this))
            // }.bind(this))
            // }.bind(this))
        },
        objectToString: function (obj, type) {
            if(!obj)return "";
            var arr = [];
            Object.each(obj,  function (value, key) {
                if( type === "style" ){
                    arr.push( key + ":"+ value +";" );
                }else{
                    arr.push( key + "='"+ value +"'" );
                }
            })
            return arr.join( " " )
        },
        openImportedErrorDlg : function(){
            if(this.progressBar)this.progressBar.close();

            var _self = this;

            var div = new Element("div", { style : "padding:10px;" });
            var dlg = o2.DL.open({
                "style" : "user",
                "title": this.lp.importFail,
                "content": div,
                "offset": {"y": 0},
                "isMax": true,
                "width": 1000,
                "height": 700,
                "buttonList": [
                    {
                        "type": "exportWithError",
                        "text": this.lp.exportExcel,
                        "action": function () { _self.exportWithImportDataToExcel(); }
                    },
                    {
                        "type": "cancel",
                        "text": this.lp.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    var htmlArray = ["<table "+ this.objectToString( this.css.properties ) +" style='"+this.objectToString( this.css.tableStyles, "style" )+"'>"];

                    htmlArray.push( this._getErrorHeadHtml() );

                    htmlArray.push( this._getErrorContentHtml() )

                    htmlArray.push( "</table>" );
                    div.set("html" , htmlArray.join(""));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },
        _getErrorHeadHtml: function () {
             var htmlArray = [];
                    var titleStyle = this.objectToString( this.css.titleStyles, "style" );
                    var vaildTitleStyles = this.objectToString( this.css.vaildTitleStyles, "style" );
                    htmlArray.push( "<tr>" );
                    this.json.data.columnList.each( function (columnJson, i) {
                        htmlArray.push( "<th style='"+titleStyle+"'>"+o2.txt(columnJson.displayName)+"</th>" );
                    });
                    htmlArray.push( "<th style='"+vaildTitleStyles+"'> "+this.lp.validationInfor +"</th>" );
                    htmlArray.push( "</tr>" );
            return htmlArray.join("");
        },
        _getErrorContentHtml: function (){
            var htmlArray = [];
                    var contentStyles = Object.clone( this.css.contentStyles );
                    if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
                    var contentStyle = this.objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

                    var validContentStyles = this.objectToString( this.css.validContentStyles, "style" );

                    this.rowList.each( function( row, lineIndex ){

                        var lineData = row.importedData;

                        htmlArray.push( "<tr>" );
                        this.json.data.columnList.each( function (columnJson, i) {
                            var d = lineData[ i ];
                            var text = typeOf(d) === "string" ? ( d || '' ).replace(/&#10;/g,"<br/>") : d;
                            htmlArray.push( "<td style='"+contentStyle+"'>"+ o2.txt(text) +"</td>" ); //줄바꿈 문자&#10;
                        });
                        htmlArray.push( "<td style='"+validContentStyles+"'>"+( row.errorTextList ? row.errorTextList.join("<br/>") : "" )+"</td>" );
                        htmlArray.push( "</tr>" );

                    }.bind(this));
            return htmlArray.join("");
        },
        //필수 검증 데이터
        checkNecessaryImportedData: function(){
            var flag = true;

            var ps = this.rowList.each( function(row, index){
                return row.checkNecessary();
            }.bind(this));

            return Promise.all( ps ).then(function (arr) {
                for( var i=0; i<arr.length; i++ ){
                    if( arr[i] === false )flag = false;
                }
                var arg = {
                    validted : flag,
                    data : this.importedData,
                    rowList : this.rowList
                };
                this.fireEvent( "validImport", [arg] );

                return Promise.resolve( arg.promise ).then(function () {
                    return arg.validted;
                });
            })
        },
        //Excel 데이터 검증
        checkImportedData : function(){
            var flag = true;

            var ps = this.rowList.map( function(row, index){
                return row.checkValid();
            }.bind(this));

            return Promise.all(ps).then(function (arr) {
                for( var i=0; i<arr.length; i++ ){
                    if( arr[i] === false )flag = false
                }

                var arg = {
                    validted : flag,
                    data : this.importedData,
                    rowList : this.rowList
                };
                this.fireEvent( "validImport", [arg] );

                return Promise.resolve( arg.promise ).then(function () {
                    return arg.validted;
                });

            }.bind(this));
        },
        getOrgData : function( str, ignoreNone, isParse ){
            str = str.trim();
            var flag = str.substr(str.length-2, 2);
            var d;
            switch (flag.toLowerCase()){
                case "@i":
                    d =  this.identityMapImported[str];
                    break;
                case "@p":
                    d = this.personMapImported[str];
                    break;
                case "@u":
                    d = this.unitMapImported[str];
                    break;
                case "@g":
                    d = this.groupMapImported[str];
                    break;
                default:
                    d = this.identityMapImported[str] ||
                        this.personMapImported[str] ||
                        this.unitMapImported[str] ||
                        this.groupMapImported[str];
                    break;
            }
            if( d )return isParse ? MWF.org.parseOrgData(d, true, true) : d;
            if( ignoreNone ) {
                return null;
            }else{
                return {"errorText":  str + this.lp.notExistInSystem };
            }
        },
        getOrgExtendData: function( orgList ){
            var identityList = [], personList = [], unitList = [], groupList = [];
            orgList.each(function (org) {
                var a;
                switch (typeOf( org )) {
                    case "string":
                        a = org; break;
                    case "object":
                        a = org.distinguishedName || org.unique || org.employee; break;
                }
                if( !a )return;

                var d = this.getOrgData(a, true, false);
                if( d )return;

                var flag = a.substr(a.length - 2, 2);
                switch (flag.toLowerCase()) {
                    case "@i":
                        identityList.push(a);
                        break;
                    case "@p":
                        personList.push(a);
                        break;
                    case "@u":
                        unitList.push(a);
                        break;
                    case "@g":
                        groupList.push(a);
                        break;
                    default:
                        identityList.push(a);
                        personList.push(a);
                        unitList.push(a);
                        groupList.push(a);
                        break;
                }
            }.bind(this));

            return this.listOrgDataFromDb({
                identityList: identityList,
                personList: personList,
                unitList: unitList,
                groupList: groupList
            });
        },
        stringToArray: function(string){
            return string.replace(/[\n\r]/g,",").replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
                return !!s;
            });
        },
        getImportedOrgData: function() {
            var orgColIndexArray = this.getOrgColIndexArray();

            var identityList = [], personList = [], unitList = [], groupList = [];
            if( orgColIndexArray.length > 0 ) {
                this.importedData.each(function (lineData, lineIndex) {
                    // if( lineIndex === 0 )return;

                    orgColIndexArray.each(function (colIndex, i) {

                        if (!lineData[colIndex]) return;

                        var arr = this.stringToArray(lineData[colIndex]);
                        arr.each(function (a) {
                            a = a.trim();
                            var flag = a.substr(a.length - 2, 2);
                            switch (flag.toLowerCase()) {
                                case "@i":
                                    identityList.push(a);
                                    break;
                                case "@p":
                                    personList.push(a);
                                    break;
                                case "@u":
                                    unitList.push(a);
                                    break;
                                case "@g":
                                    groupList.push(a);
                                    break;
                                default:
                                    identityList.push(a);
                                    personList.push(a);
                                    unitList.push(a);
                                    groupList.push(a);
                                    break;
                            }
                        })
                    }.bind(this))
                }.bind(this));
            };
            return {
                identityList: identityList,
                personList: personList,
                unitList: unitList,
                groupList: groupList
            };
        },
        listOrgDataFromDb : function (importedOrgData, callback ) {

            var identityList = importedOrgData.identityList;
            var personList = importedOrgData.personList;
            var unitList = importedOrgData.unitList;
            var groupList = importedOrgData.groupList;

            var pIdentity;
            if( identityList && identityList.length ){
                pIdentity = o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList.unique() });
            }

            var pPerson;
            if( personList && personList.length ){
                pPerson = o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList.unique(), useNameFind: true });
            }

            var pUnit;
            if( unitList && unitList.length ){
                pUnit = o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList.unique(), useNameFind: true });
            }

            var pGroup;
            if( groupList && groupList.length ){
                pGroup = o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList.unique() });
            }

            return Promise.all( [pIdentity, pPerson, pUnit, pGroup] ).then(function (arr) {
                if( arr[0] && arr[0].data && arr[0].data.length ){
                    arr[0].data.each( function (d) { if(d)this.identityMapImported[ d.matchKey ] = d; }.bind(this));
                }
                if( arr[1] && arr[1].data && arr[1].data.length ){
                    arr[1].data.each( function (d) { if(d)this.personMapImported[ d.matchKey ] = d; }.bind(this));
                }
                if( arr[2] && arr[2].data && arr[2].data.length ){
                    arr[2].data.each( function (d) { if(d)this.unitMapImported[ d.matchKey ] = d; }.bind(this));
                }
                if( arr[3] && arr[3].data && arr[3].data.length ){
                    arr[3].data.each( function (d) { if(d)this.groupMapImported[ d.matchKey ] = d; }.bind(this));
                }
                if( callback )callback();
                return;
            }.bind(this))
        },

        exportWithImportDataToExcel : function ( importData ) {

            if( !this.excelUtils ){
                this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils();
            }

            var exportTo = function () {
                var resultArr = [];
                var titleArr = this.getTitleArray();
                titleArr.push( this.lp.validationInfor );
                resultArr.push( titleArr );

                if( importData ){
                    importData.each( function (lineData, lineIndex) {
                        var array = [];
                        if( o2.typeOf(lineData)==="array" ) {
                            lineData.each(function (d, i) {
                                array.push((d || '').replace(/&#10;/g, "\n"));
                            });
                        }else if(o2.typeOf(lineData)==="object"){
                            this.json.data.columnList.each( function (columnJson, i) {
                                array.push( lineData[columnJson.path] || "" )
                            }.bind(this));
                            if( lineData["o2ErrorText"] ){
                                array.push( lineData["o2ErrorText"] );
                            }
                        }
                        resultArr.push( array );
                    }.bind(this));
                }else{
                    this.rowList.each( function( row, lineIndex ){
                        var lineData = row.importedData;
                        var array = [];
                        for( var i=0; i<lineData.length; i++ ){
                            var d = ( lineData[i] || '' ).replace(/&#10;/g, "\n");
                            array.push( d );
                        }
                        // lineData.each( function (d, i) {
                        //     array.push( ( d || '' ).replace(/&#10;/g, "\n") );
                        // });

                        array.push( row.errorTextListExcel ? row.errorTextListExcel.join("\n") : ""  );
                        resultArr.push( array );
                    }.bind(this));
                }


                var colWidthArray = this.getColWidthArray();
                colWidthArray.push(260);

                var arg = {
                    data : resultArr,
                    colWidthArray : colWidthArray,
                    title : this.getFileName()
                };

                this.fireEvent("export", [arg]);

                this.excelUtils.exportToExcel(
                    arg.data,
                    arg.title,
                    arg.colWidthArray,
                    this.getDateIndexArray()
                )
            }.bind(this);

            if( !this.importerJson ){
                this.getImporterJSON( function () {
                    exportTo();
                }.bind(this))
            }else{
                exportTo();
            }
        },

        downloadTemplate: function(fileName, callback){
            if( this.Macro ){
                this._downloadTemplate(fileName, callback);
            }else{
                this.loadMacro(function (){
                    this._downloadTemplate(fileName, callback);
                }.bind(this));
            }
        },
        _downloadTemplate: function(fileName, callback){
            if( !this.excelUtils ){
                this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils({
                    isTemplate: true
                });
            }
            var doExport = function () {
                var arg = {
                    data : [this.getTitleArray()],
                    colWidthArray : this.getColWidthArray(),
                    title : this.getFileName(fileName)
                };
                if(callback)callback(arg);

                this.excelUtils.exportToExcel(
                    arg.data,
                    arg.title,
                    arg.colWidthArray,
                    this.getDateIndexArray()
                )
            }.bind(this)
            if( !this.importerJson ){
                this.getImporterJSON( function () {
                    doExport()
                }.bind(this))
            }else{
                doExport();
            }
        },
        getFileName: function(fileName){
            var title = fileName || this.json.name;
            var titleA = title.split(".");
            if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
                titleA.splice( titleA.length-1 );
            }
            title = titleA.join(".");
            return title;
        },
        getTitleData: function (){
            var titleDataParsed = [];
            this.getTitleArray().each(function (title, i){
                var text = o2.typeOf(title) === 'object' ? title.text : title;
                var texts = ( text || " " ).split('\\');
                titleDataParsed.push( texts );
            });
            return titleDataParsed;
        },
        getTitleLevel: function (){
            var maxTitleLevel = 1;
            this.getTitleArray().each(function (title, i){
                var text = o2.typeOf(title) === 'object' ? title.text : title;
                var texts = ( text || " " ).split('\\');
                maxTitleLevel = Math.max( maxTitleLevel, texts.length );
            });
            return maxTitleLevel;
        },
        getTitleArray: function(){
            return this.json.data.columnList.map( function (columnJson, i) {
                var obj = {
                    text: columnJson.displayName
                };
                if( columnJson.optionScript ){
                    obj.options = this.Macro.exec(columnJson.optionScript, this);
                }
                return obj;
            }.bind(this));
        },
        getColWidthArray : function(){
            var colWidthArr = [];
            this.json.data.columnList.each( function (columnJson, i) {
                var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
                switch ( dataType ) {
                    case "string":
                    case "stringList":
                        if( columnJson.isName ){
                            colWidthArr.push( 340 );
                        }else if( columnJson.isSummary ){
                            colWidthArr.push( 260 );
                        }else{
                            colWidthArr.push( 150 );
                        }
                        break;
                    case "number":
                    case "integer":
                    case "long":
                    case "double":
                    case "numberList":
                    case "integerList":
                    case "longList":
                    case "doubleList":
                        colWidthArr.push(150);
                        break;
                    case "date":
                    case "dateTime":
                    case "dateList":
                    case "dateTimeList":
                        colWidthArr.push(150);
                        break;
                    default:
                        colWidthArr.push(150);
                        break;
                }
            }.bind(this));

            return colWidthArr;
        },
        getDateIndexArray : function(){
            var dateIndexArr = []; //날짜 형식 열 인덱스
            this.json.data.columnList.each( function (columnJson, i) {
                var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
                switch ( dataType ) {
                    case "date":
                    case "dateTime":
                    case "dateList":
                    case "dateTimeList":
                        dateIndexArr.push(i);
                        break;
                    default:
                        break;
                }
            }.bind(this));
            return dateIndexArr;
        },


        //api 사용 시작

        confirm: function (type, e, title, text, width, height, ok, cancel, callback, mask, style) {
            this.app.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style)
        },
        alert: function (type, title, text, width, height) {
            this.app.alert(type, "center", title, text, width, height);
        },
        notice: function (content, type, target, where, offset, option) {
            this.app.notice(content, type, target, where, offset, option)
        }
        //api 사용 끝
    });

MWF.xApplication.query.Query.Importer.Row = new Class({
    initialize: function(importer, importedData, rowIndex){
        this.importer = importer;
        this.importedData = importedData;
        this.clazzType = "row";

        this.lp = this.importer.lp;

        this.data = {};
        this.errorTextList = [];
        this.errorTextListExcel = [];
        this.calculateFieldDataMap = {};
    },
    checkValid : function(){

        var lp = this.lp;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];

        var excelUtils = MWF.ExcelUtilsV2 || this.importer.excelUtils;

        this.importer.json.data.columnList.each( function (columnJson, i) {

            var colName = excelUtils.index2ColName( i );
            var colInfor = columnText.replace( "{n}", (i+1)+"("+colName+")" );
            var colInforExcel = columnTextExcel.replace( "{n}", colName );

            var value = this.importedData[i] || "";

            var dataType = this.importer.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            if( !columnJson.allowEmpty && !value ){
                errorTextList.push( colInfor + lp.canNotBeEmpty + lp.fullstop );
                errorTextListExcel.push( colInforExcel + lp.canNotBeEmpty + lp.fullstop );
            }

            if( columnJson.validInOption && columnJson.optionScript && value ){
                var options = this.importer.Macro.exec(columnJson.optionScript, this);
                if( !options.contains(value) ){
                    errorTextList.push( colInfor + lp.notInOptions +":"+ options.join(",") + lp.fullstop );
                    errorTextListExcel.push( colInforExcel + lp.notInOptions +":"+ options.join(",") + lp.fullstop );
                }
            }

            if( columnJson.validFieldType !== false && value ){

                switch ( dataType ) {
                    case "string":
                    case "stringList":
                        if( columnJson.isName ){
                            var arr = this.stringToArray(value);
                            arr.each( function(d, idx){
                                var obj = this.importer.getOrgData( d );
                                if( obj.errorText ){
                                    errorTextList.push( colInfor + obj.errorText + lp.fullstop );
                                    errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
                                }
                            }.bind(this));
                        }
                        break;
                    case "number":
                    case "integer":
                    case "long":
                    case "double":
                        value = value.replace(/&#10;/g,"");
                        if (isNaN(value)){
                            errorTextList.push( colInfor + value + lp.notValidNumber + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidNumber + lp.fullstop );
                        }
                        break;
                    case "numberList":
                    case "integerList":
                    case "longList":
                    case "doubleList":
                        var arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if (isNaN(d)){
                                errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "date":
                    case "dateTime":
                        value = value.replace(/&#10;/g,"");
                        if( !( new Date(value).isValid() )){
                            errorTextList.push(colInfor + value + lp.notValidDate + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidDate + lp.fullstop );
                        }
                        break;
                    case "dateList":
                    case "dateTimeList":
                        var arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if( !( new Date(d).isValid() )){
                                errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "boolean":
                        value = value.replace(/&#10;/g,"");
                        if( !["true","false"].contains(value.trim().toLowerCase()) ){
                            errorTextList.push(colInfor + value + lp.notValidBoolean + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidBoolean + lp.fullstop );
                        }
                        break;
                    case "booleanList":
                        var  arr = this.stringToArray(value);
                        arr.each( function(d, idx){
                            if( !["true","false"].contains(d.trim().toLowerCase())){
                                errorTextList.push(colInfor + d + lp.notValidBoolean + lp.fullstop );
                                errorTextListExcel.push( colInforExcel + d + lp.notValidBoolean + lp.fullstop );
                            }
                        }.bind(this));
                        break;
                    case "json":
                    case "stringMap":
                        value = value.replace(/&#10;/g,"");
                        try{
                            var d = JSON.parse(value);
                        }catch (e) {
                            errorTextList.push(colInfor + value + lp.notValidJson + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidJson + lp.fullstop );
                        }
                        break;
                    default:
                        break;
                }
            }
        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(this.errorTextList.length>0){
            return false;
        }

        var p = this.createData();

        return Promise.resolve( p ).then(function (flag) {

            this.importer.json.data.calculateFieldList.each( function (fieldJson, i) {
                if( !fieldJson.path )return;
                if( fieldJson.isName ){
                    var sourceData = this.calculateFieldDataMap[fieldJson.path];
                    var arr = typeOf( sourceData ) === "array" ? sourceData : [sourceData];
                    arr = typeOf( arr ) === "array" ? arr : [arr];
                    arr.each(function (d, idx) {
                        if( !d )return d;
                        var a;
                        switch (typeOf( d )) {
                            case "string":
                                a = d; break;
                            case "object":
                                a = d.distinguishedName || d.unique || d.employee; break;
                        }
                        if( !a )return d;
                        var obj = this.importer.getOrgData( a );
                        if( obj.errorText ){
                            var errorText = (fieldJson.displayName || fieldJson.path) + ":" + obj.errorText + lp.fullstop;
                            errorTextList.push( errorText );
                            errorTextListExcel.push( errorText );
                        }
                    }.bind(this));
                }
            }.bind(this));

            this.errorTextList = this.errorTextList.concat( errorTextList );
            this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

            if( this.errorTextList.length > 0 )return false;


            if( this.importer.json.type === "cms" ){
                this.checkCMS( true );
            }else if( this.importer.json.type === "process" ){
                this.checkProcess( true );
            }

            if(this.errorTextList.length>0){
                return false;
            }

            return true;

        }.bind(this));
    },
    checkNecessary: function(){

        var lp = this.lp;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];

        var excelUtils = MWF.ExcelUtilsV2 || this.importer.excelUtils;

        this.importer.json.data.columnList.each( function (columnJson, i) {
            var colName = excelUtils.index2ColName( i );
            var colInfor = columnText.replace( "{n}", (i+1)+"("+colName+")" );
            var colInforExcel = columnTextExcel.replace( "{n}", colName );

            var value = this.importedData[i] || "";

            var dataType = this.importer.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;

            if( columnJson.validFieldType !== false && value ){

                switch ( dataType ) {
                    case "json":
                    case "stringMap":
                        value = value.replace(/&#10;/g,"");
                        try{
                            var d = JSON.parse(value);
                        }catch (e) {
                            errorTextList.push(colInfor + value + lp.notValidJson + lp.fullstop );
                            errorTextListExcel.push( colInforExcel + value + lp.notValidJson + lp.fullstop );
                        }
                        break;
                    default:
                        break;
                }
            }
        }.bind(this));

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(this.errorTextList.length>0){
            return false;
        }

        var p = this.createData();

        return Promise.resolve(p).then(function () {
            if( this.importer.json.type === "cms" ){
                this.checkCMS();
            }else if( this.importer.json.type === "process" ){
                this.checkProcess();
            }

            if(this.errorTextList.length>0){
                return false;
            }

            return true;
        }.bind(this))
    },
    getCol: function(key, isExcel){
        var text, lp = this.lp;
        var excelUtils = MWF.ExcelUtilsV2 || this.importer.excelUtils;
        if( this.pathIndexMap && typeOf(this.pathIndexMap[key]) === "number"){
            var i = this.pathIndexMap[key];
            if( isExcel ){
                text = lp.importValidationColumnTextExcel;
                return text.replace( "{n}", excelUtils.index2ColName( i ) );
            }else{
                text =  lp.importValidationColumnText;
                return text.replace( "{n}", i+1 );
            }
        }
        return "";
    },
    checkCMS : function( notCheckName ){

        var lp = this.lp;

        var errorTextList = [];
        var errorTextListExcel = [];

        var data = this.document.identity;
        if(!data){

            errorTextList.push( this.getCol("identity", false) + lp.noDrafter + lp.fullstop );
            errorTextListExcel.push( this.getCol("identity", true) + lp.noDrafter + lp.fullstop );

        }else if(data.split("@").getLast().toLowerCase() !== "i"){

            errorTextList.push( this.getCol("identity", false) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
            errorTextListExcel.push(  this.getCol("identity", true) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );

        }

        data = this.document.publishTime;
        if(!data){
            errorTextList.push(this.getCol("publishTime", false) + lp.noPublishTime + lp.fullstop );
            errorTextListExcel.push(this.getCol("publishTime", false) + lp.noPublishTime + lp.fullstop );
        }else if( ! new Date(data).isValid() ){
            errorTextList.push(this.getCol("publishTime", false) + '"'+ data +'"'+ lp.publishTimeFormatError + lp.fullstop );
            errorTextListExcel.push(this.getCol("publishTime", false) + '"'+ data +'"'+ lp.publishTimeFormatError + lp.fullstop );
        }

        data = this.document.title;
        if( data && data.length > 70){
            errorTextList.push(this.getCol("title", false) + '"'+ data +'"'+ lp.cmsTitleLengthInfor + lp.fullstop );
            errorTextListExcel.push(this.getCol("title", false) + '"'+ data +'"'+ + lp.cmsTitleLengthInfor + lp.fullstop );
        }

        data = this.document.summary;
        if( data && data.length > 70 ){
            errorTextList.push(this.getCol("summary", false) + '"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
            errorTextListExcel.push( this.getCol("summary", false) + '"'+ data +'"'+ lp.cmsSummaryLengthInfor + lp.fullstop );
        }

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    },
    checkProcess : function( notCheckName ){

        var lp = this.lp;
        var json = this.importer.json;

        var columnText =  lp.importValidationColumnText;
        var columnTextExcel = lp.importValidationColumnTextExcel;

        var errorTextList = [];
        var errorTextListExcel = [];

        var data = this.work.identity;
        if(!data){
            errorTextList.push( this.getCol("identity", false) + lp.noDrafter + lp.fullstop );
            errorTextListExcel.push( this.getCol("identity", true) + lp.noDrafter + lp.fullstop );
        }else if(data.split("@").getLast().toLowerCase() !== "i"){
            errorTextList.push( this.getCol("identity", false) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
            errorTextListExcel.push(  this.getCol("identity", true) + '"'+ data +'"'+ lp.drafterIsNotIdentity + lp.fullstop );
        }

        if( json.data.processStatus === "completed" ){
            if(!this.work.form){
                errorTextList.push( lp.noForm + lp.fullstop );
                errorTextListExcel.push( lp.noForm + lp.fullstop );
            }

            data = this.work.startTime;
            if(!data){
                errorTextList.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + lp.noStartTime + lp.fullstop );
            }else if( ! new Date(data).isValid() ){
                errorTextList.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
                errorTextListExcel.push(this.getCol("startTime", false) + '"'+ data +'"'+ lp.startTimeFormatError + lp.fullstop );
            }

            data = this.work.completeTime;
            if(!data){
                errorTextList.push(this.getCol("completeTime", false) + lp.noEndTime + lp.fullstop );
                errorTextListExcel.push(this.getCol("completeTime", false) + lp.noEndTime + lp.fullstop );
            }else if( ! new Date(data).isValid() ){
                errorTextList.push(this.getCol("completeTime", false) + '"'+ data +'"'+ lp.endTimeFormatError + lp.fullstop );
                errorTextListExcel.push(this.getCol("completeTime", false) + '"'+ data +'"'+ lp.endTimeFormatError + lp.fullstop );
            }
        }

        this.errorTextList = this.errorTextList.concat( errorTextList );
        this.errorTextListExcel = this.errorTextListExcel.concat( errorTextListExcel );

        if(errorTextList.length > 0){
            return false;
        }

        return true;
    },


    createData : function(){

        var json = this.importer.json;

        if( json.type === "cms" ){
            this.document = {
                categoryId : json.data.category.id,
                readerList : [],
                authorList : [],
                docData : this.data
            };
        }else if( json.type === "process" ){
            this.work = {
                processFlag : json.data.process.id,
                data: this.data
            };
        }

        this.importer.fireEvent("beforeCreateRowData", [this]);

        json.data.columnList.each( function (columnJson, i) {
            if(!columnJson.path)return;

            var value = this.importedData[i] || "";

            if( !value && columnJson.defaultValueScript ){
                value = this.importer.Macro.exec(columnJson.defaultValueScript, this);
            }

            var dataType = (json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess);

            if( !value && ["number","double","integer","long"].contains(dataType) ){
                value = 0;
            }

            if( !value && (typeOf(value) === "null" || value === "") )return;

            var data = this.parseData(value, dataType, columnJson);

            if( !data && data !== 0 && data !== false )return;

            if( json.type === "dynamicTable" ){
                this.data[ columnJson.path ] = data;
            }else{
                this.setDataWithPath(this.data, columnJson.path, data);
            }

            var array;
            if( json.type === "cms" ){
                if( columnJson.isName ) {
                    if (columnJson.isAuthor) {
                        array = this.parseCMSReadAndAuthor(data, "作者");
                        this.document.authorList = this.document.authorList.concat(array)
                    }
                    if (columnJson.isReader) {
                        array = this.parseCMSReadAndAuthor(data, "阅读");
                        this.document.readerList = this.document.readerList.concat(array)
                    }
                }

            }

        }.bind(this));

        var calculateOrgData = [], caculateMap = {};
        json.data.calculateFieldList.each( function (fieldJson, i) {
            if( !fieldJson.path )return;
            if( !fieldJson.valueScript )return;

            var data = this.importer.Macro.exec(fieldJson.valueScript, this);
            this.calculateFieldDataMap[fieldJson.path] = data;
            caculateMap[fieldJson.path] = data;
            if( fieldJson.isName && data ){
                switch (o2.typeOf(data)) {
                    case "array":
                        if(data.length)calculateOrgData = calculateOrgData.concat(data); break;
                    default:
                        calculateOrgData.push(data); break;
                }
            }
        }.bind(this));

        var p = this.importer.getOrgExtendData( calculateOrgData );

        return Promise.resolve(p).then(function () {

            json.data.calculateFieldList.each( function (fieldJson, i) {
                if( !fieldJson.path )return;
                if( !fieldJson.valueScript )return;

                var data;
                if( fieldJson.isName ){
                    var arr = caculateMap[fieldJson.path];
                    arr = typeOf( arr ) === "array" ? arr : [arr];
                    data = arr.map(function (d, idx) {
                        if( !d )return d;
                        var a;
                        switch (typeOf( d )) {
                            case "string":
                                a = d; break;
                            case "object":
                                a = d.distinguishedName || d.unique || d.employee; break;
                        }
                        if( !a )return d;
                        return this.importer.getOrgData( a, true, true );
                    }.bind(this));
                    data = data.clean();
                }else{
                    data = caculateMap[fieldJson.path];
                }

                if( o2.typeOf(data) === "null" )return;

                if( json.type === "dynamicTable" ){
                    this.data[ fieldJson.path ] = data;
                }else{
                    this.setDataWithPath(this.data, fieldJson.path, data);
                }

                if( json.type === "cms" ){
                    if( fieldJson.isAuthor ){
                        var array = this.parseCMSReadAndAuthor( data, "作者" );
                        this.document.authorList = this.document.authorList.concat( array )
                    }
                    if( fieldJson.isReader ){
                        var array = this.parseCMSReadAndAuthor( data, "阅读" );
                        this.document.readerList = this.document.readerList.concat( array )
                    }

                }
            }.bind(this));

            var array;
            if( json.type === "cms" ){
                this.document.docData = this.data;

                if( json.data.documentPublisher === "importer" ){
                    array = layout.session.user.identityList;
                    if( array && array.length ){
                        this.document.identity = array[0].distinguishedName;
                    }
                }else{
                    this.setDataWithField(this.document, "documentPublisherField", "identity", true);
                }

                if( json.data.documentPublishTime === "importer" ){
                    this.document.publishTime = new Date().format("db");
                }else{
                    this.setDataWithField(this.document, "documentPublisherTimeField", "publishTime", false);
                }

                this.setDataWithField(this.document, "documentTitleField", "title", false);
                if( !this.document.title )this.document.title = "제목 없음";

                this.setDataWithField(this.document, "documentSummaryField", "summary", false);

            }else if( json.type === "process" ){
                this.work.data = this.data;

                if( json.data.processDrafter === "importer" ){
                    array = layout.session.user.identityList;
                    if( array && array.length ){
                        this.work.identity = array[0].distinguishedName
                    }
                }else{
                    this.setDataWithField(this.work, "processDrafterField", "identity", true);
                }

                this.setDataWithField(this.work, "processTitleField", "title", false);
                if( !this.work.title )this.work.title = "제목 없음";

                if( json.data.processStatus === "completed" ){
                    this.work.form = json.data.processForm || "";
                    this.setDataWithField(this.work, "processSerialField", "serial", false);
                    this.setDataWithField(this.work, "processStartTimeField", "startTime", false);
                    this.setDataWithField(this.work, "processCompleteTimeField", "completeTime", false);
                }
            }

            this.importer.fireEvent("afterCreateRowData", [ this]);
        }.bind(this));

    },
    parseData: function(value, dataType, json){
        var data;
        var type = this.importer.json.type;
        switch ( dataType ) {
            case "string":
            case "stringList":
                if( json.isName ){
                    var  arr = this.stringToArray(value);
                    if( type === "dynamicTable" ){
                        data = arr
                    }else{
                        data = arr.map( function(d, idx){
                            return this.importer.getOrgData( d, true, true ) || d;
                        }.bind(this)).clean();
                    }
                }else{
                    if( dataType === "string" ){
                        var linebreak = type === "dynamicTable" ? json.lineBreak_Querytable : json.lineBreak_CMSProcess;
                        if( typeOf(value) !== 'string' ){
                            data = value.toString ? value.toString() : (value || "");
                        }else{
                            data = value.replace(/&#10;/g, linebreak || "" );
                        }
                    }else{
                        data = this.stringToArray(value);
                    }
                }
                break;
            case "number":
            case "double":
                if( value === 0 ){
                    data = 0;
                }else{
                    value = value.replace(/&#10;/g,"");
                    data = parseFloat(value);
                }
                break;
            case "integer":
            case "long":
                if( value === 0 ){
                    data = 0;
                }else {
                    value = value.replace(/&#10;/g, "");
                    data = parseInt(value);
                }
                break;
            case "numberList":
            case "doubleList":
                data = this.stringToArray(value).map( function(d, idx){ return parseFloat( d ); }.bind(this)).clean();
                break;
            case "integerList":
            case "longList":
                data = this.stringToArray(value).map( function(d, idx){ return parseInt( d ); }.bind(this)).clean();
                break;
            case "date":
                value = value.replace(/&#10;/g,"");
                data = Date.parse(value).format( "%Y-%m-%d" );
                break;
            case "dateTime":
                value = value.replace(/&#10;/g,"");
                data = Date.parse(value).format( "db" );
                break;
            case "dateList":
                data = this.stringToArray(value).map( function(d, idx){ return Date.parse(d).format( "%Y-%m-%d" ); }.bind(this)).clean();
                break;
            case "dateTimeList":
                data = this.stringToArray(value).map( function(d, idx){ return Date.parse(d).format( "db" ); }.bind(this)).clean();
                break;
            case "boolean":
                value = value.replace(/&#10;/g,"");
                data = value.trim().toLowerCase() !== "false";
                break;
            case "booleanList":
                data = this.stringToArray(value).map( function(d, idx){ return value.trim().toLowerCase() !== "false"; }.bind(this)).clean();
                break;
            case "json":
            case "stringMap":
                value = value.replace(/&#10;/g,"");
                data = JSON.parse(value);
                break;
            default:
                data = value.replace(/&#10;/g,"");
                break;
        }
        return data;
    },
    stringToArray: function(string){
        return string.replace(/[\n\r]/g,",").replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
            return !!s;
        });
    },
    setDataWithPath: function(obj, path, data){
        var names = path.split(".");
        var d = obj;
        Array.each(names, function (n, idx) {
            if( idx === names.length -1 )return;
            if ( !d[n] ){
                var value = this.isNumberString( names[idx+1] ) ? [] : {};
                var n1 = this.isNumberString( n ) ? n.toInt() : n;
                d[n1] = value;
                d = d[n1];
            }else{
                d = d[n];
            }
        }.bind(this));
        d[names[names.length -1]] = data;
    },
    setDataWithField: function(obj, fieldName, path, isName){
        if(!this.pathIndexMap)this.pathIndexMap = {};

        if( !path )return;

        var json = this.importer.json;
        if( json.data[fieldName] ){
            var f = json.data[fieldName];

            json.data.columnList.each(function(json, i){
                if(json.path === f)this.pathIndexMap[path] = i;
            }.bind(this));

            var d = this.data;
            Array.each( f.split("."), function (n) {
                if(this.isNumberString(n))n = n.toInt();
                if (d) d = d[n];
            }.bind(this));
            if(!d)return;

            var _d = (typeOf(d) === "array" && d.length) ? d[0] : d;
            if( _d ){
                if(isName){
                    obj[path] = typeOf(_d) === "object" ? _d.distinguishedName : _d;
                }else{
                    obj[path] = d;
                }
            }
        }
    },
    isNumberString: function(string){
        return string.toInt().toString() === string;
    },
    parseCMSReadAndAuthor : function( data, t ){
        var cnArray = ["组织","群组","人员","人员","角色"];
        var keyArray = ["U","G","I","P","R"];
        if( typeOf(data) !== "array" )data = [data];
        return data.map( function( d ){
            var dn = typeOf( d ) === "string" ? d : d.distinguishedName;

            var name;
            if( typeOf(d) === "object" && d.name ){
                name = d.name;
            }else if( MWF.name && MWF.name.cn ){
                name = MWF.name.cn( dn );
            }else{
                name = dn.split("@")[0];
            }

            var index = keyArray.indexOf(dn.substr(dn.length-1, 1));
            if( index > -1 ){
                return {
                    "permission" : t,
                    "permissionObjectType": cnArray[ index ],
                    "permissionObjectName": name,
                    "permissionObjectCode": dn
                }
            }
        }).clean()
    },

    getSrcData: function(){
        var srcData = {};
        this.importer.json.data.columnList.each( function (columnJson, i) {
            if(columnJson.path)srcData[ columnJson.path ] = this.importedData[i] || "";
        }.bind(this));
        return srcData;
    },
    getResult: function(){
        if( this.importer.json.type === "cms" ){
            this.document.srcData = this.getSrcData(); //this.importedData;
            return this.document;
        }else if( this.importer.json.type === "process" ){
            this.work.srcData = this.getSrcData(); //this.importedData;
            return this.work;
        }else if( this.importer.json.type === "dynamicTable" ){
            this.data.srcData = this.getSrcData(); //this.importedData;
            return this.data;
        }
    }
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.query.Query.Importer.ExcelUtils = new Class({
    Extends: MWF.xApplication.Template.utils.ExcelUtils,
    initialize: function( options ){
        if(options)this.setOptions(options);
        this.sheet2JsonOptions = {header:1};
        this.pollyfill();
    }
});

MWF.xApplication.query.Query.Importer.ProgressBar = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        zindex: null,
        disableDetailButton: false
    },
    initialize : function( importer, options ){
        this.setOptions(options);
        this.importer = importer;
        this.actions = this.importer.lookupAction;
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
        this.openDlg();
        this.status = "ready";
    },
    openDlg: function () {
        var _self = this;
        this.contentNode = new Element("div",{"styles": this.css.processContentNode});
        var opt = {
            "style" : "user",
            "title": this.lp.importRecordDetail,
            "content": this.contentNode,
            "offset": {"y": 0},
            "isMax": false,
            "width": 500,
            "height": 200,
            "zindex": this.options.zindex,
            "buttonList": [
                {
                    "type": "openImportRecordDetail",
                    "text": this.lp.openImportRecordDetail,
                    "action": function () { _self.openImportRecordDetail(); }
                },
                {
                    "type": "cancel",
                    "text": this.lp.close,
                    "action": function () { this.dlg.close(); }.bind(this)
                }
            ],
            "onPostShow": function(){
                this.fireEvent("postShow");
            }.bind(this),
            "onPostLoad": function () {
                this.titleAction.hide();
                this.button.hide();
            },
            "onPostClose": function(){
                this.dlg = null;
            }.bind(this)
        };
        if( this.options.disableDetailButton ){
            opt.buttonList.splice(0, 1);
        }
        this.dlg = o2.DL.open(opt);

    },
    createNode: function( noProgress ){
        // var lp = this.lp;
        // this.maskNode = new Element("div",{"styles": this.css.maskNode}).inject(this.importer.container);
        //
        // this.node = new Element("div", {"styles": this.css.progressBarNode}).inject(this.importer.container);
        //
        // this.topNode =  new Element("div",{"styles": this.css.progressTopNode}).inject(this.node);
        //
        // this.subjectNode =  new Element("div",{"styles": this.css.progressSubjectNode}).inject(this.topNode);
        //
        // this.topCloseAction = new Element("div.topCloseAction", {
        //     "styles": this.css.progressTopCloseAction,
        //     "text": "x"
        // }).inject(this.topNode);
        // this.topCloseAction.hide();
        // this.topCloseAction.addEvent("click", function(){
        //     this.close();
        // }.bind(this))
        //
        // this.contentNode = new Element("div",{"styles": this.css.processContentNode}).inject(this.node);
        //
        // this.bottomNode = new Element("div", {"styles": this.css.progressBottomNode}).inject(this.node);
        // this.bottomNode.hide();
        //
        // this.closeAction = new Element("div.closeAction", {
        //     "styles": this.css.progressCloseAction,
        //     "text": lp.close
        // }).inject(this.bottomNode);
        // this.closeAction.addEvent("click", function(){
        //     this.close();
        // }.bind(this))
    },
    setContentHtml: function(noProgress){
        var lp = this.lp;
        var contentHTML = "";
        if (noProgress){
            // contentHTML = "<div style=\"height: 20px; line-height: 20px\">"+lp.readyToImportData1+"</div></div>" ;
            contentHTML =
                "<div style=\"overflow: hidden\">" +
                "   <div class='mwf_progressInforNode'>"+lp.readyToImportData1+"</div>"+
                "</div>" ;
            this.contentNode.set("html", contentHTML );
            this.progressNode = null;
            this.progressPercentNode = null;
            this.progressInforNode = this.contentNode.getElement(".mwf_progressInforNode");
            this.progressInforNode.setStyles(this.css.progressInforNode)
        }else{
            contentHTML =
                "<div style=\"overflow: hidden\">"+
                "   <div class='mwf_progressNode'>" +
                "       <div class='mwf_progressPercentNode'></div>"+
                "   </div>" +
                "   <div class='mwf_progressInforNode'>"+lp.readyToImportData1+"</div>"+
                "</div>" ;
            this.contentNode.set("html", contentHTML );
            this.progressNode = this.contentNode.getElement(".mwf_progressNode");
            this.progressNode.setStyles(this.css.progressNode);

            this.progressPercentNode = this.contentNode.getElement(".mwf_progressPercentNode");
            this.progressPercentNode.setStyles(this.css.progressPercentNode);

            this.progressInforNode = this.contentNode.getElement(".mwf_progressInforNode");
            this.progressInforNode.setStyles(this.css.progressInforNode)
        }
    },
    showCheckData : function(){
        // this.node.show();
        this.setContentHtml(true);
        this.setMessageTitle( this.lp.checkDataTitle );
        this.setMessageText( this.lp.checkDataContent );
        this.status = "check";
        // this.setSize();
    },
    showImporting: function( recordId, callback, date ){
        // this.node.show();
        this.setContentHtml();
        this.recordId = recordId;
        this.currentDate = date || new Date();
        this.intervalId = window.setInterval( function(){
            this.actions.getImportModelRecordStatus( this.recordId, function( json ){
                var data = json.data;
                this.status = data.status;
                if( data.status === "待导入" ) { //다른 사용자가 가져오기 중이거나 이전 가져오기가 아직 완료되지 않았습니다
                    this.setMessageTitle(this.lp.importWaitingTitle);
                    this.setMessageText(this.lp.importWaitingContent);
                }else if( data.status === "导入中" ){ //가져오기 중
                    this.setMessageTitle( this.lp.importDataTitle );
                    this.setMessageText( this.lp.importDataContent.replace( "{count}", data.executeCount));
                    this.updateProgress( data );
                }else{ //종료됨, 상태: "가져오기 성공","부분 성공","가져오기 실패"
                    if( callback )callback( data );
                    if( data.promise && typeOf(data.promise.then) === "function" ){
                        Promise.resolve( data.promise ).then(function () {
                            if(this.intervalId)window.clearInterval( this.intervalId );
                            this.transferComplete( data );
                        }.bind(this))
                    }else{
                        if(this.intervalId)window.clearInterval( this.intervalId );
                        this.transferComplete( data );
                    }
                }
            }.bind(this), null)
        }.bind(this), 500 );
        // this.setSize();
    },
    showCloseAction: function(){
        this.dlg.titleAction.show();
        this.dlg.button.show();

        // if( this.bottomNode )this.bottomNode.show();
        // if( this.topCloseAction )this.topCloseAction.show();
        // this.setSize();
    },
    close: function(){
        // this.maskNode.destroy();
        // this.node.destroy();
        this.dlg.close();
    },
    // setSize: function(){
    //     var containerSize = this.importer.container.getSize();
    //     var nodeSize = this.node.getSize();
    //     var top = (containerSize.y - nodeSize.y) / 2;
    //     var left = (containerSize.x - nodeSize.x) / 2;
    //     this.node.setStyles({
    //         "top": (top-40)+"px",
    //         "left": left+"px"
    //     })
    // },
    updateProgress: function(data){
        //status, data.executeCount, data.count, data.failCount
        var lp = this.lp;

        var total = data.count.toInt();
        var processed = data.executeCount.toInt();
        var failCount = data.failCount.toInt();

        var percent = 100*(processed/total);

        var sendDate = new Date();
        var lastDate = this.lastTime || this.currentDate;
        var ms = sendDate.getTime() - lastDate.getTime();
        var speed = ( (processed - ( this.lastProcessed || 0 )) * 1000)/ms ;
        var u = lp.importSpeed;
        speed = speed.round(2);

        this.progressPercentNode.setStyle("width", ""+percent+"%");

        var text = lp.importingDataContent.replace("{speed}",speed).replace("{total}",total).replace("{remaining}",( total - processed ));
        text += failCount ? lp.importingDataErrorContent.replace("{errorCount}",failCount) : "";
        this.progressInforNode.set("text", text);

        this.lastProcessed = processed;
        this.lastTime = new Date();
    },
    transferComplete: function( data ){
        var lp = this.lp;
        var sendDate = new Date();
        var ms = sendDate.getTime()-this.currentDate.getTime();
        if(ms<1000)ms = 1000;

        var timeStr = "";
        if (ms>3600000){
            var h = ms/3600000;
            var m_s = ms % 3600000;
            var m = m_s / 60000;
            var s_s = m_s % 60000;
            var s = s_s/1000;
            timeStr = ""+h.toInt()+lp.hour+m.toInt()+lp.mintue+s.toInt()+lp.second;
        }else if (ms>60000){
            var m = ms / 60000;
            var s_s = ms % 60000;
            var s = s_s/1000;
            timeStr = ""+m.toInt()+lp.mintue+s.toInt()+lp.second;
        }else{
            var s = ms/1000;
            timeStr = ""+s.toInt()+lp.second;
        }

        if( data.status === "导入成功" ){
            var size = data.count;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importSuccessTitle );
            var text = lp.importSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else if(data.status === "部分成功"){
            var size = data.count;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importPartSuccessTitle );
            var text = lp.importPartSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{errorCount}",data.failCount).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else{ //가져오기 실패
            var size = data.count;
            this.setMessageTitle( lp.importFailTitle );
            var text = lp.importFailContent.replace("{errorInfo}",data.distribution || "").replace("{total}",size).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }
        this.clearMessageProgress();
        this.showCloseAction();
    },
    setMessageText: function( text){
        this.progressInforNode.set("text", text);
    },
    setMessageTitle: function( text){
        this.dlg.titleText.set("text", text);
    },
    clearMessageProgress: function(){
        this.progressNode.destroy();
    },
    openImportRecordDetail: function () {
        MWF.xDesktop.requireApp("query.Query", "ImporterRecord", function () {
            var detail = new MWF.xApplication.query.Query.ImporterRecord.Detail(
                this.importer.container,
                this.importer.app,
                { importerId: this.importerId, recordId: this.recordId }
            );
            detail.load();
            this.dlg.close();
        }.bind(this), false);

    }
});
