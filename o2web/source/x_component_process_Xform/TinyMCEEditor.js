MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class TinyMCEEditor HTML 편집기.
 * @o2cn TinyMCEEditor 편집기
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var tinyMCEEditor = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var tinyMCEEditor = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.TinyMCEEditor = MWF.APPTinyMCEEditor = new Class(
    /** @lends MWF.xApplication.process.Xform.TinyMCEEditor# */
    {
        Extends: MWF.APP$Module,
        options: {
            /**
             * 컴포넌트 비동기 로드 후 발생.
             * @event MWF.xApplication.process.Xform.TinyMCEEditor#afterLoad
             * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
             */
            "moduleEvents": ["queryLoad", "load", "postLoad", "afterLoad"]
        },
        initialize: function (node, json, form, options) {
            this.node = $(node);
            this.node.store("module", this);
            this.json = json;
            this.form = form;
            this.field = true;
            this.fieldModuleLoaded = false;
        },
        load: function () {
            this._loadModuleEvents();
            if (this.fireEvent("queryLoad")) {
                this._queryLoaded();
                this._loadUserInterface();
                this._loadStyles();
                //this._loadEvents();

                this._afterLoaded();

                this.fireEvent("postLoad");
                this.fireEvent("load");
            }
        },

        _loadUserInterface: function () {
            this.node.empty();
            if (!this.isReadable){
                this.node?.addClass('hide');
                return ''
            }

            if (this.isReadonly()) {
                // this.node.set("html", this._getBusinessData());
                this.node.setStyles({
                    "-webkit-user-select": "text",
                    "-moz-user-select": "text"
                });
                if(this.downloading){
                    this.praseHtml();
                    this.fireEvent("afterLoad");
                    this.fieldModuleLoaded = true;
                }else if (layout.mobile) {
                    this.loadLazyImage(function () { //이미지 지연 로딩
                        var images = this.node.getElements("img");
                        //모바일에서 이미지 너비를 100%로 설정
                        images.each(function (img) {
                            if (img.hasClass("lozad")) {
                                img.setStyles({
                                    "max-width": "100%"
                                });
                            } else {
                                img.setStyles({
                                    "height": "auto",
                                    "max-width": "100%"
                                });
                            }
                        }.bind(this));
                        this.fireEvent("afterLoad");
                        this.fieldModuleLoaded = true;
                    }.bind(this))
                } else {
                    this.loadLazyImage(function () { //이미지 지연 로딩
                        if (this.json.enablePreview !== "n") {
                            this.loadImageViewer(); //PC에서 클릭 시 큰 이미지 표시
                            this.fireEvent("afterLoad");
                            this.fieldModuleLoaded = true;
                        }
                    }.bind(this))
                }
            } else {
                var config = Object.clone(this.json.editorProperties);
                if (this.json.config) {
                    if (this.json.config.code) {
                        var obj = this.form.Macro.exec(this.json.config.code, this);
                        Object.each(obj, function (v, k) {
                            config[k] = v;
                        });
                    }
                }
                this.loadTinyMCEEditor(config);
            }
            //    this._loadValue();
        },
        praseHtml: function () {
            o2.require("o2.widget.ImageLazyLoader", function () {
                var loadder = new o2.widget.ImageLazyLoader(this.node, this._getBusinessData());
                this.node.set("html", loadder.clearHtml());
                var images = this.node.getElements("img");
                const ps = images.map( function( img ){
                    if (!img.crossOrigin) {
                        img.crossOrigin = "anonymous";
                    }
                    return this.imgToBase64(img)
                        .then(base64 => {
                            img.src = base64;
                        })
                        .catch(err => {
                            console.warn("이미지 처리 실패: ", err.message, img.src);
                            img.destroy();
                        });
                }.bind(this));
                this.moduleValueAG = Promise.allSettled(ps);
            }.bind(this), null, false);
        },
        imgToBase64: function(img, format = 'image/png', quality = 0.5) {
            return new Promise((resolve, reject) => {

                const parseImg = () => {
                    try {
                        const width = img.naturalWidth || img.offsetWidth;
                        const height = img.naturalHeight || img.offsetHeight;

                        const toWidth =  img.offsetWidth || img.naturalWidth;
                        const toHeight =  img.offsetHeight || img.naturalHeight;

                        if (width <= 0 || height <= 0) {
                            reject(new Error("이미지 크기가 올바르지 않아 변환할 수 없습니다"));
                            return;
                        }

                        const canvas = document.createElement('canvas');
                        const ctx = canvas.getContext('2d');

                    canvas.width = toWidth;
                    canvas.height = toHeight;

                    ctx.drawImage(img, 0, 0, toWidth, toHeight);

                        const base64Str = canvas.toDataURL(format, quality);
                        resolve(base64Str);
                    } catch (error) {
                        reject(new Error(`변환 실패: ${error.message}`));
                    }
                };

                img.setStyles({
                    "height": "auto",
                    "width": "auto",
                    "max-width" : "100%"
                });

                // 이미지 로드 성공 처리
                const handleLoad = () => {
                    img.removeEventListener('load', handleLoad);
                    img.removeEventListener('error', handleError);
                    parseImg();
                };

                // 이미지 로드 실패 처리(핵심: error 리스너 추가)
                const handleError = () => {
                    img.removeEventListener('load', handleLoad);
                    img.removeEventListener('error', handleError);
                    reject(new Error(`이미지 로드 실패: ${img.src}`));
                };

                // 이미지 로드 완료
                if (img.complete) {
                    // 로드 상태 확인(캐시된 실패 이미지 방지)
                    if (img.naturalWidth === 0) {
                        reject(new Error(`이미지 로드 오류: ${img.src}`));
                    } else {
                        parseImg();
                    }
                } else {
                    img.addEventListener('load', handleLoad);
                    img.addEventListener('error', handleError);
                    img.src = img.src;
                }
            });
        },
        loadLazyImage: function (callback) {
            o2.require("o2.widget.ImageLazyLoader", function () {
                var loadder = new o2.widget.ImageLazyLoader(this.node, this._getBusinessData());
                loadder.load(function () {
                    if (callback) callback();
                }.bind(this))
            }.bind(this));
        },
        loadImageViewer: function () {
            o2.require("o2.widget.ImageViewer", function () {
                var imageViewer = new o2.widget.ImageViewer(this.node);
                imageViewer.load();
            }.bind(this));
        },
        loadResource: function ( callback ) {
            o2.load([
                "../o2_lib/tinymce/tinymce_5.9.2/tinymce.min.js",
                "../o2_lib/tinymce/tinymce_5.9.2/o2config.js"
            ], function () {
                var config = o2.TinyMCEConfig( this.form.json.mode === "Mobile" );
                callback( config );
            }.bind(this))
        },
        getImageUploadOption: function(){
            return {
                localImageMaxWidth : 2000,
                reference: this.form.businessData.work.job,
                referenceType: "processPlatformJob"
            };
        },
        getEditorId: function(){
            return this.form.businessData.work.id +"_"+this.json.id.split(".").join("_") + "_" + (layout.mobile ? "mobile" : "pc");
        },
        loadTinyMCEEditor: function (config) {
            this.loadResource( function( defaultConfig ){
                var editorConfig = Object.merge(defaultConfig, config || {});

                var id = this.getEditorId();
                editorConfig.selector = '#'+id;
                var editorDiv = new Element("div", {"id": id}).inject(this.node);

                var htmlData = this._getBusinessData();
                if (htmlData) {
                    o2.require("o2.widget.ImageLazyLoader", null, null, false);
                    var loadder = new o2.widget.ImageLazyLoader(this.node, htmlData);
                    editorDiv.set("html", loadder.clearHtml());
                } else if (this.json.templateCode) {
                    editorDiv.set("html", this.json.templateCode || "");
                }
                // var height = this.node.getSize().y;

                editorConfig.base64Encode = !layout.mobile && (this.json.base64Encode === "y");
                editorConfig.enablePreview = (this.json.enablePreview !== "n");
                var options = this.getImageUploadOption();
                for(var key in options){
                    editorConfig[key] = options[key];
                }

                var setup = editorConfig.setup;
                var _self = this;
                editorConfig.setup = function(editor) {
                    this.form.app.addEvent("queryClose", function () {
                        try{
                            editor.destroy();
                            _self.editor = null;
                        }catch (e) {}
                    });
                    this.form.app.addEvent("queryReload", function () {
                        try{
                            editor.destroy();
                            _self.editor = null;
                        }catch (e) {}
                    });
                    this.form.addEvent("reloadReadForm", function () {
                        try{
                            editor.destroy();
                            _self.editor = null;
                        }catch (e) {}
                    });
                    this._loadEvents(editor);
                    if(setup)setup(editor);
                }.bind(this);

                var init_instance_callback = editorConfig.init_instance_callback;
                editorConfig.init_instance_callback = function(editor) {
                    this.editor = editor;

                    this.editor.on("change", function () {
                        this._setBusinessData(this.getData());
                    }.bind(this));
                    if(init_instance_callback)init_instance_callback(editor);
                    if(editorConfig.init_instance_defaultCallback)editorConfig.init_instance_defaultCallback(editor);

                    this.fireEvent("afterLoad");
                }.bind(this);

                tinymce.init(editorConfig);

                this.fieldModuleLoaded = true;
            }.bind(this));
        },
        _loadEvents: function (editor) {
            Object.each(this.json.events, function (e, key) {
                if (e.code) {
                    editor.on(key, function (event) {
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this), this);
                }
            }.bind(this));

        },
        addModuleEvent: function (key, fun) {
            this.editor.on(key, function (event) {
                return (fun) ? fun(this, event) : null;
            }.bind(this), this);
        },
        _loadValue: function () {
            var data = this._getBusinessData();
        },
        // /**
        //  * @summary 컴포넌트 값을 기본값으로 재설정하거나 비움.
        //  *  @example
        //  * this.form.get('fieldId').resetData();
        //  */
        resetData: function () {
            this.setData(this._getBusinessData());
        },
        /**
         * @summary 컴포넌트 값이 비어 있는지 판단.
         * @example
         * if( this.form.get('fieldId').isEmpty() ){
         *     this.form.notice('HTML 편집기는 비워 둘 수 없습니다', 'warn');
         * }
         * @return {Boolean} 값이 비어 있는지 여부.
         */
        isEmpty: function () {
            return !this.getData().trim();
        },
        /**
         * 양식에 해당 컴포넌트가 없을 때 this.data[fieldId]로 값을 조회할 수 있으나, this.form.get('fieldId')로는 컴포넌트를 조회할 수 없음.
         * @summary 컴포넌트 값 가져오기.
         * @example
         * var data = this.form.get('fieldId').getData();
         * @example
         *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
         *  var data;
         *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
         *      data = this.form.get('fieldId').getData();
         *  }else{
         *      data = this.data['fieldId']; //데이터에서 필드 값을 직접 가져오기
         *  }
         * @return 컴포넌트 데이터.
         */
        getData: function () {
            return (this.editor && this.editor.getContent) ? this.editor.getContent() : this._getBusinessData();
        },
        /**
         * 양식에 해당 컴포넌트가 없으면 this.data[fieldId] = data로 값을 설정할 수 있음.
         * @summary 컴포넌트에 값 할당.
         * @param data{String} .
         * @example
         *  this.form.get("fieldId").setData("test"); //텍스트 값 할당
         * @example
         *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
         *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
         *      this.form.get('fieldId').setData( data );
         *  }else{
         *      this.data['fieldId'] = data;
         *  }
         */
        setData: function (data) {
            this._setBusinessData(data);
            if (this.editor && this.editor.setContent) this.editor.setContent(data);
        },
        destroy: function(){
            if( this.editor ){
                this.editor.destroy();
                this.editor = null;
            }
        },
        createErrorNode: function (text) {
            node = new Element("div", {styles:{
                    "margin-top": "0.3em"
                }});
            var iconNode = new Element("div.ooicon-error", {
                "styles": {
                    "width": "20px",
                    "height": "1.2em",
                    "float": "left",
                    "display": "flex",
                    "color": "red",
                    "align-items": "center",
                    "justify-content": "center"
                    // "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "line-height": "1.2em",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
            return node;
        },
        notValidationMode: function (text) {
            if (!this.isNotValidationMode) {
                this.isNotValidationMode = true;
                this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
                this.node.setStyle("border", "1px solid red");

                this.errNode = this.createErrorNode(text).inject(this.node, "after");
                this.showNotValidationMode(this.node);
                if (!this.errNode.isIntoView()) this.errNode.scrollIntoView(false);
            }
        },
        showNotValidationMode: function (node) {
            var p = node.getParent("div");
            if (p) {
                if (p.get("MWFtype") == "tab$Content") {
                    if (p.getParent("div").getStyle("display") == "none") {
                        var contentAreaNode = p.getParent("div").getParent("div");
                        var tabAreaNode = contentAreaNode.getPrevious("div");
                        var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
                        var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
                        tabNode.click();
                        p = tabAreaNode.getParent("div");
                    }
                }
                this.showNotValidationMode(p);
            }
        },
        validationMode: function () {
            if (this.isNotValidationMode) {
                this.isNotValidationMode = false;
                this.node.setStyles(this.node.retrieve("borderStyle"));
                if (this.errNode) {
                    this.errNode.destroy();
                    this.errNode = null;
                }
            }
        },

        validationConfigItem: function (routeName, data) {
            var flag = (data.status == "all") ? true : (routeName == data.decision);
            if (flag) {
                var n = this.getData();
                var v = (data.valueType == "value") ? n : n.length;
                switch (data.operateor) {
                    case "isnull":
                        if (!v) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notnull":
                        if (v) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "gt":
                        if (v > data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "lt":
                        if (v < data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "equal":
                        if (v == data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "neq":
                        if (v != data.value) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "contain":
                        if (v.indexOf(data.value) != -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value) == -1) {
                            this.notValidationMode(data.prompt);
                            return false;
                        }
                        break;
                }
            }
            return true;
        },
        validationConfig: function (routeName, opinion) {
            if (this.json.validationConfig) {
                if (this.json.validationConfig.length) {
                    for (var i = 0; i < this.json.validationConfig.length; i++) {
                        var data = this.json.validationConfig[i];
                        if (!this.validationConfigItem(routeName, data)) return false;
                    }
                }
                return true;
            }
            return true;
        },
        validation: function (routeName, opinion) {
            this.moduleValidationAG = null;
            //if (this.isReadonly() || this.json.showMode!=="disabled" || this.node?.isDisplayNone() || !this.isEditable) return true;

            if (this.isReadonly() || this.json.showMode==="disabled" || !this.isEditable) return true;

            if (!this.validationConfig(routeName, opinion)) return false;

            return this._validation(routeName);
        },


        getExcelData: function(){
            return this.getData();
        },
        setExcelData: function(data){
            if( typeOf(data) === "string" )data = data.replace(/&#10;/g,"<br>"); //excel 필드 줄바꿈은 &#10
            this.excelData = data;
            this.setData(data, true);
        },
        validationExcel: function () {
            if (!this.isReadonly()){
                var errorList = this.validationConfigExcel();
                if (errorList.length) return errorList;

                if (!this.json.validation) return [];
                if (!this.json.validation.code) return [];

                var flag = this.form.Macro.exec(this.json.validation.code, this);

                if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
                if (flag.toString() !== "true") {
                    return [flag];
                }
            }
            return [];
        },
        validationConfigExcel: function () {
            var errorList = [];
            if (this.json.validationConfig){
                if (this.json.validationConfig.length){
                    for (var i=0; i<this.json.validationConfig.length; i++) {
                        var flag = this.validationConfigItemExcel(this.json.validationConfig[i]);
                        if ( flag !== true ){
                            errorList.push( flag );
                        }
                    }
                }
            }
            return errorList;
        },
        validationConfigItemExcel: function(data){
            if ( data.status==="all"){
                var n = this._getBusinessData();
                var v = (data.valueType==="value") ? n : n.length;
                switch (data.operateor){
                    case "isnull":
                        if (!v)return data.prompt;
                        break;
                    case "notnull":
                        if (v)return data.prompt;
                        break;
                    case "gt":
                        if (v>data.value)return data.prompt;
                        break;
                    case "lt":
                        if (v<data.value)return data.prompt;
                        break;
                    case "equal":
                        if (v===data.value)return data.prompt;
                        break;
                    case "neq":
                        if (v!==data.value)return data.prompt;
                        break;
                    case "contain":
                        if (v.indexOf(data.value)!==-1) return data.prompt;
                        break;
                    case "notcontain":
                        if (v.indexOf(data.value)===-1)return data.prompt;
                        break;
                }
            }
            return true;
        }
    });