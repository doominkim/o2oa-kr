MWF.xDesktop.requireApp("process.Xform", "$ElModule", null, false);
/** @class Elicon Element UI 기반 아이콘 컴포넌트.
 * @o2cn 아이콘 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var icon = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var icon = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 * @see {@link https://element.eleme.cn/#/zh-CN/component/icon|Element UI Icon 아이콘}
 */
MWF.xApplication.process.Xform.Elicon = MWF.APPElicon =  new Class(
    /** @lends MWF.xApplication.process.Xform.Elicon# */
    {
    Implements: [Events],
    Extends: MWF.APP$ElModule,
    _appendVueData: function(){
        if (!this.json.icon) this.json.icon = "el-icon-platform-eleme";
        if (!this.json.iconSize) this.json.iconSize = "16";
        if (!this.json.iconColor) this.json.iconColor = "";
        if (!this.json.icon) this.json.icon = "";
        if (!this.json.elStyles) this.json.elStyles = {};
    },
    _createElementHtml: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        } 

        var html = "<i";
        html += " :class=\"icon\"";

        if (this.json.elProperties){
            Object.keys(this.json.elProperties).forEach(function(k){
                if (this.json.elProperties[k]) html += " "+k+"=\""+this.json.elProperties[k]+"\"";
            }, this);
        }

        html += " :style=\"[elStyles, {fontSize: iconSize+'px', color: iconColor}]\"";


        html += "></i>";
        return html;
    }

}); 
