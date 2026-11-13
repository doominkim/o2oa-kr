o2.xDesktop.requireApp("process.Xform", "$Input", null, false);
/** @classdesc $Selector 컴포넌트 클래스, 모든 선택형 컴포넌트의 부모 클래스
 * @class
 * @hideconstructor
 * @o2category FormComponents
 * @extends MWF.xApplication.process.Xform.$Input
 * @abstract
 */
MWF.xApplication.process.Xform.$Selector = MWF.APP$Selector = new Class(
    /** @lends MWF.xApplication.process.Xform.$Selector# */
{
    Extends: MWF.APP$Input,

    /**
     * 컴포넌트 로드 후 발생. 선택 항목 로드가 비동기이면 비동기 처리 완료 후 이 이벤트 발생
     * @event MWF.xApplication.process.Xform.$Selector#load
     * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
     */

    _showValue: function (node, value) {
        var optionItems = this.getOptions();
        if (optionItems && typeOf(optionItems.then) === "function") {
            optionItems.then(function (opt) {
                this.__showValue(node, value, opt)
            }.bind(this));
        } else {
            this.__showValue(node, value, optionItems)
        }
    },
    /**
     * @summary 선택 항목 새로고침, 선택 항목이 스크립트이면 다시 계산.
     * @example
     * this.form.get('fieldId').resetOption();
     */
    resetOption: function () {
        this.node.empty();
        this.setOptions();
        if( this.moduleSelectAG && this.moduleSelectAG.then ){
            this.moduleSelectAG.then(function(){
                if( this.json.type === 'Select' )this._checkData();
                this.fireEvent("resetOption");
            }.bind(this));
        }else{
            if( this.json.type === 'Select' )this._checkData();
            this.fireEvent("resetOption");
        }
    },
    _checkData: function (){
        var d1 = this.getData(), d2 = this._getBusinessData();
        switch (o2.typeOf(d1)) {
            case "string":
                if( d1 !== d2 )this._setBusinessData(d1);
                break;
            case 'array':
                if (d1.join(",") !== d2.join(",")) {
                    this._setBusinessData(d1);
                }
                break;
        }
    },
    getText: function(){
        return null;
    },
    setBusinessDataById: function(v, id ){
        //xx..0..xx 형태의 id를 가진 필드를 분리
        var evdata = this.form.Macro.environment.data;
        var data = this.form.businessData.data;
        var thisId = id || this.json.id;
        var text = this.getText();

        if(thisId.indexOf("..") < 1){
            data[thisId] = v;
            data[thisId+"$text"] = text;
            this._checkEvdata(evdata, thisId, v);
            this._checkEvdata(evdata, thisId+"$text", text);
        }else{

            var idList = thisId.split("..");
            idList = idList.map( function(d){ return d.test(/^\d+$/) ? d.toInt() : d; });

            //var data = this.form.businessData.data;
            var lastIndex = idList.length - 1;

            for(var i=0; i<=lastIndex; i++){

                var id = idList[i];
                if( !id && id !== 0 )return;

                if( i === lastIndex ){
                    data[id] = v;
                    data[id + "$text"] = text;
                    //evdata.check(id, v);
                    this._checkEvdata(evdata, id, v);
                    this._checkEvdata(evdata, id + "$text", text);
                }else{
                    var nexId = idList[i+1];
                    if(o2.typeOf(nexId) === "number"){ //다음 ID가 숫자
                        if( !data[id] && o2.typeOf(data[id]) !== "array" ){
                            data[id] = [];
                            //evdata.check(id, []);
                            this._checkEvdata(evdata, id, []);
                        }
                        if( nexId > data[id].length ){ //최대 인덱스 초과, 버림
                            return;
                        }
                    }else{ //다음 ID가 문자열
                        if( !data[id] || o2.typeOf(data[id]) !== "object"){
                            data[id] = {};
                            //evdata.check(id, {});
                            this._checkEvdata(evdata, id, {});
                        }
                    }
                    data = data[id];
                    evdata = evdata[id];
                }
            }
        }
        return evdata;
    },
    /**
     * @summary 선택 항목 가져오기.
     * @return {Array | Promise} 선택 항목 배열 또는 Promise 반환, 예: <pre><code class='language-js'>[
     *  "여|female",
     *  "남|male"
     * ]</code></pre>
     * @example
     * this.form.get('fieldId').getOptions();
     * @example
     * //비동기
     * var opt = this.form.get('fieldId').getOptions();
     * Promise.resolve(opt).then(function(options){
     *     //options는 선택 항목 배열
     * })
     */
    getOptions: function (async, refresh) {
        this.optionsCache = null;
        var opt = this._getOptions(async, refresh);
        if ((opt && typeOf(opt.then) === "function")) {
            var p = Promise.resolve(opt).then(function (option) {
                this.moduleSelectAG = null;
                this.optionsCache = (option || []);
                return this.optionsCache;
            }.bind(this));
            this.moduleSelectAG = p;
            return p;
        } else {
            this.optionsCache = (opt || []);
            return this.optionsCache;
        }
    },
    _getOptions: function (async, refresh) {
        switch (this.json.itemType) {
            case "values":
                return this.json.itemValues;
            case "script":
                return this.form.Macro.exec(((this.json.itemScript) ? this.json.itemScript.code : ""), this);
            default:
                break;
        }

        var opts, firstOpts = this.getFirstOption();
        switch (this.json.itemType) {
            case "dict":
                opts = this.getOptionsWithDict(async, refresh);
                break;
            case "view":
                opts = this.getOptionsWithView(async, refresh);
                break;
            case "statement":
                opts = this.getOptionsWithStatement(async, refresh);
                break;
        }
        if (opts && typeOf(opts.then) === "function") {
            return Promise.resolve(opts).then(function (opts) {
                return this._contactOption(firstOpts, opts);
            }.bind(this));
        } else {
            return this._contactOption(firstOpts, opts);
        }
        // if( (defaultOpts && typeOf(defaultOpts.then) === "function" ) || (opts && typeOf(opts.then) === "function" ) ){
        //     return Promise.all( [defaultOpts, opts] ).then(function (arr) {
        //         return this._contactOption( arr[0], arr[1] );
        //     }.bind(this));
        // }else{
        //     return this._contactOption( defaultOpts, opts );
        // }
    },
    _contactOption: function (opt1, opt2) {
        var optA, optB;
        if (!opt1) opt1 = [];
        if (!opt2) opt2 = [];
        optA = typeOf(opt1) !== "array" ? [opt1] : opt1;
        optB = typeOf(opt2) !== "array" ? [opt2] : opt2;
        optA.each(function (o) {
            if (o) optB.unshift(o);
        });
        return optB;
    },
    getFirstOption: function () {
        //return this.form.Macro.exec(((this.json.defaultOptionsScript) ? this.json.defaultOptionsScript.code : ""), this);
        if (!this.json.firstOptionEnable) return [];
        return [this.json.firstOption || "|"];
    },

    /**
     * @summary 정리된 선택 항목 가져오기.
     * @param {Boolean} [refresh] 캐시를 무시하고 선택 항목을 다시 계산할지 여부.
     * @return {Object} 정리된 선택 항목 배열 또는 Promise 반환, 예:
     * <pre><code class='language-js'>{"valueList": ["","female","male"], "textList": ["","여","남"]}
     * </code></pre>
     * @example
     * var optionData = this.form.get('fieldId').getOptionsObj();
     * @example
     * //비동기
     * var opt = this.form.get('fieldId').getOptionsObj(true);
     * Promise.resolve(opt).then(function(optionData){
     *     //optionData는 선택 항목
     * })
     */
    getOptionsObj: function (refresh) {
        var optionItems = (refresh !== true && this.optionsCache) ? this.optionsCache : this.getOptions();
        if (optionItems && typeOf(optionItems.then) === "function") {
            return Promise.resolve(optionItems).then(function (optItems) {
                return this._getOptionsObj(optItems);
            }.bind(this));
        } else {
            return this._getOptionsObj(optionItems);
        }
    },
    _getOptionsObj: function (optItems) {
        var textList = [];
        var valueList = [];
        optItems.each(function (item) {
            switch (o2.typeOf(item)){
                case 'string':
                    var tmps = item.split("|");
                    textList.push(tmps[0]);
                    valueList.push(tmps[1] || tmps[0]);
                    break;
                case 'object':
                    textList.push(item.label || item.text);
                    valueList.push(item.value);
                    break;
            }
        });
        return {textList: textList, valueList: valueList};
    },

    setOptions: function () {
        var optionItems = this.getOptions();
        this._setOptions(optionItems);
    },

    /**
     * @summary 선택된 항목의 value와 text 가져오기.
     * @return {Object} 선택된 항목의 value와 text 반환, 예:
     * <pre><code class='language-js'>{"value": ["male"], "text": ["남"]}
     * {"value": [""], "text": [""]}
     * </code></pre>
     * @example
     * var data = this.form.get('fieldId').getTextData();
     * var text = data.text[0] //선택된 항목의 텍스트 가져오기
     */
    getTextData: function () {
        var ops;
        if (this.isReadonly() || !this._getInputTextData || (this.json.type || '').substring(0, 2) === 'El' ) {
            ops = this.getOptionsObj();
            var data = this._getBusinessData();
            var d = typeOf(data) === "array" ? data : [data];
            if (ops && typeOf(ops.then) === "function") {
                return Promise.resolve(ops).then(function (opts) {
                    return this._getTextData(d, opts)
                }.bind(this));
            } else {
                return this._getTextData(d, ops)
            }
        } else {
            return this._getInputTextData();
        }
    },
    _getTextData: function (d, opts) {
        var value = [], text = [];
        d.each(function (v) {
            var idx = opts.valueList.indexOf(v);
            value.push(v || "");
            text.push(idx > -1 ? opts.textList[idx] : (v || ""));
        });
        if (!value.length) value = [""];
        if (!text.length) text = [""];
        return {"value": value, "text": text};
    },
    getOptionsWithDict: function (async, refresh) {
        if (!this.json.itemDict || !this.json.itemDict.length) return [];
        var obj = this.json.itemDict[0];
        var dict = new this.form.Macro.environment.Dict({
            "type": obj.appType,
            "application": obj.appId,
            "name": obj.id
        });

        var paths = (this.json.itemDictPath || "").split("/");
        paths.splice(0, 1); //첫 번째는 root이므로 삭제
        var path = paths.length ? paths.join(".") : null;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemDictAsync !== false);
        var data = dict.get(path, null, null, asy, !this.json.dictUseCache);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseDictOptions(data);
            }.bind(this));
        } else {
            return this.parseDictOptions(data);
        }
    },
    getString: function (d) {
        switch (o2.typeOf(d)) {
            case "null":
                return "";
            case "string":
                return d;
            case "boolean":
            case "number":
            case "date":
                return d.toString();
            default:
                return "";
        }
    },
    parseDictOptions: function (d) {
        var arr = [], value, text, valuekey = this.json.dictValueKey, textkey = this.json.dictTextKey;
        switch (o2.typeOf(d)) {
            case "array":
                d.each(function (i) {
                    switch (o2.typeOf(i)) {
                        case "object":
                            if (valuekey && textkey) {
                                value = this.getString(i[valuekey]);
                                text = this.getString(i[textkey]);
                                arr.push(text + "|" + value);
                            } else if (valuekey) {
                                arr.push(this.getString(i[valuekey]));
                            } else if (textkey) {
                                arr.push(this.getString(i[textkey]));
                            }
                            break;
                        case "null":
                            break;
                        default:
                            arr.push(i.toString());
                            break;
                    }
                }.bind(this));
                return arr;
            case "object":
                Object.each(d, function (i, key) {
                    switch (o2.typeOf(i)) {
                        case "object":
                            if (valuekey && textkey) {
                                value = this.getString(i[valuekey]);
                                text = this.getString(i[textkey]);
                                arr.push(value + "|" + text);
                            } else if (valuekey) {
                                arr.push(this.getString(i[valuekey]));
                            } else if (textkey) {
                                arr.push(this.getString(i[textkey]));
                            }
                            break;
                        case "null":
                            break;
                        default:
                            arr.push(i.toString() + "|" + key.toString());
                            break;
                    }
                }.bind(this))
                return arr;
            case "null":
                return [];
            default:
                return [d.toString()];
        }
    },
    getOptionsWithView: function (async, refresh) {
        if (!this.json.itemView) return [];
        var obj = this.json.itemView;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemViewAsync !== false);

        var filter = [];
        if (this.json.viewFilterList && this.json.viewFilterList.length) {
            this.json.viewFilterList.each(function (entry) {
                entry.value = this.form.Macro.exec(entry.code.code, this);
                filter.push(entry);
            }.bind(this));
        }

        var data = this.form.Macro.environment.view.lookup({
            "view": obj.id,
            "application": obj.application,
            "filter": filter
        }, null, asy);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseViewOptions(data);
            }.bind(this));
        } else {
            return this.parseViewOptions(data);
        }
    },
    parseViewOptions: function (json) {
        var arr = [], value, text, valuekey = this.json.viewValueColumn, textkey = this.json.viewTextColumn;
        json.grid.each(function (d) {
            var i = d.data || {};
            if (valuekey && textkey) {
                value = valuekey === "bundle" ? d.bundle : (this.getString(i[valuekey]));
                text = textkey === "bundle" ? d.bundle : (this.getString(i[textkey]));
                arr.push(text + "|" + value);
            } else if (valuekey) {
                arr.push(valuekey === "bundle" ? d.bundle : (this.getString(i[valuekey])));
            } else if (textkey) {
                arr.push(textkey === "bundle" ? d.bundle : (this.getString(i[textkey])));
            }
        }.bind(this))
        return arr.unique();
    },

    getOptionsWithStatement: function (async, refresh) {
        if (!this.json.itemStatement) return [];
        var obj = this.json.itemStatement;

        var asy = o2.typeOf(async) === "boolean" ? async : (this.json.itemViewAsync !== false);

        var filter = [];
        if (this.json.statementFilterList && this.json.statementFilterList.length) {
            this.json.statementFilterList.each(function (entry) {
                entry.value = this.form.Macro.exec(entry.code.code, this);
                filter.push(entry);
            }.bind(this));
        }

        var parameter = {};
        if(this.json.statementParameterList && this.json.statementParameterList.length) {
            this.json.statementParameterList.each(function (entry) {
                parameter[entry.parameter] = this.parseParameter(entry);
            }.bind(this));
        }

        var data = this.form.Macro.environment.statement.execute({
            "name": obj.name,
            "mode": "data",
            "page": 1, //(number) 선택, 현재 페이지 번호, 기본값 1
            "pageSize": 1000, //(number) 선택, 페이지당 데이터 수, 기본값 20
            "filter": filter,
            "parameter": parameter,
            "parameterList": this.json.parameterList
        }, null, asy);
        if (data && typeOf(data.then) === "function") {
            return data.then(function (data) {
                return this.parseStatementOptions(data);
            }.bind(this));
        } else {
            return this.parseStatementOptions(data);
        }
    },
    parseStatementOptions: function (json) {
        var arr = [], value, text, valuekey = this.json.statementValueColumn,
            textkey = this.json.statementTextColumn;
        json.data.each(function (d) {
            if (valuekey && textkey) {
                value = this.getDataByPath(d, valuekey);
                text = this.getDataByPath(d, textkey);
                arr.push(text + "|" + value);
            } else if (valuekey) {
                value = this.getDataByPath(d, valuekey);
                arr.push(value);
            } else if (textkey) {
                text = this.getDataByPath(d, textkey);
                arr.push(text);
            }
        }.bind(this));
        return arr.unique();
    },
    parseParameter: function (f) {
        var value = f.value;
        if (f.valueType === "script") {
            value = this.form.Macro.exec(f.valueScript ? f.valueScript.code : "", this);
        }
        if (typeOf(value) === "date") {
            value = value.format("db");
        }
        var user = layout.user;
        switch (value) {
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

        if (f.formatType === "dateTimeValue" || f.formatType === "datetimeValue") {
            value = "{ts '" + value + "'}"
        } else if (f.formatType === "dateValue") {
            value = "{d '" + value + "'}"
        } else if (f.formatType === "timeValue") {
            value = "{t '" + value + "'}"
        }
        return value;
    },
    getDataByPath: function (obj, path, isUppcase) {
        var pathList = isUppcase ? path.toUpperCase().split(".") : path.split(".");
        for (var i = 0; i < pathList.length; i++) {
            var p = pathList[i];
            if ((/(^[1-9]\d*$)/.test(p))) p = p.toInt();
            if (obj[p]) {
                obj = obj[p];
            } else if (obj[p] === undefined || obj[p] === null) {
                if (!isUppcase && i === 0) {
                    return this.getDataByPath(obj, path, true);
                } else {
                    obj = "";
                }
                break;
            } else {
                obj = obj[p];
                break;
            }
        }
        return this.getString(obj);
    }

});
