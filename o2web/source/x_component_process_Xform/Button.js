MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Button 버튼 컴포넌트.
 * @o2cn 버튼
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
 */
MWF.xApplication.process.Xform.Button = MWF.APPButton =  new Class({
    Implements: [Events],
    Extends: MWF.APP$Module,
    iconStyle: "personfieldIcon",

    _loadUserInterface: function(){
        // var button = new Element("button");
        // button.inject(this.node, "after");
        // this.node.destroy();
        // this.node = button;

        if (this.isReadable && this.isEditable){
            var button = this.node.getElement("button");
            if (!button) button = new Element("button");
                button.inject(this.node, "after");
            this.node.destroy();
            this.node = button;
    
            this.node.set({
                "id": this.json.id,
                "text": this.json.name || this.json.id,
                "MWFType": this.json.type
            });
            if (!this.json.preprocessing) this.node.setStyles(this.form.css.buttonStyles);
            if( this.json.properties ){
                this.node.set(this.json.properties )
            }
        }else{
            this.node.setStyle('display', 'none');
        }
        
    }

}); 
