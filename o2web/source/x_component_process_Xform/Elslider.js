o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput Element UI 기반 슬라이더 컴포넌트.
 * @o2cn 슬라이더 컴포넌트
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Element UI Slider 슬라이더}
 */
MWF.xApplication.process.Xform.Elslider = MWF.APPElslider =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elslider# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * 값이 변경될 때 발생(마우스로 드래그할 때는 마우스를 놓은 후에만 발생). this.event[0]은 변경된 값
         * @event MWF.xApplication.process.Xform.Elslider#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Slider 슬라이더의 Events 장}
         */
        /**
         * 데이터가 변경될 때 발생(마우스로 드래그할 때는 이동 중 실시간 발생). this.event[0]은 변경된 값
         * @event MWF.xApplication.process.Xform.Elslider#input
         * @see {@link https://element.eleme.cn/#/zh-CN/component/slider|Slider 슬라이더의 Events 장}
         */
        "elEvents": ["change", "input"]
    },
    /**
     * @summary 컴포넌트 설정 정보이자 Vue 컴포넌트의 data.
     * @member MWF.xApplication.process.Xform.Elinput#json {JsonObject}
     * @example
     *  //스크립트에서 이 객체를 가져올 수 있으며, 아래 두 줄의 코드는 동일한 객체를 가져옴
     * var json = this.form.get("elinput").json;       //컴포넌트의 json 객체 가져오기
     * var json = this.form.get("elinput").vm.$data;   //Vue 컴포넌트의 data 데이터 가져오기,
     *
     * //json 객체로 Element 컴포넌트 조작
     * json.disabled = true;     //입력 상자를 비활성화로 설정
     */
    // _loadNode: function(){
    //     if (this.isReadonly()) this.json.disabled = true;
    //     this._loadNodeEdit();
    // },
    isReadonly : function(){
        return !!(this.readonly || this.json.isReadonly || this.form.json.isReadonly || this.json.showMode==="read" || this.isSectionMergeRead());
    },
    _loadNode: function(){
        if (!this.isReadable && !!this.isHideUnreadable){
            this.node?.addClass('hide');
        }else{
             if (this.isReadonly()){
                this._loadNodeRead();
            }else{
                if (!this.isReadable) this.json.disabled = true;
                this._loadNodeEdit();
            }
        }
    },

    _loadMergeReadContentNode: function( contentNode, data ){
        contentNode.set("text", data.data);
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.max || !this.json.max.toFloat()) this.json.max = 100;
        if (!this.json.min || !this.json.min.toFloat()) this.json.min = 0;
        this.json.min = this.json.min.toFloat();
        this.json.max = this.json.max.toFloat();

        if (!this.json.step || !this.json.step.toFloat()) this.json.step = 1;
        this.json.step = this.json.step.toFloat();

        if (this.json.showTooltip!==false) this.json.showTooltip = true;

        if (this.json.vertical && !this.json.height) this.json.height = "100px";
        if (!this.json.height) this.json.height = "";
        if (!this.json.inputSize) this.json.inputSize = "";
        if (!this.json.tooltipClass) this.json.tooltipClass = "";

        if (!this.json.disabled) this.json.disabled = false;

        if (this.json.marksScript && this.json.marksScript.code){
            this.json.marks = this.form.Macro.exec(this.json.marksScript.code, this);
        }else{
            this.json.marks = {};
        }
    },
    appendVueExtend: function(app){
        if (!app.methods) app.methods = {};
        // app.methods.$loadElEvent = function(ev){
        //     this.validationMode();
        //     if (ev==="change") this._setBusinessData(this.getInputData());
        //     if (this.json.events && this.json.events[ev] && this.json.events[ev].code){
        //         this.form.Macro.fire(this.json.events[ev].code, this, event);
        //     }
        // }.bind(this);

        if (this.json.formatTooltip && this.json.formatTooltip.code){
            var fun = this.form.Macro.exec(this.json.formatTooltip.code, this);
            if (o2.typeOf(fun)==="function"){
                app.methods.$formatTooltip = function(){
                    fun.apply(this, arguments);
                }.bind(this);
            }else{
                app.methods.$formatTooltip = function(){};
            }
        }
    },
    _createElementHtml: function(){
        var html = "<el-slider";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :max=\"max\"";
        html += " :min=\"min\"";
        html += " :step=\"step\"";
        html += " :show-stops=\"showStops\"";
        html += " :range=\"range\"";
        html += " :vertical=\"vertical\"";
        html += " :height=\"height\"";
        html += " :show-input=\"showInput\"";
        html += " :show-input-controls=\"showInputControls\"";
        html += " :input-size=\"inputSize\"";
        html += " :show-tooltip=\"showTooltip\"";
        html += " :tooltip-class=\"tooltipClass\"";
        html += " :disabled=\"disabled\"";
        html += " :marks=\"marks\"";

        if (this.json.formatTooltip && this.json.formatTooltip.code){
            html += " :format-tooltip=\"$formatTooltip\"";
        }

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
        html += "</el-slider>";
        return html;
    }
    // __setReadonly: function(data){
    //
    // }
});
