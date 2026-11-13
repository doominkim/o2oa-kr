MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Iframe HTML iframe。
 * @o2cn iframe
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var iframe = this.form.get("name"); //컴포넌트 조회
 * //방법 2
 * var iframe = this.target; //컴포넌트 이벤트 스크립트에서 조회
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Iframe = MWF.APPIframe =  new Class({
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
		if (!this.isReadable){
            this.node?.addClass('hide');
        }else{
			this.node.empty();

			var src = this.json.src;
			if (this.json.valueType=="script"){
				src = this.form.Macro.exec(((this.json.script) ? this.json.script.code : ""), this);
			}

			this.iframe = new Element("iframe", {
				"src": src
			}).inject(this.node, "after");
			
			this.node.destroy();
			this.node = this.iframe.setStyles({
				"width": "100%",
				"border": "0"
			});
		}

		
	}
}); 