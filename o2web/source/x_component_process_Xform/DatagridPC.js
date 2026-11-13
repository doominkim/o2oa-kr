/**
 * 데이터 그리드 데이터 구조.
 * @typedef {Object} DatagridData
 * @property {Array} data - 데이터 그리드 목록 데이터
 * @property {Object} total - 통계 데이터
 * @example
 * 	{
	  "data": [ //데이터 그리드 항목
		{
		  "datagrid_datagrid$Title": { //데이터 그리드 1열 title 식별자
			"org_20": {  //데이터 그리드 1열 필드 식별자, 사용자 컴포넌트 단일 객체로 객체를 저장
			  "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "홍길동",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "란더종횡/마케팅부",
			  "unitName": "마케팅부"
			}
		  },
		  "datagrid_datagrid$Title1": { //데이터 그리드 2열 title 식별자
			"org_21": [{  //데이터 그리드 2열 필드 식별자, 사용자 컴포넌트 다중 객체로 배열을 저장
			  "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "홍길동",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "란더종횡/마케팅부",
			  "unitName": "마케팅부"
			},{
			  "distinguishedName": "김철수@bf007525-99a3-4178-a474-32865bdddec8@I",
			  "id": "bf007525-99a3-4178-a474-32865bdddec8",
			  "name": "김철수",
			  "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
			  "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
			  "unitLevelName": "란더종횡/마케팅부",
			  "unitName": "마케팅부"
			}]
		  },
		  "datagrid_datagrid$Title_2": { //데이터 그리드 2열 title 식별자
			"number": "111" //데이터 그리드 3열 필드 식별자와 값
		  },
		  "datagrid_datagrid$Title_3": { //데이터 그리드 3열 title 식별자
			"textfield_2": "항저우" //데이터 그리드 4열 필드 식별자와 값
		  },
		  "datagrid_datagrid$Title_4": { //데이터 그리드 4열 title 식별자
			"attachment_1": [  //데이터 그리드 5열 필드 식별자
			  {
				"activityName": "기안",
				"extension": "jpg",
				"id": "9514758e-9e28-4bfe-87d7-824f2811f173",
				"lastUpdateTime": "2020-12-09 21:48:03",
				"length": 452863.0,
				"name": "111.jpg",
				"person": "김철수@lisi@P"
			  }
			]
		  }
		},
		...
	  ],
	  "total": {  //통계 데이터, 열 title에 합계를 설정한 경우
		"datagrid_datagrid$Title_2": "333", //합계 열2
		"datagrid_datagrid$Title_3": "2" //합계 열3
	  }
	}
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class DatagridPC 데이터 그리드 컴포넌트(PC). v6.2부터 데이터 표(Datatable) 사용 권장.
 * @o2cn 데이터 그리드 PC(구버전)
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var datagrid = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var datagrid = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @o2range {Process|CMS}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.DatagridPC = new Class(
	/** @lends MWF.xApplication.process.Xform.DatagridPC# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 현재 항목 편집이 완료될 때 발생. this.event로 해당 tr을 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatagridPC#completeLineEdit
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 추가 시 발생. this.event로 해당 tr을 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatagridPC#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 전에 발생. this.event로 해당 tr을 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.DatagridPC#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 후 발생.
			 * @event MWF.xApplication.process.Xform.DatagridPC#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 편집 시 발생.
			 * @event MWF.xApplication.process.Xform.DatagridPC#editLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 내보내기 시 발생. this.event는 내보낼 데이터를 가리키며, this.event를 수정해 데이터를 변경할 수 있음.
			 * @event MWF.xApplication.process.Xform.DatagridPC#export
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 * @example
			 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
			 * {
			 *  	data : [
			 *   		["이름","성별","학력","전공","생년월일","졸업일"], //제목
			 *  		[ "홍길동","남","대학교 학사","컴퓨터","2001-1-2","2019-9-2" ], //첫 번째 행 데이터
			 *  		[ "김철수","남","전문대학","수학","1998-1-2","2018-9-2" ]  //두 번째 행 데이터
			 * 	], //내보낼 데이터
			 *     colWidthArray : [100, 50, 100, 200, 150, 150], //열별 너비
			 *     title : "xxxx" //내보낼 excel 파일 제목
			 * }
			 */
			/**
			 * excel 가져오기에서 데이터 검증 후 발생. this.event는 가져온 데이터를 가리킴.
			 * @event MWF.xApplication.process.Xform.DatagridPC#validImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 * @example
			 * <caption>this.event 데이터 형식은 다음과 같음:</caption>
			 * {
			 *  	data : [
			 *  	   {
			 *  	 	"이름" : "홍길동",
			 *  	 	"성별" : "남",
			 *  	 	"학력" : "대학교 학사",
			 *  	    "전공" : "컴퓨터",
			 *  	    "생년월일" : "aa01-1-2",
			 *  	 	"졸업일" : "2019-9-2",
			 *  	 	"errorTextList" : [
			 *  	 	    "5열: aa01-1-2는 올바른 날짜 형식이 아닙니다."
			 *  	 	] //검증에서 발견된 오류 정보, 해당 행 데이터가 올바르면 이 필드가 없음
			 *  	 }
			 *  	 ...
			 *     ], //가져온 데이터
			 *     "validted" : true  //검증 통과 여부, 이 이벤트에서 수정해 강제 가져오기 여부를 결정할 수 있음
			 * }
			 */
			/**
			 * excel 가져오기에서 데이터 검증에 성공해 데이터 그리드에 다시 설정하려 할 때 발생. this.event는 정리된 가져오기 데이터를 가리키며, 형식은 {@link DatagridData} 참고.
			 * @event MWF.xApplication.process.Xform.DatagridPC#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			"moduleEvents": ["queryLoad","postLoad","load","completeLineEdit", "addLine", "deleteLine", "afterDeleteLine","editLine", "export", "import", "validImport"]
		},

		initialize: function(node, json, form, options){
			/**
			 * @ignore
			 * @member parentLine
			 * @memberOf MWF.xApplication.process.Xform.DatagridPC#
			 */

			this.node = $(node);
			this.node.store("module", this);
			this.json = json;
			this.form = form;
			this.field = true;
			this.fieldModuleLoaded = false;
		},
		load: function(){
			this._loadModuleEvents();
			if (this.fireEvent("queryLoad")){
				this._queryLoaded();
				this._loadUserInterface();
				this._loadStyles();
				this._loadDomEvents();
				//this._loadEvents();

				this._afterLoaded();
				this.fireEvent("afterLoad");
				// this.fireEvent("load");
			}
		},
		_loadUserInterface: function(){
			// this.fireEvent("queryLoad");

			this.editModules = [];
			this.node.setStyle("overflow-x", "auto");
			this.node.setStyle("overflow-y", "hidden");
			this.table = this.node.getElement("table");

			this.editable = (this.readonly || this.json.isReadonly === true || this.form.json.isReadonly === true) ? false : true;
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.deleteable = this.json.deleteable !== "no";
			this.addable = this.json.addable !== "no";

			//가져오기 허용
			this.importenable  = this.editable && (this.json.impexpType === "impexp" || this.json.impexpType === "imp");
			//내보내기 허용
			this.exportenable  = this.json.impexpType === "impexp" || this.json.impexpType === "exp";

			this.gridData = this._getValue();

			if( this.gridData.data && o2.typeOf(this.gridData.data)==="object"){
				this.gridData.data = [];
			}

			this.totalModules = [];
			this._loadDatagridTitleModules();

			if (this.editable!=false){
				this._loadDatagridDataModules();
				this._addTitleActionColumn();

				this._loadEditDatagrid(function(){
					this._loadImportExportAction();
					this.fieldModuleLoaded = true;
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));
				//this._loadReadDatagrid();
			}else{
				this._loadDatagridDataModules();
				this._getDatagridEditorTr();
				this._loadReadDatagrid(function(){
					if(this.editorTr)this.editorTr.setStyle("display", "none");
					this._loadImportExportAction();
					this.fieldModuleLoaded = true;
					this.fireEvent("postLoad");
					this.fireEvent("load");
				}.bind(this));

			}
		},
		_loadStyles: function(){
			this.table.setStyles(this.json.tableStyles);
			this.node.setStyles(this.json.styles);
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = [];
			value = this._getBusinessData();
			if (!value){
				if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
				if (value && !value.then) if (o2.typeOf(value)=="array") value = {"data": value || []};
			}
			return value || {};
		},
		getValue: function(){
			if (!this.isReadable) return {};
			return this._getValue();
		},
		_getDatagridTr: function(){
			this._getDatagridTitleTr();
			this._getDatagridEditorTr();
		},
		_getDatagridTitleTr: function(){
			this.titleTr = this.table.getElement("tr");
			return this.titleTr;
		},
		_getDatagridEditorTr: function(){
			var trs = this.table.getElements("tr");
			this.editorTr = trs[trs.length-1];
			this.editorTr.addClass("datagridEditorTr");

			return this.editorTr;
		},
		_addTitleActionColumn: function(){
			if (!this.titleTr) this._getDatagridTitleTr();
			if (!this.editorTr) this._getDatagridEditorTr();

			var actionTh = new Element("th", {"styles": {"width": "46px"}}).inject(this.titleTr, "top");
			new Element("th").inject(this.titleTr, "bottom");
			if( this.addable ){
				this._createAddLineAction(actionTh);
			}
			//this._createDelLineAction(actionTh);

			var actionEditTd = new Element("td").inject(this.editorTr, "top");
			this._createCompleteAction(actionEditTd);
			if( this.deleteable ){
				this._createCancelAction(actionEditTd);
			}

			new Element("td").inject(this.editorTr, "bottom");

			//if (this.totalTr){
			//    new Element("td").inject(this.totalTr, "top");
			//    new Element("td").inject(this.totalTr, "bottom");
			//    this.totalModules.each(function(m){
			//        m.index = m.index+1;
			//    });
			//}
		},

		_loadEditDatagrid: function(callback){
			var p = o2.promiseAll(this.gridData).then(function(v){
				this.gridData = v;
				if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
				this.__loadEditDatagrid(callback);
				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			// if (this.gridData && this.gridData.isAG){
			// 	this.moduleValueAG = this.gridData;
			// 	this.gridData.addResolve(function(v){
			// 		this.gridData = v;
			// 		this._loadEditDatagrid(callback);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
			// 	this.__loadEditDatagrid(callback);
			// 	this.moduleValueAG = null;
			// }
		},
		__loadEditDatagrid: function(callback){
			var titleThs = this.titleTr.getElements("th");
			var editorTds = this.editorTr.getElements("td");

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var tr = $(this.table.insertRow(idx+1));
					tr.store("data", data);
					titleThs.each(function(th, index){
						var cellData = data[th.get("id")];
						var text = "";
						if( typeOf( cellData ) !== "array" ) {
							for (key in cellData) {
								var value = cellData[key];
								text = this._getValueText(index - 1, value);
								break;
							}
						}
						this._createNewEditTd(tr, index, editorTds[index].get("id"), text, titleThs.length-1, idx);
					}.bind(this));
				}.bind(this));
			}
			this.editorTr.setStyle("display", "none");
			if (callback) callback();
		},


		_getValueText: function(idx, value){

			var module = this.editModules[idx];
			if (module){
				switch (module.json.type){
					case "Select":
						for (var i=0; i<module.json.itemValues.length; i++){
							var itemv = module.json.itemValues[i];
							var arr = itemv.split(/\|/);
							var text = arr[0];
							var v = (arr.length>1) ? arr[1] : arr[0];
							if (value===v) return text;
						}
						// var ops = module.node.getElements("option");
						// for (var i=0; i<ops.length; i++){
						// 	if (ops[i].value == value){
						// 		return ops[i].get("text");
						// 		break;
						// 	}
						// }
						break;
					case "Radio":
						var ops = module.node.getElements("input");
						for (var i=0; i<ops.length; i++){
							if (ops[i].value == value){
								return ops[i].get("showText");
								break;
							}
						}
						break;
					case "Checkbox":
						var ops = module.node.getElements("input");
						var texts = [];
						for (var i=0; i<ops.length; i++){
							if (value.indexOf(ops[i].value) != -1) texts.push(ops[i].get("showText"));
						}
						if (texts.length) return texts.join(", ");
						break;
					case "Orgfield":
					case "Personfield":
					case "Org":
						//var v = module.getTextData();
						//return v.text[0];

						if (typeOf(value)==="array"){
							var textArray = [];
							value.each( function( item ){
								if (typeOf(item)==="object"){
									textArray.push( item.name+((item.unitName) ? "("+item.unitName+")" : "") );
								}else{
									textArray.push(item);
								}
							}.bind(this));
							return textArray.join(", ");
						}else if (typeOf(value)==="object"){
							return value.name+((value.unitName) ? "("+value.unitName+")" : "");
						}else{
							return value;
						}

						break;
					case "Textarea":
						var reg = new RegExp("\n","g");
						var reg2 = new RegExp("\u003c","g"); //꺾쇠괄호 이스케이프, 그렇지 않으면 내용이 잘림
						var reg3 = new RegExp("\u003e","g");
						value = value.replace(reg2,"&lt").replace(reg3,"&gt").replace(reg,"<br/>");
						break;
					// case "address":
					// 	if (typeOf(value)==="array"){
					//
					// 	}
					// 	break;
				}
			}
			return value;
		},

		_createNewEditTd: function(tr, idx, id, text, lastIdx, rowIndex){
			var cell = $(tr.insertCell(idx));
			if (idx==0){
				cell.setStyles(this.form.css.gridLineActionCell);
				if( this.addable )this._createAddLineAction(cell);
				if( this.deleteable )this._createDelLineAction(cell);
			}else if (idx == lastIdx){
				cell.setStyles(this.form.css.gridMoveActionCell);
				this._createMoveLineAction(cell);
			}else{
				cell.set("MWFId", id);

				var module = this.editModules[idx-1];
				if( module && module.json.type == "ImageClipper" ){
					this._createImage( cell, module, text )
				}else if( module && module.json.type == "Image" ) {
					this._createImg(cell, module, rowIndex);
				}else if( module && module.json.type == "Button" ) {
					this._createButton(cell, module, rowIndex);
				}else if( module && module.json.type == "Label" ) {
					this._createLabel(cell, module, rowIndex);
				}else if( module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg") ){
					this._createAttachment( cell, module, text );
				}else{
					if( module && module.json.type == "Textarea" ){
						cell.set("html", text);
					}else{
						cell.set("text", text);
					}
					// /cell.set("text", text);
				}
				if( !module ||  !["Button"].contains( module.json.type ) ){
					cell.addEvent("click", function(e){
						this._editLine(e.target);
					}.bind(this));
				}
			}
			var json = this.form._getDomjson(cell);

			if (json){
				cell.store("dataGrid", this);
				var module = this.form._loadModule(json, cell);
				cell.store("module", module);
				this.form.modules.push(module);
				if( json.isShow === false )cell.hide();
			}

		},

		_createAddLineAction: function(td){
			var addLineAction = new Element("div.addLineAction.ooicon-create", {
				"styles": this.form.css.addLineAction,
				"events": {
					"click": function(e){
						this._addLine(e.target);
					}.bind(this)
				}
			});
			addLineAction.inject(td);
		},
		_createDelLineAction: function(td){
			var delLineAction = new Element("div.delLineAction.ooicon-delete", {
				"styles": this.form.css.delLineAction,
				"events": {
					"click": function(e){
						this._deleteLine(e);
					}.bind(this)
				}
			});
			delLineAction.inject(td);
		},
		_createCompleteAction: function(td){
			var completeAction = new Element("div.completeLineAction.ooicon-checkmark", {
				"styles": this.form.css.completeLineAction,
				"events": {
					"click": function(e){
						this._completeLineEdit(e);
					}.bind(this)
				}
			});
			completeAction.inject(td);
		},
		_createCancelAction: function(td){
			var cancelAction = new Element("div.cancelLineEditAction.ooicon-process-cancel", {
				"styles": this.form.css.delLineAction,
				"events": {
					"click": function(e){
						this._cancelLineEdit(e);
					}.bind(this)
				}
			});
			cancelAction.inject(td);
		},

		_editLine:function(td){
			if (this.isEdit){
				if (!this._completeLineEdit()) return false;
			}

			this.currentEditLine = td.getParent("tr");
			if (this.currentEditLine){
				this.editorTr.setStyles({
					//"background-color": "#fffeb5",
					"display": "table-row"
				});
				this.editorTr.inject(this.currentEditLine, "before");
				this.currentEditLine.setStyle("display", "none");

				var data = this.currentEditLine.retrieve("data");
				var titleThs = this.titleTr.getElements("th");
				titleThs.each(function(th, idx){
					var id = th.get("id");
					var module = this.editModules[idx-1];
					if (module){
						if (module.json.type=="sequence"){
							module.node.set("text", module.node.getParent("tr").rowIndex);
						}else if( module.setData ){

							if (data[id]) {
								module.setData(data[id][module.json.id]);
							} else {
								module.setData(null);
							}
						}
					}
				}.bind(this));



				var cellIdx = this.currentEditLine.getElements("td").indexOf(td);
				var module = this.editModules[cellIdx-1];
				if (module) module.focus();


				this.fireEvent("editLine");

				this.isEdit =true;
			}
			this.validationMode();
		},
		editValidation: function(){
			var flag = true;
			this.editModules.each(function(field, key){
				if (field.json.type!="sequence" && field.validationMode ){
					field.validationMode();
					if (!field.validation()) flag = false;
				}
			}.bind(this));
			return flag;
		},

		// _cancelLineEdit: function(e){
		// 	this.isEdit = false;
		//
		// 	var flag = true;
		//
		// 	var griddata = {};
		// 	var newTr = null;
		//
		// 	if (this.currentEditLine){
		// 		newTr = this.currentEditLine;
		// 		griddata = this.currentEditLine.retrieve("data");
		// 	}else{
		// 		newTr = new Element("tr").inject(this.editorTr, "before");
		// 		griddata = {};
		// 	}
		//
		// 	if (flag){
		// 		newTr.destroy();
		// 	}
		// 	this.currentEditLine = null;
		//
		// 	this._editorTrGoBack();
		//
		// 	// if (this.json.contentStyles){
		// 	// 	var tds = newTr.getElements("td");
		// 	// 	tds.setStyles(this.json.contentStyles);
		// 	// }
		// 	// if (this.json.actionStyles){
		// 	// 	newTr.getFirst().setStyles(this.json.actionStyles);
		// 	// }
		//
		// 	// this._loadBorderStyle();
		// 	// this._loadZebraStyle();
		// 	// this._loadSequence();
		//
		// 	this.fireEvent("cancelLineEdit");
		// },
		_cancelLineEdit: function(e){

			var datagrid = this;
			this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.cancelDatagridLineEditTitle, MWF.xApplication.process.Xform.LP.cancelDatagridLineEdit, 300, 120, function(){
				if (datagrid.currentEditLine) {
					datagrid.currentEditLine.setStyle("display", "table-row");
				}

				datagrid.editModules.each(function(module){
					if (module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
						module.attachmentController.attachments.each(function(att){
							datagrid.form.workAction.deleteAttachment(att.data.id, datagrid.form.businessData.work.id);
						});
						module.attachmentController.clear();
					}
				});

				datagrid.isEdit = false;
				datagrid.currentEditLine = null;

				datagrid._editorTrGoBack();

				// this._loadBorderStyle();
				// this._loadZebraStyle();
				// this._loadSequence();
				// this.getData();

				// datagrid._loadZebraStyle();
				// datagrid._loadSequence();
				// datagrid._loadTotal();
				// datagrid.getData();
				this.close();

				datagrid.fireEvent("cancelLineEdit");
			}, function(){
				// var color = currentTr.retrieve("bgcolor");
				// currentTr.tween("background", color);
				this.close();
			}, null, null, this.form.json.confirmStyle);

		},
		_completeLineEdit: function( ev ){
			//this.currentEditLine.getElemets(td);
			if (!this.editValidation()){
				return false;
			}

			this.isEdit = false;

			var flag = true;
			var saveFlag = false;

			var griddata = {};
			var newTr = null;

			if (this.currentEditLine){
				newTr = this.currentEditLine;
				griddata = this.currentEditLine.retrieve("data");
			}else{
				newTr = new Element("tr").inject(this.editorTr, "before");
				griddata = {};
			}

			var titleThs = this.titleTr.getElements("th");
			var editorTds = this.editorTr.getElements("td");
			var cells = newTr.getElements("td");
			titleThs.each(function(th, idx){
				var cell = cells[idx];
				var id = th.get("id");
				var module = this.editModules[idx-1];
				if (module){
					if (module.json.type=="sequence"){
						flag = false;
						var i = newTr.rowIndex;
						var data = {"value": [i], "text": [i]};
					}else if (module.json.type=="Attachment" || module.json.type == "AttachmentDg"){
						saveFlag = true;
						flag = false;
						var data = module.getTextData();
						//data.site = module.json.site;
						if (!griddata[id]) griddata[id] = {};
						griddata[id][module.json.id] = data;
						// }else if( ["Orgfield","Personfield","Org","Address"].contains(module.json.type) ){
						// 	data = module.getTextData();
						// 	if( data.value && data.value.length )flag = false;
						// 	if (!griddata[id]) griddata[id] = {};
						// 	griddata[id][module.json.id] = data.value;
					}else if( module.getTextData ){
						var data = module.getTextData();
						if (data.value[0]) flag = false;
						if (data.value.length<2){
							if (!griddata[id]) griddata[id] = {};
							griddata[id][module.json.id] = data.value[0];
						}else{
							if (!griddata[id]) griddata[id] = {};
							griddata[id][module.json.id] = data.value;
						}
					}

					if( data ){
						if (cell){
							if( module.json.type == "ImageClipper" ){
								this._createImage( cell, module, data.text[0] );
							}else if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
								this._createAttachment( cell, module, data );
							}else{
								var text = this._getValueText(idx-1, data.text.join(", "));
								if( module.json.type == "Textarea"){
									cell.set("html", text);
								}else{
									cell.set("text", data.text.join(", "));
								}
							}
						}else{
							if( module.json.type == "Attachment" || module.json.type == "AttachmentDg" ){
								this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), data, titleThs.length-1);
							}else{
								var text = this._getValueText(idx-1, data.text.join(", "));
								this._createNewEditTd(newTr, idx, editorTds[idx].get("id"), text, titleThs.length-1);
							}
						}
					}else{
						if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
					}
				}else{
					if (!cell) this._createNewEditTd(newTr, idx, id, "", titleThs.length-1);
				}
				module = null;
			}.bind(this));

			newTr.store("data", griddata);
			newTr.setStyle("display", "table-row");

			if (flag){
				newTr.destroy();
			}
			this.currentEditLine = null;

			this._editorTrGoBack();

			if (this.json.contentStyles){
				var tds = newTr.getElements("td");
				tds.setStyles(this.json.contentStyles);
			}
			if (this.json.actionStyles){
				newTr.getFirst().setStyles(this.json.actionStyles);
			}

			this._loadBorderStyle();
			this._loadZebraStyle();
			this._loadSequence();
			this.getData();
			this.validationMode();
			this.fireEvent("completeLineEdit", [newTr]);

			if( saveFlag ){
				this.form.saveFormData();
			}

			return true;
		},
		// _createImg : function(cell, module, idx){
		// 	cell.empty();
		// 	if( module.node ){
		// 		var node = module.node.clone();
		// 		node.set("id", module.json ? (module.json.id +"_"+idx) : "" );
		// 		node.inject(cell);
		// 	}
		// },
		_createImg : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_createLabel : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_createButton : function(cell, module, idx){
			this._cloneModule(cell, module, idx);
		},
		_cloneModule : function(cell, module, idx){
			cell.empty();
			if( module.node && module.json ){
				var json = Object.clone( module.json );
				json.id = json.id +"_"+idx;

				var node = module.node.clone();
				node.set("id", json.id);
				node.inject(cell);

				this.form._loadModule(json, node)
			}
		},
		_createImage : function( cell, module, data ){
			cell.empty();
			if( !data )return;
			var img = new Element("img",{
				src : MWF.xDesktop.getImageSrc( data )
			}).inject( cell, "top" );
			if( module.json.clipperType == "size" ){
				var width = module.json.imageWidth;
				var height = module.json.imageHeight;
				if (width && height) {
					img.setStyles({
						width: width + "px",
						height: height + "px"
					})
				}
			}
		},
		_createAttachment: function ( cell, module, data ){
			cell.empty();
			var options = {
				"style": module.json.style || "default",
				"title": MWF.xApplication.process.Xform.LP.attachmentArea,
				"listStyle": module.json.dg_listStyle || "icon",
				"size": module.json.dg_size || "min",
				"resize": (module.json.dg_resize === "y" || this.json.dg_resize === "true"),
				"attachmentCount": 0,
				"isUpload": false,
				"isDelete": false,
				"isReplace": false,
				"isDownload": true,
				"isSizeChange": (module.json.dg_isSizeChange === "y" || module.json.dg_isSizeChange === "true"),
				"readonly": true,
				"availableListStyles": module.json.dg_availableListStyles ? module.json.dg_availableListStyles : ["list", "seq", "icon", "preview"],
				"isDeleteOption": "n",
				"isReplaceOption": "n",
				"toolbarGroupHidden": module.json.dg_toolbarGroupHidden || []
			};
			if (this.readonly) options.readonly = true;
			if(!this.editable && !this.addable)options.readonly = true;

			var atts = [];
			( data || [] ).each(function(d){
				var att = module.attachmentController.attachments.find(function(a){
					return d.id == a.data.id;
				});
				if (att) module.attachmentController.removeAttachment(att);
			});
			module.setAttachmentBusinessData();


			var attachmentController = new MWF.xApplication.process.Xform.AttachmentController(cell, module, options);
			attachmentController.load();

			( data || [] ).each(function (att) {
				var attachment = this.form.businessData.attachmentList.find(function(a){
					return a.id==att.id;
				});
				var attData = attachment || att;
				//if (att.site===this.json.id || (this.json.isOpenInOffice && this.json.officeControlName===att.site)) this.attachmentController.addAttachment(att);
				attachmentController.addAttachment(attData);
			}.bind(this));
		},
		_editorTrGoBack: function(){
			this.editorTr.setStyle("display", "none");
//		this.editTr.removeEvents("blur");
			if (this.totalTr){
				this.editorTr.inject(this.totalTr, "before");
			}else{
				var lastTrs = this.table.getElements("tr");
				var lastTr = lastTrs[lastTrs.length-1];
				this.editorTr.inject(lastTr, "after");
			}
		},
		_addLine: function(node){
			if (this.isEdit){
				if (!this._completeLineEdit()) return false;
			}
			this.editorTr.setStyles({
				//"background-color": "#fffeb5",
				"display": "table-row"
			});
			this.currentEditLine = null;
			var currentTr = node.getParent("tr");
			if (currentTr){
				this.editorTr.inject(currentTr, "after");
			}
			this.isEdit =true;

			this._loadSequence();
			this.validationMode();
			this.fireEvent("addLine",[this.editorTr]);
//		newTr.addEvent("blur", function(e){
//			this._completeLineEdit();
//		}.bind(this));
		},
		_deleteLine: function(e){
			var saveFlag = false;
			var currentTr = e.target.getParent("tr");
			if (currentTr){
				var color = currentTr.getStyle("background");
				currentTr.store("bgcolor", color);
				currentTr.tween("background-color", "#ffd4d4");
				var datagrid = this;
				var _self = this;
				this.form.confirm("warn", e, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
					_self.fireEvent("deleteLine", [currentTr]);

					var data = currentTr.retrieve("data");

					//var attKeys = [];
					var titleThs = _self.titleTr.getElements("th");
					titleThs.each(function(th, i){
						var key = th.get("id");
						var module = (i>0) ? _self.editModules[i-1] : null;
						if (key && module && (module.json.type=="Attachment" || module.json.type=="AttachmentDg")){
							data[key][module.json.id].each(function(d){
								_self.form.workAction.deleteAttachment(d.id, _self.form.businessData.work.id);
							});
							saveFlag = true;
						}
					});

					currentTr.destroy();
					datagrid._loadZebraStyle();
					datagrid._loadSequence();
					datagrid._loadTotal();
					datagrid.getData();
					this.close();

					_self.fireEvent("afterDeleteLine");

					if(saveFlag){
						_self.form.saveFormData();
					}
				}, function(){
					var color = currentTr.retrieve("bgcolor");
					currentTr.tween("background", color);
					this.close();
				}, null, null, this.form.json.confirmStyle);
			}
			this.validationMode();
		},
		_createMoveLineAction: function(td){
			var moveLineAction = new Element("div", {
				"styles": this.form.css.moveLineAction,
				"events": {
					"mousedown": function(e){
						this._moveLine(e);
					}.bind(this)
				}
			});
			moveLineAction.inject(td);
		},
		_getMoveDragNode: function(tr){
			var table = tr.getParent("table");
			var div = table.getParent("div");
			var size = div.getSize();
			var dragNode = div.clone().setStyle("width", size.x).inject(document.body);
			var dragtable = dragNode.getElement("table");
			dragtable.empty();

			var clone = tr.clone().setStyles(this.form.css.gridMoveLineDragNodeTr).inject(dragtable);
			var tds = tr.getElements("td");
			var clonetds = clone.getElements("td");
			tds.each(function(td, idx){
				var size = td.getComputedSize();
				clonetds[idx].setStyle("width", size.width+1);
			});

			var coordinates = tr.getCoordinates();
			dragNode.setStyles(this.form.css.gridMoveLineDragNode);
			dragNode.setStyles(coordinates);

			return dragNode;
		},
		_moveLine: function(e){
			var trs = this.table.getElements("tr");
			var div = e.target;
			var tr = div.getParent("tr");

			var dragNode = this._getMoveDragNode(tr);
			coordinates = dragNode.getCoordinates();
			var dragTr = dragNode.getElement("tr");
			var dragTable = dragNode.getElement("table");

			var color = tr.getStyle("background");
			tr.store("bgcolor", color);
			//tr.tween('background-color', '#f3f1ad');
			tr.tween('background-color', '#e4f6e9');


			var drag = new Drag.Move(dragNode, {
				"droppables": trs.erase(tr),
				"limit": {"x": [coordinates.left, coordinates.left]},
				onDrop: function(dragging, droppable){
					dragging.destroy();

					var color = tr.retrieve("bgcolor");
					if (color){
						tr.tween("background", color);
					}else{
						tr.tween("background", "transparent");
					}
					//if (droppable){
					//    color = droppable.retrieve("bgcolor");
					//    if (color){
					//        droppable.tween("background", color);
					//    }else{
					//        droppable.tween("background", "transparent");
					//    }
					//}

					tr.setStyle("display", "table-row");
					if (droppable != null){
						tr.inject(dragTr, "after");
						dragTr.destroy();
						//this._loadZebraStyle();
						//this._loadSequence();
						//this._loadTotal();
						this._loadDatagridStyle();
						this.getData();
					}

				}.bind(this),
				"onEnter": function(dragging, drop){
					//var color = drop.getStyle("background");
					//if (color.toUpperCase()!='#d1eaf3') drop.store("bgcolor", color);
					//drop.tween("background-color", "#d1eaf3");
					dragNode.setStyle("display", "none");
					dragTr.inject(drop, "after");
				},
				"onLeave": function(dragging, drop){
					//var color = drop.retrieve("bgcolor");
					//if (color){
					//	drop.tween("background", color);
					//}else{
					//	drop.tween("background", "transparent");
					//}
					dragTr.inject(dragTable);
					dragNode.setStyle("display", "block");
//				tr.setStyle("display", "table-row");
				},
				"ondrag": function(){
					this.table.setStyle("cursor", "move");
					dragNode.setStyle("cursor", "move");
				},
				"onCancel": function(dragging){
					dragging.destroy();
					var color = tr.retrieve("bgcolor");
					if (color){
						tr.tween("background", color);
					}else{
						tr.tween("background", "transparent");
					}
					tr.setStyle("display", "table-row");
				}
			});
			drag.start(e);
			tr.setStyle("display", "none");
		},
		_loadReadDatagrid: function(callback){
			var p = o2.promiseAll(this.gridData).then(function(v){
				this.gridData = v;
				if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
				this.__loadReadDatagrid(callback);
				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));

			// if (this.gridData && this.gridData.isAG){
			// 	this.moduleValueAG = this.gridData;
			// 	this.gridData.addResolve(function(v){
			// 		this.gridData = v;
			// 		this._loadReadDatagrid(callback);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(this.gridData)=="array") this.gridData = {"data": this.gridData};
			// 	this.__loadReadDatagrid(callback);
			// 	this.moduleValueAG = null;
			// }
		},

		__loadReadDatagrid: function(callback){
			//this.gridData = this._getValue();
			if (!this.titleTr) this._getDatagridTitleTr();
			//var titleTr = this.table.getElement("tr");
			var titleHeaders = this.titleTr.getElements("th");

			var lastTrs = this.table.getElements("tr");
			var lastTr = lastTrs[lastTrs.length-1];
			//var tds = lastTr.getElements("td");

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var tr = this.table.insertRow(idx+1);
					tr.store("data", data);

					titleHeaders.each(function(th, index){
						var cell = tr.insertCell(index);
						// cell.set("MWFId", tds[index].get("id"));
						var cellData = data[th.get("id")];

						var module = this.editModules[index];
						if( typeOf( cellData ) !== "array" ){
							if (cellData) {
								for (key in cellData) {
									var v = cellData[key];
									if (module && module.json.type == "ImageClipper") {
										this._createImage(cell, module, v);
									} else if (module && (module.json.type == "Attachment" || module.json.type == "AttachmentDg")) {
										this._createAttachment(cell, module, v);
									} else {
										var text = this._getValueText(index, v);
										if (module && module.json.type == "Textarea") {
											cell.set("html", text);
										} else {
											cell.set("text", text);
										}
									}
									break;
								}
							}else if( module && module.json.type == "Image" ) {
								this._createImg(cell, module, idx);
							}else if( module && module.json.type == "Button" ) {
								this._createButton(cell, module, idx);
							}else if( module && module.json.type == "Label" ) {
								this._createLabel(cell, module, idx);
							}else if( module && module.json.type == "sequence" ){ //Sequence
								cell.setStyle("text-align", "center");
								cell.set("text", tr.rowIndex);
							}
						}


						var json = this.form._getDomjson(th);
						if( json && json.isShow === false )cell.hide();
					}.bind(this));
				}.bind(this));
			}
			this._loadTotal();

			if (callback) callback();
		},

		_loadImportExportAction: function(){
			this.impexpNode = this.node.getElement("div.impexpNode");
			if( this.impexpNode )this.impexpNode.destroy();
			this.impexpNode = null;

			if( !this.exportenable && !this.importenable )return;

			var position = ["leftTop","centerTop","rightTop"].contains( this.json.impexpPosition || "" ) ? "top" : "bottom";
			var container = new Element("div").inject(this.node, position);

			this.importExportAreaNode = new Element("div").inject( container );
			if( ["leftTop","leftBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "left" })
			}else if( ["rightTop","rightBottom"].contains( this.json.impexpPosition || "" ) ){
				this.importExportAreaNode.setStyles({ "float" : "right" })
			}else{
				this.importExportAreaNode.setStyles({ "margin" : "0px auto" })
			}

			if( this.exportenable ){
				this.exportActionNode = new Element("div", {
					text : this.json.exportActionText || MWF.xApplication.process.Xform.LP.datagridExport
				}).inject(this.importExportAreaNode);
				var styles;
				if( this.json.exportActionStyles ){
					styles = this.json.exportActionStyles
				}else{
					styles = this.form.css.gridExportActionStyles;
				}
				this.exportActionNode.setStyles(styles);

				this.exportActionNode.addEvent("click", function () {
					this.exportToExcel();
				}.bind(this))
			}

			if( this.importenable ){
				this.importActionNode = new Element("div", {
					text : this.json.importActionText || MWF.xApplication.process.Xform.LP.datagridImport
				}).inject(this.importExportAreaNode);
				var styles;
				if( this.json.importActionStyles ){
					styles = this.json.importActionStyles;
				}else{
					styles = this.form.css.gridImportActionStyles;
				}
				this.importActionNode.setStyles(styles);

				this.importActionNode.addEvent("click", function () {
					this.importFromExcel();
				}.bind(this))
			}

			if( ["centerTop","centerBottom"].contains( this.json.impexpPosition ) ){
				var width = 2;

				if( this.exportActionNode ){
					width = width + this.exportActionNode.getSize().x +
						this.exportActionNode.getStyle("padding-left").toFloat() +
						+ this.exportActionNode.getStyle("padding-right").toFloat() +
						+ this.exportActionNode.getStyle("margin-left").toFloat() +
						+ this.exportActionNode.getStyle("margin-right").toFloat()
				}

				if( this.importActionNode ){
					width = width + this.importActionNode.getSize().x +
						this.importActionNode.getStyle("padding-left").toFloat() +
						+ this.importActionNode.getStyle("padding-right").toFloat() +
						+ this.importActionNode.getStyle("margin-left").toFloat() +
						+ this.importActionNode.getStyle("margin-right").toFloat()
				}

				this.importExportAreaNode.setStyle( "width", width+"px" );
			}
		},

		_loadDatagridStyle: function(){
			//var ths = this.titleTr.getElements("th");
			//ths.setStyles(this.form.css.datagridTitle);
			this.loadGridTitleStyle();
			this.loadGridContentStyle();
			this.loadGridActionStyle();
			this.loadGridEditStyle();

			this._loadTotal();
			this._loadBorderStyle();
			this._loadZebraStyle();
			this._loadSequence();
		},
		loadGridEditStyle: function(){
			if (this.editorTr){
				if (this.json.editStyles){
					var tds = this.editorTr.getElements("td");
					tds.setStyles(this.json.editStyles);
				}
			}
		},
		loadGridActionStyle: function(){
			if (this.editable!=false){
				if (this.json.actionStyles){
					var trs = this.table.getElements("tr");
					trs.each(function(tr, idx){
						if (idx != 0) tr.getFirst().setStyles(this.json.actionStyles);
					}.bind(this));
				}
			}
		},
		loadGridTitleStyle: function(){
			if (this.json.titleStyles){
				var ths = this.titleTr.getElements("th");
				ths.setStyles(this.json.titleStyles);
			}
		},
		loadGridContentStyle: function(){
			if (this.json.contentStyles){
				var tds = this.table.getElements("td");
				tds.setStyles(this.json.contentStyles);
			}
		},

		_loadZebraStyle: function(){
			var trs = this.table.getElements("tr");
			for (var i=1; i<trs.length; i++){
				if (!trs[i].hasClass("datagridTotalTr")){
					if (this.json.backgroundColor) trs[i].setStyle("background-color", this.json.backgroundColor);
					if ((i%2)==0){
						if (this.json.zebraColor) trs[i].setStyle("background-color", this.json.zebraColor);
					}
				}
			}
		},
		createTotalTr: function(){
			var trs = this.node.getElements("tr");
			var lastTr = trs[trs.length-1];
			this.totalTr = new Element("tr.datagridTotalTr", {"styles": this.form.css.datagridTotalTr}).inject(lastTr, "after");
			var ths = this.node.getElements("th");
			ths.each(function(th, idx){
				var td = new Element("td", {"text": "", "styles": this.form.css.datagridTotalTd}).inject(this.totalTr);
				if (this.json.amountStyles) td.setStyles(this.json.amountStyles);

				var json = this.form._getDomjson(th);
				if( json && json.isShow === false )td.hide();
			}.bind(this));
		},
		_loadTotal: function(){
			var data = {};
			this.totalResaults = {};
			if (this.totalModules.length){
				if (!this.totalTr){
					this.createTotalTr();
				}

				var totalResaults = [];
				//this.totalModules.each(function(m. i){
				//    totalResaults.push(0);
				//}.bind(this));

				var trs = this.table.getElements("tr");
				var totalTds = this.totalTr.getElements("td");

				for (var i=1; i<trs.length; i++){
					if (!trs[i].hasClass("datagridTotalTr") && (!trs[i].hasClass("datagridEditorTr"))){
						var cells = trs[i].getElements("td");

						this.totalModules.each(function(m, i){
							if (!totalResaults[i]) totalResaults.push(0);
							var tmpV = new Decimal(totalResaults[i]);
							if (m.type=="number"){
								var cell = cells[m.index];
								var addv = ( cell.get("text") || "0" ).toFloat();
								tmpV = tmpV.plus(addv||0);
								//tmpV = tmpV + addv;
							}
							if (m.type=="count"){
								tmpV = tmpV.plus(1);
								//tmpV = tmpV+1;
							}
							totalResaults[i] = tmpV.toString();
							data[m.module.json.id] = totalResaults[i];

						}.bind(this));
					}
				}

				this.totalModules.each(function(m, i){
					this.totalResaults[m.module.json.id] = totalResaults[i];
					var td = totalTds[m.index];
					td.set("text", isNaN( totalResaults[i] ) ? "" : totalResaults[i] );
				}.bind(this));
			}
			return data;
		},
		_loadSequence: function(){
			var trs = this.table.getElements("tr");
			for (var i=1; i<trs.length; i++){
				var cells = trs[i].getElements("td");
				cells.each(function(cell){
					var module = cell.retrieve("module");
					if (module){
						if (module.json.cellType=="sequence"){
							cell.set("text", i)
						}
					}
				}.bind(this));
			}
		},

		_loadBorderStyle: function(){
			if (this.json.border){
				this.table.setStyles({
					"border-top": this.json.border,
					"border-left": this.json.border
				});
				var ths = this.table.getElements("th");
				ths.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border
				});
				var tds = this.table.getElements("td");
				tds.setStyles({
					"border-bottom": this.json.border,
					"border-right": this.json.border,
					"background": "transparent"
				});
			}
		},
		_loadDatagridTitleModules: function(){
			var ths = this.node.getElements("th");
			ths.each(function(th){
				var json = this.form._getDomjson(th);
				th.store("dataGrid", this);
				if (json){
					var module = this.form._loadModule(json, th);
					this.form.modules.push(module);
					if( json.isShow === false )th.hide();
				}
			}.bind(this));
		},
		_loadDatagridDataModules: function(){
			var tds = this.node.getElements("td");
			tds.each(function(td){
				var json = this.form._getDomjson(td);
				td.store("dataGrid", this);
				if (json){
					var isField = false;
					var module = this.form._loadModule(json, td, function(){
						isField = this.field;
						this.field = false;
					});
					if( isField ){
						module.node.setStyle("padding-right","0px");
					}
					td.store("module", module);
					this.form.modules.push(module);
					if( json.isShow === false )td.hide();
				}
			}.bind(this));
		},
		_afterLoaded: function(){
			if (this.moduleValueAG){
				this.moduleValueAG.then(function(){
					this._loadDatagridStyle();
				}.bind(this));
			}else{
				this._loadDatagridStyle();
			}
		},
		// /**
		//  * @summary 데이터 그리드 값을 기본값으로 재설정하거나 비움.
		//  *  @example
		//  * this.form.get('fieldId').resetData();
		//  */
		resetData: function(){
			this.setData(this._getValue());
		},
		/**매개변수가 Promise인 경우 문서 참고: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|Promise로 양식 비동기 처리}<br/>
		 * 양식에 해당 컴포넌트가 없으면 this.data[fieldId] = data로 값을 설정할 수 있음.
		 * @summary 데이터 그리드에 값 설정.
		 * @param data{DatagridData|Promise|Array} 필수, 배열 또는 Promise.
		 * @example
		 *  this.form.get("fieldId").setData([]); //빈 값 설정
		 * @example
		 *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
		 *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
		 *      this.form.get('fieldId').setData( data );
		 *  }else{
		 *      this.data['fieldId'] = data;
		 *  }
		 *@example
		 *  //Promise 사용
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //비동기 요청 시작
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //load 이벤트 바인딩
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //데이터가 data.json에 있다고 가정
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //이 시점에는 비동기 요청이 이미 완료되어 getData 메서드로 data.json의 값을 얻음
		 * })
		 *  field.setData( promise );
		 */
		setData: function(data){
			if (!data){
				data = this._getValue();
			}
			this._setData(data);
		},
		_setData: function(data){
			var p = o2.promiseAll(this.data).then(function(v){
				this.gridData = v;
				if (o2.typeOf(data)=="array") data = {"data": data};
				this.__setData(data);
				this.moduleValueAG = null;
				return v;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));
			this.moduleValueAG = p;
			if (this.moduleValueAG) this.moduleValueAG.then(function(){
				this.moduleValueAG = null;
			}.bind(this), function(){
				this.moduleValueAG = null;
			}.bind(this));

			// if (data && data.isAG){
			// 	this.moduleValueAG = data;
			// 	data.addResolve(function(v){
			// 		this._setData(v);
			// 	}.bind(this));
			// }else{
			// 	if (o2.typeOf(data)=="array") data = {"data": data};
			// 	this.__setData(data);
			// 	this.moduleValueAG = null;
			// }
		},
		__setData: function(data){
			// if( typeOf( data ) === "object" && typeOf(data.data) === "array"  ){
			this._setBusinessData(data);
			this.gridData = data;

			// if (this.isEdit) this._completeLineEdit();
			if( this.isEdit ){ //편집 중인 행이 있으면 편집 취소
				if (this.currentEditLine) {
					this.currentEditLine.setStyle("display", "table-row");
				}
				this.isEdit = false;
				this.currentEditLine = null;
				this._editorTrGoBack();
			}

			if (this.gridData){
				var trs = this.table.getElements("tr");
				for (var i=1; i<trs.length-1; i++){
					var tr = trs[i];
					if( tr.hasClass("datagridEditorTr") )continue;
					var tds = tr.getElements("td");
					for (var j=0; j<tds.length; j++){
						var td = tds[j];
						var module = td.retrieve("module");
						if (module){
							this.form.modules.erase(module);
							module = null;
						}
					}
				}
				for (var i=1; i<trs.length-1; i++){
					if( trs[i].hasClass("datagridTotalTr") )continue;
					if( trs[i].hasClass("datagridEditorTr") )continue;
					trs[i].destroy();
				}
				//while (this.table.rows.length>2){
				//this.table.rows[1].destroy();
				//}
				if (this.editable!=false){
					this._loadEditDatagrid();
					//this._loadReadDatagrid();
				}else{
					this._loadReadDatagrid();
				}
				this._loadDatagridStyle();
			}
		},
		/**
		 * @summary 합계 데이터 조회.
		 * @example
		 * var totalObject = this.form.get('fieldId').getTotal();
		 * @return {Object} 합계 데이터
		 */
		getTotal: function(){
			this._loadTotal();
			return this.totalResaults;
		},
		/**
		 * @summary 데이터 그리드가 비어 있는지 판단.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('최소 한 건의 데이터를 추가해야 합니다', 'warn');
		 * }
		 * @return {Boolean} 비어 있는지 여부
		 */
		isEmpty: function(){
			var data = this.getData();
			if( !data )return true;
			if( typeOf( data ) === "object" ){
				if( typeOf( data.data ) !== "array" )return true;
				if( data.data.length === 0 )return true;
			}
			return false;
		},

		/**
		 * 스크립트에서 this.data[fieldId]로도 컴포넌트 값을 가져올 수 있음.
		 * 차이는 다음과 같음:<br/>
		 * 1. Promise를 사용하는 경우<br/>
		 * 비동기 함수 생성기(Promise)로 컴포넌트에 값을 할당할 때 getData 메서드로 즉시 가져오면 수정 전 값이 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * this.data[fieldId]로 즉시 가져오면 비동기 함수 생성기가 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|자세한 차이는 링크 참고}<br/>
		 * 2. 양식에 해당 컴포넌트가 없을 때는 this.data[fieldId]로 값을 가져올 수 있지만, this.form.get('fieldId')로는 컴포넌트를 가져올 수 없음.
		 * @summary 데이터 그리드 데이터 조회.
		 * @example
		 * var data = this.form.get('fieldId').getData();
		 *@example
		 *  //양식에 컴포넌트가 있는지 확실하지 않으면 판단 필요
		 *  var data;
		 *  if( this.form.get('fieldId') ){ //양식에 해당 컴포넌트가 있는지 판단
		 *      data = this.form.get('fieldId').getData();
		 *  }else{
		 *      data = this.data['fieldId']; //데이터에서 필드 값을 직접 가져오기
		 *  }
		 *  @example
		 *  //Promise 사용
		 *  var field = this.form.get("fieldId");
		 *  var promise = new Promise(function(resolve, reject){ //비동기 요청 시작
		 *    var oReq = new XMLHttpRequest();
		 *    oReq.addEventListener("load", function(){ //load 이벤트 바인딩
		 *      resolve(oReq.responseText);
		 *    });
		 *    oReq.open("GET", "/data.json"); //데이터가 data.json에 있다고 가정
		 *    oReq.send();
		 *  });
		 *  promise.then( function(){
		 *    var data = field.getData(); //이 시점에는 비동기 요청이 이미 완료되어 getData 메서드로 data.json의 값을 얻음
		 * })
		 *  field.setData( promise );
		 * @return {DatagridData}
		 */
		getData: function(){
			if (this.editable!=false){
				if (this.isEdit) this._completeLineEdit();
				var data = [];
				var trs = this.table.getElements("tr");
				for (var i=1; i<trs.length-1; i++){
					var tr = trs[i];
					var d = tr.retrieve("data");
					if (d) data.push(d);
				}

				this.gridData = {};
				this.gridData.data = data;

				this._loadTotal();
				this.gridData.total = this.totalResaults;

				this._setBusinessData(this.gridData);

				return (this.gridData.data.length) ? this.gridData : {data:[]};
			}else{
				return this._getBusinessData();
			}
		},
		getAmount: function(){
			return this._loadTotal();
		},
		createErrorNode: function(text){
			node = new Element("div", {styles:{
                "margin-top": "0.3em"  
            }});
            var iconNode = new Element("div.ooicon-error", {
                "styles": {
                    "width": "20px",
                    "height": "1.2em",
                    "float": "left",
                    "display": "flex",
					"color": "red",
                    "align-items": "center",
                    "justify-content": "center"
                    // "background": "url("+"../x_component_process_Xform/$Form/default/icon/error.png) center center no-repeat"
                }
            }).inject(node);
            var textNode = new Element("div", {
                "styles": {
                    "height": "auto",
                    "line-height": "1.2em",
                    "margin-left": "20px",
                    "color": "red",
                    "word-break": "keep-all"
                },
                "text": text
            }).inject(node);
			return node;
		},
		notValidationMode: function(text){
			if (!this.isNotValidationMode){
				this.isNotValidationMode = true;
				this.node.store("borderStyle", this.node.getStyles("border-left", "border-right", "border-top", "border-bottom"));
				this.node.setStyle("border", "1px solid red");

				this.errNode = this.createErrorNode(text).inject(this.node, "after");
				this.showNotValidationMode(this.node);
			}
		},
		showNotValidationMode: function(node){
			var p = node.getParent("div");
			if (p){
				if (p.get("MWFtype") == "tab$Content"){
					if (p.getParent("div").getStyle("display")=="none"){
						var contentAreaNode = p.getParent("div").getParent("div");
						var tabAreaNode = contentAreaNode.getPrevious("div");
						var idx = contentAreaNode.getChildren().indexOf(p.getParent("div"));
						var tabNode = tabAreaNode.getLast().getFirst().getChildren()[idx];
						tabNode.click();
						p = tabAreaNode.getParent("div");
					}
				}
				this.showNotValidationMode(p);
			}
		},
		validationMode: function(){
			if (this.isNotValidationMode){
				this.isNotValidationMode = false;
				this.node.setStyles(this.node.retrieve("borderStyle"));
				if (this.errNode){
					this.errNode.destroy();
					this.errNode = null;
				}
			}
		},

		validationConfigItem: function(routeName, data){
			var flag = (data.status=="all") ? true: (routeName == data.decision);
			if (flag){
				var n = this.getData();
				if( typeOf(n)==="object" && JSON.stringify(n) === JSON.stringify({data:[]}) )n = "";
				var v = (data.valueType=="value") ? n : n.length;
				switch (data.operateor){
					case "isnull":
						if (!v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notnull":
						if (v){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "gt":
						if (v>data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "lt":
						if (v<data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "equal":
						if (v==data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "neq":
						if (v!=data.value){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "contain":
						if (v.indexOf(data.value)!=-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
					case "notcontain":
						if (v.indexOf(data.value)==-1){
							this.notValidationMode(data.prompt);
							return false;
						}
						break;
				}
			}
			return true;
		},
		validationConfig: function(routeName, opinion){
			if (this.json.validationConfig){
				if (this.json.validationConfig.length){
					for (var i=0; i<this.json.validationConfig.length; i++) {
						var data = this.json.validationConfig[i];
						if (!this.validationConfigItem(routeName, data)) return false;
					}
				}
				return true;
			}
			return true;
		},
		validation: function(routeName, opinion){
			if (this.isReadonly() || this.json.showMode==="disabled" || this.node?.isDisplayNone() || !this.isEditable) return true;
			
			if (this.isEdit){
				if (!this.editValidation()){
					return false;
				}
			}
			if (!this.validationConfig(routeName, opinion))  return false;

			if (!this.json.validation) return true;
			if (!this.json.validation.code) return true;

			this.currentRouteName = routeName;
			var flag = this.form.Macro.exec(this.json.validation.code, this);
			this.currentRouteName = "";

			if (!flag) flag = MWF.xApplication.process.Xform.LP.notValidation;
			if (flag.toString()!="true"){
				this.notValidationMode(flag);
				return false;
			}
			return true;
		},
		getAttachmentRandomSite: function(){
			var i = (new Date()).getTime();
			return this.json.id+i;
		},


		isAvaliableImpExpColumn : function(thJson, module, type){
			if (thJson && ( thJson.isShow === false || thJson.isImpExp === false ))return false; //숨김 열, 가져오기/내보내기 불가
			if (module && (module.json.type == "sequence" || module.json.cellType == "sequence") )return false; //순번 열
			if (module && ["Image","Button","ImageClipper","Attachment","AttachmentDg","Label"].contains(module.json.type) )return false; //이미지, 첨부파일, Label 열은 가져오기/내보내기 안 함
			// if (type==="import" && module && ["Label"].contains(module.json.type))return false; //Label은 가져오기 안 함
			return true;
		},
		getExportColWidthArray : function(){
			var titleThs = this.titleTr.getElements("th");
			var colWidthArr = [];
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열

				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
					if (module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)) {
						colWidthArr.push(340);
					} else if (module && module.json.type === "Address") {
						colWidthArr.push(170);
					} else if (module && module.json.type === "Textarea") {
						colWidthArr.push(260);
					} else if (module && module.json.type === "Htmleditor") {
						colWidthArr.push(500);
					} else if (module && module.json.type === "TinyMCEEditor") {
						colWidthArr.push(500);
					} else if (module && module.json.type === "Calendar") {
						colWidthArr.push(150);
					} else {
						colWidthArr.push(150);
					}
				}
			}.bind(this));
			return colWidthArr;
		},
		getExportDateIndexArray : function(){
			var titleThs = this.titleTr.getElements("th");
			var dateIndexArr = []; //날짜 형식 열 인덱스
			var idx=0;
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열
				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
					if (module && module.json.type === "Calendar") {
						dateIndexArr.push(idx);
					}
					idx++;
				}
			}.bind(this));
			return dateIndexArr;
		},
		getExportTitleArray : function( type ){
			var titleThs = this.titleTr.getElements("th");
			var titleArr = [];
			titleThs.each(function(th, index){
				if ( this.editable && (index===0 || index === titleThs.length-1) )return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열
				var thJson = this.form._getDomjson( th );
				var module = this.editModules[this.editable ? (index-1) : index];
				if ( this.isAvaliableImpExpColumn( thJson, module, type ) ) {
					titleArr.push(th.get("text"));
				}
			}.bind(this));
			return titleArr;
		},

		exportToExcel : function () {
			var titleThs = this.titleTr.getElements("th");
			// var editorTds = this.editorTr.getElements("td");

			var resultArr = [];

			var titleArr = this.getExportTitleArray();
			var colWidthArr = this.getExportColWidthArray();
			var dateIndexArr = this.getExportDateIndexArray(); //날짜 형식 열 인덱스

			// var idx=0;
			// titleThs.each(function(th, index){
			// 	if ( this.editable && (index===0 || index === titleThs.length-1) )return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열
			//
			// 	var thJson = this.form._getDomjson( th );
			// 	var module = this.editModules[this.editable ? (index-1) : index];
			// 	if ( this.isAvaliableImpExpColumn( thJson, module ) ) {
			// 		if (module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)) {
			// 			colWidthArr.push(340);
			// 		} else if (module && module.json.type === "Address") {
			// 			colWidthArr.push(170);
			// 		} else if (module && module.json.type === "Textarea") {
			// 			colWidthArr.push(260);
			// 		} else if (module && module.json.type === "Htmleditor") {
			// 			colWidthArr.push(500);
			// 		} else if (module && module.json.type === "Calendar") {
			// 			colWidthArr.push(150);
			// 			dateIndexArr.push(idx);
			// 		} else {
			// 			colWidthArr.push(150);
			// 		}
			// 		idx++;
			// 		titleArr.push(th.get("text"));
			// 	}
			// }.bind(this));

			resultArr.push( titleArr );

			if (this.gridData.data){
				this.gridData.data.each(function(data, idx){
					var array = [];

					titleThs.each(function(th, index){
						if ( this.editable && (index===0 || index === titleThs.length-1) )return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열

						var module = this.editModules[ this.editable ? (index-1) : index];
						var thJson = this.form._getDomjson( th );

						if ( this.isAvaliableImpExpColumn( thJson, module ) ) {

							var cellData = data[th.get("id")];
							var text = "";

							if( cellData ) {
								if (typeOf(cellData) !== "array") { //번호
									for (key in cellData) {
										var value = cellData[key];
										if (module && ["Org", "Reader", "Author", "Personfield", "Orgfield"].contains(module.json.type)) {
											if (typeOf(value) === "array") {
												var textArray = [];
												value.each(function (item) {
													if (typeOf(item) === "object") {
														textArray.push(item.distinguishedName);
													} else {
														textArray.push(item);
													}
												}.bind(this));
												text = textArray.join(", \n");
											} else if (typeOf(value) === "object") {
												text = value.distinguishedName;
											} else {
												text = value;
											}
										} else if (module && module.json.type === "Textarea") {
											text = value;
										} else {
											text = this._getValueText(this.editable ? (index - 1) : index, value);
										}
										break;
									}
								}
							} else if (module && module.json.type === "Label" && module.node) {
								text = module.node.get("text");
							}

							if( !text && typeOf(text) !== "number" ){
								text = "";
							}

							array.push( text );
						}


					}.bind(this));

					resultArr.push( array );

				}.bind(this));
			}

			var title;
			if( this.json.excelName && this.json.excelName.code ){
				title = this.form.Macro.exec(this.json.excelName.code, this);
			}else{
				title = MWF.xApplication.process.Xform.LP.exportDefaultName;
			}
			var titleA = title.split(".");
			if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
				titleA.splice( titleA.length-1 );
			}
			title = titleA.join(".");

			var arg = { data : resultArr, colWidthArray : colWidthArr, title : title };
			this.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).exportToExcel( resultArr, arg.title || title, colWidthArr, dateIndexArr );
		},
		importFromExcel : function () {
			var columnList = [];

			var dateColArray = []; //날짜 열
			var titleThs = this.titleTr.getElements("th");

			var idx = 1;
			var orgTitleList = [];
			titleThs.each(function(th, index){
				if (this.editable && (index===0 || index === titleThs.length-1 ))return; //첫 번째 열은 작업 열, 마지막 열은 정렬 열
				var module = this.editModules[this.editable ? (index-1) : index];
				var thJson = this.form._getDomjson( th );
				if ( this.isAvaliableImpExpColumn( thJson, module, "import" )){
					columnList.push({
						text : th.get("text").trim(),
						index: idx,
						thJson: thJson,
						module: module
					});
					idx++;
					if (module && module.json.type === "Calendar"){
						dateColArray.push(idx);
					}else if( module && ["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type) ){
						orgTitleList.push(th.get("text"));
					}
				}
			}.bind(this));


			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).upload( dateColArray, function (importedData) {

				var checkAndImport = function () {
					if( !this.checkImportedData( columnList, importedData ) ){
						this.openImportedErrorDlg( columnList, importedData );
					}else{
						this.setImportData( columnList, importedData )
					}
				}.bind(this);

				if( orgTitleList.length > 0 ){
					this.listImportAllOrgData( orgTitleList, importedData, function () {
						checkAndImport();
					}.bind(this));
				}else{
					checkAndImport();
				}


			}.bind(this));
		},
		parseImportedData: function(columnList, importedData){
			var data = {
				"data" : []
			};

			importedData.each( function( importedLineData, lineIndex ){

				var lineData = {};

				columnList.each( function (obj, i) {
					var index = obj.index;
					var module = obj.module;
					var thJson = obj.thJson;
					var text = obj.text;

					var d = importedLineData[text] || "";

					var value;
					switch (module.json.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							if( !d ){
								value = [];
							}else{
								var arr = d.split(/\s*,\s*/g ); //공백,공백
								// if( arr.length === 0 ){
								// 	value = this.getImportOrgData( d );
								// }else{
								value = [];
								arr.each( function(d, idx){
									var obj = this.getImportOrgData( d );
									value.push( obj );
								}.bind(this));
								// }
							}
							break;
						case "Combox":
						case "Address":
							arr = d.split(/\s*,\s*/g ); //공백,공백
							value = arr; //arr.length === 1  ? arr[0] : arr;
							break;
						case "Checkbox":
							arr = d.split(/\s*,\s*/g ); //공백,공백
							var options = module.getOptionsObj();
							arr.each( function( a, i ){
								var idx = options.textList.indexOf( a );
								arr[ i ] = idx > -1 ? options.valueList[ idx ] : "";
							});
							value = arr.length === 1  ? arr[0] : arr;
							break;
						case "Radio":
						case "Select":
							value = d.replace(/&#10;/g,""); //줄바꿈 문자&#10;
							var options = module.getOptionsObj();
							var idx = options.textList.indexOf( value );
							value = idx > -1 ? options.valueList[ idx ] : "";
							break;
						case "Textarea":
							value = d.replace(/&#10;/g,"\n"); //줄바꿈 문자&#10;
							break;
						case "Calendar":
							value = d.replace(/&#10;/g,""); //줄바꿈 문자&#10;
							if( value ){
								var format;
								if (!module.json.format){
									if (module.json.selectType==="datetime" || module.json.selectType==="time"){
										format = (module.json.selectType === "time") ? "%H:%M" : (Locale.get("Date").shortDate + " " + "%H:%M")
									}else{
										format = Locale.get("Date").shortDate;
									}
								}else{
									format = module.json.format;
								}
								value = Date.parse( value ).format( format );
							}
							break;
						default:
							value = d.replace(/&#10;/g,""); //줄바꿈 문자&#10;
							break;
					}

					lineData[ thJson.id ] = {};
					lineData[ thJson.id ][ module.json.id ] = value;

				}.bind(this));

				data.data.push( lineData );
			}.bind(this));
			return data;
		},
		setImportData: function(columnList, importedData){

			var data = this.parseImportedData( columnList, importedData );

			this.fireEvent("import", [data] );

			this.setData( data );
			this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

		},
		openImportedErrorDlg : function( columnList, tableData ){
			var _self = this;

			var objectToString = function (obj, type) {
				if(!obj)return "";
				var arr = [];
				Object.each(obj,  function (value, key) {
					if( type === "style" ){
						arr.push( key + ":"+ value +";" )
					}else{
						arr.push( key + "='"+ value +"'" )
					}
				})
				return arr.join( " " )
			}

			var htmlArray = ["<table "+ objectToString( this.json.properties ) +" style='"+objectToString( this.json.tableStyles, "style" )+"'>"];

			var titleStyle = objectToString( this.json.titleStyles, "style" );
			htmlArray.push( "<tr>" );
			columnList.each( function (obj, i) {
				htmlArray.push( "<th style='"+titleStyle+"'>"+obj.text+"</th>" );
			});
			htmlArray.push( "<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>" );
			htmlArray.push( "</tr>" );

			var contentStyles = Object.clone( this.json.contentStyles );
			if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
			var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

			tableData.each( function( lineData, lineIndex ){

				htmlArray.push( "<tr>" );
				columnList.each( function (obj, i) {
					htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.text ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //줄바꿈 문자&#10;
				});
				htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
				htmlArray.push( "</tr>" );

			}.bind(this));
			htmlArray.push( "</table>" );

			var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
			var dlg = o2.DL.open({
				"style" : this.form.json.dialogStyle || "user",
				"title": MWF.xApplication.process.Xform.LP.importFail,
				"content": div,
				"offset": {"y": 0},
				"isMax": true,
				"width": 1000,
				"height": 700,
				"buttonList": [
					{
						"type": "exportWithError",
						"text": MWF.xApplication.process.Xform.LP.datagridExport,
						"action": function () { _self.exportWithImportDataToExcel(columnList, tableData); }
					},
					{
						"type": "cancel",
						"text": MWF.LP.process.button.cancel,
						"action": function () { dlg.close(); }
					}
				],
				"onPostClose": function(){
					dlg = null;
				}.bind(this)
			});

		},
		exportWithImportDataToExcel : function ( columnList, importedData ) {
			var titleThs = this.titleTr.getElements("th");
			// var editorTds = this.editorTr.getElements("td");

			var resultArr = [];

			var colWidthArr = this.getExportColWidthArray();
			colWidthArr.push( 220 );

			var dateIndexArr = this.getExportDateIndexArray(); //날짜 형식 열 인덱스

			var titleArr = this.getExportTitleArray("import");
			titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
			resultArr.push( titleArr );

			importedData.each( function( lineData, lineIndex ){
				var array = [];
				columnList.each( function (obj, i) {
					array.push( ( lineData[ obj.text ] || '' ).replace(/&#10;/g, "\n") );
				});
				array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

				resultArr.push( array );
			}.bind(this));

			var title;
			if( this.json.excelName && this.json.excelName.code ){
				title = this.form.Macro.exec(this.json.excelName.code, this);
			}else{
				title = MWF.xApplication.process.Xform.LP.exportDefaultName;
			}
			var titleA = title.split(".");
			if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
				titleA.splice( titleA.length-1 );
			}
			title = titleA.join(".");

			var arg = { data : resultArr, colWidthArray : colWidthArr, title : title, withError : true };
			this.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this).exportToExcel( resultArr, arg.title || title, colWidthArr, dateIndexArr );
		},
		checkImportedData : function( columnList, tableData ){
			var flag = true;

			var parsedData = this.parseImportedData(columnList, tableData);

			var lp = MWF.xApplication.process.Xform.LP;
			var columnText =  lp.importValidationColumnText;
			var columnTextExcel = lp.importValidationColumnTextExcel;
			var excelUtil = new MWF.xApplication.process.Xform.DatagridPC.ExcelUtils(this);

			tableData.each( function(lineData, lineIndex){

				var parsedLineData = (parsedData && parsedData.data) ? parsedData.data[lineIndex] : {};

				var errorTextList = [];
				var errorTextListExcel = [];

				columnList.each( function (obj, i) {
					var index = obj.index;
					var module = obj.module;
					var thJson = obj.thJson;
					var text = obj.text;

					var colInfor = columnText.replace( "{n}", index );
					var colInforExcel = columnTextExcel.replace( "{n}", excelUtil.index2ColName( index-1 ) );

					var d = lineData[text] || "";

					var parsedD = "";
					var ptd = parsedLineData[thJson.id];
					if( typeOf(ptd) === "object")parsedD = ptd[module.json.id];

					if( d ){
						switch (module.json.type) {
							case "Org":
							case "Reader":
							case "Author":
							case "Personfield":
							case "Orgfield":
								var arr = d.split(/\s*,\s*/g ); //공백,공백
								arr.each( function(d, idx){
									var obj = this.getImportOrgData( d );
									if( obj.errorText ){
										errorTextList.push( colInfor + obj.errorText + lp.fullstop );
										errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
									}
								}.bind(this));
								break;
							case "Number":
								if (isNaN(d)){
									errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
									errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
								}
								break;
							case "Calendar":
								if( !( isNaN(d) && !isNaN(Date.parse(d) ))){
									errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop );
									errorTextListExcel.push( colInforExcel + d + lp.notValidDate + lp.fullstop );
								}
								break;
							default:
								break;
						}
					}
					if (module.json.type!="sequence" && module.setData && module.json.type!=="Address"){
						var hasError = false;
						if(["Org","Reader","Author","Personfield","Orgfield"].contains(module.json.type)){
							if(o2.typeOf(parsedD)==="array" && parsedD.length){
								hasError = parsedD.some(function (item) { return item.errorText; })
							}
						}
						if( !hasError ){
							module.setData(parsedD);
							module.validationMode();
							if (!module.validation() && module.errNode){
								errorTextList.push(colInfor + module.errNode.get("text"));
								errorTextListExcel.push( colInforExcel + module.errNode.get("text"));
								module.errNode.destroy();
							}
						}
					}
				}.bind(this));

				if(errorTextList.length>0){
					lineData.errorTextList = errorTextList;
					lineData.errorTextListExcel = errorTextListExcel;
					flag = false;
				}
			}.bind(this));

			var arg = {
				validted : flag,
				data : tableData
			};
			this.fireEvent( "validImport", [arg] );

			return arg.validted;
		},
		getImportOrgData : function( str ){
			str = str.trim();
			var flag = str.substr(str.length-2, 2);
			switch (flag.toLowerCase()){
				case "@i":
					return this.identityMapImported[str] || {"errorText": str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@p":
					return this.personMapImported[str] || {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@u":
					return this.unitMapImported[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				case "@g":
					return this.groupMapImported[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
				default:
					return this.identityMapImported[str] ||
						this.personMapImported[str] ||
						this.unitMapImported[str] ||
						this.groupMapImported[str] ||
						{"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };

			}
		},
		listImportAllOrgData : function (orgTitleList, tableData, callback) {
			var identityList = [], personList = [], unitList = [], groupList = [];
			if( orgTitleList.length > 0 ){
				tableData.each( function( lineData, lineIndex ){
					// if( lineIndex === 0 )return;

					orgTitleList.each( function (title, index) {

						if( !lineData[title] )return;

						var arr = lineData[title].split(/\s*,\s*/g );
						arr.each( function( a ){
							a = a.trim();
							var flag = a.substr(a.length-2, 2);
							switch (flag.toLowerCase()){
								case "@i":
									identityList.push( a ); break;
								case "@p":
									personList.push( a ); break;
								case "@u":
									unitList.push( a ); break;
								case "@g":
									groupList.push( a ); break;
								default:
									identityList.push( a );
									personList.push( a );
									unitList.push( a );
									groupList.push( a );
									break;
							}
						})
					})
				});
				var identityLoaded, personLoaded, unitLoaded, groupLoaded;
				var check = function () {
					if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
						if(callback)callback();
					}
				};

				this.identityMapImported = {};
				if( identityList.length ){
					o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
						json.data.each( function (d) { this.identityMapImported[ d.matchKey ] = d; }.bind(this));
						identityLoaded = true;
						check();
					}.bind(this))
				}else{
					identityLoaded = true;
					check();
				}

				this.personMapImported = {};
				if( personList.length ){
					o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList }, function (json) {
						json.data.each( function (d) { this.personMapImported[ d.matchKey ] = d; }.bind(this));
						personLoaded = true;
						check();
					}.bind(this))
				}else{
					personLoaded = true;
					check();
				}

				this.unitMapImported = {};
				if( unitList.length ){
					o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList }, function (json) {
						json.data.each( function (d) { this.unitMapImported[ d.matchKey ] = d; }.bind(this));
						unitLoaded = true;
						check();
					}.bind(this))
				}else{
					unitLoaded = true;
					check();
				}

				this.groupMapImported = {};
				if( groupList.length ){
					o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
						json.data.each( function (d) { this.groupMapImported[ d.matchKey ] = d; }.bind(this));
						groupLoaded = true;
						check();
					}.bind(this))
				}else{
					groupLoaded = true;
					check();
				}
			}
		}

	});

MWF.xApplication.process.Xform.DatagridPC$Title =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
		this.dataGrid = this.node.retrieve("dataGrid");
		if ((this.json.total == "number") || (this.json.total == "count")){
			this.dataGrid.totalModules.push({
				"module": this,
				"index": (this.dataGrid.editable!=false) ? this.node.cellIndex+1 : this.node.cellIndex,
				"type": this.json.total
			})
		}
		//	this.form._loadModules(this.node);
	}
});
MWF.xApplication.process.Xform.DatagridPC$Data =  new Class({
	Extends: MWF.APP$Module,
	_afterLoaded: function(){
		debugger;
		//this.form._loadModules(this.node);
		this.dataGrid = this.node.retrieve("dataGrid");

		var td = this.node;

		if (this.json.cellType == "sequence"){
			var flag = true;
			for (var i=0; i<this.dataGrid.editModules.length; i++){
				if (this.dataGrid.editModules[i].json.id == this.json.id){
					flag = false;
					break;
				}
			}
			if (flag){
				this.dataGrid.editModules.push({
					"json": {"type": "sequence", "id": this.json.id},
					"node": td  ,
					"focus": function(){}
				});
			}
		}else{
			var moduleNodes = this.form._getModuleNodes(this.node);
			moduleNodes.each(function(node){
				var json = this.form._getDomjson(node);
				if( json ){
					var isField = false;
					if (json.type=="Attachment" || json.type=="AttachmentDg" ){
						json.type = "AttachmentDg";
						//json.site = this.dataGrid.getAttachmentRandomSite();
						//json.id = json.site;
					}
					var module = this.form._loadModule(json, node, function(){
						isField = this.field;
						this.field = false;
					});
					if( isField ){
						module.node.setStyle("padding-right","0px");
					}
					module.dataModule = this;
					this.dataGrid.editModules.push(module);
				}
			}.bind(this));
		}
	}
});

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.process.Xform.DatagridPC.ExcelUtils = new Class({
	Extends: MWF.xApplication.Template.utils.ExcelUtils,
	initialize: function(){
		this.sheet2JsonOptions = {};
		this.pollyfill();
	}
});
