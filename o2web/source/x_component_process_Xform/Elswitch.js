o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput Element UI 기반 스위치 컴포넌트.
 * @o2cn 스위치 컴포넌트
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/switch|Element UI Switch 스위치}
 */
MWF.xApplication.process.Xform.Elswitch = MWF.APPElswitch =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elswitch# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],
        /**
         * switch 상태가 변경될 때의 콜백 함수. this.event[0]은 새 상태 값
         * @event MWF.xApplication.process.Xform.Elswitch#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/switch|스위치 컴포넌트의 Events 장}
         */
        "elEvents": ["change"]
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        this._loadActiveJson();
        if (data.data==="" || data.data){
            contentNode.set("text", (this.json.activeText || "true"));
        }else{
            contentNode.set("text", (this.json.inactiveText || "false"));
        }
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

    _appendVueData: function(){
        this.form.Macro.environment.data.check(this.json.id);
        this.json[this.json.id] = this._getBusinessData();

        if (!this.json.width || !this.json.width.toFloat()) this.json.width = 40;
        if (!this.json.activeText) this.json.activeText = "";
        if (!this.json.inactiveText) this.json.inactiveText = "";
        if (!this.json.activeColor) this.json.activeColor = "#409EFF";
        if (!this.json.inactiveColor) this.json.inactiveColor = "#C0CCDA";

        if (!this.json.activeIconClass) this.json.activeIconClass = "";
        if (!this.json.inactiveIconClass) this.json.inactiveIconClass = "";

        if (!this.json.readonly) this.json.readonly = false;
        if (!this.json.description) this.json.description = "";
        if (!this.json.disabled) this.json.disabled = false;

        this._loadActiveJson();
    },
    _loadActiveJson: function(){
        if (!this.json.valueType) this.json.activeValueType = "boolean";
        switch(this.json.valueType){
            case "boolean":
                this.json.activeValue = true;
                this.json.inactiveValue = false;
                break;
            case "string":
                if (!this.json.activeValue) this.json.activeValue = "1";
                if (!this.json.inactiveValue) this.json.inactiveValue = "0";
                break;
            case "number":
                if (!this.json.activeValue) this.json.activeValue = 1;
                if (!this.json.inactiveValue) this.json.inactiveValue = 0;
                this.json.activeValue = this.json.activeValue.toFloat();
                this.json.inactiveValue = this.json.inactiveValue.toFloat();
                break;
            default:
                this.json.activeValue = true;
                this.json.inactiveValue = false;
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
        var html = "<el-switch";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :width=\"width\"";
        html += " :active-text=\"activeText\"";
        html += " :inactive-text=\"inactiveText\"";
        html += " :active-color=\"activeColor\"";
        html += " :inactive-color=\"inactiveColor\"";

        html += " :disabled=\"disabled\"";
        html += " :active-icon-class=\"activeIconClass\"";
        html += " :inactive-icon-class=\"inactiveIconClass\"";

        html += " :active-value=\"activeValue\"";
        html += " :inactive-value=\"inactiveValue\"";

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
        html += "</el-switch>";
        return html;
    },
    __setReadonly: function(data){
        if (this.isReadonly()){
            if (data==="" || data){
                this.node.set("text", (this.json.activeText || "true"));
            }else{
                this.node.set("text", (this.json.inactiveText || "false"));
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

            this.fireEvent("load");
            this.isLoaded = true;
        }
    },

        getExcelData: function(){
            var data = this.json[this.json.$id];
            if (data==="" || data){
                return this.json.activeText || "true";
            }else{
                return this.json.inactiveText || "false";
            }
        },
        setExcelData: function(d){
            var data = true;
            this.excelData = d;
            if ( (d || "").toString === ( this.json.inactiveText || "false" ) ){
                data = false;
            }
            this.setData(data, true);
        }
});
