MWF.xDesktop.requireApp("process.Xform", "Elradio", null, false);
/** @class Elradio Element UI 기반 다중 선택 컴포넌트.
 * @o2cn 다중 선택 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var radio = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var radio = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.Elradio
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/checkbox|Element UI Checkbox 체크박스}
 */
MWF.xApplication.process.Xform.Elcheckbox = MWF.APPElcheckbox =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elcheckbox# */
    {
    Implements: [Events],
    Extends: MWF.APPElradio,
    options: {
        /**
         * 컴포넌트 로드 전 발생. 현재 컴포넌트의 queryLoad 이벤트 시점에는 form에 등록되지 않아 this.form.get("fieldId")로 가져올 수 없으며, this.target으로 현재 컴포넌트를 가져와야 함.
         * @event MWF.xApplication.process.Xform.Elcheckbox#queryLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        /**
         * 컴포넌트 로드 후 발생. 선택 항목 로드가 비동기이면 비동기 처리 완료 후 이 이벤트 발생
         * @event MWF.xApplication.process.Xform.Elcheckbox#load
         */
        /**
         * 컴포넌트 로드 후 발생.
         * @event MWF.xApplication.process.Xform.Elcheckbox#postLoad
         * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
         */
        "moduleEvents": ["load", "queryLoad", "postLoad", "change"],
        /**
         * 바인딩 값이 변경될 때 발생하는 이벤트. this.event[0]은 갱신된 값
         * @event MWF.xApplication.process.Xform.Elcheckbox#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/checkbox|체크박스의 Checkbox Events 장}
         */
        "elEvents": ["change"]
    },
    /**
     * @summary 컴포넌트 설정 정보이자 Vue 컴포넌트의 data.
     * @member MWF.xApplication.process.Xform.Elradio#json {JsonObject}
     * @example
     *  //스크립트에서 이 객체를 가져올 수 있으며, 아래 두 줄의 코드는 동일한 객체를 가져옴
     * var json = this.form.get("elcheckbox").json;       //컴포넌트의 json 객체 조회
     * var json = this.form.get("elcheckbox").vm.$data;   //Vue 컴포넌트의 data 데이터 조회,
     *
     * //json 객체로 Element 컴포넌트 조작
     * json.border = true;      //테두리 있는 체크박스 버튼
     * json.size = "small";     //체크박스 버튼 크기 설정, border가 true일 때만 유효
     * json.textColor = "#ff0000";  //버튼 스타일 설정 시 선택 상태의 텍스트 색상
     * json.fillColor = "#ff0000";  //버튼 스타일 설정 시 선택 상태의 배경 색상
     */
    load: function(){
        this._loadModuleEvents();
        if (this.fireEvent("queryLoad")){
            this._queryLoaded();
            this._loadUserInterface();
        }
    },
    _loadNode: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node?.addClass('hide');
        }else{
            this.node.empty();
            if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                this._loadNodeEdit();
            }
        }
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        this._showValue(contentNode, data.data)
    },
    _loadMergeEditNodeByDefault: function(){
        var data = this.getSortedSectionData();
        var businessData = [];
        data.each(function(d){
            businessData = businessData.concat( d.data || [] );
        });
        this._setBusinessData( businessData );
        this._loadNode();
    },
    _loadNodeRead: function(){
        this.node.empty();
        this.node.set({
            "nodeId": this.json.id,
            "MWFType": this.json.type
        });
        var value = this.getValue();
        this._showValue(this.node, value);
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

        this.fireEvent("postLoad");
        if( this.moduleSelectAG && typeOf(this.moduleSelectAG.then) === "function" ){
            this.moduleSelectAG.then(function () {
                this.fireEvent("load");
                this.isLoaded = true;
            }.bind(this));
        }else{
            this.fireEvent("load");
            this.isLoaded = true;
        }
    },
    __showValue: function(node, value, optionItems){
        if (value){
            var texts = [];
            optionItems.each(function(item){
                var tmps = item.split("|");
                var t = tmps[0];
                var v = tmps[1] || t;

                if (value.indexOf(v)!=-1){
                    texts.push(t || v);
                }
            });
            node.set("text", texts.join(", "));
        }
    },
    _resetNodeEdit: function(){
        var div = new Element("div");
        div.inject(this.node, "after");
        this.node.destroy();
        this.node = div;
    },
    _loadNodeEdit: function(){
        if (!this.json.preprocessing) this._resetNodeEdit();
        this.setOptions();
    },

    setOptions: function(){
        var optionItems = this.getOptions();
        this._setOptions(optionItems);
    },

    _setOptions: function(optionItems){
        var p = o2.promiseAll(optionItems).then(function(radioValues){
            //this.moduleSelectAG = null;
            return this._loadElradio(radioValues);
        }.bind(this), function(){
            this.moduleSelectAG = null;
        }.bind(this));
        this.moduleSelectAG = Promise.resolve(p);
    },

    _loadElradio: function(radioValues){
        return new Promise(function(resolve){
            if (radioValues && o2.typeOf(radioValues)==="array"){
                this.node.appendHTML(this._createElementHtml(radioValues), "before");
                var button = this.node.getPrevious();

                this.node.destroy();
                this.node = button;
                this.node.set({
                    "id": this.json.id,
                    "MWFType": this.json.type
                });
                this._createVueApp(resolve);
            }
        }.bind(this));
    },

    _createElementHtml: function(radioValues){
        if (!this.json.disabled) this.json.disabled = false;

        var id = (this.json.id.indexOf("..")!==-1) ? this.json.id.replace(/\.\./g, "_") : this.json.id;
        id = (id.indexOf("@")!==-1) ? id.replace(/@/g, "_") : id;
        id = (id.indexOf("（")!==-1) ? id.replace(/（/g, "_") : id;
        id = (id.indexOf("）")!==-1) ? id.replace(/）/g, "_") : id;
        this.json["$id"] = (id.indexOf("-")!==-1) ? id.replace(/-/g, "_") : id;
        var html = "<el-checkbox-group class='o2_vue' style='box-sizing: border-box!important'";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :text-color=\"textColor\"";
        html += " :fill=\"fillColor\"";
        html += " :size=\"size\"";
        html += " :disabled=\"disabled\"";
        html += " @change=\"change\"";


        if (this.json.elGroupProperties){
            Object.keys(this.json.elGroupProperties).forEach(function(k){
                if (this.json.elGroupProperties[k]) html += " "+k+"=\""+this.json.elGroupProperties[k]+"\"";
            }, this);
        }
        // if (this.json.elGroupStyles){
        //     var style = "";
        //     Object.keys(this.json.elGroupStyles).forEach(function(k){
        //         if (this.json.elGroupStyles[k]) style += k+":"+this.json.elGroupStyles[k]+";";
        //     }, this);
        //     html += " style=\""+style+"\"";
        // }
        if (this.json.elGroupStyles) html += " :style=\"elGroupStyles\"";

        html += " >";

        radioValues.each(function(item){
            var tmps = item.split("|");
            var text = tmps[0];
            var value = tmps[1] || text;

            html += (this.json.buttonRadio) ? "<el-checkbox-button" : "<el-checkbox";
            html += " label=\""+value+"\"";
            html += " :border=\"border\"";

            if (this.json.elProperties){
                Object.keys(this.json.elProperties).forEach(function(k){
                    if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
                }, this);
            }

            // var radiostyle = "box-sizing: border-box!important;";
            // if (this.json.elStyles){
            //     Object.keys(this.json.elStyles).forEach(function(k){
            //         if (this.json.elStyles[k]) radiostyle += k+":"+this.json.elStyles[k]+";";
            //     }, this);
            // }
            // html += " style=\""+radiostyle+"\"";
            if (this.json.elStyles) html += " :style=\"elStyles\"";

            html += " >"+text;
            html += (this.json.buttonRadio) ? "</el-checkbox-button>" : "</el-checkbox>";
        }.bind(this));
        html += "</el-checkbox-group>";
        return html;
    },

        _setValue: function(value, m){
            var mothed = m || "__setValue";
            if (!!value){
                var p = o2.promiseAll(value).then(function(v){
                    //if (o2.typeOf(v)=="array") v = v[0];
                    if (this.moduleSelectAG){
                        this.moduleValueAG = this.moduleSelectAG;
                        this.moduleSelectAG.then(function(){
                            this[mothed](v);
                            return v;
                        }.bind(this), function(){});
                    }else{
                        this[mothed](v)
                    }
                    return v;
                }.bind(this), function(){});
                this.moduleValueAG = p;
                if (this.moduleValueAG) this.moduleValueAG.then(function(){
                    this.moduleValueAG = null;
                }.bind(this), function(){
                    this.moduleValueAG = null;
                }.bind(this));
            }else{
                this[mothed](value);
            }
        },
    __setValue: function(value){
        this._setBusinessData(value);
        this.json[this.json.$id] = (value) ? value : [];
    },
    __setData: function(data){
        this._setBusinessData(data);
        this.json[this.json.$id] = data;
        this.validationMode();
        this.fireEvent("setData");
    },


    _createVueApp: function(callback){
        if (!this.vm){
            this._loadVue(function(){
                this._mountVueApp(callback);
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    // _loadVue: function(callback){
    //     if (!window.Vue){
    //         var vue = (o2.session.isDebugger) ? "vue_develop" : "vue";
    //         o2.loadAll({"css": "../o2_lib/vue/element/index.css", "js": [vue, "elementui"]}, { "sequence": true }, callback);
    //     }else{
    //         if (callback) callback();
    //     }
    // },
    _mountVueApp: function(callback){
        if (!this.vueApp) this.vueApp = this._createVueExtend(callback);

        /**
         * @summary Vue 객체 인스턴스
         * @see https://vuejs.org/
         * @member {VueInstance}
         */
        this.vm = new Vue(this.vueApp);
        this.vm.$mount(this.node);
    },

    _createVueExtend: function(callback){
        var _self = this;
        return {
            data: this._createVueData(),
            mounted: function(){
                _self._afterMounted(this.$el);
                if (callback) callback();
            },
            methods: {
                change: function(v){
                    _self.validationMode();
                    o2.promiseAll(_self.validation()).then(flag=>{
                        if(String(flag) === "true"){
                            _self._setBusinessData(v || []);
                            _self.fireEvent("change");
                        }
                    });
                }
            }
        };
    },
    _createVueData: function(){
        if (this.json.$id===this.json.id) this.form.Macro.environment.data.check(this.json.$id);
        this.json[this.json.$id] = this._getBusinessData();
        if (!this.json[this.json.$id] || !this.json[this.json.$id].length) this.json[this.json.$id] = [];

        if (this.json.vueData && this.json.vueData.code){
            var d = this.form.Macro.exec(this.json.vueData.code, this);
            this.json = Object.merge(d, this.json);
        }

        if (!this.json.textColor) this.json.textColor = "";
        if (!this.json.fillColor) this.json.fillColor = "";
        if (!this.json.size) this.json.size = "";

        return this.json;
    },
    _afterMounted: function(el){
        this.node = el;
        this.node.set({
            "id": this.json.id,
            "MWFType": this.json.type
        });
        this._loadVueCss();
        this._loadDomEvents();
        this._afterLoaded();
        this.fireEvent("postLoad");
        this.fireEvent("load");
    },

    getInputData: function(){
        return this.json[this.json.$id];
    },

    /**
     * @summary 선택 항목의 text 조회.
     * @return {String[]} 선택 항목의 text 배열 반환
     * @example
     * var texts = this.form.get('fieldId').getText(); //선택 항목의 텍스트 배열 조회
     */
    getText: function(){
        var d = this.getTextData();
        if( typeOf(d.then) === "function" ){
            return d.then(function( d1 ){
                var texts = d1.text;
                return (texts && texts.length) ? texts : [];
            })
        }else{
            var texts = d.text;
            return (texts && texts.length) ? texts : [];
        }
    },

    _loadVueCss: function(){
        if (this.styleNode){
            this.node.removeClass(this.styleNode.get("id"));
        }
        if (this.json.vueCss && this.json.vueCss.code){
            this.styleNode = this.node.loadCssText(this.json.vueCss.code, {"notInject": true});
            this.styleNode.inject(this.node, "before");
        }
    },

        getExcelData: function( type ){
            var value = this.getData();
            if( type === "value" )return value.join(", ");

            var options = this.getOptionsObj();
            return Promise.resolve(options).then(function (opts) {
                value = o2.typeOf(value) === "array" ? value : [value];
                var arr = [];
                value.each( function( a, i ){
                    var idx = opts.valueList.indexOf( a );
                    var text = idx > -1 ? opts.textList[ idx ] : "";
                    if( !text )text = value;
                    arr.push( text );
                });
                return arr.join(", ");
            });
        },
        setExcelData: function(d, type){
            var arr = this.stringToArray(d);
            this.excelData = arr;
            if( type === "value" ){
                this.setData(value, true);
            }else{
                var options = this.getOptionsObj();
                this.moduleExcelAG = Promise.resolve(options).then(function (opts) {
                    arr.each( function( a, i ){
                        var idx = opts.textList.indexOf( a );
                        var v = idx > -1 ? opts.valueList[ idx ] : null;
                        arr[ i ] = v || a;
                    });
                    arr.clean();
                    this.json[this.json.$id] = arr;
                    this._setBusinessData(arr);
                    this.setData(arr, true);
                    this.moduleExcelAG = null;
                }.bind(this));
            }
        }
});
