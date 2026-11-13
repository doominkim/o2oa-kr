MWF.xDesktop.requireApp("process.Xform", "Select", null, false);
/** @class Security 보안 등급 선택 컴포넌트.
 * 보안 등급 표시 기능을 사용하면 이 컴포넌트에서 객체 보안 등급을 선택할 수 있음
 * @o2cn 드롭다운 선택
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var field = this.form.get("fieldId"); //컴포넌트 객체 조회
 * //방법 2
 * var field = this.target; //컴포넌트 자체 스크립트(이벤트 스크립트, 기본값 스크립트, 검증 스크립트 등)에서 조회
 *
 * field.hide(); //필드 숨기기
 * var id = field.json.id; //필드 식별자 조회
 * var flag = field.isEmpty(); //필드가 비어 있는지 여부
 * @extends MWF.xApplication.process.Xform.$Selector
 * @o2category FormComponents
 * @o2range {Process|CMS|Portal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Security = MWF.APPSecurity =  new Class(
	/** @lends MWF.xApplication.process.Xform.Select# */
	{
		Implements: [Events],
		Extends: MWF.APPSelect,
		iconStyle: "selectIcon",

		/**
		 * 값이 변경될 때 발생. this.event로 변경된 선택 항목(Dom 객체)을 가져올 수 있음.
		 * @event MWF.xApplication.process.Xform.Security#change
		 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
		 */

		/**
		 * @ignore
		 * @member {Element} descriptionNode
		 * @memberOf MWF.xApplication.process.Xform.Security#
		 */
		initialize: function(node, json, form, options){
			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
			this.fieldModuleLoaded = false;
			this.nodeHtml = this.node.get("html");
		},
		__showValue: function(node, value, optionItems){
			if (value){
				value = value.toString();
				if (typeOf(value)!=="array") value = [value];
				var texts = [];
				optionItems.each(function(item){
					var tmps = item.split("|");
					var t = tmps[0];
					var v = tmps[1] || t;

					if (v){

						if (value.indexOf(v)!=-1){
							texts.push(t);
						}
					}

				});
				node.set("text", texts.join(", "));
			}
		},


		_getOptions: function(async, refresh){
			if (this.securityLabelList) return Promise.resolve(this.securityLabelList);

			return o2.Actions.load("x_general_assemble_control").SecurityClearanceAction.object().then(function(json){
				var opts = ["|"];
				Object.keys(json.data).forEach(function(k){
					opts.push(k+"|"+json.data[k]);
				}.bind(this))
				this.securityLabelList = opts;
				return this.securityLabelList;
			}.bind(this));
		},
		_setBusinessData: function(v, id){
			if (!this.isEditable) return;
			var value = v.toInt();
			this.setBusinessDataById(value, id);
			// this.form.businessData.data.$work.objectSecurityClearance = value;
		},
		getInputData: function(){
			if( this.isReadonly()){
				return this._getBusinessData();
			}else{
				var ops = this.node.getElements("option");
				var value = [];
				ops.each(function(op){
					if (op.selected){
						var v = op.get("value");
						value.push(v || "");
					}
				});
				if (!value.length) return null;
				return (value.length==1) ? value[0].toInt() : value.toInt();
			}
		},
		addOption: function(){}
	});
