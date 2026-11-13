/**
 * 데이터 템플릿 데이터 구조.
 * @typedef {Array} DatatemplateData
 * @example
 [ //데이터 템플릿 데이터 항목
 {
           "org": [{
                "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "홍길동",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "란더종횡/마케팅부",
                "unitName": "마케팅부"
            }],
            "org_1": [{
                "distinguishedName": "홍길동@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "홍길동",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "란더종횡/마케팅부",
                "unitName": "마케팅부"
            }, {
                "distinguishedName": "김철수@bf007525-99a3-4178-a474-32865bdddec8@I",
                "id": "bf007525-99a3-4178-a474-32865bdddec8",
                "name": "김철수",
                "person": "0c828550-d8ab-479e-9880-09a59332f1ed",
                "unit": "9e6ce205-86f6-4d84-96e1-83147567aa8d",
                "unitLevelName": "란더종횡/마케팅부",
                "unitName": "마케팅부"
            }],
            "number": "111",
            "textfield": "항저우",
            "attachment": [
                {
                    "activityName": "기안",
                    "extension": "jpg",
                    "id": "9514758e-9e28-4bfe-87d7-824f2811f173",
	 				"businessId": "1234758e-9e28-4bfe-87d7-824f2811f173",
                    "lastUpdateTime": "2020-12-09 21:48:03",
                    "length": 452863.0,
                    "name": "111.jpg",
                    "person": "김철수@lisi@P"
                }
            ]
        },
 ...
 ]
 */
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
/** @class Datatemplate 데이터 템플릿 컴포넌트. 구조와 스타일을 사용자 정의할 수 있는 다중 행 데이터 편집 컴포넌트.
 * @o2cn 데이터 템플릿
 * @example
 * //스크립트에서 이 컴포넌트를 가져올 수 있음
 * //방법 1:
 * var datatemplate = this.form.get("name"); //컴포넌트 가져오기
 * //방법 2
 * var datatemplate = this.target; //컴포넌트 이벤트 스크립트에서 가져오기
 * @extends MWF.xApplication.process.Xform.$Module
 * @o2category FormComponents
 * @since v6.2
 * @o2range {Process|CMS|Protal}
 * @hideconstructor
 */
MWF.xApplication.process.Xform.Datatemplate = MWF.APPDatatemplate = new Class(
	/** @lends MWF.xApplication.process.Xform.Datatemplate# */
	{
		Implements: [Events],
		Extends: MWF.APP$Module,
		isEdit: false,
		options: {
			/**
			 * 모든 내용 로드 후 실행(비동기 로드 포함).
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterLoad
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목을 초기화했지만 아직 로드하지 않았을 때마다 발생. this.event로 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.Datatemplate#beforeLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 각 항목이 로드된 후 발생. this.event로 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterLoadLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 데이터 템플릿이 변경될 때 발생. this.event.lines로 수정된 항목 배열을, this.event.type으로 수정 유형을 가져올 수 있음.<br/>
			 * <table>
			 *     <tr><th><b>this.event.type</b></th><th><b>발생 유형</b></th><th><b>this.event.lines</b></th></tr>
			 *     <tr><td>addline</td><td>행 추가</td><td>추가한 행 배열</td></tr>
			 *     <tr><td>deleteline</td><td>행 삭제</td><td>삭제한 행 배열</td></tr>
			 *     <tr><td>deletelines</td><td>여러 행 삭제</td><td>삭제한 행 배열</td></tr>
			 *     <tr><td>editmodule</td><td>필드 값 변경 시</td><td>this.event.lines는 편집한 행 배열<br/>this.event.module은 수정한 필드</td></tr>
			 *     <tr><td>import</td><td>데이터 가져오기 후</td><td>데이터 템플릿의 모든 행</td></tr>
			 * </table>
			 * @event MWF.xApplication.process.Xform.Datatemplate#change
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 추가 시 발생. this.event.line으로 해당 항목 객체를, this.event.ev로 이벤트를 발생시킨 Event를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.Datatemplate#addLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 전에 발생. this.event로 해당 항목 객체를 가져올 수 있음.
			 * @event MWF.xApplication.process.Xform.Datatemplate#deleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 항목 삭제 후 발생.
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterDeleteLine
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * 내보내기 전에 발생.
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeExport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 내보내기 시 발생. this.event는 내보낼 데이터를 가리키며, this.event를 수정해 데이터를 변경할 수 있음.
			 * @event MWF.xApplication.process.Xform.Datatemplate#export
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
			 * 가져오기 전에 발생.
			 * @event MWF.xApplication.process.Xform.DatatablePC#beforeImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 가져오기에서 데이터 검증 후 발생. this.event는 가져온 데이터를 가리킴.
			 * @event MWF.xApplication.process.Xform.Datatemplate#validImport
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
			 * excel 가져오기에서 데이터 검증에 성공해 데이터 템플릿에 다시 설정하려 할 때 발생. this.event는 정리된 가져오기 데이터를 가리키며, 형식은 {@link DatatemplateData} 참고.
			 * @event MWF.xApplication.process.Xform.Datatemplate#import
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			/**
			 * excel 가져오기에서 데이터를 데이터 템플릿에 다시 설정한 후 발생. this.event는 정리된 가져오기 데이터를 가리키며, 형식은 {@link DatatemplateData} 참고.
			 * @event MWF.xApplication.process.Xform.Datatemplate#afterImport
			 * @see {@link https://www.yuque.com/o2oa/ixsnyt/hm5uft#i0zTS|컴포넌트 이벤트 설명}
			 */
			"moduleEvents": ["queryLoad","postLoad","load", "afterLoad",
				"beforeLoadLine", "afterLoadLine", "change", "addLine", "deleteLine", "afterDeleteLine", "beforeExport",
				"beforeImport", "export", "import", "validImport", "afterImport"]
		},

		initialize: function(node, json, form, options){
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
		_loadMergeEditNodeByScript: function(){
			if (this.json.sectionMergeEditScript && this.json.sectionMergeEditScript.code) {
				var data = this.form.Macro.exec(this.json.sectionMergeEditScript.code, this);
				this._setBusinessData( data );
				//this._loadUserInterface();
			}
		},
		_loadMergeEditNodeByDefault: function(){
			var data = this.getSortedSectionData();
			var businessData = [];
			data.each(function(d){
				d.data = d.data || [];
				businessData = businessData.concat( d.data );
			});
			this._setBusinessData(businessData);
			//this._loadUserInterface();
		},
		_loadUserInterface: function(){
			// this.fireEvent("queryLoad");
			this.loading = true;

			if (!this.isReadable && !!this.isHideUnreadable){
				this.node?.addClass('hide');
				return '';
			}

			if( this.isSectionMergeEdit() ){ //구간 병합, 구간 값 병합 데이터 삭제 후 편집
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}

			//구간 병합 표시
			this.isMergeRead = this.isSectionMergeRead();

			//구간 사용 및 모든 구간 표시
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			var iconNode = this.node.getElement("div[o2icon='datatemplate']");
			if(iconNode)iconNode.destroy();

			// this.editModules = [];
			// this.node.setStyle("overflow-x", "auto");
			// this.node.setStyle("overflow-y", "hidden");

			this.editable = !(this.readonly || (this.json.isReadonly === true) || (this.form.json.isReadonly === true));
			if( this.isMergeRead )this.editable = false;
			if (this.editable && this.json.editableScript && this.json.editableScript.code){
				this.editable = this.form.Macro.exec(((this.json.editableScript) ? this.json.editableScript.code : ""), this);
			}

			this.getRelativeId();

			//가져오기 허용
			this.importenable  = this.editable && (this.importActionIdList.length > 0) &&
				(this.json.impexpType === "impexp" || this.json.impexpType === "imp");

			//내보내기 허용
			this.exportenable  = (this.exportActionIdList.length > 0) && (this.json.impexpType === "impexp" || this.json.impexpType === "exp");

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData();
			}else if( this.isMergeRead ){
				this.data = this.getSectionMergeReadData();
			}else{
				this.data = this._getValue();
				if( !this._getBusinessData() ){
					this.isNew = true;
					this._setValue(this.data);
				}
			}

			this.lineList = [];
			this.sectionlineList = [];

			//this.data가 object일 때만 값이 있음
			// this.lineMap = {};

			// this.totalModules = [];
			this._loadStyles();

			//html 템플릿과 json 템플릿 가져오기
			this.getTemplate();


			if( !this.form.isLoaded ){ //양식이 아직 로드되지 않은 경우
				//양식의 afterModulesLoad 이벤트로 노드 외부 작업 설정: 추가, 삭제, 가져오기, 내보내기
				this.setOuterActionEvents();
			}

			//노드 숨김
			this.node.getChildren().hide();

			this.fireEvent("load");
			this._loadDataTemplate(function(){
				// this._loadImportExportAction();
				this.fieldModuleLoaded = true;
				this.loading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		/*
		 * @summary 데이터 표 다시 로드. load와 postLoad 이벤트가 발생함.
		 * @example
		 *  this.form.get("fieldId").reload(); //다시 로드
		 */
		reload: function( isReloadTemplate ){
			this._loadReadEditAbeld();

			this.reloading = true;

			// this.editModules = [];

			this.getRelativeId();

			this.checkMerge( this.getValue() );

			this.clearSubModules();

			if (!this.isReadable && !!this.isHideUnreadable){
				this.node?.addClass('hide');
				return;
			}

			this._loadStyles();

			if( isReloadTemplate ){ //html 템플릿과 json 템플릿 다시 가져오기
				this.node.getChildren().setStyle("display", "");
				this.getTemplate();
				this.node.getChildren().hide();
			}

			this.lineList = [];
			this.sectionlineList = [];

			if( this.form.isLoaded ){ //양식이 아직 로드되지 않은 경우
				this._setOuterActionEvents(true);
			}

			this.fireEvent("load");
			this._loadDataTemplate(function(){
				// this._loadImportExportAction();
				this.reloading = false;
				this.fireEvent("postLoad");
			}.bind(this));
		},
		getRelativeId: function(){
			this.outerAddActionIdList = (this.json.outerAddActionId || "").split(",");
			this.outerDeleteActionIdList = (this.json.outerDeleteActionId || "").split(",");
			this.outerSelectAllIdList = (this.json.outerSelectAllId || "").split(",");

			this.addActionIdList = (this.json.addActionId || "").split(",");
			this.deleteActionIdList = (this.json.deleteActionId || "").split(",");
			this.sequenceIdList = (this.json.sequenceId || "").split(",");
			this.selectorId = this.json.selectorId;

			this.importActionIdList = (this.json.importActionId || "").split(",");
			this.exportActionIdList = (this.json.exportActionId || "").split(",");
		},
		getTemplate: function(){
			this.templateJson = {};
			this.templateHtml = this.node.get("html");
			var moduleNodes = this.form._getModuleNodes(this.node);
			moduleNodes.each(function (node) {
				if (node.get("MWFtype") !== "form") {
					var json = this.form._getDomjson(node);
					if(json)this.templateJson[json.id] = json ;
				}
			}.bind(this));
		},
		_loadStyles: function(){
			this.node.setStyles(this.json.styles);
			this.node.set(this.json.properties);
		},
		_getOuterActionModules: function( idList ){ //데이터 템플릿에는 없지만 양식 안에 있는 Id 판단
			var list = [];
			idList.each( function (id) {
				var module = this._getModuleByPath(id);
				var tId = id.split("..").getLast();
				if( !this.templateJson.hasOwnProperty(tId) && module ){
					list.push( module );
				}
			}.bind(this));
			return list;
		},
		_setOuterActionEvents: function( reload ){
			this.addActionList = this._getOuterActionModules( [].concat(this.addActionIdList, this.outerAddActionIdList) );
			this.addActionList.each( function (module) {
				var addEvent = function (){
					!module._isDtEventAdded && module.node.addEvents({"click": function(e){
						this._addLine(e);
					}.bind(this)});
					module._isDtEventAdded = true;
					!this.editable ? module.node.hide() : module.node.show();
					module.removeEvent("load", addEvent);
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					 module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}

			}.bind(this));

			this.deleteActionList = this._getOuterActionModules( [].concat( this.outerDeleteActionIdList ) );
			this.deleteActionList.each( function (module) {
				var addEvent = function (){
					!module._isDtEventAdded && module.node.addEvents({"click": function(e){
						this._deleteSelectedLine(e);
					}.bind(this)});
					module._isDtEventAdded = true;
					!this.editable ? module.node.hide() : module.node.show();
					module.removeEvent("load", addEvent);
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));

			this.selectAllList = this._getOuterActionModules( this.outerSelectAllIdList );
			this.selectAllList.each( function (module) {
				// module.setData(""); //기본적으로 선택 안 함
				var addEvent = function (){
					!module._isDtEventAdded && module.node.addEvents({"change": function(e){
						debugger;
						this._checkSelectAll(e);
					}.bind(this)});
					module._isDtEventAdded = true;
					!this.editable ? module.node.hide() : module.node.show();
					module.removeEvent("load", addEvent);
				}.bind(this);

				( !!reload && module.reload ) && module.reload();

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));
			this.selectAllSelector = this.selectAllList[0];
			if(this.selectAllSelector){
				this.unselectAll();
			}

			this.importActionList = this._getOuterActionModules( this.importActionIdList );
			this.importActionList.each( function (module) {
				var addEvent = function (){
					!module._isDtEventAdded && module.node.addEvents({"click": function(e){
						this.importFromExcel();
					}.bind(this)});
					module._isDtEventAdded = true;
					!this.editable ? module.node.hide() : module.node.show();
					module.removeEvent("load", addEvent);
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));

			this.exportActionList = this._getOuterActionModules( this.exportActionIdList );
			this.exportActionList.each( function (module) {
				var addEvent = function (){
					!module._isDtEventAdded && module.node.addEvents({"click": function(e){
						this.exportToExcel();
					}.bind(this)});
					module._isDtEventAdded = true;
					module.removeEvent("load", addEvent);
				}.bind(this);

				if( module.json.type.substr(0, 2) === "El" ){
					module.vm ? addEvent() : module.addEvent("load", addEvent);
				}else{
					addEvent();
				}
			}.bind(this));
		},
		setOuterActionEvents: function(){

			this.bindEvent = function () {
				this._setOuterActionEvents();

				this.fireEvent("afterLoad");

				//로드 완료 후 이벤트 삭제
				this.form.removeEvent("afterModulesLoad", this.bindEvent );
			}.bind(this);

			//양식의 모든 컴포넌트가 로드된 후에 외부 컴포넌트를 가져와야 함
			this.form.addEvent("afterModulesLoad", this.bindEvent );
		},
		isShowSectionKey: function(){
			return this.json.showSectionKey && this.isMergeRead ;
		},
		isShowSectionBy: function(){
			return this.json.showSectionBy && this.isShowAllSection ;
		},
		isSectionData: function(){ //데이터 구간 처리 여부
			var data = this.getBusinessDataById();
			if( o2.typeOf( data ) === "object" ){
				var keys = Object.keys(data);
				// if( o2.typeOf(data[keys[0]]) === "array"  ){
				// 	return true;
				// }
				for( var i=0; i<keys.length; i++ ){
					var key = keys[i];
					if( key !== "data" ){
						if( o2.typeOf(data[key]) === "array"  ){
							return true;
						}
					}
				}
			}
			return false;
		},
		/**
		 * @summary 데이터 템플릿이 구간 병합 표시, 구간 병합 편집으로 설정된 경우 이 메서드로 모든 구간 데이터를 가져올 수 있음.
		 * @return {Object} 객체.
		 * @example
		 * var data = this.form.get("fieldId").getAllSectionData();
		 * //data 형식은 다음과 같음:
		 * {
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": [ //구간1의 key와 data
		 *			{
		 *				"good": "yf",
		 *				"number_2": 11,
		 *				"prize": 1
		 *			}
		 *		],
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": [ //구간2의 key와 data
		 *  		{
		 *  			"good": "yf",
		 *  			"number_2": 11,
		 *  			"prize": 10
		 *  		}
		 *  	]
		 *  }
		 */
		getAllSectionData: function(){
			return this.getBusinessDataById();
		},
		_getAllSectionData: function(){
			var bData = this.getBusinessDataById();
			var flag = false;
			if( !bData ){
				flag = true;
				bData = {};
			}
			if( this.sectionBy && !bData[this.sectionBy] ){
				flag = true;
				this.isNew = true;
				bData[this.sectionBy] = this.getValue();
			}
			if( flag )this.setBusinessDataById( bData );
			this.dataWithSectionBy = this.getAllSortedSectionData();
			return flag ? this.getBusinessDataById() : bData;
		},
		getAllSortedSectionData: function(){ //병합·정렬된 데이터 가져오기
			var data = this.getBusinessDataById();
			var array = [];
			for( var key in data ){
				array.push({
					sectionKey: key,
					key: key,
					data: data[key]
				})
			}
			if( this.json.sectionDisplaySortScript && this.json.sectionDisplaySortScript.code){
				array.sort( function(a, b){
					this.form.Macro.environment.event = {
						"a": a,
						"b": b
					};
					var flag = this.form.Macro.exec(this.json.sectionDisplaySortScript.code, this);
					this.form.Macro.environment.event = null;
					return flag;
				}.bind(this))
			}
			return array;
		},
		getSectionMergeReadData: function(){
			switch (this.json.mergeTypeRead) {
				case "dataScript":
					if (this.json.sectionMergeReadDataScript && this.json.sectionMergeReadDataScript.code) {
						return this.form.Macro.exec(this.json.sectionMergeReadDataScript.code, this);
					}else{
						return [];
					}
				default:
					var sortedData = this.getSortedSectionData();
					if( this.json.showSectionKey ){
						this.dataWithSectionKey = sortedData;
					}
					var data = [];
					//구간 값을 각 행의 데이터에 넣음
					sortedData.each(function(d){
						( d.data || [] ).each(function( obj ){
							if( o2.typeOf(obj) === "object" ){
								// obj.sectionKey = d.sectionKey;
								data.push( obj )
							}
						});
						// data = data.concat( d.data.data );
					});
					return data;
			}
		},
		getDefaultValue: function(){
			if( this.json.section==="yes" && !this.sectionBy )return [];
			var value;
			if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
			if (value && !value.then) if (o2.typeOf(value)==="object") value = [value];
			if(!value){
				value = [];
				var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
				for( var i=0; i<count; i++ )value.push({})
			}
			return value;
		},
		_getValue: function(){
			if (this.moduleValueAG) return this.moduleValueAG;
			var value = this._getBusinessData();
			if( !value ){
				value = this.getDefaultValue();
			}
			// if (!value){
			// 	if (this.json.defaultData && this.json.defaultData.code) value = this.form.Macro.exec(this.json.defaultData.code, this);
			// 	if (value && !value.then) if (o2.typeOf(value)==="object") value = [value];
			// }
			// if(!value){
			// 	value = [];
			// 	var count = this.json.defaultCount ? this.json.defaultCount.toInt() : 0;
			// 	for( var i=0; i<count; i++ )value.push({})
			// }
			return value;
		},
		getValue: function(){
			if (!this.isReadable) return [];
			return this._getValue();
		},

		_setValue: function(value){
			if (!!value && o2.typeOf(value.then)=="function"){
				var p = o2.promiseAll(value).then(function(v){
					this.__setValue(v);
				}.bind(this), function(){});
				this.moduleValueAG = p;
				p.then(function(){
					this.moduleValueAG = null;
				}.bind(this), function(){
					this.moduleValueAG = null;
				}.bind(this));
			}else{
				this.moduleValueAG = null;
				this.__setValue(value);
			}
		},
		__setValue: function(value){
			this._setBusinessData(value);
			this.moduleValueAG = null;
			return value;
		},

		_loadDataTemplate: function(callback, operation){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(this.data)=="object") this.data = [this.data];


				if( this.isShowAllSection ){
					this._loadSectionLineList_EditSection(callback, operation)
				}else if( this.isShowSectionKey() ){
					this._loadSectionLineList(callback, operation)
				}else{
					this._loadLineList(callback, operation);
				}

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
		},
		_loadSectionLineList_EditSection: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			//this._getAllSectionData();
			this.dataWithSectionBy.each(function(data, idx){
				var isEdited = false;
				if( this.isSectionLineEditable( data ) && this.editable ){
					isEdited = true;
				}
				var isNew = false;
				var sectionLine;
				var beforeNode = idx > 0 ? this.sectionlineList[idx-1].node : null;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					if( !operation || operation === "moveUpList"){
						this._injectLineNode( sectionLine.node, beforeNode );
					}
					sectionLine.setIndex( data, idx, isEdited, isNew, operation );
				}else{
					var div = this._injectLineNode( new Element("div"), beforeNode );
					sectionLine = this._loadSectionLine_EditSection(div, data, idx, isEdited, isNew );
				}
				if( this.sectionBy && this.sectionBy === data.sectionKey ){
					this.sectionLineEdited = sectionLine;
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine_EditSection: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.Datatemplate.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isDeleteable: this.editable && this.isSectionLineEditable(data),
				isAddable: this.editable && this.isSectionLineEditable(data)
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},
		isSectionLineEditable: function(data){
			return this.isShowAllSection && this.sectionBy && this.sectionBy === data.sectionKey;
		},

		_loadSectionLineList: function(callback, operation){
			var map = this.unchangedSectionLineMap || {};
			this.dataWithSectionKey.each(function(data, idx){
				var sectionLine;
				var isEdited = false;
				var isNew = false;
				var beforeNode = idx > 0 ? this.sectionlineList[idx-1].node : null;
				if( map[data.sectionKey] ) {
					sectionLine =  map[data.sectionKey];
					if( !operation || operation === "moveUpList"){
						this._injectLineNode( sectionLine.node, beforeNode );
					}
					sectionLine.setIndex( data, idx, isEdited, isNew, operation );
				}else {
					var div = this._injectLineNode( new Element("div"), beforeNode );
					sectionLine = this._loadSectionLine(div, data, idx, isEdited, isNew );
				}
				this.sectionlineList.push(sectionLine);
			}.bind(this))
			if (callback) callback();
		},
		_loadSectionLine: function(container, data, index, isEdited, isNew){
			var sectionLine = new MWF.xApplication.process.Xform.Datatemplate.SectionLine(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isNew: isNew,
				isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.editable,
				isDeleteable: this.editable,
				isAddable: this.editable,
				isMergeRead: this.isMergeRead
			});
			// this.fireEvent("beforeLoadLine", [line]);
			sectionLine.load();
			// this.fireEvent("afterLoadLine", [line]);
			return sectionLine;
		},
		_loadLineList: function(callback, operation){
			var map = this.unchangedLineMap || {};
			Object.each(map, function (line, idx) {
				line.setIndex( idx.toInt() );
			});
			this.data.each(function(data, idx){
				var idxStr = idx.toString();
				var beforeNode = idx > 0 ? this.lineList[idx - 1].node : null;
				if( map[idxStr] ){
					if( !operation || operation === "moveUpList" ){
						this._injectLineNode( map[idxStr].node, beforeNode );
					}
					this.lineList.push( map[idxStr] );
				}else{
					var isNew = this.isNew || (o2.typeOf(this.newLineIndex) === "number" ? idx === this.newLineIndex : false);
					var div = this._injectLineNode( new Element("div"), beforeNode );
					var line = this._loadLine(div, data, idx, isNew);
					this.lineList.push(line);
				}
			}.bind(this));
			this.newLineIndex = null;
			this.isNew = false;
			if (callback) callback();
		},
		_loadLine: function(container, data, index, isNew){
			var line = new MWF.xApplication.process.Xform.Datatemplate.Line(container, this, data, {
				index : index,
				indexText : (index+1).toString(),
				isEdited : this.editable,
				isNew : isNew,
				isMergeRead: this.isMergeRead
			});
			this.fireEvent("beforeLoadLine", [line]);
			line.load();
			this.fireEvent("afterLoadLine", [line]);
			return line;
		},
		_injectLineNode: function( lineNode, beforeNode ){
			if( beforeNode ){
				lineNode.inject( beforeNode, "after");
			}else{
				lineNode.inject( this.node );
			}
			return lineNode;
		},
		isMax : function(){
			var maxCount = this.json.maxCount ? this.json.maxCount.toInt() : 0;
			if( this.editable && maxCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length >= maxCount )return true;
				}else{
					if( this.lineList.length >= maxCount )return true;
				}
			}
			return false;
		},
		isMin : function(){
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( this.editable && minCount > 0 ) {
				if( this.isShowAllSection ){
					if( this.sectionLineEdited && this.sectionLineEdited.lineList.length <= minCount )return true;
				}else {
					if( this.lineList.length <= minCount )return true;
				}
			}
			return false;
		},
		_setLineData: function(line, d){
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var sdata = data[ line.sectionLine.sectionKey ];
				if( sdata  ){
					sdata[line.options.indexInSectionLine] = d;
					this.setAllSectionData( data, false, "setLineData" );
				}
			}else{
				var index = line.options.index;
				var data = this.getInputData();
				data[index] = d;
				this.setData( data, false, "setLineData" );
			}
		},
		_addLine: function(ev, d){

			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			var data, index, newLine, changedData;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}

				changedData = d||{};
				sdata.push(changedData);
				index = sdata.length - 1;
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "addLine");
				newLine = this.sectionLineEdited.lineList[index];

				this.saveArrayData('addLine', index, null, changedData, this.sectionBy);
			}else{
				data = this.getInputData();

				changedData = d || {};
				data.push(changedData);
				index = data.length-1;
				this.newLineIndex = index;
				this.setData( data, false, "addLine" );
				newLine = this.getLine(index);

				this.saveArrayData('addLine', index, null, changedData);
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_insertLine: function(ev, beforeLine){
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}

			//데이터 기반 사용
			var data, index, newLine, changedData;
			if( this.isShowAllSection ){
				index = beforeLine.options.indexInSectionLine + 1;

				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}
				changedData = {};
				sdata.splice(index, 0, changedData);
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "insertLine");
				newLine = this.sectionLineEdited.lineList[index];

				this.saveArrayData('insertLine', index, null, changedData, this.sectionBy);
			}else {
				index = beforeLine.options.index+1;
				data = this.getInputData();

				changedData = {};
				data.splice(index, 0, changedData);
				this.newLineIndex = index;
				this.setData( data, false, "insertLine" );
				newLine = this.getLine( index );

				this.saveArrayData('insertLine', index, null, changedData);
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_insertLineByIndex: function(ev, index, d){
			if( this.isMax() ){
				var text = MWF.xApplication.process.Xform.LP.maxItemCountNotice.replace("{n}",this.json.maxCount);
				this.form.notice(text,"info");
				return false;
			}
			//데이터 기반 사용
			var data, newLine, changedData;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
				var sdata = data[ this.sectionBy ];
				if( !sdata ){
					sdata = data[ this.sectionBy ] = [];
				}
				if (sdata.length < index) return null;
				changedData = d || {};
				sdata.splice(index, 0, changedData);
				this.newLineIndex = index;

				this.setAllSectionData( data , false, "insertLine");
				newLine = this.sectionLineEdited.lineList[index];

				this.saveArrayData('insertLine', index, null, changedData, this.sectionBy);
			}else {
				data = this.getInputData();
				if(data.length < index )return null;
				var changedData = d||{};
				data.splice(index, 0, changedData);
				this.newLineIndex = index;
				this.setData( data , false, "insertLine");
				newLine = this.getLine( index );

				this.saveArrayData('insertLine', index, null, changedData);
			}

			this.validationMode();
			this.fireEvent("addLine",[{"line":newLine, "ev":ev}]);
			this.fireEvent("change", [{"lines":[newLine], "type":"addline"}]);
			return newLine;
		},
		_deleteSelectedLine: function(ev){
			var selectedLine = this.lineList.filter(function (line) {
				return line.selected && ( line.options.isEdited || line.options.isNew );
			});
			if( selectedLine.length === 0 ){
				this.form.notice( MWF.xApplication.process.Xform.LP.selectItemNotice,"info");
				return false;
			}
			var minCount = this.json.minCount ? this.json.minCount.toInt() : 0;
			if( minCount > 0 ){
				var length;
				if( this.isShowAllSection && this.sectionLineEdited ){
					length = this.sectionLineEdited.lineList.length;
				}else{
					length = this.lineList.length;
				}
				if( length - selectedLine.length < minCount ){
					var text = MWF.xApplication.process.Xform.LP.minItemNotice.replace("{n}", minCount );
					this.form.notice(text,"info");
					return false;
				}
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteSelectedItemNotice, 300, 120, function(){

				_self._delLines( selectedLine );

				this.close();

			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);

		},
		_delLines: function(lines){
			var _self = this;
			var saveFlag = false;

			var data;
			if( this.isShowAllSection ){
				data = this.getBusinessDataById();
			}else{
				data = _self.getInputData();
			}

			lines.reverse().each(function(line){
				_self.fireEvent("deleteLine", [line]);

				if(line.deleteAttachment())saveFlag = true;

				if( line.sectionLine ){
					var d = data[ line.sectionLine.sectionKey ];
					if( d ){
						var index = line.options.indexInSectionLine;
						d.splice(index, 1);

						_self.saveArrayData('delete', index, null, null, line.sectionLine.sectionKey);
					}
				}else {
					var index = line.options.index;
					data.splice(index, 1);
					_self.saveArrayData('delete', index);
				}

				_self.fireEvent("afterDeleteLine");
			});

			if( this.isShowAllSection ){
				_self.setAllSectionData(data, false, "deleteLines");
			}else{
				_self.setData( data , false, "deleteLines");
			}
			this.validationMode();

			_self.fireEvent("change", [{"lines":lines, "type":"deletelines"}]);

			if(saveFlag)this.saveFormData();
		},
		_deleteLine: function(ev, line){
			if( this.isMin() ){
				var text = MWF.xApplication.process.Xform.LP.minItemCountNotice.replace("{n}", this.json.minCount );
				this.form.notice(text,"info");
				return false;
			}
			var _self = this;
			this.form.confirm("warn", ev, MWF.xApplication.process.Xform.LP.deleteDatagridLineTitle, MWF.xApplication.process.Xform.LP.deleteDatagridLine, 300, 120, function(){
				_self._delLine(line);
				this.close();
			}, function(){
				this.close();
			}, null, null, this.form.json.confirmStyle);
		},
		_delLine: function(line){
			this.fireEvent("deleteLine", [line]);

			var saveFlag = line.deleteAttachment();
			//데이터 기반 사용
			var data;
			if( line.sectionLine ){
				var data = this.getBusinessDataById();
				var d = data[ line.sectionLine.sectionKey ];
				if( d ){
					var index = line.options.indexInSectionLine;
					d.splice(index, 1);
					this.saveArrayData('delete', index, null, null, line.sectionLine.sectionKey);
				}
				this.setAllSectionData( data, false, "deleteLine" );
			}else{
				data = this.getInputData();
				var index = line.options.index;
				data.splice(index, 1);
				this.saveArrayData('delete', index );
				this.setData( data , false, "deleteLine");
			}

			this.validationMode();
			this.fireEvent("afterDeleteLine");

			this.fireEvent("change", [{"lines":[line], "type":"deleteline"}]);

			if(saveFlag)this.saveFormData();
		},
		_checkSelectAll: function () {
			var selectData = this.selectAllSelector.getData();
			var selected;
			if(o2.typeOf(selectData)==="array"){
				selected = selectData.contains(this.json.outerSelectAllSelectedValue);
			}else{
				selected = selectData === this.json.outerSelectAllSelectedValue;
			}
			this.selected = selected;
			if( this.isShowAllSection && this.sectionLineEdited){
				this.sectionLineEdited.lineList.each(function (line) {
					if( line.options.isEdited || line.options.isNew ){
						this.selected ? line.select() : line.unselect();
					}
				}.bind(this))
			}else{
				this.lineList.each(function (line) {
					if( line.options.isEdited || line.options.isNew ) {
						this.selected ? line.select() : line.unselect();
					}
				}.bind(this))
			}
		},
		selectAll: function(){
			this.selected = true;
			if(this.selectAllSelector)this.selectAllSelector.setData(this.json.outerSelectAllSelectedValue);
		},
		unselectAll: function(){
			this.selected = false;
			if( this.selectAllSelector.getOptionsObj ){
				var options = this.selectAllSelector.getOptionsObj();
				var value;
				var arr = options.valueList || [];
				for( var i=0; i<arr.length; i++ ){
					var v = arr[i];
					if( v !== this.json.outerSelectAllSelectedValue ){
						value = v;
						break;
					}
				}
				this.selectAllSelector.setData( typeOf(value) !== 'null' ? [value] : []);
			}else{
				this.selectAllSelector.setData([]);
			}
		},

		// editValidation: function(){
		// 	var flag = true;
		// 	this.editModules.each(function(field, key){
		// 		if (field.json.type!=="sequence" && field.validationMode ){
		// 			field.validationMode();
		// 			if (!field.validation()) flag = false;
		// 		}
		// 	}.bind(this));
		// 	return flag;
		// },
		exportToExcel: function(){
			this.exporter = new MWF.xApplication.process.Xform.Datatemplate.Exporter(this);
			this.exporter.exportToExcel();
		},
		importFromExcel: function(){
			this.importer = new MWF.xApplication.process.Xform.Datatemplate.Importer(this);
			this.importer.importFromExcel();
		},


		_afterLoaded: function(){
		},
		// /**
		//  * @summary 데이터 템플릿 값을 기본값으로 재설정하거나 비움.
		//  *  @example
		//  * this.form.get('fieldId').resetData();
		//  */
		resetData: function(){
			// var value = this.getDefaultValue() || [];
			var value = this.getValue() || [];
			this.setData(value, false, 'resetData');
		},
		/**매개변수가 Promise인 경우 문서 참고: {@link  https://www.yuque.com/o2oa/ixsnyt/ws07m0|Promise로 양식 비동기 처리}<br/>
		 * 양식에 해당 컴포넌트가 없으면 this.data[fieldId] = data로 값을 설정할 수 있음.
		 * @summary 데이터 템플릿에 값 설정.
		 * @param data{DatatemplateData|Promise|Array} 필수, 배열 또는 Promise.
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
		setData: function(data, fireChange, operation){
			if (!data){
				data = this._getValue();
			}
			this._setData(data, fireChange, operation);
		},
		_setData: function(data, fireChange, operation){
			var p = o2.promiseAll(this.data).then(function(v){
				this.data = v;
				// if (o2.typeOf(data)==="object") data = [data];
				this.__setData(data, fireChange, operation);
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
		},
		__setData: function(data, fireChange, operation){
			if( this.isShowAllSection ){
				//외부에서 현재 구간 편집에 대한 setData 호환, 내부 setData는 여기를 거치지 않고 바로 setAllSectionData로 감
				this._setEditedSectionData(data, fireChange, operation);
				return;
			}else if( this.isMergeRead ){
				//병합 및 읽기 전용, setData 불가
				throw new Error("The data template is merged and read-only, you can use the 'setAllSectionData' method to set all section data");
			}

			var old;
			if(fireChange)old = Object.clone(this._getBusinessData() || {});

			this._setUnchangedLineMap(data, operation);

			if( !operation && !this.saving ){
				this.saving = true;
				if( this.sectionBy ){
					this.saveDataById(this.json.id + '..' + this.sectionBy, this._getBusinessData());
				}else{
					this.saveDataById();
				}
				this.saving = false;
			}

			this._setBusinessData(data);
			this.data = data;

			if (this.data){
				this.clearSubModules();
			}

			this.lineList = [];
			this.sectionlineList = [];
			this._loadDataTemplate(function(){
				this._setSubDatatemplateOuterEvents();
				if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");
				this.unchangedLineMap = null;
			}.bind(this), operation);
		},
		_setSubDatatemplateOuterEvents: function(){
			//하위 데이터 템플릿에 외부 이벤트 바인딩 알림
			for (var i=0; i<this.lineList.length; i++){
				this.lineList[i].setSubDatatemplateOuterActionEvents();
			}
		},
		_setEditedSectionData: function(data, fireChange, operation){
			if( this.isShowAllSection ){
				var d = this.getBusinessDataById();
				d[ this.sectionBy ] = data || { data: [] };
				this.setAllSectionData( d, fireChange , operation);
			}
		},
		/**
		 * @summary 데이터 템플릿이 구간 병합 표시, 구간 병합 편집으로 설정된 경우 이 메서드로 모든 구간 데이터를 설정할 수 있음.
		 * @param data{Object} 필수, 객체.
		 * @param fireChange{boolean} 선택, change 이벤트 발생 여부, 기본값 false.
		 * @example
		 *  this.form.get("fieldId").setAllSectionData({}); //빈 값 설정
		 * @example
		 *  this.data['fieldId'].setAllSectionData({
		 *  	"3455b82a-399c-4ee4-b9b9-e70ae40fbaf1": [ //구간1의 key와 data
		 *			{
		 *				"good": "yf",
		 *				"number_2": 11,
		 *				"prize": 1
		 *			}
		 *		],
		 *  	"83de86fc-60bc-4b4c-955c-1085915865a4": [ //구간2의 key와 data
		 *  		{
		 *  			"good": "yf",
		 *  			"number_2": 11,
		 *  			"prize": 10
		 *  		}
		 *  	]
		 *  });
		 */
		setAllSectionData: function(data, fireChange, operation){
			var old;
			if(fireChange)old = Object.clone(this.getBusinessDataById() || {});

			this._setUnchangedSectionLineMap(data);

			this.setBusinessDataById(data);

			if( operation ){
				this.data = this.isShowAllSection ? this._getAllSectionData() : data;
			}else{
				this.checkMerge(data);
			}

			if (this.data){
				this.clearSubModules();
			}

			if (fireChange && JSON.stringify(old) !== JSON.stringify(data)) this.fireEvent("change");

			if( !operation ){
				//this.saveFormData();
				this.saveDataById();
			}

			this.lineList = [];
			this.sectionlineList = [];
			this._loadDataTemplate(function () {
				Object.each(this.unchangedSectionLineMap, function(sline, key){
					sline.isUnchangedAll = null;
					sline.unchangedLineMap = null;
				});
				this.unchangedSectionLineMap = null;
			}.bind(this), operation);
		},

		checkMerge: function(data){
			//구간 병합 후 편집
			var isMergeEidt = this.isSectionMergeEdit();
			if( isMergeEidt ){ //구간 병합, 구간 값 병합 데이터 삭제 후 편집
				if( this.json.mergeTypeEdit === "script" ){
					this._loadMergeEditNodeByScript();
				}else{
					this._loadMergeEditNodeByDefault();
				}
			}
			//구간 병합 표시
			this.isMergeRead = this.isSectionMergeRead();
			//구간 사용 및 모든 구간 표시
			this.sectionBy = this._getSectionBy();
			this.isShowAllSection = this.isAllSectionShow();

			if( this.isShowAllSection ){
				this.data = this._getAllSectionData();
			}else if( this.isMergeRead ) {
				this.data = this.getSectionMergeReadData();
			}else if( isMergeEidt ){
				this.data = this._getBusinessData()
			}else{
				this.data = data;
			}
		},
		_setUnchangedSectionLineMap: function( data, operation ){
			if( !data ){
				return;
			}
			var map = {};
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					for( var key in data ){
						if( key === sline.sectionKey ){
							var m = sline._setUnchangedLineMap(data[key], operation);
							if( m )map[key] = m;
							break;
						}
					}
				}.bind(this));
			}
			this.unchangedSectionLineMap = map;
		},
		_setUnchangedLineMap: function(data, operation){
			if( !data ){
				this.unchangedLineMap = {};
				return;
			}
			var fromOutside = !operation;
			var lineDataList = this.lineList.map(function (line) {
				if( line.options.isEdited !== this.editable )return "";
				if( line.options.isDeleteable !== this.editable )return "";
				if( line.options.isAddable !== this.editable )return "";
				return fromOutside ?  JSON.stringify(line.data) : line.data;
			}.bind(this));

			var dStr, map = {};
			data.each(function (d, idx) {
				if(fromOutside)dStr = JSON.stringify(d);
				for (var i = 0; i < this.lineList.length; i++) {
					var isEqual= fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
					if ( isEqual ) {
						map[idx] = this.lineList[i];
						lineDataList[i] = "";
						break;
					}
				}
			}.bind(this));
			this.unchangedLineMap = map;
		},
		clearSubModules: function(){
			if( this.sectionlineList && this.sectionlineList.length ){
				for( var i=0; i<this.sectionlineList.length; i++ ){
					this.sectionlineList[i].clearSubModules();
				}
			}else{
				var map = this.unchangedLineMap || {};
				var lines = [];
				Object.values(map).each(function (d) {
					lines = lines.concat(d);
				});
				for (var i=0; i<this.lineList.length; i++){
					//this.lineList[i].clearSubModules();
					var l = this.lineList[i];
					if(!lines.contains(l))l.clearSubModules();
				}
			}
		},
		resetId: function(){
			if( this.sectionlineList && this.sectionlineList.length ){
				this.sectionlineList.each(function (sline, i) {
					sline.resetId();
				}.bind(this));
			}else{
				for (var i=0; i<this.lineList.length; i++){
					this.lineList[i].resetId();
				}
			}
		},
		/**
		 * @summary 데이터 템플릿이 비어 있는지 판단.
		 * @example
		 * if( this.form.get('fieldId').isEmpty() ){
		 *     this.form.notice('최소 한 건의 데이터를 추가해야 합니다', 'warn');
		 * }
		 * @return {Boolean} 비어 있는지 여부
		 */
		isEmpty: function(){
			var data = this.getInputData();
			if( !data )return true;
			if( o2.typeOf( data ) === "array" ){
				return data.length === 0;
			}
			if( o2.typeOf( data ) === "object" ){
				return Object.keys(data).length === 0;
			}
			return false;
		},
		//api 관련 시작
		/**
		 * 해당 항목 가져오기.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line | Null} 해당 데이터 템플릿 항목
		 * @example
		 * //데이터 템플릿 “dt1”의 첫 번째 항목 가져오기.
		 * var line = this.form.get("dt1").getLine(0);
		 * //첫 번째 항목의 subject 필드 값 가져오기
		 * var data = line.get("subject").getData();
		 * //subject 필드 값 설정
		 * line.get("subject").setData("test1");
		 */
		getLine: function(index){
			var line = this.lineList[index];
			return line || null;
		},
		/**
		 * 데이터 템플릿 끝에 항목 추가.
		 * @param {Object} [data] 추가할 항목 데이터.
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line} 추가된 데이터 템플릿 항목
		 * @example
		 * var line = this.form.get("dt1").addLine();
		 */
		addLine: function( data ){
			return this._addLine( null, data );
		},
		/**
		 * 데이터 템플릿의 지정 위치에 항목 추가.
		 * @param {Number} index 항목 순번, 0부터 시작. 인덱스가 현재 데이터 템플릿 항목 수를 넘으면 삽입에 실패하고 null 반환.
		 * @param {Object} [data] 추가할 항목 데이터.
		 * @return {MWF.xApplication.process.Xform.Datatemplate.Line | Null} 삽입된 데이터 템플릿 항목
		 * @example
		 * var line = this.form.get("dt1").insertLine(0);
		 */
		insertLine: function(index, data){
			return this._insertLineByIndex(null, index, data);
		},
		/**
		 * 지정 위치의 항목 삭제.
		 * @param {Number} index 항목 순번, 0부터 시작. 인덱스가 현재 데이터 템플릿 항목 수를 넘으면 삭제 실패.
		 * @example
		 * //첫 번째 항목 바로 삭제
		 * this.form.get("dt1").deleteLine(0);
		 */
		deleteLine: function(index, ev){
			var line = this.lineList[index];
			if( !line )return null;
			// if( ev ){
			// 	this._deleteLine(ev, line);
			// }else{
			this._delLine(line);
			// }
		},
		/**
		 * 해당 양식 컴포넌트 가져오기, get과 동일.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @param {String} id 컴포넌트 식별자
		 * @return {FormComponent} 해당 양식 컴포넌트
		 * @example
		 * //데이터 템플릿 “dt1”의 첫 번째 항목의 subject 필드 가져오기.
		 * var module = this.form.get("dt1").getModule(0, "subject");
		 * //subject 필드 값 가져오기
		 * var data = module.getData();
		 * //subject 필드 값 설정
		 * module.setData("test1");
		 */
		getModule: function(index, id){
			var line = this.lineList[index];
			if( !line )return null;
			return line.getModule(id);
		},
		/**
		 * 해당 양식 컴포넌트 가져오기, getModule과 동일.
		 * @param {Number} index 항목 순번, 0부터 시작
		 * @param {String} id 컴포넌트 식별자
		 * @return {FormComponent} 해당 양식 컴포넌트
		 * @example
		 * //데이터 템플릿 “dt1”의 첫 번째 항목의 subject 필드 가져오기.
		 * var module = this.form.get("dt1").get(0, "subject");
		 * //subject 필드 값 가져오기
		 * var data = module.getData();
		 * //subject 필드 값 설정
		 * module.setData("test1");
		 */
		get: function(index, id){
			return this.getModule(index, id);
		},
		//api 관련

		/**
		 * 스크립트에서 this.data[fieldId]로도 컴포넌트 값을 가져올 수 있음.
		 * 차이는 다음과 같음:<br/>
		 * 1. Promise를 사용하는 경우<br/>
		 * 비동기 함수 생성기(Promise)로 컴포넌트에 값을 할당할 때 getData 메서드로 즉시 가져오면 수정 전 값이 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * this.data[fieldId]로 즉시 가져오면 비동기 함수 생성기가 반환될 수 있으며, Promise 실행이 완료되면 수정 후 값이 반환됨.<br/>
		 * {@link https://www.yuque.com/o2oa/ixsnyt/ws07m0#EggIl|자세한 차이는 링크 참고}<br/>
		 * 2. 양식에 해당 컴포넌트가 없을 때는 this.data[fieldId]로 값을 가져올 수 있지만, this.form.get('fieldId')로는 컴포넌트를 가져올 수 없음.
		 * @summary 데이터 템플릿 데이터 조회.
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
		 * @return {DatatemplateData}
		 */
		getData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			var data;
			if (this.editable!==false){
				// var data = [];
				// this.lineList.each(function(line, index){
				// 	data.push(line.getData())
				// });
				//
				// this.data = data;
				//
				// this._setBusinessData(this.data);
				//
				// return (this.data.length) ? this.data : [];
				this.lineList.each(function (line) {
					line.computeModuleData("save");
				})
				data = this._getBusinessData();
				return o2.typeOf(data) === 'array' ? Array.clone(data) : data;
			}else{
				data = this._getBusinessData();
				return o2.typeOf(data) === 'array' ? Array.clone(data) : data;
			}
		},
		getInputData: function(){
			if( this.importer ){
				this.importer.destroySimulateModule();
			}
			if (this.editable!==false){
				return this._getBusinessData();
			}else{
				return this._getBusinessData();
			}
		},
		_getSectionBy: function(){
			if (this.json.section!=="yes"){
				return "";
			}else {
				switch (this.json.sectionBy){
					case "person":
						return layout.desktop.session.user.id;
					case "unit":
						return (this.form.businessData.task) ? (this.form.businessData.task.unitDn || this.form.businessData.task.unit) : "";
					case "activity":
						return (this.form.businessData.work) ? this.form.businessData.work.activity : "";
					case "splitValue":
						return (this.form.businessData.work) ? this.form.businessData.work.splitValue : "";
					case "script":
						if( this.json.sectionByScript && this.json.sectionByScript.code){
							return this.form.Macro.exec(this.json.sectionByScript.code, this) || "";
						}else{
							return "";
						}
					default:
						return "";
				}
			}
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
					"color": "red",
                    "display": "flex",
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

				var parentNode = this.errNode;
				while( parentNode && parentNode.offsetParent === null ){
					parentNode = parentNode.getParent();
				}

				if (parentNode && !parentNode.isIntoView()) parentNode.scrollIntoView(false);
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
				if( o2.typeOf(n)==="object"){
					var arr = [];
					Object.each( n, function (d, key) {
						if(o2.typeOf(d) === "array")arr = arr.concat(d);
					});
					n = arr;
				}
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
		saveValidation: function () {
			var flag = true;
			const promiseList = [];
			for (var i=0; i<this.lineList.length; i++){
				//if( this.lineList[i] && !this.lineList[i].saveValidation())flag = false;
				if( this.lineList[i] ){
					const checkResult = this.lineList[i].saveValidation();
					if(checkResult instanceof Promise){
						promiseList.push(checkResult);
					}else if(!checkResult){
						flag = false;
					}
				}
			}
			if( flag === false ){
				return false;
			}
			if(promiseList.length > 0){
				return Promise.all( promiseList ).then( function( resultArr ){
					return resultArr.every(res => String(res) === "true");
				});
			}
			return flag;
		},
		validation: function(routeName, opinion){
			//if (this.isReadonly() || this.json.showMode==="disabled" || this.node?.isDisplayNone() || !this.isEditable) return true;
			if (this.isReadonly() || this.json.showMode==="disabled" || !this.isEditable) return true;

			// if (this.isEdit){
			// 	if (!this.editValidation()){
			// 		return false;
			// 	}
			// }
			if (!this.validationConfig(routeName, opinion))  return false;

			return this._validation(routeName);
		},
		getAttachmentRandomSite: function(){
			var i = (new Date()).getTime();
			return this.json.id+i;
		},
		saveArrayData: function(type, index, toIndex, data, sectionBy){
			if(this.form.app.options.name !== 'process.Work' || this.form.isDraftWork() || !this.form.businessData.work.dataChanged){
				return;
			}
			if( this.isMergeRead ){ //병합 및 읽기 전용, 처리하지 않음
				return;
			}
			var method = ['insertLine','addLine'].contains(type) ? 'add' : type;

			var originalData = this.getOriginalDataById();
			if( !originalData ){
				if( method === 'add' ){
					//this.saveFormData();
					this.saveDataById();
				}
				return;
			}

			var oData = !!sectionBy ? originalData[ sectionBy ] : originalData;
			if( !oData ){
				if(method === 'add'){
					//this.saveFormData();
					this.saveDataById();
				}
				return;
			}

			o2.Actions.load('x_processplatform_assemble_surface').DataAction.updateArrayDataWithJob(
				this.form.businessData.work.job,
				{
					method: method,
					index: index,
					toIndex: toIndex,
					data: data,
					path: this.json.id.split('..').join('.') + ( sectionBy ? ('.'+ sectionBy) : '' )
				}, ()=>{
					switch (type){
						case 'addLine':
							oData.push(Object.clone(data));
							break;
						case 'insertLine':
							oData.splice(index, 0, Object.clone(data));
							break;
						case 'delete':
							oData.splice(index, 1);
							break;
					}
				}, null, false
			);
		},
		saveFormData: function(){
			var appName = this.form.app.options.name;
			if( ['process.Work', 'cms.Document'].includes(appName) ){
				this.form.saveFormData();
			}
		}
	});


MWF.xApplication.process.Xform.Datatemplate.SectionLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited: true, //편집 중 여부
		isDeleteable: true, //삭제 가능 여부
		isAddable: true, //추가 가능 여부
		isMergeRead: false, //병합 열람
		index: 0,
		indexText: "0"
	},
	initialize: function (node, template, data, options) {
		this.setOptions(options);
		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;
		this.lineList = [];
		this.sectionKey = this.data.sectionKey;
	},
	load: function () {
		if( this.template.isShowSectionKey() || this.template.isShowSectionBy() ){
			this.loadSectionKeyNode();
		}

		if( this.data.data ){
			( this.data.data || [] ).each(function(d, idx){
				if( !d )return;
				var div = new Element("div").inject(this.node);
				var isEdited = false, isNew = false;
				if( this.options.isEdited ){
					var dt = this.template;
					isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
					isEdited = true;
					dt.isNew = false;
					dt.newLineIndex = null;
				}
				var line = this._loadLine( div, d, idx, isEdited, isNew );
				this.lineList.push(line);
				this.template.lineList.push(line);
			}.bind(this));
		}
	},
	resetId: function(){
		this.lineList.each(function (line) {
			line.resetId();
		}.bind(this))
	},
	setIndex: function( data, index, isEdited, isNew, operation ){
		if( this.isUnchangedAll && index === this.options.index )return;

		this.data = data;

		this.options.index = index;
		this.options.indexText = (index+1).toString();

		//this.node.inject(preNode, "after");

		this.lineList = [];
		var map = this.unchangedLineMap || {};
		Object.each(map, function (line, idx) {
			line.setIndex( this.template.lineList.length + idx.toInt(), idx.toInt() );
		}.bind(this));
		if( this.data &&  this.data.data ){
			this.data.data.each(function (d, idx) {
				if( !d )return;
				var idxStr = idx.toString();
				var beforeNode = idx > 0 ? this.lineList[idx - 1].node : null;
				if( map[idxStr] ){
					if( !operation || operation === "moveUpList" ){
						this._injectLineNode( map[idxStr].node, beforeNode )
					}
					this.lineList.push( map[idxStr] );
					this.template.lineList.push(map[idxStr]);
				}else{
					var node = this._injectLineNode( new Element("div"), beforeNode );
					var isEdited = false, isNew = false;
					if( this.options.isEdited ){
						var dt = this.template;
						isNew = dt.isNew || (o2.typeOf(dt.newLineIndex) === "number" ? idx === dt.newLineIndex : false);
						isEdited = true;
						dt.isNew = false;
						dt.newLineIndex = null;
					}
					var line = this._loadLine( node, d, idx, isEdited, isNew );
					this.lineList.push(line);
					this.template.lineList.push(line);
				}
			}.bind(this))
		}
	},
	_loadLine: function(container, data, index, isEdited, isNew){
		var line = new MWF.xApplication.process.Xform.Datatemplate.Line(container, this.template, data, {
			indexInSectionLine : index,
			indexInSectionLineText : (index+1).toString(),
			index: this.template.lineList.length,
			indexText : (this.template.lineList.length + 1).toString(),
			isNew: isNew,
			isEdited: typeOf(isEdited) === "boolean" ? isEdited : this.options.isEdited,
			isDeleteable: this.options.isDeleteable,
			isAddable: this.options.isAddable,
			isMergeRead: this.options.isMergeRead,
			sectionKey: this.sectionKey
		}, this);
		this.template.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.template.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_injectLineNode: function( lineNode, beforeNode ){
		if( beforeNode ){
			lineNode.inject( beforeNode, "after");
		}else{
			lineNode.inject( this.node );
		}
		return lineNode;
	},
	loadSectionKeyNode: function () {
		var sectionKeyStyles = this.template._parseStyles(this.template.json.sectionKeyStyles);
		var keyNode = new Element("div.mwf_sectionkey", {
			styles : sectionKeyStyles
		}).inject( this.node );
		this.keyNode = keyNode;
		var separator;
		if( this.template.isShowSectionKey() ){
			separator = this.template.json.keyContentSeparator;
		}else{
			separator = this.template.json.keyContentSeparatorSectionBy;
		}
		this.template.getSectionKeyWithMerge( this.data, function (key) {
			if( o2.typeOf(key) === "string" ){
				keyNode.set("text", key + (separator || ""));
			}else{
				Promise.resolve(key).then(function (k) {
					keyNode.set("text", k + (separator || ""));
				}.bind(this))
			}
		}.bind(this));
	},
	_setUnchangedLineMap: function(data, operation){
		var fromOutside = !operation;
		var dt = this.template;
		var editalbe;
		if( dt.isShowAllSection ){
			editalbe = dt.isShowAllSection && dt.sectionBy && dt.sectionBy === this.sectionKey;
		}
		var lineDataList = this.lineList.map(function (line) {
			if( dt.isShowAllSection ){
				if( line.options.isEdited !== (dt.editable && editalbe) )return "";
				if( line.options.isDeleteable !== (dt.editable && editalbe) )return "";
				if( line.options.isAddable !== (dt.editable && editalbe) )return "";
			}else{
				if( line.options.isEdited !== dt.editable )return "";
				if( line.options.isDeleteable !== dt.editable )return "";
				if( line.options.isAddable !== dt.editable )return "";
			}
			return fromOutside ?  JSON.stringify(line.data) : line.data;
		}.bind(this));

		var dStr, map = {};
		data.each(function (d, idx) {
			if(fromOutside)dStr = JSON.stringify(d);
			for (var i = 0; i < this.lineList.length; i++) {
				var isEqual = fromOutside ? (dStr === lineDataList[i]) : (d === lineDataList[i] );
				if ( isEqual ) {
					map[idx] = this.lineList[i];
					lineDataList[i] = "";
					break;
				}
			}
		}.bind(this));


		this.isUnchangedAll = data.length === this.lineList.length;
		if( this.isUnchangedAll ){
			for( var i=0; i<data.length; i++ ){
				var line = map[i.toString()];
				if( !line || line.options.indexInSectionLine !== i ){
					this.isUnchangedAll = false;
					break;
				}
			}
		}

		this.unchangedLineMap = map;

		return Object.keys(map).length ? this : null;
	},
	clearSubModules: function(){
		if( this.isUnchangedAll )return;

		var map = this.unchangedLineMap || {};
		var hasUnchangedLine = Object.keys(map).length > 0;

		if( !hasUnchangedLine ){
			if( this.keyNode ){
				this.keyNode.destroy();
				this.keyNode = null;
			}
		}

		var lines = [];
		Object.values(map).each(function (d) {
			lines = lines.concat(d);
		});
		for (var i=0; i<this.lineList.length; i++){
			var l = this.lineList[i];
			if(!lines.contains(l)){
				l.clearSubModules();
			}
		}
	}
});

MWF.xApplication.process.Xform.Datatemplate.Line =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: false,
		isEdited : true,
		isAddable: true,
		isDeleteable: true,
		index : 0,
		indexText : "0",
		indexInSectionLine: 0,
		indexInSectionLineText : "0",
		sectionKey: ""
	},
	initialize: function (node, template, data, options, sectionLine) {

		this.setOptions(options);

		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;
		this.sectionLine = sectionLine;

		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};

		this.addActionList = [];
		this.deleteActionList = [];
		this.sequenceNodeList = [];
		this.selector = null;
		this.importActionList = [];
		this.exportActionList = [];

		this.subDatatemplateModuleList = [];
	},
	resetId: function(){
		this.setIndex();
	},
	setIndex: function(index, indexInSectionLine){
		var hasIndexArg = typeOf(index) !== "null";
		var hasIndexInSectionLineArg = typeOf(indexInSectionLine) !== "null";

		if( hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.index === index && this.options.indexInSectionLine === indexInSectionLine )return;
		}else if( hasIndexArg && !hasIndexInSectionLineArg){
			if( this.options.index === index )return;
		}else if(!hasIndexArg && hasIndexInSectionLineArg){
			if( this.options.indexInSectionLine === indexInSectionLine )return;
		}

		if( hasIndexArg ){
			this.options.index = index;
			this.options.indexText = (index.toInt()+1).toString();
		}

		if( hasIndexInSectionLineArg ){
			this.options.indexInSectionLine = indexInSectionLine;
			this.options.indexInSectionLineText = (indexInSectionLine.toInt()+1).toString();
		}

		//병합 상태 또는 분할 상태
		var sectionKey = this.options.sectionKey || this.template.sectionBy;
		this.modules.each(function (module) {
			var json = module.json;
			var id, oldId = json.id, templateJsonId = json.originialId;
			if( this.template.isShowAllSection ){
				id = this.template.json.id + ".." + sectionKey + ".." + this.options.indexInSectionLine + ".." + json.originialId;
			}else if( sectionKey ){
				id = this.template.json.id + ".." + sectionKey + ".." + this.options.index + ".." + json.originialId;
			}else{
				id = this.template.json.id + ".." + this.options.index + ".." + json.originialId;
			}
			json.id = id;
			switch (json.type) {
				case "Select":
					(module.areaNode || module.node).set("id", id);
					break;
				case "Datatable":
				case "Datatemplate":
					module.resetId();
					break;
				default:
					module.node.set("id", id);
					break;
			}

			if( json.type==="Attachment" || json.type==="AttachmentDg" ){
				json.originialSite = json.site || json.originialId;
				json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
			}

			delete this.all[oldId];
			this.all[id] = module;

			delete this.allField[oldId];
			this.allField[id] = module;

			if(this.form.all[oldId] && this.form.all[oldId] === module){
				delete this.form.all[oldId];
			}
			this.form.all[id] = module;

			if(this.form.forms[oldId] && this.form.forms[oldId] === module){
				delete this.form.forms[oldId];
			}
			if( module.field )this.form.forms[id] = module;

			this.checkSequence(module, templateJsonId);
		}.bind(this));
	},
	load: function(){
		this.node.set("html", this.template.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node);

		//분할 상태
		var sectionKey = this.options.sectionKey || this.template.sectionBy;

		moduleNodes.each(function (node) {
			if (node.get("MWFtype") !== "form") {
				var _self = this;

				var tJson = this.form._getDomjson(node);
				if( tJson ){
					var json = Object.clone(tJson);

					if( !this.options.isEdited )json.isReadonly = true;

					var templateJsonId = json.id;


					var index = this.options.index;
					var id;
					if( this.template.isShowAllSection ){
						id = this.template.json.id + ".." + sectionKey + ".." + this.options.indexInSectionLine + ".." + json.id;
					}else if( sectionKey ){
						id = this.template.json.id + ".." + sectionKey + ".."+ index + ".." + json.id;
					}else{
						id = this.template.json.id + ".." + index + ".." + json.id;
					}

					json.originialId = templateJsonId;
					json.id = id;
					node.set("id", id);

					if( json.type==="Attachment" || json.type==="AttachmentDg" ){
						json.type = "AttachmentDg";
						json.ignoreSite = true;
						json.originialSite = json.site || json.originialId;
						json.site = this.getAttachmentSite(json, templateJsonId, sectionKey);
					}

					if (this.form.all[id]) this.form.all[id] = null;
					if (this.form.forms[id])this.form.forms[id] = null;

					var module = this.form._loadModule(json, node, function () {
						if( _self.options.isMergeRead ){
							this.field = false; //데이터 저장을 원하지 않음
							this._getBusinessData = function(){
								return _self.data[templateJsonId];
							};
							this._setBusinessData = function () {};
						}
						if( _self.widget )this.widget = _self.widget;
						this.parentLine = _self;
						this.parentDatatemplate = _self.template;

						//읽기 전용 메서드 값은 페이지 로드 시 또는 new 시점에 한 번 계산
						if( this.json.compute === "show" ){
							var needComputeShow = false;
							if( _self.template.loading ) {
								needComputeShow = true;
							}else if( _self.options.isNew && !_self.reloading ){
								needComputeShow = true;
							}
							if( !needComputeShow ){
								this.json.compute = "create"; //
								if( this.options.moduleEvents && this.options.moduleEvents.length ){ //compute 복원
									var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
									var resetCompute = function () {
										this.json.compute = "show";
										this.removeEvent( eventName, resetCompute );
									}.bind(this)
									this.addEvent(eventName, resetCompute);
								}
							}
						}
					});
					if(!module.parentLine)module.parentLine = this;
					if(!module.parentDatatemplate)module.parentDatatemplate = this.template;

					if( json.type==="Attachment" || json.type==="AttachmentDg" ){
						module.addEvent("change", function(){
							_self.template.saveFormData();
						}.bind(this))
					}else if( json.type==="Datatemplate" ){
						this.subDatatemplateModuleList.push(module);
					}else if( module.field && !["Datatable", "OOFiles"].contains(json.type) ) {
						if (module.json.originialId !== this.template.selectorId){
							module.addEvent("change", function () {
								this.saveDataById();
							});
						}
					}

					this.form.modules.push(module);

					this.modules.push(module);
					this.all[id] = module;
					this.all_templateId[templateJsonId] = module;

					if (module.field) {
						// if(this.data.hasOwnProperty(templateJsonId)){
						// 	module.setData(this.data[templateJsonId]);
						// }
						this.allField[id] = module;
						this.allField_templateId[templateJsonId] = module;
						this.fields.push( module );

						module.addEvent("change", function(){
							this.template.fireEvent("change", [{lines: [this], type: "editmodule", module: module}]);
						}.bind(this))
					}

					this.setEvents(module, templateJsonId);
					this.checkSequence(module, templateJsonId);
				}
			}
		}.bind(this));

		if(this.options.isNew){
			this.data = this.getData();
			this.options.isNew = false;
		}
	},
	setSubDatatemplateOuterActionEvents: function(){
		this.subDatatemplateModuleList.each(function(module){
			//하위 템플릿 이벤트 바인딩
			module._setOuterActionEvents();
			//하위 데이터 템플릿이 다시 하위 템플릿의 외부 이벤트를 바인딩하도록 함
			module._setSubDatatemplateOuterEvents();
		})
	},
	getIndex: function(){
		return this.options.index;
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	getAttachmentSite: function(json, templateJsonId, sectionKey){
		//site 길이를 최대 64로 보장, 그렇지 않으면 서버에서 오류 발생

		var index = this.options.index;

		var baseSite;
		baseSite =  "." + index + "."  + (json.site || templateJsonId);

		var maxLength;
		var sectionId = "";
		if( sectionKey ){
			maxLength = Math.floor((63 - baseSite.length)/2 );

			sectionId = (sectionKey.length > maxLength) ? sectionKey.substr(sectionKey.length-maxLength, maxLength) : sectionKey;
			sectionId = "." + sectionId;
		}else{
			maxLength = 64 - baseSite.length;
		}

		var templateId = this.template.json.id;
		templateId = (templateId.length > maxLength) ? templateId.substr(templateId.length-maxLength, maxLength) : templateId;

		return templateId + sectionId + baseSite;
	},
	setEvents: function (module, id) {
		if( this.template.addActionIdList.contains( id )){
			this.addActionList.push( module );

			var addAddEvent = function (){
				module.node.addEvent("click", function (ev) {
					this.template._insertLine( ev, this )
				}.bind(this));
			}.bind(this);

			if( module.json.type.substr(0, 2) === "El" ){
				module.vm ? addAddEvent() : module.addEvent("load", addAddEvent);
			}else{
				addAddEvent();
			}

			if( !this.template.editable )module.node.hide();
			if( !this.options.isAddable )module.node.hide();
		}

		if( this.template.deleteActionIdList.contains(id)){
			this.deleteActionList.push( module );

			var addDeleteEvent = function (){
				module.node.addEvent("click", function (ev) {
					this.template._deleteLine( ev, this )
				}.bind(this));
			}.bind(this);

			if( module.json.type.substr(0, 2) === "El" ){
				module.vm ? addDeleteEvent() : module.addEvent("load", addDeleteEvent);
			}else{
				addDeleteEvent();
			}

			if( !this.template.editable )module.node.hide();
			if( !this.options.isDeleteable )module.node.hide();
		}

		if( this.template.selectorId === id){
			this.selector = module;
			// module.setData(""); //기본적으로 선택 안 함
			var addSelectEvent = function (){
				module.node.addEvent("click", function (ev) {
					this.checkSelect();
				}.bind(this));
			}.bind(this);

			if( module.json.type.substr(0, 2) === "El" ){
				module.vm ? addSelectEvent() : module.addEvent("load", addSelectEvent);
			}else{
				addSelectEvent();
			}

			if( !this.template.editable )module.node.hide();
			if( !this.options.isDeleteable )module.node.hide();

			if( this.options.isEdited || this.options.isNew ){
				this.unselect();
			}else{
				this.selected = false;
			}

		}

		//???
		// if( this.template.importActionIdList.contains(id))this.importActionList.push( module );
		// if( this.template.exportActionIdList.contains(id))this.exportActionList.push( module );

	},
	checkSequence: function(module, id){
		if( this.template.sequenceIdList.contains(id)){
			this.sequenceNodeList.push( module );
			var indexText;
			if(
				( this.template.isShowSectionKey() && this.template.json.sequenceBySection === "section" ) ||
				( this.template.isShowSectionBy() && this.template.json.sequenceBy === "section" )
			){
				indexText = this.options.indexInSectionLineText;
			}else{
				indexText = this.options.indexText;
			}
			if(this.form.getModuleType(module) === "label"){
				if (module.json.valueType === "script" && module.json.script && module.json.script.code){
					var value = module.form.Macro.exec(module.json.script.code, module);
					module._setNodeText(value);
				}else{
					module.node.set("text", indexText );
				}
			}else if(module.setData){
				module.setData( indexText );
			}
		}
	},
	checkSelect: function () {
		var selectData = this.selector.getData();
		var selected;
		if(o2.typeOf(selectData)==="array"){
			selected = selectData.contains(this.template.json.selectorSelectedValue);
		}else{
			selected = selectData === this.template.json.selectorSelectedValue;
		}
		this.selected = selected;
	},
	select: function(){
		this.selected = true;
		if(this.selector){
			if( ["OOCheckGroup", "Checkbox", "Elcheckbox"].contains(this.selector.json.type)){
				this.selector.setData([this.template.json.selectorSelectedValue]);
			}else{
				this.selector.setData(this.template.json.selectorSelectedValue);
			}
		}
	},
	unselect: function(){
		this.selected = false;
		if( this.selector.getOptionsObj ){
			var options = this.selector.getOptionsObj();
			var value;
			var arr = options.valueList || [];
			for( var i=0; i<arr.length; i++ ){
				var v = arr[i];
				if( v !== this.template.json.selectorSelectedValue ){
					value = v;
					break;
				}
			}
			this.selector.setData(typeOf(value) !== 'null' ? [value] : []);
		}else{
			this.selector.setData([]);
		}
	},
	reload: function(){
		this.reloading = true;
		for(var key in this.all){
			var module = this.all[key];
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.empty();
		this.load();
		this.reloading = false;
	},
	clearSubModules: function () { //module 제거
		for(var key in this.all){
			var module = this.all[key];
			//데이터 템플릿이나 데이터 표가 중첩된 경우 하위도 제거
			if(module.clearSubModules)module.clearSubModules();
			if( module.json && (module.json.type==="TinyMCEEditor" || module.json.type==="Htmleditor"))module.destroy();
			this.form.modules.erase(module);
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
	},
	computeModuleData: function( when ){
		for( var key in this.allField){
			var module = this.allField[key];
			var id = key.split("..").getLast();
			if( module.json.compute === when ){
				this.data[id] = module.getData();
			}
		}
	},
	getData: function () {
		var data = this.data;
		for( var key in this.allField){
			var module = this.allField[key];
			var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ) {
				data[id] = module._getBusinessData();
			}else if( module.json.compute === "save" && module.getInputData ){
				data[id] = module.getInputData();
			}else{
				data[id] = module.getData();
			}
		}
		return data;
	},
	setData: function (data) {
		this.template._setLineData(this, data);
	},
	deleteAttachment: function(){
		var saveFlag = false;
		for( var key in this.allField){
			var module = this.allField[key];
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				var array = module._getBusinessData();
				(array || []).each(function(d){
					saveFlag = true;
					var id = ( this.form.businessData.work || this.form.businessData.document ).id;
					( this.form.workAction || this.form.documentAction ).deleteAttachment(d.id, id);
				}.bind(this));
			}
		}
		return saveFlag;
	},
	saveValidation: function(){
		if( !this.options.isEdited )return true;
		var flag = true;
		const promiseList = [];
		this.fields.each(function(field, key){
			if (field.json.type!="sequence" && field.validationMode ){
				field.validationMode();

				const checkResult = field.saveValidation();

				if(checkResult instanceof Promise){
					promiseList.push(checkResult);
				}else if(!checkResult){
					flag = false
				}
			}
		}.bind(this));
		if( flag === false ){
			return false;
		}
		if(promiseList.length > 0){
			return Promise.all( promiseList ).then( function( resultArr ){
				return resultArr.every(res => String(res) === "true");
			});
		}
		return flag;
	}
});

MWF.xApplication.process.Xform.Datatemplate.ImporterDatatemplate = new Class({
	Extends: MWF.xApplication.process.Xform.Datatemplate,
	initialize: function(mainDatatable){
		this.mainDatatable = mainDatatable;
		this.json = Object.clone( mainDatatable.json );
		this.json.id = this.json.id + "_o2simula";

		this.node = new Element("div", {
			id: this.json.id
		});
		this.node.store("module", this);

		this.form = mainDatatable.form;
		this.field = false;
		this.fieldModuleLoaded = false;
	},
	load: function(){

		this.deleteFormData = function(data){
			delete data[this.id];
			this._self.form.removeEvent("getData", this._self.deleteFormData);
		}.bind({_self: this, id: this.json.id});
		this.form.addEvent("getData", this.deleteFormData);

		this._loadModuleEvents();
		if (this.fireEvent("queryLoad")){
			this._queryLoaded();
			this._loadUserInterface();
			this._afterLoaded();
			this.fireEvent("afterLoad");
		}
	},
	_loadUserInterface: function(){

		this.editable = true;

		//여러 행 동시 편집 여부
		this.multiEditMode = true;

		this.data = [];
		this._setValue(this.data);

		this.lineList = [];

		this.loadDatatemplate();
	},
	loadDatatemplate: function(){
		this.loading = true;

		this.templateJson = this.mainDatatable.templateJson;
		this.templateHtml = this.mainDatatable.templateHtml;

		this.fireEvent("load");
		this._loadDataTemplate(function(){
			this.fieldModuleLoaded = true;
			this.loading = false;
			this.fireEvent("postLoad");
		}.bind(this));
	},
	_loadDataTemplate: function(callback){
		this._loadLineList(function(){
			if(callback)callback();
		}.bind(this));
	},
	_loadLine: function(container, data, index, isNew){
		var line = new MWF.xApplication.process.Xform.Datatemplate.ImporterLine(container, this, data, {
			index : index,
			indexText : (index+1).toString()
		});
		this.fireEvent("beforeLoadLine", [line]);
		line.load();
		this.fireEvent("afterLoadLine", [line]);
		return line;
	},
	_addLine: function(ev, d){

		var data, index, line;

		index = this.lineList.length;
		data = this.getInputData();

		data.push(d||{});
		this.newLineIndex = index;

		this.setData( data );
		line = this.getLine(index);
		line.isNewAdd = true;

		this.fireEvent("addLine", [{"line":line, "ev":ev}]);

		this.fireEvent("change", [{"lines":[line], "type":"addline"}]);

		return line;
	},
	_setSubDatatemplateOuterEvents: function(){
	},
	destroy: function(){
		this.clearSubModules();
		var id = this.json.id;
		if (this.form.all[id]) delete this.form.all[id];
		if (this.form.forms[id])delete this.form.forms[id];
		// if( this.form.businessData.data.hasOwnProperty(id) )delete this.form.businessData.data[id];
		// if( this.form.Macro.environment.data.hasOwnProperty(id) )delete this.form.Macro.environment.data[id];
		this.node.destroy();
	}
});

//Excel 가져오기 시 생성된 컴포넌트
MWF.xApplication.process.Xform.Datatemplate.ImporterLine =  new Class({
	Implements: [Options, Events],
	options: {
		isNew: true,
		isEdited : true, //편집 중 여부
		isEditable : true, //편집 가능 여부
		isDeleteable: true, //삭제 가능 여부
	},
	initialize: function (node, template, data, options) {

		this.setOptions(options);

		this.node = node;
		this.template = template;
		this.data = data;
		this.form = this.template.form;

		this.init()
	},
	init: function(){
		this.modules = [];
		this.all = {};
		this.all_templateId = {};

		this.fields = [];
		this.allField = {};
		this.allField_templateId = {};
	},
	load: function(){
		this.loadModules();
	},
	loadModules: function(){
		this.node.set("html", this.template.templateHtml);
		var moduleNodes = this.form._getModuleNodes(this.node);

		//병합 상태 또는 분할 상태
		// var sectionKey = this.options.sectionKey || this.datatable.sectionBy;
		moduleNodes.each(function (node) {
			var mwfType = node.get("MWFtype");
			if (mwfType === "form")return;

			var _self = this;

			var tJson = this.form._getDomjson(node);
			if( tJson ){
				var json = Object.clone(tJson);

				if( !this.options.isEdited )json.isReadonly = true;

				var templateJsonId = json.id;

				var index = "0"; //this.options.index;

				var id = this.template.json.id + "..data.." + index + ".." + json.id;

				json.id = id;
				node.set("id", id);

				if (this.form.all[id]) this.form.all[id] = null;
				if (this.form.forms[id])this.form.forms[id] = null;

				var module = this.form._loadModule(json, node, function () {
					if( _self.widget )this.widget = _self.widget;
					this.parentLine = _self;
					this.parentDatatemplate = _self.template;

					//읽기 전용 메서드 값은 페이지 로드 시 또는 new 시점에 한 번 계산
					if( this.json.compute === "show" ){
						var needComputeShow = false;
						if( _self.template.loading ) {
							needComputeShow = true;
						}else if( _self.options.isNew && !_self.reloading ){
							needComputeShow = true;
						}
						if( !needComputeShow ){
							this.json.compute = "create"; //
							if( this.options.moduleEvents && this.options.moduleEvents.length ){ //compute 복원
								var eventName = ( this.options.moduleEvents || [] ).contains("afterLoad") ? "afterLoad" : "load";
								var resetCompute = function () {
									this.json.compute = "show";
									this.removeEvent( eventName, resetCompute );
								}.bind(this)
								this.addEvent(eventName, resetCompute);
							}
						}
					}
				}, true);
				if(!module.parentLine)module.parentLine = this;
				if(!module.parentDatatemplate)module.parentDatatemplate = this.template;

				this.form.modules.push(module);

				this.modules.push(module);
				this.all[id] = module;
				this.all_templateId[templateJsonId] = module;

				if( module.field && this.options.isEdited ) {
					this.allField[id] = module;
					this.allField_templateId[templateJsonId] = module;
					this.fields.push( module );
				}
			}
		}.bind(this));

	},
	clearSubModules: function () { //module 제거
		for(var key in this.all){
			var module = this.all[key];
			//데이터 템플릿이나 데이터 표가 중첩된 경우 하위도 제거
			if(module.clearSubModules)module.clearSubModules();
			if( module.json && (module.json.type==="TinyMCEEditor" || module.json.type==="Htmleditor"))module.destroy();
			this.form.modules.erase(module);
			// if( this.form.businessData.data.hasOwnProperty(key) )delete this.form.businessData.data[key];
			// if( this.form.Macro.environment.data.hasOwnProperty(key) )delete this.form.Macro.environment.data[key];
			if (this.form.all[key]) delete this.form.all[key];
			if (this.form.forms[key])delete this.form.forms[key];
		}
		this.node.destroy();
		this.init();
	},
	getModule: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	get: function(templateJsonId){
		return this.all_templateId[templateJsonId];
	},
	getData: function () {
		var data = this.data;
		for( var key in this.allField_templateId){
			var module = this.allField_templateId[key];
			// var id = key.split("..").getLast();
			if( module.json.type==="Attachment" || module.json.type==="AttachmentDg" ){
				data[key] = module._getBusinessData();
			}else{
				data[key] = module.getData();
			}
		}
		return data;
	}
});

MWF.xApplication.process.Xform.Datatemplate.Exporter = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (template, options) {

		this.setOptions(options);

		this.template = template;
		this.form = this.template.form;

	},
	exportToExcel : function () {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 모바일 대응
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}

		this.template.fireEvent("beforeExport");

		var resultArr = [];
		var titleArr = this.template.json.excelFieldConfig.map(function(config){
			return config.title;
		});
		resultArr.push( titleArr );

		var lineList = [];

		if( this.template.isShowAllSection ){
			lineList = this.template.sectionLineEdited ? this.template.sectionLineEdited.lineList : this.template.lineList;
		}else if( this.template.isMergeRead ) {
			lineList = this.template.lineList;
		}else{
			lineList = this.template.lineList;
		}

		lineList.each(function (line, index) {
			var lineData = this.getLineExportData(line, index);
			var p = Promise.all(lineData).then(function (arr) {
				return arr;
			});
			resultArr.push( p );
		}.bind(this));

		Promise.all(resultArr).then(function ( rstArr ) {
			var colWidthArr = this.getColWidthArray();
			var excelName = this.getExcelName();

			var arg = {
				data : rstArr,
				colWidthArray : colWidthArr,
				title : excelName
			};
			this.template.fireEvent("export", [arg]);

			new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template ).exportToExcel(
				arg.data || rstArr,
				arg.title || excelName,
				arg.colWidthArray || colWidthArr,
				this.getDateIndexArray(),  //날짜 형식 열 인덱스
				null,
				function () {
					if (this.mask) {
						this.mask.hide();
						this.mask = null;
					}
				}.bind(this)
			);
		}.bind(this))
	},
	getLineExportData: function(line, index ){
		var exportData = [];
		this.template.json.excelFieldConfig.each(function (config) {

			var module = line.all_templateId[config.field];
			var json = module ? module.json : "";

			if ( !module || !json || !this.isAvaliableField( json ) ) {
				exportData.push("");
			}else{
				var text;
				if ( json.type === "Label" && module.node) {
					text = module.node.get("text");
				}else{
					text = module.getExcelData();
				}

				if( !text && o2.typeOf(text) !== "number" ){
					text = "";
				}

				exportData.push( text );
			}
		}.bind(this));
		return exportData;
	},
	// getLineExportData: function(line, index ){
	// 	var exportData = [];
	// 	this.template.json.excelFieldConfig.each(function (config) {
	//
	// 		var module = line.all_templateId[config.field];
	// 		var json = module ? module.json : "";
	//
	// 		if ( !module || !json || !this.isAvaliableField( json ) ) {
	// 			exportData.push("");
	// 		}else{
	// 			var value = module.getData();
	// 			var text = "";
	//
	//
	// 			if( value ){
	// 				switch (module.json.type) {
	// 					case "Org":
	// 					case "Reader":
	// 					case "Author":
	// 					case "Personfield":
	// 					case "Orgfield":
	// 						if (o2.typeOf(value) === "array") {
	// 							var textArray = [];
	// 							value.each(function (item) {
	// 								if (o2.typeOf(item) === "object") {
	// 									textArray.push(item.distinguishedName);
	// 								} else {
	// 									textArray.push(item);
	// 								}
	// 							}.bind(this));
	// 							text = textArray.join(", \n");
	// 						} else if (o2.typeOf(value) === "object") {
	// 							text = value.distinguishedName;
	// 						} else {
	// 							text = value;
	// 						}
	// 						break;
	// 					case "Combox":
	// 					case "Address":
	// 						text = o2.typeOf(value) === "array" ? value.join(", ") : value;
	// 						break;
	// 					case "Checkbox":
	// 						var options = module.getOptionsObj();
	// 						var value = o2.typeOf(value) === "array" ? value : [value];
	// 						var arr = [];
	// 						value.each( function( a, i ){
	// 							var idx = options.valueList.indexOf( a );
	// 							arr.push( idx > -1 ? options.textList[ idx ] : "") ;
	// 						});
	// 						text = arr.join(", ");
	// 						break;
	// 					case "Radio":
	// 					case "Select":
	// 						var options = module.getOptionsObj();
	// 						var idx = options.textList.indexOf( value );
	// 						text = idx > -1 ? options.valueList[ idx ] : "";
	// 						break;
	// 					case "Textarea":
	// 						text = value;
	// 						break;
	// 					case "Calendar":
	// 						text = value;
	// 						break;
	// 					default:
	// 						text = value;
	// 						break;
	// 				}
	// 			} else if ( json.type === "Label" && module.node) {
	// 				text = module.node.get("text");
	// 			}
	//
	// 			if( !text && o2.typeOf(text) !== "number" ){
	// 				text = "";
	// 			}
	//
	// 			exportData.push( text );
	// 		}
	// 	}.bind(this));
	// 	return exportData;
	// },
	isAvaliableField : function(json){
		if (["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains( json.type) )return false; //이미지, 첨부파일, Label은 가져오기/내보내기 안 함
		return true;
	},
	getExcelName: function(){
		var title;
		if( this.template.json.excelName && this.template.json.excelName.code ){
			title = this.form.Macro.exec(this.template.json.excelName.code, this);
		}else{
			title = MWF.xApplication.process.Xform.LP.datatemplateExportDefaultName;
		}
		var titleA = title.split(".");
		if( ["xls","xlst"].contains( titleA[titleA.length-1].toLowerCase() ) ){
			titleA.splice( titleA.length-1 );
		}
		title = titleA.join(".");
		return title;
	},
	getColWidthArray : function(){
		var colWidthArr = [];
		this.template.json.excelFieldConfig.each(function (config) {
			var json = this.form.json.moduleList[config.field];
			if ( !json ){
				colWidthArr.push(150);
			}else if ( ["Org","Reader","Author","Personfield","Orgfield"].contains(json.type)) {
				colWidthArr.push(340);
			} else if (json.type === "Address") {
				colWidthArr.push(170);
			} else if (json.type === "Textarea") {
				colWidthArr.push(260);
			} else if (json.type === "Htmleditor") {
				colWidthArr.push(500);
			} else if (json.type === "TinyMCEEditor") {
				colWidthArr.push(500);
			} else if (json.type === "Calendar") {
				colWidthArr.push(150);
			} else {
				colWidthArr.push(150);
			}
		}.bind(this));
		return colWidthArr;
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //날짜 형식 열 인덱스
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && json.type === "Calendar") {
				dateIndexArr.push(i);
			}
		}.bind(this));
		return dateIndexArr;
	},

	exportWithImportDataToExcel : function ( columnList, importedData ) {
		MWF.require("MWF.widget.Mask", null, false);
		this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
		// 모바일 대응
		if (layout.mobile) {
			this.mask.load();
		} else {
			this.mask.loadNode(this.form.app.content);
		}

		this.template.fireEvent("beforeExport");

		var resultArr = [];
		var titleArr = this.template.json.excelFieldConfig.map(function(config){
			return config.title;
		});
		titleArr.push( MWF.xApplication.process.Xform.LP.validationInfor );
		resultArr.push( titleArr );


		// this.template.lineList.each(function (line, index) {
		// 	resultArr.push( this.getLineExportData(line, index) );
		// }.bind(this));

		importedData.each( function( lineData, lineIndex ){
			var array = [];
			columnList.each( function (obj, i) {
				array.push( ( lineData[ obj.text ] || '' ).replace(/&#10;/g, "\n") );
			});
			array.push( lineData.errorTextListExcel ? lineData.errorTextListExcel.join("\n") : ""  );

			resultArr.push( array );
		}.bind(this));

		var colWidthArr = this.getColWidthArray();
		colWidthArr.push(300); //안내 메시지

		var excelName = this.getExcelName();

		var arg = {
			data : resultArr,
			colWidthArray : colWidthArr,
			title : excelName,
			withError: true
		};
		this.template.fireEvent("export", [arg]);

		new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template ).exportToExcel(
			arg.data || resultArr,
			arg.title || excelName,
			arg.colWidthArray || colWidthArr,
			this.getDateIndexArray(),  //날짜 형식 열 인덱스
			null,
			function () {
				if (this.mask) {
					this.mask.hide();
					this.mask = null;
				}
			}.bind(this)
		);
	}
});

MWF.xApplication.process.Xform.Datatemplate.Importer = new Class({
	Implements: [Options, Events],
	options: {
	},
	initialize: function (template, options) {

		this.setOptions(options);

		this.template = template;
		this.form = this.template.form;

		this.lp = MWF.xApplication.process.Xform.LP;
		this.columnText =  this.lp.importValidationColumnText;
		this.columnTextExcel = this.lp.importValidationColumnTextExcel;
		this.excelUtil = new MWF.xApplication.process.Xform.Datatemplate.ExcelUtils( this.template );

	},
	isAvaliableField : function(json, module, type){
		if (["Image","Button","ImageClipper","WritingBoard","Attachment","AttachmentDg","Label",
			"Elbutton","Elcarousel","Eldropdown","Elicon","Eltree"].contains( json.type) )return false; //이미지, 첨부파일, Label은 가져오기/내보내기 안 함
		return true;
	},
	importFromExcel : function () {
		this.template.fireEvent("beforeImport");

		this.getFieldArray();
		var dateColArray = this.getDateIndexArray(); //날짜 열
		var orgTitleArray = this.getOrgTitleArray();

		this.excelUtil.upload( dateColArray, function (data) {

			MWF.require("MWF.widget.Mask", null, false);
			this.mask = new MWF.widget.Mask({ "style": "desktop", "zIndex": 50000 });
			// 모바일 대응
			if (layout.mobile) {
				this.mask.load();
			} else {
				this.mask.loadNode(this.form.app.content);
			}

			this.importedData = data;

			if( !this.checkCount() )return;

			this.loadSimulateModule();

			this.fieldArray.each(function (c) {
				c.module = this.importerLine.getModule(c.field)
			}.bind(this));

			if( orgTitleArray.length > 0 ){
				this.listAllOrgData( orgTitleArray, function () {
					this.checkAndImport();
				}.bind(this));
			}else{
				this.checkAndImport();
			}


		}.bind(this));
	},
	checkAndImport: function(){
		this.checkData( function (flag) {

			if( !flag ){
				this.openErrorDlg();
			}else{
				this.importData()
			}
			this.destroySimulateModule();

		}.bind(this));
	},
	destroySimulateModule: function(){
		if( !this.importerDatatemplate ){
			this.form.disallowSaving = false;
			return;
		}

		this.importerDatatemplate.destroy();
		this.importerDatatemplate = null;
		this.form.disallowSaving = false;
	},
	loadSimulateModule: function(){
		this.form.disallowSaving = true;
		this.importerDatatemplate = new MWF.xApplication.process.Xform.Datatemplate.ImporterDatatemplate( this.template );
		this.importerDatatemplate.load();

		this.importerLine = this.importerDatatemplate.addLine({});
	},
	getFieldArray: function(){
		//this.loadSimulateModule();
		this.fieldArray = []; //날짜 형식 열 인덱스
		this.template.json.excelFieldConfig.each(function (config, i) {
			const fieldJson = this.form.json.moduleList[config.field];
			if( !fieldJson ){
				console.warn(`데이터 템플릿에서 가져오기/내보내기 설정 필드를 찾을 수 없습니다: ${config.title}(${ config.field })`);
				return;
			}
			this.fieldArray.push({
				"text": config.title,
				"field": config.field,
				"index": i,
				// "module": this.simelateModuleMap[config.field],
				"json": fieldJson
			});
		}.bind(this));
	},
	getDateIndexArray : function(){
		var dateIndexArr = []; //날짜 형식 열 인덱스
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && json.type === "Calendar" && (c.mJson.format === "%Y-%m-%d" || c.mJson.format === "%Y-%m-%d %H:%M:%S")) {
				dateIndexArr.push(i);
			}
		}.bind(this));
		return dateIndexArr;
	},
	getOrgTitleArray : function(){
		var orgTitleArr = []; //날짜 형식 열 인덱스
		this.template.json.excelFieldConfig.each(function (config, i) {
			var json = this.form.json.moduleList[config.field];
			if (json && ["Org","Reader","Author","Personfield","Orgfield"].contains(json.type) ) {
				orgTitleArr.push(config.title);
			}
		}.bind(this));
		return orgTitleArr;
	},
	parseImportedData: function(){
		var data = [];

		this.importedData.each( function( ilineData ){
			var lineData = {};

			this.fieldArray.each( function (obj, i) {
				var index = obj.index;
				var module = obj.module;
				var json = obj.json;
				var text = obj.text;

				var d = ilineData[text] || "";

				var value;
				if( d === "" || d === undefined || d === null ){
					value = "";
				}else{
					switch (json.type) {
						case "Org":
						case "Reader":
						case "Author":
						case "Personfield":
						case "Orgfield":
							var arr = this.stringToArray(d); //공백,공백
							if( arr.length === 0 ){
								value = this.getOrgData( d );
							}else{
								value = [];
								arr.each( function(d, idx){
									var obj = this.getOrgData( d );
									value.push( obj );
								}.bind(this));
							}
							break;
						default:
							value = d; //d.replace(/&#10;/g,""); //줄바꿈 문자&#10;
							break;
					}
				}

				lineData[ json.id ] = value;

			}.bind(this));

			data.push( lineData );

		}.bind(this));

		return data;
	},
	stringToArray: function(string){
		return string.replace(/&#10;/g,",").split(/\s*,\s*/g ).filter(function(s){
			return !!s;
		});
	},
	importData: function(){
		var data = this.parsedData;

		this.template.fireEvent("import", [data] );

		this.template.setData( data );

		this.template.fireEvent("afterImport", [data] );

		this.template.fireEvent("change", [{lines: this.template.lineList, type : "import"}]);

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}

		this.form.notice( MWF.xApplication.process.Xform.LP.importSuccess );

	},
	openErrorDlg : function(){
		var eData = this.importedData;
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
			});
			return arr.join(" ")
		}

		var htmlArray = ["<table "+ objectToString( this.template.json.impExpTableProperties ) +" style='"+objectToString( this.template.json.impExpTableStyles, "style" )+"'>"];

		var titleStyle = objectToString(this.template.json.impExpTableTitleStyles, "style");
		htmlArray.push("<tr>");
		this.fieldArray.each(function (obj, i) {
			htmlArray.push( "<th style='"+titleStyle+"'>"+obj.text+"</th>" );
		});
		htmlArray.push("<th style='"+titleStyle+"'> "+MWF.xApplication.process.Xform.LP.validationInfor +"</th>");
		htmlArray.push("</tr>" );

		var contentStyles = Object.clone( this.template.json.impExpTableContentStyles );
		if( !contentStyles[ "border-bottom" ] && !contentStyles[ "border" ] )contentStyles[ "border-bottom" ] = "1px solid #eee";
		var contentStyle = objectToString( Object.merge( contentStyles, {"text-align":"left"}) , "style" );

		eData.each( function( lineData, lineIndex ){

			htmlArray.push( "<tr>" );
			this.fieldArray.each( function (obj, i) {
				htmlArray.push( "<td style='"+contentStyle+"'>"+ ( lineData[ obj.text ] || '' ).replace(/&#10;/g,"<br/>") +"</td>" ); //줄바꿈 문자&#10;
			});
			htmlArray.push( "<td style='"+contentStyle+"'>"+( lineData.errorTextList ? lineData.errorTextList.join("<br/>") : "" )+"</td>" );
			htmlArray.push( "</tr>" );

		}.bind(this));
		htmlArray.push( "</table>" );

		var width = this.template.json.impExpDlgWidth || 1000;
		var height = this.template.json.impExpDlgHeight || 700;
		width = width.toInt();
		height = height.toInt();

		var div = new Element("div", { style : "padding:10px;", html : htmlArray.join("") });
		var dlg = o2.DL.open({
			"style" : this.form.json.dialogStyle || "user",
			"title": MWF.xApplication.process.Xform.LP.importFail,
			"content": div,
			"offset": {"y": 0},
			"isMax": true,
			"width": width,
			"height": height,
			"buttonList": [
				{
					"type": "exportWithError",
					"text": MWF.xApplication.process.Xform.LP.datagridExport,
					"action": function () { _self.exportWithImportDataToExcel(eData); }
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

		if (this.mask) {
			this.mask.hide();
			this.mask = null;
		}
	},
	checkCount: function(){
		var idata = this.importedData;

		var lp = MWF.xApplication.process.Xform.LP;

		var exceeded = false;
		var maxCount = this.template.json.maxCount ? this.template.json.maxCount.toInt() : 0;
		if( maxCount > 0 && idata.length > maxCount )exceeded = true;

		var less = false;
		var minCount = this.template.json.minCount ? this.template.json.minCount.toInt() : 0;
		if( minCount > 0 && idata.length < minCount) less = true;

		if( exceeded ) {
			var text = lp.importTooManyNotice.replace("{n1}", idata.length).replace("{n2}", this.template.json.maxCount);
			this.form.notice(text, "error");
			return false;
		}

		if( less ){
			var text = lp.importTooFewNotice.replace("{n1}", idata.length).replace("{n2}", this.template.json.minCount );
			this.form.notice(text,"error");
			return false;
		}
		return true;
	},
	checkData : function( callback){
		this.parsedData = this.parseImportedData();
		this.isImportSuccess = true;

		this.checkLineData(0, function () {
			var arg = {
				validted : this.isImportSuccess,
				data : this.importedData,
				parsedData: this.parsedData
			};
			this.template.fireEvent( "validImport", [arg] );
			callback( arg.validted )
		}.bind(this));
	},
	checkLineData: function(lineIndex, callback){
		if( lineIndex < this.importedData.length ){
			this._checkLineData(this.importedData[lineIndex], lineIndex, function (flag) {
				lineIndex++;
				if( !flag )this.isImportSuccess = false;
				this.checkLineData(lineIndex, callback);
			}.bind(this));
		}else{
			if(callback)callback();
		}
	},
	_checkLineData: function(lineData, lineIndex, callback){
		lineData.errorTextList = lineData.errorTextList || [];
		lineData.errorTextListExcel = lineData.errorTextListExcel || [];

		var parsedLineData = (this.parsedData && this.parsedData[lineIndex]) ? this.parsedData[lineIndex] : [];

		this.checkModuleData(0, lineData, parsedLineData, function () {
			var flag = !lineData.errorTextList.length;
			callback(flag);
		});
	},
	checkModuleData: function(index, lineData, parsedLineData, callback){
		if( index < this.fieldArray.length ){
			var result = this._checkModuleData(this.fieldArray[index], lineData, parsedLineData);
			Promise.resolve(result).then(function (flag) {
				index++;
				this.checkModuleData(index, lineData, parsedLineData, callback);
			}.bind(this))
		}else{
			if(callback)callback();
		}
	},
	_checkModuleData: function (field, lineData, parsedLineData) {
		var index = field.index;
		var json = field.json;
		var module = field.module;
		var text = field.text;

		var colInfor = this.columnText.replace( "{n}", index+1 );
		var colInforExcel = this.columnTextExcel.replace( "{n}", this.excelUtil.index2ColName( index ) );

		var d = lineData[text] || "";
		var parsedD = parsedLineData[json.id] || "";
		var lp = this.lp;
		var flag = true;

		if(d){
			switch (json && json.type) {
				case "Org":
				case "Reader":
				case "Author":
				case "Personfield":
				case "Orgfield":
					var arr = this.stringToArray(d);
					arr.each( function(d, idx){
						var obj = this.getOrgData( d );
						if( obj.errorText ){
							lineData.errorTextList.push( colInfor + obj.errorText + lp.fullstop );
							lineData.errorTextListExcel.push( colInforExcel + obj.errorText + lp.fullstop );
							flag = false;
						}
					}.bind(this));
					break;
				case "Number":
				case "Currency":
				case "Elnumber":
					if (isNaN(d)){
						lineData.errorTextList.push( colInfor + d + lp.notValidNumber + lp.fullstop );
						lineData.errorTextListExcel.push( colInforExcel + d + lp.notValidNumber + lp.fullstop );
						flag = false;
					}
					break;
				case "Calendar":
				case "Eldate":
				case "Eldatetime":
					if( json.format === "%Y-%m-%d" || json.format === "%Y-%m-%d %H:%M:%S" ) {
						if (!(isNaN(d) && !isNaN(Date.parse(d)))) {
							lineData.errorTextList.push(colInfor + d + lp.notValidDate + lp.fullstop);
							lineData.errorTextListExcel.push(colInforExcel + d + lp.notValidDate + lp.fullstop);
							flag = false;
						}
					}
					break;
				default:
					break;
			}
		}
		if (module && module.setData && json.type !== "Address"){
			var hasError = false;
			if(["Org","Reader","Author","Personfield","Orgfield"].contains(json.type)){
				if(o2.typeOf(parsedD)==="array" && parsedD.length){
					hasError = parsedD.some(function (item) { return item.errorText; });
					flag = false;
				}
			}
			if(!hasError){
				module.setExcelData(parsedD);
				var ps = [];
				if( module.moduleExcelAG )ps.push( module.moduleExcelAG );
				if( module.moduleValueAG && !ps.contains(module.moduleValueAG) )ps.push( module.moduleValueAG );
				if( module.moduleSelectAG && !ps.contains(module.moduleSelectAG) )ps.push( module.moduleSelectAG );
				return Promise.all( ps ).then(function () {
					var result = module.validationExcel();
					if ( result && result.length ){
						lineData.errorTextList.push(colInfor + result.join("\n") );
						lineData.errorTextListExcel.push( colInforExcel + result.join("\n"));
						flag = false;
					}
					parsedLineData[json.id] = module.getData();
					return flag;
				})
			}
		}
		return flag
	},
	exportWithImportDataToExcel: function(eData){
		var exporter = new MWF.xApplication.process.Xform.Datatemplate.Exporter(this.template);
		exporter.exportWithImportDataToExcel(this.fieldArray, eData)
	},
	getOrgData : function( str ){
		str = str.trim();
		var flag = str.substr(str.length-2, 2);
		switch (flag.toLowerCase()){
			case "@i":
				return this.identityMap[str] || {"errorText": str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@p":
				return this.personMap[str] || {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@u":
				return this.unitMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			case "@g":
				return this.groupMap[str] ||  {"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };
			default:
				return this.identityMap[str] ||
					this.personMap[str] ||
					this.unitMap[str] ||
					this.groupMap[str] ||
					{"errorText":  str + MWF.xApplication.process.Xform.LP.notExistInSystem };

		}
	},
	listAllOrgData : function (orgTitleList, callback) {
		var identityList = [], personList = [], unitList = [], groupList = [];
		if( orgTitleList.length > 0 ){
			this.importedData.each( function( lineData, lineIndex ){
				// if( lineIndex === 0 )return;

				orgTitleList.each( function (title, index) {

					if( !lineData[title] )return;

					var arr = this.stringToArray(lineData[title]);
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
				}.bind(this))
			}.bind(this));
			var identityLoaded, personLoaded, unitLoaded, groupLoaded;
			var check = function () {
				if( identityLoaded && personLoaded && unitLoaded && groupLoaded ){
					if(callback)callback();
				}
			};

			this.identityMap = {};
			if( identityList.length ){
				identityList = identityList.unique();
				o2.Actions.load("x_organization_assemble_express").IdentityAction.listObject({ identityList : identityList }, function (json) {
					json.data.each( function (d) { this.identityMap[ d.matchKey ] = d; }.bind(this));
					identityLoaded = true;
					check();
				}.bind(this))
			}else{
				identityLoaded = true;
				check();
			}

			this.personMap = {};
			if( personList.length ){
				personList = personList.unique();
				o2.Actions.load("x_organization_assemble_express").PersonAction.listObject({ personList : personList, useNameFind: true }, function (json) {
					json.data.each( function (d) { this.personMap[ d.matchKey ] = d; }.bind(this));
					personLoaded = true;
					check();
				}.bind(this))
			}else{
				personLoaded = true;
				check();
			}

			this.unitMap = {};
			if( unitList.length ){
				unitList = unitList.unique();
				o2.Actions.load("x_organization_assemble_express").UnitAction.listObject({ unitList : unitList, useNameFind: true }, function (json) {
					json.data.each( function (d) { this.unitMap[ d.matchKey ] = d; }.bind(this));
					unitLoaded = true;
					check();
				}.bind(this))
			}else{
				unitLoaded = true;
				check();
			}

			this.groupMap = {};
			if( groupList.length ){
				groupList = groupList.unique();
				o2.Actions.load("x_organization_assemble_express").GroupAction.listObject({ groupList : groupList }, function (json) {
					json.data.each( function (d) { this.groupMap[ d.matchKey ] = d; }.bind(this));
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

MWF.xDesktop.requireApp("Template", "utils.ExcelUtils", null, false);
MWF.xApplication.process.Xform.Datatemplate.ExcelUtils = new Class({
	Extends: MWF.xApplication.Template.utils.ExcelUtils,
	initialize: function(){
		this.sheet2JsonOptions = {};
		this.pollyfill();
	}
});

