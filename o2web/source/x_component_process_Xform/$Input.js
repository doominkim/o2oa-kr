MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class $Input 컴포넌트 클래스, 모든 입력 컴포넌트의 부모 클래스
 * @hideconstructor
 * @o2category FormComponents
* @extends MWF.xApplication.process.Xform.$Module
 * @abstract
 */
MWF.xApplication.process.Xform.$Input = MWF.APP$Input =  new Class(
    /** @lends MWF.xApplication.process.Xform.$Input# */
    {
	Implements: [Events],
	Extends: MWF.APP$Module,
	iconStyle: "personfieldIcon",
    options: {
        "moduleEvents": ["change", "load", "queryLoad", "postLoad"]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.field = true;
        this.fieldModuleLoaded = false;
        this.nodeHtml = this.node.get("html");
    },
	_loadUserInterface: function(){
        if ( this.isSectionMergeRead() ) { //구간 병합 표시
            this._loadMergeReadNode();
        }else{
            if( this.isSectionMergeEdit() ){
                this._loadMergeEditNode();
            }else{
                this._loadNode();
            }
            if (this.json.compute === "show"){
                this._setValue(this._computeValue());
            }else{
                this._loadValue();
            }
        }
	},
    _loadDomEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)===-1){
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    _loadEvents: function(){
        Object.each(this.json.events, function(e, key){
            if (e.code){
                if (this.options.moduleEvents.indexOf(key)!==-1){
                    this.addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }else{
                    (this.node.getFirst() || this.node).addEvent(key, function(event){
                        return this.form.Macro.fire(e.code, this, event);
                    }.bind(this));
                }
            }
        }.bind(this));
    },
    addModuleEvent: function(key, fun){
        if (this.options.moduleEvents.indexOf(key)!==-1){
            this.addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }else{
            (this.node.getFirst() || this.node).addEvent(key, function(event){
                return (fun) ? fun(this, event) : null;
            }.bind(this));
        }
    },
    /**
     * @summary 컴포넌트 다시 로드. postLoad 이벤트가 실행됨.
     * @example
     * this.form.get("fieldId").reload(); //다시 로드
     */
    reload: function(){
        if( this.iconNode ){
            this.iconNode.destroy();
            this.iconNode = null;
        }
        if( this.descriptionNode ){
            this.descriptionNode.destroy();
            this.descriptionNode = null;
        }
        this.node.empty();
        this._loadReadEditAbeld();
        this._beforeReloaded();
        this._loadUserInterface();
        this._loadStyles();
        this._afterLoaded();
        this._afterReloaded();
        this.fireEvent("postLoad");
    },

    _loadNode: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node?.addClass('hide');
        }else{
            if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                this._loadNodeEdit();
            }
        }
    },

    // _loadNode: function(){
    //     if (this.isReadonly()){
    //         this._loadNodeRead();
    //     }else{
    //         this._loadNodeEdit();
    //     }
    // },
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        this.clearDefaultMargin();
    },
    loadDescription: function(){
        if (this.isReadonly())return;
        var v = this._getBusinessData();
        if (!v){
            if (this.json.description){
                var size, w;
                if( this.node.offsetParent === null ){ //숨김
                    size = { y: 26 }
                }else{
                    size = this.node.getFirst().getSize();
                    // w = size.x-3;
                    // if( this.hasIcon() ){
                    //     if (COMMON.Browser.safari) w = w-20;
                    // }
                }

                /**
                 * @summary 설명 정보 노드, 사용자가 직접 입력할 수 있는 컴포넌트에만 있으며 읽기 전용일 때는 없음.
                 * @member {Element}
                 */
                this.descriptionNode = new Element("div", {"styles": this.form.css.descriptionNode, "text": this.json.description}).inject(this.node);
                this.descriptionNode.setStyles({
                    "height": ""+size.y+"px",
                    "line-height": ""+size.y+"px"
                });
                // if( w )this.descriptionNode.setStyles({
                //     "width": ""+w+"px"
                // });
                this.descriptionNode.setStyles({
                    "width": "auto",
                    "overflow": "auto"
                });
                this.setDescriptionEvent();
            }
        }
    },
    setDescriptionEvent: function(){
        if (this.descriptionNode){
            this.descriptionNode.addEvents({
                "mousedown": function(){
                    this.descriptionNode.setStyle("display", "none");
                    this.clickSelect();
                }.bind(this)
            });
            this.node.getFirst().addEvents({
                "focus": function(){
                    if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
                }.bind(this),
                "blur": function(){
                    if (!this.node.getFirst().get("value")) if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
                }.bind(this)
            });
        }
    },
    checkDescription: function(){
        if (!this.node.getFirst().get("value")){
            if (this.descriptionNode)  this.descriptionNode.setStyle("display", "block");
        }else{
            if (this.descriptionNode) this.descriptionNode.setStyle("display", "none");
        }
    },
    _resetNodeEdit: function(){
        var input = new Element("input", {
            "styles": {
                "background": "transparent",
                "width": "100%",
                "border": "0px"
            },
            "readonly": true
        });

        var node = new Element("div", {"styles": {
                "overflow": (this.json.styles && this.json.styles.overflow) ? this.json.styles.overflow : "hidden",
                "position": "relative",
                "margin-right": this.hasIcon() ? "20px" : "0px",
                "padding-right": "4px"
            }}).inject(this.node, "after");
        input.inject(node);

        this.node.destroy();
        this.node = node;
    },
    hasIcon: function(){
	    return this.json.showIcon!='no' && ( !this.form.json || !this.form.json.hideModuleIcon );
    },
    _loadNodeEdit: function(){
	    if (!this.json.preprocessing) this._resetNodeEdit();
	    var input = this.node.getFirst();
        if( !input && this.nodeHtml ){
            this.node.set("html", this.nodeHtml);
            input = this.node.getFirst();
        }
        input.set(this.json.properties);
		this.node.set({
			"id": this.json.id,
			"MWFType": this.json.type,
			"readonly": true,
			"events": {
				"click": this.clickSelect.bind(this)
			}
		});
        if ( this.hasIcon() ){
            this.iconNode = new Element("div", {
                "styles": this.form.css[this.iconStyle],
                "events": {
                    "click": this.clickSelect.bind(this)
                }
            }).inject(this.node, "before");
        }else if( this.form.json.nodeStyleWithhideModuleIcon ){
            this.node.setStyles(this.form.json.nodeStyleWithhideModuleIcon)
        }

        this.node.getFirst().addEvent("change", function(){
            this.validationMode();
            o2.promiseAll(this.validation()).then(flag=>{
                if(String(flag) === 'true'){
                    this._setBusinessData(this.getInputData("change"));
                    this.fireEvent("change");
                }
            });
        }.bind(this));
        //
        // var inputNode = this.node.getFirst();
        // if (inputNode) inputNode.addEvent("input", function(e){
        //     var v=e.target.get("value");
        //     this._setEnvironmentData(v);
        // }.bind(this));
	},
    _loadStyles: function(){
        if (this.json.styles) this.node.setStyles(this.json.styles);
        if (this.json.inputStyles) if (this.node.getFirst()) this.node.getFirst().setStyles(this.json.inputStyles);
        if (this.iconNode && this.iconNode.offsetParent !== null){
            var size = this.node.getSize();
            //if (!size.y){
            //    var y1 = this.node.getStyle("height");
            //    var y2 = this.node.getFirst().getStyle("height");
            //    alert(y1+"," +y2);
            //    var y = ((y1!="auto" && y1>y2) || y2=="auto") ? y1 : y2;
            //    size.y = (y=="auto") ? "auto" : y.toInt();
            //    //alert(size.y)
            //}
            this.iconNode.setStyle("height", ""+size.y+"px");
            //alert(this.iconNode.getStyle("height"))
        }
    },
    _computeValue: function(value){
        return (this.json.defaultValue && this.json.defaultValue.code) ? this.form.Macro.exec(this.json.defaultValue.code, this): (value || "");
    },
	getValue: function(){
        if (!this.isReadable) return '';
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if (o2.typeOf(value)==="null" || value==='') value = this._computeValue();
		return value ?? "";
	},
    _setValue: function(value){
        if (!!value && o2.typeOf(value.then)=="function"){
            var p = Promise.resolve(value).then(function(v){
                this.__setValue(v);
            }.bind(this), function(){});
            this.moduleValueAG = p;
        }else{
            this.moduleValueAG = null;
            this.__setValue(value);
        }
    },
    __setValue: function(value){
        this.moduleValueAG = null;
        this._setBusinessData(value);
        if (this.node.getFirst()) this.node.getFirst().set("value", value || "");
        if (this.isReadonly()) this.node.set("text", value);
        this.moduleValueAG = null;
        this.fieldModuleLoaded = true;
        return value;
    },

	_loadValue: function(){
        this._setValue(this.getValue());
	},
	clickSelect: function(){
	},
	_afterLoaded: function(){
//		if (this.iconNode){
////			var p = this.node.getPosition();
////			var s = this.node.getSize();
////			var is = this.iconNode.getSize();
////
////			var y = p.y;
////			var x = p.x+s.x-is.x;
//			this.iconNode.setStyles({
//				"top": "5px",
//				"left": "-18px"
//			});
//		}
        if (!this.isReadonly()){
            this.loadDescription();
        }
	},
    _beforeReloaded: function(){},
    _afterReloaded: function(){},
    /**
     * @summary 컴포넌트가 읽기 전용인지 판단.
     * @example
     * var readonly = this.form.get('subject').isReadonly();
     * @return {Boolean} 읽기 전용 여부.
     */
	isReadonly : function(){
        return !!(!this.isEditable || this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.json.showMode==="read" || this.isSectionMergeRead());
    },
	getTextData: function(){
		//var value = this.node.get("value");
		//var text = this.node.get("text");
        var value = (this.node.getFirst()) ? this.node.getFirst().get("value") : this.node.get("text");
        var text = (this.node.getFirst()) ? this.node.getFirst().get("text") : this.node.get("text");
		return {"value": [value || ""] , "text": [text || value || ""]};
	},
    /**
     * @summary 컴포넌트 값이 비어 있는지 판단.
     * @example
     * if( this.form.get('subject').isEmpty() ){
     *     this.form.notice('제목은 비워 둘 수 없습니다.', 'warn');
     * }
     * @return {Boolean} 값이 비어 있는지 여부.
     */
    isEmpty : function(){
	    var data = this.getData();
	    return !data || !data.trim();
    },
    /**
     * 스크립트에서 this.data[fieldId]로도 컴포넌트 값을 가져올 수 있음.
     * 차이는 다음과 같음:<br/>
     * 1. Promise를 사용하는 경우<br/>
     * 비동기 함수 생성기(Promise)로 컴포넌트에 값을 할당할 때 getData 메서드로 즉시 가져오면 수정 전 값이 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
     * this.data[fieldId]로 즉시 가져오면 비동기 함수 생성기가 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
     * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|자세한 차이는 링크 참고}<br/>
     * 2. 양식에 해당 컴포넌트가 없을 때는 this.data[fieldId]로 값을 가져올 수 있지만, this.form.get('fieldId')로는 컴포넌트를 가져올 수 없음.
     * @summary 컴포넌트 값 가져오기.
     * @example
     * var data = this.form.get('fieldId').getData(); //Promise를 사용하지 않는 경우,
     * @example
     *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
     *  var data;
     *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
     *      data = this.form.get('fieldId').getData();
     *  }else{
     *      data = this.data['fieldId']; //데이터에서 필드 값을 직접 가져오기
     *  }
     * @example
     *  //Promise를 사용하는 경우
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test는 데이터 사전 이름
     *  var promise = dict.get("tools", true); //데이터 사전 get 메서드를 비동기로 사용하면 Promise 반환, 인자 true는 비동기를 의미
     *  promise.then( function(){
     *      var data = field.getData(); //이 시점에는 비동기 요청이 완료되어 getData 메서드로 데이터 사전 값을 가져옴
     *  })
     *  field.setData( promise );
     * @return 컴포넌트 데이터.
     */
	getData: function(when){
        if (this.json.compute == "save") this._setValue(this._computeValue());
        return this.getInputData();
	},
    getInputData: function(){
        if (this.node.getFirst()){
            return this.node.getFirst().get("value");
        }else{
            return this._getBusinessData();
        }
    },
    // /**
    //  * @summary 컴포넌트 값을 기본값으로 재설정하거나 비움.
    //  *  @example
    //  * this.form.get('subject').resetData();
    //  */
    resetData: function(){
        this.setData(this.getValue());
    },
    /**인자가 Promise일 때는 문서 참고: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|Promise로 양식 비동기 처리}<br/>
     * 양식에 해당 컴포넌트가 없을 때는 this.data.add(fieldId, data, true)로 값을 할당할 수 있음.
     * @summary 컴포넌트에 값 할당.
     * @param data{String|Promise} .
     * @param fireChange{boolean} 선택, change 이벤트 발생 여부, 기본값 false.
     * @example
     *  this.form.get("fieldId").setData("test"); //텍스트 값 할당
     * @example
     *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
     *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
     *      this.form.get('fieldId').setData( data );
     *  }else{
     *      this.data.add(fieldId, data, true);
     *  }
     * @example
     *  //Promise 사용
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test는 데이터 사전 이름
     *  var promise = dict.get("tools", true); //데이터 사전 get 메서드를 비동기로 사용하면 Promise 반환, 인자 true는 비동기를 의미
     *  field.setData( promise );
     */
	setData: function(data, fireChange){
        // if (data && data.isAG){
        //     var ag = o2.AG.all(data).then(function(v){
        //         if (o2.typeOf(v)=="array") v = v[0];
        //         this.__setData(v);
        //     }.bind(this));
        //     this.moduleValueAG = ag;
        //     ag.then(function(){
        //         this.moduleValueAG = null;
        //     }.bind(this));
        // }else{
        if (!!data && o2.typeOf(data.then)=="function"){
            var p = o2.promiseAll(data).then(function(v){
                this.__setData(v, fireChange);
                // if (this.node.getFirst() && !this.readonly && !this.json.isReadonly) {
                //     this.checkDescription();
                //     this.validationMode();
                // }
            }.bind(this), function(){});
            this.moduleValueAG = p;
            p.then(function(){
                this.moduleValueAG = null;
            }.bind(this), function(){
                this.moduleValueAG = null;
            }.bind(this));
        }else{
            this.moduleValueAG = null;
            this.__setData(data, fireChange);
            // if (this.node.getFirst() && !this.readonly && !this.json.isReadonly) {
            //     this.checkDescription();
            //     this.validationMode();
            // }
        }
            //this.__setData(data);
        //}
	},
    __setData: function(data, fireChange){
	    var old = this.getInputData();
        this._setBusinessData(data);
        if (this.node.getFirst()){
            this.node.getFirst().set("value", data);
            this.checkDescription();
            this.validationMode();
        }else{
            this.node.set("text", data);
        }
        if (fireChange && old!==data) this.fireEvent("change");
        this.moduleValueAG = null;
    },


    createErrorNode: function(text){

        //var size = this.node.getFirst().getSize();
        //var w = size.x-3;
        //if (COMMON.Browser.safari) w = w-20;
        //node.setStyles({
        //    "width": ""+w+"px",
        //    "height": ""+size.y+"px",
        //    "line-height": ""+size.y+"px",
        //    "position": "absolute",
        //    "top": "0px"
        //});
        var node;
        if( this.form.json.errorStyle ){
            if( this.form.json.errorStyle.type === "notice" ){
                if( !this.form.errorNoticing ){ //팝업이면
                    this.form.errorNoticing = true;
                    this.form.notice(text, "error", this.node, null, null, {
                        onClose : function () {
                            this.form.errorNoticing = false;
                        }.bind(this)
                    });
                }
            }else{
                node = new Element("div",{
                    "styles" : this.form.json.errorStyle.node,
                    "text": text
                });
                if( this.form.json.errorStyle.close ){
                    var closeNode = new Element("div",{
                        "styles" : this.form.json.errorStyle.close ,
                        "events": {
                            "click" : function(){
                                //this.destroy();
                                this.validationMode();
                            }.bind(this)
                        }
                    }).inject(node);
                }
            }
        }else{
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
        }
        return node;
    },
    notValidationMode: function(text){
        if (!this.isNotValidationMode){
            this.isNotValidationMode = true;
            this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
            this.node.setStyle("border-color", "red");

            this.errNode = this.createErrorNode(text);
            //if (this.iconNode){
            //    this.errNode.inject(this.iconNode, "after");
            //}else{
                this.errNode.inject(this.node, "after");
            //}
            this.showNotValidationMode(this.node);

            var parentNode = this.errNode;
            while( parentNode && parentNode.offsetParent === null ){
                parentNode = parentNode.getParent();
            }

            if ( parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
        }
    },
    showNotValidationMode: function(node){
        var p = node.getParent("div");
        if (p){
            var mwftype = p.get("MWFtype") || p.get("mwftype");
            if (mwftype == "tab$Content"){
                if (p.getParent("div").getStyle("display")=="none"){
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
    validationMode: function(){
        if (this.isNotValidationMode){
            this.isNotValidationMode = false;
            this.node.setStyles(this.node.retrieve("borderStyle"));
            if (this.errNode){
                this.errNode.destroy();
                this.errNode = null;
            }
        }
    },
    validationConfigItem: function(routeName, data){
        var flag = (data.status==="all") ? true: (routeName === data.decision);
        if (flag){
            var n = this.getInputData();
            var v = (data.valueType==="value") ? n : n.length;
            switch (data.operateor){
                case "isnull":
                    if ((!v && v!==0) || (o2.typeOf(v)==="array" && !v.length)){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notnull":
                    if (v){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "gt":
                    if (v>data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "lt":
                    if (v<data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "equal":
                    if (v==data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "neq":
                    if (v!=data.value){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "contain":
                    if (v.indexOf(data.value)!=-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
                case "notcontain":
                    if (v.indexOf(data.value)==-1){
                        this.notValidationMode(data.prompt);
                        return false;
                    }
                    break;
            }
        }
        return true;
    },
    validationConfig: function(routeName, opinion){
        if (this.json.validationConfig){
            if (this.json.validationConfig.length){
                for (var i=0; i<this.json.validationConfig.length; i++) {
                    var data = this.json.validationConfig[i];
                    if (!this.validationConfigItem(routeName, data)) return false;
                }
            }
            return true;
        }
        return true;
    },

    getExcelData: function(){
        return this.getData();
    },
    setExcelData: function(data){
	    if( typeOf(data) === "string" )data = data.replace(/&#10;/g,""); //excel 필드 줄바꿈은 &#10
        this.excelData = data;
        this.setData(data, true);
    },
    stringToArray: function(string){ //excel 필드 줄바꿈은 &#10;, 줄바꿈과 쉼표를 배열 구분자로 사용
        return string.replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
            return !!s;
        });
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
            var ed = typeOf( this.excelData ) === "null" ? "" : this.excelData;
            var v, ev;
            if(data.valueType==="value"){
                v = n;
                ev = ed;
            }else{
                v = n.length;
                ev = ed.length || 0;
            }
            switch (data.operateor){
                case "isnull":
                    if (!v){
                        return !ev ? data.prompt : "선택 항목에 없습니다";
                    }
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
