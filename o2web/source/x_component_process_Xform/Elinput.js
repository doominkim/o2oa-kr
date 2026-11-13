o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput Element UI 기반 입력 상자 컴포넌트.
 * @o2cn 입력 상자
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/input|Element UI Input 입력 상자}
 */
MWF.xApplication.process.Xform.Elinput = MWF.APPElinput =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elinput# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * input이 포커스를 얻을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elinput#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|입력 컴포넌트의 Input Events 장}
         */
        /**
         * input이 포커스를 잃을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elinput#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|입력 컴포넌트의 Input Events 장}
         */
        /**
         * 입력 상자가 포커스를 잃거나 사용자가 Enter를 누를 때만 발생. this.event[0]은 컴포넌트의 value
         * @event MWF.xApplication.process.Xform.Elinput#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|입력 컴포넌트의 Input Events 장}
         */
        /**
         * Input 값이 변경될 때 발생. this.event[0]은 컴포넌트의 value
         * @event MWF.xApplication.process.Xform.Elinput#input
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|입력 컴포넌트의 Input Events 장}
         */
        /**
         * clearable 속성으로 생성된 지우기 버튼을 클릭할 때 발생
         * @event MWF.xApplication.process.Xform.Elinput#clear
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input|입력 컴포넌트의 Input Events 장}
         */
        "elEvents": ["focus", "blur", "change", "input", "clear"]
    },
    _appendVueData: function(){
        if (!this.json.maxlength) this.json.maxlength = "";
        if (!this.json.minlength) this.json.minlength = "";
        if (!this.json.max) this.json.max = "";
        if (!this.json.min) this.json.min = "";
        if (!this.json.showWordLimit) this.json.showWordLimit = false;
        if (!this.json.showPassword) this.json.showPassword = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.clearable) this.json.clearable = false;
        if (!this.json.size) this.json.size = "";
        if (!this.json.prefixIcon) this.json.prefixIcon = "";
        if (!this.json.suffixIcon) this.json.suffixIcon = "";
        if (!this.json.rows) this.json.rows = 2;
        if (!this.json.autosize) this.json.autosize = false;
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.resize) this.json.resize = "none";
        if (!this.json.description) this.json.description = "";
        if (this.json.minRows && this.json.maxRows){
            this.json.autosize = {minRows: this.json.minRows.toInt(), maxRows: this.json.maxRows.toInt()}
        }
    },
    // appendVueExtend: function(app){
    //     if (!app.methods) app.methods = {};
    //     app.methods.$loadElEvent = function(ev){
    //         this.validationMode();
    //         if (ev==="change") this._setBusinessData(this.getInputData());
    //         if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
    //             this.form.Macro.fire(this.json.events[ev].code, this, event);
    //         }
    //     }.bind(this);
    // },
    _createElementHtml: function(){
        //var numberStr = (this.json.inputType === "number" && this.json.resultType === "number" ) ? ".number" : "";
        var html = "<el-input";
        html += " v-model"+"=\""+this.json.$id+"\"";
        if( this.json.inputType === 'number' ){
            html += " :max=\"max\"";
            html += " :min=\"min\"";
        }else{
            html += " :maxlength=\"maxlength\"";
            html += " :minlength=\"minlength\"";
        }
        html += " :show-word-limit=\"showWordLimit\"";
        html += " :show-password=\"showPassword\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :prefix-icon=\"prefixIcon\"";
        html += " :suffix-icon=\"suffixIcon\"";
        html += " :rows=\"rows\"";
        html += " :autosize=\"autosize\"";
        html += " :readonly=\"readonly\"";
        html += " :resize=\"resize\"";
        html += " :clearable=\"clearable\"";
        html += " :type=\"inputType\"";
        html += " :placeholder=\"description\"";


        // this.options.elEvents.forEach(function(k){
        //     html += " @"+k+"=\"$loadElEvent('"+k+"')\"";
        // });
        this.options.elEvents.forEach(function(k){
            html += " @"+k+"=\"$loadElEvent_"+k.camelCase()+"\"";
        });

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

        html += "</el-input>";
        return html;
    },
        _createEventFunction: function(methods, k){
            methods["$loadElEvent_"+k.camelCase()] = function(){
                var checkResult = true;
                debugger;
                if (k==="change"){
                    if(this.json.inputType === "number"  ){
                        if( this.json.resultType === "number" ){
                            if( parseFloat(arguments[0]).toString() !== "NaN" ){
                                this.json[this.json.$id] = parseFloat(arguments[0]);
                            }
                        }
                        var value = this.getMin( this.getMax( this.json[this.json.$id] ) );
                        if( value !== this.json[this.json.$id] ){
                            this.json[this.json.$id] = value;
                        }
                    }
                    this.validationMode();
                    this._setBusinessData(this.getInputData());
                    checkResult = this.validation();
                }
                if (this.json.events && this.json.events[k] && this.json.events[k].code){
                    this.form.Macro.fire(this.json.events[k].code, this, arguments);
                }
                o2.promiseAll(checkResult).then(flag=>{
                    if(String(flag) === "true"){
                        this.fireEvent(k, arguments);
                    }
                });
            }.bind(this);
        },
        getMax: function( value ){
            if( isNaN(value) )return value;
            if( typeOf( value ) === "string" )value = parseFloat(value);
            if( typeOf(this.json.max)!=='null' && !isNaN( this.json.max )){
                var max = this.json.max;
                if( typeOf( max ) === "string" )max = parseFloat(max);
                return isNaN(max) ? value : Math.min( max, value );
            }else{
                return value;
            }
        },
        getMin: function( value ){
            if( isNaN(value) )return value;
            if( typeOf( value ) === "string" )value = parseFloat(value);
            if( typeOf(this.json.min)!=='null' && !isNaN( this.json.min )){
                var min = this.json.min;
                if( typeOf( min ) === "string" )min = parseFloat(min);
                return isNaN(min) ? value : Math.max( min, value );
            }else{
                return value;
            }
        },
    getValue: function(){
        if (!this.isReadable) return '';
        if (this.moduleValueAG) return this.moduleValueAG;
        var value = this._getBusinessData();
        if( this.json.inputType === "number" ){
            if (value || value===0 ){
                return this.json.resultType === "string" ? value.toString() : value;
            }else{
                value = this._computeValue();
                value = (o2.typeOf(value)!=="null") ? value : "";
                return this.json.resultType === "string" ? value.toString() : value;
            }
        }else{
            if (value || value===0 || value === false){
                return value;
            }else{
                value = this._computeValue();
                return (o2.typeOf(value)!=="null") ? value : "";
            }
        }
    },
    __setReadonly: function(data){
        if (this.isReadonly()) {
            this.node.set("text", data);
            if( this.json.inputType === "textarea"){
                this.node.setStyle('white-space', 'pre-wrap');
            }
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

        }
    }
});
