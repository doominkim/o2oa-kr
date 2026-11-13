MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Label 텍스트 컴포넌트.
 * @o2cn 텍스트 컴포넌트
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var label = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var label = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Label = MWF.APPLabel =  new Class(
    /** @lends MWF.xApplication.process.Xform.Label# */
    {
	Implements: [Events],
	Extends: MWF.APP$Module,

	_loadUserInterface: function(){
        if (!this.isReadable){
            this.node?.addClass('hide');
            return '';
        }

		if (this.json.valueType == "text"){
			this.node.set("text", this.json.text || "");
		}
		if (this.json.valueType == "script"){
			var code = (this.json.script) ? this.json.script.code : "";
			if (code){
			    var value = this.form.Macro.exec(code, this);
			    this._setNodeText(value);
				//this.node.set("text", this.form.Macro.exec(code, this) || "");
			}
		}
		if (this.json.prefixIcon || this.json.suffixIcon){
            var text = this.node.get("text");
            this.node.empty();

            var tNode = new Element("div", {"styles": {
            	"margin-left": (this.json.prefixIcon) ? "20px" : "0px",
                "margin-right": (this.json.suffixIcon) ? "20px" : "0px",
                "height": "100%"
			}, "text": text}).inject(this.node);

            var height = (this.node.offsetParent === null) ? "20" : this.node.getSize().y;
            if (this.json.prefixIcon){
                var node = new Element("div", {"styles": {
                    "float": "left",
                    "width": "20px",
                    "height": ""+height+"px",
                    "background": "url("+this.json.prefixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
            if (this.json.suffixIcon){
                var node = new Element("div", {"styles": {
                    "float": "right",
                    "width": "20px",
                    "height": ""+height+"px",
                    "background": "url("+this.json.suffixIcon+") center center no-repeat"
                }}).inject(tNode, "before");
            }
		}
	},
    _setNodeText: function(value){
        if (value && value.isAG){
            value.addResolve(function(v){
                this._setNodeText(v);
            }.bind(this));
        }else{
            o2.promiseAll(value).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
            //this.node.set("text", value || "");
        }
    },
    /**인자가 Promise일 때는 문서 참고: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|Promise로 양식 비동기 처리}<br/>
     * @summary 컴포넌트에 텍스트 설정, 이 텍스트는 서버에 저장되지 않음.
     * @param text{String|Promise} .
     * @example
     *  this.form.get("fieldId").setText("test"); //텍스트 값 설정
     * @example
     *  //Promise 사용
     *  var field = this.form.get("fieldId");
     *  var dict = new this.Dict("test"); //test는 데이터 사전 이름
     *  var promise = dict.get("tools", true); //데이터 사전 get 메서드를 비동기로 사용하면 Promise 반환, 인자 true는 비동기를 의미
     *  field.setText( promise );
     */
    setText: function(text){
	    if (!!text){
            o2.promiseAll(text).then(function(v){
                this.node.set("text", v || "");
            }.bind(this), function(){});
        }else{
            this.node.set("text", text || "");
        }
        //this.node.set("text", text);
    }
});
