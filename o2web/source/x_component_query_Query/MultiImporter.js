this.include({'type':'service', name:'o2ExcelUtilsV2'});
this.include({'type':'service', name:'o2PathDataUtils'});
MWF.xDesktop.requireApp("query.Query", "Importer", null, false);
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
MWF.QMultiImporter = new Class(
    {
        Implements: [Options, Events],
        Extends: MWF.widget.Common,
        options: {
            "style": "default"
        },
        initialize: function(container, json, options, app, parentMacro){
            //json 형식
            // var json = {  //사용할 가져오기 모델
            //         name: '계약 정보', //다운로드 파일
            //         importers: [{ //사용할 가져오기 모델
            //             application: app, //가져오기 모델이 속한 앱
            //             name: '프로젝트 기본 정보', //가져오기 모델 이름
            //             dataType: 'master', //첫 번째 주 문서. master가 여러 개면 순서대로, 첫 번째가 첫 번째 주 문서
            //             matchKeys: ['subject'] //DB와 일치시킬 필드. 가져온 문서의 존재 여부를 판단해 수정 또는 신규 작업 수행.
            //         },
            //         {
            //             application: app, //가져오기 모델이 속한 앱
            //             name: '외주 정보', //가져오기 모델 이름
            //             dataType: 'master', //두 번째 주 문서
            //             matchMasterKeys: [{ //주 문서와의 연결 필드
            //                 master: 'subject', //첫 번째 주 문서의 필드
            //                 slave: 'subject', //이 테이블의 필드
            //             }], //주 문서와 연결된 필드명
            //         },
            //          {
            //             application: app,
            //             name: '단일 지역',
            //             dataType: 'slave', //하위 데이터
            //             keepOldDataKeys: ['textfield_9_0'], //dataType이 slave일 때 유효. 설정 시 기존 데이터 유지, 아니면 비움. 이 필드로 가져온 데이터를 매칭해 일치하면 수정, 아니면 신규. 이 필드가 없으면 전체 덮어쓰기.
            //             path: 'datatemplate_9_0', //데이터 경로. 데이터 표: id.data, 데이터 템플릿: id
            //             matchMasterKeys: [{ //주 문서와의 연결 필드
            //                 master: 'subject', //주 문서의 필드
            //                 slave: 'subject', //이 테이블의 필드
            //             }], //주 문서와 연결된 필드명
            //         }, {
            //             application: app,
            //             name: '지급 정보',
            //             dataType: 'slave', //하위 데이터
            //             clearWithEmpty: false, //수정 시 주 문서에 데이터가 있으면 이 테이블의 해당 필드를 비울지 여부
            //             path: 'datatable_9.data', //데이터 경로. 데이터 표: id.data, 데이터 템플릿: id
            //             matchMasterKeys: [{ //주 문서와의 연결 필드
            //                 master: 'subject', //주 문서의 필드
            //                 slave: 'subject', //이 테이블의 필드
            //             }], //주 문서와 연결된 필드명
            //         }]
            //     };

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
            return new Promise((resolve)=>{
                var ps = this.json.importers.map( (importerJSON)=>{
                    var importer = new MWF.QMultiImporter.Importer(
                        this.container,
                        importerJSON,
                        this.options,
                        this.app,
                        this.parentMacro
                    );
                    importer.multiImporter = this;
                    return importer.load();
                });
                return Promise.all(ps).then((arr)=>{
                    this.importerList = [...arr];
                    this.fireEvent("queryLoad");
                    resolve(this);
                    return this;
                });
            });
        },
        checkImporters: function(){
            for( var i=1; i<this.importerList.length; i++ ){
                var impoter = this.importerList[i];
                if( !impoter.json.application ){
                    throw new Error(`앱(application)이 설정되지 않은 가져오기 모델 설정이 있습니다`);
                }
                if( !impoter.json.name ){
                    throw new Error(`이름(name)이 설정되지 않은 가져오기 모델 설정이 있습니다`);
                }
                if( !impoter.json.dataType ){
                    throw new Error(`유형(dataType)이 설정되지 않은 가져오기 모델 설정이 있습니다`);
                }
                if( !['master', 'slave'].includes(impoter.json.dataType) ){
                    throw new Error(`데이터 유형이 허용 범위를 벗어났습니다(dataType 값은 "master" 또는 "slave"여야 합니다)`);
                }
            }

            var mainType = this.getTargetType();
            var mainTarget = this.getTarget();

            var masters = this.importerList.filter((importer)=>{
                return importer.isMaster();
            });

            if(masters.length > 1){
                for( var i=1; i<masters.length; i++ ){
                    var impoter = masters[i];
                    if( mainType !== impoter.json.type ){
                        throw new Error(`주 문서의 가져오기 유형(가져오기 대상: 게시판 또는 프로세스)이 하나로 통일되어 있지 않습니다`);
                    }
                    var target = impoter.json.type === 'cms' ? impoter.json.data.category : impoter.json.data.process;
                    if( mainTarget.id !== target.id ){
                        throw new Error(`주 문서의 가져오기 대상이 일치하지 않습니다(${mainTarget.name}, ${target.name})`);
                    }
                    if( !impoter.json.matchMasterKeys || impoter.json.matchMasterKeys.length === 0 ){
                        throw new Error(`두 번째 주 문서(master)에 주 문서와의 매칭 관계(matchMasterKeys)가 설정되지 않았습니다`);
                    }
                }
            }

            var slaves = this.importerList.filter((importer)=>{
                return !importer.isMaster();
            });
            if(slaves.length > 0){
                for( var i=1; i<slaves.length; i++ ){
                    var impoter = slaves[i];
                    if( !impoter.json.path ){
                        throw new Error(`하위 문서(slave)에 경로(path)가 설정되지 않았습니다`);
                    }
                    if( !impoter.json.matchMasterKeys || impoter.json.matchMasterKeys.length === 0 ){
                        throw new Error(`하위 문서(slave)에 주 문서와의 매칭 관계(matchMasterKeys)가 설정되지 않았습니다`);
                    }
                }
            }
        },
        getMainImporter: function(){
            if( this.mainImporter ){
                return this.mainImporter;
            }
            var masters = this.importerList.filter((importer)=>{
                return importer.isMaster();
            });
            this.mainImporter = masters.length ? masters[0] : this.importerList[0];
            return this.mainImporter;
        },
        getTargetType: function(){
            //가져오기 유형, cms 또는 process
            return this.getMainImporter().json.type;
        },
        getTarget: function(){
            var mainImpoter = this.getMainImporter();
            return mainImpoter.json.type === 'cms' ?
                mainImpoter.json.data.category :
                mainImpoter.json.data.process;
        },
        getPathDataHandler: function (data) {
            return new MWF.PathDataHandler({
                type: this.getTargetType(),
                processIdType: (data.workCompletedId && 'workCompleted') || (data.workId && 'work') || 'job'
            });
        },
        createLoadding: function(){
            this.loadingAreaNode = new Element("div", {"styles": this.css.viewLoadingAreaNode}).inject(this.contentAreaNode);
            new Element("div", {"styles": {"height": "5px"}}).inject(this.loadingAreaNode);
            var loadingNode = new Element("div", {"styles": this.css.viewLoadingNode}).inject(this.loadingAreaNode);
            new Element("div", {"styles": this.css.viewLoadingIconNode}).inject(loadingNode);
            var loadingTextNode = new Element("div", {"styles": this.css.viewLoadingTextNode}).inject(loadingNode);
            loadingTextNode.set("text", "loading...");
        },
        copyView: function(){
            // {
            //     "type": "cms",
            //     "viewName": "계약 문서",
            //     "categoryList": [{
            //         "name": "계약 문서",
            //         "id": "f4bc07cb-fefd-4eb0-9a1e-d7795f8d9816"
            //     }],
            //     "processList": [{
            //         "name": "지표 프로세스",
            //         "alias": "",
            //         "id": "205d2d6d-8ed7-49e2-b788-1f35446a59da"
            //     }],
            //     "fieldList": ["subject"]
            // }
            var importer = this.getMainImporter();
            var type = this.getTargetType();
            var body = {
                type: type,
                viewName: this.json.name,
                fieldList: this.getMatchKeys()
            };
            if( body.fieldList.length === 0 ){
                return;
            }
            switch (type){
                case 'cms':
                    body.categoryList = [importer.json.data.category]; break;
                case 'process':
                    body.processList = [importer.json.data.process]; break;
                default:
                    this.notice('가져오기에 실패했습니다. 가져오기 모델의 대상이 데이터 테이블로 설정되어 있습니다. 관리자에게 문의하세요!', 'error');
                    return;
            }
            this.matchViewJson = o2.Actions.load('x_program_center').InvokeAction.execute('o2ExcelViewCopyer', body, (json)=>{
                return {
                    view: json.newViewId,
                    application: json.application
                };
            });
        },
        getMatchKeys: function(){
            return this.importerList.map((importer)=>{
                return this.getMatchKeysWithImporter(importer);
            }).flat(Infinity).unique();
        },
        getMatchKeysWithImporter: function(importer){
            if( importer.json.matchKeys ){
                return importer.json.matchKeys || [];
            }else if(importer.json.matchMasterKeys){
                return (importer.json.matchMasterKeys || []).map((matchKey)=>{
                    return matchKey.master;
                });
            }
        },
        getDateColIndexArray: function (){
            return this.importerList.map((importer)=>{
                return importer.getDateColIndexArray();
            });
        },
        importFromExcel: function(){
            // var masters = this.json.importers.filter((importer)=>{return importer.dataType === 'master';});
            // if(masters.length > 1){
            //     this.notice('가져오기에 실패했습니다. 두 개 이상의 가져오기 모델이 동시에 주 데이터 모델로 설정되어 있습니다. 관리자에게 문의하십시오!', 'error');
            //     return;
            // }

            var p = !this.importerList ? this.load() : null;
            Promise.resolve(p).then(()=>{
                try{
                    this.checkImporters();
                }catch (e) {
                    this.app.notice(e.message, 'error');
                    console.error(e);
                    return;
                }


                this.copyView();

                this.excelImporter = new MWF.QMultiImporter.ExcelImporter({
                    dateColIndexes: this.getDateColIndexArray(),
                    needSheetName: true
                });

                this.progressDialog = new MWF.QMultiImporter.ProgressDialog(this);
                this.progressDialog.multipleImporter = this;

                var ps = [], count=0;
                this.excelImporter.execute( function (importedDataList) {

                    this.importerList.forEach((importer)=>{
                        var dataExisted = importedDataList.some((imp)=>{
                            return imp.name === importer.json.name;
                        });
                        if(!dataExisted){
                            var errorText = `가져온 Excel 파일에서 가져오기 모델 "${importer.json.name}"과(와) 이름이 같은 시트를 찾지 못했습니다.
                            템플릿을 다시 내려받아 데이터를 입력한 뒤 다시 시도하세요.`;
                            this.app.notice(errorText, 'error');
                            throw new Error(errorText);
                        }
                    });

                    var checkOpenDlg = ()=>{
                        count++;
                        if( importedDataList.length === count ){
                            this.progressDialog.openDlg();
                        }
                    };

                    importedDataList.forEach((importedData, i)=>{
                        var {name, data} = importedData;
                        var importer = this.importerList.find((imp)=>{
                            return imp.json.name === name;
                        });
                        if( !importer ){
                            console.warn(`이름이 ${name}인 가져오기 모델을 찾지 못해 이 시트의 데이터 가져오기를 건너뜁니다.`);
                            checkOpenDlg();
                        }else{
                            var p = importer.importFromExcel(data, this.progressDialog, ()=>{
                                checkOpenDlg();
                            });
                            ps.push(p);
                        }
                    });
                    Promise.all(ps).then((arr)=>{
                        var hasError = arr.some((result)=>{
                            return result.status === 'error';
                        });
                        hasError ? this.openErrorDlg(arr) : this.doImportData(arr);
                    });
                }.bind(this));
            });
        },
        objectToString: function (obj, type) {
            if(!obj)return "";
            var arr = [];
            Object.each(obj,  function (value, key) {
                if( type === "style" ){
                    arr.push( key + ":"+ value +";" )
                }else{
                    arr.push( key + "='"+ value +"'" )
                }
            })
            return arr.join( " " )
        },
        openErrorDlg : function(resultList){
            if(this.progressDialog)this.progressDialog.close();

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
                    // {
                    //     "type": "exportWithError",
                    //     "text": this.lp.exportExcel,
                    //     "action": function () { _self.exportWithImportDataToExcel(); }
                    // },
                    {
                        "type": "cancel",
                        "text": this.lp.cancel,
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    var contentStyles = Object.clone( this.css.contentStyles );
                    if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
                    var contentStyleStr = this.objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

                    var htmlArray = [];
                    resultList.forEach((result)=>{
                        htmlArray.push(`<div style='font-size:16px;margin-bottom:5px;'>${result.importer.json.name}：</div>`)
                        htmlArray.push("<table "+ this.objectToString( this.css.properties ) +" style='"+this.objectToString( this.css.tableStyles, "style" )+"'>");
                        if(result.status==='error'){
                            htmlArray.push( result.importer._getErrorHeadHtml() );
                            htmlArray.push( result.importer._getErrorContentHtml() );
                        }else{
                            htmlArray.push(
                                `<tr><td style=${contentStyleStr}><div style='color:green;'>검증 통과</div></td></tr>`
                            );
                        }
                        htmlArray.push( "</table>" );
                        htmlArray.push("<div style='height:15px;'></div>")
                    })
                    div.set("html" , htmlArray.join(''));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },
        openResultDlg : function(resultList){
            if(this.progressDialog)this.progressDialog.close();


            var _self = this;

            var div = new Element("div", { style : "flex:1;height: calc( 100% - 30px );" });
            var dlg = o2.DL.open({
                "style" : "user",
                "title": '가져오기 상세',
                "content": div,
                "offset": {"y": 0},
                "isMax": true,
                "width": '90%',
                "height": '90%',
                "buttonList": [
                    {
                        "type": "cancel",
                        "text": '닫기',
                        "action": function () { dlg.close(); }
                    }
                ],
                "onPostShow": function () {
                    MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                        debugger;
                        this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), {
                            "statementName" : "o2ExcelMultipleImportDataLog",
                            "filter": [
                                {
                                    "path":"o.mainLogId", //쿼리 구문 형식은 jpql에서 o.title, 네이티브 sql에서 xtitle 사용
                                    "comparison":"equals",
                                    "value": this.log.id,
                                    "formatType":"textValue"
                                }
                            ]
                        }, {}, this.app, this.parentMacro);
                    }.bind(this));
                }.bind(this),
                "onPostClose": function(){
                    dlg = null;
                }.bind(this)
            });

        },
        getBusinessData : function(importer, d){
            var data;
            switch(importer.json.type){
                case 'cms': data = d.docData; break;
                case 'process': data = d.data; break;
                default: data = d.srcData; break;
            }
            return data;
        },
        parseData: function(resultList){
            console.log('resultList', resultList);

            var masters = resultList.filter((r)=>{return r.importer.isMaster();});
            var mainResult = masters[0] || null;

            resultList.forEach((result)=>{
                if( result !== mainResult ){
                    if( result.importer.isMaster() ){
                        result.masterDataMap = this.masterToMasterDataMap(result);
                    }else{
                        result.masterDataMap = this.slaveToMasterDataMap(result);
                    }
                }
            });

            //주 데이터 처리
            if(mainResult){
                var r = mainResult.importer;
                var masterData = r.getData();
                masterData.forEach((d)=>{
                    d.sheetNames = [r.json.name];
                });
                resultList.forEach((result)=>{
                    if( result === mainResult ){
                        return;
                    }
                    var matchMasterKeys = result.importer.json.matchMasterKeys;
                    masterData.forEach((d)=>{
                        var data = this.getBusinessData(r, d);
                        var key = matchMasterKeys.map((obj)=>{ return data[obj.master]; }).join('_');
                        if( result.masterDataMap[key] ){
                            d.sheetNames.push(result.importer.json.name);
                            if(result.importer.isMaster()){
                                var doc = result.masterDataMap[key].doc;
                                this._mergeObject(data, doc);
                            }else{
                                this.setDataWithPath(data, result.importer.json.path, result.masterDataMap[key].list);
                            }
                            delete result.masterDataMap[key];
                        }else if(result.importer.json.clearWithEmpty){
                            if(result.importer.isMaster()){
                                var emptyData = result.importer.getEmptyData();
                                this._mergeObject(data, emptyData);
                            }else{
                                this.setDataWithPath(data, result.importer.json.path, []);
                            }
                        }
                    });
                });
                mainResult.masterData = masterData;
            }

            //주 데이터 없는 하위 데이터 처리
            resultList.forEach((result)=>{
                if( result === mainResult ){
                    return;
                }
                var importer = result.importer;
                var matchMasterKeys = importer.json.matchMasterKeys;
                var masterBusinessData = [];
                Object.each(result.masterDataMap, (d, key)=>{
                    var data;
                    if( result.importer.isMaster() ){
                        if( d.doc ){
                            data = d.doc;
                            matchMasterKeys.forEach((obj)=>{
                                if( obj.master !== obj.slave ){
                                    data[obj.master] = data[obj.slave];
                                }
                            });
                            masterBusinessData.push(data);
                        }
                    }else{
                        if( d.list && d.list.length > 0 ){
                            data = {};
                            matchMasterKeys.forEach((obj)=>{
                                data[obj.master] = d.list[0][obj.slave];
                            });
                            this.setDataWithPath(data, d.path, d.list);
                            masterBusinessData.push(data);
                        }
                    }
                });
                result.masterBusinessData = masterBusinessData;
            });
        },
        _mergeObject : function(oldObj, newObj){
            for( var fieldName in newObj ){
                if( !oldObj.hasOwnProperty( fieldName ) ){
                    oldObj[fieldName] = newObj[ fieldName ];
                }
            }
        },
        masterToMasterDataMap: function (secondMasterResult){
            var r = secondMasterResult.importer;
            var slaveDataList = r.getData();
            var map = {};
            var matchMasterKeys = r.json.matchMasterKeys;
            slaveDataList.forEach((d)=>{
                var data = this.getBusinessData(r, d);
                var key = matchMasterKeys.map((obj)=>{ return data[obj.slave]; }).join('_');
                !map[key] && (map[key] = {key: key, path:r.json.path, doc: data});
            });
            return map;
        },
        slaveToMasterDataMap: function(slaveResult){
            var r = slaveResult.importer;
            var slaveDataList = r.getData();
            var map = {};
            var matchMasterKeys = r.json.matchMasterKeys;
            slaveDataList.forEach((d)=>{
                var data = this.getBusinessData(r, d);
                var key = matchMasterKeys.map((obj)=>{ return data[obj.slave]; }).join('_');
                !map[key] && (map[key] = {key: key, path:r.json.path, list: []});
                map[key].list.push(data);
            });
            return map;
        },
        matchDoc: function (result){
            if( !this.matchViewJson ){
                return;
            }
            var matchKeys = this.getMatchKeysWithImporter(result.importer);
            if( !matchKeys.length ){
                return;
            }
            var allList = [];
            var matchObjList = matchKeys.map((key)=>{
                var list = [];
                if( result.masterData ){
                    list = result.masterData.map((data)=>{
                        return this.getDataWithPath(data.docData || data.data || data.srcData, key);
                    });
                    allList = allList.concat(list);
                }else if(result.masterDataMap){
                    Object.each(result.masterDataMap, (data, key1)=>{
                        if( data.list ){
                            data.list.forEach((d)=>{
                                list.push( this.getDataWithPath(d, key));
                                allList = allList.concat(list);
                            });
                        }else if(data.doc){
                            list.push( this.getDataWithPath(data.doc, key));
                            allList = allList.concat(list);
                        }
                    });
                }
                return {key: key, list: list.filter((d)=>{ return !!d; }).unique()};
            });
            if( !allList.length ){
                return;
            }
            return Promise.resolve(this.matchViewJson).then((viewJson)=>{
                viewJson.filter = matchObjList.map((matchObj)=>{
                    return {
                        "logic":"and",
                        "path": matchObj.key,
                        "comparison":"in",
                        "value": matchObj.list.join(','),
                        "formatType":"textValue"
                    };
                });
                return o2.api.view.lookup(viewJson).then((data)=>{
                    //data는 반환된 데이터입니다.
                    var map = {};
                    data.grid.forEach((row)=>{
                        var mapKey = matchKeys.map(key=>{
                            return row.data[key];
                        }).join('_');
                        map[mapKey] = row;
                    });

                    if( result.masterData ){
                        result.masterData.forEach((data)=>{
                            var mapKey = matchKeys.map(key=>{
                                return this.getDataWithPath(data.docData || data.data || data.srcData, key);
                            }).join('_');
                            !!map[mapKey] && this._setDataWithViewData(map[mapKey], data);
                        });
                    }else if(result.masterBusinessData){
                        result.masterBusinessData.forEach((data)=>{
                            var mapKey = matchKeys.map(key=>{
                                return this.getDataWithPath(data, key);
                            }).join('_');
                            !!map[mapKey] && this._setDataWithViewData(map[mapKey], data);
                        });
                    }
                });
            });
        },
        _setDataWithViewData: function (viewData, data){
            data.id = viewData.bundle;
            !!viewData.data.workId && (data.workId = viewData.data.workId);
            !!viewData.data.workCompletedId && (data.workCompletedId = viewData.data.workCompletedId);
            !!viewData.data.completed && (data.completed = viewData.data.completed);
        },
        parseOperation: function(resultList){
            var mainImporter = this.getMainImporter();
            resultList.forEach((result)=>{
                if( result.importer === mainImporter ){
                    result.operationPaths = resultList.filter(item=>{
                        return item.importer.hasOperation();
                    }).map((item)=>{
                        return item.importer.json.path;
                    });
                }else if( result.importer.hasOperation() ){
                    result.operationPaths = [result.importer.json.path];
                }
            });
        },
        doImportData: function(resultList){

            debugger;

            resultList.forEach((r)=>{
                if( r.importer !== this.getMainImporter() ){
                    r.importer.progressBar.hide();
                }else{
                    r.importer.progressBar.setMessageTitle(`"${this.getTarget().name}"(으)로 데이터를 가져오는 중`);
                }
            })

            // if(this.progressDialog)this.progressDialog.close();
            //데이터 생성
            //데이터 재검증(계산된 내용)
            var date = new Date();

            this.parseData(resultList);

            this.parseOperation(resultList);

            //가져온 주 문서 총수
            this.totalCount = 0;
            resultList.forEach( (result)=>{
                if( result.masterData ) {
                    result.totalCount = result.masterData.length;
                    this.totalCount += result.totalCount;
                }else if(result.masterDataMap){
                    result.totalCount = Object.keys(result.masterDataMap).length;
                    this.totalCount += result.totalCount;
                }
            })

            if(this.totalCount === 0){
                if(this.progressDialog)this.progressDialog.close();
                this.notice('Excel 파일에서 가져올 데이터를 찾지 못해 가져오기를 종료합니다', 'error')
            }

            var actionPromises = [];
            var completedCount = 0;
            var checkAllCompleted = ()=>{
                completedCount++;
                if( resultList.length === completedCount ){
                    Promise.allSettled(actionPromises).then(() => {
                        this._saveLog();
                    });
                }
            }
            //주 데이터 저장
            resultList.forEach( (result)=>{
                var p = this.matchDoc(result);
                Promise.resolve(p).then(()=>{

                    console.log(result.masterData || result.masterBusinessData);

                    if( result.masterData ){
                        result.masterData.forEach((d)=>{
                            var promise;
                            switch (this.getTargetType()) {
                                case 'cms':
                                    promise = !!d.id ? this.updateDocument(result, d) : this.createDocument(result, d); break;
                                case 'process':
                                    promise = !!d.id ? this.updateWork(result, d) : this.createWork(result, d); break;
                            }
                            actionPromises.push(promise);
                        });
                    }else{
                        var isMaster = result.importer.isMaster();
                        result.masterBusinessData.forEach((d)=>{
                            if(!d.id){
                                this.logFailure(result, d, '키 값으로 시스템과 주 테이블에서 일치하는 주 데이터를 찾지 못했습니다.');
                            }else{
                                var promise;
                                switch (this.getTargetType()) {
                                    case 'cms':
                                        promise = isMaster ? this.updateDocument(result, d, true) : this.updateDocumentPartData(result, d); break;
                                    case 'process':
                                        promise = isMaster ? this.updateWork(result, d, true) : this.updateWorkPartData(result, d); break;
                                }
                                actionPromises.push(promise);
                            }
                        });
                    }

                    checkAllCompleted();
                })
            });
        },
        getErrorText: function (xhr) {
            var responseJSON = JSON.parse( (err.xhr || err).responseText || '' );
            return responseJSON.message; //message는 오류 안내 텍스트
        },
        getKeepOldDataConfigs: function(data){
            if(this.keepOldDataConfig)return this.keepOldDataConfig;
            this.keepOldDataConfig = this.json.importers.filter((importer)=>{
                return importer.keepOldDataKeys && importer.keepOldDataKeys.length > 0;
            });
            return this.keepOldDataConfig;
        },
        getPathDataMatchId: function (data){
            return this.getTargetType() === 'process' ?
                data.workCompletedId || data.workId || data.id :
                data.id;
        },
        handleKeepOldData: function (result, data){
            var configs = this.getKeepOldDataConfigs();
            var d = data.docData || data.data || data.srcData || data;
            return configs.map(config => {
                var pathData = this.getDataWithPath(d, config.path) || [];
                if( pathData.length ){

                    var handler = this.getPathDataHandler( data );
                    var id = this.getPathDataMatchId(data);
                    var p = handler.get(id, config.path.split('.')) || [];
                    p.then((json)=>{
                        var oldData = json.data || [];
                        var needPushData = [];
                        pathData.forEach(newObj=>{
                            var matchOldObj = oldData.find(oldObj=>{
                                var isMatch = true;
                                for( var i=0; i<config.keepOldDataKeys.length; i++ ){
                                    var key = config.keepOldDataKeys[i];
                                    if( !newObj[key] || newObj[key] !== oldObj[key] ){
                                        isMatch = false;
                                    }
                                }
                                return isMatch;
                            });
                            if( matchOldObj ){
                                for(var key in newObj){
                                    matchOldObj[key] = newObj[key];
                                }
                            }else{
                                needPushData.push(newObj);
                            }
                        });
                        if(needPushData.length > 0){
                            oldData.push(...needPushData);
                        }
                        this.setDataWithPath(d, config.path, oldData);
                    });
                    return p;
                }
            });
        },
        handleOperation: function (result, data) {
            var operationPaths = result.operationPaths;
            if( !operationPaths || !operationPaths.length ){
                return [];
            }
            //하위 데이터 중 $operation이 있는 항목 처리(덮어쓰기/신규 포함)
            var d = data.docData || data.data || data.srcData || data;
            return operationPaths.map( (operationPath)=>{
                var pathData = this.getDataWithPath(d, operationPath) || [];
                var pathImporter = this._getImporterWithPath(operationPath);
                if( !pathData.length && pathImporter?.json.clearWithEmpty ){
                    return;
                }
                var hasCover = pathData.some(item=>{
                    return item.$operation === '덮어쓰기';
                });
                if( !hasCover ){
                    var handler = this.getPathDataHandler( data );
                    var id = this.getPathDataMatchId(data);
                    var p = handler.get(id, operationPath.split('.')) || [];
                    p.then((json)=>{
                        var oldData = json.data || [];
                        oldData = oldData.concat(pathData);
                        this.setDataWithPath(d, operationPath, oldData);
                    });
                    return p;
                }
            });
        },
        _getImporterWithPath: function (path){
            return this.importerList.find(function(importer){
                return importer.json.path === path;
            });
        },
        updateDocumentPartData: function(result, data){
            var ps1 = this.handleOperation(result, data);
            return Promise.all(ps1).then(() => {
                var ps2 = this.handleKeepOldData(result, data);
                return Promise.all(ps2).then(()=> {
                    const method = o2.Actions.load('x_cms_assemble_control').DataAction.updateWithDocument;
                    return method(data.id, data, (json) => {
                        return this.logSuccess(result, data, '데이터 수정');
                    }, (xhr) => {
                        this.logFailure(result, data, this.getErrorText(xhr));
                        return true;
                    });
                });
            })
        },
        updateDocument: function(result, data, partFlag){
            var ps1 = this.handleOperation(result, data);
            return Promise.all(ps1).then(() => {
                var ps2 = this.handleKeepOldData(result, data);
                return Promise.all(ps2).then(()=>{
                    const method = o2.Actions.load('x_cms_assemble_control').DataAction.updateWithDocument;
                    return method(data.id, partFlag ? data : data.docData, (json)=>{
                        return this.logSuccess(result, data, '데이터 수정');
                    }, (xhr)=>{
                        this.logFailure(result, data, this.getErrorText(xhr));
                        return true;
                    });
                });
            })
        },
        createDocument: function(result, data){
            const method = o2.Actions.load('x_cms_assemble_control').DocumentAction.persist_publishContent;
            return method(data, (json)=>{
                data.id = json.data.id;
                return this.logSuccess(result, data, '문서 생성');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        updateWorkPartData: function(result, data){
            var ps1 = this.handleOperation(result, data);
            return Promise.all(ps1).then(() => {
                var ps2 = this.handleKeepOldData(result, data);
                return Promise.all(ps2).then(()=> {
                    const method = o2.Actions.load('x_processplatform_assemble_surface').DataAction.updateWithJob;
                    return method(data.id, data, (json) => {
                        return this.logSuccess(result, data, '데이터 수정');
                    }, (xhr) => {
                        this.logFailure(result, data, this.getErrorText(xhr))
                        return true;
                    })
                });
            })
        },
        updateWork: function(result, data, partFlag){
            var ps1 = this.handleOperation(result, data);
            return Promise.all(ps1).then(() => {
                var ps2 = this.handleKeepOldData(result, data);
                return Promise.all(ps2).then(()=> {
                    const method = o2.Actions.load('x_processplatform_assemble_surface').DataAction.updateWithJob;
                    return method(data.id, partFlag ? data : data.data, (json) => {
                        return this.logSuccess(result, data, '데이터 수정');
                    }, (xhr) => {
                        this.logFailure(result, data, this.getErrorText(xhr))
                        return true;
                    })
                });
            })
        },
        createWork: function(result, data){
            var mainImpoter = this.getMainImporter();
            if( mainImpoter.json.data.processStatus === "completed" ){
                this._createWorkCompleted(result, data);
            }else{
                this._createWork(result, data);
            }
        },
        _createWorkCompleted: function(result, data){
            const method = o2.Actions.load('x_processplatform_assemble_surface').WorkCompletedAction.create;
            return method(this.getTarget().id, data, (json)=>{
                data.id = json.data.id;
                return this.logSuccess(result, data, '프로세스 기안(종료)');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        _createWork: function(result, data){
            const method = o2.Actions.load('x_processplatform_assemble_surface').WorkAction.create;
            return method(this.getTarget().id, data, (json)=>{
                data.id = json.data[0].job;
                return this.logSuccess(result, data, '프로세스 기안');
            }, (xhr)=>{
                this.logFailure(result, data, this.getErrorText(xhr));
                return true;
            });
        },
        logFailure: function(result, data, errorText){
            return this._addlog(result, data, null, errorText);
        },
        logSuccess: function(result, data, operationType){
            return this._addlog(result, data, operationType);
        },
        _addlog: function(result, data, operationType, errorText, saveFlag){
            var importer = result.importer;
            var mainImpoter = this.getMainImporter();
            if(!this.log){
                this.log = {
                    "person": layout.session.user.distinguishedName,
                    "fileName": this.excelImporter.file.name,
                    "totalCount": this.totalCount,
                    "successCount": 0,
                    "failureCount": 0,
                    "importerName": this.importerList.map((importer)=>{return importer.json.name}),
                    "importer": this.importerList.map((importer)=>{return importer.json.id}),
                    "targetType": mainImpoter.json.type,
                    "targetName": this.getTarget().name,
                    "targetId": this.getTarget().id,
                }
                !errorText ? this.log.successCount++ : this.log.failureCount++;

                mainImpoter.progressBar.showImporting(this.log);

                this.logTable = new o2.api.Table('o2ExcelMultipleImportLog');
                this.logTable.addRow(this.log, (json)=>{
                    this.log.id = json.data.id;
                    return importer.saveLog(this.log, data, operationType, errorText);
                }, null, false);
            }else{
                !errorText ? this.log.successCount++ : this.log.failureCount++;

                mainImpoter.progressBar.showImporting(this.log);

                if(!!saveFlag)this.logTable.updateRow(this.log.id, this.log);
                return importer.saveLog(this.log, data, operationType, errorText);
            }
        },
        _saveLog: function(){
            if(this.log && this.logTable){
                return this.logTable.updateRow(this.log.id, this.log, null, null, false);
            }
        },
        getDataWithPath: function(obj, path){
            var idList = path.split(".");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){
                var id = idList[i];
                if( !id && id !== 0 )return null;
                if( ["object","array"].contains(o2.typeOf(obj)) ){
                    if( i === lastIndex ){
                        return obj[id];
                    }else{
                        obj = obj[id];
                    }
                }else{
                    return null;
                }
            }
        },
        setDataWithPath: function(obj, path, data){
            var isNumberString = function(string){
                return string.toInt().toString() === string;
            }
            var names = path.split(".");
            var d = obj;
            Array.each(names, function (n, idx) {
                if( idx === names.length -1 )return;
                if ( !d[n] ){
                    var value = isNumberString( names[idx+1] ) ? [] : {};
                    var n1 = isNumberString( n ) ? n.toInt() : n;
                    d[n1] = value;
                    d = d[n1];
                }else{
                    d = d[n];
                }
            }.bind(this));
            d[names[names.length -1]] = data;
        },

        exportWithImportDataToExcel : function ( importData ) {

            if( !this.excelExporter ){
                this.excelExporter = new MWF.QMultiImporter.ExcelExporter();
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

                this.excelExporter.execute(
                    arg.data,
                    arg.title,
                    arg.colWidthArray,
                    this.getDateIndexArray()
                )
            }.bind(this);

            if( !this.importerJsonList ){
                this.getImporterJsonList( function () {
                    exportTo();
                }.bind(this))
            }else{
                exportTo();
            }
        },


        downloadTemplate: function(fileName, callback){

            var p = !this.importerList ? this.load() : null;

            return Promise.resolve(p).then(()=>{

                var args = this.importerList.map((importer)=>{
                    return {
                        data : [importer.getTitleArray()],
                        sheetName: importer.getFileName(),
                        colWidthArray : importer.getColWidthArray(),
                        dateIndexArray: importer.getDateIndexArray()
                    };
                });

                //if(callback)callback(args);

                var excelExporter = new MWF.QMultiImporter.ExcelExporter({
                    fileName: this.getFileName(fileName),
                    worksheet: args.map((arg)=>{
                        return {
                            isTemplate: true,
                            sheetName: arg.sheetName,
                            colWidthArray: arg.colWidthArray,
                            dateIndexArray: arg.dateIndexArray
                        };
                    })
                });

                return excelExporter.execute(
                    args.map( (arg)=> {return arg.data;} ),
                    callback
                );
            });
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

MWF.QMultiImporter.Importer = new Class({
    Extends: MWF.QImporter,
    load: function( callback ){
        //this.excelUtils = new MWF.xApplication.query.Query.Importer.ExcelUtils();
        var ps = [this.getImporterJSON(), this.loadMacro()];
        return Promise.all(ps).then(()=>{
            this._loadModuleEvents();
            this.fireEvent("queryLoad");
            //this.importFromExcel();
            if(callback)callback();
            return this;
        });
    },
    isMaster: function (){
        return this.json.dataType === 'master';
    },
    hasOperation: function(){
        if( this.isMaster() ){
            return false;
        }
        return this.json.data.columnList.some((column)=>{
            return column.path === "$operation";
        });
    },
    getEmptyData: function(){
        var data = {};
        this.json.data.columnList.each( function (columnJson, i) {
            var dataType = this.json.type === "dynamicTable" ? columnJson.dataType_Querytable : columnJson.dataType_CMSProcess;
            var value;
            switch ( dataType ) {
                case "string":
                    value = ''; break;
                case "stringList":
                    value = []; break;
                case "number":
                case "integer":
                case "long":
                case "double":
                    value = ''; break;
                case "numberList":
                case "integerList":
                case "longList":
                case "doubleList":
                    value = []; break;
                case "date":
                case "dateTime":
                    value = ''; break;
                case "dateList":
                case "dateTimeList":
                    value = []; break;
                case "boolean":
                    value = ''; break;
                case "booleanList":
                    value = []; break;
                case "json":
                case "stringMap":
                    value = ''; break;
                default:
                    value = ''; break;
            }
            this.multiImporter.setDataWithPath(data, columnJson.path, value);
        }.bind(this));
        return data;
    },
    getSampleData: function (d){
        var mainImporter = this.multiImporter.getMainImporter();
        var data = this.multiImporter.getBusinessData(mainImporter, d) || d;
        var sampleData = [];
        var columnList = mainImporter.json.data.columnList;
        for( var i=0; i<columnList.length && i<10; i++ ){
            var column = columnList[i];
            sampleData.push({
                t: column.displayName,
                v: this.multiImporter.getDataWithPath(data, column.path) || ''
            });
        }
        return sampleData;
    },
    saveLog: function(mainLog, data, operationType, errorText){
        if( !this.logTable )this.logTable = new o2.api.Table("o2ExcelMultipleImportDataLog");
        // {
        //     "person": "",
        //     "fileName": "",
        //     "sheetName": "",
        //     "status": "",
        //     "errorText": "",
        //     "importerName": "",
        //     "importer": "",
        //     "targetType": "",
        //     "targetName": "",
        //     "targetId": "",
        //     "data": "",
        //     "docId": "",
        //     "mainLogId": ""
        // }
        var log = {
            ...mainLog,
            sheetName: data.sheetNames?.length ? data.sheetNames.join(', ') : this.json.name,
            status: !errorText ? 'success' : 'failure',
            errorText: errorText || '',
            importerName: this.json.name,
            importer: this.json.id,
            data: JSON.stringify(data),
            sampleData: JSON.stringify(this.getSampleData(data)),
            docId: data.id,
            mainLogId: mainLog.id,
            operationType: operationType
        }
        delete log.id;
        return this.logTable.addRow(log);
    },
    importFromExcel: function (importedData, progressDialog, callback){
        return new Promise((resolve)=>{
            if( this.importerJson && this.json ){
                this._importFromExcel(importedData, progressDialog, resolve, callback);
            }else{
                this.load(()=>{
                    this._importFromExcel(importedData, progressDialog, resolve, callback);
                });
            }
        });
    },
    _importFromExcel: function( importedData, progressDialog, resolve, callback){

        this.rowList = [];

        this.identityMapImported = {};
        this.personMapImported = {};
        this.unitMapImported = {};
        this.groupMapImported = {};

        this.lookupAction.getUUID( function (json){
            this.recordId = json.data;
        }.bind(this), null, false);

        importedData = importedData.filter(function (array) {
            for( var i=0; i<array.length; i++ ){
                if(array[i])return true;
            }
            return false;
        });
        if( !importedData.length ){
            if(callback)callback();
            resolve({status: 'success', importer: this});
        }

        var titleLength = importedData[0].length;
        importedData.each(function (array) {
            for( var i=0; i<titleLength; i++){
                if( typeOf(array[i]) === "null" )array[i] = "";
            }
        });

        progressDialog.addEvent('postShow', function(){

            this.progressBar = new MWF.QMultiImporter.ProgressBar(this, progressDialog);

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
                            this.rowList.push( new MWF.QMultiImporter.Row( this, lineData, lineIndex ) );
                        }.bind(this));

                        var isValid = this.json.enableValid ? this.checkImportedData() : this.checkNecessaryImportedData();
                        Promise.resolve(isValid).then(function ( isValid1 ) {
                            if( isValid1 ){
                                //this.doImportData(resolve);
                                resolve({status: 'success', importer: this});
                            }else{
                                //this.openImportedErrorDlg();
                                this.progressBar.setMessageText('가져오기 실패: 검증을 통과하지 못했습니다');
                                resolve({status: 'error', importer: this});
                            }
                        }.bind(this));
                    }.bind(this)
                );
            }.bind(this));

            this.progressBar.importerId = this.importerId;
        }.bind(this));

        if(callback)callback();
    },
    //**구버전 호환, 신버전에서 수정됨 start
    loadMacro: function (callback) {
        return new Promise((resolve) => {
            MWF.require("MWF.xScript.Macro", function () {
                this.Macro = new MWF.Macro.ViewContext(this);
                if (callback) callback();
                resolve();
            }.bind(this));
        })
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
    }
    //**구버전 호환, 신버전에서 수정됨 end
});



MWF.QMultiImporter.Row = new Class({
    Extends: MWF.QImporter.Row,
    checkCMS : function( notCheckName ){
        if( !this.importer.isMaster() ){
            return;
        }

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

        if( this.importer.isMaster() ){
            return;
        }

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
    //구버전 호환, 신버전에서 수정됨 start
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
    }
    //구버전 호환, 신버전에서 수정됨 end
});

//MWF.xDesktop.requireApp("Template", "utils.ExcelUtilsV2", null, false);

MWF.QMultiImporter.ExcelImporter = new Class({
    Extends: MWF.ExcelImporter
});

MWF.QMultiImporter.ExcelExporter = new Class({
    Extends: MWF.ExcelExporter
});

MWF.QMultiImporter.ProgressDialog = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        zindex: null,
        disableDetailButton: false
    },
    initialize : function( importer, options ){
        this.setOptions(options);
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
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
            "height": 400,
            "zindex": this.options.zindex,
            "buttonList": [
                {
                    "type": "openImportRecordDetail",
                    "text": this.lp.openImportRecordDetail,
                    "action": function () { _self.multipleImporter.openResultDlg(); }
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
    showCloseAction: function(){
        this.dlg.titleAction.show();
        this.dlg.button.show();

        // if( this.bottomNode )this.bottomNode.show();
        // if( this.topCloseAction )this.topCloseAction.show();
        // this.setSize();
    },
    close: function(){
        this.dlg.close();
    }
});

MWF.QMultiImporter.ProgressBar = new Class({
    Extends: MWF.QImporter.ProgressBar,
    initialize : function( importer, dlg, options ){
        this.setOptions(options);
        this.importer = importer;
        this.dlg = dlg;
        this.actions = this.importer.lookupAction;
        this.lp = MWF.xApplication.query.Query.LP;
        this.css = importer.css;
        this.container = dlg.contentNode;
        this.titleNode = new Element('div', {
            text: this.importer.json.name,
            style: 'color:#666'
        }).inject(this.container);
        this.contentNode = new Element('div', {style: 'margin-bottom:10px;'}).inject(this.container);
        this.status = "ready";

        this.lp = Object.assign(this.lp, {
            "openImportRecordDetail": "가져오기 로그 보기",
            "importSuccess" : "문서를 가져왔습니다!",
            "importPartSuccessTitle": "일부 가져오기 성공",
            "importFailTitle": "가져오기 실패",
            "checkDataTitle": "데이터 가져오기",
            "checkDataContent": "데이터 확인 중...",
            "importDataTitle": "데이터 가져오는 중",
            "importDataContent": "문서 가져오기를 시작합니다. 총 {count}건",
            "readyToImportData": "문서 가져오기 준비 중",
            "readyToImportData1": "문서 가져오기 준비 중...",
            "importSpeed": "건/초",
            "importingDataContent": "문서 가져오는 중: {speed}건/초, 총 {total}건, 남은 {remaining}건",
            "importingDataErrorContent": ", 오류 {errorCount}건",
        })

    },
    hide: function (){
        this.titleNode.hide();
        this.contentNode.hide();
    },
    showImporting: function( log, callback ){
        // this.node.show();
        this.setContentHtml();
        if( !this.currentDate )this.currentDate = new Date();

        var executeCount = log.successCount + log.failureCount;
        if( log.totalCount > executeCount ){ //가져오기 중
            //this.setMessageTitle( this.lp.importDataTitle );
            this.setMessageText( this.lp.importDataContent.replace( "{count}", log.totalCount ) );
            this.updateProgress( log );
        }else{ //종료됨, 상태: "가져오기 성공","부분 성공","가져오기 실패"
            this.transferComplete( log );
            if( callback )callback( log );
        }

        // this.setSize();
    },
    updateProgress: function(log){
        //status, data.executeCount, data.count, data.failCount
        var lp = this.lp;

        var total = log.totalCount;
        var processed = log.successCount + log.failureCount;
        var failCount = log.failureCount;

        var percent = 100*(processed/total);

        var sendDate = new Date();
        var lastDate = this.lastTime || this.currentDate;
        var ms = sendDate.getTime() - lastDate.getTime();
        var speed = ( (processed - ( this.lastProcessed || 0 )) * 1000)/ms ;
        var u = lp.importSpeed;
        speed = speed.round(2);

        this.progressPercentNode.setStyle("width", ""+percent+"%");

        var text = lp.importingDataContent.replace("{speed}",(speed === Infinity || speed === -Infinity) ? '' : speed).replace("{total}",total).replace("{remaining}",( total - processed ));
        text += failCount ? lp.importingDataErrorContent.replace("{errorCount}",failCount) : "";
        this.progressInforNode.set("text", text);

        this.lastProcessed = processed;
        this.lastTime = new Date();
    },
    transferComplete: function( log ){
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

        if( log.totalCount === log.successCount ){
            var size = log.totalCount;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importSuccessTitle );
            var text = lp.importSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else if(log.successCount > 0){
            var size = log.totalCount;
            var speed = (size * 1000)/ms ;
            var u = lp.importSpeed;
            speed = speed.round(2);

            this.setMessageTitle( lp.importPartSuccessTitle );
            var text = lp.importPartSuccessContent.replace("{total}",size).replace("{speed}",speed).replace("{errorCount}",log.failureCount).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }else{ //가져오기 실패
            var size = log.totalCount;
            this.setMessageTitle( lp.importFailTitle );
            var text = lp.importFailContent.replace("{errorInfo}",log.distribution || "").replace("{total}",size).replace("{timeStr}",timeStr);
            this.setMessageText( text );
        }
        this.clearMessageProgress();
        this.showCloseAction();
    },
    showCloseAction: function(){
        this.dlg.dlg.titleAction.show();
        this.dlg.dlg.button.show();
    },
    close: function(){
        this.dlg.dlg.close();
    },
    setMessageTitle: function( text){
        //this.dlg.titleText.set("text", text);
        this.titleNode.textContent = text;
    }
});
