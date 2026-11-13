o2.xDesktop.requireApp("process.Xform", "$Elinput", null, false);
/** @class Elinput Element UI 기반 색상 선택 컴포넌트.
 * @o2cn 색상 선택
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
 * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|Element UI ColorPicker 색상 선택기}
 */
MWF.xApplication.process.Xform.Elcolorpicker = MWF.APPElcolorpicker =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elcolorpicker# */
    {
    Implements: [Events],
    Extends: MWF.APP$Elinput,
    options: {
        "moduleEvents": ["load", "queryLoad", "postLoad"],

        /**
         * 	바인딩 값이 변경될 때 발생. this.event[0]은 현재 값
         * @event MWF.xApplication.process.Xform.Elcolorpicker#change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|색상 선택 컴포넌트의 Events 장}
         */

        /**
         * 패널에 현재 표시된 색상이 변경될 때 발생. this.event[0]은 현재 표시된 색상 값
         * @event MWF.xApplication.process.Xform.Elcolorpicker#active-change
         * @see {@link https://element.eleme.cn/#/zh-CN/component/color-picker|색상 선택 컴포넌트의 Events 장}
         */
        "elEvents": ["change","active-change"]
    },
    _loadMergeReadContentNode: function( contentNode, data ){
        // var d = o2.typeOf( data.data ) === "array" ? data.data : [data.data];
        // contentNode.set("text", d.join( this.json.rangeSeparator ? " "+this.json.rangeSeparator+" " : " ~ " ) );

        var _self = this;

        var json = Object.clone(this.json);
        json.isReadonly = true;
        var id = this.json.id + "_" + data.key.replace(/-/g, "_");
        json.id = id;

        this._setBusinessData(data.data, id);

        var html = this.node.get("html");
        contentNode.set("html", html);
        var style = this.node.get("style");
        contentNode.set("style", style);
        contentNode.set("id", id);

        if (this.form.all[id]) this.form.all[id] = null;
        if (this.form.forms[id])this.form.forms[id] = null;

        var module = this.form._loadModule(json, contentNode, function () {
            this.field = false;
            if( _self.widget )this.widget = _self.widget;
        });
        this.form.modules.push(module);
        this.form.addEvent("getData", function (data) {
            if( data[id] )delete data[id];
        })
    },
    __setReadonly: function(data){
        if (this.isReadonly()) {
            this.node.set("text", data);
            if( this.json.elProperties ){
                this.node.set(this.json.elProperties );
            }
            if (this.json.elStyles){
                this.node.setStyles( this._parseStyles(this.json.elStyles) );
            }

        }
    },
    _loadNode: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
        }else{
            if (this.isReadonly()) this.json.disabled = true;
            this._loadNodeEdit();
        }

        
    },
    _appendVueData: function(){
        if (!this.json.isReadonly && !this.form.json.isReadonly) this.json.isReadonly = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.showAlpha) this.json.showAlpha = false;
        if (!this.json.colorFormat) {
            if( this.json.showAlpha ){
                this.json.colorFormat = "hex";
            }else{
                this.json.colorFormat = "rgb";
            }
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
        var html = "<el-color-picker";
        html += " v-model=\""+this.json.$id+"\"";
        html += " :readonly=\"isReadonly\"";
        html += " :disabled=\"disabled\"";
        html += " :show-alpha=\"showAlpha\"";
        html += " :color-format=\"colorFormat\"";

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

        html += "</el-color-picker>";
        return html;
    }
});
