MWF.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elbutton Element UI 기반 버튼 컴포넌트.
 * @o2cn 버튼 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var button = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var button = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/button|Element UI Button 버튼}
 */
MWF.xApplication.process.Xform.Elbutton = MWF.APPElbutton =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elbutton# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    /**
     * @summary 컴포넌트 설정 정보이자 Vue 컴포넌트의 data.
     * @member MWF.xApplication.process.Xform.Elbutton#json {JsonObject}
     * @example
     *  //스크립트에서 이 객체를 가져올 수 있으며, 아래 두 줄의 코드는 동일한 객체를 가져옴
     * var json = this.form.get("elbutton").json;       //컴포넌트의 json 객체 조회
     * var json = this.form.get("elbutton").vm.$data;   //Vue 컴포넌트의 data 데이터 조회,
     *
     * //json 객체로 Element 컴포넌트 조작
     * json.bttype = "success"; //버튼 스타일을 success로 변경
     * json.loading = true;     //버튼을 로딩 중 상태로 표시
     * json.disabled = true;    //버튼을 비활성화
     */

    _appendVueData: function(){
        if (!this.json.size) this.json.size = "";
        if (!this.json.bttype) this.json.bttype = "";
        if (!this.json.plain) this.json.plain = false;
        if (!this.json.round) this.json.round = false;
        if (!this.json.circle) this.json.circle = false;
        if (!this.json.disabled) this.json.disabled = false;
        if (!this.json.loading) this.json.loading = false;
        if (!this.json.icon) this.json.icon = false;
    },
    _createElementHtml: function(){
        if (!this.isReadable) return '';

        var html = "<el-button";
        html += " :size=\"size\"";
        html += " :type=\"bttype\"";
        html += " :plain=\"plain\"";
        html += " :round=\"round\"";
        html += " :circle=\"circle\"";
        html += " :disabled=\"disabled\"";
        html += " :loading=\"loading\"";
        if( this.json.iconPosition !== "right" )html += " :icon=\"icon\"";

        if (this.json.autofocus==="yes") html += " autofocus";

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

        html += ">"+((this.json.circle!=="yes" && (this.json.isText!=="no" && this.json.isText)) ? (this.json.name || this.json.id) : "");
        if( this.json.iconPosition === "right" )html += "<i class=\""+ this.json.icon +" el-icon--right\"></i>";
        html += "</el-button>";
        return html;
    }
}); 
