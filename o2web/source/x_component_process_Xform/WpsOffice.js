MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class WpsOffice Wps 컴포넌트.
 * @o2cn WpsOffice
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var wpsOffice = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var wpsOffice = this.target; //컴포넌트 자체 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.WpsOffice = MWF.APPWpsOffice =  new Class(
    /** @lends MWF.xApplication.process.Xform.WpsOffice# */
    {
        Extends: MWF.APP$Module,
        isActive: false,
        options:{
            "version": "wpsWebOffice",
            /**
             * 문서 열기 전 이벤트.
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#beforeOpen
             */
            /**
             * 문서 열기 후 이벤트.
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#afterOpen
             */
            /**
             * 저장 후 이벤트.
             * @since V8.0
             * @event MWF.xApplication.process.Xform.WpsOffice#afterSave
             */
            "moduleEvents": ["beforeOpen",
                "afterOpen",
                "afterSave"
            ]
        },
        initialize: function(node, json, form, options){

            this.node = $(node);
            this.node.store("module", this);
            this.json = json;
            this.form = form;
            this.documentId = "";
            this.mode = "write";

            this.officeType = {
                "docx" : "Writer",
                "doc" : "Writer",
                "xlsx" : "Spreadsheet",
                "xls" : "Spreadsheet",
                "pptx" : "Presentation",
                "ppt" : "Presentation",
                "pdf" : "Pdf",
                "ofd" : "Pdf"
            };

            this.appToken = "x_processplatform_assemble_surface";
            this.version = this.options.version;
        },
        _loadUserInterface: function(){
            this.node.empty();
            if (!this.isReadable){
                this.node?.addClass('hide');
                return '';
            }
            this.node.setStyles({
                "min-height": "700px"
            });
        },
        _afterLoaded: function(){
            if (!this.isReadable){
                this.node?.addClass('hide');
                return '';
            }

            if(!layout.serviceAddressList["x_wpsfile_assemble_control"]){
                this.node.set("html","<h3><font color=red>please install wps application</font></h3>");
                return false;
            }
            if(this.mode !== "read" && this.json.allowUpload){
                this.createUpload();
            }
            this.action = o2.Actions.load("x_wpsfile_assemble_control");
            if (!this.json.isNotLoadNow){
                this.data = this.getData();
                if(this.data.documentId === ""){

                    if (this.json.officeType === "other" && this.json.templateType === "script"){
                        this.json.template = this.form.Macro.exec(this.json.templeteScript.code, this);
                    }

                    this[this.json.officeType === "other"&&this.json.template !== ""? "createDocumentByTemplate":"createDocument"](function (){
                        this.loadDocument();
                    }.bind(this));



                }else {
                    this.documentId = this.data.documentId;
                    this.loadDocument();
                }
            }
        },
        createUpload : function (){


            this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);
            var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
            uploadBtn.addEvent("click",function (){
                o2.require("o2.widget.Upload", null, false);
                var upload = new o2.widget.Upload(this.content, {
                    "action": o2.Actions.get(this.appToken).action,
                    "method": "uploadAttachment",
                    "accept" : ".docx,.xlsx,.pptx,.pdf,.ofd",
                    "parameter": {
                        "id" : this.form.businessData.work.id
                    },
                    "data":{
                    },
                    "onCompleted": function(data){

                        o2.Actions.load(this.appToken).AttachmentAction.delete(this.documentId,function( json ){
                        }.bind(this));

                        this.documentId = data.id;
                        this.reload();
                    }.bind(this)
                });

                upload.load();
            }.bind(this));

        },
        reload : function (){
            if (!this.isReadable){
                this.node?.addClass('hide');
                return '';
            }
            this.setData();
            this.node.empty();
            this.createUpload();
            this.loadDocument();
        },
        createDocumentByTemplate : function (callback){
            if (this.form.businessData.work.startTime){
            }else{
                this.form.saveFormDataDraftSync();
            }
            this.action.CustomAction.getInfo(this.json.template).then(function(json) {
                var data = {
                    "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.extension,
                    "fileType": json.data.extension,
                    "appToken" : this.appToken,
                    "workId" : this.form.businessData.work.id,
                    "site" : "filetext",
                    "tempId": this.json.template
                };

                this.action.CustomAction.createForO2(data,
                    function( json ){
                        this.documentId = json.data.fileId;
                        this.setData();
                        if (callback) callback();
                    }.bind(this),null, false
                );

            }.bind(this))
        },
        createDocument : function (callback){
            if (this.form.businessData.work.startTime){
            }else{
                this.form.saveFormDataDraftSync();
            }
            var data = {
                "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.json.officeType,
                "appToken" : this.appToken,
                "workId" : this.form.businessData.work.id,
                "site" : "filetext"
            };
            this.action.CustomAction.createForO2(data,
                function( json ){
                    this.documentId = json.data.fileId;
                    this.setData();
                    if (callback) callback();
                }.bind(this),null, false
            );
        },
        loadDocument: function () {
            o2.Actions.load(this.appToken).AttachmentAction.getOnlineInfo(this.documentId, function( json ){

                this.documentData = json.data;

                this.fileName = this.documentData.name;
                this.extension = this.documentData.extension;

                this.getEditor(function () {
                    this.loadApi(function (){
                        this.loadEditor();
                    }.bind(this));
                }.bind(this));

            }.bind(this),null,false);

        },
        loadApi : function (callback){

            if(this.version === "wpsWebOffice"){
                o2.load(["../x_component_WpsOfficeEditor/web-office-sdk-solution-v2.0.2.umd.min.js"], {"sequence": true}, function () {
                    if (callback) callback();
                }.bind(this));
            }else if(this.version === "jbWps"){
                o2.load(["../x_component_WpsOfficeEditor/open-jssdk-v0.1.2.umd.js"], {"sequence": true}, function () {
                    if (callback) callback();
                }.bind(this));
            }else {
                o2.load(["../x_component_WpsOfficeEditor/web-office-sdk-v1.1.19.umd.js"], {"sequence": true}, function () {
                    if (callback) callback();
                }.bind(this));
            }

        },
        getEditor: function (callback) {
            if (this.isReadonly()){
                this.mode  = "read";
            }else{
                if (this.json.readScript && this.json.readScript.code){
                    var flag = this.form.Macro.exec(this.json.readScript.code, this);
                    if (flag){
                        this.mode = "read";
                    }
                }
            }

            if(this.action.ConfigAction.getBaseConfig){
                this.action.ConfigAction.getBaseConfig(function( json ){
                    this.appId = json.data.appId;
                    this.version = json.data.version;
                    if (callback) callback();
                }.bind(this),null,false);
            }else {
                this.action.ConfigAction.getAppId(function( json ){
                    this.appId = json.data.value;
                    if (callback) callback();
                }.bind(this),null,false);

            }

        },

        loadEditor : function (){

            this.fireEvent("beforeOpen");

            if(this.wpsOffice) this.wpsOffice.destroy();

            this.officeNode = new Element("div#_" + this.documentId,{"style":"height:100%;overflow:hidden;min-height:700px"}).inject(this.node);

            var config = {

                mount: this.officeNode,
                mode : this.json.showMode,
                cooperUserAttribute: {
                    isCooperUsersAvatarVisible: this.json.isCooperUsersAvatarVisible //공동 작업 사용자 아바타 표시 여부
                },
                // 공통 옵션, 모든 유형의 문서에 적용
                commonOptions: {
                    isShowTopArea: this.json.isShowTopArea, // 상단 영역(헤더와 도구 모음) 숨기기
                    isShowHeader: this.json.isShowHeader, // 헤더 영역 숨기기
                    isBrowserViewFullscreen : this.json.isBrowserViewFullscreen,//브라우저 영역 전체 화면 여부
                    isIframeViewFullscreen : this.json.isIframeViewFullscreen//iframe 영역 내 전체 화면 여부
                },
                wordOptions : {
                    isShowDocMap : this.json.isShowDocMap,//목차 기능 사용 여부, 기본 사용
                    isBestScale : this.json.isBestScale,//문서 열 때 기본으로 최적 비율로 표시(pc용)
                    isShowBottomStatusBar : this.json.isShowBottomStatusBar,//pc-하단 상태 표시줄 표시 여부
                    "mobile.isOpenIntoEdit" : this.json.isOpenIntoEdit//mobile-편집 권한이 있어야 하며, 모바일에서 열 때 편집 모드 진입 여부
                },
                commandBars: [
                    {
                        cmbId: "HeaderLeft",
                        attributes: {
                            visible: this.json.HeaderLeft,
                            enable: this.json.HeaderLeft
                        }
                    },
                    {
                        cmbId: "HeaderRight",
                        attributes: {
                            visible: this.json.HeaderRight,
                            enable: this.json.HeaderRight
                        }
                    },
                    {
                        cmbId: "FloatQuickHelp",
                        attributes: {
                            visible: this.json.FloatQuickHelp,
                            enable: this.json.FloatQuickHelp
                        }
                    },
                    {
                        cmbId: "HistoryVersion",
                        attributes: {
                            visible: this.json.HistoryVersion,
                            enable: this.json.HistoryVersion
                        }
                    },
                    {
                        cmbId: "HistoryRecord",
                        attributes: {
                            visible: this.json.HistoryRecord,
                            enable: this.json.HistoryRecord
                        }
                    },
                    {
                        cmbId: "HistoryVersionDivider",
                        attributes: {
                            visible: this.json.HistoryVersionDivider,
                            enable: this.json.HistoryVersionDivider
                        }
                    },
                    {
                        cmbId: "Logo",
                        attributes: {
                            visible: this.json.Logo,
                            enable: this.json.Logo
                        }
                    },
                    {
                        cmbId: "Cooperation",
                        attributes: {
                            visible: this.json.Cooperation,
                            enable: this.json.Cooperation
                        }
                    },
                    {
                        cmbId: "More",
                        attributes: {
                            visible: this.json.More,
                            enable: this.json.More
                        }
                    },
                    {
                        cmbId: "SendButton",
                        attributes: {
                            visible: this.json.SendButton,
                            enable: this.json.SendButton
                        }
                    },
                    {
                        cmbId: "CooperHistoryMenuItem",
                        attributes: {
                            visible: this.json.CooperHistoryMenuItem,
                            enable: this.json.CooperHistoryMenuItem
                        }
                    },
                    {
                        cmbId: "TabPrintPreview",
                        attributes: {
                            visible: this.json.TabPrintPreview,
                            enable: this.json.TabPrintPreview
                        }
                    },
                    {
                        cmbId: "MenuPrintPreview",
                        attributes: {
                            visible: this.json.MenuPrintPreview,
                            enable: this.json.MenuPrintPreview
                        }
                    },
                    {
                        cmbId: "ReviewTrackChanges",
                        attributes: {
                            visible: this.json.ReviewTrackChanges,
                            enable: this.json.ReviewTrackChanges
                        }
                    },
                    {
                        cmbId: "TrackChanges",
                        attributes: {
                            visible: this.json.TrackChanges,
                            enable: this.json.TrackChanges
                        }
                    },
                    {
                        cmbId: "ContextMenuConvene",
                        attributes: {
                            visible: this.json.ContextMenuConvene,
                            enable: this.json.ContextMenuConvene
                        }
                    },
                    {
                        cmbId: "WriterHoverToolbars",
                        attributes: {
                            visible: this.json.WriterHoverToolbars,
                            enable: this.json.WriterHoverToolbars
                        }
                    },
                    {
                        cmbId: "ReadSetting",
                        attributes: {
                            visible: this.json.ReadSetting,
                            enable: this.json.ReadSetting
                        }
                    }
                ]

            };

            if(this.version === "wpsWebOffice"){
                config.officeType = WebOfficeSDK.OfficeType[this.officeType[this.extension.toLowerCase()]];
                config.appId = this.appId;
                config.fileId = this.documentId.replace(/-/g, "_");
                config.token = layout.session.token;
                config.customArgs = {
                    "appToken" : this.appToken,
                    "mode" : this.mode
                };
                this.wpsOffice = WebOfficeSDK.init(config);
            }else if(this.version === "jbWps"){
                this.action.CustomAction.getJbWpsFileUrl(this.documentId,{
                    "mode" : this.mode,
                    "appToken" : this.appToken
                },function( json ){
                    this.wpsUrl = json.data.wpsUrl;
                    config.url = this.wpsUrl+ "&_w_tokentype=1";

                }.bind(this),null,false);
    
                this.wpsOffice = OpenSDK.config(config);
                this.wpsOffice.setToken({
                    token: layout.session.token,
                    timeout: 100 * 60 * 1000 // token 만료 시간, refreshToken 설정 함수와 함께 사용 가능, 만료 전에 refreshToken 호출
                });
            }else {
                this.action.CustomAction.getWpsFileUrl(this.documentId,{
                    "mode" : this.mode,
                    "appToken" : this.appToken
                },function( json ){
                    this.wpsUrl = json.data.wpsUrl;
                    config.url = this.wpsUrl;

                }.bind(this),null,false);
    
                this.wpsOffice = WebOfficeSDK.config(config);
                this.wpsOffice.setToken({
                    token: layout.session.token,
                    timeout: 100 * 60 * 1000 // token 만료 시간, refreshToken 설정 함수와 함께 사용 가능, 만료 전에 refreshToken 호출
                });

            }

            this.wpsOffice.on('fileOpen', function(result) {
                this.fireEvent("afterOpen");
            }.bind(this));
            this.wpsOffice.on('fileStatus', function(result) {
                if(result.status === 7){
                    this.fireEvent("afterSave");
                }
            }.bind(this));

            this.wpsOffice.on('fullscreenChange', function(result) {
                // console.log(JSON.stringify(result))
            });
            this.wpsOffice.on('previewLimit', function(result) {
                // console.log(JSON.stringify(result))
            });
            this.wpsOffice.on('tabSwitch', function(result) {
                // console.log(JSON.stringify(result))
            });
            this.wpsOffice.on('error', function(result) {
                // console.log(JSON.stringify(result))
            });
            //댓글 표시 여부
            if(this.json.isShowComment){
                this.showComments();
            }else {
                this.hideComments();
            }
        },

        hide: function(){
            this.node.hide();
        },
        show: function(){
            this.node.show();
        },
        isEmpty : function(){
            var data = this.getData();
            if(data.documentId === ""){
                return true;
            }else {
                return false;
            }
        },
        getData: function(){
            var data = {
                "documentId" : ""
            };
            if(this.form.businessData.data[this.json.id]){
                data.documentId = this.form.businessData.data[this.json.id].documentId;
            }
            return data;
        },
        setData: function() {
            var data = {
                "documentId": this.documentId,
                "appToken": this.appToken
            }
            this.data = data;
            this._setBusinessData(data);

            var jsonData = {}
            jsonData[this.json.id] = data;
            if (this.form.businessData.work.startTime){
                o2.Actions.load(this.appToken).DataAction.updateWithJob(this.form.businessData.work.job, jsonData, function (json) {
                    data = json.data;
                })
            }else{
                this.form.saveFormDataDraftSync();
            }

        },
        /**
         * @summary wps 저장
         * @example
         * this.form.get("fieldId").save(callback)
         */
        save: function(callback){
            var promise =  this.wpsOffice.save();
            promise.then(function(){
                if(callback) callback();
            });
        },
        /**
         * @summary 전역 브로드캐스트 전송
         * @example
         * this.form.get("fieldId").sendBroadcast("테스트")
         */
        sendBroadcast : async function (text){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const Public = await app.Public;
            const result = await Public.SendBroadcast({
                Data: { message: text }
            });
        },
        /**
         * @summary word 전체 페이지 수 가져오기,
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").getTotalPage()
         */
        getTotalPage : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const totalPages = await app.ActiveDocument.Range.Information(app.Enum.WdInformation.wdNumberOfPagesInDocument);
            return totalPages;
        },
        /**
         * @summary 현재 페이지 가져오기
         * @example
         * this.form.get("fieldId").getCurrentPage()
         */
        getCurrentPage: async function (){
            // 현재 페이지 번호 가져오기
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const currentPage = await app.ActiveDocument.ActiveWindow.Selection.Information(
                app.Enum.WdInformation.wdActiveEndPageNumber
            )
            return currentPage;
        },
        /**
         * @summary 지정한 페이지로 이동
         * @example
         * this.form.get("fieldId").gotoPage(page)
         */
        gotoPage : async function (page){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToPage,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Count: page
            });
        },
        /**
         * @summary 모든 책갈피 가져오기
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").getAllBookmark()
         */
        getAllBookmark : async function (callback){
            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const bookMarks =  await app.ActiveDocument.Bookmarks.Json();
            return bookMarks;
        },
        /**
         * @summary 책갈피 값 가져오기
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").getBookmarkText()
         */
        getBookmarkText : async function (name) {

            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const text =  await app.ActiveDocument.GetBookmarkText(name);

            return text;
        },
        /**
         * @summary 책갈피로 이동
         * @example
         * this.form.get("fieldId").gotoBookmark()
         */
        gotoBookmark : async function (name){
            this.wpsOffice.ready();
            const app = this.wpsOffice.Application;

            await app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToBookmark,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Name: name
            });
        },
        /**
         * @summary 책갈피에 값 설정
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").setBookmarkText(name,value)
         */
        setBookmarkText : async function (name,value){
            await this.wpsOffice.ready();

            const app = this.wpsOffice.Application;

            // 책갈피 객체
            const bookmarks = await app.ActiveDocument.Bookmarks;
            // 책갈피 내용 교체
            const isReplaceSuccess = await bookmarks.ReplaceBookmark([
                {
                    name: name,
                    type: 'text',
                    value: value
                }
            ])
            return isReplaceSuccess;
        },
        /**
         * @summary 날인
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").setSeal(bookmark,img,left,top,width,height)
         */
        setSeal : async function (bookmark,img,left,top,width,height){
            await this.wpsOffice.ready();

            const app = this.wpsOffice.Application;

            //현재 선택 영역 가져오기
            const selection = await app.ActiveDocument.ActiveWindow.Selection;
            // 지정한 책갈피로 이동
            await app.ActiveDocument.ActiveWindow.Selection.GoTo(
                app.Enum.WdGoToItem.wdGoToBookmark, // 유형: Bookmark
                app.Enum.WdGoToDirection.wdGoToAbsolute, // 위치 확인
                1, // 수량
                bookmark, // 책갈피 이름
            );

            // 도형 객체 가져오기
            const shapes = await app.ActiveDocument.Shapes;
            // 커서 위치에 비포함형 이미지 삽입
            const shape = await shapes.AddPicture({
                FileName: img, // 이미지 주소
                LinkToFile: true,
                SaveWithDocument: true,
                Left: left, // 이미지 왼쪽 위치
                Top: top, // 이미지 위쪽 위치
                Width: width, // 이미지 너비
                Height: height, // 이미지 높이
            });

            // 텍스트 배치 모드를 [텍스트 뒤]로 설정
            shape.ZOrder(app.Enum.ZOrderCmd.sendBehindText);
        },

        /**
         * @summary 찾기 및 바꾸기
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").replaceText(key,value)
         */
        replaceText : async function (key,value,options){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const isReplaceSuccess = app.ActiveDocument.ReplaceText([{key: key, value: value}])
            return isReplaceSuccess;
        },
        /**
         * @summary 수정 추적 모드 켜기
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").startRevisions()
         */
        startRevisions : function (){

            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;
                // 현재 문서의 편집 상태를 수정 추적 모드로 전환
                app.ActiveDocument.TrackRevisions = true;
            }.bind(this));
        },
        /**
         * @summary 수정 추적 모드 끄기
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").stopRevisions()
         */
        stopRevisions : function (){
            //수정 추적 모드 끄기
            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;

                // 현재 문서의 편집 상태를 수정 추적 모드로 전환
                app.ActiveDocument.TrackRevisions = false;
            }.bind(this));
        },
        /**
         * @summary 모든 수정 내용 적용
         * @example
         * this.form.get("fieldId").acceptAllRevisions()
         */
        acceptAllRevisions : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 수정 내용 객체 가져오기
            const revisions = await app.ActiveDocument.Revisions;
            // 지정한 문서의 모든 수정 내용 적용
            await revisions.AcceptAll();
        },
        /**
         * @summary 모든 수정 내용 거부
         * @example
         * this.form.get("fieldId").rejectAllRevisions()
         */
        rejectAllRevisions : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 수정 내용 객체 가져오기
            const revisions = await app.ActiveDocument.Revisions;
            // 지정한 문서의 모든 수정 내용 거부
            await revisions.RejectAll();
        },
        /**
         * @summary 변경 내용 표시
         * @example
         * this.form.get("fieldId").showRevisions()
         */
        showRevisions : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 구역 객체 가져오기
            const View = await app.ActiveDocument.ActiveWindow.View;
            View.RevisionsView = 0;
            // 수정 추적 상태를 「메모 및 변경 내용 표시 최종본」으로 설정
            View.ShowRevisionsAndComments = true;

        },
        /**
         * @summary 변경 내용 숨기기
         * @example
         * this.form.get("fieldId").hideRevisions()
         */
        hideRevisions : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 구역 객체 가져오기
            const View = await app.ActiveDocument.ActiveWindow.View;
            View.RevisionsView = 0;
            // 수정 추적 상태를 「메모 및 변경 내용 표시 최종본」으로 설정
            View.ShowRevisionsAndComments = false;
        },
        /**
         * @summary 인쇄
         * @example
         * this.form.get("fieldId").print()
         */
        print : function (){
            this.wpsOffice.ready().then(function(){
                const app = this.wpsOffice.Application;
                // 페이지 맞춤 객체: 더 보기 메뉴
                app.CommandBars('TabPrintPreview').then(function(printMenu){
                    printMenu.Execute();
                });
            }.bind(this));
        },
        /**
         * @summary pdf 내보내기
         * @example
         * this.form.get("fieldId").exportPDF()
         */
        exportPDF : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var pdfUrl;
            switch (this.json.officeType){
                case "docx":
                    pdfUrl = await app.ActiveDocument.ExportAsFixedFormat();
                    break;
                case "xlsx":
                    pdfUrl = await app.ActiveWorkbook.ExportAsFixedFormat()
                    break;
                case "pptx":
                    pdfUrl = await app.ActivePresentation.ExportAsFixedFormat()
            }

            window.open(pdfUrl.url);
        },
        /**
         * @summary 문서 권한 정보 가져오기
         * @example
         * this.form.get("fieldId").getOperatorsInfo()
         */
        getOperatorsInfo : async function (){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var operatorsInfo ;
            switch (this.json.officeType){
                case "docx":
                    operatorsInfo =  await app.ActiveDocument.GetOperatorsInfo();
                    break;
                case "xlsx":
                    operatorsInfo =  await app.ActiveWorkbook.GetOperatorsInfo();
                    break;
                case "pptx":
                    operatorsInfo =  await app.ActivePresentation.GetOperatorsInfo();
            }
            return operatorsInfo;
        },
        /**
         * @summary 콘텐츠 컨트롤 개수 조회
         * @return Promise 객체 반환
         * @example
         * this.form.get("fieldId").getContentControlsCount()
         */
        getContentControlsCount : async function (){
            //콘텐츠 컨트롤 조회
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            // 콘텐츠 컨트롤 객체
            const contentControls = await app.ActiveDocument.ContentControls;
            // 콘텐츠 컨트롤 수
            const count = await contentControls.Count;
            return count;
        },
        /**
         * @summary 콘텐츠 컨트롤 텍스트 조회
         * @example
         * this.form.get("fieldId").getContentControlText()
         */
        getContentControlText : async function (pos){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const contentControls = await app.ActiveDocument.ContentControls;
            const contentControl = await contentControls.Item(pos);
            const range = await contentControl.Range;
            const text = range.Text;

            return text
            // range.Text = 'WebOffice'
        },
        /**
         * @summary 콘텐츠 컨트롤 텍스트 설정
         * @example
         * this.form.get("fieldId").setContentControlText()
         */
        setContentControlText : async function (pos,text){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const contentControls = await app.ActiveDocument.ContentControls;
            const contentControl = await contentControls.Item(pos);
            const range = await contentControl.Range;
            range.Text = text;

        },
        /**
         * @summary 문서 확대/축소 비율 조회
         * @example
         * this.form.get("fieldId").getZoom()
         */
        getZoom : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var zoom;
            switch (this.json.officeType){
                case "docx":
                    zoom =  await app.ActiveDocument.ActiveWindow.View.Zoom.Percentage
                    break;
                case "xlsx":
                    zoom =  await app.ActiveWorkbook.ActiveSheetView.Zoom
                    break;
                case "pptx":
                    zoom =  await app.ActivePresentation.View.Zoom
            }
            return zoom;
        },
        /**
         * @summary 문서 확대/축소 비율 설정
         * @example
         * this.form.get("fieldId").setZoom()
         */
        setZoom : async function (zoom){
            //확대/축소 값은 50%~300% 사이입니다.
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;

            switch (this.json.officeType){
                case "docx":
                    app.ActiveDocument.ActiveWindow.View.Zoom.Percentage = zoom;
                    break;
                case "xlsx":
                    app.ActiveWorkbook.ActiveSheetView.Zoom = zoom;
                    break;
                case "pptx":
                    app.ActivePresentation.View.Zoom = zoom;
            }
        },
        /**
         * @summary 댓글 존재 여부
         * @example
         * this.form.get("fieldId").hasComments()
         */
        hasComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            var hasComments;
            switch (this.json.officeType){
                case "docx":
                    hasComments = await app.ActiveDocument.HasComments();
                    break;
                case "xlsx":
                    hasComments = await app.ActiveWorkbook.HasComments();
                    break;
                case "pptx":
                    hasComments = await app.ActivePresentation.HasComments();
            }
            return hasComments;
        },
        /**
         * @summary 댓글 표시
         * @example
         * this.form.get("fieldId").showComments()
         */
        showComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = true;
        },
        /**
         * @summary 댓글 숨기기
         * @example
         * this.form.get("fieldId").hideComments()
         */
        hideComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.View.ShowComments = false;
        },
        /**
         * @summary 모든 댓글 조회
         * @example
         * this.form.get("fieldId").hideComments()
         */
        getComments : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            const operatorsInfo = await app.ActiveDocument.GetComments({ Offset: 0, Limit: 2000 });
            return operatorsInfo;
        },
        /**
         * @summary 페이지 모드
         * @example
         * this.form.get("fieldId").showPageMode()
         */
        showPageMode : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.SwitchTypoMode(false);
        },
        /**
         * @summary 연속 페이지 모드
         * @example
         * this.form.get("fieldId").showPageMode()
         */
        showUnionPageMode : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            app.ActiveDocument.SwitchTypoMode(true);
        },
        /**
         * @summary 연속 페이지 모드에서 파일명 표시
         * @example
         * this.form.get("fieldId").showFileName()
         */
        showFileName : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            await app.ActiveDocument.SwitchFileName(true);
        },
        /**
         * @summary 연속 페이지 모드에서 파일명 숨기기
         * @example
         * this.form.get("fieldId").hideFileName()
         */
        hideFileName : async function (){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            await app.ActiveDocument.SwitchFileName(false);
        },
        /**
         * @summary 탐색 목차 표시
         * @example
         * this.form.get("fieldId").showDocumentMap()
         */
        showDocumentMap : async function (callback){

            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            app.ActiveDocument.ActiveWindow.DocumentMap = true;

        },
        /**
         * @summary 탐색 목차 숨기기
         * @example
         * this.form.get("fieldId").hideDocumentMap()
         */
        hideDocumentMap : async function (callback){
            await this.wpsOffice.ready();
            const app = this.wpsOffice.Application;
            await app.ActiveDocument.SwitchTypoMode(true);
            app.ActiveDocument.ActiveWindow.DocumentMap = false;
        },
    });
