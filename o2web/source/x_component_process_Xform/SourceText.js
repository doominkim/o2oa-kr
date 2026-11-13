MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class SourceText 데이터 텍스트 컴포넌트.
 * @o2cn 데이터 텍스트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var sourceText = this.form.get("fieldId"); //컴포넌트 가져오기
 * //방법 2
 * var sourceText = this.target; //컴포넌트 자체의 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.SourceText = MWF.APPSourceText =  new Class({
    Extends: MWF.APP$Module,
    options: {
        "moduleEvents": ["loadText", "load", "queryLoad", "postLoad"]
    },

	_loadUserInterface: function(){
        /**
         * @ignore
         * @member parentLine
         * @memberOf MWF.xApplication.process.Xform.SourceText#
         */
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }
        this._loadJsonData();
	},
    _getSource: function(){
        var parent = this.node.getParent();
        while(parent && (parent.get("MWFtype")!="source" && parent.get("MWFtype")!="subSource" && parent.get("MWFtype")!="subSourceItem")) parent = parent.getParent();
        return (parent) ? parent.retrieve("module") : null;
    },
    _loadJsonData: function(){
        this.node.set("text", "");
        this.source = this._getSource();
        if (this.source){
            if (this.source.data){
                COMMON.AjaxModule.load("JSONTemplate", function(){

                    this.template = new Template();
                    this.text = this.template.substitute("{"+this.json.jsonPath+"}", this.source.data);

                    if (this.json.jsonText){
                        if (this.json.jsonText.code){
                            this.text = this.form.Macro.exec(this.json.jsonText.code, this);
                            if( typeOf(this.text) === "string" )this.node.set("text", this.text);
                        }else{
                            this.node.set("text", this.text);
                        }
                    }else{
                        this.node.set("text", this.text);
                    }
                    this.fireEvent("loadText");

                }.bind(this));
            }
        }
    }
}); 