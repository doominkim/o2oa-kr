o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elcascader Element UI 기반 계단식 선택 상자 컴포넌트.
 * @o2cn 계단식 선택 상자
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var input = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var input = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|Element UI Cascader 계단식 선택기}
 */
MWF.xApplication.process.Xform.Elcascader = MWF.APPElcascader =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elcascader# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        /**
         * 컴포넌트 로드 후 발생. 선택 항목 로드가 비동기이면 비동기 처리 완료 후 이 이벤트 발생
         * @event MWF.xApplication.process.Xform.Elcascader#load
         */
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 포커스를 얻을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elcascader#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 포커스를 잃을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elcascader#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 선택 노드가 변경될 때 발생. this.event[0]은 선택 노드의 값
         * @event MWF.xApplication.process.Xform.Elcascader#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 드롭다운이 표시/숨김될 때 발생. this.event[0] 값: 표시되면 true, 숨겨지면 false
         * @event MWF.xApplication.process.Xform.Elcascader#visible-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 다중 선택 모드에서 Tag를 제거할 때 발생. this.event[0]은 제거된 Tag에 해당하는 노드의 값
         * @event MWF.xApplication.process.Xform.Elcascader#remove-tag
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 펼친 노드가 변경될 때 발생. this.event[0]은 각 상위 항목 값으로 구성된 배열
         * @event MWF.xApplication.process.Xform.Elcascader#expand-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        /**
         * 필터 함수 호출 전 훅 함수. this.event[0]은 value 인자: 이 함수의 반환값이 false이거나 거부된 Promise이면 이후 필터링은 실행되지 않음.
         * @event MWF.xApplication.process.Xform.Elcascader#before-filter
         * @see {@link https://element.eleme.cn/#/zh-CN/component/cascader|계단식 선택 상자의 Cascader Events 장}
         */
        "elEvents": ["focus", "blur", "change", "visible-change", "remove-tag", "expand-change", "before-filter"]
    },
    // _loadNode: function(){
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    resetOption: function(){
        this.reload();
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        this._loadOptions();
        Promise.resolve(this.json.options).then(function(options){
            if (data.data){
                var text = this.__getOptionsText(options, data.data);
                contentNode.set("text", text);
            }
        }.bind(this));
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.options) this.json.options = [];
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (this.json.showAllLevels!==false) this.json.showAllLevels = true;
        if (!this.json.separator) this.json.separator = "/";
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.filterable) this.json.filterable = false;
        if (!this.json.collapseTags) this.json.collapseTags = false;
        if (!this.json.props) this.json.props = {};

        if (!this.json.props.expandTrigger) this.json.props.expandTrigger = "click";
        if (!this.json.props.multiple) this.json.props.multiple = false;
        if (this.json.props.emitPath!==false) this.json.props.emitPath = true;
        if (!this.json.props.lazy) this.json.props.lazy = false;
        if (!this.json.props.lazyLoad) this.json.props.lazyLoad = null;
        if (!this.json.props.value) this.json.props.value = "value";
        if (!this.json.props.label) this.json.props.label = "label";
        if (!this.json.props.children) this.json.props.children = "children";
        if (!this.json.props.disabled) this.json.props.disabled = "disabled";
        if (!this.json.props.leaf) this.json.props.leaf = "leaf";

        this._setPopperClass();

        this._loadOptions();

        //if (this.json.props.multiple===true) if (!this.json[this.json.id] || !this.json[this.json.id].length) this.json[this.json.id] = [];
        if (this.json.props.multiple===true) if (!this.json[this.json.$id] || !this.json[this.json.$id].length) this.json[this.json.$id] = [];
    },
    appendVueMethods: function(methods){
        if (this.json.filterMethod && this.json.filterMethod.code){
            var fn = this.form.Macro.exec(this.json.filterMethod.code, this);
            methods.$filterMethod = function(){
                return fn.apply(this, arguments);
            }.bind(this);
        }

        this.loadedOptionsMap = {};
        if (this.json.lazyLoadScript && this.json.lazyLoadScript.code){
            var fn = this.form.Macro.exec(this.json.lazyLoadScript.code, this);
            this.json.props.lazyLoad = function(node, resolve){
                fn.apply(this, [node, function (json){
                    var key = node.path.join(this.json.separator);
                    this.loadedOptionsMap[key] = json;
                    resolve(json);
                }.bind(this)]);
            }.bind(this);
        }

        if (this.json.beforeFilter && this.json.beforeFilter.code){
            var fn = this.form.Macro.exec(this.json.beforeFilter.code, this);
            methods.$beforeFilter = function(){
                fn.apply(this, arguments);
            }.bind(this);
        }
    },

    _setOptionsWithCode: function(code){
        var v = this.form.Macro.exec(code, this);
        if (v.then){
            this.moduleSelectAG = v.then(function(o){
                if (o2.typeOf(o)==="array"){
                    this.json.options = o;
                    this.json.$options = o;
                }
                return this.json.options || [];
            }.bind(this));
        }else if (o2.typeOf(v)==="array"){
            this.json.options = v;
            this.json.$options = v;
        }
    },
    _loadOptions: function(){
        if (this.json.itemsScript && this.json.itemsScript.code)  this._setOptionsWithCode(this.json.itemsScript.code);
    },
    _createElementHtml: function(){

        if (!this.json.options) this.json.options = [];
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (this.json.showAllLevels!==false) this.json.showAllLevels = true;
        if (!this.json.separator) this.json.separator = "/";
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.filterable) this.json.filterable = false;
        if (!this.json.props) this.json.props = {};

        var html = "<el-cascader ";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :clearable=\"clearable\"";
        html += " :size=\"size\"";
        html += " :filterable=\"filterable\"";
        html += " :disabled=\"disabled\"";
        html += " :placeholder=\"description\"";
        html += " :options=\"options\"";
        html += " :collapse-tags=\"collapseTags\"";
        html += " :show-all-levels=\"showAllLevels\"";
        html += " :separator=\"separator\"";
        html += " :popper-class=\"popperClass\"";
        html += " :props=\"props\"";

        if (this.json.filterMethod && this.json.filterMethod.code){
            html += " :filter-method=\"$filterMethod\"";
        }
        if (this.json.beforeFilter && this.json.beforeFilter.code){
            html += " :before-filter=\"$beforeFilter\"";
        }

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        if (this.json.elStyles) html += " :style=\"elStyles\"";
        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-cascader >";
        return html;
    },
    //__setReadonly: function(data){},
    getCheckedNodes: function(leafOnly){
        return (this.vm) ? this.vm.getCheckedNodes(leafOnly) : null;
    },
    __setReadonly: function(data){
        if (this.isReadonly()){
            this._loadOptions();
            this.fireEvent("postLoad");
            Promise.resolve(this.json.options || this.moduleSelectAG).then(function(options){
                if (data){
                    var text = this.__getOptionsText(options, data);
                    this.node.set("text", text);
                    if( this.json.elProperties ){
                        this.node.set(this.json.elProperties );
                    }
                    if (this.json.elStyles){
                        this.node.setStyles( this._parseStyles(this.json.elStyles) );
                    }

                    if( !this.eventLoaded ){
                        this._loadDomEvents();
                        this.eventLoaded = true;
                    }

                    this.fireEvent("load");
                    this.isLoaded = true;
                }
            }.bind(this));
        }
    },
    __getOptionsText: function(options, values, isGetArray){
        if (!!this.json.props.multiple){
            var text = [];
            values.forEach(function(v){
                if( typeOf( v ) === "array" ){
                    text = text.concat(this.__getOptionsTextValue(options, v));
                }else{
                    text = text.concat(this.__getLastOptionsTextValue(options, v));
                }
            }.bind(this));
            return isGetArray ? text : text.join(",");
        }else{
            if( typeOf( values ) === "array" ){
                var arr = this.__getOptionsTextValue(options, values);
                return isGetArray ? arr : arr.join(",");
            }else{
                return this.__getLastOptionsTextValue(options, values)
            }
        }
    },
    __getOptionsTextValue: function(options, values, prefix, prefixLabel){
        var separator = this.json.separator || "/";
        var text = [];
        var v = typeOf( values ) === "string" ? values : values.join(separator);
        options.forEach(function(op){
            var opValue = (prefix) ? prefix + separator + op[this.json.props.value] : op[this.json.props.value];
            var opLabel = (prefixLabel) ? prefixLabel + separator + op[this.json.props.label] : op[this.json.props.label];
            if (opValue == v) {
                text.push(opLabel);
            }else if (v.startsWith(opValue)){
                var children = op[this.json.props.children] || this.loadedOptionsMap[opValue];
                if( children && children.length ){
                    text = text.concat(this.__getOptionsTextValue(children, values, opValue, opLabel));
                }
            }
        }.bind(this));
        if (!this.json.showAllLevels){
            return text.map(function(t){
                return t.substring(t.indexOf(separator)+1, t.length);
            });
        }else{
            return text;
        }
    },
    __getLastOptionsTextValue: function (options, value, key) {
        var text;
        for( var i=0; i<options.length; i++ ){
            var op = options[i];
            var k = (key ? key + '/' : '') + op[this.json.props.value];
            var children = op[this.json.props.children] || this.loadedOptionsMap[k];
            if( children && children.length ){
                text = this.__getLastOptionsTextValue( children, value, k);
                if( text )return text;
            }else{
                var opValue = op[this.json.props.value];
                var opLabel = op[this.json.props.label];
                if( opValue === value ){
                   return opLabel;
                }
            }
        }
        return text;
    },

        getDataByText: function(text){
            this._loadOptions();
            var opt = this.json.options;
            if( !opt )return "";
            if( o2.typeOf(opt.then)==="function" ){
                return Promise.resolve(opt).then(function(options){
                    return this._getDataByText(options, text);
                }.bind(this));
            }else{
                return this._getDataByText(opt, text);
            }
        },
        _getDataByText: function(options, text){
            var values = [];
            if (!!this.json.props.multiple){
                var texts = typeOf( text ) === "array" ? text : [text];
                texts.forEach(function(t){
                    if( typeOf( t ) === "array" && t.length > 1 ){
                        values = values.concat(this._getEachDataByText(options, t));
                    }else{
                        values = values.concat(this._getLastDataByText(options, typeOf( t ) === "array" ? (t[0] || "") : t));
                    }
                }.bind(this));
                return values;
            }else{
                if( typeOf( text ) === "array" && typeOf( text[0] ) === "array" ){
                    text = text[0];
                }
                if( typeOf( text ) === "array" && text.length > 1 ){
                    values = this._getEachDataByText(options, text);
                    return values.length ? values[0] : [];
                }else{
                    return this._getLastDataByText(options, typeOf( text ) === "array" ? (text[0] || "") : text);
                }
            }
        },
        _getEachDataByText: function(options, texts, prefix, prefixLabel){
            var separator = this.json.separator || "/";
            var value = [];
            var t = typeOf( texts ) === "string" ? texts : texts.join(separator);
            options.forEach(function(op){
                var opValue = (prefix) ? prefix + separator + op[this.json.props.value] : op[this.json.props.value];
                var opLabel = (prefixLabel) ? prefixLabel + separator + op[this.json.props.label] : op[this.json.props.label];
                if (opLabel === t) {
                    value.push(opValue.split(separator));
                }else if (t.startsWith(opLabel) && op[this.json.props.children] && op[this.json.props.children].length){
                    value = value.concat(this._getEachDataByText(op[this.json.props.children], texts, opValue, opLabel));
                }
            }.bind(this));
            if (!this.json.showAllLevels){
                return value.map(function(t){
                    return typeOf( t ) === "array" ? t.getLast() : t;
                });
            }else{
                return value;
            }
        },
        _getLastDataByText: function (options, text) {
            var value;
            for( var i=0; i<options.length; i++ ){
                var op = options[i];
                if( op[this.json.props.children] && op[this.json.props.children].length ){
                    value = this._getLastDataByText( op[this.json.props.children], text );
                    if( value )return value;
                }else{
                    var opValue = op[this.json.props.value];
                    var opLabel = op[this.json.props.label];
                    if( opLabel === text ){
                        value = opValue;
                    }
                }
            }
            return value;
        },

        getTextData: function() {
            var text = this.getText();
            var value = this.getData();
            if (typeOf(text) !== 'array') text = [text || ''];
            if (typeOf(value) !== 'array') value = [value || ''];
            return {"value": value, "text": text};
        },

        /**
         * @summary 선택 항목의 text 조회.
         * @return {Array} 선택 항목의 text 배열 반환
         * @example
         * var texts = this.form.get('fieldId').getText(); //선택 항목의 텍스트 배열 조회
         */
        getText: function(){
            return this._getText( true );
        },
        _getText: function( isGetArray ){
            var data = this.json[this.json.$id];
            if( !data )return "";

            var opt = this.json.options;
            if( !opt )return "";
            if( o2.typeOf(opt.then)==="function" ){
                return Promise.resolve(opt).then(function(options){
                    return this.__getOptionsText(options, data, isGetArray);
                }.bind(this));
            }else{
                return this.__getOptionsText(opt, data, isGetArray);
            }
        },

        getExcelData: function( type ){
            var data = this.json[this.json.$id];
            if( !data )return "";
		    if( type === "value" )return typeOf(data) === "array" ? data.join(", ") : (data || "");

            var text = this._getText();
            return typeOf(text) === "array" ? text.join(", ") : (text || "");
        },
        setExcelData: function(d, type){
            var separator = this.json.separator || "/";
            var arr = this.stringToArray(d);
            this.excelData = arr;
            arr = arr.map(function (a) {
                return a.contains(separator) ? a.split(separator) : a;
            });
            if( type === "value" ){
                this.setData(arr);
            }else{
                var data = this.getDataByText( arr );
                this.setData(data);
            }
        }
});
