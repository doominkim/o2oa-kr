MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.WpsOffice2 = MWF.APPWpsOffice2 =  new Class({
    Extends: MWF.APP$Module,
    isActive: false,
    options:{
        "version": "v1.1.8",
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

        if(!layout.serviceAddressList["x_wpsfile2_assemble_control"]){
            this.node.set("html","<h3><font color=red>먼저 WPS 사내 구축형 앱을 설치하세요</font></h3>");
            return false;
        }
        this.action = o2.Actions.load("x_wpsfile2_assemble_control");
        if (!this.json.isNotLoadNow){
            this.data = this.getData();
            if(this.data.documentId === ""){
                this.createDocument(function (){
                    this.loadDocument();
                }.bind(this));
            }else {
                this.documentId = this.data.documentId;
                this.loadDocument();
            }
        }
    },
    createDocument : function (callback){
        var json = {};
        if(this.json.template !== ""){
            json.templateId = this.json.template;
            this.json.officeType = "word";
        }
        if(this.json.fileName !== ""){
            json.fileName = this.json.fileName;
        }
        json.category = "process";
        json.docId = this.form.businessData.work.job;

        this.action.CustomAction.createFileBlank(this.json.officeType,json, function( json ){
            this.documentId = json.data.docId;
            this.setData();
            if (callback) callback();
        }.bind(this),null, false);
    },
    loadDocument: function () {

        this.getEditor(function () {
            this.loadApi(function (){
                this.loadEditor();
            }.bind(this));
        }.bind(this));
    },
    loadApi : function (callback){
        o2.load(["../o2_lib/wps/polyfill.min.js","../o2_lib/wps/web-office-sdk.umd.js"], {"sequence": true}, function () {
            if (callback) callback();
        }.bind(this));
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

        this.fireEvent("beforeOpen");

        this.action.CustomAction.getFileUrl(this.documentId,{"permission":this.mode} ,function( json ){
            this.wpsUrl = json.data.wpsUrl;
            this.wpsToken = json.data.token;

            if(this.json.officeType === "other"){
                if(this.wpsUrl.indexOf("/w/")>-1){
                    this.json.officeType = "word";
                }
                if(this.wpsUrl.indexOf("/p/")>-1){
                    this.json.officeType = "ppt";
                }
                if(this.wpsUrl.indexOf("/s/")>-1){
                    this.json.officeType = "excel";
                }
            }
            if (callback) callback();
        }.bind(this),null,false);
    },
    loadEditor : function (){



        this.wpsOffice = WebOfficeSDK.config({
            url : this.wpsUrl,
            mount: this.node,
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

        });
        var token = this.wpsToken;
        // token 설정
        // this.wpsOffice.setToken({
        //     token: token,
        //     timeout: 10000 // token 만료 시간, refreshToken 설정 함수와 함께 사용 가능. 만료 전에 refreshToken 호출
        // })
        // 문서 열기 결과
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
    setData: function(){
        var data = {
            "documentId" : this.documentId
        };
        this._setBusinessData(data);
    },
    save: function(callback){
        var promise =  this.wpsOffice.save();
        promise.then(function(){
            if(callback) callback();
        });
    },
    getTotalPage :  function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.WordApplication()
            var p2 = app.ActiveDocument.Range.Information(app.Enum.WdInformation.wdNumberOfPagesInDocument);
            p2.then(function(totalPages){
                if(totalPages.End){
                    if(callback) callback(totalPages.PagesCount);
                }else {
                    promise();
                }
            }.bind(this));
        }.bind(this));
    },
    getCurrentPage:  function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.Information(app.Enum.WdInformation.wdActiveEndPageNumber);
            p2.then(function(currentPage){
                if(callback) callback(currentPage);
            });
        }.bind(this));
    },
    gotoPage : function (page){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToPage,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Count: page
            });
            p2.then(function(){

            })
        }.bind(this));
    },
    getAllBookmark : function (callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Bookmarks.Json();
            p2.then(function(bookMarks){
                if(callback) callback(bookMarks);
            });
        }.bind(this));
    },
    getBookmarkText : function (name,callback) {
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.GetBookmarkText(name);
            p2.then(function(text){
                if(callback) callback(text);
            });
        }.bind(this));
    },
    gotoBookmark : function (name){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.Selection.GoTo({
                What: app.Enum.WdGoToItem.wdGoToBookmark,
                Which: app.Enum.WdGoToDirection.wdGoToAbsolute,
                Name: name
            })
            p2.then(function(){
            });
        }.bind(this));
    },
    setBookmarkText : function (name,value,callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ReplaceBookmark([{name: name, type: 'text', value: value} ])
            p2.then(function(isReplaceSuccess){
                if(callback) callback(isReplaceSuccess);
            });
        }.bind(this));
    },
    replaceText : function (key,value,options){

        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ReplaceText([{key: key, value: value}])
            p2.then(function(isReplaceSuccess){
                if(callback) callback(isReplaceSuccess);
            });
        }.bind(this));
    },
    startRevisions : function (){
        //변경 추적 모드 켜기
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 현재 문서의 편집 상태를 수정 추적 모드로 전환
            app.ActiveDocument.TrackRevisions = true;
        }.bind(this));
    },
    stopRevisions : function (){
        //수정 추적 모드 끄기
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;

            // 현재 문서의 편집 상태를 수정 추적 모드로 전환
            app.ActiveDocument.TrackRevisions = false;
        }.bind(this));

    },
    acceptAllRevisions : function (callback){
        //모든 변경 내용 수락
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 수정 내용 객체 가져오기
            app.ActiveDocument.Revisions.then(function(revisions){
                // 지정한 문서의 모든 수정 내용 적용
                revisions.AcceptAll().then(function(){
                    if(callback) callback();
                });
            }.bind(this));

        }.bind(this));
    },
    rejectAllRevisions : function (callback){
        //모든 변경 내용 거부
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 수정 내용 객체 가져오기
            app.ActiveDocument.Revisions.then(function(revisions){
                // 모든 변경 내용 거부
                revisions.RejectAll().then(function(){
                    if(callback) callback();
                });
            }.bind(this));
        }.bind(this));
    },
    showRevisions : function (){
        //변경 흔적 표시
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 구역 객체 가져오기
            app.ActiveDocument.ActiveWindow.View.then(function(View){
                // 변경 추적 상태를 최종 상태로 설정
                View.RevisionsView = 0;

                // 수정 추적 상태를 「메모 및 변경 내용 표시 최종본」으로 설정
                View.ShowRevisionsAndComments = true;
            });
        }.bind(this));
    },
    hideRevisions : function (){
        //숨김
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 구역 객체 가져오기
            app.ActiveDocument.ActiveWindow.View.then(function(View){
                // 변경 추적 상태를 최종 상태로 설정
                View.RevisionsView = 0;

                // 수정 추적 상태를 「메모 및 변경 내용 표시 최종본」으로 설정
                View.ShowRevisionsAndComments = false;
            });
        }.bind(this));
    },
    addControlButton : function(text,callback){
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            app.CommandBars('StartTab').Controls.then(function(controls){
                controls.Add(1).then(function(controlButton){
                    controlButton.Caption = text;
                    controlButton.OnAction = callback
                });
            });
        }.bind(this));
    },
    print : function (){
        //숨김
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 페이지 맞춤 객체: 더 보기 메뉴
            app.CommandBars('TabPrintPreview').then(function(printMenu){
                printMenu.Execute();
            });
        }.bind(this));
    },
    exportPDF : function (){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2;
            switch (this.json.officeType){
                case "word":
                    p2 = app.ActiveDocument.ExportAsFixedFormat();
                    break;
                case "excel":
                    p2 = app.ActiveWorkbook.ExportAsFixedFormat()
                    break;
                case "ppt":
                    p2 = app.ActivePresentation.ExportAsFixedFormat()
            }
            p2.then(function (pdfUrl){
                window.open(pdfUrl.url);
            });
        }.bind(this));
    },
    getContentControlsCount : function (callback){
        //콘텐츠 컨트롤 조회
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 = app.ActiveDocument.ContentControls.Count;
            p2.then(function (count){
                if(callback) callback(count);
            });
        }.bind(this));
    },
    getContentControl : function (pos,callback){
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 =  app.ActiveDocument.ContentControls.Item(pos);
            p2.then(function (contentControl){
                var p3 = contentControl.Title;
                var p4 = contentControl.Tag;
                var p5 = contentControl.PlaceholderText;
                var content = {};
                p3.then(function (contentTitle){
                    content.contentTitle = contentTitle;
                    p4.then(function (contentTag){
                        content.contentTag = contentTag;
                        p5.then(function (contentPlaceholderText){
                            content.contentPlaceholderText = contentPlaceholderText;
                            if(callback) callback(content);
                        });
                    });
                });
            });
        }.bind(this));
    },
    setContentControl : function (pos,contentTitle,contentTag,contentPlaceholderText,callback){

        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 =  app.ActiveDocument.ContentControls.Item(pos);
            p2.then(function (contentControl){
                contentControl.Title = contentTitle
                contentControl.Tag = contentTag
                contentControl.SetPlaceholderText({Text: contentPlaceholderText})
                if(callback) callback();
            });
        }.bind(this));
    },
    getOperatorsInfo : function (callback){
        //문서 권한 정보 조회
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 ;
            switch (this.json.officeType){
                case "word":
                    p2 =  app.ActiveDocument.GetOperatorsInfo();
                    break;
                case "excel":
                    p2 =  app.ActiveWorkbook.GetOperatorsInfo();
                    break;
                case "ppt":
                    p2 =  app.ActivePresentation.GetOperatorsInfo();
            }
            p2.then(function (operatorsInfo){
                if(callback) callback(operatorsInfo);
            });
        }.bind(this));
    },
    getZoom : function (callback){
        //문서 확대/축소 비율 조회
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.WordApplication();
            var p2 ;
            switch (this.json.officeType){
                case "word":
                    p2 =  app.ActiveDocument.ActiveWindow.View.Zoom.Percentage
                    break;
                case "excel":
                    p2 =  app.ActiveWorkbook.ActiveSheetView.Zoom
                    break;
                case "ppt":
                    p2 =  app.ActivePresentation.View.Zoom
            }
            p2.then(function (zoom){
                if(callback) callback(zoom);
            });
        }.bind(this));
    },
    setZoom : function (zoom,callback){
        //확대/축소 값은 50%~300% 사이입니다.
        var p1 =  this.wpsOffice.ready();
        p1.then(function(){
            const app = this.wpsOffice.Application;
            switch (this.json.officeType){
                case "word":
                    app.ActiveDocument.ActiveWindow.View.Zoom.Percentage = zoom;

                    break;
                case "excel":
                    app.ActiveWorkbook.ActiveSheetView.Zoom = zoom;
                    break;
                case "ppt":
                    app.ActivePresentation.View.Zoom = zoom;
            }
            if(callback) callback();
        }.bind(this));
    },
    destroy : function (){
        this.wpsOffice.destroy();
    },
    reload : function (){
        this.destroy();
        this.loadDocument();
    },
    hasComments : function (callback){
        //댓글 존재 여부
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            var hasComments;
            switch (this.json.officeType){
                case "word":
                    hasComments = app.ActiveDocument.HasComments();
                    break;
                case "excel":
                    hasComments  = app.ActiveWorkbook.HasComments();
                    break;
                case "ppt":
                    hasComments = app.ActivePresentation.HasComments();
            }
            Promise.resolve(hasComments).then(function(comments){
                if(callback) callback(comments);
            });

        }.bind(this));

    },
    showComments : function (){
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 댓글 표시 여부 제어
            app.ActiveDocument.ActiveWindow.View.ShowComments = true;
        }.bind(this));
    },
    hideComments : function (callback){
        this.wpsOffice.ready().then(function(){
            const app = this.wpsOffice.Application;
            // 댓글 표시 여부 제어
            app.ActiveDocument.ActiveWindow.View.ShowComments = false;
        }.bind(this));

    },
    getComments : function (callback){
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.GetComments({ Offset: 0, Limit: 2000 });
            p2.then(function (operatorsInfo){
                if(callback) callback(operatorsInfo);
            })
        }.bind(this));
    },
    showPageMode : function (callback){
        //페이지 모드
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchTypoMode(false);
            p2.then()
        }.bind(this));
    },
    showUnionPageMode : function (callback){
        //연속 페이지 모드
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchTypoMode(true);
            p2.then()
        }.bind(this));
    },
    showFileName : function (callback){
        //연속 페이지 모드에서 파일명 표시
        this.showUnionPageMode(function (){

            var p1 = this.wpsOffice.ready();
            p1.then(function (){
                const app = this.wpsOffice.Application;
                var p2 = app.ActiveDocument.SwitchFileName(true);
                p2.then()
            }.bind(this));

        }.bind(this))
    },
    hideFileName : function (callback){
        //연속 페이지 모드에서 파일명 표시
        var p1 = this.wpsOffice.ready();
        p1.then(function (){
            const app = this.wpsOffice.Application;
            var p2 = app.ActiveDocument.SwitchFileName(false);
            p2.then()
        }.bind(this));
    },
    showDocumentMap : function (callback){
        //연속 페이지 모드
        // var promise =  async () =>{
        //     await this.wpsOffice.ready();
        //     const app = this.wpsOffice.Application;
        //     app.ActiveDocument.ActiveWindow.DocumentMap = true
        //     if(callback) callback();
        // }
        // promise();

        var promise = new Promise(function(resolve, reject){
            this.wpsOffice.ready();
            resolve();
        }.bind(this));
        promise.then(function(){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.DocumentMap = true;
            if(callback) callback();
        }.bind(this));
    },
    hideDocumentMap : function (callback){
        //연속 페이지 모드
        var promise = new Promise(function(resolve, reject){
            this.wpsOffice.ready();
            resolve();
        }.bind(this));
        promise.then(function(){
            const app = this.wpsOffice.Application;
            app.ActiveDocument.ActiveWindow.DocumentMap = false;
            if(callback) callback();
        }.bind(this));
    },
});
