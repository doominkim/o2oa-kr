/*
 DOMElement 관리 클래스
 var item = new MDomItem( containerNode, {
     name : "demo",
     type : "checkbox",
     selectValue: [opt1,opt2,opt3]
     selectText : function( callback , options ){ //비동기 메서드 작성법, callback이 필요하며 이 option 값을 callback에 넘겨 실행, 이 function은 bind하면 안 됨
         this.app.actions.XXX( id, function( json ){
         var optText = [];
         ....
         callback( optText )
     })
 },
 value : function(){ var val = .... ; return val; }
 } , null, app, css);
 item.load();
 외부 메서드:
 get( textOrValue ) 파라미터 "text" 또는 "value"로 텍스트 또는 값 조회
 getValue() 값 조회
 getText() 텍스트 조회
 getModifiedValue() 수정된 값 조회, 수정되지 않았으면 null 반환
 getModifiedText() 수정된 텍스트 조회, 수정되지 않았으면 null 반환
 setValue() 객체 값 설정
 resetType( type ) 유형 변경
 resetItemOptions( selectValue[, selectText ]) 선택 항목 변경, selectValue는 선택 값, selectText는 선택 텍스트
 dispose() 객체 비우기
 verify( isShowWarning ) 파라미터 isShowWarning: 검증 실패 시 사용자에게 알릴지 여부. isShowWarning 파라미터와 options의 notEmpty, warningType, validRule 속성으로 객체를 검증하고 boolean 반환
 */

//MWF.xDesktop.requireApp("Template", "lp." + MWF.language, null, false);
var MDomItem_ClassType = {
    "text": "Text",
    "textarea": "Textarea",
    "hidden": "Hidden",
    "password": "Password",
    "radio": "Radio",
    "checkbox": "Checkbox",
    "select": "Select",
    "multiselect": "Multiselect",
    "innertext": "Innertext",
    "innerhtml": "Innerhtml",
    "img": "Img",
    "button": "Button",
    "mselector": "MSelector",
    "imageclipper": "ImageClipper",
    "rtf": "Rtf",
    "org": "Org",
    "a": "A",
    "oo-button": 'OOButton',
    "oo-checkgroup": "OOCheckGroup",
    "oo-datetime": "OODatetime",
    "oo-input": "OOInput",
    "oo-org": "OOSelector",
    "oo-radiogroup": "OORadioGroup",
    "oo-select": "OOSelect",
    "oo-cascade": "OOCascade",
    "oo-textarea": "OOTextarea"
};

var MDomItem = new Class({
    Implements: [Options, Events],
    options: {
        name: "",		//생성된 객체의 name 속성

        value: "",		//객체의 값
        text: "",		//대응하는 한국어 이름
        type: "",  //text,innertext, radio,checkbox,select,multiselect,img,button,hidden,rtf,imageClipper, org, mSelector 가능

        isEdited: true,

        tType: "", //type이 text일 때 유효, number,date,time, datetime,person, unit, identity 가능, 조직 혼합 선택이면 배열 사용, 예: ["person", "unit", "identity"]

        orgType: "", //person, unit, identity, process, duty, 혼합 선택이면 배열 사용, 예: ["person", "unit", "identity"]
        unitType: "", //orgType에 unit이 포함되면 조직 유형 지정 가능
        count: 1, //다중 선택이면 다중 선택 상한값, 0은 제한 없음, 기본값 1,
        units: [], //orgType이 identity, unit일 때의 부서 선택 범위
        groups: [], //orgType이 person일 때의 선택 범위
        exclude: [], //선택 시 제외 항목
        expandSubEnable: true, //orgType이 identity, unit일 때 하위 선택 범위 펼치기 여부
        orgStyle: "", //표시 유형, 예: default, xform 등

        unsetDefaultEvent: false, //tType 또는 orgType에 값이 있을 때 기본 이벤트 취소 여부

        //json 전달 가능, 예:  { change : function(){alert("change " + this.name );}, click : function(){alert("click " + this.name)  } }
        //또는 문자열:                  "{ change : function(){alert('change ' + this.name );}, click : function(){alert('click ' + this.name)  } }"
        //또는 문자열:                     "change^^function(){alert('change ' + this.name );##click^^function(obj){alert('click ' + this.name);}"
        event: null,	//바인딩할 이벤트

        selectValue: "",	//선택형 컨트롤의 선택 값
        selectText: "",	//선택형 컨트롤의 선택 텍스트
        defaultValue: "",	//기본값
        className: "",		//클래스
        style: {},			//스타일
        attr: {},	//기타 파라미터, 예: " {readonly : true, size : '20' }"

        notEmpty: false, //빈 값 허용 여부, 기본 허용
        defaultValueAsEmpty: false, //빈 값 확인 시 기본값을 빈 값으로 볼지 여부, 값을 조회할 때 기본값이면 빈 값 반환
        emptyTip: null, //빈 값일 때의 안내, 설정하지 않아도 됨

        disable: false, //false이면 load 무효;

        //alert 또는 batch 또는 single, 단건 알림 또는 일괄 알림
        warningType: "batch",

        validImmediately: false,

        //검증 유형 또는 사용자 정의 메서드 전달 가능, 예
        //{email : true , url : true, date : true, dateISO : true, number : true, digits : true,
        // maxlength:5, minlength:10, rangelength:[5,10], max:5, min:10 ,range:[5,10], extension: ["xls","xlsx"],fun : function(){ return true }
        // }
        validRule: null,

        //validMessage는 validRule에 대응하며 오류 시 표시할 메시지, 예 {email : "올바른 형식의 이메일을 입력하십시오", fun : "올바른 비밀번호를 입력하십시오"}, 설정하지 않으면 기본값은 다음과 같음:
        // {  email: "올바른 형식의 이메일을 입력하십시오",
        // url: "올바른 웹 주소를 입력하십시오",
        // date: "올바른 날짜를 입력하십시오",
        // dateISO: "올바른 날짜를 입력하십시오 (ISO).",
        // number: "올바른 숫자를 입력하십시오",
        // digits: "정수만 입력할 수 있습니다",
        // maxlength: this.format("최대 {0}자까지 입력하십시오"),
        // minlength: this.format("최소 {0}자 이상 입력하십시오"),
        // rangelength: this.format("{0}자에서 {1}자 사이로 입력하십시오"),
        // range: this.format("{0}에서 {1} 사이의 값을 입력하십시오"),
        // max: this.format("{0} 이하의 값을 입력하십시오"),
        // min: this.format("{0} 이상의 값을 입력하십시오"),
        // fun : "올바른 값을 입력하십시오: "+ this.options.text
        // }
        validMessage: null,

        RTFConfig: null, //CKEditor 설정 항목
        mSelectorOptions: null, //사용자 정의 드롭다운 컴포넌트 설정 항목
        calendarOptions: null, //날짜 선택기 설정 항목
        orgWidgetOptions: null, //org 컴포넌트 옵션
        orgOptions: null
    },
    initialize: function (container, options, parent, app, css) {
        this.form = parent;
        this.tr = parent;
        this.parent = parent;
        this.app = app;
        this.container = $(container);
        if (css) this.css = css;
        if (options.formStyle) {
            this.cssPath = "../x_component_Template/$MForm/" + options.formStyle + "/css.wcss";
            this._loadCss();
        }

        //for(var o in options ){	//function으로 설정 계산 허용, on으로 시작하는 속성은 fireEvent용으로 예약
        //    if( o !== "validRule" && o!=="validMessage" && o.substr(0,2)!=="on" && typeOf( options[o] )=== "function" ){
        //        options[o] = options[o].call();
        //    }
        //}
        //this.setOptions(options);

        this.valSeparator = /,|;|\^\^|\|/; //다중 값 객체이면 사용자가 선택한 여러 값의 구분자
        this.mElement = this.container;	//컨테이너
        this.node = this.container;
        this.items = [];
        this.optionFuns = {};

        this.orginalOptions = options;

        this.checkOptions(options);

        // this.setOptionList( options );
    },
    checkOptions: function (opts, callback) {
        this.optionsReady = false;
        this._checkOptions(opts, (options, isSync)=>{
            this.optionsReady = true;
            this.setOptions(options);
            if(isSync){
                if (this.loadFunctionCalled) { //외부 프로그램이 이미 load를 실행했지만 options 설정이 끝나지 않아 중단되었다면 load를 다시 호출해야 함
                    this.load();
                }
            }
            if(callback)callback();
        });
    },
    _checkOptions: function (opts, callback) {
        var ps = [];
        var keys_ps = [];
        var hasFun = false;

        for (var key in opts) {
            if (key !== "validRule" && key !== "validMessage" && key.substr(0, 2) !== "on" && typeOf(opts[key]) === "function") {
                hasFun = true;
                break;
            }
        }

        if (!hasFun) {
            if(callback)callback(opts);
        } else {
            var options = Object.merge({}, opts);
            Object.each(options, (o, key) => {
                if (key !== "validRule" && key !== "validMessage" && key.substr(0, 2) !== "on" && typeOf(o) === "function") {
                    var value = o.apply(this, [options]);
                    if (value && value.then && typeOf(value.then) === 'function') {
                        ps.push(value);
                        keys_ps.push(key);
                    } else {
                        options[key] = value;
                    }
                }
            });
            if (ps.length) {
                Promise.all(ps).then((arr) => {
                    arr.forEach((v, i) => {
                        options[keys_ps[i]] = v;
                    });
                    if (callback) callback(options, true);
                })
            } else {
                if (callback) callback(options);
            }
        }
    },
    // setOptionList : function( options ){  //options의 function 비동기 메서드로 function(callback){ ...value 조회; callback( value );  } 형태의 콜백으로 option을 설정하기 위함
    //     var callbackNameList = [];
    //     for(var o in options ){	//function으로 설정 계산 허용, on으로 시작하는 속성은 fireEvent용으로 예약
    //         if( o !== "validRule" && o!=="validMessage" && o.substr(0,2)!=="on" && typeOf( options[o] )== "function" ){
    //             var fun = options[o];
    //             if( fun.length && /\(\s*([\s\S]*?)\s*\)/.exec(fun)[1].split(/\s*,\s*/)[0] == "callback" ){ //형식 파라미터가 있고(fun.length!==0) 첫 번째 형식 파라미터가 callback인 경우, 주의: funciont는 bind(this)하면 안 됨, 그렇지 않으면 판단 불가
    //                 callbackNameList.push( o );
    //             }else{
    //                 options[o] = fun( options, this ); //fun 실행
    //             }
    //         }
    //     }
    //     this.setFunOption( options, callbackNameList, true ); //콜백을 재귀 실행하여 options 설정
    // },
    // setFunOption : function( options, callbackNameList, isFirst ){
    //     this.optionsReady = false;
    //     if( callbackNameList.length == 0 ){
    //         this.setOptions( options );
    //         this.optionsReady = true;
    //         if( this.loadFunctionCalled ){ //외부 프로그램이 이미 load를 실행했지만 options 설정이 끝나지 않아 중단되었다면 load를 다시 호출해야 함
    //             this.load();
    //         }
    //     }else{
    //         if( isFirst )options = Object.merge( {}, options ); //외부 프로그램의 options 수정 방지
    //         var name = callbackNameList.shift(); //첫 번째 요소를 반환하고 callbackNameList에서 첫 번째 요소 삭제
    //         var fun = options[name];  //대응하는 파라미터, function임
    //         fun( function( val ){  //function 실행
    //             options[name] = val; //콜백 내부에서 option에 값 할당
    //             this.setFunOption( options, callbackNameList, false );  //다음 콜백 계속 실행
    //         }.bind(this), options );
    //     }
    // },
    reload: function () {
        this.optionsReady = false;
        this._checkOptions(this.orginalOptions, (options)=>{
            this.setOptions(options);
            this.optionsReady = true;
            this._reload();
        });
    },
    _reload: function (){
        this.mElement.empty();
        this.items = [];
        this.load();
    },
    load: function () {
        if (!this.optionsReady) { //options 설정이 끝나지 않은 경우
            this.loadFunctionCalled = true;
            return;
        }
        if (this.options.disable) return;
        if (!this.options.type) {
            this.options.type = this.options.orgType ? "org" : "text";
        }
        this.options.type = this.options.type.toLowerCase();

        this.fireEvent("queryLoad");

        this.createElement();

        this.fireEvent("postLoad", [this]);
    },
    _loadCss: function (reload) {
        var key = encodeURIComponent(this.cssPath);
        if (!reload && o2.widget.css[key]) {
            this.css = !this.css ? o2.widget.css[key] : Object.merge({}, o2.widget.css[key], this.css);
        } else {
            this.cssPath = (this.cssPath.indexOf("?") !== -1) ? this.cssPath + "&v=" + o2.version.v : this.cssPath + "?v=" + o2.version.v;
            var r = new Request.JSON({
                url: o2.filterUrl(this.cssPath),
                secure: false,
                async: false,
                method: "get",
                noCache: false,
                onSuccess: function (responseJSON, responseText) {
                    this.css = !this.css ? responseJSON : Object.merge({}, responseJSON, this.css);
                    o2.widget.css[key] = responseJSON;
                }.bind(this),
                onError: function (text, error) {
                    alert(error + text);
                }
            });
            r.send();
        }
    },
    getLabelLength: function (label){
        if (label && label.length < 4) {
            if (/^[\u4e00-\u9fa5]+$/.test(label)) { //전체 한자
                return label.length;
            }
        }
        return 0;
    },
    fitLabel: function (label) {
        switch (this.getLabelLength(label)) {
            case 2:
                return label[0] + '　　' + label[1];
            case 3:
                return label[0] + ' ' + label[1] + ' ' + label[2];
            default:
                return label;
        }
    },
    editMode: function (keep) {
        if (keep) this.save();
        this.options.isEdited = true;
        this.dispose();
        this.items = [];
        this.load();
    },
    save: function () {
        this.options.value = this.getValue();
    },
    readMode: function (keep) {
        if (keep) this.save();
        this.options.isEdited = false;
        this.dispose();
        this.items = [];
        this.load();
    },
    enable: function () {
        this.options.disable = false;
        this.dispose();
        this.load();
    },
    disable: function () {
        this.options.disable = true;
        this.dispose();
    },
    createElement: function () {
        if (this.options.disable) return;
        var clazzName = MDomItem_ClassType[this.options.type];
        if (clazzName) {
            this.dom = new MDomItem[clazzName](this);
            this.dom.load();
        }
        return this.container;
    },
    get: function (vort) {	//value와 text
        if (this.options.disable) {
            return {
                text: "",
                value: ""
            };
        }
        if (this.dom) return this.dom.get(vort);
    },
    getValue: function (separator, name) {
        var result = this.get(null, name).value;
        if (separator && typeOf(result) === "array") {
            return result.join(separator);
        } else {
            return result;
        }
    },
    getText: function (separator, name) {
        var result = this.get(null, name).text;
        if (separator && typeOf(result) === "array") {
            return result.join(separator);
        } else {
            return result;
        }
    },
    getModifiedValue: function (separator) {
        var value = this.getValue(separator);
        return value === this.options.value ? null : value;
    },
    getModifiedText: function () {
        var text = this.getText();
        return text === this.options.text ? null : text;
    },
    getVaildValue: function (verify, separator, isHiddenWarming, onlyModified) {
        if (!verify || this.verify(!isHiddenWarming)) {
            return onlyModified ? this.getModifiedValue(separator) : this.getValue(separator);
        } else {
            return false;
        }
    },
    set: function (type, valueOrText) {
        this.setValue(valueOrText)
    },
    resetItemOptions: function (selectValue, selectText, isForce) {
        if (this.options.disable) {
            if (isForce) {
                this.options.disable = false;
            } else {
                return;
            }
        }
        var availTypes = "radio,checkbox,select,multiselect".split(",");
        if (availTypes.contains(this.options.type)) {
            this.options.selectValue = selectValue;
            this.options.selectText = selectText;
            this._reload();
        }
    },
    reloadItemOptions: function () {
        var availTypes = "radio,checkbox,select,multiselect,oo-radio-group,oo-checkbox-group,oo-select".split(",");
        if (!availTypes.contains(this.options.type))return;
        var opts = {};
        ['selectGroup', 'selectOption', 'selectValue', 'selectText'].forEach( (key)=> {
            if( this.orginalOptions[key] ) {
                opts[key] = this.orginalOptions[key];
            }
        });

        this.optionsReady = false;
        this._checkOptions(opts, (options)=>{
            this.setOptions(options);
            this.optionsReady = true;
            this._reload();
        });
    },
    reset: function () {
        if (typeOf(this.dom.reset) === "function") {
            this.dom.reset();
        } else {
            this.setValue(this.options.defaultValue || "");
        }
    },
    setValue: function (value) {
        if (this.dom) this.dom.setValue(value);
    },
    setStyles: function (styles) {
        if (this.options.disable) return;
        this.items.each(function (item) {
            item.setStyles(styles)
        })
    },
    getElements: function () {
        if (this.options.disable) return null;
        return this.mElement.getElements("[name='" + this.options.name + "']");
    },
    dispose: function () {
        this.container.empty();
    },
    verify: function (isShowWarning) {
        var flag = true;
        if (!this.options.isEdited) return flag;
        if (this.options.disable) return flag;

        if (this.options.warningType === "batch") {
            if (!this.isNotEmpty(isShowWarning)) flag = false;
            if (!this.checkValid(isShowWarning)) flag = false;
        } else {
            if (!this.isNotEmpty(isShowWarning) || !this.checkValid(isShowWarning)) {
                return false;
            }
        }
        return flag;
    },
    isNotEmpty: function (isShowWarning) {
        if (!this.options.isEdited) return true;
        if (this.options.disable) return true;
        if (this.options.notEmpty === true || this.options.notEmpty === "yes") {
            if (!this.checkNotEmpty(isShowWarning)) {
                return false;
            }
        }
        return true;
    },
    checkNotEmpty: function (isShowWarning) {
        if (this.options.disable) return true;
        var value = this.getValue();
        var isEmpty = typeOf(value) === "array" ? (value.length === 0) : (value === null || value === undefined || value === "" || value === " ");
        if (!isEmpty && this.options.defaultValueAsEmpty) {
            isEmpty = typeOf(value) === "array" ? (value.length === 1 && value[0] === this.options.defaultValue) : value === this.options.defaultValue;
        }
        if (!isEmpty) {
            this.clearWarning("empty");
            return true;
        }
        if (!isShowWarning) return false;
        var text = this.options.text || this.options.label;
        var items = this.mElement.getElements("[name='" + this.options.name + "']");
        var focus = false;
        try {
            var warningText = this.options.emptyTip ||
                (this.dom && this.dom.getErrorText()) ||
                MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", text);

            switch (this.options.warningType) {
                case 'batch':
                case "single":
                    this.setWarning(warningText, "empty");
                    break;
                default:
                    if (this.app && this.app.notice) {

                        if (!this.container.isIntoView()) {
                            this.container.scrollIntoView({ behavior: "smooth", block: "center" });
                            // var pNode = this.container.getParent();
                            // while (pNode && ((pNode.getScrollSize().y - (pNode.getComputedSize().height + 1) <= 0) || pNode.getStyle("overflow") === "visible")) {
                            //     pNode = pNode.getParent();
                            // }
                            // if (!pNode) pNode = document.body;
                            // pNode.scrollToNode(this.container, "bottom");
                        }
                        var y = this.container.getSize().y;
                        this.app.notice(warningText, "error", this.container, {"x": "right", "y": "top"}, {x: 10, y: y});
                    }
                    if (!this.options.validImmediately) {
                        if (["text", "password", "textarea", "select", "multiselect"].contains(this.options.type)) {
                            items[0].focus();
                        }
                    }
            }
            this.fireEvent("empty", this);
        } catch (e) {
        }
        return false;
    },
    clearWarning: function (type){
        this.options.type.startsWith('oo-') ?
            this._clearOOElWarning(type) :
            this._clearElWarning(type);
    },
    _clearOOElWarning: function (type){
        var node = this.mElement.querySelector("[name='" + this.options.name + "']");
        this.validationText = '';
        node && node.unInvalidStyle && node.unInvalidStyle();
    },
    _clearElWarning: function (type) {
        if (this.tipNode && this.setedEmpty) {
            this.fireEvent("unempty", this);
            this.tipNode.empty();
            this.setedEmpty = false;
        }
        if (type === "empty") {
            if (this.warningEmptyNode) {
                this.fireEvent("unempty", this);
                this.warningEmptyNode.destroy();
                this.warningEmptyNode = null;
            }
        } else {
            if (this.warningInvalidNode) {
                this.fireEvent("unempty", this);
                this.warningInvalidNode.destroy();
                this.warningInvalidNode = null;
            }
        }
        this.warningStatus = false;
    },
    setWarning: function (msg, type) {
        this.options.type.startsWith('oo-') ?
            this._setOOElWarning(msg, type) :
            this._setElWarning(msg, type);
    },
    _setOOElWarning: function (msg, type) {
        var node = this.mElement.querySelector("[name='" + this.options.name + "']");
        this.validationText = msg;
        node && node.checkValidity && node.checkValidity();
    },
    _setElWarning: function (msg, type) {
        var div;
        if (type === "empty") {
            if (this.tipNode) {
                this.setedEmpty = true;
                div = this.tipNode;
                div.set("html", "");
            } else if (this.warningEmptyNode) {
                div = this.warningEmptyNode;
                div.set("html", "");
            } else {
                div = this.warningEmptyNode = new Element("div");
                div.inject(this.container);
            }
        } else {
            if (this.tipNode) {
                this.setedEmpty = true;
                div = this.tipNode;
                div.set("html", "");
            } else if (this.warningInvalidNode) {
                div = this.warningInvalidNode;
                div.set("html", "");
            } else {
                div = this.warningInvalidNode = new Element("div");
                div.inject(this.container);
            }
        }

        this.warningStatus = true;

        if (typeOf(msg) !== "array") {
            msg = [msg];
        }

        msg.each(function (m) {
            //var html = "<table style=\"margin-top:3px;\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">";
            //html += "<tr valign=\"middle\"><td><img src=\"./img/exclamation.png\" /></td>";
            //html += "<td style=\"width:3px;\"></td><td><div style=\"color:#FF0000; margin-top:2px;\">"+m+"</div></td></tr>";
            //html += "</table>";
            var node = new Element("div", {
                "text": m,
                "styles": this.css.warningMessageNode
            }).inject(div)
        }.bind(this));

    },
    checkValid: function (isShowWarning) {
        if (this.options.disable) return true;
        if (['oo-button', 'button', 'a'].contains((this.options.type || 'text').toLowerCase())) return true;
        var value = this.getValue();
        var rules = this.options.validRule;
        if (!rules) return true;

        var msgs = [];
        var flag = true;

        //if( value && value !== "" && value !== " " ){
        var rule, msg, method, valid;
        if (typeOf(rules) === "object") {
            for (var r in rules) {
                valid = true;
                rule = rules[r];

                if (typeof rule === "function") {
                    valid = rule.call(this, value, this);
                } else if (this.validMethod[r]) {
                    method = this.validMethod[r];
                    valid = method.call(this, value, rule, this);
                }

                if (!valid && isShowWarning) {
                    msg = this.getValidMessage(r, rule);
                    if (msg !== "") msgs.push(msg);
                }

                if (!valid) flag = false;
            }
        } else if (typeOf(rules) === "array") {
            for (var i = 0; i < rules.length; i++) {
                if (typeof rules[i] === "function") {
                    msg = rules[i].call(this, value, this);
                    if (msg && typeof msg === "string") {
                        flag = false;
                        if (isShowWarning) msgs.push(msg);
                    }
                }
            }
        } else if (typeOf(rules) === "function") {
            msg = rules.call(this, value, this);
            if (msg && typeof msg === "string") {
                flag = false;
                if (isShowWarning) msgs.push(msg);
            }
        }
        //}

        if (msgs.length > 0) {
            switch (this.options.warningType){
                case 'batch':
                case 'single':
                    this.setWarning(msgs, "invaild");
                    break;
                default:
                    if (this.app && this.app.notice) {
                        if (!this.container.isIntoView()) {
                            this.container.scrollIntoView({ behavior: "smooth", block: "center" });
                            // var pNode = this.container.getParent();
                            // while (pNode && ((pNode.getScrollSize().y - (pNode.getComputedSize().height + 1) <= 0) || pNode.getStyle("overflow") === "visible")) pNode = pNode.getParent();
                            // if (!pNode) pNode = document.body;
                            // pNode.scrollToNode(this.container, "bottom");
                        }
                        var y = this.container.getSize().y;
                        this.app.notice(msgs.join("\n"), "error", this.container, {"x": "right", "y": "top"}, {
                            x: 10,
                            y: y
                        });
                    }
            }
            this.fireEvent("empty", this);
        } else {
            if (this.warningInvalidNode) {
                this.warningInvalidNode.destroy();
                this.warningInvalidNode = null;
            }
            this.fireEvent("unempty", this);
        }

        return flag;

    },
    validMethod: {
        email: function (value) {
            return /^[a-zA-Z0-9.!#$%&'*+\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$/.test(value);
        },
        url: function (value) {
            return /^(https?|s?ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
        },
        phoneNumber: function (value) {
            return /^0?1[0-9]\d{9}$/.test(value);
        },
        date: function (value) {
            return !/Invalid|NaN/.test(new Date(value).toString());
        },
        dateISO: function (value) {
            return /^\d{4}[\/\-](0?[1-9]|1[012])[\/\-](0?[1-9]|[12][0-9]|3[01])$/.test(value);
        },
        number: function (value) {
            return /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/.test(value);
        },
        digits: function (value) {
            return /^\d+$/.test(value);
        },
        minlength: function (value, param) {
            return value.length >= param;
        },
        maxlength: function (value, param) {
            return value.length <= param;
        },
        rangelength: function (value, param) {
            return (value.length >= param[0] && value.length <= param[1]);
        },
        min: function (value, param) {
            return value >= param;
        },
        max: function (value, param) {
            return value <= param;
        },
        range: function (value, param) {
            return (value >= param[0] && value <= param[1]);
        },
        extension: function (value, param) {
            param = typeOf(param) === "array" ? param.join("|") : param.replace(/,/g, "|"); //"png|jpe?g|gif";
            return value.match(new RegExp(".(" + param + ")$", "i"));
        }
    },
    getValidMessage: function (type, param) {
        var msg = this.options.validMessage;
        if (msg && typeOf(msg) === "object") {
            if (msg[type]) {
                if (typeof msg[type] === "function") {
                    return (msg[type]).call(this);
                } else {
                    return msg[type];
                }
            }
        }
        var lp = MWF.xApplication.Template.LP.MDomItem;
        switch (type) {
            case "email":
                return lp.emailTip;
            case "url":
                return lp.urlTip;
            case "phoneNumber":
                return lp.phoneNumberTip;
            case "date":
                return lp.dateTip;
            case "dateISO":
                return lp.dateISOTip;
            case "number":
                return lp.numberTip;
            case "digits":
                return lp.digitsTip;
            case "maxlength":
                return lp.maxlengthTip.replace("{n}", param);
            case "minlength":
                return lp.minlengthTip.replace("{n}", param);
            case "rangelength":
                return lp.rangelengthTip.replace("{n0}", param[0]).replace("{n1}", param[1]);
            case "range":
                return lp.rangeTip.replace("{n0}", param[0]).replace("{n1}", param[1]);
            case "min":
                return lp.minTip.replace("{n}", param);
            case "max":
                return lp.maxTip.replace("{n}", param);
            case "extension":
                return lp.extensionTip.replace("{text}", param);
            default:
                return lp.defaultTip.replace("{text}", this.options.text);
        }
    },
    destroy: function () {
        if (this.dom && this.dom.OrgWidgetList) {
            this.dom.OrgWidgetList.each(function (widget) {
                widget.destroy();
            })
        }
        if (this.mElement) {
            this.mElement.empty();
        }
        MWF.release(this);
    }
});

MDomItem.Util = {
    selectCalendar: function (target, container, options, callback) {
        var type = options.type;
        var calendarOptions = {
            "style": "xform",
            "isTime": type === "time" || type.toLowerCase() === "datetime",
            "timeOnly": type === "time",
            "target": container,
            "onComplate": function (dateString, date) {
                if (callback) callback(dateString, date);
            }.bind(this),
            "onClear": function () {
                if (callback) callback("", null);
            }
        };
        if (options.calendarOptions) {
            calendarOptions = Object.merge(calendarOptions, options.calendarOptions)
        }
        var calendar;
        MWF.require("MWF.widget.Calendar", function () {
            calendar = new MWF.widget.Calendar(target, calendarOptions);
            calendar.show();
        }.bind(this), false);
        return calendar;
    },
    selectPerson: function (container, options, callback) {
        if (options.type === "custom") {
            this._selectCustom(container, options, callback);
        } else {
            this._selectPerson(container, options, callback);
        }
    },
    _selectCustom: function (container, options, callback) {
        MWF.xDesktop.requireApp("Template", "Selector.Custom", null, false); //리소스 로드

        var opt = {
            "title": options.title,
            "count": options.count,
            "values": options.selectedValues || [],
            "expand": typeOf(options.expand) === "boolean" ? options.expand : true,
            "exclude": options.exclude || [],
            "expandSubEnable": typeOf(options.expandSubEnable) === "boolean" ? options.expandSubEnable : true,
            "hasLetter": false, //문자 클릭 검색 여부
            "hasTop": false, //선택 가능, 선택됨 제목
            // "level1Indent": 0, //첫 번째 레벨 들여쓰기
            // "indent": 36, //두 번째 레벨 이상의 들여쓰기
            "selectAllEnable": true, //다중 선택 허용 여부, 분류를 선택할 수 있으면 blue_flat 스타일에서는 무효
            "width": "700px", //선택 상자 너비
            "height": "550px", //선택 상자 높이
            "category": true, //분류별 선택
            "noSelectedContainer": false, //오른쪽 선택 영역 숨김 여부
            "categorySelectable": false, //분류 선택 가능 여부, 선택 가능하면 item의 이벤트 실행
            "uniqueFlag": "id", //항목 매칭(선택 여부) 키워드
            "defaultExpandLevel": 1, //기본 펼침 항목, 0은 모든 분류 접기
            "onComplete": function (array) {
                if (callback) callback(array);
            }.bind(this)
        };
        if (options.orgOptions) {
            opt = Object.merge(opt, options.orgOptions);
        }
        var selector = new MWF.xApplication.Template.Selector.Custom(container, opt);
        selector.load();
    },
    _selectPerson: function (container, options, callback) {
        MWF.xDesktop.requireApp("Selector", "package", null, false);

        var selectType = "", selectTypeList = [];
        var type = options.type;
        if (typeOf(type) === "array") {
            if (type.length > 1) {
                selectTypeList = type;
            } else if (type.length === 0) {
                selectType = "person";
            } else {
                selectType = type[0] || "person";
            }
        } else {
            selectType = type || "person";
        }

        var opt = {
            "type": selectType,
            "types": selectTypeList,
            "title": options.title,
            "count": options.count,
            "values": options.selectedValues || [],
            "units": options.units,
            "unitType": options.unitType,
            "groups": options.groups,
            "expand": options.expand,
            "exclude": options.exclude || [],
            "expandSubEnable": options.expandSubEnable,
            "onComplete": function (array) {
                if (callback) callback(array);
            }.bind(this)
        };
        if (options.orgOptions) {
            opt = Object.merge(opt, options.orgOptions);
        }
        if (opt.types.length === 0) opt.types = null;
        var selector = new MWF.O2Selector(container, opt);
    },
    replaceText: function (value, selectValue, selectText, separator) {
        if (typeOf(value) === "number") value = [value];
        if (typeOf(selectValue) === "number") selectValue = [selectValue];
        if (typeOf(selectText) === "number") selectText = [selectText];
        var vals = typeOf(value) === "array" ? value : value.split(separator);
        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(separator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(separator);
        for (var i = 0; i < vals.length; i++) {
            for (var j = 0; j < selectValues.length; j++) {
                if (vals[i] === selectValues[j]) {
                    vals[i] = selectTexts[j]
                }
            }
        }
        return vals;
    },
    getEvents: function (events) {
        if (!events || events === "" || events === "$none") return;
        if (typeof events === "string") {
            if (events.indexOf("^^") > -1) {
                var eventsArr = events.split("##");
                if (eventsArr[0].split("^^").length !== 2) return;
                events = {};
                for (var i = 0; i < eventsArr.length; i++) {
                    var ename = eventsArr[i].split("^^")[0];
                    var efunction = eventsArr[i].split("^^")[1];
                    events[ename] = eval("(function(){ return " + efunction + " })()");  //문자열을 객체 또는 function으로 변환, 방법 1
                }
            } else {
                //문자열을 객체 또는 function으로 변환, 방법 2
                eval("var events = " + events);
            }
        }
        return events;
    },
    bindEvent: function (obj, item, events) {
        events = MDomItem.Util.getEvents(events);
        if (typeOf(events) === "object") {

            for (var e in events) {
                //jquery 방식
                //item.bind( e, { fun : events[e] }, function( event ){
                //this는 이벤트를 발생시킨 객체, self는 현재 jDomItem 객체
                //event.data.fun.call( this, _self );
                //})

                //메서드 고정, 파라미터를 this 포인터로 메서드에 전달하므로 메서드 본문에서 this로 파라미터 조회 필요
                //item.addEvent( e, events[e].bind({"item": item, "_self":_self}));

                //파라미터 고정, 메서드를 function에 전달하여 콜백 메서드에서 바로 조회 가능, jquery 방식과 동일
                item.addEvent(e, function (ev) {
                    this.fun.call(ev ? ev.target : null, obj.module || obj, ev);
                }.bind({fun: events[e]}));

                //동작하지 않을 수 있음
                //item.addEvent( e, (function(){
                //	return function(){
                //		events[e].call(item,_self);
                //	}
                //})(e));
            }

            //            for( var e in events ){
            //                if( type && (e==="dblclick" || e==="click") ){
            //                    if( jQuery.inArray( type , this.unsetClassType) === -1 ){
            //                        if( !item.attr("title") || item.attr("title") === "" ){
            //                            item.attr( "title", e==="dblclick" ? "더블클릭하여 선택: "+this.options.text : "클릭하여 선택: "+this.options.text );
            //                        }
            //                        item.removeClass("inputtext").addClass("inputclick");
            //                        break;
            //                    }
            //                }
            //            }
        }
    }
};

MDomItem.Text = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.isEdited) {
            this.loadEdit()
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        var parent = module.container;
        var className = this.getClassName();
        item = new Element("input", {
            "type": "text",
            "name": options.name,
            "value": value
        });

        var tType = this.options.tType;
        if (tType === "time" || tType === "date" || tType.toLowerCase() === "datetime") {
            item.set("autocomplete", "off");
        }

        item.set(options.attr || {});
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});

        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, options.event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    loadRead: function () {
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        var className = this.getClassName();
        var parent = module.container;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        item = new Element("span", {
            "name": options.name,
            "text": value
        });
        item.set(options.attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {	//value와 text 또는 빈 값
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        if (this.options.isEdited) {
            value = item.get("value");
        } else {
            value = item.get("text");
        }
        if (vort === "value") return value;
        if (vort === "text") return value;
        return {
            value: value,
            text: value
        };
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            item.set("value", value);
        } else {
            item.set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            if (typeOf(tType) === "array") {
                if (tType.contains("identity") || tType.contains("person") || tType.contains("unit")) {
                    className = "inputPerson";
                } else {
                    className = "inputText";
                }
            } else {
                if (!tType) {
                    className = "inputText";
                } else if (tType === "number") {
                    className = "inputText";
                } else if (tType === "time" || tType === "date" || tType.toLowerCase() === "datetime") {
                    className = "inputTime";
                } else if (tType === "identity" || tType === "person" || tType.toLowerCase() === "unit") {
                    className = "inputPerson";
                } else {
                    className = "inputText";
                }
            }
        }
        return className;
    },
    bindDefaultEvent: function (item) {
        if (this.options.unsetDefaultEvent) return;
        var tType = this.options.tType;
        var type = "text";
        if (typeOf(tType) === "array" || (tType === "identity" || tType.toLowerCase() === "person" || tType === "unit")) {
            item.addEvent("click", function (ev) {
                this.module.fireEvent("querySelect", this.module);
                var options = this.options;
                var opt = {
                    type: tType,
                    title: options.text,
                    count: options.count,
                    selectedValues: this.get("value").split(","),
                    units: options.units,
                    unitType: options.unitType,
                    groups: options.groups,
                    expand: options.expand
                };
                MDomItem.Util.selectPerson(this.app.content, opt, function (array) {
                    item.empty();
                    this.orgData = this.module.orgData = [];
                    this.orgObject = this.module.orgObject = array;
                    array.each(function (it) {
                        this.orgData.push(it.data.distinguishedName || it.data.name);
                    }.bind(this));
                    item.set("value", this.orgData.join(","));
                    this.items[0].fireEvent("change", [this.module, ev]);
                    if (this.options.validImmediately) this.module.verify(true);
                }.bind(this))
            }.bind(this));
        } else {
            if (tType === "number") {
                item.addEvent("keyup", function () {
                    this.value = this.value.replace(/[^\d.]/g, '');
                });
                if (this.options.validImmediately) {
                    item.addEvent("blur", function () {
                        this.module.verify(true);
                    }.bind(this))
                }
            } else if (tType === "time" || tType.toLowerCase() === "datetime" || tType === "date") {
                item.addEvent("click", function (ev) {
                    this.module.fireEvent("querySelect", this.module);
                    if (this.calendarSelector) {
                        this.calendarSelector.show();
                    } else {
                        this.calendarSelector = MDomItem.Util.selectCalendar(item, this.app.content, {
                            calendarOptions: this.options.calendarOptions,
                            type: tType
                        }, function (dateString, date) {
                            this.items[0].fireEvent("change", [this.module, ev]);
                            if (this.options.validImmediately) this.module.verify(true);
                        }.bind(this))
                    }
                }.bind(this));
            } else {
                if (this.options.validImmediately) {
                    item.addEvent("blur", function () {
                        this.module.verify(true);
                    }.bind(this))
                }
            }
        }
    }
});

MDomItem.Textarea = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.isEdited) {
            this.loadEdit()
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        var parent = module.container;
        var className = this.getClassName();
        item = this.createInput();
        item.set({
            "name": options.name,
            "value": value
        });
        item.set(options.attr || {});
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});
        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, options.event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    createInput: function () {
        return new Element("textarea");
    },
    loadRead: function () {
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        var className = this.getClassName();
        var parent = module.container;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        item = new Element("span", {
            "name": options.name,
            "text": value
        });
        item.set(options.attr || {});
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    bindDefaultEvent: function (item) {
        if (this.options.unsetDefaultEvent) return;
        if (this.options.validImmediately) {
            item.addEvent("blur", function () {
                this.module.verify(true);
            }.bind(this))
        }
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        if (this.options.isEdited) {
            value = item.get("value");
        } else {
            value = item.get("text");
        }
        if (vort === "value") return value;
        if (vort === "text") return value;
        return {
            value: value,
            text: value
        };
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            item.set("value", value);
        } else {
            item.set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputTextarea"
        }
        return className;
    }
});

MDomItem.Hidden = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        var parent = this.container;
        var item = new Element("input", {
            "type": "hidden",
            "name": this.options.name,
            "value": this.options.value
        });
        item.set(this.options.attr || {});
        //this.bindEvent(item,event,type);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        if (this.options.isEdited) {
            value = item.get("value");
        } else {
            value = item.get("text");
        }
        if (vort === "value") return value;
        if (vort === "text") return value;
        return {
            value: value,
            text: value
        };
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("value", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    }
});

MDomItem.Password = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.isEdited) {
            this.loadEdit()
        } else {
        }
    },
    loadEdit: function () {
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        var parent = module.container;
        var className = this.getClassName();
        item = new Element("input", {
            "type": "password",
            "name": options.name,
            "value": value
        });
        item.set(options.attr || {});
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});
        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, options.event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    loadRead: function () {

    },
    bindDefaultEvent: function (item) {
        if (this.options.unsetDefaultEvent) return;
        if (this.options.validImmediately) {
            item.addEvent("blur", function () {
                this.module.verify(true);
            }.bind(this))
        }
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        if (this.options.isEdited) {
            value = item.get("value");
        } else {
            value = this.options.value || this.options.defaultValue
        }
        if (vort === "value") return value;
        if (vort === "text") return value;
        return {
            value: value,
            text: value
        };
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        if (this.options.isEdited) {
            var item = this.mElement.getElement("[name='" + this.options.name + "']");
            item.set("value", value);
        } else {
            this.options.value = value;
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputPassword"
        }
        return className;
    }
});

MDomItem.Radio = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (!this.options.selectValue && this.options.selectText) this.options.selectValue = this.options.selectText;
        if (!this.options.selectText && this.options.selectValue) this.options.selectText = this.options.selectValue;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var _self = this;
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);
        for (i = 0; i < selectValues.length; i++) {

            item = new Element("div");
            if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
            if (this.options.clazz) item.addClass(this.options.clazz);
            item.setStyles(styles);

            var input = new Element("input", {
                "type": "radio",
                "name": name,
                "value": selectValues[i],
                "checked": selectValues[i] === value
            }).inject(item);
            input.set(attr);

            var textNode = new Element("span", {
                "text": selectTexts[i]
            }).inject(item);

            textNode.addEvent("click", function (ev) {
                if (_self.options.attr && _self.options.attr.disabled) return;
                this.input.checked = !this.input.checked;
                var envents = MDomItem.Util.getEvents(_self.options.event);
                if (typeOf(envents) === "object") {
                    if (envents.change) {
                        envents.change.call(this.input, _self.module, ev);
                    }
                    if (envents.click) {
                        envents.click.call(this.input, _self.module, ev);
                    }
                }
                if (_self.options.validImmediately) {
                    _self.module.verify(true);
                }
            }.bind({input: input}));

            if (this.options.validImmediately) {
                input.addEvent("click", function () {
                    this.module.verify(true)
                }.bind(this));
            }
            MDomItem.Util.bindEvent(this, item, event); //? input or item
            if (parent) item.inject(parent);
            this.items.push(item);
        }
    },
    loadRead: function () {
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='" + name + "']");
        if (this.options.isEdited) {
            items.each(function (el) {
                if (el.checked) {
                    value = el.get("value");
                    text = el.getParent().get("text").trim();
                }
            });
        } else {
            text = items[0].get("text");
            if (this.options.selectValue && this.options.selectText) {
                value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator).join();
            } else {
                value = text;
            }
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var items = this.mElement.getElements("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            items.each(function (el) {
                if (el.get("value") === value) el.checked = true;
            });
        } else {
            value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
            value = value.join(",");
            items[0].set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}", this.options.text);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputRadio"
        }
        return className;
    }
});

MDomItem.Checkbox = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (!this.options.selectValue && this.options.selectText) this.options.selectValue = this.options.selectText;
        if (!this.options.selectText && this.options.selectValue) this.options.selectText = this.options.selectValue;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var _self = this;
        var item;
        var values;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var isEdited = this.options.isEdited;
        var parent = this.mElement = this.container;
        var className = this.getClassName();
        values = typeOf(value) === "string" ? value.split(this.valSeparator) : value;
        values = typeOf(value) === "array" ? value : [value];
        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);
        for (var i = 0; i < selectValues.length; i++) {

            item = new Element("div");
            if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
            if (this.options.clazz) item.addClass(this.options.clazz);
            item.setStyles(styles);

            var input = new Element("input", {
                "type": "checkbox",
                "name": name,
                "value": selectValues[i],
                "checked": values.contains(selectValues[i])
            }).inject(item);
            input.set(attr);

            var textNode = new Element("span", {
                "text": selectTexts[i]
            }).inject(item);
            textNode.addEvent("click", function (ev) {
                if (_self.options.attr && _self.options.attr.disabled) return;
                this.input.checked = !this.input.checked;
                var envents = MDomItem.Util.getEvents(_self.options.event);
                if (typeOf(envents) === "object") {
                    if (envents.change) {
                        envents.change.call(this.input, _self.module, ev);
                    }
                    if (envents.click) {
                        envents.click.call(this.input, _self.module, ev);
                    }
                }
                if (_self.options.validImmediately) {
                    _self.module.verify(true);
                }
            }.bind({input: input}));

            if (this.options.validImmediately) {
                item.addEvent("click", function () {
                    this.module.verify(true);
                }.bind(this))
            }
            MDomItem.Util.bindEvent(this, item, event); // ? input or item
            if (parent) item.inject(parent);
            this.items.push(item);
        }
    },
    loadRead: function () {
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='" + name + "']");
        if (this.options.isEdited) {
            value = [];
            text = [];
            items.each(function (el) {
                if (el.checked) {
                    value.push(el.get("value"));
                    text.push(el.getParent().get("text").trim())
                }
            });
        } else {
            text = items[0].get("text");
            if (this.options.selectValue && this.options.selectText) {
                value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator);
            } else {
                value = text;
            }
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var items = this.mElement.getElements("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            var values = typeOf(value) === "array" ? value : value.split("^^");
            items.each(function (el) {
                if (values.contains(el.get("value"))) {
                    el.checked = true;
                } else {
                    el.checked = false;
                }
            });
        } else {
            value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
            value = value.join(",");
            items[0].set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}", this.options.text);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputCheckbox"
        }
        return className;
    }
});

MDomItem.Select = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (!this.options.selectValue && this.options.selectText) this.options.selectValue = this.options.selectText;
        if (!this.options.selectText && this.options.selectValue) this.options.selectText = this.options.selectValue;
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        item = new Element("select", {
            "name": name
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);

        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);

        for (i = 0; i < selectValues.length; i++) {
            new Element("option", {
                "value": selectValues[i],
                "selected": selectValues[i] === value,
                "text": selectTexts[i]
            }).inject(item)
        }

        if (this.options.validImmediately) {
            item.addEvent("change", function () {
                this.module.verify(true);
            }.bind(this))
        }
        MDomItem.Util.bindEvent(this, item, event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    loadRead: function () {
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='" + name + "']");
        if (this.options.isEdited) {
            items[0].getElements("option").each(function (el) {
                if (el.selected) {
                    value = el.get("value");
                    text = el.get("text").trim();
                }
            });
        } else {
            text = items[0].get("text");
            if (this.options.selectValue && this.options.selectText) {
                value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator).join();
            } else {
                value = text;
            }
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var items = this.mElement.getElements("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            items[0].getElements("option").each(function (el) {
                if (el.get("value") === value) el.selected = true;
            });
        } else {
            value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
            value = value.join(",");
            items[0].set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}", this.options.text);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputSelect"
        }
        return className;
    }
});

MDomItem.Multiselect = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (!this.options.selectValue && this.options.selectText) this.options.selectValue = this.options.selectText;
        if (!this.options.selectText && this.options.selectValue) this.options.selectText = this.options.selectValue;
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var item;
        var values;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();

        values = typeOf(value) === "string" ? value.split(this.valSeparator) : value;
        values = typeOf(value) === "array" ? value : [value];

        item = new Element("select", {
            "name": name,
            "multiple": true
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);

        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);
        for (i = 0; i < selectValues.length; i++) {
            new Element("option", {
                "value": selectValues[i],
                "selected": values.contains(selectValues[i]),
                "text": selectTexts[i]
            }).inject(item)
        }
        if (this.options.validImmediately) {
            item.addEvent("change", function () {
                this.module.verify(true);
            }.bind(this))
        }
        MDomItem.Util.bindEvent(this, item, event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    loadRead: function () {
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var items;
        var name = this.options.name;
        items = this.mElement.getElements("[name='" + name + "']");
        if (this.options.isEdited) {
            value = [];
            text = [];
            items[0].getElements("option").each(function (el) {
                if (el.selected) {
                    value.push(el.get("value"));
                    text.push(el.get("text").trim());
                }
            });
        } else {
            text = items[0].get("text");
            if (this.options.selectValue && this.options.selectText) {
                value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator);
            } else {
                value = text;
            }
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var items = this.mElement.getElements("[name='" + this.options.name + "']");
        if (this.options.isEdited) {
            var values = typeOf(value) === "array" ? value : value.split("^^");
            items[0].getElements("option").each(function (el) {
                if (values.contains(el.get("value"))) {
                    el.selected = true;
                } else {
                    el.selected = false;
                }
            })
        } else {
            value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
            value = value.join(",");
            items[0].set("text", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}", this.options.text);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputMultiselect"
        }
        return className;
    }
});

MDomItem.Innertext = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);
        if (this.options.isEdited) {
            MDomItem.Util.bindEvent(this, item, this.options.event);
        }
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        text = item.get("text");
        if (this.options.selectValue && this.options.selectText) {
            value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator);
        } else {
            value = text;
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
        value = value.join(",");
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("text", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
        }
        return className;
    }
});

MDomItem.Innerhtml = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.mElement = this.container;
        var className = this.getClassName();
        if (selectValue && selectText) {
            value = MDomItem.Util.replaceText(value, selectValue, selectText, this.valSeparator);
            value = value.join(",");
        }
        item = new Element("span", {
            "name": name,
            "html": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        if (this.options.clazz) item.addClass(this.options.clazz);
        item.setStyles(styles);
        if (this.options.isEdited) {
            MDomItem.Util.bindEvent(this, item, this.options.event);
        }
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        text = item.get("html");
        if (this.options.selectValue && this.options.selectText) {
            value = MDomItem.Util.replaceText(text, this.options.selectText, this.options.selectValue, this.valSeparator);
        } else {
            value = text;
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        value = MDomItem.Util.replaceText(value, this.options.selectValue, this.options.selectText, this.valSeparator);
        value = value.join(",");
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("html", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
        }
        return className;
    }
});

MDomItem.Img = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.disable) return;

        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();

        item = new Element("img", {
            "name": name,
            "src": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);

        MDomItem.Util.bindEvent(this, item, event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        value = item.get("src");
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("src", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var tType = this.options.tType;
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
        }
        return className;
    }
});

MDomItem.Button = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.disable) return;

        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();

        item = this.createInput();
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);
        MDomItem.Util.bindEvent(this, item, event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    createInput: function () {
        var options = this.options;
        return new Element("button", {
            "type": "button",
            "name": options.name,
            "value": options.value,
            "text": options.value
        });
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        value = item.get("value");
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("value", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputButton"
        }
        return className;
    }
});

MDomItem.A = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.items = module.items;
        this.container = this.mElement = module.container;
    },
    load: function () {
        if (this.options.disable) return;

        var item;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var event = this.options.event;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();

        item = new Element("a", {
            "name": name,
            "value": value,
            "text": value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);
        MDomItem.Util.bindEvent(this, item, event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        value = item.get("value");
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set("value", value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputA"
        }
        return className;
    }
});

MDomItem.MSelector = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        MWF.xDesktop.requireApp("Template", "MSelector", null, false);

        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;

        this.mSelectorOptions = {
            "style": "default",
            "width": "230px",
            "height": "30px",
            "defaultOptionLp": MWF.xApplication.Template.LP.MDomItem.defaultOption,
            "trigger": "delay", //immediately
            "isSetSelectedValue": true,
            "inputEnable": false,
            "isCreateReadNode": false, //MDomItem 방식에 맞춤

            "textField": "",
            "valueField": "",

            "value": value,
            "text": "",
            "defaultVaue": this.options.defaultValue,
            "selectValue": selectValue,
            "selectText": selectText,

            "isEdited": this.options.isEdited

            //"onSelectItem" : function( itemNode, itemData ){}.bind(this)
            //"onLoadData" :function( callback ){}.bind(this)
        };
        if (this.options.mSelectorOptions) {
            this.mSelectorOptions = Object.merge(this.mSelectorOptions, this.options.mSelectorOptions);
        }
        this.mSelectorOptions.value = value;

        if (!this.options.isEdited) {
            var name = this.options.name;
            var item;
            var attr = this.options.attr || {};
            var className = this.getClassName();
            var styles = this.options.style || {};
            var parent = this.container;
            this.mSelectorOptions.onLoadReadNode = function (text) {
                if (this.items.length > 0) {
                    parent.empty();
                }
                item = new Element("span", {
                    "name": name,
                    "text": text
                });
                item.set(attr);
                if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
                item.setStyles(styles);
                if (this.options.clazz) item.addClass(this.options.clazz);

                if (parent) item.inject(parent);
                this.items.push(item);
            }.bind(this);
        }

        this.mSelector = new MSelector(this.container, this.mSelectorOptions, this.app, this.css);
        this.mSelector.load();
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        if (vort === "value") return this.mSelector.getValue();
        if (vort === "text") return this.mSelector.getText();
        return this.mSelector.get();
    },
    setValue: function (value) {
        this.mSelector.setValue(value);
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
        }
        return className;
    }
});

MDomItem.ImageClipper = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var item;
        var values;
        var name = this.options.name;
        var value;
        if (typeOf(this.options.value) === "boolean") {
            value = this.options.value.toString();
        } else {
            value = this.options.value || this.options.defaultValue
        }
        var styles = this.options.style || {};
        var parent = this.container;
        this.imageId = this.module.imageId = value;
        if (value && parent) {
            if (styles.imageWrapStyle) {
                this.imageWrap = new Element("div", {styles: styles.imageWrapStyle}).inject(parent)
            }
            this.image = new Element("img", {
                "src": MWF.xDesktop.getImageSrc(value)
            }).inject(this.imageWrap || parent);
            this.image.addEvent("click", function () {
                window.open(o2.filterUrl(MWF.xDesktop.getImageSrc(this.imageId)), "_blank");
            }.bind(this));
            if (styles.imageStyle) this.image.setStyles(styles.imageStyle);
        }
        var action = new Element("button", {
            "text": MWF.xApplication.Template.LP.MDomItem.setPicture
        }).inject(parent);
        //if( this.css && this.css["inputButton"] )action.setStyles( this.css["inputButton"] );
        if (styles.actionStyle) action.setStyles(styles.actionStyle);
        action.addEvents({
            "click": function () {
                MWF.xDesktop.requireApp("Template", "widget.ImageClipper", null, false);
                this.clipper = new MWF.xApplication.Template.widget.ImageClipper(this.app, {
                    "imageUrl": value ? MWF.xDesktop.getImageSrc(value) : "",
                    "aspectRatio": this.options.aspectRatio || 0,
                    "ratioAdjustedEnable": this.options.ratioAdjustedEnable || false,
                    "reference": this.options.reference,
                    "referenceType": this.options.referenceType,
                    "onChange": function () {
                        if (this.image) this.image.destroy();
                        if (this.imageWrap) this.imageWrap.destroy();
                        if (styles.imageWrapStyle) {
                            this.imageWrap = new Element("div", {styles: styles.imageWrapStyle}).inject(parent, "top")
                        }
                        this.image = new Element("img", {
                            "src": this.clipper.imageSrc
                        }).inject(this.imageWrap || parent, "top");
                        if (styles.imageStyle) this.image.setStyles(styles.imageStyle);
                        this.image.addEvent("click", function () {
                            window.open(o2.filterUrl(MWF.xDesktop.getImageSrc(this.imageId)), "_blank");
                        }.bind(this));
                        this.imageId = this.module.imageId = this.clipper.imageId;
                        if (this.options.validImmediately) {
                            this.module.verify(true)
                        }
                    }.bind(this)
                });
                this.clipper.load();
            }.bind(this)
        });
    },
    loadRead: function () {
        var value = this.options.value || this.options.defaultValue;
        var parent = this.container;
        this.imageId = this.module.imageId = value;
        if (value && parent) {
            this.image = new Element("img", {
                "src": MWF.xDesktop.getImageSrc(value)
            }).inject(parent);
            var styles = this.options.style || {};
            if (styles.imageStyle) this.image.setStyles(styles.imageStyle);
        }
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var items;
        var value = this.imageId;
        if (vort === "value") return value;
        if (vort === "text") return value;
        var result = {};
        result.value = value;
        result.text = value;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var styles = this.options.style || {};
        this.imageId = this.module.imageId = value;
        if (value) {
            if (this.image) {
                this.image.set("src", MWF.xDesktop.getImageSrc(value))
            } else {
                if (styles.imageWrapStyle) {
                    this.imageWrap = new Element("div", {styles: styles.imageWrapStyle}).inject(this.container)
                }
                this.image = new Element("img", {
                    "src": MWF.xDesktop.getImageSrc(value)
                }).inject(this.imageWrap || this.container);
                if (styles.imageStyle) this.image.setStyles(styles.imageStyle);
            }
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.uploadPictureNotice + "：" + this.options.text;
    }
});

MDomItem.Rtf = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var _self = this;
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var attr = this.options.attr || {};
        var parent = this.container;
        window.COMMON.AjaxModule.load("ckeditor", function () {
            CKEDITOR.disableAutoInline = true;
            var item = new Element("div", {
                "name": name,
                "id": name
            });
            item.set(attr);
            if (parent) item.inject(parent);

            if (value) item.set("html", this.parseHtml(value));

            var editorConfig = {
                //"autoGrow_maxHeight": 400,
                //"autoGrow_minHeight": 300,
                "resize_enabled": true,
                //"resize_maxHeight": "3000",
                //"resize_minHeight": "200",
                "autoParagraph": true,
                "autoUpdateElement": true,
                "enterMode": 1,
                //"height": "200",
                //"width": "",
                "readOnly": false,
                "language": MWF.language || "zh-cn",
                "enablePreview": true,
                "removePlugins": ['image', 'easyimage', 'exportpdf', 'cloudservices'],
                "extraPlugins": ['lineheight', 'o2image', 'o2uploadimage', 'o2uploadremoteimage']
                // "extraAllowedContent " : "img[onerror,data-id]"
            };
            if (this.options.RTFConfig) {
                editorConfig = Object.merge(editorConfig, this.options.RTFConfig)
            }
            if (editorConfig.skin) editorConfig.skin = "moono-lisa";
            if (!editorConfig.filebrowserFilesImage && !editorConfig.cloudFileDisable) {
                editorConfig.filebrowserFilesImage = function (e, callback) {
                    MWF.xDesktop.requireApp("File", "FileSelector", function () {
                        _self.selector_cloud = new MWF.xApplication.File.FileSelector(document.body, {
                            "style": "default",
                            "title": MWF.xApplication.Template.LP.MDomItem.selectCoundPicture,
                            "toBase64": true,
                            "listStyle": "preview",
                            "selectType": "images",
                            "onPostSelectAttachment": function (url, base64File) {
                                if (callback) callback(url, base64File);
                            }
                        });
                        _self.selector_cloud.load();
                    }, true);
                }
            }
            this.editor = this.module.editor = CKEDITOR.replace(item, editorConfig);

            var imgSrc = MWF.xDesktop.getImageSrc();
            var imgHost = imgSrc.split("/x_file_assemble_control/")[0];
            this.editor.on("instanceReady", function (e) {
                var editable = e.editor.editable && e.editor.editable();
                if (!editable) return;
                var imgs = editable.find("img");
                for (var i = 0; i < imgs.count(); i++) {
                    var img = imgs.getItem(i);
                    var src = img.getAttribute("src");
                    if (src && src.indexOf("/x_file_assemble_control/") > -1) {
                        if (imgHost !== src.split("/x_file_assemble_control/")[0]) {
                            var id = img.getAttribute("data-id");
                            if (id) {
                                var newSrc = MWF.xDesktop.getImageSrc(id);
                                if (newSrc) {
                                    img.setAttribute("src", newSrc);
                                    img.setAttribute("data-cke-saved-src", newSrc);
                                }
                            }
                        }
                    }
                }

                var htmlFilter = e.editor.dataProcessor.htmlFilter;

                htmlFilter.addRules({
                    a: function(element) {
                        var href = element.attributes.href;
                        if (href) {
                            // 정규식 매칭: javascript:로 시작(대소문자 구분 없음)
                            var jsProtocolReg = /^javascript:/i;
                            if (jsProtocolReg.test(href)) {
                                delete element.attributes.href; // 위험한 href 속성 제거
                                // 더 엄격하게 하려면 <a> 태그를 바로 제거: element.remove();
                            }
                        }
                    }
                });

                // 추가 처리: 소스 모드에서의 내용 필터링(선택, 방어 강화)
                e.editor.on('beforeSetData', function(evt) {
                    evt.data.dataValue = evt.data.dataValue.replace(
                        /<a\s+[^>]*href\s*=\s*(["']?)javascript:.*?\1[^>]*>/gi,
                        function(match) {
                            // href 속성 제거 또는 태그 내용 수정
                            return match.replace(/href\s*=\s*(["']?)javascript:.*?\1/gi, '');
                        }
                    );
                });
            });
            this.items.push(this.editor);
        }.bind(this));
    },
    loadRead: function () {
        var _self = this;
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = null;
        item = new Element("span", {
            "name": name
            // "html" : value
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);

        if (parent) item.inject(parent);

        this.loadLazyImage(item, value, function () {
            if (window.layout && layout.mobile) {
            } else if (this.options.enablePreview) {
                this.loadImageViewer(item);
            } else if (this.options.RTFConfig && this.options.RTFConfig.enablePreview === false) {
            } else {
                this.loadImageViewer(item);
            }
        }.bind(this));

        this.items.push(item);
    },
    getAttrRegExp: function (attr) {
        return "\\s+" + attr + "\\s*=\\s*[\"|\'](.*?)[\"|\']";
    },
    getAttributeValue: function (str, attribute) {
        var regexp = new RegExp(this.getAttrRegExp(attribute), "i");
        var array = str.match(regexp);
        return (o2.typeOf(array) === "array" && array.length === 2) ? array[1] : "";
    },
    addAttribute: function (str, attribute, value) {
        var regexp = new RegExp("\\/*\\s*>", "i");
        return str.replace(regexp, ' ' + attribute + '="' + value + '"' + " />");
    },
    removeAttribute: function (str, attribute) {
        var regexp = new RegExp(this.getAttrRegExp(attribute), "ig");
        return str.replace(regexp, "");
    },
    parseHtml: function (html) {
        html = this.replaceHrefJavascriptStr(html);
        html = this.replaceIframeJavascriptStr(html);
        html = this.replaceOnAttribute(html);
        html = this.parseOnerror(html);
        return html;
    },
    parseOnerror: function (html) {
        var regexp_all = /(i?)(<img)([^>]+>)/gmi;
        var images = html.match(regexp_all);
        if (images) {
            if (images.length) {
                for (var i = 0; i < images.length; i++) {
                    var image = images[i];

                    var image1 = this.removeAttribute(image, "onerror");
                    image1 = this.addAttribute(image1, "onerror", "MWF.xDesktop.setImageSrc()");

                    html = html.replace(image, image1);
                }
            }
        }
        return html;
    },
    replaceHrefJavascriptStr: function (html) {
        var regexp_a_all = /(i?)(<a)([^>]+>)/gmi;
        var as = html.match(regexp_a_all);
        if (as) {
            if (as.length) {
                for (var i = 0; i < as.length; i++) {
                    var a = as[i];
                    var href = this.getAttributeValue(a, "href");
                    if (href.toLowerCase().indexOf('javascript:') > -1) {
                        var a1 = this.removeAttribute(a, "href");
                        html = html.replace(a, a1);
                    }
                }
            }
        }
        return html;
    },
    replaceIframeJavascriptStr: function (html) {
        var regexp_a_all = /(i?)(<iframe)([^>]+>)/gmi;
        var as = html.match(regexp_a_all);
        if (as) {
            if (as.length) {
                for (var i = 0; i < as.length; i++) {
                    var a = as[i];
                    var src = this.getAttributeValue(a, "src");
                    if (src.toLowerCase().indexOf('javascript:') > -1 || src.toLowerCase().startsWith('data:')) {
                        var a1 = this.removeAttribute(a, "src");
                        html = html.replace(a, a1);
                    }
                }
            }
        }
        return html;
    },
    replaceOnAttribute: function (htmlString) {

        var tempDiv = document.createElement('div');

        tempDiv.innerHTML = htmlString;

        var elements = tempDiv.getElementsByTagName('*');

        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];

            var attributeNames = element.getAttributeNames();

            for (var j = 0; j < attributeNames.length; j++) {
                var attributeName = attributeNames[j];
                if (attributeName.substr(0, 2).toLowerCase() === 'on') {
                    element.removeAttribute(attributeName);
                }
            }
        }
        return tempDiv.innerHTML;
    },
    loadLazyImage: function (node, html, callback) {
        if (this.options && this.options.imageLazyLoading) {
            o2.require("o2.widget.ImageLazyLoader", null, false);
            var loadder = new o2.widget.ImageLazyLoader(node, html);
            loadder.load(function () {
                if (callback) callback();
            }.bind(this));
        } else {
            node.set("html", this.parseHtml(html));
            if (callback) callback();
        }
    },
    loadImageViewer: function (node) {
        o2.require("o2.widget.ImageViewer", null, false);
        var imageViewer = new o2.widget.ImageViewer(node);
        imageViewer.load();
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value;
        var text;
        if (this.options.isEdited) {
            if (this.options.RTFConfig && this.options.RTFConfig.isSetImageMaxWidth) {
                var div = new Element("div", {
                    "styles": {"display": "none"},
                    "html": this.editor.getData()
                }).inject(this.container);
                div.getElements("img").each(function (el) {
                    el.setStyle("max-width", "100%");
                });
                value = div.get("html");
                div.destroy();
            } else {
                value = this.editor.getData();
            }
        } else {
            var item = this.mElement.getElement("[name='" + name + "']");
            value = item.get("html");
        }
        if (!value) value = "";
        if (!text) text = value;
        if (vort === "value") return value;
        if (vort === "text") return text;
        var result = {};
        result.value = value;
        result.text = text;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        if (this.options.isEdited) {
            this.editor.setData(value);
        } else {
            var item = this.mElement.getElement("[name='" + this.options.name + "']");
            item.set("html", value);
        }
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", this.options.text || this.options.label);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
        }
        return className;
    }
});

MDomItem.Org = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        if (!value) {
            this.orgData = [];
        } else if (typeOf(value) === "array") {
            this.orgData = value;
        } else if (typeOf(value) === "string") {
            this.orgData = value.split(this.valSeparator)
        } else if (typeOf(value) === "object") {
            this.orgData = [value]
        } else {
            this.orgData = [];
        }
        item = new Element("div", {
            "name": name
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);
        this.loadOrgWidget(this.orgData, item, true);
        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, this.options.event);
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    loadRead: function () {
        var item;
        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var styles = this.options.style || {};
        var attr = this.options.attr || {};
        var parent = this.container;
        var className = this.getClassName();
        if (!value) {
            this.orgData = [];
        } else if (typeOf(value) === "array") {
            this.orgData = value;
        } else if (typeOf(value) === "string") {
            this.orgData = value.split(this.valSeparator)
        } else {
            this.orgData = [];
        }
        this.module.orgData = this.orgData;
        item = new Element("div", {
            "name": name
        });
        item.set(attr);
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);
        this.loadOrgWidget(this.orgData, item, false);

        if (parent) item.inject(parent);
        this.items.push(item);
    },
    reset: function () {
        this.setValue(this.options.defaultValue || []);
    },
    getData: function (parse) {
        var data = [];
        this.OrgWidgetList.each(function (widget) {
            data.push(parse ? MWF.org.parseOrgData(widget.data, true) : widget.data);
        }.bind(this));
        return data;
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var value = this.orgData;
        if (vort === "value") return value;
        if (vort === "text") return value;
        var result = {};
        result.value = value;
        result.text = value;
        return result;
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        if (!value) {
            this.orgData = [];
        } else if (typeOf(value) === "array") {
            this.orgData = value;
        } else if (typeOf(value) === "string") {
            this.orgData = value.split(this.valSeparator);
        } else {
            this.orgData = [];
        }
        this.orgObjData = null;
        item.empty();
        this.loadOrgWidget(this.orgData, item, this.options.isEdited);
        this.module.orgData = this.orgData
    },
    getErrorText: function () {
        return MWF.xApplication.Template.LP.MDomItem.selectTip.replace("{text}", this.options.text);
    },
    bindDefaultEvent: function (item) {
        if (this.options.unsetDefaultEvent) return;
        item.addEvent("click", function (ev) {
            this.module.fireEvent("querySelect", this.module);
            var options = this.options;
            var opt = {
                type: options.orgType,
                title: options.text,
                count: options.count,
                selectedValues: this.orgObjData || this.orgData,
                units: options.units,
                unitType: options.unitType,
                groups: options.groups,
                expand: options.expand,
                exclude: options.exclude,
                expandSubEnable: options.expandSubEnable
            };
            if (this.options.orgOptions) {
                opt.orgOptions = this.options.orgOptions;
            }
            MDomItem.Util.selectPerson(this.app.content, opt, function (array) {
                item.empty();
                this.orgData = this.module.orgData = [];
                this.orgObjData = [];
                this.orgObject = this.module.orgObject = array;
                array.each(function (it) {
                    this.orgData.push(it.data.distinguishedName || it.data.name);
                    this.orgObjData.push(it.data);
                }.bind(this));
                this.OrgWidgetList = [];
                if (item.tagName.toLowerCase() === 'oo-selector') {
                    item.value = this.orgData;
                } else {
                    this.loadOrgWidget(this.orgObjData, item, true);
                }
                this.modified = true;
                this.items[0].fireEvent("change", [this.module, ev]);
                if (this.options.validImmediately) this.module.verify(true);
            }.bind(this))
        }.bind(this));
    },
    getValueByType: function (type) {
        var types = typeOf(type) === "string" ? type.split(",") : type;
        types = types.map(function (item, index) {
            switch (item.toLowerCase()) {
                case "person":
                    return "p";
                case "identity":
                    return "i";
                case "unit":
                    return "u";
                case "group":
                    return "g";
                case "role":
                    return "r";
                default:
                    return item.toLowerCase();
            }
        });
        var value = [];
        this.get("value").each(function (v) {
            var flag = v.substr(v.length - 1, 1);
            if (types.contains(flag.toLowerCase())) value.push(v);
        });
        return value;
    },
    loadOrgWidget: function (value, node, canRemove) {
        this.OrgWidgetList = this.OrgWidgetList || [];
        MWF.require("MWF.widget.O2Identity", null, false);
        var options = {
            "style": this.options.orgStyle || "xform",
            "canRemove": canRemove,
            "onRemove": this.removeOrgItem,
            "lazy": true
        };
        if (this.options.orgWidgetOptions) {
            options = Object.merge(options, this.options.orgWidgetOptions);
        }
        value.each(function (v) {
            var data;
            var distinguishedName;
            if (typeOf(v) === "string") {
                distinguishedName = v;
                if (distinguishedName.indexOf("@") > 0) {
                    data = {
                        "distinguishedName": distinguishedName,
                        "name": distinguishedName.split("@")[0]
                    }
                } else {
                    data = {
                        "id": distinguishedName,
                        "name": distinguishedName.split("@")[0]
                    }
                }
            } else {
                distinguishedName = v.distinguishedName || v.name || "";
                if (!v.name) v.name = distinguishedName.split("@")[0];
                data = v;
            }
            var flag = distinguishedName.substr(distinguishedName.length - 1, 1);
            switch (flag.toLowerCase()) {
                case "i":
                    widget = new MWF.widget.O2Identity(data, node, options); break;
                case "p":
                    widget = new MWF.widget.O2Person(data, node, options); break;
                case "u":
                    widget = new MWF.widget.O2Unit(data, node, options); break;
                case "g":
                    widget = new MWF.widget.O2Group(data, node, options); break;
                //case "d":
                //    var widget = new MWF.widget.O2Duty(data, node, options);
                //    break;
                default:
                    var orgType = this.options.orgType;
                    var t = (typeOf(orgType) === "array" && orgType.length === 1) ? orgType[0] : orgType;
                    switch (typeOf(t) === "string" ? t.toLowerCase() : "") {
                        case "identity":
                            widget = new MWF.widget.O2Identity(data, node, options); break;
                        case "person":
                            widget = new MWF.widget.O2Person(data, node, options); break;
                        case "unit":
                            widget = new MWF.widget.O2Unit(data, node, options); break;
                        case "group":
                            widget = new MWF.widget.O2Group(data, node, options); break;
                        case "process":
                            if (data.id === data.name) delete data.name;
                            widget = new MWF.widget.O2Process(data, node, options);
                            break;
                        case "CMSView":
                            widget = new MWF.widget.O2CMSView(data, node, options); break;
                        default:
                            widget = new MWF.widget.O2Other(data, node, options); break;
                    }
            }
            widget.field = this;
            this.OrgWidgetList.push(widget);
        }.bind(this));
    },
    removeOrgItem: function (widget, ev) {
        //this는 MWF.widget.O2Identity 같은 객체
        var _self = this.field; //이것이 MDomItem 객체
        var dn = widget.data.distinguishedName || widget.data.name;
        var data = [];
        var index;
        _self.orgData.each(function (d, i) {
            if (d !== dn) data.push(d)
        });
        _self.orgData = data;

        if (_self.orgObject) {
            data = [];
            _self.orgObject.each(function (d) {
                if (d.data) {
                    if (d.data.distinguishedName) {
                        if (d.data.distinguishedName !== dn) data.push(d);
                    } else {
                        if (d.data.name !== dn) data.push(d);
                    }
                }
            });
            _self.orgObject = data;
        }

        if (_self.orgObjData) {
            data = [];
            _self.orgObjData.each(function (d) {
                if (d.distinguishedName) {
                    if (d.distinguishedName !== dn) data.push(d);
                } else {
                    if (d.name !== dn) data.push(d);
                }
            });
            _self.orgObjData = data;
        }

        this.node.destroy();
        _self.items[0].fireEvent("change");
        ev.stopPropagation();
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className
        } else if (!this.options.isEdited) {
        } else {
            className = "inputPerson"
        }
        return className;
    }
});

MDomItem.File = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container;
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        if (this.options.isEdited) {
            this.loadEdit();
        } else {
            this.loadRead();
        }
    },
    loadEdit: function () {

    },
    loadRead: function () {

    },
    get: function (vort) {

    },
    setValue: function (value) {

    },
    getErrorText: function () {

    }
});

MDomItem.OOInput = new Class({
    initialize: function (module) {
        this.module = module;
        this.options = module.options;
        this.css = module.css;
        this.app = module.app;
        this.items = module.items;
        this.container = this.mElement = module.container
        this.valSeparator = module.valSeparator;
    },
    load: function () {
        if (this.options.disable) return;
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        var parent = module.container;
        var className = this.getClassName();
        item = this.createInput();
        item.set({
            "name": options.name,
            "value": value
        });

        if (options.label || options.text) {
            item.setAttribute('label', this.module.fitLabel(options.label || options.text));
        }

        item.setAttribute('readonly', false);
        item.setAttribute('readmode', false);
        item.setAttribute('disabled', false);

        if (options.isEdited) {
            if (options.attr?.readonly === true) options.showMode = 'readonlyMode';
            switch (options.showMode) {
                case 'readonlyMode':
                    item.setAttribute('readonly', true);
                    break;
                case 'disabled':
                    item.setAttribute('disabled', true);
                    break;
                case 'read':
                    item.setAttribute('readmode', true);
                    break;
            }
        } else {
            item.setAttribute('readmode', true);
        }
        if (options.required || options.notEmpty) {
            item.setAttribute("required", true);
        } else {
            item.removeAttribute("required");
        }

        if (options.dataType || options.tType) {
            item.setAttribute("type", options.dataType || options.tType);
        }

        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.set(options.attr || {});
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});
        if (parent) item.inject(parent);
        this.items.push(item);
        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, options.event);
    },
    createInput: function () {
        var input = new Element("oo-input");
        if (this.options.showIcon !== 'no') {
            input.setAttribute('right-icon', 'edit');
        }
        if (this.css?.OOInput) input.setStyles(this.css.OOInput);
        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOInputProperties3' : 'OOInputProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);
        return input;
    },
    bindDefaultEvent: function (item) {
        if (this.options.unsetDefaultEvent) return;
        if (this.options.validImmediately) {
            item.addEvent("blur", function () {
                this.module.verify(true);
            }.bind(this))
        }
        item.addEventListener('validity', (e) => {
            if (this.validationText) {
                e.target.setCustomValidity(this.validationText);
            }
        });
        item.addEventListener('invalid', (e)=>{
            if (item._props.validity){
                e.target.setCustomValidity(item._props.validity);
            }else{
                const o = {
                    valueMissing: this.getErrorText()
                };
                //e.detail로 유효성 상태 객체 ValidityState 조회
                for (const k in o){
                    if (e.detail[k]){
                        if (o[k]){
                            break;
                        }
                    }
                }
            }
        });
    },
    get: function (vort) {
        if (this.options.disable) {
            return (vort === "value" || vort === "text") ? null : {
                value: null,
                text: null
            };
        }
        var name = this.options.name;
        var item = this.mElement.getElement("[name='" + name + "']");
        var value = item.get("value");
        if (vort === "value") return value;
        if (vort === "text") return value;
        return {
            value: value,
            text: value
        };
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        item.set('value', value || '');
    },
    getErrorText: function () {
        var label = this.options.label || this.options.text;
        label = !!label ? `“${label.replace(/　/g, '')}”` :  '이 필드';
        return MWF.xApplication.Template.LP.MDomItem.emptyTip.replace("{text}", label);
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className;
        } else {
            className = "";
        }
        return className;
    }
})

MDomItem.OOTextarea = new Class({
    Extends: MDomItem.OOInput,
    createInput: function () {
        var input = new Element("oo-textarea");
        if (this.css?.OOTextarea) input.setStyles(this.css.OOTextarea);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOTextareaProperties3' : 'OOTextareaProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    }
});

MDomItem.OODatetime = new Class({
    Extends: MDomItem.OOInput,
    createInput: function () {
        var input = new Element("oo-datetime");

        var options = this.options;
        if (options.mode) {
            input.setAttribute('mode', options.mode);
        }
        if(options.view){
            input.setAttribute('view', options.view);
        }
        if (options.secondEnable === "yes") {
            input.setAttribute("second-enable", true);
        } else {
            input.setAttribute("second-enable", false);
        }

        input.setAttribute("week-begin", options.weekBegin || 1);

        if (options.format) input.setAttribute("format", options.format);

        if (this.css?.OODatetime) input.setStyles(this.css.OODatetime);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OODatetimeProperties3' : 'OODatetimeProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    }
});

MDomItem.OOSelector = new Class({
    Extends: MDomItem.Org,
    load: function () {
        if (this.options.disable) return;
        var item;
        var options = this.options;

        if (!options.orgOptions) options.orgOptions = {};
        options.orgOptions.style = 'v10';
        options.orgOptions.tabStyle = 'v10';

        var name = this.options.name;
        var value = this.options.value || this.options.defaultValue;
        var styles = this.options.style || {};
        var parent = this.container;
        var className = this.getClassName();
        console.log(value);
        if (!value) {
            this.orgData = [];
        } else {
            switch (typeOf(value)) {
                case 'array':
                    this.orgData = value;
                    break;
                case 'string':
                    this.orgData = value.split(this.valSeparator);
                    break;
                case 'object':
                    this.orgData = [value];
                    break;
                default:
                    this.orgData = [];
            }
        }
        item = new Element("oo-selector");
        item.set({
            "name": options.name,
            "value": value
        });
        if (this.css?.OOOrg) item.setStyles(this.css.OOOrg);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOOrgProperties3' : 'OOOrgProperties';
        if (this.css[propertyName]) item.set(this.css[propertyName]);

        item.set(options.attr || {});

        if (options.label || options.text) {
            item.setAttribute('label', this.module.fitLabel(options.label || options.text));
        }

        if (options.showIcon !== 'no') item.setAttribute('right-icon', 'person');

        item.setAttribute('readonly', false);
        item.setAttribute('readmode', false);
        item.setAttribute('disabled', false);

        if (options.isEdited) {
            if (options.attr?.readonly === true) options.showMode = readonlyMode;
            switch (options.showMode) {
                case 'readonlyMode':
                    item.setAttribute('readonly', true);
                    break;
                case 'disabled':
                    item.setAttribute('disabled', true);
                    break;
                case 'read':
                    item.setAttribute('readmode', true);
                    break;
                default:
                    this.bindDefaultEvent(item);
                    MDomItem.Util.bindEvent(this, item, options.event);
                    break;
            }
        } else {
            item.setAttribute('readmode', true);
        }
        if (options.required || options.notEmpty) {
            item.setAttribute("required", true);
        } else {
            item.removeAttribute("required");
        }
        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.setStyles(styles);
        if (this.options.clazz) item.addClass(this.options.clazz);
        // this.loadOrgWidget( this.orgData, item, true );
        if (parent) item.inject(parent);
        this.items.push(item);
    },
    setValue: function (value) {
        if (this.options.disable) {
            return;
        }
        var item = this.mElement.getElement("[name='" + this.options.name + "']");
        if (!value) {
            this.orgData = [];
        } else {
            switch (typeOf(value)) {
                case 'array':
                    this.orgData = value;
                    break;
                case 'string':
                    this.orgData = value.split(this.valSeparator);
                    break;
                case 'object':
                    this.orgData = [value];
                    break;
                default:
                    this.orgData = [];
            }
        }
        this.orgObjData = null;
        item.value = this.orgData;
        this.module.orgData = this.orgData;
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className;
        } else {
            className = "";
        }
        return className;
    }
});

MDomItem.OORadioGroup = new Class({
    Extends: MDomItem.OOInput,
    load: function () {
        if (this.options.disable) return;
        var module = this.module;
        var options = this.options;
        var item;
        var value;
        if (typeOf(options.value) === "boolean") {
            value = options.value.toString();
        } else {
            value = options.value || options.defaultValue
        }
        var parent = module.container;
        var className = this.getClassName();
        item = this.createInput();
        item.set({
            "name": options.name,
            "value": value
        });

        if (options.label || options.text) {
            item.setAttribute('label', this.module.fitLabel(options.label || options.text));
        }

        if (!options.col || options.col === "0") {
            item.removeAttribute('col');
        } else {
            item.setAttribute('col', options.col);
        }

        if (options.isEdited) {
            if (options.attr?.readonly === true) options.showMode = 'read';
            switch (options.showMode) {
                case 'disabled':
                    item.setAttribute('disabled', true);
                    break;
                case 'read':
                    item.setAttribute('readmode', true);
                    if (options.readModeEvents !== 'yes') {
                        item.setStyle('pointer-events', 'none');
                    }
                    break;
            }
        } else {
            item.setAttribute('readmode', true);
            if (options.readModeEvents !== 'yes') {
                item.setStyle('pointer-events', 'none');
            }
        }
        if (options.required || options.notEmpty) {
            item.setAttribute("required", true);
        } else {
            item.removeAttribute("required");
        }

        if (options.dataType) {
            item.setAttribute("type", options.dataType);
        }

        if (className && this.css && this.css[className]) item.setStyles(this.css[className]);
        item.set(options.attr || {});
        if (options.clazz) item.addClass(options.clazz);
        item.setStyles(options.style || {});

        if (parent) item.inject(parent);
        console.log('load:' + this.options.name);
        this.items.push(item);
        this.bindDefaultEvent(item);
        MDomItem.Util.bindEvent(this, item, options.event);
    },
    createInput: function () {
        var input = new Element('oo-radio-group');
        this.itemTag = 'oo-radio';
        this.input = input;

        this.renderOption();

        if (this.css?.OORadioGroup) input.setStyles(this.css.OORadioGroup);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OORadioGroupProperties3' : 'OORadioGroupProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    },
    renderOption: function () {
        if (this.options.selectOption) {
            this._renderOption();
        } else {
            this._renderOption2();
        }
    },
    _renderOption: function () {
        var input = this.input;
        var options = this.options;
        var valueKey = options.valueKey || 'value';
        var labelKey = options.labelKey || 'label';
        options.selectOption.forEach(option => {
            var optionNode = new Element(this.itemTag, {
                "value": option[valueKey],
                "type": "radio",
                "name": options.name,
                "text": option[labelKey],
                "styles": options.buttonStyles || {}
            });
            if (option.disabled) {
                optionNode.setAttribute('disabled', true);
            }
            optionNode.inject(input);
        });
    },
    _renderOption2: function () {
        var input = this.input;
        var options = this.options;
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);

        for (var i = 0; i < selectValues.length; i++) {
            var optionNode = new Element(this.itemTag, {
                "value": selectValues[i],
                "type": "radio",
                "name": options.name,
                "text": selectTexts[i],
                "styles": options.buttonStyles || {}
            });
            // if (option.disabled) {
            //     optionNode.setAttribute('disabled', true);
            // }
            // optionNode.setAttribute('text', selectTexts[i]);
            optionNode.inject(input);
        }
    },
});

MDomItem.OOCheckGroup = new Class({
    Extends: MDomItem.OORadioGroup,
    createInput: function () {
        var input = new Element('oo-checkbox-group');
        this.itemTag = 'oo-checkbox';
        this.input = input;

        this.renderOption();

        if (this.css?.OOCheckGroup) input.setStyles(this.css.OOCheckGroup);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOCheckGroupProperties3' : 'OOCheckGroupProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    }
});

MDomItem.OOSelect = new Class({
    Extends: MDomItem.OOInput,
    createInput: function () {
        var input = new Element('oo-select');
        this.input = input;
        this.renderOption(input);
        if (this.css?.OOSelect) input.setStyles(this.css.OOSelect);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOSelectProperties3' : 'OOSelectProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    },
    renderOption: function () {
        var input = this.input;
        if (this.options.selectGroup) {
            this._renderGroup(input);
        } else if (this.options.selectOption) {
            this._renderOption(input);
        } else {
            this._renderOption2(input);
        }
    },
    _renderOption: function (input) {
        var options = this.options;
        var valueKey = options.valueKey || 'value';
        var labelKey = options.labelKey || 'label';
        options.selectOption.forEach(option => {
            var optionNode = new Element("oo-option", {
                "value": option[valueKey]
            });
            if (option.disabled) {
                optionNode.setAttribute('disabled', true);
            }
            optionNode.setAttribute('value', option[valueKey]);
            optionNode.setAttribute('text', option[labelKey]);
            optionNode.inject(input);
        });
    },
    _renderOption2: function (input) {
        var selectValue = this.options.selectValue || this.options.selectText;
        var selectText = this.options.selectText || this.options.selectValue;
        var selectValues = typeOf(selectValue) === "array" ? selectValue : selectValue.split(this.valSeparator);
        var selectTexts = typeOf(selectText) === "array" ? selectText : selectText.split(this.valSeparator);

        for (var i = 0; i < selectValues.length; i++) {
            var optionNode = new Element("oo-option", {
                "value": selectValues[i]
            });
            // if( option.disabled ){
            //     optionNode.setAttribute('disabled', true);
            // }
            optionNode.setAttribute('value', selectValues[i]);
            optionNode.setAttribute('text', selectTexts[i]);
            optionNode.inject(input);
        }
    },
    _renderGroup: function (input) {
        var options = this.options;
        var groupLabelKey = options.grouplabelKey || 'label';
        var valueKey = options.valueKey || 'value';
        var labelKey = options.labelKey || 'label';
        var childrenKey = options.childrenKey || 'children';
        options.selectGroup.forEach(group => {
            var groupNode = new Element('oo-option-group').inject(input);
            groupNode.setAttribute('text', group[groupLabelKey]);
            (group[childrenKey] || []).forEach(option => {
                var optionNode = new Element("oo-option", {
                    "value": option[valueKey]
                });
                if (option.disabled) {
                    optionNode.setAttribute('disabled', true);
                }
                optionNode.setAttribute('value', option[valueKey]);
                optionNode.setAttribute('text', option[labelKey]);
                optionNode.inject(groupNode);
            });
        });
    }
});

MDomItem.OOCascade = new Class({
    Extends: MDomItem.OOInput,
    createInput: function () {
        var input = new Element('oo-cascade');
        this.input = input;
        this.renderOption(input);
        if (this.css?.OOCascade) input.setStyles(this.css.OOCascade);

        var propertyName = this.module.getLabelLength(this.options.label || this.options.text) === 3 ? 'OOCascadeProperties3' : 'OOCascadeProperties';
        if (this.css[propertyName]) input.set(this.css[propertyName]);

        return input;
    },
    renderOption: function () {

    }
});

MDomItem.OOButton = new Class({
    Extends: MDomItem.Button,
    createInput: function () {
        var options = this.options;
        var input = new Element("oo-button");
        input.setAttribute('type', options.appearance || "default");
        if (options.text || options.value) input.setAttribute('text', options.text || options.value);
        if (options.leftIcon) input.setAttribute('left-icon', options.leftIcon);
        if (options.rightIcon) input.setAttribute('right-icon', options.rightIcon);
        if (options.disabled) input.setAttribute('disabled', options.disabled);
        if (this.css?.OOButton) input.set(this.css.OOButton);
        if (this.css?.OOButtonProperties) input.set(this.css.OOButtonProperties);
        return input;
    },
    getClassName: function () {
        var className = null;
        if (this.options.className === "none") {
        } else if (this.options.className !== "") {
            className = this.options.className;
        } else {
            className = "";
        }
        return className;
    }
});
