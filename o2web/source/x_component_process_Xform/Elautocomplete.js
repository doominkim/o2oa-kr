o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
MWF.xDesktop.requireApp("process.Xform", "$Selector", null, false);

if( !o2.APP$ElSelector ){
    o2.xApplication.process.Xform.$ElSelector = o2.APP$ElSelector = new Class({
        Implements: [Events],
        Extends: MWF.APP$Elinput
    });
    Object.assign(o2.APP$ElSelector.prototype, o2.APP$Selector.prototype);
}

/** @class Elautocomplete Element UI 기반 자동 완성 입력 상자 컴포넌트.
 * @o2cn 자동 완성 입력 상자
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
 */
MWF.xApplication.process.Xform.Elautocomplete = MWF.APPElautocomplete =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elautocomplete# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElSelector,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * input이 포커스를 잃고 값이 변경되었을 때, 또는 제안 패널의 항목을 클릭해 값이 변경되었을 때 발생. this.event[0]은 컴포넌트 값
         * @event MWF.xApplication.process.Xform.Elautocomplete#change
         * @see {@link https://element.eleme.io/#/zh-CN/component/input#dai-shu-ru-jian-yi|input 컴포넌트의 입력 제안 장}
         */
        /**
         * 제안 패널의 항목을 클릭한 후 발생. this.event[0]은 선택된 항목
         * @event MWF.xApplication.process.Xform.Elautocomplete#select
         * @see {@link https://element.eleme.io/#/zh-CN/component/input#input-methods|input 컴포넌트의 Input Method 장}
         */
        "elEvents": ["select", "change"]
    },
    /**
     * @summary 컴포넌트 설정 정보이자 Vue 컴포넌트의 data.
     * @member MWF.xApplication.process.Xform.Elautocomplete#json {JsonObject}
     * @example
     *  //스크립트에서 이 객체를 가져올 수 있으며, 아래 두 줄의 코드는 동일한 객체를 가져옴
     * var json = this.form.get("elinput").json;       //컴포넌트의 json 객체 가져오기
     * var json = this.form.get("elinput").vm.$data;   //Vue 컴포넌트의 data 데이터 가져오기,
     *
     * //json 객체로 Element 컴포넌트 조작
     * json.size = "mini";      //입력 상자 크기 변경
     * json.disabled = true;     //입력 상자를 비활성화로 설정
     */
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.placement) this.json.placement = "bottom-start";
        if (!this.json.popperClass) this.json.popperClass = "";
        if (!this.json.triggerOnFocus && this.json.triggerOnFocus!==false) this.json.triggerOnFocus = true;
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.description) this.json.description = "";
        if (!this.json.disabled) this.json.disabled = false;

        this._setPopperClass();
    },
    resetOption: function(){
        this.reload();
    },
    _createEventFunction: function(methods, k){
        methods["$loadElEvent_"+k.camelCase()] = function(){
            var checkResult = true;
            if (k==="change") {
                this.validationMode();
                this._setBusinessData(this.getInputData());
                checkResult = this.validation();
            }
            if (this.json.events && this.json.events[k] && this.json.events[k].code){
                this.form.Macro.fire(this.json.events[k].code, this, arguments);
            }
            if(k==="select"){
                this.validationMode();
                var arr = [];
                var d = this._getBusinessData();
                if( arguments[0] && arguments[0].value )arr.push(arguments[0].value);
                if( (d||"") !== (arr[0] || "")){
                    if (this.json.events && this.json.events["change"] && this.json.events["change"].code){
                        this.form.Macro.fire(this.json.events["change"].code, this, arr);
                    }
                }
                this._setBusinessData(arr[0]);
                this.validation();
            }
            o2.promiseAll(checkResult).then(flag=>{
                if(String(flag) === "true"){
                    this.fireEvent(k, arguments);
                }
            })
        }.bind(this);
    },
    appendVueExtend: function(app){
        if (!app.methods) app.methods = {};
        // app.methods.$loadElEvent = function(ev){
        //     this.validationMode();
        //     if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
        //         this.form.Macro.fire(this.json.events[ev].code, this, event);
        //     }
        // }.bind(this);
        if (!this.json.itemType || this.json.itemType==='values'){
            app.methods.$fetchSuggestions = function(qs, cb){
                if (this.json.itemValues){
                    var items = this.json.itemValues.filter(function(v){
                        return !qs || v.indexOf(qs)!=-1;
                    }).map(function(v){
                        return {"value": v};
                    });
                    cb(items);
                    //return items;
                }
                return [];
            }.bind(this);
        }else if(this.json.itemType==='script'){
            if (this.json.itemScript && this.json.itemScript.code){
                var fetchSuggestions = this.form.Macro.exec(this.json.itemScript.code, this);
                if (o2.typeOf(fetchSuggestions)==="function"){
                    app.methods.$fetchSuggestions = function(){
                        fetchSuggestions.apply(this, arguments);
                    }.bind(this);
                }
            }
        }else{
            var options;
            Promise.resolve(this.getOptions()).then(function (opt) {
                options = opt;
            });
            app.methods.$fetchSuggestions = function(qs, cb){
                if (options){
                    var items = options.filter(function(v){
                        return !qs || v.indexOf(qs)!=-1;
                    }).map(function(v){
                        return {"value": v};
                    });
                    cb(items);
                    //return items;
                }
                return [];
            }.bind(this);
        }
        // app.methods.$fetchSuggestions = function(qs, cb){
        //     if (this.json.itemType!=='script'){
        //         if (this.json.itemValues){
        //             var items = this.json.itemValues.filter(function(v){
        //                 return !qs || v.indexOf(qs)!=-1;
        //             }).map(function(v){
        //                 return {"value": v};
        //             });
        //             cb(items);
        //             return;
        //         }
        //         cb();
        //     }else{
        //         this.form.Macro.environment.queryString = qs;
        //         var list = this.form.Macro.exec(this.json.itemScript.code, this);
        //         Promise.resolve(list).then(function(items){
        //             cb(items);
        //             delete this.form.Macro.environment.queryString;
        //         }).catch(function(){
        //             cb();
        //             delete this.form.Macro.environment.queryString;
        //         });
        //     }
        // }.bind(this);
    },
    _createElementHtml: function(){
        var html = "<el-autocomplete";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :placement=\"placement\"";
        html += " :popper-class=\"popperClass\"";
        html += " :trigger-on-focus=\"triggerOnFocus\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :placeholder=\"description\"";
        html += " :fetch-suggestions=\"$fetchSuggestions\"";
        html += " :hide-loading=\"false\"";
        html += " :disabled=\"disabled\"";
        html += " :popper-append-to-body=\"false\"";

        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });
        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent('"+k+"')\"";
        // });

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }
        if (this.json.elStyles) html += " :style=\"elStyles\"";
        // if (this.json.elStyles){
        //     var style = "";
        //     Object.keys(this.json.elStyles).forEach(function(k){
        //         if (this.json.elStyles[k]) style += k+":"+this.json.elStyles[k]+";";
        //     }, this);
        //     html += " style=\""+style+"\"";
        // }

        html += ">";

        if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-autocomplete>";
        return html;
    }
});
