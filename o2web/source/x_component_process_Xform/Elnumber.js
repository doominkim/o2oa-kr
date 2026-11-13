o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput Element UI 기반 숫자 입력 상자 컴포넌트.
 * @o2cn 숫자 입력 상자
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|Element UI InputNumber 카운터}
 */
MWF.xApplication.process.Xform.Elnumber = MWF.APPElnumber =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elnumber# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * input이 포커스를 얻을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elnumber#focus
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|카운터의 Events 장}
         */
        /**
         * input이 포커스를 잃을 때 발생. this.event[0]은 Event
         * @event MWF.xApplication.process.Xform.Elnumber#blur
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|카운터의 Events 장}
         */
        /**
         * 바인딩 값이 변경될 때 발생. this.event[0]은 컴포넌트의 currentValue, this.event[1]은 컴포넌트의 oldValue
         * @event MWF.xApplication.process.Xform.Elnumber#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/input-number|카운터의 Events 장}
         */
        "elEvents": ["focus", "blur", "change"]
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
     * json.size = "mini";      //입력 상자 크기 변경
     * json.disabled = true;     //입력 상자를 비활성화로 설정
     */
    _loadUserInterface: function(){
        if ( this.isSectionMergeRead() ) { //구간 병합 표시
            this.node.empty();
            this.node.set({
                "nodeId": this.json.id,
                "MWFType": this.json.type
            });
            switch (this.json.mergeTypeRead) {
                case "amount":
                    this._loadMergeAmountReadNode();
                    break;
                case "average":
                    this._loadMergeAverageReadNode();
                    break;
                default:
                    this._loadMergeReadNode();
                    break;
            }
        }else{
            if( this.isSectionMergeEdit() ){
                switch (this.json.mergeTypeEdit) {
                    case "amount":
                        this._loadMergeAmountEidtNode();
                        break;
                    case "average":
                        this._loadMergeAverageEditNode();
                        break;
                }
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
    _loadMergeAmountReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this.node.set("text", total.toString());
    },
    _loadMergeAverageReadNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this.node.set("text", average.toString());
    },
    _loadMergeAmountEidtNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        this._setBusinessData( total.toNumber() );
        this._loadNode();
    },
    _loadMergeAverageEditNode: function(){
        var data = this.getBusinessDataById();
        var total = new Decimal(0);
        for( var key in data ){
            total = total.plus(new Decimal(data[key] || 0));
        }
        var average = total.div(  new Decimal(Object.keys(data).length) );
        this._setBusinessData( average.toNumber() );
        this._loadNode();
    },
    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        // if (!this.json.max || o2.typeOf(this.json.max)!=="number") this.json.max = "Infinity";
        // if (!this.json.min || o2.typeOf(this.json.max)!=="number") this.json.min = "-Infinity";
        if (!this.json.step || o2.typeOf(this.json.step)!=="number") this.json.step = 1;
        if (!this.json.stepStrictly) this.json.stepStrictly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.precision) this.json.precision = 0;
        if (!this.json.size) this.json.size = "";
        if (!this.json.controlsPosition) this.json.controlsPosition = "";
        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.description) this.json.description = "";
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
        var html = "<el-input-number";
        html += " v-model=\""+this.json.$id+"\"";
        if (o2.typeOf(this.json.max)==="number") html += " :max=\"max\"";
        if (o2.typeOf(this.json.min)==="number") html += " :min=\"min\"";
        html += " :step=\"step\"";
        html += " :step-strictly=\"stepStrictly\"";
        html += " :disabled=\"disabled\"";
        html += " :size=\"size\"";
        html += " :precision=\"precision\"";
        html += " :controls-position=\"controlsPosition\"";
        html += " :readonly=\"readonly\"";
        html += " :placeholder=\"description\"";

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

        // if (this.json.vueSlot) html += this.json.vueSlot;

        html += "</el-input-number>";
        return html;
    }
});
